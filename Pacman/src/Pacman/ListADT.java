package Pacman;
import java.util.Iterator;


public interface ListADT
{
    public void addToFront(Object element);
    
    public void addToRear(Object element);
    
    public void addAfter(Object element, Object target);
    
    public Object removeFirst();
    
    public Object removeLast();
    
    public Object remove(Object targetElement);
    
    public boolean contains(Object target);
    
    public boolean isEmpty();
    
    public int size();
    
    public String toString();
    
    public Iterator iterator();
    
    public Object first();
    
    public Object last();
}
