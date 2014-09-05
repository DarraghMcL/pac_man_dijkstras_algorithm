package Pacman;
public class LinkedQueue implements QueueADT
{
    private int count;
    private LinearNode front, rear;
    
    //initialising empty queue
    public LinkedQueue()
    {
	count = 0;
	front = rear = null;
    }
    
    public void enqueue (Object element)
    {
	LinearNode node = new LinearNode (element);
	if(isEmpty())
	{
	    front = node;
	}
	else
	{
	    rear.setNext(node);
	}
	
	rear= node;
	count++;
    }
    
    public Object dequeue()
    {
	if(isEmpty())
	{
	    throw new EmptyCollectionException("queue");
	}
	
	Object result = front.getElement();
	front = front.getNext();
	count--;
	
	if(isEmpty())
	{
	    rear=null;
	}
	
	return result;
    }
    
    public Object first()
    {
	if(isEmpty())
	{
	    throw new EmptyCollectionException ("queue");
	}
	
	Object result = front.getElement();
	return result;
    }
    
    public boolean isEmpty()
    {
	return (front == null);
    }
    
    public int size()
    {
	return count;
    }
    
    public String toString()
    {
	LinearNode temp = front;
	String result = "";
	while(temp != null)
	{
	    result = result + " " + temp.getElement();
	    temp = temp.getNext();
	}
	return result;
    }
}
