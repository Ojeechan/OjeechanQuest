package classes.scenes.old;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.controllers.GameController;
import classes.controllers.KeyController;
import classes.controllers.SceneController;
import classes.controllers.SoundController;
import classes.scenes.old.assets.Player;
import classes.scenes.old.logics.DrawLogic;
import classes.scenes.old.logics.UpdateLogic;
import classes.utils.GeneralUtil;

import interfaces.GameScene;
import interfaces.Calculation;

/**
 * アクションステージでゴールした際のアニメーションを定義するクラス
 *
 * @author Naoki Yoshikawa
 **/
@SuppressWarnings("serial")
public class Goal extends BaseActionOperator implements GameScene {

    // 背景画像オブジェクト
    private BufferedImage background;
    private BufferedImage background2;

    // 背後で実行するゲームシーンオブジェクト
    private Calculation baseScene;

    // フェードに使用するアルファ値
    private float alpha;

    // アニメーション中のプレイヤーの描画位置のX座標
    private double x;

    /**
     * 背景画像の読み込み、アニメーション用プレイヤースプライトの設定
     */
    public Goal() {
        initParam();

        background = GeneralUtil.readImage(ImageResource.LayeredBackground.GOAL.getValue());
        background2 = GeneralUtil.readImage(ImageResource.LayeredBackground.GOAL2.getValue());
        keyConfig.setKeys(KeyEvent.VK_ENTER, keyConfig.new Key(KeyController.DETECT_INITIAL_PRESS_ONLY));

        // あとでコード的な裏付けを追加するが、今は一旦ゴリ押しでキャストする
        baseScene = (Calculation)GameController.getWindow().getBasePanel();
        Player original = baseScene.getPlayer();
        original.stop();
        original.switchLabel(ImageResource.RUN_RIGHT);
        x = - original.getActualWidth();
    }

    /**
     * フレームごとの再描画を行う
     *
     * @param g グラフィックスオブジェクト
     */
    public void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);

        g2.setComposite(composite);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, (int) GameController.getWindow().getWindowWidth(), (int) GameController.getWindow().getWindowHeight());

        BufferedImage image;
        if(x < GameController.getWindow().getWindowWidth() / 2) {
            image = background;
        } else {
            image = background2;
        }

        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        g2.setComposite(composite);
        g.drawImage(image, 64, 48, image.getWidth() * 8, image.getHeight() * 8, null);
         DrawLogic.paintGoalLogic(baseScene, x, g);
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
        if(x <= GameController.getWindow().getWindowWidth() / 2) {
            if(x == GameController.getWindow().getWindowWidth() / 2) {
                new Thread(new SoundController.PlaySE(SoundResource.SE_CLOCK)).start();
            }
            x++;
        }

        if(alpha < 0.97) {
            alpha += 0.004f;
        } else {
            alpha = 1;
        }
        Map<Integer, KeyController.Key> keys = getKeyConfig().getKeys();
        if (keys.get(KeyEvent.VK_ENTER).isPressed()) {
            GameController.getWindow().changeScene(GameController.getScene(SceneController.WORLD));
        }

        UpdateLogic.goalLogic(baseScene, dt);
        ;
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
        return new Goal();
    }

    /**
     * 自身のシーンで使用するBGMのファイルパスを返す
     *
     * @return BGMのファイルパス
     */
    public String getSound() {
        return SoundResource.BGM_AMBIENT1;
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
        super.helpOn = true;
        alpha = 0.5f;
    }

    /**
     * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
     *
     * @param res 呼び出し元からのレスポンスコード
     */
    public void callback(int res) {

    }
}
