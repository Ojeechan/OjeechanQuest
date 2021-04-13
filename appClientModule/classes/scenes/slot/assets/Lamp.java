package classes.scenes.slot.assets;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import classes.utils.GeneralUtil;

/**
 * スロットモジュールのうち、ランプを定義するクラス
 *
 * @author Naoki Yoshikawa
 */
public class Lamp {

    // ランプの画像オブジェクト
    private BufferedImage img;

    // 描画位置の座標
    private int x;
    private int y;

    // 発光時の明るさの最大値
    private float[] luminance;

    // 発光パターンの配列
    private FlashPattern[] pattern;

    // 現在の発光パターンを示すインデックス
    private int flashMode;

    // どの小役が入賞した時に対応するか
    private int[] flagType;

    // アニメーション用のタイムパラメータ
    private double time;

    private Init init;

    /**
     * 画像オブジェクト、描画位置の座標、発光量の最大値、発光パターン、入賞対応役の設定
     *
     * @param path      ボタンに設定する画像ファイルのパス
     * @param x         描画位置のX座標
     * @param y         描画位置のY座標
     * @param luminance 発光量の最大値
     * @param fp        発光パターンの配列
     * @param mode      現在の発光パターン
     * @param flagType  対応して点灯する子役フラグ
     */
    public Lamp(
            String path,
            int x, int y,
            float[] luminance,
            FlashPattern[] fp, int mode,
            int[] flagType) {
        this.img = GeneralUtil.readImage(path);
        this.x = x;
        this.y = y;
        this.luminance = luminance;
        this.pattern = fp;
        this.flagType = flagType;
        init = new Init(mode);
    }

    private class Init {
        private int flashMode;
        private Init(int flashMode) {
            this.flashMode = flashMode;
        }
    }

    /**
     * ランプに設定された画像オブジェクトを返す
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
     * 現在の発光モードを返す
     *
     * @return 発光モード
     */
    public int getMode() {
        return this.flashMode;
    }

    /**
     * 発光パターンを設定する
     *
     * @param mode 移行先の発光パターン
     */
    public void setFlashMode(int mode) {
        this.flashMode = mode;
    }

    /**
     * <pre>
     * 成立フラグがランプの対応役かどうかを判断する
     * 対応役ならtrue
     * </pre>
     *
     * @param flag 成立フラグの種類
     *
     * @return 対応役かどうか
     */
    public boolean isResponsible(int flag) {
        for(int flagType: flagType) {
            if(flagType == flag) {
                return true;
            }
        }
        return false;
    }

    /**
     * 明度変更用に設定されたのRescaleOpオブジェクトを返す
     *
     * @return 明度変更用RescaleOpオブジェクト
     */
    public RescaleOp getLuminance() {
        float[] modified = new float[this.luminance.length];
        for(int i = 0; i < modified.length; i++) {
            modified[i] = this.luminance[i] * (float) this.pattern[this.flashMode].getPattern(this.time);
        }

        return new RescaleOp(modified, new float[4], null);
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
        this.time = (time + dt) % (Math.PI * 2.0);
        //this.time = (time + dt);
    }

    /**
     * タイムパラメータを初期化する
     */
    public void resetTime() {
        this.time = 0;
    }


    /**
     * 各パラメータを初期化する
     */
    public void initParam() {
        this.flashMode = init.flashMode;
        this.time = 0.0;
    }
}
