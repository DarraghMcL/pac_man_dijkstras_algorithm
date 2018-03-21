//Darragh McLernon
package Pacman;

import java.applet.Applet;
import java.awt.*;
import java.applet.AudioClip;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ghost extends Thread {

    private boolean isRunning = true;
    private int ghostRow, ghostCol;
    private Maze maze;
    private UndirectedGraph graph;
    private char direction = 'l';
    private boolean vulnerable = false;
    private int ghostAnimationCounter = 0;
    //array of character images
    private String[] ghostScaredAnimation = {"Resources/ghostScared_0.png", "Resources/ghostScared_1.png"};
    private String[] ghostEaten = {"Resources/eyes0.png", "Resources/eyes1.png"};
    private String[][] ghostAnimations = {{"Resources/ghost0_0.png", "Resources/ghost1_0.png", "Resources/ghost2_0.png", "Resources/ghost3_0.png"}, {"Resources/ghost0_1.png", "Resources/ghost1_1.png", "Resources/ghost2_1.png", "Resources/ghost3_1.png"}};
    private int ghostNumber;
    //ghost behaviour triggers
    private boolean escaped = false;
    private boolean searching = false;
    private boolean eaten = false;
    //timers
    private int escapeTimer;
    private int vulCounter = 0;
    //ghost search parameter ranges
    private int[] searchPara = {200, 300, 700};
    private int difficulty;
    //obtaining sound file for ghost alerts
    private AudioClip alertPlayer;
    private File alertFile = new File("Resources/alert.wav");
    private URL alertURL;
    private int alertCounter = 0;

    //ghost constructor
    Ghost(int initialCol, int initialRow, Maze startMaze, int Number, int timer, int difc, UndirectedGraph graph) {
        //applies the difficulty
        difficulty = difc;
        try {
            //getting sound file
            alertURL = alertFile.toURL();
        } catch (MalformedURLException ex) {
            Logger.getLogger(Ghost.class.getName()).log(Level.SEVERE, null, ex);
        }
        alertPlayer = Applet.newAudioClip(alertURL);
        escapeTimer = timer;
        ghostRow = initialRow;
        ghostCol = initialCol;
        maze = startMaze;
        ghostNumber = Number;
        this.graph = graph;
    }

    //returns the node that the ghosts is currently standing on by checking the x and y values of the cell he is on
    private Cell getGhostCell() {
        return graph.get_cell_by_coords(ghostCol, ghostRow);
    }

    //TODO look at this
    private void ghostChase() {
        //if next node is to the right change direction to 'r'
        if ((getCol() - graph.getShortestDistance(getGhostCell(), maze.getPacman().getPacmanCell()).getColumn()) < 0) {
            setDirection('r');

        } else if ((getCol() - graph.getShortestDistance(getGhostCell(), maze.getPacman().getPacmanCell()).getColumn()) > 0) {
            setDirection('l');

        } else if ((getRow() - graph.getShortestDistance(getGhostCell(), maze.getPacman().getPacmanCell()).getRow()) < 0) {
            setDirection('d');

        } else if ((getRow() - graph.getShortestDistance(getGhostCell(), maze.getPacman().getPacmanCell()).getRow()) > 0) {
            setDirection('u');
        }
    }

    //applies shortest path algorithm to get the ghost back in the box
    private void returnToBox() {
        if ((getCol() - graph.getShortestDistance(getGhostCell(), graph.get_cell_by_coords(maze.ghostInitialCol, maze.ghostInitialRow)).getColumn()) < 0) {
            setDirection('r');

        } else if ((getCol() - graph.getShortestDistance(getGhostCell(), graph.get_cell_by_coords(maze.ghostInitialCol, maze.ghostInitialRow)).getColumn()) > 0) {
            setDirection('l');

        } else if ((getRow() - graph.getShortestDistance(getGhostCell(), graph.get_cell_by_coords(maze.ghostInitialCol, maze.ghostInitialRow)).getRow()) < 0) {
            setDirection('d');

        } else if ((getRow() - graph.getShortestDistance(getGhostCell(), graph.get_cell_by_coords(maze.ghostInitialCol, maze.ghostInitialRow)).getRow()) > 0) {
            setDirection('u');
        }
    }

    //returns true if pacman is within the ghost search parameter
    private boolean searching(Rectangle rect) {
        return (getSearchParameter().intersects(rect));
    }

    //ghost draw class
    public void drawGhost(Graphics g) {
        if (ghostAnimationCounter == 2) {
            ghostAnimationCounter = 0;
        }
        //if the ghost has been eaten only his eyes are displayed
        if (eaten) {
            g.drawImage(Toolkit.getDefaultToolkit().getImage(ghostEaten[ghostAnimationCounter]), ghostCol * 20, ghostRow * 20, maze);
        }
        //if the ghost is vulnerable his picture changes
        if (vulnerable && !eaten) {
            g.drawImage(Toolkit.getDefaultToolkit().getImage(ghostScaredAnimation[ghostAnimationCounter]), ghostCol * 20, ghostRow * 20, maze);
        }
        //displays an alert if pacman moves into ghost search parameter
        if (searching && alertCounter < 10) {
            g.drawImage(Toolkit.getDefaultToolkit().getImage("Resources/sighted.png"), ghostCol * 20, (ghostRow * 20) - 50, maze);
            alertCounter++;
        }
        //usual ghost animation
        if (!vulnerable && !eaten) {
            g.drawImage(Toolkit.getDefaultToolkit().getImage(ghostAnimations[ghostAnimationCounter][ghostNumber]), ghostCol * 20, (ghostRow * 20), maze);
        }

        //count for animating ghosts
        ghostAnimationCounter++;
    }

    //sets the ghost to vulnerable state
    public void ghostIsVulnerable() {
        vulCounter = 0;
        vulnerable = true;
    }

    public boolean isVulnerable() {
        return vulnerable;
    }

    protected int getRow() {
        return ghostRow;
    }

    protected int getCol() {
        return ghostCol;
    }

    protected void setRow(int y) {
        ghostRow = y;
    }

    protected void setCol(int x) {
        ghostCol = x;
    }

    //moves the ghosts by the specified x and y amounts
    private void moveGhost(int column, int row) {
        ghostRow = ghostRow + row;
        ghostCol = ghostCol + column;
    }

    //gets the ghost collision box
    public Rectangle getGhostBounds() {
        return new Rectangle(getCol() * 20, getRow() * 20, 20, 20);
    }

    //gets the ghost search parameter
    //parameter is increased depending on the difficulty selected
    private Rectangle getSearchParameter() {
        return new Rectangle((getCol() * 20) - searchPara[difficulty] / 2, (getRow() * 20) - searchPara[difficulty] / 2, searchPara[difficulty], searchPara[difficulty]);
    }

    //determines if the cell can be navigated
    private boolean isCellNavigable(int column, int row) {

        switch (graph.get_cell_by_coords(column,row).getType()) {
            case 'o':
                return true;
            case 'd':
                return true;
            case 'p':
                return true;
            case 'g':
                return true;
            case 'e':
                return true;
            default:
                return false;
        }
    }

    //determines if a cell is navigable after the ghost has left the box
    private boolean isCellNavigableOutOfBox(int column, int row) {

        switch (graph.get_cell_by_coords(column,row).getType()) {
            case 'o':
                return true;
            case 'd':
                return true;
            case 'p':
                return true;
            default:
                return false;
        }
    }

    //determines the navigable cells for when the ghost is out of the box
    private boolean isDirectionNavigable(char direction) {
        if (direction == 'u' && isCellNavigable(getCol(), getRow()-1)) {
            return true;

        } else if (direction == 'd' && isCellNavigable(getCol(), getRow()+1)) {
            return true;

        } else if (direction == 'l' && isCellNavigable(getCol()-1, getRow())) {
            return true;

        } else if (direction == 'r' && isCellNavigable(getCol()+1, getRow())) {
            return true;
        }
        return false;
    }

    //sets the ghosts direction
    private void setDirection(char direction) {
        this.direction = direction;
    }

    //makes the ghosts move based on the current direction
    private void ghostMovement() {
        if (direction == 'u' && isCellNavigable(getCol(), getRow()-1)) {
            moveGhost(0, -1);


        } else if (direction == 'd' && isCellNavigable(getCol(), getRow()+1)) {
            moveGhost(0, +1);


        } else if (direction == 'l' && isCellNavigable(getCol()-1, getRow())) {
            moveGhost(-1, 0);


        } else if (direction == 'r' && isCellNavigable(getCol()+1, getRow())) {
            moveGhost(+1, 0);
        }
    }

    //ghost movements for when they're out of the box
    private void ghostMovementOutOfBox() {
        if (direction == 'u' && isCellNavigableOutOfBox(getCol(), getRow()-1)) {
            moveGhost(0, -1);


        } else if (direction == 'd' && isCellNavigableOutOfBox(getCol(), getRow()+1)) {
            moveGhost(0, +1);


        } else if (direction == 'l' && isCellNavigableOutOfBox(getCol()-1, getRow())) {
            moveGhost(-1, 0);


        } else if (direction == 'r' && isCellNavigableOutOfBox(getCol()+1, getRow())) {
            moveGhost(+1, 0);
        }
    }

    //small escaping animation
    private void escapeArtist() throws InterruptedException {
        //each ghost will wait first based on their escape timer
        Thread.sleep(escapeTimer);


        for (int a = 0; a
                < 4; a++) {
            moveGhost(-1, 0);
            maze.repaint();
            Thread.sleep(200);
        }
        for (int b = 0; b
                < 4; b++) {
            moveGhost(+1, 0);
            maze.repaint();
            Thread.sleep(200);
        }
        for (int a = 0; a
                < 4; a++) {
            moveGhost(-1, 0);
            maze.repaint();
            Thread.sleep(200);
        }
        for (int b = 0; b
                < 4; b++) {
            moveGhost(+1, 0);
            maze.repaint();
            Thread.sleep(200);
        }
        for (int a = 0; a
                < 2; a++) {
            moveGhost(-1, 0);
            maze.repaint();
            Thread.sleep(200);
        }
        for (int c = 0; c
                < 2; c++) {
            moveGhost(0, -1);
            maze.repaint();
            Thread.sleep(200);
        }
        escaped = true;
    }
    //dumb ghost

    private void ghostBrains() {
        //declaring random and direction holders
        Random rand = new Random();
        int arraySize = 0;
        char[] availableDir;
        char[] tempArray = new char[4];

        //if the ghost is moving up add the available directions to temp array
        //opposite direction (down) is not added to prevent the ghosts from
        //turning around when he hits a junction
        if (direction == 'u') {
            if (isDirectionNavigable('u')) {
                tempArray[arraySize] = 'u';
                arraySize++;
            }
            if (isDirectionNavigable('l')) {
                tempArray[arraySize] = 'l';
                arraySize++;
            }
            if (isDirectionNavigable('r')) {
                tempArray[arraySize] = 'r';
                arraySize++;
            }
            //if a ghost meets a dead end with no other available directions, he may then turn around
            if (!isDirectionNavigable('u') && !isDirectionNavigable('l') && !isDirectionNavigable('r')) {
                direction = 'd';
            } else {
                availableDir = new char[arraySize];
                for (int i = 0; i
                        < arraySize; i++) {
                    availableDir[i] = tempArray[i];
                }
                direction = availableDir[rand.nextInt(arraySize)];
            }

            //repeating previous statement for each direction
        } else if (direction == 'd') {
            if (isDirectionNavigable('d')) {
                tempArray[arraySize] = 'd';
                arraySize++;
            }
            if (isDirectionNavigable('l')) {
                tempArray[arraySize] = 'l';
                arraySize++;
            }
            if (isDirectionNavigable('r')) {
                tempArray[arraySize] = 'r';
                arraySize++;
            }
            if (!isDirectionNavigable('d') && !isDirectionNavigable('l') && !isDirectionNavigable('r')) {
                direction = 'u';
            } else {
                availableDir = new char[arraySize];
                for (int i = 0; i
                        < arraySize; i++) {
                    availableDir[i] = tempArray[i];
                }
                direction = availableDir[rand.nextInt(arraySize)];
            }
        } else if (direction == 'l') {
            if (isDirectionNavigable('u')) {
                tempArray[arraySize] = 'u';
                arraySize++;
            }
            if (isDirectionNavigable('d')) {
                tempArray[arraySize] = 'd';
                arraySize++;
            }
            if (isDirectionNavigable('l')) {
                tempArray[arraySize] = 'l';
                arraySize++;
            }
            if (!isDirectionNavigable('u') && !isDirectionNavigable('l') && !isDirectionNavigable('d')) {
                direction = 'r';
            } else {
                availableDir = new char[arraySize];
                for (int i = 0; i
                        < arraySize; i++) {
                    availableDir[i] = tempArray[i];
                }
                direction = availableDir[rand.nextInt(arraySize)];
            }
        } else if (direction == 'r') {
            if (isDirectionNavigable('u')) {
                tempArray[arraySize] = 'u';
                arraySize++;
            }
            if (isDirectionNavigable('d')) {
                tempArray[arraySize] = 'd';
                arraySize++;
            }
            if (isDirectionNavigable('r')) {
                tempArray[arraySize] = 'r';
                arraySize++;
            }
            if (isDirectionNavigable('u') && isDirectionNavigable('d') && isDirectionNavigable('r')) {
                direction = 'l';
            } else {
                availableDir = new char[arraySize];
                for (int i = 0; i
                        < arraySize; i++) {
                    availableDir[i] = tempArray[i];
                }
                direction = availableDir[rand.nextInt(arraySize)];
            }
        }
    }

    //run method
    public void run() {
        //five second delay at the beginning to allow for music to play
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        //main running loop
        while (isRunning) {
            //behaviour to get ghost out of box
            while (!escaped && getRow() == maze.ghostInitialRow && getCol() == maze.ghostInitialCol) {
                try {
                    escapeArtist();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            //behaviour when ghost is eaten
            while (eaten) {
                //get shortest path to back to ghost box
                returnToBox();
                ghostMovement();
                maze.repaint();

                //when they're back in the box break out of the loop and start the escape animation
                if (getRow() == 23 && getCol() == 9) {
                    eaten = false;
                    vulnerable = false;
                    try {
                        escapeArtist();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Ghost.class.getName()).log(Level.SEVERE, "Failed to escape", ex);
                    }
                }
                //checks if paused
                try {
                    if (maze.isPaused) {
                        synchronized (this) {
                            while (maze.isPaused && isRunning) {
                                Thread.sleep(0);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                }

                //sleeps thread to control ghost speed
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {

                    System.err.println(e);
                }

            }

            //behaviour when pacman is within ghost search parameter
            while (escaped && !vulnerable && searching && !eaten) {
                ghostChase();
                ghostMovementOutOfBox();
                maze.repaint();
                if (!searching(maze.getPacman().getPacmanBounds())) {
                    searching = false;
                }

                //checks for pause
                try {
                    if (maze.isPaused) {
                        synchronized (this) {
                            while (maze.isPaused && isRunning) {
                                Thread.sleep(0);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println(e);
                }

                //sleep to control speed
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {

                    System.err.println(e);
                }
            }

            //behaviour when ghosts are vulnerable
            while (vulnerable && vulCounter < 20 && escaped && !eaten) {
                ghostBrains();
                ghostMovementOutOfBox();
                maze.repaint();
                vulCounter++;
                //checks for pause
                try {
                    if (maze.isPaused) {
                        synchronized (this) {
                            while (maze.isPaused && isRunning) {
                                Thread.sleep(0);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                }

                //sleep to control speed, longer sleep for slower ghosts while vulnerable
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {

                    System.err.println(e);
                }

                //counter to break vulnerable state after 19 ticks
                if (vulCounter == 19) {
                    vulnerable = false;
                }
            }

            //normal behaviour
            //when pacman moves into ghost search range
            if (searching(maze.getPacman().getPacmanBounds())) {
                searching = true;
                alertPlayer.play();
                alertCounter = 0;
            } else {
                searching = false;
            }
            ghostBrains();
            ghostMovementOutOfBox();
            maze.repaint();

            //checks for pause
            try {
                if (maze.isPaused) {
                    synchronized (this) {
                        while (maze.isPaused && isRunning) {
                            Thread.sleep(0);
                        }
                    }
                }
            } catch (InterruptedException e) {
            }

            //sleep to control normal ghost speed
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {

                System.err.println(e);
            }
        }
    }


    public boolean isEaten() {
        return this.eaten;
    }

    public void setEaten(Boolean isEaten) {
        eaten = isEaten;
    }
}
