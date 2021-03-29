package classes.scenes.effects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JLayeredPane;

import classes.controllers.EffectController;
import classes.controllers.GameController;
import classes.controllers.WindowController;
import classes.scenes.BaseSystemOperator;

import interfaces.GameScene;

@SuppressWarnings("serial")
public class Encount2 extends BaseSystemOperator implements GameScene {

	/* 定数群 */
	private final int SPEED = 2000;// アニメーションのスピード

	// フレームカウント数
	private double count;

	// フレームカウント数上限
	private double end;

	// エフェクト実行時のBGMファイルのパス
	private String sound;

	// エフェクトキューの管理オブジェクト
	private EffectController controller;

	/**
	 * 制御用パラメータ、エフェクト管理オブジェクトの設定
	 *
	 * @param controller エフェクト管理オブジェクト
	 */
	public Encount2(EffectController controller) {
		initParam();
		this.end = WindowController.WIDTH;
		this.sound = null;
		this.controller = controller;
	}

	/**
	 * フレームごとの再描画を行う
	 *
	 * @param g グラフィックスオブジェクト
	 */
	@Override
 	public void paintComponent(Graphics g) {
 		//

 		g.setColor(Color.BLACK);

 		int drawWidth = (int) GameController.getWindow().getAbsPosX(count);

		g.fillRect(
				drawWidth,
				0,
				(int) GameController.getWindow().getWindowWidth(),
				(int) (GameController.getWindow().getWindowHeight() / 4)
				);
		g.fillRect(
				0,
				(int) (GameController.getWindow().getWindowHeight() / 4),
				(int) (GameController.getWindow().getWindowWidth() - drawWidth),
				(int) (GameController.getWindow().getWindowHeight() / 4)
				);
		g.fillRect(
				drawWidth,
				(int) (GameController.getWindow().getWindowHeight() / 2),
				(int) (GameController.getWindow().getWindowWidth()),
				(int) (GameController.getWindow().getWindowHeight() / 4)
				);
		g.fillRect(
				0,
				(int) (GameController.getWindow().getWindowHeight() / 4 * 3),
				(int) (GameController.getWindow().getWindowWidth() - drawWidth),
				(int) (GameController.getWindow().getWindowHeight() / 4)
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
			WindowController w = GameController.getWindow();
			w.playBGM(w.getBasePanel());
			w.popScene();
			controller.process();
		}
		proceed(dt);
		;
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
		return new Encount2(controller);
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
     * 各パラメータを初期化する
     */
	public void initParam() {
		count = 0;
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
	 * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
	 *
	 * @param res 呼び出し元からのレスポンスコード
	 */
	public void callback(int res) {

	}
}