/*
    Todo:
        -make sure bert can handle saved files with "\r\n"
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


    SudokuSolverController(SudokuSolverModel m, SudokuSolverView v, String args[]) {
        model = m;
        view = v;

        //Set initial mode
        mode = 1;
        view.SelectMode(mode);

        //Set default check on fill
        checkOnFill = true;

        view.AddGridButtonListener(new GridButtonHandler());
        view.AddOptionButtonListener(new OptionButtonHandler());
        view.AddMenuListener(new MenuBarHandler());

        //Load puzzle from command line argument
        if(args.length >= 1) {
            ProcessFile(args[0]);
            view.SetStatusLabel("");
        }
    }


    //
    // Allow a puzzle to be loaded from a text file.  Each line of the
    // file is checked for validity then added to the board. The file
    // must be formatted as (row col value) on each line.
    //
    private void LoadPuzzleFromFile()
    {
        //Prompt user with JFileChooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Text Documents (*.txt)", "txt"));
        int returnVal = fileChooser.showOpenDialog(view.GetFrame());

        //Process file
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            ProcessFile(fileChooser.getSelectedFile().getAbsolutePath());
        }

        view.SetStatusLabel(""); //Clear original status label message
    }

    //
    // Process each line of the puzzle file
    //
    private void ProcessFile(String fileName)
    {
        try {
            ClearBoard();

            //Open file
            Scanner input = new Scanner(new File(fileName));

            //Process each line
            while (input.hasNextLine())
            {
                Scanner line = new Scanner(input.nextLine());
                ProcessInputFileLine(line);
            }
        }
        catch (FileNotFoundException exception) {
            System.err.println("File not found." + exception.toString());
        }
    }

    //
    // Extract the row, column, and value from each line.  Validate it, then
    // add it to the board.
    //
    private void ProcessInputFileLine(Scanner line)
    {
        int row, col, value;

        //Get row if provided
        if(line.hasNextInt())
            row = line.nextInt() - 1;
        else
            return;

        //Get col if provided
        if(line.hasNextInt())
            col = line.nextInt() - 1;
        else
            return;

        //Get value if provided
        if(line.hasNextInt())
            value = line.nextInt();
        else
            return;

        //Make sure input is valid
        if(row < 0 || row > 8 || col < 0 || col > 8) {
            System.err.println("Invalid index: " + (row+1) + " " + (col+1));
            return;
        }
        if(value < 1 || value > 9) {
            System.err.println("Invalid value: " + value);
            return;
        }

        //Add value to board
        Coordinates coords = new Coordinates(col, row);
        try {
            model.updatePiece(coords, value, true);
            view.SetOriginalPiece(coords, value);
        }
        catch (Exception e) {
            System.err.println("Input \"" + (row+1) + " " + (col+1) + " "
                    + value + "\" " + "does not match the cell's candidate list.");
        }
    }

    //
    // Allows the current puzzle to be saved to a text file that can be
    // loaded later. The file is formatted as (row col value) on each line.
    //
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
                WriteBoardToFile(fileWriter);

            }
            catch (IOException exception) {
                System.err.println("Could not create file.");
            }

        }
    }

    //
    // For each piece on the board create a line in the text file that contains
    // the row, column, and value for that piece.
    //
    private void WriteBoardToFile(FileWriter fileWriter) throws IOException
    {
        int[][] board = model.getBoard();

        //Loop through board positions
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

    //
    // Reset the board to its default state
    //
    private void ClearBoard()
    {
        model.resetBoard();
        view.ClearBoard();
    }

    //
    // Check the board to see if it is in a winning configuration.
    //
    private void CheckForWin()
    {
        if(model.gameComplete()) {
            view.SetStatusLabel("You Win!");
            view.DisplayMessage("You have found a solution! You Win!", "You Win!");
        }
    }

    //
    // Build and return a string of candidates from the
    // candidate list passed to the method.
    //
    private static String CreateCandidatesMessage(List<Integer> candidateList)
    {
        StringBuilder statusMessage = new StringBuilder("Candidates: ");

        //Create status message from candidates
        for(int i=0; i<candidateList.size(); ++i) {
            statusMessage.append(candidateList.get(i));
            if(i != candidateList.size()-1)
                statusMessage.append(", ");
        }

        //Check if the list is empty
        if(candidateList.isEmpty())
            statusMessage.append("none");

        return statusMessage.toString();
    }

    //
    // Button handler that will react to a button on the main grid
    // being pressed.  When a button is clicked the mode is checked
    // and the correct action is performed based on the mode.
    //
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
                //try to add the value to the board
                try {
                    model.updatePiece(button.GetCoordinates(), mode, checkOnFill);
                    view.UpdatePosition(button.GetCoordinates(), mode);
                }
                catch (Exception exception) {
                    view.SetStatusLabel(mode + " cannot be placed " +
                            "here because it is not one of the candidates " +
                            "for this location.");
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

    //
    // Option button handler that will set the mode based on which option
    // button the user pressed.
    //
    private class OptionButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            //Determine which button was pressed
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

    //
    // Menu bar handler that responds to an ActionEvent by passing the
    // action event to each individual menu handler.
    //
    private class MenuBarHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {

            view.SetStatusLabel(""); //Reset status label

            //pass ActionEvent to individual menu handlers
            FileMenuHandler(e);
            HelpMenuHandler(e);
            HintMenuHandler(e);
        }
    }

    //
    // Handles ActionEvents that occur from an item in
    // the file menu being clicked.
    //
    private void FileMenuHandler(ActionEvent e)
    {
        if (e.getActionCommand().equals("Load Puzzle")) {
            LoadPuzzleFromFile();
        }

        else if (e.getActionCommand().equals("Save Puzzle")) {
            SavePuzzleToFile();
        }

        else if(e.getActionCommand().equals("Quit"))
            java.lang.System.exit(0);
    }

    //
    // Handles ActionEvents that occur from an item in
    // the help menu being clicked.
    //
    private void HelpMenuHandler(ActionEvent e)
    {
        if (e.getActionCommand().equals("How To Play"))
            view.DisplayMessage(GenerateHowToPlayMessage(), "How To Play");

        else if (e.getActionCommand().equals("Interface Help"))
            view.DisplayMessage(GenerateInterfaceHelpMessage(), "Interface Help");

        else if(e.getActionCommand().equals("Authors"))
            view.DisplayMessage(GenerateAuthorMessage(), "Authors");
    }

    //
    // Handles ActionEvents that occur from an item in
    // the hint menu being clicked.
    //
    private void HintMenuHandler(ActionEvent e)
    {
        //Perform single algorithm
        if (e.getActionCommand().equals("Single Algorithm")) {
            try {
                Coordinates updatedCoords = model.singleAlgorithm();
                view.UpdatePosition(updatedCoords, model.getPieceAt(updatedCoords));
            } catch (Exception exception) {
                view.SetStatusLabel("Single Algorithm: No piece to place");
            }
        }

        //Perform Hidden Single Algorithm
        else if (e.getActionCommand().equals("Hidden Single Algorithm")) {
            try {
                Coordinates updatedCoords = model.hiddenSingleAlgorithm();
                view.UpdatePosition(updatedCoords, model.getPieceAt(updatedCoords));
            } catch (Exception exception) {
                view.SetStatusLabel("Hidden Single Algorithm: No piece to place");
            }
        }

        //Perform Locked Candidate Algorithm
        else if (e.getActionCommand().equals("Locked Candidate Algorithm")) {
            if(model.lockedCandidateAlgorithm())
                view.SetStatusLabel("Locked candidate found.");
            else
                view.SetStatusLabel("Locked candidate not found.");
        }

        //Perform Naked Pairs Algorithm
        else if (e.getActionCommand().equals("Naked Pairs Algorithm")) {
            if(model.nakedPairsAlgorithm())
                view.SetStatusLabel("Naked pair found.");
            else
                view.SetStatusLabel("Naked pair not found.");
        }

        //Perform Fill All Possible Blank Cells
        else if (e.getActionCommand().equals("Fill All Possible Blank Cells")) {
            view.UpdateBoard(model.resolveAllPossibleCells());
        }

        //Toggle Check on fill
        else if(e.getActionCommand().equals("Check On Fill")) {
            JCheckBoxMenuItem CheckOnFillItem = (JCheckBoxMenuItem)e.getSource();
            checkOnFill = CheckOnFillItem.getState();
        }
    }

    private String GenerateHowToPlayMessage()
    {
        return "The objective of a Sudoku puzzle is to try to fill in all\n" +
                "of the squares in such a way that every row, column, and\n" +
                "box contain the numbers 1-9. Duplicate numbers in each\n" +
                "row, column, and box are not allowed. \n";
    }

    private String GenerateAuthorMessage()
    {
        return "Michal Bochnak\n" +
                "Netid: mbochnak2 \n\n" +
                "Sean Martinelli\n" +
                "Netid: smarti58";
    }

    private String GenerateInterfaceHelpMessage()
    {
        return "Menu Items: \n" +
                "   File:\n" +
                "       -Load puzzle: Allows you to load a puzzle from a text file.\n" +
                "       -Save Puzzle: Allows you to save a puzzle to a text file.\n" +
                "       -Quit: Exits the application. \n\n" +
                "   Help:\n" +
                "       -How To Play: Gives instructions about how to play the game. \n" +
                "       -Interface Help: Displays this message.\n" +
                "       -Authors: Displays the authors of this program.\n\n" +
                "   Hint:\n" +
                "       -Check On Fill: If this is selected the program will check to your move to see if it\n" +
                "           is valid before it is placed on the board. Deselect to disable checking.\n" +
                "       -Single Algorithm: Perform this algorithm. If the algorithm is successful, a space on\n" +
                "            the board will be resolved.\n" +
                "       -Hidden Single Algorithm: Perform the Hidden Single Algorithm. If the algorithm is\n" +
                "           successful, a space on the board will be resolved.\n" +
                "       -Locked Candidate Algorithm: Perform this algorithm. If the algorithm is successful,\n" +
                "           the candidate list for a space(s) will be narrowed down.\n" +
                "       -Naked Pairs Algorithm: Perform this algorithm. If the algorithm is successful,\n" +
                "           the candidate list for a space(s) will be narrowed down.\n" +
                "       -Fill All Possible Blank Cells: This uses all of the previous \n" +
                "           algorithms to try to resolve as many spaces on the board as possible. \n\n" +
                "Mode Buttons: (panel on the right side of the screen)\n" +
                "       1-9:  Allows you to select which number you would to insert.\n" +
                "       Eraser:  Allows you to remove a previous move that you made.\n" +
                "       ?:  Displays the candidate list for a space on the board.\n";
    }
}
