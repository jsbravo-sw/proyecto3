package model.data_structures;


/*
 * -----
 * Clase que contiene un arreglo [] Heap -> pos(0)=null
 * Método sort() basado en https://www.geeksforgeeks.org/heap-sort/
 *-----
 */

public class Heap <T extends Comparable<T>> 
{
	private T arr [];
	private int size;
	
	@SuppressWarnings("unchecked")
	public Heap(int pSize)
	{
		size = pSize+1;
		arr = (T[]) new Object [size];

	}
	@SuppressWarnings("unchecked")
	public Heap (Lista<T> pArr)
	{
		size = pArr.size()+1;
		arr = (T[]) new Comparable[size];
		for (int i=0; i<pArr.size();i++)
		{
			arr[i+1] = pArr.get(i);
		}
	}

	public void sort()
	{
		int n = arr.length;

		for (int i = (n / 2); i >0; i--)
		{
			// Creo el heap
			crearHeap(n, i+1);
		}

		for (int i=n-1; i>0; i--)
		{
			// Muevo la raz al final
			T temp = arr[1];
			arr[1] = arr[i];
			arr[i] = temp;

			// Ordeno de nuevo desde la posicin 1 (pos(0)==null) el rbol de tamao i.
			crearHeap(i, 1);
		}
	}

	// Ordenar un rbol binario donde n es el tamao del sub
	private void crearHeap(int pTamanio, int pIndice)
	{
		int raiz = pIndice;  // El mayor debera ser la raz
		int hijoIzquierdo = 2*pIndice;  // El primer hijo es 2i
		int hijoDerecho = 2*pIndice + 1;  // El segundo hijo es 2i+1
		// Si el hijo izquierdo es mayor, entonces el mayor es el hijo
		if (hijoIzquierdo < pTamanio && arr[hijoIzquierdo].compareTo(arr[raiz])>0)
			raiz = hijoIzquierdo;

		// Si el hijo derecho es mayor, entonces el mayor  es el hijo

		if (hijoDerecho < pTamanio && arr[hijoDerecho].compareTo(arr[raiz])>0)
			raiz = hijoDerecho;

		// Si el mayor ya no es la raz
		if (raiz != pIndice)
		{
			// Hago sink, cambiando el mayor por el hijo y viceversa.
			T swap = arr[pIndice];
			arr[pIndice] = arr[raiz];
			arr[raiz] = swap;

			// Vuelvo a revisar en el rbol binario restante
			crearHeap(pTamanio, raiz);
		}
	}

	@SuppressWarnings("unchecked")
	public T[] arr()
	{
		return (T[]) arr;
	}

	public int size()
	{
		return size-1;
	}
	public Lista<T> list()
	{
		Lista<T> resp = new Lista<T>();
		for (int i=1;i<size;i++)
		{
			resp.addRepeated(arr[i]);
		}
		return resp;
	}
}
