//
// Michal Bochnak, Netid: mbochn2
// Sean Martinelli, Netid: smarti58
//
// CS 342 Project #3 - Sudoku Solver
// 10/26/2017
// UIC, Pat Troy
//
// SudokuSolverModel.java
//

//
//
//

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

    public void resetBoard() {

        piecesOnBoard = 0;
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

    public void updatePiece (Coordinates coords, int num, boolean checkOnFill)
                    throws Exception {

        if (!checkOnFill) {
            board[coords.getCol()][coords.getRow()] = num;
            updateCandidateList(coords, num);
            piecesOnBoard++;
            return;
        }
        else if (onCandidateList(coords, num)) {
            board[coords.getCol()][coords.getRow()] = num;
            updateCandidateList(coords, num);
            piecesOnBoard++;
            return;
        }
        throw new Exception();
    }

    public boolean inBoardRange(int n) {
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

    public void generateCandidateListWholeBoard() {

        for (int i = 0; i < 9; ++i) {
            for (int k = 0; k < 9; ++k) {
                Set<Integer> usedCandidates = getUsedCandidates(i, k);
                populateCandidates(i, k, usedCandidates);
            }
        }

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

    public  Coordinates singleAlgorithm() throws Exception {

        for (int i = 0; i < 9; ++i) {
            for (int k = 0; k < 9; ++k) {

                if (board[i][k] == 0) {
                    Coordinates tempCoords = new Coordinates(i, k);
                    List<Integer> tempList = getCandidatesAt(tempCoords);
                    if (tempList.size() == 1) {
                        try {
                            updatePiece(tempCoords, tempList.get(0), true);
                            return tempCoords;
                        }
                        catch (Exception e) {
                            throw e; // to display message about failure
                        }
                    }
                }

            }
        }
        throw new Exception();
    }

    public Coordinates hiddenSingleAlgorithm() throws Exception {

        Coordinates tempCoords = new Coordinates(-1, -1);

        for (int i = 0; i < 9; ++i) {
            for (int k = 0; k < 9; ++k) {

                if (board[i][k] == 0) {
                    tempCoords.setColAndRow(i, k);
                    List<Integer> tempCand = getCandidatesAt(tempCoords);
                    if (tempCand.size() > 1) {
                        for (int cand : tempCand) {
                            if (isHiddenSingle(cand, tempCoords)) {
                                try {
                                    updatePiece(tempCoords, cand, true);
                                    return tempCoords;
                                } catch (Exception e) {
                                    throw e;
                                }
                            }
                        }
                    }
                }

            }
        }
        throw new Exception();        // to display message about failure
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

    public boolean lockedCandidateAlgorithm() {

        for (int i = 0; i < 9; ++i) {

           if (restrictedToColBox(i))
               return true;
           else if (restrictedToRowBox(i))
               return true;
           else if (restrictedToBoxCol(i))
               return true;
           else if (restrictedToBoxRow(i))
               return true;        }

        return false;
    }

    // check column, remove in box
    private boolean restrictedToColBox(int col) {
        for (int row = 0; row < 9; ++row) {
            List<Integer> myCand = getCandidatesAt(new Coordinates(col, row));
            for (int cand : myCand) {
                List<Integer> presentAtIndeces = findOccuranceIndecesInCol(col, cand);
                if (presentAtIndeces.size() <= 3) {
                    int boxID = inBoxRange(presentAtIndeces);
                    Coordinates boxStart = findTopLeftOfTheBox(new Coordinates(col, (boxID * 3)));
                    if (boxID != -1 && (numCandOccurenceInBox(boxStart, cand) > presentAtIndeces.size())) {
                        for (int i = boxStart.getCol(); i < boxStart.getCol() + 3; ++i){
                            for (int k = boxStart.getRow(); k < boxStart.getRow() + 3; ++k){
                                if (i != col ) {
                                    candidates[i][k][cand - 1] = 0;
                                }
                            }
                        }
                        return  true;
                    }
                }
            }
        }
        return false;
    }

    private int numCandOccurenceInBox(Coordinates boxStart, int cand) {

        int counter = 0;

        for (int col = boxStart.getCol(); col < boxStart.getCol() + 3; ++col) {
            for (int row = boxStart.getRow(); row < boxStart.getRow() + 3; ++row) {
                if (getCandidatesAt(new Coordinates(col, row)).contains(cand))
                    ++counter;
            }
        }

        return counter;
    }

    private int inBoxRange(List <Integer> presentAtIndeces) {
        if (inGivenRange(0, 2, presentAtIndeces)) {
            return 0;
        }
        else if (inGivenRange(3, 5, presentAtIndeces)) {
            return 1;
        }
        else if (inGivenRange(6, 8, presentAtIndeces)) {
            return 2;
        }
        else
            return -1;
    }

    private boolean inGivenRange(int lowerBound, int upperBound, List<Integer> list) {

        for (int a : list) {
            if (a < lowerBound || a > upperBound) {
                return false;
            }
        }

        return true;
    }

    private List<Integer> findOccuranceIndecesInCol(int col, int cand) {
        List<Integer> temp = new ArrayList<>();
        for (int row = 0; row < 9; ++row) {
            if (getCandidatesAt(new Coordinates(col, row)).contains(cand)) {
                temp.add(row);
            }
        }
        return temp;
    }

    // check row, remove from box
    private boolean restrictedToRowBox(int row) {
        for (int col = 0; col < 9; ++col) {
            List<Integer> myCand = getCandidatesAt(new Coordinates(col, row));
            for (int cand : myCand) {
                List<Integer> presentAtIndeces = findOccuranceIndecesInRow(row, cand);
                if (presentAtIndeces.size() <= 3) {
                    int boxID = inBoxRange(presentAtIndeces);
                    Coordinates boxStart = findTopLeftOfTheBox(new Coordinates((boxID * 3), row));
                    if (boxID != -1 && (numCandOccurenceInBox(boxStart, cand) > presentAtIndeces.size())) {
                        for (int i = boxStart.getCol(); i < boxStart.getCol() + 3; ++i){
                            for (int k = boxStart.getRow(); k < boxStart.getRow() + 3; ++k){
                                if (k != row ) {
                                    candidates[i][k][cand - 1] = 0;
                                }
                            }
                        }
                        return  true;
                    }
                }
            }
        }
        return false;
    }

    private List<Integer> findOccuranceIndecesInRow(int row, int cand) {
        List<Integer> temp = new ArrayList<>();
        for (int col = 0; col < 9; ++col) {
            if (getCandidatesAt(new Coordinates(col, row)).contains(cand)) {
                temp.add(col);
            }
        }
        return temp;
    }

    // check box, remove from column
    private boolean restrictedToBoxCol(int boxIndex) {
        Coordinates boxStart = findTopLeftByIndex(boxIndex);
        for (int col = boxStart.getCol(); col < boxStart.getCol() + 3; ++col) {
            for (int row = boxStart.getRow(); row < boxStart.getRow() + 3; ++row) {
                List<Integer> myCand = getCandidatesAt(new Coordinates(col, row));
                for (int cand : myCand) {
                    List<Coordinates> presentAtCoordinates = findOccuranceCoordinatesInBox(boxStart, cand);
                    if (presentAtCoordinates.size() <= 3) {
                        int colID = inOneColumn(presentAtCoordinates);
                        if (colID != -1 && (numCandOccurenceInCol(colID, cand) > presentAtCoordinates.size())) {
                            for (int myRow = 0; myRow < 9; ++myRow) {
                                if (myRow != boxStart.getRow() && myRow != boxStart.getRow()+1
                                            && myRow != boxStart.getRow()+2) {

                                    candidates[colID][myRow][cand - 1] = 0;
                                }
                            }
                            return  true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private int numCandOccurenceInCol(int colID, int cand) {

        int count = 0;

        for (int row = 0; row < 9; ++row) {
            if (getCandidatesAt(new Coordinates(colID, row)).contains(cand))
                ++count;
        }

        return count;
    }

    private int inOneColumn(List<Coordinates> list) {
        int col = list.get(0).getCol();

        for (Coordinates c : list) {
            if (c.getCol() != col)
                return -1;
        }

        return col;
    }

    private List<Coordinates> findOccuranceCoordinatesInBox(Coordinates boxStart, int cand) {

        List<Coordinates> temp = new ArrayList<Coordinates>();

        for (int col = boxStart.getCol(); col < boxStart.getCol() + 3;  ++col) {
            for (int row = boxStart.getRow(); row < boxStart.getRow() + 3; ++row) {
                if (getCandidatesAt(new Coordinates(col, row)).contains(cand))
                    temp.add(new Coordinates(col, row));
            }
        }

        return temp;
    }

    private Coordinates findTopLeftByIndex(int boxIndex) {

        Coordinates temp = new Coordinates(-1, -1);

        switch (boxIndex) {
            case 0:     temp.setColAndRow(0,0);
                break;
            case 1:     temp.setColAndRow(3,0);
                break;
            case 2:     temp.setColAndRow(6,0);
                break;
            case 3:     temp.setColAndRow(0,3);
                break;
            case 4:     temp.setColAndRow(3,3);
                break;
            case 5:     temp.setColAndRow(6,3);
                break;
            case 6:     temp.setColAndRow(0,6);
                break;
            case 7:     temp.setColAndRow(3,6);
                break;
            case 8:     temp.setColAndRow(6,6);
                break;
        }

        return temp;
    }

    // check box, remove from row
    private boolean restrictedToBoxRow(int boxIndex) {
        Coordinates boxStart = findTopLeftByIndex(boxIndex);
        for (int col = boxStart.getCol(); col < boxStart.getCol() + 3; ++col) {
            for (int row = boxStart.getRow(); row < boxStart.getRow() + 3; ++row) {
                List<Integer> myCand = getCandidatesAt(new Coordinates(col, row));
                for (int cand : myCand) {
                    List<Coordinates> presentAtCoordinates = findOccuranceCoordinatesInBox(boxStart, cand);
                    if (presentAtCoordinates.size() <= 3) {
                        int rowID = inOneRow(presentAtCoordinates);
                        if (rowID != -1 && (numCandOccurenceInRow(rowID, cand) > presentAtCoordinates.size())) {
                            for (int myCol = 0; myCol < 9; ++myCol) {
                                if (myCol != boxStart.getCol() && myCol != boxStart.getCol()+1
                                        && myCol != boxStart.getCol()+2) {

                                    candidates[myCol][rowID][cand - 1] = 0;
                                }
                            }
                            return  true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private int numCandOccurenceInRow(int rowID, int cand) {

        int count = 0;

        for (int col = 0; col < 9; ++col) {
            if (getCandidatesAt(new Coordinates(col, rowID)).contains(cand))
                ++count;
        }

        return count;
    }

    private int inOneRow(List<Coordinates> list) {
        int row = list.get(0).getRow();

        for (Coordinates c : list) {
            if (c.getRow() != row)
                return -1;
        }

        return row;
    }

    private boolean lockedCandCheckRows() {

        return false;
    }

    private boolean lockedCandCheckBoxes() {
        return false;
    }

    public boolean nakedPairsAlgorithm() {

        return nakedPairsCheckColumns() || nakedPairsCheckRows() || nakedPairsCheckBoxes();
    }

    //FIXME: remove print statement
    private boolean nakedPairsCheckColumns() {

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
    private boolean nakedPairsCheckRows() {

        for (int row = 0; row < 9; ++row) {
            ArrayList<ArrayList<Integer>> nakedPairs = getRowCandWithLengthOf(2, row);
            if (nakedPairs.size() > 1) {
                ArrayList<Integer> sameCand = findSameCand(nakedPairs);

                if (sameCand != null && availableToRemoveRow(sameCand, row)) {
                    for (int i = 0; i < 9; ++i) {
                        // && getCandidatesAt(getCandidatesAt(new Coordinates(i, row))) > 1 ?
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
            for (int k = i; k < nakedPairs.size(); ++k) {

                if (( i != k) && (nakedPairs.get(i).containsAll(nakedPairs.get(k)))) {
                    return nakedPairs.get(i);
                }

            }
        }

        return null;
    }

    //FIXME: remove print statement
    private boolean nakedPairsCheckBoxes() {

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
                                     for (int a : sameCand) {
                                         candidates[i][k][a - 1] = 0;
                                     }
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

    public int [][] resolveAllPossibleCells() {

        while (true) {

            boolean singleAlgorithmStatus = true;
            boolean hiddenSingleAlgorithmStatus = true;
            boolean nakedPairsAlgorithmStatus = true;
            boolean lockedCandidateAlgorithmStatus = true;

            try {
                singleAlgorithm();
            } catch (Exception exc) {
                singleAlgorithmStatus = false;
            }

            if (singleAlgorithmStatus == false) {

                try {
                    hiddenSingleAlgorithm();
                } catch (Exception exc) {
                    hiddenSingleAlgorithmStatus = false;
                }
            }

            if (hiddenSingleAlgorithmStatus == false) {
                lockedCandidateAlgorithmStatus = lockedCandidateAlgorithm();
            }

            if (lockedCandidateAlgorithmStatus == false) {
                nakedPairsAlgorithmStatus = nakedPairsAlgorithm();
            }

            if (nakedPairsAlgorithmStatus == false) {
                return board;
            }
        }
    }




}
