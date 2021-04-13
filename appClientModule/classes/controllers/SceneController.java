package classes.controllers;

import classes.scenes.Pause;
import classes.scenes.Title;
import classes.scenes.WorldMap;
import classes.scenes.action.ActionStage;
import classes.scenes.slot.SlotHall;
import classes.scenes.slot.SlotStage;
import classes.scenes.vinyl.Turntable;
import classes.scenes.vinyl.VinylShop;
import interfaces.GameScene;

/**
 * シーンインスタンスのロード、初期化を管理する
 *
 * @author Naoki Yoshikawa
 */
public class SceneController {

    private GameScene[] scenes;
    public static final int TITLE = 0;
    public static final int WORLD = 1;
    public static final int SLOT = 2;
    public static final int HALL = 3;
    public static final int TURNTABLE = 4;
    public static final int VINYL = 5;
    public static final int PAUSE = 6;
    public static final int ACTION = 7;

    /**
     * シーンのインスタンスを管理する
     */
    public SceneController() {
        scenes = new GameScene[8];
        scenes[TITLE] = new Title();
        scenes[WORLD] = new WorldMap();
        scenes[SLOT] = new SlotStage();
        scenes[HALL] = new SlotHall();
        scenes[TURNTABLE] = new Turntable();
        scenes[VINYL] = new VinylShop();
        scenes[PAUSE] = new Pause();
        scenes[ACTION] = new ActionStage();
    }

    public void initEffect() {
        SlotHall sh = (SlotHall) scenes[HALL];
        sh.setEffect();

        VinylShop vs = (VinylShop) scenes[VINYL];
        vs.setEffect();
    }

    public GameScene getScene(int index) {
        scenes[index].initParam();
        return scenes[index];
    }

}
