/**
 * Created by sean_martinelli on 10/20/17.
 */

/*
NOTES:
    - updatePiece(): generateCandidateListWholeBoard() instead of updateCandidateList()
        will allow to get rid of excess code - less efficient though\
    - Erasing will mess up the candidates if Locked / Naked algorithms were used
    -loading solved -> error
 */


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

    public int getPieceAt(Coordinates c) {
        return board[c.getCol()][c.getRow()];
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public void setCandidates(int[][][] candidates) {
        this.candidates = candidates;
    }

    public void resetBoard() {

        initializeBoardToZeros();
        initializeCandidatesToDefault();
    }

    public void initializeBoardToZeros() {

        for (int n[]: board) {
            for (int i = 0; i < 9; ++i)
                n[i] = 0;
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
            piecesOnBoard++;
            return true;
        }
        else if (onCandidateList(coords, num)) {
            board[coords.getCol()][coords.getRow()] = num;
            updateCandidateList(coords, num);
            piecesOnBoard++;
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

        if (coordinatesInRange(coords) && spotTaken(coords)) {
            board[coords.getCol()][coords.getRow()] = 0;
            piecesOnBoard--;
            generateCandidateListWholeBoard();
            return true;
        }

        return false;
    }

    private boolean coordinatesInRange(Coordinates c) {
        return c.getCol() >=0 && c.getCol() <= 8 && c.getRow() >=0 && c.getRow() <= 8;
    }

    //FIXME: Remove listCandidate() call
    public void generateCandidateListWholeBoard() {

        for (int i = 0; i < 9; ++i) {
            for (int k = 0; k < 9; ++k) {
                Set<Integer> usedCandidates = getUsedCandidates(i, k);
                populateCandidates(i, k, usedCandidates);
            }
        }

        listCandidates();
    }

    private Set<Integer> getUsedCandidates(int col, int row) {
        Set<Integer> allUsedCand = new HashSet<Integer>();

        Set<Integer> colUsedCand = findUsedCandCol(col);
        Set<Integer> rowUsedCand = findUsedCandRow(row);
        Set<Integer> boxUsedCand = findUsedCandBox(new Coordinates(col, row));

        allUsedCand.addAll(colUsedCand);
        allUsedCand.addAll(rowUsedCand);
        allUsedCand.addAll(boxUsedCand);

        return allUsedCand;
    }

    public Set<Integer> findUsedCandCol(int column) {

        Set<Integer> temp = new HashSet<Integer>();

        for (int i = 0; i < 9; ++i) {
            if (board[column][i] != 0)
                temp.add(board[column][i]);
        }

        return temp;
    }

    public Set<Integer> findUsedCandRow(int row) {

        Set<Integer> temp = new HashSet<Integer>();

        for (int i = 0; i < 9; ++i) {
            if (board[i][row] != 0)
                temp.add(board[i][row]);
        }

        return temp;
    }

    public Set<Integer> findUsedCandBox(Coordinates c) {

        Set<Integer> temp = new HashSet<Integer>();
        Coordinates boxTop = findTopLeftOfTheBox(c);

        int colBox = boxTop.getCol();
        int rowBox = boxTop.getRow();

        for (int i = colBox; i < colBox + 3; ++i)
            for (int k = rowBox; k < rowBox + 3; ++k)
                if (board[i][k] != 0)
                    temp.add(board[i][k]);

        return temp;
    }

    public void populateCandidates(int col, int row, Set<Integer> usedCand) {

        for (int i = 1; i <= 9; ++i) {
            if (!usedCand.contains(i))
                candidates[col][row][i-1] = i;
            else
                candidates[col][row][i-1] = 0;
        }
    }

    public boolean gameComplete() {
        return piecesOnBoard == 81 && correctPiecesArrangement();
    }

    public void updateCandidateList(Coordinates coords, int num) {

        clearCandidatesAt(coords);
        removeCandidateInCol(coords, num);
        removeCandidateInRow(coords, num);
        removeCandidateInBox(coords, num);
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

    public boolean onCandidateList(Coordinates coords, int number) {
        return getCandidatesAt(coords).contains(number);
    }

    public boolean correctPiecesArrangement() {

        for (int i = 0; i < 9; ++i) {
            for (int k = 0; k < 9; ++k) {
                    if (!uniquePiece(new Coordinates(i, k)))
                        return false;
            }
        }

        return true;
    }

    private boolean uniquePiece(Coordinates c) {
        return uniqueInCol(c) && uniqueInRow(c) && uniqueInBox(c);
    }

    private boolean uniqueInCol(Coordinates coords) {

        int tempPiece = getPieceAt(coords);

        for (int i = 0; i < 9; ++i) {
            if (i != coords.getRow()) {
                if(board[coords.getCol()][i] == tempPiece)
                    return false;
            }
        }

        return true;
    }

    private boolean uniqueInRow(Coordinates coords) {

        int tempPiece = getPieceAt(coords);

        for (int i = 0; i < 9; ++i) {
            if (i != coords.getCol()) {
                if(board[i][coords.getRow()] == tempPiece)
                    return false;
            }
        }

        return true;
    }

    private boolean uniqueInBox(Coordinates coords) {

        int tempPiece = getPieceAt(coords);
        Coordinates boxTop = findTopLeftOfTheBox(coords);

        int colBox = boxTop.getCol();
        int rowBox = boxTop.getRow();

        for (int i = colBox; i < colBox + 3; ++i)
            for (int k = rowBox; k < rowBox + 3; ++k)
                if (i != coords.getCol() && k != coords.getRow())
                    if (board[i][k] == tempPiece)
                        return false;

        return true;
    }

    public void listCandidates() {
        System.out.println("Candidates: ");

        for (int i = 0; i < 9; ++i) {
            for (int k = 0; k < 9; ++k) {
                System.out.print("[" + i + "," + k + "]    ");
                for (int m = 0; m < 9; ++m) {
                    System.out.print(candidates[i][k][m] + " ");
                }
                System.out.println();
            }
        }

        System.out.println();
    }

    public void clearCandidatesAt(Coordinates c) {

        int x = c.getCol();
        int y = c.getRow();

        for (int i = 0; i < 9; ++i)
            candidates[x][y][i] = 0;
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

    public  Coordinates singleAlgorithm() {

        for (int i = 0; i < 9; ++i) {
            for (int k = 0; k < 9; ++k) {

                if (board[i][k] == 0) {
                    Coordinates tempCoords = new Coordinates(i, k);
                    List<Integer> tempList = getCandidatesAt(tempCoords);
                    if (tempList.size() == 1) {
                        updatePiece(tempCoords, tempList.get(0), true);
                        return tempCoords;
                    }
                }

            }
        }

        return null;        // to display message about failure
    }

    public Coordinates hiddenSingleAlgorithm() {

        Coordinates tempCoords = new Coordinates(-1, -1);

        for (int i = 0; i < 9; ++i) {
            for (int k = 0; k < 9; ++k) {

                if (board[i][k] == 0) {
                    tempCoords.setColAndRow(i, k);
                    List<Integer> tempCand = getCandidatesAt(tempCoords);
                    if (tempCand.size() > 1) {
                        for (int cand : tempCand) {
                            if (isHiddenSingle(cand, tempCoords)) {
                                System.out.println("hiddenSingleAlgorithm");
                                updatePiece(tempCoords, cand, true);
                                return tempCoords;
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
                if (!coordinatesSame(c, new Coordinates(col, i))) {
                    if (getCandidatesAt(new Coordinates(col, i)).contains(cand))
                        return false;
                }
            }
        }

        return true;
    }

    public boolean hiddenInRow(int cand, Coordinates c) {

        int row = c.getRow();

        for (int i = 0; i < 9; ++i) {
            if (board[i][row] == 0) {
                if (!coordinatesSame(c, new Coordinates(i, row))) {
                    if (getCandidatesAt(new Coordinates(i, row)).contains(cand))
                        return false;
                }
            }
        }

        return true;
    }

    public boolean hiddenInBox(int cand, Coordinates c) {

        Coordinates tempCoords = findTopLeftOfTheBox(c);

        int col = tempCoords.getCol();
        int row = tempCoords.getRow();

        for (int i = col; i < col + 3; ++i) {
            for (int k = row; k < row + 3; ++k) {

                if (board[i][k] == 0) {
                    if (!coordinatesSame(c, new Coordinates(i, k))) {
                        if (getCandidatesAt(new Coordinates(i, k)).contains(cand))
                            return false;
                    }
                }

            }
        }

        return true;
    }

    private boolean coordinatesSame(Coordinates a, Coordinates b) {
        return (a.getCol() == b.getCol()) && (a.getRow() == b.getRow());
    }

    //FIXME: Implement
    public boolean lockedCandidateAlgorithm() {
        // col
        // row
        // boxes

        return true;
    }

    public boolean nakedPairsAlgorithm() {
        return checkColumns() || checkRows() || checkBoxes();
    }

    //FIXME: remove print statement
    private boolean checkColumns() {

        System.out.println("Columns");

        for (int col = 0; col < 9; ++col) {
            ArrayList<ArrayList<Integer>> nakedPairs = getColCandWithLengthOf(2, col);
            if (nakedPairs.size() > 1) {
                ArrayList<Integer> sameCand = findSameCand(nakedPairs);

                if (sameCand != null && availableToRemoveCol(sameCand, col)) {
                    for (int i = 0; i < 9; ++i) {
                        if (!listAreSame(getCandidatesAt(new Coordinates(col, i)), sameCand)) {
                            for (int a : sameCand) {
                                candidates[col][i][a - 1] = 0;
                            }
                        }
                    }

                    return true;
                }

            }
        }

        return false;
    }

    private boolean availableToRemoveCol(ArrayList<Integer>sameCand, int col) {

        int count = 0;

        for (int row = 0; row < 9; ++row) {
            for (int a : sameCand)
                if (getCandidatesAt(new Coordinates(col, row)).contains(a))
                    count++;
        }

        return count > 4;
    }

    private ArrayList<ArrayList<Integer>> getColCandWithLengthOf(int length, int col) {

        ArrayList<ArrayList<Integer>>  temp = new ArrayList<ArrayList<Integer>> ();

        for (int row = 0; row < 9; ++row) {
            List<Integer> cand = getCandidatesAt(new Coordinates(col, row));
            if (cand.size() == 2)
                temp.add((ArrayList)cand);
        }

        return temp;
    }

    //FIXME: remove print statement
    private boolean checkRows() {

        System.out.println("Rows");

        for (int row = 0; row < 9; ++row) {
            ArrayList<ArrayList<Integer>> nakedPairs = getRowCandWithLengthOf(2, row);
            if (nakedPairs.size() > 1) {
                ArrayList<Integer> sameCand = findSameCand(nakedPairs);

                if (sameCand != null && availableToRemoveRow(sameCand, row)) {
                    for (int i = 0; i < 9; ++i) {
                        if (!listAreSame(getCandidatesAt(new Coordinates(i, row)), sameCand)) {
                            for (int a : sameCand) {
                                candidates[i][row][a - 1] = 0;
                            }
                        }
                    }

                    return true;
                }

            }
        }
        return false;
    }

    private boolean availableToRemoveRow(ArrayList<Integer> sameCand, int row) {

        int count = 0;

        for (int col = 0; col < 9; ++col) {
            for (int a : sameCand)
                if (getCandidatesAt(new Coordinates(col, row)).contains(a))
                    count++;
        }

        return count > 4;
    }

    private boolean listAreSame(List<Integer> L1, List<Integer> L2) {

        if (L1.size() != L2.size())
            return false;
        else {
            for (int i = 0; i < L1.size(); ++i) {
                if (L1.get(i) != L2.get(i))
                    return false;
            }
        }

        return true;
    }

    private ArrayList<Integer> findSameCand(ArrayList<ArrayList<Integer>> nakedPairs){

        for (int i = 0; i < nakedPairs.size(); ++i) {
            for (int k = i + 1; k < nakedPairs.size(); ++i) {

                if (nakedPairs.get(i) == nakedPairs.get(k)) {
                    return nakedPairs.get(i);
                }

            }
        }

        return null;
    }

    //FIXME: remove print statement
    private boolean checkBoxes() {

        System.out.println("Boxes");

        for (int col = 0; col <= 6; col+=3 ) {
            for (int row = 0; row <= 6; row += 3) {
                ArrayList<ArrayList<Integer>> nakedPairs =
                        getBoxCandWithLengthOf(2, new Coordinates(col, row));
                if (nakedPairs.size() > 1) {
                    ArrayList<Integer> sameCand = findSameCand(nakedPairs);

                    if ( sameCand != null &&  availableToRemoveBox(sameCand, col, row)) {
                        for (int i = col; i < col + 3; ++i ) {
                            for (int k = row; k < row + 3; ++k) {
                                if (!listAreSame(getCandidatesAt(new Coordinates(i, k)), sameCand)) {
                                     for (int a : sameCand)
                                        candidates[i][k][a - 1] = 0;
                                }
                            }
                        }

                        return true;
                    }

                }
            }
        }

        return false;
    }

    private boolean availableToRemoveBox(ArrayList<Integer> sameCand, int col,  int row) {

        int count = 0;

        for (int i = col; i < col + 3; ++i) {
            for (int k = row; k < row + 3; ++k) {
                for (int a : sameCand)
                    if (getCandidatesAt(new Coordinates(col, row)).contains(a))
                        count++;
            }
        }

        return count > 4;
    }

    private ArrayList<ArrayList<Integer>> getBoxCandWithLengthOf(int length, Coordinates c) {

        ArrayList<ArrayList<Integer>>  temp = new ArrayList<ArrayList<Integer>> ();

        for (int col = c.getCol(); col < c.getCol()+3; ++col) {
            for (int row = c.getRow(); row < c.getRow()+3; ++row) {
                List<Integer> cand = getCandidatesAt(new Coordinates(col, row));
                if (cand.size() == 2)
                    temp.add((ArrayList)cand);
            }
        }

        return temp;
    }

    private ArrayList<ArrayList<Integer>> getRowCandWithLengthOf(int length, int row) {

        ArrayList<ArrayList<Integer>>  temp = new ArrayList<ArrayList<Integer>> ();

        for (int col = 0; col < 9; ++col) {
            List<Integer> cand = getCandidatesAt(new Coordinates(col, row));
            if (cand.size() == 2)
                temp.add((ArrayList)cand);
        }

        return temp;
    }

    private int cellsWithNumCandRow(int numCand, int row) {

        int count = 0;

        for (int col = 0; col < 9; ++col) {
            if (getCandidatesAt(new Coordinates(col, row)).size() == numCand)
                count++;
        }

        return count;
    }

    //FIXME: Implement
    public int [][] resolveAllPossibleCells() {

        return board;
    }



}
