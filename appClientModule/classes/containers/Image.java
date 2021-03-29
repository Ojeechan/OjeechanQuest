package classes.containers;

import java.awt.image.BufferedImage;

/**
 * 画像のプロパティを集約するラッパークラス
 *
 * @author  Naoki Yoshikawa
 */
public class Image {

	// 画像オブジェクト
	private BufferedImage img;

	// 画像の描画座標
	private int x;
	private int y;

	private double time;

	private Init init;

	/**
	 * 画像矩形のプロパティを設定する
	 *
	 * @param x   イメージ矩形左上の描画先X座標
	 * @param y   イメージ矩形左上の描画先Y座標
	 * @param img 画像オブジェクト
	 */
	public Image(int x, int y, BufferedImage img) {
		init = new Init(x, y);
		this.img = img;
		initParam();
	}

	private class Init {
		private int x;
		private int y;
		private Init(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * 画像オブジェクトを返す
	 *
	 * @return 画像オブジェクト
	 */
	public BufferedImage getImage() {
		return img;
	}

	/**
	 * 画像矩形の左上のX座標を設定する
	 *
	 * @param x 画像矩形の左上のX座標
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * 画像矩形の左上のY座標を設定する
	 *
	 * @param y 画像矩形の左上のY座標
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * 画像矩形の左上のX座標を返す
	 *
	 * @return x 画像矩形の左上のX座標
	 */
	public int getX() {
		return x;
	}

	/**
	 * 画像矩形の左上のY座標を返す
	 *
	 * @return y 画像矩形の左上のY座標
	 */
	public int getY() {
		return y;
	}

	/**
	 * アニメーション用のタイムパラメータを返す
	 *
	 * @return タイムパラメータ
	 */
	public double getTime() {
		return this.time;
	}

	/**
	 * タイムパラメータを加算する
	 *
	 * @param dt 加算値
	 */
	public void proceed(double dt) {
		this.time = (time + dt) % (2 * Math.PI);
	}

	/**
	 * タイムパラメータを初期化する
	 */
	public void resetTime() {
		this.time = 0.0;
	}

	/**
	 * パラメータを初期化する
	 */
	public void initParam() {
		this.x = init.x;
		this.y = init.y;
		this.time = 0.0;
	}
}
