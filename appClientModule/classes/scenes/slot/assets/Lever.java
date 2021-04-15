package classes.scenes.slot.assets;

import java.awt.image.BufferedImage;

import classes.utils.GeneralUtil;

/**
 * スロットモジュールのうち、レバーを定義するクラス
 *
 * @author Naoki Yoshikawa
 */
public class Lever {

    // ランプの画像オブジェクト
    private BufferedImage imgUp;
    private BufferedImage imgDown;

    // 描画位置の座標
    private int x;
    private int y;

    private boolean isDown;
    private double time;
    private int limit;

    /**
     * 画像オブジェクト、描画位置の座標の設定
     *
     * @param pathUp    レバーの非押下時用に設定する画像ファイルのパス
     * @param pathDown  レバーの押下時用に設定する画像ファイルのパス
     * @param x         描画位置のX座標
     * @param y         描画位置のY座標
     */
    public Lever(String pathUp, String pathDown, int x, int y) {
        this.imgUp = GeneralUtil.readImage(pathUp);
        this.imgDown = GeneralUtil.readImage(pathDown);
        this.x = x;
        this.y = y;
        initParam();
    }

    /**
     * レバーに設定された通常時の画像オブジェクトを返す
     *
     * @return 画像オブジェクト
     */
    public BufferedImage getImageUp() {
        return this.imgUp;
    }

    /**
     * レバーに設定されたレバーオン時の画像オブジェクトを返す
     *
     * @return 画像オブジェクト
     */
    public BufferedImage getImageDown() {
        return this.imgDown;
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
     * レバーが下がっているかどうか
     * 下がっていれば true
     * </pre>
     *
     * @return レバーが下がっているかどうか
     */
    public boolean isDown() {
        return this.isDown;
    }

    /**
     * レバーを下げる
     */
    public void down() {
        this.isDown = true;
    }

    /**
     * タイムパラメータを加算する
     *
     * @param dt タイムパラメータ
     */
    public void proceed(double dt) {
        this.time += dt * 100;
        if(this.time > this.limit) {
            this.isDown = false;
            time = 0;
        }
    }

    /**
     * 各パラメータを初期化する
     */
    public void initParam() {
        this.isDown = false;
        this.time = 0;
        this.limit = 10;
    }
}
