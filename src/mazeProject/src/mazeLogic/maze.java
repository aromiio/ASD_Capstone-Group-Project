package mazeLogic;

import java.util.*;

public class maze {
    public cell[][] grid;
    private final int SIZE = 21;
    public boolean isGenerating = false;

    public maze(){
        grid = new cell[SIZE][SIZE];
        for(int r=0; r<SIZE; r++){
            for(int c=0; c<SIZE; c++){
                grid[r][c] = new cell(c,r);
            }
        }
    }

    //PRIMS ALGORITHM
    public void generatePrims(Runnable onUpdate){
        isGenerating = true;
        //reset semua jd grid tembok dlu
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                grid[r][c].type = 0;
                grid[r][c].resetStatus();
            }
        }

        List<cell> walls = new ArrayList<>();
        cell start = grid[0][0];
        start.type = 1;
        addNeighbors(start, walls);
        Random random = new Random();

        javax.swing.Timer genTimer = new javax.swing.Timer(10, null);
        genTimer.addActionListener(e->{
            if(!walls.isEmpty()){
                cell wall = walls.remove(random.nextInt(walls.size()));
                cell neighbor = canBePath(wall);

                if(neighbor != null && neighbor.type ==0){
                    wall.type = 1;
                    neighbor.type = 1;
                    addNeighbors(neighbor, walls);
                }
                onUpdate.run();
            } else{
                genTimer.stop();

                grid[18][19].type = 1;
                grid[19][19].type = 1;

                isGenerating = false;
                setRandomTerrain();
                onUpdate.run();
            }
        });
        genTimer.start();
    }

    private void addNeighbors(cell c, List<cell> list){
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] d : dirs) {
            int midX = c.x + d[0];
            int midY = c.y + d[1];

            int targetX = c.x + (d[0]);
            int targetY = c.y + (d[1]);

            if (targetX >= 0 && targetX < SIZE && targetY >= 0 && targetY < SIZE) {
                if (grid[targetY][targetX].type == 0) {
                    if(!list.contains(grid[midY][midX])) {
                        list.add(grid[midY][midX]);
                    }
                }
            }
        }

    }

    private cell canBePath(cell c){
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] d : dirs) {
            int x1 = c.x + d[0];
            int y1 = c.y + d[1];
            int x2 = c.x - d[0];
            int y2 = c.y - d[1];

            if (isValid(x1, y1) && isValid(x2, y2)) {
                if (grid[y1][x1].type == 1 && grid[y2][x2].type == 0) return grid[y2][x2];
                if (grid[y2][x2].type == 1 && grid[y1][x1].type == 0) return grid[y1][x1];
            }
        }
        return null;
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
    }

    public void setRandomTerrain(){
        Random random = new Random();
        for(int r=0; r<SIZE; r++){
            for(int c=0; c<SIZE; c++){
                if(grid[r][c].type !=0){
                    double p = random.nextDouble();
                    if(p<0.1) grid[r][c].type = 10;
                    else if(p<0.30) grid[r][c].type = 5;
                    else grid[r][c].type = 1;
                }
            }
        }
    }

}
