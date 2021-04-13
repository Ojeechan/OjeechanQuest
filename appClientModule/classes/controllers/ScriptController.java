package classes.controllers;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import classes.utils.GeneralUtil;

/**
 * メッセージウィンドウとスクリプトを管理するクラス
 *
 * @author Naoki Yoshikawa
 */
public class ScriptController {
    // メッセージウィンドウの描画位置の座標
    private double x;
    private double y;

    // メッセージウィンドウのサイズ
    private double width;
    private double height;

    // メッセージウィンドウの背景画像オブジェクト
    private BufferedImage background;

    private List<Script> scriptList;

    private Map<Character, BufferedImage> cacheMap;

    /**
     * メッセージウィンドウの表示位置の座標、ウィンドウサイズ、背景画像の設定
     *
     * @param x      メッセージウィンドウの表示位置のX座標
     * @param y      メッセージウィンドウの表示位置のY座標
     * @param width  メッセージウィンドウの幅(px)
     * @param height メッセージウィンドウの高さ(px)
     * @param path   メッセージウィンドウの背景画像のファイルパス
     */
    public ScriptController(double x, double y, double width, double height, String path) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.background = GeneralUtil.readImage(path);
        this.scriptList = new ArrayList<Script>();
        this.cacheMap = new HashMap<Character, BufferedImage>();
    }

    /**
     * 行ごとのスクリプトのフォント画像を保持するラッパークラス
     */
    public class Script {
        // 行ごとのフォント画像のリスト
        private List<BufferedImage> scriptImgList;
        private int size;
        private double renderIndex;

        /**
         * フォント、スクリプトの文字列の設定
         *
         * @param size   フォントサイズ
         * @param script スクリプトの内容
         * @param font   フォントオブジェクト
         */
        private Script(int size, String script, FontController.Fonts font) {
            this.scriptImgList = new ArrayList<BufferedImage>();
            this.size = size;
            convertFontImage(script, font);
            this.renderIndex = 0;
        }

        /**
         * スクリプトの文字列をフォント画像の配列に変換する
         *
         * @param script スクリプトの内容を表す文字列
         * @param font   フォントオブジェクト
         */
        private void convertFontImage(String script, FontController.Fonts font) {
            for(int i = 0; i < script.length(); i++) {
                if(cacheMap.containsKey(script.charAt(i))) {
                    scriptImgList.add(cacheMap.get(script.charAt(i)));
                } else {
                    BufferedImage img = GeneralUtil.readImage(font.getValue(script.charAt(i)));
                    scriptImgList.add(img);
                    cacheMap.put(script.charAt(i), img);
                }
            }
        }

        /**
         * スクリプトのフォントサイズを返す
         *
         * @return フォントサイズ
         */
        public int getSize() {
            return this.size;
        }

        /**
         * スクリプトの画像リストを返す
         *
         * @return フォントサイズ
         */
        public List<BufferedImage> getFonts() {
            return this.scriptImgList;
        }

        public int getCharPerRow() {
            return (int) (width / size);
        }

        public int getRenderIndex() {
            return (int) this.renderIndex;
        }

        public void resetIndex() {
            this.renderIndex = 0;
        }

        public void proceedIndex(double dt) {
            renderIndex = Math.min(renderIndex + dt * 25, scriptImgList.size());
        }


    }

    /**
     * スクリプトウィンドウの描画位置のX座標を返す
     *
     * @return 描画位置のX座標(ウィンドウ矩形の左上)
     */
    public double getX() {
        return this.x;
    }

    /**
     * スクリプトウィンドウの描画位置のY座標を返す
     *
     * @return 描画位置のY座標(ウィンドウ矩形の左上)
     */
    public double getY() {
        return this.y;
    }

    /**
     * スクリプトウィンドウの幅をピクセルで返す(
     *
     * @return ウィンドウの幅(px)
     */
    public double getWidth() {
        return this.width;
    }

    /**
     * スクリプトウィンドウの高さをピクセル返す
     *
     * @return ウィンドウの高さ(px)
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * スクリプトウィンドウの背景画像オブジェクトを返す
     *
     * @return 背景画像オブジェクト
     */
    public BufferedImage getBackground() {
        return this.background;
    }

    /**
     * 設定されたスクリプトのリストを返す
     *
     * @return スクリプトのリスト
     */
    public List<Script> getScriptList() {
        return scriptList;
    }

    /**
     * 文字列のスクリプトをウィンドウサイズに合わせて行ごとのスクリプトオブジェクトに変換する
     *
     * @param size   フォントサイズ
     * @param script スクリプトの内容を表す文字列
     * @param font   フォントオブジェクト
     */
    public void setScript(int size, String script, FontController.Fonts font) {
        scriptList.add(new Script(size, script, font));
    }
}
