package classes.scenes.old.logics;

import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import classes.constants.ImageResource;
import classes.controllers.GameController;
import classes.controllers.KeyController;
import classes.controllers.MidiController;
import classes.controllers.SceneController;
import classes.controllers.MapController;
import classes.scenes.action.assets.*;
import classes.scenes.old.GameOver;
import classes.scenes.old.Goal;
import classes.scenes.old.Stage2;
import classes.scenes.old.assets.Ball;
import classes.scenes.old.assets.BaseSprite;
import classes.scenes.old.assets.Car;
import classes.scenes.old.assets.Enemy;
import classes.scenes.old.assets.Player;
import classes.scenes.old.assets.StaticObject;
import classes.scenes.old.assets.VoiceIcon;
import classes.utils.GeneralUtil;
import classes.scenes.old.utils.UpdateUtil;

import interfaces.Calculation;

/**
 * アクションステージのパラメータ更新メソッドの呼び出しを集約した中間クラス
 *
 * @author  Naoki Yoshikawa
 */
public class UpdateLogic {

    /**
     * プレイヤーのパラメータをフレームごとに更新する
     *
     * @param target 更新対象のステージオブジェクト
     * @param window ゲームウィンドウオブジェクト
     * @param dt     デルタタイム
     */
    private static void playerLogic(Calculation target, double dt) {

        Map<Integer, KeyController.Key> keys = target.getKeyConfig().getKeys();
        Player player = target.getPlayer();

        // Stage2はMidiコントローラで操作する
        if (target instanceof Stage2) {

            Stage2 s2 = (Stage2) target;

            if (s2.getMidiController().hasMidiConnection()) {
                // コード進行 C=0, B=1, ..., D=10, C#=11 としてトランスポーズする

                // コードの種類によって3rd, 5th, 7thの対象を分岐させる
                MidiController.Midi midi3rd = null;
                MidiController.Midi midi5th = null;
                MidiController.Midi midi7th = null;

                switch(s2.getDiatonic()) {
                    case Stage2.MAJOR7:
                        midi3rd = s2.getMidiConfig().get(MidiController.E);
                        midi5th = s2.getMidiConfig().get(MidiController.G);
                        midi7th = s2.getMidiConfig().get(MidiController.B);
                        break;
                    case Stage2.MINOR7:
                        midi3rd = s2.getMidiConfig().get(MidiController.D_SHARP);
                        midi5th = s2.getMidiConfig().get(MidiController.G);
                        midi7th = s2.getMidiConfig().get(MidiController.A_SHARP);
                        break;
                    case Stage2.SEVENTH:
                        midi3rd = s2.getMidiConfig().get(MidiController.E);
                        midi5th = s2.getMidiConfig().get(MidiController.G);
                        midi7th = s2.getMidiConfig().get(MidiController.A_SHARP);
                        break;
                    case Stage2.MINOR7_F5:
                        midi3rd = s2.getMidiConfig().get(MidiController.D_SHARP);
                        midi5th = s2.getMidiConfig().get(MidiController.F_SHARP);
                        midi7th = s2.getMidiConfig().get(MidiController.A_SHARP);
                        break;
                }

                // 1st
                if(s2.getMidiConfig().get(MidiController.C).isPressed()) {
                    player.run(BaseSprite.RIGHT, dt);
                // 5th
                } else if(midi5th.isPressed()) {
                    player.run(BaseSprite.LEFT, dt);
                } else {
                    player.stop();
                }

                if(midi3rd.isPressed()) {
                    player.jump();
                }

            } else {

                // コントローラからのキーイベントを処理
                if(keys.get(KeyEvent.VK_SHIFT).isPressed()) {
                    player.dash();
                } else {
                    player.walk();
                }

                if (keys.get(KeyEvent.VK_LEFT).isPressed()) {
                    player.run(BaseSprite.LEFT, dt);
                } else if (keys.get(KeyEvent.VK_RIGHT).isPressed()) {
                    player.run(BaseSprite.RIGHT, dt);
                } else {
                    player.stop();
                }

                if (keys.get(KeyEvent.VK_Z).isPressed()) {
                    player.jump();
                }

                if (keys.get(KeyEvent.VK_DOWN).isPressed()) {
                    player.duck();
                }

                if(keys.get(KeyEvent.VK_SPACE).isPressed()) {
                    player.createNewBlock(target.getSpriteList());
                }

                if(!player.getOnGround()) {
                    player.rotate();
                }
            }

        }

        if(keys.get(KeyEvent.VK_ESCAPE).isPressed()) {
            GameController.getWindow().pushScene(GameController.getScene(SceneController.PAUSE));
            target.getKeyConfig().releaseAll();
        }

        // プレイヤーの状態を更新
        UpdateUtil.updatePlayer(target, dt);
    }

    /**
     * プレイヤー以外のスプライトのパラメータをフレームごとに更新する
     *
     * @param target 更新対象のステージオブジェクト
     * @param dt     デルタタイム
     */
    private static void activeSpriteLogic(Calculation target, double dt) {

        // マップにいるスプライトを取得
        List<BaseSprite> sprites = target.getSpriteList();
        Iterator<BaseSprite> iterator = sprites.iterator();
        Player player = target.getPlayer();
        MapController map = target.getMap();

        // プレイヤー以外のスプライトの演算
        while (iterator.hasNext()) {
            BaseSprite sprite = (BaseSprite)iterator.next();

            // 敵スプライトの処理
            if (sprite instanceof Enemy) {
                Enemy enemy = (Enemy)sprite;

                // 敵が踏まれていない時
                if(enemy.getIsAlive()) {

                    // プレイヤーと敵の衝突判定を行う
                    if (player.isCollision(enemy)) {
                        double playerEnergy = GeneralUtil.calculateCrashPower(player, enemy);
                        double enemyEnergy = GeneralUtil.calculateCrashPower(enemy, player);

                        // 矩形の底辺のY座標を比較して、踏めているかどうかを判断する
                        if ((int)player.getEntityBaseY() < (int)enemy.getEntityBaseY()) {
                            enemy.die(enemyEnergy, -6);
                            player.enemyJump();

                        // 踏めていなければプレイヤーは死亡する
                        } else {
                            player.die(playerEnergy, 2);
                            enemy.die(enemyEnergy, -12);
                        }
                    }

                    // 敵スプライトの位置によって向きを決定し、自動走行させる
                    if(enemy.getImageRightX() >= map.getWidth() && enemy.getDirectionX() == BaseSprite.RIGHT) {
                        enemy.run(BaseSprite.LEFT, dt);
                    } else if(enemy.getImageLeftX() <= 0 && enemy.getDirectionX() == BaseSprite.LEFT){
                        enemy.run(BaseSprite.RIGHT, dt);
                    } else {
                        enemy.run(enemy.getDirectionX(), dt);
                    }

                    // 敵スプライトの状態を更新
                    UpdateUtil.updateAnimatedObject(enemy, target);
                    enemy.animate(dt);

                // 敵が踏まれた後
                } else {
                    // 回転しながら画面外へ退場する
                    enemy.rotate();
                    UpdateUtil.outEnemy(enemy, target);

                    // 画面外へ出たらリスポーン
                    if(enemy.getImageTopY() > map.getHeight()) {
                        enemy.respawn(map.getHeight());
                    }
                }

            // 音声弾の処理
            } else if (sprite instanceof VoiceIcon) {
                VoiceIcon voice = (VoiceIcon)sprite;
                voice.decrease(dt);
                // 出現可能時間を消化しきった後消滅する
                if(voice.getLife() < 0) {
                    iterator.remove();
                    continue;
                }

                // プレイヤーが音声弾に乗っているかの判定
                GeneralUtil.rewritePoint(player, sprite);

                // 音声弾の状態を更新
                UpdateUtil.updateVoiceIcon(voice);

            // アクションステージを走行する車スプライトの処理
            } else if(sprite instanceof Car) {
                Car car = (Car) sprite;
                UpdateUtil.updateCar(car, target);

            // アクションステージのボールスプライトの処理
            } else if(sprite instanceof Ball) {
                Ball ball = (Ball) sprite;

                // 動力を持っている場合バウンドで減衰させる
                if(ball.getIsActive()) {
                    ball.amortize();

                // プレイヤーがボールに衝突すると、動力を得る
                } else {
                    if (player.isCollision(ball)) {
                        double ballEnergy = GeneralUtil.calculateCrashPower(ball, player);
                        ball.blown(ballEnergy, -10 + player.getVY());
                        ball.collided();
                    }
                }

                // 空中で回転させる
                if(!ball.getOnGround()) {
                    ball.rotate();
                }

                // ボールのバウンドを演算する
                ball.blown2();

                // ボールの状態を更新
                UpdateUtil.updateBaseSprite(ball, target);
            }
        }
    }

    /**
     * ステージオブジェクトとプレイヤーの衝突時の処理を行う
     *
     * @param target 更新対象のステージオブジェクト
     * @param window ゲームウィンドウオブジェクト
     * @param dt     デルタタイム
     */
    private static void activeObjectLogic(Calculation target, double dt) {
        List<StaticObject> assets = target.getFrontObjectList();
        Iterator<StaticObject> iterator = assets.iterator();

        // ステージオブジェクトは一括でここで処理する
        while (iterator.hasNext()) {
            StaticObject object = iterator.next();

            // ゴールオブジェクトに触れた場合、ゴールシーンへ移行する
            if(object.getObjectType().equals(ImageResource.StageObject.GOAL.toString()) && object.getIsLayered()) {
                GameController.getWindow().pushScene(new Goal());

            // 各ブロックとプレーヤーとの衝突判定
            } else if(object.getObjectType().equals(ImageResource.StageObject.BLOCK_C.toString())
                    || object.getObjectType().equals(ImageResource.StageObject.BLOCK_D.toString())
                    || object.getObjectType().equals(ImageResource.StageObject.BLOCK_E.toString())
                    || object.getObjectType().equals(ImageResource.StageObject.BLOCK_F.toString())
                    || object.getObjectType().equals(ImageResource.StageObject.BLOCK_G.toString())
                    || object.getObjectType().equals(ImageResource.StageObject.BLOCK_A.toString())
                    || object.getObjectType().equals(ImageResource.StageObject.BLOCK_B.toString())
                    || object.getObjectType().equals(ImageResource.StageObject.BLOCK_STOP.toString())
                    || object.getObjectType().equals(ImageResource.StageObject.BLOCK_BEAT.toString())
                    ){

                // Todo: 試験的に衝突方向による演算の分岐を実装
                if(target.getPlayer().isCollision(object)) {

                    if(target.getPlayer().getVY() < 0 && !object.getIsInMotion()){
                        object.strucken();
                        if(object.getObjectType().equals(ImageResource.StageObject.BLOCK_C.toString()) ){
                            //CONTROLLER_Sound.loopClipPeriod(CONSTANT_Sound.SE_C);
                        } else if ( object.getObjectType().equals(ImageResource.StageObject.BLOCK_D.toString())) {
                            //CONTROLLER_Sound.loopClipPeriod(CONSTANT_Sound.SE_D);
                        } else if ( object.getObjectType().equals(ImageResource.StageObject.BLOCK_E.toString())){
                            //CONTROLLER_Sound.loopClipPeriod(CONSTANT_Sound.SE_E);
                        } else if ( object.getObjectType().equals(ImageResource.StageObject.BLOCK_F.toString())) {
                            //CONTROLLER_Sound.loopClipPeriod(CONSTANT_Sound.SE_F);
                        } else if ( object.getObjectType().equals(ImageResource.StageObject.BLOCK_G.toString())) {
                            //CONTROLLER_Sound.loopClipPeriod(CONSTANT_Sound.SE_G);
                        } else if ( object.getObjectType().equals(ImageResource.StageObject.BLOCK_A.toString())) {
                            //CONTROLLER_Sound.loopClipPeriod(CONSTANT_Sound.SE_A);
                        } else if ( object.getObjectType().equals(ImageResource.StageObject.BLOCK_B.toString())) {
                            //CONTROLLER_Sound.loopClipPeriod(CONSTANT_Sound.SE_B);
                        } else if ( object.getObjectType().equals(ImageResource.StageObject.BLOCK_STOP.toString())) {
                            //CONTROLLER_Sound.stopLoopClip();
                        } else if ( object.getObjectType().equals(ImageResource.StageObject.BLOCK_BEAT.toString())) {
                            //CONTROLLER_Sound.loopClipPeriod(CONSTANT_Sound.SE_BEAT);
                        }
                    }
                    // プレイヤーの頭打ち処理
                    // とりあえず動き優先で作ったのであとで最適化する
                    GeneralUtil.getBlock(target.getPlayer(), object);
                }

                // プレーヤーが活性化したブロックは膨らむアニメーションに入る
                if(object.getIsInMotion()) {
                    object.swell();
                }
            }

            // ブロックの状態を更新
            UpdateUtil.updateStageObject(object, target);
        }
    }

    /**
     * <pre>
     * ムービーシーンなどで、当たり判定を無視してスプライトの更新のみ行う
     * 更新対象はactiveSpriteLogicと同じ
     * @see activeSpriteLogic
     * </pre>
     *
     * @param target 更新対象のステージオブジェクト
     * @param dt     デルタタイム
     */
    private static void inactiveSpriteLogic(Calculation target, double dt) {
        List<BaseSprite> sprites = target.getSpriteList();
        Iterator<BaseSprite> iterator = sprites.iterator();
        MapController map = target.getMap();
        while (iterator.hasNext()) {
            BaseSprite sprite = (BaseSprite)iterator.next();

            // 敵スプライトの処理
            if(sprite instanceof Enemy) {
                Enemy enemy = (Enemy) sprite;

                // 敵スプライトの位置によって向きを決定し、自動走行させる
                if(enemy.getImageRightX() >= map.getWidth()    && enemy.getDirectionX() == BaseSprite.RIGHT) {
                    enemy.run(BaseSprite.LEFT, dt);
                } else if(enemy.getImageLeftX() <= 0 && enemy.getDirectionX() == BaseSprite.LEFT){
                    enemy.run(BaseSprite.RIGHT, dt);
                } else {
                    enemy.run(enemy.getDirectionX(), dt);
                    enemy.jump();
                }

                // 敵スプライトの状態を更新
                UpdateUtil.updateAnimatedObject(enemy, target);
                enemy.animate(dt);

            // 音声弾の処理
            } else if(sprite instanceof VoiceIcon) {
                VoiceIcon voice = (VoiceIcon) sprite;
                UpdateUtil.updateVoiceIcon(voice);

            // アクションステージを走行する車スプライトの処理
            } else if(sprite instanceof Car) {
                Car car = (Car) sprite;
                UpdateUtil.updateCar(car, target);

            // アクションステージのボールスプライトの処理
            } else if(sprite instanceof Ball) {
                Ball ball = (Ball) sprite;
                UpdateUtil.updateBaseSprite(ball, target);
            }
        }
    }

    /**
     * <pre>
     * ムービーシーンなどで、当たり判定を無視してステージオブジェクトの更新のみ行う
     * 更新対象はactiveObjectLogicと同じ
     * @see activeObjectLogic
     * </pre>
     *
     * @param target 更新対象のステージオブジェクト
     * @param dt     デルタタイム
     */
    private static void inactiveObjectLogic(Calculation target, double dt) {
        List<StaticObject> assets = target.getFrontObjectList();
        Iterator<StaticObject> iterator = assets.iterator();

        // ステージオブジェクトごとに状態を更新
        while (iterator.hasNext()) {
            StaticObject object = iterator.next();
            UpdateUtil.updateStageObject(object, target);
        }
    }

    /**
     * アクションステージでの更新用privateメソッドを選択し集約するインタフェースメソッド
     *
     * @param target 更新対象のステージオブジェクト
     * @param dt     デルタタイム
     */
    public static void basicLogic(Calculation target, double dt) {
        Player player = target.getPlayer();
        MapController map = target.getMap();

        // プレイヤーの生存時とゲームオーバー時で更新ロジックを切り替える
        if(player.getIsAlive()) {
            playerLogic(target, dt);
            activeSpriteLogic(target, dt);
            activeObjectLogic(target, dt);
        } else {
            jumpAwayLogic(target, dt);
            inactiveSpriteLogic(target, dt);
            inactiveObjectLogic(target, dt);

            // 画面外にプレイヤーが消えた時点でゲームオーバーシーンを呼び出す
            if(player.getImageBaseY() > map.getHeight()) {
                GameController.getWindow().pushScene(new GameOver());
            }
        }
    }

    /**
     * タイトル背景で流れるデモシーンを自動操作する
     *
     * @param target 更新対象のステージオブジェクト
     * @param dt     デルタタイム
     */
    public static void demoLogic(Calculation target, double dt) {
        Player player = target.getPlayer();
        MapController map = target.getMap();

        // プレイヤースプライトの位置によって方向を決定し、自動走行させる
        if(player.getEntityRightX() >= map.getWidth() / 2 && player.getDirectionX() == BaseSprite.RIGHT) {
            player.run(BaseSprite.LEFT, dt);
        } else if(player.getEntityLeftX() <= player.getActualWidth() && player.getDirectionX() == BaseSprite.LEFT){
            player.run(BaseSprite.RIGHT, dt);
        } else {
            player.run(player.getDirectionX(), dt);
        }

        // プレイヤーの状態を更新
        UpdateUtil.updatePlayer(target, dt);

        // プレイヤー以外のスプライト、ステージオブジェクトは当たり判定を行わない
        inactiveSpriteLogic(target, dt);
        inactiveObjectLogic(target, dt);
    }

    /**
     * プレイヤーと敵が衝突した際の吹き飛ばされるモーションを処理する
     *
     * @param target 更新対象のステージオブジェクト
     * @param dt     デルタタイム
     */
    public static void jumpAwayLogic(Calculation target, double dt) {
        Player player = target.getPlayer();
        player.rotate();
        UpdateUtil.outPlayer(player, target);
    }

    /**
     * プレイヤーが吹き飛ばされたあと着地して幽霊になるまでを処理する
     *
     * @param target 更新対象のステージオブジェクト
     */
    public static void respawnAsSpookLogic(Calculation target) {
        Player player = target.getPlayer();
        MapController map = target.getMap();
        player.stop();
        player.respawn(map.getHeight());
    }

    /**
     * ゲームオーバーレイヤーの下で、幽霊状態のプレイヤーを自動走行させる
     *
     * @param target 更新対象のステージオブジェクト
     * @param dt     デルタタイム
     */
    public static void gameoverLogic(Calculation target, double dt) {
        Player player = target.getPlayer();

        // プレイヤーの位置に応じて向きを決定し、自動走行させる
        player.spook(dt);

        // プレイヤーの状態を更新
        UpdateUtil.updatePlayer(target, dt);

        // プレイヤー以外のスプライト、ステージオブジェクトとの衝突判定は行わない
        inactiveSpriteLogic(target, dt);
        inactiveObjectLogic(target, dt);
    }

    /**
     * ゴールシーン中のプレイヤーを自動走行させる
     *
     * @param target 更新対象のステージオブジェクト
     * @param dt     デルタタイム
     */// ゴールモーション中のロジック
    public static void goalLogic(Calculation target, double dt) {
        Player player = target.getPlayer();
        // プレイヤーを自動走行させる
        player.goal();

        // プレイヤーの状態を更新
        UpdateUtil.updatePlayer(target, dt);

        // プレイヤー以外のスプライト、ステージオブジェクトとの衝突判定は行わない
        inactiveSpriteLogic(target, dt);
        inactiveObjectLogic(target, dt);
    }
}
