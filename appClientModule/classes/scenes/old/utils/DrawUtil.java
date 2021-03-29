package classes.scenes.old.utils;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import classes.containers.Background;
import classes.controllers.GameController;
import classes.controllers.MapController;
import classes.scenes.old.assets.*;
import classes.scenes.old.assets.Player;
import classes.utils.GeneralUtil;
import interfaces.Calculation;

/**
 * グラフィックスオブジェクトに対して描画を行うメソッドを集約したユーティリティクラス
 *
 * @author  Naoki Yoshikawa
 */
public class DrawUtil {

	/* アクションステージ */

	/**
   	 * スプライトに設定された画像を描画する
   	 *
   	 * @param sprite  描画対象のスプライトオブジェクト
   	 * @param offsetX プレーヤーを画面中央に表示するためのX方向の補正値
   	 * @param offsetY プレーヤーを画面中央に表示するためのY方向の補正値
   	 * @param g       グラフィックスオブジェクト
   	 */
	public static void drawSprite(BaseSprite sprite, int offsetX, int offsetY, Graphics g) {
    	BufferedImage image = sprite.getImage();
    	g.drawImage(
			image,
			(int) GameController.getWindow().getAbsPosX(sprite.getImageLeftX() + offsetX),
			(int) GameController.getWindow().getAbsPosY(sprite.getImageTopY() + offsetY),
			(int) GameController.getWindow().getAbsPosX(sprite.getImageWidth()),
			(int) GameController.getWindow().getAbsPosY(sprite.getImageHeight()),
            null
        );
    }

	/**
   	 * スプライトに設定された画像をY軸対称に反転させて描画する
   	 *
   	 * @param sprite  描画対象のスプライトオブジェクト
   	 * @param offsetX プレーヤーを画面中央に表示するためのX方向の補正値
   	 * @param offsetY プレーヤーを画面中央に表示するためのY方向の補正値
   	 * @param g       グラフィックスオブジェクト
   	 */
	public static void drawVerticalFlip(BaseSprite sprite, int offsetX, int offsetY, Graphics g) {

        BufferedImage image = sprite.getImage();
        g.drawImage(
            image,
            (int) sprite.getImageLeftX() + offsetX,
            (int) sprite.getImageTopY() + offsetY,
            (int) sprite.getImageRightX() + offsetX,
            (int) sprite.getImageBaseY() + offsetY,
            image.getWidth(),
            0,
            0,
            image.getHeight(),
            null
        );
    }

	/**
   	 * スプライトに設定された画像をX軸対称に反転させて描画する
   	 *
   	 * @param sprite  描画対象のスプライトオブジェクト
   	 * @param offsetX プレーヤーを画面中央に表示するためのX方向の補正値
   	 * @param offsetY プレーヤーを画面中央に表示するためのY方向の補正値
   	 * @param g       グラフィックスオブジェクト
   	 */
	public static void drawHorizontalFlip(BaseSprite sprite, int offsetX, int offsetY, Graphics g) {
		BufferedImage image = sprite.getImage();
		g.drawImage(
    		image,
    		(int) sprite.getImageLeftX() + offsetX,
            (int) sprite.getImageTopY() + offsetY,
            (int) sprite.getImageRightX() + offsetX,
            (int) sprite.getImageBaseY() + offsetY,
            0,
            image.getHeight(),
            image.getWidth(),
            0,
            null
        );
	}

	/**
   	 * スプライトに設定された画像を180度に回転させて描画する
   	 *
   	 * @param sprite  描画対象のスプライトオブジェクト
   	 * @param offsetX プレーヤーを画面中央に表示するためのX方向の補正値
   	 * @param offsetY プレーヤーを画面中央に表示するためのY方向の補正値
   	 * @param g       グラフィックスオブジェクト
   	 */
	public static void drawBothFlip(BaseSprite sprite, int offsetX, int offsetY, Graphics g) {
		BufferedImage image = sprite.getImage();
		g.drawImage(
    		image,
    		(int) sprite.getImageLeftX() + offsetX,
            (int) sprite.getImageTopY() + offsetY,
            (int) sprite.getImageRightX() + offsetX,
            (int) sprite.getImageBaseY() + offsetY,
            image.getWidth(),
            image.getHeight(),
            0,
            0,
            null
        );
	}

	/**
   	 * スプライトに設定された画像を指定角度分回転させて描画する
   	 *
   	 * @param sprite  描画対象のスプライトオブジェクト
   	 * @param offsetX プレーヤーを画面中央に表示するためのX方向の補正値
   	 * @param offsetY プレーヤーを画面中央に表示するためのY方向の補正値
   	 * @param g       グラフィックスオブジェクト
   	 */
	public static void drawRotated(BaseSprite sprite, int offsetX, int offsetY, Graphics g) {
		// 回転軸をスプライトの中央に取得
		double anchorX = sprite.getEntityLeftX() + sprite.getActualWidth() / 2 + offsetX;
		double anchorY = sprite.getEntityTopY() + sprite.getActualHeight() / 2 + offsetY;
		GeneralUtil.setRotationDeg(sprite.getDegree(), anchorX, anchorY, g);
		drawSprite(sprite, offsetX, offsetY, g);
		GeneralUtil.resetRotation(g);
    }

	/**
   	 * スプライトに設定された画像をY軸対称に反転させた上で指定角度分回転させて描画する
   	 *
   	 * @param sprite  描画対象のスプライトオブジェクト
   	 * @param offsetX プレーヤーを画面中央に表示するためのX方向の補正値
   	 * @param offsetY プレーヤーを画面中央に表示するためのY方向の補正値
   	 * @param g       グラフィックスオブジェクト
   	 */
	public static void drawRotatedVerticalFlip(BaseSprite sprite, int offsetX, int offsetY, Graphics g) {
		// 回転軸をスプライトの中央に取得
		double anchorX = sprite.getEntityLeftX() + sprite.getActualWidth() / 2;
		// 反転させるためスプライト画像の右側を基準にピクセル数の差分を求める
		double gap = sprite.getImageRightX() - anchorX;
    	anchorX = sprite.getImageLeftX() + gap + offsetX;
		double anchorY = sprite.getEntityTopY() + sprite.getActualHeight() / 2 + offsetY;
		GeneralUtil.setRotationDeg(sprite.getDegree(), anchorX, anchorY, g);
		drawVerticalFlip(sprite, offsetX, offsetY, g);
		GeneralUtil.resetRotation(g);
    }

	/**
   	 * プレイヤースプライトのジャンプ中の順回転(反時計回り)を描画する
   	 *
   	 * @param sprite  描画対象のプレイヤースプライトオブジェクト
   	 * @param offsetX プレーヤーを画面中央に表示するためのX方向の補正値
   	 * @param offsetY プレーヤーを画面中央に表示するためのY方向の補正値
   	 * @param g       グラフィックスオブジェクト
   	 */
	public static void drawPlayerJump(Player sprite, int offsetX, int offsetY, Graphics g) {
		// 回転軸をスプライトの中央に取得
		double anchorX = sprite.getEntityLeftX() + sprite.getActualWidth() / 2 + offsetX;
		double anchorY = sprite.getEntityTopY() + sprite.getActualHeight() / 2 + offsetY;
		// ジャンプ中の回転速度を参照する
		GeneralUtil.setRotationDeg(sprite.rotateJump(), anchorX, anchorY, g);
		drawSprite(sprite, offsetX, offsetY, g);
		GeneralUtil.resetRotation(g);
    }

	/**
   	 * プレイヤースプライトのジャンプ中の逆回転(時計回り)を描画する
   	 *
   	 * @param sprite  描画対象のプレイヤースプライトオブジェクト
   	 * @param offsetX プレーヤーを画面中央に表示するためのX方向の補正値
   	 * @param offsetY プレーヤーを画面中央に表示するためのY方向の補正値
   	 * @param g       グラフィックスオブジェクト
   	 */
	public static void drawPlayerJumpVerticalFlip(Player sprite, int offsetX, int offsetY, Graphics g) {
		// 回転軸をスプライトの中央に取得
		double anchorX = sprite.getEntityLeftX() + sprite.getActualWidth() / 2;
		// 反転させるためスプライト画像の右側を基準にピクセル数の差分を求める
		double gap = sprite.getImageRightX() - anchorX;
    	anchorX = sprite.getImageLeftX() + gap + offsetX;
		double anchorY = sprite.getEntityTopY() + sprite.getActualHeight() / 2 + offsetY;
		// ジャンプ中の回転速度を参照する
		GeneralUtil.setRotationDeg(sprite.rotateJump(), anchorX, anchorY, g);
		drawVerticalFlip(sprite, offsetX, offsetY, g);
		GeneralUtil.resetRotation(g);
    }

	/**
   	 * プレイヤースプライトが敵と衝突して吹き飛ばされる際の画像を描画する
   	 *
   	 * @param player  描画対象のプレイヤースプライトオブジェクト
   	 * @param offsetX プレーヤーを画面中央に表示するためのX方向の補正値
   	 * @param offsetY プレーヤーを画面中央に表示するためのY方向の補正値
   	 * @param g       グラフィックスオブジェクト
   	 */
	public static void drawPlayerDying(Player player, int offsetX, int offsetY,  Graphics g) {

		BufferedImage image = player.getImage();

		// 回転軸をスプライトの中央に取得
		double anchorX = player.getDeadX() + player.getImageWidth() / 2 + offsetX;
		double anchorY = player.getDeadBaseY() +  player.getImageHeight() / 2 + offsetY;
		GeneralUtil.setRotationDeg(player.getDegree(), anchorX, anchorY, g);

		// 描画
		g.drawImage(
			image,
			(int) player.getDeadX() + offsetX,
            (int) player.getDeadTopY() + offsetY,
            player.getImageWidth(),
            player.getImageHeight(),
	        null
		);

		GeneralUtil.resetRotation(g);
    }

	/**
   	 * プレイヤーが幽霊になっている間の死体画像を描画する
   	 *
   	 * @param player  描画対象のプレイヤースプライトオブジェクト
   	 * @param offsetX プレーヤーを画面中央に表示するためのX方向の補正値
   	 * @param offsetY プレーヤーを画面中央に表示するためのY方向の補正値
   	 * @param image   死体に使う画像オブジェクト
   	 * @param g       グラフィックスオブジェクト
   	 */
	public static void drawDeadBody(Player player, int offsetX, int offsetY, BufferedImage image, Graphics g) {
		// 描画
		g.drawImage(
			image,
			(int)player.getDeadX() + offsetX,
			(int)player.getDeadBaseY() + offsetY,
			player.getImageWidth(),
			player.getImageHeight(),
			null
		);
	}

	/**
   	 * プレイヤーが幽霊になっている間の死体画像を描画する
   	 *
   	 * @param target         シーンオブジェクト(Stage1, Titleなど)
   	 * @param backgroundList 重ねて描画する背景画像のリスト
   	 * @param g              グラフィックスオブジェクト
   	 */
	public static void drawBackground(Calculation target, List<Background> backgroundList, Graphics g) {
		Player player = target.getPlayer();
		MapController map = target.getMap();
		// オフセット値を1/nにすることでスクロール速度を調整
		// セット値の大きさに比例してスクロールが遅くなる
		for(Background background: backgroundList) {

			// 描画範囲の左端と右端のX座標を算出
			int cameraOffset = GeneralUtil.getCameraOffset(
					(int) player.getImageLeftX(),
					(int) GameController.getWindow().getWindowWidth(),
					map.getWidth()
					);
			int leftEnd = (int) -cameraOffset / background.getScrollRate() % background.getImage().getWidth();
			int rightEnd = (int) (leftEnd + GameController.getWindow().getWindowWidth());

			// 描画範囲が背景画像の右端より左に収まっている場合
			if(rightEnd < background.getImage().getWidth()) {
				g.drawImage(
					background.getImage(),
					0,
					0,
					(int) GameController.getWindow().getWindowWidth(),
					(int) GameController.getWindow().getWindowHeight(),
					leftEnd,
					0,
					rightEnd,
					(int) GameController.getWindow().getWindowHeight(),
					null
				);

			// 描画範囲が背景画像の右端より先にある場合、左端から折り返して描画する
			} else {
				g.drawImage(
					background.getImage(),
					0,
					0,
					background.getImage().getWidth() - leftEnd,
					(int) GameController.getWindow().getWindowHeight(),
					leftEnd,
					0,
					background.getImage().getWidth(),
					(int) GameController.getWindow().getWindowHeight(),
					null
				);

				g.drawImage(
					background.getImage(),
					background.getImage().getWidth() - leftEnd + 1,
					0,
					(int) GameController.getWindow().getWindowWidth(),
					(int) GameController.getWindow().getWindowHeight(),
					0,
					0,
					rightEnd - background.getImage().getWidth(),
					(int) GameController.getWindow().getWindowHeight(),
					null
				);
			}
		}
	}

	/**
   	 * アクションステージでプレイヤーがゴールした際のアニメーションを描画する
   	 *
   	 * @param target シーンオブジェクト(Stage1, Titleなど)
   	 * @param startX プレーヤースプライトを描画するX座標
   	 * @param g      グラフィックスオブジェクト
   	 */
	public static void drawGoalMotion(Calculation target, double startX, Graphics g) {
		Player player = target.getPlayer();
		BufferedImage image = player.getImage();
		int middleHeight =
				(int) (GameController.getWindow().getWindowHeight() / 2
						+ player.getImageHeight()
						- (player.getTransparencyTop() * player.getHeightRatio())
						);
		g.drawImage(
			image,
			(int) startX - player.getActualWidth() / 2,
			// 描画するY座標は画面の中央
			middleHeight,
			player.getImageWidth(),
			player.getImageHeight(),
			null
		);
	}

	/**
   	 * アクションステージのマップーデータから地形を描画する
   	 *
   	 * @param map     ステージの地形情報をもつStageBuilderオブジェクト
   	 * @param offsetX プレーヤーを画面中央に表示するためのX方向の補正値
   	 * @param offsetY プレーヤーを画面中央に表示するためのY方向の補正値
   	 * @param g       グラフィックスオブジェクト
   	 */
	public static void drawMap(MapController map, int offsetX, int offsetY, Graphics g) {
        // オフセットを元に描画範囲を求める
        int firstTileX = GeneralUtil.pixelsToTiles(-offsetX);
        int lastTileX = firstTileX + GeneralUtil.pixelsToTiles(GameController.getWindow().getWindowWidth()) + 1;
        // 描画範囲がマップの大きさより大きくならないように調整
        lastTileX = Math.min(lastTileX, map.getColumn());

        int firstTileY = 0;
		int lastTileY = 14 + MapController.TILE_SIZE;
		// 描画範囲がマップの大きさより大きくならないように調整
        lastTileY = Math.min(lastTileY, map.getRow());
        char[][] mapData = map.getMapData();
        Map<Character, BufferedImage> assetMap = map.getAssetMap();

        for (int i = firstTileY; i < lastTileY; i++) {
            for (int j = firstTileX; j < lastTileX; j++) {
                // mapの値に応じて画像を描く
            	if (assetMap.containsKey(mapData[i][j])) {
            		g.drawImage(
        				assetMap.get(mapData[i][j]),
        				GeneralUtil.tilesToPixels(j) + offsetX,
        				GeneralUtil.tilesToPixels(i) + offsetY,
        				null
    				);
                }
            }
        }
    }

	/**
   	 * シーン間の移行時に流れるアイキャッチシーンのアニメーションを描画する
   	 *
   	 * @param player プレイヤースプライトオブジェクト
   	 * @param x      プレイヤースプライトを描画するX座標
   	 * @param g      グラフィックスオブジェクト
   	 */
	public static void drawEyeCatchMotion(Player player, int x, Graphics g) {
		BufferedImage image = player.getImage();
		g.drawImage(
				image,
				x - player.getActualWidth() / 2,
                (int) (GameController.getWindow().getWindowHeight()/2 - (int)(player.getTransparencyTop() * player.getHeightRatio())),
				player.getImageWidth(),
				player.getImageHeight(),
				null
			);
	}
}
