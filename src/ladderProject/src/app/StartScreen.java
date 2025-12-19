package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.plaf.basic.BasicButtonUI;

public class StartScreen extends JFrame {

    public StartScreen() {
        setTitle("🚀 Space Adventure: Mission Control");
        setSize(700, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        StarPanel mainPanel = new StarPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("SPACE ADVENTURE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(SpaceTheme.STAR_YELLOW);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Mission Control & Leaderboard");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(SpaceTheme.COSMIC_CYAN);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(title);
        mainPanel.add(subtitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel statsPanel = createStatsPanel();
        mainPanel.add(statsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel setupPanel = new JPanel();
        setupPanel.setOpaque(false);
        setupPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel lblPlayers = new JLabel("Number of Astronauts (2-4): ");
        lblPlayers.setForeground(Color.WHITE);
        lblPlayers.setFont(new Font("Segoe UI", Font.BOLD, 14));

        SpinnerModel model = new SpinnerNumberModel(2, 2, 4, 1);
        JSpinner spinner = new JSpinner(model);
        spinner.setPreferredSize(new Dimension(60, 30));

        setupPanel.add(lblPlayers);
        setupPanel.add(spinner);

        mainPanel.add(setupPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnNext = new JButton("PREPARE LAUNCH");
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnNext.setBackground(SpaceTheme.PLANET_GREEN);
        btnNext.setForeground(SpaceTheme.DEEP_SPACE);

        btnNext.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnNext.setMaximumSize(new Dimension(200, 50));
        btnNext.setCursor(Cursor.getDefaultCursor());

        btnNext.setFocusPainted(false);
        btnNext.setBorderPainted(false);
        btnNext.setContentAreaFilled(true);
        btnNext.setOpaque(true);
        btnNext.setRolloverEnabled(false);
        btnNext.setUI(new BasicButtonUI());

        btnNext.addActionListener(e -> {
            int count = (Integer) spinner.getValue();
            dispose();
            new NameSetupScreen(count);
        });

        mainPanel.add(btnNext);
        add(mainPanel);
        setVisible(true);
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(800, 250));

        panel.add(createSubStatBox("Top Scores", SpaceTheme.NEBULA_PURPLE, SpaceBoardGame.GLOBAL_SCORES, "pts"));
        panel.add(createSubStatBox("Mission Victories", SpaceTheme.COSMIC_CYAN, SpaceBoardGame.GLOBAL_WINS, "wins"));
        return panel;
    }

    private JPanel createSubStatBox(String title, Color color, Map<String, Integer> data, String suffix) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(SpaceTheme.PANEL_DARK);
        box.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(color, 2),
                title, 0, 0, new Font("Segoe UI", Font.BOLD, 14), color));

        List<Map.Entry<String, Integer>> sorted = data.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(3)
                .collect(Collectors.toList());

        if (sorted.isEmpty()) {
            JLabel lbl = new JLabel("No data yet...");
            lbl.setForeground(Color.GRAY);
            box.add(lbl);
        } else {
            int rank = 1;
            for (Map.Entry<String, Integer> entry : sorted) {
                JLabel lbl = new JLabel(rank + ". " + entry.getKey() + " : " + entry.getValue() + " " + suffix);
                lbl.setForeground(SpaceTheme.TEXT_LIGHT);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                box.add(lbl);
                box.add(Box.createRigidArea(new Dimension(0, 5)));
                rank++;
            }
        }
        return box;
    }
}
