package model.data_structures;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import model.vo.InfoServicios;
import model.vo.Servicio;
import model.vo.VerticeConServicios;

public class Graph <K extends Comparable<K>, V, A>{

	@JsonProperty
	private int arcos;
	@JsonProperty 
	private hashTableSeparateChaining<K, Vertex> adj;
	@JsonProperty 
	private Lista<Edge> listaArcos;


	//----------------------------------------------------------------------------------
	//Clases Auxiliares
	//----------------------------------------------------------------------------------
	public class Edge implements Comparable<Edge>{

		//private K fuente;

		@JsonProperty 
		private Vertex fuente;

		@JsonProperty 
		private Vertex destino;

		//private K destino;
		@JsonProperty 
		private A infoArco;

		public Edge(K pFuente, V pFuenteValue, K pDestino, V pDestinoValue, A pInfoArco)
		{

			fuente = null;
			if (fuente == null)
			{
				fuente = new Vertex(pFuente,pFuenteValue);

			}
			//adj.put(pFuente, fuente);
			destino = null;
			if (destino == null)
			{
				destino = new Vertex (pDestino, pDestinoValue);

			}
			//adj.put(pDestino, destino);
			infoArco = pInfoArco;
			fuente.agregarOutcoming(destino);
			destino.agregarIncoming(fuente);
		}

		public Edge(K pFuente, K pDestino, A pInfoArco)
		{
			//TODO: Revisar necesidad agregar adj
			fuente = findVertex(pFuente);
			destino = findVertex(pDestino);
			infoArco = pInfoArco;
			fuente.agregarOutcoming(destino);
			destino.agregarIncoming(fuente);
		}
		public Vertex getFuente()	
		{		
			return fuente;	
		}

		public Vertex getDestino()	
		{		
			return destino;	
		}

		public A getInfo()	
		{		
			return infoArco;	
		}

		public void setInfoArc(A pInforArc)
		{
			infoArco = pInforArc;
		}

		@Override
		public int compareTo(Edge pEdge) 
		{
			return fuente.equals(pEdge.getFuente()) && destino.equals(pEdge.getDestino())? 0: -1;
		}
	}

	public class Vertex implements Comparable <Vertex> 
	{


		@JsonProperty 
		private K idVertex;
		@JsonProperty 
		private V informacion;
		@JsonProperty 
		private Lista<Edge> arcos;

		//Vertices adj que llegan a este vertice
		@JsonProperty 
		private Lista<Vertex> incoming;
		//Vertices adj de los que sale este vertice
		@JsonProperty
		private Lista<Vertex> outcoming;

		private double minimoCosto, maximoCosto;

		@JsonProperty
		private boolean marcado;

		private boolean completo;
		public Vertex anterior;

		public Vertex(K ident, V pInformacion)
		{
			idVertex = ident;
			informacion = pInformacion;
			marcado = false;
			incoming = new Lista<Vertex>();
			outcoming = new Lista<Vertex>();
			arcos = new Lista<Edge>();
			anterior = null;
		}

		public K id() 
		{
			return idVertex;
		}
		public V info()
		{ 
			return informacion;
		}
		public Lista<Edge> arcos()
		{
			return arcos;
		}
		public boolean marcado()
		{			
			return marcado;		
		}
		public void setMarcado(boolean pMar)
		{			
			marcado = pMar;		
		}
		public int compareTo(Vertex v)
		{
			return idVertex.compareTo(v.id());
		}
		public void agregarIncoming (Vertex v)
		{
			incoming.add(v);
		}
		public void agregarOutcoming (Vertex v)
		{
			outcoming.add(v);
		}

		public void setInfoVer(V pInformacion)
		{
			informacion = pInformacion;
		}

		public boolean isCompleto() {
			return completo;
		}

		public void setCompleto(boolean completo) {
			this.completo = completo;
		}

		public double getMinimoCosto() {
			return minimoCosto;
		}

		public void setMinimoCosto(int minimoCosto) {
			this.minimoCosto = minimoCosto;
		}


	}

	//---------------------------------------------------------
	//Constructor
	//---------------------------------------------------------

	public Graph(int V) 
	{
		adj = new hashTableSeparateChaining<>(V);
		arcos=0;
		listaArcos = new Lista<Edge>();
	}

	//Numeros de vertices
	public int V()
	{
		return adj.data();
	}
	public hashTableSeparateChaining<K, Vertex> vertex()
	{
		return adj;
	}
	//numeros de arcos
	public int E()
	{
		return arcos;
	}

	public void addVertex(K idVertex, V infoVertex)
	{
		Vertex nuevo = new Vertex(idVertex, infoVertex);
		adj.put(idVertex, nuevo);

	}

	public void addEdge(K ideVertexIni, K ideVertexFin, A infoArc)
	{
		Edge edge = new Edge(ideVertexIni, ideVertexFin, infoArc);
		Edge temp = findEdge(edge);
		//Si el arco existe, se actualiza su informacion
		if (temp!=null)
		{
			temp.infoArco = infoArc;
		}
		else
		{
			adj.get(ideVertexIni).arcos().add(edge);
			arcos++;
			listaArcos.add(edge);
		}

	}

	public Graph<K,V,A> reverse()
	{
		Graph<K,V,A> reverse= new Graph<>(V());

		Lista<Keys<K>> ver = adj.keys();

		for (int i = 0; i < ver.size(); i++) {
			Vertex auxVertex = findVertex(ver.get(i).getKey());
			reverse.addVertex(auxVertex.id(), auxVertex.info());	
		}

		for (int i = 0; i < ver.size(); i++) {
			Vertex auxVertex = findVertex(ver.get(i).getKey());
			Lista<Edge> edges = auxVertex.arcos();

			for (int j = 0; j < edges.size(); j++) {
				Vertex vertexFin = edges.get(j).getDestino();
				reverse.addEdge(vertexFin.id(), auxVertex.id(), edges.get(j).getInfo());
			}
		}

		return reverse;
	}

	public Lista depthFirstSearch()
	{
		Lista<Vertex> newLista = new Lista();
		Lista llaves = adj.keys();
		for (int i = 0; i < llaves.size(); i++) {
			//System.out.println(((Keys)llaves.get(i)).getKey());
			Vertex v = adj.get((K)((Keys)llaves.get(i)).getKey());
			//System.out.println(v.id());
			if(!v.marcado())
				DFSUtil(v,newLista);
		}

		for (int i = 0; i < llaves.size(); i++) {
			//System.out.println(((Keys)llaves.get(i)).getKey());
			Vertex v = adj.get((K)((Keys)llaves.get(i)).getKey());
			v.setMarcado(false);
		}
		return newLista;
	}

	private void DFSUtil(Vertex v, Lista pLista)
	{
		v.setMarcado(true);
		pLista.addAtEnd(v);

		for (int i = 0; i < adj.get(v.id()).arcos().size(); i++) {
			Vertex aux = adj.get(v.id()).arcos().get(i).getDestino();

			if(!aux.marcado())
			{
				DFSUtil(aux,pLista);
			}

		}
		//System.out.print(v.id()+" ");
	}

	public Lista darPostOrdenInvertido(Lista<Vertex> visitar)
	{
		Lista postOrden = new Lista();
		for (int i = 0; i < visitar.size(); i++) {
			Vertex aux = adj.get(visitar.get(i).id());

			if(!aux.marcado())
			{
				postOrdenUtil(aux, postOrden);
			}
		}

		for (int i = 0; i < visitar.size(); i++) {
			Vertex v = visitar.get(i);
			v.setMarcado(false);
		}
		return postOrden;
	}

	private void postOrdenUtil(Vertex aux, Lista pLista)
	{
		aux.setMarcado(true);
		for (int j = 0; j < adj.get(aux.id()).arcos().size(); j++) {
			Vertex aux2 = adj.get(aux.id()).arcos().get(j).getFuente();
			Vertex aux3 = adj.get(aux.id()).arcos().get(j).getDestino();
			if(!aux3.marcado() && aux2.equals(aux))
			{
				postOrdenUtil(aux3,pLista);
			}

		}
		pLista.add(aux);
	}

	public ArbolBinarioRN depthFirstSearch(Lista<Vertex> pVisitar)
	{
		ArbolBinarioRN arbol = new ArbolBinarioRN();
		for (int i = 0; i < pVisitar.size(); i++) {
			//System.out.println(((Keys)llaves.get(i)).getKey());
			Vertex v = findVertex(pVisitar.get(i).id());
			Lista<Vertex> newLista= new Lista();
			//System.out.println(v.id());
			if(!v.marcado())
			{
				DFSUtil(v,newLista);
				arbol.put(i, newLista);
			}
		}

		for (int i = 0; i < pVisitar.size(); i++) {
			//System.out.println(((Keys)llaves.get(i)).getKey());
			Vertex v = pVisitar.get(i);
			v.setMarcado(false);
		}
		return arbol;
	}

	public int test()
	{
		return listaArcos.size();
	}
	public int test2()
	{
		return adj.size(); 
	}


	public Vertex findVertex(K pIdVertex)
	{
		return adj!=null? adj.get(pIdVertex) : null;
	}

	public Edge findEdge(K v1, K v2)
	{
		Edge temp = new Edge(v1,v2,null);
		return findEdge(temp);
	}

	public Edge findEdge(Edge pEdge)
	{
		for( int i = 0; i<listaArcos.size();i++)
		{
			if (listaArcos.get(i).compareTo(pEdge)==0)
			{
				return listaArcos.get(i);
			}
		}
		return null;
	}

	public Edge findEdgeForId(K v1, K v2)
	{
		for( int i = 0; i<listaArcos.size();i++)
		{
			if (listaArcos.get(i).getFuente().id().equals(v1)&& listaArcos.get(i).getDestino().id().equals(v2) )
			{
				return listaArcos.get(i);
			}
		}
		return null;
	}

	public void setInfoVertex(K idVertex, V infoVertex)
	{
		adj.get(idVertex).setInfoVer(infoVertex);
	}

	public void setInfoArc(K idVertexIni, K idVertexFin, A infoArc)
	{
		Edge temp = findEdgeForId(idVertexIni, idVertexFin);
		//Si el arco existe, se actualiza su informacion
		if (temp!=null)
		{
			temp.setInfoArc(infoArc);
		}
	}

	public V getInfoVertex(K idVertex)
	{
		return ((Vertex)vertex().get(idVertex)).info();
	}

	public A getInfoArc(K idVertexIni, K idVertexFin)
	{
		if(findEdge(idVertexIni, idVertexFin)==null)
		{
			return null;
		}
		return findEdge(idVertexIni, idVertexFin).getInfo();
	}

	public Iterable <K> adj(K idVertex)
	{
		return (Iterable<K>) findVertex(idVertex).arcos();
	}

	public void persistirGrafo()
	{
		JSONObject obj = new JSONObject();
		JSONArray listaVertices = new JSONArray();

		Lista<K> llaves = (Lista<K>) adj.keys();

		for (int i = llaves.size()-1 ; i >=0; i--) {
			Vertex x = adj.get((K) ((Keys)llaves.get(i)).getKey());
			JSONObject verticeObj = new JSONObject();
			JSONObject verticeInnerObj = new JSONObject();
			JSONArray arrArcos = new JSONArray();

			Lista<Edge> arcos = x.arcos();

			for (int k = x.arcos().size()-1; k >= 0 ; k--) {

				JSONObject arcoObj = new JSONObject();
				JSONObject arcoInnerObj = new JSONObject();
				Edge y = arcos.get(k);

				arcoInnerObj.put("fuente", String.valueOf(y.getFuente().id()));
				arcoInnerObj.put("destino", String.valueOf(y.getDestino().id()));

				InfoServicios info = (InfoServicios) y.getInfo();
				JSONObject informacionObj = new JSONObject();

				informacionObj.put("trip_total", String.valueOf(info.getTrip_total()));
				informacionObj.put("trip_miles", String.valueOf(info.getTrip_miles()));
				informacionObj.put("trip_seconds", String.valueOf(info.getTrip_seconds()));

				arcoInnerObj.put("informacionArco", informacionObj);

				arcoObj.put("arco" + (x.arcos().size()-k), arcoInnerObj );
				arrArcos.add(arcoObj);
			}

			verticeInnerObj.put("Arcos", arrArcos);
			verticeInnerObj.put("id", x.id());

			VerticeConServicios vertice = (VerticeConServicios) x.info();		
			JSONArray serviciosSalenObj = new JSONArray();

			for (int j = 0; j < vertice.numeroServiciosQueSalen(); j++) 
			{
				String servicioId = vertice.getServiciosQueSalen().get(j);
				serviciosSalenObj.add(String.valueOf(servicioId));

			}
			JSONObject infoServicioObj = new JSONObject();
			infoServicioObj.put("ServiciosQueSalen", serviciosSalenObj);

			JSONArray serviciosLleganObj = new JSONArray();

			for (int k = 0; k < vertice.numeroServiciosQueLlegan(); k++) 
			{
				String servicioId = vertice.getServiciosQueLlegan().get(k);
				serviciosLleganObj.add(String.valueOf(servicioId));

			}
			infoServicioObj.put("ServiciosQueLlegan", serviciosLleganObj);

			verticeInnerObj.put("informacionVertice", infoServicioObj);

			verticeObj.put("vertice" + (llaves.size()-i), verticeInnerObj);
			listaVertices.add(verticeObj);
		}

		obj.put("Vertices", listaVertices);

		try {

			FileWriter file = new FileWriter(".\\data\\graph.json");
			file.write(obj.toJSONString());
			file.flush();
			file.close();

		} catch (IOException e) {
			System.out.println("Error al crear el json");
		}

	}

	/**
	 * Performs a Breadth-First Search
	 * starting at 'root' node (First created vertex)
	 * @return true is connected, false is not or if empty
	 */
	public boolean BreadthFirstSearch()
	{
		if (vertices().isEmpty()) return false;
		clearStates();

		Vertex root = vertices().get(0);
		if (root==null) return false;

		LinkedListQueue<Vertex> queue = new LinkedListQueue<Vertex>();
		queue.enqueue(root);
		root.marcado = true;

		while (!queue.isEmpty())
		{
			root = queue.peek();
			for (int i=0;i<root.outcoming.size();i++)
			{
				if (root.outcoming.get(i).marcado == false)
				{
					root.outcoming.get(i).marcado = true;
					queue.enqueue(root.outcoming.get(i));
				}
			}

			queue.dequeue();
		}
		return isConnected();
	}

	/**
	 * Perfoms a Breadth-First Search on a given starting vertex
	 * @param v1 Value of type T for starting vertex
	 * @return true if connected, false if not or if empty
	 */
	public boolean BreadthFirstSearch(K v1)
	{
		if (vertices().isEmpty()) return false;
		clearStates();

		Vertex root = findVertex(v1);
		if (root==null) return false;

		LinkedListQueue<Vertex> queue = new LinkedListQueue<>();
		queue.enqueue(root);
		root.marcado = true;

		while (!queue.isEmpty())
		{
			root = queue.peek();
			for (int i=0;i<root.outcoming.size();i++)
			{
				if (root.outcoming.get(i).marcado == false)
				{
					root.outcoming.get(i).marcado = true;
					queue.enqueue(root.outcoming.get(i));
				}
			}

			queue.dequeue();
		}
		return isConnected();
	}


	/**
	 * Sets all states to UNVISITED
	 */
	private void clearStates()
	{
		for (int i=0;i<vertices().size();i++)
		{
			vertices().get(i).setMarcado(false);
		}
	}

	/**
	 * Test if DFS or BFS returned a connected graph
	 * @return true if connected, false if not.
	 */
	public boolean isConnected()
	{
		for (int i=0;i<vertices().size();i++)
		{
			if (vertices().get(i).marcado() == false)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Performs a recursive Depth First Search on the
	 * 'root' node (the first vertex created)
	 * @return true if connected, false if empty or not connected
	 */


	public Lista<Vertex> vertices()
	{
		Lista<Vertex> resp = new Lista<Vertex>();
		for (int i=0;i<adj.keys().size();i++)
		{
			resp.addAtEnd(adj.get(adj.keys().get(i).getKey()));
		}
		return resp;
	}

	/**
	 * Creates path information on the graph using the Dijkstra Algorithm,
	 * Puts the information into the Vertices, based on given starting vertex.
	 * @param v1 Value of type T for starting vertex
	 * @return true if successfull, false if empty or not found.
	 */
	private boolean Dijkstra(K v1, int pParam)
	{
		double elMachete = 0;

		if (vertices().isEmpty()) return false;

		// reset all vertices minDistance and previous
		resetDistances();

		// make sure it is valid
		Vertex source = findVertex(v1);
		if (source==null) return false;

		// set to 0 and add to heap
		source.minimoCosto = 0;
		PriorityQueue<Vertex> pq = new PriorityQueue<>();
		pq.add(source);

		while (!pq.isEmpty())
		{
			//pull off top of queue
			Vertex u = pq.poll();

			// loop through adjacent vertices
			for (int i=0; i<u.outcoming.size();i++)
			{
				Vertex v = u.outcoming.get(i);
				K vInicial = v.idVertex;
				K uFinal = u.idVertex;
				// get edge
				Edge e = findEdge(uFinal, vInicial);
				if (e==null) return false;
				// add cost to current

				if (pParam == 0)
				{
					elMachete = ((InfoServicios) e.infoArco).getTrip_miles();
				}
				else if (pParam == 1)
				{
					elMachete = ((InfoServicios) e.infoArco).getTrip_total();
				}
				else if (pParam == 2)
				{
					elMachete = ((InfoServicios) e.infoArco).getTrip_seconds();
				}
				else if (pParam == 3)
				{
					elMachete = ((InfoServicios) e.infoArco).hayPeaje();
				}

				double totalDistance = u.minimoCosto + elMachete ;
				if (totalDistance < v.minimoCosto)
				{
					// new cost is smaller, set it and add to queue
					pq.remove(v);
					v.minimoCosto = totalDistance;
					// link vertex
					v.anterior = u;
					pq.add(v);
				}
			}
		}

		return true;
		
		
	}

	private boolean DijkstraLongest(K v1, int pParam)
	{
		double elMachete = 0;

		if (vertices().isEmpty()) return false;

		// reset all vertices minDistance and previous
		resetDistancesMax();

		// make sure it is valid
		Vertex source = findVertex(v1);
		if (source==null) return false;

		// set to 0 and add to heap
		source.maximoCosto = 0;
		PriorityQueue<Vertex> pq = new PriorityQueue<>(Collections.reverseOrder());
		pq.add(source);

		while (!pq.isEmpty())
		{
			//pull off top of queue
			Vertex u = pq.poll();

			// loop through adjacent vertices
			for (int i=0; i<u.outcoming.size();i++)
			{
				Vertex v = u.outcoming.get(i);
				K vInicial = v.idVertex;
				K uFinal = u.idVertex;
				// get edge
				Edge e = findEdge(uFinal, vInicial);
				if (e==null) return false;
				// add cost to current

				if (pParam == 0)
				{
					elMachete = ((InfoServicios) e.infoArco).getTrip_miles();
				}
				else if (pParam == 1)
				{
					elMachete = ((InfoServicios) e.infoArco).getTrip_total();
				}
				else if (pParam == 2)
				{
					elMachete = ((InfoServicios) e.infoArco).getTrip_seconds();
				}
				else if (pParam == 3)
				{
					elMachete = ((InfoServicios) e.infoArco).hayPeaje();
				}

				double totalDistance = u.maximoCosto + elMachete ;
				if (totalDistance > v.maximoCosto)
				{
					// new cost is smaller, set it and add to queue
					pq.remove(v);
					v.maximoCosto = totalDistance;
					// link vertex
					v.anterior = u;
					pq.add(v);
				}
			}
		}

		return true;
		
		
	}

	/**
	 * Goes through the result tree created by the Dijkstra method
	 * and steps backward
	 * @param target Vertex end of path
	 * @return string List of vertices and costs
	 */
	private Lista<String> getShortestPath(Vertex target)
	{
		Lista<String> path = new Lista<String>();

		// check for no path found
		if (target.minimoCosto==Integer.MAX_VALUE)
		{
			path.add("No existe un camino entre los vértices ");
			return path;
		}

		// loop through the vertices from end target 
		for (Vertex v = target; v !=null; v = v.anterior)
		{
			path.add(v.idVertex + "/peso/" + v.minimoCosto);
		}

		// flip the list
		//Lo inverto
		Lista <String> reverse = new Lista<String>();
		for (int i=path.size()-1;i>=0;i--)
		{
			reverse.addAtEnd(path.get(i));
		}
		return reverse;
	}

	private Lista<String> getLongestPath(Vertex target)
	{
		Lista<String> path = new Lista<String>();

		// check for no path found
		if (target.maximoCosto==Double.MIN_VALUE)
		{
			path.add("No existe un camino entre los vértices ");
			return path;
		}

		// loop through the vertices from end target 
		for (Vertex v = target; v !=null; v = v.anterior)
		{
			path.add(v.idVertex + "/peso/" + v.maximoCosto);
		}

		// flip the list
		//Lo inverto
		Lista <String> reverse = new Lista<String>();
		for (int i=path.size()-1;i>=0;i--)
		{
			reverse.addAtEnd(path.get(i));
		}
		return reverse;
	}

	/**
	 * for Dijkstra, resets all the path tree fields
	 */
	private void resetDistances()
	{
		for (int i=0;i<vertices().size();i++)
		{
			vertices().get(i).minimoCosto = Integer.MAX_VALUE;
			vertices().get(i).anterior = null;
		}

	}

	/**
	 * for Dijkstra, resets all the path tree fields
	 */
	private void resetDistancesMax()
	{
		for (int i=0;i<vertices().size();i++)
		{
			vertices().get(i).maximoCosto = Double.MIN_VALUE;
			vertices().get(i).anterior = null;
		}

	}
	
	/**
	 * PUBLIC WRAPPER FOR PRIVATE FUNCTIONS
	 * Calls the Dijkstra method to build the path tree for the given
	 * starting vertex, then calls getShortestPath method to return
	 * a list containg all the steps in the shortest path to
	 * the destination vertex.
	 * @param from value of type T for Vertex 'from'
	 * @param to value of type T for vertex 'to'
	 * @return ArrayList of type String of the steps in the shortest path.
	 */
	public Lista<String> getPath(K from, K to, int pParam)
	{
		boolean test = Dijkstra(from, pParam);
		if (test==false) return null;
		Lista<String> path = getShortestPath(findVertex(to));
		return path;
	}
	
	public Lista<String> getMaxPath(K from, K to, int pParam)
	{
		boolean test = DijkstraLongest(from, pParam);
		if (test==false) return null;
		Lista<String> path = getLongestPath(findVertex(to));
		return path;
	}

}
