package interfaces;

import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JLayeredPane;

import classes.controllers.KeyController;


/**
 * ゲームシーンオブジェクトを規定するインターフェース
 * @author Naoki Yoshikawa
 *
 */
public interface GameScene {

	public static final int BGM_LOOP = 0;
	public static final int BGM_ONCE = 1;
	public static final int BGM_NONE = 2;
	public static final int BGM_END = -1;

	/**
	 * ユーザ入力または自動操作による入力値をもとに、フレームごとのオブジェクトの更新を行う
	 *
	 * @param dt     デルタタイム
	 */
	void updator(double dt);

	/**
	 * フレームごとの再描画を行う
	 *
	 * @param g グラフィックスオブジェクト
	 */
	void paintComponent(Graphics g);

	/**
     * JLayeredPanelに追加するために自身のインスタンスを返す
     *
     * @return 自身のパネルインスタンス
     */
	JLayeredPane getPanel();

	/**
     * 自身の初期状態のインスタンスを返す
     *
     * @return 自身の初期状態のインスタンス
     */
	GameScene getNewScene();

	/**
     * 自身のシーンで使用するBGMのファイルパスを返す
     *
     * @return BGMのファイルパス
     */
	String getSound();

	/**
	 * <pre>
     * 自身のシーンで使用するBGMの再生モードを返す
     * 0: ループ再生(ループ区間指定可)
     * 1: 一度のみ再生
     * 2: 再生なし
     * </pre>
     * @return BGMのファイルパス
     */
	int getBgmMode();

	/**
     * 自身のシーンで使用するBGMの再生区間を返す
     *
     * @return BGM再生区間を表す始点と終点の値の組
     */
	Point getDuration();

	/**
     * 現在のウィンドウサイズを再設定する
     */
	void initPanel();

	/**
     * 各パラメータを初期化する
     */
	void initParam();

	/**
     * レイヤーシーンからのコールバックを受け付ける
     *
     * @param i レスポンスコード
     */
	void callback(int i);

	/**
     * キー設定の情報を返す
     *
     * @return キー設定オブジェクト
     */
	KeyController getKeyConfig();
}
