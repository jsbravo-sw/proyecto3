package controller;

import API.ITaxiTripsManager;
import model.data_structures.ArbolBinarioRN;
import model.data_structures.IList;
import model.data_structures.Lista;
import model.logic.TaxiTripsManager;
import model.vo.Servicio;
import model.vo.Taxi;

public class Controller 
{
	/**
	 * modela el manejador de la clase l�gica
	 */
	private static ITaxiTripsManager manager =new TaxiTripsManager();

	//Carga El sistema
	public static boolean cargarSistema(String direccionJson, int pParam)
	{
		return manager.cargarSistema(direccionJson, pParam);
	}
	
	public static void persistirGrafo()
	{
		manager.persistirGrafo();
	}
	
	public static void leerGrafo(String direccionJsonGraph)
	{
		manager.leerGrafo(direccionJsonGraph);
	}
}
