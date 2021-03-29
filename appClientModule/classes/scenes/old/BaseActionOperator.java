package classes.scenes.old;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLayeredPane;

import classes.containers.Background;
import classes.controllers.GameController;
import classes.controllers.KeyController;
import classes.controllers.MapController;
import classes.ui.StringSelectOption;
import classes.scenes.old.assets.BaseSprite;
import classes.scenes.old.assets.Player;
import classes.scenes.old.assets.StaticObject;
import classes.scenes.old.logics.DrawLogic;
import interfaces.Calculation;


/**
 * アクションステージの操作系を定義する基底クラス
 *
 * @author Naoki Yoshikawa
 **/
@SuppressWarnings("serial")
public class BaseActionOperator extends JLayeredPane implements Calculation, KeyListener {

	// ステージ固有の定数
	private static double GRAVITY = 0.6; // 重力

	// 背景画像オブジェクト
	protected List<Background> background;

	// アクションステージの地形情報オブジェクト
	protected MapController map;

	//プレイヤースプライト
	protected Player player;

	// キーコンフィグ
	protected KeyController keyConfig;

	// スプライトリスト
    protected LinkedList<BaseSprite> baseSpriteList;
    protected LinkedList<StaticObject> frontObjectList;
    protected LinkedList<StaticObject> backObjectList;

    protected boolean helpOn;
	protected List<StringSelectOption> keyHelpList;

    /**
     * パネルの設定、キーイベントの登録、キーコンフィグ、スプライトリストの初期化
     */
    public BaseActionOperator() {
    	initPanel();
    	addKeyListener(this);
		keyConfig = KeyController.getDefaultKeys();
		keyConfig.releaseAll();
    	baseSpriteList = new LinkedList<BaseSprite>();
    	frontObjectList = new LinkedList<StaticObject>();
    	backObjectList = new LinkedList<StaticObject>();
    	background = new LinkedList<Background>();
    	this.helpOn = true;
		this.keyHelpList = new ArrayList<StringSelectOption>();
    }

    /**
     * 背景画像のラッパーオブジェクトのリストを返す
     *
     * @return 背景画像オブジェクトのリスト
     */
    public List<Background> getBackgroundList() {
    	return background;
    }

    /**
     * ステージ固有の重力値を返す
     *
     * @return 重力値
     */
    public double getGravity() {
    	return GRAVITY;
    }

    /**
     * プレイヤースプライトを返す
     *
     * @return プレイヤースプライトオブジェクト
     */
	public Player getPlayer() {
		return player;
	}


	/**
	 * ステージの地形情報オブジェクトを返す
	 *
	 * @return 地形情報オブジェクト
	 */
	public MapController getMap() {
		return map;
	}

	/**
	 * スプライトオブジェクトのリストを返す
	 *
	 * @return スプライトオブジェクトのリスト
	 */
	public LinkedList<BaseSprite> getSpriteList() {
		return baseSpriteList;
	}

	/**
	 * 最前面に描画するステージオブジェクトのリストを返す
	 *
	 * @return ステージオブジェクトのリスト
	 */
	public LinkedList<StaticObject> getFrontObjectList() {
		return frontObjectList;
	}


	/**
	 * 最背面に描画するステージオブジェクトのリストを返す
	 *
	 * @return ステージオブジェクトのリスト
	 */
	public LinkedList<StaticObject> getBackObjectList() {
		return backObjectList;
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
	 * フレームごとの再描画を行う
	 *
	 * @param g グラフィックスオブジェクト
	 */
	public void paintComponent(Graphics g) {

		// 描画順にzインデックスが手前になるので順番に注意
		DrawLogic.paintBackgroundLogic(this, background, g);
		DrawLogic.drawObjectLogic(this, g);
    }

	/**
	 * ステージ固有のキーコンフィグを返す
	 *
	 * @return キーコンフィグオブジェクト
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
