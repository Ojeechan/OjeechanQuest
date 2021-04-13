package classes.scenes.effects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.controllers.EffectController;
import classes.controllers.GameController;
import classes.controllers.SoundController;
import classes.controllers.WindowController;
import classes.scenes.BaseSystemOperator;
import classes.utils.GeneralUtil;

import interfaces.GameScene;

/**
 * シーン遷移時のエフェクトを定義するクラス
 *
 * @author Naoki Yoshikawa
 */
@SuppressWarnings("serial")
public class Turntable2 extends BaseSystemOperator implements GameScene {

    /* 定数群 */
    private final int SPEED = 50;// アニメーションのスピード

    // エフェクト実行時のBGMファイルのパス
    private String sound;

    private double bound;
    private double height;
    private double vy;

    // エフェクトキューの管理オブジェクト
    private EffectController controller;

    private BufferedImage turntable;

    /**
     * 制御用パラメータ、遷移先シーン、エフェクト管理オブジェクトの設定
     *
     * @param controller エフェクト管理オブジェクト
     */
    public Turntable2(EffectController controller) {
        this.sound = null;
        this.controller = controller;
        this.turntable = GeneralUtil.readImage(ImageResource.VinylIcon.TURNTABLE_CLOSE.getValue());
        initParam();
    }

    /**
     * フレームごとの再描画を行う
     *
     * @param g グラフィックスオブジェクト
     */
    @Override
     public void paintComponent(Graphics g) {
         g.setColor(Color.BLACK);

         g.fillRect(
                 0,
                 0,
                 (int) GameController.getWindow().getWindowWidth(),
                 (int) GameController.getWindow().getWindowHeight()
                 );

         g.drawImage(
                turntable,
                0,
                (int) GameController.getWindow().getAbsPosY(height),
                (int) GameController.getWindow().getAbsPosX(turntable.getWidth()),
                (int) GameController.getWindow().getAbsPosY(turntable.getHeight()),
                null
                );
     }

     /**
      * バウンド時の速度の減衰を表現する
      */
    private void bounce() {
         bound = bound/1.5;
         new Thread(new SoundController.PlaySE(SoundResource.SE_REEL)).start();
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

        vy += SPEED * dt;

        if(bound > 1.5) {
            height += vy;
            if(height > 0) {
                bounce();
                vy = -bound;
            }
        } else {
            WindowController w = GameController.getWindow();
            w.popScene();
            w.getBasePanel().initParam();
            controller.process();
        }
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
        return new Turntable2(controller);
    }

    /**
     * 自身のシーンで使用するBGMのファイルパスを返す
     * @see interfaces.GameScene
     *
     * @return BGMのファイルパス
     */
    @Override
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
        this.bound = 50;
        this.height = -turntable.getHeight();
        this.vy = 0;
    }

    /**
     * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
     *
     * @param res 呼び出し元からのレスポンスコード
     */
    public void callback(int res) {

    }
}
