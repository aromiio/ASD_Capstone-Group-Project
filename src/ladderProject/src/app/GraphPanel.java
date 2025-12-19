package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

class GraphPanel extends JPanel {
    GameController controller;
    final int NODE_R = 22;
    List<StarParticle> backgroundStars = new ArrayList<>();

    BufferedImage planetNormal;
    BufferedImage planetScore;
    BufferedImage planetStar;
    BufferedImage planetEnd;
    BufferedImage planetStarts;

    BufferedImage getPlanetImage(int nodeIndex) {
        if (nodeIndex == 0) {
            return planetStarts;
        }

        if (nodeIndex == controller.GOAL_NODE) {
            return planetEnd;
        }

        if (nodeIndex > 0 && (nodeIndex + 1) % 5 == 0) {
            return planetStar;
        }

        Integer score = controller.graph.getNodeScore(nodeIndex);
        boolean claimed = controller.graph.isScoreClaimed(nodeIndex);
        if (score != null && !claimed) {
            return planetScore;
        }

        return planetNormal;
    }

    GraphPanel(GameController controller) {
        this.controller = controller;
        setBackground(SpaceTheme.DEEP_SPACE);

        setPreferredSize(new Dimension(850, 750));
        controller.addRedrawListener(() -> repaint());

        try {
            planetNormal = ImageIO.read(getClass().getResource("/assets/planet_normal.png"));
            planetScore  = ImageIO.read(getClass().getResource("/assets/planet_score.png"));
            planetStar   = ImageIO.read(getClass().getResource("/assets/planet_star.png"));
            planetEnd    = ImageIO.read(getClass().getResource("/assets/planet_end.png"));
            planetStarts    = ImageIO.read(getClass().getResource("/assets/planet_starts.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 200; i++) {
            backgroundStars.add(new StarParticle(900, 800));
        }

        javax.swing.Timer animTimer = new javax.swing.Timer(40, e -> {
            for (StarParticle star : backgroundStars) {
                star.update(1000);
            }
            repaint();
        });
        animTimer.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int node = findNodeAt(e.getPoint());
                if (node >= 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Planet ").append(node + 1).append("\n\n");

                    Integer s = controller.graph.getNodeScore(node);
                    sb.append("Score: ").append(s==null ? "None" : s + " points").append("\n");

                    if (node > 0 && (node+1) % 5 == 0) {
                        sb.append("⭐ STAR PLANET - Extra dice roll!\n");
                    }

                    if (controller.graph.getLadderDest(node) != null) {
                        sb.append("🚀 Wormhole → Planet ").append(controller.graph.getLadderDest(node)).append("\n");
                    }

                    List<Integer> nbs = controller.graph.getNeighbors(node);
                    sb.append("🔗 Connected: ").append(nbs.toString()).append("\n");

                    JOptionPane.showMessageDialog(GraphPanel.this, sb.toString(), "Planet Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    int findNodeAt(Point p) {
        for (int i=0;i<controller.graph.size;i++) {
            Point pt = controller.graph.getPos(i);
            Ellipse2D circle = new Ellipse2D.Double(pt.x - NODE_R, pt.y - NODE_R, NODE_R*2, NODE_R*2);
            if (circle.contains(p)) return i;
        }
        return -1;
    }


    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (StarParticle star : backgroundStars) {
            g.setColor(new Color(255, 255, 255, (int)(star.alpha * 255)));
            g.fillOval((int)star.x, (int)star.y, (int)star.size, (int)star.size);
        }

        Graph graph = controller.graph;

        g.setStroke(new BasicStroke(3f));
        g.setColor(SpaceTheme.EDGE_COLOR);
        for (int i=0;i<graph.size-1;i++) {
            if (graph.g[i][i+1] > 0) {
                Point a = graph.getPos(i), b = graph.getPos(i+1);
                g.drawLine(a.x, a.y, b.x, b.y);
            }
        }

        g.setStroke(new BasicStroke(4f));
        for (Map.Entry<Integer,Integer> e : graph.ladders.entrySet()) {
            Point a = graph.getPos(e.getKey()), b = graph.getPos(e.getValue());

            g.setColor(new Color(100, 200, 255, 80));
            g.setStroke(new BasicStroke(8f));
            g.drawLine(a.x, a.y, b.x, b.y);

            g.setColor(SpaceTheme.LADDER_COLOR);
            g.setStroke(new BasicStroke(4f));
            g.drawLine(a.x, a.y, b.x, b.y);
        }

        if (controller.shortestPathActivated && controller.shortestPath != null && controller.shortestPath.size() > 1) {
            g.setStroke(new BasicStroke(5f));
            g.setColor(new Color(255, 160, 60, 200));
            for (int k=0;k<controller.shortestPath.size()-1;k++) {
                Point a = graph.getPos(controller.shortestPath.get(k));
                Point b = graph.getPos(controller.shortestPath.get(k+1));
                g.drawLine(a.x, a.y, b.x, b.y);
            }
        }

        for (int i = 0; i < graph.size; i++) {
            Point p = graph.getPos(i);

            boolean isStar = (i > 0 && (i + 1) % 5 == 0);

            if (isStar) {
                g.setColor(new Color(255, 220, 120, 90));
                int glowSize = 62;
                g.fillOval(
                        p.x - glowSize / 2,
                        p.y - glowSize / 2,
                        glowSize,
                        glowSize
                );
            }

            BufferedImage img = getPlanetImage(i);

            int size = 48;
            if (i == controller.GOAL_NODE || i == 0) size = 70;


            int x = p.x - size / 2;
            int y = p.y - size / 2;

            g.drawImage(img, x, y, size, size, null);
            String number = String.valueOf(i + 1);

            g.setFont(new Font("Segoe UI", Font.BOLD, 12));
            FontMetrics fm = g.getFontMetrics();

            int textW = fm.stringWidth(number);
            int textH = fm.getHeight();

            int badgeSize = 18;
            int bx = p.x + size / 2 - badgeSize + 4;
            int by = p.y - size / 2 - 4;

            g.setColor(new Color(0, 0, 0, 180));
            g.fillOval(bx, by, badgeSize, badgeSize);

            g.setColor(Color.WHITE);
            g.drawString(
                    number,
                    bx + (badgeSize - textW) / 2,
                    by + (badgeSize + fm.getAscent()) / 2 - 2
            );

            Integer score = graph.getNodeScore(i);
            boolean claimed = graph.isScoreClaimed(i);

            if (score != null && !claimed) {
                g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));

                String txt = "💎 " + score;

                FontMetrics fme = g.getFontMetrics();
                int tw = fme.stringWidth(txt);

                g.setColor(new Color(0, 0, 0, 160)); // background gelap
                g.fillRoundRect(
                        p.x - tw / 2 - 4,
                        p.y + size / 2 - 2,
                        tw + 8,
                        fm.getHeight(),
                        8,
                        8
                );

                g.setColor(Color.WHITE);
                g.drawString(
                        txt,
                        p.x - tw / 2,
                        p.y + size / 2 + fm.getAscent() - 2
                );
            }

        }

        Map<Integer, java.util.List<Player>> atNode = new HashMap<>();
        for (Player pl : controller.players) {
            atNode.computeIfAbsent(pl.position, k -> new ArrayList<>()).add(pl);
        }

        for (Map.Entry<Integer, java.util.List<Player>> en : atNode.entrySet()) {
            Point p = controller.graph.getPos(en.getKey());
            java.util.List<Player> list = en.getValue();
            for (int i=0;i<list.size();i++) {
                Player pl = list.get(i);
                int size = 20;
                int offsetX = -NODE_R + 4 + (i*24);
                int offsetY = -NODE_R - 28;

                Color color = pl.color;

                g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
                g.fillOval(p.x + offsetX - 2, p.y + offsetY - 2, size + 4, size + 4);

                g.setColor(color);
                g.fillOval(p.x + offsetX, p.y + offsetY, size, size);
                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(2f));
                g.drawOval(p.x + offsetX, p.y + offsetY, size, size);

                g.setFont(new Font("Segoe UI", Font.BOLD, 10));
                FontMetrics fm = g.getFontMetrics();
                String fullName = pl.name;
                int nameWidth = fm.stringWidth(fullName);
                int nameHeight = fm.getHeight();

                g.setColor(new Color(0, 0, 0, 180));
                g.fillRoundRect(p.x + offsetX - 2, p.y + offsetY + size + 2, nameWidth + 4, nameHeight, 5, 5);

                g.setColor(Color.WHITE);
                g.drawString(fullName, p.x + offsetX, p.y + offsetY + size + nameHeight - 2);
            }
        }

        g.dispose();
    }
}
