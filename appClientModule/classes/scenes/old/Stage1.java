package classes.scenes.old;

import java.awt.Color;
import java.awt.Point;

import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.scenes.action.assets.*;
import classes.scenes.old.assets.Ball;
import classes.scenes.old.assets.Player;
import classes.scenes.old.assets.StaticObject;
import classes.scenes.old.logics.UpdateLogic;
import classes.controllers.MapController;

import interfaces.GameScene;

/**
 * <pre>
 * ゲームステージを定義するゲームシーンオブジェクト
 * 当たり判定調査用の実験ステージ
 * </pre>
 *
 * @author Naoki Yoshikawa
 **/
@SuppressWarnings("serial")
public class Stage1 extends BaseActionOperator implements GameScene {

    // デフォルト値以外の重力値
    public static final double GRAVITY = 1.2;

    // 地形データのファイルパス
    private static final String MAPDATA = "map03.dat";

    /**
     * マップの読み込み、スプライトの設定
     */
    public Stage1() {

        // マップを作成
        map = new MapController(MAPDATA, MapController.getStage1Asset());

        // デフォルト設定のキャラクターを取得
        player = Player.getDefault(192, 400, 32, 32);
        player.setDashRate(4.0);
        player.setJumpSpeed(17.0);

        baseSpriteList.add(
                new Ball(
                400.0,
                map.getHeight() - 32,
                32,
                32,
                ImageResource.StageObject.BLOCK_A.getValue(),
                3
            )
        );

        baseSpriteList.add(
                new Ball(
                464.0,
                map.getHeight() - 32,
                32,
                32,
                ImageResource.StageObject.BLOCK_B.getValue(),
                2
            )
        );

        baseSpriteList.add(
                new Ball(
                428.0,
                map.getHeight() - 128,
                32,
                32,
                ImageResource.StageObject.BLOCK_C.getValue(),
                1
            )
        );

        baseSpriteList.add(
                new Ball(
                600.0,
                map.getHeight() - 128,
                32,
                32,
                ImageResource.StageObject.BLOCK_C.getValue(),
                0.5
            )
        );
        baseSpriteList.add(
                new Ball(
                690.0,
                map.getHeight() - 128,
                32,
                32,
                ImageResource.StageObject.BLOCK_E.getValue(),
                0.5
            )
        );
        baseSpriteList.add(
                new Ball(
                730.0,
                map.getHeight() - 128,
                32,
                32,
                ImageResource.StageObject.BLOCK_B.getValue(),
                0.5
            )
        );
        baseSpriteList.add(
            new Ball(
                760.0,
                map.getHeight() - 128,
                32,
                32,
                ImageResource.StageObject.BLOCK_A.getValue(),
                0.5
            )
        );

        this.setBackground(Color.BLACK);

        frontObjectList.add(
                new StaticObject(
                    100,
                    500,
                    ImageResource.StageObject.BLOCK_STOP
                )
            );

        frontObjectList.add(
                new StaticObject(
                    150,
                    500,
                    ImageResource.StageObject.BLOCK_BEAT
                )
            );

        frontObjectList.add(
            new StaticObject(
                200,
                500,
                ImageResource.StageObject.BLOCK_C
            )
        );

        frontObjectList.add(
                new StaticObject(
                    250,
                    500,
                    ImageResource.StageObject.BLOCK_D
                )
            );

        frontObjectList.add(
                new StaticObject(
                    300,
                    500,
                    ImageResource.StageObject.BLOCK_E
                )
            );

        frontObjectList.add(
                new StaticObject(
                    350,
                    500,
                    ImageResource.StageObject.BLOCK_F
                )
            );

        frontObjectList.add(
                new StaticObject(
                    400,
                    500,
                    ImageResource.StageObject.BLOCK_G
                )
            );

        frontObjectList.add(
                new StaticObject(
                    450,
                    500,
                    ImageResource.StageObject.BLOCK_A
                )
            );

        frontObjectList.add(
                new StaticObject(
                    500,
                    500,
                    ImageResource.StageObject.BLOCK_B
                )
            );
    }

    /**
     * ステージ固有の重力値を設定する
     */
    @Override
    public double getGravity() {
        return GRAVITY;
    }

    /*
     * GameSceneインターフェースの機能群
     */

    /**
     * ユーザ入力または自動操作による入力値をもとに、フレームごとのオブジェクトの更新を行う
     * @see interfaces.GameScene
     *
     * @param dt     デルタタイム
     */
    public void updator(double dt) {
        UpdateLogic.basicLogic(this, dt);
        ;
    }

    /**
     * JLayeredPanelに追加するために自身のインスタンスを返す
     * @see interfaces.GameScene
     *
     * @return 自身のパネルインスタンス
     */
    public JLayeredPane getPanel() {
        return this;
    }

    /**
     * 自身の初期状態のインスタンスを返す
     * @see interfaces.GameScene
     *
     * @return 自身の初期状態のインスタンス
     */
    public GameScene getNewScene() {
        return new Stage1();
    }

    /**
     * 自身のシーンで使用するBGMのファイルパスを返す
     * @see interfaces.GameScene
     *
     * @return BGMのファイルパス
     */
    public String getSound() {
        return SoundResource.BGM_MAINTHEME;
    }

    /**
     * <pre>
     * 自身のシーンで使用するBGMの再生モードを返す
     * 0: ループ再生(ループ区間指定可)
     * 1: 一度のみ再生
     * 2: 再生なし
     * </pre>
     *
     * @return BGMのファイルパス
     */
    public int getBgmMode() {
        return GameScene.BGM_LOOP;
    }

    /**
     * 自身のシーンで使用するBGMの再生区間を返す
     *
     * @return BGM再生区間を表す始点と終点の値の組
     */
    public Point getDuration() {
        return new Point(0, GameScene.BGM_END);
    }

    /**
     * 各パラメータを初期化する
     */
    public void initParam() {
        // アクション系はスプライトのリストにinitをかけていけばいいはず
        // よってコンストは今のままでいい？
        super.helpOn = true;
    }

    /**
     * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
     *
     * @param res 呼び出し元からのレスポンスコード
     */
    public void callback(int res) {

    }
}
