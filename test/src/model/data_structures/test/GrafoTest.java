package model.data_structures.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import model.data_structures.ArbolBinarioRN;
import model.data_structures.Graph;
import model.data_structures.Graph.Edge;
import model.data_structures.Graph.Vertex;
import model.data_structures.Lista;
import model.data_structures.Node;
import model.data_structures.hashTableSeparateChaining;

public class GrafoTest
{
	
	private Graph<String, Integer, String> grafo;
	private Graph<String, Integer, String> grafo2;
	
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
	
	@Test
	public 	void iterableAdjTest()
	{
		
		Vertex arco = ((Vertex)grafo.vertex().get("vertice1"));
		assertEquals("No retorno los identificadores de los vertices adyacentes", "1-2", ((Edge)arco.arcos().get(1)).getInfo() );
		assertEquals("No retorno los identificadores de los vertices adyacentes", "1-3", ((Edge)arco.arcos().get(0)).getInfo() );
	}
	
	@Test
	public 	void graphReverseTest()
	{	
		String v1 = "vertice1";
		int x1 = 1;
		
		String v2 = "vertice2";
		int x2 = 2;
		
		String v3 = "vertice3";
		int x3 = 3;
		
		String v4 = "vertice4";
		int x4 = 4;
		
		String v5 = "vertice5";
		int x5 = 5;
		
		grafo2 = new Graph<String, Integer, String>(5);
		
		grafo2.addVertex(v1, x1);
		grafo2.addVertex(v2, x2);
		grafo2.addVertex(v3, x3);
		grafo2.addVertex(v4, x4);
		grafo2.addVertex(v5, x5);
		
		grafo2.addEdge(v1, v2, "1-2");
		grafo2.addEdge(v2, v3, "2-3");
		grafo2.addEdge(v3, v2, "3-2");
		grafo2.addEdge(v1, v3, "1-3");
		grafo2.addEdge(v3, v4, "3-4");
		grafo2.addEdge(v4, v2, "4-2");
		grafo2.addEdge(v1, v5, "1-5");
		grafo2.addEdge(v5, v1, "5-1");
		Graph<String, Integer, String> grafoReverse = grafo2.reverse();
		
		assertEquals( "Tamaño incorrecto", 5 ,grafoReverse.V() );
		assertEquals( "Tamaño incorrecto", 8 ,grafoReverse.E() );
		
		assertEquals("Informacion incorrecta dentro del vertice", 1, ((int)grafoReverse.getInfoVertex("vertice1")));
		assertEquals("Informacion incorrecta dentro del vertice", 2, ((int)grafoReverse.getInfoVertex("vertice2")));
			
		assertEquals("Informacion incorrecta en el arco", "1-3", grafo2.getInfoArc("vertice1", "vertice3"));
		assertEquals("Informacion incorrecta en el arco", "1-2", grafo2.getInfoArc("vertice1", "vertice2"));
		
		assertEquals("Informacion incorrecta en el arco", null, grafoReverse.getInfoArc("vertice1", "vertice3"));
		assertEquals("Informacion incorrecta en el arco", null, grafoReverse.getInfoArc("vertice1", "vertice2"));
		
		assertEquals("Informacion incorrecta en el arco", "2-3", grafo2.getInfoArc("vertice2", "vertice3"));
		assertEquals("Informacion incorrecta en el arco", "2-3", grafoReverse.getInfoArc("vertice3", "vertice2"));
		//grafo2.depthFirstSearch();
		Lista p = grafoReverse.depthFirstSearch();
		Lista postOrden = grafoReverse.darPostOrdenInvertido(p);
		ArbolBinarioRN arbol = grafo2.depthFirstSearch(postOrden);
		for (int i = 0; i < p.size(); i++) {
			//System.out.println(((Vertex)p.get(i)).id());
		}
		for (int i = 0; i < postOrden.size(); i++) {
			//System.out.println(((Vertex)postOrden.get(i)).id());
		}
		
		for (int i = 0; i < arbol.getKeys().size(); i++) {
			Lista aux = (Lista) arbol.get(arbol.getKeys().get(i));
			//System.out.println(arbol.getKeys().size());
			for (int j = 0; j < aux.size(); j++) {
				System.out.println(((Vertex)aux.get(j)).id() + " " + i);
			}
			
		}
	}
	

	
}
