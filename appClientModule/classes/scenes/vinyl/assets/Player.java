package classes.scenes.vinyl.assets;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import classes.constants.ImageResource;
import classes.math.Vector2;
import classes.scenes.old.assets.BaseSprite;
import classes.utils.GeneralUtil;
import interfaces.Animation;

public class Player extends BaseSprite implements Animation {

    /* 定数群 */
    private final int SPEED = 200; // プレイヤーの移動速度

    /**
     * Animationインターフェース用のメンバー変数
     */
    protected Map<String, List<BufferedImage>> frameHolder;
    protected String currentLabel;
    protected double subIndex;
    protected double speedRate; // 指定値nを取り、アニメーション速度を1/n倍にする

    // プレイヤーの位置ベクトル
    private Vector2 v;
    private double z;

    // 衝突判定前の移動成分ベクトル
    private Vector2 nv;

    // プレイヤーの2Dビュー上での角度
    private double angle;

    /**
     * プレイヤーの初期位置の座標、画像サイズ、画像ファイルのパスを設定する
     *
     * @param x        初期位置のX座標
     * @param y        初期位置のY座標
     * @param fileName プレイヤーのアニメーション用画像のファイルパス配列
     */
    public Player(
            double x,
            double y,
            ImageResource.FrameBundle[]... fileName
            ) {

        // ToDo: x,y 成分を2Dベクトルに統一する
        super(x, y, null);

        frameHolder = GeneralUtil.loadImageFile(fileName);

        widthRatio = 3;
         heightRatio = 3;
         speedRate = 4;
    }

    public void initParam() {
        this.v = new Vector2(x, y);
        this.nv = new Vector2(0, 0);
        switchLabel(ImageResource.HALFUP);
        angle = Math.PI;
         z = 0;
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
     * プレイヤーの移動ベクトルを返す
     *
     * @return 移動ベクトル
     */
    public Vector2 getDirVec() {
        return this.nv;
    }

    /**
     * 画面上のプレイヤーの向きをX軸からの回転角度で表して返す
     *
     * @return プレイヤーの向きを表す角度
     */
    public double getAngle() {
        return this.angle;
    }

    /**
     * 画面上のプレイヤーの向きを左右のいずれかで表して返す
     *
     * @return プレイヤーの左右を表す数値 -1:左, 1:右, 0:左右なし(上下)
     */
    public int getDirection() {
        if(getAngle() > -Math.PI/2 && getAngle() < Math.PI/2) {
            return 1;
        } else if(getAngle() > Math.PI/2 && getAngle() < Math.PI/2 * 3) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * プレイヤーの現在の高さをZ成分として返す
     *
     * @return プレイヤーの現在の高さの座標
     */
    public double getZ() {
        return this.z;
    }

    /**
     * プレイヤーに仮計算の移動ベクトルを与える
     *
     * @param dir 該当フレームでの移動ベクトル
     * @param dt  デルタタイム
     */
    public void move(Vector2 dir, double dt) {

        double theta = Math.atan2(dir.getY(), dir.getX());
        theta -= Math.PI/4.0;
        this.nv = this.nv.add(new Vector2(Math.cos(theta), Math.sin(theta)).scalar(dt * SPEED));
    }

    /**
     * 仮計算の移動ベクトルを位置ベクトルに加算する
     *
     * @param dt デルタタイム
     */
    public void proceed(double dt) {
        if(nv.mag() == 0) {
            subIndex = 0;
        } else {
            this.v = this.v.add(nv);
            stop();
            animate(dt);
        }
    }

    /**
     * <pre>
     * フレームごとの進行方向の仮ベクトルを参照し、プレイヤーの角度を設定する
     * 角度はクオータービュー上の角度を表す
     * </pre>
     */
    public void setAngle() {
        if(nv.mag() != 0) {
            this.angle = Math.atan2(nv.getY(), nv.getX()) + Math.PI/4.0;
        }
    }

    /**
     * 移動成分を0にする
     */
    public void stop() {
        nv = new Vector2(0.0, 0.0);
    }

    public void jump() {
        this.vy = -15;
    }

    /**
        * デフォルトのプレーヤーインスタンスを返すファクトリーメソッド
        *
        * @param x        プレイヤーの初期表示用X座標
     * @param y        プレイヤーの初期表示用Y座標
        * @return プレーヤーのデフォルトインスタンス
        */
    public static Player getDefault(int x, int y) {
        Player defaultPlayer = new Player(
                x,
                y,
                ImageResource.Player1RunRight.values(),
                ImageResource.Player1VinylRight.values(),
                ImageResource.Player1StandRight.values(),
                ImageResource.PlayerFront.values(),
                ImageResource.PlayerBack.values(),
                ImageResource.PlayerHalfdown.values(),
                ImageResource.PlayerHalfup.values()
                );
        return defaultPlayer;
    }

    /**
     * Animationインターフェースの機能群
     * @see interfaces.Animation
     */

    /**
     * このアセットの持つアニメーション用の画像群を返す
     * @return アニメーションごとのフレーム画像を保持するマップ
     */
    public Map<String, List<BufferedImage>> getFrameHolder() {
        return this.frameHolder;
    }

    /**
     * アニメーションのフレームを進める速度を設定する
     *
     * @param speedRate アニメーション速度
     * <pre>
     *   設定値1で等倍速、設定値が大きくなるほどアニメーション速度は下がる
     *   0以下を指定した場合は強制的に設定値1に書き換えるため0除算は発生しない
     * </pre>
     */
    public void setSpeedRate(int speedRate) {
        // 0および負数は無効化し、設定値1とする
        if(speedRate < 0) {
            speedRate = 1;
        }
        this.speedRate = speedRate;
    }

    /**
     * 変更予定のアニメーションラベル名と現在のラベル名を比較する
      *
     * @param label 変更予定のアニメーションラベル名
     */
    public boolean isSameLabel(String label) {
        if(currentLabel != null) {
            return this.currentLabel.equals(label);
        } else {
            return false;
        }
    }

    /**
     * アニメーションする画像群をラベル名を指定して切り替える
      *
     * @param label アニメーションを行う画像群のラベル名
     */
    public void switchLabel(String label) {
        if(!isSameLabel(label)) {
            // フレームインデックスを1フレーム目にリセットする
            this.subIndex = 0.0;
            // ラベル名をセットする
            this.currentLabel = label;
            // 1フレーム目の画像をセットする
            this.image = this.getCurrentBundle().get(getCurrentFrame());
        }
    }

    /**
     * フレームインデックスを進めて画像をアニメーションする
      *
     * @param dt デルタタイム
     */
    public void animate(double dt) {
        int fixRate = 50;

        // [速度定数]/[指定されたアニメーション速度]をベースとして、ダッシュ倍率をかけたものをアニメーションの速度とする
        subIndex = (subIndex + (fixRate / speedRate) * dt) % getCurrentBundle().size();

        // subIndexの整数部分を現在のアニメーションインデックスとして参照する
        image = getCurrentBundle().get(getCurrentFrame());
    }

    /**
     * フレームインデックスを進めて画像をアニメーションする
      *
     * @return 現在使用しているアニメーション用の画像リスト
     */
    public List<BufferedImage> getCurrentBundle() {
        return frameHolder.get(currentLabel);
    }

    /**
     * 現在表示されているアニメーション用の画像リスト内のインデックスを返す
      *
     * @return 現在のアニメーションインデックス
     */
    public int getCurrentFrame() {
        return (int) subIndex;
    }
}
