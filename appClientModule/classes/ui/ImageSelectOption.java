package classes.ui;

import java.awt.image.BufferedImage;

import classes.containers.Image;
import classes.controllers.FontController.Fonts;
import classes.utils.GeneralUtil;

/***
 * 画像を用いたアイコン型の選択リストを生成するUIオブジェクト
 *
 * @author Naoki Yoshikawa
 */
public class ImageSelectOption extends StringSelectOption {

    // アイコンに使用する画像オブジェクト
    private Image image;

    /**
     * 画像オブジェクト、描画位置の座標、フォント、表示する文字列の設定
     *
     * @param filePath    アイコン画像ファイルのパス
     * @param imageX      アイコン画像の描画位置のX座標
     * @param imageY      アイコン画像の描画位置のY座標
     * @param fonts       説明文に使用するフォントオブジェクト
     * @param description 説明文の文字列
     * @param stringX     説明文の描画位置のX座標
     * @param stringY     説明文の描画位置のX座標
     * @param size        フォントサイズ
     */
    public ImageSelectOption(
            BufferedImage img,
            int imageX,
            int imageY,
            Fonts fonts,
            String description,
            int stringX,
            int stringY,
            int size
            ) {
        super(stringX, stringY, fonts, description, size);
        this.image = new Image(imageX, imageY, img);
        fontList = GeneralUtil.changeStringToImage(fonts, description);

    }

    /**
     * 画像オブジェクトを返す
     *
     * @return 画像オブジェクト
     */
    public BufferedImage getImage() {
        return image.getImage();
    }

    /**
     * 画像矩形の左上のX座標を返す
     *
     * @return 画像矩形の左上のX座標
     */
    public int getImageX() {
        return image.getX();
    }

    /**
     * 画像矩形の左上のY座標を返す
     *
     * @return 画像矩形の左上のY座標
     */
    public int getImageY() {
        return image.getY();
    }


}
