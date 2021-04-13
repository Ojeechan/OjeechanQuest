package classes.scenes.vinyl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.controllers.FontController;
import classes.controllers.GameController;
import classes.controllers.KeyController;
import classes.controllers.SceneController;
import classes.controllers.SoundController;
import classes.controllers.WindowController;
import classes.scenes.vinyl.assets.Player;
import classes.scenes.vinyl.assets.Vinyl;
import classes.scenes.vinyl.assets.Vinyl.Track;
import classes.ui.StringSelectOption;
import classes.scenes.slot.SlotHall;
import classes.utils.GeneralUtil;
import classes.scenes.old.utils.DrawUtil;

import interfaces.GameScene;


/**
 * ターンテーブルステージを定義するゲームシーンオブジェクト
 *
 * @author Naoki Yoshikawa
 **/
@SuppressWarnings("serial")
public class Turntable extends BaseVinylOperator implements GameScene {

    /* 定数群 */
    private final double WIDTH_RATIO = 1.9;
    private final double HEIGHT_RATIO = 0.7;
    private final double LIFT_SPEED = 100;
    private final double STEP_SPEED = 9.5;
    private final double PLAYER_ON_X = WindowController.WIDTH/2;
    private final double PLAYER_OFF_X = 200;
    private final double GROUND = WindowController.HEIGHT/2;
    private final double GRAVITY = 1;

    // プレイヤーオブジェクト
    private Player player;

    // 現在のカーソルのあるインデックス
    private int cursorVinylIndex;
    private int cursorTrackIndex;

    // 現在再生中のインデックス
    private int playingVinylIndex;
    private int playingTrackIndex;

    // 対象レコードがセットされるまでの仮インデックス
    private int tmpVinylIndex;

    // 曲目とレコードタイトルを表示するための選択リストオブジェクト
    private List<List<StringSelectOption>> dscList;
    private List<StringSelectOption> titleList;

    // フォントとレコードの回転角度
    private double degree;
    private double vinylDegree;

    // レコードの描画位置のY座標
    private double vinylY;

    // レコードのY方向の移動速度
    private double vinylVY;

    private double runPos;

    // 各ステージオブジェクトの画像オブジェクト
    private BufferedImage turntable;
    private BufferedImage playingVinyl;

    private String sound;

    /**
     * <pre>
     * キーコンフィグ、プレイヤースプライト、ステージオブジェクトの設定
     * レコード情報の読み込み
     * </pre>
     */
    public Turntable() {

        keyConfig.getKeys().put(KeyEvent.VK_UP, keyConfig.new Key(KeyController.DETECT_INITIAL_PRESS_ONLY));
        keyConfig.getKeys().put(KeyEvent.VK_DOWN, keyConfig.new Key(KeyController.DETECT_INITIAL_PRESS_ONLY));
        keyConfig.getKeys().put(KeyEvent.VK_LEFT, keyConfig.new Key(KeyController.DETECT_INITIAL_PRESS_ONLY));
        keyConfig.getKeys().put(KeyEvent.VK_RIGHT, keyConfig.new Key(KeyController.DETECT_INITIAL_PRESS_ONLY));

        keyHelpList.add(new StringSelectOption(810, 400, FontController.Fonts.NORMAL, "↑", 16));
        keyHelpList.add(new StringSelectOption(810, 430, FontController.Fonts.NORMAL, "↓", 16));
        keyHelpList.add(new StringSelectOption(790, 430, FontController.Fonts.NORMAL, "←", 16));
        keyHelpList.add(new StringSelectOption(830, 430, FontController.Fonts.NORMAL, "→", 16));
        keyHelpList.add(new StringSelectOption(810, 350, FontController.Fonts.NORMAL, "ENTER: けってい", 16));
        keyHelpList.add(new StringSelectOption(810, 30, FontController.Fonts.NORMAL, "H:HELP ON/OFF", 16));
        keyHelpList.add(new StringSelectOption(810, 50, FontController.Fonts.NORMAL, "ESC:PAUSE", 16));

        player = Player.getDefault(0, 0);

        player.setWidthRatio(2);
        player.setHeightRatio(2);
        player.setSpeedRate(3);

        turntable = GeneralUtil.readImage(ImageResource.VinylIcon.TURNTABLE_CLOSE.getValue());
    }

    /**
     * <pre>
     * トラック用カーソルのインデックスを1つ進める
     * 最大値の次は0に戻る
     * </pre>
     *
     * @return 1つ進めた後のインデックス
     */
    public int nextTrack() {
        cursorTrackIndex++;
        return cursorTrackIndex %= GameController.getVinylList().get(cursorVinylIndex).getTrackList().size();
    }

    /**
     * <pre>
     * トラック用カーソルのインデックスを1つ戻す
     * 0の次は最大インデックスに戻る
     * </pre>
     *
     * @return 1つ戻した後のインデックス
     */
    public int previousTrack() {
        cursorTrackIndex--;
        return cursorTrackIndex < 0
                ? cursorTrackIndex = GameController.getVinylList().get(cursorVinylIndex).getTrackList().size() + cursorTrackIndex
                : cursorTrackIndex;
    }

    /**
     * <pre>
     * レコード用カーソルのインデックスを1つ進める
     * 最大値の次は0に戻る
     * </pre>
     *
     * @return 1つ進めた後のインデックス
     */
    public int nextVinyl() {
        cursorVinylIndex++;
        return cursorVinylIndex %= GameController.getVinylList().size();
    }

    /**
     * <pre>
     * レコード用カーソルのインデックスを1つ戻す
     * 0の次は最大インデックスに戻る
     * </pre>
     *
     * @return 1つ戻した後のインデックス
     */
    public int previousVinyl() {
        cursorVinylIndex--;
        return cursorVinylIndex < 0 ? cursorVinylIndex = GameController.getVinylList().size() + cursorVinylIndex : cursorVinylIndex;
    }

    /**
     * フレームごとの再描画を行う
     *
     * @param g グラフィックスオブジェクト
     */
    @Override
    public void paintComponent(Graphics g) {

        List<StringSelectOption> sso = dscList.get(cursorVinylIndex);
        Vinyl v = GameController.getVinylList().get(cursorVinylIndex);
        StringSelectOption title = titleList.get(cursorVinylIndex);


        WindowController w = GameController.getWindow();

        g.setColor(new Color(125, 125, 125));
        g.fillRect(0, 0, (int) GameController.getWindow().getWindowWidth(), (int) GameController.getWindow().getWindowHeight());

        // ターンテーブル
        g.drawImage(
                turntable,
                0,
                0,
                (int) w.getWindowWidth(),
                (int) w.getWindowHeight(),
                null
                );

        // レコード
        if(vinylY + playingVinyl.getHeight() > 0) {
            g.drawImage(
                    playingVinyl,
                    (int) w.getAbsPosX(WindowController.WIDTH/2 - playingVinyl.getWidth()/2 * WIDTH_RATIO),
                    (int) w.getAbsPosY(vinylY - playingVinyl.getHeight()/2 * HEIGHT_RATIO),
                    (int) w.getAbsPosX(playingVinyl.getWidth() * WIDTH_RATIO),
                    (int) w.getAbsPosY(playingVinyl.getHeight() * HEIGHT_RATIO),
                    null
                    );
                playingVinyl.flush();
        }

        // レコードのジャケット
        double wave = GeneralUtil.getSinValue(System.currentTimeMillis() / 1000.0, 1.0, 0, 6);
        g.drawImage(
                v.getJacket(),
                (int) w.getAbsPosX(200),
                (int) w.getAbsPosY(20 + wave),
                (int) w.getAbsPosX(v.getJacket().getWidth()/3),
                (int) w.getAbsPosY(v.getJacket().getHeight()/3),
                null
                );

        // レコードのタイトル
        GeneralUtil.drawImageString(
                title.getImageFont(),
                title.getX(),
                title.getY(),
                title.getSize(),
                GeneralUtil.ALIGN_LEFT,
                g
                );

        // レコードの各トラック
        for(int i = 0; i < sso.size(); i++) {
            StringSelectOption s = sso.get(i);

            if(i == cursorTrackIndex) {
                GeneralUtil.drawStringRotated(
                        s.getImageFont(),
                        s.getX(),
                        s.getY(),
                        s.getSize(),
                        GeneralUtil.ALIGN_LEFT,
                        degree,
                        g
                        );
            } else {
                GeneralUtil.drawImageString(
                        s.getImageFont(),
                        s.getX(),
                        s.getY(),
                        s.getSize(),
                        GeneralUtil.ALIGN_LEFT,
                        g
                        );
            }
        }

        if(helpOn) {
            GeneralUtil.drawSelectOptions(keyHelpList, GeneralUtil.ALIGN_CENTER, g);
        }

        // キャラクター
        DrawUtil.drawSprite(player, -player.getImageWidth() / 2, (int)-runPos, g);
    }

    /**
     * 画像を回転させて新しい画像を返す
     *
     * @param img  回転させる元の画像オブジェクト
     * @param rads 回転させる角度
     * @return 回転させて描画した画像オブジェクト
     */
    private BufferedImage rotateImage(BufferedImage img, double rads) {

        // 回転角をマッピング
        double sin = Math.abs(Math.sin(rads));
        double cos = Math.abs(Math.cos(rads));

        // 変換前後の矩形サイズを算出
        int width = img.getWidth();
        int height = img.getHeight();
        int newWidth = (int) Math.floor(width * cos + height * sin);
        int newHeight = (int) Math.floor(height * cos + width * sin);

        // 開店後の矩形オブジェクトを設定
        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();

        // 矩形サイズの変形前後でのサイズ差分を補正
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - width) / 2, (newHeight - height) / 2);

        // 回転軸座標の設定
        int x = width / 2;
        int y = height / 2;

        // 回転の適用
        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, this);
        /*
        g2d.setColor(Color.RED);
        g2d.drawRect(0, 0, newWidth - 1, newHeight - 1);
        */
        g2d.dispose();

        return rotated;
    }

    /**
     * レコードタイトル用の選択リストオブジェクトを読み込む
     */
    private void setTitle() {
        titleList = new ArrayList<StringSelectOption>();
        List<Vinyl> v = GameController.getVinylList();
        int drawX = 20;
        int drawY = 20;
        int fontSize = 24;
        for(int i = 0; i < v.size(); i++) {

            StringSelectOption title = new StringSelectOption(
                    drawX,
                    drawY,
                    FontController.Fonts.NORMAL,
                    v.get(i).getTitle(),
                    fontSize
                    );

            titleList.add(title);
        }
    }

    /**
     * 曲目用の選択リストオブジェクトを読み込む
     */
    private void setDescription() {
        dscList = new ArrayList<List<StringSelectOption>>();
        List<Vinyl> v = GameController.getVinylList();
        int drawX = 20;
        int drawY = 44;
        int fontSize = 16;
        for(int i = 0; i < v.size(); i++) {
            List<Track> t = v.get(i).getTrackList();
            List<StringSelectOption> ssoList = new ArrayList<StringSelectOption>();
            for(int j = 0; j < t.size(); j++) {
                StringSelectOption title = new StringSelectOption(
                        drawX,
                        drawY + fontSize * (j+1),
                        FontController.Fonts.NORMAL,
                        t.get(j).getDescription(),
                        fontSize
                        );
                ssoList.add(title);
            }
            dscList.add(ssoList);
        }
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
    @Override
    public void updator(double dt) {

        Vinyl v = GameController.getVinylList().get(playingVinylIndex);

        // ポーズ
        if(keyConfig.getKeys().get(KeyEvent.VK_ESCAPE).isPressed()) {
            new Thread(new SoundController.PauseBGM()).start();
            GameController.getWindow().pushScene(GameController.getScene(SceneController.PAUSE));
        }

        // 上下キーはトラックを選択する
        if (keyConfig.getKeys().get(KeyEvent.VK_UP).isPressed()) {
            previousTrack();
            new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();
            degree = 0;
        } else if (keyConfig.getKeys().get(KeyEvent.VK_DOWN).isPressed()) {
            nextTrack();
            new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();
            degree = 0;
        } else if (keyConfig.getKeys().get(KeyEvent.VK_ENTER).isPressed()) {

            tmpVinylIndex = cursorVinylIndex;
            playingTrackIndex = cursorTrackIndex;
            degree = 0;
            player.jump();
            runPos = (playingTrackIndex - 4) * 8;

            // レコードが変わった場合はまず現在のレコードを画面外へ
            if(playingVinylIndex != cursorVinylIndex) {
                new Thread(new SoundController.PauseBGM()).start();
                player.setVX(-STEP_SPEED);
                vinylVY = -LIFT_SPEED;

            // 同じレコード内の別トラック
            } else if(vinylY == GROUND && player.getImageLeftX() == PLAYER_ON_X) {
                    vinylDegree = 0;
                    List<Track> tracks = GameController.getVinylList().get(tmpVinylIndex).getTrackList();
                    sound = tracks.get(playingTrackIndex).getSound();
                    new Thread(new SoundController.PlayBGM(sound)).start();
            } else {
                vinylVY = LIFT_SPEED;
            }
        }

        /* プレイヤーのY方向の計算 */
        // ジャンプと重力の計算
        player.setImageTopY(player.getImageTopY() + player.getVY());

        if(player.getImageTopY() < GROUND) {
            player.setVY(player.getVY() + GRAVITY);

        } else if(player.getImageTopY() > GROUND) {
            player.setImageTopY(GROUND);
            player.setVY(0);
        }

        /* プレイヤーのX方向の計算 */
        // 左に動ききるまで
        if(player.getImageLeftX() > PLAYER_OFF_X && player.getVX() < 0) {
            player.setImageLeftX(player.getImageLeftX() + player.getVX());
            if(player.getImageLeftX() < PLAYER_OFF_X) {
                player.setImageLeftX(PLAYER_OFF_X);
            }

        // 右に動ききるまで
        } else if(player.getImageLeftX() < PLAYER_ON_X && player.getVX() > 0) {
            player.setImageLeftX(player.getImageLeftX() + player.getVX());
            if(player.getImageLeftX() >= PLAYER_ON_X) {
                player.setImageLeftX(PLAYER_ON_X);
                List<Track> tracks = GameController.getVinylList().get(tmpVinylIndex).getTrackList();
                sound = tracks.get(playingTrackIndex).getSound();
                new Thread(new SoundController.PlayBGM(sound)).start();
            }

        // プレイヤーのX移動が終わっている時のみレコードを入れ替える
        // プレイヤーが左端にいる時のみ vinylVY!=0 となりレコードの移動が発生する
        } else {
            vinylY += vinylVY;
        }

        /* レコードの入れ替え */
        // レコードが上昇して画面外にでるまで
        if(vinylY < -playingVinyl.getHeight() * HEIGHT_RATIO) {
            vinylVY = LIFT_SPEED;
            playingVinylIndex = tmpVinylIndex;
            vinylDegree = 0;

        // レコードが下降してセットされるまで
        } else if (vinylY >= GROUND && vinylVY > 0) {
            vinylVY = 0;
            vinylY = GROUND;
            player.setVX(STEP_SPEED);
            player.jump();
        }

        // 左右キーはレコードを選択する
        if (keyConfig.getKeys().get(KeyEvent.VK_LEFT).isPressed()) {
            previousVinyl();
            cursorTrackIndex = 0;
            new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();
        } else if (keyConfig.getKeys().get(KeyEvent.VK_RIGHT).isPressed()) {
            nextVinyl();
            cursorTrackIndex = 0;
            new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();
        }

        // 操作キー一覧の表示
        if(keyConfig.getKeys().get(KeyEvent.VK_H).isPressed()) {
            super.helpOn = !super.helpOn;
        }

        player.animate(dt);

        // 文字のフレームあたりの回転角度
        degree = (degree + Math.PI/180) % (Math.PI * 2);

        if(SoundController.isPlaying()) {
            // 33回転風の角度を設定しておく
            vinylDegree = (vinylDegree + (0.55 * 2 * Math.PI * dt)) % (Math.PI * 2);
            player.switchLabel(ImageResource.VINYL_RIGHT);
            // 徐々に内側に
            runPos += 0.15 * dt;
        } else {
            player.switchLabel(ImageResource.STAND_RIGHT);
        }

        playingVinyl = rotateImage(v.getVinylImg(), vinylDegree);
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
        return new SlotHall();
    }

    /**
     * 自身のシーンで使用するBGMのファイルパスを返す
     * @see interfaces.GameScene
     *
     * @return BGMのファイルパス
     */
    public String getSound() {
        return sound;
    }

    /**
     * <pre>
     * 自身のシーンで使用するBGMの再生モードを返す
     * 0: ループ再生(ループ区間指定可)
     * 1: 一度のみ再生
     * 2: 再生なし
     * </pre>
     * @return BGMのファイルパス
     */
    public int getBgmMode() {
        return GameScene.BGM_ONCE;
    }

    /**
     * 自身のシーンで使用するBGMの再生区間を返す
     *
     * @return BGM再生区間を表す始点と終点の値の組
     */
    public Point getDuration() {
        int start = 57000;
        return new Point(start, start + 473000);
    }

    /**
     * 各パラメータを初期化する
     */
    public void initParam() {
        player.initParam();
        player.switchLabel(ImageResource.VINYL_RIGHT);
        player.setImageLeftX(PLAYER_OFF_X);
        player.setImageTopY(-100);
        player.setVX(0);
        playingVinylIndex = tmpVinylIndex = cursorVinylIndex = 0;
        playingTrackIndex = cursorTrackIndex = 0;
        playingVinyl = GameController.getVinylList().get(cursorVinylIndex).getVinylImg();
        vinylY = -playingVinyl.getHeight() * HEIGHT_RATIO;
        vinylVY = 0;
        degree = 0;
        vinylDegree = 0;
        helpOn = true;
        keyConfig.releaseAll();
        setDescription();
        setTitle();
        sound = null;
    }

    /**
     * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
     *
     * @param res 呼び出し元からのレスポンスコード
     */
    public void callback(int res) {

    }
}
