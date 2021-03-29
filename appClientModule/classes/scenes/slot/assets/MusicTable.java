package classes.scenes.slot.assets;

import classes.constants.SoundResource;

/**
 * ボーナス中のBGMを定義するクラス
 *
 * @author Naoki Yoshikawa
 */
public class MusicTable {

	// ループ再生の開始/終了位置
	private int start;
	private int end;

	// BGM音源ファイルのパス
	private String path;

	/**
	 * ループ再生の位置、音源ファイルの設定
	 *
	 * @param start ループ再生の開始位置
	 * @param end   ループ再生の終了位置
	 * @param path  音源ファイルのパス
	 */
	public MusicTable(int start, int end, String path) {
		this.start = start;
		this.end = end;
		this.path = path;
	}

	/**
	 * ループ再生の開始位置を返す
	 *
	 * @return ループ再生の開始位置
	 */
	public int getStartTime() {
		return this.start;
	}

	/**
	 * ループ再生の終了位置を返す
	 *
	 * @return ループ再生の終了位置
	 */
	public int getEndTime() {
		return this.end;
	}

	/**
	 * 音源ファイルのパスを返す
	 *
	 * @return 音源ファイルのパス
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * 赤系ボーナス用のBGMを返す
	 *
	 * @return ボーナスBGMオブジェクト
	 */
	public static MusicTable getFanfare() {
		return new MusicTable(265000, 795000, SoundResource.BGM_FANFARE);
	}

	/**
	 * 青系ボーナス用のBGMを返す
	 *
	 * @return ボーナスBGMオブジェクト
	 */
	public static MusicTable getEdm() {
		return new MusicTable(352000, 705000, SoundResource.BGM_EDM);
	}

}
