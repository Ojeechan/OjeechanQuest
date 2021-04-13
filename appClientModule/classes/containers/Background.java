package classes.containers;
import java.awt.image.BufferedImage;

/**
 * 背景画像のプロパティを集約するラッパークラス
 *
 * @author  Naoki Yoshikawa
 */
public class Background {

    // 背景に使用する画像オブジェクト
    private BufferedImage image;

    // 視差効果に用いるスクロール速度
    private int scrollRate;

    // 画像描画時の拡大倍率
    private int sizeRate;

    // スクロール速度の基準値
    public static final int SCROLLRATE_BASE = 10;

    /**
     * 背景画像のプロパティを設定する
     *
     * @param image      背景画像オブジェクト
     * @param scrollRate スクロール速度
     * @param sizeRate   背景画像の拡大倍率
     */
    public Background(BufferedImage image, int scrollRate, int sizeRate) {
        this.image = image;
        this.scrollRate = scrollRate;
        this.sizeRate = sizeRate;
    }

    /**
     * 背景画像オブジェクトを返す
     *
     * @return 背景画像オブジェクト
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * 背景画像のスクロール速度を返す
     *
     * @return スクロール速度
     */
    public int getScrollRate() {
        return scrollRate;
    }

    /**
     * 背景画像の拡大倍率を返す
     *
     * @return 拡大倍率
     */
    public int getSizeRate() {
        return sizeRate;
    }
}
