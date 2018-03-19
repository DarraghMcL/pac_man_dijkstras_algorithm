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
    //variables declaration

    private boolean isRunning = true;
    private int ghostRow, ghostCol;
    private Maze maze;
    private Cell[][] cells;
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
    //graph and node declaration for shorest path algorithm
    private UndirectedGraph shortestPath;
    private Node[] nodes;
    //ghost search parameter ranges
    private int[] searchPara = {200, 300, 700};
    private int difficulty;
    //obtaining sound file for ghost alerts
    private AudioClip alertPlayer;
    private File alertFile = new File("Resources/alert.wav");
    private URL alertURL;
    private int alertCounter = 0;

    //ghost constructor
    Ghost(int initialRow, int initialCol, Maze startMaze, int Number, int timer, int difc) {
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
        cells = maze.getCells();
        ghostNumber = Number;
        //creating a graph for obtaining the shortest path
        if (maze.firstPlay) {
            createGraph();
            System.out.println("GraphSize:" + graphSize());
        }
    }

    //returns the size of the graph(number of cells in the map)
    private int graphSize() {
        int size = 0;
        for (int i = 0; i < maze.tileHeight; i++) {
            for (int j = 0; j < maze.tileWidth; j++) {

                size++;
            }
        }
        return size;
    }

    //creates the search graph
    private void createGraph() {
        int counter = 0;
        //creates new graph with appropiate size
        shortestPath = new UndirectedGraph(graphSize());
        //creats an array of nodes the same size as the graph
        nodes = new Node[graphSize()];
        //creates a new node for each location in the array of nodes
        for (int i = 0; i < maze.tileHeight; i++) {
            for (int j = 0; j < maze.tileWidth; j++) {
                nodes[counter] = new Node("Node:" + counter);
                nodes[counter].SetX(cells[i][j].x);
                nodes[counter].SetY(cells[i][j].y);
                nodes[counter].setId(counter);
                counter++;
            }
        }
        //adds each node to the graph

        for (int o = 0; o < graphSize(); o++) {

            shortestPath.addNode(nodes[o]);
        }

        //adds an edge to appropiate nodes

        for (int p = 0; p < graphSize(); p++) {
            SetNodeEdgesX(p);
            SetNodeEdgesY(p);
        }
    }

    //iterates through each node for the input node and checks if a horizontal edge should be added
    private void SetNodeEdgesX(int nodeNumber) {
        for (int i = 0; i < graphSize(); i++) {
            if ((nodes[nodeNumber].getX() - nodes[i].getX()) == -1 && (nodes[nodeNumber].getY() - nodes[i].getY()) == 0) {
                if (isCellNavigable(nodes[nodeNumber].getY(), nodes[nodeNumber].getX())) {
                    if (isCellNavigable(nodes[i].getY(), nodes[i].getX())) {
                        shortestPath.setEdge(nodes[nodeNumber], nodes[i], 1);
                    }
                }
            }
        }
    }

    //iterates through each node for the input node and checks if a vertical edge should be added
    private void SetNodeEdgesY(int nodeNumber) {
        for (int i = 0; i < graphSize(); i++) {
            if ((nodes[nodeNumber].getY() - nodes[i].getY()) == -1 && (nodes[nodeNumber].getX() - nodes[i].getX()) == 0) {
                if (isCellNavigable(nodes[nodeNumber].getY(), nodes[nodeNumber].getX())) {
                    if (isCellNavigable(nodes[i].getY(), nodes[i].getX())) {
                        shortestPath.setEdge(nodes[nodeNumber], nodes[i], 1);
                    }
                }
            }
        }
    }

    //returns the node that the ghosts is currently standing on by checking the x and y values of the cell he is on
    private Node getGhostNode() {
        int nodeNum = 0;
        for (int i = 0; i < nodes.length; i++) {
            if (getRow() == nodes[i].getX() && getCol() == nodes[i].getY()) {
                nodeNum = i;
            }
        }
        return nodes[nodeNum];
    }

    //returns the node that pacman is currently standing on by checking the x and y values of the cell he is on
    private Node getPacmanNode() {
        int nodeNum = 0;
        for (int i = 0; i
                < nodes.length; i++) {
            if (maze.getPacmanX() == nodes[i].getX() && maze.getPacmanY() == nodes[i].getY()) {
                nodeNum = i;
            }
        }
        return nodes[nodeNum];
    }

    //returns the node at the specified x and y coords
    private Node getSpecificNode(int x, int y) {
        int nodeNum = 0;

        for (int i = 0; i < nodes.length; i++) {
            if (x == nodes[i].getX() && y == nodes[i].getY()) {
                nodeNum = i;
            }
        }
        return nodes[nodeNum];
    }

    //applies the shortest path algorith and changes the direction to the next node in the path
    private void ghostChase() {
        //if next node is to the right change direction to 'r'
        if ((getRow() - shortestPath.getShortestDistance(getGhostNode(), getPacmanNode()).getX()) < 0) {
            setDirection('r');

        } else if ((getRow() - shortestPath.getShortestDistance(getGhostNode(), getPacmanNode()).getX()) > 0) {
            setDirection('l');

        } else if ((getCol() - shortestPath.getShortestDistance(getGhostNode(), getPacmanNode()).getY()) < 0) {
            setDirection('d');

        } else if ((getCol() - shortestPath.getShortestDistance(getGhostNode(), getPacmanNode()).getY()) > 0) {
            setDirection('u');
        }
    }

    //applies shortest path algorithm to get the ghost back in the box
    private void returnToBox() {
        if ((getRow() - shortestPath.getShortestDistance(getGhostNode(), getSpecificNode(maze.ghostInitialRow, maze.ghostInitialCol)).getX()) < 0) {
            setDirection('r');

        } else if ((getRow() - shortestPath.getShortestDistance(getGhostNode(), getSpecificNode(maze.ghostInitialRow, maze.ghostInitialCol)).getX()) > 0) {
            setDirection('l');

        } else if ((getCol() - shortestPath.getShortestDistance(getGhostNode(), getSpecificNode(maze.ghostInitialRow, maze.ghostInitialCol)).getY()) < 0) {
            setDirection('d');

        } else if ((getCol() - shortestPath.getShortestDistance(getGhostNode(), getSpecificNode(maze.ghostInitialRow, maze.ghostInitialCol)).getY()) > 0) {
            setDirection('u');
        }
    }

    //returns true if pacman is within the ghost search parameter
    private boolean searching() {
        return (getSearchParameter().intersects(maze.getPacmanBounds()));

    }

    //ghost draw class
    public void drawGhost(Graphics g) {
        if (ghostAnimationCounter == 2) {
            ghostAnimationCounter = 0;
        }
        //if the ghost has been eaten only his eyes are displayed
        if (eaten) {
            g.drawImage(Toolkit.getDefaultToolkit().getImage(ghostEaten[ghostAnimationCounter]), ghostRow * 20, ghostCol * 20, maze);
        }
        //if the ghost is vulnerable his picture changes
        if (vulnerable && !eaten) {
            g.drawImage(Toolkit.getDefaultToolkit().getImage(ghostScaredAnimation[ghostAnimationCounter]), ghostRow * 20, ghostCol * 20, maze);
        }
        //displays an alert if pacman moves into ghost search parameter
        if (searching && alertCounter < 10) {
            g.drawImage(Toolkit.getDefaultToolkit().getImage("Resources/sighted.png"), ghostRow * 20, (ghostCol * 20) - 50, maze);
            alertCounter++;
        }
        //usual ghost animation
        if (!vulnerable && !eaten) {
            g.drawImage(Toolkit.getDefaultToolkit().getImage(ghostAnimations[ghostAnimationCounter][ghostNumber]), (ghostRow * 20), (ghostCol * 20), maze);
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

    protected void setRow(int x) {
        ghostRow = x;
    }

    protected void setCol(int y) {
        ghostCol = y;
    }

    //moves the ghosts by the specified x and y amounts
    private void moveGhost(int x, int y) {
        ghostRow = ghostRow + x;
        ghostCol = ghostCol + y;
    }

    //gets the ghost collision box
    public Rectangle getGhostBounds() {
        return new Rectangle(getRow() * 20, getCol() * 20, 20, 20);
    }

    //gets the ghost search parameter
    //parameter is increased depending on the difficulty selected
    private Rectangle getSearchParameter() {
        return new Rectangle((getRow() * 20) - searchPara[difficulty] / 2, (getCol() * 20) - searchPara[difficulty] / 2, searchPara[difficulty], searchPara[difficulty]);
    }

    //determines if the cell can be navigated
    private boolean isCellNavigable(int column, int row) {

        return (cells[column][row].getType() == 'o' || cells[column][row].getType() == 'd' || cells[column][row].getType() == 'p' || cells[column][row].getType() == 'g' || cells[column][row].getType() == 'e');
    }

    //determines if a cell is navigable after the ghost has left the box
    private boolean isCellNavigableOutOfBox(int column, int row) {

        return (cells[column][row].getType() == 'o' || cells[column][row].getType() == 'd' || cells[column][row].getType() == 'p');

    }

    //determines the navigable cells for when the ghost is out of the box
    private boolean isDirectionNavigable(char direction) {
        if (direction == 'u' && isCellNavigable(getCol() - 1, getRow())) {
            return true;

        } else if (direction == 'd' && isCellNavigable(getCol() + 1, getRow())) {
            return true;

        } else if (direction == 'l' && isCellNavigable(getCol(), getRow() - 1)) {
            return true;

        } else if (direction == 'r' && isCellNavigable(getCol(), getRow() + 1)) {
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
        if (direction == 'u' && isCellNavigable(getCol() - 1, getRow())) {
            moveGhost(0, -1);


        } else if (direction == 'd' && isCellNavigable(getCol() + 1, getRow())) {
            moveGhost(0, +1);


        } else if (direction == 'l' && isCellNavigable(getCol(), getRow() - 1)) {
            moveGhost(-1, 0);


        } else if (direction == 'r' && isCellNavigable(getCol(), getRow() + 1)) {
            moveGhost(+1, 0);
        }
    }

    //ghost movements for when they're out of the box
    private void ghostMovementOutOfBox() {
        if (direction == 'u' && isCellNavigableOutOfBox(getCol() - 1, getRow())) {
            moveGhost(0, -1);


        } else if (direction == 'd' && isCellNavigableOutOfBox(getCol() + 1, getRow())) {
            moveGhost(0, +1);


        } else if (direction == 'l' && isCellNavigableOutOfBox(getCol(), getRow() - 1)) {
            moveGhost(-1, 0);


        } else if (direction == 'r' && isCellNavigableOutOfBox(getCol(), getRow() + 1)) {
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
                arraySize = 0;
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
                if (!searching()) {
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
            if (searching()) {
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


    public boolean isEaten(){
        return this.eaten;
    }

    public void setEaten(Boolean isEaten){
        eaten = isEaten;
    }
}
