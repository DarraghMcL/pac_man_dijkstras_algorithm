package Pacman;

import java.awt.*;
import javax.swing.*;

class PacmanGUI extends JFrame {
    public static void main(String args[]) {
        final PacmanGUI UI = new PacmanGUI();
        Runnable r;
        r = new Runnable() {
            public void run() {
                UI.startGUI();
            }
        };
        SwingUtilities.invokeLater(r);
    }


    private void startGUI() {
        JFrame gameBoard = new JFrame("Assignment - Pacman");
        gameBoard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameBoard.setLayout(new BorderLayout());
        // Reference to the maze

        //add the maze to the game board
        gameBoard.add(new Maze(), BorderLayout.CENTER);

        // Display the game window on screen
        gameBoard.pack();
        gameBoard.setVisible(true);
    }
}




