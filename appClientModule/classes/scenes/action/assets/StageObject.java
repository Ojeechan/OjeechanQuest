package classes.scenes.action.assets;

import java.awt.image.BufferedImage;

import classes.math.Vector2;
import classes.scenes.action.assets.BaseSprite;

/**
 * アクションステージのステージオブジェクトを定義するクラス
 *
 * @author Naoki Yoshikawa
 */
public class StageObject extends BaseSprite {

	// プレイヤーとのY軸方向の距離
	private double distance;

	// 反転して描画するかどうか
	private boolean isReflected;

	// オブジェクトの種類を表すインデックス
	private int type;

	// 初期値を保持するインナークラス
	private Init init;

	/**
	 * 各パラメータの設定
	 *
	 * @param v           2Dビュー上の位置ベクトル
	 * @param img         画像オブジェクト
	 * @param isReflected 反転して描画するかどうか
	 * @param type　　　　　オブジェクトの種類を表すインデックス
	 */
	public StageObject(Vector2 v, BufferedImage img, boolean isReflected, int type) {
		super(v.getX(), v.getY(), img);
		init = new Init(v);
		widthRatio = 1;
		heightRatio = 1;
		this.isReflected = isReflected;
		this.type = type;
	}

	/**
	 * 初期値を保持するインナークラス
	 */
	private class Init {
		private Vector2 pos;
		private Init(Vector2 pos) {
			this.pos = pos;
		}
	}

	/**
	 * パラメータを初期化する
	 */
	public void initParam() {
		super.pos = init.pos.copy();
		super.dir = new Vector2(0, 0);
		this.distance = 0;
	}

	/**
	 * プレイヤーとの距離を設定する
	 *
	 * @param distance プレイヤーとの距離
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

	/**
	 * プレイヤーとの距離を返す
	 *
	 * @return プレイヤーとの距離
	 */
	public double getDistance() {
		return this.distance;
	}

	/**
	 * <pre>
	 * ステージオブジェクトを反転して描画するかどうかを返す
	 * 反転する場合 true
	 * </pre>
	 *
	 * @return 反転して描画するかどうか
	 */
	public boolean getIsReflected() {
		return this.isReflected;
	}

	/**
	 * オブジェクトの種類を表すインデックスを数値で返す
	 *
	 * @return オブジェクトの種類を表すインデックス
	 */
	public int getType() {
		return this.type;
	}
}
