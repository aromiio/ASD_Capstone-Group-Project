package app;

import java.awt.*;
import java.util.*;
import java.util.Queue;

public class Player {
    String name;
    int position = 0;
    Queue<Integer> claimedScores = new LinkedList<>();
    int totalScore = 0;

    public Color color;
    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }
}

