package classes.scenes.action;

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
import classes.controllers.WindowController;
import classes.scenes.BaseSystemOperator;
import classes.scenes.effects.SceneChange1;
import classes.scenes.effects.SceneChange2;
import classes.ui.StringSelectOption;
import classes.utils.GeneralUtil;

import interfaces.GameScene;

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

	/**
	 * 選択リスト、背景画像、背後での実行シーンの設定
	 */
	public GameOver() {

		// 選択リストの座標、フォント、表示する文言を設定
		selectOptionList.add(
				new StringSelectOption(
						180,
						(int)(WindowController.HEIGHT / 2),
						FontController.Fonts.NORMAL,
						OPTION_AGAIN,
						32
						)
    		    );

    	selectOptionList.add(
    			new StringSelectOption(
    					800,
    					(int)(WindowController.HEIGHT / 2),
    					FontController.Fonts.NORMAL,
    					OPTION_TITLE,
    					32
    					)
    		    );

		background = GeneralUtil.readImage(ImageResource.LayeredBackground.GAMEOVER.getValue());

		effect.addEffect(new SceneChange1(effect));
    	effect.addEffect(new SceneChange2(effect));
	}

	/**
	 * フレームごとの再描画を行う
	 *
	 * @param g グラフィックスオブジェクト
	 */
    public void paintComponent(Graphics g) {
    	WindowController w = GameController.getWindow();
    	Graphics2D g2 = (Graphics2D) g;
    	AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
        g2.setComposite(composite);
        g2.setColor(Color.BLACK);
        g2.fillRect(
        		0,
        		0,
        		(int) w.getWindowWidth(),
        		(int) w.getWindowHeight()
        		);

        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        g2.setComposite(composite);
        double drawWidth = background.getWidth() * 8;
        double drawHeight = background.getHeight() * 8;
    	g.drawImage(
    			background,
    			(int) w.getAbsPosX((WindowController.WIDTH - drawWidth)/2),
    			(int) w.getAbsPosY((WindowController.HEIGHT - drawHeight)/2),
    			(int) w.getAbsPosX(drawWidth),
    			(int) w.getAbsPosY(drawHeight),
    			null
    			);

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
    	WindowController w = GameController.getWindow();

    	// キーイベントの処理
    	if (keys.get(KeyEvent.VK_UP).isPressed() || keys.get(KeyEvent.VK_LEFT).isPressed()) {
    		previousIndex();
    		new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();
        } else if (keys.get(KeyEvent.VK_DOWN).isPressed() || keys.get(KeyEvent.VK_RIGHT).isPressed()) {
        	nextIndex();
    		new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();
        } else if (keys.get(KeyEvent.VK_ENTER).isPressed()) {
        	new Thread(new SoundController.PlaySE(SoundResource.SE_AH)).start();
        	w.popScene();
        	switch(index) {
        	// 「もういっかい」選択時
        	case 0:
        		w.getBasePanel().initParam();
        		w.playBGM(w.getBasePanel());
        		break;
        	// 「タイトルへ」選択時
        	case 1:
        		effect.setScene(GameController.getScene(SceneController.TITLE));
            	effect.process();
        		break;
    		default:
        	}
        }

    	GameController.getWindow().getBasePanel().updator(dt);
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
