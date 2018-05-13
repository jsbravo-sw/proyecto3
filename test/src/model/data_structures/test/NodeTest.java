package model.data_structures.test;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import model.data_structures.Node;

public class NodeTest<T> 
{
	private Node<T> node;
	private Node<T> next;
	private Node<T> previous;
	private T elem;
	private T elem2;
	private T elem3;
	
	@Before
	public void testSetup( )
	{
		next = new Node<T>(elem2, null, node);
		previous = new Node <T> (elem3, node, null);
		node = new Node<T>(elem, next,previous);
		
	}
	
	@Test
	public void nodeTest( )
	{
		assertEquals( "El nodo no se inicializa correctamente", node, node );

	}
	@Test
	public void nextTest( )
	{
		assertEquals( "No se retorna el nodo siguiente correctamente", next, node.next() );
	
		
	}
	@Test
	public void previousTest( )
	{
		assertEquals( "No se retorna el nodo anterior correctamente", previous, node.previous() );
	
	}
	@Test
	public void hasNextTest( )
	{
		assertEquals( "No retorna correctamente si el nodo tiene uno siguiente", true, node.hasNext() );
		assertEquals ("No retorna correctamente si el nodo tiene uno siguiente", false ,next.hasNext() ) ;
	
	}
	@Test
	public void hasPreviousTest( )
	{
		assertEquals( "No retorna correctamente si el nodo tiene uno anterior", false, previous.hasPrevious() );
		assertEquals ("No retorna correctamente si el nodo tiene uno anterior", true ,node.hasPrevious());
	}
	@Test
	public void elemTest( )
	{
		assertEquals( "No se asigna el elemento correctamente", elem, node.elem() );
	
	}
	@Test
	public void setElemTest( )
	{
		node.setElem(elem2);
		assertEquals( "No se asigna el elemento correctamente", elem2, node.elem() );
	
	}

}
