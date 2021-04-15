package classes.scenes.slot.assets;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import classes.constants.SoundResource;
import classes.controllers.GameController;
import classes.controllers.SoundController;
import classes.utils.GeneralUtil;

/**
 * <pre>
 * スロットモジュールを集約したラッパークラス
 * 各モジュール操作用のインターフェースを持つ
 * </pre>
 *
 * @author Naoki Yoshikawa
 */
public class Slot {

    /* 定数群 */
    private final double WAIT_REEL = 0.7;   // リール全停止から次のレバーオンが可能になるまでの間隔
    private final double WAIT_BUTTON = 0.7; // レバーオンから第一停止が可能になるまでの間隔
    private final int MAXBET = 3;           // 1プレイのベット枚数
    private final int RANGE = 5;            // 有効滑りコマ数

    /* スロットのモジュール */
    private Reel[] reels;        // リールオブジェクトの配列
    private Button[] buttons;    // 停止ボタンオブジェクトの配列
    private Lamp[] lamps;        // 照明系オブジェクトの配列
    private Lever lever;         // レバー
    private BufferedImage frame; // 台枠の画像オブジェクト
    private Mode[] modeTable;    // フラグの当選確率をもつテーブルの配列

    /* 制御フラグ、内部モード */
    private boolean isReady;  // 全リールが停止していているか
    private boolean isReplay; // 次ゲーム用のリプレイフラグ
    private int flag;         // 成立フラグのid
    private int mode;         // 確率分布を決定するモード
    private boolean isBonus;  // ボーナス成立フラグ

    // 通常時のBGMのパス
    private String bgm;

    // 残り払い出し枚数: ボーナス入賞時のみセットされる
    private int payout;

    // 実際の入賞ライン
    // 固定配列の可変長配列ってあまりみたことないけどどうなんだろう？
    private List<int[]> validLine;

    // リールが全停止してから次回レバーオンまでの待ち時間
    private double waitTime;

    // 各リールで目押しが成功しているかを記録する
    private int[][] validStop;

    // 目押しをこぼした時、強制的に書き換えるこぼし目
    private final int[] FAIL = new int[] {0, 0, 1};

    // 初期値を保持するインナークラスインスタンス
    private Init init;

    /**
     * 各スロットモジュールの設定
     *
     * @param path      台枠の画像ファイルのパス
     * @param reels     リールオブジェクトの配列
     * @param buttons   ボタンオブジェクトの配列
     * @param lamps     ランプオブジェクトの配列
     * @param lever     レバーオブジェクト
     * @param modeTable モードオブジェクトの配列
     * @param musics    BGMオブジェクトの配列
     * @param mode      初期モード
     * @param bgm       通常時のBGM音源ファイルのパス
     */
    public Slot(
            String path,
            Reel[] reels,
            Button[] buttons,
            Lamp[] lamps,
            Lever lever,
            Mode[] modeTable,
            Map<Integer, MusicTable> musics,
            int mode,
            String bgm
            ) {
        this.reels = reels;
        this.buttons = buttons;
        this.lamps = lamps;
        this.lever = lever;
        this.frame = GeneralUtil.readImage(path);
        this.modeTable = modeTable;
        this.bgm = bgm;
        this.init = new Init(mode);
    }

    private class Init {
        private int mode;
        private Init(int mode) {
            this.mode = mode;
        }
    }

    /**
     * スロットに設定されたリールオブジェクトの配列を返す
     *
     * @return リールオブジェクトの配列
     */
    public Reel[] getReels() {
        return this.reels;
    }

    /**
     * スロットに設定された台枠の画像オブジェクトを返す
     *
     * @return 台枠の画像オブジェクト
     */
    public BufferedImage getSlotFrame() {
        return this.frame;
    }

    /**
     * スロットに設定されたランプオブジェクトの配列を返す
     *
     * @return ランプオブジェクトの配列
     */
    public Lamp[] getLamps() {
        return this.lamps;
    }

    /**
     * スロットに設定されたボタンオブジェクトの配列を返す
     *
     * @return ボタンオブジェクトの配列
     */
    public Button[] getButtons() {
        return this.buttons;
    }

    /**
     * スロットに設定されたレバーオブジェクトを返す
     *
     * @return レバーオブジェクト
     */
    public Lever getLever() {
        return this.lever;
    }

    /**
     * <pre>
     * リールが全停止して、次の回転が可能かどうかを返す
     * 次の回転が可能な場合 true
     * </pre>
     *
     * @return 次の回転が可能かどうかを
     */
    public boolean isReady() {
        return this.isReady;
    }

    /**
     * 現在のゲームでの払い出し枚数を返す
     *
     * @return 払い出し枚数
     */
    public int getPayout() {
        // ボーナス払い出し中
        if(this.payout > 0) {
            return this.payout;
        // ボーナス以外の成立役、かつ有効ラインに揃えた場合
        // またはフリーズ時の停止後
        } else if(
            (getFlagInfo().getType() > Mode.BONUS
            && checkReady()
            && checkLine())
            || (getFlagInfo().getType() == Mode.FREEZE
            && isReady())
            ) {
            return getFlagInfo().getPayout();
        } else {
            return 0;
        }
    }

    /**
     * 現在の停止位置でのフラグの入賞ラインを返す
     *
     * @return 入賞ライン
     */
    public List<int[]> getValidLine() {
        return this.validLine;
    }

    /**
     * 現行モードの現在のフラグの情報テーブルを返す
     *
     * @return フラグの情報テーブル
     */
    public FlagTable getFlagInfo() {
        return modeTable[mode].getFlagTables()[flag];
    }

    public void pullLever() {
        lever.down();
    }

    /**
     * 次ゲームの抽選を行う
     */
    public void leverOn() {

        // 指定時間、次のレバーオンまでウェイトをかける
        double waitDelta = (System.currentTimeMillis() / 1000.0) - this.waitTime;

        // ウェイト時間を超えていない場合レバーオンさせない
        if (waitDelta < WAIT_REEL) {
            return;
        }

        // フラグの抽選
        this.flag = this.draw();

        // ボタン停止用のウェイトをセット;
        startClock();

        this.isReady = false;

        // 停止位置情報をリセット
        this.validStop = new int[][] {new int[] {}, new int[] {}, new int[] {}};
        this.validLine = new ArrayList<int[]>();

        // リールの入賞時に発光していた図柄の消灯
        offAllReels();

        //3枚掛け リプレイは減算されない
        if(!this.isReplay) {
            GameController.addCoin(-MAXBET);
        }

        this.isReplay = false;

        for(Reel reel: this.reels) {
            reel.start();
        }

        int flagType = getFlagInfo().getType();

        System.out.println("flagType = " + flagType);
        System.out.println("flag = " +flag);
        System.out.println("mode = " +mode);

        if(flagType == Mode.BONUS) {

            // フラグ成立の初回ゲームのみ
            if(!isBonus) {
                // 対応するボーナス入賞待ちテーブルへ
                this.mode = modeTable[mode].getFlagTables()[flag].getDestMode();
                // ここのフラグ再設定は必要　移行先のモードで停止ラインを判別するため
                // ただし0は確実に自分のフラグなので正しく機能する
                // FlagTableにて入賞時の移行先と成立時の移行先を同一フィールドで兼用しているため
                // 分離すればモード移行は一律リール停止後でいいかもしれない
                this.flag = 0;
                // BGMを止める
                new Thread(new SoundController.PauseBGM()).start();
                isBonus = true;
            }

            // これはスロットのパラメータ化する？
            new Thread(new SoundController.PlaySE(SoundResource.SE_BONUS)).start();
        }

        // 一旦SEはベタがき　上のボーナスと合わせてパラメータで設定する
        else if(flagType == Mode.CHERRY || flagType == Mode.SUIKA) {
            new Thread(new SoundController.PlaySE(SoundResource.SE_DORA)).start();
        }

        else if(flagType == Mode.REPLAY) {
            this.isReplay = true;
        }

        else if(flagType == Mode.FREEZE){
            return;
        }

        // フラグに対応するランプのモードをフラグ成立モードへ
        for(Lamp lamp: this.lamps) {
            if (lamp.isResponsible(flagType)) {
                // 今の所右ランプが発光、上ランプは消灯？
                lamp.setFlashMode(1);
            //} else if (lamp.getMode() != 2){
            // 非対応役かつボーナス中でない場合は通常時モード
            } else if (lamp.getMode() == 1){
                lamp.setFlashMode(0);
                lamp.resetTime();
            }
        }

        for(Button button: this.buttons) {
            button.on();
        }
    }

    /**
     * <pre>
     * ボタンを押してリールを停止できる状態かどうかを返す
     * 停止可能なら true
     * </pre>
     *
     * @return リールがボタンで停止可能かどうか
     */
    private boolean checkPushable() {

        double waitDelta = (System.currentTimeMillis() / 1000.0) - this.waitTime;

        // リールが回っていない時、ウェイト時間以内、フリーズ中は押せない
        if(isReady
            || waitDelta < WAIT_BUTTON
            || getFlagInfo().getType() == Mode.FREEZE
            ) {
            return false;
        }

        waitTime = 0.0;

        // 他のリールがすべっている間は押せない
        for(Reel reel: this.reels) {
            if(reel.getDistance() > 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * <pre>
     * 現時点までに他のリールで目押しを失敗しているかどうか
     * すでに失敗しているリールがあれば true
     * </pre>
     *
     * @return 目押しを失敗しているかどうか
     */
    private boolean failStop() {
        for(int[] stopInfo: validStop) {
            if(stopInfo.length == 0) continue;
            boolean valid = true;
            for(int i: stopInfo) {
                if(i == 1) valid = false;
            }
            if(valid) return true;
        }
        return false;
    }

    /**
     * <pre>
     * 停止済みのリールを参照して、停止対象のリールが対象図柄を止められる位置を判断する
     * int[3]がそれぞれ上、中、下段を表し、値が1のときその位置に停止可能であることを示す
     * [1, 0, 0]なら上段のみに停止可能 [1, 1, 1]なら上中下段すべてに停止可能
     * </pre>
     *
     * @return フラグの対象図柄を停止できる位置の配列
     */
    // 現在のフラグに設定されているすべての停止可能位置をバイナリーの配列で取得
    // i=0,1,2;上中下段で値が1の時停止可能
    private int[] getCandPos(int reelPos) {
        int[] posBit = new int[] {0, 0, 0};
        // 外ループは全ラインのリスト
        for(int[] line: getFlagInfo().getLine()) {

            // 取り出したラインごとに停止位置をチェックしていく
            if(posBit[reelPos] == 1) {
                continue;
            }

            boolean valid = true;
            for(int i = 0; i < line.length; i++) {
                // 停止されているリールが該当有効ライン上には停止していない場合、このラインは否定
                if(validStop[i].length != 0 && validStop[i][line[i]] != 1) {
                    valid = false;
                }
            }

            if(valid) {
                posBit[line[reelPos]] = 1;
            }
        }
        return posBit;
    }

    /**
     * 指定したリールを停止する
     *
     * @param button 停止するリール番号
     */
    public void stopReel(int button) {
        // 他のリールが押されてから滑りきるまでは押せない
        if (!checkPushable()) return;

        Reel reel = reels[button];

        if(reel.isSpinning()) {
            // 対応リールの停止位置までの距離を計算する
            int targetIcon = getFlagInfo().getValidIcon()[button];
            int[] candidates = getCandPos(button);

            // 最寄りの対象図柄までの距離を計算
            double distance = calcDistance(targetIcon, button, candidates);

            // [テスト用]次の1コマで止まる
            //double distance = reel.getStopPos(0) % reel.getIconHeight();

            // 上中下段分の距離を足す
            reel.setDistance(distance);

            // スロットは距離を計算し、停止はリールに判断させる
            // 停止ボタンが押されると回転中ではなく滑り中、とみなす
            reel.stop();

            // 押されたボタンは消灯
            this.buttons[button].off();
        }
    }

    /**
     * 対象フラグの図柄を停止させるための最短距離を計算する
     *
     * @param  targetIcon 対象フラグの図柄
     * @param  button     停止するリール番号
     * @param  candidates 停止可能位置(上中下段)
     *
     * @return 対象の停止位置までの距離
     */
    //
    private double calcDistance(int targetIcon, int button, int[] candidates) {
        Reel reel = this.reels[button];
        // これは一コマ内の補正 = コマぴったりに止めるためであって停止位置は影響しない
        double d = reel.getStopPos(0) % reel.getIconHeight();

        // 現在位置をリール図柄のインデックスに変換(floor(pos/32px))
        // リール図柄のインデックス間の距離を求める(現在のインデックスからリール配列リストを遡る)
        // 図柄間の距離+図柄内での現在位置までの距離=移動距離なのでこれを返す
        int[] reelOrder = reel.getReelOrder();

        // 該当リール、該当フラグの時の有効ライン上の停止位置をすべて検証する
        int len = reelOrder.length;
        // すべり対象範囲外の最小値
        int outDist = RANGE * reel.getIconHeight();
        int[] distance = new int[] {outDist, outDist, outDist};

        // 有効停止位置すべてから、最寄りの対象図柄までの距離を調べ、最短のものを採用する
        for(int i = 0;  i < candidates.length; i++) {
            // 有効ライン上になければ無視
            if(candidates[i] == 0) continue;

            int pos = reel.getCurrentIndex(i);

            for(int j = 0; j < RANGE; j++) {
                if(reelOrder[pos] == targetIcon) {
                    distance[i] = reel.getIconHeight() * j;
                    // 見つかった時点で次の停止位置からのチェックへ
                    break;
                }
                pos--;
                if(pos < 0) {
                    pos = len - 1;
                }
            }
        }

        int min = minVal(distance, outDist);
        if(min == outDist) {
            return calculateFail(button);
        } else {
            // 最短距離を持つ停止位置を該当リールの停止位置情報に記録
            validStop[button] = minValIndex(distance, outDist);
        }

        return d + min;
    }

    /**
     * 目押しを外した場合に、こぼし目に停止するための距離を再計算する
     *
     * @param button 停止するリール番号
     *
     * @return 対象の停止位置までの距離
     */
    // こぼし目の再計算
    private double calculateFail(int button) {

        Reel reel = this.reels[button];
        // これは一コマ内の補正 = コマぴったりに止めるためであって停止位置は影響しない
        double d = reel.getStopPos(0) % reel.getIconHeight();
        int[] reelOrder = reel.getReelOrder();

        // 該当リール、該当フラグの時の有効ライン上の停止位置をすべて検証する
        int len = reelOrder.length;
        // 強制中段
        int pos = reel.getCurrentIndex(1);
        int distance = 0;

        for(int j = 0; j < len; j++) {
            if(reelOrder[pos] == FAIL[button]) {
                distance = reel.getIconHeight() * j;
                // 見つかった時点で次の停止位置からのチェックへ
                break;
            }
            pos--;
            if(pos < 0) {
                pos = len - 1;
            }
        }

        validStop[button] = new int[] {0, 0, 0};

        return d + distance;
    }

    /**
     * 配列中の最小値を返す
     *
     * @param minArray 検査対象の配列
     * @param initMin  最小値の退避変数
     *
     * @return 配列中の最小値
     */
    private int minVal(int[] minArray, int initMin) {
        for(int i = 0; i < minArray.length; i++) {
            if(minArray[i] < initMin) {
                initMin = minArray[i];
            }
        }
        return initMin;
    }

    /**
     * <pre>
     * 配列中の最小値をもつインデックスを返す
     * 複数該当した場合は全てを配列で返す
     * </pre>
     *
     * @param minArray 検査対象の配列
     * @param initMin  最小値の退避変数
     *
     * @return 配列中の最小値をもつインデックスの配列
     */
    // 配列中の最小値を持つインデックスを返す
    // 複数該当した場合は複数返す
    private int[] minValIndex(int[] minArray, int initMin) {
        int[] minIndex = new int[minArray.length];
        for(int i = 0; i < minArray.length; i++) {
            if(minArray[i] <= initMin) {
                if(minArray[i] < initMin) {
                    initMin = minArray[i];
                    for(int j = 0; j < minArray.length; j++) {
                        minIndex[j] = 0;
                    }
                }
                minIndex[i] = 1;
            }
        }
        return minIndex;
    }

    /**
     * リール図柄をスクロールさせて回転を表現する
     *
     * @param dt デルタタイム
     */
    public void spinReel(double dt) {
        for(Reel reel: reels) {

            // レバオンから停止ボタンが押されるまで
            if (reel.isSpinning()) {

                if(getFlagInfo().getType() == Mode.FREEZE) {
                    // ここで目的のリール位置まで逆回転して、到達したら全リールを一斉に特定の出目で出すメソッドを呼ぶ
                    reel.spin(-dt / 10);
                    stopFreeze();
                } else {
                    reel.spin(dt);
                }

            // 停止したら何もしない
            } else {
                // 停止ボタンが押されてから滑りきるまで
                if (reel.getDistance() > 0){
                    reel.adjustStop(dt);
                }
            }
        }
    }

    /**
     * <pre>
     * 指定速度でリールを逆回転させる
     * 特殊演出用
     * </pre>
     * @param dt デルタタイム
     */
    public void reverseReel(double dt) {
        for(Reel reel: reels) {
            reel.spin(-dt);
        }
    }

    /**
     * フリーズ後のフリー回転状態から、特定の出目に自動停止させる
     */
    private void stopFreeze() {
        if(reels[0].getCurrentIndex(0) == 5) {
            for(Reel reel: reels) {
                reel.setDistance(32*4);
                reel.stop();
            }
        }
    }

    /**
     * リール位置を指定の位置に強制的に揃える
     *
     * @param reelPos 表示させるリール位置の配列
     */
    public void adjustReel(double[] reelPos) {
        for(int i = 0; i < reels.length; i++) {
            reels[i].setReelPos(reelPos[i]);
        }
    }

    /**
     * レバーのアニメーションを進行させる
     *
     * @param dt タイムパラメータ
     */
    public void animateLever(double dt) {
        if(lever.isDown()) {
            lever.proceed(dt);
        }
    }

    /**
     * ランプのアニメーションを進行させる
     *
     * @param dt タイムパラメータ
     */
    public void animateLamp(double dt) {
        for(Lamp lamp: lamps) {
            lamp.proceed(dt);
        }
    }

    /**
     * 1回転ごとのフラグ抽選を行う
     *
     * @return フラグのID
     */
    private int draw() {
        Random r = new Random();
        double complement = 1.0;
        for(int i = 0; i < modeTable[mode].getFlagTables().length - 1; i++) {
            double prob = modeTable[mode].getFlagTables()[i].getProbability() / complement;
            if(prob > r.nextDouble()) {
                return i;
            } else {
                complement = 1.0 - prob;
            }
        }

        return modeTable[mode].getFlagTables().length - 1;
    }

    /**
     * <pre>
     * 子役重複を抽選する
     * 重複当選していたら true
     * </pre>
     *
     * @return 重複当選しているかどうか
     */
    private boolean subDraw() {
        Random r = new Random();
        double prob = modeTable[mode].getFlagTables()[flag].getSubProbability();
        if(prob > r.nextDouble()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ランプの発光モードを切り替える
     *
     * @param mode 発光モードのインデックス
     */
    private void changeLampMode(int mode) {
        for(Lamp lamp: this.lamps) {
            lamp.setFlashMode(mode);
        }
    }

    /**
     * 全てのリールを点灯させる
     */
    private void flashAllReels() {
        for(Reel reel: this.reels) {
            reel.flash();
        }
    }

    /**
     * 全てのリールを消灯させる
     */
    private void offAllReels() {
        for(Reel reel: this.reels) {
            reel.off();
        }
    }

    /**
     * <pre>
     * 回転開始後、全てのリールが停止しているかどうかを判断する
     * 全てのリールが停止している場合 true
     * </pre>
     *
     * @return 全てのリールが停止しているかどうか
     */
    public boolean checkReady() {
        // リールは全て止まっているか
        for(Reel reel: this.reels) {
            if(reel.isSpinning() || reel.getDistance() > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * <pre>
     * 全てのリールが停止してからの経過時間を計測する
     * 次のレバーオンまでのウェイトに使用する
     * </pre>
     */
    public void startClock() {
        this.waitTime = System.currentTimeMillis() / 1000.0;
    }

    /**
     * 入賞ラインのチェックと、入賞時の払い出しを行う
     */
    public void payout() {

        int flagType = getFlagInfo().getType();

        // フラグに対応するランプのモードをフラグ成立モードへ
        for(Lamp lamp: this.lamps) {
            if (lamp.isResponsible(flagType)) {
                // 今の所右ランプが発光、上ランプは消灯？
                lamp.setFlashMode(0);
            }
        }

        if(flagType == Mode.FREEZE) {
            // 払い出し枚数を加算
            GameController.addCoin(getFlagInfo().getPayout());

            new Thread(new SoundController.PlaySE(getFlagInfo().getPaySound())).start();
            new Thread(new SoundController.LoopBGM(this.bgm)).start();
        }

        else if(checkLine()) {

            // ハズレ以外なら成立ラインを発光させる
            if(flagType != Mode.HAZURE) {
                flashAllReels();
            }

            // ボーナスフラグが立っている状態で入賞させる
            if(flagType == Mode.BONUS) {
                // 入賞時に対応するBGMを流す
                startBGM();
                isBonus = false;
                // 払い出し枚数を設定
                payout = getFlagInfo().getPayout();
                // 払い出しモード(ベルのみ)へ
                mode = getFlagInfo().getDestMode();
                changeLampMode(2);

            } else {
                // 払い出し音
                if(!getFlagInfo().getPaySound().isEmpty()) {
                    new Thread(new SoundController.PlaySE(getFlagInfo().getPaySound())).start();
                }

                // 払い出し枚数を加算
                GameController.addCoin(getFlagInfo().getPayout());

                // ボーナス入賞時のみ残り払い出し枚数が発生するはず
                if(payout > 0) {
                    payout -= getFlagInfo().getPayout();
                    // 規定枚数の払い出し終了で通常時へ移行する
                    if(payout <= 0) {
                        payout = 0;
                        mode = 0;
                        flag = modeTable[mode].getFlagTables().length - 1;
                        new Thread(new SoundController.LoopBGM(this.bgm)).start();
                        changeLampMode(0);
                    }
                }
            }
        }

        // 重複抽選は取りこぼしても行われる
        if(subDraw()) {
            this.mode = getFlagInfo().getDestMode();
            this.flag = 0;
            new Thread(new SoundController.PauseBGM()).start();
            changeLampMode(1);
        }

        this.isReady = true;
    }

    /**
     * <pre>
     * 成立フラグの対応図柄が、有効ライン上に停止しているかチェックする
     * 有効ライン上に停止していたら true
     * </pre>
     *
     * @return 図柄が有効ライン上に停止しているかどうか
     */
    private boolean checkLine() {

        if(getFlagInfo().getLine() == null) return false;

        for(int[] lines: getFlagInfo().getLine()) {

            if(lines == null) return false;

            boolean valid = true;
            for(int i = 0; i < lines.length; i++) {

                // 対応リールの有効ライン上に止まっているコマのインデックス
                // 小数誤差の回避のため、コマの停止位置判断は各上中下段のコマの中央の高さでおこなう
                int pos = reels[i].getAdjustedIndex(lines[i], reels[i].getIconHeight() / 2);

                // コマのインデックスから図柄を取得し、対象の図柄と一致するか
                if(reels[i].getReelOrder()[pos] != getFlagInfo().getValidIcon()[i]) {
                    valid = false;
                    break;
                }
            }

            if(valid) {
                int[] validLine = lines.clone();

                // 雑だけどチェリーは角だけ光らせたいので一旦これで書き換える
                if(getFlagInfo().getType() == Mode.CHERRY) {
                    validLine[1] = -1;
                    validLine[2] = -1;
                }
                this.validLine.add(validLine);
            }
        }

        return !validLine.isEmpty();
    }

    /**
     * モードごとに設定されたボーナスBGMを再生する
     */
    private void startBGM () {
        MusicTable music = modeTable[mode].getMusics().get(flag);
        new Thread(
                new SoundController.PeriodLoopBGM(music.getPath(), music.getStartTime(), music.getEndTime())
                ).start();
    }

    /**
     * 各パラメータを初期化する
     */
    public void initParam() {
        this.mode = this.init.mode;
        this.isReady = true;
        this.flag = modeTable[mode].getFlagTables().length - 1;
        this.payout = 0;
        this.validLine = new ArrayList<int[]>();
        this.waitTime = 0.0;
        this.isReplay = false;
        this.isBonus = false;
        this.validStop = new int[][] {new int[] {}, new int[] {}, new int[] {}};

        for(Reel reel: reels) {
            reel.initParam();
        }

        for(Button button: buttons) {
            button.initParam();
        }

        for(Lamp lamp: lamps) {
            lamp.initParam();
        }

        lever.initParam();
    }
}
