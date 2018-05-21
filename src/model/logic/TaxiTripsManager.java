package model.logic;


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
	public Graph<String, VerticeConServicios, InfoServicios> graphString;
	public int param;

	private int cont = 0;
	private String json;

	public boolean cargarSistema(String direccionJson, int pParam) 
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

					//System.out.println("Iteracion "+ i);
					//System.out.println("Número de arcos: " + graphString.test());
					//System.out.println("Número de vértices: " + graphString.vertex().keys().size());
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

						//System.out.println("Iteracion "+ i);
						//System.out.println("Número de arcos: " + graphString.test());
						//System.out.println("Número de vértices: " + graphString.vertex().keys().size());
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
		graphString.persistirGrafo();

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
			out.println("Numero de vertices: " + graphString.V());
			out.println("Numero de arcos: " + graphString.E());

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
		graphString = new Graph<String, VerticeConServicios, InfoServicios>(500);
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

				graphString.addVertex(vertexId, aux);		

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
					graphString.addEdge(fuenteEdge, destinoEdge, aux);

				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} 
		System.out.println("Número de arcos: " + graphString.test());
		System.out.println("Número de keys (vértices): " + graphString.vertex().keys().size());
	}

	public VerticeConServicios req1()
	{
		VerticeConServicios vertice = null;
		int comparador = 0;
		Lista<Keys<String>> llaves = graphString.vertex().keys();
		for (int i = 0; i < llaves.size(); i++) 
		{
			Vertex auxVertex = graphString.findVertex(llaves.get(i).getKey());
			VerticeConServicios auxVer =  (VerticeConServicios) auxVertex.info();
			comparador = auxVer.numeroServiciosTotal();
			vertice = auxVer;

			for (int j = 0; j < llaves.size(); j++) 
			{
				if(j!=i)
				{
					Vertex auxVertex2 = graphString.findVertex(llaves.get(j).getKey());
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

	public Lista componentesConexos()
	{
		Lista newLista = new Lista();

		Graph grafoInvertido = graphString.reverse();
		ArbolBinarioRN arbol = graphString.depthFirstSearch(grafoInvertido.darPostOrdenInvertido(grafoInvertido.depthFirstSearch()));

		Lista llaves = arbol.getKeys();
		for (int i = 0; i < llaves.size(); i++) {
			Lista aux = (Lista) arbol.get(llaves.get(i));
			CompFuertementeConexa comp = new CompFuertementeConexa(aux);
			newLista.add(comp);
		}

		return newLista;
	}

	public void verReq4(Lista<VerticeConServicios> lista)
	{

		if (lista.size()!=0)
		{
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
}







