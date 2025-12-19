package graphLogic;

import java.awt.geom.Point2D;

public class Node {
    private int id;
    private String label;
    private Point2D.Double position;
    private int imageIndex;
    private boolean isPartOfPath = false;

    public Node(int id, String label, double x, double y, int imgIdx) {
        this.id = id;
        this.label = label;
        this.position = new Point2D.Double(x, y);
        this.imageIndex = (imgIdx % 5 == 0) ? 5 : (imgIdx % 5);
    }

    public int getId() { return id; }
    public String getLabel() { return label; }
    public Point2D.Double getPosition() { return position; }
    public int getImageIndex() { return imageIndex; }
    public boolean isPartOfPath() { return isPartOfPath; }
    public void setPartOfPath(boolean partOfPath) { isPartOfPath = partOfPath; }
    public void setPosition(double x, double y) { this.position.x = x; this.position.y = y; }
}