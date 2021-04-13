package classes.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import classes.constants.ImageResource;
import classes.containers.Background;
import classes.controllers.FontController.Fonts;
import classes.controllers.ScriptController.Script;
import classes.controllers.GameController;
import classes.controllers.MapController;

import classes.scenes.old.assets.*;
import classes.ui.StringSelectOption;

/**
 * <pre>
 * 汎用メソッドを集約したユーティリティクラス
 * 現在ステージごとの区別をせずにこの1クラスに集約している
 * </pre>
 *
 * @author  Naoki Yoshikawa
 */
public class GeneralUtil {

    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;

    /**
        * プレイヤーをカメラ中央に固定するためのオフセット値を算出する
        *
        * @param imagePos   プレイヤーの画像の座標
        * @param cameraSize カメラ枠のサイズ(高さ または 幅)
        * @param mapSize    マップのサイズ(高さ または 幅)
        *
        * @return カメラのオフセット値
        */
    public static int getCameraOffset(int imagePos, int cameraSize, int mapSize) {
        int offset = cameraSize / 2 - imagePos;
        offset = Math.min(offset, 0);
        offset = Math.max(offset, cameraSize - mapSize);
        return offset;
    }

    /**
        * 整数のARGB値パラメータををシフトしたのちOR演算で1つの整数にまとめる
        *
        * @param a アルファ値
        * @param r 赤色
        * @param g 緑色
        * @param b 青色
        *
        * @return ARGBを表す整数値
        */
    public static int argb(int a,int r,int g,int b) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    /**
        * 指定したパスの画像の中から、指定した色を持つピクセルを特定する
        *
        * @param img    ピクセルを検索する画像データ
        * @param target 検索対象の色
        *
        * @return 指定色を持つ画像内のピクセルのリスト
        */
    public static List<Point> checkColor(BufferedImage img, Color target) {
        List<Point >pixelList= new ArrayList<Point>();
        int w = img.getWidth();
        int h = img.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = new Color(img.getRGB(x, y));
                if(c.equals(target)) {
                    pixelList.add(new Point(x, y));
                }
            }
        }
        return pixelList;
    }

    /**
        * 指定したパスの画像の中から、指定したピクセルの色をランダムに変更する
        *
        * @param image      ピクセルを検索する画像オブジェクト
        * @param targetList 色を変更するピクセルのリスト
        *
        * @return 色を変更した画像オブジェクト
        */
    public static BufferedImage changeRandomColor(BufferedImage image, List<Point> targetList) {
        Random random = new Random();
        Color randomColor = new Color(
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)
            );

        for (Point target: targetList) {
            image.setRGB(target.x, target.y, randomColor.getRGB());
        }

        return image;
    }

    /**
        * 指定したパスの画像の中から、指定したピクセルの色を指定色に変更する
        *
        * @param image      ピクセルを検索する画像オブジェクト
        * @param targetList 色を変更するピクセルのリスト
        * @param color      変更先の色
        *
        * @return 色を変更した画像オブジェクト
        */
    public static BufferedImage changeColor(BufferedImage image, List<Point> targetList, Color color) {
        for (Point target: targetList) {
            image.setRGB(target.x, target.y, color.getRGB());
        }
        return image;
    }



    /**
        * スプライトに設定した画像矩形の四方の辺から、透明ピクセルを除いた実体までの距離を設定する
        *
        * @param sprite 画像を持つスプライトオブジェクト
        */
    public static void getAlphaPixel(BaseSprite sprite) {
        BufferedImage image = sprite.getImage();
        // 上下左右の矩形の辺から、透明でないピクセルまでのピクセル数を算出し設定
        sprite.setTransparencyTop(getTransparencyTop(image));
        sprite.setTransparencyBottom(getTransparencyBottom(image));
        sprite.setTransparencyLeft(getTransparencyLeft(image));
        sprite.setTransparencyRight(getTransparencyRight(image));
    }

    /**
        * スプライトに設定した画像矩形の四方の辺から、透明ピクセルを除いた実体までの距離を設定する
        *
        * @param sprite 画像を持つスプライトオブジェクト
        */
    public static void getAlphaPixel(classes.scenes.action.assets.BaseSprite sprite) {
        BufferedImage image = sprite.getImage();
        // 上下左右の矩形の辺から、透明でないピクセルまでのピクセル数を算出し設定
        sprite.setTransparencyTop(getTransparencyTop(image));
        sprite.setTransparencyBottom(getTransparencyBottom(image));
        sprite.setTransparencyLeft(getTransparencyLeft(image));
        sprite.setTransparencyRight(getTransparencyRight(image));
    }

    /**
        * スプライトに設定した画像矩形の上端から、透明ピクセルを除いた実体までの距離を設定する
        *
        * @param image スプライトの画像オブジェクト
        *
        * @return 画像矩形の上端から透明ピクセルを除いた実体までの距離
        */
    public static int getTransparencyTop(BufferedImage image) {
        for(int i = 0; i < image.getHeight(); i++) {
            for(int j = 0; j < image.getWidth(); j++) {
                if(image.getRGB(j, i) != 0) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
        * スプライトに設定した画像矩形の下端から、透明ピクセルを除いた実体までの距離を設定する
        *
        * @param image スプライトの画像オブジェクト
        *
        * @return 画像矩形の下端から透明ピクセルを除いた実体までの距離
        */
    public static int getTransparencyBottom(BufferedImage image) {
        for(int i = 0; i < image.getHeight(); i++) {
            for(int j = 0; j < image.getWidth(); j++) {
                if(image.getRGB(j, image.getHeight() - i - 1) != 0) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
        * スプライトに設定した画像矩形の左端から、透明ピクセルを除いた実体までの距離を設定する
        *
        * @param image スプライトの画像オブジェクト
        *
        * @return 画像矩形の左端から透明ピクセルを除いた実体までの距離
        */
    public static int getTransparencyLeft(BufferedImage image) {
        for(int i = 0; i < image.getWidth(); i++) {
            for(int j = 0; j < image.getHeight(); j++) {
                if(image.getRGB(i, j) != 0) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
        * スプライトに設定した画像矩形の右端から、透明ピクセルを除いた実体までの距離を設定する
        *
        * @param image スプライトの画像オブジェクト
        *
        * @return 画像矩形の右端から透明ピクセルを除いた実体までの距離
        */
    public static int getTransparencyRight(BufferedImage image) {
        for(int i = 0; i < image.getWidth(); i++) {
            for(int j = 0; j < image.getHeight(); j++) {
                if(image.getRGB(image.getWidth() - i - 1, j) != 0) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
        * ファイルパスから画像をオブジェクトに読み出す
        * @see ImageIO
        *
        * @param path 画像のファイルパス
        *
        * @return 画像オブジェクト
        *
        * @exception IllegalArgumentException 引数のパスが不正の時nullとなる
        * @exception IOException 読み込み時のエラー
        */
    public static BufferedImage readImage(String path) {
        try {
            return ImageIO.read(GeneralUtil.class.getResource(path));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
        * 画像のアルファ値を変更して透明にする
        *
        * @param former アルファ値を変更する画像オブジェクト
        *
        * @return アルファ値を変更した画像オブジェクト
        */
    public static BufferedImage makeTransparent(BufferedImage former) {
        int w = former.getWidth();
        int h = former.getHeight();
        BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

            // イメージのアルファ値を元イメージの半分にする。
            int rgb = former.getRGB(x, y);
            if(rgb != 0) {
                Color c = new Color(rgb);
                rgb = GeneralUtil.argb(c.getAlpha() / 2,  c.getRed(), c.getGreen(), c.getBlue());
            }
            newImage.setRGB(x, y, rgb);
            }
        }
        return newImage;
    }

    /**
        * ピクセルの座標をステージのタイルサイズでタイル座標に変換する
        *
        * @param pixels ピクセルの座標
        *
        * @return タイルサイズに応じたタイル座標
        */
    public static int pixelsToTiles(double pixels) {
        return (int) Math.floor(pixels / MapController.TILE_SIZE);
    }

    /**
        * タイル座標をピクセル座標に変換する
        *
        * @param tiles タイル座標
        *
        * @return タイルサイズに応じたピクセル座標
        */
    public static int tilesToPixels(int tiles) {
        return tiles * MapController.TILE_SIZE;
    }

    /**
     * <pre>
        * スプライトとマップオブジェクトの衝突判定を行う
        * 現在試験開発中
        * </pre>
        *
        * @param object 衝突判定を行うスプライトオブジェクト
        * @param map    アクションステージの地形オブジェクト
        */
    public static void getCollision(BaseSprite object, MapController map) {

        Point tile = getCollisionNew(object, map);

        if (tile.x == -9999) {
            object.setImageLeftX(object.getImageLeftX() + object.getVX());

        } else {
            if(object instanceof Ball) {
                Ball ball = (Ball) object;

                ball.bounceX();

            }
            object.setImageLeftX(tile.x);
        }
        if (tile.y == -9999) {
            object.setImageBaseY(object.getImageBaseY() + object.getVY());
            object.setOnGround(false);
        } else {
            if (object.getVY() > 0) {
                object.setImageBaseY(tile.y);

                if(object instanceof Ball) {
                    Ball ball = (Ball) object;

                    ball.bounceY();

                } else {
                    // 共通化してstopにする
                    object.setVY(0);

                }
                object.land();

            } else if (object.getVY() < 0) {
                object.setImageBaseY(tile.y);
                if(object instanceof Ball) {
                    Ball ball = (Ball) object;
                    ball.bounceY();
                } else {
                    // 共通化してstopにする
                    object.setVY(0);
                }
            }
        }
    }

    /* 衝突判定系 実装検証中 */

    /**
     * <pre>
        * スプライトとマップオブジェクトの衝突判定を行う
        * 現在試験開発中
        * </pre>
        *
        * @param sprite 衝突判定を行うスプライトオブジェクト
        * @param map    アクションステージの地形オブジェクト
        *
        * @return 衝突を初めに検知した位置の座標
        */
    public static Point getCollisionNew(BaseSprite sprite, MapController map) {
        double vx = Math.ceil(sprite.getVX());
        double vy = Math.ceil(sprite.getVY());
        double originX = sprite.getImageLeftX();
        double originY = sprite.getImageBaseY();
        double ratio = 0;
        Point point = new Point(-9999, -9999);
        int newX = (int)originX;
        int newY = (int)originY;

        while(ratio <= 1) {

            int tempX = (int)(originX + vx * (ratio+0.1));
            int tempY = (int)(originY + vy * (ratio+0.1));

            if (point.x == -9999 && (tempX < 0 || tempX + sprite.getActualWidth() >= map.getWidth())) {
                point.x = newX;
            }

            if (point.y == -9999 && (tempY < 0 || tempY >= map.getHeight())) {
                point.y = newY;
            }

            ratio += 0.1;
            newX = tempX;
            newY = tempY;
        }

        return point;
    }

    /**
     * <pre>
        * スプライトとブロックオブジェクトとの衝突判定を行う
        * 現在試験開発中
        * </pre>
        *
        * @param subject ブロックとの衝突判定を行うスプライトオブジェクト
        * @param object  ブロックオブジェクト
        */
    public static void getBlock(BaseSprite subject, BaseSprite object) {

        Point tile = getCollisionBlock(subject, object);

        if (tile.x == -9999) {
            subject.setImageLeftX(subject.getImageLeftX() + subject.getVX());

        } else {
            if(subject instanceof Ball) {
                Ball ball = (Ball) subject;

                ball.bounceX();

            }
            subject.setImageLeftX(tile.x);
        }
        if (tile.y == -9999) {
            subject.setImageBaseY(subject.getImageBaseY() + subject.getVY());
            subject.setOnGround(false);
        } else {
            if (subject.getVY() > 0) {
                subject.setImageBaseY(tile.y);

                if(subject instanceof Ball) {
                    Ball ball = (Ball) subject;

                    ball.bounceY();

                } else {
                    // 共通化してstopにする
                    subject.setVY(0);

                }
                subject.land();

            } else if (subject.getVY() < 0) {
                subject.setImageBaseY(tile.y);
                if(subject instanceof Ball) {
                    Ball ball = (Ball) subject;
                    ball.bounceY();
                } else {
                    // 共通化してstopにする
                    subject.setVY(0);
                }
            }
        }
    }

    /**
     * <pre>
        * スプライトとブロックオブジェクトとの衝突判定を行う
        * 現在試験開発中
        * </pre>
        *
        * @param subject ブロックとの衝突判定を行うスプライトオブジェクト
        * @param object  ブロックオブジェクト
        *
        * @return 衝突を初めに検知した位置の座標
        */
    public static Point getCollisionBlock(BaseSprite subject, BaseSprite object) {
        double vx = Math.ceil(subject.getVX());
        double vy = Math.ceil(subject.getVY());
        double originX = subject.getImageLeftX();
        double originY = subject.getImageBaseY();
        double ratio = 0;
        Point point = new Point(-9999, -9999);
        int newX = (int)originX;
        int newY = (int)originY;

        while(ratio <= 1) {

            int tempX = (int)(originX + vx * (ratio+0.1));
            int tempY = (int)(originY + vy * (ratio+0.1));

            if (point.x == -9999 && subject.isCollision(object)) {
                point.x = newX;
            }

            if (point.y == -9999 && subject.isCollision(object)) {
                point.y = newY;
            }

            ratio += 0.1;
            newX = tempX;
            newY = tempY;
        }

        return point;
    }

    /**
        * プレイヤーオブジェクトの移動先がオブジェクトと重なっていた場合その手前に戻す
        *
        * @param player プレイヤーオブジェクト
        * @param sprite プレイヤーと衝突判定を行うオブジェクト
        */
    public static void rewritePoint(Player player, BaseSprite sprite) {

        Rectangle subject = new Rectangle(
                (int)sprite.getEntityLeftX(),
                (int)sprite.getEntityTopY(),
                sprite.getActualWidth(),
                sprite.getActualHeight()
            );
        Rectangle object = new Rectangle(
                (int)player.getEntityLeftX(),
                (int)player.getEntityTopY(),
                player.getActualWidth(),
                player.getActualHeight()
            );

        // 自分の矩形と相手の矩形が重なっているか調べる
        if (subject.intersects(object)) {
            // 下降中
            if(player.getVY() > 0) {
                player.setImageBaseY(sprite.getEntityTopY());
                player.land();
                player.setVY(0);
            }
            /*
            //上昇中
            else if(player.getVY() < 0) {
                player.setBaseY(sprite.getBaseY() + player.getActualHeight());
                player.setVY(0);
            }

            else {

                // 右移動中
                if (player.getVX() > 0) {
                    player.setX(sprite.getLeftX() + sprite.getTransparencyLeft() - player.getActualWidth());
                    player.setVX(0);
                }

                else if (sprite.getVX() < 0) { // 左へ移動中なので左のブロックと衝突
                    // 位置調整
                    player.setX(sprite.getLeftX() + sprite.getActualWidth());
                    player.setVX(0);
                }
            }*/
        }
    }

    /**
     * <pre>
        * プレイヤーオブジェクトとステージオブジェクトが重なっているかを判断する
        * 現在試験開発中
        * </pre>
        *
        * @param object プレーヤーとの重複判定を行うステージオブジェクト
        * @param player プレーヤーオブジェクト
        *
        * @return プレーヤーが重なっているかどうか [true = 重なっている]
        */
    public static boolean isInside(StaticObject object, Player player) {
        if(player.getEntityLeftX() > object.getEntityLeftX()
                && player.getEntityLeftX() + player.getActualWidth() < object.getEntityRightX()
                && player.getEntityBaseY() < object.getEntityBaseY()
                && player.getEntityTopY() > object.getEntityBaseY() - object.getActualHeight()
                ) {
            return true;
        }
        return false;
    }

    /**
     * <pre>
        * プレイヤーオブジェクトがステージオブジェクトの直下にいるか判断する
        * 現在試験開発中
        * </pre>
        *
        * @param object  プレーヤーとの重複判定を行うステージオブジェクト
        * @param player  プレーヤーオブジェクト
        * @param offsetX カメラのX方向のオフセット値
        *
        * @return プレーヤーが直下にいるかどうか [true = 直下にいる]
        */
    public static boolean isUnder(StaticObject object, Player player, int offsetX) {
        int rate = Background.SCROLLRATE_BASE;
        if(player.getEntityLeftX() + offsetX > object.getEntityLeftX() + offsetX/rate
          && player.getEntityLeftX() + offsetX < object.getEntityRightX() + offsetX/rate
          || player.getEntityRightX() + offsetX > object.getEntityLeftX() + offsetX/rate
          && player.getEntityRightX() + offsetX < object.getEntityRightX() + offsetX/rate
          ) {
            return true;
        }
        return false;
    }

    /**
     * int型のランダム値を取得する
     *
        * @param range  ランダム値の範囲
        * @param suffix ランダム範囲のシフト値
        *
        * @return ランダム値
        */
    public static int getRandom(int range, int suffix) {
        return new Random().nextInt(range) + suffix;
    }

    /**
     * <pre>
     * グラフィックスオブジェクトに回転軸を設定する
     * 度数表記に対応
     * </pre>
     *
     * @param degree  回転角度(度数)
        * @param anchorX 回転軸のX座標
        * @param anchorY 回転軸のY座標
        * @param g       グラフィックスオブジェクト
        */
    public static void setRotationDeg(double degree, double anchorX, double anchorY, Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        AffineTransform at = g2.getTransform();
        at.setToRotation(
                // 回転角度
                Math.toRadians(degree),
                // 回転の中心座標
                anchorX,
                anchorY
            );
        g2.setTransform(at);
    }

    /**
     * <pre>
     * グラフィックスオブジェクトに回転軸を設定する
     * ラジアン表記に対応
     * </pre>
     *
     * @param degree  回転角度(ラジアン)
        * @param anchorX 回転軸のX座標
        * @param anchorY 回転軸のY座標
        * @param g       グラフィックスオブジェクト
        */
    public static void setRotationRad(double degree, double anchorX, double anchorY, Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        AffineTransform at = g2.getTransform();
        at.setToRotation(
                // 回転角度
                degree,
                // 回転の中心座標
                anchorX,
                anchorY
            );
        g2.setTransform(at);
    }

    /**
     * グラフィックスオブジェクトに回転軸をリセットする
     *
        * @param g       グラフィックスオブジェクト
        */
    public static void resetRotation(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        AffineTransform at = g2.getTransform();
        at.setToRotation(0);
        g2.setTransform(at);
    }

    /**
     * スプライト同士の衝突時のエネルギーを計算する
     *
     * @param subject 衝突するオブジェクト
        * @param object  衝突されるオブジェクト
        *
        * @return 衝突時のエネルギー
        */
    public static double calculateCrashPower(BaseSprite subject, BaseSprite object) {
        double weightRatio = object.getWeight() / subject.getWeight();

        double speedGap = Math.max(Math.abs(subject.getVX()), Math.abs(object.getVX())) - Math.min(Math.abs(subject.getVX()), Math.abs(object.getVX()));
        double crashPower = speedGap * weightRatio;

        if(object.getVX() < 0) {
            crashPower = -crashPower;
        }

        return crashPower;
    }

    /**
     * sine関数によるマッピングを行う
     *
     * @param time       時間パラメータ
        * @param frequency  周期の間隔
        * @param shift      Y方向の平行移動
        * @param amplitude  Y方向の増幅倍率
        *
        * @return 時間パラメータのマッピング値
        */
    public static double getSinValue(double time, double frequency, double shift, double amplitude) {
        return (Math.sin(time / frequency) + shift) * amplitude;
    }

    /**
     * cosine関数によるマッピングを行う
     *
     * @param time       時間パラメータ
        * @param frequency  周期の間隔
        * @param shift      Y方向の平行移動
        * @param amplitude  Y方向の増幅倍率
        *
        * @return 時間パラメータのマッピング値
        */
    public static double getCosValue(double time, double frequency, double shift, double amplitude) {
        return (Math.cos(time / frequency) + shift) * amplitude;
    }

    /**
     * アニメーション用の画像群をまとめて読み込んで、画像のマップを返す
     *
     * @param frameBundles 可変長引数の画像のファイルパス群
        *
        * @return 読み込んだ画像をアニメーションごとにリスト化したマップ
        */
    public static Map<String, List<BufferedImage>> loadImageFile(ImageResource.FrameBundle[]... frameBundles) {
        List<BufferedImage> imageList;
        HashMap<String, List<BufferedImage>> frameHolder = new HashMap<String, List<BufferedImage>>();
        for(ImageResource.FrameBundle[] bundle: frameBundles) {
            imageList = new ArrayList<BufferedImage>();
            for(int i = 1; i < bundle.length; i++) {
                imageList.add(GeneralUtil.readImage(bundle[i].getValue()));
            }
            frameHolder.put(bundle[0].getValue(), imageList);
        }
        return frameHolder;
    }

    /**
     * 個別のアイコン画像を縦に結合して1つの画像オブジェクトを生成する
     *
     * @param icons  結合する個別のアイコン画像オブジェクトの配列
     * @param width  個別のアイコン画像の幅
     * @param height 個別のアイコン画像の高さ
        *
        * @return 縦に結合した画像オブジェクト
        */
    public static BufferedImage concatIconsVerical(BufferedImage[] icons, int width, int height) {
        // 結合用に、画像オブジェクトを生成
        BufferedImage img = new BufferedImage(width, height * icons.length, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();

        // 結合用画像に個別の画像を書き込む
        for(int i = 0; i < icons.length; i++) {
            g.drawImage(icons[i], 0, height * i, null);
        }

        return img;
    }

    /**
     * 個別のアイコン画像を横に結合して1つの画像オブジェクトを生成する
     *
     * @param icons  結合する個別のアイコン画像オブジェクトの配列
     * @param width  個別のアイコン画像の幅
     * @param height 個別のアイコン画像の高さ
        *
        * @return 横に結合した画像オブジェクト
        */
    public static BufferedImage concatIconsHorizontal(BufferedImage[] icons, int width, int height) {
        // 結合用に、画像オブジェクトを生成
        BufferedImage img = new BufferedImage(width * icons.length, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();

        // 結合用画像に個別の画像を書き込む
        for(int i = 0; i < icons.length; i++) {
            g.drawImage(icons[i], 0, height * i, null);
        }

        return img;
    }

    /**
        * 指定した文字列を指定したフォントの画像に変換しリストを返す
        *
        * @param fonts フォントオブジェクト
        * @param word  指定フォントで表示する文字列
        *
        * @return 文字列を画像に変換したリスト
        */
    public static List<BufferedImage> changeStringToImage(Fonts fonts, String word) {
        List<BufferedImage> letterImageList = new ArrayList<BufferedImage>();
        for(int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i);
            if(letter == ' ') {
                letterImageList.add(null);
            } else {
                letterImageList.add(readImage(fonts.getValue(letter)));
            }
        }

        return letterImageList;
    }

    /**
        * 指定した文字列を指定したフォントの画像に変換しマップを返す
        *
        * @param fonts フォントオブジェクト
        * @param word  指定フォントで表示する文字列
        *
        * @return 文字列を画像に変換したマップ
        */
    public static Map<Character, BufferedImage> stringToImageMap(Fonts fonts, String word) {
        Map<Character, BufferedImage> charImageMap = new HashMap<Character, BufferedImage>();
        for(int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if(!charImageMap.containsKey(c)) {
                if(c ==  ' ') {
                    charImageMap.put(c, null);
                } else {
                    charImageMap.put(c, readImage(fonts.getValue(c)));
                }
            }
        }

        return charImageMap;
    }

    /* ゲーム全般 */

    /**
        * 選択リストに設定された文字列のフォント画像を順に描画する
        *
        * @param optionList 選択リストオブジェクトのリスト
        * @param alignment  描画の基準となる位置(左、中央、右)
        * @param g          Graphicsオブジェクト
        */
    public static void drawSelectOptions(List<StringSelectOption> optionList, int alignment, Graphics g) {
        for(StringSelectOption selectOption: optionList) {
            List<BufferedImage> fontList = selectOption.getImageFont();
            drawImageString(
                    fontList,
                    selectOption.getX(),
                    selectOption.getY(),
                    selectOption.getSize(),
                    alignment,
                    g
                    );
        }
    }

    /**
        * 文字フォント画像を指定位置に描画する
        *
        * @param fontList  フォント画像オブジェクトのリスト
        * @param x         表示位置のX座標
        * @param y         表示位置のY座標
        * @param fontSize  フォントサイズ
        * @param alignment 左、中央、右揃えを指定する数値(ALIGN_LEFT=0, ALIGN_CENTER=1, ALIGN_RIGHT=2)
        * @param g         Graphicsオブジェクト
        */
    public static void drawImageString(
            List<BufferedImage> fontList,
            int x,
            int y,
            int fontSize,
            int alignment,
            Graphics g
            ) {
        int totalSize = fontList.size();
        for(int i = 0; i < fontList.size(); i++) {
              BufferedImage font = fontList.get(i);
              int drawX = x + fontSize*i;
              if(font != null) {
                  switch(alignment) {
                      case ALIGN_CENTER:
                          drawX -= fontSize*totalSize/2;
                          break;
                      case ALIGN_RIGHT:
                          drawX = x - fontSize*(i - fontSize);
                          break;
                  }
                  g.drawImage(
                          font,
                          (int) GameController.getWindow().getAbsPosX(drawX),
                          (int) GameController.getWindow().getAbsPosY(y),
                          (int) GameController.getWindow().getAbsPosX(fontSize),
                          (int) GameController.getWindow().getAbsPosY(fontSize),
                          null
                      );

              }
          }
    }

    /**
        * 文字フォント画像を指定位置に描画する
        *
        * @param fontList  フォント画像オブジェクトのリスト
        * @param x         表示位置のX座標
        * @param y         表示位置のY座標
        * @param fontSize  フォントサイズ
        * @param alignment 文字の左/中央/右寄せ
        * @param degree    文字の回転角度
        * @param g         Graphicsオブジェクト
        */
    public static void drawStringRotated(
            List<BufferedImage> fontList,
            int x,
            int y,
            int fontSize,
            int alignment,
            double degree,
            Graphics g
            ) {

        int totalSize = fontList.size();

        for(int i = 0; i < fontList.size(); i++) {
              BufferedImage font = fontList.get(i);
              double anchorX = x + fontSize*i + fontSize/2;
              double anchorY = y + fontSize/2;
            GeneralUtil.setRotationRad(
                    degree * (i+1),
                    GameController.getWindow().getAbsPosX(anchorX),
                    GameController.getWindow().getAbsPosY(anchorY),
                    g
                    );
              int drawX = x + fontSize*i;

              if(font != null) {
                  switch(alignment) {
                      case ALIGN_CENTER:
                          drawX = x + fontSize*i - fontSize*totalSize/2;
                          break;
                      case ALIGN_RIGHT:
                          drawX = x - fontSize*(i - fontSize);
                          break;
                  }
                  g.drawImage(
                          font,
                          (int) GameController.getWindow().getAbsPosX(drawX),
                          (int) GameController.getWindow().getAbsPosY(y),
                          (int) GameController.getWindow().getAbsPosX(fontSize),
                          (int) GameController.getWindow().getAbsPosY(fontSize),
                          null
                    );

            }
              GeneralUtil.resetRotation(g);
          }
    }

    /**
        * 文字フォント画像を指定位置に描画する
        *
        * @param fontList  フォント画像オブジェクトのリスト
        * @param x         表示位置のX座標
        * @param y         表示位置のY座標
        * @param fontSize  フォントサイズ
        * @param degree    文字の回転角度
        * @param g         Graphicsオブジェクト
        */
    public static void rotateChaotic(
            List<BufferedImage> fontList,
            int x,
            int y,
            int fontSize,
            double degree,
            Graphics g
            ) {

        for(int i = 0; i < fontList.size(); i++) {
              BufferedImage font = fontList.get(i);
              double anchorX = x + fontSize*i + fontSize/2;
              double anchorY = y + fontSize;
              GeneralUtil.setRotationRad(
                      degree * (i+1),
                      anchorX,
                      anchorY,
                      g
                      );
              int drawX = x + fontSize*i;
                  g.drawImage(
                          font,
                          (int) GameController.getWindow().getAbsPosX(drawX),
                          (int) GameController.getWindow().getAbsPosY(y),
                          (int) GameController.getWindow().getAbsPosX(fontSize),
                          (int) GameController.getWindow().getAbsPosY(fontSize),
                          null
                    );
              GeneralUtil.resetRotation(g);
          }
    }

    /**
     * <pre>
        * 動的に変化する文字列に対応するフォント画像を描画する
        * あらかじめ使用するフォント画像のリストを読み込んでから渡す
        * </pre>
        *
        * @param s        表示する文字列
        * @param fontMap  フォント画像のマップ
        * @param posX     描画位置の左端のX座標
        * @param posY     描画位置の上端のY座標
        * @param size     フォントサイズ
        * @param g        Graphicsオブジェクト
        */
    public static void drawDynamicString(
            String s,
            Map<Character, BufferedImage> fontMap,
            int posX,
            int posY,
            int size,
            Graphics g
            ) {
        for(int i = 0; i < s.length(); i++) {
            g.drawImage(
                      fontMap.get(s.charAt(i)),
                      (int) GameController.getWindow().getAbsPosX(posX + size * i),
                      (int) GameController.getWindow().getAbsPosY(posY),
                      (int) GameController.getWindow().getAbsPosX(size),
                      (int) GameController.getWindow().getAbsPosY(size),
                      null
                );
        }
    }

    /**
        * スクリプトの文字列を指定位置に描画する
        *
        * @param s  スクリプトオブジェクト
        * @param x  表示位置のX座標
        * @param y  表示位置のY座標
        * @param g  Graphicsオブジェクト
        */
    public static void drawScript(Script s, int x, int y,  Graphics g) {

        for(int i = 0; i < s.getRenderIndex(); i++) {
            int drawX = x + s.getSize()* (i % s.getCharPerRow());
              int drawY = y + s.getSize()*(int) (i / s.getCharPerRow());

              if(s.getFonts().get(i) != null) {
                  g.drawImage(
                          s.getFonts().get(i),
                          (int) GameController.getWindow().getAbsPosX(drawX),
                          (int) GameController.getWindow().getAbsPosY(drawY),
                          (int) GameController.getWindow().getAbsPosX(s.getSize()),
                          (int) GameController.getWindow().getAbsPosY(s.getSize()),
                          null
                    );
            }
          }
    }

    /**
        * 文字フォント画像を指定位置に描画する
        *
        * @param fontList  フォント画像オブジェクトのリスト
        * @param x         表示位置のX座標
        * @param y         表示位置のY座標
        * @param fontSize  フォントサイズ
        * @param alignment 左、中央、右揃えを指定する数値(ALIGN_LEFT=0, ALIGN_CENTER=1, ALIGN_RIGHT=2)
        * @param g         グラフィックスオブジェクト
        */
    public static void drawStringShiver(
            List<BufferedImage> fontList,
            int x,
            int y,
            int fontSize,
            int alignment,
            Graphics g
            ) {
        Random r = new Random();

        int totalSize = fontList.size();
        for(int i = 0; i < fontList.size(); i++) {
            int randX = r.nextInt(4) - 2;
            int randY = r.nextInt(4) - 2;
            BufferedImage font = fontList.get(i);
              int drawX = x + fontSize*i;
              if(font != null) {
                  switch(alignment) {
                      case ALIGN_CENTER:
                          drawX -= fontSize*totalSize/2;
                          break;
                      case ALIGN_RIGHT:
                          drawX = x - fontSize*(i - fontSize);
                          break;
                  }
                  g.drawImage(
                          font,
                          (int) GameController.getWindow().getAbsPosX(drawX + randX),
                          (int) GameController.getWindow().getAbsPosY(y + randY),
                          (int) GameController.getWindow().getAbsPosX(fontSize),
                          (int) GameController.getWindow().getAbsPosY(fontSize),
                          null
                      );

              }
          }
    }

    /**
     * 覚書きメソッド、特に使用予定はなし
        */
    public static void codeNote() {
/*
        // 床をテクスチャ風にできないか実験
        BufferedImage image = GeneralUtil.readImage(ImageResource.Font.A.getValue());
        int w = 20;
        int h = 20;
        int imgSize = image.getWidth();

        System.out.println("@imgSize = " + imgSize);

        double radius = Math.sqrt(w * w + h * h) / 2;
        double initTheta = Math.acos(w / 2 / radius);
        double addTheta = player.getDirection();

        System.out.println("@radius = " + radius);
        System.out.println("@initTheta = " + initTheta);

        Vector2 P2 = new Vector2(
                Math.cos(initTheta + addTheta),
                Math.sin(initTheta + addTheta)
                ).scalar(radius);

        Vector2 P1 = P2.copy().scalar(-1);

        System.out.println("P1.x = " + (P1.getX() + imgSize / 2));
        System.out.println("P1.y = " + (P1.getY() + imgSize / 2));
        System.out.println("P2.x = " + (P2.getX() + imgSize / 2));
        System.out.println("P2.y = " + (P2.getY() + imgSize / 2));


        g.drawImage(
                image,
                0, 0, 960, 270,
                (int) (P1.getX() + imgSize / 2),
                (int) (P1.getY() + imgSize / 2),
                (int) (P2.getX() + imgSize / 2),
                (int) (P2.getY() + imgSize / 2),
                null);


        g.drawImage(
                image,
                0, 270, 960, 540,
                (int) (P1.getX() + imgSize / 2),
                (int) (P1.getY() + imgSize / 2),
                (int) (P2.getX() + imgSize / 2),
                (int) (P2.getY() + imgSize / 2),
                null);
*/
    }
}
