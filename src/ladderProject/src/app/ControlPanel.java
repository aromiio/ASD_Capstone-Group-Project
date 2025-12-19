package app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class ControlPanel extends JPanel {
    GameController controller;
    JLabel diceLabel = new JLabel("🎲");
    AnimatedDicePanel dicePanel;
    JLabel probBox = new JLabel("Direction");
    JButton rollBtn;
    JPanel playersBox = new JPanel();
    JPanel scoreboard = new JPanel();

    int currentDiceValue = 1;
    boolean isDiceRolling = false;

    ControlPanel(GameController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(300, 750));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(SpaceTheme.PANEL_DARK);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SpaceTheme.COSMIC_CYAN, 2),
                new EmptyBorder(15,15,15,15)
        ));

        JLabel diceTitle = new JLabel("");
        diceTitle.setForeground(SpaceTheme.STAR_YELLOW);
        diceTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        diceTitle.setAlignmentX(CENTER_ALIGNMENT);
        add(diceTitle);
        add(Box.createVerticalStrut(10));

        dicePanel = new AnimatedDicePanel();
        dicePanel.setPreferredSize(new Dimension(120, 120));
        dicePanel.setMaximumSize(new Dimension(120, 120));
        dicePanel.setAlignmentX(CENTER_ALIGNMENT);
        add(dicePanel);
        add(Box.createVerticalStrut(12));

        probBox.setOpaque(true);
        probBox.setHorizontalAlignment(SwingConstants.CENTER);
        probBox.setPreferredSize(new Dimension(280, 45));
        probBox.setMaximumSize(new Dimension(280, 45));
        probBox.setBackground(SpaceTheme.SPACE_BLUE);
        probBox.setForeground(SpaceTheme.TEXT_LIGHT);
        probBox.setFont(new Font("Segoe UI", Font.BOLD, 13));
        probBox.setBorder(BorderFactory.createLineBorder(SpaceTheme.COSMIC_CYAN, 2));
        add(probBox);
        add(Box.createVerticalStrut(12));
        probBox.setAlignmentX(CENTER_ALIGNMENT);

        rollBtn = new JButton("ROLL DICE");
        rollBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        rollBtn.setForeground(Color.WHITE);
        rollBtn.setBackground(SpaceTheme.ROLLDICE);
        rollBtn.setOpaque(true);
        rollBtn.setFocusPainted(false);
        rollBtn.setBorderPainted(false);
        rollBtn.setMaximumSize(new Dimension(280, 45));
        rollBtn.setCursor(Cursor.getDefaultCursor());
        rollBtn.setAlignmentX(CENTER_ALIGNMENT);
        add(rollBtn);
        add(Box.createVerticalStrut(15));

        JLabel playersTitle = new JLabel("Astronauts");
        playersTitle.setForeground(SpaceTheme.STAR_YELLOW);
        playersTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        playersTitle.setAlignmentX(CENTER_ALIGNMENT);
        add(playersTitle);
        add(Box.createVerticalStrut(4));

        playersBox.setLayout(new BoxLayout(playersBox, BoxLayout.Y_AXIS));
        playersBox.setOpaque(false);
        playersBox.setMaximumSize(new Dimension(280, 180));
        JScrollPane scrollPlayers = new JScrollPane(playersBox);
        scrollPlayers.setOpaque(false);
        scrollPlayers.getViewport().setOpaque(false);
        scrollPlayers.setBorder(null);
        scrollPlayers.setAlignmentX(CENTER_ALIGNMENT);
        add(scrollPlayers);
        add(Box.createVerticalStrut(8));

        JLabel scoreTitle = new JLabel("Mission Points");
        scoreTitle.setForeground(SpaceTheme.STAR_YELLOW);
        scoreTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        scoreTitle.setAlignmentX(CENTER_ALIGNMENT);
        add(scoreTitle);
        add(Box.createVerticalStrut(8));

        scoreboard.setLayout(new BoxLayout(scoreboard, BoxLayout.Y_AXIS));
        scoreboard.setOpaque(false);
        scoreboard.setMaximumSize(new Dimension(280, 180));
        JScrollPane scrollScore = new JScrollPane(scoreboard);
        scrollScore.setOpaque(false);
        scrollScore.getViewport().setOpaque(false);
        scrollScore.setBorder(null);
        add(scrollScore);
        add(Box.createVerticalStrut(15));

        JButton resetRound = createSpaceButton("Reset Mission");
        add(resetRound);
        add(Box.createVerticalStrut(8));

        controller.diceResultLabel = diceLabel;
        controller.probLabel = probBox;
        controller.turnLabel = new JLabel(controller.turnQueue.peek().name);

        refreshPlayers();
        refreshScoreboard();

        rollBtn.addActionListener(e -> {
            if (controller.isAnimating || isDiceRolling) return;

            rollBtn.setEnabled(false);


            animateDiceRoll(() -> {
                controller.performTurn();

                currentDiceValue = Integer.parseInt(controller.diceResultLabel.getText());
                dicePanel.setDiceValue(currentDiceValue);
                dicePanel.repaint();

                if (controller.lastMoveForward) {
                    probBox.setBackground(new Color(100, 200, 150));
                    probBox.setText("FORWARD (" + String.format("%.3f", controller.lastProb) + ")");
                } else {
                    probBox.setBackground(new Color(217, 83, 79));
                    probBox.setText("BACKWARD (" + String.format("%.3f", controller.lastProb) + ")");
                }

                refreshPlayers();
                refreshScoreboard();

                javax.swing.Timer enableTimer = new javax.swing.Timer(1000, evt -> {
                    rollBtn.setEnabled(true);
                });
                enableTimer.setRepeats(false);
                enableTimer.start();
            });
        });

        resetRound.addActionListener(e -> {
            controller.resetBoardKeepWins();
            currentDiceValue = 1;
            dicePanel.setDiceValue(1);
            dicePanel.repaint();
            probBox.setText("Direction");
            probBox.setBackground(SpaceTheme.SPACE_BLUE);
            refreshPlayers();
            refreshScoreboard();
        });

        controller.addRedrawListener(() -> {
            refreshPlayers();
            refreshScoreboard();
            controller.turnLabel.setText(controller.turnQueue.peek().name);
        });
    }

    private JButton createSpaceButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(Color.WHITE);
        btn.setBackground(SpaceTheme.COSMIC_CYAN);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getDefaultCursor());
        btn.setMaximumSize(new Dimension(280, 35));
        btn.setAlignmentX(CENTER_ALIGNMENT);
        return btn;
    }

    private void animateDiceRoll(Runnable onComplete) {
        isDiceRolling = true;
        int[] counter = {0};
        final int maxRolls = 20;

        javax.swing.Timer rollTimer = new javax.swing.Timer(80, null);
        rollTimer.addActionListener(e -> {
            currentDiceValue = 1 + (int)(Math.random() * 6);
            dicePanel.setDiceValue(currentDiceValue);
            dicePanel.repaint();
            counter[0]++;

            if (counter[0] >= maxRolls) {
                rollTimer.stop();
                isDiceRolling = false;

                javax.swing.Timer delayTimer = new javax.swing.Timer(300, evt -> {
                    onComplete.run();
                });
                delayTimer.setRepeats(false);
                delayTimer.start();
            }
        });
        rollTimer.start();
    }

    class AnimatedDicePanel extends JPanel {
        private int diceValue = 1;

        public AnimatedDicePanel() {
            setOpaque(false);
        }

        public void setDiceValue(int value) {
            this.diceValue = value;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = 100;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            if (isDiceRolling) {
                g.setColor(new Color(100, 200, 255, 100));
                g.fillRoundRect(x - 5, y - 5, size + 10, size + 10, 20, 20);
            }

            // Dice background
            g.setColor(SpaceTheme.DICE);
            g.fillRoundRect(x, y, size, size, 15, 15);

            // Border with glow
            g.setColor(isDiceRolling ? SpaceTheme.STAR_YELLOW : SpaceTheme.COSMIC_CYAN);
            g.setStroke(new BasicStroke(3f));
            g.drawRoundRect(x, y, size, size, 15, 15);

            g.setColor(Color.WHITE);
            int dotSize = 12;
            int offset = 22;

            int cx = x + size/2;
            int cy = y + size/2;

            switch (diceValue) {
                case 1:
                    fillDot(g, cx, cy, dotSize);
                    break;
                case 2:
                    fillDot(g, x + offset, y + offset, dotSize);
                    fillDot(g, x + size - offset, y + size - offset, dotSize);
                    break;
                case 3:
                    fillDot(g, x + offset, y + offset, dotSize);
                    fillDot(g, cx, cy, dotSize);
                    fillDot(g, x + size - offset, y + size - offset, dotSize);
                    break;
                case 4:
                    fillDot(g, x + offset, y + offset, dotSize);
                    fillDot(g, x + size - offset, y + offset, dotSize);
                    fillDot(g, x + offset, y + size - offset, dotSize);
                    fillDot(g, x + size - offset, y + size - offset, dotSize);
                    break;
                case 5:
                    fillDot(g, x + offset, y + offset, dotSize);
                    fillDot(g, x + size - offset, y + offset, dotSize);
                    fillDot(g, cx, cy, dotSize);
                    fillDot(g, x + offset, y + size - offset, dotSize);
                    fillDot(g, x + size - offset, y + size - offset, dotSize);
                    break;
                case 6:
                    fillDot(g, x + offset, y + offset, dotSize);
                    fillDot(g, x + offset, cy, dotSize);
                    fillDot(g, x + offset, y + size - offset, dotSize);
                    fillDot(g, x + size - offset, y + offset, dotSize);
                    fillDot(g, x + size - offset, cy, dotSize);
                    fillDot(g, x + size - offset, y + size - offset, dotSize);
                    break;
            }

            g.dispose();
        }

        private void fillDot(Graphics2D g, int x, int y, int size) {
            g.fillOval(x - size/2, y - size/2, size, size);
        }
    }

    private void refreshPlayers() {
        playersBox.removeAll();
        for (Player pl : controller.players) {
            JPanel p = new JPanel(new BorderLayout(5, 5));
            p.setOpaque(false);
            p.setBorder(new EmptyBorder(4, 4, 4, 4));

            String info = String.format("<html><b>%s</b><br/><small>Planet: %d",
                    pl.name, pl.position);
            JLabel l = new JLabel(info);
            l.setForeground(SpaceTheme.TEXT_LIGHT);
            l.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
            p.add(l, BorderLayout.CENTER);

            playersBox.add(p);
        }
        playersBox.revalidate();
        playersBox.repaint();
    }

    private void refreshScoreboard() {
        scoreboard.removeAll();

        for (Player pl : controller.players) {
            JLabel plLabel = new JLabel(String.format("<html><b>%s:</b> <font color='#FFEAA7'>%d</font> pts</html>",
                    pl.name, pl.totalScore));
            plLabel.setForeground(SpaceTheme.TEXT_LIGHT);
            plLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            scoreboard.add(plLabel);
        }

        scoreboard.revalidate();
        scoreboard.repaint();
    }
}
