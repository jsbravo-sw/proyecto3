package model.data_structures;

public class Node <T>
{
	private Node<T> next, previous;
	private T elem;

	public Node () 
	{
		next = null;
		elem = null;
		previous = null;
	}

	public Node (T pT, Node<T> pNext, Node<T> pPrevious)
	{
		next = pNext;
		elem =pT;
		previous = pPrevious;
	}


	public boolean hasNext()
	{
		return (next!=null);
	}

	public boolean hasPrevious()
	{
		return (previous!=null);
	}

	public void setElem(T pElem)
	{
		elem = pElem;
	}


	public void setNext(Node<T> pNext)
	{
		next = pNext;
	}

	public void setPrevious(Node<T> pPrevious)
	{
		previous = pPrevious;

	}

	public Node<T> next()
	{
		return next;
	}

	public Node<T> previous()
	{
		return previous;
	}

	public T elem()
	{
		return elem;
	}

}
