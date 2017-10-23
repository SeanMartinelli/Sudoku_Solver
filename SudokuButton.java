/**
 * Created by Michal on 10/21/2017.
 */

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