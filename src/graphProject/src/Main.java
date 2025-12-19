import graphTheme.graphGame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(graphGame::new);
    }
}