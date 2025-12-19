package app;

import javax.swing.*;
import java.awt.*;
import java.util.List;

class GameWindow {
    private JFrame frame;
    private GraphPanel graphPanel;
    private ControlPanel controlPanel;
    private GameController controller;

    GameWindow(List<String> playerNames) {
        controller = new GameController(playerNames);

        frame = new JFrame("🚀 Space Adventure: Mission Active");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 850);
        frame.setLayout(new BorderLayout(10,10));
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(SpaceTheme.DEEP_SPACE);

        graphPanel = new GraphPanel(controller); // Using custom GraphPanel
        JScrollPane scrollPane = new JScrollPane(graphPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        controlPanel = new ControlPanel(controller); // Using custom ControlPanel

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topBar.setBackground(SpaceTheme.PANEL_DARK);
        topBar.setBorder(BorderFactory.createLineBorder(SpaceTheme.COSMIC_CYAN, 2));

        JButton quitBtn = new JButton("<- Abort Mission");
        quitBtn.setOpaque(true);
        quitBtn.setBackground(new Color(200, 50, 50));
        quitBtn.setForeground(Color.darkGray);
        quitBtn.addActionListener(e -> {
            frame.dispose();
            new StartScreen();
        });
        topBar.add(quitBtn);

        frame.add(topBar, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.EAST);

        frame.setVisible(true);
        controller.redraw();
    }
}