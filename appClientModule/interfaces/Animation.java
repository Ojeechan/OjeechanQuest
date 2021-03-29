package interfaces;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

/**
 * アニメーションを行うスプライトオブジェクトを規定するインターフェース
 *
 * @author Naoki Yoshikawa
 */
public interface Animation {

	/**
	 * アニメーションの各コマ画像をアニメーション名ごとに保持するマップを返す
	 *
	 * @return アニメーション画像のマップ
	 */
	public Map<String, List<BufferedImage>> getFrameHolder();

	/**
	 * 現在のアニメーションのコマ位置を返す
	 *
	 * @return アニメーションのコマ位置
	 */
    public int getCurrentFrame();

    /**
	 * アニメーションのコマ送り速度を設定する
	 *
	 * @param speedRate コマ送り速度
	 */
    public void setSpeedRate(int speedRate);

    /**
     * <pre>
	 * 2つのアニメーション名が同名かどうかを判断する
	 * 同名ならば true
	 * </pre>
	 *
	 * @param label 比較するラベル名
	 *
	 * @return 2つのアニメーション名が同名かどうか
	 */
    public boolean isSameLabel(String label);

    /**
     * アニメーションを切り替える
     *
     * @param label 切り替える先のアニメーション名
     */
	public void switchLabel(String label);

	/**
	 * アニメーションのコマを進める
	 *
	 * @param dt デルタタイム
	 */
    public void animate(double dt);

}
