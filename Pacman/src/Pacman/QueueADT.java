package Pacman;


public interface QueueADT
{
   //  Adds one element to the rear of the queue
   public void enqueue (Object element);

   //  Removes and returns the element at the front of the queue
   public Object dequeue();

   //  Returns without removing the element at the front of the queue
   public Object first();
   
   //  Returns true if the queue contains no elements
   public boolean isEmpty();

   //  Returns the number of elements in the queue
   public int size();

   //  Returns a string representation of the queue
   public String toString();
}
