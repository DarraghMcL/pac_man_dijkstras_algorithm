package Pacman;

public interface GraphInterface
{
    //Adds edge
     void setEdge (Cell start, Cell end, int weight);
    
    //Removes an edge
     void deleteEdge(Cell start, Cell end);
    
    //determines if an edge exists between two nodes
     boolean edgeExists(Cell start, Cell end);

}
