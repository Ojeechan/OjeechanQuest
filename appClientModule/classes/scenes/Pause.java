package classes.scenes;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.Map;

import javax.swing.JLayeredPane;


import classes.constants.SoundResource;
import classes.controllers.FontController;
import classes.controllers.GameController;
import classes.controllers.KeyController;
import classes.controllers.SceneController;
import classes.controllers.SoundController;
import classes.controllers.WindowController;
import classes.scenes.effects.SceneChange1;
import classes.scenes.effects.SceneChange2;
import classes.ui.StringSelectOption;
import classes.utils.GeneralUtil;

import interfaces.GameScene;

/**
 * ゲーム中のポーズ画面を定義するゲームシーンオブジェクト
 *
 * @author Naoki Yoshikawa
 */
@SuppressWarnings("serial")
public class Pause extends BaseSystemOperator implements GameScene {

    // 選択リストの文字列定数
    private final String OPTION_RESUME = "さいかい";
    private final String OPTION_ITEM = "アイテムかくにん";
    private final String OPTION_WORLDMAP = "ワールドマップ";
    private final String OPTION_TITLE = "タイトル";

    private final int RESUME = 0;
    private final int ITEM = 1;
    private final int WORLDMAP = 2;
    private final int TITLE = 3;

    private GameScene itemWindow;

    /**
     * 選択リストを設定
     */
    public Pause() {

        // 選択リストの座標、フォント、表示する文言を設定
        selectOptionList.add(
                new StringSelectOption(
                        (int) WindowController.WIDTH / 2,
                        (int) 225,
                        FontController.Fonts.NORMAL,
                        OPTION_RESUME,
                        32
                        )
                );

        selectOptionList.add(
                new StringSelectOption(
                        (int) WindowController.WIDTH / 2,
                        (int) 257,
                        FontController.Fonts.NORMAL,
                        OPTION_ITEM,
                        32
                        )
                );

        selectOptionList.add(
                new StringSelectOption(
                        (int) WindowController.WIDTH / 2,
                        (int) 289,
                        FontController.Fonts.NORMAL,
                        OPTION_WORLDMAP,
                        32
                        )
                );

        selectOptionList.add(
                new StringSelectOption(
                        (int) WindowController.WIDTH / 2,
                        (int) 321,
                        FontController.Fonts.NORMAL,
                        OPTION_TITLE,
                        32
                        )
                );

        keyHelpList.add(new StringSelectOption(120, 30, FontController.Fonts.NORMAL, "H:HELP ON/OFF", 16));
        keyHelpList.add(new StringSelectOption(120, 50, FontController.Fonts.NORMAL, "↑/↓: SELECT", 16));
        keyHelpList.add(new StringSelectOption(120, 70, FontController.Fonts.NORMAL, "ENTER: OK", 16));

        effect.addEffect(new SceneChange1(effect));
        effect.addEffect(new SceneChange2(effect));

        itemWindow = new ItemWindow();

        initParam();
    }

    /**
     * フレームごとの再描画を行う
     *
     * @param g グラフィックスオブジェクト
     */
    public void paintComponent(Graphics g) {



        // ベースを黒の半透明で塗りつぶす
        Graphics2D g2 = (Graphics2D) g;
        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
        g2.setComposite(composite);
        g2.setColor(Color.BLACK);
        g2.fillRect(
                0,
                0,
                (int) GameController.getWindow().getWindowWidth(),
                (int) GameController.getWindow().getWindowHeight()
                );

        // サブセクションを赤の半透明で塗りつぶす
        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
        g2.setComposite(composite);
        g2.setColor(Color.RED);
        g2.fillRect(
                (int) (GameController.getWindow().getWindowWidth() / 4),
                (int) (GameController.getWindow().getWindowHeight() / 4),
                (int) (GameController.getWindow().getWindowWidth() / 2),
                (int) (GameController.getWindow().getWindowHeight() / 2)
                );

        // アルファ値を不透明に戻し、選択リストを描画
        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        g2.setComposite(composite);

        for(int i = 0; i < selectOptionList.size(); i++) {
            StringSelectOption sso = selectOptionList.get(i);
            if(i == index) {
                GeneralUtil.drawStringShiver(
                        sso.getImageFont(),
                        sso.getX(),
                        sso.getY(),
                        32,
                        GeneralUtil.ALIGN_CENTER,
                        g2
                        );
            } else {
                GeneralUtil.drawImageString(
                        sso.getImageFont(),
                        sso.getX(),
                        sso.getY(),
                        32,
                        GeneralUtil.ALIGN_CENTER,
                        g2
                        );
            }
        }

        // 操作キー一覧の表示
        if(keyConfig.getKeys().get(KeyEvent.VK_H).isPressed()) {
            super.helpOn = !super.helpOn;
        }

        // キー設定を描画
          if(helpOn) {
            GeneralUtil.drawSelectOptions(keyHelpList, GeneralUtil.ALIGN_CENTER, g);
        }
    }

    /**
     * GameSceneインターフェースの機能群
     * @see interfaces.GameScene
     */

    /**
     * ユーザ入力または自動操作による入力値をもとに、フレームごとのオブジェクトの更新を行う
     *
     * @param dt     デルタタイム
     */
    public void updator(double dt) {
        WindowController w = GameController.getWindow();
        Map<Integer, KeyController.Key> keys = getKeyConfig().getKeys();
        if (keys.get(KeyEvent.VK_UP).isPressed()) {
            new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();
            previousIndex();
        } else if (keys.get(KeyEvent.VK_DOWN).isPressed()) {
            new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();
            nextIndex();
        } else if (keys.get(KeyEvent.VK_ENTER).isPressed()) {
            new Thread(new SoundController.PlaySE(SoundResource.SE_ENTER)).start();
            keyConfig.releaseAll();
            switch(index) {
            // "さいかい"を選択
            case RESUME:
                w.popScene();
                w.resumeBGM();
                break;
            // "アイテムかくにん"を選択
            case ITEM:
                helpOn = false;
                itemWindow.initParam();
                GameController.getWindow().pushScene(itemWindow);
                break;
            // "ワールドマップ"を選択
            case WORLDMAP:
                w.clearStack();
                effect.setScene(GameController.getScene(SceneController.WORLD));
                effect.process();
                break;
                // "タイトル"を選択
            case TITLE:
                w.clearStack();
                effect.setScene(GameController.getScene(SceneController.TITLE));
                effect.process();
                break;
            default:
            }

        }
    }

    /**
     * JLayeredPanelに追加するために自身のインスタンスを返す
     *
     * @return 自身のパネルインスタンス
     */
    public JLayeredPane getPanel() {
        return this;
    }

    /**
     * 自身の初期状態のインスタンスを返す
     *
     * @return 自身の初期状態のインスタンス
     */
    public GameScene getNewScene() {
        return new Pause();
    }

    /**
     * 自身のシーンで使用するBGMのファイルパスを返す
     *
     * @return BGMのファイルパス
     */
    public String getSound() {
        return null;
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
        return GameScene.BGM_NONE;
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
        super.index = 0;
        super.helpOn = false;
        effect.initEffect();
        itemWindow.initParam();
        keyConfig.releaseAll();
    }

    /**
     * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
     *
     * @param res 呼び出し元からのレスポンスコード
     */
    public void callback(int res) {

    }
}
