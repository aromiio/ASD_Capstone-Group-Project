package app;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.*;

class StarPanel extends JPanel {
    private java.util.List<StarParticle> stars = new ArrayList<>();
    private javax.swing.Timer timer;

    public StarPanel() {
        setBackground(SpaceTheme.DEEP_SPACE);
        for (int i = 0; i < 100; i++) {
            stars.add(new StarParticle(800, 600));
        }
        timer = new javax.swing.Timer(30, e -> {
            for (StarParticle s : stars) s.update(getHeight());
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.WHITE);
        for (StarParticle s : stars) {
            float alpha = Math.max(0, Math.min(1, s.alpha));
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.fill(new Ellipse2D.Double(s.x, s.y, s.size, s.size));
        }
    }
}
