package classes.scenes.old;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.containers.Background;
import classes.controllers.MapController;
import classes.controllers.MidiController;
import classes.controllers.SoundController;
import classes.controllers.MidiController.Midi;
import classes.scenes.action.assets.*;
import classes.scenes.old.assets.Car;
import classes.scenes.old.assets.Enemy;
import classes.scenes.old.assets.Player;
import classes.scenes.old.assets.StaticObject;
import classes.scenes.old.logics.UpdateLogic;
import classes.utils.GeneralUtil;

import interfaces.GameScene;

/**
 * <pre>
 * ゲームステージを定義するゲームシーンオブジェクト
 * 横スクロールアクション型のロジックを実装するステージ
 * </pre>
 *
 * @author Naoki Yoshikawa
 **/
@SuppressWarnings("serial")
public class Stage2 extends BaseActionOperator implements GameScene, Receiver {

    // コードネームの文字列定数
    public static final String MAJOR7 = "M7";
    public static final String MINOR7 = "m7";
    public static final String SEVENTH = "7";
    public static final String MINOR7_F5 = "m7-5";

    // コード進行の位置を表すインデックス
    private int progIndex;

    // MIDIの設定
    private Map<Integer, Midi> midiConfig;

    // コード進行順のベースラインのリスト
    private Map<Integer, String> baseline;

    // コード進行のリスト
    private int[] chordProg;

    // MIDIコントローラーオブジェクト
    private MidiController midiController;

    // 伴奏のテンポ(BPMで指定)
    private int bpm;

    // 伴奏のキー
    private int key;

    // メジャーキーかどうか
    private boolean isMajor;

    // ダイアトニックトーンの配列
    private String[] diatonics;

    /**
     * マップの読み込み、スプライトの設定
     */
    public Stage2() {

        // マップを作成
        map = new MapController("map02.dat");

        // 背景画像のレイヤーを設定
        //background.add(new Background(GeneralUtil.readImage(ImageResource.LayeredBackground.BASE2.getValue()), 10, 1));
        background.add(new Background(GeneralUtil.readImage(ImageResource.LayeredBackground.LAYER3.getValue()), 8, 1));
        background.add(new Background(GeneralUtil.readImage(ImageResource.LayeredBackground.LAYER1.getValue()), 6, 1));
        background.add(new Background(GeneralUtil.readImage(ImageResource.LayeredBackground.LAYER2.getValue()), 3, 1));

        // デフォルト設定のキャラクターを取得
        player = Player.getDefault(200, map.getHeight(), 32, 32);

        // 敵スプライトの設定
        baseSpriteList.add(
                new Enemy(
                50.0,
                map.getHeight() - 32,
                32,
                32,
                ImageResource.Enemy1Left.values(),
                ImageResource.Enemy1Right.values()
            )
        );

        // 車スプライトの設定
        BufferedImage carImg = GeneralUtil.readImage(ImageResource.StageObject.CAR1.getValue());
        List<Point> pixelList = GeneralUtil.checkColor(carImg, Color.WHITE);
        for(int i = 0; i < 5; i++) {
            baseSpriteList.add(
                    new Car(
                        map.getWidth(),
                        map.getHeight(),
                        32,
                        32,
                        ImageResource.StageObject.CAR1.getValue(),
                        pixelList
                    )
                );
        }

        // ステージオブジェクトの設定
        frontObjectList.add(
                new StaticObject(
                map.getWidth() - 256,
                map.getHeight(),
                ImageResource.StageObject.GOAL
            )
        );

        backObjectList.add(
                new StaticObject(
                0,
                map.getHeight(),
                ImageResource.StageObject.START,
                2
            )
        );

        // MIDIコントローラーのリスナーに登録
        midiController = new MidiController();
        midiController.getMIDIController(this);
        midiConfig = MidiController.getDefaultMidi().getMidi();
        // このマップはここじゃなくてもいいはず
        // コード進行の配列は半音ではなくナンバリングを書く
        // メジャーマイナーによる振り分けはシステム側で行う
        // コード進行が変更された場合はこの配列を変更する
        initParam();
        startTimer();
    }

    /**
     * キーと進行中のナンバーに応じてダイアトニックのルートを返す
     *
     * @return ダイアトニックコードのルート
     */
    public String getDiatonic() {
        return diatonics[chordProg[progIndex] - 1];
    }

    /**
     * ナンバリングのみのコード進行を、キーの長短を加味した半音階での表示に変換する
     * (e.g. [1, 6, 2, 5]と指定した場合、メジャーなら[0, 3, 10, 5]、マイナーなら[0, 4, 10, 5])
     *
     * @return 半音階で表したコード進行
     */
    public int[] getConvertedChord() {
        return convertChordProg(this.chordProg);
    }

    /**
     * ナンバリングのみのコード進行を返す
     *
     * @return ナンバリングのみのコード進行
     */
    public int[] getChordProg() {
        return this.chordProg;
    }

    /**
     * MIDIコントローラーのコンフィグを返す
     *
     * @return MIDIコントローラーのコンフィグ
     */
    public MidiController getMidiController() {
        return this.midiController;
    }

    /**
     * 現在のコード進行の位置を返す
     *
     * @return　コード進行のインデックス
     */
    public int getProgIndex() {
        return this.progIndex;
    }

    /**
     * MIDIコンフィグを返す
     *
     * @return MIDIコンフィグ
     */
    public Map<Integer, Midi> getMidiConfig() {
        return this.midiConfig;
    }

    /**
     * 音階に応じたベース音を設定する
     *
     * @param notes 使用する音階の配列
     */
    private void setBaseNote(int[] notes) {
        baseline = new HashMap<Integer, String>();
        for(int note: notes) {
            baseline.put(note, getRoot((note + key) % 12));
        }
    }

    /**
     * キー変更時にダイアトニックトーンを変更する
     *
     * @param key 指定キー
     */
    public void changeKey(int key) {
        this.key = key;
        int[] notes;
        if (isMajor) {
            notes = new int[] {0, 10, 8, 7, 5, 3, 1};
            diatonics = new String[] {MAJOR7, MINOR7, MINOR7, MAJOR7, SEVENTH, MINOR7, MINOR7_F5};
        } else {
            notes = new int[] {0, 10, 9, 7, 5, 4, 2};
            diatonics = new String[] {MINOR7, MINOR7_F5, MAJOR7, MINOR7, SEVENTH, MAJOR7, SEVENTH};
        }

        setBaseNote(notes);
    }

    /**
     * コード進行パターンを変更する
     *
     * @param chordProg 新しいコード進行
     */
    public void changeChordProg(int[] chordProg) {
        this.chordProg = chordProg;
    }

    /**
     * 指定されたコード進行のナンバリングと現在のキーの長短を合わせてダイアトニックトーンの音程を返す
     *
     * @param chordProg ナンバリングのみのコード進行
     * @return 半音階表記でのコード進行
     */
    public int[] convertChordProg(int[] chordProg) {
        int[] convert = new int[chordProg.length];
        for(int i = 0; i < chordProg.length; i++) {
            switch(chordProg[i]) {
            case 1:
                convert[i] = 0;
                continue;
            case 2:
                convert[i] = 10;
                continue;
            case 3:
                if(isMajor) {
                    convert[i] = 8;
                } else {
                    convert[i] = 9;
                }
                continue;
            case 4:
                convert[i] = 7;
                continue;
            case 5:
                convert[i] = 5;
                continue;
            case 6:
                if(isMajor) {
                    convert[i] = 3;
                } else {
                    convert[i] = 4;
                }
                continue;
            case 7:
                if(isMajor) {
                    convert[i] = 1;
                } else {
                    convert[i] = 2;
                }
                continue;
            }
        }
        return convert;
    }

    /**
     * 指定されたコード進行のナンバリングと現在のキーの長短を合わせてダイアトニックトーンの音程を返す
     *
     * @param chordProg ナンバリングのみのコード進行
     * @return 半音階表記でのコード進行
     */
    private String getRoot(int note) {
        switch (note) {
            case 0:
                return SoundResource.BGM_C;
            case 1:
                return SoundResource.BGM_B;
            case 2:
                return SoundResource.BGM_A_SHARP;
            case 3:
                return SoundResource.BGM_A;
            case 4:
                return SoundResource.BGM_G_SHARP;
            case 5:
                return SoundResource.BGM_G;
            case 6:
                return SoundResource.BGM_F_SHARP;
            case 7:
                return SoundResource.BGM_F;
            case 8:
                return SoundResource.BGM_E;
            case 9:
                return SoundResource.BGM_D_SHARP;
            case 10:
                return SoundResource.BGM_D;
            case 11:
                return SoundResource.BGM_C_SHARP;
            default:
                return null;
        }
    }

    /**
     * 指定されたコード進行に合わせてベース音を鳴らす
     */
    private void playBase() {
        SoundController.stop();
        SoundController.setBGM(baseline.get(getConvertedChord()[progIndex]));
        SoundController.loop(0, bpm * 1000);
        playDrum(60000 / bpm / 2);
    }

    /**
     * コード進行に同期して一定間隔でバスドラムとスネアを鳴らす
     *
     * @param time 次にドラムを鳴らすまでの時間
     */
    private void playDrum(int time) {
        TimerTask task = new TimerTask() {
            public void run() {
                new Thread(new SoundController.PlaySE(SoundResource.SE_SNARE)).start();
            }
        };

        new Thread(new SoundController.PlaySE(SoundResource.SE_BASSDRUM)).start();

        Timer timer = new Timer();
        timer.schedule(task, time);
    }

    /**
     * 最初のベース音を鳴らし、bpmを指定する
     */
    private void startTimer() {
        playBase();
        chordTimer(60000 / bpm);
    }

    /**
     * 指定したタイムスパンでコードを進行させる
     */
    private void chordTimer(int time) {
        TimerTask task = new TimerTask() {
            public void run() {
                progIndex = (progIndex + 1) % chordProg.length;
                System.out.println(progIndex);
                System.out.println(getConvertedChord()[progIndex]);
                playBase();
                chordTimer(time);
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, time);
    }

    /*
     * GameSceneインターフェースの機能群
     */

    /**
     * ユーザ入力または自動操作による入力値をもとに、フレームごとのオブジェクトの更新を行う
     * @see interfaces.GameScene
     *
     * @param dt     デルタタイム
     */
    public void updator(double dt) {
        UpdateLogic.basicLogic(this, dt);
    }

    /**
     * JLayeredPanelに追加するために自身のインスタンスを返す
     * @see interfaces.GameScene
     *
     * @return 自身のパネルインスタンス
     */
    public JLayeredPane getPanel() {
        return this;
    }

    /**
     * 自身の初期状態のインスタンスを返す
     * @see interfaces.GameScene
     *
     * @return 自身の初期状態のインスタンス
     */
    public GameScene getNewScene() {
        return new Stage2();
    }

    /**
     * 自身のシーンで使用するBGMのファイルパスを返す
     * @see interfaces.GameScene
     *
     * @return BGMのファイルパス
     */
    public String getSound() {
        return null;//SoundResource.BGM_POLLYANNA;
    }

    /**
     * <pre>
     * 自身のシーンで使用するBGMの再生モードを返す
     * 0: ループ再生(ループ区間指定可)
     * 1: 一度のみ再生
     * 2: 再生なし
     * </pre>
     *
     * @return BGMのファイルパス
     */
    public int getBgmMode() {
        return GameScene.BGM_LOOP;
    }

    /**
     * 自身のシーンで使用するBGMの再生区間を返す
     *
     * @return BGM再生区間を表す始点と終点の値の組
     */
    public Point getDuration() {
        return new Point(0, GameScene.BGM_END);
    }

    /**
     * 各パラメータを初期化する
     */
    public void initParam() {
        // アクション系はスプライトのリストにinitをかけていけばいいはず
        // よってコンストは今のままでいい？
        super.helpOn = true;
         this.key = 0;
        this.isMajor = true;
        chordProg = new int[] {1, 6, 2, 5};
        this.bpm = 30;
        changeKey(key);
        progIndex = 0;
    }

    @Override
    public void send(MidiMessage msg, long timeStamp) {

        byte[] aMsg = msg.getMessage();
        int chord = getConvertedChord()[progIndex];//chordProg[progIndex];

        int index = ((int)aMsg[1] + chord + key) % 12;

        Midi midi =  midiConfig.get(index);
        System.out.println((int)aMsg[1] % 12);
        if(aMsg[2] > 0) {
            midi.press();
        } else {
            midi.release();
        }
    }

    @Override
    public void close() {
        // TODO 自動生成されたメソッド・スタブ

    }

    /**
     * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
     *
     * @param res 呼び出し元からのレスポンスコード
     */
    public void callback(int res) {

    }
}