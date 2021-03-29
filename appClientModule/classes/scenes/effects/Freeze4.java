package classes.scenes.effects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.controllers.EffectController;
import classes.controllers.GameController;
import classes.controllers.WindowController;
import classes.scenes.BaseSystemOperator;
import classes.scenes.old.assets.Player;
import classes.scenes.old.utils.DrawUtil;
import classes.scenes.slot.SlotStage;
import classes.utils.GeneralUtil;

import interfaces.GameScene;

/**
 * フリーズ時のエフェクトを定義するクラス
 *
 * @author Naoki Yoshikawa
 */
@SuppressWarnings("serial")
public class Freeze4 extends BaseSystemOperator implements GameScene {

    /* 定数群 */
	private final int FADE_START = 0; // 背景が明るくなり始める時間
	private final int FADE_END = 500; // 背景が明るくなり終わる時間
	private final int SPEED = 102;    // アニメーションのスピード

	// フレームカウント数
	private double count;

	// フレームカウント数上限
	private double end;

	// エフェクト実行時のBGMファイルのパス
	private String sound;

	// エフェクトキューの管理オブジェクト
	private EffectController controller;

	// ムービーで使用するゲームシーンとプレイヤーオブジェクト
	private SlotStage slot;
	private Player player;

	// ムービーのアニメーションフレーム
	private BufferedImage[] animation;

	// アニメーションフレームのインデックス
	private int index;

	/**
	 * 制御用パラメータ、エフェクト管理オブジェクトの設定
     *
	 * @param controller エフェクト管理オブジェクト
	 */
	public Freeze4(EffectController controller) {
		this.end = 2000;
		this.sound = SoundResource.BGM_FREEZE_MOVIE2;
		this.controller = controller;
		this.animation = new BufferedImage[] {
				GeneralUtil.readImage(ImageResource.SlotIcon.MOVIE1.getValue()),
				GeneralUtil.readImage(ImageResource.SlotIcon.MOVIE2.getValue()),
				GeneralUtil.readImage(ImageResource.SlotIcon.MOVIE3.getValue()),
				GeneralUtil.readImage(ImageResource.SlotIcon.MOVIE4.getValue()),
				GeneralUtil.readImage(ImageResource.SlotIcon.MOVIE2.getValue())
		};
		this.slot = (SlotStage) controller.getScene();
		int playerSize = 32;
		this.player = Player.getDefault(
				(int) (WindowController.WIDTH / 2),
				(int) (WindowController.HEIGHT / 2),
				playerSize,
				playerSize
				);
		player.setWidthRatio(4);
		player.setHeightRatio(4);
		initParam();
	}

	/**
	 * フレームごとの再描画を行う
	 *
	 * @param g グラフィックスオブジェクト
	 */
	@Override
 	public void paintComponent(Graphics g) {
		WindowController w = GameController.getWindow();

		Graphics2D g2 = (Graphics2D) g;
    	AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha());
        g2.setComposite(composite);
        g2.setColor(Color.BLACK);
        g2.fillRect(
        		0,
        		0,
        		(int) w.getWindowWidth(),
        		(int) w.getWindowHeight()
        		);
        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
        g2.setComposite(composite);

        g.setColor(Color.BLACK);
 		g.fillRect(
 				0,
 				(int) w.getAbsPosY(180),
 				(int) w.getWindowWidth(),
 				(int) w.getAbsPosY(180)
 				);

 		if(index < 2 || index > 3) {
 			DrawUtil.drawSprite(
 					player,
 					-player.getImageWidth() / 2,
 					player.getActualHeight() / 2,
 					g
 					);
 		}
		g.drawImage(
				animation[index],
				(int) w.getAbsPosX((WindowController.WIDTH- animation[index].getWidth()) / 2),
				(int) w.getAbsPosY(180),
				(int) w.getAbsPosX(animation[index].getWidth()),
				(int) w.getAbsPosY(animation[index].getHeight() * 0.9),
				null
				);
 	}

 	/**
 	 * フレームカウントを進める
 	 */
	private void proceed(double dt) {
		if(index > 3) {
 			player.stop();
 		}
		count += SPEED * dt ;
 		player.animate(dt);

 	}

	/**
	 * 現在設定されているアルファ値を返す
	 *
	 * @return アルファ値
	 */
	private float getAlpha() {
		if(count < FADE_START) {
			return 1f;
		} else {
			float alpha = Math.min(1f, (float) 1f - (float) (count - FADE_START)/4 / (float) FADE_END);
			return Math.max(0f, alpha);
		}
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
			GameController.getWindow().popScene();
			controller.process();
		}

		slot.getSlot().reverseReel(dt/10.0);

		if(slot.getSlot().getLever().getIsDown()) {
    		slot.getSlot().animateLever(dt);
    	}

		index = Math.min((int) (count / (end / animation.length)), animation.length - 1);

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
		return new Freeze4(controller);
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
		count = 0;
		this.index = 0;
		player.run(1, 0);
	}

	/**
	 * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
	 *
	 * @param res 呼び出し元からのレスポンスコード
	 */
	public void callback(int res) {

	}
}
