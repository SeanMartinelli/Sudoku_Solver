//
// Michal Bochnak, Netid: mbochn2
// Sean Martinelli, Netid: smarti58
//
// CS 342 Project #3 - Sudoku Solver
// 10/26/2017
// UIC, Pat Troy
//
// Coordinates.java
//

//
// Includes coordinates for the positions on the game board
//

public class Coordinates {

    private int col, row;

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

    public void setColAndRow(int x, int y) {
        col = x;
        row = y;
    }

}
