package classes.scenes.vinyl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.controllers.EffectController;
import classes.controllers.FontController;
import classes.controllers.GameController;
import classes.controllers.SceneController;
import classes.controllers.WindowController;
import classes.math.Vector2;
import classes.scenes.vinyl.assets.Player;
import classes.scenes.vinyl.utils.DrawUtil;
import classes.ui.StringSelectOption;
import classes.scenes.effects.Turntable1;
import classes.scenes.effects.Turntable2;
import classes.scenes.effects.Turntable3;
import classes.scenes.slot.SlotHall;
import classes.utils.GeneralUtil;

import interfaces.GameScene;

/**
 * <pre>
 * ターンテーブルステージの導入部分を定義するゲームシーンオブジェクト
 * クオータービューの実験
 * </pre>
 *
 * @author Naoki Yoshikawa
 **/
@SuppressWarnings("serial")
public class VinylShop extends BaseVinylOperator implements GameScene {

	/* 定数群 */
	private final int TILE_SIZE = 64; // 1タイルあたりの大きさ
	private final int TILE_NUM = 8;   // フィールドの1辺あたりのタイル数
	private final Vector2 ORIGIN = new Vector2(WindowController.WIDTH/2, 100); // クオータービュー上の原点の位置ベクトル
	private final Vector2 ANGLE = new Vector2(2, 1); // クオータービューのX軸を表す方向ベクトル

	// プレイヤーオブジェクト
	private Player player;

	// クオータービューの基底単位ベクトル
	private Vector2 axisX;
	private Vector2 axisY;

	// クオータービューのタイルごとの描画起点の位置ベクトル
	private Vector2 sx;
	private Vector2 sy;

	// クオータービューのタイルの高さを表す方向ベクトル
	private Vector2 h;

	// 各ステージオブジェクトの画像オブジェクト
	private BufferedImage[] boxes;
	private BufferedImage turntable;
	private BufferedImage cashier;
	private BufferedImage owner;
	private List<BufferedImage> owners;

	// 変数生成オブジェクト
	private Random r;

	private GameScene shopWindow;

	// ステージオブジェクトの高さ(Z成分)を表す配列
	private int[] heightMap = new int[] {
		    1, 0, 1, 1, 1, 1, 0, 1,
		    1, 0, 0, 0, 0, 0, 0, 1,
		    1, 0, 1, 1, 1, 1, 0, 1,
		    1, 0, 0, 0, 0, 0, 0, 0,
		    1, 0, 0, 0, 0, 0, 0, 0,
		    1, 0, 1, 1, 1, 1, 0, 1,
		    1, 0, 0, 0, 0, 0, 0, 1,
		    1, 0, 1, 1, 1, 1, 0, 1
	    };

	// ステージオブジェクトの配置を表す配列
	private int[] objMap = new int[] {
		    22, 0, 1, 1, 1, 1, 0, 2,
		    0, 0, 0, 0, 0, 0, 0, 2,
		    0, 0, 1, 1, 1, 1, 0, 2,
		    2, 0, 0, 0, 0, 0, 0, 0,
		    2, 0, 0, 0, 0, 0, 0, 0,
		    21, 0, 1, 1, 1, 1, 0, 2,
		    0, 0, 0, 0, 0, 0, 0, 2,
		    0, 0, 1, 1, 1, 1, 0, 2
	    };

	// 画面遷移を管理するオブジェクト
	private EffectController effect;

	/**
	 * <pre>
	 * キーコンフィグ、プレイヤースプライト、ステージオブジェクトの設定
	 * クオータービューの基底ベクトルの計算
	 * </pre>
	 */
	public VinylShop() {



		keyConfig.getKeys().put(KeyEvent.VK_W, keyConfig.new Key());
    	keyConfig.getKeys().put(KeyEvent.VK_S, keyConfig.new Key());
    	keyConfig.getKeys().put(KeyEvent.VK_A, keyConfig.new Key());
    	keyConfig.getKeys().put(KeyEvent.VK_D, keyConfig.new Key());

    	keyHelpList.add(new StringSelectOption(100, 400, FontController.Fonts.NORMAL, "W", 16));
    	keyHelpList.add(new StringSelectOption(80, 430, FontController.Fonts.NORMAL, "A", 16));
    	keyHelpList.add(new StringSelectOption(105, 430, FontController.Fonts.NORMAL, "S", 16));
    	keyHelpList.add(new StringSelectOption(130, 430, FontController.Fonts.NORMAL, "D", 16));
    	keyHelpList.add(new StringSelectOption(810, 350, FontController.Fonts.NORMAL, "ENTER: しらべる", 16));
    	keyHelpList.add(new StringSelectOption(120, 30, FontController.Fonts.NORMAL, "H:HELP ON/OFF", 16));
    	keyHelpList.add(new StringSelectOption(80, 50, FontController.Fonts.NORMAL, "ESC:PAUSE", 16));

    	player = Player.getDefault(
    			TILE_SIZE * TILE_NUM - 1,
    			(int) (TILE_SIZE * TILE_NUM/2)
    			);


    	double thetaX = Math.atan2(ANGLE.getY(), ANGLE.getX());
    	double thetaY = Math.atan2(ANGLE.getY(), -ANGLE.getX());

    	axisX = new Vector2(Math.cos(thetaX), Math.sin(thetaX));
    	axisY = new Vector2(Math.cos(thetaY), Math.sin(thetaY));

    	sx = axisX.copy().scalar(TILE_SIZE);
    	sy = axisY.copy().scalar(TILE_SIZE);
    	h = new Vector2(0, TILE_SIZE);

    	r = new Random();

    	boxes = new BufferedImage[] {
    			GeneralUtil.readImage(ImageResource.VinylIcon.VINYLBOX3_1.getValue()),
    			GeneralUtil.readImage(ImageResource.VinylIcon.VINYLBOX3_2.getValue()),
    			GeneralUtil.readImage(ImageResource.VinylIcon.VINYLBOX3_3.getValue()),
    			GeneralUtil.readImage(ImageResource.VinylIcon.VINYLBOX3_4.getValue()),
    			GeneralUtil.readImage(ImageResource.VinylIcon.VINYLBOX3_5.getValue()),
    			GeneralUtil.readImage(ImageResource.VinylIcon.VINYLBOX5_1.getValue()),
    			GeneralUtil.readImage(ImageResource.VinylIcon.VINYLBOX5_2.getValue()),
    			GeneralUtil.readImage(ImageResource.VinylIcon.VINYLBOX5_3.getValue()),
    			GeneralUtil.readImage(ImageResource.VinylIcon.VINYLBOX5_4.getValue()),
    			GeneralUtil.readImage(ImageResource.VinylIcon.VINYLBOX5_5.getValue())
    	};

    	for(int i = 0; i < objMap.length; i++) {
    		if(objMap[i] == 1) {
    			objMap[i] = r.nextInt(boxes.length - 1) + 1;
    		} else if(objMap[i] == 2) {
    			objMap[i] = r.nextInt(boxes.length - 1) + 11;
    		}
    	}

    	turntable = GeneralUtil.readImage(ImageResource.VinylIcon.TURNTABLE.getValue());
    	cashier = GeneralUtil.readImage(ImageResource.VinylIcon.CASHIER.getValue());
    	owner = GeneralUtil.readImage(ImageResource.VinylIcon.OWNER.getValue());

    	owners = new ArrayList<BufferedImage>();
    	for(int i = 0; i < 10; i++) {
    		owners.add(owner);
    	}

    	shopWindow = new ShopWindow();
	}

	/**
	 * フレームごとの再描画を行う
	 *
	 * @param g グラフィックスオブジェクト
	 */
    @Override
	public void paintComponent(Graphics g) {


    	g.setColor(new Color(30, 30, 30));
    	g.fillRect(
 				0,
 				0,
 				(int) GameController.getWindow().getWindowWidth(),
 				(int) GameController.getWindow().getWindowHeight()
 				);

    	GeneralUtil.rotateChaotic(
    			owners,
    			0,
    			0,
    			256,
    			System.currentTimeMillis()/1000.0 % (Math.PI * 2),
    			g
    			);

    	Vector2 nx;
		Vector2 ny;
		Vector2 start;
		Vector2 diagonal;
		Vector2 lifted;
    	for(int i = 0; i < TILE_NUM; i++) {
    		for(int j = 0; j < TILE_NUM; j++) {
    			nx = sx.copy().scalar(j);
    			ny = sy.copy().scalar(i);
    			diagonal = sx.add(sy);
    			start = ORIGIN.add(nx.add(ny));

    			lifted = start.sub(h);
    			g.setColor(
    					new Color(
    							(int) ((30*i + 30*j) * System.currentTimeMillis()/1000.0  % 256),
    							100,
    							100
    							)
    					);

    			Polygon p = new Polygon();

    	    	// 上面
    	    	p.reset();
    	    	p.addPoint(
    	    			(int) GameController.getWindow().getAbsPosX(lifted.getX()),
    	    			(int) GameController.getWindow().getAbsPosY(lifted.getY())
    	    			);
    	    	p.addPoint(
    	    			(int) GameController.getWindow().getAbsPosX(lifted.add(sx).getX()),
    	    			(int) GameController.getWindow().getAbsPosY(lifted.add(sx).getY())
    	    			);
    	    	p.addPoint(
    	    			(int) GameController.getWindow().getAbsPosX(lifted.add(diagonal).getX()),
    	    			(int) GameController.getWindow().getAbsPosY(lifted.add(diagonal).getY())
    	    			);

    	    	p.addPoint(
    	    			(int) GameController.getWindow().getAbsPosX(lifted.add(sy).getX()),
    	    			(int) GameController.getWindow().getAbsPosY(lifted.add(sy).getY())
    	    			);

    	    	g.fillPolygon(p);


    	    	// 左側面
    	    	g.setColor(Color.BLACK);
    	    	p.reset();
    	    	p.addPoint(
    	    			(int) GameController.getWindow().getAbsPosX(lifted.add(diagonal).getX()),
    	    			(int) GameController.getWindow().getAbsPosY(lifted.add(diagonal).getY())
    	    			);
    	    	p.addPoint(
    	    			(int) GameController.getWindow().getAbsPosX(start.add(diagonal).getX()),
    	    			(int) GameController.getWindow().getAbsPosY(start.add(diagonal).getY())
    	    			);
    	    	p.addPoint(
    	    			(int) GameController.getWindow().getAbsPosX(start.add(sy).getX()),
    	    			(int) GameController.getWindow().getAbsPosY(start.add(sy).getY())
    	    			);
    	    	p.addPoint(
    	    			(int) GameController.getWindow().getAbsPosX(lifted.add(sy).getX()),
    	    			(int) GameController.getWindow().getAbsPosY(lifted.add(sy).getY())
    	    			);

    	    	g.fillPolygon(p);

    	    	// 右側面
    	    	g.setColor(new Color(60, 60, 60));
    	    	p.reset();
    	    	p.addPoint(
    	    			(int) GameController.getWindow().getAbsPosX(lifted.add(diagonal).getX()),
    	    			(int) GameController.getWindow().getAbsPosY(lifted.add(diagonal).getY())
    	    			);
    	    	p.addPoint(
    	    			(int) GameController.getWindow().getAbsPosX(start.add(diagonal).getX()),
    	    			(int) GameController.getWindow().getAbsPosY(start.add(diagonal).getY())
    	    			);
    	    	p.addPoint(
    	    			(int) GameController.getWindow().getAbsPosX(start.add(sx).getX()),
    	    			(int) GameController.getWindow().getAbsPosY(start.add(sx).getY())
    	    			);
    	    	p.addPoint(
    	    			(int) GameController.getWindow().getAbsPosX(lifted.add(sx).getX()),
    	    			(int) GameController.getWindow().getAbsPosY(lifted.add(sx).getY())
    	    			);

    	    	g.fillPolygon(p);
    		}
    	}

    	double wave = GeneralUtil.getSinValue(System.currentTimeMillis() / 1000.0, 1.0, 0, 12);
    	g.drawImage(
    			owner,
    			(int) GameController.getWindow().getAbsPosX(400),
    			(int) GameController.getWindow().getAbsPosY(wave),
    			(int) GameController.getWindow().getAbsPosX(496),
    			(int) GameController.getWindow().getAbsPosY(96 + wave),
    			0,
    			0,
    			owner.getWidth(),
    			owner.getHeight(),
    			null
    			);


    	int row = (int) (player.getPosVec().getY() / TILE_SIZE);
    	int col = (int) (player.getPosVec().getX() / TILE_SIZE);

    	for(int i = 0; i < TILE_NUM; i++) {
    		for(int j = 0; j < TILE_NUM; j++) {
    			nx = sx.copy().scalar(j);
    			ny = sy.copy().scalar(i);
    			start = ORIGIN.add(nx.add(ny));
    			lifted = start.sub(h);

    			// 該当するタイル位置での描画を、プレイヤーの描画タイミングとする
    			// プレイヤーは自身の位置より同じか大きいX、Yを持つオブジェクトの背後に描画される
    			if(i == row && j == col) {
    				DrawUtil.drawSpriteVector(player, ORIGIN.sub(h), axisX.copy(), axisY.copy(), g);
    			}

    			switch(objMap[i*TILE_NUM + j]) {

    			    // Y軸の向きのレコードボックス
    			    case 1: case 2: case 3: case 4:case 5:
	    			case 6: case 7: case 8: case 9: case 10:
	    				//BufferedImage box = boxes[objMap[i*TILE_NUM + j] - 1];
	    				BufferedImage box = boxes[r.nextInt(10)];
	    				g.drawImage(
	    	    				box,
	    	    				(int) GameController.getWindow().getAbsPosX(lifted.getX() - box.getWidth()/2),
	    	    				(int) GameController.getWindow().getAbsPosY(lifted.getY() - TILE_SIZE/2),
	    	    				(int) GameController.getWindow().getAbsPosX(box.getWidth()),
	    	    				(int) GameController.getWindow().getAbsPosY(box.getHeight()),
	    	    				null
	    	    				);
		    			break;

		    		// X軸の向きのレコードボックス
	    			case 11: case 12: case 13: case 14: case 15:
	    			case 16: case 17: case 18: case 19: case 20:
	    				box = boxes[r.nextInt(10)];
	    				g.drawImage(
	    	    				box,
	    	    				(int) GameController.getWindow().getAbsPosX(lifted.getX() - box.getWidth()/2),
	    	    				(int) GameController.getWindow().getAbsPosY(lifted.getY() - TILE_SIZE/2),
	    	    				(int) GameController.getWindow().getAbsPosX(lifted.getX() - box.getWidth()/2 + box.getWidth()),
	    	    				(int) GameController.getWindow().getAbsPosY(lifted.getY() - TILE_SIZE/2 + box.getHeight()),
	    	    				box.getWidth(),
	    	    	            0,
	    	    	            0,
	    	    	            box.getHeight(),
	    	    	            null
	    	    				);
		    			break;

		    		// ターンテーブル
	    			case 21:
	    				g.drawImage(
	    	    				turntable,
	    	    				(int) GameController.getWindow().getAbsPosX(lifted.getX() - 110/2 * 3),
	    	    				(int) GameController.getWindow().getAbsPosY(lifted.getY() - TILE_SIZE/2),
	    	    				(int) GameController.getWindow().getAbsPosX(turntable.getWidth()),
	    	    				(int) GameController.getWindow().getAbsPosY(turntable.getHeight()),
	    	    				null
	    	    				);
	    				break;

	    			// レジカウンター
	    			case 22:
	    				g.drawImage(
	    	    				cashier,
	    	    				(int) GameController.getWindow().getAbsPosX(lifted.getX() - 110/2 * 3),
	    	    				(int) GameController.getWindow().getAbsPosY(lifted.getY() - TILE_SIZE/2 * 4),
	    	    				(int) GameController.getWindow().getAbsPosX(cashier.getWidth()),
	    	    				(int) GameController.getWindow().getAbsPosY(cashier.getHeight()),
	    	    				null
	    	    				);
	    				break;
	    			default:
    	    	}
    		}
    	}

    	if(helpOn) {
    		GeneralUtil.drawSelectOptions(keyHelpList, GeneralUtil.ALIGN_CENTER, g);
    	}
    }

    /**
     * <pre>
     * プレイヤーの移動先の位置に障害物があるかどうかを返す
     * 障害物がある場合 true
     * </pre>
     *
     * @return 移動先の位置に障害物があるかどうか
     */
    private boolean isCollided() {
    	Vector2 dest = player.getPosVec().add(player.getDirVec());

    	int fieldSize = TILE_SIZE * TILE_NUM;
    	double row =  dest.getY() / TILE_SIZE;
    	double col = dest.getX() / TILE_SIZE;

    	if(dest.getX() >= 0
    	    && dest.getX() < fieldSize
    	    && dest.getY() >= 0
    	    && dest.getY() < fieldSize
    		&& row * TILE_NUM + col >= 0
    		&& heightMap[(int) row * TILE_NUM + (int) col] <= player.getZ()
    		){
    		return false;
    	} else {
    		return true;
    	}
    }

    private Point getTilePos() {
    	Vector2 pos = player.getPosVec();
    	return new Point((int) (pos.getX()/TILE_SIZE), (int) (pos.getY()/TILE_SIZE));
    }

    /**
     * プレイヤーの角度に応じた画像を表すラベルを設定する
     */
    private void switchImage() {

    	if(player.getAngle() == -Math.PI/2) {
    		player.switchLabel(ImageResource.BACK);
    	} else if (player.getAngle() == Math.PI/2) {
    		player.switchLabel(ImageResource.FRONT);
    	}  else if (player.getAngle() == Math.PI/4 || player.getAngle() == Math.PI/4 * 3) {
    		player.switchLabel(ImageResource.HALFDOWN);
    	} else if (player.getAngle() == -Math.PI/4 || player.getAngle() == Math.PI/4 * 5) {
    		player.switchLabel(ImageResource.HALFUP);
    	} else if (player.getAngle() == 0 || player.getAngle() == Math.PI) {
    		player.switchLabel(ImageResource.Player1RunRight.LABEL.getValue());
    	}
    }

    /**
	 * エフェクトのプロシージャを設定する
	 */
	public void setEffect() {
		effect = new EffectController(GameController.getScene(SceneController.TURNTABLE));
		effect.addEffect(new Turntable1(effect));
    	effect.addEffect(new Turntable2(effect));
    	effect.addEffect(new Turntable3(effect));
	}

	public void callback() {
		keyConfig.releaseAll();
		this.requestFocusInWindow();
		this.validate();
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
    	// ポーズ
	    if(keyConfig.getKeys().get(KeyEvent.VK_ESCAPE).isPressed()) {
	    	GameController.getWindow().pushScene(GameController.getScene(SceneController.PAUSE));
	    	keyConfig.releaseAll();
	    }

    	if(keyConfig.getKeys().get(KeyEvent.VK_A).isPressed()) {
			//this.height += 1;
    		player.move(new Vector2(-1, 0), dt);
		} else if(keyConfig.getKeys().get(KeyEvent.VK_D).isPressed()) {
			//this.height -= 1;
			player.move(new Vector2(1, 0), dt);
		}

    	if(keyConfig.getKeys().get(KeyEvent.VK_W).isPressed()) {
			//this.height += 1;
    		player.move(new Vector2(0, -1), dt);
		} else if(keyConfig.getKeys().get(KeyEvent.VK_S).isPressed()) {
			//this.height -= 1;
			player.move(new Vector2(0, 1), dt);
		}

    	// 特定座標かつ特定の向きの時にEnterを押す
    	if(keyConfig.getKeys().get(KeyEvent.VK_ENTER).isPressed()) {

    		// ターンテーブル
    		if(
    			getTilePos().equals(new Point(1, 6))
    		    && ( player.getAngle() >= -Math.PI/2 && player.getAngle() <= -Math.PI/4
    			    || player.getAngle() >= Math.PI && player.getAngle() <= Math.PI/2 * 3
    			)
	    	){
	        	effect.process();

	        // 店員
    		} else if(
    			(getTilePos().equals(new Point(1, 0))
    					&& ( player.getAngle() == -Math.PI/2
    					  || player.getAngle() >= Math.PI && player.getAngle() <= Math.PI/2 * 3
    					)
    				)
    			|| (getTilePos().equals(new Point(1, 1))
    					&& ( player.getAngle() >= -Math.PI/2 && player.getAngle() >= 0
					    || player.getAngle() == Math.PI/2 * 3
					)
				)
	    	){
    			keyConfig.releaseAll();
    			shopWindow.initParam();
    			GameController.getWindow().pushScene(shopWindow);
    			super.helpOn = false;
	    	}
    	}

    	// 操作キー一覧の表示
	    if(keyConfig.getKeys().get(KeyEvent.VK_H).isPressed()) {
			super.helpOn = !super.helpOn;
		}

    	player.setAngle();
    	switchImage();

    	if(!isCollided()) {
    		player.proceed(dt);
    	} else {
    		player.stop();
    	}
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
    	return new SlotHall();
    }

    /**
     * 自身のシーンで使用するBGMのファイルパスを返す
     * @see interfaces.GameScene
     *
     * @return BGMのファイルパス
     */
    public String getSound() {
    	return SoundResource.BGM_VINYLSHOP;
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
		return GameScene.BGM_LOOP;
	}

	/**
     * 自身のシーンで使用するBGMの再生区間を返す
     *
     * @return BGM再生区間を表す始点と終点の値の組
     */
	public Point getDuration() {
		int start = 57000;
		return new Point(start, start + 473000);
	}

    /**
     * 各パラメータを初期化する
     */
	public void initParam() {
		helpOn = true;
		player.initParam();
		effect.initEffect();
		keyConfig.releaseAll();
	}

	/**
	 * タイトル画面にてはじめからを選択時の、ゲーム状態リセット用の初期化メソッド
	 */
	public void restock() {
		ShopWindow sw = (ShopWindow) shopWindow;
		sw.restock();
	}


	/**
	 * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
	 *
	 * @param res 呼び出し元からのレスポンスコード
	 */
	public void callback(int res) {

	}
}
