package classes.scenes.old.assets;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import classes.utils.GeneralUtil;

/**
 * 全てのスプライトが共通して使用する座標、画像の矩形情報を扱う基底クラス
 *
 * @author  Naoki Yoshikawa
 */
public class BaseSprite {

    // スプライトの向き
    public static final int LEFT = -1;
    public static final int RIGHT = 1;

    // スプライト画像のうち透明部分を覗く実体の高さ(上下)と幅(左右)の座標
    protected int transparencyTop;
    protected int transparencyBottom;
    protected int transparencyRight;
    protected int transparencyLeft;

    // キャラクターサイズ倍率
    protected double widthRatio;
    protected double heightRatio;

    // スプライト矩形の左上角の座標 いずれベクトルにする？
    protected double x;
    protected double y;

    // スプライトの画像
    protected BufferedImage image;

    // 加速値
    protected double vx;
    protected double vy;

    // スピード値
    protected double speed;

    // 着地しているか
    protected boolean onGround;

    // 現在の回転角度
    protected int degree;
    // 加算される回転角度
    protected int newDegree;

    // スプライトの重量
    protected double weight;

    // 加速度が無い時用の、最後のキャラクターの向き 1:右, -1:左
    protected int directionX;

    /**
     * スプライトに必要なパラメータを初期化
     *
     * @param x        プレイヤーの初期表示用X座標
     * @param y        プレイヤーの初期表示用Y座標
     * @param width    プレイヤー画像を描画する際の幅
     * @param height   プレイヤー画像の描画する際の高さ
     * @param image アニメーションに使用する画像のファイルパス
     */
    protected BaseSprite(double x, double y, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.vx = 0.0;
        this.vy = 0.0;
        this.speed = 0.0;
        this.degree = 0;
        this.newDegree = 45;
        this.weight = 0;
        this.directionX = 1;
    }

    /* getter, setter */

    /**
     * スプライトの画像データをBufferedImageオブジェクトで返す
     *
     * @return スプライトの画像データ
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * スプライト画像の幅を拡大倍率を含めた実際の描画サイズで返す
     *
     * @return スプライト画像の描画時の幅
     */
    public int getImageWidth() {
        return (int)(image.getWidth() * widthRatio);
    }

    /**
        * スプライト画像の高さを拡大倍率を含めた実際の描画サイズで返す
        *
        * @return スプライト画像の描画時の高さ
        */
    public int getImageHeight() {
        return (int)(image.getHeight() * heightRatio);
    }

    /**
        * スプライトの画像データをBufferedImageオブジェクトで設定する
        *
        * @param image スプライトの画像データ
        */
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     *
        * スプライトの画像矩形の上辺のY座標から、透明ピクセルを除いて最も上にある不透明ピクセルの
        * Y座標までの差分をピクセル数で返す
        *
        *
        * @return スプライト矩形の上辺から実体ピクセルまでの距離
        */
    public int getTransparencyTop() {
        return transparencyTop;
    }

    /**
        * スプライトの画像矩形の底辺のY座標から、透明ピクセルを除いて最も下にある不透明ピクセルの
        * Y座標までの差分をピクセル数で返す
        *
        * @return スプライト矩形の底辺から実体ピクセルまでの距離
        */
    public int getTransparencyBottom() {
        return transparencyBottom;
    }

    /**
        * スプライトの画像矩形の左辺のX座標から、透明ピクセルを除いて最も左にある不透明ピクセルの
        * 座標までの差分をピクセル数で返す
        *
        * @return スプライト矩形の左辺から実体ピクセルまでの距離
        */
    public int getTransparencyLeft() {
        return transparencyLeft;
    }

    /**
        * スプライトの画像矩形の右辺のX座標から、透明ピクセルを除いて最も右にある不透明ピクセルの
        * 座標までの差分をピクセル数で返す
        *
        * @return スプライト矩形の右辺から実体ピクセルまでの距離
        */
    public int getTransparencyRight() {
        return transparencyRight;
    }

    /**
        * スプライトの画像矩形の上辺のY座標から、透明ピクセルを除いて最も上にある不透明ピクセルの
        * Y座標までの差分をピクセル数で設定する
        *
        * @param transparencyTop スプライト矩形の上辺から実体ピクセルまでの距離
        */
    public void setTransparencyTop(int transparencyTop) {
        this.transparencyTop = transparencyTop;
    }

    /**
        * スプライトの画像矩形の底辺のY座標から、透明ピクセルを除いて最も下にある不透明ピクセルの
        * Y座標までの差分をピクセル数で設定する
        *
        * @param transparencyBottom スプライト矩形の底辺から実体ピクセルまでの距離
        */
    public void setTransparencyBottom(int transparencyBottom) {
        this.transparencyBottom = transparencyBottom;
    }

    /**
        * スプライトの画像矩形の左辺のX座標から、透明ピクセルを除いて最も左にある不透明ピクセルの
        * X座標までの差分をピクセル数で設定する
        *
        * @param transparencyLeft スプライト矩形の左辺から実体ピクセルまでの距離
        */
    public void setTransparencyLeft(int transparencyLeft) {
        this.transparencyLeft = transparencyLeft;
    }

    /**
        * スプライトの画像矩形の右辺のX座標から、透明ピクセルを除いて最も右にある不透明ピクセルの
        * X座標までの差分をピクセル数で設定する
        *
        * @param transparencyRight スプライト矩形の左辺から実体ピクセルまでの距離
        */
    public void setTransparencyRight(int transparencyRight) {
        this.transparencyRight = transparencyRight;
    }

    /**
        * スプライト画像描画時の高さの拡大倍率を返す
        *
        * @return スプライト画像の高さの拡大倍率
        */
    public double getHeightRatio() {
        return heightRatio;
    }

    /**
        * スプライト画像描画時の幅の拡大倍率を返す
        *
        * @return スプライト画像の幅の拡大倍率
        */
    public double getWidthRatio() {
        return widthRatio;
    }

    /**
        * スプライト画像描画時の高さの拡大倍率を設定する
        *
        * @param heightRatio スプライト画像の高さの拡大倍率
        */
    public void setHeightRatio(double heightRatio) {
        this.heightRatio = heightRatio;
    }

    /**
        * スプライト画像描画時の幅の拡大倍率を設定する
        *
        * @param widthRatio スプライト画像の幅の拡大倍率
        */
    public void setWidthRatio(double widthRatio) {
        this.widthRatio = widthRatio;
    }

    /**
        * スプライト画像を拡大倍率込みで描画した際の、透明ピクセルを除いた実体部分の高さをピクセル数で返す
        *
        * @return 描画スプライト画像の実体の高さ
        */
    public int getActualHeight() {
        GeneralUtil.getAlphaPixel(this);
        return (int)((image.getHeight() - transparencyTop - transparencyBottom) * heightRatio);
    }

    /**
        * スプライト画像を拡大倍率込みで描画した際の、透明ピクセルを除いた実体部分の幅をピクセル数で返す
        *
        * @return 描画スプライト画像の実体の幅
        */
    public int getActualWidth() {
        GeneralUtil.getAlphaPixel(this);
        return (int)((image.getWidth() - transparencyRight - transparencyLeft) * widthRatio);
    }

    /**
        * スプライトの透明部分を除いた実体の上端のY座標を返す
        *
        * @return スプライト画像の実体部分上端のY座標
        */
    public double getEntityTopY() {
        return y - getActualHeight();
    }

    /**
        * スプライトの透明部分を除いた実体の下端のY座標を返す
        *
        * @return スプライト画像の実体部分下端のY座標
        */
    public double getEntityBaseY() {
        return y - getTransparencyBottom();
    }

    /**
        * スプライトの透明部分を除いた実体の左端のX座標を返す
        *
        * @return スプライト画像の実体部分左端のX座標
        */
    public double getEntityLeftX() {
        return x + getTransparencyLeft();
    }

    /**
        * スプライトの透明部分を除いた実体の右端のX座標を返す
        *
        * @return スプライト画像の実体部分右端のX座標
        */
    public double getEntityRightX() {
        return x + getActualWidth();
    }

    /**
        * スプライトの透明部分を除いた実体の上端のY座標を設定する
        *
        * @param y スプライト画像の実体部分上端のY座標
        */
    public void setEntityTopY(double y) {
        this.y = y + getActualHeight();
    }

    /**
        * スプライトの透明部分を除いた実体の下端のY座標を設定する
        *
        * @param y スプライト画像の実体部分下端のY座標
        */
    public void setEntityBaseY(double y) {
        this.y = y + getTransparencyBottom();
    }

    /**
        * スプライトの透明部分を除いた実体の左端のX座標を設定する
        *
        * @param x スプライト画像の実体部分左端のX座標
        */
    public void setEntityLeftX(double x) {
        this.x = x - getTransparencyLeft();
    }

    /**
        * スプライトの透明部分を除いた実体の右端のX座標を設定する
        *
        * @param x スプライト画像の実体部分右端のX座標
        */
    public void setEntityRightX(double x) {
        this.x = x - getActualWidth() - getTransparencyLeft();
    }

    /**
        * 拡大倍率を含めたスプライト画像の描画時の矩形上端のY座標を返す
        *
        * @return スプライト画像の矩形上端のY座標
        */
    public double getImageTopY() {
        return y - getImageHeight();
    }

    /**
        * 拡大倍率を含めたスプライト画像の描画時の矩形下端のY座標を返す
        *
        * @return スプライト画像の矩形下端のY座標
        */
    public double getImageBaseY() {
        return y;
    }

    /**
        * 拡大倍率を含めたスプライト画像の描画時の矩形中央のY座標を返す
        *
        * @return スプライト画像の矩形中央のY座標
        */
    public double getMiddleY() {
        return y - getActualHeight() / 2;
    }

    /**
        * 拡大倍率を含めたスプライト画像の描画時の矩形左端のX座標を返す
        *
        * @return スプライト画像の矩形左端のX座標
        */
    public double getImageLeftX() {
        return x;
    }

    /**
        * 拡大倍率を含めたスプライト画像の描画時の矩形右端のX座標を返す
        *
        * @return スプライト画像の矩形右端のX座標
        */
    public double getImageRightX() {
        return x + image.getWidth() * widthRatio;
    }

    /**
        * 拡大倍率を含めたスプライト画像の描画時の矩形上端のY座標を設定する
        *
        * @param y スプライト画像の矩形上端のY座標
        */
    public void setImageTopY(double y) {
        this.y = y + getImageHeight();
    }

    /**
        * 拡大倍率を含めたスプライト画像の描画時の矩形下端のY座標を設定する
        *
        * @param y スプライト画像の矩形下端のY座標
        */
    public void setImageBaseY(double y) {
        this.y = y;
    }

    /**
        * 拡大倍率を含めたスプライト画像の描画時の矩形左端のX座標を設定する
        *
        * @param x スプライト画像の矩形左端のX座標
        */
    public void setImageLeftX(double x) {
        this.x = x;
    }

    /**
        * 拡大倍率を含めたスプライト画像の描画時の矩形右端のX座標を設定する
        *
        * @param x スプライト画像の矩形右端のX座標
        */
    public void setImageRightX(double x) {
        this.x = x - getImageWidth();
    }

    /**
        * スプライトの現在のX方向の速度を返す
        *
        * @return スプライトの現在のX方向の速度
        */
    public double getVX() {
        return vx;
    }

    /**
        * スプライトの現在のY方向の速度を返す
        *
        * @return スプライトの現在のY方向の速度
        */
    public double getVY() {
        return vy;
    }

    /**
        * スプライトのX方向の速度を設定する
        *
        * @param vx スプライトの現在のX方向の速度
        */
    public void setVX(double vx) {
        this.vx = vx;
    }

    /**
        * スプライトのY方向の速度を設定する
        *
        * @param vy スプライトの現在のY方向の速度
        */
    public void setVY(double vy) {
        this.vy = vy;
    }

    /**
        * スプライトの重量を返す
        *
        * @return スプライトの重量
        */
    public double getWeight() {
        return weight;
    }

    /**
        * スプライトに設定された1フレームあたりの規定の移動速度を返す
        *
        * @return スプライトの設定速度
        */
    public double getSpeed() {
        return speed;
    }

    /**
        * スプライトの1フレームあたりの規定の移動速度を設定する
        *
        * @param speed スプライトの設定速度
        */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * <pre>
        * スプライトが着地しているかを返す
        * 着地している場合 true
        * </pre>
        *
        * @return スプライトが着地しているかどうか
        */
    public boolean getOnGround() {
        return onGround;
    }

    /**
     * <pre>
        * スプライトが着地しているかを設定する
        * 着地している場合 true
        * </pre>
        *
        * @param onGround スプライトが着地しているかどうか
        */
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    /**
        * 0度を初期状態として、スプライト画像の回転角度を返す
        *
        * @return スプライトの回転角度
        */
    public int getDegree() {
        return degree;
    }

    /**
     * <pre>
        * スプライトのX方向の向きを整数型で返す
        *　[1: 右] [-1: 左]
        * </pre>
        *
        * @return スプライトの向き
        */
    public int getDirectionX () {
        return this.directionX;
    }

    /**
        * スプライトを着地状態にする
        */
    public void land() {
        onGround = true;
    }

    /**
        * スプライト画像に回転角度の情報を付与する
        */
    public void rotate() {
        // 左向きなら時計回り
        if(directionX < 0) {
            degree -= newDegree;
        // 右向きなら反時計回り
        } else if(directionX > 0) {
            degree += newDegree;
        }

        degree %= 360;
    }

    /**
        * 自身と引数のスプライトが衝突しているかを判定する
        *
        * @param sprite 衝突判定対象のスプライトオブジェクト
        * @return 衝突判定結果 [true: 衝突している]
        */
    public boolean isCollision(BaseSprite sprite) {
        Rectangle subject = new Rectangle(
                (int)getEntityLeftX(),
                (int)getEntityTopY(),
                getActualWidth(),
                getActualHeight()
                );
        Rectangle object = new Rectangle(
                (int)sprite.getEntityLeftX(),
                (int)sprite.getEntityTopY(),
                sprite.getActualWidth(),
                sprite.getActualHeight()
                );
        // 自分の矩形と相手の矩形が重なっているか調べる
        if (subject.intersects(object)) {
            return true;
        }
        return false;
    }
}
