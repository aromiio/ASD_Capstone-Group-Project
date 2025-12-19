import mazeTheme.mazeGame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        //System.setProperty("sun.java2d.uiScale", "1.0");
        SwingUtilities.invokeLater(() -> {
            new mazeGame();
        });
    }
}