package interfaces;

import java.util.LinkedList;

import classes.controllers.KeyController;
import classes.controllers.MapController;
import classes.scenes.old.assets.BaseSprite;
import classes.scenes.old.assets.Player;
import classes.scenes.old.assets.StaticObject;

/**
 * アクションステージオブジェクトを規定するインターフェース
 * スプライト、ステージオブジェクトの設定
 * 重力等の必須パラメータの設定
 * マップ情報、キーコンフィグの設定
 *
 * @author Naoki Yoshikawa
 */
public interface Calculation {
    /**
     * アクションステージで使用するスプライトのリストを返す
     *
     * @return スプライトのリスト
     */
    LinkedList<BaseSprite> getSpriteList();

    /**
     * Zインデックスの最前面に表示するステージオブジェクトのリストを返す
     *
     * @return ステージオブジェクトのリスト
     */
    LinkedList<StaticObject> getFrontObjectList();

    /**
     * Zインデックスの最背面に表示するステージオブジェクトのリストを返す
     *
     * @return ステージオブジェクトのリスト
     */
    LinkedList<StaticObject> getBackObjectList();

    /**
     * ステージの重力値を返す
     *
     * @return 重力値
     */
    double getGravity();

    /**
     * プレイヤーオブジェクトを返す
     *
     * @return プレイヤーオブジェクト
     */
    Player getPlayer();

    /**
     * 地形情報の管理オブジェクトを返す
     *
     * @return 地形情報の管理オブジェクト
     */
    MapController getMap();

    /**
     * キーコンフィグオブジェクトを返す
     *
     * @return キーコンフィグオブジェクト
     */
    KeyController getKeyConfig();
}
