package classes.scenes.old.assets;

import classes.constants.SoundResource;
import classes.controllers.SoundController;
import classes.utils.GeneralUtil;

/**
 * アクションステージ用のボールアセットを定義するクラス
 *
 * @author Naoki Yoshikawa
 */
public class Ball extends BaseSprite {

	/* Todo: 壁バウンド　地面バウンド */

	//
	private static final int FRAME_INTERVAL = 10;

	// プレーヤーの衝突などで動力を与えられた状態かどうか
	private boolean isActive;

	// バウンドを停止する時間を計測するカウンター
	private int frameCount;

	public Ball(double x, double y, int width, int height, String path, double size) {
		super(x, y, GeneralUtil.readImage(path));
		widthRatio = size;
        heightRatio = size;
    	weight = 10;
    	newDegree = 15;
    	isActive = false;
    	frameCount = 0;
	}

	/**
	 * 与えられた速度で吹き飛ぶ
	 *
	 * @param vx X方向の速度
	 * @param vy Y方向の速度
	 */
	public void blown(double vx, double vy) {
		// 当たり判定の無効時間を追加するまでは効果音も保留
		this.vx = vx;
		this.vy = vy;
		new Thread(new SoundController.PlaySE(SoundResource.SE_BOUNCE)).start();
	}

	/**
	 * 壁でのバウンド時にX方向の速度を半減する
	 */
	public void bounceX() {
		// 当たり判定の無効時間を追加するまでは効果音も保留
		new Thread(new SoundController.PlaySE(SoundResource.SE_BOUNCE)).start();
		this.vx = -this.vx / 2;
	}

	/**
	 * 地面でのバウンド時にY方向の速度を半減させる
	 */
	public void bounceY() {
		if(!onGround) {
			new Thread(new SoundController.PlaySE(SoundResource.SE_BOUNCE)).start();
		}
		this.vy = -this.vy / 2;
	}

	/**
	 * <pre>
	 * ボールが動力を持っているかどうか
	 * 動力が残っている場合 true
	 * </pre>
	 *
	 * @return ボールが動力を持っているかどうか
	 */
	public boolean getIsActive() {
		return isActive;
	}

	/**
	 * プレイヤーと衝突し動力エネルギーを得る
	 */
	public void collided() {
		isActive = true;
		frameCount = 1;
	}

	/**
	 * 徐々に動力エネルギーを減衰させる
	 * Todo: 自分でもこのロジックの意図が不明…
	 */
	public void amortize(){
		frameCount++;
		frameCount %= FRAME_INTERVAL;
		if(frameCount == 0) {
			isActive = false;
		}
	}

	/**
	 * 衝突時の吹き飛びかたについての実験メソッド
	 */
	public void blown2() {
		System.out.println("@ball vx = " + vx);
		if(Math.abs(vx) < 0.1) {
    		vx = 0;
    	}
		if(this.vx > 0) {
			if(onGround) {
				this.vx -= 0.05;
			}
			this.vx -= 0.05;
		} else if(this.vx < 0) {
			if(onGround) {
				this.vx += 0.05;
			}
			this.vx += 0.05;
		}
	}

}
