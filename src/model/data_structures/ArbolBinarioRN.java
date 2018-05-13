package model.data_structures;

import java.util.NoSuchElementException;

import jdk.nashorn.internal.ir.Node;



public class ArbolBinarioRN <Key extends Comparable<Key>, Value>
{

	//-------------------------------------------------
	// CONSTANTES
	//-------------------------------------------------
	private static final boolean RED = true;
	private static final boolean BLACK = false;	

	//-------------------------------------------------
	// ATRIBUTOS
	//-------------------------------------------------
	private Node root;
	private Lista<Key> keys;
	private int avg;

	private class Node
	{
		private Key key;
		private Value value;
		private Node left, right;
		private boolean color;
		private int size;

		public Node(Key pKey, Value pValue, boolean pColor, int pSize)
		{
			this.key = pKey;
			this.value = pValue;
			this.color = pColor;
			this.size = pSize;
		}

	}

	public ArbolBinarioRN()
	{
avg=0;
keys = new Lista<>();
	}
	

	private boolean isRed(Node x)
	{
		if(x==null)
			return false;

		return x.color==RED;
	}

	public int size(Node x)
	{
		if(x==null)
			return 0;

		return x.size;
	}
	public int size()
	{
		return size(root);
	}

	public boolean isEmpty()
	{
		return root == null;
	}

	public Key min()
	{
		return Min(root).key;

	}

	public Key max()
	{
		return Max(root).key;		
	}

	public Value get(Key key)
	{
		if(key == null)
			throw new NullPointerException("Argument to get () is null");

		return get(root,key);
	}

	private Value get(Node x, Key key)
	{
		while(x != null)
		{
			int cmp = key.compareTo(x.key);
			avg++;
			if(cmp<0)
				x = x.left;
			else if (cmp>0)
				x = x.right;
			else
				return x.value;
		}

		return null;
	}

	public boolean contains(Key key)
	{
		return get(key) != null;
	}


	public void put(Key pKey,Value pValue)
	{
		if(pKey == null )
			throw new NullPointerException("First argument to put() is null");
		if(pValue == null)
		{
			delete(pKey);
			return;
		}

		root = put(root,pKey, pValue);
		root.color = BLACK;
		keys.add(pKey);
	}

	private Node put(Node h, Key pKey, Value pValue)
	{
		if( h == null)
			return new Node (pKey, pValue, RED, 1);

		int cmp = pKey.compareTo(h.key);
		if(cmp<0)
			h.left = put(h.left, pKey, pValue);
		else if(cmp>0)
			h.right = put(h.right, pKey, pValue);
		else
			h.value = pValue;

		if(isRed(h.right) && !isRed(h.left))
			h = rotateLeft(h);
		if(isRed(h.left) && isRed(h.left.left))
			h = rotateRight(h);
		if(isRed(h.right) && isRed(h.right))
			flipColors(h);

		h.size = size(h.left) + size(h.right) + 1;

		return h;
	}



	public void deleteMin()
	{
		if(isEmpty())
			throw new NoSuchElementException("BST underflow");
		if(!isRed(root.left) && !isRed(root.right))
			root.color = RED;

		root = deleteMin(root);
		if(!isEmpty())
			root.color = BLACK;
	}

	private Node deleteMin(Node h)
	{
		if(h.left == null)
			return null;

		if(!isRed(h.left) && !isRed(h.left.left))
			h = moveRedLeft(h);

		h.left = deleteMin(h.left);
		return balance(h);
	}

	public void deleteMax()
	{
		if(isEmpty())
			throw new NoSuchElementException("BST underflow");
		if(!isRed(root.left) && !isRed(root.right))
			root.color = RED;

		root = deleteMax(root);
		if(!isEmpty())
			root.color = BLACK;
	}

	private Node deleteMax(Node h)
	{
		if(isRed(h.left))
			h = rotateRight(h);

		if(h.right == null)
			return null;

		if(!isRed(h.right) && !isRed(h.right.left))
			h = moveRedRight(h);

		h.right = deleteMax(h.right);

		return balance(h);
	}

	public void delete(Key pKey)
	{
		if(pKey == null)
			throw new NullPointerException("argument to delete() is null");
		if(!contains(pKey))
			return;

		if(!isRed(root.left) && !isRed(root.right))
			root.color = RED;

		root = delete(root, pKey);
		if(!isEmpty())
			root.color = BLACK;
	}

	/**
	 * search the height of the given key. -1 if was not found.
	 * @param pKey the key.
	 * @return The height of the key.
	 */
	public int getHeight(Key pKey)
	{
		if (pKey == null)
			throw new NullPointerException("The given parameter is null");
		else
			return getHeight(root, pKey, 0);
	}

	/**
	 * Search the height of the searched key in the subtree given by the actualNode.
	 * @param actualNode actually, it always start with the root
	 * @param pKey searched key.
	 * @return Height of the searched key. -1 if it was not found.
	 */
	private int getHeight(Node actualNode, Key pKey, int height)
	{	
		while (actualNode != null)
		{
			if (pKey.compareTo(actualNode.key) < 0)
			{
				actualNode = actualNode.left;
				height++;

			}
			else if(pKey.compareTo(actualNode.key) > 0)
			{
				actualNode = actualNode.right;
				height++;
			}
			else
				return height;
		}
		return -1;
	}
	
	public boolean check() {
        return isBST() && isSizeConsistent() && isRankConsistent() && is23();
    }
    // does this binary tree satisfy symmetric order?
    // Note: this test also ensures that data structure is a binary tree since order is strict
    private boolean isBST() {
        return isBST(root, null, null);
    }

    // is the tree rooted at x a BST with all keys strictly between min and max
    // (if min or max is null, treat as empty constraint)
    // Credit: Bob Dondero's elegant solution
    private boolean isBST(Node x, Key min, Key max) {
        if (x == null) return true;
        if (min != null && x.key.compareTo(min) <= 0) return false;
        if (max != null && x.key.compareTo(max) >= 0) return false;
        return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
    } 

    // are the size fields correct?
    private boolean isSizeConsistent() { return isSizeConsistent(root); }
    private boolean isSizeConsistent(Node x) {
        if (x == null) return true;
        if (x.size != size(x.left) + size(x.right) + 1) return false;
        return isSizeConsistent(x.left) && isSizeConsistent(x.right);
    } 

    // check that ranks are consistent
    private boolean isRankConsistent() {
        for (int i = 0; i < size(); i++)
            if (i != rank(select(i))) 
            	return false;
        for (int i=0;i<keys.size();i++)
            if (keys.get(i).compareTo(select(rank(keys.get(i)))) != 0) 
            	return false;
        return true;
    }

    // Does the tree have no red right links, and at most one (left)
    // red links in a row on any path?
    private boolean is23() { return is23(root); }
    private boolean is23(Node x) {
        if (x == null) return true;
        if (isRed(x.right)) return false;
        if (x != root && isRed(x) && isRed(x.left))
            return false;
        return is23(x.left) && is23(x.right);
    } 
    
	/**
     * Return the key in the symbol table whose rank is {@code k}.
     * This is the (k+1)st smallest key in the symbol table. 
     *
     * @param  k the order statistic
     * @return the key in the symbol table of rank {@code k}
     * @throws IllegalArgumentException unless {@code k} is between 0 and
     *        <em>n</em>–1
     */
    public Key select(int k) {
        if (k < 0 || k >= size()) {
            throw new IllegalArgumentException("argument to select() is invalid: " + k);
        }
        Node x = select(root, k);
        return x.key;
    }

    // the key of rank k in the subtree rooted at x
    private Node select(Node x, int k) {
        // assert x != null;
        // assert k >= 0 && k < size(x);
        int t = size(x.left); 
        if      (t > k) return select(x.left,  k); 
        else if (t < k) return select(x.right, k-t-1); 
        else            return x; 
    } 
    
    /**
     * Return the number of keys in the symbol table strictly less than {@code key}.
     * @param key the key
     * @return the number of keys in the symbol table strictly less than {@code key}
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public int rank(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to rank() is null");
        return rank(key, root);
    } 

    // number of keys less than key in the subtree rooted at x
    private int rank(Key key, Node x) {
        if (x == null) return 0; 
        int cmp = key.compareTo(x.key); 
        if      (cmp < 0) return rank(key, x.left); 
        else if (cmp > 0) return 1 + size(x.left) + rank(key, x.right); 
        else              return size(x.left); 
    } 
    
	public int height()
	{
		int h=0;
		h=height(root,h);

		return h-1;
	}

	public Iterable<Key>keysInRange(Key a,Key b) 
	{
		Lista<Key>k=new Lista<Key>();
		RangeK(root, k, a, b);
		return k;
	}

	private void RangeK(Node A, Lista<Key>List, Key a, Key b)
	{
		if (A == null)
			return;

		int low = a.compareTo(A.key);
		int high = b.compareTo(A.key);

		if (low < 0)
			RangeK(A.left, List, a, b);

		if (low <= 0 && high >= 0) 
			List.addAtEnd(A.key);

		if (high > 0)
			RangeK(A.right, List, a, b);
	}

	public Iterable<Value>valuesInRange(Key a,Key b) 
	{
		Lista k=new Lista<>();
		RangeV(root, k, a, b);
		return k;
	}

	private void RangeV(Node A, Lista List, Key a, Key b)
	{
		if (A == null) 
			return;

		int low = a.compareTo( A.key);
		int high = b.compareTo(A.key);

		if (low < 0)
			RangeV(A.left, List, a, b);

		if (low <= 0 && high >= 0)
			List.addAtEnd((Comparable) A.value);

		if (high > 0)
			RangeV(A.right, List, a, b);
	}
	
	public Iterable<Key	> keys()
	{
		return keys;
	}

	private int height(Node node,int num)
	{ 
		if(node==null)
			return 0;

		num=num+height(node.right,num);
		if(node.color==false)
		{
			num++;
		}
		return num;
	}
	
	//RedBlack maxHeight es 2log2(n+1) 
	public int maxHeight()
	{ 
		return (int) Math.ceil(2*Math.log(size()+1)/Math.log(2));
	}
	//2-3 maxHeight es 1 + log2n
	public int maxHeight23()
	{ 
		return (int) Math.ceil(Math.log(size())/Math.log(2))+1;
	}
	//2-3 minHeight es 1 + log3n
	public int minHeight23()
	{ 
		return (int) Math.ceil(Math.log(size())/Math.log(3))+1;
	}
	private Node delete(Node h, Key pKey)
	{
		if(pKey.compareTo(h.key)<0)
		{
			if(!isRed(h.left) && !isRed(h.left.left))
				h = moveRedLeft(h);
			h.left = delete(h.left, pKey);
		}
		else
		{
			if(isRed(h.left))
				h = rotateRight(h);
			if(pKey.compareTo(h.key) == 0 && (h.right == null))
				return null;
			if(!isRed(h.right) && !isRed(h.right.left))
				h = moveRedRight(h);
			if(pKey.compareTo(h.key) == 0)
			{
				Node x = Min(h.right);
				h.key = x.key;
				h.value = x.value;
				h.right = deleteMin(h.right);
			}
			else
				h.right = delete(h.right, pKey);
		}
		return balance(h);
	}



	private Node rotateRight(Node h)
	{
		Node x = h.left;
		h.left = x.right;
		x.right = h;
		x.color = x.right.color;
		x.right.color = RED;
		x.size = h.size;
		h.size = size(h.left) + size(h.right) + 1;
		return x ;
	}

	private Node rotateLeft(Node h)
	{
		Node x = h.right;
		h.right = x.left;
		x.left = h;
		x.color = x.left.color;
		x.left.color = RED;
		x.size = h.size;
		h.size = size(h.left) + size(h.right) + 1;
		return x;
	}

	private void flipColors(Node h)
	{
		h.color = !h.color;
		h.left.color = !h.left.color;
		h.right.color = !h.right.color;
	}

	private Node moveRedLeft(Node h)
	{
		flipColors(h);
		if(isRed(h.right.left))
		{
			h.right = rotateRight(h.right);
			h = rotateLeft(h);
			flipColors(h);
		}
		return h ;
	}

	private Node moveRedRight(Node h)
	{
		flipColors(h);
		if(isRed(h.left.left))
		{
			h = rotateRight(h);
			flipColors(h);
		}
		return h;
	}

	private Node balance(Node h)
	{
		if(isRed(h.right))
			h = rotateLeft(h);
		if(isRed(h.left) && isRed(h.left.left))
			h = rotateRight(h);
		if(isRed(h.left) && isRed(h.right))
			flipColors(h);

		h.size = size(h.left) + size(h.right) + 1;
		return h;
	}

	private Node Min(Node a)
	{ 
		if( a.left ==null)
			return a;

		return Min(a.left);
	}

	private Node Max(Node a)
	{ 
		if( a.right ==null)
			return a;

		return Max(a.right);
	}
	
	public int treeHeight(Node n) {
	    if (n == null) return -1;
	    return Math.max(treeHeight(n.left), treeHeight(n.right)) + 1;
	}
	public int heightA() {
	    return treeHeight(root);
	}
	public int searchAvg()
	{
		int temp = 0;
		int veces = 0;
		avg = 0;
		for (int i =0;i<size();i++)
		{
			get(keys.get(i));
			temp+=avg;
			avg = 0;
			veces++;
		}
		return (int) Math.ceil(temp/veces);
	}
	public Lista<Key> getKeys()
	{
		return keys;
	}
}

