package app;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Graph {
    int size;
    int[][] g;
    Point[] pos;
    Map<Integer,Integer> ladders = new HashMap<>();
    Map<Integer,Integer> nodeScores = new HashMap<>();
    Set<Integer> claimedScores = new HashSet<>();

    Graph(int size) {
        this.size = size;
        g = new int[size][size];
        pos = new Point[size];
    }

    public void layoutZigZagWavy(int width, int height, int margin, int cols) {
        int rows = (int) Math.ceil((double) size / cols);

        int cellW = (width - margin * 2) / cols;
        int cellH = (height - margin * 2) / rows;

        Random r = new Random(42); // FIXED seed → layout konsisten

        for (int i = 0; i < size; i++) {
            int row = i / cols;
            int col = i % cols;

            if (row % 2 == 1) {
                col = cols - 1 - col;
            }

            int baseX = margin + col * cellW + cellW / 2;
            int baseY = margin + row * cellH + cellH / 2;

            int maxOffset = cellH / 5;
            int yOffset = r.nextInt(maxOffset * 2) - maxOffset;

            pos[i] = new Point(baseX, baseY + yOffset);
        }
    }

    public void connectRandomEdges(int attempts) {
        Random r = ThreadLocalRandom.current();

        for (int i=0;i<size-1;i++) {
            int w = 1;
            g[i][i+1] = w;
            g[i+1][i] = w;
        }
    }

    public void addLadder(int a, int b) {
        ladders.put(a, b);
    }

    public void clearLadders() { ladders.clear(); }
    public Integer getLadderDest(int a) { return ladders.get(a); }
    public void setNodeScore(int idx, int score) { nodeScores.put(idx, score); }
    public Integer getNodeScore(int idx) { return nodeScores.get(idx); }
    public boolean isScoreClaimed(int idx) { return claimedScores.contains(idx); }
    public void claimNodeScore(int idx) { claimedScores.add(idx); }
    public void clearNodeScores() { nodeScores.clear(); claimedScores.clear(); }

    public List<Integer> getNeighbors(int idx) {
        List<Integer> res = new ArrayList<>();
        for (int j=0;j<size;j++) if (g[idx][j] > 0) res.add(j);
        return res;
    }

    public Point getPos(int idx) { return pos[idx]; }

    public List<Integer> dijkstraPath(int ori, int dest) {
        int[] dist = new int[this.size];
        for (int i=0;i<this.size;i++) dist[i] = Integer.MAX_VALUE;
        dist[ori] = 0;
        boolean[] isVisited = new boolean[this.size];
        int[] prev = new int[this.size];
        Arrays.fill(prev, -1);
        for (int i=0;i<this.size;i++) {
            int nextNode = findTheNextNode(isVisited, dist);
            if (nextNode == -1) break;
            isVisited[nextNode] = true;
            for (int j=0;j<this.size;j++) {
                if (!isVisited[j] && this.g[nextNode][j] > 0 && (dist[nextNode] + this.g[nextNode][j] < dist[j])) {
                    dist[j] = dist[nextNode] + this.g[nextNode][j];
                    prev[j] = nextNode;
                }
            }
        }
        if (dist[dest] == Integer.MAX_VALUE) return Collections.emptyList();
        List<Integer> path = new ArrayList<>();
        route(dest, ori, prev, path);
        return path;
    }

    private void route(int n, int from, int[] prev, List<Integer> collector) {
        if (n == from) collector.add(n);
        else {
            if (prev[n] == -1) collector.add(n);
            else {
                route(prev[n], from, prev, collector);
                collector.add(n);
            }
        }
    }

    private int findTheNextNode(boolean[] isVisited, int[] dist) {
        int min = Integer.MAX_VALUE;
        int minVertex = -1;
        for (int i=0;i<this.size;i++) {
            if (!isVisited[i] && dist[i] < min) {
                min = dist[i];
                minVertex = i;
            }
        }
        return minVertex;
    }

    static final int MIN_NODE_DISTANCE = 70;

    void resolveOverlaps() {
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                Point a = pos[i];
                Point b = pos[j];

                double dx = b.x - a.x;
                double dy = b.y - a.y;
                double dist = Math.sqrt(dx*dx + dy*dy);

                if (dist < MIN_NODE_DISTANCE && dist > 0) {
                    double push = (MIN_NODE_DISTANCE - dist) / 2;

                    double ux = dx / dist;
                    double uy = dy / dist;

                    a.x -= ux * push;
                    a.y -= uy * push;
                    b.x += ux * push;
                    b.y += uy * push;
                }
            }
        }
    }
}