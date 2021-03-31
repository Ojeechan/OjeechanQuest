package classes.scenes.slot.assets;

import java.util.HashMap;
import java.util.Map;

import classes.constants.SoundResource;

/**
 * 各フラグの当選確率分布を決定するモードを管理するクラス
 *
 * @author Naoki Yoshikawa
 */
public class Mode {

	/* 定数群 */
	// 図柄の種類を表す文字列定数
	public static final int FREEZE = 0; // フリーズフラグ
	public static final int BONUS  = 1; // ボーナスフラグ
	public static final int CHERRY = 2; // チェリーフラグ
	public static final int SUIKA  = 3; // スイカフラグ
	public static final int BELL   = 4; // ベルフラグ
	public static final int REPLAY = 5; // リプレイフラグ
	public static final int HAZURE = 6; // 上記以外 = はずれフラグ

	// モードに設定するフラグ情報の配列
	private FlagTable[] flagTables;

	// ボーナス中のBGMのマップ
	private Map<Integer, MusicTable> musics;

	/**
	 * フラグ配列、ボーナス中BGMの設定
	 *
	 * @param flagTables フラグ情報の配列
	 * @param musics     ボーナス中のBGMのマップ
	 */
	public Mode(FlagTable[] flagTables, Map<Integer, MusicTable> musics) {
		this.flagTables = flagTables;
		this.musics = musics;
	}

	/**
	 * モードに設定されたフラグの配列を返す
	 *
	 * @return フラグの配列
	 */
	public FlagTable[] getFlagTables() {
		return this.flagTables;
	}

	/**
	 * モードに設定されたボーナス中のBGMのマップを返す
	 *
	 * @return ボーナスBGMのマップ
	 */
	public Map<Integer, MusicTable> getMusics() {
		return this.musics;
	}

	/*
	 * FlagTable オブジェクト
	 * 第一引数から
	 * 確率
	 * 入賞図柄
	 * 有効ライン
	 * 払い出し枚数
	 * 移行先モード(ボーナスであればフラグ成立時、子役であれば重複当選時など)
	 * 成立フラグの種類(String)
	 */

	/**
	 * 通常時のテーブルを返すファクトリーメソッド
	 *
	 * @return 通常時のモードテーブル
	 */
	public static Mode getNormal() {
		FlagTable[] ft = new FlagTable[] {
				new FlagTable(1.0/1.0, 0.0, null, null, 2000, 1, FREEZE, SoundResource.SE_FREEZE_STOP),
		    	new FlagTable(1.0/200.0, 0.0, new int[]{5,5,5}, getAllLine(), 300, 1, BONUS, ""),
		    	new FlagTable(1.0/200.0, 0.0, new int[]{6,6,6}, getAllLine(), 250, 2, BONUS, ""),
		    	new FlagTable(1.0/300.0, 0.0, new int[]{5,5,4},  new int[][] { new int[] {1,1,1} }, 150, 3, BONUS, ""),
		    	new FlagTable(1.0/300.0, 0.0, new int[]{6,6,4},  getAllLine(), 150, 4, BONUS, ""),
		    	new FlagTable(1.0/25.0, 1.0/20.0, new int[]{2,1,0},  new int[][] { new int[] {2,1,1}, {0,1,1}}, 3, 6, CHERRY, SoundResource.SE_CHERRY),
		    	new FlagTable(1.0/50.0, 1.0/4.0, new int[]{3,3,3},  new int[][] { new int[] {0,1,2}, {2,1,0} }, 15, 7, SUIKA, SoundResource.SE_SUIKA),
		    	// リプベルも外れと同様制御させればいいので(要は打ち手が外せない)本来はチェックは要らない
		    	// ただ一旦外せる役と同等のロジックに流す
		    	new FlagTable(1.0/8.0, 0.0, new int[]{1,1,1},  new int[][] { new int[] {0,1,2} }, 9, 0, BELL, SoundResource.SE_BELL),
		    	new FlagTable(1.0/3.0, 0.0, new int[]{0,0,0},  new int[][] { new int[] {1,1,1} }, 0, 0, REPLAY, SoundResource.SE_REPLAY),
		    	// はずれのみ有効ライン等の考えに則すかどうか考える
		    	// (リール制御ではずせばいいので入賞チェックが必要ないため)
		    	new FlagTable(1.0, 0.0, new int[]{0,0,1},  new int[][] { new int[] {1,1,1} }, 0, 0, HAZURE, "")
		    };

		Map<Integer, MusicTable> musics = new HashMap<Integer, MusicTable>();
	    musics.put(0, MusicTable.getFanfare());
	    musics.put(1, MusicTable.getEdm());
	    musics.put(2, MusicTable.getFanfare());
	    musics.put(3, MusicTable.getEdm());

	    return new Mode(ft, musics);
	}

	/**
	 * 赤BIG成立時のモードテーブルを返すファクトリーメソッド
	 *
	 * @return 赤BIG成立時のモードテーブル
	 */
	public static Mode getRedBig() {
		FlagTable[] ft = new FlagTable[] {
		    	new FlagTable(1.0/2.0, 0.0, new int[]{5,5,5}, getAllLine(), 300, 5, BONUS, ""),
		    	new FlagTable(1.0/25.0, 0.0, new int[]{2,1,0},  new int[][] { new int[] {2,1,1} }, 3, 1, CHERRY, SoundResource.SE_CHERRY),
		    	new FlagTable(1.0/50.0, 0.0, new int[]{3,3,3},  new int[][] { new int[] {0,1,2} }, 15, 1, SUIKA, SoundResource.SE_SUIKA),
		    	// リプベルも外れと同様制御させればいいので(要は打ち手が外せない)本来はチェック入らない
		    	// ただ一旦外せる役と同等のロジックに流す
		    	new FlagTable(1.0/8.0, 0.0, new int[]{1,1,1},  new int[][] { new int[] {0,1,2} }, 9, 1, BELL, SoundResource.SE_BELL),
		    	new FlagTable(1.0/3.0, 0.0, new int[]{0,0,0},  new int[][] { new int[] {1,1,1} }, 0, 1, REPLAY, SoundResource.SE_REPLAY),
		    	// はずれのみ有効ライン等の考えに則すかどうか考える
		    	// (リール制御ではずせばいいので入賞チェックが必要ないため)
		    	new FlagTable(1.0, 0.0, new int[]{0,0,1},  new int[][] { new int[] {1,1,1} }, 0, 1, HAZURE, "")
		    };

		Map<Integer, MusicTable> musics = new HashMap<Integer, MusicTable>();
	    musics.put(0, MusicTable.getFanfare());

	    return new Mode(ft, musics);
	}

	/**
	 * 青BIG成立時のモードテーブルを返すファクトリーメソッド
	 *
	 * @return 青BIG成立時のモードテーブル
	 */
	public static Mode getBlueBig() {
		FlagTable[] ft = new FlagTable[] {
		    	new FlagTable(1.0/2.0, 0.0, new int[]{6,6,6}, getAllLine(), 250, 5, BONUS, ""),
		    	new FlagTable(1.0/25.0, 0.0, new int[]{2,1,0},  new int[][] { new int[] {2,1,1} }, 3, 2, CHERRY, SoundResource.SE_CHERRY),
		    	new FlagTable(1.0/50.0, 0.0, new int[]{3,3,3},  new int[][] { new int[] {2,1,0} }, 15, 2, SUIKA, SoundResource.SE_SUIKA),
		    	// リプベルも外れと同様制御させればいいので(要は打ち手が外せない)本来はチェック入らない
		    	// ただ一旦外せる役と同等のロジックに流す
		    	new FlagTable(1.0/8.0, 0.0, new int[]{1,1,1},  new int[][] { new int[] {0,1,2} }, 9, 2, BELL, SoundResource.SE_BELL),
		    	new FlagTable(1.0/3.0, 0.0, new int[]{0,0,0},  new int[][] { new int[] {1,1,1} }, 0, 2, REPLAY, SoundResource.SE_REPLAY),
		    	// はずれのみ有効ライン等の考えに則すかどうか考える
		    	// (リール制御ではずせばいいので入賞チェックが必要ないため)
		    	new FlagTable(1.0, 0.0, new int[]{0,0,1},  new int[][] { new int[] {1,1,1} }, 0, 2, HAZURE, "")
		    };

		Map<Integer, MusicTable> musics = new HashMap<Integer, MusicTable>();
	    musics.put(0, MusicTable.getEdm());

	    return new Mode(ft, musics);
	}

	/**
	 * 赤REG成立時のモードテーブルを返すファクトリーメソッド
	 *
	 * @return 赤REG成立時のモードテーブル
	 */
	public static Mode getRedReg() {
		FlagTable[] ft = new FlagTable[] {
		    	new FlagTable(1.0/2.0, 0.0, new int[]{5,5,4}, new int[][] { new int[] {1,1,1} }, 150, 5, BONUS, ""),
		    	new FlagTable(1.0/25.0, 0.0, new int[]{2,1,0}, new int[][] { new int[] {2,1,1} }, 3, 3, CHERRY, SoundResource.SE_CHERRY),
		    	new FlagTable(1.0/50.0, 0.0, new int[]{3,3,3}, new int[][] { new int[] {2,1,0} }, 15, 3, SUIKA, SoundResource.SE_SUIKA),
		    	// リプベルも外れと同様制御させればいいので(要は打ち手が外せない)本来はチェック入らない
		    	// ただ一旦外せる役と同等のロジックに流す
		    	new FlagTable(1.0/8.0, 0.0, new int[]{1,1,1},  new int[][] { new int[] {0,1,2} }, 9, 3, BELL, SoundResource.SE_BELL),
		    	new FlagTable(1.0/3.0, 0.0, new int[]{0,0,0},  new int[][] { new int[] {1,1,1} }, 0, 3, REPLAY, SoundResource.SE_REPLAY),
		    	// はずれのみ有効ライン等の考えに則すかどうか考える
		    	// (リール制御ではずせばいいので入賞チェックが必要ないため)
		    	new FlagTable(1.0, 0.0, new int[]{0,0,1},  new int[][] { new int[] {1,1,1} }, 0, 3, HAZURE, "")
		    };

		Map<Integer, MusicTable> musics = new HashMap<Integer, MusicTable>();
	    musics.put(0, MusicTable.getFanfare());

	    return new Mode(ft, musics);
	}

	/**
	 * 青REG成立時のモードテーブルを返すファクトリーメソッド
	 *
	 * @return 青REG成立時のモードテーブル
	 */
	public static Mode getBlueReg() {
		FlagTable[] ft = new FlagTable[] {
		    	new FlagTable(1.0/2.0, 0.0, new int[]{6,6,4}, getAllLine(), 150, 5, BONUS, ""),
		    	new FlagTable(1.0/25.0, 0.0, new int[]{2,1,0},  new int[][] { new int[] {2,1,1} }, 3, 4, CHERRY, SoundResource.SE_CHERRY),
		    	new FlagTable(1.0/50.0, 0.0, new int[]{3,3,3},  new int[][] { new int[] {2,1,0} }, 15, 4, SUIKA, SoundResource.SE_SUIKA),
		    	// リプベルも外れと同様制御させればいいので(要は打ち手が外せない)本来はチェック入らない
		    	// ただ一旦外せる役と同等のロジックに流す
		    	new FlagTable(1.0/8.0, 0.0, new int[]{1,1,1},  new int[][] { new int[] {0,1,2} }, 9, 4, BELL, SoundResource.SE_BELL),
		    	new FlagTable(1.0/3.0, 0.0, new int[]{0,0,0},  new int[][] { new int[] {1,1,1} }, 0, 4, REPLAY, SoundResource.SE_REPLAY),
		    	// はずれのみ有効ライン等の考えに則すかどうか考える
		    	// (リール制御ではずせばいいので入賞チェックが必要ないため)
		    	new FlagTable(1.0, 0.0, new int[]{0,0,1},  new int[][] { new int[] {1,1,1} }, 0, 4, HAZURE, "")
		    };

		Map<Integer, MusicTable> musics = new HashMap<Integer, MusicTable>();
	    musics.put(0, MusicTable.getEdm());

	    return new Mode(ft, musics);
	}

	/**
	 * ボーナス入賞時のモードテーブルを返すファクトリーメソッド
	 *
	 * @return ボーナス入賞時のモードテーブル
	 */
	public static Mode getBonus() {
		FlagTable[] ft = new FlagTable[] {
		    	new FlagTable(1.0, 0.0, new int[]{1,1,1}, new int[][] { new int[] {1,1,1} }, 15, 0, BELL, SoundResource.SE_BELL),
		    };

		Map<Integer, MusicTable> musics = new HashMap<Integer, MusicTable>();

	    return new Mode(ft, musics);
	}

	/**
	 * チェリー重複時のモードテーブルを返すファクトリーメソッド
	 *
	 * @return チェリー重複時のモードテーブル
	 */
	public static Mode getCherryBonus() {
		FlagTable[] ft = new FlagTable[] {
		    	new FlagTable(1.0/3.0, 0.0, new int[]{5,5,5}, getAllLine(), 300, 1, BONUS, ""),
		    	new FlagTable(1.0/3.0, 0.0, new int[]{6,6,6}, getAllLine(), 250, 2, BONUS, ""),
		    	new FlagTable(1.0/6.0, 0.0, new int[]{5,5,4}, new int[][] { new int[] {1,1,1} }, 150, 3, BONUS, ""),
		    	new FlagTable(1.0/6.0, 0.0, new int[]{6,6,4}, getAllLine(), 150, 4, BONUS, "")
		    };

		Map<Integer, MusicTable> musics = new HashMap<Integer, MusicTable>();
	    musics.put(0, MusicTable.getFanfare());
	    musics.put(1, MusicTable.getEdm());
	    musics.put(2, MusicTable.getFanfare());
	    musics.put(3, MusicTable.getEdm());

	    return new Mode(ft, musics);
	}

	/**
	 * スイカ重複時のモードテーブルを返すファクトリーメソッド
	 *
	 * @return スイカ重複時のモードテーブル
	 */
	public static Mode getSuikaBonus() {
		FlagTable[] ft = new FlagTable[] {
		    	new FlagTable(1.0/3.0, 0.0, new int[]{5,5,5}, new int[][] { new int[] {2,2,2} }, 300, 1, BONUS, ""),
		    	new FlagTable(1.0/2.5, 0.0, new int[]{6,6,6}, new int[][] { new int[] {0,0,0} }, 250, 2, BONUS, ""),
		    	new FlagTable(1/7.5, 0.0, new int[]{5,5,4},  new int[][] { new int[] {1,1,1} }, 150, 3, BONUS, ""),
		    	new FlagTable(1/7.5, 0.0, new int[]{6,6,4},  new int[][] { new int[] {1,1,1} }, 150, 4, BONUS, "")
		    };

		Map<Integer, MusicTable> musics = new HashMap<Integer, MusicTable>();
	    musics.put(0, MusicTable.getFanfare());
	    musics.put(1, MusicTable.getEdm());
	    musics.put(2, MusicTable.getFanfare());
	    musics.put(3, MusicTable.getEdm());

	    return new Mode(ft, musics);
	}

	/**
	 * 5ラインすべてを取得する
	 *
	 * @return 5ラインの定義の配列
	 */
	private static int[][] getAllLine () {
		return new int[][] { new int[] {2,2,2}, new int[] {1,1,1}, new int[] {0,0,0}, new int[] {0,1,2}, new int[] {2,1,0} };
	}
}
