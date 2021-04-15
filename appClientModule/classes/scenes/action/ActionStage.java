package classes.scenes.action;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JLayeredPane;

import classes.constants.ImageResource;
import classes.constants.SoundResource;
import classes.controllers.EffectController;
import classes.controllers.FontController;
import classes.controllers.GameController;
import classes.controllers.SceneController;
import classes.controllers.SoundController;
import classes.controllers.WindowController;
import classes.math.Vector2;
import classes.ui.StringSelectOption;
import classes.scenes.BaseSystemOperator;
import classes.scenes.action.assets.Player;
import classes.scenes.effects.Turntable1;
import classes.scenes.effects.Turntable2;
import classes.scenes.effects.Turntable3;
import classes.utils.GeneralUtil;
import classes.scenes.action.assets.StageObject;

import interfaces.GameScene;

/**
 * <pre>
 * アクションゲームステージを定義するゲームシーンオブジェクト
 * 擬似3Dの実験
 * </pre>
 *
 * @author Naoki Yoshikawa
 **/
@SuppressWarnings("serial")
public class ActionStage extends BaseSystemOperator implements GameScene {

    /* 定数群 */
    private final int GROUND = (int) (WindowController.HEIGHT/3 * 2); // 地面の高さ
    private final int CAMERA_WIDTH = (int) WindowController.WIDTH;    // カメラビューの幅
    private final int[] CAR_SPEED = new int[] {200, 350, 100};        // 車オブジェクトの速度
    private final int BACK_SIGHT = 30;    // 後方視野の開始位置
    private final int FORE_SIGHT = 2000;  // 前方視野の距離
    private final double GRAVITY = 1.0;   // 重力
    private final int FIELD_WIDTH = 500;  // 2Dビューの幅
    private final int FIELD_HEIGHT = 250; // 2Dビューの高さ
    private final int ITEM_SIZE = 20;     // 所持できる最大アイテム数
    private int SCALE = 100;  // ステージオブジェクトの拡大倍率の補正値
    private int SHIFT = 300;  // ステージオブジェクトのカメラビュー上でのX方向への補正値
    private int NORMAL = 0;   // ゲームモード:通常時
    private int TUTORIAL = 1; // ゲームモード:初回時
    private int DEBUG = 2; // ゲームモード:初回時

    // プレイヤーオブジェクト
    private Player player;

    // 自動車オブジェクトと画像
    private StageObject car;
    private BufferedImage[] carImgs;

    // キノコオブジェクトと画像
    private StageObject[] mushrooms;
    private BufferedImage[] mushroomImgs;

    // 買取屋オブジェクト
    private StageObject house;

    // 静的ステージオブジェクトのリスト
    private List<StageObject> objects;
    private List<StageObject> visibleObjFront;
    private List<StageObject> visibleObjBack;

    // 所持キノコのリスト
    private List<StageObject> items;

    // 背景、ロゴ画像オブジェクト
    private BufferedImage sky;
    private BufferedImage mountain1;
    private BufferedImage mountain2;
    private BufferedImage mountain3;
    private BufferedImage fog;
    private BufferedImage go;

    // チュートリアルで表示するGOロゴのアニメーション用カウント
    double goCount;

    // 画面遷移を管理するオブジェクト
    private EffectController effect;

    // 乱数生成用
    private Random r;

    // コイン数を表示するのに使用する数字のフォント画像のリスト
     private Map<Character, BufferedImage> numFontMap;

     // UI表示用メッセージのオブジェクト
     private StringSelectOption coin;
    private StringSelectOption max;
    private StringSelectOption sell;
    private StringSelectOption enter;
    private List<StringSelectOption> extraHelpList;

    // ゲームモード[NORMARL, TUTORIAL]
    private int mode;

    /**
     * キーコンフィグ、UIテキスト、プレイヤー、ステージオブジェクトの設定
     */
    public ActionStage() {

        keyConfig.getKeys().put(KeyEvent.VK_W, keyConfig.new Key());
        keyConfig.getKeys().put(KeyEvent.VK_S, keyConfig.new Key());
        keyConfig.getKeys().put(KeyEvent.VK_A, keyConfig.new Key());
        keyConfig.getKeys().put(KeyEvent.VK_D, keyConfig.new Key());

        keyHelpList.add(new StringSelectOption(800, 30, FontController.Fonts.NORMAL, "H:HELP ON/OFF", 16));
        keyHelpList.add(new StringSelectOption(800, 50, FontController.Fonts.NORMAL, "ESC:PAUSE", 16));
        keyHelpList.add(new StringSelectOption(90, 300, FontController.Fonts.NORMAL, "A  D:いどう", 16));
        keyHelpList.add(new StringSelectOption(90, 316, FontController.Fonts.NORMAL, "SPACE:ジャンプ", 16));

        extraHelpList = new ArrayList<StringSelectOption>();
        extraHelpList.add(new StringSelectOption(50, 284, FontController.Fonts.NORMAL, "W", 16));
        extraHelpList.add(new StringSelectOption(58, 300, FontController.Fonts.NORMAL, "S", 16));

        sell = new StringSelectOption(800, 130, FontController.Fonts.NORMAL, "ENTER: うる", 32);

        numFontMap = GeneralUtil.stringToImageMap(FontController.Fonts.NORMAL, "0123456789-");
        coin = new StringSelectOption(
                760,
                390,
                FontController.Fonts.NORMAL,
                "もちがね",
                16
                );

        max = new StringSelectOption(
                760,
                450,
                FontController.Fonts.NORMAL,
                "きのこまっくす",
                16
                );

        enter = new StringSelectOption(
                (int) (WindowController.WIDTH / 2),
                (int) (WindowController.HEIGHT / 3),
                FontController.Fonts.NORMAL,
                "ENTER",
                32
                );

        r = new Random();

        player = Player.getDefault(
                new Vector2(50, 240),
                WindowController.WIDTH / 2,
                GROUND
                );

        // 背景
        sky = GeneralUtil.readImage(ImageResource.LayeredBackground.SKY.getValue());
        fog = GeneralUtil.readImage(ImageResource.LayeredBackground.FOG.getValue());
        mountain1 = GeneralUtil.readImage(ImageResource.LayeredBackground.MOUNTAIN1.getValue());
        mountain2 = GeneralUtil.readImage(ImageResource.LayeredBackground.MOUNTAIN2.getValue());
        mountain3 = GeneralUtil.readImage(ImageResource.LayeredBackground.MOUNTAIN3.getValue());
        go = GeneralUtil.readImage(ImageResource.Logo.GO.getValue());

        // ステージの静的/動的オブジェクト
        objects = new ArrayList<StageObject>();

        BufferedImage img = GeneralUtil.readImage(ImageResource.StageObject.TREE.getValue());
        objects.add(new StageObject(new Vector2(0, 0), img, false, 0));
        objects.add(new StageObject(new Vector2(100, 0), img, false, 0));
        objects.add(new StageObject(new Vector2(200, 0), img, false, 0));
        objects.add(new StageObject(new Vector2(400, 0), img, false, 0));
        objects.add(new StageObject(new Vector2(500, 0), img, false, 0));
        objects.add(new StageObject(new Vector2(50, 50), img, false, 0));
        objects.add(new StageObject(new Vector2(150, 50), img, false, 0));
        objects.add(new StageObject(new Vector2(350, 50), img, false, 0));
        objects.add(new StageObject(new Vector2(450, 50), img, false, 0));
        objects.add(new StageObject(new Vector2(25, 100), img, false, 0));
        objects.add(new StageObject(new Vector2(125, 100), img, false, 0));
        objects.add(new StageObject(new Vector2(225, 100), img, false, 0));
        objects.add(new StageObject(new Vector2(325, 100), img, false, 0));
        objects.add(new StageObject(new Vector2(425, 100), img, false, 0));
        objects.add(new StageObject(new Vector2(75, 150), img, false, 0));
        objects.add(new StageObject(new Vector2(175, 150), img, false, 0));
        objects.add(new StageObject(new Vector2(375, 150), img, false, 0));
        objects.add(new StageObject(new Vector2(475, 150), img, false, 0));

        img = GeneralUtil.readImage(ImageResource.StageObject.HOUSE.getValue());
        house = new StageObject(new Vector2(450, 185), img, false, 0);
        objects.add(house);

        img = GeneralUtil.readImage(ImageResource.StageObject.ROADLIGHT.getValue());
        for(int i = 0; i < 5; i++) {
            objects.add(new StageObject(new Vector2(250, 100 * i), img, false, 0));
        }

        for(int i = 0; i < 5; i++) {
            objects.add(new StageObject(new Vector2(290, 100 * i), img, true, 0));
        }

        carImgs = new BufferedImage[3];
        carImgs[0] = img = GeneralUtil.readImage(ImageResource.StageObject.CAR3.getValue());
        carImgs[1] = img = GeneralUtil.readImage(ImageResource.StageObject.CAR2.getValue());
        carImgs[2] = img = GeneralUtil.readImage(ImageResource.StageObject.CAR1.getValue());
        int type = randomCar();
        car = new StageObject(new Vector2(265, -50), carImgs[type], false, type);

        mushroomImgs = new BufferedImage[4];
        mushroomImgs[0] = img = GeneralUtil.readImage(ImageResource.StageObject.MUSHROOM1.getValue());
        mushroomImgs[1] = img = GeneralUtil.readImage(ImageResource.StageObject.MUSHROOM2.getValue());
        mushroomImgs[2] = img = GeneralUtil.readImage(ImageResource.StageObject.MUSHROOM3.getValue());
        mushroomImgs[3] = img = GeneralUtil.readImage(ImageResource.StageObject.MUSHROOM4.getValue());
        mushrooms = new StageObject[10];
        for(int i = 0; i < mushrooms.length; i++) {
            type = randomMushroom();
            mushrooms[i] = new StageObject(
                    new Vector2(r.nextInt(FIELD_WIDTH - BACK_SIGHT), r.nextInt(FIELD_HEIGHT - BACK_SIGHT)),
                    mushroomImgs[type],
                    false,
                    type
                    );
        }

        mode = TUTORIAL;
        goCount = 0;
    }

    /**
     * タイトル画面で「はじめから」を選択した時のみゲーム起動時の状態に初期化する
     */
    public void resetParam() {
        this.mode = TUTORIAL;
    }

    /**
     * フレームごとの再描画を行う
     *
     * @param g グラフィックスオブジェクト
     */
    @Override
    public void paintComponent(Graphics g) {

        WindowController w = GameController.getWindow();

        // 背景の描画
        g.drawImage(
                sky,
                (int) 0,
                (int) 0,
                (int) w.getWindowWidth(),
                (int) w.getAbsPosY(GROUND),
                null
                );

        g.drawImage(
                mountain1,
                (int) 0,
                (int) 0,
                (int) w.getWindowWidth(),
                (int) w.getAbsPosY(GROUND),
                null
                );

        g.drawImage(
                fog,
                (int) (System.currentTimeMillis()/10 % w.getWindowWidth()),
                (int) 0,
                (int) w.getWindowWidth(),
                (int) w.getAbsPosY(GROUND),
                null
                );

        g.drawImage(
                mountain2,
                (int) 0,
                (int) 0,
                (int) w.getWindowWidth(),
                (int) w.getAbsPosY(GROUND),
                null
                );

        g.drawImage(
                fog,
                (int) ((System.currentTimeMillis() + 1000)/10 % w.getWindowWidth()),
                (int) 0,
                (int) w.getWindowWidth(),
                (int) w.getAbsPosY(GROUND),
                null
                );

        g.drawImage(
                mountain3,
                (int) 0,
                (int) 0,
                (int) w.getWindowWidth(),
                (int) w.getAbsPosY(GROUND),
                null
                );

        g.drawImage(
                fog,
                (int) ((System.currentTimeMillis()+3000)/10 % w.getWindowWidth()),
                (int) 0,
                (int) w.getWindowWidth(),
                (int) w.getAbsPosY(GROUND),
                null
                );

        // ステージオブジェクトおよびプレイヤーの描画
        for(StageObject o: visibleObjBack) {
            drawObject(o, g);
        }

        drawPlayer(g);

        for(StageObject o: visibleObjFront) {
            drawObject(o, g);
        }

/*
            // 左右の見えない壁の可視化
            g.setColor(Color.BLACK);
            drawBorder(new StageObject(new Vector2(0,0), null, false), g);
*/


        // デバッグ用2Dビューの描画
        if(mode == DEBUG) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, (int) w.getAbsPosX(100), (int) w.getAbsPosY(50));
            g.setColor(Color.RED);
            g.fillOval(
                    (int) w.getAbsPosX(player.getPosVec().getX()/5),
                    (int) w.getAbsPosY(player.getPosVec().getY()/5),
                    5,
                    5
                    );

            g.setColor(Color.RED);
            g.drawString("Player.x(2DView): " + Double.toString(player.getPosVec().getX()), 210, 100);
            g.drawString("Player.y(2DView): " + Double.toString(player.getPosVec().getY()), 210, 116);
            g.drawString("status : " + player.getStatus(), 210, 164);
            g.drawString("adjustX : " + Double.toString(SHIFT), 210, 132);
            g.drawString("rate" + Double.toString(SCALE), 210, 148);
        }

        // ヘルプメッセージの描画
        if(helpOn) {
            GeneralUtil.drawSelectOptions(keyHelpList, GeneralUtil.ALIGN_CENTER, g);
            if(mode != TUTORIAL) {
                GeneralUtil.drawSelectOptions(extraHelpList, GeneralUtil.ALIGN_CENTER, g);
            }
        }

        if(isFront(house) && !items.isEmpty()) {
            GeneralUtil.drawStringShiver(
                    sell.getImageFont(),
                    sell.getX(),
                    sell.getY(),
                    sell.getSize(),
                    GeneralUtil.ALIGN_CENTER,
                    g
                    );
        }

        if(mode == TUTORIAL && (int) goCount == 0) {
            g.drawImage(
                    go,
                    (int) w.getAbsPosX(700),
                    (int) w.getAbsPosY(100),
                    (int) w.getAbsPosX(go.getWidth() * 3),
                    (int) w.getAbsPosY(go.getHeight() * 3),
                    null
                    );
        }

        if(player.getStatus() == Player.SPOOK && w.isEmptyStack()) {
            GeneralUtil.drawStringShiver(
                    enter.getImageFont(),
                    enter.getX(),
                    enter.getY(),
                    enter.getSize(),
                    GeneralUtil.ALIGN_CENTER,
                    g
                    );
        }

        // UI用の黒枠の描画
        g.setColor(Color.BLACK);
        g.fillRect(
                 0,
                 (int) w.getAbsPosY(GROUND),
                 (int) w.getWindowWidth(),
                 (int) w.getWindowHeight()
                 );

        // 所持アイテムの描画
        for(int i = 0; i < items.size(); i++) {
            StageObject o = items.get(i);
            g.drawImage(
                    o.getImage(),
                    (int) w.getAbsPosX(o.getPosVec().getX() + 32*i),
                    (int) w.getAbsPosY(o.getPosVec().getY() + 32),
                    (int) w.getAbsPosX(o.getImageWidth()),
                    (int) w.getAbsPosY(o.getImageHeight()),
                    null
                    );
        }

        // 所持アイテム最大時
        if(items.size() >= ITEM_SIZE) {
            Graphics2D g2 = (Graphics2D)g;
            BasicStroke bs = new BasicStroke(10);
            g2.setStroke(bs);
            g.setColor(Color.YELLOW);
            g.drawRect(
                     0,
                     (int) w.getAbsPosY(GROUND),
                     (int) w.getWindowWidth(),
                     (int) w.getWindowHeight()
                     );
            GeneralUtil.drawImageString(
                     max.getImageFont(),
                     max.getX(),
                     max.getY(),
                     max.getSize(),
                     GeneralUtil.ALIGN_LEFT,
                     g
                     );
        }

        // 所持金の表示
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
                 420,
                 20,
                 g
                 );
    }

    /**
     * カメラビュー範囲内のオブジェクトかどうかを判断し、対象のリストに追加する
     *
     * @param o 検査対象のステージオブジェクト
     */
    private void setVisibleObjs(StageObject o) {
        Vector2 distance = player.getPosVec().sub(o.getPosVec());
        double distanceY = distance.getY();
        double distanceX = distance.getX() * getAdjustX(distanceY);

        if(Math.abs(distanceX) < CAMERA_WIDTH/2 + 100
                && distanceY < FORE_SIGHT
                && distanceY > 0
                ) {

            if(distanceY < BACK_SIGHT) {
                visibleObjFront.add(o);
            } else {
                visibleObjBack.add(o);
            }

            o.setDistance(distance.mag());
        }
    }

    /**
     * プレイヤーをカメラビューに描画する
     *
     * @param g グラフィックスオブジェクト
     */
    private void drawPlayer(Graphics g) {
        WindowController w = GameController.getWindow();

        double drawY = GROUND - player.getImageHeight() + player.getZ();
        double anchorX = player.getImageLeftX() + player.getImageWidth()/2;
        double anchorY = drawY + player.getImageHeight() - player.getActualHeight()/2;
        GeneralUtil.setRotationRad(
                player.getDegreeNew(),
                w.getAbsPosX(anchorX),
                w.getAbsPosY(anchorY),
                g
                );

        if(player.getDirection() >= 0) {
            g.drawImage(
                    player.getImage(),
                    (int) w.getAbsPosX(player.getImageLeftX()),
                    (int) w.getAbsPosY(drawY),
                    (int) w.getAbsPosX(player.getImageWidth()),
                    (int) w.getAbsPosY(player.getImageHeight()),
                    null
                    );
        } else {
            g.drawImage(
                    player.getImage(),
                    (int) w.getAbsPosX(player.getImageLeftX()),
                    (int) w.getAbsPosY(drawY),
                    (int) w.getAbsPosX(player.getImageLeftX() + player.getImageWidth()),
                    (int) w.getAbsPosY(drawY + player.getImageHeight()),
                    (int) player.getImage().getWidth(),
                    (int) 0,
                    (int) 0,
                    (int) player.getImage().getHeight(),
                    null
                    );
        }

        GeneralUtil.resetRotation(g);
    }

    /**
     * フィールドの境界をカメラビュー上に描画する
     *
     * @param o フィールド境界を示すステージオブジェクト
     * @param g Graphicsオブジェクト
     */
    private void drawBorder(StageObject o, Graphics g) {
        WindowController w = GameController.getWindow();
        double distanceY = BACK_SIGHT;
        double distanceX = (player.getPosVec().getX() - o.getPosVec().getX()) * getAdjustX(distanceY);

        distanceX = Math.max(CAMERA_WIDTH/2 - distanceX, 0);

        g.setColor(Color.BLACK);
        g.fillRect(
                0,
                0,
                (int) w.getAbsPosX(distanceX),
                (int) w.getWindowHeight()
                );
    }

    /**
     * ステージオブジェクトをカメラビュー上に描画する
     * @param o ステージオブジェクト
     * @param g Graphicsオブジェクト
     */
    private void drawObject(StageObject o, Graphics g) {
        WindowController w = GameController.getWindow();
        double eyesight = CAMERA_WIDTH/2;
        Vector2 distance = player.getPosVec().sub(o.getPosVec());
        double distanceY = distance.getY();
        double distanceX = distance.getX() * getAdjustX(distanceY);
        double rate = getScaleRate(distanceY);
        double drawWidth = o.getImageWidth() * rate;
        double drawHeight = o.getImageHeight() * rate;

        Graphics2D g2 = (Graphics2D) g;
        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);

        // プレイヤーより手前のオブジェクトは透過する
        if(distanceY < BACK_SIGHT) {
            g2.setComposite(composite);
        }

        // 反転描画
        if(o.isReflected()) {
            g.drawImage(
                    o.getImage(),
                    (int) w.getAbsPosX(eyesight - distanceX - drawWidth/2),
                    (int) w.getAbsPosY(GROUND - drawHeight),
                    (int) w.getAbsPosX(eyesight - distanceX - drawWidth/2 + drawWidth),
                    (int) w.getAbsPosY(GROUND),
                    o.getImageWidth(),
                    0,
                    0,
                    o.getImageHeight(),
                    null
                    );
        // 通常描画
        } else {
            g.drawImage(
                    o.getImage(),
                    (int) w.getAbsPosX(eyesight - distanceX - drawWidth/2),
                    (int) w.getAbsPosY(GROUND - drawHeight),
                    (int) w.getAbsPosX(drawWidth),
                    (int) w.getAbsPosY(drawHeight),
                    null
                    );
        }

        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        g2.setComposite(composite);

    }

    /**
     * ステージオブジェクトをカメラビュー上で描画する際の拡大倍率を返す
     *
     * @param distance プレイヤーからステージオブジェクトまでのY方向の距離
     * @return 拡大倍率
     */
    private double getScaleRate(double distance) {
        if(distance == 0) return 0;
        return SCALE/distance;
    }

    /**
     * <pre>
     * ステージオブジェクトをカメラビュー上で描画する際のX方向の補正値を返す
     * プレイヤーとステージオブジェクトの距離が遠いほど補正値は小さくなる
     * </pre>
     *
     * @param distance プレイヤーからステージオブジェクトまでのY方向の距離
     * @return X方向の補正値
     */
    private double getAdjustX(double distance) {
        if(distance == 0) return 0;
        return SHIFT/distance;
    }

    /**
     * プレイヤーの角度に応じた画像を表すラベルを設定する
     */
    private void switchImage() {

        if(player.getAngle() == -Math.PI/2) {
            player.switchLabel(ImageResource.BACK);
        } else if (player.getAngle() == Math.PI/2) {
            player.switchLabel(ImageResource.FRONT);
        }  else if (player.getAngle() == Math.PI/4 || player.getAngle() == Math.PI/4 * 3) {
            player.switchLabel(ImageResource.HALFDOWN);
        } else if (player.getAngle() == -Math.PI/4 || player.getAngle() == -Math.PI/4 * 3) {
            player.switchLabel(ImageResource.HALFUP);
        } else if (player.getAngle() == 0 || player.getAngle() == Math.PI) {
            player.switchLabel(ImageResource.Player1RunRight.LABEL.getValue());
        }
    }

    /**
     * <pre>
     * プレイヤーがフィールド境界に達しているかを判断する
     * 達している場合 true
     * </pre>
     *
     * @return プレイヤーがフィールド境界に達しているか
     */
    private boolean isBorder() {
        Vector2 dest = player.getPosVec().add(player.getDirVec());
        if(dest.getX() < 0
            || dest.getX() > FIELD_WIDTH
            || dest.getY() < 0
            || dest.getY() > FIELD_HEIGHT
            ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * <pre>
     * プレイヤーがフィールドアイテムを表すステージオブジェクトに触れているかどうかを返す
     * 触れている場合 true
     * </pre>
     *
     * @param o ステージオブジェクト
     * @return ステージオブジェクトに触れているかどうか
     */
    private boolean isCollidedItem(StageObject o) {
        Vector2 distance = player.getPosVec().sub(o.getPosVec());
        if(distance.getY() < BACK_SIGHT + 1 && distance.getY() > BACK_SIGHT - 1) {
            return rectCollision(o, distance);
        }
        return false;
    }

    /**
     * <pre>
     * プレイヤーが動的ステージオブジェクトと衝突しているかどうかを返す
     * 衝突している場合 true
     * </pre>
     *
     * @param o  ステージオブジェクト
     * @param dt デルタタイム
     * @return 衝突しているかどうか
     */
    private boolean isCollidedCar(StageObject o, double dt) {
        Vector2 distance = player.getPosVec().sub(o.getPosVec());
        if(distance.getY() > BACK_SIGHT
            && distance.getY() - CAR_SPEED[o.getType()] * dt < BACK_SIGHT
            ) {
            return rectCollision(o, distance);
        }

        return false;
    }

    /**
     * <pre>
     * プレイヤーがステージオブジェクトの規定範囲内の正面にいるかどうかを返す
     * 正面にいる場合 true
     * </pre>
     *
     * @param o ステージオブジェクト
     * @return 正面にいるかどうか
     */
    private boolean isFront(StageObject o) {
        Vector2 distance = player.getPosVec().sub(o.getPosVec());
        if(distance.getY() < 60 && distance.getY() > BACK_SIGHT) {
            return rectCollision(o, distance);
        }
        return false;
    }

    /**
     * <pre>
     * Y座標がプレイヤーとの衝突範囲内にあるステージオブジェクトのX座標を検査し、衝突判定を行う
     * 矩形が重なっている場合 true
     * </pre>
     *
     * @param o        衝突判定対象のステージオブジェクト
     * @param distance プレイヤーとステージオブジェクトのY軸方向の距離
     * @return 矩形が重なっているかどうか
     */
    private boolean rectCollision(StageObject o, Vector2 distance) {
        WindowController w = GameController.getWindow();
        double distanceX = distance.getX() * getAdjustX(distance.getY());
        double rate = getScaleRate(distance.getY());
        double drawWidth = o.getImageWidth() * rate;
        double drawHeight = o.getImageHeight() * rate;

        Rectangle subject = new Rectangle(
                (int) w.getAbsPosX(player.getImageLeftX()),
                (int) w.getAbsPosY(GROUND - player.getImageHeight() + player.getZ()),
                (int) w.getAbsPosX(player.getImageWidth()),
                (int) w.getAbsPosY(player.getImageHeight())
                );
        Rectangle object = new Rectangle(
                (int) w.getAbsPosX(CAMERA_WIDTH/2 - distanceX - drawWidth/2),
                (int) w.getAbsPosY(GROUND - drawHeight),
                (int) w.getAbsPosX(drawWidth),
                (int) w.getAbsPosY(drawHeight)
                );

        // 自分の矩形と相手の矩形が重なっているか調べる
        if (subject.intersects(object)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 確率分布にしたがって車種をランダムに決定する
     *
     * @return 自動車を表すステージオブジェクトのインデックス
     */
    private int randomCar() {
        double rVal = r.nextDouble();
        if(rVal < 0.1) {
            return 0;
        } else if(rVal < 0.3) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * 確率分布にしたがってキノコをランダムに決定する
     *
     * @return キノコを表すステージオブジェクトのインデックス
     */
    private int randomMushroom() {
        double rVal = r.nextDouble();
        if(rVal < 0.01) {
            return 0;
        } else if(rVal < 0.15) {
            return 1;
        } else if(rVal < 0.4) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * キノコの種類に応じた金額を加算する
     *
     * @param o ステージオブジェクト
     */
    private void sellItem(StageObject o) {
        switch(o.getType()) {
        case 0:
            GameController.addCoin(1000);
            break;
        case 1:
            GameController.addCoin(500);
            break;
        case 2:
            GameController.addCoin(100);
            break;
        case 3:
            GameController.addCoin(10);
            break;
        default:
            break;
        }
    }

    /**
     * エフェクトのプロシージャを設定する
     */
    public void setEffect() {
        effect = new EffectController(GameController.getScene(SceneController.TURNTABLE));
        effect.addEffect(new Turntable1(effect));
        effect.addEffect(new Turntable2(effect));
        effect.addEffect(new Turntable3(effect));
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

        // 着地判定
        double newZ = player.getZ() + player.getVZ();
        if(newZ < 0) {
            player.setZ(newZ);
            player.setVZ(player.getVZ() + GRAVITY);
        } else {
            player.setZ(0);
            player.setVZ(0);
        }

        // プレイヤーのステータスごとに処理を分岐
        statusUpdator(player.getStatus(), dt);

        // プレイヤーの移動を確定
        if(!isBorder()) {
            player.proceed(dt);
        } else {
            player.stop();
        }

        // 車の判定
        car.setPosVec(car.getPosVec().add(car.getDirVec().copy().scalar(dt)));
        if(car.getPosVec().getY() > FIELD_HEIGHT) {
            int type = randomCar();
            car = new StageObject(new Vector2(265, -50), carImgs[type], false, type);
            car.initParam();
            car.setDirVec(new Vector2(0, CAR_SPEED[type]));
        }

        if(isCollidedCar(car, dt)) {
            new Thread(new SoundController.PlaySE(SoundResource.SE_CRASH)).start();
            player.hit();
        }

        if(mode != TUTORIAL) {
            // キノコの判定
            for(int i = 0; i < mushrooms.length; i++) {
                if (isCollidedItem(mushrooms[i])) {
                    StageObject m = new StageObject(
                            new Vector2(32, GROUND),
                            mushrooms[i].getImage(),
                            mushrooms[i].isReflected(),
                            mushrooms[i].getType()
                            );
                    m.initParam();
                    m.setWidthRatio(3);
                    m.setHeightRatio(3);
                    m.setDirVec(new Vector2(r.nextInt(20) - 10, -r.nextInt(20) - 10));
                    items.add(m);
                    if(items.size() > ITEM_SIZE) {
                        items.remove(0);
                    }
                    new Thread(new SoundController.PlaySE(SoundResource.SE_BOUNCE)).start();
                    int type = randomMushroom();
                    mushrooms[i] = new StageObject(
                            new Vector2(r.nextInt(FIELD_WIDTH - BACK_SIGHT), r.nextInt(FIELD_HEIGHT - BACK_SIGHT)),
                            mushroomImgs[type],
                            false,
                            type
                            );
                    mushrooms[i].initParam();
                }
            }
        } else {
            goCount = (goCount + dt) % 2;
        }

        visibleObjFront = new ArrayList<StageObject>();
        visibleObjBack = new ArrayList<StageObject>();

        // 視界内のオブジェクトを一括取得
        for(StageObject o: objects) {
            setVisibleObjs(o);
        }

        setVisibleObjs(car);

        for(StageObject o: mushrooms) {
            setVisibleObjs(o);
        }

        // 視界内オブジェクトのリストを距離順にソート
        Collections.sort(visibleObjBack, new Comparator<StageObject>(){
              public int compare(StageObject t1, StageObject t2) {
                return (int) (t2.getDistance() - t1.getDistance());
              }
            });

        Collections.sort(visibleObjFront, new Comparator<StageObject>(){
              public int compare(StageObject t1, StageObject t2) {
                return (int) (t1.getDistance() - t2.getDistance());
              }
            });
    }

    /**
     * キノコが吹き飛ぶアニメーションを実行する
     */
    private void blowMushroom() {
        for(int i = 0; i < items.size(); i++) {
            if(isInsideCamera(items.get(i), i)) {
                items.get(i).setPosVec(items.get(i).getPosVec().add(items.get(i).getDirVec()));
                items.get(i).setDirVec(items.get(i).getDirVec().add(new Vector2(0, GRAVITY)));
            }
        }
    }

    /**
     * <pre>
     * ステージオブジェクトが画面内に描画されているかを判断する
     * 描画されている場合 true
     * </pre>
     *
     * @param o      ステージオブジェクト
     * @param adjust X座標の補正値
     * @return 画面内に描画されているかどうか
     */
    private boolean isInsideCamera(StageObject o, int adjust) {
        Vector2 pos = o.getPosVec();
        return pos.getX() + 32*adjust - o.getImageWidth() > 0
                || pos.getX() + 32*adjust < WindowController.WIDTH
                || pos.getY() + 32 + o.getImageHeight() > 0
                || pos.getY() < WindowController.HEIGHT;
    }

    /**
     * プレイヤーのステータスによって処理を分岐させる
     *
     * @param status プレイヤーのステータス
     * @param dt     デルタタイム
     */
    private void statusUpdator(int status, double dt) {

        // 通常時
        if(status == Player.NORMAL) {
            // 左右の移動
            if(keyConfig.getKeys().get(KeyEvent.VK_A).isPressed()) {
                player.move(new Vector2(-1, 0), dt);
            } else if(keyConfig.getKeys().get(KeyEvent.VK_D).isPressed()) {
                player.move(new Vector2(1, 0), dt);
            }

            // 奥行きの移動
            if(mode != TUTORIAL) {
                if(keyConfig.getKeys().get(KeyEvent.VK_W).isPressed()) {
                    player.move(new Vector2(0, -1), dt);
                } else if(keyConfig.getKeys().get(KeyEvent.VK_S).isPressed()) {
                    player.move(new Vector2(0, 1), dt);
                }
            }

            // ジャンプ
            if(keyConfig.getKeys().get(KeyEvent.VK_SPACE).isPressed() && !(player.getZ() > 0)) {
                if(player.getZ() == 0) {
                    player.jump();
                    new Thread(new SoundController.PlaySE(SoundResource.SE_JUMP)).start();
                }
            }

            // 操作キー一覧の表示
            if(keyConfig.getKeys().get(KeyEvent.VK_H).isPressed()) {
                super.helpOn = !super.helpOn;
            }

            // キノコの売却
            if(keyConfig.getKeys().get(KeyEvent.VK_ENTER).isPressed()
                    && isFront(house)
                    && !items.isEmpty()
                    ) {
                sellItem(items.get(0));
                items.remove(0);
                new Thread(new SoundController.PlaySE(SoundResource.SE_CLOCK)).start();
            }

            if(mode == TUTORIAL && isFront(house)) {
                // TODO 吹き出しを出すシーンを重ねて、奥行きモードへ
                mode = NORMAL;
                GameController.getWindow().pushScene(new Tutorial());
            }

            player.setAngle();
            switchImage();

        // 車に撥ねられた時
        } else if(status == Player.HIT) {
            blowMushroom();
            if(player.getZ() == 0) {
                Vector2 deadPos = player.getPosVec().copy().sub(new Vector2(0, BACK_SIGHT));
                StageObject o = new StageObject(deadPos, player.getFrameHolder().get(ImageResource.DUCK_RIGHT).get(0), false, 0);
                o.initParam();
                objects.add(o);
                player.die();
            }
        // 幽霊状態の時
        } else if(status == Player.SPOOK) {
            blowMushroom();
            if(keyConfig.getKeys().get(KeyEvent.VK_ENTER).isPressed() ){
                GameController.getWindow().pushScene(new GameOver());
            }
        }
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
        return new ActionStage();
    }

    /**
     * 自身のシーンで使用するBGMのファイルパスを返す
     * @see interfaces.GameScene
     *
     * @return BGMのファイルパス
     */
    public String getSound() {
        return SoundResource.BGM_ACTION;
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
        return GameScene.BGM_LOOP;
    }

    /**
     * 自身のシーンで使用するBGMの再生区間を返す
     *
     * @return BGM再生区間を表す始点と終点の値の組
     */
    public Point getDuration() {
        return new Point(0, 1903000);
    }

    /**
     * 各パラメータを初期化する
     */
    public void initParam() {
        helpOn = true;
        player.initParam();

        for(StageObject o: objects) {
            o.initParam();
        }

        car.initParam();
        car.setDirVec(new Vector2(0, CAR_SPEED[car.getType()]));

        for(StageObject o: mushrooms) {
            o.initParam();
        }

        house.initParam();

        items = new ArrayList<StageObject>();
        visibleObjBack = new ArrayList<StageObject>();
        visibleObjFront = new ArrayList<StageObject>();

        keyConfig.releaseAll();
    }


    /**
     * シーンレイヤーのスタックのうち、子シーンからのコールバックを受ける
     *
     * @param res 呼び出し元からのレスポンスコード
     */
    public void callback(int res) {

    }
}
