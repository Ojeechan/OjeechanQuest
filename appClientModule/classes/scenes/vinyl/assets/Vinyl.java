package classes.scenes.vinyl.assets;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.utils.GeneralUtil;

/**
 * レコードオブジェクトの定義クラス
 *
 * @author Naoki Yoshikawa
 */
public class Vinyl {

    // レコードのタイトル
    private String title;

    // レコードのコメント
    private String dsc;

    // レコードジャケット、レコード本体の画像オブジェクト
    private BufferedImage jacket;
    private BufferedImage vinylImg;

    // レコードの曲目のリスト
    private List<Track> trackList;

    /**
     * パラメータの初期化を行う
     *
     * @param title   レコードのタイトル
     * @param dsc     レコードの説明文
     * @param jacPath レコードジャケット用画像のファイルパス
     * @param vinPath レコード本体用画像のファイルパス
     */
    public Vinyl(String title, String dsc, String jacPath, String vinPath) {
        this.title = title;
        this.dsc = dsc;
        this.jacket = GeneralUtil.readImage(jacPath);
        this.vinylImg = GeneralUtil.readImage(vinPath);
        this.trackList = new ArrayList<Track>();
    }

    /**
     * レコード内の各トラック情報のラッパークラス
     */
    public class Track {
        // トラック名
        private String dsc;

        // 音源のファイルパス
        private String sound;

        /**
         * パラメータの初期化
         *
         * @param dsc   トラック名
         * @param sound 音源のファイルパス
         */
        public Track(String dsc, String sound) {
            this.dsc = dsc;
            this.sound = sound;
        }

        /**
         * レコードのトラック名を返す
         *
         * @return トラック名
         */
        public String getDescription() {
            return this.dsc;
        }

        /**
         * トラック音源のファイルパスを返す
         *
         * @return 音源のファイルパス
         */
        public String getSound() {
            return this.sound;
        }
    }

    /**
     * レコードにトラック情報を追加する
     *
     * @param dsc   トラック名
     * @param sound 音源のファイルパス
     */
    public void addTrack(String dsc, String sound) {
        trackList.add(new Track(dsc, sound));
    }

    /**
     * レコードのトラック一覧のリストを返す
     *
     * @return トラック一覧のリスト
     */
    public List<Track> getTrackList() {
        return this.trackList;
    }

    /**
     * レコード名を返す
     * @return レコード名
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * レコードの説明文を返す
     * @return レコードの説明文
     */
    public String getDsc() {
        return this.dsc;
    }

    /**
     * レコードジャケットの画像オブジェクトを返す
     *
     * @return レコードジャケットの画像オブジェクト
     */
    public BufferedImage getJacket() {
        return this.jacket;
    }

    /**
     * レコード本体の画像オブジェクトを返す
     *
     * @return レコード本体の画像オブジェクト
     */
    public BufferedImage getVinylImg() {
        return this.vinylImg;
    }

    /**
     * 初期状態で所持しているレコードのリストを返すファクトリーメソッド
     *
     * @return レコードのリスト
     */
    public static List<Vinyl> getDefaultVinyl() {

        List<Vinyl> vinylList = new ArrayList<Vinyl>();

        // タイトル、ワールドマップ
        Vinyl v = new Vinyl(
                "システム",
                "システムのBGMです",
                ImageResource.VinylIcon.JACKET2.getValue(),
                ImageResource.VinylIcon.VINYL2.getValue()
                );
        v.addTrack("1: タイトル", SoundResource.BGM_RELAX);
        v.addTrack("2: ワールドマップ", SoundResource.BGM_WORLDMAP);
        v.addTrack("3: エンディング", SoundResource.BGM_ENDING);
        v.addTrack("4: まものむら", SoundResource.BGM_MAKAI);

        vinylList.add(v);

        return vinylList;
    }

}
