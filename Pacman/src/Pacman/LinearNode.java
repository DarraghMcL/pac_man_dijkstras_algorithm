package Pacman;
public class LinearNode
{
    private LinearNode next;
    private Object element;
  
    //constructor for empty node
    public LinearNode()
    {
	next = null;
	element=null;
    }
    
    //creates a node with the supplied value
    public LinearNode(Object elem)
    {
	next = null;
	element = elem;
    }
    
    //returns next node
    public LinearNode getNext()
    {
	return next;
    }
    
    //sets the next node
    public void setNext (LinearNode node)
    {
	next = node;
    }
    
    //returns the node value
    public Object getElement()
    {
	return element;
    }
    
    //sets the node with supplied value
    public void setElement(Object elem)
    {
	element = elem;
    }
}
