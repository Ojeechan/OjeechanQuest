package classes.scenes.vinyl;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.controllers.FontController;
import classes.controllers.GameController;
import classes.controllers.ScriptController;
import classes.controllers.SoundController;
import classes.controllers.ScriptController.Script;
import classes.scenes.BaseSystemOperator;
import classes.ui.StringSelectOption;
import classes.utils.GeneralUtil;
import interfaces.GameScene;

/**
 * システムウィンドウ内でのはい/いいえの選択を定義するクラス
 *
 * @author Naoki Yoshikawa
 */
public class Yesno extends BaseSystemOperator implements GameScene {

	// スクリプトを管理するオブジェクト
    private ScriptController script;

    // 選択肢
    private StringSelectOption[] yesno;

    // カーソル位置のインデックス
  	private int cursorIndex;

  	/**
  	 * 選択リストの設定
  	 */
	public Yesno() {
		script = new ScriptController(
				410,
				340,
				310,
				170,
				ImageResource.VinylIcon.WINDOW.getValue()
    			);

		script.setScript(
    			24,
    			"こちらにしますか？",
    			FontController.Fonts.NORMAL
    			);

		yesno = new StringSelectOption[] {
				new StringSelectOption(
						430,
						430,
						FontController.Fonts.NORMAL,
						"はい",
						20
		    			),
				new StringSelectOption(
		    			480,
		    			430,
						FontController.Fonts.NORMAL,
						"いいえ",
						20
		    			)
		};
	}

	/**
	 * <pre>
	 * カーソルのインデックスを1つ進める
	 * 最大値の次は0に戻る
	 * </pre>
	 *
	 * @return 1つ進めた後のインデックス
	 */
	public int next() {
		cursorIndex++;
    	return cursorIndex %= yesno.length;
    }

	/**
	 * <pre>
	 * カーソルのインデックスを1つ戻す
	 * 0の次は最大インデックスに戻る
	 * </pre>
	 *
	 * @return 1つ戻した後のインデックス
	 */
    public int prev() {
    	cursorIndex--;
    	return cursorIndex < 0
    			? cursorIndex = yesno.length + cursorIndex
    			: cursorIndex;
    }

    /**
	 * フレームごとの再描画を行う
	 *
	 * @param g グラフィックスオブジェクト
	 */
	public void paintComponent(Graphics g) {

		// インデックスをセリフに対応させる
		Script s = script.getScriptList().get(0);

		GeneralUtil.drawScript(
				s,
				(int) (script.getX() + 10),
				(int) (script.getY() + 10),
				g
				);


		// 選択リスト
		for(int i = 0; i < yesno.length; i++) {

			if(i == cursorIndex) {
				GeneralUtil.drawStringShiver(
						yesno[i].getImageFont(),
						yesno[i].getX(),
						yesno[i].getY(),
						yesno[i].getSize(),
						GeneralUtil.ALIGN_LEFT,
						g
						);
			} else {
				GeneralUtil.drawImageString(
						yesno[i].getImageFont(),
						yesno[i].getX(),
						yesno[i].getY(),
						yesno[i].getSize(),
						GeneralUtil.ALIGN_LEFT,
						g
						);
			}
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
		// 左右キーはページを選択する
	    if (keyConfig.getKeys().get(KeyEvent.VK_LEFT).isPressed()) {
    		prev();
    		new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();
        } else if (keyConfig.getKeys().get(KeyEvent.VK_RIGHT).isPressed()) {
        	next();
    		new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();
        }

	    // 決定
	    if(keyConfig.getKeys().get(KeyEvent.VK_ENTER).isPressed()) {
	    	GameController.getWindow().popScene();
	    	GameController.getWindow().callback(cursorIndex);
		}

	    Script s = script.getScriptList().get(0);
	    s.proceedIndex(dt);

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
    	return new Yesno();
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
		cursorIndex = 0;
		script.getScriptList().get(0).resetIndex();;
	}

	/**
	 * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
	 *
	 * @param res 呼び出し元からのレスポンスコード
	 */
	public void callback(int res) {

	}
}
