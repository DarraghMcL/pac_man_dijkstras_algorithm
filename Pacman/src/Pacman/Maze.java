//Darragh McLernon
package Pacman;

import java.applet.Applet;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.applet.AudioClip;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Random;
import java.util.stream.IntStream;

import static java.util.stream.IntStream.range;

/**
 * Represents the maze that appears on screen. Creates the maze data using
 * a 2D array of Cell objects, and renders the maze on screen.
 */
public class Maze extends JPanel {

    private final static int CELL = 20; //cell dimension in pixels
    /**
     * The two-dimensional array of Cells that will make up the maze
     */
    //private ArrayList<Cell> cells
    private UndirectedGraph graph;
    //initialising pacman
    private Pacman pacman;
    private int pacmanInitialRow = 13;
    private int pacmanInitialColumn = 20;
    //initialising ghosts
    private Ghost ghost0;
    private Ghost ghost1;
    private Ghost ghost2;
    private Ghost ghost3;
    public int ghostInitialRow = 10;
    public int ghostInitialCol = 23;
    //declaring variables
    private boolean showHighScore = false;
    private boolean updateHighScores = true;
    private int points = 0;
    private int levelNumber = 1;
    boolean isPaused = false;
    public boolean firstPlay = true;
    private int difficulty = 0;
    //array of highscores
    private int[] highScores = new int[5];
    private int[] tempHighScores = new int[6];
    //file and applet declaration for audio
    private FileInputStream scoreFile;
    private FileWriter highScoreIn;
    private String map[] = {"Resources/level1.txt", "Resources/level2.txt"};
    private AudioClip wakaWakaPlayer;
    private File wakaWakaFile = new File("Resources/wakaWaka.wav");
    private URL wakaWakaURL;
    private AudioClip deadPlayer;
    private File deadFile = new File("Resources/dead.wav");
    private URL deadURL;
    private AudioClip openingPlayer;
    private File openingFile = new File("Resources/opening.wav");
    private URL openingURL;

    public Pacman getPacman() {
        return pacman;
    }

    public Maze() {
        //creating new instances of sound players
        try {
            wakaWakaURL = wakaWakaFile.toURL();
            deadURL = deadFile.toURL();
            openingURL = openingFile.toURL();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        wakaWakaPlayer = Applet.newAudioClip(wakaWakaURL);
        deadPlayer = Applet.newAudioClip(deadURL);
        openingPlayer = Applet.newAudioClip(openingURL);

        //new map is generated from a randomly selected map
        graph = new UndirectedGraph();

        //this code is used to generate the test map
        //createCellArray("Resources/levelTest.txt");

        //creating new instances of pacman and ghosts
        setPreferredSize(new Dimension(CELL * graph.getGraphWidth(), CELL * graph.getGraphHeight() + 100));
        pacman = new Pacman(pacmanInitialColumn, pacmanInitialRow, this, 3, graph);
        pacman.start();

        System.out.println(pacman.getPacmanCell().toString());

        ghost0 = new Ghost(ghostInitialCol, ghostInitialRow, this, 0, 200, difficulty, graph);
        ghost0.start();

        ghost1 = new Ghost(ghostInitialCol, ghostInitialRow, this, 1, 1500, difficulty, graph);
        ghost1.start();

        ghost2 = new Ghost(ghostInitialCol, ghostInitialRow, this, 2, 1000, difficulty, graph);
        ghost2.start();

        ghost3 = new Ghost(ghostInitialCol, ghostInitialRow, this, 3, 2000, difficulty, graph);
        ghost3.start();


        this.setFocusable(true);
        this.addKeyListener(new KeyAdapter() {
            //listening for arrow keyboard input

            public void keyPressed(KeyEvent e) {
                //checks if the cell is navigatable before changing direction to prevent pacman from stopping
                if (e.getKeyCode() == KeyEvent.VK_UP && graph.isCellNavigable(graph.get_cell_by_coords(pacman.getCol(), pacman.getRow() - 1))) {
                    pacman.setDirection('u');
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN && graph.isCellNavigable(graph.get_cell_by_coords(pacman.getCol(), pacman.getRow() + 1))) {
                    pacman.setDirection('d');
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT && graph.isCellNavigable(graph.get_cell_by_coords(pacman.getCol() - 1, pacman.getRow()))) {
                    pacman.setDirection('l');
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && graph.isCellNavigable(graph.get_cell_by_coords(pacman.getCol() + 1, pacman.getRow()))) {
                    pacman.setDirection('r');
                }

                //pausing the game
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    if (!isPaused) {
                        isPaused = true;
                    } else {
                        resumeGame();
                    }
                }

                //changiong the difficulty setting using 1, 2 and 3 keys
                //new difficulty will only be applied when a new level is generated
                if (e.getKeyCode() == KeyEvent.VK_1) {
                    difficulty = 0;
                }
                if (e.getKeyCode() == KeyEvent.VK_2) {
                    difficulty = 1;
                }
                if (e.getKeyCode() == KeyEvent.VK_3) {
                    difficulty = 2;
                }

                //restarting or quitting the game after all lives are lost
                if (showHighScore && e.getKeyCode() == KeyEvent.VK_SPACE) {

                    //updating the highscores and reseting the game variables
                    showHighScore = false;
                    updateHighScores = true;
                    pacman.setLives(3);
                    points = 0;
                    levelNumber = 1;
                    graph = new UndirectedGraph();
                    firstPlay = true;
                    try {
                        resetLevel();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    firstPlay = false;
                }
                //pressing escape at the highScore screen will exit the game
                if (showHighScore && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    updateHighScores = true;
                    System.exit(0);
                }
            }
        });
        repaint();
    }

    //reads the high scores from file and stores them in an array
    private void readHighScores() {
        try {
            //getting file to read from
            scoreFile = new FileInputStream("Resources/highScores.txt");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        //creating reader buffers
        String score = "";
        int i = 0;
        try (DataInputStream in = new DataInputStream(scoreFile);
             BufferedReader br = new BufferedReader(new InputStreamReader(in))
        ) {
            //looping through each line and adding it to the array
            //lines need to be parsed to int when read
            while ((score = br.readLine()) != null) {
                highScores[i] = Integer.parseInt(score);
                i++;
            }
            br.close();
        } catch (NumberFormatException | IOException ex) {
            ex.printStackTrace();

        }
    }

    //method writes the current high score list to file
    private void writeHighScores() throws IOException {
        try {
            //getting file to write to
            highScoreIn = new FileWriter("Resources/highScores.txt");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //adding new high score to temporary table and sorting the table

        range(0, 5).forEach(i -> tempHighScores[i] = highScores[i]);

        tempHighScores[5] = points;
        Arrays.sort(tempHighScores);
        //reversing to have array in descending order
        for (int w = 0; w < tempHighScores.length / 2; w++) {
            // swap the elements
            int temp = tempHighScores[w];
            tempHighScores[w] = tempHighScores[tempHighScores.length - (w + 1)];
            tempHighScores[tempHighScores.length - (w + 1)] = temp;
        }
        //adding temp array to main array of highscores
        IntStream.range(0, 5).forEach(j -> highScores[j] = tempHighScores[j]);
        //writing the updated array to the file
        BufferedWriter out = new BufferedWriter(highScoreIn);
        for (int k = 0; k < 5; k++) {
            out.write(Integer.toString(highScores[k]));
            //avoids a new line at end of file
            if (k < 4) {
                out.newLine();
            }
        }
        out.close();
    }

    //resume game after pausing
    private synchronized void resumeGame() {
        isPaused = false;
        notify();
    }


    //when pacman is at a 'd' node it is changed to 'o'
    public void eatsPills() {
        Cell cell = pacman.getPacmanCell();
        if (cell.getType() == 'd') {
            cell.setType('o');
            //sound is played and score is increased
            wakaWakaPlayer.play();
            points = points + 10;
        }
    }

    //when pacman is at a 'p' node it is changed to 'o'
    public void eatsPowerPills() {
        Cell cell = pacman.getPacmanCell();
        if (cell.getType() == 'p') {
            cell.setType('o');
            //ghosts are made vulnerable
            ghost0.ghostIsVulnerable();
            ghost1.ghostIsVulnerable();
            ghost2.ghostIsVulnerable();
            ghost3.ghostIsVulnerable();
            //score is increased
            points = points + 50;
        }
    }

    //when pacman looses all of his lives
    private void gameOver() {
        pacman.stop();
        ghost0.stop();
        ghost1.stop();
        ghost2.stop();
        ghost3.stop();

        showHighScore = true;
    }

    //allows characters to use the tunnel
    //when a character lands on a tunnel node they are moved to the corresponding node
    private void tunnel() {
        if (pacman.getRow() == 10 && pacman.getCol() == 1) {
            pacman.setCol(43);
            pacman.setRow(10);
        }

        if (pacman.getRow() == 10 && pacman.getCol() == 44) {
            pacman.setCol(2);
            pacman.setRow(10);
        }

        if (ghost0.getRow() == 10 && ghost0.getCol() == 1) {
            ghost0.setCol(43);
            ghost0.setRow(10);
        }

        if (ghost0.getRow() == 10 && ghost0.getCol() == 44) {
            ghost0.setCol(2);
            ghost0.setRow(10);
        }

        if (ghost1.getRow() == 10 && ghost1.getCol() == 1) {
            ghost1.setCol(43);
            ghost1.setRow(10);
        }

        if (ghost1.getRow() == 10 && ghost1.getCol() == 44) {
            ghost1.setCol(2);
            ghost1.setRow(10);
        }

        if (ghost2.getRow() == 10 && ghost2.getCol() == 1) {
            ghost2.setCol(43);
            ghost2.setRow(10);
        }

        if (ghost2.getRow() == 10 && ghost2.getCol() == 44) {
            ghost2.setCol(2);
            ghost2.setRow(10);
        }

        if (ghost3.getRow() == 10 && ghost3.getCol() == 1) {
            ghost3.setCol(43);
            ghost3.setRow(10);
        }

        if (ghost3.getRow() == 10 && ghost3.getCol() == 44) {
            ghost3.setCol(2);
            ghost3.setRow(10);
        }
    }

    //used when the level is cleared or a new game is started
    private void resetLevel() throws InterruptedException {
        //pause all threads and recreate the character instances
        isPaused = true;
        pacman = new Pacman(pacmanInitialColumn, pacmanInitialRow, this, pacman.getLives(), graph);
        ghost0 = new Ghost(ghostInitialCol, ghostInitialRow, this, 0, 200, difficulty, graph);
        ghost1 = new Ghost(ghostInitialCol, ghostInitialRow, this, 1, 1500, difficulty, graph);
        ghost2 = new Ghost(ghostInitialCol, ghostInitialRow, this, 2, 1000, difficulty, graph);
        ghost3 = new Ghost(ghostInitialCol, ghostInitialRow, this, 3, 2000, difficulty, graph);

        //restart the threads
        resumeGame();
        pacman.start();
        ghost0.start();
        ghost1.start();
        ghost2.start();
        ghost3.start();

    }

    //multiple method to detect collisions with pacman and each ghost
    private boolean collisionWithGhost0() {
        return pacman.getPacmanBounds().intersects(ghost0.getGhostBounds());
    }

    private boolean collisionWithGhost1() {
        return pacman.getPacmanBounds().intersects(ghost1.getGhostBounds());
    }

    private boolean collisionWithGhost2() {
        return pacman.getPacmanBounds().intersects(ghost2.getGhostBounds());
    }

    private boolean collisionWithGhost3() {
        return pacman.getPacmanBounds().intersects(ghost3.getGhostBounds());
    }


    //method to determine various collision behaviours
    //the first two statements are repeated for all ghosts
    private void collisions() throws InterruptedException {
        //when ghost is vulnerable and not eaten, eat him
        if (collisionWithGhost0() && ghost0.isVulnerable() && !ghost0.isEaten()) {
            points = points + 100;
            ghost0.setEaten(true);
        }
        //when ghost is not vulnerable, pacman dies
        if (collisionWithGhost0() && ghost0.isVulnerable() && !ghost0.isEaten()) {
            deadPlayer.play();
            pacman.setLives(pacman.getLives() - 1);
            resetLevel();
        }

        if (collisionWithGhost1() && ghost1.isVulnerable() && !ghost1.isEaten()) {
            points = points + 100;
            ghost1.setEaten(true);
        }
        if (collisionWithGhost1() && ghost1.isVulnerable() && !ghost1.isEaten()) {
            deadPlayer.play();
            pacman.setLives(pacman.getLives() - 1);
            resetLevel();
        }

        if (collisionWithGhost2() && ghost2.isVulnerable() && !ghost2.isEaten()) {
            points = points + 100;
            ghost2.setEaten(true);
        }
        if (collisionWithGhost2() && !ghost2.isVulnerable() && !ghost2.isEaten()) {
            deadPlayer.play();
            pacman.setLives(pacman.getLives() - 1);
            resetLevel();
        }

        if (collisionWithGhost3() && ghost3.isVulnerable() && !ghost3.isEaten()) {
            points = points + 100;
            ghost3.setEaten(true);
        }
        if (collisionWithGhost3() && !ghost3.isVulnerable() && !ghost3.isEaten()) {
            deadPlayer.play();
            pacman.setLives(pacman.getLives() - 1);
            resetLevel();
        }

    }

    //paint class for maze
    //many methods are called here becuase the paint method is recalled regularly
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, graph.getGraphWidth() * CELL, graph.getGraphHeight() * CELL);


        for (Cell cell : graph.getCells()) {
            cell.drawBackground(g);
        }
        //plays the opening theme music if it is the first time the level is seen
        if (firstPlay) {
            openingPlayer.play();
            firstPlay = false;
        }
        //checks the characters for using the tunnel
        tunnel();
        //checks for collisions
        try {
            collisions();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        //drawing all resources on screen
        g.drawImage(Toolkit.getDefaultToolkit().getImage("Resources/border.png"), 0, 440, pacman.maze);
        pacman.drawPacman(g);
        ghost0.drawGhost(g);
        ghost1.drawGhost(g);
        ghost2.drawGhost(g);
        ghost3.drawGhost(g);
        g.setColor(Color.yellow);
        g.drawString(Integer.toString(pacman.getLives()), 170, 470);
        g.drawString(Integer.toString(points), 170, 510);
        g.drawString("Level: " + Integer.toString(levelNumber), 300, 470);
        //rechecking for collisions for accuracy
        try {
            collisions();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        //displays high score table when all lives are lost
        if (pacman.getLives() == 0 && updateHighScores) {
            readHighScores();
            try {
                writeHighScores();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            updateHighScores = false;
            gameOver();
        }

        //checks if the game is paused
        if (isPaused) {
            g.drawImage(Toolkit.getDefaultToolkit().getImage("Resources/pause.gif"), 370, 200, pacman.maze);
        }

        //paints the highscore table
        if (showHighScore) {
            g.drawImage(Toolkit.getDefaultToolkit().getImage("Resources/highScore.png"), 370, 200, pacman.maze);
            g.drawString(Integer.toString(highScores[0]), 450, 280);
            g.drawString(Integer.toString(highScores[1]), 450, 315);
            g.drawString(Integer.toString(highScores[2]), 450, 350);
            g.drawString(Integer.toString(highScores[3]), 450, 385);
            g.drawString(Integer.toString(highScores[4]), 450, 415);
        }

        //when the level is cleared
        if (graph.isLevelCleared()) {
            try {
                graph = new UndirectedGraph();
                firstPlay = true;
                resetLevel();
                levelNumber++;
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

    }

}
