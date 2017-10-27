//
// Michal Bochnak, Netid: mbochn2
// Sean Martinelli, Netid: smarti58
//
// CS 342 Project #3 - Sudoku Solver
// 10/26/2017
// UIC, Pat Troy
//
// SudokuSolverView.java
//

//
// This class is the interface between the user and the program.
// It is responsible for displaying all of the information to the user
// and receiving user input.
//

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SudokuSolverView
{
    private JFrame frame;
    private JPanel mainGrid;
    private SudokuButton[][] buttonArray;
    private JButton[] optionPanelButtons;
    private JMenu fileMenu, helpMenu, hintMenu;
    private JLabel statusLabel;
    private Timer animationTimer;

    SudokuSolverView()
    {
        SetCrossPlatformLookAndFeel();

        //Create new JFrame
        frame = new JFrame("Sudoku Solver");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(560, 552);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        //Set up the components that make up the JFrame
        SetUpMenu();
        SetUpMainGrid();
        SetUpButtonArray();
        SetUpSubGridArray();
        SetUpOptionPanelButtons();
        SetUpOptionPanel();
        SetUpStatusBar();

        //Set initial cursor to 1
        SetCursor("1.png");

        frame.setVisible(true);
    }


    //
    // Update and highlight a single user specified position on the board
    //
    public void UpdatePosition(Coordinates coords, int value)
    {
        try {
            buttonArray[coords.getRow()][coords.getCol()].setText(Integer.toString(value));
            HighLightLocation(coords);
        }
        catch (ArrayIndexOutOfBoundsException exception) {
            //Don't update the board if given an invalid index
        }
    }

    //
    // Clear the specified position on the board
    //
    public void ClearPosition(Coordinates coords)
    {
        buttonArray[coords.getRow()][coords.getCol()].setText("");
    }

    //
    // Set a piece specified by a puzzle file.  The piece will be displayed in bold font
    //
    public void SetOriginalPiece(Coordinates coords, int value)
    {
        try {
            buttonArray[coords.getRow()][coords.getCol()].setText(Integer.toString(value));
            buttonArray[coords.getRow()][coords.getCol()].MakeEditable(false);
            buttonArray[coords.getRow()][coords.getCol()].setFont(
                    new Font("Arial", Font.BOLD, 26));
        }
        catch (ArrayIndexOutOfBoundsException exception) {
            //Don't update the board if given an invalid index
        }
    }

    public JFrame GetFrame()
    {
        return frame;
    }

    //
    // Highlights the specified mode in blue
    //
    public void SelectMode(int mode)
    {
        try {
            //Reset all button background colors
            for (JButton button : optionPanelButtons)
                button.setBackground(Color.darkGray);

            //Set new selected button background color
            optionPanelButtons[mode - 1].setBackground(Color.blue);
        }
        catch (ArrayIndexOutOfBoundsException exception) {
            //Don't do anything if invalid mode was supplied
        }
    }

    public void SetStatusLabel(String message)
    {
        statusLabel.setText(message);
    }

    //
    // Add an action listener to each grid button
    //
    public void AddGridButtonListener(ActionListener listener)
    {
        for(int i = 0; i < buttonArray.length; ++i)
            for(int j = 0; j < buttonArray.length; ++j)
                buttonArray[i][j].addActionListener(listener);
    }

    //
    // Add an action listener to each option button
    //
    public void AddOptionButtonListener(ActionListener listener)
    {
        for(JButton button : optionPanelButtons)
            button.addActionListener(listener);
    }

    //
    // Add an action listener to each menu item
    //
    public void AddMenuListener(ActionListener listener)
    {
        //Add action listener to each file menu item
        for(int i=0; i<fileMenu.getItemCount(); ++i)
            fileMenu.getItem(i).addActionListener(listener);

        //Add action listener to each file menu item
        for(int i=0; i<helpMenu.getItemCount(); ++i)
            helpMenu.getItem(i).addActionListener(listener);

        //Add action listener to each file menu item
        for(int i=0; i<hintMenu.getItemCount(); ++i)
            hintMenu.getItem(i).addActionListener(listener);
    }

    //
    // Update the text at each location on the board
    //
    public void UpdateBoard(int[][] newBoard)
    {
        //Loop through all positions on board
        for(int i = 0; i < buttonArray.length; ++i)
            for(int j = 0; j < buttonArray[i].length; ++j)
            {
                //Set position (0s should be blank on the board)
                if(newBoard[j][i] != 0)
                    buttonArray[i][j].setText(Integer.toString(newBoard[j][i]));
                else
                    buttonArray[i][j].setText("");
            }
    }

    //
    // Clear and reset the font for the entire board
    //
    public void ClearBoard()
    {
        //Loop through all positions on teh board
        for(int i = 0; i < buttonArray.length; ++i)
            for(int j = 0; j < buttonArray[i].length; ++j)
            {
                buttonArray[i][j].setText("");
                buttonArray[i][j].MakeEditable(true);
                buttonArray[i][j].setFont(new Font("Arial", Font.PLAIN, 24));
            }
    }

    public void DisplayMessage(String message, String title)
    {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.PLAIN_MESSAGE);
    }

    //
    // Set the cursor to an icon specified by the filename provided
    //
    public void SetCursor(String fileName) {

        Point clickLocation = new Point(16,16);

        //Set the cursor to the image at the specified file name
        if(fileName.equals("Default")) {
            mainGrid.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        } else {

            //Change click location for eraser
            if(fileName.equals("eraser_blue.png"))
                clickLocation = new Point(3,27);

            //Set Icon
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image cursorImage = toolkit.getImage(getClass().getResource("Icons/" + fileName));
            Cursor newCursor = toolkit.createCustomCursor(cursorImage, clickLocation, "Eraser");
            mainGrid.setCursor(newCursor);
        }
    }

    //
    // Set up all of the components that make up the menuBar
    //
    private void SetUpMenu()
    {
        //Menu bar
        JMenuBar menuBar = new JMenuBar();

        //Create Menus
        CreateFileMenu(menuBar);
        CreateHelpMenu(menuBar);
        CreateHintMenu(menuBar);

        frame.setJMenuBar(menuBar);
    }

    //
    // Set up the File menu and all of the menu items that
    // go with it then add it to menuBar
    //
    private void CreateFileMenu(JMenuBar menuBar)
    {
        // set up File menu
        fileMenu = new JMenu( "File" );
        fileMenu.setMnemonic( 'F' );

        //set up New Game File menu item
        JMenuItem loadPuzzleItem = new JMenuItem("Load Puzzle");
        loadPuzzleItem.setMnemonic('L');
        fileMenu.add(loadPuzzleItem);

        //set up New Game File menu item
        JMenuItem savePuzzleItem = new JMenuItem("Save Puzzle");
        savePuzzleItem.setMnemonic('S');
        fileMenu.add(savePuzzleItem);

        // set up About File menu item
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.setMnemonic('Q');
        fileMenu.add(quitItem);

        menuBar.add(fileMenu);
    }

    //
    // Set up the Help menu and all of the menu items that
    // go with it then add it to menuBar
    //
    private void CreateHelpMenu(JMenuBar menuBar)
    {
        // set up Help menu
        helpMenu = new JMenu( "Help" );

        // set up howToPlay menu item
        JMenuItem howToPlayItem = new JMenuItem("How To Play");
        helpMenu.add(howToPlayItem);

        // set up instructions menu item
        JMenuItem instructionsItem = new JMenuItem("Interface Help");
        helpMenu.add(instructionsItem);

        // set up authors menu item
        JMenuItem authorsItem = new JMenuItem("Authors");
        helpMenu.add(authorsItem);

        menuBar.add(helpMenu);
    }

    //
    // Set up the Hint menu and all of the menu items that
    // go with it then add it to menuBar
    //
    private void CreateHintMenu(JMenuBar menuBar)
    {
        // set up Hint menu
        hintMenu = new JMenu( "Hint" );
        hintMenu.setMnemonic('H');

        JCheckBoxMenuItem checkOnFillItem = new JCheckBoxMenuItem("Check On Fill", true);
        hintMenu.add(checkOnFillItem);

        //set up Single Algorithm menu item
        JMenuItem singleAlgoItem = new JMenuItem("Single Algorithm");
        hintMenu.add(singleAlgoItem);

        //set up Hidden Single menu item
        JMenuItem hiddenSingleAlgoItem = new JMenuItem("Hidden Single Algorithm");
        hintMenu.add(hiddenSingleAlgoItem);

        //set up New Game File menu item
        JMenuItem lockedCandidateAlgoItem = new JMenuItem("Locked Candidate Algorithm");
        hintMenu.add(lockedCandidateAlgoItem);

        //set up New Game File menu item
        JMenuItem nakedPairsAlgoItem = new JMenuItem("Naked Pairs Algorithm");
        hintMenu.add(nakedPairsAlgoItem);

        //set up New Game File menu item
        JMenuItem fillAllPossibleItem = new JMenuItem("Fill All Possible Blank Cells");
        hintMenu.add(fillAllPossibleItem);

        menuBar.add(hintMenu);
    }

    //
    // Set up the main Sudoku grid and add it to the frame.
    //
    private void SetUpMainGrid()
    {
        //Create main grid
        mainGrid = new JPanel();
        mainGrid.setLayout(new GridLayout(3,3,4, 3));
        mainGrid.setPreferredSize(new Dimension(500,500));
        mainGrid.setBackground(Color.darkGray);

        frame.add(mainGrid, BorderLayout.WEST);
    }

    //
    // Create the sub-grids and add them to the main grid.
    // Then add the buttons from the buttonArray to each sub-grid.
    //
    private void SetUpSubGridArray()
    {
        JPanel[] subGridArray = new JPanel[9];

        //Create sub grids
        int rowIndex = 0;
        int colIndex = 0;

        //Loop though each sub-grid
        for(int i = 0; i < 9; ++i)
        {
            subGridArray[i] = new JPanel();
            subGridArray[i].setLayout(new GridLayout(3,3, 1, 1));
            subGridArray[i].setBackground(Color.darkGray);

            //Loop through each space in the current sub-grid
            for(int j=rowIndex; j<rowIndex+3; ++j)
                for(int k=colIndex; k<colIndex+3; ++k)
                    subGridArray[i].add(buttonArray[j][k]);

            //Advance the col and row index to the correct location for the next sub-grid
            colIndex += 3;
            if(colIndex > 8) {
                colIndex = 0;
                rowIndex += 3;
            }
        }

        //Add the sub-grids to the main grid
        for(JPanel subGrid : subGridArray)
            mainGrid.add(subGrid);
    }

    //
    // Create the buttons that will be used for the Sudoku board
    //
    private void SetUpButtonArray()
    {
        buttonArray = new SudokuButton[9][9];

        for(int i = 0; i < 9; ++i) {
            for(int j = 0; j < 9; ++j)
            {
                buttonArray[i][j] = new SudokuButton(i, j);
                buttonArray[i][j].setBackground(Color.white);
                buttonArray[i][j].setForeground(Color.darkGray);
                buttonArray[i][j].setFont(new Font("Arial", Font.PLAIN, 24));
                buttonArray[i][j].setFocusPainted(false);
            }
        }
    }

    //
    // Create the buttons that will be used for the option panel.
    //
    private void SetUpOptionPanelButtons()
    {
        optionPanelButtons = new JButton[11];

        //Make 11 buttons
        for(int i = 0; i < 11; ++i)
        {
            if(i < 9) { //Number buttons
                optionPanelButtons[i] = new JButton(Integer.toString(i + 1));

            } else if(i == 9) {  //Eraser button
                optionPanelButtons[i] = new JButton();
                optionPanelButtons[i].setActionCommand("x");
                try { //Add image to piece
                    Image img = ImageIO.read(getClass().getResource("Icons/eraser_white.png"));
                    optionPanelButtons[i].setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    System.err.println(ex + "Cannot find: Icons/eraser_white.png");
                }

            } else if(i == 10) { //Candidate list button
                optionPanelButtons[i] = new JButton("?");
            }

            //Set look of buttons
            optionPanelButtons[i].setPreferredSize(new Dimension(40,40));
            optionPanelButtons[i].setBackground(Color.darkGray);
            optionPanelButtons[i].setForeground(Color.white);
            optionPanelButtons[i].setFont(new Font("Arial", Font.BOLD, 16));
            optionPanelButtons[i].setFocusPainted(false);
        }
    }

    //
    // Create the option panel that will allow the user to select the number
    // they would like to insert.  This menu also contains an erase and help button.
    //
    private void SetUpOptionPanel()
    {
        //Create optionPanel
        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new BorderLayout());
        optionPanel.setBackground(Color.darkGray);

        //Create grid of option panel buttons
        JPanel OptionButtonGrid = new JPanel();
        OptionButtonGrid.setLayout(new GridLayout(11,1, 0, 3));
        OptionButtonGrid.setBackground(Color.darkGray);
        OptionButtonGrid.setPreferredSize(new Dimension(40,500));

        //Add buttons
        for(JButton button : optionPanelButtons) {
            OptionButtonGrid.add(button);
        }

        //Create padding to position grid
        Box topPadding = Box.createVerticalBox();
        topPadding.setPreferredSize(new Dimension(60,10));

        Box bottomPadding = Box.createVerticalBox();
        bottomPadding.setPreferredSize(new Dimension(60,10));

        Box leftPadding = Box.createHorizontalBox();
        leftPadding.setPreferredSize(new Dimension(11,500));

        Box rightPadding = Box.createHorizontalBox();
        rightPadding.setPreferredSize(new Dimension(4,500));

        //Add components to the optionPanel
        optionPanel.add(topPadding, BorderLayout.NORTH);
        optionPanel.add(bottomPadding, BorderLayout.SOUTH);
        optionPanel.add(rightPadding, BorderLayout.EAST);
        optionPanel.add(leftPadding, BorderLayout.WEST);
        optionPanel.add(OptionButtonGrid, BorderLayout.CENTER);

        frame.add(optionPanel, BorderLayout.EAST);
    }

    //
    // Set up the status bar at the bottom of the page.  This will allow
    // messages to be displayed to the user.
    //
    private void SetUpStatusBar()
    {
        //Create statusBar
        JPanel statusBar = new JPanel();
        statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusBar.setPreferredSize(new Dimension(0, 30));
        statusBar.setBackground(Color.darkGray);
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));

        //Create statusLabel
        statusLabel = new JLabel("\"File > Load Puzzle\" to begin...");
        statusLabel.setForeground(Color.white);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 11));
        statusBar.add(statusLabel);

        frame.add(statusBar, BorderLayout.SOUTH);
    }


    private void SetCrossPlatformLookAndFeel()
    {
        // Set cross-platform look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e) {
            // Just continue using default look and feel
        }
    }

    //
    // Highlight the specified location on the board. The highlight fades back
    // to the normal look of a button over a short period of time.
    //
    private void HighLightLocation(final Coordinates coords)
    {
        //If a timer is running make sure not to start another one
        if(animationTimer != null && animationTimer.isRunning())
            return;

        //Create fade timer
        animationTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Get current background and foreground colors
                Color colorBackground = buttonArray[coords.getRow()][coords.getCol()].getBackground();
                Color colorForeground = buttonArray[coords.getRow()][coords.getCol()].getForeground();

                //Check if the timer should stop
                if(colorBackground.getRed() == 255) {
                    animationTimer.stop();
                    buttonArray[coords.getRow()][coords.getCol()].setForeground(Color.darkGray);
                    return;
                }

                //Update colors
                buttonArray[coords.getRow()][coords.getCol()].setBackground(new Color(
                        colorBackground.getRed()+5,colorBackground.getGreen()+5,255));
                buttonArray[coords.getRow()][coords.getCol()].setForeground(new Color(
                        colorForeground.getRed()-3,colorForeground.getGreen()-3,
                        colorForeground.getBlue()-3));
            }
        });

        //Set initial color
        buttonArray[coords.getRow()][coords.getCol()].setBackground(new Color(0, 0, 255));
        buttonArray[coords.getRow()][coords.getCol()].setForeground(Color.white);
        animationTimer.start();
    }
}

