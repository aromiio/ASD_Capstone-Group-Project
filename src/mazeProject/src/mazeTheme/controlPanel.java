package mazeTheme;

import mazeLogic.maze;
import javax.swing.*;
import java.awt.*;

public class controlPanel extends JPanel{
    public controlPanel(gamePanel gp, maze m){
        setLayout(null);
        setOpaque(false);
        setBounds(0, 0, 1199, 720);

        add(createInvisibleBtn(746, 228, 405, 50, () -> {
            m.generatePrims(() -> gp.repaint());
        }));

        add(createInvisibleBtn(746, 296, 75, 75, () -> gp.runSolver("BFS")));
        add(createInvisibleBtn(831, 296, 75, 75, () -> gp.runSolver("DFS")));
        add(createInvisibleBtn(746, 382, 75, 75, () -> gp.runSolver("Dijkstra")));
        add(createInvisibleBtn(831, 382, 75, 75, () -> gp.runSolver("AStar")));

        add(createInvisibleBtn(743, 470, 163, 40, () -> { //reset
            for(int r=0; r<21; r++) {
                for(int c=0; c<21; c++) {
                    m.grid[r][c].type = 0;
                    m.grid[r][c].resetStatus();
                }
            }
            gp.calculateStats();
            gp.repaint();
        }));
    }

    private JButton createInvisibleBtn(int x, int y, int w, int h, Runnable action) {
        JButton b = new JButton();
        b.setBounds(x, y, w, h);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> {
            soundManager.playSound("button.wav", 0.8f);
            action.run();
        });
        return b;
    }
}
