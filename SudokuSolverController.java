/*
    Todo:
        -Maybe make updatePiece() in the model throw an exception instead of returning bool
        -Finish error handling in LoadPuzzleFromFile (if invalid index is in text file)
        -Also check to make sure there are actually 3 ints on input file line before trying to read them.
        -Set up check on fill
        -Dialog boxes
        -Check for win after each move
        -save file
        -Hints menu
        -Use images and change cursor?
 */

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
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

    public void LoadPuzzleFromFile()
    {
        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(view.GetFrame());

        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();

            try {
                Scanner input = new Scanner(file);

                while (input.hasNextLine())
                {
                    Scanner line = new Scanner(input.nextLine());
                    int row = line.nextInt() - 1;
                    int col = line.nextInt() - 1;
                    int value = line.nextInt();

                    Coordinates coords = new Coordinates(col, row);
                    if(model.updatePiece(coords, value, true))
                        view.SetOriginalPiece(coords, value);
                    else
                        System.err.println("invalid: \"" + row + " " + col + " " + value + "\" " +
                                "does not match the cell's candidate list.");
                }
            }
            catch (FileNotFoundException exception)
            {
                System.err.println("File not found.");
            }
        }

        view.SetStatusLabel(""); //Clear original status label message
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
                if(model.updatePiece(button.GetCoordinates(), mode, checkOnFill))
                    view.UpdatePosition(button.GetCoordinates(), mode);
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
        }
    }

    private class OptionButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(e.getActionCommand().equals("x"))
                mode = 10;
            else if(e.getActionCommand().equals("?"))
                mode = 11;
            else
                mode = Integer.parseInt(e.getActionCommand());

            view.SelectMode(mode);
        }
    }

    private class MenuHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(e.getActionCommand().equals("Load Puzzle"))
                LoadPuzzleFromFile();
            else if(e.getActionCommand().equals("Quit"))
                java.lang.System.exit(0);
        }
    }
}
