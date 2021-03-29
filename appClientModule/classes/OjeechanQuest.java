package classes;

import classes.controllers.GameController;
import classes.controllers.WindowController;

/**
 * アプリケーションのエントリーポイントクラス
 *
 * @author  Naoki Yoshikawa
 */
public class OjeechanQuest {

	/**
	 * mainメソッド
	 *
	 * @param args コマンドライン引数
	 */
    public static void main(String[] args) {
    	WindowController window = new WindowController();
    	new GameController(window);
    }
}