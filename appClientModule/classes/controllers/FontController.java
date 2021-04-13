package classes.controllers;

import java.util.HashMap;
import java.util.Map;

import classes.constants.ImageResource;

/**
 * <pre>
 * フォントの管理クラス
 * フォント画像のパスのセットをstatic enumで提供する
 * </pre>
 *
 * @author  Naoki Yoshikawa
 */
public class FontController {

    /**
     * フォントオブジェクト
     */
    public static enum Fonts {
        /**
         * 選択リスト等で通常時に使用される基本フォント
         */
        @SuppressWarnings("serial")
        NORMAL(new HashMap<Character, String>(){{
            put('は', ImageResource.Font.HA.getValue());
            put('じ', ImageResource.Font.JI.getValue());
            put('め', ImageResource.Font.ME.getValue());
            put('か', ImageResource.Font.KA.getValue());
            put('ら', ImageResource.Font.RA.getValue());
            put('つ', ImageResource.Font.TSU.getValue());
            put('づ', ImageResource.Font.DSU.getValue());
            put('き', ImageResource.Font.KI.getValue());
            put('さ', ImageResource.Font.SA.getValue());
            put('い', ImageResource.Font.I.getValue());
            put('こ', ImageResource.Font.KO.getValue());
            put('も', ImageResource.Font.MO.getValue());
            put('う', ImageResource.Font.U.getValue());
            put('あ', ImageResource.Font.A.getValue());
            put('り', ImageResource.Font.RI.getValue());
            put('が', ImageResource.Font.GA.getValue());
            put('ね', ImageResource.Font.NE.getValue());
            put('ん', ImageResource.Font.N.getValue());
            put('す', ImageResource.Font.SU.getValue());
            put('る', ImageResource.Font.RU.getValue());
            put('れ', ImageResource.Font.RE.getValue());
            put('せ', ImageResource.Font.SE.getValue());
            put('た', ImageResource.Font.TA.getValue());
            put('ま', ImageResource.Font.MA.getValue());
            put('の', ImageResource.Font.NO.getValue());
            put('む', ImageResource.Font.MU.getValue());
            put('ど', ImageResource.Font.DO.getValue());
            put('で', ImageResource.Font.DE.getValue());
            put('や', ImageResource.Font.YA.getValue());
            put('け', ImageResource.Font.KE.getValue());
            put('し', ImageResource.Font.SHI.getValue());
            put('べ', ImageResource.Font.BE.getValue());
            put('な', ImageResource.Font.NA.getValue());
            put('く', ImageResource.Font.KU.getValue());
            put('お', ImageResource.Font.O.getValue());
            put('ち', ImageResource.Font.CHI.getValue());
            put('に', ImageResource.Font.NI.getValue());
            put('え', ImageResource.Font.E.getValue());
            put('と', ImageResource.Font.TO.getValue());
            put('ご', ImageResource.Font.GO.getValue());
            put('ざ', ImageResource.Font.ZA.getValue());
            put('て', ImageResource.Font.TE.getValue());
            put('ぱ', ImageResource.Font.PA.getValue());
            put('ぐ', ImageResource.Font.GU.getValue());
            put('ゃ', ImageResource.Font.YA_SMALL.getValue());
            put('っ', ImageResource.Font.TSU_SMALL.getValue());
            put('ょ', ImageResource.Font.YO_SMALL.getValue());

            put('ア', ImageResource.Font.KANA_A.getValue());
            put('シ', ImageResource.Font.KANA_SHI.getValue());
            put('ノ', ImageResource.Font.KANA_NO.getValue());
            put('ク', ImageResource.Font.KANA_KU.getValue());
            put('ボ', ImageResource.Font.KANA_BO.getValue());
            put('タ', ImageResource.Font.KANA_TA.getValue());
            put('イ', ImageResource.Font.KANA_I.getValue());
            put('ト', ImageResource.Font.KANA_TO.getValue());
            put('ル', ImageResource.Font.KANA_RU.getValue());
            put('ワ', ImageResource.Font.KANA_WA.getValue());
            put('ド', ImageResource.Font.KANA_DO.getValue());
            put('マ', ImageResource.Font.KANA_MA.getValue());
            put('プ', ImageResource.Font.KANA_PU.getValue());
            put('カ', ImageResource.Font.KANA_KA.getValue());
            put('ジ', ImageResource.Font.KANA_JI.getValue());
            put('ス', ImageResource.Font.KANA_SU.getValue());
            put('テ', ImageResource.Font.KANA_TE.getValue());
            put('ム', ImageResource.Font.KANA_MU.getValue());
            put('エ', ImageResource.Font.KANA_E.getValue());
            put('ン', ImageResource.Font.KANA_N.getValue());
            put('デ', ImageResource.Font.KANA_DE.getValue());
            put('グ', ImageResource.Font.KANA_GU.getValue());
            put('ス', ImageResource.Font.KANA_SU.getValue());
            put('ロ', ImageResource.Font.KANA_RO.getValue());
            put('マ', ImageResource.Font.KANA_MA.getValue());
            put('ホ', ImageResource.Font.KANA_HO.getValue());
            put('ツ', ImageResource.Font.KANA_TSU.getValue());
            put('フ', ImageResource.Font.KANA_FU.getValue());
            put('リ', ImageResource.Font.KANA_RI.getValue());
            put('ズ', ImageResource.Font.KANA_ZU.getValue());
            put('レ', ImageResource.Font.KANA_RE.getValue());
            put('コ', ImageResource.Font.KANA_KO.getValue());
            put('サ', ImageResource.Font.KANA_SA.getValue());
            put('ウ', ImageResource.Font.KANA_U.getValue());
            put('ド', ImageResource.Font.KANA_DO.getValue());
            put('キ', ImageResource.Font.KANA_KI.getValue());
            put('セ', ImageResource.Font.KANA_SE.getValue());
            put('ッ', ImageResource.Font.KANA_TSU_SMALL.getValue());
            put('ィ', ImageResource.Font.KANA_I_SMALL.getValue());
            put('ャ', ImageResource.Font.KANA_YA_SMALL.getValue());
            put('ョ', ImageResource.Font.KANA_YO_SMALL.getValue());

            put('A', ImageResource.Font.ALPH_A.getValue());
            put('C', ImageResource.Font.ALPH_C.getValue());
            put('D', ImageResource.Font.ALPH_D.getValue());
            put('E', ImageResource.Font.ALPH_E.getValue());
            put('F', ImageResource.Font.ALPH_F.getValue());
            put('H', ImageResource.Font.ALPH_H.getValue());
            put('K', ImageResource.Font.ALPH_K.getValue());
            put('L', ImageResource.Font.ALPH_L.getValue());
            put('N', ImageResource.Font.ALPH_N.getValue());
            put('O', ImageResource.Font.ALPH_O.getValue());
            put('P', ImageResource.Font.ALPH_P.getValue());
            put('R', ImageResource.Font.ALPH_R.getValue());
            put('S', ImageResource.Font.ALPH_S.getValue());
            put('T', ImageResource.Font.ALPH_T.getValue());
            put('U', ImageResource.Font.ALPH_U.getValue());
            put('B', ImageResource.Font.ALPH_B.getValue());
            put('G', ImageResource.Font.ALPH_G.getValue());
            put('M', ImageResource.Font.ALPH_M.getValue());
            put('W', ImageResource.Font.ALPH_W.getValue());
            put('I', ImageResource.Font.ALPH_I.getValue());

            put('j', ImageResource.Font.ALPH_J_SMALL.getValue());
            put('e', ImageResource.Font.ALPH_E_SMALL.getValue());
            put('c', ImageResource.Font.ALPH_C_SMALL.getValue());
            put('h', ImageResource.Font.ALPH_H_SMALL.getValue());
            put('a', ImageResource.Font.ALPH_A_SMALL.getValue());
            put('n', ImageResource.Font.ALPH_N_SMALL.getValue());

            put('0', ImageResource.Font.ZERO.getValue());
            put('1', ImageResource.Font.ONE.getValue());
            put('2', ImageResource.Font.TWO.getValue());
            put('3', ImageResource.Font.THREE.getValue());
            put('4', ImageResource.Font.FOUR.getValue());
            put('5', ImageResource.Font.FIVE.getValue());
            put('6', ImageResource.Font.SIX.getValue());
            put('7', ImageResource.Font.SEVEN.getValue());
            put('8', ImageResource.Font.EIGHT.getValue());
            put('9', ImageResource.Font.NINE.getValue());

            put('↑', ImageResource.Font.UP.getValue());
            put('↓', ImageResource.Font.DOWN.getValue());
            put('←', ImageResource.Font.LEFT.getValue());
            put('→', ImageResource.Font.RIGHT.getValue());
            put(':', ImageResource.Font.COLON.getValue());
            put('/', ImageResource.Font.SLASH.getValue());
            put('?', ImageResource.Font.QUESTION.getValue());
            put('？', ImageResource.Font.QUESTION.getValue());
            put('ー', ImageResource.Font.DASH.getValue());
            put('.', ImageResource.Font.DOT.getValue());
        }}),

        /**
         * スロットステージの7セグ用フォント
         */
        @SuppressWarnings("serial")
        SEGNUM(new HashMap<Character, String>(){{
            put('0', ImageResource.Font.ZERO_SEG.getValue());
            put('1', ImageResource.Font.ONE_SEG.getValue());
            put('2', ImageResource.Font.TWO_SEG.getValue());
            put('3', ImageResource.Font.THREE_SEG.getValue());
            put('4', ImageResource.Font.FOUR_SEG.getValue());
            put('5', ImageResource.Font.FIVE_SEG.getValue());
            put('6', ImageResource.Font.SIX_SEG.getValue());
            put('7', ImageResource.Font.SEVEN_SEG.getValue());
            put('8', ImageResource.Font.EIGHT_SEG.getValue());
            put('9', ImageResource.Font.NINE_SEG.getValue());
            put('-', ImageResource.Font.MINUS_SEG.getValue());

        }});

        private final Map<Character, String> fontMap;

        private Fonts(Map<Character, String> fontMap) {
            this.fontMap = fontMap;
        }

        /**
         * フォント画像のパスを取得する
         *
         * @param key フォントを取得する文字列1文字
         *
         * @return フォント画像のパス
         */
        public String getValue(char key) {
            if(this.fontMap.containsKey(key)) {
                return this.fontMap.get(key);
            } else {
                // フォントは随時追加予定
                // 未実装のフォントについてはダッシュを返す
                return ImageResource.Font.DASH.getValue();
            }
        }
    }
}
