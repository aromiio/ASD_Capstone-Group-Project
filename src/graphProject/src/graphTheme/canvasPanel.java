package graphTheme;

import graphLogic.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

public class canvasPanel extends JPanel {
    private List<Node> nodes;
    private List<Edge> edges;
    private Map<Integer, Image[]> nodeImages = new HashMap<>();
    private Node draggedNode;

    public canvasPanel(List<Node> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
        setOpaque(false);
        loadResources();
        setupDragLogic();
    }

    private void loadResources() {
        try {
            for (int i = 1; i <= 5; i++) {
                Image a = ImageIO.read(getClass().getResource("/graphResource/" + i + ".png"));
                Image b = ImageIO.read(getClass().getResource("/graphResource/" + i + "B.png"));
                nodeImages.put(i, new Image[]{a, b});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setupDragLogic() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (Node n : nodes) {
                    if (e.getPoint().distance(n.getPosition()) < 55) {
                        draggedNode = n;
                        break;
                    }
                }
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedNode != null) {
                    draggedNode.setPosition(e.getX(), e.getY());
                    repaint();
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) { draggedNode = null; }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        for (Edge e : edges) {
            int x1 = (int)e.getSource().getPosition().x;
            int y1 = (int)e.getSource().getPosition().y;
            int x2 = (int)e.getTarget().getPosition().x;
            int y2 = (int)e.getTarget().getPosition().y;

            g2.setStroke(new BasicStroke(e.isPartOfPath() ? 6 : 3));
            g2.setColor(e.isPartOfPath() ? new Color(220, 80, 80) : new Color(140, 140, 140, 150));
            g2.drawLine(x1, y1, x2, y2);

            // Draw Weight Box (implementasi kode lama)
            String wStr = String.valueOf(e.getWeight());
            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2;
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            int sw = g2.getFontMetrics().stringWidth(wStr);
            g2.setColor(new Color(255, 255, 255, 200));
            g2.fillRoundRect(midX - 15, midY - 12, sw+10, 20, 8, 8);
            g2.setColor(Color.BLACK);
            g2.drawString(wStr, midX - sw/2, midY + 5);
        }

        for (Node n : nodes) {
            Image img = n.isPartOfPath() ? nodeImages.get(n.getImageIndex())[1] : nodeImages.get(n.getImageIndex())[0];
            g2.drawImage(img, (int)n.getPosition().x - 55, (int)n.getPosition().y - 55, 110, 110, null);
            g2.setColor(Color.DARK_GRAY);
            int lw = g2.getFontMetrics().stringWidth(n.getLabel());
            g2.drawString(n.getLabel(), (int)n.getPosition().x - lw/2, (int)n.getPosition().y + 75);
        }
    }
}