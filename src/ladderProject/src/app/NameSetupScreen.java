package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.plaf.basic.BasicButtonUI;

class NameSetupScreen extends JFrame {
    private List<JTextField> nameFields = new ArrayList<>();

    public NameSetupScreen(int playerCount) {
        setTitle("Astronaut Registration");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        StarPanel panel = new StarPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 50, 30, 50));

        JLabel header = new JLabel("ENTER NAMES");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(SpaceTheme.STAR_YELLOW);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(header);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        Color[] colors = {Color.magenta, Color.pink, Color.GREEN, Color.yellow};

        for (int i = 0; i < playerCount; i++) {
            JPanel row = new JPanel(new BorderLayout(10, 10));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(400, 40));

            JLabel lbl = new JLabel("P" + (i + 1));
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lbl.setForeground(colors[i % colors.length]);
            lbl.setPreferredSize(new Dimension(40, 30));

            JTextField tf = new JTextField("Astronaut " + (i + 1));
            tf.setBackground(SpaceTheme.SPACE_BLUE);
            tf.setForeground(Color.WHITE);
            tf.setCaretColor(Color.YELLOW);
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            nameFields.add(tf);
            row.add(lbl, BorderLayout.WEST);
            row.add(tf, BorderLayout.CENTER);

            panel.add(row);
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        panel.add(Box.createVerticalGlue());

        JButton btnLaunch = new JButton("LAUNCH MISSION");
        btnLaunch.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLaunch.setBackground(SpaceTheme.COSMIC_CYAN);
        btnLaunch.setForeground(SpaceTheme.DEEP_SPACE);
        btnLaunch.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLaunch.setFocusPainted(false);
        btnLaunch.setBorderPainted(false);
        btnLaunch.setContentAreaFilled(true);
        btnLaunch.setOpaque(true);
        btnLaunch.setRolloverEnabled(false);
        btnLaunch.setUI(new BasicButtonUI());

        btnLaunch.addActionListener(e -> {
            List<String> names = new ArrayList<>();
            for (JTextField tf : nameFields) {
                String n = tf.getText().trim();
                names.add(n.isEmpty() ? "Unknown" : n);
            }
            dispose();
            new GameWindow(names); // Launch Main Game
        });

        panel.add(btnLaunch);
        add(panel);
        setVisible(true);
    }
}
