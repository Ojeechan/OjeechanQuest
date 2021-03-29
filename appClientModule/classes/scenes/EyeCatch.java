package classes.scenes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JLayeredPane;

import classes.constants.SoundResource;
import classes.controllers.GameController;
import classes.scenes.action.assets.*;
import classes.scenes.old.assets.Player;
import classes.scenes.old.logics.DrawLogic;
import interfaces.*;

/**
 * ゲームシーン間でアイキャッチ用のアニメーションを流すゲームシーンオブジェクト
 * @author Naoki Yoshikawa
 */
@SuppressWarnings("serial")
public class EyeCatch extends BaseSystemOperator implements GameScene {

	// 遷移先のゲームシーンオブジェクト
	private GameScene nextScene;

	// キャラクター描画位置のX座標
	private int x;

	// 描画するプレイヤースプライト
	private Player player;

	/**
	 * 遷移先のシーンとプレイヤースプライトを設定する
	 *
	 * @param nextScene 遷移先のシーンオブジェクト
	 */
	EyeCatch(GameScene nextScene) {
		initParam();
		this.nextScene = nextScene;
	}

	/**
	 * フレームごとの再描画を行う
	 *
	 * @param g グラフィックスオブジェクト
	 */
    public void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, (int) GameController.getWindow().getWindowWidth(), (int) GameController.getWindow().getWindowHeight());
     	DrawLogic.drawEyeCatchLogic(player, x, g);
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
		x += 3;
		if(x - player.getActualWidth() > GameController.getWindow().getWindowWidth()) {
			GameController.getWindow().changeScene(nextScene);
		}
		player.animate(dt);
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
		return new EyeCatch(nextScene);
	}

	/**
     * 自身のシーンで使用するBGMのファイルパスを返す
     *
     * @return BGMのファイルパス
     */
	public String getSound() {
		return SoundResource.BGM_MAKAI;
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
		x = 0;
		player = Player.getDefault(x, 0, 32, 32);
		player.setImageBaseY(220);
	}

	/**
	 * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
	 *
	 * @param res 呼び出し元からのレスポンスコード
	 */
	public void callback(int res) {

	}
}
