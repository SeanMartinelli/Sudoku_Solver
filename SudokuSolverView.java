import javax.swing.*;
import java.awt.*;

/**
 * Created by sean on 10/16/2017.
 */
public class SudokuSolverView
{

    JButton[] buttonArray;
    JButton[] optionPanelButtons;
    JPanel[] subGridArray;

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
        JFrame frame = new JFrame("Sudoku Solver");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(550, 500);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        //Create main grid
        JPanel mainGrid = new JPanel();
        mainGrid.setLayout(new GridLayout(3,3,4, 3));
        mainGrid.setPreferredSize(new Dimension(500,500));
        mainGrid.setBackground(Color.darkGray);

        subGridArray = new JPanel[9];
        buttonArray = new JButton[81];
        optionPanelButtons = new JButton[11];

        for(int i = 0; i < 81; ++i) {
            buttonArray[i] = new JButton(Integer.toString((i % 9) + 1));
            buttonArray[i].setBackground(Color.white);
            buttonArray[i].setForeground(Color.darkGray);
            buttonArray[i].setFont(new Font("Arial", Font.BOLD, 22));
        }

        for(int i = 0; i < 9; ++i)
        {
            subGridArray[i] = new JPanel();
            subGridArray[i].setLayout(new GridLayout(3,3, 1, 1));
            subGridArray[i].setBackground(Color.darkGray);

            for(int j = i*9; j < i*9+9; ++j)
                subGridArray[i].add(buttonArray[j]);
        }

        for(JPanel subGrid : subGridArray)
            mainGrid.add(subGrid);


        for(int i = 0; i < 11; ++i)
        {
            if(i < 9)
                optionPanelButtons[i] = new JButton(Integer.toString(i+1));
            else if(i == 9)
                optionPanelButtons[i] = new JButton("x");
            else if(i == 10)
                optionPanelButtons[i] = new JButton("?");

            optionPanelButtons[i].setPreferredSize(new Dimension(40,40));
            optionPanelButtons[i].setBackground(Color.darkGray);
            optionPanelButtons[i].setForeground(Color.white);
            //optionPanelButtons[i].setBorder(new LineBorder(Color.WHITE));
            optionPanelButtons[i].setFont(new Font("Arial", Font.PLAIN, 11));
        }

        //Create optionPanel
        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new BorderLayout());
        optionPanel.setBackground(Color.darkGray);

        Box horizontalBox = Box.createHorizontalBox();
        Box verticalBox = Box.createVerticalBox();
        horizontalBox.setPreferredSize(new Dimension(50,500));

        verticalBox.add(Box.createVerticalGlue());
        for(JButton button : optionPanelButtons) {
            verticalBox.add(button);
            verticalBox.add(Box.createVerticalGlue());
        }

        horizontalBox.add(Box.createHorizontalGlue());
        horizontalBox.add(verticalBox);
        horizontalBox.add(Box.createHorizontalGlue());

        optionPanel.add(horizontalBox, BorderLayout.CENTER);

        frame.add(mainGrid, BorderLayout.WEST);
        frame.add(optionPanel, BorderLayout.EAST);

        frame.setVisible(true);

    }
}
