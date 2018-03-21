//Darragh McLernon
package Pacman;

import java.awt.*;

public class Pacman extends Thread {
    private boolean isRunning;
    private int pacmanRow, pacmanCol;

    //creates the maze for pacman to navigate
    Maze maze;
    private UndirectedGraph graph;
    private char direction = 'r';
    private int livesLeft;

    //declaring the images for pacman animation
    private int animationCounter = 0;
    private String[] pacmanUp = {"Resources/upOpen.png", "Resources/upClose.png"};
    private String[] pacmanDown = {"Resources/downOpen.png", "Resources/downClose.png"};
    private String[] pacmanLeft = {"Resources/leftOpen.png", "Resources/leftClose.png"};
    private String[] pacmanRight = {"Resources/rightOpen.png", "Resources/rightClose.png"};

    //constructor
    public Pacman(int initialColumn, int initialRow, Maze startMaze, int lives, UndirectedGraph graph) {
        pacmanCol = initialColumn;
        pacmanRow = initialRow;
        maze = startMaze;
        livesLeft = lives;
        isRunning = true;
        this.graph = graph;
    }

    public Cell getPacmanCell() {
        return graph.get_cell_by_coords(pacmanCol, pacmanRow);
    }

    //draw pacman
    public void drawPacman(Graphics g) {
        if (animationCounter == 2) {
            animationCounter = 0;
        }
        //selecting a different image for pacmans direction
        if (direction == 'u') {
            g.drawImage(Toolkit.getDefaultToolkit().getImage(pacmanUp[animationCounter]), pacmanCol * 20, pacmanRow * 20, maze);
        } else if (direction == 'd') {
            g.drawImage(Toolkit.getDefaultToolkit().getImage(pacmanDown[animationCounter]), pacmanCol * 20, pacmanRow * 20, maze);
        } else if (direction == 'r') {
            g.drawImage(Toolkit.getDefaultToolkit().getImage(pacmanRight[animationCounter]), pacmanCol * 20, pacmanRow * 20, maze);
        } else if (direction == 'l') {
            g.drawImage(Toolkit.getDefaultToolkit().getImage(pacmanLeft[animationCounter]), pacmanCol * 20, pacmanRow * 20, maze);
        }
        animationCounter++;

    }

    //returns number of lives
    public int getLives() {
        return livesLeft;
    }

    //sets number of lives
    public void setLives(int lives) {
        livesLeft = lives;
    }

    //gets pacmans row
    protected int getRow() {
        return pacmanRow;
    }

    //get pacmans column
    protected int getCol() {
        return pacmanCol;
    }

    //set the row
    protected void setRow(int y) {
        pacmanRow = y;
    }

    //set the column
    protected void setCol(int x) {
        pacmanCol = x;
    }


    //set direction
    public void setDirection(char direction) {
        this.direction = direction;
    }

    //gets pacmans collision box
    public Rectangle getPacmanBounds() {
        return new Rectangle(getCol() * 20, getRow() * 20, 20, 20);
    }

    //run method
    public void run() {
        maze.repaint();
        try {
            //sleep at beginning to allow for music to play
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        //main running loop
        while (isRunning) {
            //moves pacman based on direction selected
            if (direction == 'u') {
                if (graph.isCellNavigable(graph.get_cell_by_coords(getCol(), getRow()-1))) {
                    movePacman(0, -1);
                }
            } else if (direction == 'd') {
                if (graph.isCellNavigable(graph.get_cell_by_coords(getCol(), getRow()+1))) {
                    movePacman(0, +1);
                }
            } else if (direction == 'l') {
                if (graph.isCellNavigable(graph.get_cell_by_coords(getCol()-1, getRow()))) {
                    movePacman(-1, 0);
                }
            } else if (direction == 'r') {
                if (graph.isCellNavigable(graph.get_cell_by_coords(getCol()+1, getRow()))) {
                    movePacman(+1, 0);
                }
            }

            try {
                //check for pause
                if (maze.isPaused) {
                    synchronized (this) {
                        while (maze.isPaused && isRunning) {
                            Thread.sleep(0);
                        }
                    }
                }
            } catch (InterruptedException e) {
            }

            //checks if pills are eaten
            maze.eatsPowerPills();
            maze.eatsPills();
            maze.repaint();

            //sleep thread to control speed
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
    }

    //moves pacman based on provided x and y coords
    private void movePacman(int column, int row) {
        pacmanRow = pacmanRow + row;
        pacmanCol = pacmanCol + column;
        System.out.println(this.getPacmanCell());
    }
}