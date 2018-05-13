package model.data_structures;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Lista  <T extends Comparable<T>> implements IList<T> 
{
	@JsonProperty
	private Node<T> first, last;
	@JsonProperty
	private int size;
	private Iterator<T> iter;

	public Lista()
	{
		first=null;
		last=null;
		size=0;

	}


	public boolean add (T pElem , int pPos)
	{
		Node<T> aux = new Node<T>(pElem, null, null);   
		if (pPos <= 0)
		{
			if(first == null)
			{
				first = aux;
				last = first;
				iter = new Iterator<T>(first);
			}
			else
			{
				first.setPrevious(aux);
				aux.setNext(first);
				first = aux;
			}
			size++;
			return true;
		}
		else if (pPos >= size)
		{
			{   
				if(first == null)
				{
					first = aux;
					last = aux;
				}
				else
				{
					aux.setPrevious(last);
					last.setNext(aux);
					last = aux;
				}
				size++;
				return true;
			}
		}
		else
		{
			Node<T> chk = first;
			for (int i = 1; i <= size; i++)
			{
				if (i == pPos)
				{
					Node<T> tmp = chk.next();
					chk.setNext(aux);
					aux.setPrevious(chk);
					aux.setNext(tmp);
					tmp.setPrevious(aux);
					size++;
					return true;
				}
				chk = chk.next();            
			}

		}
		return false;
	}            

	public void addAtEnd(T t) {
		Node n=  new Node(t,null,last);
		if (size==0)
		{			
			first=n;
			last=first;

		}
		else{

			last.setNext(n);
			last=n;

		}
		size ++;
	}
	
	public T add (T pElem)
	{
		Node<T> aux = new Node<T>(pElem, null, null);
		if(first == null)
		{
			first = aux;
			last = first;
			iter = new Iterator<T>(first);
		}
		else
		{
			T elemRepeated = repeated(pElem);
			if (elemRepeated!=null)
			{
				return elemRepeated;
			}

			first.setPrevious(aux);
			aux.setNext(first);
			first = aux;
		}
		size++;
		return null;
	}

	public void addRepeated (T pElem)	{
		Node<T> aux = new Node<T>(pElem, null, null);
		if(first == null)
		{
			first = aux;
			last = first;
			iter = new Iterator<T>(first);
		}
		else
		{
			first.setPrevious(aux);
			aux.setNext(first);
			last = first;
			first = aux;

		}
		size++;
	}


	public T addInOrder (T pElem)
	{
		Node<T> aux = new Node<T>();
		aux.setElem(pElem);
		if(first == null)
		{
			first = aux;
			last = first;
			iter = new Iterator<T>(first);
		}
		else
		{
			Node<T> chk =  first;
			while (chk.elem().compareTo(aux.elem())>0 && chk!=last )
			{
				chk = chk.next()!=null? chk.next() : last ;
			}
			if (chk.elem().compareTo(aux.elem())==0)
			{
				return chk.elem();	
			}
			else if( chk.elem().compareTo(aux.elem())<0 && chk.elem().compareTo(first.elem())==0)
			{
				chk.setPrevious(aux);
				aux.setNext(chk);
				first = aux;
				//	last = chk;
			}
			else if (chk.elem().compareTo(aux.elem())<0)
			{
				Node<T> prevChk = chk.previous();
				chk.setPrevious(aux);
				aux.setNext(chk);
				aux.setPrevious(prevChk);
				prevChk.setNext(aux);
				//last = chk;
			}
			else
			{	
				chk.setNext(aux);
				aux.setPrevious(chk);
				last = aux;
			}


		}
		size++;
		return null;
	}

	public T addInOrderRepeated (T pElem)
	{
		Node<T> aux = new Node<T>();
		aux.setElem(pElem);
		if(first == null)
		{
			first = aux;
			last = first;
			iter = new Iterator<T>(first);
		}
		else
		{
			Node<T> chk =  first;
			while (chk.elem().compareTo(aux.elem())>=0 && chk!=last )
			{
				chk = chk.next()!=null? chk.next() : last ;
			}
			if( chk.elem().compareTo(aux.elem())<0 && chk.elem().compareTo(first.elem())==0)
			{
				aux.setNext(chk);
				chk.setPrevious(aux);
				first = aux;
				//	last = chk;
			}
			else if (chk.elem().compareTo(aux.elem())==0 && chk.elem().compareTo(last.elem())==0)
			{

				last.setNext(aux);
				aux.setPrevious(last);
				last = aux;
			}
			else if (chk.elem().compareTo(aux.elem())==0)
			{
				Node<T> nextChk = chk.next();
				aux.setNext(nextChk);
				aux.setPrevious(chk);
				chk.setNext(aux);
			}
			else if (chk.elem().compareTo(aux.elem())<0)
			{
				Node<T> prevChk = chk.previous();
				chk.setPrevious(aux);
				aux.setNext(chk);
				prevChk.setNext(aux);
				aux.setPrevious(prevChk);
				//last = chk;
			}
			else
			{	
				last.setNext(aux);
				aux.setPrevious(last);
				last = aux;
			}
		}
		size++;
		return null;
	}



	public boolean delete(T elem2) 
	{        
		Node<T> chk = first;
		for (int i=0;i<size;i++)
		{

			if (chk.elem()==elem2)
			{
				if (chk.hasPrevious())
				{
					Node<T> aux = chk.next();
					chk.previous().setNext(aux);
					size--;
					return true;
				}
				else
				{
					size--;
					first = chk.next();
					return true;
				}
			}
			chk = chk.next();
		}
		return false;
	}    

	public T repeated (T pElem)
	{
		T resp = binarySearch(pElem);

		return resp;
	}


	public T get(int pos) 
	{
		if (pos < 0)
			return null;
		Node<T> chk = first;
		if (chk!= null) 
		{
			for (int i=0;i<size();i++)
			{
				if(chk.next()==null && i+1==pos)
				{
					return null;
				}
				else if(i==pos)
				{
					return chk.elem();
				}

				chk = chk.next();
			}

		}
		return null;
	}


	public int size() 
	{
		return size;
	}

	public java.util.Iterator<T> iterator() 
	{
		return (java.util.Iterator<T>) iter;
	}

	//public T getCurrent() 
//	{
		//return iter.actual().elem();
	//}


	public T next() 
	{
		return iter.next();
	}

	public boolean isEmpty()
	{
		return size==0? true:false;
	}

	public T binarySearch(T x)
	{

		int low = 0;
		int high = size - 1;

		while(high >= low) {
			int middle = (low + high) / 2;
			T actual = (T) get(middle);
			if(x.compareTo(actual)==0) {
				return (actual);
			}
			if(x.compareTo(actual) < 0) {
				low = middle + 1;
			}
			if(x.compareTo(actual) > 0) {
				high = middle - 1;
			}
		}
		return null;
	}


	@Override
	public T get(T elem) {
		return binarySearch(elem);
	}



}