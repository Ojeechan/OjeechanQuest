package classes.scenes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.controllers.GameController;
import classes.controllers.SceneController;
import classes.controllers.SoundController;
import classes.utils.GeneralUtil;

import interfaces.GameScene;

/**
 * ゲーム起動後、初めに表示するロゴ画面を定義するゲームシーンオブジェクト
 *
 * @author Naoki Yoshikawa
 */
@SuppressWarnings("serial")
public class Introduction extends BaseSystemOperator implements GameScene {

	// ロゴ画像
	private BufferedImage logo;
	private BufferedImage logo2;

	// アニメーションの自動切り替えに使用するカウント
	private double animeCount;

	// アニメーションの自動切り替えに使用するカウント
	private double sceneCount;

	// シーン遷移するカウント数
	private int end;

	// ロゴを表示しているか
	private boolean logoFlg;

	// BGMの読み込み完了フラグ
	private boolean onLoad;

	/**
	 * 画像の読み込み、フラグの初期化
	 */
	public Introduction() {
		logo = GeneralUtil.readImage(ImageResource.Logo.INTRODUCTION.getValue());
		logo2= GeneralUtil.readImage(ImageResource.Logo.INTRODUCTION2.getValue());
		end = 30;
    	initParam();
    }

    /**
	 * フレームごとの再描画を行う
	 *
	 * @param g Graphicsオブジェクト
	 */
    public void paintComponent(Graphics g) {

    	g.setColor(Color.BLACK);
        g.fillRect(
        		0,
        		0,
        		(int) GameController.getWindow().getWindowWidth(),
        		(int) GameController.getWindow().getWindowHeight()
        		);

        // ベースのロゴを描画
        if(animeCount >= 1) {
	    	int drawX = (int) (Math.min(animeCount, logo.getWidth()));
	        int drawY = (int) (Math.min(animeCount, logo.getHeight()));

	        BufferedImage sub = logo.getSubimage(0, 0, (int) drawX, (int) drawY);

	        int drawWidth = (int) GameController.getWindow().getAbsPosX(sub.getWidth() * 4);
	        int drawHeight = (int) GameController.getWindow().getAbsPosY(sub.getHeight() * 4);

	        g.drawImage(
	        		sub,
	        		(int) ((GameController.getWindow().getWindowWidth()- drawWidth)/2),
	        		(int) ((GameController.getWindow().getWindowHeight()- drawHeight)/2),
	        		(int) drawWidth,
	        		(int) drawHeight,
	        		null
	        		);
        }

        // ロゴの表示タイミングになったら別のロゴも描画する
        if(logoFlg) {
        	int drawWidth = (int) GameController.getWindow().getAbsPosX(logo2.getWidth() * 4);
            int drawHeight = (int) GameController.getWindow().getAbsPosY(logo2.getHeight() * 4);
        	g.drawImage(
        			logo2,
        			(int) ((GameController.getWindow().getWindowWidth() - drawWidth)/2),
        			(int) ((GameController.getWindow().getWindowHeight()- drawHeight)/2),
        			drawWidth,
        			drawHeight,
        			null
        			);
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

    	// イントロのBGMが鳴り終わったタイミングでロゴ表示とSEを実行
    	if (onLoad && !SoundController.isPlaying()) {
    		if (!logoFlg){
        		new Thread(new SoundController.PlaySE(SoundResource.SE_OPENINGLOGO)).start();
        		logoFlg = true;

        	// ロード完了後、かつ一定時間の経過でタイトル画面へ遷移
    		} else if (sceneCount >= end){
    			GameController.getWindow().setGameScene(GameController.getScene(SceneController.TITLE));
    			GameController.getWindow().playBGM(GameController.getWindow().getBasePanel());
        	}
    		sceneCount = Math.min(sceneCount + 20 * dt, end);
    	}
    	animeCount = Math.min(animeCount + 20 * dt, logo.getWidth());
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
    	return new Title();
    }

    /**
     * 自身のシーンで使用するBGMのファイルパスを返す
     *
     * @return BGMのファイルパス
     */
    public String getSound() {
    	return SoundResource.BGM_INTRODUCTION;
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
		return GameScene.BGM_ONCE;
	}

    /**
     * 各パラメータを初期化する
     */
	public void initParam() {
		animeCount = sceneCount = 0;
    	logoFlg = false;
    	onLoad = false;
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
	 * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
	 *
	 * @param res 呼び出し元からのレスポンスコード
	 */
	public void callback(int res) {
		onLoad = true;
	}
}
