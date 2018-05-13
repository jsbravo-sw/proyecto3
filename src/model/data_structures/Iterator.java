package model.data_structures;

public class Iterator  <E extends Comparable<E>>
{

	private Node<E> actual;

	public Iterator(Node<E> pNode) 
	{
		actual = pNode;
	}

	public boolean hasNext() 
	{
		return actual != null;
	}

	public E next() 
	{
		if(!hasNext())
		{
			return null;

		}
		E valor = actual.elem();
		actual = actual.next();
		return valor;
	}
	public Node<E> actual ()
	{
		return actual;
	}

}