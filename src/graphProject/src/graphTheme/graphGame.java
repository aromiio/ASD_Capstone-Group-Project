package graphTheme;

import graphLogic.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class graphGame extends JFrame {
    private canvasPanel canvas;
    private sideBar sidebar;
    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private int[][] adjMatrix;

    public graphGame() {
        setTitle("Dijkstra Graph Ghibli");
        setSize(1199, 741);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/graphResource/bg_game.png"));
        JLabel background = new JLabel(bgIcon);
        background.setBounds(0, 0, 1199, 741);
        setContentPane(background);
        setLayout(null);

        sidebar = new sideBar();
        sidebar.setBounds(0, 0, 1199, 741);
        canvas = new canvasPanel(nodes, edges);
        canvas.setBounds(0, 0, 1199, 741);

        add(sidebar);
        add(canvas);

        sidebar.visualizeBtn.addActionListener(e -> buildGraph());
        sidebar.findPathBtn.addActionListener(e -> runDijkstra());

        buildGraph();
        setVisible(true);
    }

    private void buildGraph() {
        try {
            nodes.clear(); edges.clear();
            String[] ls = sidebar.labelArea.getText().trim().split("\\s+");
            String[] rs = sidebar.matrixArea.getText().trim().split("\n");
            int n = ls.length;
            adjMatrix = new int[n][n];

            for (int i = 0; i < n; i++) {
                double a = i * (2 * Math.PI / n);
                nodes.add(new Node(i, ls[i], 650 + 250 * Math.cos(a), 400 + 200 * Math.sin(a), i + 1));
            }

            for (int i = 0; i < n; i++) {
                String[] cs = rs[i].trim().split("\\s+");
                for (int j = 0; j < n; j++) {
                    adjMatrix[i][j] = Integer.parseInt(cs[j]);
                    if (adjMatrix[i][j] > 0 && i < j) edges.add(new Edge(nodes.get(i), nodes.get(j), adjMatrix[i][j]));
                }
            }
            sidebar.updateCombos(ls);
            canvas.repaint();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Format Error!"); }
    }

    private void runDijkstra() {
        int s = sidebar.fromCombo.getSelectedIndex();
        int e = sidebar.toCombo.getSelectedIndex();

        String[] labelNames = sidebar.labelArea.getText().trim().split("\\s+");

        DijkstraSolver solver = new DijkstraSolver(adjMatrix);
        DijkstraResult res = solver.solve(s, e);

        if (res.getDistance() == Integer.MAX_VALUE) {
            sidebar.resultArea.setForeground(Color.RED);
            sidebar.resultArea.setText("Path Not Found!\nIsland is isolated.");
        } else {
            sidebar.resultArea.setForeground(new Color(60, 60, 60));
            sidebar.resultArea.setText("✨ Path Found!\n\n" +
                    "Total Distance: " + res.getDistance() + "\n" +
                    "Route: " + formatRoute(res.getRoute(), labelNames));
            animate(res.getRoute());
        }
    }

    private String formatRoute(List<Integer> route, String[] labels) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < route.size(); i++) {
            sb.append(labels[route.get(i)]);
            if (i < route.size() - 1) sb.append(" -> ");
        }
        return sb.toString();
    }

    private void animate(List<Integer> route) {
        for (Node n : nodes) n.setPartOfPath(false);
        for (Edge ed : edges) ed.setPartOfPath(false);
        Timer t = new Timer(500, null);
        final int[] step = {0};
        t.addActionListener(ev -> {
            if (step[0] < route.size()) {
                nodes.get(route.get(step[0])).setPartOfPath(true);
                if (step[0] > 0) {
                    int p = route.get(step[0]-1), c = route.get(step[0]);
                    for (Edge ed : edges) if ((ed.getSource().getId()==p && ed.getTarget().getId()==c) || (ed.getSource().getId()==c && ed.getTarget().getId()==p)) ed.setPartOfPath(true);
                }
                canvas.repaint(); step[0]++;
            } else t.stop();
        });
        t.start();
    }
}