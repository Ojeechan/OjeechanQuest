package classes.scenes.effects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JLayeredPane;

import classes.constants.SoundResource;
import classes.controllers.EffectController;
import classes.controllers.GameController;
import classes.controllers.WindowController;
import classes.scenes.BaseSystemOperator;
import classes.scenes.slot.SlotStage;

import interfaces.GameScene;

/**
 * フリーズ時のエフェクトを定義するクラス
 *
 * @author Naoki Yoshikawa
 */
@SuppressWarnings("serial")
public class Freeze1 extends BaseSystemOperator implements GameScene {

    /* 定数群 */
    private final int SPEED = 3000;// アニメーションのスピード

    // フレームカウント数
    private double count;

    // フレームカウント数上限
    private double end;

    // エフェクト実行時のBGMファイルのパス
    private String sound;

    // エフェクトキューの管理オブジェクト
    private EffectController controller;

    // スロットシーンオブジェクト
    private SlotStage slot;

    /**
     * 制御用パラメータ、遷移先シーン、エフェクト管理オブジェクトの設定
     *
     * @param controller エフェクト管理オブジェクト
     */
    public Freeze1(EffectController controller) {
        initParam();
        this.end = WindowController.HEIGHT / 2;
        this.sound = SoundResource.BGM_PUCHUN;
        this.controller = controller;
        this.slot = (SlotStage) controller.getScene();
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
                 (int) GameController.getWindow().getAbsPosY(count)
                 );
         g.fillRect(
                 0,
                 (int) GameController.getWindow().getAbsPosY(WindowController.HEIGHT - count),
                 (int) GameController.getWindow().getWindowWidth(),
                 (int) GameController.getWindow().getWindowHeight()
                 );
     }

     /**
      * フレームカウントを進める
      */
    private void proceed(double dt) {
         count += SPEED * dt ;
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
        if(count > end) {
            slot.getSlot().adjustReel(new double[] {0, 0, 0});
            GameController.getWindow().popScene();
            controller.process();
            return;
        }
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
        return new Freeze1(controller);
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
    @Override
    public void initParam() {
        count = 0;
    }

    /**
     * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
     *
     * @param res 呼び出し元からのレスポンスコード
     */
    public void callback(int res) {

    }
}
