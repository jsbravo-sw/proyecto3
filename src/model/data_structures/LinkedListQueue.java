package model.data_structures;

public class LinkedListQueue  <T extends Comparable<T>> implements IQueue<T> 
{
	private Node<T> first,  last;
	private int size;

	public LinkedListQueue()
	{
		first=null;
		last=null;
		size=0;
	}


	public T get(int pos) 
	{
		if (pos < 0)
			return null;
		Node<T> chk = first;
		if (chk!= null) 
		{
			for (int i=0;i<pos;i++)
			{
				if (!chk.hasNext())
					return null;
				chk = chk.next();
			}
			return chk.elem();
		}
	return null;

}


public int size() 
{
	return size;
}

public boolean isEmpty()
{
	return size==0? true:false;
}


public void enqueue(T item) 
{
	Node<T> newNode = new Node<T>();
	newNode.setElem(item);
	if (first!=null)
	{
		last.setNext(newNode);
		last = last.next();
	}
	else
	{
		first = newNode;
		last = first;
	}
	size++;
}

public T dequeue() 
{
	T resp;
	if (first!=null)
	{
		resp = last.elem();
		last = last.previous();
		size--;
	}
	else
	{ 
		resp = first.elem();
		first = null;
		size--;
	}
	return resp;
}

public T peek() 
{
	T resp;
	if (first!=null)
	{
		resp = last.elem();
		
	}
	else
	{ 
		resp = first.elem();
	
	}
	return resp;
}

}
