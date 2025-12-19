package app;

import javax.swing.*;
import java.util.*;

public class SpaceBoardGame {
    static Map<String, Integer> GLOBAL_WINS = new HashMap<>();
    static Map<String, Integer> GLOBAL_SCORES = new HashMap<>();

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }

        SoundManager.playBGM();
        SwingUtilities.invokeLater(StartScreen::new);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            SoundManager.cleanup();
        }));
    }
}
