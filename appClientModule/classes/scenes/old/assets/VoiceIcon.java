package classes.scenes.old.assets;

import classes.utils.GeneralUtil;

/**
 * アクションステージ用の音声弾アセットを定義するクラス
 *
 * @author Naoki Yoshikawa
 */
public class VoiceIcon extends BaseSprite {

	/* 定数群 */
	// 射程距離
	public static double RANGE = 100;
	// 玉の速度
	public final static double SPEED = 5.0;
	// 玉の消滅までの時間の規定値
	private final int LIFE = 100;

	// 射出時点での描画位置のX座標
	private double firstX;

	// 射出されてからの経過時間
	private double life;

	/**
	 * 描画位置の座標、描画サイズ、向き、画像の設定
	 *
	 * @param x         描画位置のX座標
	 * @param y         描画位置のY座標
	 * @param direction X軸の進行方向
	 * @param path      画像ファイルのパス
	 */
	public VoiceIcon(double x, double y, int direction, String path) {
		super(x, y, GeneralUtil.readImage(path));
		vx = SPEED;
        if(direction == 0) {
        	vx = -vx;
        }
        firstX = x;
        life = LIFE;
    }

	/**
	 * <pre>
	 * 現在の弾の拡大倍率を取得する
	 * 弾は遠くに進むほど大きくなる
	 * </pre>
	 *
	 * @return 弾の拡大倍率
	 */
    public double getSizeRatio() {
    	double ratio = x - firstX;
    	if(ratio < 0) {
    		ratio = -ratio;
    	}
    	return  ratio / RANGE;
    }

    /**
	 * 弾の残り出現可能時間を返す
	 *
	 * @return 弾の残り出現時間
	 */
    public double getLife() {
    	return this.life;
    }

    /**
	 * 弾の射出された位置のX座標を返す
	 *
	 * @return 弾の射出位置のX座標
	 */
	public double getFirstX() {
		return firstX;
	}

    /**
	 * 弾の出現可能時間を減産する
	 *
	 * @param dt デルタタイム
	 */
    public void decrease(double dt) {
    	this.life -= SPEED * dt;
    }
}
