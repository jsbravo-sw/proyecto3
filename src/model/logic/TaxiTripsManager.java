package model.logic;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import API.ITaxiTripsManager;
import model.data_structures.ArbolBinarioRN;
import model.data_structures.Graph;
import model.data_structures.Graph.Edge;
import model.data_structures.Graph.Vertex;
import model.data_structures.Keys;
import model.data_structures.Lista;
import model.data_structures.SequentialSearch;
import model.vo.CompFuertementeConexa;
import model.vo.InfoServicios;
import model.vo.Servicio;
import model.vo.VerticeConServicios;

public class TaxiTripsManager implements ITaxiTripsManager {
	public static final String DIRECCION_SMALL_JSON = "./data/taxi-trips-wrvz-psew-subset-small.json";
	public static final String DIRECCION_MEDIUM_JSON = "./data/taxi-trips-wrvz-psew-subset-medium.json";
	public static final String DIRECCION_LARGE_JSON = "./data/taxi-trips-wrvz-psew-subset-large.json";

	//String que representa la lat/long del vertice y la lista sus servicios asociados
	@JsonProperty
	public Graph<String, Lista<Servicio>, InfoServicios> graph;
	public Graph<String, VerticeConServicios, InfoServicios> graphString;
	public int param;

	private int cont = 0;
	private String json;

	public boolean cargarSistema(String direccionJson, int pParam) 
	{
		JSONParser parser = new JSONParser();
		graph = new Graph<String, Lista<Servicio>, InfoServicios>(500);
		param = pParam;
		int out =0;
		if (!direccionJson.equals(DIRECCION_LARGE_JSON))
		{

			if(direccionJson.equals(DIRECCION_MEDIUM_JSON))
				json = "Medium";
			else
				json = "Small";
			try 
			{
				Object obj = parser.parse(new FileReader(direccionJson));
				JSONArray serviceList = (JSONArray) obj;
				for (int i = 0; serviceList != null && i < serviceList.size(); i++)
				{

					JSONObject jsonObject = (JSONObject) serviceList.get(i);
					String pickup_centroid_latitude = jsonObject.get("pickup_centroid_latitude") != null? (String) jsonObject.get("pickup_centroid_latitude"):"0.0";
					String pickup_centroid_longitude = jsonObject.get("pickup_centroid_longitude") != null? (String) jsonObject.get("pickup_centroid_longitude"):"0.0";
					String dropoff_centroid_latitude = jsonObject.get("dropoff_centroid_latitude") != null? (String) jsonObject.get("dropoff_centroid_latitude"):"0.0";
					String dropoff_centroid_longitude = jsonObject.get("dropoff_centroid_longitude") != null? (String) jsonObject.get("dropoff_centroid_longitude"):"0.0";

					//Si algun servicio no tiene definidas lat/long de recogida y terminacionm, salte a la siguiente iteracion
					if (Double.parseDouble(pickup_centroid_latitude)==0.0 || Double.parseDouble(pickup_centroid_longitude)==0.0 || Double.parseDouble(dropoff_centroid_latitude)==0.0 || Double.parseDouble(dropoff_centroid_longitude)==0.0 )
					{
						out++;
						System.out.println("*******Salte*******");
						continue;

					}

					String company =  jsonObject.get("company") != null? (String) jsonObject.get("company"):"Independent Owner";
					String dropoff_community_area = jsonObject.get("dropoff_community_area") != null? (String) jsonObject.get("dropoff_community_area"): "0";
					String pickup_community_area =  jsonObject.get("pickup_community_area") != null? (String) jsonObject.get("pickup_community_area"):"0";
					String taxi_id =  jsonObject.get("taxi_id") != null? (String) jsonObject.get("taxi_id"):"No-ID";
					String trip_end_timestamp =  jsonObject.get("trip_end_timestamp") != null? (String) jsonObject.get("trip_end_timestamp"):"0000-00-00T00:00:00.000";
					String trip_id =  jsonObject.get("trip_id") != null? (String) jsonObject.get("trip_id"):"No-TripID";
					String trip_miles =  jsonObject.get("trip_miles") != null? (String) jsonObject.get("trip_miles"):"0";
					String trip_seconds =  jsonObject.get("trip_seconds") != null? (String) jsonObject.get("trip_seconds"):"0";
					String trip_start_timestamp =  jsonObject.get("trip_start_timestamp") != null? (String) jsonObject.get("trip_start_timestamp"):"0000-00-00T00:00:00.000";
					String trip_total=  jsonObject.get("trip_total") != null? (String) jsonObject.get("trip_total"):"0.0";
					boolean toll = jsonObject.get("tolls") != null? Double.parseDouble((String) jsonObject.get("tolls"))>0? true : false : false;

					String pickUpId = pickup_centroid_latitude + "/" + pickup_centroid_longitude;
					String dropOffId = dropoff_centroid_latitude + "/" + dropoff_centroid_longitude;

					Servicio newServicio = new Servicio (trip_id, taxi_id, Integer.parseInt(trip_seconds),Double.parseDouble(trip_miles), Double.parseDouble(trip_total), trip_start_timestamp, trip_end_timestamp,Integer.parseInt(pickup_community_area), Integer.parseInt(dropoff_community_area),pickup_centroid_latitude, pickup_centroid_longitude, toll);

					//Si está vacío el grafo
					if (graph.vertex().data()==0)
					{
						Lista<Servicio> aux = new Lista<Servicio>();
						newServicio.setVertexInicialId(pickUpId);
						newServicio.setVertexFinalId(dropOffId);
						aux.addAtEnd(newServicio);
						graph.addVertex(pickUpId, aux);
						//					System.out.println(pickUpId);
						//Preguntar: En el vertice donde acaba el servicio tambien se adjunta el servicio?
						//aux = new Lista<Servicio>();
						graph.addVertex(dropOffId, new Lista <Servicio>());
						InfoServicios infoArc = new InfoServicios (newServicio, pickUpId, dropOffId);
						graph.addEdge(pickUpId,dropOffId, infoArc);
						//						System.out.println(dropOffId);
						//						System.out.println("Al menos uno"+ graph.V());



					}
					//El grafo ya tiene vertices
					else
					{
						String elInicial = null;
						String elFinal = null;
						Lista<Servicio> aux = new Lista<Servicio>();


						//Variables repetidas en inicio y final
						double menor = Double.MAX_VALUE;
						String elMenor = null;
						//La zona inicial pertenece a *algún* vértice existente?
						Lista<String> yoPertenezcoA = new Lista<String>();

						String aEsteVertice = null;

						for (int h = 0; h< graph.vertex().keys().size();h++)
						{
							//Si la distancia es menor al parametro, entonces pertenece a ese vertice
							//							System.out.println("-> For que recorre todas las llaves");
							//							System.out.println("Tamanio: " + graph.vertex().keys().size());
							//							System.out.println("Llave: " + graph.vertex().keys().get(h).getKey());
							if (getDistance(Double.parseDouble(graph.vertex().keys().get(h).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(h).getKey().split("/")[1]), Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude))<=param)
							{
								//TODO: Agregar a una lista todos los vertices a los que pertenece, recorrer toda la lista y buscar cual tiene menor distancia.
								yoPertenezcoA.addAtEnd(graph.vertex().keys().get(h).getKey());	
								aEsteVertice = graph.vertex().keys().get(h).getKey();
								//System.out.println("**El inicial pertenece a: " + aEsteVertice + "**");
							}
						}
						//						System.out.println("recorri todos los vertices y comprobe pertenencia del inicial <-");
						//						System.out.println("Yo (el inicial) pertenezco a " + yoPertenezcoA.size());
						if (yoPertenezcoA.size()==0)
						{
							//Si no pertenece a ningun vertice por la distancia de parametro, entonces es un vertice nuevo
							//	System.out.println("-> Caso donde no pertenece a ningun vertice (inicial)");
							aux = new Lista<Servicio>();
							newServicio.setVertexInicialId(pickUpId);
							aux.addAtEnd(newServicio);
							graph.addVertex(pickUpId, aux);
							elInicial = pickUpId;
							//.out.println("cree el vertice, añadi el servicio <-");

						}
						else if (yoPertenezcoA.size()>1)
						{
							//getDistance(Double.parseDouble(graph.vertex().keys().get(0).getKey().split("-")[0]),Double.parseDouble(graph.vertex().keys().get(0).getKey().split("-")[1]), Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude));

							for (int w=0;w<yoPertenezcoA.size();w++)
							{
								//Comparo las distancias
								if (menor>getDistance(Double.parseDouble(yoPertenezcoA.get(w).split("/")[0]), Double.parseDouble(yoPertenezcoA.get(w).split("/")[1]),Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude)))
								{
									menor = getDistance(Double.parseDouble(yoPertenezcoA.get(w).split("/")[0]), Double.parseDouble(yoPertenezcoA.get(w).split("/")[1]),Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude));
									elMenor = yoPertenezcoA.get(w);
								}

							}
							//Encontre el vertice con menor distancia desde el servicio, pertenece a ese vertice
							elInicial = elMenor;
							newServicio.setVertexInicialId(elInicial);
							graph.findVertex(elMenor).info().addAtEnd(newServicio);
						}
						//Si solo pertenece a un vertice
						else if (yoPertenezcoA.size()==1)
						{
							//							System.out.println("-> Caso el inicial pertenece a *un* vertice");
							//							System.out.println(graph.findVertex(aEsteVertice).info());
							newServicio.setVertexInicialId(aEsteVertice);
							graph.findVertex(aEsteVertice).info().addAtEnd(newServicio);
							elInicial = aEsteVertice;
							//	System.out.println("encontré el vertice - acabe <-" );
						}


						//Reinicio las variables repetidas
						yoPertenezcoA = new Lista<String>();
						elMenor = null;
						menor = Double.MAX_VALUE;

						for (int h = 0; h< graph.vertex().keys().size();h++)
						{
							//Si la distancia es menor al parametro, entonces pertenece a ese vertice
							if (getDistance(Double.parseDouble(graph.vertex().keys().get(h).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(h).getKey().split("/")[1]), Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude))<=param)
							{
								yoPertenezcoA.addAtEnd(graph.vertex().keys().get(h).getKey());
								aEsteVertice = graph.vertex().keys().get(h).getKey();
							}

						}
						if (yoPertenezcoA.size()==0)
						{
							//			System.out.println("-> Caso donde no pertenece a ningun vertice (final)");
							aux = new Lista<Servicio>();
							newServicio.setVertexFinalId(dropOffId);
							//aux.addAtEnd(newServicio);
							graph.addVertex(dropOffId, aux);
							elFinal = dropOffId;
							//	System.out.println("cree el vertice, añadi el servicio <-");

						}
						//TODO: Revision añadir servicio a donde llego
						if (yoPertenezcoA.size()>1)
						{
							for (int w= 0; w<yoPertenezcoA.size();w++)
							{
								if (menor>getDistance(Double.parseDouble(yoPertenezcoA.get(w).split("/")[0]), Double.parseDouble(yoPertenezcoA.get(w).split("/")[1]),Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude)))
								{
									menor = getDistance(Double.parseDouble(yoPertenezcoA.get(w).split("/")[0]), Double.parseDouble(yoPertenezcoA.get(w).split("/")[1]),Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude));
									elMenor = yoPertenezcoA.get(w);
								}
							}
							elFinal = elMenor;
							newServicio.setVertexFinalId(elFinal);
						}

						else if (yoPertenezcoA.size()==1)
						{
							//							System.out.println("-> Caso el final pertenece a *un* vertice");
							//							System.out.println(graph.findVertex(aEsteVertice).info());
							newServicio.setVertexFinalId(aEsteVertice);
							//graph.findVertex(aEsteVertice).info().addAtEnd(newServicio);
							elFinal = aEsteVertice;
							//System.out.println("encontré el vertice - acabe <-" );
						}
						//Recorro todos los servicios del vertice inicial, busco si tambien terminan en el final y le saco el promedio al arco
						//TODO: Recorre la lista despues de agregar el servicio;
						if (graph.findEdge(elInicial, elFinal)!=null)
						{
							graph.findEdge(elInicial, elFinal).getInfo().addServicio(newServicio);
						}
						else
						{
							InfoServicios infoArc = new InfoServicios(newServicio, elInicial, elFinal);
							graph.addEdge(elInicial, elFinal,infoArc);
						}

					}

					System.out.println("Iteracion "+ i);
					System.out.println("Número de arcos: " + graph.test());
					System.out.println("Número de vértices: " + graph.vertex().keys().size());
				}
				int sum = 0;
				for (int i=0;i<graph.vertex().keys().size();i++)
				{
					//			System.out.println("Cantidad de servicios en el vertice " +(i+1)  +": " + graph.vertex().get(graph.vertex().keys().get(i).getKey()).info().size());
					sum+=graph.vertex().get(graph.vertex().keys().get(i).getKey()).info().size();
				}
				System.out.println("Total de servicios: " + sum);
				System.out.println("Total de servicios saltados por lat/long vacias: " + out);
				System.out.println("Total neto: " + (sum + out));

			}
			catch (Exception e) 
			{
				e.printStackTrace();
			} 

			persistirGrafo();
			System.out.println("Inside loadServices with " + direccionJson);
			return true;
		}
		else
		{
			json = "Large";
			try 
			{
				for (int k=2;k<9;k++)
				{
					Object obj = parser.parse(new FileReader("./data/taxi-trips-wrvz-psew-subset-0"+k+"-02-2017.json"));
					JSONArray serviceList = (JSONArray) obj;
					for (int i = 0; serviceList != null && i < serviceList.size(); i++)
					{
						JSONObject jsonObject = (JSONObject) serviceList.get(i);
						String pickup_centroid_latitude = jsonObject.get("pickup_centroid_latitude") != null? (String) jsonObject.get("pickup_centroid_latitude"):"0.0";
						String pickup_centroid_longitude = jsonObject.get("pickup_centroid_longitude") != null? (String) jsonObject.get("pickup_centroid_longitude"):"0.0";
						String dropoff_centroid_latitude = jsonObject.get("dropoff_centroid_latitude") != null? (String) jsonObject.get("dropoff_centroid_latitude"):"0.0";
						String dropoff_centroid_longitude = jsonObject.get("dropoff_centroid_longitude") != null? (String) jsonObject.get("dropoff_centroid_longitude"):"0.0";

						//Si algun servicio no tiene definidas lat/long de recogida y terminacionm, salte a la siguiente iteracion
						if (Double.parseDouble(pickup_centroid_latitude)==0.0 || Double.parseDouble(pickup_centroid_longitude)==0.0 || Double.parseDouble(dropoff_centroid_latitude)==0.0 || Double.parseDouble(dropoff_centroid_longitude)==0.0 )
						{
							out++;
							System.out.println("*******Salte*******");
							continue;

						}

						String company =  jsonObject.get("company") != null? (String) jsonObject.get("company"):"Independent Owner";
						String dropoff_community_area = jsonObject.get("dropoff_community_area") != null? (String) jsonObject.get("dropoff_community_area"): "0";
						String pickup_community_area =  jsonObject.get("pickup_community_area") != null? (String) jsonObject.get("pickup_community_area"):"0";
						String taxi_id =  jsonObject.get("taxi_id") != null? (String) jsonObject.get("taxi_id"):"No-ID";
						String trip_end_timestamp =  jsonObject.get("trip_end_timestamp") != null? (String) jsonObject.get("trip_end_timestamp"):"0000-00-00T00:00:00.000";
						String trip_id =  jsonObject.get("trip_id") != null? (String) jsonObject.get("trip_id"):"No-TripID";
						String trip_miles =  jsonObject.get("trip_miles") != null? (String) jsonObject.get("trip_miles"):"0";
						String trip_seconds =  jsonObject.get("trip_seconds") != null? (String) jsonObject.get("trip_seconds"):"0";
						String trip_start_timestamp =  jsonObject.get("trip_start_timestamp") != null? (String) jsonObject.get("trip_start_timestamp"):"0000-00-00T00:00:00.000";
						String trip_total=  jsonObject.get("trip_total") != null? (String) jsonObject.get("trip_total"):"0.0";


						String pickUpId = pickup_centroid_latitude + "/" + pickup_centroid_longitude;
						String dropOffId = dropoff_centroid_latitude + "/" + dropoff_centroid_longitude;
						
						boolean toll = jsonObject.get("tolls") != null? Double.parseDouble((String) jsonObject.get("tolls"))>0? true : false : false;

						Servicio newServicio = new Servicio (trip_id, taxi_id, Integer.parseInt(trip_seconds),Double.parseDouble(trip_miles), Double.parseDouble(trip_total), trip_start_timestamp, trip_end_timestamp,Integer.parseInt(pickup_community_area), Integer.parseInt(dropoff_community_area),pickup_centroid_latitude, pickup_centroid_longitude, toll);

						//Si está vacío el grafo
						if (graph.vertex().data()==0)
						{
							Lista<Servicio> aux = new Lista<Servicio>();
							newServicio.setVertexInicialId(pickUpId);
							newServicio.setVertexFinalId(dropOffId);
							aux.addAtEnd(newServicio);
							graph.addVertex(pickUpId, aux);
							//					System.out.println(pickUpId);
							//Preguntar: En el vertice donde acaba el servicio tambien se adjunta el servicio?
							//aux = new Lista<Servicio>();
							graph.addVertex(dropOffId, new Lista <Servicio>());
							InfoServicios infoArc = new InfoServicios (newServicio, pickUpId, dropOffId);
							graph.addEdge(pickUpId,dropOffId, infoArc);
							//						System.out.println(dropOffId);
							//						System.out.println("Al menos uno"+ graph.V());



						}
						//El grafo ya tiene vertices
						else
						{
							String elInicial = null;
							String elFinal = null;
							Lista<Servicio> aux = new Lista<Servicio>();


							//Variables repetidas en inicio y final
							double menor = Double.MAX_VALUE;
							String elMenor = null;
							//La zona inicial pertenece a *algún* vértice existente?
							Lista<String> yoPertenezcoA = new Lista<String>();

							String aEsteVertice = null;

							for (int h = 0; h< graph.vertex().keys().size();h++)
							{
								//Si la distancia es menor al parametro, entonces pertenece a ese vertice
								//							System.out.println("-> For que recorre todas las llaves");
								//							System.out.println("Tamanio: " + graph.vertex().keys().size());
								//							System.out.println("Llave: " + graph.vertex().keys().get(h).getKey());
								if (getDistance(Double.parseDouble(graph.vertex().keys().get(h).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(h).getKey().split("/")[1]), Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude))<=param)
								{
									//TODO: Agregar a una lista todos los vertices a los que pertenece, recorrer toda la lista y buscar cual tiene menor distancia.
									yoPertenezcoA.addAtEnd(graph.vertex().keys().get(h).getKey());	
									aEsteVertice = graph.vertex().keys().get(h).getKey();
									//System.out.println("**El inicial pertenece a: " + aEsteVertice + "**");
								}
							}
							//						System.out.println("recorri todos los vertices y comprobe pertenencia del inicial <-");
							//						System.out.println("Yo (el inicial) pertenezco a " + yoPertenezcoA.size());
							if (yoPertenezcoA.size()==0)
							{
								//Si no pertenece a ningun vertice por la distancia de parametro, entonces es un vertice nuevo
								//	System.out.println("-> Caso donde no pertenece a ningun vertice (inicial)");
								aux = new Lista<Servicio>();
								newServicio.setVertexInicialId(pickUpId);
								aux.addAtEnd(newServicio);
								graph.addVertex(pickUpId, aux);
								elInicial = pickUpId;
								//.out.println("cree el vertice, añadi el servicio <-");

							}
							else if (yoPertenezcoA.size()>1)
							{
								//getDistance(Double.parseDouble(graph.vertex().keys().get(0).getKey().split("-")[0]),Double.parseDouble(graph.vertex().keys().get(0).getKey().split("-")[1]), Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude));

								for (int w=0;w<yoPertenezcoA.size();w++)
								{
									//Comparo las distancias
									if (menor>getDistance(Double.parseDouble(yoPertenezcoA.get(w).split("/")[0]), Double.parseDouble(yoPertenezcoA.get(w).split("/")[1]),Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude)))
									{
										menor = getDistance(Double.parseDouble(yoPertenezcoA.get(w).split("/")[0]), Double.parseDouble(yoPertenezcoA.get(w).split("/")[1]),Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude));
										elMenor = yoPertenezcoA.get(w);
									}

								}
								//Encontre el vertice con menor distancia desde el servicio, pertenece a ese vertice
								elInicial = elMenor;
								newServicio.setVertexInicialId(elInicial);
								graph.findVertex(elMenor).info().addAtEnd(newServicio);
							}
							//Si solo pertenece a un vertice
							else if (yoPertenezcoA.size()==1)
							{
								//							System.out.println("-> Caso el inicial pertenece a *un* vertice");
								//							System.out.println(graph.findVertex(aEsteVertice).info());
								newServicio.setVertexInicialId(aEsteVertice);
								graph.findVertex(aEsteVertice).info().addAtEnd(newServicio);
								elInicial = aEsteVertice;
								//	System.out.println("encontré el vertice - acabe <-" );
							}


							//Reinicio las variables repetidas
							yoPertenezcoA = new Lista<String>();
							elMenor = null;
							menor = Double.MAX_VALUE;

							for (int h = 0; h< graph.vertex().keys().size();h++)
							{
								//Si la distancia es menor al parametro, entonces pertenece a ese vertice
								if (getDistance(Double.parseDouble(graph.vertex().keys().get(h).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(h).getKey().split("/")[1]), Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude))<=param)
								{
									yoPertenezcoA.addAtEnd(graph.vertex().keys().get(h).getKey());
									aEsteVertice = graph.vertex().keys().get(h).getKey();
								}

							}
							if (yoPertenezcoA.size()==0)
							{
								//			System.out.println("-> Caso donde no pertenece a ningun vertice (final)");
								aux = new Lista<Servicio>();
								newServicio.setVertexFinalId(dropOffId);
								//aux.addAtEnd(newServicio);
								graph.addVertex(dropOffId, aux);
								elFinal = dropOffId;
								//	System.out.println("cree el vertice, añadi el servicio <-");

							}
							//TODO: Revision añadir servicio a donde llego
							if (yoPertenezcoA.size()>1)
							{
								for (int w= 0; w<yoPertenezcoA.size();w++)
								{
									if (menor>getDistance(Double.parseDouble(yoPertenezcoA.get(w).split("/")[0]), Double.parseDouble(yoPertenezcoA.get(w).split("/")[1]),Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude)))
									{
										menor = getDistance(Double.parseDouble(yoPertenezcoA.get(w).split("/")[0]), Double.parseDouble(yoPertenezcoA.get(w).split("/")[1]),Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude));
										elMenor = yoPertenezcoA.get(w);
									}
								}
								elFinal = elMenor;
								newServicio.setVertexFinalId(elFinal);
							}

							else if (yoPertenezcoA.size()==1)
							{
								//							System.out.println("-> Caso el final pertenece a *un* vertice");
								//							System.out.println(graph.findVertex(aEsteVertice).info());
								newServicio.setVertexFinalId(aEsteVertice);
								//graph.findVertex(aEsteVertice).info().addAtEnd(newServicio);
								elFinal = aEsteVertice;
								//System.out.println("encontré el vertice - acabe <-" );
							}
							//Recorro todos los servicios del vertice inicial, busco si tambien terminan en el final y le saco el promedio al arco
							//TODO: Recorre la lista despues de agregar el servicio;
							if (graph.findEdge(elInicial, elFinal)!=null)
							{
								graph.findEdge(elInicial, elFinal).getInfo().addServicio(newServicio);
							}
							else
							{
								InfoServicios infoArc = new InfoServicios(newServicio, elInicial, elFinal);
								graph.addEdge(elInicial, elFinal,infoArc);
							}

						}

						System.out.println("Iteracion "+ i);
						System.out.println("Número de arcos: " + graph.test());
						System.out.println("Número de vértices: " + graph.vertex().keys().size());
					}
					int sum = 0;
					for (int i=0;i<graph.vertex().keys().size();i++)
					{
						//			System.out.println("Cantidad de servicios en el vertice " +(i+1)  +": " + graph.vertex().get(graph.vertex().keys().get(i).getKey()).info().size());
						sum+=graph.vertex().get(graph.vertex().keys().get(i).getKey()).info().size();
					}
					System.out.println("Total de servicios: " + sum);
					System.out.println("Total de servicios saltados por lat/long vacias: " + out);
					System.out.println("Total neto: " + (sum + out));
				}
			}

			catch (Exception e) 
			{
				e.printStackTrace();
			} 
			persistirGrafo();
			System.out.println("Inside loadServices with " + direccionJson);
			return true;

		}
	}

	public double getDistance (double lat1, double lon1, double lat2, double lon2)
	{
		// TODO Auto-generated method stub
		final int R = 6371*1000; // Radious of the earth in meters
		Double latDistance = Math.toRadians(lat2-lat1);
		Double lonDistance = Math.toRadians(lon2-lon1);
		Double a = Math.sin(latDistance/2) * Math.sin(latDistance/2) + Math.cos(Math.toRadians(lat1))
		* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance/2) * Math.sin(lonDistance/2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		Double distance = R * c;
		return distance;
	}

	public void persistirGrafo()
	{	
		graph.persistirGrafo();

		cont++;

		try
		{
			File file = new File(".\\docs\\Leame.txt");
			if(!file.exists())
				file.createNewFile();

			FileWriter fileWriter = new FileWriter(".\\docs\\Leame.txt", true);
			PrintWriter out = new PrintWriter(fileWriter);	
			out.println("--------------------------------------------------------------------");
			out.println(json);
			out.println("Grafo " + cont);
			out.println("Dx " + param);
			out.println("Numero de vertices: " + graph.V());
			out.println("Numero de arcos: " + graph.E());

			out.close();
			fileWriter.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void leerGrafo(String direccionJsonGraph) {
		// TODO Auto-generated method stub
		JSONParser parser = new JSONParser();
		graph = new Graph<String, Lista<Servicio>, InfoServicios>(500);
		try 
		{
			Object obj = parser.parse(new FileReader(direccionJsonGraph));
			JSONObject jsonObject1 = (JSONObject) obj;
			JSONArray serviceList = (JSONArray) jsonObject1.get("Vertices");
			for (int i = 0; serviceList != null && i < serviceList.size(); i++)
			{
				JSONObject jsonObject = (JSONObject) serviceList.get(i);
				JSONObject vertexObject = (JSONObject) jsonObject.get("vertice" + (i+1));
				JSONObject infoVertexObject = (JSONObject) vertexObject.get("informacionVertice");
				String vertexId = (String) vertexObject.get("id");

				//informacion del vertice
				String dropOffZone =  infoVertexObject.get("dropOffZone") != null? (String) infoVertexObject.get("dropOffZone"): "0";
				String pickUpZone =  infoVertexObject.get("pickUpZone") != null? (String) infoVertexObject.get("pickUpZone"): "0";
				String horaInicial =  infoVertexObject.get("horaInicial") != null? (String) infoVertexObject.get("horaInicial"): "00:00:00.000";
				String pickUpLongitud =  infoVertexObject.get("pickUpLongitud") != null? (String) infoVertexObject.get("pickUpLongitud"): "00.0";
				String pickUpLatitud =  infoVertexObject.get("pickUpLatitud") != null? (String) infoVertexObject.get("pickUpLatitud"): "00.0";
				String horaFinal =  infoVertexObject.get("horaFinal") != null? (String) infoVertexObject.get("horaFinal"): "00:00:00.000";
				String tripMiles =  infoVertexObject.get("tripMiles") != null? (String) infoVertexObject.get("tripMiles"): "0.0";
				String tripTotal =  infoVertexObject.get("tripTotal") != null? (String) infoVertexObject.get("tripTotal"): "0.0";
				String fechaInicio =  infoVertexObject.get("fechaInicio") != null? (String) infoVertexObject.get("fechaInicio"): "0000-00-00";
				String fechaFinal =  infoVertexObject.get("fechaFinal") != null? (String) infoVertexObject.get("fechaFinal"): "0000-00-00";
				String tripId =  infoVertexObject.get("tripId") != null? (String) infoVertexObject.get("tripId"): "No-TripId";
				String taxiId =  infoVertexObject.get("taxiId") != null? (String) infoVertexObject.get("taxiId"): "No-TaxiId";
				String tripSeconds =  infoVertexObject.get("tripSeconds") != null? (String) infoVertexObject.get("tripSeconds"): "0";
				
				boolean toll = jsonObject.get("tolls") != null? Double.parseDouble((String) jsonObject.get("tolls"))>0? true : false : false;
				
				Lista<Servicio> listServiciosVertex = new Lista<Servicio>();
				Servicio newServicio = new Servicio(tripId, taxiId, Integer.parseInt(tripSeconds), Double.parseDouble(tripMiles), Double.parseDouble(tripTotal), fechaInicio+"T"+horaInicial, fechaFinal+"T"+horaFinal, Integer.parseInt(pickUpZone), Integer.parseInt(dropOffZone), pickUpLatitud, pickUpLongitud, toll);
				if(graph.findVertex(vertexId)!=null)
				{
					listServiciosVertex = (Lista<Servicio>) ((Vertex)graph.vertex().get(vertexId)).info();
				}	
				listServiciosVertex.add(newServicio);
				graph.addVertex(vertexId, listServiciosVertex);		

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			JSONParser parser2 = new JSONParser();
			Object obj2 = parser2.parse(new FileReader(direccionJsonGraph));
			JSONObject jsonObject1 = (JSONObject) obj2;
			JSONArray serviceList = (JSONArray) jsonObject1.get("Vertices");
			for (int i = 0; serviceList != null && i < serviceList.size(); i++)
			{
				JSONObject jsonObject = (JSONObject) serviceList.get(i);
				JSONObject vertexObject = (JSONObject) jsonObject.get("vertice" + (i+1));
				JSONArray edgesObject = (JSONArray) vertexObject.get("Arcos");

				for (int j = 0; edgesObject !=null && j < edgesObject.size(); j++) {
					JSONObject jsonObject2 = (JSONObject) edgesObject.get(j);
					JSONObject edgeObject = (JSONObject) jsonObject2.get("arco" + (j+1));
					JSONObject infoEdgeObject = (JSONObject) edgeObject.get("informacionArco");

					String trip_total = infoEdgeObject.get("trip_total") != null? (String) infoEdgeObject.get("trip_total"): "0.0";
					String trip_seconds = infoEdgeObject.get("trip_seconds") != null? (String) infoEdgeObject.get("trip_seconds"): "0.0";
					String trip_miles = infoEdgeObject.get("trip_miles") != null? (String) infoEdgeObject.get("trip_miles"): "0.0";

					String fuenteEdge = edgeObject.get("fuente")!=null? (String) edgeObject.get("fuente"): "No-Fuente";
					String destinoEdge = edgeObject.get("destino")!=null? (String) edgeObject.get("destino"): "No-Destino";

					InfoServicios aux = new InfoServicios(Double.parseDouble(trip_total), Double.parseDouble(trip_miles), Double.parseDouble(trip_seconds));
					graph.addEdge(fuenteEdge, destinoEdge, aux);

				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} 
		System.out.println("Número de arcos: " + graph.test());
		System.out.println("Número de keys (vértices): " + graph.vertex().keys().size());
		vertexMasCongestionado();
	}

	public Vertex vertexMasCongestionado()
	{
		Vertex vertice = null;
		int comparador = 0;
		Lista<Keys<String>> llaves = graph.vertex().keys();
		for (int i = 0; i < llaves.size(); i++) 
		{
			Vertex auxVertex = graph.findVertex(llaves.get(i).getKey());
			Lista edges = auxVertex.arcos();
			int cantidadVertex = 0;

			cantidadVertex = edges.size();

			for (int j = 0; j < llaves.size(); j++) 
			{
				if(j!=i)
				{
					Vertex auxVertex2 = graph.findVertex(llaves.get(j).getKey());
					Lista edges2 = auxVertex2.arcos();
					for (int k = 0; k < edges2.size(); k++) {
						Edge auxEdge = (Edge) edges2.get(k);

						if(auxEdge.getDestino().equals(auxVertex))
							cantidadVertex++;
					}
				}
			}

			if(cantidadVertex>comparador)
			{
				comparador = cantidadVertex;
				vertice = auxVertex;
			}

		}	
		return vertice;
	}

	public Lista componentesConexos()
	{
		Lista newLista = new Lista();

		Graph grafoInvertido = graph.reverse();
		ArbolBinarioRN arbol = graph.depthFirstSearch(grafoInvertido.darPostOrdenInvertido(grafoInvertido.depthFirstSearch()));

		Lista llaves = arbol.getKeys();
		for (int i = 0; i < llaves.size(); i++) {
			Lista aux = (Lista) arbol.get(llaves.get(i));
			CompFuertementeConexa comp = new CompFuertementeConexa(aux);
			newLista.add(comp);
		}

		return newLista;
	}



	public boolean cargarSistemaV2(String direccionJson, int pParam) 
	{
		JSONParser parser = new JSONParser();
		graphString = new Graph<String, VerticeConServicios, InfoServicios>(500);
		param = pParam;
		int out =0;
		if (!direccionJson.equals(DIRECCION_LARGE_JSON))
		{

			if(direccionJson.equals(DIRECCION_MEDIUM_JSON))
				json = "Medium";
			else
				json = "Small";
			try 
			{
				Object obj = parser.parse(new FileReader(direccionJson));
				JSONArray serviceList = (JSONArray) obj;
				for (int i = 0; serviceList != null && i < serviceList.size(); i++)
				{

					JSONObject jsonObject = (JSONObject) serviceList.get(i);
					String pickup_centroid_latitude = jsonObject.get("pickup_centroid_latitude") != null? (String) jsonObject.get("pickup_centroid_latitude"):"0.0";
					String pickup_centroid_longitude = jsonObject.get("pickup_centroid_longitude") != null? (String) jsonObject.get("pickup_centroid_longitude"):"0.0";
					String dropoff_centroid_latitude = jsonObject.get("dropoff_centroid_latitude") != null? (String) jsonObject.get("dropoff_centroid_latitude"):"0.0";
					String dropoff_centroid_longitude = jsonObject.get("dropoff_centroid_longitude") != null? (String) jsonObject.get("dropoff_centroid_longitude"):"0.0";

					//Si algun servicio no tiene definidas lat/long de recogida y terminacionm, salte a la siguiente iteracion
					if (Double.parseDouble(pickup_centroid_latitude)==0.0 || Double.parseDouble(pickup_centroid_longitude)==0.0 || Double.parseDouble(dropoff_centroid_latitude)==0.0 || Double.parseDouble(dropoff_centroid_longitude)==0.0 )
					{
						out++;
						System.out.println("*******Salte*******");
						continue;

					}

					String company =  jsonObject.get("company") != null? (String) jsonObject.get("company"):"Independent Owner";
					String dropoff_community_area = jsonObject.get("dropoff_community_area") != null? (String) jsonObject.get("dropoff_community_area"): "0";
					String pickup_community_area =  jsonObject.get("pickup_community_area") != null? (String) jsonObject.get("pickup_community_area"):"0";
					String taxi_id =  jsonObject.get("taxi_id") != null? (String) jsonObject.get("taxi_id"):"No-ID";
					String trip_end_timestamp =  jsonObject.get("trip_end_timestamp") != null? (String) jsonObject.get("trip_end_timestamp"):"0000-00-00T00:00:00.000";
					String trip_id =  jsonObject.get("trip_id") != null? (String) jsonObject.get("trip_id"):"No-TripID";
					String trip_miles =  jsonObject.get("trip_miles") != null? (String) jsonObject.get("trip_miles"):"0";
					String trip_seconds =  jsonObject.get("trip_seconds") != null? (String) jsonObject.get("trip_seconds"):"0";
					String trip_start_timestamp =  jsonObject.get("trip_start_timestamp") != null? (String) jsonObject.get("trip_start_timestamp"):"0000-00-00T00:00:00.000";
					String trip_total=  jsonObject.get("trip_total") != null? (String) jsonObject.get("trip_total"):"0.0";
					
					boolean toll = jsonObject.get("tolls") != null? Double.parseDouble((String) jsonObject.get("tolls"))>0? true : false : false;

					String pickUpId = pickup_centroid_latitude + "/" + pickup_centroid_longitude;
					String dropOffId = dropoff_centroid_latitude + "/" + dropoff_centroid_longitude;

					Servicio newServicio = new Servicio (trip_id, taxi_id, Integer.parseInt(trip_seconds),Double.parseDouble(trip_miles), Double.parseDouble(trip_total), trip_start_timestamp, trip_end_timestamp,Integer.parseInt(pickup_community_area), Integer.parseInt(dropoff_community_area),pickup_centroid_latitude, pickup_centroid_longitude, toll);

					//Si está vacío el grafo
					if (graphString.vertex().data()==0)
					{
						VerticeConServicios auxSalida = new VerticeConServicios(Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude));
						auxSalida.agregarServicioQueSale(trip_id);
						graphString.addVertex(pickUpId, auxSalida);



						VerticeConServicios auxLlegada = new VerticeConServicios(Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude));
						auxLlegada.agregarServicioQueLlega(trip_id);
						graphString.addVertex(dropOffId, auxLlegada);


						InfoServicios infoArc = new InfoServicios (newServicio, pickUpId, dropOffId);
						graphString.addEdge(pickUpId,dropOffId, infoArc);

					}
					//El grafo ya tiene vertices
					else
					{
						String elInicial = null;
						String elFinal = null;


						//Variables repetidas en inicio y final
						double menor = Double.MAX_VALUE;
						String elMenor = "";
						//La zona inicial pertenece a *algún* vértice existente?
						Lista<String> yoPertenezcoASalida = new Lista<String>();
						Lista<String> yoPertenezcoALlegada = new Lista<String>();

						String aEsteVerticeSalida = "";
						String aEsteVerticeLlegada = "";

						for (int h = 0; h< graphString.vertex().keys().size();h++)
						{
							//Si la distancia es menor al parametro, entonces pertenece a ese vertice
							//							System.out.println("-> For que recorre todas las llaves");
							//							System.out.println("Tamanio: " + graphString.vertex().keys().size());
							//							System.out.println("Llave: " + graphString.vertex().keys().get(h).getKey());
							if (getDistance(Double.parseDouble(graphString.vertex().keys().get(h).getKey().split("/")[0]),Double.parseDouble(graphString.vertex().keys().get(h).getKey().split("/")[1]), Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude))<=param)
							{
								//TODO: Agregar a una lista todos los vertices a los que pertenece, recorrer toda la lista y buscar cual tiene menor distancia.
								yoPertenezcoASalida.addAtEnd(graphString.vertex().keys().get(h).getKey());	
								aEsteVerticeSalida = graphString.vertex().keys().get(h).getKey();
								//System.out.println("**El inicial pertenece a: " + aEsteVertice + "**");
							}
							if (getDistance(Double.parseDouble(graphString.vertex().keys().get(h).getKey().split("/")[0]),Double.parseDouble(graphString.vertex().keys().get(h).getKey().split("/")[1]), Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude))<=param)
							{
								yoPertenezcoALlegada.addAtEnd(graphString.vertex().keys().get(h).getKey());	
								aEsteVerticeLlegada = graphString.vertex().keys().get(h).getKey();

							}
						}
						//						System.out.println("recorri todos los vertices y comprobe pertenencia del inicial <-");
						//						System.out.println("Yo (el inicial) pertenezco a " + yoPertenezcoA.size());
						if (yoPertenezcoASalida.size()==0)
						{
							//Si no pertenece a ningun vertice por la distancia de parametro, entonces es un vertice nuevo
							//	System.out.println("-> Caso donde no pertenece a ningun vertice (inicial)");
							VerticeConServicios auxSalida = new VerticeConServicios(Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude));
							auxSalida.agregarServicioQueSale(trip_id);
							graphString.addVertex(pickUpId, auxSalida);

							elInicial = pickUpId;
							//.out.println("cree el vertice, añadi el servicio <-");

						}
						else if (yoPertenezcoASalida.size()==1)
						{
							graphString.findVertex(aEsteVerticeSalida).info().agregarServicioQueSale(trip_id);
							elInicial = aEsteVerticeSalida;
						}
						else if (yoPertenezcoASalida.size()>1)
						{
							//getDistance(Double.parseDouble(graphString.vertex().keys().get(0).getKey().split("-")[0]),Double.parseDouble(graphString.vertex().keys().get(0).getKey().split("-")[1]), Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude));

							for (int w=0;w<yoPertenezcoASalida.size();w++)
							{
								//Comparo las distancias

								if (menor>getDistance(Double.parseDouble(yoPertenezcoASalida.get(w).split("/")[0]), Double.parseDouble(yoPertenezcoASalida.get(w).split("/")[1]),Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude)))
								{
									menor = getDistance(Double.parseDouble(yoPertenezcoASalida.get(w).split("/")[0]), Double.parseDouble(yoPertenezcoASalida.get(w).split("/")[1]),Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude));
									elMenor = yoPertenezcoASalida.get(w);
								}

							}
							//Encontre el vertice con menor distancia desde el servicio, pertenece a ese vertice
							elInicial = elMenor;
							graphString.findVertex(elInicial).info().agregarServicioQueSale(trip_id);
						}

						//__________________________________________________________

						if (yoPertenezcoALlegada.size()==0)
						{
							VerticeConServicios auxLlegada = new VerticeConServicios(Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude));
							auxLlegada.agregarServicioQueLlega(trip_id);
							graphString.addVertex(dropOffId, auxLlegada);

							elFinal = dropOffId;

						}

						else if (yoPertenezcoALlegada.size()==1)
						{
							graphString.findVertex(aEsteVerticeLlegada).info().agregarServicioQueLlega(trip_id);
							elFinal = aEsteVerticeLlegada;
						}
						//TODO: Revision añadir servicio a donde llego
						if (yoPertenezcoALlegada.size()>1)
						{
							for (int w= 0; w<yoPertenezcoALlegada.size();w++)
							{
								if (menor>getDistance(Double.parseDouble(yoPertenezcoALlegada.get(w).split("/")[0]), Double.parseDouble(yoPertenezcoALlegada.get(w).split("/")[1]),Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude)))
								{
									menor = getDistance(Double.parseDouble(yoPertenezcoALlegada.get(w).split("/")[0]), Double.parseDouble(yoPertenezcoALlegada.get(w).split("/")[1]),Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude));
									elMenor = yoPertenezcoALlegada.get(w);
								}
							}
							elFinal = elMenor;
							graphString.findVertex(elFinal).info().agregarServicioQueLlega(trip_id);
						}

						if (graphString.findEdge(elInicial, elFinal)!=null)
						{
							graphString.findEdge(elInicial, elFinal).getInfo().addServicio(newServicio);
						}
						else
						{
							InfoServicios infoArc = new InfoServicios(newServicio, elInicial, elFinal);
							graphString.addEdge(elInicial, elFinal,infoArc);
						}

					}

					System.out.println("Iteracion "+ i);
					System.out.println("Número de arcos: " + graphString.test());
					System.out.println("Número de vértices: " + graphString.vertex().keys().size());
				}
				int sum = 0;
				for (int i=0;i<graphString.vertex().keys().size();i++)
				{
					//			System.out.println("Cantidad de servicios en el vertice " +(i+1)  +": " + graphString.vertex().get(graphString.vertex().keys().get(i).getKey()).info().size());
					sum+=graphString.vertex().get(graphString.vertex().keys().get(i).getKey()).info().numeroServiciosQueLlegan();
					System.out.println("Total de servicios: " + sum);
					System.out.println("Total de servicios saltados por lat/long vacias: " + out);
					System.out.println("Total neto: " + (sum + out));

				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			} 

			persistirGrafo();
			System.out.println("Inside loadServices with " + direccionJson);
			return true;
		}
		else
		{
			json = "Large";

			for (int k=2;k<9;k++)
			{
				try
				{
					Object obj = parser.parse(new FileReader("./data/taxi-trips-wrvz-psew-subset-0"+k+"-02-2017.json"));
					JSONArray serviceList = (JSONArray) obj;
					for (int i = 0; serviceList != null && i < serviceList.size(); i++)
					{
						JSONObject jsonObject = (JSONObject) serviceList.get(i);
						String pickup_centroid_latitude = jsonObject.get("pickup_centroid_latitude") != null? (String) jsonObject.get("pickup_centroid_latitude"):"0.0";
						String pickup_centroid_longitude = jsonObject.get("pickup_centroid_longitude") != null? (String) jsonObject.get("pickup_centroid_longitude"):"0.0";
						String dropoff_centroid_latitude = jsonObject.get("dropoff_centroid_latitude") != null? (String) jsonObject.get("dropoff_centroid_latitude"):"0.0";
						String dropoff_centroid_longitude = jsonObject.get("dropoff_centroid_longitude") != null? (String) jsonObject.get("dropoff_centroid_longitude"):"0.0";

						//Si algun servicio no tiene definidas lat/long de recogida y terminacionm, salte a la siguiente iteracion
						if (Double.parseDouble(pickup_centroid_latitude)==0.0 || Double.parseDouble(pickup_centroid_longitude)==0.0 || Double.parseDouble(dropoff_centroid_latitude)==0.0 || Double.parseDouble(dropoff_centroid_longitude)==0.0 )
						{
							out++;
							System.out.println("*******Salte*******");
							continue;

						}

						String company =  jsonObject.get("company") != null? (String) jsonObject.get("company"):"Independent Owner";
						String dropoff_community_area = jsonObject.get("dropoff_community_area") != null? (String) jsonObject.get("dropoff_community_area"): "0";
						String pickup_community_area =  jsonObject.get("pickup_community_area") != null? (String) jsonObject.get("pickup_community_area"):"0";
						String taxi_id =  jsonObject.get("taxi_id") != null? (String) jsonObject.get("taxi_id"):"No-ID";
						String trip_end_timestamp =  jsonObject.get("trip_end_timestamp") != null? (String) jsonObject.get("trip_end_timestamp"):"0000-00-00T00:00:00.000";
						String trip_id =  jsonObject.get("trip_id") != null? (String) jsonObject.get("trip_id"):"No-TripID";
						String trip_miles =  jsonObject.get("trip_miles") != null? (String) jsonObject.get("trip_miles"):"0";
						String trip_seconds =  jsonObject.get("trip_seconds") != null? (String) jsonObject.get("trip_seconds"):"0";
						String trip_start_timestamp =  jsonObject.get("trip_start_timestamp") != null? (String) jsonObject.get("trip_start_timestamp"):"0000-00-00T00:00:00.000";
						String trip_total=  jsonObject.get("trip_total") != null? (String) jsonObject.get("trip_total"):"0.0";
						boolean toll = jsonObject.get("tolls") != null? Double.parseDouble((String) jsonObject.get("tolls"))>0? true : false : false;

						String pickUpId = pickup_centroid_latitude + "/" + pickup_centroid_longitude;
						String dropOffId = dropoff_centroid_latitude + "/" + dropoff_centroid_longitude;

						Servicio newServicio = new Servicio (trip_id, taxi_id, Integer.parseInt(trip_seconds),Double.parseDouble(trip_miles), Double.parseDouble(trip_total), trip_start_timestamp, trip_end_timestamp,Integer.parseInt(pickup_community_area), Integer.parseInt(dropoff_community_area),pickup_centroid_latitude, pickup_centroid_longitude, toll);

						//Si está vacío el grafo
						if (graphString.vertex().data()==0)
						{
							VerticeConServicios auxSalida = new VerticeConServicios(Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude));
							auxSalida.agregarServicioQueSale(trip_id);
							graphString.addVertex(pickUpId, auxSalida);



							VerticeConServicios auxLlegada = new VerticeConServicios(Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude));
							auxLlegada.agregarServicioQueLlega(trip_id);
							graphString.addVertex(dropOffId, auxLlegada);


							InfoServicios infoArc = new InfoServicios (newServicio, pickUpId, dropOffId);
							graphString.addEdge(pickUpId,dropOffId, infoArc);

						}
						//El grafo ya tiene vertices
						else
						{
							String elInicial = null;
							String elFinal = null;


							//Variables repetidas en inicio y final
							double menor = Double.MAX_VALUE;
							String elMenor = "";
							//La zona inicial pertenece a *algún* vértice existente?
							Lista<String> yoPertenezcoASalida = new Lista<String>();
							Lista<String> yoPertenezcoALlegada = new Lista<String>();

							String aEsteVerticeSalida = "";
							String aEsteVerticeLlegada = "";

							for (int h = 0; h< graphString.vertex().keys().size();h++)
							{
								//Si la distancia es menor al parametro, entonces pertenece a ese vertice
								//							System.out.println("-> For que recorre todas las llaves");
								//							System.out.println("Tamanio: " + graphString.vertex().keys().size());
								//							System.out.println("Llave: " + graphString.vertex().keys().get(h).getKey());
								if (getDistance(Double.parseDouble(graphString.vertex().keys().get(h).getKey().split("/")[0]),Double.parseDouble(graphString.vertex().keys().get(h).getKey().split("/")[1]), Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude))<=param)
								{
									//TODO: Agregar a una lista todos los vertices a los que pertenece, recorrer toda la lista y buscar cual tiene menor distancia.
									yoPertenezcoASalida.addAtEnd(graphString.vertex().keys().get(h).getKey());	
									aEsteVerticeSalida = graphString.vertex().keys().get(h).getKey();
									//System.out.println("**El inicial pertenece a: " + aEsteVertice + "**");
								}
								if (getDistance(Double.parseDouble(graphString.vertex().keys().get(h).getKey().split("/")[0]),Double.parseDouble(graphString.vertex().keys().get(h).getKey().split("/")[1]), Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude))<=param)
								{
									yoPertenezcoALlegada.addAtEnd(graphString.vertex().keys().get(h).getKey());	
									aEsteVerticeLlegada = graphString.vertex().keys().get(h).getKey();

								}
							}
							//						System.out.println("recorri todos los vertices y comprobe pertenencia del inicial <-");
							//						System.out.println("Yo (el inicial) pertenezco a " + yoPertenezcoA.size());
							if (yoPertenezcoASalida.size()==0)
							{
								//Si no pertenece a ningun vertice por la distancia de parametro, entonces es un vertice nuevo
								//	System.out.println("-> Caso donde no pertenece a ningun vertice (inicial)");
								VerticeConServicios auxSalida = new VerticeConServicios(Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude));
								auxSalida.agregarServicioQueSale(trip_id);
								graphString.addVertex(pickUpId, auxSalida);

								elInicial = pickUpId;
								//.out.println("cree el vertice, añadi el servicio <-");

							}
							else if (yoPertenezcoASalida.size()==1)
							{
								graphString.findVertex(aEsteVerticeSalida).info().agregarServicioQueSale(trip_id);
								elInicial = aEsteVerticeSalida;
							}
							else if (yoPertenezcoASalida.size()>1)
							{
								//getDistance(Double.parseDouble(graphString.vertex().keys().get(0).getKey().split("-")[0]),Double.parseDouble(graphString.vertex().keys().get(0).getKey().split("-")[1]), Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude));

								for (int w=0;w<yoPertenezcoASalida.size();w++)
								{
									//Comparo las distancias

									if (menor>getDistance(Double.parseDouble(yoPertenezcoASalida.get(w).split("/")[0]), Double.parseDouble(yoPertenezcoASalida.get(w).split("/")[1]),Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude)))
									{
										menor = getDistance(Double.parseDouble(yoPertenezcoASalida.get(w).split("/")[0]), Double.parseDouble(yoPertenezcoASalida.get(w).split("/")[1]),Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude));
										elMenor = yoPertenezcoASalida.get(w);
									}

								}
								//Encontre el vertice con menor distancia desde el servicio, pertenece a ese vertice
								elInicial = elMenor;
								graphString.findVertex(elInicial).info().agregarServicioQueSale(trip_id);
							}

							//__________________________________________________________

							if (yoPertenezcoALlegada.size()==0)
							{
								VerticeConServicios auxLlegada = new VerticeConServicios(Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude));
								auxLlegada.agregarServicioQueLlega(trip_id);
								graphString.addVertex(dropOffId, auxLlegada);

								elFinal = dropOffId;

							}

							else if (yoPertenezcoALlegada.size()==1)
							{
								graphString.findVertex(aEsteVerticeLlegada).info().agregarServicioQueLlega(trip_id);
								elFinal = aEsteVerticeLlegada;
							}
							//TODO: Revision añadir servicio a donde llego
							if (yoPertenezcoALlegada.size()>1)
							{
								for (int w= 0; w<yoPertenezcoALlegada.size();w++)
								{
									if (menor>getDistance(Double.parseDouble(yoPertenezcoALlegada.get(w).split("/")[0]), Double.parseDouble(yoPertenezcoALlegada.get(w).split("/")[1]),Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude)))
									{
										menor = getDistance(Double.parseDouble(yoPertenezcoALlegada.get(w).split("/")[0]), Double.parseDouble(yoPertenezcoALlegada.get(w).split("/")[1]),Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude));
										elMenor = yoPertenezcoALlegada.get(w);
									}
								}
								elFinal = elMenor;
								graphString.findVertex(elFinal).info().agregarServicioQueLlega(trip_id);
							}

							if (graphString.findEdge(elInicial, elFinal)!=null)
							{
								graphString.findEdge(elInicial, elFinal).getInfo().addServicio(newServicio);
							}
							else
							{
								InfoServicios infoArc = new InfoServicios(newServicio, elInicial, elFinal);
								graphString.addEdge(elInicial, elFinal,infoArc);
							}

						}

						System.out.println("Iteracion "+ i);
						System.out.println("Número de arcos: " + graphString.test());
						System.out.println("Número de vértices: " + graphString.vertex().keys().size());
					}
					int sum = 0;
					for (int i=0;i<graphString.vertex().keys().size();i++)
					{
						//			System.out.println("Cantidad de servicios en el vertice " +(i+1)  +": " + graphString.vertex().get(graphString.vertex().keys().get(i).getKey()).info().size());
						sum+=graphString.vertex().get(graphString.vertex().keys().get(i).getKey()).info().numeroServiciosQueLlegan();
						System.out.println("Total de servicios: " + sum);
						System.out.println("Total de servicios saltados por lat/long vacias: " + out);
						System.out.println("Total neto: " + (sum + out));

					}
					return true;
				}

				catch (Exception e)
				{
				}
			}
			return true;
		}
	}
}







