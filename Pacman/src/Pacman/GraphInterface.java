package Pacman;

public interface GraphInterface
{
    //Adds edge
    public void setEdge (Node start, Node end, int weight);
    
    //Removes an edge
    public void deleteEdge(Node start, Node end);
    
    //determines if an edge exists between two nodes
    public boolean edgeExists(Node start, Node end);
    
    //returns the size of the graph
    public int getSize();
    
    //display the graph
    public void dumpGraph();
}
