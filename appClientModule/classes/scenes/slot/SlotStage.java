package classes.scenes.slot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLayeredPane;

import classes.scenes.slot.assets.*;
import classes.ui.StringSelectOption;
import classes.scenes.slot.utils.DrawUtil;
import classes.utils.GeneralUtil;
import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.controllers.EffectController;
import classes.controllers.FontController;
import classes.controllers.GameController;
import classes.controllers.KeyController;
import classes.controllers.SceneController;
import classes.scenes.effects.FillBackground;
import classes.scenes.effects.Freeze1;
import classes.scenes.effects.Freeze2;
import classes.scenes.effects.Freeze3;
import classes.scenes.effects.Freeze4;

import interfaces.GameScene;

/**
 * スロットステージを定義するゲームシーンオブジェクト
 *
 * @author Naoki Yoshikawa
 **/
@SuppressWarnings("serial")
public class SlotStage extends BaseSlotOperator implements GameScene {

    // パーツモジュールのラッパーとしてのスロットオブジェクト
    private Slot slot;

    // 7セグ使用するフォント画像オブジェクトのリスト
    private List<BufferedImage> segFontList;

    // シーン遷移間のエフェクト
    private EffectController effect;

    /**
     * <pre>
     * スロットを構成する各モジュールの設定
     * リール、ボタン、ランプ、レバー、当選モードを生成
     * </pre>
     */
    public SlotStage() {

        keyHelpList.add(new StringSelectOption(810, 380, FontController.Fonts.NORMAL, "SPACE: START", 16));
        keyHelpList.add(new StringSelectOption(810, 400, FontController.Fonts.NORMAL, "←  ↑/↓  →: STOP", 16));
        keyHelpList.add(new StringSelectOption(120, 30, FontController.Fonts.NORMAL, "H:HELP ON/OFF", 16));
        keyHelpList.add(new StringSelectOption(80, 50, FontController.Fonts.NORMAL, "ESC:PAUSE", 16));

        // インデックスは筐体ごとに変えられるよう、定義側で管理する
        Map<Integer, String> imgPaths = new HashMap<Integer, String>();
        // 0:リプレイ, 1:ベル, 2:チェリー, 3:スイカ, 4:Bar, 5:ボーナス
        imgPaths.put(0, ImageResource.SlotIcon.REPLAY.getValue());
        imgPaths.put(1, ImageResource.SlotIcon.BELL.getValue());
        imgPaths.put(2, ImageResource.SlotIcon.CHERRY.getValue());
        imgPaths.put(3, ImageResource.SlotIcon.SUIKA.getValue());
        imgPaths.put(4, ImageResource.SlotIcon.BAR.getValue());
        imgPaths.put(5, ImageResource.SlotIcon.SEVEN_RED.getValue());
        imgPaths.put(6, ImageResource.SlotIcon.SEVEN_BLUE.getValue());

        float[] luminance = {2.0f, 2.0f, 2.0f, 1.0f};

        Reel[] reels = new Reel[] {
                // 0:リプレイ, 1:ベル, 2:チェリー, 3:スイカ, 4:Bar, 5:赤, 6:青
                // リールのみ相対配置を行うので、リール自体に位置は指定しない
                new Reel(imgPaths, new int[] {0,5,2,5,1,0,4,6,6,1,0,2,3,3,1,0,3,4,3,1}, 32, 32, luminance),
                new Reel(imgPaths, new int[] {1,2,5,2,0,1,3,6,2,0,1,4,3,4,0,1,2,3,2,0}, 32, 32, luminance),
                new Reel(imgPaths, new int[] {0,5,2,5,1,0,4,2,2,1,0,3,4,3,1,0,6,6,4,1}, 32, 32, luminance)
            };

        luminance = new float[] {1.5f, 1.5f, 1.5f, 1.0f};

        Button[] buttons = new Button[] {
                new Button(
                        ImageResource.SlotIcon.BUTTON.getValue(),
                        290,
                        460,
                        luminance
                        ),
                new Button(
                        ImageResource.SlotIcon.BUTTON.getValue(),
                        445,
                        460,
                        luminance
                        ),
                new Button(
                        ImageResource.SlotIcon.BUTTON.getValue(),
                        600,
                        460,
                        luminance
                        )
            };

        // ひとまず発光パターンインデックス[0,1,2]を[通常、対応役のフラグ成立、ボーナス中]とする
        // on/off と onの時の発光パターンの組み合わせで考える
        FlashPattern[] fp1 = new FlashPattern[] {
                new FlashPattern(0.8, 3.0, 0.2, 2),
                new FlashPattern(0, 0, 0, 1),
                new FlashPattern(0.01, 1.5, 0.8, 4)
            };

        FlashPattern[] fp2 = new FlashPattern[] {
                new FlashPattern(0, 0, 0, 1),
                new FlashPattern(0, 0, 0, 0),
                new FlashPattern(0.15, 1.5, 0.3, 2)
            };

        FlashPattern[] fp3 = new FlashPattern[] {
                new FlashPattern(0, 0, 0, 1),
                new FlashPattern(0, 0, 0, 0),
                new FlashPattern(0.15, 1.5, 0.3, 3)
            };

        Lamp[] lamps = new Lamp[] {

                // 上部のロゴランプ
                new Lamp(
                    ImageResource.SlotIcon.LAMP_TOP.getValue(), // ランプの画像オブジェクト
                    60, // 配置位置のXY座標
                    -140,
                    new float[] {1.2f, 1.2f, 1.2f, 1.0f}, // 発光時の最大値
                    fp1, 0,   // フラッシュパターンと初期モード
                    new int[] {Mode.BONUS} // 対応フラグ
                ),

                // 右の子役ランプ
                new Lamp(
                    ImageResource.SlotIcon.LAMP_RIGHT.getValue(),
                    700,
                    160,
                    new float[] {5.0f, 5.0f, 5.0f, 1.0f},
                    fp2, 0,
                    new int[] {Mode.CHERRY, Mode.SUIKA}
                ),

                // 左のボーナスランプ
                new Lamp(
                    ImageResource.SlotIcon.LAMP_LEFT.getValue(),
                    110,
                    160,
                    new float[] {5.0f, 5.0f, 5.0f, 1.0f},
                    fp3, 0,
                    new int[] {Mode.BONUS}
                )
            };

        Lever lever = new Lever(
                ImageResource.SlotIcon.LEVER.getValue(),
                ImageResource.SlotIcon.LEVER_DOWN.getValue(),
                130,
                460
                );

        // ボーナスごとにBGMを設定する
        Map<Integer, MusicTable> musics = new HashMap<Integer, MusicTable>();
        musics.put(0, new MusicTable(265000, 795000, SoundResource.BGM_FANFARE));
        musics.put(1, new MusicTable(490000, 797000, SoundResource.BGM_EDM));
        musics.put(2, new MusicTable(265000, 795000, SoundResource.BGM_FANFARE));
        musics.put(3, new MusicTable(490000, 797000, SoundResource.BGM_EDM));

        // 当選率配分の異なるモードを設定する
        Mode[] modeTable = new Mode[] {
                Mode.getNormal(),
                Mode.getRedBig(),
                Mode.getBlueBig(),
                Mode.getRedReg(),
                Mode.getBlueReg(),
                Mode.getBonus(),
                Mode.getCherryBonus(),
                Mode.getSuikaBonus()
            };

        // スロットに各モジュールをセット
        this.slot = new Slot(
                ImageResource.SlotIcon.FRAME.getValue(), // フレーム
                reels,     // リール
                buttons,   // ボタン
                lamps,     // ランプ
                lever,     // レバー
                modeTable, // フラグごとの確率・払い出し枚数などをモードごとにまとめたオブジェクト
                musics,    // 入賞時のBGM
                0,         // 初期モード
                getSound() // 通常時のBGM
            );

        // 枚数表示用の7セグフォントを読み込んでおく
        this.segFontList = GeneralUtil.changeStringToImage(FontController.Fonts.SEGNUM, "0123456789-");

        effect = new EffectController(this);
        effect.addEffect(new Freeze1(effect));
        effect.addEffect(new Freeze2(effect));
        effect.addEffect(new FillBackground(effect, 2, null, Color.BLACK));
        effect.addEffect(new FillBackground(effect, 10, SoundResource.BGM_FREEZE, Color.BLACK));
        effect.addEffect(new Freeze3(effect));
        effect.addEffect(new Freeze4(effect));
    }

    /**
     * スロットオブジェクトを変えす
     *
     * @return スロットオブジェクト
     */
    public Slot getSlot() {
        return this.slot;
    }

    /**
     * フレームごとの再描画を行う
     *
     * @param g グラフィックスオブジェクト
     */
    @Override
    public void paintComponent(Graphics g) {

        // 座標指定は960*540時の絶対位置の座標を指定する
        // DrawUtil側で現在の画面サイズ比に変換する
        DrawUtil.drawSlotReel(slot, 280, 128, 3, 2, 50, g);
        DrawUtil.drawSlotFrame(slot, g);
        DrawUtil.drawSlotLamp(slot, g);
        DrawUtil.drawSlotButton(slot, g);
        DrawUtil.drawSlotLever(slot, g);
        DrawUtil.drawSeg(
                GameController.getCoin(),
                this.segFontList,
                520,
                100,
                g
                );
        DrawUtil.drawSeg(
                slot.getPayout(),
                this.segFontList,
                520,
                64,
                g
                );

        if(helpOn) {
            GeneralUtil.drawSelectOptions(keyHelpList, GeneralUtil.ALIGN_CENTER, g);
        }
    }

    /**
     * GameSceneインターフェースの機能群
     * @see interfaces.GameScene
     */

    /**
     * ユーザ入力または自動操作による入力値をもとに、フレームごとのオブジェクトの更新を行う
     *
     * @param dt     デルタタイム
     */
    public void updator(double dt) {

        Map<Integer, KeyController.Key> keys = getKeyConfig().getKeys();

        // ポーズ
        if(keys.get(KeyEvent.VK_ESCAPE).isPressed()) {
            GameController.getWindow().pushScene(GameController.getScene(SceneController.PAUSE));
            keyConfig.releaseAll();
        }

        // 操作キー一覧の表示
        if(keyConfig.getKeys().get(KeyEvent.VK_H).isPressed()) {
            super.helpOn = !super.helpOn;
        }

        // レバオン
        if(keys.get(KeyEvent.VK_SPACE).isPressed()) {
            slot.pullLever();
            if(slot.isReady()) {
                slot.leverOn();
                // 抽選結果がフリーズフラグだった場合演出を割り込ませて更新終了
                if(slot.getFlagInfo().getType() == Mode.FREEZE) {
                    // フリーズ時の画面エフェクトを登録
                    if(!effect.isRemained()) {
                        effect.initEffect();
                    }
                    keyConfig.releaseAll();
                    effect.process();
                    return;
                }
            }
        }

        // 各リールの停止
        if (keys.get(KeyEvent.VK_LEFT).isPressed()) {
            slot.stopReel(0);
        } else if (keys.get(KeyEvent.VK_DOWN).isPressed() || keys.get(KeyEvent.VK_UP).isPressed()) {
            slot.stopReel(1);
        } else if (keys.get(KeyEvent.VK_RIGHT).isPressed()) {
            slot.stopReel(2);
        }

        // ボタンが押されていなければリールを回転させる
        // ボタンが押されたあとは残りの滑り制御を行う
        slot.spinReel(dt);

        // リールの回転状態を参照する
        if(!slot.isReady() && slot.checkReady()) {
            slot.payout();
            slot.startClock();
        }

        slot.animateLever(dt);
        slot.animateLamp(dt);

    }

    /**
     * JLayeredPanelに追加するために自身のインスタンスを返す
     *
     * @return 自身のパネルインスタンス
     */
    public JLayeredPane getPanel() {
        return this;
    }

    /**
     * 自身の初期状態のインスタンスを返す
     *
     * @return 自身の初期状態のインスタンス
     */
    public GameScene getNewScene() {
        return null;
    }

    /**
     * 自身のシーンで使用するBGMのファイルパスを返す
     *
     * @return BGMのファイルパス
     */
    public String getSound() {
        return SoundResource.BGM_SLOT;
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
        slot.initParam();
        keyConfig.releaseAll();
    }

    /**
     * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
     *
     * @param res 呼び出し元からのレスポンスコード
     */
    public void callback(int res) {

    }
}
