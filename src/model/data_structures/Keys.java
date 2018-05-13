package model.data_structures;


public class Keys<Key> implements Comparable<Keys<Key>>{

	private Key key;
	
	public Keys(Key pKey) {
		key = pKey;
	}

	public Key getKey()
	{
		return key;
	}
	
	@Override
	public int compareTo(Keys o) {
		
		if(key.equals(o.getKey()))
		{
			return 0;
		}
		return -1;
	}

}
