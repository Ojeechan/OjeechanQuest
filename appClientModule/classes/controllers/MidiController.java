package classes.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

/**
 * MIDIコントローラー入力の管理クラス
 *
 * @author  Naoki Yoshikawa
 */
public class MidiController {

    // キーのモード
    // キーが押されている間はisPressed()はtrueを返す
    public static final int NORMAL = 0;
    // キーがはじめに押されたときだけisPressed()はtrueを返す
    // キーが押され続けても2回目以降はfalseを返す
    // このモードを使うとジャンプボタンを押し続けてもジャンプを繰り返さない
    public static final int DETECT_INITIAL_PRESS_ONLY = 1;

    // キーの状態
    // キーが離された
    private static final int STATE_RELEASED = 0;
    // キーが押されている
    private static final int STATE_PRESSED = 1;
    // キーが離されるのを待っている
    private static final int STATE_WAITING_FOR_RELEASE = 2;
    private Map<Integer, MidiController.Midi> registeredMidi;

    // PCに接続されているMIDIデバイスのリスト
    private static List<MidiDevice> devices;

    // すべてのキー12半音に対応する数値
    public static final int C       = 0;
    public static final int C_SHARP = 1;
    public static final int D       = 2;
    public static final int D_SHARP = 3;
    public static final int E       = 4;
    public static final int F       = 5;
    public static final int F_SHARP = 6;
    public static final int G       = 7;
    public static final int G_SHARP = 8;
    public static final int A       = 9;
    public static final int A_SHARP = 10;
    public static final int B       = 11;

    /**
     * MIDIコンフィグとMIDIデバイスリストの初期化
     */
    public MidiController() {
        registeredMidi = new HashMap<Integer, MidiController.Midi>();
        devices = new ArrayList<MidiDevice>();
    }

    /**
     * 個別のMIDI信号を定義するインナークラス
     */
    public class Midi {

        // キーのモード
        private int mode;

        // キーが押された回数
        private int amount;

        // キーの状態
        private int state;

        /**
         * デフォルトコンストラクタ
         * 連打制御を行わないモード
         */
        public Midi() {
            this(NORMAL);
        }

        /**
         * モード指定のコンストラクタ
         *
         * @param mode キーイベント処理のモード
         */
        public Midi(int mode) {
            this.mode = mode;
            reset();
        }

        /**
         * キーの押された回数をリセットし、押されていない状態に戻す
         */
        public void reset() {
            state = STATE_RELEASED;
            amount = 0;
        }

        /**
         * キーを押下状態にする
         */
        public void press() {
            // STATE_WAITING_FOR_RELEASEのときは押されたことにならない
            if (state != STATE_WAITING_FOR_RELEASE) {
                amount++;
                state = STATE_PRESSED;
            }
        }

        /**
         * キーを押されていない状態にする
         */
        public void release() {
            state = STATE_RELEASED;
        }

        /**
         * キーが押されている間に受け取った入力回数を返す
         *
         * @return 1回のキー押下で受け付けた入力回数
         */
        public int getPressedAmount() {
            return amount;
        }

        /**
         * <pre>
         * キーが押下状態にあるかどうかを判断する
         * 押下状態にあればtrue
         * </pre>
         *
         * @return 押下状態にあるかどうか
         */
        public boolean isPressed() {
            if (amount != 0) {
                if (state == STATE_RELEASED) {
                    amount = 0;
                } else if (mode == DETECT_INITIAL_PRESS_ONLY) {
                    // 最初の1回だけtrueを返して押されたことにする
                    // 次回からはSTATE_WAITING_FOR_RELEASEになるため
                    // キーを押し続けても押されたことにならない
                    state = STATE_WAITING_FOR_RELEASE;
                    amount = 0;
                }

                return true;
            }

            return false;
        }
    }

    /**
     * 登録されたキーコンフィグのマップを返す
     *
     * @return キーコンフィグ
     */
    public Map<Integer, MidiController.Midi> getMidi() {
        return this.registeredMidi;
    }

    /**
     * デフォルトのキーコンフィグをもつコントローラーオブジェクトを返すファクトリーメソッド
     *
     * @return デフォルトのコントローラーオブジェクト
     */
    public static MidiController getDefaultMidi() {
        MidiController defaultConfig = new MidiController();
        defaultConfig.registeredMidi.put(MidiController.C, defaultConfig.new Midi());
        defaultConfig.registeredMidi.put(MidiController.C_SHARP, defaultConfig.new Midi());
        defaultConfig.registeredMidi.put(MidiController.D, defaultConfig.new Midi());
        defaultConfig.registeredMidi.put(MidiController.D_SHARP, defaultConfig.new Midi());
        defaultConfig.registeredMidi.put(MidiController.E, defaultConfig.new Midi(DETECT_INITIAL_PRESS_ONLY));
        defaultConfig.registeredMidi.put(MidiController.F, defaultConfig.new Midi());
        defaultConfig.registeredMidi.put(MidiController.F_SHARP, defaultConfig.new Midi());
        defaultConfig.registeredMidi.put(MidiController.G, defaultConfig.new Midi());
        defaultConfig.registeredMidi.put(MidiController.G_SHARP, defaultConfig.new Midi());
        defaultConfig.registeredMidi.put(MidiController.A, defaultConfig.new Midi());
        defaultConfig.registeredMidi.put(MidiController.A_SHARP, defaultConfig.new Midi());
        defaultConfig.registeredMidi.put(MidiController.B, defaultConfig.new Midi());
        return defaultConfig;
    }

    /**
     * <pre>
     * MIDIコントローラーが接続されているかを判断する
     * 一台以上接続されている場合 true
     * </pre>
     *
     * @return MIDIコントローラーが接続されているかどうか
     */
    public boolean hasMidiConnection() {
        return !devices.isEmpty();
    }

    /**
     * 接続されているMIDIコントローラーのイベントトリガーに引数のオブジェクトを登録する
     *
     * @param receiver MIDIの入力イベントを購買するオブジェクト
     */
    public void getMIDIController(Receiver receiver)
    {
        if(devices != null) {
            return;
        }

        MidiDevice device;
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

        for (int i = 0; i < infos.length; i++) {
            try {
                device = MidiSystem.getMidiDevice(infos[i]);
                //does the device have any transmitters?
                //if it does, add it to the device list
                devices.add(device);

                //get all transmitters
                List<Transmitter> transmitters = device.getTransmitters();
                //and for each transmitter

                for(int j = 0; j<transmitters.size();j++) {
                    //create a new receiver
                    transmitters.get(j).setReceiver(
                            //using my own MidiInputReceiver
                            //new MidiInputReceiver(device.getDeviceInfo().toString())
                            receiver
                    );
                }

                Transmitter trans = device.getTransmitter();
                trans.setReceiver(receiver/*new MidiInputReceiver(device.getDeviceInfo().toString())*/);

                //open each device
                device.open();
                //if code gets this far without throwing an exception
                //print a success message
                System.out.println(device.getDeviceInfo()+" Was Opened");
                System.out.println(" open? " + device.isOpen());


            } catch (MidiUnavailableException e) {

            }
        }
    }

    /**
     * MIDIデバイスを全て閉じる
     */
    public static void closeDevice() {
        for (MidiDevice d: devices) {
            if(d.isOpen())
            {
                d.close();
            }
        }
    }

}
