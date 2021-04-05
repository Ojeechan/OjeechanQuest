package classes.scenes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.controllers.FontController;
import classes.controllers.GameController;
import classes.controllers.KeyController;
import classes.controllers.ScriptController;
import classes.controllers.SoundController;
import classes.controllers.WindowController;
import classes.controllers.ScriptController.Script;
import classes.scenes.BaseSystemOperator;
import classes.scenes.vinyl.assets.Vinyl;
import classes.ui.StringSelectOption;
import classes.utils.GeneralUtil;

import interfaces.GameScene;

/**
 * レコード屋の買い物用ウィンドウを定義するクラス
 *
 * @author Naoki Yoshikawa
 */
public class ItemWindow extends BaseSystemOperator implements GameScene {

	// スクリプトを管理するオブジェクト
    private ScriptController script;

    // 現在のカーソルのあるインデックス
 	private int cursorVinylIndex;

 	// 店員キャラクター画像オブジェクト
 	private BufferedImage owner;

    // 曲目とレコードタイトルを表示するための選択リストオブジェクト
 	private List<StringSelectOption> itemList;

 	// 表示するスクリプトを表すインデックス
 	private int scriptIndex;

 	// コイン数を表示するのに使用する数字のフォント画像のリスト
 	private Map<Character, BufferedImage> numFontMap;

 	// UIテキスト用のオブジェクト
 	private StringSelectOption coin;

 	// アニメーション用のインデックス
 	private double count;

 	/**
 	 * 選択リスト、スクリプト、キーコンフィグの設定
 	 */
	public ItemWindow() {

		keyHelpList.add(new StringSelectOption(850, 20, FontController.Fonts.NORMAL, "←/→: せんたく", 16));
    	keyHelpList.add(new StringSelectOption(850, 36, FontController.Fonts.NORMAL, "C: キャンセル", 16));
    	keyHelpList.add(new StringSelectOption(850, 52, FontController.Fonts.NORMAL, "H:HELP ON/OFF", 16));
    	keyHelpList.add(new StringSelectOption(850, 68, FontController.Fonts.NORMAL, "ESC:PAUSE", 16));

		// キーコンフィグの追加
		keyConfig.getKeys().put(KeyEvent.VK_C, keyConfig.new Key(KeyController.DETECT_INITIAL_PRESS_ONLY));

		// 画像の読み込み
	    this.numFontMap = GeneralUtil.stringToImageMap(FontController.Fonts.NORMAL, "0123456789-");
		owner = GeneralUtil.readImage(ImageResource.VinylIcon.OWNER.getValue());

		coin = new StringSelectOption(
				760,
    			350,
				FontController.Fonts.NORMAL,
				"もちがね",
				16
    			);

		initParam();
	}

	/**
     * レコード一覧用のスクリプトを読み込む
     */
    private void setScriptList() {
    	// 店員のスクリプトを設定
		script = new ScriptController(
				410,
				340,
				310,
				170,
				ImageResource.VinylIcon.ITEMWINDOW.getValue()
    			);

		for(Vinyl v:GameController.getVinylList()) {
			script.setScript(
	    			24,
	    			v.getDsc(),
	    			FontController.Fonts.NORMAL
	    			);
		}

    	script.setScript(
    			24,
    			"",
    			FontController.Fonts.NORMAL
    			);
    }

	/**
     * レコード一覧用の選択リストオブジェクトを読み込む
     */
    private void setItemList() {
    	itemList = new ArrayList<StringSelectOption>();
    	List<Vinyl> vList = GameController.getVinylList();
    	for(int i = 0; i < vList.size(); i++) {
    		StringSelectOption title = new StringSelectOption(
	    			280,
	    			120,
					FontController.Fonts.NORMAL,
					vList.get(i).getTitle(),
					20
	    			);
    		itemList.add(title);
    	}
    }

	/**
	 * <pre>
	 * トラック用カーソルのインデックスを1つ進める
	 * 最大値の次は0に戻る
	 * </pre>
	 *
	 * @return 1つ進めた後のインデックス
	 */
	public int next() {
		cursorVinylIndex++;
    	return cursorVinylIndex %= GameController.getVinylList().size();
    }

	/**
	 * <pre>
	 * トラック用カーソルのインデックスを1つ戻す
	 * 0の次は最大インデックスに戻る
	 * </pre>
	 *
	 * @return 1つ戻した後のインデックス
	 */
    public int prev() {
    	cursorVinylIndex--;
    	return cursorVinylIndex < 0
    			? cursorVinylIndex = GameController.getVinylList().size() + cursorVinylIndex
    			: cursorVinylIndex;
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
				(int) GameController.getWindow().getAbsPosX(WindowController.WIDTH/4),
				(int) GameController.getWindow().getAbsPosY(30),
				(int) GameController.getWindow().getAbsPosX(480),
				(int) GameController.getWindow().getAbsPosY(480),
				null
				);

		// インデックスで指定したスクリプトを表示
		Script s = script.getScriptList().get(scriptIndex);
		GeneralUtil.drawScript(
				s,
				(int) (script.getX() + 5),
				(int) (script.getY() + 10),
				g
	            );

		// 店員キャラクターの描画
		double wave = GeneralUtil.getSinValue(System.currentTimeMillis() / 1000.0, 1.0, 0, 5);
		g.drawImage(
				owner,
				(int) GameController.getWindow().getAbsPosX(250),
				(int) GameController.getWindow().getAbsPosY(350 + wave),
				(int) GameController.getWindow().getAbsPosX(128),
				(int) GameController.getWindow().getAbsPosY(128),
				null
				);

		// ジャケットの描画
		BufferedImage jacket;
		List<Vinyl> vList = GameController.getVinylList();
		for(int i = 0; i < vList.size(); i++) {
    		jacket = vList.get(i).getJacket();
    		double drawWidth = jacket.getWidth()/4;
    		double drawHeight = jacket.getHeight()/4;
			if(cursorVinylIndex != i) {
				g.drawImage(
						jacket,
						(int) GameController.getWindow().getAbsPosX(350 + (jacket.getWidth()/6 * i) - drawWidth/2),
						(int) GameController.getWindow().getAbsPosY(250 - drawHeight/2),
    					(int) GameController.getWindow().getAbsPosX(drawWidth),
    					(int) GameController.getWindow().getAbsPosY(drawHeight),
    					null
    					);
    		}
		}

		// 選択レコードは最後に描画
		jacket = vList.get(cursorVinylIndex).getJacket();
		double ratio = GeneralUtil.getSinValue(count, 1, 1, 0.05) + 1;
		double drawWidth = jacket.getWidth()/3 * ratio;
		double drawHeight = jacket.getHeight()/3 * ratio;
		g.drawImage(
				jacket,
				(int) GameController.getWindow().getAbsPosX(350 + (jacket.getWidth()/6 * cursorVinylIndex) - drawWidth/2),
				(int) GameController.getWindow().getAbsPosY(250 - drawHeight/2),
				(int) GameController.getWindow().getAbsPosX(drawWidth),
				(int) GameController.getWindow().getAbsPosY(drawHeight),
				null
				);

		// 商品名の表示
		StringSelectOption item = itemList.get(cursorVinylIndex);
		GeneralUtil.drawImageString(
				item.getImageFont(),
				item.getX(),
				item.getY(),
				item.getSize(),
				GeneralUtil.ALIGN_LEFT,
				g
				);

		// 所持金の表示
		g.setColor(Color.BLACK);
		g.fillRect(
				(int) GameController.getWindow().getAbsPosX(750),
				(int) GameController.getWindow().getAbsPosY(340),
				(int) GameController.getWindow().getAbsPosX(200),
				(int) GameController.getWindow().getAbsPosY(160)
				);
		GeneralUtil.drawImageString(
				coin.getImageFont(),
				coin.getX(),
				coin.getY(),
				coin.getSize(),
				GeneralUtil.ALIGN_LEFT,
				g
				);
		GeneralUtil.drawDynamicString(
				Integer.toString(GameController.getCoin()),
				numFontMap,
				770,
				400,
				20,
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

	    // 左右キーはページを選択する
	    if (keyConfig.getKeys().get(KeyEvent.VK_LEFT).isPressed()) {
    		prev();
    		scriptIndex = cursorVinylIndex;
    		script.getScriptList().get(scriptIndex).resetIndex();
    		count = 0;
    		new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();
        } else if (keyConfig.getKeys().get(KeyEvent.VK_RIGHT).isPressed()) {
        	next();
        	scriptIndex = cursorVinylIndex;
        	script.getScriptList().get(scriptIndex).resetIndex();
        	count = 0;
    		new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();
        }


		if(keyConfig.getKeys().get(KeyEvent.VK_C).isPressed()) {
			keyConfig.releaseAll();
			GameController.getWindow().popScene();
			GameController.getWindow().callback(0);
		}

		// 操作キー一覧の表示
	    if(keyConfig.getKeys().get(KeyEvent.VK_H).isPressed()) {
			super.helpOn = !super.helpOn;
		}

	    Script s = script.getScriptList().get(scriptIndex);
	    s.proceedIndex(dt);

	    count = (count + dt * 9) % (2 * Math.PI);
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
    	return new ItemWindow();
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
		scriptIndex = 0;
		cursorVinylIndex = 0;
		helpOn = true;
		setItemList();
		setScriptList();
	}

	/**
	 * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
	 *
	 * @param res 呼び出し元からのレスポンスコード
	 */
	public void callback(int res) {
	}
}
