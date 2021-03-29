package classes.scenes.slot.assets;

import classes.utils.GeneralUtil;

/**
 * 発光するスロットモジュールに対して発光パターンを設定するクラス
 *
 * @author Naoki Yoshikawa
 */
public class FlashPattern {

	// 発光パターンの周期
	private double frequency;

	// 発光量全体のシフト調整値
	private double shift;

	// 振幅の補正倍率
	private double amplitude;

	// 波形の選択値
	private int waveType;

	/**
	 * 波形のパラメータを設定する
	 *
	 * @param f        発光パターンの周期
	 * @param s        発光量のシフト調整値
	 * @param a        振幅の補正倍率
	 * @param waveType 波形パターンのID
	 */
	public FlashPattern(double f, double s, double a, int waveType) {
		this.frequency = f;
		this.shift = s;
		this.amplitude = a;
		this.waveType = waveType;
	}

	/**
	 * sine関数でマップした数値を返す
	 *
	 * @param time タイムパラメータ
	 * @return sine関数のマップ値
	 */
	public double getSin(double time) {
		return GeneralUtil.getSinValue(time, this.frequency, this.shift, this.amplitude);
	}

	/**
	 * cosine関数でマップした数値を返す
	 *
	 * @param time タイムパラメータ
	 * @return cosine関数のマップ値
	 */
	public double getCos(double time) {
		return GeneralUtil.getSinValue(time, this.frequency, this.shift, this.amplitude);
	}

	/**
	 * binary関数でマップした数値を返す
	 *
	 * @param time タイムパラメータ
	 * @return binary関数のマップ値
	 */
	public double getBinSin(double time) {
		double s = GeneralUtil.getSinValue(time, this.frequency, 0.0, 1.0);
		if(s < 0) {
			return 0;
		} else {
			return this.amplitude;
		}
	}

	/**
	 * 指定した関数でマップした発光量を返す
	 *
	 * @param time タイムパラメータ
	 * @return 指定した波形関数によるマップ値
	 */
	public double getPattern(double time) {

		switch(waveType) {
		case 0: // on
			return 1.0;
		case 1: // off
			return 0.0;
		case 2:
			return getSin(time);
		case 3:
			return getCos(time);
		case 4: // 点滅
			return getBinSin(time);
		default:
			return 1.0;
		}
	}

}
