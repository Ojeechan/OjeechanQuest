package classes.scenes;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.containers.Image;
import classes.controllers.FontController;
import classes.controllers.GameController;
import classes.controllers.KeyController;
import classes.controllers.SceneController;
import classes.controllers.ScriptController;
import classes.controllers.SoundController;
import classes.controllers.WindowController;
import classes.controllers.ScriptController.Script;
import classes.scenes.effects.SceneChange1;
import classes.scenes.effects.SceneChange2;
import classes.scenes.vinyl.assets.Vinyl;
import classes.ui.ImageSelectOption;
import classes.ui.StringSelectOption;
import classes.utils.GeneralUtil;

import interfaces.GameScene;

/**
 * ステージ選択画面を定義するゲームシーンオブジェクト
 *
 * @author Naoki Yoshikawa
 */
@SuppressWarnings("serial")
public class WorldMap extends BaseSystemOperator implements GameScene {

    // ステージ名の文字列定数
    private static final String MAPPOINT1 = "きんさく";    // "こつこつかせぐ"
    private static final String MAPPOINT2 = "おもてカジノ"; // "いっかくせんきん"
    private static final String MAPPOINT3 = "レコードや"; // "かったりきいたり"

    private final int STAGE_ACTION = 0;
    private final int STAGE_SLOT = 1;
    private final int STAGE_VINYLSHOP = 2;

    //背景画像オブジェクト
    private BufferedImage background;

    // ワールドマップロゴの画像オブジェクト
    private Image logo;

    // スクリプトを管理するオブジェクト
    private ScriptController script;

    /**
     * 画像アイコン型の選択リストの初期化、各種画像の読み込み
     */
    public WorldMap() {

        BufferedImage img = GeneralUtil.readImage(ImageResource.StageObject.WORLDMAP_POINT.getValue());

        selectOptionList.add(
                new ImageSelectOption(
                        img,
                        382,
                        125,
                        FontController.Fonts.NORMAL,
                        MAPPOINT1,
                        650,
                        100,
                        48
                    )
                );

        selectOptionList.add(
                new ImageSelectOption(
                        img,
                        382,
                        250,
                        FontController.Fonts.NORMAL,
                        MAPPOINT2,
                        650,
                        100,
                        48
                    )
                );

        selectOptionList.add(
                new ImageSelectOption(
                        img,
                        450,
                        320,
                        FontController.Fonts.NORMAL,
                        MAPPOINT3,
                        650,
                        100,
                        48
                    )
                );

        background = GeneralUtil.readImage(ImageResource.LayeredBackground.WORLDMAP.getValue());
        logo = new Image(
                50,
                320,
                GeneralUtil.readImage(ImageResource.Logo.WORLDMAP.getValue()));

        keyHelpList.add(new StringSelectOption(120, 30, FontController.Fonts.NORMAL, "H:HELP ON/OFF", 16));
        keyHelpList.add(new StringSelectOption(120, 50, FontController.Fonts.NORMAL, "↑/↓: せんたく", 16));
        keyHelpList.add(new StringSelectOption(120, 70, FontController.Fonts.NORMAL, "ENTER: けってい", 16));

        effect.addEffect(new SceneChange1(effect));
        effect.addEffect(new SceneChange2(effect));

        setScriptList();
    }

    /**
     * レコード一覧用のスクリプトを読み込む
     */
    private void setScriptList() {
        // 店員のスクリプトを設定
        script = new ScriptController(
                600,
                150,
                680,
                150,
                ImageResource.VinylIcon.WINDOW.getValue()
                );

        script.setScript(
                24,
                "こつこつかせぐ",
                FontController.Fonts.NORMAL
                );

        script.setScript(
                24,
                "いっぱつあてる",
                FontController.Fonts.NORMAL
                );

        script.setScript(
                24,
                "おかいもの",
                FontController.Fonts.NORMAL
                );
    }

    /**
     * フレームごとの再描画を行う
     *
     * @param g グラフィックスオブジェクト
     */
    public void paintComponent(Graphics g) {

        // 背景画像の描画
        g.drawImage(
                background,
                0,
                0,
                (int) GameController.getWindow().getWindowWidth(),
                (int) GameController.getWindow().getWindowHeight(),
                0,
                0,
                background.getWidth(),
                background.getHeight(),
                null);

        // 画像アイコンタイプの選択リストを描画
        for(StringSelectOption selectOption: selectOptionList) {
            ImageSelectOption imageIcon = (ImageSelectOption) selectOption;
             int drawWidth = (int) GameController.getWindow().getAbsPosX(imageIcon.getImage().getWidth());
             int drawHeight = (int) GameController.getWindow().getAbsPosY(imageIcon.getImage().getHeight());
             double iconRatio = 1.0;

            if(selectOption == selectOptionList.get(index)) {
                iconRatio = GeneralUtil.getSinValue(selectOptionList.get(index).getTime(), 1.0, 1.0, 0.5) + 1.0;
            }

            g.drawImage(
                     imageIcon.getImage(),
                     (int) GameController.getWindow().getAbsPosX(imageIcon.getImageX()),
                     (int) GameController.getWindow().getAbsPosY(imageIcon.getImageY()),
                     (int) (drawWidth * iconRatio),
                     (int) (drawHeight * iconRatio),
                     null
                     );
        }

        // フォント画像を描画
        StringSelectOption selected = selectOptionList.get(index);
        GeneralUtil.drawImageString(
                selected.getImageFont(),
                selected.getX(),
                selected.getY(),
                selected.getSize(),
                GeneralUtil.ALIGN_CENTER,
                g
                );

        Script s = script.getScriptList().get(index);
        GeneralUtil.drawScript(
                s,
                (int) (script.getX() + 5),
                (int) (script.getY() + 10),
                g
                );

        // カーソルを描画
        ImageSelectOption sso = (ImageSelectOption) selectOptionList.get(index);

          g.drawImage(
                  cursorIcon,
                  (int) GameController.getWindow().getAbsPosX(sso.getImageX() - cursorIcon.getWidth()),
                  (int) GameController.getWindow().getAbsPosY(sso.getImageY()),
                  (int) GameController.getWindow().getAbsPosX(cursorIcon.getWidth()),
                  (int) GameController.getWindow().getAbsPosY(cursorIcon.getHeight()),
                  null
                  );

          double logoRatio = GeneralUtil.getSinValue(logo.getTime(), 0.05, 0.0, 0.01) + 1.0;
          int drawWidth = (int) GameController.getWindow().getAbsPosX(logo.getImage().getWidth() * 5);
          int drawHeight = (int) GameController.getWindow().getAbsPosY(logo.getImage().getHeight() * 5);

          // ロゴを描画
          g.drawImage(
                  logo.getImage(),
                  (int) GameController.getWindow().getAbsPosX(logo.getX()),
                  (int) GameController.getWindow().getAbsPosY(logo.getY()),
                  (int)(drawWidth * logoRatio),
                  (int)(drawHeight * logoRatio),
                  null
                  );

        // キー設定を描画
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

        // キーイベントの処理
        if (keys.get(KeyEvent.VK_UP).isPressed()) {
            previousIndex();
            resetTimes();
            script.getScriptList().get(index).resetIndex();
            new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();

        } else if (keys.get(KeyEvent.VK_DOWN).isPressed()) {
            nextIndex();
            resetTimes();
            script.getScriptList().get(index).resetIndex();
            new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();

        } else if (keys.get(KeyEvent.VK_ENTER).isPressed()) {
            keyConfig.releaseAll();

            new Thread(new SoundController.PlaySE(SoundResource.SE_ENTER)).start();

            switch(index) {

            // アクションステージへ
            case STAGE_ACTION:
                effect.setScene(GameController.getScene(SceneController.ACTION));
                effect.process();
                break;
            // スロットステージへ
            case STAGE_SLOT:
                effect.setScene(GameController.getScene(SceneController.HALL));
                effect.process();
                break;
            // レコードステージへ
            case STAGE_VINYLSHOP:
                effect.setScene(GameController.getScene(SceneController.VINYL));
                effect.process();
                break;
            default:
            }
        } else if(keys.get(KeyEvent.VK_ESCAPE).isPressed()) {
            GameController.getWindow().pushScene(GameController.getScene(SceneController.PAUSE));
            keyConfig.releaseAll();
        }

        // 操作キー一覧の表示
        if(keyConfig.getKeys().get(KeyEvent.VK_H).isPressed()) {
            super.helpOn = !super.helpOn;
        }

        selectOptionList.get(index).proceed(dt);
        script.getScriptList().get(index).proceedIndex(dt);
        logo.proceed(dt);
    }

    private void resetTimes() {
        for(StringSelectOption s: selectOptionList) {
            s.resetTime();
        }
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
        return new Title();
    }

    /**
     * 自身のシーンで使用するBGMのファイルパスを返す
     *
     * @return BGMのファイルパス
     */
    public String getSound() {
        return SoundResource.BGM_WORLDMAP;
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
        return GameScene.BGM_LOOP;
    }

    /**
     * 自身のシーンで使用するBGMの再生区間を返す
     *
     * @return BGM再生区間を表す始点と終点の値の組
     */
    public Point getDuration() {
        return new Point(530000, 1815000);
    }

    /**
     * 各パラメータを初期化する
     */
    public void initParam() {
        super.index = 0;
        super.helpOn = false;
        effect.initEffect();
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
