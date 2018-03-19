package Pacman;

public interface GraphInterface
{
    //Adds edge
     void setEdge (Node start, Node end, int weight);
    
    //Removes an edge
     void deleteEdge(Node start, Node end);
    
    //determines if an edge exists between two nodes
     boolean edgeExists(Node start, Node end);
    
    //returns the size of the graph
     int getSize();
    
    //display the graph
     void dumpGraph();
}
