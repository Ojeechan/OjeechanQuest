package classes.scenes.action;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.controllers.FontController;
import classes.controllers.GameController;
import classes.controllers.SceneController;
import classes.controllers.ScriptController;
import classes.controllers.WindowController;
import classes.controllers.ScriptController.Script;
import classes.scenes.BaseSystemOperator;
import classes.ui.StringSelectOption;
import classes.utils.GeneralUtil;

import interfaces.GameScene;

/**
 * アクションステージの初期フローにおいて、買取屋のセリフウィンドウを定義するクラス
 *
 * @author Naoki Yoshikawa
 */
public class Tutorial extends BaseSystemOperator implements GameScene {

	// スクリプトを管理するオブジェクト
    private ScriptController script;

 	/**
 	 * スクリプト、ヘルプメッセージの設定
 	 */
	public Tutorial() {

    	keyHelpList.add(new StringSelectOption(600, 250, FontController.Fonts.NORMAL, "ENTER: OK", 16));

		// 店員のスクリプトを設定
		script = new ScriptController(
				400,
				100,
				400,
				200,
				ImageResource.LayeredBackground.WINDOW.getValue()
    			);

    	script.setScript(
    			32,
    			"キノコカイトリマス",
    			FontController.Fonts.NORMAL
    			);

		initParam();
	}

    /**
	 * フレームごとの再描画を行う
	 *
	 * @param g グラフィックスオブジェクト
	 */
    @Override
	public void paintComponent(Graphics g) {

		// ウィンドウ背景の描画

    	g.drawImage(
				script.getBackground(),
				(int) GameController.getWindow().getAbsPosX(script.getX()),
				(int) GameController.getWindow().getAbsPosY(script.getY()),
				(int) GameController.getWindow().getAbsPosX(script.getWidth()),
				(int) GameController.getWindow().getAbsPosY(script.getHeight()),
				null
				);

		// インデックスで指定したスクリプトを表示
		Script s = script.getScriptList().get(0);
		GeneralUtil.drawScript(
				s,
				(int) (script.getX() + 30),
				(int) (script.getY() + 80),
				g
	            );

		if(helpOn) {
    		GeneralUtil.drawSelectOptions(keyHelpList, GeneralUtil.ALIGN_CENTER, g);
    	}
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
	@Override
	public void updator(double dt) {

		WindowController w = GameController.getWindow();
		// ポーズ
	    if(keyConfig.getKeys().get(KeyEvent.VK_ESCAPE).isPressed()) {
	    	w.pushScene(GameController.getScene(SceneController.PAUSE));
	    	keyConfig.releaseAll();
	    }

	    if(keyConfig.getKeys().get(KeyEvent.VK_ENTER).isPressed()) {
	    	w.popScene();
	    }

	    script.getScriptList().get(0).proceedIndex(dt);
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
    	return new Tutorial();
    }

    /**
     * 自身のシーンで使用するBGMのファイルパスを返す
     * @see interfaces.GameScene
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
		return null;
	}

    /**
     * 各パラメータを初期化する
     */
	public void initParam() {
		helpOn = true;
	}

	/**
	 * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
	 *
	 * @param res 呼び出し元からのレスポンスコード
	 */
	public void callback(int res) {

	}
}
