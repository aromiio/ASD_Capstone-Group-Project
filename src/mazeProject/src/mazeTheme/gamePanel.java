package mazeTheme;

import mazeLogic.cell;
import mazeLogic.maze;
import mazeLogic.pathSolver;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class gamePanel extends JPanel{
    private maze mazeData;
    private pathSolver solver;
    private BufferedImage bgGame, tileGrass, tileMud, tileWater, iconWckd, iconThom, tileWall;

    private int grassCount = 0, mudCount = 0, waterCount = 0, totalCost = 0;

    public gamePanel(maze mazeData){
        this.mazeData = mazeData;
        this.solver = new pathSolver(mazeData);
        setPreferredSize(new Dimension(1199, 741));
        loadResources();
    }

    private void loadResources(){
        try{
            bgGame = ImageIO.read(getClass().getResource("/mazeSource/bg_game.png"));
            tileGrass = ImageIO.read(getClass().getResource("/mazeSource/tile_grass.png"));
            tileMud = ImageIO.read(getClass().getResource("/mazeSource/tile_mud.png"));
            tileWater = ImageIO.read(getClass().getResource("/mazeSource/tile_water.png"));
            iconThom = ImageIO.read(getClass().getResource("/mazeSource/thom.png"));
            iconWckd = ImageIO.read(getClass().getResource("/mazeSource/wckd.png"));
            tileWall = ImageIO.read(getClass().getResource("/mazeSource/tile_wall.png"));
        } catch(Exception e){
            System.out.println(("Error Loading Images: " + e.getMessage()));
        }
    }

    public void runSolver(String type){
        this.solver = new pathSolver(mazeData);
        Runnable updateTask = () ->{
            calculateStats();
            repaint();
        };

        if(type.equals("BFS")) solver.solveBFS(20, updateTask);
        else if(type.equals("DFS")) solver.solveDFS(20, updateTask);
        else if(type.equals("Dijkstra")) solver.solveDijkstra(20, updateTask);
        else if(type.equals("AStar")) solver.solveAStar(20, updateTask);
    }

    public void calculateStats(){
        grassCount =0; mudCount =0; waterCount =0; totalCost =0;

        cell current = mazeData.grid[20][20];
        while(current != null){
            if(current.type == 1) grassCount++;
            else if(current.type == 5) mudCount++;
            else if(current.type == 10) waterCount++;
            current = current.parent;
        }
        totalCost = (grassCount*1) + (mudCount*5) + (waterCount*10);
    }

    //
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //gambar background
        if(bgGame != null){
            g2.drawImage(bgGame, 0, 0, 1199, 741, null);
        }

        //gambar maze
        int cellWidth = 29;
        int cellHeight = 29;

        for(int r=0; r<21; r++){
            for(int c=0; c<21; c++){

                int dx = 56 + (c*cellWidth);
                int dy = 41 + (r*cellHeight);
                cell currentCell = mazeData.grid[r][c];

                if(currentCell.type == 0){
                    g2.drawImage(tileWall, dx, dy, cellWidth, cellHeight, null);
                    g2.setColor(new Color(255, 255, 255, 80));
                    g2.drawRect(dx, dy, cellWidth, cellHeight);
                } else{
                    if(mazeData.isGenerating){
                        g2.setColor(new Color(0x1E2416));
                        g2.fillRect(dx, dy, cellWidth, cellHeight);
                    } else{
                        //cells random normal
                        if(currentCell.type == 1) g2.drawImage(tileGrass, dx, dy, cellWidth, cellHeight, null);
                        else if(currentCell.type == 5) g2.drawImage(tileMud, dx, dy, cellWidth, cellHeight, null);
                        else if(currentCell.type == 10) g2.drawImage(tileWater, dx, dy, cellWidth, cellHeight, null);
                    }
                }

                //gambar overlay path
                if(currentCell.isSearching){
                    g2.drawImage(iconWckd, dx, dy, cellWidth, cellHeight, null);
                }
                if(currentCell.isFinalPath) g2.drawImage(iconThom, dx, dy, cellWidth, cellWidth, null);
            }
        }

        //string path analysis
        g2.setFont(new Font("Poppins", Font.BOLD, 12));
        g2.setColor(Color.WHITE);

        g2.drawString(String.valueOf(grassCount), 1070, 353);
        g2.drawString(String.valueOf(mudCount), 1070, 391);
        g2.drawString(String.valueOf(waterCount), 1070, 430);
        g2.drawString(String.valueOf(totalCost), 1070, 477);

    }

}
