package classes.scenes.slot.assets;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.Map;

import classes.constants.SoundResource;
import classes.controllers.SoundController;
import classes.utils.GeneralUtil;

/**
 * スロットモジュールのうち、リールを定義するクラス
 *
 * @author Naoki Yoshikawa
 */
public class Reel {

    /* 定数群 */
    // 1リールあたりの図柄数
    private final int ICON_NUM = 20;
    // リールの回転速度
    private final double SPEED = 700.0;

    // リール配列を示す数値の配列　図柄を数値に対応させて判断する
    private int[] reelOrder;

    // リールに設定された画像オブジェクト
    private BufferedImage img;
    private BufferedImage imgBright;

    // 図柄1つあたりのサイズ(ピクセル)
    private int width;
    private int height;

    // 上段図柄の矩形左上角を計測位置とした現在のリールの停止位置
    private double reelPos;

    // 停止目標位置までの距離
    private double distance;

    // リールが回転中かどうか
    private boolean isSpinning;

    // リール図柄が発光中かどうか
    private boolean isFlashing;

    // 発光時の明るさの最大値
    private float[] luminance;

    /**
     * <pre>
     * リール配列、図柄のサイズ、図柄の画像の設定
     * 各パラメータの初期化
     * </pre>
     *
     * @param paths     各図柄に使用する画像ファイルのパスの配列
     * @param reelOrder リール配列
     * @param width     図柄の描画時のベースとなる幅
     * @param height    図柄の描画時のベースとなる高さ
     * @param luminance 発光時の明るさ
     */
    public Reel(Map<Integer, String> paths, int[] reelOrder, int width, int height, float[] luminance) {
        this.reelOrder = reelOrder;
        this.width = width;
        this.height = height;
        this.img = GeneralUtil.concatIconsVerical(intToIcon(paths, reelOrder), this.width, this.height);
        this.imgBright = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        this.luminance = luminance;
        getLuminance().filter(img, imgBright);
        initParam();
    }

    /**
     * <pre>
     * リール配列を示すint型配列を返す
     * 数値は各図柄のIDを示す
     * </pre>
     *
     * @return リール配列
     */
    public int[] getReelOrder() {
        return this.reelOrder;
    }

    /**
     * 個別の図柄画像を結合して生成された、リール全体の画像オブジェクトを返す
     *
     * @return リールの画像オブジェクト
     */
    public BufferedImage getImage() {
        return this.img;
    }

    /**
     * リール全体の明度をあげた発光時用の画像オブジェクトを返す
     *
     * @return リールの発光時用画像オブジェクト
     */
    public BufferedImage getImageBright() {
        return this.imgBright;
    }

    /**
     * 枠外含む描画範囲(全5コマ枠)の上辺のY座標を返す
     *
     * @return 5コマ枠矩形の上辺のY座標
     */
    public double getReelTop() {
        return this.reelPos;
    }

    /**
     * 枠外含む描画範囲(全5コマ枠)の下辺のY座標を返す
     *
     * @return 5コマ枠矩形の下辺のY座標
     */
    public double getReelBottom() {
        return this.reelPos + getReelSize();
    }

    /**
     * リールの枠外含む描画範囲(全5コマ枠)の矩形の高さを返す
     *
     * @return 5コマ枠矩形の高さ
     */
    public int getReelSize() {
        // 描画範囲は5コマ
        return this.height * 5;
    }

    /**
     * 図柄の幅を返す(px)
     *
     * @return 図柄の幅
     */
    public int getIconWidth() {
        return this.width;
    }

    /**
     * 図柄の高さを返す(px)
     *
     * @return 図柄の幅
     */
    public int getIconHeight() {
        return this.height;
    }

    /**
     * <pre>
     * リールが回転中かどうかを返す
     * 回転中の場合true
     * </pre>
     *
     * @return リールが回転中かどうか
     */
    public boolean getIsSpinning() {
        return this.isSpinning;
    }

    /**
     * <pre>
     * リールの図柄が発光中かどうかを返す
     * 発光中の場合true
     * </pre>
     *
     * @return 図柄が発光中かどうか
     */
    public boolean getIsFlashing() {
        return this.isFlashing;
    }

    /**
     * 停止位置までの残り距離を返す
     *
     * @return 停止位置までの距離
     */
    public double getDistance() {
        return this.distance;
    }

    /**
     * 停止位置までの残り距離を設定する
     *
     * @param distance 停止位置までの距離
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * リール位置を設定する
     *
     * @param pos 上段の図柄矩形左上を0.0とするY座標
     */
    public void setReelPos(double pos) {
        this.reelPos = pos;
    }

    /**
     * 明度変更用に設定されたのRescaleOpオブジェクトを返す
     *
     * @return 明度変更用RescaleOpオブジェクト
     */
    public RescaleOp getLuminance () {
        return new RescaleOp(luminance, new float[4], null);
    }

    /**
     * 指定された停止位置がリール画像内の座標のどこに対応しているかを返す
     *
     * @param pos 上中下段のいずれか (0=上段, 1=中段, 2=下段)
     * @return 指定位置に該当するリール画像内のY座標
     */
    public double getStopPos(int pos) {
        return (reelPos + this.height * (1 + pos)) % this.img.getHeight();
    }

    /**
     * 指定された停止位置に現在停止している図柄の配列インデックスを返す
     *
     * @param pos 上中下段のいずれか (0=上段, 1=中段, 2=下段)
     * @return 指定位置に停止している図柄の配列内でのインデックス
     */
    public int getCurrentIndex(int pos) {
        return (int) (getStopPos(pos)  / getIconHeight());
    }

    /**
     * <pre>
     * 指定された停止位置に現在停止している図柄の配列インデックスを返す
     * 停止位置の判断座標をadjustで指定された分だけずらすことで、
     * 上中下段の境界で判断することを防ぐ
     * </pre>
     *
     * @param pos    上中下段のいずれか (0=上段, 1=中段, 2=下段)
     * @param adjust 停止位置の判断座標を指定分Y方向にずらす
     *
     * @return 指定位置に停止している図柄の配列内でのインデックス
     */
    public int getAdjustedIndex(int pos, int adjust) {
        return (int) ((reelPos + this.height * (1 + pos) + adjust) % this.img.getHeight() / getIconHeight());
    }

    /**
     * int型の配列で与えられたリール配列を、図柄の画像に変換する
     *
     * @param paths    図柄の画像ファイルのパスの配列
     * @param intArray リール配列
     *
     * @return 図柄の画像によるリール配列
     */
    private BufferedImage[] intToIcon(Map<Integer, String> paths, int[] intArray) {
        // 一度読み込んだ図柄は対応インデックスにキャッシュしておく
        BufferedImage[] cache = new BufferedImage[ICON_NUM];
        // 画像に変換して配列に格納する
        BufferedImage[] imageArray = new BufferedImage[intArray.length];
        for(int i = 0; i < intArray.length; i++) {
            BufferedImage b = cache[intArray[i]];
            if(b != null) {
                imageArray[i] = b;
            } else {
                imageArray[i] = GeneralUtil.readImage(paths.get(intArray[i]));
            }
        }

        return imageArray;
    }

    /**
     * リールを回転させる
     */
    public void start() {
        this.isSpinning = true;
    }

    /**
     * リールを指定スピード分回転させる
     *
     * @param dt デルタタイム
     */
    public void spin(double dt) {
        this.reelPos -= SPEED * dt;
        this.reelPos %= this.img.getHeight();
        if (this.reelPos < 0) {
            // イメージの切れ目を繋いでループさせる
            this.reelPos = this.img.getHeight() + this.reelPos;
        }
    }

    /**
     * 停止ボタンが押されてから停止するまでの滑りを演算する
     *
     * @param dt デルタタイム
     */
    public void adjustStop(double dt) {
        if(this.distance > 0) {
            this.distance -= SPEED * dt;
            // リールのズレを、差分を足して調整
            if(this.distance < 0) {
                this.reelPos -= this.distance;
                new Thread(new SoundController.PlaySE(SoundResource.SE_REEL)).start();
            }
            spin(dt);
        } else {
            this.distance = 0.0;
        }
    }

    /**
     * <pre>
     * 停止フラグを立てて滑り状態へ移行する
     * 滑り状態へ移行するため実際はこのメソッド呼び出し後ももう少し回る
     * </pre>
     */
    public void stop() {
        this.isSpinning = false;
    }

    /**
     * 役の入賞時、成立ラインの図柄を光らせる
     */
    public void flash() {
        this.isFlashing = true;
    }

    /**
     * 図柄を消灯させる
     */
    public void off() {
        this.isFlashing = false;
    }

    /**
     * 各パラメータを初期化する
     */
    public void initParam() {
        this.reelPos = 0.0;
        this.distance = 0.0;
        this.isSpinning = false;
        this.isFlashing = false;
    }
}
