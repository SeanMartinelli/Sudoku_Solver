/**
 * Created by sean_martinelli on 10/20/17.
 */

/*
NOTES:
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
        initializeBoardToZeros();
        initializeCandidatesToDefault();
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

    public void initializeBoardToZeros() {
        for (int n[]: board) {
            for (int a: n)
                a = 0;
        }
    }

    public void initializeCandidatesToDefault() {
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
            updateCandidateList(coords, num);
            return true;
        }
        else if (onCandidateList(coords, num)) {
            board[coords.getCol()][coords.getRow()] = num;
            updateCandidateList(coords, num);
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

    public boolean removePiece(Coordinates coords) {

        return true;
    }

    public boolean gameComplete() {
        return piecesOnBoard == 81 && correctPiecesArrangement();
    }

    public void updateCandidateList(Coordinates coords, int num) {
        clearCandidatesAt(coords);
        removeCandidateInCol(coords, num);
        removeCandidateInRow(coords, num);
        removeCandidateInBox(coords, num);

        // debug
        listCandidates();
    }

    public boolean onCandidateList(Coordinates coords, int number) {
        return getCandidatesAt(coords).contains(number);
    }

    public boolean correctPiecesArrangement() {

        return true;
    }

    public void listCandidates() {
        for (int i = 0; i < 9; ++i) {
            for (int k = 0; k < 9; ++k) {
                System.out.print("[" + i + "," + k + "]    ");
                for (int m = 0; m < 9; ++m) {
                    System.out.print(candidates[i][k][m] + " ");
                }
                System.out.println();
            }
        }
    }

    public void clearCandidatesAt(Coordinates c) {
        int x = c.getCol();
        int y = c.getRow();
        for (int i = 0; i < 9; ++i)
            candidates[x][y][i] = 0;
    }

    public void removeCandidateInRow(Coordinates c, int n) {
        int y = c.getRow();
        for (int i = 0; i < 9; ++i) {
            candidates[i][y][n - 1] = 0;
        }
    }

    public void removeCandidateInCol(Coordinates c, int n) {
        int x = c.getCol();
        for (int i = 0; i < 9; ++i) {
            candidates[x][i][n - 1] = 0;
        }
    }

    public void removeCandidateInBox(Coordinates c, int n) {
        Coordinates tempCoords = findTopLeftOfTheBox(c);

        int col = tempCoords.getCol();
        int row = tempCoords.getRow();

        for (int i = col; i < col + 3; ++i)
            for (int k = row; k < row + 3; ++k)
                candidates[i][k][n-1] = 0;
    }

    public Coordinates findTopLeftOfTheBox(Coordinates coordinates) {
        int x, y, boxCol, boxRow;

        boxCol = coordinates.getCol() / 3;
        if (boxCol == 0)
            x = 0;
        else if (boxCol == 1)
            x = 3;
        else
            x = 6;

        boxRow = coordinates.getRow() / 3;
        if (boxRow == 0)
            y = 0;
        else if (boxRow == 1)
            y = 3;
        else
            y = 6;

        return new Coordinates(x, y);
    }

    public  int[][] singleAlgorithm() {
        for (int i = 0; i < 9; ++i) {
            for (int k = 0; k < 9; ++k) {
                if (board[i][k] == 0) {
                    List<Integer> tempList = getCandidatesAt(new Coordinates(i, k));
                    if (tempList.size() == 1) {
                        updatePiece(new Coordinates(i, k), tempList.get(0), true);
                        return board;
                    }
                }
            }
        }
        return null;        // to display message about failure
    }

    public int[][] hiddenSingleAlgorithm() {
        Coordinates tempCoords = new Coordinates(-1, -1);
        for (int i = 0; i < 9; ++i) {
            for (int k = 0; k < 9; ++k) {
                if (board[i][k] == 0) {
                    tempCoords.setColAndRow(i, k);
                    List<Integer> tempCand = getCandidatesAt(tempCoords);
                    if (tempCand.size() > 1) {
                        for (int cand : tempCand) {
                            if (isHiddenSingle(cand, tempCoords)) {
                                updatePiece(new Coordinates(i, k), cand, true);
                                return board;
                            }
                        }
                    }
                }
            }
        }
        return null;        // to display message about failure
    }

    public boolean isHiddenSingle(int cand, Coordinates coords) {
        if (hiddenInCol(cand, coords))
            return true;
        else if (hiddenInRow(cand, coords))
            return true;
        else if (hiddenInBox(cand, coords))
            return true;
        else
            return false;
    }

    public boolean hiddenInCol(int cand, Coordinates c) {
        int col = c.getCol();
        for (int i = 0; i < 9; ++i) {
            if (board[col][i] == 0) {
                if (getCandidatesAt(new Coordinates(col, i)).contains(cand))
                    return false;
            }
        }
        return true;
    }

    public boolean hiddenInRow(int cand, Coordinates c) {
        int row = c.getRow();
        for (int i = 0; i < 9; ++i) {
            if (board[i][row] == 0) {
                if (getCandidatesAt(new Coordinates(i, row)).contains(cand))
                    return false;
            }
        }
        return true;
    }

    public boolean hiddenInBox(int cand, Coordinates c) {
        Coordinates tempCoords = findTopLeftOfTheBox(c);

        int col = tempCoords.getCol();
        int row = tempCoords.getRow();

        for (int i = col; i < col + 3; ++i) {
            for (int k = row; k < row + 3; ++i) {
                if (board[i][k] == 0) {
                    if (getCandidatesAt(new Coordinates(i, k)).contains(cand))
                        return false;
                }
            }
        }
        return true;
    }

    public boolean lockedCandidateAlgorithm() {

        return true;
    }



}   // end of SudokuSolverModel class
