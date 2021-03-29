package classes.scenes.old;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.controllers.FontController;
import classes.controllers.GameController;
import classes.controllers.KeyController;
import classes.controllers.SceneController;
import classes.controllers.SoundController;
import classes.scenes.BaseSystemOperator;
import classes.scenes.effects.SceneChange1;
import classes.scenes.effects.SceneChange2;
import classes.scenes.old.logics.UpdateLogic;
import classes.ui.StringSelectOption;
import classes.scenes.old.utils.DrawUtil;
import classes.utils.GeneralUtil;

import interfaces.GameScene;
import interfaces.Calculation;

/**
 * ゲームオーバー画面を定義するゲームシーンオブジェクト
 *
 * @author Naoki Yoshikawa
 */
@SuppressWarnings("serial")
public class GameOver extends BaseSystemOperator implements GameScene {

	// 選択リストの文言に使用する文字列定数
	private static final String OPTION_AGAIN = "もういっかい";
	private static final String OPTION_TITLE = "タイトル";

	// 背景画像オブジェクト
	private BufferedImage background;

	// ゲームオーバーシーンの背後で実行するゲームシーンオブジェクト
	private Calculation baseScene;

	/**
	 * 選択リスト、背景画像、背後での実行シーンの設定
	 */
	public GameOver() {

		// 選択リストの座標、フォント、表示する文言を設定
		selectOptionList.add(
				new StringSelectOption(
						35,
						(int)(GameController.getWindow().getWindowHeight() * 0.5),
						FontController.Fonts.NORMAL,
						OPTION_AGAIN,
						32
						)
    		    );

    	selectOptionList.add(
    			new StringSelectOption(
    					500,
    					(int)(GameController.getWindow().getWindowHeight() * 0.5),
    					FontController.Fonts.NORMAL,
    					OPTION_TITLE,
    					32
    					)
    		    );

		background = GeneralUtil.readImage(ImageResource.LayeredBackground.GAMEOVER.getValue());

		// あとでコード的な裏付けを追加するが、今は一旦ゴリ押しでキャストする
		baseScene = (Calculation)GameController.getWindow().getBasePanel();

		UpdateLogic.respawnAsSpookLogic(baseScene);

		effect.addEffect(new SceneChange1(effect));
    	effect.addEffect(new SceneChange2(effect));
	}

	/**
	 * フレームごとの再描画を行う
	 *
	 * @param g グラフィックスオブジェクト
	 */
    public void paintComponent(Graphics g) {
    	Graphics2D g2 = (Graphics2D) g;
    	AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
        g2.setComposite(composite);
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, (int) GameController.getWindow().getWindowWidth(), (int) GameController.getWindow().getWindowHeight());

        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        g2.setComposite(composite);
    	g.drawImage(background, 96, 48, background.getWidth() * 8,  background.getHeight() * 8,null);
    	GeneralUtil.drawSelectOptions(selectOptionList, GeneralUtil.ALIGN_CENTER, g);

        StringSelectOption sso = selectOptionList.get(index);

      	g.drawImage(
      			cursorIcon,
      			(int) GameController.getWindow().getAbsPosX(sso.getX() - cursorIcon.getWidth()),
      			(int) GameController.getWindow().getAbsPosY(sso.getY()),
      			(int) GameController.getWindow().getAbsPosX(cursorIcon.getWidth()),
      			(int) GameController.getWindow().getAbsPosY(cursorIcon.getHeight()),
      			null
      			);
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
    	Map<Integer, KeyController.Key> keys = getKeyConfig().getKeys();

    	// キーイベントの処理
    	if (keys.get(KeyEvent.VK_UP).isPressed() || keys.get(KeyEvent.VK_LEFT).isPressed()) {
    		previousIndex();
    		new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();
        } else if (keys.get(KeyEvent.VK_DOWN).isPressed() || keys.get(KeyEvent.VK_RIGHT).isPressed()) {
        	nextIndex();
    		new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();
        } else if (keys.get(KeyEvent.VK_ENTER).isPressed()) {
        	new Thread(new SoundController.PlaySE(SoundResource.SE_AH)).start();
        	switch(index) {
        	// 「もういっかい」選択時
        	case 0:
        		GameController.getWindow().resetScene();
        		break;
        	// 「タイトルへ」選択時
        	case 1:
        		effect.setScene(GameController.getScene(SceneController.TITLE));
            	effect.process();
        		break;
    		default:
        	}
        }

    	UpdateLogic.gameoverLogic(baseScene, dt);
    	;
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
    	return new GameOver();
    }

    /**
     * 自身のシーンで使用するBGMのファイルパスを返す
     *
     * @return BGMのファイルパス
     */
	public String getSound() {
		return SoundResource.BGM_GAMEOVER;
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
		super.index = 0;
		super.helpOn = false;
		effect.initEffect();
	}

	/**
	 * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
	 *
	 * @param res 呼び出し元からのレスポンスコード
	 */
	public void callback(int res) {

	}
}
