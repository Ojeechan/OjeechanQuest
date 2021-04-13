package classes.controllers;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import interfaces.GameScene;

/**
 * 音源の読み込み、再生を行う管理クラス
 *
 * @author  Naoki Yoshikawa
 */
public class SoundController {

    // 再生する音源データオブジェクト
    private static Clip bgm;

    /**
     * SE再生用のスレッドに使用するインナークラス
     */
    public static class PlaySE implements Runnable {
        private String path;

        /**
         * BGMのファイルパスを設定する
         *
         * @param path ファイルパス
         */
        public PlaySE(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            Clip c = createClip(SoundController.class.getResource(this.path));
            c.start();
        }
    }

    /**
     * BGMのループなし再生スレッドに使用するインナークラス
     */
    public static class PlayBGM implements Runnable {
        private String path;

        /**
         * BGMのファイルパスを設定する
         *
         * @param path ファイルパス
         */
        public PlayBGM(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            stop();
            setBGM(this.path);
            play();
        }
    }

    /**
     * コールバックの実験
     */
    public static class CallBackBGM implements Runnable {
        private GameScene scene;

        /**
         * BGMのファイルパスを設定する
         *
         * @param scene コールバック先のゲームシーンオブジェクト
         */
        public CallBackBGM(GameScene scene) {
            this.scene = scene;
        }

        @Override
        public void run() {
            stop();
            setBGM(this.scene.getSound());
            pause();
            play();
            scene.callback(0);
        }
    }

    /**
     * BGMのループ再生スレッドに使用するインナークラス
     */
    public static class LoopBGM implements Runnable {
        private String path;

        /**
         * BGMのファイルパスを設定する
         *
         * @param path ファイルパス
         */
        public LoopBGM(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            stop();
            setBGM(this.path);
            loop();
        }
    }

    /**
     * BGMの区間指定ループ再生スレッドに使用するインナークラス
     */
    public static class PeriodLoopBGM implements Runnable {
        private String path;
        private int start;
        private int end;

        /**
         * BGMのファイルパス、指定区間を設定する
         *
         * @param path  ファイルパス
         * @param start ループの開始位置
         * @param end   ループの終了位置
         */
        public PeriodLoopBGM(String path, int start, int end) {
            this.path = path;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            stop();
            setBGM(this.path);
            loop(start, end);
        }
    }

    /**
     * BGMの一時停止スレッドに使用するインナークラス
     */
    public static class PauseBGM implements Runnable {

        @Override
        public void run() {
            pause();
        }
    }

    /**
     * BGMのループなし再開スレッドに使用するインナークラス
     */
    public static class ResumeBGM implements Runnable {

        @Override
        public void run() {
            play();
        }
    }

    /**
     * BGMのループあり再開スレッドに使用するインナークラス
     */
    public static class ResumeLoopBGM implements Runnable {

        @Override
        public void run() {
            loop();
        }
    }

    /**
     * <pre>
     * staticで使用する場合に音源のファイルパスを先に設定する
     * 主にBGMで使用
     * </pre>
     * @param path 音源のファイルパス
     */
    public static synchronized void setBGM(String path) {
        stop();
        bgm = createClip(SoundController.class.getResource(path));
    }

    /**
     * staticで使用する場合に、設定された音源データをループ再生する
     */
    public static void loop() {
        if(bgm != null) {
            bgm.stop();
            bgm.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * staticで使用する場合に、設定された音源データを区間指定してループ再生する
     *
     * @param start ループ開始位置
     * @param end   ループ終了位置
     */
    public static void loop(int start, int end) {

        if(end == GameScene.BGM_END) {
            end = bgm.getFrameLength() - 1;
        }

        bgm.setLoopPoints(start, Math.min(end, bgm.getFrameLength() - 1));

        loop();
    }

    /**
     * staticで使用する場合に、設定された音源データを1度だけ再生する
     */
    public static void play() {
        if(bgm != null) {
            bgm.start();
        }
    }

    /**
     * staticで使用する場合に、設定された音源データを一時停止する
     */
    public static void pause() {
        if(bgm != null) {
            bgm.stop();
        }
    }

    /**
     * staticで使用する場合に、設定された音源データを停止し破棄する
     */
    public static void stop() {
        if(bgm != null) {
            bgm.stop();
            bgm.flush();
            bgm.close();
        }
    }

    /**
     * <pre>
     * staticで使用する場合に、設定された音源データか再生中であるか判断する
     * 再生中であれば true
     * </pre>
     *
     * @return 再生中であるかどうか
     */
    public static boolean isPlaying() {
        return bgm.isRunning();
    }

    /**
     * <pre>
     * staticで使用する場合に、音源データが設定されていうrか判断する
     * 設定されていれば true
     * </pre>
     *
     * @return セットされたBGMがあるかどうか
     */
    public static boolean hasSound() {
        return bgm != null;
    }

    /**
     * ファイルパスから音源データを読み込む
     *
     * @param path 音源データのファイルパス
     * @return Clip型の音源データオブジェクト
     */
    private static Clip createClip(URL path) {
        //指定されたURLのオーディオ入力ストリームを取得
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(path)){
            AudioFormat af = ais.getFormat();
            DataLine.Info dataLine = new DataLine.Info(Clip.class,af);
            Clip c = (Clip)AudioSystem.getLine(dataLine);
            c.open(ais);
            return c;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        return null;
    }
}
