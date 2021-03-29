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

/**
 * シーン遷移時のエフェクトを定義するクラス
 *
 * @author Naoki Yoshikawa
 */
@SuppressWarnings("serial")
public class SceneChange1 extends BaseSystemOperator implements GameScene {

	/* 定数群 */
	private final int SPEED = 5600;// アニメーションのスピード

	// フレームカウント数
	private int count;

	// フレームカウント数上限
	private int end;

	// エフェクト実行時のBGMファイルのパス
	private String sound;


	// エフェクトキューの管理オブジェクト
	private EffectController controller;

	private Color[] cs;

	/**
	 * 制御用パラメータ、遷移先シーン、エフェクト管理オブジェクトの設定
	 *
	 * @param controller エフェクト管理オブジェクト
	 */
	public SceneChange1(EffectController controller) {
		initParam();
		this.end = (int) (WindowController.HEIGHT * 7);
		this.sound = null;
		this.controller = controller;
		this.cs = new Color[] {
				Color.WHITE,
				Color.YELLOW,
				Color.CYAN,
				Color.GREEN,
				Color.MAGENTA,
				Color.RED,
				Color.BLUE
		};
	}

	/**
     * 各パラメータを初期化する
     */
	public void initParam() {
		count = 0;
	}

	/**
	 * フレームごとの再描画を行う
	 *
	 * @param g グラフィックスオブジェクト
	 */
 	public void paintComponent(Graphics g) {
		double drawWidth = (int) (GameController.getWindow().getWindowWidth() / 7.0);
		for(int i = 0; i < cs.length; i++) {
			int drawHeight = (int) GameController.getWindow().getAbsPosY(count / (i + 1));
			g.setColor(cs[i]);
			g.fillRect(
					(int) (drawWidth * i),
					0,
					(int) drawWidth,
					drawHeight
					);
			g.fillOval(
					(int) (drawWidth * i),
					(int) (drawHeight - drawWidth / 2.0),
					(int) drawWidth,
					(int) drawWidth
					);
		}
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
			GameController.getWindow().setGameScene(controller.getScene());
			GameController.getWindow().popScene();
			controller.process();
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
		return new SceneChange1(controller);
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
     * 自身のシーンで使用するBGMの再生区間を返す
     *
     * @return BGM再生区間を表す始点と終点の値の組
     */
	public Point getDuration() {
		return new Point(0, GameScene.BGM_END);
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
	 * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
	 *
	 * @param res 呼び出し元からのレスポンスコード
	 */
	public void callback(int res) {

	}
}
