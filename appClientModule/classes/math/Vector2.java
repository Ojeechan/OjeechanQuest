package classes.math;

/**
 * 2次元ベクトルを定義するクラス
 *
 * @author Naoki Yoshikawa
 */
public class Vector2 {
    private double x;
    private double y;

    /**
     * ベクトルの成分を設定する
     *
     * @param x ベクトルのX成分
     * @param y ベクトルのY成分
     */
    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * ベクトルのX成分を返す
     *
     * @return X成分
     */
    public double getX() {
        return this.x;
    }

    /**
     * ベクトルのY成分を返す
     *
     * @return Y成分
     */
    public double getY() {
        return this.y;
    }

    /**
     * <pre>
     * 2ベクトルの加算結果を返す
     * 新たにインスタンスを返すため、元のベクトルは変化しない
     * </pre>
     *
     * @param u このインスタンスに加算するベクトルオブジェクト
     *
     * @return 加算結果のベクトル
     */
    public Vector2 add(Vector2 u) {
        return new Vector2(this.x + u.getX(), this.y + u.getY());
    }

    /**
     * <pre>
     * 2ベクトルの減算結果を返す
     * 新たにインスタンスを返すため、元のベクトルは変化しない
     * </pre>
     *
     * @param u このインスタンスに減算するベクトルオブジェクト
     *
     * @return 減算結果のベクトル
     */
    public Vector2 sub(Vector2 u) {
        return new Vector2(this.x - u.getX(), this.y - u.getY());
    }

    /**
     * 2ベクトルの内積を返す
     *
     * @param u このインスタンスとの内積を求めるベクトルオブジェクト
     *
     * @return 2ベクトルの内積
     */
    public double dot(Vector2 u) {
        return this.x * u.x + this.y * u.y;
    }

    /**
     * ベクトルのコピーオブジェクトを返す
     *
     * @return コピーオブジェクト
     */
    public Vector2 copy() {
        Vector2 cv = new Vector2(x, y);
        return cv;
    }

    /**
     * ベクトルの大きさを返す
     *
     * @return ベクトルの大きさ
     */
    public double mag() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    /**
     * <pre>
     * ベクトルをスカラー倍する
     * add, subと異なりベクトルの値そのものを変更する
     * </pre>
     *
     * @param s スカラー倍
     *
     * @return スカラー倍したベクトル
     */
    public Vector2 scalar(double s) {
        this.x *= s;
        this.y *= s;
        return this;
    }
}