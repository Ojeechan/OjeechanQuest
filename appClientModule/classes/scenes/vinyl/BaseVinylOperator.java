package classes.scenes.vinyl;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLayeredPane;

import classes.controllers.GameController;
import classes.controllers.KeyController;
import classes.ui.StringSelectOption;

/**
 * レコードステージの操作系を定義する基底クラス
 *
 * @author Naoki Yoshikawa
 **/

@SuppressWarnings("serial")
public class BaseVinylOperator extends JLayeredPane implements KeyListener {

    // キーコンフィグ
    protected KeyController keyConfig;

    protected boolean helpOn;
    protected List<StringSelectOption> keyHelpList;

    /**
     * ウィンドウパネル、キーイベント、キーコンフィグの設定
     */
    public BaseVinylOperator() {
        initPanel();
        addKeyListener(this);
        keyConfig = KeyController.getDefaultKeys();
        keyConfig.getKeys().put(KeyEvent.VK_H, keyConfig.new Key(KeyController.DETECT_INITIAL_PRESS_ONLY));
        keyConfig.releaseAll();
        this.helpOn = true;
        this.keyHelpList = new ArrayList<StringSelectOption>();
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
     * 固有のキーコンフィグオブジェクトを返す
     *
     * @return キーコンフィグ
     */
    public KeyController getKeyConfig() {
        return keyConfig;
    }

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
