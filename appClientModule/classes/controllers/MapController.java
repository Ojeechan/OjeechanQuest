package classes.controllers;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import classes.constants.ImageResource;
import classes.utils.GeneralUtil;

/**
 * <pre>
 * アクションステージの地形情報の管理クラス
 * マップのサイズ、地面、ブロックの配置などを設定する
 * </pre>
 *
 * @author Naoki Yoshikawa
 **/
public class MapController {

	// タイルサイズ
    public static final int TILE_SIZE = 32;

    // マップ
    private char[][] map;

    // タイル換算での行列数
    private int row;
    private int col;

    // ステージオブジェクトの画像データのリスト
    private Map<Character, BufferedImage> stageAssetMap;

    // ステージオブジェクトの画像のファイルパスのリスト
    private Map<Character, String> assetResourceMap;

    /**
     * マップデータに画像を必要とするオブジェクトがない場合はこちらを呼び出す
     *
     * @param filename マップデータのファイルパス
     */
    public MapController(String filename) {
        load(filename);
        stageAssetMap = new HashMap<Character, BufferedImage>();
    }

    /**
     * マップデータに画像を必要とするオブジェクトがある場合はこちらを呼び出す
     *
     * @param filename         マップデータのファイルパス
     * @param assetResourceMap 画像を使用するオブジェクト用の画像ファイルパス
     */
    public MapController(String filename, Map<Character, String> assetResourceMap) {
        this(filename);
        this.assetResourceMap = assetResourceMap;
        loadImage();
    }

    /**
     * ピクセル数に換算したマップの幅を返す
     *
     * @return マップの幅
     */
    public int getWidth() {
        return TILE_SIZE * col;
    }

    /**
     * ピクセル数に換算したマップの高さを返す
     *
     * @return マップの高さ
     */
    public int getHeight() {
        return TILE_SIZE * row;
    }


    /**
     * タイル換算でのマップの最大列数を返す
     *
     * @return マップの最大列数
     */
    public int getColumn() {
    	return col;
    }

    /**
     * タイル換算でのマップの最大行数を返す
     *
     * @return マップの最大行数
     */
    public int getRow() {
    	return row;
    }

    /**
     * 読み込まれた整数型2次元配列のマップデータを返す
     *
     * @return マップデータ
     */
    public char[][] getMapData() {
    	return map;
    }

    /**
     * 画像を持つステージオブジェクトのマップを返す
     *
     * @return ステージオブジェクトのマップ
     */
    public Map<Character, BufferedImage> getAssetMap() {
    	return stageAssetMap;
    }

    /**
     * <pre>
     * マップデータの指定座標上のオブジェクトがメロディーブロックかどうかを判断する
     * メロディーブロックなら true
     * </pre>
     *
     * @param x マップデータのX座標
     * @param y マップデータのY座標
     *
     * @return メロディーブロックかどうか
     */
    public boolean isMelodyBlock(int x, int y) {

        if(x < 0 || y < 0 || x > col - 1 || y > row - 1) {
        	return false;
        }

    	if (map[y][x] >= '1' && map[y][x] <= '7'
        		|| map[y][x] == 'u'
				|| map[y][x] == 'i'
        		) {
            return true;
        }

        return false;
    }

    /**
     * <pre>
     * プレイヤーがメロディーブロックを叩いた時のリアクションを定義する
     * 実験的にここに実装中
     * </pre>
     *
     * @param x マップデータ中のx座標
     * @param y マップデータ中のy座標
     */
    public void knockMelodyBlock(int x, int y) {

    	switch(map[y][x]) {
    	 case '1':
    		// SoundController.stopSound(Sound.SE_C);
    		 //CONTROLLER_Sound.loopClipPeriod(CONSTANT_Sound.SE_C);
    		 break;
    	 case '2':
    		 //SoundController.stopSound(Sound.SE_D);
    		 //CONTROLLER_Sound.loopClipPeriod(CONSTANT_Sound.SE_D);
    		 break;
    	 case '3':
    		 //SoundController.stopSound(Sound.SE_E);
    		 //CONTROLLER_Sound.loopClipPeriod(CONSTANT_Sound.SE_E);
    		 break;
    	 case '4':
    		 //SoundController.stopSound(Sound.SE_F);
    		 //CONTROLLER_Sound.loopClipPeriod(CONSTANT_Sound.SE_F);
    		 break;
    	 case '5':
    		 //SoundController.stopSound(Sound.SE_G);
    		 //CONTROLLER_Sound.loopClipPeriod(CONSTANT_Sound.SE_G);
    		 break;
    	 case '6':
    		 //SoundController.stopSound(Sound.SE_A);
    		 //CONTROLLER_Sound.loopClipPeriod(CONSTANT_Sound.SE_A);
    		 break;
    	 case '7':
    		 //SoundController.stopSound(Sound.SE_B);
    		 //CONTROLLER_Sound.loopClipPeriod(CONSTANT_Sound.SE_B);
    		 break;
    	 case 'i':
    		// CONTROLLER_Sound.stopLoopClip();
    		 break;
    	 case 'u':
    		 //SoundController.stopSound(Sound.SE_BEAT);
    		// CONTROLLER_Sound.loopClipPeriod(CONSTANT_Sound.SE_BEAT);
    		 break;
		 default:
			 break;
    	 }

    }

    /**
     * マップデータ上のオブジェクトの画像を読み込む
     */
    private void loadImage() {
    	for(char symbol: assetResourceMap.keySet()) {
    		stageAssetMap.put(symbol, GeneralUtil.readImage(assetResourceMap.get(symbol)));
		}
    }

    /**
     * <pre>
     * 不変のステージオブジェクトのみ読み込む
     * 動的なオブジェクトはスプライトとしてゲームシーン側で処理する
     * </pre>
     *
     * @param filename マップ用datファイルのパス
     */
    private void load(String filename) {
        try {
            // ファイルを開く
            BufferedReader br = new BufferedReader(
        		new InputStreamReader(
                    getClass().getResourceAsStream("maps/" + filename)
                )
    		);

            // 行数を読み込む
            String line = br.readLine();
            row = Integer.parseInt(line);
            // 列数を読み込む
            line = br.readLine();
            col = Integer.parseInt(line);
            // マップを作成
            map = new char[row][col];
            for (int i = 0; i < row; i++) {
                line = br.readLine();
                for (int j = 0; j < col; j++) {
                    map[i][j] = line.charAt(j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <pre>
     * Stage1用のデフォルトアセット用ファクトリーメソッド
     * 実験用
     * </pre>
     *
     * @return デフォルトアセットのマップ
     */
    public static Map<Character, String> getStage1Asset() {
    	Map<Character, String> stage1AssetMap = new HashMap<Character, String>();
    	stage1AssetMap.put('1', ImageResource.StageObject.BLOCK_C.getValue());
    	stage1AssetMap.put('2', ImageResource.StageObject.BLOCK_D.getValue());
    	stage1AssetMap.put('3', ImageResource.StageObject.BLOCK_E.getValue());
    	stage1AssetMap.put('4', ImageResource.StageObject.BLOCK_F.getValue());
    	stage1AssetMap.put('5', ImageResource.StageObject.BLOCK_G.getValue());
    	stage1AssetMap.put('6', ImageResource.StageObject.BLOCK_A.getValue());
    	stage1AssetMap.put('7', ImageResource.StageObject.BLOCK_B.getValue());
    	stage1AssetMap.put('i', ImageResource.StageObject.BLOCK_STOP.getValue());
    	stage1AssetMap.put('u', ImageResource.StageObject.BLOCK_BEAT.getValue());
    	return stage1AssetMap;
    }
}
