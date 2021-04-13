package classes.scenes.slot.assets;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import classes.utils.GeneralUtil;

/**
 * スロットモジュールのうち、ボタンを定義するクラス
 *
 * @author Naoki Yoshikawa
 */
public class Button {

    // ボタンの画像オブジェクト
    private BufferedImage img;

    // 描画位置の座標
    private int x;
    private int y;

    // ランプがついているかどうか
    private boolean isOn;

    // 発光用の明度変更オブジェクト
    private RescaleOp rescaleOp;

    /**
     * 画像のファイルパス、描画位置の座標、発光パターンの設定
     *
     * @param path      画像のファイルパス
     * @param x         描画位置のX座標
     * @param y         描画位置のY座標
     * @param luminance 発光時の明るさの最大値を示す設定値
     */
    public Button(String path, int x, int y, float[] luminance) {
        this.img = GeneralUtil.readImage(path);
        this.x = x;
        this.y = y;
        float[] offsets = new float[4];
        this.rescaleOp = new RescaleOp(luminance, offsets, null);
        initParam();
    }

    /**
     * ボタンに設定された画像オブジェクトを返す
     *
     * @return 画像オブジェクト
     */
    public BufferedImage getImage() {
        return this.img;
    }

    /**
     * 描画位置のX座標を返す
     *
     * @return X座標
     */
    public int getX() {
        return this.x;
    }

    /**
     * 描画位置のY座標を返す
     *
     * @return Y座標
     */
    public int getY() {
        return this.y;
    }

    /**
     * <pre>
     * ボタンが点灯しているかどうかを返す
     * 点灯している場合 true
     * </pre>
     *
     * @return ボタンが点灯しているかどうか
     */
    public boolean getIsOn() {
        return this.isOn;
    }

    /**
     * 明度変更用に設定されたのRescaleOpオブジェクトを返す
     *
     * @return 明度変更用RescaleOpオブジェクト
     */
    public RescaleOp getLuminance () {
        return this.rescaleOp;
    }

    /**
     * ボタンを点灯させる
     */
    public void on() {
        this.isOn = true;
    }

    /**
     * ボタンを消灯させる
     */
    public void off() {
        this.isOn = false;
    }

    /**
     * 各パラメータを初期化する
     */
    public void initParam() {
        this.isOn = false;
    }
}
