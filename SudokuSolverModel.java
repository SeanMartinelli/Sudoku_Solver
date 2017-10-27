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
// Model class is responsible for handling all the data manipulation that is
// used in the program. It contains data structures
//

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SudokuSolverModel {

    // represents the pieces on the board, 0 if no piece at the given spot
    private int board[][];
    // represents the candidate list for every cell, 1- 9 initially
    private int candidates[][][];
    // number of pieces on the board in current moment
    private int piecesOnBoard;

    SudokuSolverModel() {
        board = new int[9][9];
        candidates = new int[9][9][9];
        piecesOnBoard = 0;
        // set board pieces to zeros
        initializeBoardToZeros();
        // set all candidates to 1 - 9
        initializeCandidatesToDefault();
    }

    public int[][] getBoard() {
        return board;
    }

    // get candidates at given index
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

    // get piece at given index
    public int getPieceAt(Coordinates c) {
        return board[c.getCol()][c.getRow()];
    }

    // set board to default
    public void resetBoard() {

        piecesOnBoard = 0;
        initializeBoardToZeros();
        initializeCandidatesToDefault();    // 1 - 9
    }

    // update piece on board
    // 'checkOnFill' specifies if user turned on the option to check if he is
    // is allowed to place piece in given spot
    public void updatePiece(Coordinates coords, int num, boolean checkOnFill)
            throws Exception {

        if (!checkOnFill) {
            board[coords.getCol()][coords.getRow()] = num;
            updateCandidateList(coords, num);
            piecesOnBoard++;
            return;
        } else if (onCandidateList(coords, num)) {
            board[coords.getCol()][coords.getRow()] = num;
            updateCandidateList(coords, num);
            piecesOnBoard++;
            return;
        }
        throw new Exception();  // failed
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

    public boolean gameComplete() {
        return piecesOnBoard == 81 && correctPiecesArrangement();
    }

    // traverse the board and tries to find the cell with single candidate
    // if found, cell coordinates are returned, else exception is thrown
    public Coordinates singleAlgorithm() throws Exception {

        for (int i = 0; i < 9; ++i) {
            for (int k = 0; k < 9; ++k) {

                // check if cell is empty
                if (board[i][k] == 0) {
                    Coordinates tempCoords = new Coordinates(i, k);
                    List<Integer> tempList = getCandidatesAt(tempCoords);
                    // one candidate, resolve the cell
                    if (tempList.size() == 1) {
                        try {
                            updatePiece(tempCoords, tempList.get(0), true);
                            return tempCoords;
                        } catch (Exception e) {
                            throw e;        // board update failed
                        }
                    }
                }

            }
        }
        throw new Exception();  // not found, failed
    }

    // traverse the board and tries to find if the cell contains hidden candidate
    // if successful updates the board, throw exception otherwise
    public Coordinates hiddenSingleAlgorithm() throws Exception {

        Coordinates tempCoords = new Coordinates(-1, -1);

        for (int i = 0; i < 9; ++i) {
            for (int k = 0; k < 9; ++k) {

                // make sure cell is empty
                if (board[i][k] == 0) {
                    // generate coordinates with current cell location
                    tempCoords.setColAndRow(i, k);
                    List<Integer> tempCand = getCandidatesAt(tempCoords);
                    // piece present more than once
                    if (tempCand.size() > 1) {
                        for (int cand : tempCand) {
                            // make sure is hidden candidate
                            if (isHiddenSingle(cand, tempCoords)) {
                                try {
                                    updatePiece(tempCoords, cand, true);
                                    return tempCoords;
                                } catch (Exception e) {
                                    throw e;    // board update failed
                                }
                            }
                        }
                    }
                }

            }
        }
        throw new Exception();        // not found, failed
    }

    // checks if locked candidates algorithm can be applied to resolve the cells
    // processes  columns, rows, and boxes
    public boolean lockedCandidateAlgorithm() {

        // traverse through all columns, rows, and boxes
        for (int i = 0; i < 9; ++i) {

            if (restrictedToColBox(i))
                return true;
            else if (restrictedToRowBox(i))
                return true;
            else if (restrictedToBoxCol(i))
                return true;
            else if (restrictedToBoxRow(i))
                return true;
        }

        return false;
    }

    // checks if naked pair algorithm can be applied to resolve the cells
    // processes  columns, rows, and boxes
    public boolean nakedPairsAlgorithm() {

        return nakedPairsCheckColumns() || nakedPairsCheckRows() || nakedPairsCheckBoxes();
    }

    // algorithm to resolve all possible cells by using the 4 algorithms:
    // 1. single algorithm, 2. hidden single algorithm, 3. locked candidates algorithm,
    // 4. naked pairs algorithm
    public int[][] resolveAllPossibleCells() {

        while (true) {

            // flags to keep track which algorithm were already applied
            // so program knows if it should proceed to the next algorithm or not
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

            // naked pairs algorithm were not able to resolve the cell,
            // return board
            if (nakedPairsAlgorithmStatus == false) {
                return board;
            }
        }
    }

    // set the board pieces to 0
    private void initializeBoardToZeros() {

        for (int n[] : board) {
            for (int i = 0; i < 9; ++i)
                n[i] = 0;
        }
    }

    // sets the cell candidates to default 0 - 9
    private void initializeCandidatesToDefault() {

        for (int[][] col : candidates) {
            for (int[] row : col) {
                for (int i = 0; i < 9; ++i)
                    row[i] = i + 1;
            }
        }
    }

    // given board piece location, combine the candidates used
    // in column, row, and box into one list
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

    // traverse along the column and find all candidates
    // that are present in the column and return the set with them
    private Set<Integer> findUsedCandCol(int column) {

        Set<Integer> temp = new HashSet<Integer>();

        for (int row = 0; row < 9; ++row) {
            if (board[column][row] != 0)
                temp.add(board[column][row]);
        }

        return temp;
    }

    // traverse along the row and find all candidates
    // that are present in the row and return the set with them
    private Set<Integer> findUsedCandRow(int row) {

        Set<Integer> temp = new HashSet<Integer>();

        for (int col = 0; col < 9; ++col) {
            if (board[col][row] != 0)
                temp.add(board[col][row]);
        }

        return temp;
    }

    // traverse the 3x3  box and find all candidates
    // that are present in the box and return the set with them
    // example - x represent box:
    //  0 0 0 0 0 0 0 0 0
    //  0 0 0 0 0 0 0 0 0
    //  0 0 0 0 0 0 0 0 0
    //  0 0 0 0 0 0 X X X
    //  0 0 0 0 0 0 X X X
    //  0 0 0 0 0 0 X X X
    //  0 0 0 0 0 0 0 0 0
    //  0 0 0 0 0 0 0 0 0
    //  0 0 0 0 0 0 0 0 0
    private Set<Integer> findUsedCandBox(Coordinates c) {

        Set<Integer> temp = new HashSet<Integer>();
        Coordinates boxTop = findTopLeftOfTheBox(c);

        int colBox = boxTop.getCol();
        int rowBox = boxTop.getRow();

        // traverse the box
        for (int i = colBox; i < colBox + 3; ++i)
            for (int k = rowBox; k < rowBox + 3; ++k)
                if (board[i][k] != 0)
                    temp.add(board[i][k]);

        return temp;
    }

    private void removeCandidateInRow(Coordinates c, int cand) {

        int y = c.getRow();

        for (int i = 0; i < 9; ++i) {
            candidates[i][y][cand - 1] = 0;
        }
    }

    private void removeCandidateInCol(Coordinates c, int cand) {

        int x = c.getCol();

        for (int i = 0; i < 9; ++i) {
            candidates[x][i][cand - 1] = 0;
        }
    }

    private void removeCandidateInBox(Coordinates c, int cand) {
        // find first cell in the box
        Coordinates tempCoords = findTopLeftOfTheBox(c);

        int col = tempCoords.getCol();
        int row = tempCoords.getRow();

        // traverse the box and remove the candidate
        for (int i = col; i < col + 3; ++i)
            for (int k = row; k < row + 3; ++k)
                candidates[i][k][cand - 1] = 0;
    }

    // check if the the piece is unique for column, row, and box
    private boolean uniquePiece(Coordinates c) {
        return uniqueInCol(c) && uniqueInRow(c) && uniqueInBox(c);
    }

    private boolean uniqueInCol(Coordinates coords) {

        int tempPiece = getPieceAt(coords);

        for (int i = 0; i < 9; ++i) {
            if (i != coords.getRow()) {
                if (board[coords.getCol()][i] == tempPiece)
                    return false;
            }
        }

        return true;
    }

    private boolean uniqueInRow(Coordinates coords) {

        int tempPiece = getPieceAt(coords);

        for (int i = 0; i < 9; ++i) {
            if (i != coords.getCol()) {
                if (board[i][coords.getRow()] == tempPiece)
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

    // verifies if candidate is hidden within the column, row, or box
    private boolean isHiddenSingle(int cand, Coordinates coords) {

        if (hiddenInCol(cand, coords))
            return true;
        else if (hiddenInRow(cand, coords))
            return true;
        else if (hiddenInBox(cand, coords))
            return true;
        else
            return false;
    }

    // checks if the candidate is hidden within given column
    private boolean hiddenInCol(int cand, Coordinates c) {

        int col = c.getCol();

        // traverse the column
        for (int row = 0; row < 9; ++row) {
            if (board[col][row] == 0) {
                if (!coordinatesSame(c, new Coordinates(col, row))) {
                    if (getCandidatesAt(new Coordinates(col, row)).contains(cand))
                        return false;
                }
            }
        }

        return true;
    }

    // checks if the candidate is hidden within given row
    private boolean hiddenInRow(int cand, Coordinates c) {

        int row = c.getRow();

        // traverse the row
        for (int col = 0; col < 9; ++col) {
            if (board[col][row] == 0) {
                if (!coordinatesSame(c, new Coordinates(col, row))) {
                    if (getCandidatesAt(new Coordinates(col, row)).contains(cand))
                        return false;
                }
            }
        }

        return true;
    }

    // checks if the candidate is hidden within the given box
    private boolean hiddenInBox(int cand, Coordinates c) {

        Coordinates tempCoords = findTopLeftOfTheBox(c);

        int col = tempCoords.getCol();
        int row = tempCoords.getRow();

        // traverse the box
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

    // check column, remove in box
    private boolean restrictedToColBox(int col) {
        // traverse the column
        for (int row = 0; row < 9; ++row) {
            List<Integer> myCand = getCandidatesAt(new Coordinates(col, row));
            for (int cand : myCand) {
                List<Integer> presentAtIndeces = findOccuranceIndecesInCol(col, cand);
                // make sure there is at most three elements,
                // otherwise it could not be fitted in one 3 element
                // column in the box
                if (presentAtIndeces.size() <= 3) {
                    // check if they all fits in the box
                    int boxID = inBoxRange(presentAtIndeces);
                    Coordinates boxStart = findTopLeftOfTheBox(new Coordinates(col, (boxID * 3)));
                    // contrainted in the box,make sure there is more occurencies
                    // than those in the box to remove
                    if (boxID != -1 && (numCandOccurencesInBox(boxStart, cand) > presentAtIndeces.size())) {
                        // traverse the box and remove
                        for (int i = boxStart.getCol(); i < boxStart.getCol() + 3; ++i) {
                            for (int k = boxStart.getRow(); k < boxStart.getRow() + 3; ++k) {
                                if (i != col) {
                                    candidates[i][k][cand - 1] = 0;
                                }
                            }
                        }
                        return true;   // success
                    }
                }
            }
        }
        return false;                   // failure
    }

    // check row, remove from box
    private boolean restrictedToRowBox(int row) {
        // traverse the row
        for (int col = 0; col < 9; ++col) {
            List<Integer> myCand = getCandidatesAt(new Coordinates(col, row));
            for (int cand : myCand) {
                List<Integer> presentAtIndeces = findOccuranceIndecesInRow(row, cand);
                // make sure there is at most three elements,
                // otherwise it could not be fitted in one 3 element
                // row in the box
                if (presentAtIndeces.size() <= 3) {
                    // check if they all fits in the box
                    int boxID = inBoxRange(presentAtIndeces);
                    Coordinates boxStart = findTopLeftOfTheBox(new Coordinates((boxID * 3), row));
                    // contrainted in the box,make sure there is more occurencies
                    // than those in the box to remove
                    if (boxID != -1 && (numCandOccurencesInBox(boxStart, cand) > presentAtIndeces.size())) {
                        // traverse the box and remove
                        for (int i = boxStart.getCol(); i < boxStart.getCol() + 3; ++i) {
                            for (int k = boxStart.getRow(); k < boxStart.getRow() + 3; ++k) {
                                if (k != row) {
                                    candidates[i][k][cand - 1] = 0;
                                }
                            }
                        }
                        return true;   // success
                    }
                }
            }
        }
        return false;                   // failure
    }

    // check box, remove from column
    private boolean restrictedToBoxCol(int boxIndex) {
        Coordinates boxStart = findTopLeftByIndex(boxIndex);
        // traverse the box
        for (int col = boxStart.getCol(); col < boxStart.getCol() + 3; ++col) {
            for (int row = boxStart.getRow(); row < boxStart.getRow() + 3; ++row) {
                List<Integer> myCand = getCandidatesAt(new Coordinates(col, row));
                for (int cand : myCand) {
                    List<Coordinates> presentAtCoordinates = findOccuranceCoordinatesInBox(boxStart, cand);
                    // make sure there is at most three elements,
                    // otherwise it could not be fitted in one 3 element
                    // row in the box
                    if (presentAtCoordinates.size() <= 3) {
                        // check if they all fits in the box
                        int colID = inOneColumn(presentAtCoordinates);
                        // contrainted in the column,make sure there is more occurencies
                        // than those in the box to remove
                        if (colID != -1 && (numCandOccurenceInCol(colID, cand) > presentAtCoordinates.size())) {
                            // traverse the column and remove
                            for (int myRow = 0; myRow < 9; ++myRow) {
                                if (myRow != boxStart.getRow() && myRow != boxStart.getRow() + 1
                                        && myRow != boxStart.getRow() + 2) {

                                    // update candidate
                                    candidates[colID][myRow][cand - 1] = 0;
                                }
                            }
                            return true;   // success
                        }
                    }
                }
            }
        }
        return false;                       // failure
    }

    // check box, remove from row
    private boolean restrictedToBoxRow(int boxIndex) {
        Coordinates boxStart = findTopLeftByIndex(boxIndex);
        // traverse the box
        for (int col = boxStart.getCol(); col < boxStart.getCol() + 3; ++col) {
            for (int row = boxStart.getRow(); row < boxStart.getRow() + 3; ++row) {
                List<Integer> myCand = getCandidatesAt(new Coordinates(col, row));
                for (int cand : myCand) {
                    List<Coordinates> presentAtCoordinates = findOccuranceCoordinatesInBox(boxStart, cand);
                    // make sure there is at most three elements,
                    // otherwise it could not be fitted in one 3 element
                    // row in the box
                    if (presentAtCoordinates.size() <= 3) {
                        // check if they all fits in the box
                        int rowID = inOneRow(presentAtCoordinates);
                        // contrainted in the row,make sure there is more occurencies
                        // than those in the box to remove
                        if (rowID != -1 && (numCandOccurenceInRow(rowID, cand) > presentAtCoordinates.size())) {
                            for (int myCol = 0; myCol < 9; ++myCol) {
                                if (myCol != boxStart.getCol() && myCol != boxStart.getCol() + 1
                                        && myCol != boxStart.getCol() + 2) {

                                    // update candidate
                                    candidates[myCol][rowID][cand - 1] = 0;
                                }
                            }
                            return true;   // success
                        }
                    }
                }
            }
        }
        return false;                       // failure
    }

    // traverses the column and counts the number of occurrences of given
    // candidate in the given column
    private List<Integer> findOccuranceIndecesInCol(int col, int cand) {
        List<Integer> temp = new ArrayList<Integer>();
        for (int row = 0; row < 9; ++row) {
            if (getCandidatesAt(new Coordinates(col, row)).contains(cand)) {
                temp.add(row);
            }
        }
        return temp;
    }

    // traverses the row and counts the number of occurrences of given
    // candidate in the given row
    private List<Integer> findOccuranceIndecesInRow(int row, int cand) {
        List<Integer> temp = new ArrayList<>();
        for (int col = 0; col < 9; ++col) {
            if (getCandidatesAt(new Coordinates(col, row)).contains(cand)) {
                temp.add(col);
            }
        }
        return temp;
    }

    // counts how many times given candidate occurs in the box
    private List<Coordinates> findOccuranceCoordinatesInBox(Coordinates boxStart, int cand) {

        List<Coordinates> temp = new ArrayList<Coordinates>();

        // traverse the box snd collect the coordinates of cells where given
        // candidate is present
        for (int col = boxStart.getCol(); col < boxStart.getCol() + 3; ++col) {
            for (int row = boxStart.getRow(); row < boxStart.getRow() + 3; ++row) {
                if (getCandidatesAt(new Coordinates(col, row)).contains(cand))
                    temp.add(new Coordinates(col, row));
            }
        }

        return temp;
    }

    // count how many times candidate occurs in the box
    private int numCandOccurencesInBox(Coordinates boxStart, int cand) {

        int counter = 0;

        // traverse the box and count
        for (int col = boxStart.getCol(); col < boxStart.getCol() + 3; ++col) {
            for (int row = boxStart.getRow(); row < boxStart.getRow() + 3; ++row) {
                if (getCandidatesAt(new Coordinates(col, row)).contains(cand))
                    ++counter;
            }
        }

        return counter;
    }

    // counts how many times given candidate occurs in the column
    private int numCandOccurenceInCol(int colID, int cand) {

        int count = 0;

        for (int row = 0; row < 9; ++row) {
            if (getCandidatesAt(new Coordinates(colID, row)).contains(cand))
                ++count;
        }

        return count;
    }

    // counts how many times given candidate occurs in the row
    private int numCandOccurenceInRow(int rowID, int cand) {

        int count = 0;

        for (int col = 0; col < 9; ++col) {
            if (getCandidatesAt(new Coordinates(col, rowID)).contains(cand))
                ++count;
        }

        return count;
    }

    // verifies if all list elements are included into one column
    // return the index of the column (column number) if so, -1 otherwise
    private int inOneColumn(List<Coordinates> list) {
        int col = list.get(0).getCol();

        for (Coordinates c : list) {
            if (c.getCol() != col)
                return -1;
        }

        return col;
    }

    // verifies if the candidates in the list are all in the same row
    private int inOneRow(List<Coordinates> list) {
        int row = list.get(0).getRow();

        for (Coordinates c : list) {
            if (c.getRow() != row)
                return -1;
        }

        return row;
    }

    // checks how many candidates can be removed from the box
    private boolean availableToRemoveCol(ArrayList<Integer> sameCand, int col) {

        int count = 0;

        // traverse the column
        for (int row = 0; row < 9; ++row) {
            for (int a : sameCand)
                if (getCandidatesAt(new Coordinates(col, row)).contains(a))
                    count++;
        }

        return count > 4;
    }

    // counts how many elements from the list are in the row
    // return true if there is more than 4, false is returned otherwise
    private boolean availableToRemoveRow(ArrayList<Integer> sameCand, int row) {

        int count = 0;

        for (int col = 0; col < 9; ++col) {
            for (int a : sameCand)
                if (getCandidatesAt(new Coordinates(col, row)).contains(a))
                    count++;
        }

        return count > 4;
    }

    // check if there is more that 4 elements to remove in the box
    private boolean availableToRemoveBox(ArrayList<Integer> sameCand, int col, int row) {

        int count = 0;

        // traverse the box and count
        for (int i = col; i < col + 3; ++i) {
            for (int k = row; k < row + 3; ++k) {
                for (int a : sameCand)
                    if (getCandidatesAt(new Coordinates(col, row)).contains(a))
                        count++;
            }
        }

        return count > 4;
    }

    // check columns for naked pairs algorithm
    private boolean nakedPairsCheckColumns() {

        // traverse the row
        for (int col = 0; col < 9; ++col) {
            ArrayList<ArrayList<Integer>> nakedPairs = getColCandWithLengthOf(2, col);
            // must be at least two pairs
            if (nakedPairs.size() > 1) {
                ArrayList<Integer> sameCand = findSameCand(nakedPairs);

                // make sure there similar candidates and that they can be removed
                if (sameCand != null && availableToRemoveCol(sameCand, col)) {
                    for (int i = 0; i < 9; ++i) {
                        // check to not remove the cells that were resolved
                        if (!listAreSame(getCandidatesAt(new Coordinates(col, i)), sameCand)) {
                            for (int a : sameCand) {
                                candidates[col][i][a - 1] = 0;
                            }
                        }
                    }

                    return true;    // success
                }

            }
        }

        return false;               // success
    }

    // checks the row if the naked pair algorithm can be applied
    // to resolve the cell
    private boolean nakedPairsCheckRows() {

        // traverse the columns
        for (int row = 0; row < 9; ++row) {
            // grab the list of naked pairs
            ArrayList<ArrayList<Integer>> nakedPairs = getRowCandWithLengthOf(2, row);
            // make sure there is more than one
            if (nakedPairs.size() > 1) {
                ArrayList<Integer> sameCand = findSameCand(nakedPairs);
                // make sure there are elements in the sameCand
                if (sameCand != null && availableToRemoveRow(sameCand, row)) {
                    for (int i = 0; i < 9; ++i) {
                        // make sure that the candidates from the resolved cell
                        // will not get removed
                        if (!listAreSame(getCandidatesAt(new Coordinates(i, row)), sameCand)) {
                            for (int a : sameCand) {
                                candidates[i][row][a - 1] = 0;
                            }
                        }
                    }
                    return true;    // success
                }

            }
        }
        return false;               // failure
    }

    // checks the boxes if the naked pair algorithm can be applied
    // to resolve the cell
    private boolean nakedPairsCheckBoxes() {

        // traverse throgh boxes
        for (int col = 0; col <= 6; col += 3) {
            for (int row = 0; row <= 6; row += 3) {
                // grab the naked pairs
                ArrayList<ArrayList<Integer>> nakedPairs =
                        getBoxCandWithLengthOf(2, new Coordinates(col, row));
                // make sure there is more than one
                if (nakedPairs.size() > 1) {
                    ArrayList<Integer> sameCand = findSameCand(nakedPairs);

                    // make sure sameCand exists and that we
                    // have pieces to remove within the box
                    if (sameCand != null && availableToRemoveBox(sameCand, col, row)) {
                        for (int i = col; i < col + 3; ++i) {
                            for (int k = row; k < row + 3; ++k) {
                                // make sure not to remove candidates in the cells that were
                                // just resolved
                                if (!listAreSame(getCandidatesAt(new Coordinates(i, k)), sameCand)) {
                                    for (int a : sameCand) {
                                        candidates[i][k][a - 1] = 0;
                                    }
                                }
                            }
                        }

                        return true;            // success
                    }

                }
            }
        }

        return false;                           // failure
    }

    // returns the list of candidates in given column with specified length
    private ArrayList<ArrayList<Integer>> getColCandWithLengthOf(int length, int col) {

        ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();

        for (int row = 0; row < 9; ++row) {
            List<Integer> cand = getCandidatesAt(new Coordinates(col, row));
            if (cand.size() == 2)
                temp.add((ArrayList) cand);
        }

        return temp;
    }

    // colect the candidates within the specified row and with specified length
    private ArrayList<ArrayList<Integer>> getRowCandWithLengthOf(int length, int row) {

        ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();

        // traverse the row
        for (int col = 0; col < 9; ++col) {
            List<Integer> cand = getCandidatesAt(new Coordinates(col, row));
            if (cand.size() == 2)
                temp.add((ArrayList) cand);
        }

        return temp;
    }

    // collect the candidates with specified length
    private ArrayList<ArrayList<Integer>> getBoxCandWithLengthOf(int length, Coordinates c) {

        ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();

        // traverse the box and add candidates with length of 2
        for (int col = c.getCol(); col < c.getCol() + 3; ++col) {
            for (int row = c.getRow(); row < c.getRow() + 3; ++row) {
                List<Integer> cand = getCandidatesAt(new Coordinates(col, row));
                if (cand.size() == 2)
                    temp.add((ArrayList) cand);
            }
        }

        return temp;
    }

    // traverse the list and checks if there are two lists that are the same
    private ArrayList<Integer> findSameCand(ArrayList<ArrayList<Integer>> nakedPairs) {

        for (int i = 0; i < nakedPairs.size(); ++i) {
            for (int k = i; k < nakedPairs.size(); ++k) {

                if ((i != k) && (nakedPairs.get(i).containsAll(nakedPairs.get(k)))) {
                    return nakedPairs.get(i);       // return found same lists
                }

            }
        }

        return null;            // not found
    }

    private boolean coordinatesSame(Coordinates a, Coordinates b) {
        return (a.getCol() == b.getCol()) && (a.getRow() == b.getRow());
    }

    // clears the list of candidates in the given cell
    private void clearCandidatesAt(Coordinates c) {

        int x = c.getCol();
        int y = c.getRow();

        for (int i = 0; i < 9; ++i)
            candidates[x][y][i] = 0;
    }

    // update the candidate list with new values
    private void populateCandidates(int col, int row, Set<Integer> usedCand) {

        for (int i = 1; i <= 9; ++i) {
            if (!usedCand.contains(i))
                candidates[col][row][i - 1] = i;
            else
                candidates[col][row][i - 1] = 0;
        }
    }

    // checks if the cell contains given candidate
    private boolean onCandidateList(Coordinates coords, int candidates) {
        return getCandidatesAt(coords).contains(candidates);
    }

    // verifies if pieces are placed correctly on the board
    private boolean correctPiecesArrangement() {

        // traverse the board
        for (int i = 0; i < 9; ++i) {
            for (int k = 0; k < 9; ++k) {
                if (!uniquePiece(new Coordinates(i, k)))
                    return false;
            }
        }

        return true;
    }

    // delete the given candidate from the column, row, and box
    // which intersects with the given coordinates
    private void updateCandidateList(Coordinates coords, int cand) {

        clearCandidatesAt(coords);
        removeCandidateInCol(coords, cand);
        removeCandidateInRow(coords, cand);
        removeCandidateInBox(coords, cand);
    }

    private boolean spotTaken(Coordinates coords) {
        return board[coords.getCol()][coords.getRow()] != 0;
    }

    private boolean coordinatesInRange(Coordinates c) {
        return c.getCol() >= 0 && c.getCol() <= 8
                && c.getRow() >= 0 && c.getRow() <= 8;
    }

    // traverse the board and initiallizes the candidates to default
    private void generateCandidateListWholeBoard() {

        for (int i = 0; i < 9; ++i) {
            for (int k = 0; k < 9; ++k) {
                // generate the candidates
                Set<Integer> usedCandidates = getUsedCandidates(i, k);
                // update the array with new values
                populateCandidates(i, k, usedCandidates);
            }
        }

    }

    // checks if two lists are the same
    private boolean listAreSame(List<Integer> L1, List<Integer> L2) {

        if (L1.size() != L2.size())
            return false;
        else {
            for (int i = 0; i < L1.size(); ++i) {
                if (L1.get(i) != L2.get(i))
                    return false;   // condition broken, not the same
            }
        }

        return true;                // list are same
    }

    // finds the coordinates of the box based on given box index
    private Coordinates findTopLeftByIndex(int boxIndex) {

        Coordinates temp = new Coordinates(-1, -1);

        switch (boxIndex) {
            case 0:
                temp.setColAndRow(0, 0);
                break;
            case 1:
                temp.setColAndRow(3, 0);
                break;
            case 2:
                temp.setColAndRow(6, 0);
                break;
            case 3:
                temp.setColAndRow(0, 3);
                break;
            case 4:
                temp.setColAndRow(3, 3);
                break;
            case 5:
                temp.setColAndRow(6, 3);
                break;
            case 6:
                temp.setColAndRow(0, 6);
                break;
            case 7:
                temp.setColAndRow(3, 6);
                break;
            case 8:
                temp.setColAndRow(6, 6);
                break;
        }

        return temp;
    }

    private Coordinates findTopLeftOfTheBox(Coordinates coordinates) {

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

    // checks if the given indeces are from one box,
    // return the index of box if so ( 0, 1, 2 )
    private int inBoxRange(List<Integer> presentAtIndeces) {
        if (inGivenRange(0, 2, presentAtIndeces)) {
            return 0;
        } else if (inGivenRange(3, 5, presentAtIndeces)) {
            return 1;
        } else if (inGivenRange(6, 8, presentAtIndeces)) {
            return 2;
        } else
            return -1;      // not in the box
    }

    // checks if all elements in the given list are within specified range
    private boolean inGivenRange(int lowerBound, int upperBound, List<Integer> list) {

        for (int a : list) {
            if (a < lowerBound || a > upperBound) {
                return false;
            }
        }

        return true;
    }

}
