/**
 * Created by sean_martinelli on 10/20/17.
 */
public class SudokuSolverController {

    private SudokuSolverView view;
    private SudokuSolverModel model;


    SudokuSolverController(SudokuSolverModel m, SudokuSolverView v) {
        model = m;
        view = v;
    }


    public SudokuSolverView getView() {
        return view;
    }

    public SudokuSolverModel getModel() {
        return model;
    }
}
