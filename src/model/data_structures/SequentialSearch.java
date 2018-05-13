package model.data_structures;

public class SequentialSearch<Key, Value> {

	private Node first;
	int Size;
	
	private class Node
	{
		Key key;
		Value val;
		Node next;

		public Node(Key key, Value val, Node next)
		{
			this.key=key;
			this.val=val;
			this.next=next;
		}
	}

	public Value get(Key key)
	{
		Node aux=first;
		while(aux!=null)
		{

			if(key.equals(aux.key))
				return aux.val;

			aux=aux.next;
		}
		return null;
	}

	public boolean put(Key key, Value val)
	{
		boolean inserto=true;
		Node aux = first;
		
		while(aux!=null)
		{
			if(key.equals(aux.key))
			{
				aux.val=val;
				inserto=false;
				return inserto;
			}
			aux=aux.next;
		}
		first=new Node(key, val, first);
		Size++;
		return inserto;
	}

	public int Size()
	{
		return Size;
	}

	private Node getNode(int n)throws Exception {

		if(n >=Size)
		{
			throw new Exception("No existe el elemento buscado");

		}
		else{
			Node aux =first;

			for (int i=0 ; i<n;i++)
			{
				aux=aux.next;

			}

			return aux;
		}
	}

	public Value getElement(int n) {

		try{
			
			Node node1= getNode(n);
			Value node2= node1.val;
			
			return node2;
		}
		catch (Exception e){
			return null;
		}

	}

	public void delete(int n) {

		try{
			Node  node1=getNode(n);

			Node nodo2= node1.next;

			if(n>0)
			{
				Node node3=getNode(n-1);
				node3.next=nodo2;
			}
			else
			{
				first=nodo2;
			}

			Size--;
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}



}