//
// Michal Bochnak, Netid: mbochn2
// Sean Martinelli, Netid: smarti58
//
// CS 342 Project #3 - Sudoku Solver
// 10/26/2017
// UIC, Pat Troy
//
// SudokuSolver.java
//

//
// Creates new sudoku solver model and sudoku solver view
// and pass it to the controller along with the command line arguments
//

public class SudokuSolver
{
    public static void main(String args[])
    {
        SudokuSolverModel sudokuSolverModel = new SudokuSolverModel();
        SudokuSolverView sudokuSolverView = new SudokuSolverView();

        new SudokuSolverController(sudokuSolverModel, sudokuSolverView, args);
    }
}