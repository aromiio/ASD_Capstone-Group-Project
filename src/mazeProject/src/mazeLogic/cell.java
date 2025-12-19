package mazeLogic;

public class cell {
    public int x,y;
    public int type;
    public boolean visited = false; //buat algoritma pencarian
    public boolean isSearching = false; //buat nnt pas nyari jalan
    public boolean isFinalPath = false; //buat thomas jalan

    public cell parent;
    public double gCost = Double.MAX_VALUE;
    public double hCost = 0;
    public double fCost() {return gCost + hCost;}

    public cell(int x, int y){
        this.x = x;
        this.y = y;
        this.type = 0; //default awal full grid
    }

    public void resetStatus(){
        visited = false;
        isSearching = false;
        isFinalPath = false;
        parent = null;
        gCost = Double.MAX_VALUE;
        hCost = 0;
    }
}
