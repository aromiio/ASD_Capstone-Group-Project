package graphLogic;
import java.util.List;

public class DijkstraResult {
    private int distance;
    private List<Integer> route;

    public DijkstraResult(int distance, List<Integer> route) {
        this.distance = distance;
        this.route = route;
    }

    public int getDistance() { return distance; }
    public List<Integer> getRoute() { return route; }
}