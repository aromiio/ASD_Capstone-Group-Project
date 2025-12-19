package graphLogic;

public class Edge {
    private Node source, target;
    private int weight;
    private boolean isPartOfPath = false;

    public Edge(Node source, Node target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public Node getSource() { return source; }
    public Node getTarget() { return target; }
    public int getWeight() { return weight; }
    public boolean isPartOfPath() { return isPartOfPath; }
    public void setPartOfPath(boolean partOfPath) { isPartOfPath = partOfPath; }
}