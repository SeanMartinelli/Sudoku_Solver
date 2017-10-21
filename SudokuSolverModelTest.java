/**
 * Created by Michal on 10/20/2017.
 */

/*
NOTES:
    If '@Test" specifier is not found:
        1. place cursor on it   2. alt + enter  3. add JUnit4
 */

import org.junit.jupiter.api.Test;

import java.util.List;

public class SudokuSolverModelTest {

    SudokuSolverModel testModel = new SudokuSolverModel();

    @Test
    void testInitializedToZero() {
        testModel.initializeBoardToZeros();
        for (int n[]: testModel.getBoard()) {
            for (int a: n) {
                assert a == 0;
            }
        }
    }

    @Test
    void testInitializeCandidatesToDefault() {
        testModel.initializeCandidatesToDefault();
        for (int[][] col: testModel.getCandidates()) {
            for (int[] row: col) {
                for (int i = 0; i < 9; ++i) {
                    assert row[i] == i + 1;
                }
            }
        }
    }

    @Test
    void testGetCandidatesAt() {
        testModel.initializeCandidatesToDefault();
        List<Integer> cand = testModel.getCandidatesAt(new Coordinates(0,0));
        for (int i = 0; i < 9; ++i)
            assert cand.get(i) == i +1;
        assert cand.size() == 9;
    }

    @Test
    void testInRange() {
        assert testModel.inRange(0) == false;
        for (int i = 1; i <= 9; ++i)
            assert testModel.inRange(i) == true;

        assert testModel.inRange(10) == false;
    }

    @Test
    void testSpotTaken() {
        testModel.initializeBoardToZeros();
        for (int i = 0; i <9; ++i) {
            for (int k = 0; k <9; ++k)
                assert testModel.spotTaken(new Coordinates(i, k)) == false;
        }
    }

    @Test
    void testListCandidates() {
        testModel.initializeCandidatesToDefault();
        testModel.listCandidates();
    }

    @Test
    void testRemoveCandidateInRow() {
        testModel.initializeCandidatesToDefault();
        System.out.println("Before removing:");
        testModel.listCandidates();
        testModel.removeCandidateInRow(new Coordinates(3,5), 6);
        System.out.println();
        System.out.println("After removing:");
        testModel.listCandidates();

        for (int i = 0; i < 9; ++i) {
            assert testModel.getCandidates()[i][5][6 - 1] == 0;
        }
    }

    @Test
    void testRemoveCandidateInCol() {
        testModel.initializeCandidatesToDefault();
        System.out.println("Before removing:");
        testModel.listCandidates();
        testModel.removeCandidateInCol(new Coordinates(3,5), 6);
        System.out.println();
        System.out.println("After removing:");
        testModel.listCandidates();

        for (int i = 0; i < 9; ++i) {
            assert testModel.getCandidates()[3][i][6 - 1] == 0;
        }
    }

    @Test
    void testRemoveCandidateInBox() {
        testModel.initializeCandidatesToDefault();
        System.out.println("Before removing:");
        testModel.listCandidates();
        testModel.removeCandidateInBox(new Coordinates(5,5), 3);
        System.out.println();
        System.out.println("After removing:");
        testModel.listCandidates();


    }

    @Test
    void testClearCandidatesAt() {
        testModel.initializeCandidatesToDefault();
        System.out.println("Before clear:");
        testModel.listCandidates();
        testModel.clearCandidatesAt(new Coordinates(3,5));
        System.out.println();
        System.out.println("After clear:");
        testModel.listCandidates();

        for (int i = 0; i < 9; ++i)
            assert testModel.getCandidates()[3][5][i] == 0;
    }


}