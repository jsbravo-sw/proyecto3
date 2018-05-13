package model.data_structures;

import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonProperty;

public class hashTableSeparateChaining<Key,Value> {

	@JsonProperty 
	private int tamanio;
	@JsonProperty 
	private int N=0;
	@JsonProperty 
	private SequentialSearch<Key,Value>[] listas;
	@JsonProperty
	private Lista<Keys<Key>> ListKeys;  

	public hashTableSeparateChaining(int M)
	{
		this.tamanio = M;
		listas =  new SequentialSearch[M];
		ListKeys = new Lista<>();
		for (int i=0; i<M; i++)
		{
			listas[i]=new SequentialSearch<Key,Value>();
		}

	}


	private int hash(Key key)
	{
		return(key.hashCode() & 0x7fffffff)%tamanio; 

	}

	public Value get (Key key)
	{
		return (Value) listas[hash(key)].get(key);

	}

	public void put (Key key,Value value)
	{

		boolean a=listas[hash(key)].put(key, value);

		if(a==true)
		{
			N++;
			Keys<Key> AuxKey = new Keys<Key>(key);
			ListKeys.addRepeated(AuxKey);	
		}
		if(N/tamanio>=6)
		{
			rehash();
		}

	}

	public int size()
	{
		return tamanio;
	}
	//Retorna la cantidad de keys que hay
	public int data()
	{
		return ListKeys.size();
	}
	
	public int primeNumber(int pNum)
	{
		pNum++;
	    for(int i=2;i<pNum;i++) 
	    {
	        if(pNum % i ==0  ) 
	        {
	            pNum++;
	            i=2;
	        }
	    }
	    return pNum;
	}

	public void rehash()
	{    

		hashTableSeparateChaining<Key, Value> nuevaHashTable=new hashTableSeparateChaining<Key,Value>(primeNumber(tamanio*2));

		for (int i=0; i<N; i++)
		{
			Key key= (Key) ListKeys.get(i);
			Value V1 = get(key);
			nuevaHashTable.put(key, V1);

		}

		listas = nuevaHashTable.lista();
		tamanio = listas.length;


	}
	public SequentialSearch<Key,Value>[] lista()
	{
		return listas;
	}

	public Lista<Keys<Key>> keys()
	{
		return ListKeys;
	}

	public int M()
	{
		return tamanio;
	}


	public Value Delete(Key key){

		SequentialSearch<Key, Value> l=listas[hash(key)];
		Value valor= l.get(key);
		int m=0;
		boolean elimino=false;

		while(elimino==false && m<l.Size())
		{ 
			Value AuxValor= l.getElement(m);

			if(AuxValor==valor)
			{
				elimino=true;
			}
			else
			{
				m++;
			}
		}

		l.delete(m);
		return valor;

	}
	public boolean contains(Key key)
	{
		Keys<Key> AuxKey = new Keys<Key>(key);
		
		for (int i = 0; i < ListKeys.size(); i++) {
			
			if(ListKeys.get(i).compareTo(AuxKey) == 0)
			{
				return true;
			}
		}

		return false;
	}
}
