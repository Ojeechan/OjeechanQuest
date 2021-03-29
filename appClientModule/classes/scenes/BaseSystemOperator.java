package classes.scenes;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.controllers.EffectController;
import classes.controllers.GameController;
import classes.controllers.KeyController;
import classes.scenes.BaseSystemOperator;
import classes.ui.StringSelectOption;
import classes.utils.GeneralUtil;

/**
 * ゲームシステムの共通部分で必要となるロジックを管理するクラス
 *
 * @author nyoshikawa
 */
@SuppressWarnings("serial")
public class BaseSystemOperator extends JLayeredPane implements KeyListener {

	// 選択リストのカーソルの画像オブジェクト
	protected BufferedImage cursorIcon;

	// 選択肢オブジェクトのリスト
	protected List<StringSelectOption> selectOptionList;

	// 現在カーソルの指している選択しオブジェクトのID
	protected int index;

	// キーコンフィグオブジェクト
	protected KeyController keyConfig;

	// シーン遷移間のエフェクト
	protected EffectController effect;

	protected boolean helpOn;
	protected List<StringSelectOption> keyHelpList;

	/**
	 * <pre>
	 * 選択リストが不要なシーンの場合こちらを親クラスとして呼び出す
	 * ゲームウィンドウ、キーコンフィグの設定
	 * 選択リストの初期化
	 * </pre>
	 */
	public BaseSystemOperator() {
		initPanel();
		addKeyListener(this);
		keyConfig = KeyController.getDefaultKeys();
		keyConfig.setKeys(KeyEvent.VK_UP, keyConfig.new Key(KeyController.DETECT_INITIAL_PRESS_ONLY));
		keyConfig.setKeys(KeyEvent.VK_DOWN, keyConfig.new Key(KeyController.DETECT_INITIAL_PRESS_ONLY));
		keyConfig.setKeys(KeyEvent.VK_LEFT, keyConfig.new Key(KeyController.DETECT_INITIAL_PRESS_ONLY));
		keyConfig.setKeys(KeyEvent.VK_RIGHT, keyConfig.new Key(KeyController.DETECT_INITIAL_PRESS_ONLY));
    	keyConfig.getKeys().put(KeyEvent.VK_H, keyConfig.new Key(KeyController.DETECT_INITIAL_PRESS_ONLY));
    	keyConfig.releaseAll();

    	selectOptionList = new ArrayList<StringSelectOption>();
    	effect = new EffectController();

    	this.helpOn = false;
		this.keyHelpList = new ArrayList<StringSelectOption>();

    	cursorIcon = GeneralUtil.readImage(ImageResource.Enemy1Right.PATH1.getValue());
	}

	/**
	 * パネルの設定情報を初期化する
	 */
	public void initPanel() {
		setOpaque(false);
		setPreferredSize(
				new Dimension(
						(int) GameController.getWindow().getWindowWidth(),
						(int) GameController.getWindow().getWindowHeight()
						)
				);
		setBounds(
				0,
				0,
				(int) GameController.getWindow().getWindowWidth(),
				(int) GameController.getWindow().getWindowHeight()
				);
		setFocusable(true);
	}

	/**
	 * <pre>
	 * カーソルのインデックスを1つ進める
	 * 最大値の次は0に戻る
	 * </pre>
	 *
	 * @return 1つ進めた後のインデックス
	 */
	public int nextIndex() {
		index++;
    	return index %= selectOptionList.size();
    }

	/**
	 * <pre>
	 * カーソルのインデックスを1つ戻す
	 * 0の次は最大インデックスに戻る
	 * </pre>
	 *
	 * @return 1つ戻した後のインデックス
	 */
    public int previousIndex() {
    	index--;
    	return index < 0 ? index = selectOptionList.size() + index : index;
    }

    /**
   	 * 設定されているキーコンフィグを返す
   	 *
   	 * @return キーコンフィグ
   	 */
    public KeyController getKeyConfig() {
		return keyConfig;
	}

    /*
     * KeyListenerインターフェースの機能群
     */

    /**
     * KeyListenerインターフェースの機能群
     * @see java.awt.event.KeyListener
     */
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
    	if (keyConfig.getKeys().containsKey(key)) {
    		keyConfig.getKeys().get(key).press();
    	}
    }

	/**
     * KeyListenerインターフェースの機能群
     * @see java.awt.event.KeyListener
     */
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (keyConfig.getKeys().containsKey(key)) {
    		keyConfig.getKeys().get(key).release();
    	}
    }

    /**
     * KeyListenerインターフェースの機能群
     * @see java.awt.event.KeyListener
     */
    public void keyTyped(KeyEvent e) {
    }

}
