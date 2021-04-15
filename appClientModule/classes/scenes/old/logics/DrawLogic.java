package classes.scenes.old.logics;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import classes.containers.Background;
import classes.controllers.GameController;
import classes.controllers.MapController;
import classes.scenes.old.utils.DrawUtil;
import classes.utils.GeneralUtil;
import classes.scenes.old.assets.Car;
import classes.scenes.old.assets.Enemy;
import classes.scenes.old.assets.Player;
import classes.scenes.old.assets.StaticObject;
import classes.scenes.old.assets.VoiceIcon;
import classes.scenes.old.assets.Ball;
import classes.scenes.old.assets.BaseSprite;
import interfaces.Calculation;

/**
 * アクションステージのグラフィック描画メソッドの呼び出しを集約した中間クラス
 *
 * @author  Naoki Yoshikawa
 */
public class DrawLogic {

    /**
     * シーンオブジェクトに設定されたオブジェクトに対し、順番に描画メソッドを呼び出す
     *
     * @param target シーンオブジェクト(Stage1, Titleなど)
     * @param g      グラフィックスオブジェクト
     */
    public static void drawObjectLogic(Calculation target, Graphics g) {

        Player player = target.getPlayer();
        MapController map = target.getMap();

        // X方向のオフセットを計算
        int offsetX = GeneralUtil.getCameraOffset(
                (int) player.getImageLeftX(),
                (int) GameController.getWindow().getWindowWidth(),
                map.getWidth()
                );
        // Y方向のオフセットを計算
        int offsetY = GeneralUtil.getCameraOffset(
                (int) player.getImageBaseY(),
                (int) GameController.getWindow().getWindowHeight(),
                map.getHeight()
                );

        // マップを描画
        DrawUtil.drawMap(map, offsetX, offsetY, g);

        // ステージオブジェクトを描画
        for (StaticObject object: target.getBackObjectList()) {
            StaticObject backObject = (StaticObject) object;
            DrawUtil.drawSprite(backObject, offsetX, offsetY, g);
        }

        // プレイヤーを描画
        // 空中にいる間は回転させる
        if(!player.getOnGround()) {

            // プレイヤーの向きで描画をY軸対称に反転させる
            if(player.getDirectionX() == BaseSprite.RIGHT) {
                DrawUtil.drawRotated(player, offsetX, offsetY, g);
            } else {
                //UTIL_DrawUtil.drawPlayerJumpVerticalFlip(player, offsetX, offsetY, g);
                DrawUtil.drawRotatedVerticalFlip(player, offsetX, offsetY, g);
            }

        // 敵にぶつかるまでと、ぶつかって幽霊になってから
        // isAliveとisSpookはややこしいが、ぶつかってからお化けになるまでの微妙な期間がある
        }  else if(player.isAlive() || player.isSpook()) {

            // プレイヤーの向きで描画をY軸対称に反転させる
            if(player.getDirectionX() == BaseSprite.RIGHT) {
                DrawUtil.drawSprite(player, offsetX, offsetY, g);
            } else {
                DrawUtil.drawVerticalFlip(player, offsetX, offsetY, g);
            }

        // 敵にぶつかってから幽霊になるまで
        } else {
            DrawUtil.drawPlayerDying(player, offsetX, offsetY, g);
        }

        // 死体を描写する
        if(player.isSpook()) {
            DrawUtil.drawDeadBody(
                player,
                offsetX,
                offsetY,
                player.getFrameHolder().get(player.getDeadImage()).get(0),
                g
            );
        }

        // 車スプライトは進行方向ごとにまとめて描画する
        List<Car> leftCarList = new ArrayList<Car>();

        // プレイヤー以外のスプライトの描写
        for (BaseSprite sprite: target.getSpriteList()) {

            // 敵スプライト
            if(sprite instanceof Enemy) {
                Enemy enemy = (Enemy) sprite;

                // プレイヤーに踏まれていない場合
                if(enemy.isAlive()) {

                    // 進行方向に合わせてY軸対称で反転描画する
                    if(enemy.getDirectionX() == BaseSprite.RIGHT) {
                        DrawUtil.drawSprite(enemy, offsetX, offsetY, g);
                    } else {
                        DrawUtil.drawVerticalFlip(enemy, offsetX, offsetY, g);
                    }

                // プレイヤーに踏まれた場合
                } else {
                    DrawUtil.drawRotated(enemy, offsetX, offsetY, g);
                }

            // 音声弾
            } else if(sprite instanceof VoiceIcon) {
                VoiceIcon voice = (VoiceIcon) sprite;
                DrawUtil.drawSprite(voice, offsetX, offsetY, g);

            // 自動車
            } else if(sprite instanceof Car) {
                Car car = (Car) sprite;

                // 右向きの車は奥車線なので先に描画する
                if(car.isRight()) {
                    DrawUtil.drawSprite(car, offsetX, offsetY, g);

                // 左向きの車は手前車線なので後から描画するため一旦リストに入れておく
                } else {
                    leftCarList.add(car);
                }

            // ボール
            } else if(sprite instanceof Ball) {
                Ball ball = (Ball) sprite;
                DrawUtil.drawRotated(ball, offsetX, offsetY, g);
            }
        }

        // 左側走行を再現
        for(Car car: leftCarList) {
            DrawUtil.drawVerticalFlip(car, offsetX, offsetY, g);
        }

        // 手前に表示するオブジェクトリストを描画
        for (BaseSprite asset: target.getFrontObjectList()) {
            if(asset instanceof StaticObject) {
                StaticObject stageObject = (StaticObject)asset;
                DrawUtil.drawSprite(stageObject, offsetX, offsetY, g);
            }
        }
    }

    /**
     * 背景の描画メソッドを呼び出す
     *
     * @param target         シーンオブジェクト(Stage1, Titleなど)
     * @param backgroundList 重ねて表示する背景画像のリスト
     * @param g              グラフィックスオブジェクト
     */
    public static void paintBackgroundLogic(Calculation target, List<Background> backgroundList, Graphics g) {;
        DrawUtil.drawBackground(target, backgroundList, g);
    }

    /**
     * ゴール時のアニメーション描画メソッドを呼び出す
     *
     * @param target シーンオブジェクト(Stage1, Titleなど)
     * @param startX キャラクターのアニメーション開始位置のX座標
     * @param g      グラフィックスオブジェクト
     */
    public static void paintGoalLogic(Calculation target, double startX, Graphics g) {
        DrawUtil.drawGoalMotion(target, startX, g);
    }

    /**
     * ワールドマップとステージ間のアイキャッチのアニメーション描画メソッドを呼び出す
     *
     * @param player アニメーションに使用するプレイヤースプライトオブジェクト
     * @param x      プレイヤーを描画するX座標
     * @param g      グラフィックスオブジェクト
     */
    public static void drawEyeCatchLogic(Player player, int x, Graphics g) {
        player.run(BaseSprite.RIGHT, 1);
        DrawUtil.drawEyeCatchMotion(player, x, g);
    }
}
