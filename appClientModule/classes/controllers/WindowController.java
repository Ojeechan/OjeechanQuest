package classes.controllers;

import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.Stack;

import javax.swing.JFrame;

import interfaces.*;

/**
 * ゲームウィンドウを管理するクラス
 *
 * @author Naoki Yoshikawa
 */
@SuppressWarnings("serial")
public class WindowController extends JFrame implements ComponentListener, WindowStateListener {

	// 基準となるウィンドウサイズ
	public static final double WIDTH = 960;
    public static final double HEIGHT = 540;

	// ウィンドウに表示するタイトル
	private final String TITLE = "Reinvention of the Wheel";

	// 現在更新・描画が行われているゲームシーンオブジェクト
	private GameScene currentScene;

    // 重ねて描画するゲームシーンオブジェクトのスタック
	private Stack<GameScene> sceneStack;

	// 現在のウィンドウサイズ
    private double currentWidth;
    private double currentHeight;

    /**
     * ウィンドウの設定及び初期シーンを指定する
     */
	public WindowController() {

		this.addComponentListener(this);
		this.addWindowStateListener(this);

		// windowを閉じた際、アプリケーションも合わせて終了させる
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// タイトルを設定
        setTitle(TITLE);

        // サイズ変更可
        setResizable(true);

        // デフォルトのウィンドウサイズ
        currentWidth = 60 * 16;//WIDTH;
        currentHeight = 60 * 9;//HEIGHT;

        sceneStack = new Stack<GameScene>();
    }

	/**
	 * <pre>
	 * ゲームシーンのスタックが空かどうかを返す
	 * 空の場合 true
	 * </pre>
	 *
	 * @return スタックが空かどうか
	 */
	public boolean isEmptyStack() {
		return sceneStack.isEmpty();
	}

	/**
	 * スタックの最上位にあるゲームシーンを返す
	 *
	 * @return スタック最上位のゲームシーンオブジェクト
	 */
	public GameScene peekScene() {
		return sceneStack.peek();
	}

	/**
	 * ゲームシーンオブジェクトのスタックを返す
	 *
	 * @return ゲームシーンオブジェクトのスタック
	 */
	public Stack<GameScene> getSceneStack() {
		return sceneStack;
	}

	/**
	 * 現在のゲームシーンオブジェクトのスタックを空にする
	 */
	public void clearStack() {
		for(GameScene s: sceneStack) {
			this.remove(s.getPanel());
		}
		sceneStack.clear();
	}

	/**
	 * スタックにゲームシーンオブジェクトを追加する
	 *
	 * @param s 追加するゲームシーンオブジェクト
	 */
	public void pushScene(GameScene s) {
		s.initPanel();

		sceneStack.push(s);

		// 描画順の逆順でaddし直す
		this.remove(getBasePanel().getPanel());
		for(GameScene scene: sceneStack) {
			this.remove(scene.getPanel());
		}
		for(int i = 0; i < sceneStack.size(); i++) {
			this.add(sceneStack.get(sceneStack.size() - (1 + i)).getPanel());
		}
        this.add(getBasePanel().getPanel());
        getBasePanel().getKeyConfig().releaseAll();
        playBGM(s);
        repaintUtil();
	}

	/**
	 * スタックの最上位からゲームシーンオブジェクトを1つ削除する
	 */
	public void popScene() {
		this.remove(sceneStack.pop().getPanel());
        getFrontPanel().getKeyConfig().releaseAll();
        repaintUtil();
	}

	/**
	 * メインのゲームシーンオブジェクトを移行する
	 *
	 * @param scene   移行先のゲームシーンオブジェクト
	 */
	public void changeScene(GameScene scene) {
		if(currentScene != null) {
			this.remove(currentScene.getPanel());
		}

		this.currentScene = scene;
		this.add(scene.getPanel());

		// BGMの切り替え
		playBGM(scene);

		repaintUtil();
	}

	/**
	 * <pre>
	 * メインのゲームシーンオブジェクトを設定する
	 * 画面遷移は行わない
	 * </pre>
	 *
	 * @param scene ゲームシーンオブジェクト
	 */
	public void setGameScene(GameScene scene) {

		if(currentScene != null) {
			this.remove(currentScene.getPanel());
		}

		this.currentScene = scene;
		this.add(scene.getPanel());


		repaintUtil();
	}

	/**
	 * スタックの子ゲームシーンから親ゲームシーンへのコールバックを仲介する
	 *
	 * @param res レスポンス内容を表す数値
	 */
	public void callback(int res) {
		if(!sceneStack.isEmpty()) {
			sceneStack.peek().callback(res);
		} else {
			currentScene.callback(res);
		}
	}

	/**
	 * WindowController.WIDTHを基準とした絶対位置を、現在のウィンドウサイズに対する絶対位置に変換する
	 *
	 * @param x WIDTH基準での絶対位置のX座標
	 * @return 現在のウィンドウサイズ基準での絶対位置のX座標
	 */
	public double getAbsPosX(double x) {
		return currentWidth * x / WIDTH;
	}

	/**
	 * WindowController.HEIGHTを基準とした絶対位置を、現在のウィンドウサイズに対する絶対位置に変換する
	 *
	 * @param y HEIGHT基準での絶対位置のY座標
	 * @return 現在のウィンドウサイズ基準での絶対位置のY座標
	 */
	public double getAbsPosY(double y) {
		return currentHeight * y / HEIGHT;
	}

	/**
	 * 現在のゲームウィンドウの幅をpxで取得する
	 *
	 * @return ゲームウィンドウの幅
	 */
	public double getWindowWidth() {
		return currentWidth;
	}

	/**
	 * 現在のゲームウィンドウの高さをpxで取得する
	 *
	 * @return ゲームウィンドウの高さ
	 */
	public double getWindowHeight() {
		return currentHeight;
	}

	/**
	 * 現在のゲームウィンドウの幅をpxで設定する
	 *
	 * @param w ゲームウィンドウの幅
	 */
	public void setWindowWidth(double w) {
		currentWidth = w;
	}

	/**
	 * 現在のゲームウィンドウの高さをpxで設定する
	 *
	 * @param w ゲームウィンドウの高さ
	 */
	public void setWindowHeight(double w) {
		currentHeight = w;
	}

	/**
	 * 最前面に表示されているシーンオブジェクトを返す
	 *
	 * @return 最前面のゲームシーンオブジェクト
	 */
	public GameScene getFrontPanel() {
		if(sceneStack.isEmpty()) {
			return this.currentScene;
		} else {
			return sceneStack.peek();
		}
	}

	/**
	 * 現在更新・描画されているメインのゲームシーンオブジェクトを返す
	 *
	 * @return メインのゲームシーンオブジェクト
	 */
	public GameScene getBasePanel() {
		return this.currentScene;
	}

	/**
	 * BGMを再生する
	 *
	 * @param scene BGMを再生するシーンオブジェクト
	 */
	public void playBGM(GameScene scene) {
		// BGMの切り替え
		if(scene.getSound() != null) {
			switch(scene.getBgmMode()) {
			case GameScene.BGM_LOOP:
				Point duration = scene.getDuration();
				new Thread(new SoundController.PeriodLoopBGM(scene.getSound(), duration.x, duration.y)).start();
				break;
			case GameScene.BGM_ONCE:
				new Thread(new SoundController.PlayBGM(scene.getSound())).start();
				break;
			default:
			}
		}
	}

	/**
	 * コールバックの実験
	 *
	 * @param scene BGMを再生するシーンオブジェクト
	 */
	public void callBackBGM(GameScene scene) {
		// BGMの切り替え
		if(scene.getSound() != null) {
			switch(scene.getBgmMode()) {
			case GameScene.BGM_LOOP:
				Point duration = scene.getDuration();
				new Thread(new SoundController.PeriodLoopBGM(scene.getSound(), duration.x, duration.y)).start();
				break;
			case GameScene.BGM_ONCE:
				//new Thread(new SoundController.PlayBGM(scene.getSound())).start();
				new Thread(new SoundController.CallBackBGM(scene)).start();
				break;
			default:
			}
		}
	}

	/**
	 * 一時停止されたBGMを再開する
	 */
	public void resumeBGM() {

		if(getFrontPanel().getSound() != null) {
			switch(getFrontPanel().getBgmMode()) {
			case GameScene.BGM_LOOP:
				new Thread(new SoundController.ResumeLoopBGM()).start();
				break;
			case GameScene.BGM_ONCE:
				new Thread(new SoundController.ResumeBGM()).start();
				break;
			default:
			}
		}
	}

	/**
	 * 現在のシーンの初期状態に戻す
	 */
	public void resetScene() {
		this.currentScene.getPanel().removeAll();
		changeScene(this.currentScene.getNewScene());
	}

	/**
	 * パネルの更新・再描画の一連の手続きを集約して行う
	 */
	private void repaintUtil() {
		// フォーカスを操作パネルに設定
		getFrontPanel().getPanel().requestFocusInWindow();
		//更新
		super.validate();
	}

	/**
	 * ゲームウィンドウのリサイズイベント時にウィンドウサイズを更新する
	 *
	 * @param e JFrameのコンポネントイベント
	 */
	@Override
	public void componentResized(ComponentEvent e) {
		currentWidth = super.getWidth();
		currentHeight = super.getHeight();
		getBasePanel().initPanel();
		for(GameScene s: sceneStack) {
			s.initPanel();
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	/**
	 * ゲームウィンドウ最大/最小化時にウィンドウサイズを更新する
	 *
	 * @param e ウィンドウイベント
	 */
	@Override
	public void windowStateChanged(WindowEvent e) {
		currentWidth = super.getWidth();
		currentHeight = super.getHeight();
		getBasePanel().initPanel();
		for(GameScene s: sceneStack) {
			s.initPanel();
		}
	}
}
