package classes.controllers;

import java.util.List;

import classes.scenes.Introduction;
import classes.scenes.Loading;
import classes.scenes.action.ActionStage;
import classes.scenes.vinyl.VinylShop;
import classes.scenes.vinyl.assets.Vinyl;
import interfaces.GameScene;

/**
 * <pre>
 * ゲームループの管理クラス
 * ゲームスピード、デルタタイムなどを設定する
 * ゲームウィンドウ、ゲームシーンのインスタンスを保持する
 * </pre>
 *
 * @author  Naoki Yoshikawa
 */
public class GameController implements Runnable {

    // ゲームスピード
    private static int SLEEP = 18;
    private static int INIT_COIN = 9999;
    private static int MAX_COIN = 999999;

    // ゲームウィンドウの管理オブジェクト
    private static WindowController window;

    // シーンインスタンスの管理オブジェクト
    private static SceneController scene;

    // デルタタイム
    private double dt;

    // ゲーム共通の通貨
    private static int coin;

    // ゲーム共通のレコードのリスト
    private static List<Vinyl> vinylList;

    /**
     * ゲームループ用のスレッドを開始する
     *
     * @param window ゲームウィンドウオブジェクト
     */
    public GameController(WindowController window) {

        GameController.window = window;

        window.setGameScene(new Introduction());
        window.pushScene(new Loading());

        window.pack();
        window.setVisible(true);

        // 初期デルタタイム
        dt = 0.0;

        // ゲーム共通の通貨
        coin = INIT_COIN;

        // ゲーム共通の所持レコード
        vinylList = Vinyl.getDefaultVinyl();

        // マルチスレッドで初期ロード
        new Thread(new Load()).start();

        // ゲームループ開始
        Thread gameLoop = new Thread(this);
        gameLoop.start();
    }

    private class Load implements Runnable {

        @Override
        public void run() {
            scene = new SceneController();
            scene.initEffect();
            window.callback(0);
        }
    }

    /**
     * ゲームウィンドウオブジェクトを返す
     *
     * @return ゲームウィンドウオブジェクト
     */
    public static WindowController getWindow() {
        return window;
    }

    /**
     * ゲーム共通で使用するスコアを返す
     *
     * @return ゲーム内共通のスコア
     */
    public static int getCoin() {
        return coin;
    }

    /**
     * ゲーム共通のスコアを設定する
     *
     * @param income 加算するゲームスコア
     */
    public static void addCoin(int income) {
        coin += income;
        coin = Math.min(coin, MAX_COIN);
        coin = Math.max(coin, -MAX_COIN);
    }

    /**
     * ゲーム共通のスコアを初期化する
     */
    public static void resetCoin() {
        coin = INIT_COIN;
    }

    /**
     * ゲーム共通で使用するレコードのリストを返す
     *
     * @return ゲーム内共通のレコード
     */
    public static List<Vinyl> getVinylList() {
        return vinylList;
    }

    /**
     * レコードを所持リストに追加する
     *
     * @param v 追加するレコードオブジェクト
     */
    public static void addVinyl(Vinyl v) {
        vinylList.add(v);
    }

    /**
     * ゲーム共通で使用するレコードのリストを初期化する
     */
    public static void resetVinylList() {
        vinylList = Vinyl.getDefaultVinyl();
    }

    /**
     * リセットが必要な各ステージのパラメータを初期化する
     */
    public static void resetParam() {
        VinylShop vs = (VinylShop) GameController.getScene(SceneController.VINYL);
        vs.restock();
        ActionStage as = (ActionStage) GameController.getScene(SceneController.ACTION);
        as.resetParam();
    }

    /**
     * 生成済みのシーンインスタンスを取得する
     *
     * @param index シーンインスタンスのインデックス
     * @return シーンインスタンス
     */
    public static GameScene getScene(int index) {
        return scene.getScene(index);
    }

    /**
     * メインループ
     */
    public void run() {

        while(true) {

            // デルタタイムの計測開始
            long startdt = System.currentTimeMillis();

            // 現在のシーンの更新メソッドに処理を委譲する
            window.getFrontPanel().updator(dt);
            window.repaint();

            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // デルタタイムの計測終了
            dt = (System.currentTimeMillis() - startdt) / 1000.0;
        }
    }
}
