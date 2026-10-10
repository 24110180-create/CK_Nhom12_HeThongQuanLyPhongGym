package com.nhom12.gym.util;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

/**
 * Tiện ích phát âm thanh mô phỏng máy quét mã vạch Honeywell Voyager 1450g
 * và cổng kiểm soát phân làn Flap Barrier tự động.
 * Tạo sóng âm PCM thuần túy bằng Java Sound API, không cần file .wav ngoài.
 */
public class SoundHelper {

    /**
     * Tiếng bíp "Tít" thành công sắc nét (1200Hz, 85ms)
     */
    public static void playSuccessBeep() {
        new Thread(() -> {
            try {
                generateTone(1200, 85, 0.7);
            } catch (Exception e) {
                java.awt.Toolkit.getDefaultToolkit().beep();
            }
        }).start();
    }

    /**
     * Tiếng cảnh báo "Tít - Tít" trầm khi từ chối quét hoặc lỗi (420Hz)
     */
    public static void playErrorBeep() {
        new Thread(() -> {
            try {
                generateTone(420, 110, 0.8);
                Thread.sleep(70);
                generateTone(420, 130, 0.8);
            } catch (Exception e) {
                java.awt.Toolkit.getDefaultToolkit().beep();
            }
        }).start();
    }

    /**
     * Tiếng chuông thông báo nhẹ (1500Hz -> 1800Hz)
     */
    public static void playNotice() {
        new Thread(() -> {
            try {
                generateTone(1400, 50, 0.5);
                generateTone(1760, 90, 0.6);
            } catch (Exception ignored) {}
        }).start();
    }

    private static void generateTone(int hz, int msecs, double vol) throws Exception {
        float sampleRate = 8000f;
        byte[] buf = new byte[(int) (sampleRate * (msecs / 1000.0))];
        for (int i = 0; i < buf.length; i++) {
            double angle = i / (sampleRate / hz) * 2.0 * Math.PI;
            buf[i] = (byte) (Math.sin(angle) * 127.0 * vol);
        }

        AudioFormat af = new AudioFormat(sampleRate, 8, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();
        sdl.write(buf, 0, buf.length);
        sdl.drain();
        sdl.close();
    }
}
