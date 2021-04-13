package classes.scenes.old.utils;

import classes.controllers.GameController;
import classes.controllers.MapController;
import classes.scenes.old.assets.BaseSprite;
import classes.scenes.old.assets.Car;
import classes.scenes.old.assets.Enemy;
import classes.scenes.old.assets.Player;
import classes.scenes.old.assets.StaticObject;
import classes.scenes.old.assets.VoiceIcon;
import classes.utils.GeneralUtil;
import interfaces.Calculation;

/**
 * パラメータの更新に関するメソッドを集約したユーティリティクラス
 *
 * @author  Naoki Yoshikawa
 */
public class UpdateUtil {

    /**
     * アクションステージにおいて音声弾を更新する
     *
     * @param voice 音声弾スプライト
     */
    public static void updateVoiceIcon(VoiceIcon voice) {
        double newX = voice.getImageLeftX() + voice.getVX();
        if(!(voice.getVX() > 0
                && voice.getFirstX() + VoiceIcon.RANGE < newX)
                && !(voice.getVX() < 0 && voice.getFirstX() - VoiceIcon.RANGE > newX)
                ) {
            voice.setImageLeftX(newX);
        }
    }

    /**
     * アクションステージにおいて自動車スプライトを更新する
     *
     * @param car    自動車スプライト
     * @param target 更新対象のゲームシーンオブジェクト
     */
    public static void updateCar(Car car, Calculation target) {

        MapController map = target.getMap();
        if(car.getIsRight()) {
            car.setImageLeftX(car.getImageLeftX() + car.getVX());
            if(car.getEntityLeftX() > map.getWidth()) {
                car.setImageLeftX(0);
                car.setVX(GeneralUtil.getRandom(12, 4));
                car.setImage(GeneralUtil.changeRandomColor(car.getImage(), car.getPixelList()));
            }
        } else {
            car.setImageLeftX(car.getEntityLeftX() + car.getVX());
            if(car.getEntityRightX() < 0) {
                car.setImageLeftX(map.getWidth());
                car.setVX(-GeneralUtil.getRandom(12, 4));
                car.setImage(GeneralUtil.changeRandomColor(car.getImage(), car.getPixelList()));
            }
        }
    }

    /**
     * アクションステージにおいてプレイヤースプライトを更新する
     *
     * @param target 更新対象のゲームシーンオブジェクト
     * @param dt     デルタタイム
     */
    public static void updatePlayer(Calculation target, double dt) {

        Player player = target.getPlayer();
        MapController map = target.getMap();
        player.setVY(player.getVY() + target.getGravity());
        player.animate(dt);

        if(player.getOnGround()) {
            player.setVX(player.getVX() * player.getDashRate());
        }

        GeneralUtil.getCollision(player, map);

        // とりあえずこれで、ただ無駄が多いので最適化する
        for(BaseSprite sprite: target.getSpriteList()) {
            if(sprite instanceof VoiceIcon) {
                GeneralUtil.rewritePoint(player, sprite);
            }
        }
    }

    /**
     * 汎用的なスプライトの更新メソッド
     *
     * @param object 更新対象のスプライトオブジェクト
     * @param target 更新対象のゲームシーンオブジェクト
     */
    public static void updateBaseSprite(BaseSprite object, Calculation target) {
        MapController map = target.getMap();
        object.setVY(object.getVY() + target.getGravity());
        GeneralUtil.getCollision(object, map);
    }

    /**
     * 汎用的なアニメーションを行うスプライトの更新メソッド
     *
     * @param object 更新対象のスプライトオブジェクト
     * @param target 更新対象のゲームシーンオブジェクト
     */
    public static void updateAnimatedObject(BaseSprite object, Calculation target) {
        updateBaseSprite(object, target);
    }

    /**
     * スプライトを画面外に退場させる
     *
     * @param object  更新対象のスプライトオブジェクト
     * @param gravity 重力値
     * @param vx      X方向の速度
     */
    private static void out(BaseSprite object, double gravity, double vx) {
        object.setVY(object.getVY() + gravity);
        object.setImageLeftX(object.getImageLeftX() + vx);
        object.setImageBaseY(object.getImageBaseY() + object.getVY());
    }

    /**
     * プレイヤーを画面外に退場させる
     *
     * @param player 更新対象のプレイヤーオブジェクト
     * @param target 更新対象のゲームシーンオブジェクト
     */
    public static void outPlayer(Player player, Calculation target) {
        // いずれは衝突物同士の重量やスピードで動的に吹っ飛ぶ距離を演算する
        double vx = player.getBlownX();
        out(player, target.getGravity(), vx);
        // ここは本来地面のheight, このままだと地面をマップ下辺より高くした時にバグる
        if(player.getImageTopY() < target.getMap().getHeight()) {
            player.setDeadX(player.getImageLeftX());
            player.setDeadY(player.getImageTopY());
        }
    }

    /**
     * 敵スプライトを画面外に退場させる
     *
     * @param enemy  更新対象の敵スプライトオブジェクト
     * @param target 更新対象のゲームシーンオブジェクト
     */
    public static void outEnemy(Enemy enemy, Calculation target) {
        double vx = enemy.getBlownX();
        out(enemy, target.getGravity(), vx);
    }

    /**
     * ステージオブジェクトを更新する
     *
     * @param object 更新対象のステージオブジェクト
     * @param target 更新対象のゲームシーンオブジェクト
     */
    public static void updateStageObject(StaticObject object, Calculation target) {
        Player player = target.getPlayer();
        MapController map = target.getMap();
        int offsetX = GeneralUtil.getCameraOffset(
                (int) player.getImageLeftX(),
                (int) GameController.getWindow().getWindowWidth(),
                map.getWidth()
                );
        /*
        if(object.getObjectType().equals(ImageResource.StageObject.SUN.toString())) {
            if(GeneralUtil.isUnder(object, player, offsetX) && object.getEntityTopY() < target.getMap().getHeight() + 100) {
                object.setImageBaseY(object.getImageBaseY() + 20);
            } else if(object.getImageBaseY() > 150){
                object.setImageBaseY(object.getImageBaseY() + -15);
            }
        } else {*/
            // draw時点でplayerインスタンスがないため、booleanに退避しておく
            object.setIsLayered(GeneralUtil.isInside(object, player));
        //}
    }

    /**
     * ブロックオブジェクトがプレーヤーに叩かれた時のリアクションを実行する
     *
     * @param object ブロックオブジェクト
     */
    public static void blockReaction(StaticObject object) {
        object.setWidthRatio(object.getWidthRatio() + 0.1);
    }

}
