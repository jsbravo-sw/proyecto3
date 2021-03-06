package model.logic;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import view.Maps;

public class TaxiTripsManager implements ITaxiTripsManager {
	public static final String DIRECCION_SMALL_JSON = "./data/taxi-trips-wrvz-psew-subset-small.json";
	public static final String DIRECCION_MEDIUM_JSON = "./data/taxi-trips-wrvz-psew-subset-medium.json";
	public static final String DIRECCION_LARGE_JSON = "./data/taxi-trips-wrvz-psew-subset-large.json";

	//String que representa la lat/long del vertice y la lista sus servicios asociados
	@JsonProperty
	public Graph<String, VerticeConServicios, InfoServicios> graph;
	public int param;

	private Lista<String> req5Min, req5Max, req6Min, req6Max;
	private String json;

	public boolean cargarSistema(String direccionJson, int pParam) 
	{
		JSONParser parser = new JSONParser();
		graph = new Graph<String, VerticeConServicios, InfoServicios>(500);
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
						//System.out.println("*******Salte*******");
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
						VerticeConServicios auxSalida = new VerticeConServicios(Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude));
						auxSalida.agregarServicioQueSale(trip_id);
						graph.addVertex(pickUpId, auxSalida);



						VerticeConServicios auxLlegada = new VerticeConServicios(Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude));
						auxLlegada.agregarServicioQueLlega(trip_id);
						graph.addVertex(dropOffId, auxLlegada);


						InfoServicios infoArc = new InfoServicios (newServicio, pickUpId, dropOffId);
						graph.addEdge(pickUpId,dropOffId, infoArc);

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

						for (int h = 0; h< graph.vertex().keys().size();h++)
						{
							//Si la distancia es menor al parametro, entonces pertenece a ese vertice
							//							System.out.println("-> For que recorre todas las llaves");
							//							System.out.println("Tamanio: " + graphString.vertex().keys().size());
							//							System.out.println("Llave: " + graphString.vertex().keys().get(h).getKey());
							if (getDistance(Double.parseDouble(graph.vertex().keys().get(h).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(h).getKey().split("/")[1]), Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude))<=param)
							{
								//TODO: Agregar a una lista todos los vertices a los que pertenece, recorrer toda la lista y buscar cual tiene menor distancia.
								yoPertenezcoASalida.addAtEnd(graph.vertex().keys().get(h).getKey());	
								aEsteVerticeSalida = graph.vertex().keys().get(h).getKey();
								//System.out.println("**El inicial pertenece a: " + aEsteVertice + "**");
							}
							if (getDistance(Double.parseDouble(graph.vertex().keys().get(h).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(h).getKey().split("/")[1]), Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude))<=param)
							{
								yoPertenezcoALlegada.addAtEnd(graph.vertex().keys().get(h).getKey());	
								aEsteVerticeLlegada = graph.vertex().keys().get(h).getKey();

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
							graph.addVertex(pickUpId, auxSalida);

							elInicial = pickUpId;
							//.out.println("cree el vertice, añadi el servicio <-");

						}
						else if (yoPertenezcoASalida.size()==1)
						{
							graph.findVertex(aEsteVerticeSalida).info().agregarServicioQueSale(trip_id);
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
							graph.findVertex(elInicial).info().agregarServicioQueSale(trip_id);
						}

						//__________________________________________________________

						if (yoPertenezcoALlegada.size()==0)
						{
							VerticeConServicios auxLlegada = new VerticeConServicios(Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude));
							auxLlegada.agregarServicioQueLlega(trip_id);
							graph.addVertex(dropOffId, auxLlegada);

							elFinal = dropOffId;

						}

						else if (yoPertenezcoALlegada.size()==1)
						{
							graph.findVertex(aEsteVerticeLlegada).info().agregarServicioQueLlega(trip_id);
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
							graph.findVertex(elFinal).info().agregarServicioQueLlega(trip_id);
						}

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

					//System.out.println("Iteracion "+ i);
					//System.out.println("Número de arcos: " + graphString.test());
					//System.out.println("Número de vértices: " + graphString.vertex().keys().size());
				}
				int sum = 0;
				for (int i=0;i<graph.vertex().keys().size();i++)
				{
					//			System.out.println("Cantidad de servicios en el vertice " +(i+1)  +": " + graphString.vertex().get(graphString.vertex().keys().get(i).getKey()).info().size());
					sum+=graph.vertex().get(graph.vertex().keys().get(i).getKey()).info().numeroServiciosQueLlegan();
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
						if (graph.vertex().data()==0)
						{
							VerticeConServicios auxSalida = new VerticeConServicios(Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude));
							auxSalida.agregarServicioQueSale(trip_id);
							graph.addVertex(pickUpId, auxSalida);



							VerticeConServicios auxLlegada = new VerticeConServicios(Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude));
							auxLlegada.agregarServicioQueLlega(trip_id);
							graph.addVertex(dropOffId, auxLlegada);


							InfoServicios infoArc = new InfoServicios (newServicio, pickUpId, dropOffId);
							graph.addEdge(pickUpId,dropOffId, infoArc);

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

							for (int h = 0; h< graph.vertex().keys().size();h++)
							{
								//Si la distancia es menor al parametro, entonces pertenece a ese vertice
								//							System.out.println("-> For que recorre todas las llaves");
								//							System.out.println("Tamanio: " + graphString.vertex().keys().size());
								//							System.out.println("Llave: " + graphString.vertex().keys().get(h).getKey());
								if (getDistance(Double.parseDouble(graph.vertex().keys().get(h).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(h).getKey().split("/")[1]), Double.parseDouble(pickup_centroid_latitude), Double.parseDouble(pickup_centroid_longitude))<=param)
								{
									//TODO: Agregar a una lista todos los vertices a los que pertenece, recorrer toda la lista y buscar cual tiene menor distancia.
									yoPertenezcoASalida.addAtEnd(graph.vertex().keys().get(h).getKey());	
									aEsteVerticeSalida = graph.vertex().keys().get(h).getKey();
									//System.out.println("**El inicial pertenece a: " + aEsteVertice + "**");
								}
								if (getDistance(Double.parseDouble(graph.vertex().keys().get(h).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(h).getKey().split("/")[1]), Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude))<=param)
								{
									yoPertenezcoALlegada.addAtEnd(graph.vertex().keys().get(h).getKey());	
									aEsteVerticeLlegada = graph.vertex().keys().get(h).getKey();

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
								graph.addVertex(pickUpId, auxSalida);

								elInicial = pickUpId;
								//.out.println("cree el vertice, añadi el servicio <-");

							}
							else if (yoPertenezcoASalida.size()==1)
							{
								graph.findVertex(aEsteVerticeSalida).info().agregarServicioQueSale(trip_id);
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
								graph.findVertex(elInicial).info().agregarServicioQueSale(trip_id);
							}

							//__________________________________________________________

							if (yoPertenezcoALlegada.size()==0)
							{
								VerticeConServicios auxLlegada = new VerticeConServicios(Double.parseDouble(dropoff_centroid_latitude), Double.parseDouble(dropoff_centroid_longitude));
								auxLlegada.agregarServicioQueLlega(trip_id);
								graph.addVertex(dropOffId, auxLlegada);

								elFinal = dropOffId;

							}

							else if (yoPertenezcoALlegada.size()==1)
							{
								graph.findVertex(aEsteVerticeLlegada).info().agregarServicioQueLlega(trip_id);
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
								graph.findVertex(elFinal).info().agregarServicioQueLlega(trip_id);
							}

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

						//System.out.println("Iteracion "+ i);
						//System.out.println("Número de arcos: " + graphString.test());
						//System.out.println("Número de vértices: " + graphString.vertex().keys().size());
					}
					int sum = 0;
					for (int i=0;i<graph.vertex().keys().size();i++)
					{
						//			System.out.println("Cantidad de servicios en el vertice " +(i+1)  +": " + graphString.vertex().get(graphString.vertex().keys().get(i).getKey()).info().size());
						sum+=graph.vertex().get(graph.vertex().keys().get(i).getKey()).info().numeroServiciosQueLlegan();
						System.out.println("Total de servicios: " + sum);
						System.out.println("Total de servicios saltados por lat/long vacias: " + out);
						System.out.println("Total neto: " + (sum + out));

					}
					return true;
				}

				catch (Exception e)
				{
					e.printStackTrace();
				} 

				persistirGrafo();
				System.out.println("Inside loadServices with " + direccionJson);

			}
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
		//		try
		//		{
		//			File file = new File(".\\docs\\Leame.txt");
		//			if(!file.exists())
		//				file.createNewFile();
		//
		//			FileWriter fileWriter = new FileWriter(".\\docs\\Leame.txt", true);
		//			PrintWriter out = new PrintWriter(fileWriter);	
		//			out.println("--------------------------------------------------------------------");
		//			out.println(json);
		//			out.println("Grafo " + 1);
		//			out.println("Dx " + param);
		//			out.println("Numero de vertices: " + graphString.V());
		//			out.println("Numero de arcos: " + graphString.E());
		//
		//			out.close();
		//			fileWriter.close();
		//		}
		//		catch(Exception e)
		//		{
		//			e.printStackTrace();
		//		}
	}

	@Override
	public void leerGrafo(String direccionJsonGraph) {
		// TODO Auto-generated method stub
		JSONParser parser = new JSONParser();
		graph = new Graph<String, VerticeConServicios, InfoServicios>(500);
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
				String[] latLon = vertexId.split("/");
				VerticeConServicios aux = new VerticeConServicios(Double.parseDouble(latLon[0]), Double.parseDouble(latLon[1]));

				JSONArray serviciosQueLlegan = (JSONArray) infoVertexObject.get("ServiciosQueLlegan");
				Lista<String> serviciosLlegan = (Lista<String>) aux.getServiciosQueLlegan();

				for (int j = 0; j < serviciosQueLlegan.size(); j++) {
					serviciosLlegan.add((String) serviciosQueLlegan.get(j));
				}

				JSONArray serviciosQueSalen = (JSONArray) infoVertexObject.get("ServiciosQueSalen");
				Lista<String> serviciosSalen = (Lista<String>) aux.getServiciosQueSalen();
				for (int k = 0; k < serviciosQueSalen.size(); k++) {
					serviciosSalen.add((String) serviciosQueSalen.get(k));
				}

				graph.addVertex(vertexId, aux);		

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
	}

	public int getCantidadServicios()
	{
		int cantidad = 0;
		Lista<Keys<String>> llaves = graph.vertex().keys();

		for (int i = 0; i < llaves.size(); i++) 
		{
			VerticeConServicios auxVer = graph.findVertex(llaves.get(i).getKey()).info();
			cantidad += auxVer.numeroServiciosQueSalen();
		}

		return cantidad;
	}

	public VerticeConServicios req1()
	{
		VerticeConServicios vertice = null;
		int comparador = 0;
		Lista<Keys<String>> llaves = graph.vertex().keys();
		for (int i = 0; i < llaves.size(); i++) 
		{
			Vertex auxVertex = graph.findVertex(llaves.get(i).getKey());
			VerticeConServicios auxVer =  (VerticeConServicios) auxVertex.info();
			comparador = auxVer.numeroServiciosTotal();
			vertice = auxVer;

			for (int j = 0; j < llaves.size(); j++) 
			{
				if(j!=i)
				{
					Vertex auxVertex2 = graph.findVertex(llaves.get(j).getKey());
					VerticeConServicios auxVer2 = (VerticeConServicios) auxVertex2.info();
					int cantidadVertex2 = auxVer2.numeroServiciosTotal();
					if(cantidadVertex2>comparador)
					{
						comparador = cantidadVertex2;
						vertice = auxVer2;
					}
				}
			}

		}	
		return vertice;
	}

	public Lista req2()
	{
		Lista newLista = new Lista();

		Graph grafoInvertido = graph.reverse();
		Lista postOrdenInvertido = grafoInvertido.darPostOrdenInvertido(grafoInvertido.depthFirstSearch());
		ArbolBinarioRN arbol = graph.depthFirstSearch(postOrdenInvertido);

		Lista llaves = arbol.getKeys();
		for (int i = 0; i < llaves.size(); i++) {
			Lista aux = (Lista) arbol.get(llaves.get(i));
			CompFuertementeConexa comp = new CompFuertementeConexa(aux);
			newLista.add(comp);
		}
		return newLista;
	}

	public void verReq4(boolean pRun)
	{
		if (!pRun)
		{
			System.out.println("El camino a seguir entre los dos puntos es: ");
		}
		double distancia = Double.MIN_VALUE;
		double costo = 0;
		double duracion = 0;
		Lista<String> caminoVertices = req4();
		Lista <VerticeConServicios> lista = new Lista<VerticeConServicios>();
		for (int i=0;i<caminoVertices.size();i++)
		{

			if (!caminoVertices.get(i).contains("No"))
			{
				double lat = Double.parseDouble(caminoVertices.get(i).split("/")[0]);
				double lon = Double.parseDouble(caminoVertices.get(i).split("/")[1]);

				distancia+= Double.parseDouble(caminoVertices.get(i).split("/")[3]);
				costo+= Double.parseDouble(caminoVertices.get(i).split("/")[5]);
				duracion+= Double.parseDouble(caminoVertices.get(i).split("/")[6]);
				VerticeConServicios aux = new VerticeConServicios(lat, lon);
				System.out.println("Vértice:" + lat +"/" + lon);
				lista.addAtEnd(aux);
			}
		}


		if (lista.size()==0)
		{
			verReq4(true);
		}


		if (lista.size()!=0)
		{

			System.out.println("Con una distancia total de: " + distancia + " millas");
			System.out.println("Con una duración total de: " + duracion + " segundos");
			System.out.println("Con un costo total de: " + costo + " dólares");
			


			Maps.mapaReq4(lista);

			//Abrir el mapa en el explorador
			try 
			{
				File f = new File(Maps.mapaReq4);
				java.awt.Desktop.getDesktop().browse(f.toURI());
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}

	}

	public Lista<String> cargarAleatorio() 
	{
		Lista<String> resp = new Lista<String>();
		try 
		{
			FileReader fr = new FileReader(new File("./data/Chicago Streets.csv"));
			BufferedReader br = new BufferedReader(fr);
			br.readLine();
			String l = br.readLine();

			String posicionesCalle[];
			double latitude = 0;
			double longitude = 0;

			while(l != null && !l.contains("MULTILINESTRING EMPTY"))
			{
				String[] arreglo = l.split(";");

				posicionesCalle = new String[arreglo.length-6];
				for (int i = 6; i < arreglo.length; i++) 
				{
					if(i > 6)
					{
						arreglo[i] = arreglo[i].substring(1, arreglo[i].length());
					}
					posicionesCalle[i-6] = arreglo[i];
					latitude = Double.parseDouble(posicionesCalle[i-6].split(" ")[1]);
					longitude = Double.parseDouble(posicionesCalle[i-6].split(" ")[0]);
					resp.addAtEnd(latitude + ";" + longitude);

				}

				l = br.readLine();
			}


			fr.close();
			br.close();
			return resp;

		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	public Lista<String> req4()
	{
		Lista<String> dirAleatorias = cargarAleatorio();
		String posicionInicial = dirAleatorias.get((int)(Math.random()*dirAleatorias.size()));
		String posicionFinal = dirAleatorias.get((int)(Math.random()*dirAleatorias.size()));
		String vertexInicial = "";
		String vertexFinal = "";

		double menorDistanciaInicial = Double.MAX_VALUE;
		double menorDistanciaFinal = Double.MAX_VALUE;
		for (int i=0; i<graph.vertex().keys().size();i++)
		{
			if (getDistance(Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[1]), Double.parseDouble(posicionInicial.split(";")[0]), Double.parseDouble(posicionInicial.split(";")[1]))<menorDistanciaInicial)
			{
				vertexInicial = graph.vertex().keys().get(i).getKey();
				menorDistanciaInicial =getDistance(Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[1]), Double.parseDouble(posicionInicial.split(";")[0]), Double.parseDouble(posicionInicial.split(";")[1]));
			}
			if (getDistance(Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[1]), Double.parseDouble(posicionFinal.split(";")[0]), Double.parseDouble(posicionFinal.split(";")[1]))<menorDistanciaFinal)
			{
				vertexFinal = graph.vertex().keys().get(i).getKey();
				menorDistanciaFinal =getDistance(Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[1]), Double.parseDouble(posicionFinal.split(";")[0]), Double.parseDouble(posicionFinal.split(";")[1]));
			}
		}
		return graph.getPath(vertexInicial, vertexFinal, 0);


	}
	
	public void req5()
	{
		Lista<String> dirAleatorias = cargarAleatorio();
		String posicionInicial = dirAleatorias.get((int)(Math.random()*dirAleatorias.size()));
		String posicionFinal = dirAleatorias.get((int)(Math.random()*dirAleatorias.size()));
		String vertexInicial = "";
		String vertexFinal = "";

		double menorDistanciaInicial = Double.MAX_VALUE;
		double menorDistanciaFinal = Double.MAX_VALUE;
		for (int i=0; i<graph.vertex().keys().size();i++)
		{
			if (getDistance(Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[1]), Double.parseDouble(posicionInicial.split(";")[0]), Double.parseDouble(posicionInicial.split(";")[1]))<menorDistanciaInicial)
			{
				vertexInicial = graph.vertex().keys().get(i).getKey();
				menorDistanciaInicial =getDistance(Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[1]), Double.parseDouble(posicionInicial.split(";")[0]), Double.parseDouble(posicionInicial.split(";")[1]));
			}
			if (getDistance(Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[1]), Double.parseDouble(posicionFinal.split(";")[0]), Double.parseDouble(posicionFinal.split(";")[1]))<menorDistanciaFinal)
			{
				vertexFinal = graph.vertex().keys().get(i).getKey();
				menorDistanciaFinal =getDistance(Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[1]), Double.parseDouble(posicionFinal.split(";")[0]), Double.parseDouble(posicionFinal.split(";")[1]));
			}
		}

		req5Max = new Lista<String>();
		req5Min = new Lista<String>();
		req5Min = graph.getPath(vertexInicial, vertexFinal, 2);
		req5Max = graph.getPath(vertexFinal, vertexInicial, 2);
		

	}
	
	public void verReq5()
	{
		req5();
		verReq5Min(false);
		try {
			Thread.sleep((5*1000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		verReq5Max(false);
	}
	
	
	public void verReq5Min(boolean pRun)
	{
		if (!pRun)
		{
			System.out.println("El camino a seguir entre los dos puntos es: ");
		}
		double duracion = Double.MIN_VALUE;
		double costo = 0;
		double distancia = 0;
		Lista<String> caminoVertices = req5Min;
		Lista <VerticeConServicios> lista = new Lista<VerticeConServicios>();
		for (int i=0;i<caminoVertices.size();i++)
		{

			if (!caminoVertices.get(i).contains("No"))
			{
				double lat = Double.parseDouble(caminoVertices.get(i).split("/")[0]);
				double lon = Double.parseDouble(caminoVertices.get(i).split("/")[1]);

				duracion += Double.parseDouble(caminoVertices.get(i).split("/")[3]);
				costo+= Double.parseDouble(caminoVertices.get(i).split("/")[5]);
				distancia+= Double.parseDouble(caminoVertices.get(i).split("/")[4]);

				VerticeConServicios aux = new VerticeConServicios(lat, lon);
				System.out.println("Vértice:" + lat +"/" + lon);
				lista.addAtEnd(aux);
			}
		}


		if (lista.size()==0)
		{
			verReq5Min(true);
		}


		if (lista.size()!=0)
		{

			System.out.println("Con una duración total de: " + duracion + " segundos");
			System.out.println("Con una distancia total de: " + distancia + " millas");
			System.out.println("Con un costo total de: " + costo + " dolares");


			Maps.mapaReq5(lista);

			//Abrir el mapa en el explorador
			try 
			{
				File f = new File(Maps.mapaReq5);
				java.awt.Desktop.getDesktop().browse(f.toURI());
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}

	}
	
	public void verReq5Max(boolean pRun)
	{
		if (!pRun)
		{
			System.out.println("El camino a seguir entre los dos puntos es: ");
		}
		double duracion = Double.MIN_VALUE;
		double costo = 0;
		double distancia = 0;
		Lista<String> caminoVertices = req5Max;
		Lista <VerticeConServicios> lista = new Lista<VerticeConServicios>();
		for (int i=0;i<caminoVertices.size();i++)
		{

			if (!caminoVertices.get(i).contains("No"))
			{
				double lat = Double.parseDouble(caminoVertices.get(i).split("/")[0]);
				double lon = Double.parseDouble(caminoVertices.get(i).split("/")[1]);

				duracion += Double.parseDouble(caminoVertices.get(i).split("/")[3]);

				costo+= Double.parseDouble(caminoVertices.get(i).split("/")[5]);
				distancia+= Double.parseDouble(caminoVertices.get(i).split("/")[4]);
				VerticeConServicios aux = new VerticeConServicios(lat, lon);
				System.out.println("Vértice:" + lat +"/" + lon);
				lista.addAtEnd(aux);
			}
		}


		if (lista.size()==0)
		{
			verReq5Max(true);
		}


		if (lista.size()!=0)
		{

			System.out.println("Con una duración total de: " + duracion + " segundos");
			System.out.println("Con una distancia total de: " + distancia + " millas");
			System.out.println("Con un costo total de: " + costo + " dolares");


			Maps.mapaReq5(lista);

			//Abrir el mapa en el explorador
			try 
			{
				File f = new File(Maps.mapaReq5);
				java.awt.Desktop.getDesktop().browse(f.toURI());
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}

	}
	
	public void req6()
	{
		Lista<String> dirAleatorias = cargarAleatorio();
		String posicionInicial = dirAleatorias.get((int)(Math.random()*dirAleatorias.size()));
		String posicionFinal = dirAleatorias.get((int)(Math.random()*dirAleatorias.size()));
		String vertexInicial = "";
		String vertexFinal = "";

		double menorDistanciaInicial = Double.MAX_VALUE;
		double menorDistanciaFinal = Double.MAX_VALUE;
		for (int i=0; i<graph.vertex().keys().size();i++)
		{
			if (getDistance(Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[1]), Double.parseDouble(posicionInicial.split(";")[0]), Double.parseDouble(posicionInicial.split(";")[1]))<menorDistanciaInicial)
			{
				vertexInicial = graph.vertex().keys().get(i).getKey();
				menorDistanciaInicial =getDistance(Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[1]), Double.parseDouble(posicionInicial.split(";")[0]), Double.parseDouble(posicionInicial.split(";")[1]));
			}
			if (getDistance(Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[1]), Double.parseDouble(posicionFinal.split(";")[0]), Double.parseDouble(posicionFinal.split(";")[1]))<menorDistanciaFinal)
			{
				vertexFinal = graph.vertex().keys().get(i).getKey();
				menorDistanciaFinal =getDistance(Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[0]),Double.parseDouble(graph.vertex().keys().get(i).getKey().split("/")[1]), Double.parseDouble(posicionFinal.split(";")[0]), Double.parseDouble(posicionFinal.split(";")[1]));
			}
		}

		req6Max = new Lista<String>();
		req6Min = new Lista<String>();
		req6Min = graph.getPath(vertexInicial, vertexFinal, 4);
		//graph.printAllPaths(vertexInicial, vertexFinal);
		

	}

	public void verReq6()
	{
		req6();
		verReq6Min(false);
		try {
			Thread.sleep((5*1000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//verReq6Max(false);
	}
	
	
	public void verReq6Min(boolean pRun)
	{
		if (!pRun)
		{
			System.out.println("El camino a seguir entre los dos puntos es: ");
		}
		double duracion = Double.MIN_VALUE;
		double costo = 0;
		double distancia = 0;
		Lista<String> caminoVertices = req6Min;
		Lista <VerticeConServicios> lista = new Lista<VerticeConServicios>();
		for (int i=0;i<caminoVertices.size();i++)
		{

			if (!caminoVertices.get(i).contains("No"))
			{
				double lat = Double.parseDouble(caminoVertices.get(i).split("/")[0]);
				double lon = Double.parseDouble(caminoVertices.get(i).split("/")[1]);

				duracion += Double.parseDouble(caminoVertices.get(i).split("/")[3]);
				costo+= Double.parseDouble(caminoVertices.get(i).split("/")[5]);
				distancia+= Double.parseDouble(caminoVertices.get(i).split("/")[4]);

				VerticeConServicios aux = new VerticeConServicios(lat, lon);
				System.out.println("Vértice:" + lat +"/" + lon);
				lista.addAtEnd(aux);
			}
		}



		if (lista.size()!=0)
		{

			System.out.println("Con una duración total de: " + duracion + " segundos");
			System.out.println("Con una distancia total de: " + distancia + " millas");
			System.out.println("Con un costo total de: " + costo + " dolares");


			Maps.mapaReq6(lista);

			//Abrir el mapa en el explorador
			try 
			{
				File f = new File(Maps.mapaReq6);
				java.awt.Desktop.getDesktop().browse(f.toURI());
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}

	}
	
	public void verReq6Max(boolean pRun)
	{
		if (!pRun)
		{
			System.out.println("El camino a seguir entre los dos puntos es: ");
		}
		double duracion = Double.MIN_VALUE;
		double costo = 0;
		double distancia = 0;
		Lista<String> caminoVertices = req6Max;
		Lista <VerticeConServicios> lista = new Lista<VerticeConServicios>();
		for (int i=0;i<caminoVertices.size();i++)
		{

			if (!caminoVertices.get(i).contains("No"))
			{
				double lat = Double.parseDouble(caminoVertices.get(i).split("/")[0]);
				double lon = Double.parseDouble(caminoVertices.get(i).split("/")[1]);

				duracion += Double.parseDouble(caminoVertices.get(i).split("/")[3]);

				costo+= Double.parseDouble(caminoVertices.get(i).split("/")[5]);
				distancia+= Double.parseDouble(caminoVertices.get(i).split("/")[4]);
				VerticeConServicios aux = new VerticeConServicios(lat, lon);
				System.out.println("Vértice:" + lat +"/" + lon);
				lista.addAtEnd(aux);
			}
		}


		if (lista.size()==0)
		{
			verReq6Max(true);
		}


		if (lista.size()!=0)
		{

			System.out.println("Con una duración total de: " + duracion + " segundos");
			System.out.println("Con una distancia total de: " + distancia + " millas");
			System.out.println("Con un costo total de: " + costo + " dolares");


			Maps.mapaReq6(lista);

			//Abrir el mapa en el explorador
			try 
			{
				File f = new File(Maps.mapaReq6);
				java.awt.Desktop.getDesktop().browse(f.toURI());
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}

	}
}







