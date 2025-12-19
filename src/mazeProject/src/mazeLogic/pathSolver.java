package mazeLogic;

import  javax.swing.Timer;
import java.util.*;

public class pathSolver {
    private maze mazeData;
    private cell startCell, endCell;
    private Timer timer;
    private Runnable onComplete;

    public pathSolver(maze mazeData){
        this.mazeData = mazeData;
        this.startCell = mazeData.grid[0][0];
        this.endCell = mazeData.grid[20][20];
    }


    //BFS
    public void solveBFS(int delay, Runnable onComplete){
        Queue<cell> queue = new LinkedList<>();
        resetGrid();
        queue.add(startCell);
        startCell.visited = true;

        timer = new Timer(delay, e->{
            if(!queue.isEmpty()){
                cell current = queue.poll();
                current.isSearching = true;

                if(current == endCell){
                    timer.stop();
                    drawFinalPath(onComplete); //panggil thomas
                    onComplete.run();
                    return;
                }

                for(cell neighbor : getNeighbors(current)){
                    if(!neighbor.visited && neighbor.type !=0){
                        neighbor.visited = true;
                        neighbor.parent = current;
                        queue.add(neighbor);
                    }
                }
                onComplete.run();

            } else {timer.stop();}
        });
        timer.start();
    }

    //DFS
    public void solveDFS(int delay, Runnable onComplete){
        Stack<cell> stack = new Stack<>();
        resetGrid();
        stack.push(startCell);

        timer = new Timer(delay, e->{
            if(!stack.isEmpty()){
                cell current = stack.pop();
                current.isSearching = true;

                if(current == endCell){
                    timer.stop();
                    drawFinalPath(onComplete);
                    onComplete.run();
                    return;
                }

                if(!current.visited){
                    current.visited = true;
                    for(cell neighbor : getNeighbors(current)){
                        if(!neighbor.visited && neighbor.type !=0){
                            neighbor.parent = current;
                            stack.push(neighbor);
                        }
                    }
                }
                onComplete.run();

            } else{ timer.stop();}
        });
        timer.start();
    }

    //Dijkstra
    public void solveDijkstra(int delay, Runnable onComplete){
        PriorityQueue<cell> pq = new PriorityQueue<>(Comparator.comparingDouble(c->c.gCost));
        resetGrid();
        startCell.gCost = 0;
        pq.add(startCell);

        timer = new Timer(delay, e->{
            if(!pq.isEmpty()){
                cell current = pq.poll();
                current.isSearching = true;

                if(current == endCell){
                    timer.stop();
                    drawFinalPath(onComplete);
                    onComplete.run();
                    return;
                }

                for(cell neighbor : getNeighbors(current)){
                    if(neighbor.type != 0){
                        double newCost = current.gCost + neighbor.type;
                        if(newCost < neighbor.gCost){
                            neighbor.gCost = newCost;
                            neighbor.parent = current;
                            pq.add(neighbor);
                        }
                    }
                }
                onComplete.run();

            } else{ timer.stop();}
        });
        timer.start();
    }

    private List<cell> getNeighbors(cell c){
        List<cell> neighbors = new ArrayList<>();
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] d : dirs) {
            int nx = c.x + d[0];
            int ny = c.y + d[1];
            if (nx >= 0 && nx < 21 && ny >= 0 && ny < 21 && mazeData.grid[ny][nx].type != 0) {
                neighbors.add(mazeData.grid[ny][nx]);
            }
        }
        return neighbors;
    }

    //Others

    private void  resetGrid(){
        for(cell[] row : mazeData.grid){
            for(cell c : row)
                c.resetStatus();
        }
    }

    private void drawFinalPath(Runnable onComplete){
        List<cell> pathCells = new ArrayList<>();
        cell temp = endCell;
        while (temp != null){
            pathCells.add(temp);
            temp = temp.parent;
        }
        Collections.reverse(pathCells);
        Timer pathTimer = new Timer(18, null);
        final int[] index = {0};

        pathTimer.addActionListener(e->{
            if (index[0] < pathCells.size()) {
                cell c = pathCells.get(index[0]);
                c.isFinalPath = true;
                c.isSearching = false;
                onComplete.run();
                index[0]++;
            } else{
                pathTimer.stop();
            }
        });
        pathTimer.start();
    }



    //A*
    private double calculateHeuristic(cell a, cell b){
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    public void solveAStar(int delay, Runnable onComplete){
        PriorityQueue<cell> openSet = new PriorityQueue<>(Comparator.comparingDouble(c-> c.fCost()));
        resetGrid();

        startCell.gCost = 0;
        startCell.hCost = calculateHeuristic(startCell, endCell);
        openSet.add(startCell);

        timer = new Timer(delay, e->{
            if(!openSet.isEmpty()){
                cell current = openSet.poll();
                current.isSearching = true;

                if(current == endCell){
                    timer.stop();
                    drawFinalPath(onComplete);
                    onComplete.run();
                    return;
                }

                current.visited = true;

                for(cell neighbor : getNeighbors(current)){
                    if(neighbor.type != 0 && !neighbor.visited){
                        double tentativeGCost = current.gCost + neighbor.type;

                        if(tentativeGCost < neighbor.gCost){
                            neighbor.parent = current;
                            neighbor.gCost = tentativeGCost;
                            neighbor.hCost = calculateHeuristic(neighbor, endCell);
                            if(!openSet.contains(neighbor)){
                                openSet.add(neighbor);
                            }
                        }
                    }
                }
                onComplete.run();

            } else{ timer.stop();}
        });
        timer.start();
    }





}
