package mazeTheme;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class soundManager {
    public static void playSound(String fileName, float volume) {
        try {
            InputStream is = soundManager.class.getResourceAsStream("/mazeSource/" + fileName);
            if (is == null) {
                System.err.println("File not found: " + fileName);
                return;
            }
            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            setVolume(clip, volume);

            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playBackgroundMusic(String fileName, float volume) {
        try {
            InputStream is = soundManager.class.getResourceAsStream("/mazeSource/" + fileName);
            if (is == null) return;

            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            setVolume(clip, volume);

            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setVolume(Clip clip, float volume) {
        if (volume < 0f || volume > 1f) volume = 0.5f;
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
        gainControl.setValue(dB);
    }
}
