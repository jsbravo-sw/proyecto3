package model.data_structures.test;

import org.junit.Test;

import junit.framework.TestCase;
import model.data_structures.ArbolBinarioRN;
import model.data_structures.Lista;

public class ArbolBinarioRNTest extends TestCase{

	ArbolBinarioRN<Integer,Integer> root = new ArbolBinarioRN<Integer,Integer>();

	Integer[] number=new Integer[10];

	public void SetupEscenario1()
	{

		number[1]=61;
		number[0]=51;
		number[2]=123;
		number[3]=9621;
		number[4]=652;
		number[5]=20;
		number[6]=21;
		number[7]=143;
		number[8]=40;
		number[9]=416;

		for (int i=0;i<10;i++)
		{
			root.put(number[i], number[i]*7);

		}
	}

	public void testPut() 
	{
		SetupEscenario1();
		assertNotNull(root);
		assertEquals(10, root.size());}

	public void testmax()
	{
		SetupEscenario1();
		assertEquals(9621,(int) root.max());
	}

	public void testmin()
	{
		SetupEscenario1();
		assertEquals(20,(int) root.min());
	}

	public void testDeleteMax()
	{
		SetupEscenario1();
		root.deleteMax();
		root.deleteMax();
		assertEquals(416,(int)root.max() );
	}

	public void testDeleteMin()
	{
		SetupEscenario1();
		root.deleteMin();
		root.deleteMin();
		assertEquals(40,(int)root.min() );
	}


	public void testGet()
	{
		SetupEscenario1();
		assertEquals(51*7, (int)root.get(51));
		assertEquals(9621*7, (int)root.get(9621));

	}

	public void testDelete ()
	{
		SetupEscenario1();
		root.delete(143);
		assertEquals(9 ,(int)root.size());

		try 
		{
			root.delete(143);
		}
		catch(Exception e)
		{
			System.out.println("ERROR" +"no se encuentra el elemento  eliminar");
		}
		assertEquals(9 ,(int)root.size());
		root.delete(20);
		assertEquals(21 ,(int)root.min());

	}

	public void testcontains()
	{
		SetupEscenario1();
		assertTrue(root.contains(21));
		assertTrue(root.contains(416));

		assertFalse(root.contains(999));
		assertFalse(root.contains(7999));

	}

	public void testHeight()
	{
		SetupEscenario1();
		assertEquals(2 ,root.height());

		ArbolBinarioRN<Integer,Integer>arbolAux=new ArbolBinarioRN<Integer,Integer>();

		arbolAux.put(64, 0);
		assertEquals(0,arbolAux.height());
		arbolAux.put(32, 0);
		assertEquals(0,arbolAux.height());
		arbolAux.put(96, 0);
		assertEquals(1,arbolAux.height());

	}

	public void testRangeK() 
	{
		SetupEscenario1();
		Lista<Integer> listaAux= (Lista<Integer>)root.keysInRange(1000, 10000);
		
		for(int i=0;i<listaAux.size();i++)
		{
			assertEquals(1 ,(int)listaAux.size());
			assertEquals(9621 ,(int)listaAux.get(0));
		}
	}

	public void testRangeV() 
	{
		SetupEscenario1();
		Lista<Integer> listaAux= (Lista<Integer>)root.valuesInRange(10,100);
		
		for(int i=0;i<listaAux.size();i++)
		{   
			assertEquals(5 ,(int)listaAux.size());
//			System.out.println("value");
//			System.out.println(listaAux.get(0));
//			System.out.println(listaAux.get(1));
//			System.out.println(listaAux.get(2));
//			System.out.println(listaAux.get(3));
//			System.out.println(listaAux.get(4));
		}
	}
	
	@Test
	public void testGetHeight()
	{
		SetupEscenario1();
		root.put(9, 9);
		assertEquals(2, root.getHeight(51));
		assertEquals(3, root.getHeight(9));
	}
	
	@Test
	public void testCheck()
	{
		SetupEscenario1();
		assertEquals(true, root.check());
	}
	
	public void testIsEmpty()
	{
		ArbolBinarioRN<String, String> a = new ArbolBinarioRN<String, String>();
		assertEquals(true,  a.isEmpty());
		a.put("H", "C");
		assertEquals(false, a.isEmpty());
	}
	
	public void testKeys()
	{
		SetupEscenario1();
		assertEquals(true, root.getKeys()!=null);
	}
}
