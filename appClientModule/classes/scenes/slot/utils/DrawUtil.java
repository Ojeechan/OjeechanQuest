package classes.scenes.slot.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import classes.controllers.GameController;
import classes.scenes.slot.assets.*;

/**
 * グラフィックスオブジェクトに対して描画を行うメソッドを集約したユーティリティクラス
 *
 * @author  Naoki Yoshikawa
 */
public class DrawUtil {

	/* スロットステージ */

	/**
   	 * スロットステージでスロットリールを描画する
   	 *
   	 * @param slot       スロットオブジェクト
   	 * @param shift      左リールの左端のX座標
   	 * @param baseHeight リールの上端のY座標
   	 * @param stretchX   リール図柄のX方向の拡大倍率
   	 * @param stretchY   リール図柄のY方向の拡大倍率
   	 * @param margin     リール間の間隔(px)
   	 * @param g          グラフィックスオブジェクト
   	 */
	public static void drawSlotReel(
			Slot slot,
			int shift,
			int baseHeight,
			int stretchX,
			int stretchY,
			int margin,
			Graphics g
			) {

		// リールの下地、一旦ベタ塗り　あとでパラメーター化
		g.setColor(new Color(130, 130, 170));
		g.fillRect(
				(int) GameController.getWindow().getAbsPosX(250),
				(int) GameController.getWindow().getAbsPosY(100),
				(int) GameController.getWindow().getAbsPosX(500),
				(int) GameController.getWindow().getAbsPosY(320)
				);

		Reel[] reels = slot.getReels();

		// リールごとに描画する
		for(int i = 0; i < reels.length; i++) {
			Reel reel = reels[i];
			int drawWidth = reel.getImage().getWidth() * stretchX;
			int drawHeight = reel.getReelSize() * stretchY;

			// i番目のリールの左端のX座標を算出
			int left = shift + (reel.getIconWidth() * stretchX + margin) * i;

			// リールの回転位置を取得して、イメージを折り返すかどうかをチェックする
			if(reel.getReelBottom() < reel.getImage().getHeight()) {
				g.drawImage(
					reel.getImage(),
					// 描画先の座標
					(int) GameController.getWindow().getAbsPosX(left),
					(int) GameController.getWindow().getAbsPosY(baseHeight),
					(int) GameController.getWindow().getAbsPosX(left + drawWidth),
					(int) GameController.getWindow().getAbsPosY(baseHeight + drawHeight),
					// 参照元の座標
					0,
					(int) reel.getReelTop(),
					reel.getIconWidth(),
					(int) reel.getReelBottom(),
					null
				);

				// 台枠が折り返している場合
			} else {
			    int splitTop = (reel.getImage().getHeight() - (int) reel.getReelTop()) * stretchY;
			    int splitBottom = reel.getReelSize() * stretchY - splitTop;
				g.drawImage(
					// 参照データ
					reel.getImage(),
					// 描画先の座標
					(int) GameController.getWindow().getAbsPosX(left),
					(int) GameController.getWindow().getAbsPosY(baseHeight),
					(int) GameController.getWindow().getAbsPosX(left + drawWidth),
					(int) GameController.getWindow().getAbsPosY(baseHeight + splitTop),
					// 参照元の座標
					0,
					(int) reel.getReelTop(),
					reel.getIconWidth(),
					reel.getImage().getHeight(),
					null
				);

				g.drawImage(
					// 参照データ
					reel.getImage(),
					// 描画先の座標
					(int) GameController.getWindow().getAbsPosX(left),
					(int) GameController.getWindow().getAbsPosY(baseHeight + splitTop),
					(int) GameController.getWindow().getAbsPosX(left + drawWidth),
					(int) GameController.getWindow().getAbsPosY(baseHeight + splitTop + splitBottom),
					// 参照元の座標
					0,
					0,
					reel.getIconWidth(),
					(int) reel.getReelBottom() - reel.getImage().getHeight(),
					null
				);
			}

			// 入賞時のみ発光した図柄を上書きする
			if(reel.getIsFlashing()) {
				for(int[] line: slot.getValidLine()) {

					// 成立ライン(上中下段のどこか)
					int pos = line[i];

					// チェリー用の回避策
					if(pos < 0) {
						continue;
					}

					// 描画位置の調整
					drawHeight = reel.getIconHeight() * stretchY;
					int iconPosY = reel.getAdjustedIndex(pos, reel.getIconHeight() / 2) * reel.getIconHeight();

					// 少し大きめにかぶせる場合設定する
					int scale = 0;

					g.drawImage(
							reel.getImageBright(),
							// 描画先の座標
							(int) GameController.getWindow().getAbsPosX(left - scale * stretchX),
							(int) GameController.getWindow().getAbsPosY(baseHeight + drawHeight * (1 + pos) - scale * stretchY),
							(int) GameController.getWindow().getAbsPosX(left + drawWidth + scale * stretchX),
							(int) GameController.getWindow().getAbsPosY(baseHeight + drawHeight * (2 + pos) + scale * stretchY),
							// 参照元の座標
							0,
							iconPosY,
							reel.getIconWidth(),
							iconPosY + reel.getIconHeight(),
							null
						);
				}
			}
		}
	}

	/**
   	 * スロットステージでスロットの台枠を描画する
   	 *
   	 * @param slot スロットオブジェクト
   	 * @param g    グラフィックスオブジェクト
   	 */
	public static void drawSlotFrame(Slot slot,  Graphics g) {
		int drawX = (int) GameController.getWindow().getAbsPosX(-18);
		int drawY = (int) GameController.getWindow().getAbsPosY(-87);
		g.drawImage(
				slot.getSlotFrame(),
				drawX,
				drawY,
				(int) GameController.getWindow().getAbsPosX(slot.getSlotFrame().getWidth()),
				(int) GameController.getWindow().getAbsPosY(slot.getSlotFrame().getHeight()),
				null
				);
	}

	/**
   	 * スロットステージでスロットのランプを描画する
   	 *
   	 * @param slot スロットオブジェクト
   	 * @param g    グラフィックスオブジェクト
   	 */
	public static void drawSlotLamp(Slot slot, Graphics g) {
		for (Lamp lamp: slot.getLamps()) {

			// もとは消灯時のイメージを使用する
			BufferedImage img = lamp.getImage();
			// ランプオンの時は最大値、または点滅モードの明るさを参照して明るくする
			BufferedImage tmp = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
			tmp = lamp.getLuminance().filter(img, tmp);
			g.drawImage(
					tmp,
					(int) GameController.getWindow().getAbsPosX(lamp.getX()),
					(int) GameController.getWindow().getAbsPosY(lamp.getY()),
					(int) GameController.getWindow().getAbsPosX(tmp.getWidth()),
					(int) GameController.getWindow().getAbsPosY(tmp.getHeight()),
					null
					);
			tmp.flush();

		}
	}

	/**
   	 * スロットステージでスロットの停止ボタンを描画する
   	 *
   	 * @param slot スロットオブジェクト
   	 * @param g    グラフィックスオブジェクト
   	 */
	public static void drawSlotButton(Slot slot, Graphics g) {
		for (Button button: slot.getButtons()) {
			BufferedImage img = button.getImage();
			if(button.getIsOn()) {
				BufferedImage tmp = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
				img = button.getLuminance().filter(img, tmp);
			}
			g.drawImage(
					img,
					(int) GameController.getWindow().getAbsPosX(button.getX()),
					(int) GameController.getWindow().getAbsPosY(button.getY()),
					(int) GameController.getWindow().getAbsPosX(img.getWidth()),
					(int) GameController.getWindow().getAbsPosY(img.getHeight()),
					null
					);
		}
	}

	/**
   	 * スロットステージでスロットのレバーを描画する
   	 *
   	 * @param slot スロットオブジェクト
   	 * @param g    グラフィックスオブジェクト
   	 */
	public static void drawSlotLever(Slot slot, Graphics g) {
		Lever lever = slot.getLever();
		double ratio = 1.5;
		BufferedImage img;
		if(lever.getIsDown()) {
			img = lever.getImageDown();
		} else {
			img = lever.getImageUp();
		}

		g.drawImage(
				img,
				(int) GameController.getWindow().getAbsPosX(lever.getX()),
				(int) GameController.getWindow().getAbsPosY(lever.getY()),
				(int) GameController.getWindow().getAbsPosX(img.getWidth() * ratio),
				(int) GameController.getWindow().getAbsPosY(img.getHeight() * ratio),
				null);
	}

	/**
   	 * スロットステージでスロットの7セグランプを描画する
   	 *
   	 * @param num         表示する数字
   	 * @param segFontList セグ用のフォント画像のリスト
   	 * @param rightPosX  セグを表示する右端のX座標
   	 * @param posY        セグを表示する上端のY座標
   	 * @param g           グラフィックスオブジェクト
   	 */
	public static void drawSeg(int num, List<BufferedImage> segFontList, int rightPosX, int posY, Graphics g) {

		rightPosX = (int) GameController.getWindow().getAbsPosX(rightPosX);
		posY = (int) GameController.getWindow().getAbsPosY(posY);

		// 表示0の場合はそのまま表示
		if(num == 0) {
			g.drawImage(
					segFontList.get(0),
					rightPosX,
					posY,
					(int) GameController.getWindow().getAbsPosX(segFontList.get(0).getWidth()),
					(int) GameController.getWindow().getAbsPosY(segFontList.get(0).getHeight()),
					null
					);
		}

		// 表示上の最大枚数は9999枚
		num = Math.min(num, 9999);

		int i = 0;
		// 10進数
		int unit = 10;

		//現在のコイン枚数を小さい位から右詰めで描画する
		while(num != 0) {
			int mod = Math.abs(num % unit);
			BufferedImage img = segFontList.get(mod);
			g.drawImage(
					img,
					(int) (rightPosX - GameController.getWindow().getAbsPosX(img.getWidth()) * i),
					posY,
					(int) GameController.getWindow().getAbsPosX(img.getWidth()),
					(int) GameController.getWindow().getAbsPosY(img.getHeight()),
					null
					);

			// マイナス値の場合は左端にマイナス記号をつける
			if(num > -unit && num < 0) {
				int leftend = segFontList.size() - 1;
				img = segFontList.get(leftend);
				g.drawImage(
						img,
						(int) (rightPosX - GameController.getWindow().getAbsPosX(img.getWidth()) * (i + 1)),
						posY,
						(int) GameController.getWindow().getAbsPosX(img.getWidth()),
						(int) GameController.getWindow().getAbsPosY(img.getHeight()),
						null
						);
			}

			num /= unit;
			i++;
		}
	}
}
