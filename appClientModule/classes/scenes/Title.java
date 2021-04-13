package classes.scenes;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.containers.Background;
import classes.controllers.FontController;
import classes.controllers.GameController;
import classes.controllers.KeyController;
import classes.controllers.SceneController;
import classes.controllers.SoundController;
import classes.controllers.WindowController;
import classes.math.Vector2;
import classes.scenes.action.assets.Player;
import classes.scenes.effects.SceneChange1;
import classes.scenes.effects.SceneChange2;
import classes.ui.StringSelectOption;
import classes.utils.GeneralUtil;

import interfaces.GameScene;

/**
 * タイトル画面を定義する行うゲームシーンオブジェクト
 *
 * @author  Naoki Yoshikawa
 */
@SuppressWarnings("serial")
public class Title extends BaseSystemOperator implements GameScene {

    // シーンで使用する選択リストの文言
    private static final String OPTION_NEWGAME = "はじめから";
    private static final String OPTION_RESUME = "つづきから";

    // トランジション対象のロゴのRGB
    private Color color;
    private final double RED = 255.0;
    private final double GREEN = 102.0;
    private final double BLUE = 0.0;

    // RGBをトランジションさせるロゴのピクセルのリスト
    private List<Point> pixelList;

    // タイトルロゴ画像、背景
    private BufferedImage title;
    private Background[] backgrounds;

    // フェードレイヤーのアルファ値
    private float alpha;

    // アニメーション用のタイムパラメータ
    private double time;

    // アニメーション用プレイヤーオブジェクト
    private Player player;

    /**
     * シーンに必要なパラメータを初期化
     */
    public Title() {
        // 選択リストの座標、フォント、表示する文言を設定
        selectOptionList.add(
                new StringSelectOption(
                        (int) (WindowController.WIDTH / 2),
                        (int) 320,
                        FontController.Fonts.NORMAL,
                        OPTION_NEWGAME,
                        32
                        )
                );

        selectOptionList.add(
                new StringSelectOption(
                        (int) (WindowController.WIDTH / 2),
                        (int) 384,
                        FontController.Fonts.NORMAL,
                        OPTION_RESUME,
                        32
                        )
                );

        title = GeneralUtil.readImage(ImageResource.Logo.TITLE.getValue());

        // タイトルロゴ画像から、指定RGBをもつピクセルを取得する
        color = new Color((int) RED, (int) GREEN, (int) BLUE);
        pixelList = GeneralUtil.checkColor(title, color);

        keyHelpList.add(new StringSelectOption(120, 30, FontController.Fonts.NORMAL, "H:HELP ON/OFF", 16));
        keyHelpList.add(new StringSelectOption(120, 50, FontController.Fonts.NORMAL, "↑/↓: せんたく", 16));
        keyHelpList.add(new StringSelectOption(120, 70, FontController.Fonts.NORMAL, "ENTER: けってい", 16));

        effect.addEffect(new SceneChange1(effect));
        effect.addEffect(new SceneChange2(effect));

        backgrounds = new Background[] {
                new Background(
                    GeneralUtil.readImage(ImageResource.LayeredBackground.BASE.getValue()),
                    8,
                    1
                    ),
                new Background(
                        GeneralUtil.readImage(ImageResource.LayeredBackground.LAYER3.getValue()),
                        6,
                        1
                        ),
                new Background(
                        GeneralUtil.readImage(ImageResource.LayeredBackground.LAYER2.getValue()),
                        3,
                        1
                        ),

                };

        player = Player.getDefault(new Vector2(0, 0), WindowController.WIDTH/2, WindowController.HEIGHT);
        player.initParam();
    }

    /**
     * フレームごとの再描画を行う
     *
     * @param g 描画対象のグラフィックオブジェクト
     */
    public void paintComponent(Graphics g) {

        WindowController w = GameController.getWindow();

        // 背景の描画
        for(Background background: backgrounds) {

            int leftEnd = (int) player.getPosVec().getX() / background.getScrollRate();
            int rightEnd = (int) (leftEnd + WindowController.WIDTH);

            // 描画範囲が背景画像の右端より左に収まっている場合
            if(rightEnd < background.getImage().getWidth()) {
                g.drawImage(
                    background.getImage(),
                    0,
                    0,
                    (int) GameController.getWindow().getWindowWidth(),
                    (int) GameController.getWindow().getWindowHeight(),
                    leftEnd,
                    0,
                    rightEnd,
                    background.getImage().getHeight(),
                    null
                );

            // 描画範囲が背景画像の右端より先にある場合、左端から折り返して描画する
            } else {
                g.drawImage(
                    background.getImage(),
                    0,
                    0,
                    (int) w.getAbsPosX(background.getImage().getWidth() - leftEnd),
                    (int) GameController.getWindow().getWindowHeight(),
                    leftEnd,
                    0,
                    background.getImage().getWidth(),
                    background.getImage().getHeight(),
                    null
                );

                g.drawImage(
                    background.getImage(),
                    (int) w.getAbsPosX(background.getImage().getWidth() - leftEnd + 1),
                    0,
                    (int) GameController.getWindow().getWindowWidth(),
                    (int) GameController.getWindow().getWindowHeight(),
                    0,
                    0,
                    rightEnd - background.getImage().getWidth(),
                    background.getImage().getHeight(),
                    null
                );
            }
        }

        // プレイヤーの描画
        g.drawImage(
                player.getImage(),
                (int) w.getAbsPosX(player.getImageLeftX()),
                (int) w.getAbsPosY(WindowController.HEIGHT - player.getImageHeight()),
                (int) w.getAbsPosX(player.getImageWidth()),
                (int) w.getAbsPosY(player.getImageHeight()),
                null
                );


        // 背景とタイトルの間のシェード
        Graphics2D g2 = (Graphics2D) g;
        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2.setComposite(composite);
        g2.setColor(Color.BLACK);
        g2.fillRect(
                0,
                0,
                (int) GameController.getWindow().getWindowWidth(),
                (int) GameController.getWindow().getWindowHeight()
                );

        // シェードより前面はアルファ値1
        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        g2.setComposite(composite);

        int drawWidth = (int) GameController.getWindow().getAbsPosX(title.getWidth());
        int drawHeight = (int) GameController.getWindow().getAbsPosY(title.getHeight());

        // タイトルロゴの描画
        g.drawImage(
                title,
                (int) ((GameController.getWindow().getWindowWidth() - drawWidth)/2),
                (int) (GameController.getWindow().getWindowHeight() / 10),
                drawWidth,
                drawHeight,
                null
                );

        // 選択リストの描画
        for(int i = 0; i < selectOptionList.size(); i++) {
            StringSelectOption sso = selectOptionList.get(i);
            if(i == index) {
                GeneralUtil.drawStringShiver(
                        sso.getImageFont(),
                        sso.getX(),
                        sso.getY(),
                        32,
                        GeneralUtil.ALIGN_CENTER,
                        g2
                        );
            } else {
                GeneralUtil.drawImageString(
                        sso.getImageFont(),
                        sso.getX(),
                        sso.getY(),
                        32,
                        GeneralUtil.ALIGN_CENTER,
                        g2
                        );
            }
        }

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

        // ロゴの色をトランジションする
        GeneralUtil.changeColor(title, pixelList, color);

        // sine関数でロゴの色を変化させる
        //double sin = GeneralUtil.getSinValue(System.currentTimeMillis() / 1000.0, 1.0, 1.0, 2.0);
        double sin = GeneralUtil.getSinValue(time, 1.0, 1.0, 2.0);
        color = new Color((int) (Math.min(RED * sin, 255.0)), (int) (Math.min(GREEN * sin, 255.0)), (int) BLUE);

        // 黒背景を徐々に明るく透明にする
        if(alpha > 0.9) {
            alpha -= 0.02 * dt;
        } else if(alpha > 0.2) {
            alpha -= 0.01 * dt;
        }

        // 選択リストに対するキーイベントの処理
        if (keys.get(KeyEvent.VK_UP).isPressed()) {
            previousIndex();
            new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();
        } else if (keys.get(KeyEvent.VK_DOWN).isPressed()) {
            nextIndex();
            new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();
        } else if (keys.get(KeyEvent.VK_ENTER).isPressed()) {
            new Thread(new SoundController.PlaySE(SoundResource.SE_ENTER)).start();
            switch(index) {
            // "はじめから"を選択した場合コインをリセット
            case 0:
                GameController.resetCoin();
                GameController.resetVinylList();
                GameController.resetParam();

            // はじめから、つづきから共にワールドマップへ遷移する
            case 1:
                effect.setScene(GameController.getScene(SceneController.WORLD));
                effect.process();
                break;
            default:
                break;
            }
        }

        // 操作キー一覧の表示
        if(keyConfig.getKeys().get(KeyEvent.VK_H).isPressed()) {
            super.helpOn = !super.helpOn;
        }

        player.move(new Vector2(1, 0), dt);
        player.proceed(dt);
        double nx = player.getPosVec().getX() % 1280.0;
        player.setPosVec(new Vector2(nx, 0));

        time = (time + dt) % (2 * Math.PI);
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
     * インターフェースGameSceneの実装メソッド @see GameScene
     *
     * @return 自身のインスタンス
     */
    public GameScene getNewScene() {
        return new Title();
    }


    /**
     * インターフェースGameSceneの実装メソッド @see GameScene
     *
     * @return 自ステージのBGMのパスを返す
     */
    public String getSound() {
        return SoundResource.BGM_RELAX;
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
        return new Point(0, GameScene.BGM_END);
    }

    /**
     * 各パラメータを初期化する
     */
    public void initParam() {
        super.index = 0;
        super.helpOn = true;
        alpha = 1f;
        time = 0.0;
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
