package classes.scenes.vinyl.utils;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import classes.controllers.GameController;
import classes.math.Vector2;

/**
 * グラフィックスオブジェクトに対して描画を行うメソッドを集約したユーティリティクラス
 *
 * @author  Naoki Yoshikawa
 */
public class DrawUtil {

    /* クォータービューステージ */

    /**
        * クォータービュー上でスプライトに設定された画像を描画する
        *
        * @param sprite 描画対象のスプライトオブジェクト
        * @param origin クオータービュー座標の原点
        * @param basisX クオータービュー座標のX軸ベクトル
        * @param basisY クオータービュー座標のY軸ベクトル
        * @param g      Graphicsオブジェクト
        */
    public static void drawSpriteVector(
            classes.scenes.vinyl.assets.Player sprite,
            Vector2 origin,
            Vector2 basisX,
            Vector2 basisY,
            Graphics g
            ) {
        BufferedImage image = sprite.getImage();
/*
        System.out.println("DrawUtil basisX: " + basisX.mag());
        System.out.println("DrawUtil basisY: " + basisY.mag());
*/
        basisX = basisX.scalar(sprite.getPosVec().getX());
        basisY = basisY.scalar(sprite.getPosVec().getY());
        Vector2 pos = origin.add(basisX.add(basisY));

        if(sprite.getDirection() < 0) {
            g.drawImage(
                    image,
                    (int) GameController.getWindow().getAbsPosX(pos.getX() - sprite.getImageWidth()/2),
                    (int) GameController.getWindow().getAbsPosY(pos.getY() - sprite.getImageHeight()/10 * 9),
                    (int) GameController.getWindow().getAbsPosX(pos.getX() + sprite.getImageWidth()/2),
                    (int) GameController.getWindow().getAbsPosY(pos.getY() + sprite.getImageHeight()/10),
                    image.getWidth(),
                    0,
                    0,
                    image.getHeight(),
                    null
                );
        } else {
            g.drawImage(
                    image,
                    (int) GameController.getWindow().getAbsPosX(pos.getX() - sprite.getImageWidth()/2),
                    (int) GameController.getWindow().getAbsPosY(pos.getY() - sprite.getImageHeight()/10 * 9),
                    (int) GameController.getWindow().getAbsPosX(sprite.getImageWidth()),
                    (int) GameController.getWindow().getAbsPosY(sprite.getImageHeight()),
                    null
                );
        }

/*
        g.setColor(Color.RED);
        g.drawOval(
                (int) GameController.getWindow().getAbsPosX(pos.getX()),
                (int) GameController.getWindow().getAbsPosY(pos.getY()),
                5,
                5
                );
*/

    }
}
