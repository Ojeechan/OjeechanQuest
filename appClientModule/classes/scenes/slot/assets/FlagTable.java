package classes.scenes.slot.assets;

/**
 * フラグごとのプロパティを定義するラッパークラス
 *
 * @author Naoki Yoshikawa
 */
public class FlagTable {

    // 各変数についてはコンストラクタのドキュメンテーションを参照
    private double prob;
    private double subProb;
    private int[] validIcon;
    private int[][] line;
    private int payout;
    private int destMode;
    private int type;
    private String paySound;

    /**
     * 成立フラグごとのプロパティを設定
     *
     * @param prob      成立確率
     * @param subProb   重複確率
     * @param validIcon 入賞図柄
     * @param line      有効ライン
     * @param payout    払い出し枚数
     * @param destMode  移行先モード(ボーナスであればフラグ成立時、子役であれば重複当選時など)
     * @param type      成立フラグの種類(文字列で"bonus", "cherry" など)
     * @param paySound  払い出し音
     */
    public FlagTable(
            double prob,
            double subProb,
            int[]validIcon,
            int[][] line,
            int payout,
            int destMode,
            int type,
            String paySound
            ) {
        this.prob = prob;
        this.validIcon = validIcon;
        this.line = line;
        this.payout = payout;
        this.destMode = destMode;
        this.type = type;
        this.subProb = subProb;
        this.paySound = paySound;
    }

    /**
     * フラグの成立確率を返す
     *
     * @return 成立確率
     */
    public double getProbability() {
        return this.prob;
    }

    /**
     * フラグの同時重複確率を返す
     *
     * @return 同時重複確率
     */
    public double getSubProbability() {
        return this.subProb;
    }

    /**
     * フラグの入賞図柄の配列を返す
     *
     * @return 入賞図柄の配列
     */
    public int[] getValidIcon() {
        return this.validIcon;
    }

    /**
     * フラグの有効ラインの配列を返す
     *
     * @return 有効ラインの配列
     */
    public int[][] getLine() {
        return this.line;
    }

    /**
     * フラグの払い出し枚数を返す
     *
     * @return 払い出し枚数
     */
    public int getPayout() {
        return this.payout;
    }

    /**
     * フラグ成立時の移行先モードを返す
     *
     * @return 移行先モード
     */
    public int getDestMode() {
        return this.destMode;
    }

    /**
     * フラグの種類を表す文字列を返す
     *
     * @return フラグの種類
     */
    public int getType() {
        return this.type;
    }

    /**
     * フラグの入賞音の音源ファイルのパスを返す
     *
     * @return 入賞音の音源ファイルのパス
     */
    public String getPaySound() {
        return this.paySound;
    }
}
