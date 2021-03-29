package classes.scenes.action.assets;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import classes.constants.ImageResource;
import classes.math.Vector2;
import classes.scenes.action.assets.BaseSprite;
import classes.utils.GeneralUtil;

import interfaces.Animation;

public class Player extends BaseSprite implements Animation {

	/* 定数群 */
	public static final int NORMAL = 0; // プレイヤーの状態:通常時
	public static final int HIT = 1;    // プレイヤーの状態:車と衝突
	public static final int SPOOK = 2;  // プレイヤーの状態:幽霊
	private final int SPEED = 30;       // プレイヤーの移動速度

	/**
     * Animationインターフェース用のメンバー変数
     */
    protected Map<String, List<BufferedImage>> frameHolder;
	protected String currentLabel;
	protected double subIndex;
	protected double speedRate; // 指定値nを取り、アニメーション速度を1/n倍にする

	// カメラビュー上の垂直方向を表すZ成分と加速度
	private double z;
	private double vz;



	// プレイヤーの2Dビュー上での角度
	private double angle;

	// プレイヤーの状態[NORMAL, HIT, SPOOK]
	private int status;

	// プレイヤーのカメラビュー上での回転角
	private double degree;

	// 初期値を保持するインナークラスインスタンス
	private Init init;

	/**
	 * プレイヤーの初期位置の座標、画像サイズ、画像ファイルのパスを設定する
	 *
	 * @param v        2Dビュー上の位置ベクトル
	 * @param x        カメラビュー上の描画位置のX座標
	 * @param y        カメラビュー上の描画位置のY座標
	 * @param fileName プレイヤーのアニメーション用画像のファイルパス配列
	 */
	public Player(
			Vector2 v,
			double x,
			double y,
			ImageResource.FrameBundle[]... fileName
			) {

		// 画像はアニメーション時にセットされる
		super(x, y, null);

		frameHolder = GeneralUtil.loadImageFile(fileName);
        widthRatio = 3;
 		heightRatio = 3;
 		speedRate = 4;

 		init = new Init(v, x, y);
	}

	/**
	 * initParam()に使用する初期値を保持するインナークラス
	 */
	private class Init {
		// 2Dビュー上の位置ベクトル
		private Vector2 pos;
		// カメラビュー上のXY座標
		private double x;
		private double y;
		private Init(Vector2 pos, double x, double y) {
			this.pos = pos;
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * パラメータを初期化する
	 */
	public void initParam() {
		switchLabel(ImageResource.RUN_RIGHT);
		setImageLeftX(init.x);
		setImageTopY(init.y);
		super.pos = init.pos;
		super.dir = new Vector2(0, 0);
    	angle = 0;
    	degree = 0;
    	status = NORMAL;
 		z = 0;
	}



	/**
	 * 画面上のプレイヤーの向きをX軸からの回転角度で表して返す
	 *
	 * @return プレイヤーの向きを表す角度
	 */
	public double getAngle() {
		return this.angle;
	}

	/**
	 * 画面上のプレイヤーの向きを左右のいずれかで表して返す
	 *
	 * @return プレイヤーの左右を表す数値 -1:左, 1:右, 0:左右なし(上下)
	 */
	public int getDirection() {
		if(getAngle() > -Math.PI/2 && getAngle() < Math.PI/2) {
			return 1;
		} else if(getAngle() > Math.PI/2 && getAngle() < Math.PI/2 * 3 || getAngle() < -Math.PI/2) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * プレイヤーのカメラビュー上での高さをZ成分として設定する
	 *
	 * @param z カメラビュー上の高さの座標
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * 1フレームでのプレイヤーの高さの移動成分を設定する
	 *
	 * @param z 高さの移動成分
	 */
	public void setVZ(double z) {
		this.vz = z;
	}

	/**
	 * プレイヤーの現在の高さをZ成分として返す
	 *
	 * @return プレイヤーの現在の高さの座標
	 */
	public double getZ() {
		return this.z;
	}

	/**
	 * プレイヤーの現在の高さをZ成分として返す
	 *
	 * @return プレイヤーの現在の高さの座標
	 */
	public double getVZ() {
		return this.vz;
	}

	/**
	 * プレイヤーの現在の高さをZ成分として返す
	 *
	 * @return プレイヤーの現在の高さの座標
	 */
	public double getDegreeNew() {
		return this.degree;
	}

	/**
	 * 車に跳ねられた状態へ移行する
	 */
	public void hit() {
		status = HIT;
		switchLabel(ImageResource.DUCK_RIGHT);
		setVZ(-20);
	}

	/**
	 * 幽霊状態へ移行する
	 */
	public void die() {
		status = SPOOK;
		this.degree = 0;
		switchLabel(ImageResource.GHOST_RIGHT);
	}

	/**
	 * <pre>
	 * プレイヤーの状態を返す
	 * NORMAL:通常
	 * HIT: 車に跳ねられた後
	 * SPOOK: 幽霊
	 * </pre>
	 *
	 * @return プレイヤーの状態
	 */
	public int getStatus() {
		return this.status;
	}

	/**
	 * プレイヤーに仮計算の移動ベクトルを与える
	 *
	 * @param dir 該当フレームでの移動ベクトル
	 * @param dt  デルタタイム
	 */
	public void move(Vector2 dir, double dt) {
		double theta = Math.atan2(dir.getY(), dir.getX());
		super.dir = super.dir.add(new Vector2(Math.cos(theta), Math.sin(theta)).scalar(dt * SPEED));
	}

	/**
	 * 仮計算の移動ベクトルを位置ベクトルに加算する
	 *
	 * @param dt  デルタタイム
	 */
	public void proceed(double dt) {
		int direction = getDirection();
		if(direction == 0) {
			direction = 1;
		}
		if(status == HIT) {
			super.pos = super.pos.add(new Vector2(dt * direction * SPEED * 4, 0));
			degree = (degree + Math.PI/6) % (2 * Math.PI);
			animate(dt);
		} else if(status == SPOOK) {
			super.pos = super.pos.add(new Vector2(dt * direction, 0));
		} else if(super.dir.mag() == 0) {
			subIndex = 0;
		} else {
			super.pos = super.pos.add(super.dir);
			stop();
			animate(dt);
		}
	}

	/**
	 * <pre>
	 * フレームごとの進行方向の仮ベクトルを参照し、プレイヤーの角度を設定する
	 * 角度はクオータービュー上の角度を表す
	 * </pre>
	 */
	public void setAngle() {
		if(super.dir.mag() != 0) {
			this.angle = Math.atan2(super.dir.getY(), super.dir.getX());
		}
	}

	/**
	 * 移動成分を0にする
	 */
	public void stop() {
		super.dir = new Vector2(0.0, 0.0);
	}

	/**
	 * カメラビュー上のZ軸方向に成分を与える
	 */
	public void jump() {
		this.vz = -15;
	}

	/**
   	 * デフォルトのプレーヤーインスタンスを返すファクトリーメソッド
   	 *
   	 * @param v      プレイヤーの2Dビュー上の初期位置ベクトル
   	 * @param x      プレイヤーのカメラビュー上の初期描画位置のX座標
	 * @param y      プレイヤーのカメラビュー上の初期描画位置のX座標
   	 * @return プレイヤーのデフォルトインスタンス
   	 */
    public static Player getDefault(Vector2 v, double x, double y) {
    	Player defaultPlayer = new Player(
    			v,
    			x,
    			y,
    			ImageResource.Player1RunRight.values(),
    			ImageResource.Player1VinylRight.values(),
    			ImageResource.Player1StandRight.values(),
    			ImageResource.PlayerFront.values(),
    			ImageResource.PlayerBack.values(),
    			ImageResource.PlayerHalfdown.values(),
    			ImageResource.PlayerHalfup.values(),
    			ImageResource.Player1GhostRight.values(),
    			ImageResource.Player1DuckRight.values()
    			);
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
    	subIndex = (subIndex + (fixRate / speedRate) * dt) % getCurrentBundle().size();

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
