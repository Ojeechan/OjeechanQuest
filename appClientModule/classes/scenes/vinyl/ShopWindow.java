package classes.scenes.vinyl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.controllers.FontController;
import classes.controllers.GameController;
import classes.controllers.KeyController;
import classes.controllers.SceneController;
import classes.controllers.ScriptController;
import classes.controllers.SoundController;
import classes.controllers.WindowController;
import classes.controllers.ScriptController.Script;
import classes.scenes.BaseSystemOperator;
import classes.scenes.vinyl.assets.Vinyl;
import classes.scenes.vinyl.assets.Vinyl.Track;
import classes.ui.StringSelectOption;
import classes.utils.GeneralUtil;

import interfaces.GameScene;

/**
 * レコード屋の買い物用ウィンドウを定義するクラス
 *
 * @author Naoki Yoshikawa
 */
public class ShopWindow extends BaseSystemOperator implements GameScene {

    // 商品レコードの配列
    private VinylStock[] vinyls;

    // スクリプトを管理するオブジェクト
    private ScriptController script;
    //private VinylShop parent;

    // 現在のカーソルのあるインデックス
     private int cursorVinylIndex;

     // 店員キャラクター画像オブジェクト
     private BufferedImage owner;

    // 曲目とレコードタイトルを表示するための選択リストオブジェクト
     private List<StringSelectOption> itemList;
     private List<StringSelectOption> priceList;

     // 表示するスクリプトを表すインデックス
     private int scriptIndex;

     // コイン数を表示するのに使用する数字のフォント画像のリスト
     private Map<Character, BufferedImage> numFontMap;

     // UIテキスト用のオブジェクト
     private StringSelectOption sold;
     private StringSelectOption coin;

     // はい/いいえ用のシーンオブジェクト
     private GameScene yesno;

     // 選択レコードの拡大縮小アニメーション用のタイムパラメータ
     private double count;

     /**
      * 選択リスト、スクリプト、キーコンフィグの設定
      */
    public ShopWindow() {

        keyHelpList.add(new StringSelectOption(850, 20, FontController.Fonts.NORMAL, "←/→: せんたく", 16));
        keyHelpList.add(new StringSelectOption(850, 36, FontController.Fonts.NORMAL, "ENTER: けってい", 16));
        keyHelpList.add(new StringSelectOption(850, 52, FontController.Fonts.NORMAL, "C: キャンセル", 16));
        keyHelpList.add(new StringSelectOption(850, 68, FontController.Fonts.NORMAL, "H:HELP ON/OFF", 16));
        keyHelpList.add(new StringSelectOption(850, 84, FontController.Fonts.NORMAL, "ESC:PAUSE", 16));

        // キーコンフィグの追加
        keyConfig.getKeys().put(KeyEvent.VK_C, keyConfig.new Key(KeyController.DETECT_INITIAL_PRESS_ONLY));

        // 画像の読み込み
        numFontMap = GeneralUtil.stringToImageMap(FontController.Fonts.NORMAL, "0123456789-");
        owner = GeneralUtil.readImage(ImageResource.VinylIcon.OWNER.getValue());

        // 商品レコードの設定
        Vinyl v1 = new Vinyl(
                "スロット",
                "スロットステージのBGMです",
                ImageResource.VinylIcon.JACKET1.getValue(),
                ImageResource.VinylIcon.VINYL1.getValue()
                );

        Track t = v1.new Track("1: きょうもどこかでOjeechan", SoundResource.BGM_FREEZE_MOVIE2);
        v1.getTrackList().add(t);

        v1.addTrack("2: あか7のテーマ", SoundResource.BGM_FANFARE);
        v1.addTrack("3: あお7のテーマ", SoundResource.BGM_EDM);
        v1.addTrack("4: スロットホール", SoundResource.BGM_HALL);
        v1.addTrack("5: あか7ワルツ", SoundResource.BGM_SLOT);
        v1.addTrack("6: フリーズ", SoundResource.BGM_FREEZE_MOVIE1);

        Vinyl v2 = new Vinyl(
                "レコードやのサウンド",
                "レコードやステージのBGMです",
                ImageResource.VinylIcon.JACKET3.getValue(),
                ImageResource.VinylIcon.VINYL3.getValue()
                );
        v2.addTrack("1: てんないBGM", SoundResource.BGM_VINYLSHOP);

        Vinyl v3 = new Vinyl(
                "アクション",
                "アクションステージのBGMです",
                ImageResource.VinylIcon.JACKET4.getValue(),
                ImageResource.VinylIcon.VINYL4.getValue()
                );
        v3.addTrack("1: きのこがり", SoundResource.BGM_ACTION);
        v3.addTrack("2: R.I.P.", SoundResource.BGM_GAMEOVER);

        vinyls = new VinylStock[] {
                new VinylStock(1, 2000, v1),
                new VinylStock(1, 9999, v2),
                new VinylStock(1, 5000, v3)
                };

        // 店員のスクリプトを設定
        script = new ScriptController(
                410,
                340,
                310,
                170,
                ImageResource.VinylIcon.WINDOW.getValue()
                );

        for(VinylStock v: vinyls) {
            script.setScript(
                    24,
                    v.vinyl.getDsc(),
                    FontController.Fonts.NORMAL
                    );
        }

        script.setScript(
                36,
                "おかねがたりない",
                FontController.Fonts.NORMAL
                );

        script.setScript(
                36,
                "ざいこがない",
                FontController.Fonts.NORMAL
                );

        script.setScript(
                24,
                "ありがとうございました",
                FontController.Fonts.NORMAL
                );

        script.setScript(
                24,
                "",
                FontController.Fonts.NORMAL
                );



        // 選択リストの読み込み
        setItemList();
        setPriceList();

        sold = new StringSelectOption(
                500,
                120,
                FontController.Fonts.NORMAL,
                "うりきれ",
                24
                );

        coin = new StringSelectOption(
                760,
                350,
                FontController.Fonts.NORMAL,
                "もちがね",
                16
                );

        // はい/いいえ用のウィンドウ
        yesno = new Yesno();

        initParam();
    }

    /**
     * 商品用のレコードのプロパティを定義するラッパークラス
     */
    private class VinylStock {
        // 在庫数
        private int stock;
        // 価格
        private int price;
        // レコードオブジェクト
        private Vinyl vinyl;

        private Init init;

        /**
         * パラメータの初期化を行う
         *
         * @param stock レコード在庫数
         * @param price レコードの価格
         * @param vinyl レコードオブジェクト
         */
        private VinylStock(int stock, int price, Vinyl vinyl) {
            this.stock = stock;
            this.price = price;
            this.vinyl = vinyl;
            this.init = new Init();
            init.stock = stock;
        }

            private class Init {
                private int stock;
            }
    }

    /**
     * レコード一覧用の選択リストオブジェクトを読み込む
     */
    private void setItemList() {
        itemList = new ArrayList<StringSelectOption>();
        for(int i = 0; i < vinyls.length; i++) {
            StringSelectOption title = new StringSelectOption(
                    280,
                    120,
                    FontController.Fonts.NORMAL,
                    vinyls[i].vinyl.getTitle(),
                    20
                    );
            itemList.add(title);
        }
    }

    /**
     * レコード一覧用の選択リストオブジェクトを読み込む
     */
    private void setPriceList() {
        priceList = new ArrayList<StringSelectOption>();
        for(int i = 0; i < vinyls.length; i++) {
            StringSelectOption title = new StringSelectOption(
                    500,
                    120,
                    FontController.Fonts.NORMAL,
                    Integer.toString(vinyls[i].price),
                    24
                    );
            priceList.add(title);
        }
    }


    /**
     * <pre>
     * トラック用カーソルのインデックスを1つ進める
     * 最大値の次は0に戻る
     * </pre>
     *
     * @return 1つ進めた後のインデックス
     */
    public int next() {
        cursorVinylIndex++;
        return cursorVinylIndex %= vinyls.length;
    }

    /**
     * <pre>
     * トラック用カーソルのインデックスを1つ戻す
     * 0の次は最大インデックスに戻る
     * </pre>
     *
     * @return 1つ戻した後のインデックス
     */
    public int prev() {
        cursorVinylIndex--;
        return cursorVinylIndex < 0
                ? cursorVinylIndex = vinyls.length + cursorVinylIndex
                : cursorVinylIndex;
    }

    /**
     * レコードの在庫数をリセットする
     */
    public void restock() {
        for(VinylStock vs: vinyls) {
            vs.stock = vs.init.stock;
        }
    }

    /**
     * フレームごとの再描画を行う
     *
     * @param g グラフィックスオブジェクト
     */
    @Override
    public void paintComponent(Graphics g) {

        // ウィンドウ背景の描画
        g.drawImage(
                script.getBackground(),
                (int) GameController.getWindow().getAbsPosX(WindowController.WIDTH/4),
                (int) GameController.getWindow().getAbsPosY(30),
                (int) GameController.getWindow().getAbsPosX(480),
                (int) GameController.getWindow().getAbsPosY(480),
                null
                );

        // インデックスで指定したスクリプトを表示
        Script s = script.getScriptList().get(scriptIndex);
        GeneralUtil.drawScript(
                s,
                (int) (script.getX() + 5),
                (int) (script.getY() + 10),
                g
                );

        // 店員キャラクターの描画
        double wave = GeneralUtil.getSinValue(System.currentTimeMillis() / 1000.0, 1.0, 0, 5);
        g.drawImage(
                owner,
                (int) GameController.getWindow().getAbsPosX(250),
                (int) GameController.getWindow().getAbsPosY(350 + wave),
                (int) GameController.getWindow().getAbsPosX(128),
                (int) GameController.getWindow().getAbsPosY(128),
                null
                );

        // ジャケットの描画
        BufferedImage jacket;
        for(int i = 0; i < vinyls.length; i++) {
            jacket = vinyls[i].vinyl.getJacket();
            double drawWidth = jacket.getWidth()/4;
            double drawHeight = jacket.getHeight()/4;
            if(cursorVinylIndex != i) {
                g.drawImage(
                        jacket,
                        (int) GameController.getWindow().getAbsPosX(350 + (jacket.getWidth()/6 * i) - drawWidth/2),
                        (int) GameController.getWindow().getAbsPosY(250 - drawHeight/2),
                        (int) GameController.getWindow().getAbsPosX(drawWidth),
                        (int) GameController.getWindow().getAbsPosY(drawHeight),
                        null
                        );
            }
        }

        // 選択レコードは最後に描画
        jacket = vinyls[cursorVinylIndex].vinyl.getJacket();
        double ratio = GeneralUtil.getSinValue(count, 1, 1, 0.05) + 1;
        double drawWidth = jacket.getWidth()/3 * ratio;
        double drawHeight = jacket.getHeight()/3 * ratio;
        g.drawImage(
                jacket,
                (int) GameController.getWindow().getAbsPosX(350 + (jacket.getWidth()/6 * cursorVinylIndex) - drawWidth/2),
                (int) GameController.getWindow().getAbsPosY(250 - drawHeight/2),
                (int) GameController.getWindow().getAbsPosX(drawWidth),
                (int) GameController.getWindow().getAbsPosY(drawHeight),
                null
                );

        // 商品名の表示
        StringSelectOption item = itemList.get(cursorVinylIndex);
        GeneralUtil.drawImageString(
                item.getImageFont(),
                item.getX(),
                item.getY(),
                item.getSize(),
                GeneralUtil.ALIGN_LEFT,
                g
                );

        // 価格の表示
        StringSelectOption price = priceList.get(cursorVinylIndex);
        List<BufferedImage> font;
        if(vinyls[cursorVinylIndex].stock < 1) {
            font = sold.getImageFont();
        } else {
            font = price.getImageFont();
        }
        GeneralUtil.drawImageString(
                font,
                price.getX(),
                price.getY(),
                price.getSize(),
                GeneralUtil.ALIGN_LEFT,
                g
                );

        // 所持金の表示
        g.setColor(Color.BLACK);
        g.fillRect(
                (int) GameController.getWindow().getAbsPosX(750),
                (int) GameController.getWindow().getAbsPosY(340),
                (int) GameController.getWindow().getAbsPosX(200),
                (int) GameController.getWindow().getAbsPosY(160)
                );
        GeneralUtil.drawImageString(
                coin.getImageFont(),
                coin.getX(),
                coin.getY(),
                coin.getSize(),
                GeneralUtil.ALIGN_LEFT,
                g
                );
        GeneralUtil.drawDynamicString(
                Integer.toString(GameController.getCoin()),
                numFontMap,
                770,
                400,
                20,
                g
                );

        if(helpOn) {
            GeneralUtil.drawSelectOptions(keyHelpList, GeneralUtil.ALIGN_CENTER, g);
        }
    }

    /*
     * GameSceneインターフェースの機能群
     */

    /**
     * ユーザ入力または自動操作による入力値をもとに、フレームごとのオブジェクトの更新を行う
     * @see interfaces.GameScene
     *
     * @param dt     デルタタイム
     */
    @Override
    public void updator(double dt) {

        // ポーズ
        if(keyConfig.getKeys().get(KeyEvent.VK_ESCAPE).isPressed()) {
            GameController.getWindow().pushScene(GameController.getScene(SceneController.PAUSE));
            keyConfig.releaseAll();
        }

        // 左右キーはページを選択する
        if (keyConfig.getKeys().get(KeyEvent.VK_LEFT).isPressed()) {
            prev();
            scriptIndex = cursorVinylIndex;
            script.getScriptList().get(scriptIndex).resetIndex();
            count = 0;
            new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();
        } else if (keyConfig.getKeys().get(KeyEvent.VK_RIGHT).isPressed()) {
            next();
            scriptIndex = cursorVinylIndex;
            script.getScriptList().get(scriptIndex).resetIndex();
            count = 0;
            new Thread(new SoundController.PlaySE(SoundResource.SE_SELECT)).start();
        }

        if(keyConfig.getKeys().get(KeyEvent.VK_ENTER).isPressed()) {

            script.getScriptList().get(scriptIndex).resetIndex();

            // 売り切れ
            if(vinyls[cursorVinylIndex].stock < 1) {
                scriptIndex = vinyls.length + 1;
            // 所持金不足
            } else if(vinyls[cursorVinylIndex].price > GameController.getCoin()) {
                scriptIndex = vinyls.length;
            // 購入
            } else {
                yesno.initParam();
                GameController.getWindow().pushScene(yesno);
                scriptIndex = vinyls.length + 3;
            }
        }

        // キャンセル
        if(keyConfig.getKeys().get(KeyEvent.VK_C).isPressed()) {
            keyConfig.releaseAll();
            GameController.getWindow().popScene();
            GameController.getWindow().callback(0);
        }

        // 操作キー一覧の表示
        if(keyConfig.getKeys().get(KeyEvent.VK_H).isPressed()) {
            super.helpOn = !super.helpOn;
        }

        Script s = script.getScriptList().get(scriptIndex);
        s.proceedIndex(dt);

        count = (count + dt * 9) % (2 * Math.PI);
    }

    /**
     * JLayeredPanelに追加するために自身のインスタンスを返す
     * @see interfaces.GameScene
     *
     * @return 自身のパネルインスタンス
     */
    public JLayeredPane getPanel() {
        return this;
    }

    /**
     * 自身の初期状態のインスタンスを返す
     * @see interfaces.GameScene
     *
     * @return 自身の初期状態のインスタンス
     */
    public GameScene getNewScene() {
        return new ShopWindow();
    }

    /**
     * 自身のシーンで使用するBGMのファイルパスを返す
     * @see interfaces.GameScene
     *
     * @return BGMのファイルパス
     */
    public String getSound() {
        return null;
    }

    /**
     * <pre>
     * 自身のシーンで使用するBGMの再生モードを返す
     * 0: ループ再生(ループ区間指定可)
     * 1: 一度のみ再生
     * 2: 再生なし
     * </pre>
     * @return BGMのファイルパス
     */
    public int getBgmMode() {
        return GameScene.BGM_NONE;
    }

    /**
     * 自身のシーンで使用するBGMの再生区間を返す
     *
     * @return BGM再生区間を表す始点と終点の値の組
     */
    public Point getDuration() {
        return null;
    }

    /**
     * 各パラメータを初期化する
     */
    public void initParam() {
        scriptIndex = 0;
        cursorVinylIndex = 0;
        helpOn = true;
    }

    /**
     * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
     *
     * @param res 呼び出し元からのレスポンスコード
     */
    public void callback(int res) {
        if(res == 0) {
            // 購入
            GameController.addCoin(-vinyls[cursorVinylIndex].price);
            GameController.addVinyl(vinyls[cursorVinylIndex].vinyl);
            vinyls[cursorVinylIndex].stock--;
            scriptIndex = vinyls.length + 2;
            new Thread(new SoundController.PlaySE(SoundResource.SE_CLOCK)).start();
        } else {
            scriptIndex = cursorVinylIndex;
        }
    }
}
