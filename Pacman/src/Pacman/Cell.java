package Pacman;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Cell {

    final int CELL = 20; //cell dimension in pixels
    private int row, column;
    private char type;
    private ArrayList<Cell> neighbors;
    private boolean visited = false;
    private int nodeId = 0;

    //Constructor
    Cell(int id, int column, int row, char type) {
        this.nodeId=id;
        this.column = column;
        this.row = row;
        this.type = type;
        this.neighbors=new ArrayList<>();
    }


    public void addNeighbor(Cell cell) {
        if(cell != null) {
            neighbors.add(cell);
        }
    }

    //returns an iterator of neighboring nodes
    public Iterator<Cell> getNeighbors() {
        return neighbors.iterator();
    }

    //checks if node has been visited
    public boolean isVisited() {
        return visited;
    }

    //sets a node as visited
    public void setVisited(boolean flag) {
        visited = flag;
    }


    Cell(){  }

    public int getRow(){
        return this.row;
    }

    public void setRow(int row){
        this.row = row;
    }

    public int getColumn(){
        return this.column;
    }

    public void setColumn(int column){
        this.column=column;
    }

    //get type
    public char getType() {
        return type;
    }

    public void setType(char c){
        this.type = c;
    }

    //gets the node id
    public int getId()
    {
        return nodeId;
    }

    //sets the node id
    public void setId(int id)
    {
        nodeId = id;
    }


    //draw the cell
    public void drawBackground(Graphics g) {

        int xBase;
        int yBase;

        switch (type) {
            case 'e': //corral exit
                g.setColor(Color.WHITE);
                g.fillRect(column * CELL, row * CELL + CELL / 2 - 10, CELL, 3);
                break;
            case 'h': //horizontal line
                g.setColor(Color.BLUE);
                g.fillRect(column * CELL, row * CELL + CELL / 2 - 1, CELL, 3);
                break;
            case 'v': //vertical line
                g.setColor(Color.BLUE);
                g.fillRect(column * CELL + CELL / 2 - 1, row * CELL, 3, CELL);
                break;
            case '1'://northeast corner
                xBase = column * CELL - CELL / 2;
                yBase = row * CELL + CELL / 2;
                drawCorner(g, xBase, yBase);
                break;
            case '2': //northwest corner
                xBase = column * CELL + CELL / 2;
                yBase = row * CELL + CELL / 2;
                drawCorner(g, xBase, yBase);
                break;
            case '3': //southeast corner
                xBase = column * CELL - CELL / 2;
                yBase = row * CELL - CELL / 2;
                drawCorner(g, xBase, yBase);
                break;
            case '4': //southwest corner
                xBase = column * CELL + CELL / 2;
                yBase = row * CELL - CELL / 2;
                drawCorner(g, xBase, yBase);
                break;
            case 'o'://empty navigable cell
                break;
            case 'd': //navigable cell with pill
                g.setColor(Color.WHITE);
                g.fillRect(column * CELL + CELL / 2 - 1, row * CELL + CELL / 2 - 1, 3, 3);
                break;
            case 'p': //navigable cell with power pellet
                g.setColor(Color.PINK);
                g.fillOval(column * CELL + CELL / 2 - 7, row * CELL + CELL / 2 - 7, 13, 13);
                break;
            case 'x': //empty non-navigable cell
                break;
            case 'g': //the Corral
            default:
                break;
        }
    }

    //draw a rounded corner 3 pixels thick
    private void drawCorner(Graphics g, int xBase, int yBase) {
        Graphics2D g2 = (Graphics2D) g;
        Rectangle oldClip = g.getClipBounds();
        g2.setClip(column * CELL, row * CELL, CELL, CELL);
        g2.setColor(Color.BLUE);
        Shape oval = new Ellipse2D.Double(xBase, yBase, CELL, CELL);
        g2.setStroke(new BasicStroke(3));
        g2.draw(oval);
        g2.setClip(oldClip);
    }

    public String toString() {
        return "[Row:" + this.getRow() + "," + "Column:" + this.getColumn() + "]";
    }
}
