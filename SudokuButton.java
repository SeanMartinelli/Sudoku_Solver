//
// Michal Bochnak, Netid: mbochn2
// Sean Martinelli, Netid: smarti58
//
// CS 342 Project #3 - Sudoku Solver
// 10/26/2017
// UIC, Pat Troy
//
// SudokuButton.java
//

//
//
//

import javax.swing.*;

public class SudokuButton extends JButton
{
    private Coordinates coords;
    private boolean isEditable;

    public SudokuButton(int row, int column)
    {
        coords = new Coordinates(column, row);
        this.isEditable = true;
    }

    public Coordinates GetCoordinates()
    {
        return coords;
    }

    public void MakeEditable(boolean isEditable)
    {
        this.isEditable = isEditable;
    }

    public boolean IsEditable()
    {
        return this.isEditable;
    }
}