package Pacman;
import java.awt.*;
import javax.swing.*;

class PacmanGUI extends JFrame
{
    Maze maze;
    JFrame gameBoard;
    public static void main(String args[])
    {
	final PacmanGUI UI = new PacmanGUI();
	Runnable r;
	r = new Runnable()
	{
	    public void run()
	    {
		UI.startGUI();
	    }
	};
	SwingUtilities.invokeLater(r);
    }
    
    
    public  void startGUI()
    {
	gameBoard = new JFrame("Assignment - Pacman");
	gameBoard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	gameBoard.setLayout(new BorderLayout());
	// Reference to the maze
	maze = new Maze();
	
	// Reference to the cells that make up the maze
	Cell[][]cells = maze.getCells();;
	
	//add the maze to the game board
	gameBoard.add(maze, BorderLayout.CENTER);
	
	// Display the game window on screen
	gameBoard.pack();
	gameBoard.setVisible(true);
    }
    

    
    
}




