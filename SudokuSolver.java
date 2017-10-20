/**
 * Created by sean_martinelli on 10/20/17.
 */
public class SudokuSolver
{
    public static void main(String args[])
    {
        SudokuSolverModel sudokuSolverModel = new SudokuSolverModel();
        SudokuSolverView sudokuSolverView = new SudokuSolverView();

        SudokuSolverController sudokuSolverController =
                new SudokuSolverController(sudokuSolverModel, sudokuSolverView);

    }
}
