/**
 * Created by Michal on 10/20/2017.
 */

public class Coordinates {

    int row, col;
    
    Coordinates(int x, int y) {
        col = x;
        row = y;
    }


    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

}
