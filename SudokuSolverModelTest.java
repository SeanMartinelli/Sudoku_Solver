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
        testModel.inititializeBoardToZeros();
        for (int n[]: testModel.getBoard()) {
            for (int a: n) {
                assert a == 0;
            }
        }
    }

    @Test
    void testInititializeCandidatesToDefault() {
        testModel.inititializeCandidatesToDefault();
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
        testModel.inititializeCandidatesToDefault();
        List<Integer> cand = testModel.getCandidatesAt(new Coordinates(0,0));
        for (int i = 0; i < 9; ++i)
            assert cand.get(i) == i +1;
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
        testModel.inititializeBoardToZeros();
        for (int i = 0; i <9; ++i) {
            for (int k = 0; k <9; ++k)
                assert testModel.spotTaken(new Coordinates(i, k)) == false;
        }
    }

}