//Darragh McLernon
package Pacman;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class UndirectedGraph extends JPanel implements GraphInterface {
    //declaring class variables

    private ArrayList<Cell> cells;
    private int[][] adjMatrix;
    int tileWidth;
    int tileHeight;

    //constructor
    UndirectedGraph() {
        cells = new ArrayList<>();
        adjMatrix = new int[1012][1012];

        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 1; j++) {
                adjMatrix[i][j] = -1;
            }
        }

        createGraph();

    }


    //adds the specified node to the matrix
    public void addCell(Cell n) {
        if (cells.size() == adjMatrix.length) {
            expandCapacity();
        }
        cells.add(n);
    }

    public ArrayList<Cell> getCells() {
        return cells;
    }

    //sets edge between specified nodes and sets weight between them
    public void setEdge(Cell start, Cell end, int weight) {
        int startIndex = start.getId();
        int endIndex = end.getId();
        adjMatrix[startIndex][endIndex] = weight;
        adjMatrix[endIndex][startIndex] = weight;
        start.addNeighbor(end);
        end.addNeighbor(start);
    }

    //deletes edges between nodes
    public void deleteEdge(Cell start, Cell end) {
        int startIndex = start.getId();
        int endIndex = end.getId();
        adjMatrix[startIndex][endIndex] = -1;
        adjMatrix[endIndex][startIndex] = -1;
    }

    //returns true if edge exists between specified nodes
    public boolean edgeExists(Cell start, Cell end) {
        int startIndex = start.getId();
        int endIndex = end.getId();

        //weight is -1 for nodes with an edge
        return adjMatrix[startIndex][endIndex] != -1;
    }

    //returns the weight between two nodes
    public int getEdgeWeight(Cell start, Cell end) {
        int startIndex = start.getId();
        int endIndex = end.getId();

        if (adjMatrix[startIndex][endIndex] == -1) {
            return 5000000;
        }

        return adjMatrix[startIndex][endIndex];
    }


    private void clearCells() {
        for (Cell cell : cells) {
            cell.setVisited(false);
        }
    }


    public void dump() {

        for (Cell cell : cells) {
            System.out.print(cell);
            System.out.println();
        }
    }

    //extends the capacity of the array
    private void expandCapacity() {
        int currentSize = adjMatrix.length;

        int[][] larger = new int[currentSize + 1][currentSize + 1];

        for (int i = 0; i < currentSize; i++) {
            for (int j = 0; j < currentSize; j++) {
                larger[i][j] = adjMatrix[i][j];
            }
        }
        adjMatrix = larger;
    }

    //Dijkras algorithm
    public Cell getShortestDistance(Cell src, Cell dst) {
        if (src == null || dst == null) {
            return null;
        }

        final int[] pathWeights = new int[1012];
        Cell[] predecessors = new Cell[1012];


        //initialisation
        //size is hardcoded to number of cells in array
        for (int i = 0; i < 1012; i++) {
            pathWeights[i] = 500000;
            predecessors[i] = null;
        }

        clearCells();

        pathWeights[src.getId()] = 0;

        Iterator<Cell> it;
        Cell temp = src;
        Cell neigh;

        //compares two nodes based on weight
        final Comparator<Cell> comp = new Comparator<Cell>() {

            public int compare(Cell n1, Cell n2) {
                if (pathWeights[n1.getId()] > pathWeights[n2.getId()]) {
                    return 1;
                }

                if (pathWeights[n1.getId()] < pathWeights[n2.getId()]) {
                    return -1;
                }

                return 0;
            }
        };

        //setting up a new queue
        PriorityQueue<Cell> cellQueue = new PriorityQueue<>(6, comp);
        cellQueue.add(src);

        //fills the queue with nodes
        while (!temp.equals(dst) && !cellQueue.isEmpty()) {
            temp = cellQueue.poll();
            if (!temp.isVisited()) {
                temp.setVisited(true);
                it = temp.getNeighbors();
                while (it.hasNext()) {
                    neigh = it.next();
                    if (pathWeights[neigh.getId()] > pathWeights[temp.getId()] + getEdgeWeight(temp, neigh)) {
                        pathWeights[neigh.getId()] = pathWeights[temp.getId()] + getEdgeWeight(temp, neigh);
                        predecessors[neigh.getId()] = temp;
                    }
                    cellQueue.add(neigh);
                }
            }
        }

        //constructing the shortest path
        Vector<Cell> pathVec = new Vector<>();
        Cell step = dst;
        while (step != null) {
            pathVec.add(step);
            step = predecessors[step.getId()];
        }

        if (!(pathVec.get(pathVec.size() - 1)).equals(src)) {
            System.out.println("Cannot find shortest path");
        }

        //returns the second node in the shortest path
        //if path is less than two steps, returns the last step
        if (pathVec.size() < 3) {
            return pathVec.get(0);
        }
        return pathVec.get(pathVec.size() - 2);
    }

    //creates the search graph
    private void createGraph() {

        // Scanner object to read from map file
        Scanner fileReader;
        ArrayList<String> lineList = new ArrayList<String>();

        // Attempt to load the maze map file
        try {
            fileReader = new Scanner(new File("Resources/level1.txt"));
            while (true) {
                String line = null;
                try {
                    line = fileReader.nextLine();
                } catch (Exception eof) {
                    //throw new A5FatalException("Could not read resource");
                }
                if (line == null) {
                    break;
                }
                lineList.add(line);
            }
            tileHeight = lineList.size();
            tileWidth = lineList.get(0).length();

            int counter = 0;
            // creating the cells
            for (int row = 0; row < tileHeight; row++) {
                String line = lineList.get(row);
                for (int column = 0; column < tileWidth; column++) {
                    char type = line.charAt(column);
                    Cell new_cell = new Cell(counter, column, row, type);
                    addCell(new_cell);
                    counter++;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Maze map file not found");
        }

        addEdges();
        //System.out.print(this.toString());
    }

    public void addEdges() {
        //adds an edge to appropiate nodes

        for (Cell cell : cells) {
            SetCellEdgesX(cell);
            SetCellEdgesY(cell);
        }
    }

    //determines if the cell can be navigated
    public boolean isCellNavigable(Cell cell) {

        if(cell !=null) {
            switch (cell.getType()) {
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
        return false;
    }

    public Cell get_cell_by_coords(int column, int row) {
        for (Cell cell : cells) {
            if (cell.getColumn() == column && cell.getRow() == row) {
                return cell;
            }
        }
        System.out.print("Could not find cell by co-ord");
        return null;
    }

    //iterates through each node for the input node and checks if a horizontal edge should be added
    private void SetCellEdgesX(Cell cell) {
        for (Cell cell_compare : cells) {
            if ((cell.getColumn() - cell_compare.getColumn()) == -1 && (cell.getRow() - cell_compare.getRow()) == 0) {
                if (isCellNavigable(cell) & isCellNavigable(cell_compare)) {
                    setEdge(cell, cell_compare, 1);
                }
            }
        }
    }

    //iterates through each node for the input node and checks if a vertical edge should be added
    private void SetCellEdgesY(Cell cell) {
        for (Cell cell_compare : cells) {
            if ((cell.getRow() - cell_compare.getRow()) == -1 && (cell.getColumn() - cell_compare.getColumn()) == 0) {
                if (isCellNavigable(cell) & isCellNavigable(cell_compare)) {
                    setEdge(cell, cell_compare, 1);
                }
            }
        }
    }

    public boolean isLevelCleared() {

        boolean isCleared = true;
        for (Cell cell : cells) {
            if (cell.getType() == 'd' || cell.getType() == 'p') {
                isCleared = false;
            }
        }
        return isCleared;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Cell cell : cells) {
            cell.drawBackground(g);
        }
    }

    public int getGraphWidth(){
        return tileWidth;
    }

    public int getGraphHeight(){
        return tileHeight;
    }


    @Override
    public String toString(){

        String string = "";
        int counter = 1;
        for(Cell cell:cells){
            if(cell.getRow()==counter){
                string+="\n";
                counter++;
            }

            string += "[" + cell.getColumn() + "," + cell.getRow() + "]";


        }
        return string;
    }

}