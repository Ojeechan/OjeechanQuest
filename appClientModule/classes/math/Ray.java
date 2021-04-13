package classes.math;

/**
 * 位置ベクトルと方向ベクトルによって光線の情報を定義するクラス

 * @author Naoki Yoshikawa
 */
public class Ray {

    // Y軸平行の許容値
    private final double EPSILON = 0.001;

    // 位置ベクトル
    private Vector2 pos;

    // 方向ベクトル
    private Vector2 dir;

    /**
     * 位置ベクトル、方向ベクトルを設定する
     *
     * @param pos 光線の位置ベクトル
     * @param dir 光線の方向ベクトル
     */
    public Ray(Vector2 pos, Vector2 dir) {
        this.pos = pos;
        this.dir = dir;
    }

    /**
     * 光線の位置ベクトルを返す
     *
     * @return 位置ベクトル
     */
    public Vector2 getPosVec() {
        return this.pos;
    }

    /**
     * 光線の方向ベクトルを返す
     *
     * @return 方向ベクトル
     */
    public Vector2 getDirVec() {
        return this.dir;
    }

    /**
     * <pre>
     * 光線の開始位置の座標を位置ベクトルで返す
     * getPosVecのエイリアス
     * </pre>
     *
     * @return 光線の開始位置ベクトル
     */
    public Vector2 getStartPos() {
        return getPosVec();
    }

    /**
     * 光線の終端位置の座標を位置ベクトルで返す
     *
     * @return 光線の終端位置ベクトル
     */
    public Vector2 getEndPos() {
        return this.pos.add(this.dir);
    }

    /**
     * 方向ベクトルに対しての正規化した法線ベクトルを返す
     *
     * @return 法線ベクトル
     */
    public Vector2 getOrthonorm() {
        Vector2 v = new Vector2(-getSlope(), 1);
        return v.scalar(1/v.mag());
    }

     /**
      * 光線の傾きを返す
      *
      * @return 光線の傾き
      */
    public double getSlope() {

        // Y軸平行の傾きは許容値とする
        if(Math.abs(this.dir.getX()) < EPSILON) {
            this.dir = new Vector2(EPSILON, this.dir.getY());
        }

        return this.dir.getY() / this.dir.getX();
    }

}
