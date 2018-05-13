package model.data_structures.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import model.data_structures.Lista;


public class ListaTest<T extends Comparable <T>> 
{
	private Lista<T> list;
	private T elem = (T) "a";
	private T elem2 = (T) "b";
	private T elem3 = (T) "c";
	private T elem4 = (T) "d";

	@Before
	public void testSetup( )
	{
		list = new Lista<T>();
	}

	public void testSetup2( )
	{
		list = new Lista<T>();
		list.addInOrder(elem);
		list.addInOrder(elem2);
		list.addInOrder(elem3);
		
		//list.add(elem2, 1);
		//list.add(elem3, 2);
	}

	@Test
	public void linkedListTest( )
	{
		assertEquals( "La lista no se inicializa correctamente", list, list );	
	}

	@Test
	public void sizeTest( )
	{
		testSetup2();
		assertEquals( "El tama�o de la lista no se halla correctamente", 3, list.size() );	
	}

	@Test
	public void addTest( )
	{
		assertEquals( "El elemento no se a�ade correctamente", 0, list.size() );
		list.add(elem, 0);
		assertEquals( "El elemento no se a�ade correctamente", 1, list.size() );
		testSetup2();
		assertEquals("El elemento es incorrecto",elem3 , list.get(0));
		assertEquals("El elemento es incorrecto",elem2 , list.get(1));
	}

	@Test
	public void addInOrderTest( )
	{
		assertEquals( "El elemento no se a�ade correctamente", 0, list.size() );
		list.addInOrder(elem2);
		assertEquals( "El elemento no se a�ade correctamente", 1, list.size() );
		
		assertEquals( "El elemento no se a�ade correctamente", elem2 , list.get(0) );
		list.addInOrder(elem4);
		assertEquals( "El elemento no se a�ade correctamente", "d", list.get(0));
		
		list.addInOrder(elem3);
		list.addInOrder(elem2);
		
	System.out.println(list.addInOrder(elem2));
	System.out.println("addInOrderTest: ");
	System.out.println("-----------------------");	
	for (int i=0;i<list.size();i++)
		{

		System.out.println(list.get(i));

		}
	System.out.println("-----------------------");	
		assertEquals( "El elemento no se a�ade correctamente", 3, list.size());
		assertEquals( "El elemento no se a�ade correctamente", "c", list.get(1));
		assertEquals( "El elemento no se a�ade correctamente", "b", list.get(2));
	}
	
	@Test
	public void repeatedTest( )
	{
		testSetup2();
		list.addInOrder(elem);
		System.out.println("repeatedTest: ");
		System.out.println("-----------------------");	
		for (int i=0;i<list.size();i++)
		{
		
			System.out.println(list.get(i));
		
		}
		System.out.println("-----------------------");	
		System.out.println("Se espera la lista: ");	
		System.out.println("a");	
		System.out.println("b");	
		System.out.println("c");	
		assertEquals( "El elemento repetido no deb�a agregarse", 3, list.size());
		assertEquals( "Debi� encontrarse el elemento repetido", "a", list.repeated(elem));
		assertEquals( "Debi� encontrarse el elemento repetido", "b", list.repeated(elem2));
		assertEquals( "Debi� encontrarse el elemento repetido", "c", list.repeated(elem3));
	}

	@Test
	public void deleteTest( )
	{
		testSetup2();
		list.delete(elem2);
		assertEquals( "El elemento no se elimina correctamente", 2, list.size() );
	}

	@Test
	public void getTest( )
	{
		testSetup2();
		T nullResp = list.get(4);
		T resp = list.get(0);
		assertEquals( "El elemento no se retorna por posici�n correctamente", null, nullResp );
		assertEquals( "El elemento no se retorna por posici�n correctamente", resp ,elem3 );
	}

	@Test
	public void isEmptyTest( )
	{
		assertEquals( "El m�todo no retorna correctamente si la lista est� vacia", true, list.isEmpty() );
		testSetup2();
		assertEquals( "El m�todo no retorna correctamente si la lista est� vacia", false, list.isEmpty() );
	}
	
	@Test
	public void binarySearch()
	{		
		testSetup2();
		T resp = list.binarySearch(elem);
		assertNotNull(resp);
		T resp2 = list.binarySearch(elem2);
		assertNotNull(resp2);
		T resp3 = list.binarySearch(elem3);
		assertNotNull(resp3);
	}
	
	@Test
	public void addInOrderRepeated()
	{
		Lista<Integer> lista = new Lista<Integer>();
		System.out.println("___________");
		lista.addInOrderRepeated(2);
		System.out.println("A�ad� 2");
		System.out.println(lista.get(0));
		lista.addInOrderRepeated(2);
		System.out.println("A�ad� 2");
		System.out.println(lista.get(0));
		System.out.println(lista.get(1));
		lista.addInOrderRepeated(2);
		System.out.println("A�ad� 2");
		System.out.println(lista.get(0));
		System.out.println(lista.get(1));
		System.out.println(lista.get(2));
		lista.addInOrderRepeated(1);
		System.out.println("A�ad� 1");
		System.out.println(lista.get(0));
		System.out.println(lista.get(1));
		System.out.println(lista.get(2));
		System.out.println(lista.get(3));
		lista.addInOrderRepeated(0);
		System.out.println("A�ad� 0");
		System.out.println(lista.get(0));
		System.out.println(lista.get(1));
		System.out.println(lista.get(2));
		System.out.println(lista.get(3));
		System.out.println(lista.get(4));
		
		
	}




}
