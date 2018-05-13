package model.data_structures;

public class hashTableLinearProbing<Key, Value> {

	private int tamanio;
	private int N;
	private Key[] keys;
	private Value[] values;


	public hashTableLinearProbing(int M)
	{
		this.tamanio=M;
		keys= (Key[]) new Object[M];
		values = (Value[]) new Object[M];

	}

	private int hash(Key key)
	{
		return (key.hashCode() & 0x7fffffff) % tamanio;
	}

	public void rehash(int s)
	{
		hashTableLinearProbing<Key, Value> nuevo= new hashTableLinearProbing<Key, Value>(s);

		for (int i = 0; i<tamanio; i++)
		{
			if(keys[i]!=null)
			{
				nuevo.put(keys[i], values[i]);
			}
		}
		keys=nuevo.keys;
		values=nuevo.values;
		tamanio=nuevo.tamanio;
	}

	public void put(Key key, Value val)
	{
		if(N/tamanio>0.5) 
			rehash(tamanio*2);

		int i;
		for (i= hash(key); keys[i]!=null; i=(i+1)%tamanio)
		{
			if(keys[i].equals(key))
			{
				values[i]=(Value) val;
				return;
			}
		}
		
		keys[i]=key;
		values[i]=(Value) val;
		N++;

	}


	public Value get(Key key)
	{
		if(!contains(key)) 
		{
			return null;
		}
		for (int i=hash(key); keys[i]!=null; i=(i+1)%tamanio)
		{
			if(keys[i].equals(key)) 	
			{
				return (Value) values[i];			
			}
		}
		
		return null;
	}

	public Value delete(Key key)
	{
		if(!contains(key)) 
			return null;
		
		int i =hash(key);
		while(keys[i]!=null)
		{
			if(keys[i].equals(key)) 
				break;
			
			i=(i+1)%tamanio;
		}
		
		Value value = values[i];
		keys[i]=null;
		values[i]=null;
		i=(i+1)%tamanio;
		
		while(keys[i]!=null)
		{
			Key keyTemp = keys[i];
			Value valueTemp = values[i];
			keys[i] = null;
			values[i] = null;
			N--;
			put(keyTemp, valueTemp);
			i=(i+1)%tamanio;
		}
		N--;
		
		return value;
	}

	public Lista<Keys<Key>> keys()
	{
		Lista<Keys<Key>> st=new Lista<>();

		for (int i=0; i<keys.length; i++)
		{
			if(keys[i]!=null) 
			{
				Keys<Key> AuxKey = new Keys(keys[i]);
				st.addRepeated(AuxKey);
			}
		}

		return st;
	}

	public boolean contains(Key key)
	{
		int i=hash(key);
		while(keys[i]!=null)
		{	
			if(keys[i].equals(key)) 
				return true;

			i=(i+1)%tamanio;	
		}
		
		return false;
	}

	public int size()
	{
		return tamanio;
	}
}