package classes.controllers;

import java.util.ArrayList;
import java.util.List;

import interfaces.GameScene;

/**
 * <pre>
 * シーン遷移時の画面エフェクトの管理クラス
 * リストにエフェクトオブジェクトを登録すると順に実行する
 * </pre>
 *
 * @author Naoki Yoshikawa
 */
public class EffectController  {

    // 画面エフェクトシーンオブジェクトのリスト
    private List<GameScene> effects;

    // 現在のシーンを表すインデックス
    private int index;

    // 画面遷移を行う場合の遷移先シーンオブジェクト
    private GameScene scene;

    /**
     * エフェクトシーンリストの初期化
     */
    public EffectController() {
        effects = new ArrayList<GameScene>();
        index = 0;
    }

    /**
     * エフェクトシーンリストの初期化、遷移先シーンの設定
     *
     * @param scene 遷移先シーンオブジェクト
     */
    public EffectController(GameScene scene) {
        this();
        this.scene = scene;
    }

    /**
     * 設定された遷移先のシーンオブジェクトを返す
     *
     * @return シーンオブジェクト
     */
    public GameScene getScene() {
        return this.scene;
    }

    /**
     * 遷移先のシーンオブジェクトを設定する
     *
     * @param scene シーンオブジェクト
     */
    public void setScene(GameScene scene) {
        this.scene = scene;
    }

    /**
     * エフェクトオブジェクトをリストに登録する
     *
     * @param effect エフェクトオブジェクト
     */
    public void addEffect(GameScene effect) {
        effects.add(effect);
    }

    public void initEffect() {
        index = 0;
        for(GameScene effect: effects) {
            effect.initParam();
        }
    }

    /**
     * <pre>
     * 遷移エフェクトが残っているかどうかを返す
     * 残ってる場合 true
     * </pre>
     *
     * @return 遷移エフェクトが残っているかどうか
     */
    public boolean isRemained() {
        return this.effects.size() > index;
    }

    /**
     * 設定されたエフェクトシーンを順次実行する
     */
    public void process() {
        WindowController w = GameController.getWindow();
        if (isRemained()) {
            w.pushScene(effects.get(index));
            index++;
        }
    }
}
