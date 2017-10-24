import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by sean on 10/16/2017.
 */
public class SudokuSolverView
{
    private JFrame frame;
    private JPanel mainGrid;
    private SudokuButton[][] buttonArray;
    private JButton[] optionPanelButtons;
    private JPanel[] subGridArray;
    private JMenu fileMenu, helpMenu, hintMenu;
    private JLabel statusLabel;
    private Timer animationTimer;

    SudokuSolverView()
    {
        // Set cross-platform look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e) {
            // Just continue using default look and feel
        }

        //Create new JFrame
        frame = new JFrame("Sudoku Solver");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(560, 552);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        SetUpMenu(frame);

        //Create main grid
        mainGrid = new JPanel();
        mainGrid.setLayout(new GridLayout(3,3,4, 3));
        mainGrid.setPreferredSize(new Dimension(500,500));
        mainGrid.setBackground(Color.darkGray);

        SetCursor("1.png");

        subGridArray = new JPanel[9];
        buttonArray = new SudokuButton[9][9];
        optionPanelButtons = new JButton[11];

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

        //Create sub grids
        int rowIndex = 0;
        int colIndex = 0;

        for(int i = 0; i < 9; ++i)
        {
            subGridArray[i] = new JPanel();
            subGridArray[i].setLayout(new GridLayout(3,3, 1, 1));
            subGridArray[i].setBackground(Color.darkGray);

            for(int j=rowIndex; j<rowIndex+3; ++j) {
                for(int k=colIndex; k<colIndex+3; ++k) {
                    subGridArray[i].add(buttonArray[j][k]);
                }
            }

            colIndex += 3;
            if(colIndex > 8) {
                colIndex = 0;
                rowIndex += 3;
            }
        }

        for(JPanel subGrid : subGridArray)
            mainGrid.add(subGrid);

        for(int i = 0; i < 11; ++i)
        {
            if(i < 9) {
                optionPanelButtons[i] = new JButton(Integer.toString(i + 1));

            } else if(i == 9) {
                optionPanelButtons[i] = new JButton();
                optionPanelButtons[i].setActionCommand("x");
                try { //Add image to piece
                    Image img = ImageIO.read(getClass().getResource("Icons/eraser_white.png"));
                    optionPanelButtons[i].setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    System.err.println(ex + "Cannot find: Icons/eraser_white.png");
                }

            } else if(i == 10) {
                optionPanelButtons[i] = new JButton("?");
            }

            optionPanelButtons[i].setPreferredSize(new Dimension(40,40));
            optionPanelButtons[i].setBackground(Color.darkGray);
            optionPanelButtons[i].setForeground(Color.white);
            optionPanelButtons[i].setFont(new Font("Arial", Font.BOLD, 16));
            optionPanelButtons[i].setFocusPainted(false);
        }

        //Create optionPanel
        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new BorderLayout());
        optionPanel.setBackground(Color.darkGray);

        JPanel test = new JPanel();
        test.setLayout(new GridLayout(11,1, 0, 3));
        test.setBackground(Color.darkGray);

        Box verticalBox = Box.createVerticalBox();
        test.setPreferredSize(new Dimension(40,500));

        verticalBox.add(Box.createVerticalStrut(5));
        for(JButton button : optionPanelButtons) {
            test.add(button);
            verticalBox.add(Box.createVerticalGlue());
        }

        //horizontalBox.add(Box.createHorizontalStrut(13));
        //horizontalBox.add(verticalBox);
        //horizontalBox.add(Box.createHorizontalStrut(10));

        Box topPadding = Box.createVerticalBox();
        topPadding.setPreferredSize(new Dimension(60,10));

        Box bottomPadding = Box.createVerticalBox();
        bottomPadding.setPreferredSize(new Dimension(60,10));

        Box leftPadding = Box.createHorizontalBox();
        leftPadding.setPreferredSize(new Dimension(11,500));

        Box rightPadding = Box.createHorizontalBox();
        rightPadding.setPreferredSize(new Dimension(4,500));

        optionPanel.add(topPadding, BorderLayout.NORTH);
        optionPanel.add(bottomPadding, BorderLayout.SOUTH);
        optionPanel.add(rightPadding, BorderLayout.EAST);
        optionPanel.add(leftPadding, BorderLayout.WEST);
        optionPanel.add(test, BorderLayout.CENTER);

        //Create statusBar
        JPanel statusBar = new JPanel();
        statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusBar.setPreferredSize(new Dimension(0, 30));
        statusBar.setBackground(Color.darkGray);
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));

        statusLabel = new JLabel("\"File > Load Puzzle\" to begin...");
        statusLabel.setForeground(Color.white);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 11));
        statusBar.add(statusLabel);

        frame.add(mainGrid, BorderLayout.WEST);
        frame.add(optionPanel, BorderLayout.EAST);
        frame.add(statusBar, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public void UpdatePosition(Coordinates coords, int value)
    {
        try {
            buttonArray[coords.getRow()][coords.getCol()].setText(Integer.toString(value));
        }
        catch (ArrayIndexOutOfBoundsException exception) {
            //Don't update the board if given an invalid index
        }
    }

    public void ClearPosition(Coordinates coords)
    {
        buttonArray[coords.getRow()][coords.getCol()].setText("");
    }

    public void SetOriginalPiece(Coordinates coords, int value)
    {
        try {
            buttonArray[coords.getRow()][coords.getCol()].setText(Integer.toString(value));
            buttonArray[coords.getRow()][coords.getCol()].MakeEditable(false);
            buttonArray[coords.getRow()][coords.getCol()].setFont(new Font("Arial", Font.BOLD, 26));
        }
        catch (ArrayIndexOutOfBoundsException exception) {
            //Don't update the board if given an invalid index
        }
    }

    public JFrame GetFrame()
    {
        return frame;
    }

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

    public void AddGridButtonListener(ActionListener listener)
    {
        for(int i = 0; i < buttonArray.length; ++i)
            for(int j = 0; j < buttonArray.length; ++j)
                buttonArray[i][j].addActionListener(listener);
    }

    public void AddOptionButtonListener(ActionListener listener)
    {
        for(JButton button : optionPanelButtons)
            button.addActionListener(listener);
    }

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
    // Set up all of the components that make up the menuBar
    private void SetUpMenu(JFrame frame)
    {
        //Menu bar
        JMenuBar menuBar = new JMenuBar();

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

        // set up Edit menu
        helpMenu = new JMenu( "Help" );

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

        //Add items to the menuBar and frame
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        menuBar.add(hintMenu);
        frame.setJMenuBar(menuBar);
    }

    public void UpdateBoard(int[][] newBoard)
    {
        for(int i = 0; i < buttonArray.length; ++i)
            for(int j = 0; j < buttonArray[i].length; ++j)
            {
                if(newBoard[j][i] != 0)
                    buttonArray[i][j].setText(Integer.toString(newBoard[j][i]));
                else
                    buttonArray[i][j].setText("");
            }
    }

    public void ClearBoard()
    {
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

    public void SetCursor(String fileName) {

        Point clickLocation = new Point(16,16);

        if(fileName.equals("Default")) {
            mainGrid.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        } else {

            if(fileName.equals("eraser_blue.png"))
                clickLocation = new Point(3,27);

            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image cursorImage = toolkit.getImage(getClass().getResource("Icons/" + fileName));
            Cursor newCursor = toolkit.createCustomCursor(cursorImage, clickLocation, "Eraser");
            mainGrid.setCursor(newCursor);
        }
    }

    public void HighLightLocation(Coordinates coords)
    {
        //If a timer is running make sure not to start another one
        if(animationTimer != null && animationTimer.isRunning())
            return;

        animationTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color colorBackground = buttonArray[coords.getRow()][coords.getCol()].getBackground();
                Color colorForeground = buttonArray[coords.getRow()][coords.getCol()].getForeground();

                if(colorBackground.getRed() == 255) {
                    animationTimer.stop();
                    return;
                }

                buttonArray[coords.getRow()][coords.getCol()].setBackground(new Color(
                        colorBackground.getRed()+5,colorBackground.getGreen()+5,255));
                buttonArray[coords.getRow()][coords.getCol()].setForeground(new Color(
                        colorForeground.getRed()-3,colorForeground.getGreen()-3,
                        colorForeground.getBlue()-3));

            }
        });

        buttonArray[coords.getRow()][coords.getCol()].setBackground(new Color(0, 0, 255));
        buttonArray[coords.getRow()][coords.getCol()].setForeground(Color.white);
        animationTimer.start();
    }

}

