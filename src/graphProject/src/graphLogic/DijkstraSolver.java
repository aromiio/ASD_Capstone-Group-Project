package graphLogic;

import java.util.*;

public class DijkstraSolver {
    private int[][] adjacencyMatrix;

    public DijkstraSolver(int[][] matrix) {
        this.adjacencyMatrix = matrix;
    }

    public DijkstraResult solve(int startNode, int endNode) {
        int size = adjacencyMatrix.length;
        int[] dist = new int[size];
        int[] prev = new int[size];
        boolean[] visited = new boolean[size];

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[startNode] = 0;

        for (int i = 0; i < size; i++) {
            int u = findMinDistance(dist, visited);
            if (u == -1) break;
            visited[u] = true;

            for (int v = 0; v < size; v++) {
                if (!visited[v] && adjacencyMatrix[u][v] > 0) {
                    int newDist = dist[u] + adjacencyMatrix[u][v];
                    if (newDist < dist[v]) {
                        dist[v] = newDist;
                        prev[v] = u;
                    }
                }
            }
        }
        return new DijkstraResult(dist[endNode], buildPath(prev, endNode));
    }

    private int findMinDistance(int[] dist, boolean[] visited) {
        int min = Integer.MAX_VALUE, index = -1;
        for (int i = 0; i < dist.length; i++) {
            if (!visited[i] && dist[i] < min) {
                min = dist[i];
                index = i;
            }
        }
        return index;
    }

    private List<Integer> buildPath(int[] prev, int target) {
        List<Integer> path = new ArrayList<>();
        for (int at = target; at != -1; at = prev[at]) path.add(at);
        Collections.reverse(path);
        return path;
    }
}