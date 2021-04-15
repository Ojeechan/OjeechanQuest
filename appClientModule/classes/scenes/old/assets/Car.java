package classes.scenes.old.assets;
import java.awt.Point;
import java.util.List;
import java.util.Random;

import classes.utils.GeneralUtil;

/**
 * アクションステージ用の車アセットを定義するクラス
 *
 * @author Naoki Yoshikawa
 */
public class Car extends BaseSprite {

    // 車が右向きかどうか
    private boolean isRight;
    private Random random;

    // 車にランダムに着色するための、着色対象のピクセルのリスト
    private List<Point> pixelList;

    /**
     * 描画位置の座標、描画サイズ、画像ファイルのパス、着色ピクセルのリスト、ランダム配色の決定
     *
     * @param x         スプライトの描画位置のX座標
     * @param y         スプライトの描画位置のY座標
     * @param width     スプライトの描画する際の幅
     * @param height    スプライトの描画する際の高さ
     * @param path        スプライト画像のファイルパス
     * @param pixelList 着色するピクセルのリスト
     */
    public Car(int x, int y, int width, int height, String path, List<Point> pixelList) {
        super(x, y, GeneralUtil.changeRandomColor(GeneralUtil.readImage(path), pixelList));
        random = new Random();
        widthRatio = 4;
        heightRatio = 4;
        this.pixelList = pixelList;
        isRight = random.nextInt(2) == 1;
        vx = GeneralUtil.getRandom(12, 4);
        this.x = random.nextInt(x);
        if(!isRight) {
            vx = -vx;
        }
    }

    /**
     * <pre>
     * 画像が右向きかどうかを返す
     * 右向きなら true
     * </pre>
     *
     * @return 右向きかどうか
     */
    public boolean isRight() {
        return isRight;
    }

    /**
     * 車の画像データのうち着色するピクセルのリストを返す
     *
     * @return 着色ピクセルのリスト
     */
    public List<Point> getPixelList() {
        return pixelList;
    }
}
