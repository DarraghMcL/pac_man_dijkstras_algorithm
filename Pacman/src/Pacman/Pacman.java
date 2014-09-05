//Darragh McLernon
package Pacman;
import java.awt.*;

public class Pacman extends Thread
{
    boolean isRunning = true;
    private int pacmanRow, pacmanCol;

    //creates the maze for pacman to navigate
    Maze maze;
    Cell[][]cells;
    private char direction='r';
    int livesLeft;

    //declaring the images for pacman animation
    int animationCounter = 0;
    String[] pacmanUp = {"Resources/upOpen.png", "Resources/upClose.png"};
    String[] pacmanDown = {"Resources/downOpen.png", "Resources/downClose.png"};
    String[] pacmanLeft = {"Resources/leftOpen.png", "Resources/leftClose.png"};
    String[] pacmanRight = {"Resources/rightOpen.png", "Resources/rightClose.png"};
    
    //constructor
    public Pacman(int initialRow, int initialColumn, Maze startMaze, int lives)
    {
	pacmanRow = initialRow;
	pacmanCol = initialColumn;
	maze = startMaze;
	livesLeft = lives;
	cells = maze.getCells();
    }
    
    //draw pacman
    public void drawPacman(Graphics g)
    {
	if(animationCounter==2)
	{
	    animationCounter=0;
	}
        //selecting a different image for pacmans direction
	    if(direction=='u')
	    {
		g.drawImage(Toolkit.getDefaultToolkit().getImage(pacmanUp[animationCounter]), pacmanRow*20, pacmanCol*20, maze);
	    }
	    else if(direction=='d')
	    {
		g.drawImage(Toolkit.getDefaultToolkit().getImage(pacmanDown[animationCounter]), pacmanRow*20, pacmanCol*20, maze);
	    }
	    else if(direction=='r')
	    {
		g.drawImage(Toolkit.getDefaultToolkit().getImage(pacmanRight[animationCounter]), pacmanRow*20, pacmanCol*20, maze);
	    }
	    else if(direction=='l')
	    {
		g.drawImage(Toolkit.getDefaultToolkit().getImage(pacmanLeft[animationCounter]), pacmanRow*20, pacmanCol*20, maze);
	    }
	animationCounter++;
	
    }

    //returns number of lives
    public int getLives()
    {
	return livesLeft;
    }

    //sets number of lives
    public void setLives(int lives)
    {
	livesLeft = lives;
    }
    
    //gets pacmans row
    protected int getRow()
    {
	return pacmanRow;
    }

    //get pacmans column
    protected int getCol()
    {
	return pacmanCol;
    }
    
    
    //set the row
    protected void setRow(int x)
    {
	pacmanRow = x;
    }
    
    //set the column
    protected void setCol(int y)
    {
	pacmanCol = y;
    }
    
    
    //set direcetion
    public void setDirection(char direction)
    {
	this.direction = direction;
    }

    //gets pacmans collision box
    public Rectangle getPacmanBounds()
    {
	return new Rectangle(getRow()*20,getCol()*20,20,20);
    }

    //run method
    public void run()
    {
	maze.repaint();
	try
	{
            //sleep at beginning to allow for music to play
	    Thread.sleep(5000);
	}
	catch (InterruptedException ex)
	{
	    ex.printStackTrace();
	}
        //main running loop
	while(isRunning)
	{
            //moves pacman based on direction selected
	    if (direction == 'u')
	    {
		if(isCellNavigable(pacmanCol-1,pacmanRow))
		    movePacman(0,-1);
	    }
	    else
		if (direction == 'd')
		{
		if(isCellNavigable(pacmanCol+1,pacmanRow))
		    movePacman(0,+1);
		}
		else
		    if (direction == 'l')
		    {
		if(isCellNavigable(pacmanCol,pacmanRow-1))
		    movePacman(-1,0);
		    }
		    else
			if (direction == 'r')
			{
		if(isCellNavigable(pacmanCol,pacmanRow+1))
		    movePacman(+1,0);
			}
	    
	    try
	    {
                //check for pause
		if(maze.isPaused)
		{
		    synchronized(this)
		    {
			while(maze.isPaused && isRunning)
			{
			    Thread.sleep(0);
			}
		    }
		}
	    }
	    catch(InterruptedException e)
	    {}

            //checks if pills are eaten
	    maze.eatsPowerPills();
	    maze.eatsPills();
	    maze.repaint();

            //sleep thread to control speed
	    try
	    {
		Thread.sleep(200);
	    }
	    catch(InterruptedException e)
	    {	
		System.err.println(e);
	    }
	}
    }   

    //moves pacman based on provided x and y coords
    public void movePacman(int x, int y)
    {
	pacmanRow = pacmanRow+ x;
	pacmanCol = pacmanCol+ y;
    }

    //determines if a cell is navigable
    public boolean isCellNavigable(int column, int row)
    {
	if(cells[column][row].getType()=='o'|| cells[column][row].getType()=='d' || cells[column][row].getType()=='p')return true;
	return false;
    }
}