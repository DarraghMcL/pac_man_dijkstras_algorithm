package Pacman;

public class EmptyCollectionException extends RuntimeException
{
    /** Creates a new instance of EmptyCollectionException */
    public EmptyCollectionException(String collection)
    {
	super ("Error: The " + collection + "is empty.");
    }
}