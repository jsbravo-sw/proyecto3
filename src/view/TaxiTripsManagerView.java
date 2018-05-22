package view;

import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import controller.Controller;
import model.data_structures.Graph.Vertex;
import model.data_structures.Lista;
import model.logic.TaxiTripsManager;
import model.vo.CompFuertementeConexa;
import model.vo.Taxi;
import model.vo.VerticeConServicios;

/**
 * view del programa
 */
public class TaxiTripsManagerView 
{

	public static void main(String[] args) 
	{
		Scanner sc = new Scanner(System.in);
		boolean fin=false;
		while(!fin)
		{
			//imprime menu
			printMenu();

			//opcion req
			int option = sc.nextInt();

			switch(option)
			{

			case 1: // cargar informacion a procesar

				//imprime menu cargar
				printMenuCargar();

				//opcion cargar
				int optionCargar = sc.nextInt();

				//directorio json
				String linkJson = "";
				switch (optionCargar)
				{
				//direccion json pequeno
				case 1:

					linkJson = TaxiTripsManager.DIRECCION_SMALL_JSON;
					break;

					//direccion json mediano
				case 2:

					linkJson = TaxiTripsManager.DIRECCION_MEDIUM_JSON;
					break;

					//direccion json grande
				case 3:

					linkJson = TaxiTripsManager.DIRECCION_LARGE_JSON;
					break;
				}

				System.out.println("Ingrese el Dx");
				int pParam = sc.nextInt();
				System.out.println("Datos cargados: " + linkJson);
				//Memoria y tiempo
				long memoryBeforeCase1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				long startTime = System.nanoTime();

				//Cargar data
				Controller.cargarSistema(linkJson, pParam);

				//Tiempo en cargar
				long endTime = System.nanoTime();
				long duration = (endTime - startTime)/(1000000);

				//Memoria usada
				long memoryAfterCase1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				System.out.println("Tiempo en cargar: " + duration + " milisegundos \nMemoria utilizada:  "+ ((memoryAfterCase1 - memoryBeforeCase1)/1000000.0) + " MB");

				break;
				
			case 2:
					String direccionJsonGraph = ".\\data\\graph.json";
					Controller.leerGrafo(direccionJsonGraph);
					break;
			case 3:
				VerticeConServicios aux2 = Controller.verticeMasCongestionado();
				Maps.mapaReq1(aux2.getLatRef(), aux2.getLongRef(), aux2.numeroServiciosTotal(), Controller.getCantidadServicios());
				System.out.println("latitud: " + aux2.getLatRef() + " longitud: " + aux2.getLongRef() + "\ntotal servicios que salieron: " + aux2.numeroServiciosQueSalen() + "\ntotal servicios que llegaron: " + aux2.numeroServiciosQueLlegan());
				break;
			case 4: 
					Lista aux = Controller.componentesConexos();
					System.out.println("Total de componenetes fuertemente conexas: " + aux.size());
					for (int i = 0; i < aux.size(); i++) 
					{
						Color color = ((CompFuertementeConexa)aux.get(i)).getColorComponente();
						System.out.println("Componente conexa " + (1+i) + ": " + "\n" + "Color de la componente(RGB): " + color.getRed() +"-" + color.getBlue()  + "-" + color.getGreen()  + "  Cantidad de vertices en la componente: " + ((CompFuertementeConexa)aux.get(i)).getTamañoComp());
					}
				break;
			case 5: 
				fin=true;
				sc.close();
				break;

			}
		}
	}
	/**
	 * Menu 
	 */
	private static void printMenu() //
	{
		System.out.println(" ");
		System.out.println("---------ISIS 1206 - Estructuras de datos----------");
		System.out.println("---------------------Taller 7 ----------------------");
		System.out.println("Iniciar la Fuente de Datos a Consultar :");
		System.out.println("1. Cargar toda la informacion del sistema de una fuente de datos (small, medium o large).");
		System.out.println("2. Cargar un grafo a partir de una fuente de datos.");
		System.out.println("3. Mostrar la informacion del vertice mas congestionado");
		System.out.println("4. Mostrar la informacion de las componentes fuertemente conexas.");
		System.out.println("5. Salir");
		System.out.println("Ingrese el numero de la opcion seleccionada y presione <Enter> para confirmar: (e.g., 1):");

	}

	private static void printMenuCargar()
	{
		System.out.println("-- Que fuente de datos desea cargar?");
		System.out.println("-- 1. Small");
		System.out.println("-- 2. Medium");
		System.out.println("-- 3. Large");
		System.out.println("-- Ingrese el numero de la fuente a cargar y presione <Enter> para confirmar: (e.g., 1)");
	}


}


