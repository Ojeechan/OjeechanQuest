package classes.scenes.old.assets;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.controllers.GameController;
import classes.controllers.SoundController;
import classes.utils.GeneralUtil;

import interfaces.Animation;

/**
 * アクションステージのプレイヤースプライトのパラメータ・操作を定義するクラス
 *
 * @author  Naoki Yoshikawa
 */
public class Player extends BaseSprite implements Animation {
	/* 定数パラメータ */
    // X方向の初期移動速度
    public static final double DEFAULT_SPEED = 100;

    // 幽霊のX方向の移動速度
    public static final double GHOST_SPEED = 50;

    // 標準ジャンプ力
    public static final double DEFAULT_JUMP = 12;

    // ダッシュ倍率
    public static final double DEFAULT_DASH = 2.0;

    // 死んで飛び上がって着地した時の座標
    private double deadX;
    private double deadY;

    // ゲームオーバー時のスプライト画像
    private String deadImage;

    // 敵に衝突したかどうか
    private boolean isAlive;

    // 幽霊状態かどうか
    private boolean isSpook;

    // ジャンプ時のY方向の速度
    private double jumpSpeed;

    // 敵と衝突した際のX方向の速度
    private double blownX;

    // 空中ジャンプできるかどうか
    private boolean canJumpTwice;

    // ダッシュ時の速度倍率
    private double dashRate;

    // しゃがみ状態かどうか
    private boolean isDucking;

    /**
     * Animationインターフェース用のメンバー変数
     */
    protected Map<String, List<BufferedImage>> frameHolder;
	protected String currentLabel;
	protected double subIndex;
	protected double speedRate; // 指定値nを取り、アニメーション速度を1/n倍にする

	/**
	 * プレイヤースプライトに必要なパラメータを初期化
	 *
	 * @param x        プレイヤーの初期表示用X座標
	 * @param y        プレイヤーの初期表示用Y座標
	 * @param width    プレイヤー画像を描画する際の幅
	 * @param height   プレイヤー画像の描画する際の高さ
	 * @param fileName アニメーションに使用する画像群のファイルパス
	 */
    public Player(double x, double y, int width, int height, ImageResource.FrameBundle[]... fileName) {
    	super(x, y, null);
    	frameHolder = GeneralUtil.loadImageFile(fileName);
        speed = DEFAULT_SPEED;
        onGround = false;
        canJumpTwice = true;
        isAlive = true;
        isSpook = false;
        deadImage = ImageResource.DUCK_RIGHT;
        deadX = 0;
        deadY = 0;
        widthRatio = 2;
 		heightRatio = 2;
 		newDegree = 30;
 		dashRate = 1.0;
 		blownX = 0;
 		weight = 10;
 		speedRate = 5;
 		jumpSpeed = DEFAULT_JUMP;
 		isDucking = false;
    }

    /* getter, setter */

    /**
	 * ジャンプ時のY方向の速度を返す
	 *
	 * @return ジャンプ時のY方向
	 */
    public double getJumpSpeed() {
    	return this.jumpSpeed;
    }

    /**
	 * ジャンプ時のY方向の速度を設定する
	 *
	 * @param jumpSpeed ジャンプ時のY方向
	 */
    public void setJumpSpeed(double jumpSpeed) {
    	this.jumpSpeed = jumpSpeed;
    }

    /**
	 * ダッシュ時の速度倍率を返す
	 *
	 * @return ダッシュ時の速度倍率
	 */
    public double getDashRate() {
    	return this.dashRate;
    }

    /**
	 * ダッシュ時の速度倍率を設定する
	 *
	 * @param dashRate ダッシュ時の速度倍率
	 */
    public void setDashRate(double dashRate) {
    	this.dashRate = dashRate;
    }

    /**
   	 * プレイヤー死亡時のスプライト画像を返す
   	 *
   	 * @return プレイヤー死亡時のスプライト画像
   	 */
    public String getDeadImage() {
    	return this.deadImage;
    }

    /**
     * <pre>
   	 * プレイヤーが生存しているかどうかを返す
   	 * 敵に接触する前のみ true
   	 * </pre>
   	 *
   	 * @return プレイヤーの生存フラグ
   	 */
    public boolean getIsAlive() {
    	return isAlive;
    }

    /**
     * <pre>
   	 * プレイヤーが幽霊状態かどうかを返す
   	 * 敵に接触し吹き飛ばされた後、画面外に出ると幽霊状態となる
   	 * </pre>
   	 *
   	 * @return プレイヤーの幽霊フラグ
   	 */
    public boolean getIsSpook() {
    	return isSpook;
    }

    /**
   	 * プレイヤーが敵に吹き飛ばされた際のX方向の速度を返す
   	 *
   	 * @return プレイヤーが敵に吹き飛ばされた際のX方向の速度
   	 */
    public double getBlownX() {
    	return blownX;
    }

    /**
   	 * プレイヤーが敵に吹き飛ばされている最中のX座標を返す
   	 *
   	 * @return プレイヤー画像の矩形の左上角のX座標
   	 */
    public double getDeadX() {
    	return deadX;
    }

    /**
   	 * プレイヤーが敵に吹き飛ばされている最中のY座標を返す
   	 *
   	 * @return プレイヤー画像の矩形の左下角のY座標
   	 */
    public double getDeadBaseY() {
    	return deadY;
    }

    /**
   	 * プレイヤーが敵に吹き飛ばされている最中のY座標を返す
   	 *
   	 * @return プレイヤー画像の矩形の左上角のY座標
   	 */
    public double getDeadTopY() {
    	return deadY - getActualHeight();
    }

    /**
   	 * プレイヤーが敵に吹き飛ばされている最中のX座標を設定する
   	 *
   	 * @param x プレイヤー画像の矩形の左上角のX座標
   	 */
    public void setDeadX(double x) {
		deadX = x;
	}

    /**
   	 * プレイヤーが敵に吹き飛ばされている最中のY座標を設定する
   	 *
   	 * @param y プレイヤー画像の矩形の左上角のY座標
   	 */
	public void setDeadY(double y) {
		deadY = y;
	}

	/* 操作メソッド */

	/**
   	 * プレイヤーが吹き飛ばされた後、着地した位置に幽霊として出現させる
   	 *
   	 * @param y 幽霊の出現するY座標
   	 */
    public void respawn(int y) {
    	// 幽霊状態に設定する
    	this.isSpook = true;
    	this.y = y;
    }

    /**
   	 * 幽霊状態のプレーヤーを自動走行させる
   	 *
   	 * @param dt デルタタイム
   	 */
    public void spook(double dt) {
    	vx = GHOST_SPEED * dt;

    	// プレーヤーのX座標によって向きを決定する
    	if(this.x > GameController.getWindow().getWindowWidth() - this.getImageWidth()) {
    		super.directionX = BaseSprite.LEFT;
    	} else if (this.x < this.getImageWidth()) {
    		super.directionX = BaseSprite.RIGHT;
    	}

    	// プレーヤーの向きによって速度を決定する
    	if(super.directionX == BaseSprite.LEFT) {
    		vx = -vx;
    	}

    	// 幽霊の画像に差し替える
		switchLabel(ImageResource.GHOST_RIGHT);
    }

    /**
   	 * ゴールオブジェクトに接触した後、プレーヤーを自動走行させる
   	 */
    public void goal() {
    	// 自動で一定速度での移動を行う
    	dashRate = 1;
    	vx = 0.4;
    }

    /**
   	 * プレイヤーのX方向の移動を停止させる
   	 */
    public void stop() {
    	// X方向の速度を0にする
    	super.vx = 0;
		// 直立イメージに差し替える
    	switchLabel(ImageResource.STAND_RIGHT);
		// しゃがみ状態を解除
    	isDucking = false;
    }

    /**
   	 * プレイヤーをX方向に移動させる
   	 *
   	 * @param direction 移動方向 [-1:左, 1:右]
   	 * @param dt        デルタタイム
   	 */
    public void run(int direction, double dt) {
    	super.vx = this.speed * direction * dt;
    	super.directionX = direction;

    	// しゃがみ状態かどうかによって異なる画像を割り当てる
    	if(!isDucking) {
    		switchLabel(ImageResource.RUN_RIGHT);
    	} else {
    		switchLabel(ImageResource.DUCK_RIGHT);
    	}
    }

    /**
   	 * プレイヤーをY方向に移動させる
   	 */
    public void jump() {
    	// 着地している場合
    	if (onGround) {
            // 上方向に速度を加える
    		vy = -jumpSpeed;
            onGround = false;
            new Thread(new SoundController.PlaySE(SoundResource.SE_JUMP)).start();
            // ジャンプ中の回転のために角度を初期化する
            degree = 0;

        // 空中かつ二段ジャンプ可能な場合
        } else if (canJumpTwice) {
        	// 上方向に速度を加える
        	vy = -jumpSpeed;
            canJumpTwice = false;
            new Thread(new SoundController.PlaySE(SoundResource.SE_JUMP)).start();
            // ジャンプ中の回転のために角度を初期化する
            degree = 0;
        }
    }

    /**
   	 * 敵を踏みつけた時、プレイヤーをY方向に移動させる
   	 */
    public void enemyJump() {
	    vy = -jumpSpeed * 1.1;
	    canJumpTwice = true;
	    onGround = false;
 	}

    /**
   	 * プレイヤーをしゃがみ状態にする
   	 */
    public void duck() {
    	isDucking = true;
		switchLabel(ImageResource.DUCK_STILL);
    }

    /**
   	 * プレイヤーのしゃがみ状態を解除する
   	 */
    public void standup() {
    	isDucking = false;
    }

    /**
   	 * プレイヤーの移動速度にダッシュ倍率をかける
   	 */
    public void dash() {
    	if(isSameLabel(ImageResource.RUN_RIGHT) || isSameLabel(ImageResource.DUCK_RIGHT)) {
    		dashRate = DEFAULT_DASH;
    	}
    }

    /**
   	 * プレイヤーの移動速度を1倍に戻してダッシュを解除する
   	 */
    public void walk() {
    	dashRate = 1.0;
    }

    /**
   	 * プレイヤーを着地したものとみなす
   	 */
    @Override
    public void land() {
    	super.land();
    	// 二段ジャンプを可能にする
    	canJumpTwice = true;
    }

    /**
   	 * プレイヤーの位置からブロックを生成し弾として発射する
   	 *
   	 * @param spriteList 発射するブロックのオブジェクトをセットするスプライトのリスト
   	 */
    public void createNewBlock(List<BaseSprite> spriteList) {
    	new Thread(new SoundController.PlaySE(SoundResource.SE_AH)).start();
    	spriteList.add(
			new VoiceIcon(
				x, getImageBaseY(),
				getDirectionX(),
				ImageResource.VoiceIcon.PATH1.getValue()
				)
			);
    }

    /**
   	 * プレイヤーが敵と衝突した際にプレイヤーを死亡状態へ移行させる
   	 *
   	 * @param vx 衝突時に吹き飛ばされる際のX方向の速度
   	 * @param vy 衝突時に吹き飛ばされる際のY方向の速度
   	 */
    public void die(double vx, double vy) {
    	new Thread(new SoundController.PlaySE(SoundResource.SE_CRASH)).start();

    	// 衝突時の方向によって死亡時の画像の向きを決定する
    	if(directionX == BaseSprite.LEFT) {
    		switchLabel(ImageResource.DUCK_LEFT);
    		deadImage = ImageResource.DUCK_LEFT;
    	} else if(directionX == BaseSprite.RIGHT) {
    		switchLabel(ImageResource.DUCK_RIGHT);
    		deadImage = ImageResource.DUCK_RIGHT;
    	}

    	// 死亡状態に移行
    	isAlive = false;

    	// 吹き飛ばされる速度を設定
    	this.vy = -DEFAULT_JUMP * vy;
    	blownX = vx;

    	// 吹き飛ばされているスプライトの初期座標を衝突位置に設定
    	deadY = y;
    	deadX = x;
    }

    /**
   	 * ジャンプ中のプレーヤーを回転させて、現在の回転角度を返す
   	 *
   	 * @return プレーヤーの回転角度
   	 */
	public int rotateJump() {

		if(directionX == BaseSprite.RIGHT && degree < 360) {
			degree += newDegree;
		} else if(directionX == BaseSprite.LEFT && degree > -360) {
			degree -= newDegree;
    	}

    	return degree;
    }

	/**
   	 * デフォルトのプレーヤーインスタンスを返すファクトリーメソッド
   	 *
   	 * @param x        プレイヤーの初期表示用X座標
	 * @param y        プレイヤーの初期表示用Y座標
	 * @param width    プレイヤー画像を描画する際の幅
	 * @param height   プレイヤー画像の描画する際の高さ
   	 * @return プレーヤーのデフォルトインスタンス
   	 */
    public static Player getDefault(int x, int y, int width, int height) {
    	Player defaultPlayer = new Player(
    			x,
    			y,
    			width,
    			height,
    			ImageResource.Player1RunLeft.values(),
    			ImageResource.Player1RunRight.values(),
    			ImageResource.Player1StandLeft.values(),
    			ImageResource.Player1StandRight.values(),
    			//ImageResource.DebuggerRunRight.values(),
    			//ImageResource.DebuggerStandRight.values(),
    			ImageResource.Player1DuckRight.values(),
    			ImageResource.Player1DuckLeft.values(),
    			ImageResource.Player1DuckStill.values(),
    			ImageResource.Player1GhostLeft.values(),
    			ImageResource.Player1GhostRight.values()
    			);
    	defaultPlayer.switchLabel(ImageResource.RUN_RIGHT);
    	return defaultPlayer;
    }

    /**
     * Animationインターフェースの機能群
     * @see interfaces.Animation
     */

    /**
     * このアセットの持つアニメーション用の画像群を返す
     * @return アニメーションごとのフレーム画像を保持するマップ
     */
    public Map<String, List<BufferedImage>> getFrameHolder() {
    	return this.frameHolder;
    }

    /**
     * アニメーションのフレームを進める速度を設定する
     *
     * @param speedRate アニメーション速度
     * <pre>
     *   設定値1で等倍速、設定値が大きくなるほどアニメーション速度は下がる
     *   0以下を指定した場合は強制的に設定値1に書き換えるため0除算は発生しない
     * </pre>
     */
    public void setSpeedRate(int speedRate) {
		// 0および負数は無効化し、設定値1とする
    	if(speedRate < 0) {
			speedRate = 1;
		}
    	this.speedRate = speedRate;
	}

    /**
     * 変更予定のアニメーションラベル名と現在のラベル名を比較する
 	 *
     * @param label 変更予定のアニメーションラベル名
     */
    public boolean isSameLabel(String label) {
    	if(currentLabel != null) {
    		return this.currentLabel.equals(label);
    	} else {
    		return false;
    	}
    }

    /**
     * アニメーションする画像群をラベル名を指定して切り替える
 	 *
     * @param label アニメーションを行う画像群のラベル名
     */
	public void switchLabel(String label) {
		if(!isSameLabel(label)) {
			// フレームインデックスを1フレーム目にリセットする
			this.subIndex = 0.0;
			// ラベル名をセットする
			this.currentLabel = label;
			// 1フレーム目の画像をセットする
			this.image = this.getCurrentBundle().get(getCurrentFrame());
		}
	}

	/**
     * フレームインデックスを進めて画像をアニメーションする
 	 *
     * @param dt デルタタイム
     */
    public void animate(double dt) {
    	int fixRate = 50;

    	// [速度定数]/[指定されたアニメーション速度]をベースとして、ダッシュ倍率をかけたものをアニメーションの速度とする
    	subIndex = (subIndex + (fixRate / speedRate) * dashRate * dt) % getCurrentBundle().size();

    	// subIndexの整数部分を現在のアニメーションインデックスとして参照する
    	image = getCurrentBundle().get(getCurrentFrame());
    }

    /**
     * フレームインデックスを進めて画像をアニメーションする
 	 *
     * @return 現在使用しているアニメーション用の画像リスト
     */
    public List<BufferedImage> getCurrentBundle() {
    	return frameHolder.get(currentLabel);
    }

    /**
     * 現在表示されているアニメーション用の画像リスト内のインデックスを返す
 	 *
     * @return 現在のアニメーションインデックス
     */
    public int getCurrentFrame() {
    	return (int) subIndex;
    }
}