package Pacman;
public class LinkedStack implements StackADT
{
    private int count;
    private LinearNode top;
    
    //constructor
    public LinkedStack()
    {
	count = 0;
	top = null;
    }
    
    public void setCount(int count)
    {
	this.count = count;
    }
    
    public void push(Object element)
    {
	LinearNode temp = new LinearNode(element);
	
	temp.setNext(top);
	top=temp;
	count++;
    }
    
    public Object pop()
    {
	if (isEmpty())
	{
	    throw new EmptyCollectionException("Stack");
	}
	
	Object result = top.getElement();
	top = top.getNext();
	count--;
	
	return result;
    }
    
    public Object peek()
    {
	if(isEmpty())
	{
	    throw new EmptyCollectionException("Stack");
	}
	
	Object result = top.getElement();
	return result;
    }
    
    public boolean isEmpty()
    {
	return (top==null);
    }
    
    
    public int size()
    {
	return count;
    }
    
    public int getCount()
    {
	return count;
    }
    
    public String toString()
    {
	LinearNode temp = top;
	String result = " ";
	while(temp!= null)
	{
	    result = result + " " + temp.getElement();
	    temp = temp.getNext();
	}
	return result;
    }
}
