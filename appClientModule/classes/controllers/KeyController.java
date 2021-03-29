package classes.controllers;

import java.awt.event.KeyEvent;
import java.util.HashMap;

/**
 * キーボード入力の管理クラス
 *
 * @author  Naoki Yoshikawa
 */
public class KeyController {

	// キーのモード
    // キーが押されている間はisPressed()はtrueを返す
    public static final int NORMAL = 0;

    // キーがはじめに押されたときだけisPressed()はtrueを返す
    // キーが押され続けても2回目以降はfalseを返す
    // このモードを使うとジャンプボタンを押し続けてもジャンプを繰り返さない
    public static final int DETECT_INITIAL_PRESS_ONLY = 1;

    // キーの状態
    // キーが離された
    private static final int STATE_RELEASED = 0;
    // キーが押されている
    private static final int STATE_PRESSED = 1;
    // キーが離されるのを待っている
    private static final int STATE_WAITING_FOR_RELEASE = 2;

    // 操作を登録されたキーの一覧
    private HashMap<Integer, KeyController.Key> registeredKeys;

    /**
     * 登録キーの初期化
     */
	public KeyController() {
		registeredKeys = new HashMap<Integer, KeyController.Key>();
	}

	/**
	 * 個別のキーオブジェクトを定義するインナークラス
	 */
	public class Key {

	    // キーのモード
	    private int mode;

	    // キーが押された回数
	    private int amount;

	    // キーの状態
	    private int state;

	    /**
	     * デフォルトコンストラクタ
	     * 連打制御を行わないモード
	     */
	    public Key() {
	        this(NORMAL);
	    }

	    /**
	     * モード指定のコンストラクタ
	     *
	     * @param mode キーイベント処理のモード
	     */
	    public Key(int mode) {
	    	this.mode = mode;
	        reset();
	    }

	    /**
	     * キーの押された回数をリセットし、押されていない状態に戻す
	     */
	    public void reset() {
	        state = STATE_RELEASED;
	        amount = 0;
	    }

	    /**
	     * キーを押下状態にする
	     */
	    public void press() {
	        // STATE_WAITING_FOR_RELEASEのときは押されたことにならない
	        if (state != STATE_WAITING_FOR_RELEASE) {
	            amount++;
	            state = STATE_PRESSED;
	        }
	    }

	    /**
	     * キーを押されていない状態にする
	     */
	    public void release() {
	        state = STATE_RELEASED;
	    }

	    /**
	     * キーが押されている間に受け取った入力回数を返す
	     *
	     * @return 1回のキー押下で受け付けた入力回数
	     */
	    public int getPressedAmount() {
	    	return amount;
	    }

	    /**
	     * <pre>
	     * キーが押下状態にあるかどうかを判断する
	     * 押下状態にあればtrue
	     * </pre>
	     *
	     * @return 押下状態にあるかどうか
	     */
	    public boolean isPressed() {
	        if (amount != 0) {
	            if (state == STATE_RELEASED) {
	                amount = 0;
	            } else if (mode == DETECT_INITIAL_PRESS_ONLY) {
	                // 最初の1回だけtrueを返して押されたことにする
	                // 次回からはSTATE_WAITING_FOR_RELEASEになるため
	                // キーを押し続けても押されたことにならない
	                state = STATE_WAITING_FOR_RELEASE;
	                amount = 0;
	            }

	            return true;
	        }

	        return false;
	    }
	}

	/**
	 * 登録されたキーコンフィグのマップを返す
	 *
	 * @return キーコンフィグ
	 */
    public HashMap<Integer, KeyController.Key> getKeys() {
		return this.registeredKeys;
	}

    /**
     * キーコンフィグにキーを登録する
     *
     * @param keyCode キーのID
     * @param key     Keyオブジェクト
     */
    public void setKeys(int keyCode, Key key) {
    	registeredKeys.put(keyCode, key);
    }

    /**
     * デフォルトのキーコンフィグをもつコントローラーオブジェクトを返すファクトリーメソッド
     *
     * @return デフォルトのコントローラーオブジェクト
     */
    public static KeyController getDefaultKeys() {
    	KeyController defaultConfig = new KeyController();
    	defaultConfig.registeredKeys.put(KeyEvent.VK_LEFT, defaultConfig.new Key());
    	defaultConfig.registeredKeys.put(KeyEvent.VK_RIGHT, defaultConfig.new Key());
    	defaultConfig.registeredKeys.put(KeyEvent.VK_UP, defaultConfig.new Key());
    	defaultConfig.registeredKeys.put(KeyEvent.VK_DOWN, defaultConfig.new Key());
    	defaultConfig.registeredKeys.put(KeyEvent.VK_SPACE, defaultConfig.new Key(DETECT_INITIAL_PRESS_ONLY));
    	defaultConfig.registeredKeys.put(KeyEvent.VK_ESCAPE, defaultConfig.new Key(DETECT_INITIAL_PRESS_ONLY));
    	defaultConfig.registeredKeys.put(KeyEvent.VK_ENTER, defaultConfig.new Key(DETECT_INITIAL_PRESS_ONLY));
    	return defaultConfig;
    }

    public void releaseAll() {
    	for(Key key:this.registeredKeys.values()) {
    		key.release();
    	}
    }
}
