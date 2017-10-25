/*
    Todo:
        -Maybe make updatePiece() in the model throw an exception instead of returning bool
        -Finish error handling in LoadPuzzleFromFile (if invalid index is in text file)
        -Also check to make sure there are actually 3 ints on input file line before trying to read them.
        -Dialog boxes
        -make sure bert can handle saved files with "\r\n"
        -Hints menu
        -Apply highlight animation to hits
        -Look into Warning about junit that pops up when first running the program
 */

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class SudokuSolverController {

    private SudokuSolverView view;
    private SudokuSolverModel model;
    private int mode;
    private boolean checkOnFill;


    SudokuSolverController(SudokuSolverModel m, SudokuSolverView v) {
        model = m;
        view = v;

        //Set initial mode
        mode = 1;
        view.SelectMode(mode);

        //Set default check on fill
        checkOnFill = true;

        view.AddGridButtonListener(new GridButtonHandler());
        view.AddOptionButtonListener(new OptionButtonHandler());
        view.AddMenuListener(new MenuHandler());
    }


    public SudokuSolverView getView() {
        return view;
    }

    public SudokuSolverModel getModel() {
        return model;
    }

    private void LoadPuzzleFromFile()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Documents (*.txt)", "txt"));
        int returnVal = fileChooser.showOpenDialog(view.GetFrame());

        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            try {
                ClearBoard();

                Scanner input = new Scanner(fileChooser.getSelectedFile());

                while (input.hasNextLine())
                {
                    Scanner line = new Scanner(input.nextLine());
                    //FIXME: No such element exception
                    int row = line.nextInt() - 1;
                    int col = line.nextInt() - 1;
                    int value = line.nextInt();

                    Coordinates coords = new Coordinates(col, row);
                    try {
                        model.updatePiece(coords, value, true);
                        view.SetOriginalPiece(coords, value);
                    }
                    catch (Exception e) {
                        System.err.println("invalid: \"" + row + " " + col + " " + value + "\" " +
                                "does not match the cell's candidate list.");
                    }
                }
            }
            catch (FileNotFoundException exception) {
                System.err.println("File not found.");
            }
        }

        view.SetStatusLabel(""); //Clear original status label message
    }

    private void SavePuzzleToFile()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Documents (*.txt)", "txt"));

        int returnVal = fileChooser.showSaveDialog(view.GetFrame());

        if(returnVal == JFileChooser.APPROVE_OPTION)
        {

            //Check if file already exists
            File checkFile = new File(fileChooser.getSelectedFile().toString() + ".txt");
            if(checkFile.exists()) {
                int userResponse = JOptionPane.showConfirmDialog(view.GetFrame(),
                        "A puzzle with that name already exists. \nDo you want to replace it?",
                        "Puzzle Already Exists", JOptionPane.YES_NO_OPTION);

                if(userResponse == JOptionPane.NO_OPTION)
                    return;
            }

            try {
                FileWriter fileWriter = new FileWriter(fileChooser.getSelectedFile() + ".txt");

                int[][] board = model.getBoard();

                for(int i = 0; i < board.length; ++i)
                    for(int j = 0; j < board[i].length; ++j)
                    {
                        if(board[i][j] != 0) {
                            fileWriter.write(Integer.toString(j+1) + " " + Integer.toString(i+1));
                            fileWriter.write(" " + Integer.toString(board[i][j]) + "\r\n");
                        }
                    }

                fileWriter.flush();
                fileWriter.close();
            }
            catch (IOException exception) {
                System.err.println("Could not create file.");
            }

        }
    }

    private void UpdateViewBoard(int[][] newBoard)
    {
        if(newBoard != null) {
            view.UpdateBoard(newBoard);
            CheckForWin();
        }
    }

    // FIXME: resetBoard function available
    private void ClearBoard()
    {
        //model.initializeBoardToZeros();
        //model.initializeCandidatesToDefault();
        model.resetBoard();
        view.ClearBoard();
    }

    private void CheckForWin()
    {
        if(model.gameComplete()) {
            view.SetStatusLabel("You Win!");
            view.DisplayMessage("You have found a solution! You Win!", "You Win!");
        }
    }

    private static String CreateCandidatesMessage(List<Integer> candidateList)
    {
        StringBuilder statusMessage = new StringBuilder("Candidates: ");

        //Create status message from candidates
        for(int i=0; i<candidateList.size(); ++i) {
            statusMessage.append(candidateList.get(i));
            if(i != candidateList.size()-1)
                statusMessage.append(", ");
        }

        if(candidateList.isEmpty())
            statusMessage.append("none");

        return statusMessage.toString();
    }

    private class GridButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            SudokuButton button = (SudokuButton)e.getSource();

            view.SetStatusLabel(""); //Clear old status message

            //Make sure the button the user clicked is editable
            if(!button.IsEditable())
                return;

            if(mode >= 1 && mode <= 9) //Insert number mode
            {
                try {
                    model.updatePiece(button.GetCoordinates(), mode, checkOnFill);
                    view.UpdatePosition(button.GetCoordinates(), mode);
                    view.HighLightLocation(button.GetCoordinates());
                }
                catch (Exception exception) {
                    //Ignore invalid piece
                }
            }
            else if(mode == 10) //Erase Mode
            {
                if(model.removePiece(button.GetCoordinates()))
                    view.ClearPosition(button.GetCoordinates());
            }
            else //Hint mode
            {
                List<Integer> candidateList = model.getCandidatesAt(button.GetCoordinates());
                view.SetStatusLabel(CreateCandidatesMessage(candidateList));
            }

            CheckForWin();
        }
    }

    private class OptionButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(e.getActionCommand().equals("x")) {
                mode = 10;
                view.SetCursor("eraser_blue.png");
            } else if(e.getActionCommand().equals("?")) {
                mode = 11;
                view.SetCursor("questionmark.png");
            } else {
                mode = Integer.parseInt(e.getActionCommand());
                view.SetCursor(Integer.toString(mode) + ".png");
            }

            view.SelectMode(mode);
        }
    }

    private class MenuHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {

            view.SetStatusLabel("");

            if (e.getActionCommand().equals("Load Puzzle"))
                LoadPuzzleFromFile();

            else if (e.getActionCommand().equals("Save Puzzle"))
                SavePuzzleToFile();

            else if (e.getActionCommand().equals("Single Algorithm")) {
                try {
                    Coordinates coords = model.singleAlgorithm();
                    view.UpdatePosition(coords, model.getPieceAt(coords));
                } catch (Exception exception) {
                    view.SetStatusLabel("Single algorithm could not find a piece.");
                }
            }

            //FIXME: Coordinates returned from hiddenSingleAlgorithm
            else if (e.getActionCommand().equals("Hidden Single Algorithm")) {
               //UpdateViewBoard(model.hiddenSingleAlgorithm());
            }

            //FIXME: modified for testing
            else if (e.getActionCommand().equals("Locked Candidate Algorithm")) {
                System.out.println(model.lockedCandidateAlgorithm());
                //java.lang.System.exit(0);
            }


            //FIXME: modified for testing
            else if (e.getActionCommand().equals("Naked Pairs Algorithm")) {
                System.out.println(model.nakedPairsAlgorithm());
                //java.lang.System.exit(0);
            }

            else if(e.getActionCommand().equals("Check On Fill")) {
                JCheckBoxMenuItem CheckOnFillItem = (JCheckBoxMenuItem)e.getSource();
                checkOnFill = CheckOnFillItem.getState();

            } else if(e.getActionCommand().equals("Quit"))
                java.lang.System.exit(0);
        }
    }
}
