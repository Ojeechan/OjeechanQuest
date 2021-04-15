package classes.scenes.old.assets;

import java.awt.image.BufferedImage;

import classes.constants.ImageResource;
import classes.utils.GeneralUtil;

/**
 * <pre>
 * アクションステージ用の静的アセットを定義するクラス
 * 背景の一部としてなど、動的な処理が必要ないオブジェクトに使用する
 * </pre>
 *
 * @author Naoki Yoshikawa
 */
public class StaticObject extends BaseSprite {

    // 追加予定のプロパティ
    private boolean isBreakable;
    private boolean hasEntity;

    // プレイヤーと重なっているかどうか
    private boolean isLayered;

    // リアクションのアニメーション中かどうか
    private boolean isInMotion;

    // 膨らんでいる最中か
    private boolean isSwelling;

    // ブロックが膨らんだ後、元に戻るための元のサイズ情報
    private double originalRatio;
    private double originalX;
    private double originalWidth;

    // キャラクターが重なった時に表示する半透明の画像
    BufferedImage transparentImage;
    private ImageResource.StageObject object;

    /**
     * 拡大表示する場合はこちらを呼び出す
     * @param x      オブジェクトの描画位置のX座標
     * @param y         オブジェクトの描画位置のY座標
     * @param object オブジェクト画像のファイルパスをもつ定数オブジェクト
     * @param ratio  拡大倍率
     */
    public StaticObject(int x, int y, ImageResource.StageObject object, int ratio) {
        this(x, y, object);
        originalRatio = ratio;
        widthRatio = ratio;
        heightRatio = ratio;

    }

    /**
     * 拡大表示しない場合はこちらを呼び出す
     *
     * @param x      オブジェクトの描画位置のX座標
     * @param y         オブジェクトの描画位置のY座標
     * @param object オブジェクト画像のファイルパスをもつ定数オブジェクト
     */
    public StaticObject(int x, int y, ImageResource.StageObject object) {
        super(x, y, GeneralUtil.readImage(object.getValue()));
        // ロジックに差異がなく、オブジェクトで切り分ける意味が薄い
        // そのためクラスではなくプロパティで自身のタイプを示す
        this.object = object;
        image = GeneralUtil.readImage(object.getValue());
        transparentImage = GeneralUtil.makeTransparent(image);
        isInMotion = false;
        isSwelling = false;
        originalRatio = 1;
        widthRatio = 1;
        heightRatio = 1;
        originalX = x;
        originalWidth = getImageWidth();
    }

    /**
     * <pre>
     * オブジェクトの種類を文字列で返す
     * 定数クラスのオブジェクト名をそのまま使用している
     * </pre>
     *
     * @return オブジェクト名
     */
    public String getObjectType() {
        return object.toString();
    }

    /**
     * プレイヤーに叩かれて拡大する前の、デフォルトの拡大倍率を返す
     *
     * @return デフォルトの拡大倍率
     */
    public double getOriginalRatio() {
        return originalRatio;
    }

    /**
     * プレイヤーと重なっているかどうかを返す
     *
     * @return 重なっているならばtrue
     */
    public boolean isLayered() {
        return isLayered;
    }

    /**
     * <pre>
     * ブロックが膨らんでいるかどうかを返す
     * Todo: ブロック用のロジックであり、クラスを切り分けるべき
     * </pre>
     *
     * @return 膨らんでいる最中の場合true
     */
    public boolean isSwelling() {
        return isSwelling;
    }

    /**
     * 設定されている画像オブジェクトを返す
     *
     * @return 画像オブジェクト
     */
    @Override
    public BufferedImage getImage() {

        // プレイヤーと重なっている場合半透明の画像を返す
        if(isLayered) {
            return transparentImage;
        } else {
            return image;
        }
    }

    /**
     * <pre>
     * プレイヤーと重なっているかどうかを設定する
     * 重なっている場合 true
     * </pre>
     *
     * @param isLayered プレイヤーと重なっているかどうか
     */
    public void setIsLayered(boolean isLayered) {
        this.isLayered = isLayered;
    }

    /**
     * 半透明の画像オブジェクトを返す
     *
     * @return 画像オブジェクト
     */
    public BufferedImage getTransparentImage() {
        return transparentImage;
    }

    /**
     * <pre>
     * ブロックがリアクション中であるかどうかを返す
     * リアクション中なら true
     * </pre>
     *
     * @return リアクション中かどうか
     */
    // Todo: ブロック用のロジックであり、クラスを切り分けるべき
    public boolean isInMotion() {
        return isInMotion;
    }

    /**
     * ブロックオブジェクトのリアクションを実験的に実装
     */
    public void swell() {
        if(isSwelling) {
            if(widthRatio < 2) {
                widthRatio += 0.1;
                heightRatio += 0.1;
                y -= 8;
                x = originalX - (getImageWidth() - originalWidth) / 2 ;
            } else {
                originalX = x;
                originalWidth = getImageWidth();
                isSwelling = false;
            }
        } else {
            if(widthRatio > originalRatio) {
                widthRatio -= 0.1;
                heightRatio -= 0.1;
                y += 8;
                x = originalX + (originalWidth - getImageWidth()) / 2 ;
            } else {
                isInMotion = false;
                originalX = x;
                originalWidth = getImageWidth();
            }
        }
    }

    /**
     * プレイヤーに叩かれた際にリアクションを取る
     */
    public void strucken() {
        isInMotion = true;
        isSwelling = true;
    }
}
