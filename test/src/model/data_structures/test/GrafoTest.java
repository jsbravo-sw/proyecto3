package model.data_structures.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import model.data_structures.Graph;
import model.data_structures.Graph.Edge;
import model.data_structures.Graph.Vertex;
import model.data_structures.Lista;
import model.data_structures.Node;
import model.data_structures.hashTableSeparateChaining;

public class GrafoTest
{
	
	private Graph<String, Integer, String> grafo;
	
	@Before
	public void testSetup( )
	{
		String v1 = "vertice1";
		int x1 = 1;
		
		String v2 = "vertice2";
		int x2 = 2;
		
		String v3 = "vertice3";
		int x3 = 3;
		
		grafo = new Graph<String, Integer, String>(3);
		
		grafo.addVertex(v1, x1);
		grafo.addVertex(v2, x2);
		grafo.addVertex(v3, x3);
		
		grafo.addEdge(v1, v2, "1-2");
		grafo.addEdge(v2, v1, "2-1");
		grafo.addEdge(v2, v3, "2-3");
		grafo.addEdge(v3, v2, "3-2");
		grafo.addEdge(v1, v3, "1-3");
		grafo.addEdge(v3, v1, "3-1");
		
	}
	
	@Test
	public void VTest( )
	{
		assertEquals( "Tamaño incorrecto", 3 ,grafo.V() );
	}
	
	@Test
	public void ETest( )
	{
		assertEquals( "Tamaño incorrecto", 6 ,grafo.E() );
	}

	@Test
	public void addVertexAndEdgeTest( )
	{
		assertEquals( "Tamaño incorrecto", 3 ,grafo.V() );
		assertEquals( "Tamaño incorrecto", 6 ,grafo.E() );
		
		grafo.addVertex("nuevoVertice", 1000);
		grafo.addEdge("nuevoVertice", "vertice3", "4-3");
		grafo.addEdge("vertice3", "nuevoVertice", "3-4");
		
		assertEquals( "Tamaño incorrecto", 4 ,grafo.V() );
		assertEquals( "Tamaño incorrecto", 8 ,grafo.E() );
		
		assertEquals("Nombre incorrecto", "nuevoVertice", ((Vertex)grafo.vertex().get("nuevoVertice")).id());

	
	}
	
	@Test
	public void getInfoVertexTest()
	{
		assertEquals("Informacion incorrecta dentro del vertice", 1, ((int)grafo.getInfoVertex("vertice1")));
		assertEquals("Informacion incorrecta dentro del vertice", 2, ((int)grafo.getInfoVertex("vertice2")));
		
		grafo.addVertex("nuevoVertice", 1000);
		
		assertEquals("Informacion incorrecta dentro del vertice", 1000, ((int)grafo.getInfoVertex("nuevoVertice")));
	}
	
	@Test
	public void setInfoVertexTest()
	{
		assertEquals("Informacion incorrecta dentro del vertice", 1, ((Vertex)grafo.vertex().get("vertice1")).info());
		grafo.setInfoVertex("vertice1", 500);
		assertEquals("Informacion incorrecta dentro del vertice", 500, ((Vertex)grafo.vertex().get("vertice1")).info());
		
	}
	
	@Test
	public void getInfoArcTest()
	{
		Lista edges = ((Vertex)grafo.vertex().get("vertice1")).arcos();
		assertEquals("Informacion incorrecta en el arco", "1-3", grafo.getInfoArc("vertice1", "vertice3"));
		assertEquals("Informacion incorrecta en el arco", "1-2", grafo.getInfoArc("vertice1", "vertice2"));
		
		grafo.addVertex("nuevoVertice", 1000);
		grafo.addEdge("nuevoVertice", "vertice3", "4-3");
		
		Lista edges2 = ((Vertex)grafo.vertex().get("nuevoVertice")).arcos();
		assertEquals("Informacion incorrecta en el arco", "4-3", grafo.getInfoArc("nuevoVertice", "vertice3"));
	}
	
	@Test
	public 	void setInfoArcTest()
	{
		Lista edges = ((Vertex)grafo.vertex().get("vertice1")).arcos();
		assertEquals("Informacion incorrecta en el arco", "1-3", ((Edge)edges.get(0)).getInfo());
		assertEquals("Informacion incorrecta en el arco", "1-2", ((Edge)edges.get(1)).getInfo());
		
		grafo.setInfoArc("vertice1", "vertice2", "1 a 2");
		assertEquals("Informacion incorrecta en el arco", "1 a 2", ((Edge)edges.get(1)).getInfo());
		
		grafo.setInfoArc("vertice1", "vertice3", "holi");
		assertEquals("Informacion incorrecta en el arco", "holi", ((Edge)edges.get(0)).getInfo());
	}
	

	
}
