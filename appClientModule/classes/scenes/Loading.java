package classes.scenes;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.controllers.GameController;
import classes.controllers.SoundController;
import classes.controllers.WindowController;
import classes.scenes.BaseSystemOperator;
import classes.utils.GeneralUtil;

import interfaces.GameScene;

/**
 * ローディング画面を定義するゲームシーンオブジェクト
 *
 * @author Naoki Yoshikawa
 */
@SuppressWarnings("serial")
public class Loading extends BaseSystemOperator implements GameScene {

    /* 定数群 */
    private final int SPEED = 10;// アニメーションのスピード

    // フレームカウント数
    private double count;

    // キャラクター、ロゴの画像オブジェクト
    private BufferedImage[] playerIcons;
    private BufferedImage loading;

    /**
     * ロゴ画像の読み込み
     */
    public Loading() {
        playerIcons = new BufferedImage[] {
                GeneralUtil.readImage(ImageResource.PlayerBack.PATH1.getValue()),
                GeneralUtil.readImage(ImageResource.PlayerHalfup.PATH1.getValue()),
                GeneralUtil.readImage(ImageResource.Player1StandRight.PATH1.getValue()),
                GeneralUtil.readImage(ImageResource.PlayerHalfdown.PATH1.getValue()),
                GeneralUtil.readImage(ImageResource.PlayerFront.PATH1.getValue()),
        };
        loading = GeneralUtil.readImage(ImageResource.Logo.LOADING.getValue());
        initParam();
    }

    /**
     * フレームごとの再描画を行う
     *
     * @param g グラフィックスオブジェクト
     */
    @Override
     public void paintComponent(Graphics g) {
        g.drawImage(
                loading,
                (int) GameController.getWindow().getAbsPosX(700),
                (int) GameController.getWindow().getAbsPosY(350),
                (int) GameController.getWindow().getAbsPosX(130),
                (int) GameController.getWindow().getAbsPosY(120),
                null
                );

         if(count < 5) {
             g.drawImage(
                     playerIcons[(int)count],
                     (int) GameController.getWindow().getAbsPosX(800),
                     (int) GameController.getWindow().getAbsPosY(380),
                     (int) GameController.getWindow().getAbsPosX(128),
                     (int) GameController.getWindow().getAbsPosY(128),
                     null
                     );
         } else {
             g.drawImage(
                playerIcons[(int) -count  + 8],
                (int) GameController.getWindow().getAbsPosX(800),
                (int) GameController.getWindow().getAbsPosY(380),
                (int) GameController.getWindow().getAbsPosX(928),
                (int) GameController.getWindow().getAbsPosY(508),
                playerIcons[(int) -count  + 8].getWidth(),
                0,
                0,
                playerIcons[(int) -count  + 8].getHeight(),
                null
                );
         }

     }

     /**
      * フレームカウントを進める
      */
    private void proceed(double dt) {
         count = (count + SPEED * dt) % 8.0;
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
        proceed(dt);
    }

    /**
     * JLayeredPanelに追加するために自身のインスタンスを返す
     * @see interfaces.GameScene
     *
     * @return 自身のパネルインスタンス
     */
    @Override
    public JLayeredPane getPanel() {
        return this;
    }

    /**
     * 自身の初期状態のインスタンスを返す
     * @see interfaces.GameScene
     *
     * @return 自身の初期状態のインスタンス
     */
    @Override
    public GameScene getNewScene() {
        return new Loading();
    }

    /**
     * 自身のシーンで使用するBGMのファイルパスを返す
     * @see interfaces.GameScene
     *
     * @return BGMのファイルパス
     */
    @Override
    public String getSound() {
        return null;
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
        return GameScene.BGM_ONCE;
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
        count = 0;
    }

    /**
     * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
     *
     * @param res 呼び出し元からのレスポンスコード
     */
    public void callback(int res) {
        WindowController w  = GameController.getWindow();
        w.popScene();
        //w.playBGM(w.getBasePanel());
        // 実験でシーンを渡してコールバックさせる
        w.callBackBGM(w.getBasePanel());

    }
}
