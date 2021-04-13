package classes.scenes.old.assets;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.controllers.SoundController;
import classes.utils.GeneralUtil;
import interfaces.Animation;

/**
 * アクションステージの敵スプライトのパラメータ・操作を定義するクラス
 *
 * @author  Naoki Yoshikawa
 */
public class Enemy extends BaseSprite implements Animation {
    /* 定数パラメータ */
    // X方向の初期移動速度
    private static final double SPEED = 50;

    // アニメーション速度
    private static final int RATE = 10;

    // 生存フラグ
    private boolean isAlive;

    // プレイヤーに踏まれた際のX方向の移動速度
    private double blownX;

    /**
     * Animationインターフェース用のメンバー変数
     */
    protected Map<String, List<BufferedImage>> frameHolder;
    protected String currentLabel;
    protected double subIndex;
    // 指定値nを取り、アニメーション速度を1/n倍にする
    protected int speedRate;

    /**
     * 敵スプライトに必要なパラメータを初期化
     *
     * @param x        敵スプライトの初期表示用X座標
     * @param y        敵スプライトの初期表示用Y座標
     * @param width    敵スプライト画像を描画する際の幅
     * @param height   敵スプライト画像の描画する際の高さ
     * @param fileName アニメーションに使用する画像群のファイルパス
     */
    public Enemy(double x, double y, int width, int height, ImageResource.FrameBundle[]... fileName) {
        super(x, y, null);
        // 元画像の縦横2倍で描画する
        super.widthRatio = 2;
         super.heightRatio = 2;
         frameHolder = GeneralUtil.loadImageFile(fileName);
        switchLabel(ImageResource.RUN_RIGHT);
        setSpeedRate(RATE);
        this.isAlive = true;
        super.speed = SPEED;
        super.weight = 10;
    }

    /* getter, setter */

    /**
        * 敵スプライトがプレイヤーに踏まれた際のX方向の速度を返す
        *
        * @return 敵スプライトがプレイヤーに踏まれた際のX方向の速度
        */
    public double getBlownX() {
        return blownX;
    }

    /**
     * <pre>
        * 敵スプライトが生存しているかどうかを返す
        * プレイヤーに踏まれる前はtrue
        * 踏まれてからリスポーンするまではfalse
        * </pre>
        *
        * @return 敵スプライトの生存フラグ
        */
    public boolean getIsAlive() {
        return isAlive;
    }

    /**
     * <pre>
        * 敵スプライトの生存を設定する
        * プレイヤーに踏まれる前はtrue
        * 踏まれてからリスポーンするまではfalse
        * </pre>
        *
        * @param isAlive 敵スプライトの生存フラグ
        */
    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

     /**
        * 敵スプライトをY方向に移動させる
        */
    public void jump() {
        if(onGround) {
            vy = - 12;
            onGround = false;
        }
    }

    /**
        * 敵スプライトをX方向に移動させる
        *
        * @param direction 移動方向 [-1:左, 1:右]
        * @param dt        デルタタイム
        */
    public void run(int direction, double dt) {
        vx = speed * direction * dt;
    }

    /**
        * プレイヤーに踏まれた際のリアクションをとる
        *
        * @param vx 踏まれたリアクション時のX方向の速度
        * @param vy 踏まれたリアクション時のY方向の速度
        */
    public void die(double vx, double vy) {
        // リアクションの速度をセットする
        blown(vx, vy);
        // 生存フラグを変更
        isAlive = false;
    }

    /**
        * プレイヤーに踏まれた際のリアクションの速度を設定する
        *
        * @param vx 踏まれたリアクション時のX方向の速度
        * @param vy 踏まれたリアクション時のY方向の速度
        */
    private void blown(double vx, double vy) {
        new Thread(new SoundController.PlaySE(SoundResource.SE_AH)).start();
        this.vy = vy;
        blownX = vx;
    }

    /**
        * プレイヤーに倒された後所定の位置に再出現する
        *
        * @param mapHeight マップの高さ(ピクセル)
        */
    public void respawn(int mapHeight) {
        isAlive = true;
        x = 0;
        y = mapHeight - getActualHeight();
    }

    /**
     * Animationインターフェースの機能群
     * @see interfaces.Animation.getFrameHolder()
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
            if(!isSameLabel(label)) {
                // フレームインデックスを1フレーム目にリセットする
                this.subIndex = 0.0;
                // ラベル名をセットする
                this.currentLabel = label;
                // 1フレーム目の画像をセットする
                this.image = this.getCurrentBundle().get(getCurrentFrame());
            }
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
