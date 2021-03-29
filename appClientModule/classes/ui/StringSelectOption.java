package classes.ui;

import java.awt.image.BufferedImage;
import java.util.List;

import classes.controllers.FontController.Fonts;
import classes.utils.GeneralUtil;

/***
 * 文字列型の選択リストを生成するUIオブジェクト
 *
 * @author Naoki Yoshikawa
 */
public class StringSelectOption {

	// 選択肢に表示する文字列
	private String label;

	// 選択肢の描画位置の座標
	private int x;
	private int y;

	// フォントの縦横サイズ
	private int size;

	// 選択肢の文字列に使用するフォント画像のリスト
	protected List<BufferedImage> fontList;

	// アニメーション用のタイムパラメータ
	private double time;

	/**
	 * 描画位置の座標、フォント、表示する文字列を設定
	 *
	 * @param x     選択肢の描画位置のX座標
	 * @param y     選択肢の描画位置のY座標
	 * @param fonts フォントオブジェクト
	 * @param label 選択肢の文字列
	 * @param size  フォントの縦横サイズ(px)
	 */
	public StringSelectOption(int x, int y, Fonts fonts, String label, int size) {
		this.label = label;
		this.x = x;
		this.y = y;
		this.size = size;
		this.fontList = GeneralUtil.changeStringToImage(fonts, label);
		this.time = 0.0;
	}

	/**
	 * 選択肢に表示する文字列を返す
	 *
	 * @return 選択肢の文字列
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * フォントの縦横の描画サイズをpxで返す
	 *
	 * @return フォントの描画サイズ
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * 選択肢の文字列に使用するフォント画像のリストを返す
	 *
	 * @return フォント画像のリスト
	 */
	public List<BufferedImage> getImageFont() {
		return fontList;
	}

	/**
	 * 選択肢を囲む矩形の左上のX座標を返す
	 *
	 * @return 選択肢の左上のX座標
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * 選択肢を囲む矩形の左上のY座標を返す
	 *
	 * @return 選択肢の左上のY座標
	 */
	public int getY() {
		return this.y;
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
		this.time = 0;
	}
}
