package mazeTheme;

import mazeLogic.maze;
import javax.swing.*;
import java.awt.*;

public class mazeGame extends JFrame{
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);

    public mazeGame(){
        setTitle("The Maze Runner: Escape from WICKED");
        soundManager.playBackgroundMusic("bg.wav", 0.3f);
        mainContainer.setLayout(cardLayout);

        maze m = new maze();
        gamePanel gp = new gamePanel(m);
        controlPanel cp = new controlPanel(gp, m);

        JPanel menuScreen = createMenuScreen();
        JLayeredPane gameLayer = new JLayeredPane();
        gameLayer.setPreferredSize(new Dimension(1199, 741));

        gp.setBounds(0,0,1199,741);
        cp.setBounds(0,0,1199,741);

        gameLayer.add(gp, Integer.valueOf(0));
        gameLayer.add(cp, Integer.valueOf(1));

        mainContainer.add(menuScreen, "MENU_SCREEN");
        mainContainer.add(gameLayer, "GAME_SCREEN");
        add(mainContainer);

        pack();

        cardLayout.show(mainContainer, "MENU_SCREEN");
        setVisible(true);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JPanel createMenuScreen(){
        JPanel panel = new JPanel(null){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                try{
                    ImageIcon bgMenu = new ImageIcon(getClass().getResource("/mazeSource/bg_menu.png"));
                    g.drawImage(bgMenu.getImage(), 0,0,1199,741,null);
                } catch (Exception e){
                    System.out.println("Error Loading Menu Background");
                }
            }
        };
        JButton btnPlay = createInvisibleBtn(537, 517, 114, 38, () -> {
            soundManager.playSound("button.wav", 0.7f);
            cardLayout.show(mainContainer, "GAME_SCREEN");
        });
        panel.add(btnPlay);
        return panel;
    }
    private JButton createInvisibleBtn(int x, int y, int w, int h, Runnable action){
        JButton b = new JButton();
        b.setBounds(x, y, w, h);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> action.run());
        return b;
    }
}
