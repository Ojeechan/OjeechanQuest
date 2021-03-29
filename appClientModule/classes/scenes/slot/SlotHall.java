package classes.scenes.slot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.controllers.EffectController;
import classes.controllers.FontController;
import classes.controllers.GameController;
import classes.controllers.SceneController;
import classes.controllers.WindowController;
import classes.scenes.effects.*;
import classes.math.*;
import classes.scenes.slot.assets.Player;
import classes.ui.StringSelectOption;
import classes.scenes.slot.utils.DrawUtil;
import classes.utils.GeneralUtil;

import interfaces.GameScene;

/**
 * <pre>
 * スロットステージの導入パートを定義するゲームシーンオブジェクト
 * 擬似3Dビューの実験
 * </pre>
 *
 * @author Naoki Yoshikawa
 **/
@SuppressWarnings("serial")
public class SlotHall extends BaseSlotOperator implements GameScene {

	/* 定数群 */
	private final int FIELD_SIZE = 640;    // 座標管理用のフィールドマップのサイズ
	private final int SLOT_SIZE = 1;       // フィールドマップ内でのスロットオブジェクトのサイズ
	private final int WALL_HEIGHT = 60000; // 3Dビュー表示上の壁の高さ
	private final int SPEED = 150;         // プレイヤーの移動速度
	private final int ADJUST = 100;        // スロット画像が視角端で消えるのを防ぐための、余分な光線の数
	private final int ENCOUNT = 100;       // スロット画像との接触判定が有効になる範囲

	// スロット画像オブジェクト
	private BufferedImage imgSlot;

	// 1人称視点を受け持つプレイヤーオブジェクト
	private Player player;

	// ステージを囲む壁をベクトルで表す
	private Ray[] walls;

	// スロットオブジェクトの位置をベクトルで表す
	private Ray slot;

	// チョップアニメーション中かどうか
	private boolean isChopping = false;

	// チョップアニメーションのフレームカウントと上限値
	private int c = 0;
	private int endp = 30;

	// チョップアニメーションの表示位置の座標
	private double chopX = 0;
	private double chopY = 0;

	// チョップ画像オブジェクト
	private BufferedImage chopImg;

    // シーン遷移間のエフェクト
	private EffectController effect;

	// スロット画像に最初にヒットした視線
	private Vector2 slotRay;

	// ヒットした視線のインデックス(左端が0, 右端がn)
	private int column;

	// スロット画像のサイズ補正
	private double adj;

	/**
	 * <pre>
	 * 画像の読み込み、キーコンフィグ、プレイヤー、シーン遷移エフェクトの設定
	 * 壁、スロットをベクトルで定義
	 * </pre>
	 */
    public SlotHall() {

    	// 適当に画面中央に初期サイズの画像を表示
    	imgSlot = GeneralUtil.readImage(ImageResource.SlotIcon.SLOT.getValue());
    	chopImg = GeneralUtil.readImage(ImageResource.SlotIcon.CHOP.getValue());

    	keyConfig.getKeys().put(KeyEvent.VK_W, keyConfig.new Key());
    	keyConfig.getKeys().put(KeyEvent.VK_S, keyConfig.new Key());
    	keyConfig.getKeys().put(KeyEvent.VK_A, keyConfig.new Key());
    	keyConfig.getKeys().put(KeyEvent.VK_D, keyConfig.new Key());


    	walls = new Ray[4];
    	walls[0] = new Ray(new Vector2(0, 0), new Vector2(0, FIELD_SIZE));
    	walls[1] = new Ray(new Vector2(0, 0), new Vector2(FIELD_SIZE, 0));
    	walls[2] = new Ray(new Vector2(FIELD_SIZE, FIELD_SIZE), new Vector2(0, -FIELD_SIZE));
    	walls[3] = new Ray(new Vector2(FIELD_SIZE, FIELD_SIZE), new Vector2(-FIELD_SIZE, 0));

    	slot = new Ray(new Vector2(FIELD_SIZE/2, FIELD_SIZE/3), new Vector2(SLOT_SIZE, 0));

    	player = new Player(FIELD_SIZE/2, FIELD_SIZE/6 * 5);

    	slotRay = null;
    	column = -1;
    	adj = 0.0;

    	keyHelpList.add(new StringSelectOption(100, 400, FontController.Fonts.NORMAL, "W", 16));
    	keyHelpList.add(new StringSelectOption(80, 430, FontController.Fonts.NORMAL, "A", 16));
    	keyHelpList.add(new StringSelectOption(105, 430, FontController.Fonts.NORMAL, "S", 16));
    	keyHelpList.add(new StringSelectOption(130, 430, FontController.Fonts.NORMAL, "D", 16));
    	keyHelpList.add(new StringSelectOption(790, 430, FontController.Fonts.NORMAL, "←", 16));
    	keyHelpList.add(new StringSelectOption(830, 430, FontController.Fonts.NORMAL, "→", 16));
    	keyHelpList.add(new StringSelectOption(810, 350, FontController.Fonts.NORMAL, "ENTER", 16));
    	keyHelpList.add(new StringSelectOption(120, 30, FontController.Fonts.NORMAL, "H:HELP ON/OFF", 16));
    	keyHelpList.add(new StringSelectOption(80, 50, FontController.Fonts.NORMAL, "ESC:PAUSE", 16));
    }

    /**
	 * フレームごとの再描画を行う
	 *
	 * @param g グラフィックスオブジェクト
	 */
    @Override
	public void paintComponent(Graphics g) {

    	WindowController w = GameController.getWindow();

    	// 天井
    	g.setColor(new Color(30, 0, 0));
		g.fillRect(
				0,
				0,
				(int) w.getAbsPosX(WindowController.WIDTH),
				(int) w.getAbsPosY(WindowController.HEIGHT / 2)
				);


		// 床
		g.setColor(new Color(80, 0, 0));
		g.fillRect(
				0,
				(int) w.getAbsPosY(WindowController.HEIGHT / 2),
				(int) w.getAbsPosX(WindowController.WIDTH),
				(int) w.getAbsPosY(WindowController.HEIGHT)
				);

		// 画面中央の高さの座標
    	int centerY = (int) (WindowController.HEIGHT / 2);

		// レイトレーシングの実験
		// 視野角を光線で等分して、光線間の角度を求める
		double rayAngle = player.getViewAngle() / (double) player.getRayNum();

		// 壁との交点を検証する光線を左から順に設定
		double rayDirection = player.getDirection();

		// スロット画像に最初にヒットした視線
		slotRay = null;
		// ヒットした視線のインデックス(左端が0, 右端がn)
		column = -1;
		// スロット画像のサイズ補正
		adj = Math.cos(rayDirection - player.getDirection());

		// 視角のうち、中央より右半分
		// 右端が1つ不足するため[player.getRayNum() + ADJUST]としている
		for(int i = 0; i < player.getRayNum() + ADJUST; i++) {
			calcRay(rayDirection, rayAngle, centerY, i, g);
		}

		// 視角のうち、中央より左半分
		// 左端から画像が消えることを防ぐため[player.getRayNum() + ADJUST]としている
		for(int i = 1; i < player.getRayNum() + ADJUST; i++) {
			calcRay(rayDirection, rayAngle, centerY, -i, g);
		}

		// スロットマシーンの描画
		if(slotRay != null) {
			double adjMag = slotRay.mag() * adj;
			double wave = GeneralUtil.getSinValue(System.currentTimeMillis() / 1000.0, 1.0, 0, 50) * 100 / adjMag;
			double shadow = GeneralUtil.getSinValue(System.currentTimeMillis() / 1000.0, 1.0, 10.0, 0.1);
			double slotWidth = 50000 / adjMag;
			double slotHeight = 50000 / adjMag ;
			double shadowWidth = 35000 / adjMag * shadow;
			double shadowHeight = 4000 / adjMag * shadow;
			double drawX = (double) (WindowController.WIDTH / (player.getRayNum() * 2) * column - slotWidth / 2);
			double drawY = centerY - slotHeight / 2 + wave;

			g.setColor(Color.BLACK);

			// スロットの影の描画
			g.fillOval(
				(int) w.getAbsPosX(drawX + (slotWidth - shadowWidth) / 1.8),
				(int) w.getAbsPosY(centerY + 60000 / adjMag / 2),
				(int) w.getAbsPosX(shadowWidth),
				(int) w.getAbsPosY(shadowHeight)
				);

			// スロットマシーンの描画
			g.drawImage(
				imgSlot,
				(int) w.getAbsPosX(drawX),
				(int) w.getAbsPosY(drawY),
				(int) w.getAbsPosX(drawX + slotWidth),
				(int) w.getAbsPosY(drawY + slotHeight),
				0,
				0,
				imgSlot.getWidth(),
				imgSlot.getHeight(),
				null
				);
		}

		// チョップの描画
		if(isChopping) {
			g.drawImage(
				chopImg,
				(int) w.getAbsPosX(chopX),
				(int) w.getAbsPosY(chopY),
				(int) w.getAbsPosX(chopImg.getWidth()),
				(int) w.getAbsPosY(chopImg.getHeight()),
				null
				);
		}

		if(helpOn) {
			GeneralUtil.drawSelectOptions(keyHelpList, GeneralUtil.ALIGN_CENTER, g);
    	}
    }

    /**
     * 壁の定義ベクトルと視線ベクトルから壁を描画する
     *
     * @param rayDirection プレイヤーの正面の方向の角度(rad)
     * @param rayAngle     計算対象の光線の正面からの角度(rad)
     * @param centerY      ゲーム画面の高さの半分
     * @param i            視線のインデックス
     * @param g            グラフィックスオブジェクト
     *
     * @return 計算に使用した視線ベクトル
     */
    private void calcRay(double rayDirection, double rayAngle, double centerY, int i, Graphics g) {
    	Ray r = new Ray(
				player.getPosVec().copy(),
				new Vector2(Math.cos(rayDirection + rayAngle * i), Math.sin(rayDirection + rayAngle * i)).scalar(1000.0)
				);

		Vector2 minVec = null;

		// 四方の壁と光線の交点を調べる
		for(Ray wall: walls) {
			Vector2 v = intersect(r, wall);
			if(v != null) {

				// 複数の交点がある場合最短距離を採用する
				if(minVec == null || v.sub(r.getStartPos()).mag() < minVec.mag()) {
					minVec = v.sub(r.getStartPos());
				}
			}
		}

		if(minVec != null) {
			double drawWidth = (double) (WindowController.WIDTH / (player.getRayNum() * 2 + 1));
			double drawHeight = WALL_HEIGHT / (minVec.mag() * Math.cos(rayDirection + rayAngle * i - player.getDirection()));
			double shade = minVec.mag() / 1200 * Math.sqrt(2);

			// 遠くの壁ほど暗くする
			Color c = new Color(
					150 - Math.min(150, (int) (150 * shade)),
					0,
					30 - Math.min(30, (int) (30 * shade))
					);

		    g.setColor(c);
		    g.fillRect(
		    	(int) GameController.getWindow().getAbsPosX(drawWidth * (player.getRayNum() + i)),
		    	(int) GameController.getWindow().getAbsPosY(centerY - drawHeight / 2),
		    	(int) Math.max(1, GameController.getWindow().getAbsPosX(drawWidth + 1)),
		    	(int) GameController.getWindow().getAbsPosY(drawHeight)
		    	);
		}

		Vector2 v = intersect(r, slot);
		if(v != null) {
			slotRay = v.sub(r.getStartPos());
			column = player.getRayNum() + i;
			adj = Math.cos(rayDirection + rayAngle * i - player.getDirection());
		}
    }

    /**
     * 2本の光線の交点を求める
     *
     * @param r1 光線1
     * @param r2 光線2
     *
     * @return 交点の位置ベクトル
     */
    private Vector2 intersect(Ray r1, Ray r2) {

    	// 一方の光線の傾き、通る1点のXY座標
    	double slope1 = r1.getSlope();
    	double x1     = r1.getPosVec().getX();
    	double y1     = r1.getPosVec().getY();

    	// 他方の光線の傾き、通る1点のXY座標
    	double slope2 = r2.getSlope();
    	double x2     = r2.getPosVec().getX();
    	double y2     = r2.getPosVec().getY();

    	// 2直線の交点のX座標
    	double x = (slope1 * x1 - slope2 * x2 - y1 + y2) / (slope1 - slope2);

    	// 2直線の交点のY座標
    	double y = slope1 * (x - x1) + y1;

    	// 線分の範囲内かどうかを判断
    	if(
	    	x > Math.min(r1.getStartPos().getX(), r1.getEndPos().getX())
	        && x < Math.max(r1.getStartPos().getX(), r1.getEndPos().getX())
	        && x > Math.min(r2.getStartPos().getX(), r2.getEndPos().getX())
	        && x < Math.max(r2.getStartPos().getX(), r2.getEndPos().getX())
    	) {
    		return new Vector2(x, y);
    	}

    	return null;
    }

    /**
     * チョップアニメーションの表示位置の座標を設定する
     */
    private void chop() {
    	if(c > endp) {
    		isChopping = false;
    		c = 0;
    	}

    	chopX(c);
    	chopY(c);

    	c++;
    }

    /**
     * チョップアニメーションのX座標用関数
     *
     * @param t タイムパラメータ
     */
    private void chopX(int t) {
    	chopX = 700 - t * 10;
    }

    /**
     * チョップアニメーションのY座標用関数
     *
     * @param t タイムパラメータ
     */
    private void chopY(int t) {
    	chopY = -1000 * (1.0 / (double)t + 1) + 1500;
    }

    /**
	 * エフェクトのプロシージャを設定する
	 */
	public void setEffect() {
		effect = new EffectController(GameController.getScene(SceneController.SLOT));
		effect.addEffect(new Encount1(effect));
    	effect.addEffect(new Encount2(effect));
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

    	player.stop();

    	if(player.getPosVec().sub(slot.getPosVec()).mag() < ENCOUNT
    			&& GameController.getWindow().isEmptyStack()
    			) {
    		if(!effect.isRemained()) {
    			effect.initEffect();
    		}
    		keyConfig.releaseAll();
    		effect.process();
    	}

    	/* キーイベント処理 */
		// 中央の視線に対する前後移動
    	if(keyConfig.getKeys().get(KeyEvent.VK_W).isPressed()) {
			player.moveVertical(SPEED * dt);
		} else if (keyConfig.getKeys().get(KeyEvent.VK_S).isPressed()) {
			player.moveVertical(-SPEED * dt);
		}

    	// 中央の視線に対する左右移動
		if(keyConfig.getKeys().get(KeyEvent.VK_A).isPressed()) {
			player.moveHorizontal(SPEED * dt);
		} else if (keyConfig.getKeys().get(KeyEvent.VK_D).isPressed()) {
    		player.moveHorizontal(-SPEED * dt);
		}

		// 視点の回転
		if(keyConfig.getKeys().get(KeyEvent.VK_LEFT).isPressed()) {
			player.turn(-Math.PI/3 * dt);
		} else if (keyConfig.getKeys().get(KeyEvent.VK_RIGHT).isPressed()) {
			player.turn(Math.PI/3 * dt);
		}

		// チョップ
		if(keyConfig.getKeys().get(KeyEvent.VK_ENTER).isPressed()) {
			isChopping = true;
			c = 0;
		}

		if(isChopping) {
			chop();
		}

		// ポーズ
	    if(keyConfig.getKeys().get(KeyEvent.VK_ESCAPE).isPressed()) {
	    	GameController.getWindow().pushScene(GameController.getScene(SceneController.PAUSE));
	    	keyConfig.releaseAll();
	    }

        // 操作キー一覧の表示
	    if(keyConfig.getKeys().get(KeyEvent.VK_H).isPressed()) {
			super.helpOn = !super.helpOn;
		}

		/* 壁の当たり判定 */
		// 今フレームでの移動ベクトルを取得
		Ray moveRay = player.getMoveRay();

		// 移動が発生している場合のみチェックする
		if(moveRay.getDirVec().mag() > 0) {
			for(Ray wall: walls) {
				// 交点の位置ベクトル
				Vector2 v = intersect(moveRay, wall);
				if(v != null) {
					// 壁との交点までの方向ベクトル forward vector
					Vector2 fwdVec = v.sub(moveRay.getStartPos());

					// 壁の法線ベクトル orthonormal vector
					Vector2 normVec = wall.getOrthonorm();

					// 方向ベクトルを法線ベクトルへ投影
					double alpha = fwdVec.scalar(-1).dot(wall.getOrthonorm());

					// 壁と平行なベクトル parallel vector
					Vector2 parVec = fwdVec.add(normVec.scalar(alpha));
					parVec = parVec.scalar(1/parVec.mag());
					player.adjustPos(parVec);

					// 衝突する壁は常に一つ?角は？
					break;
				}
			}
		}

		// 仮の移動ベクトルを最終確定
		player.proceed();
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
    	return SoundResource.BGM_HALL;
    }

    /**
	 * <pre>
     * 自身のシーンで使用するBGMの再生モードを返す
     * 0: ループ再生(ループ区間指定可)
     * 1: 一度のみ再生
     * 2: 再生なし
     * </pre>
     *
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
		return new Point(0, GameScene.BGM_END);
	}

    /**
     * 各パラメータを初期化する
     */
	public void initParam() {
		effect.initEffect();
		slotRay = null;
    	column = -1;
    	adj = 0.0;
    	player.initParam();
    	keyConfig.releaseAll();
	}

	/**
	 * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
	 *
	 * @param res 呼び出し元からのレスポンスコード
	 */
	public void callback(int res) {

	}
}
