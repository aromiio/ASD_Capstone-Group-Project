package app;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameController {
    Graph graph;
    java.util.List<Player> players;
    Queue<Player> turnQueue = new LinkedList<>();
    Map<String,Integer> winsMap;
    public JLabel diceResultLabel = new JLabel("-");
    JLabel probLabel = new JLabel("-");
    JLabel turnLabel = new JLabel("-");
    Random rng = ThreadLocalRandom.current();
    Map<String, Integer> highScoresMap;

    final int NODE_COUNT = 64;
    final int LADDER_COUNT = 5;
    final int SCORE_NODE_COUNT = 30;
    final int START_NODE = 0;
    final int GOAL_NODE = NODE_COUNT - 1;

    boolean shortestPathActivated = false;
    java.util.List<Integer> shortestPath = new ArrayList<>();
    java.util.List<Runnable> redrawListeners = new ArrayList<>();

    boolean extraTurnGranted = false;
    boolean isAnimating = false;
    boolean lastMoveForward;
    double lastProb;

    GameController(List<String> playerNames) {

        for (String name : playerNames) {
            SpaceBoardGame.GLOBAL_WINS.putIfAbsent(name, 0);
            SpaceBoardGame.GLOBAL_SCORES.putIfAbsent(name, 0);
        }

        this.winsMap = SpaceBoardGame.GLOBAL_WINS;
        this.highScoresMap = SpaceBoardGame.GLOBAL_SCORES;
        graph = new Graph(NODE_COUNT);
        graph.layoutZigZagWavy(900, 700, 50, 8);
        graph.resolveOverlaps();

        graph.connectRandomEdges(NODE_COUNT * 2);
        graph.clearLadders();

        for (int i=0;i<LADDER_COUNT;i++) {
            int a = rng.nextInt(NODE_COUNT);
            int b = rng.nextInt(NODE_COUNT);
            if (a==b || Math.abs(a-b) <= 1) { i--; continue; }
            graph.addLadder(a,b);
        }

        graph.clearNodeScores();
        Set<Integer> chosen = new HashSet<>();
        while (chosen.size() < SCORE_NODE_COUNT) {
            int idx = rng.nextInt(NODE_COUNT);
            if (idx == START_NODE || idx == GOAL_NODE) continue;
            chosen.add(idx);
        }
        for (int idx : chosen) {
            int s = 10 + rng.nextInt(91);
            graph.setNodeScore(idx, s);
        }

        Color[] PLAYER_COLORS = {
                new Color(0x7D47C4),
                new Color(0xD35981),
                new Color(0x7BBC66),
                new Color(0xFFD600)
        };

        players = new ArrayList<>();

        for (int i = 0; i < playerNames.size(); i++) {
            String name = playerNames.get(i);

            Color color = PLAYER_COLORS[i % PLAYER_COLORS.length];
            Player p = new Player(name, color);

            p.position = START_NODE;
            players.add(p);
            turnQueue.add(p);
        }


        shortestPathActivated = isPrime(START_NODE);
        if (shortestPathActivated) {
            shortestPath = graph.dijkstraPath(START_NODE, GOAL_NODE);
        }

        turnLabel.setText(players.get(0).name);
    }

    void addRedrawListener(Runnable r) { redrawListeners.add(r); }
    void redraw() { for (Runnable r : redrawListeners) r.run(); }

    public void performTurn() {
        Player current = turnQueue.peek();
        int turnStartPos = current.position;

        if (turnQueue.isEmpty() || isAnimating) return;

        int dice = rng.nextInt(6) + 1;
        diceResultLabel.setText(String.valueOf(dice));

        double prob = rng.nextDouble();
        lastProb = prob;
        lastMoveForward = prob >= 0.2;

        probLabel.setText(String.format("%.3f", prob));

        java.util.List<Integer> movementPath = new ArrayList<>();
        movementPath.add(current.position);

        if (lastMoveForward) {
            int targetPos = Math.min(GOAL_NODE, current.position + dice);
            for (int i = current.position + 1; i <= targetPos; i++) {
                movementPath.add(i);
            }
        } else {
            int targetPos = Math.max(0, current.position - dice);
            for (int i = current.position - 1; i >= targetPos; i--) {
                movementPath.add(i);
            }
        }

        animateMovement(current, movementPath, () -> {

            int afterMovePos = current.position;

            if (isPrime(turnStartPos)) {
                List<Integer> path = graph.dijkstraPath(afterMovePos, GOAL_NODE);
                if (path.size() > 1 && path.get(1) > afterMovePos) {
                    current.position = path.get(1);
                }
            }

            Integer ladderDest = graph.getLadderDest(current.position);
            if (ladderDest != null && ladderDest > current.position) {
                int oldPos = current.position;
                current.position = ladderDest;

                SoundManager.playSound(SoundManager.WORMHOLE);

                JOptionPane.showMessageDialog(null,
                        "🚀 " + current.name + " used a wormhole from planet " + (oldPos + 1) + " to " + (ladderDest + 1) + "!",
                        "Wormhole Travel!",
                        JOptionPane.INFORMATION_MESSAGE);
                redraw();
            }

            checkStarCollection(current);

            // Check for score
            checkScoreCollection(current);

            // Check win
            if (current.position == GOAL_NODE) {
                winsMap.put(current.name, winsMap.getOrDefault(current.name, 0) + 1);

                // Update high scores for leaderboard
                finalizeGameStats(current);

                SoundManager.playSound(SoundManager.WIN);
                JOptionPane.showMessageDialog(null,
                        "🎉 " + current.name + " reached the goal planet and wins!\n💎 Score: " + current.totalScore,
                        "Mission Complete!",
                        JOptionPane.INFORMATION_MESSAGE);
                resetBoardKeepWins();
                return;
            }

            if (!extraTurnGranted) {
                Player pl = turnQueue.poll();
                turnQueue.add(pl);
                turnLabel.setText(turnQueue.peek().name);
            } else {
                turnLabel.setText(current.name + " (⭐ Extra Turn!)");
            }

            redraw();
        });
    }

    private void finalizeGameStats(Player winner) {
        for (Player p : players) {

            if (p == winner) {
                winsMap.put(p.name, winsMap.get(p.name) + 1);
            }

            int prevHigh = highScoresMap.getOrDefault(p.name, 0);
            if (p.totalScore > prevHigh) {
                highScoresMap.put(p.name, p.totalScore);
            }
        }
    }

    private void animateMovement(Player player, java.util.List<Integer> path, Runnable onComplete) {
        if (path.size() <= 1 || (path.size() == 2 && path.get(0).equals(path.get(1)))) {
            isAnimating = false;
            onComplete.run();
            return;
        }

        isAnimating = true;
        if (lastMoveForward) {
            SoundManager.playSound(SoundManager.MOVE_FORWARD);
        } else {
            SoundManager.playSound(SoundManager.MOVE_BACKWARD);
        }

        int[] currentStep = {0};
        javax.swing.Timer moveTimer = new javax.swing.Timer(500, null);
        moveTimer.addActionListener(e -> {
            currentStep[0]++;
            if (currentStep[0] < path.size()) {
                player.position = path.get(currentStep[0]);
                redraw();
            } else {
                moveTimer.stop();
                isAnimating = false;
                onComplete.run();
            }
        });
        moveTimer.start();
    }

    private void checkStarCollection(Player current) {
        extraTurnGranted = false;
        int label = current.position + 1;
        if (label > 1 && label % 5 == 0) {
            extraTurnGranted = true;
            JOptionPane.showMessageDialog(null,
                    current.name + " got into STAR planet " + (current.position + 1),
                    "🎲 Extra dice roll granted!",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void checkScoreCollection(Player current) {
        Integer nodeScore = graph.getNodeScore(current.position);
        if (nodeScore != null && !graph.isScoreClaimed(current.position)) {
            graph.claimNodeScore(current.position);
            current.claimedScores.add(nodeScore);
            current.totalScore += nodeScore;

            SoundManager.playSound(SoundManager.COLLECT);
        }
    }

    void resetBoardKeepWins() {
        graph = new Graph(NODE_COUNT);
        graph.layoutZigZagWavy(900, 700, 50, 8);
        graph.resolveOverlaps();

        graph.connectRandomEdges(NODE_COUNT * 2);
        graph.clearLadders();

        for (int i=0;i<LADDER_COUNT;i++) {
            int a = rng.nextInt(NODE_COUNT);
            int b = rng.nextInt(NODE_COUNT);
            if (a==b || Math.abs(a-b) <= 1) { i--; continue; }
            graph.addLadder(a,b);
        }

        graph.clearNodeScores();
        Set<Integer> chosen = new HashSet<>();
        while (chosen.size() < SCORE_NODE_COUNT) {
            int idx = rng.nextInt(NODE_COUNT);
            if (idx == START_NODE || idx == GOAL_NODE) continue;
            chosen.add(idx);
        }
        for (int idx : chosen) {
            int s = 10 + rng.nextInt(91);
            graph.setNodeScore(idx, s);
        }

        for (Player pl : players) {
            pl.position = START_NODE;
            pl.claimedScores.clear();
            pl.totalScore = 0;
        }

        turnQueue.clear();
        for (Player p : players) turnQueue.add(p);

        redraw();
    }

    static boolean isPrime(int x) {
        if (x <= 1) return false;
        if (x <= 3) return true;
        if (x % 2 == 0) return false;
        for (int i=3;i*i<=x;i+=2) if (x % i == 0) return false;
        return true;
    }
}
