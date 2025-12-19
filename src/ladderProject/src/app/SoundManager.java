package app;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static Map<String, Clip> soundCache = new HashMap<>();
    private static Clip bgmClip;
    private static boolean soundEnabled = true;

    public static final String BGM = "bgm.wav";
    public static final String MOVE_FORWARD = "move_forward.wav";
    public static final String MOVE_BACKWARD = "move_backward.wav";
    public static final String WIN = "win.wav";
    public static final String WORMHOLE = "wormhole.wav";
    public static final String COLLECT = "collect.wav";

    private static Clip loadSound(String filename) {
        try {
            if (soundCache.containsKey(filename)) {
                return soundCache.get(filename);
            }

            var url = SoundManager.class
                    .getClassLoader()
                    .getResource("sounds/" + filename);

            if (url == null) {
                System.err.println("❌ Sound file not found in resources: sounds/" + filename);
                return null;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            soundCache.put(filename, clip);
            return clip;

        } catch (Exception e) {
            System.err.println("Error loading sound: " + filename);
            e.printStackTrace();
            return null;
        }
    }

    public static void playSound(String soundFile) {
        if (!soundEnabled) return;

        try {
            Clip clip = loadSound(soundFile);
            if (clip != null) {
                // Stop kalau masih playing
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0); // Reset ke awal
                clip.start();
            }
        } catch (Exception e) {
            System.err.println("Error playing sound: " + soundFile);
            e.printStackTrace();
        }
    }

    public static void playBGM() {
        if (!soundEnabled) return;

        try {
            if (bgmClip != null && bgmClip.isRunning()) {
                return; // Sudah playing
            }

            bgmClip = loadSound(BGM);
            if (bgmClip != null) {
                bgmClip.setFramePosition(0);
                bgmClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop forever

                setVolume(bgmClip, 0.6f);
            }
        } catch (Exception e) {
            System.err.println("Error playing BGM");
            e.printStackTrace();
        }
    }

    public static void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
        }
    }

    public static void toggleBGM() {
        if (bgmClip == null) return;

        if (bgmClip.isRunning()) {
            bgmClip.stop();
        } else {
            bgmClip.start();
        }
    }

    private static void setVolume(Clip clip, float volume) {
        if (clip == null) return;

        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        } catch (Exception e) {
            System.err.println("Volume control not supported");
        }
    }

    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
        if (!enabled && bgmClip != null) {
            bgmClip.stop();
        } else if (enabled) {
            playBGM();
        }
    }

    public static void cleanup() {
        stopBGM();
        for (Clip clip : soundCache.values()) {
            if (clip != null) {
                clip.close();
            }
        }
        soundCache.clear();
    }
}