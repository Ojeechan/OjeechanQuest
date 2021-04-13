package classes.scenes.slot.assets;

import classes.math.Ray;
import classes.math.Vector2;

/**
 * スロットステージ導入部分での一人称視点プレイヤーを定義するクラス
 *
 * @author Naoki Yoshikawa

 */
public class Player {

    // プレイヤーの位置ベクトル
    private Vector2 v;

    // 衝突判定前の移動成分ベクトル
    private Vector2 nv;

    // プレイヤーの正面の向きを表す角度
    private double direction;

    // 正面から左右の視野角
    private double viewAngle;

    // 左右の視野角ごとの光線数
    private int rayNum;

    private Init init;

    /**
     * プレイヤーの座標、方向、視角、光線数を設定する
     *
     * @param x プレイヤーのマップ上でのX座標
     * @param y プレイヤーのマップ上でのY座標
     */
    public Player(double x, double y) {
        init = new Init(new Vector2(x, y));
        viewAngle = Math.PI / 5.5;
        rayNum = 640;
    }

    private class Init {
        private Vector2 v;
        private Init(Vector2 v) {
            this.v = v;
        }
    }

    /**
     * プレイヤーの位置ベクトルを返す
     *
     * @return 位置ベクトル
     */
    public Vector2 getPosVec() {
        return this.v;
    }

    /**
     * プレイヤーの真正面の視線の方向ベクトルを返す
     *
     * @return 真正面の視線の方向ベクトル
     */
    public Vector2 getCenterDir() {
        return new Vector2(Math.cos(direction), Math.sin(direction));
    }

    /**
     * <pre>
     * プレイヤーの正面の方向の角度を返す
     * X軸の正の方向を0(rad)とする
     * </pre>
     *
     * @return X軸に対するプレイヤーの正面の角度(rad)
     */
    public double getDirection() {
        return this.direction;
    }

    /**
     * 壁の内側に戻すための調整メソッド
     *
     * @param vx　壁との交点のX座標
     */
    public void adjustX(double vx) {
        this.v = new Vector2(vx, v.getY());
    }

    /**
     * 壁の内側に戻すための調整メソッド
     *
     * @param vy　壁との交点のY座標
     */
    public void adjustY(double vy) {
        this.v = new Vector2(v.getX(), vy);
    }

    /**
     * 移動成分を壁ずりベクトルに補正する
     *
     * @param newDir 壁ずりベクトル
     */
    public void adjustPos(Vector2 newDir) {
        this.nv = newDir;
    }


    /**
     * 仮計算の移動ベクトルを位置ベクトルに加算する
     */
    public void proceed() {
        this.v = this.v.add(this.nv);
    }

    /**
     * 正面の視線に対して平行の移動成分を設定する
     *
     * @param s 移動距離
     */
    public void moveVertical(double s) {
        //v = v.add(new Vector2(Math.cos(direction), Math.sin(direction)).scalar(s));
        nv = nv.add(new Vector2(Math.cos(direction), Math.sin(direction)).scalar(s));
    }

    /**
     * 正面の視線に対して垂直の移動成分を設定する
     *
     * @param s 移動距離
     */
    public void moveHorizontal(double s) {
        double left = direction + Math.PI / 2;
        //v = v.sub(new Vector2(Math.cos(left), Math.sin(left)).scalar(s));
        nv = nv.sub(new Vector2(Math.cos(left), Math.sin(left)).scalar(s));
    }

    /**
     * 移動成分を0にする
     */
    public void stop() {
        nv = new Vector2(0.0, 0.0);
    }

    /**
     * 当該フレームでの移動情報を位置ベクトルと方向ベクトルの組で返す
     *
     * @return 移動情報を表すRayオブジェクト
     */
    public Ray getMoveRay() {
        return new Ray(v, nv);
    }

    /**
     * 視点を回転させる
     *
     * @param degree 回転角度(rad)
     */
    public void turn(double degree) {
        this.direction += degree;
    }

    /**
     * 視角の左端の角度を返す
     *
     * @return 視角の左端の角度
     */
    public double getLeftEnd() {
        return direction - viewAngle;
    }

    /**
     * 視角の右端の角度を返す
     *
     * @return 視角の右端の角度
     */
    public double getRightEnd() {
        return direction + viewAngle;
    }

    /**
     * 正面を中央とした左右片側あたりの視角を返す
     *
     * @return 左右片側あたりの視角
     */
    public double getViewAngle() {
        return viewAngle;
    }

    /**
     * 正面を中央とした左右の視角の合計を返す
     *
     * @return 左右の視角の合計
     */
    public double getWholeViewAngle() {
        return viewAngle * 2;
    }

    /**
     * プレイヤーから発せられる光線の数を返す
     *
     * @return 光線の数
     */
    public int getRayNum() {
        return this.rayNum;
    }

    /**
     * 各パラメータを初期化する
     */
    public void initParam() {
        this.nv = new Vector2(0, 0);
        this.v = init.v.copy();
        direction = - Math.PI / 2;
    }
}
