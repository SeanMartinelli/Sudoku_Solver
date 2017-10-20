/**
 * Created by sean_martinelli on 10/20/17.
 */

/*
NOTES:
    - If '@Test" specifier is not found:
        1. place cursor on it   2. alt + enter  3. add JUnit4
    - candidates as 3D array for now, 2D List<...> is kind of tricky to implement here
 */


import java.util.ArrayList;
import java.util.List;


public class SudokuSolverModel {

    private int board[][];
    private int candidates[][][];
    private int piecesOnBoard;

    SudokuSolverModel() {
        board = new int[9][9];
        candidates = new int[9][9][9];
        piecesOnBoard = 0;
        inititializeBoardToZeros();
        inititializeCandidatesToDefault();
    }

    public int[][] getBoard() {
        return board;
    }

    public int[][][] getCandidates() {
        return candidates;
    }

    public List<Integer> getCandidatesAt(Coordinates coords) {
        List<Integer> candidatesList = new ArrayList<Integer>();
        int c;

        for (int i = 0; i < 9; ++i) {
            c = candidates[coords.getCol()][coords.getRow()][i];
            if (c != 0)
                candidatesList.add(c);
        }
        return candidatesList;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public void setCandidates(int[][][] candidates) {
        this.candidates = candidates;
    }

    public void inititializeBoardToZeros() {
        for (int n[]: board) {
            for (int a: n)
                a = 0;
        }
    }

    public void inititializeCandidatesToDefault() {
        for (int[][] col: candidates) {
            for (int[] row: col) {
                for (int i = 0; i < 9; ++i)
                    row[i] = i + 1;
            }
        }
    }

    public boolean updatePiece(Coordinates coords, int num, boolean checkOnFill) {
        if (!inRange(num) || spotTaken(coords))
            return false;
        else if (!checkOnFill) {
            board[coords.getCol()][coords.getRow()] = num;
            updateCandidateList(coords);
            return true;
        }
        else if (onCandidateList(coords)) {
            board[coords.getCol()][coords.getRow()] = num;
            updateCandidateList(coords);
            return true;
        }
        return false;
    }

    public boolean inRange(int n) {
        return (n >= 1) && (n <= 9 );
    }

    public boolean spotTaken(Coordinates coords) {
        return board[coords.getCol()][coords.getRow()] != 0;
    }

    public boolean removePiece() {

        return true;
    }

    public boolean gameComplete() {
        return piecesOnBoard == 81 && correctPiecesArrangement();
    }

    public void updateCandidateList(Coordinates coords) {

    }

    public boolean onCandidateList(Coordinates coords) {

        return true;
    }

    public boolean correctPiecesArrangement() {

        return true;
    }

}
