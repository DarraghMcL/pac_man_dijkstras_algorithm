//Darragh McLernon
package Pacman;

import java.util.*;

public class Node
{
    //declaring class variables
    private String label;
    private boolean visited = false;
    private Vector<Node> neighbors;
    private int nodeId = 0;
    private int XCoord;
    private int YCoord;

    //constructor
    public Node(String label)
    {
        this.label = label;
        neighbors = new Vector<Node>();
    }

    //sets the x value
    public void SetX(int x)
    {
        XCoord = x;
    }

    //sets the y value
    public void SetY(int y)
    {
        YCoord = y;
    }

    //get x value
    public int getX()
    {
        return XCoord;
    }

    //get y value
    public int getY()
    {
        return YCoord;
    }

    //sets the node label
    public void setLabel(String pLabel)
    {
        label = pLabel;
    }

    //adds neighbor to the node
    public void addNeighbor(Node v)
    {
        neighbors.add(v);
    }

    //returns an iterator of neighboring nodes
    public Iterator<Node> getNeighbors()
    {
        return neighbors.iterator();
    }

    //returns node label
    public String getLabel()
    {
        return label;
    }

    //checks if node has been visited
    public boolean isVisited()
    {
        return visited;
    }

    //sets a node as visited
    public void setVisited(boolean flag)
    {
        visited = flag;
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

    //overwritten tostring method
    public String toString()
    {
        return (label + " ");
    }
}
