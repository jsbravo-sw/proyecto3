package API;

import model.data_structures.ArbolBinarioRN;
import model.data_structures.Graph.Vertex;
import model.data_structures.IList;
import model.data_structures.Lista;
import model.vo.Servicio;
import model.vo.Taxi;
import model.vo.VerticeConServicios;

/**
 * API para la clase de logica principal  
 */
public interface ITaxiTripsManager 
{
	
	//1C
	/**
	 * Dada la direccion del json que se desea cargar, se generan vo's, estructuras y datos necesarias
	 * @param direccionJson, ubicacion del json a cargar
	 * @return true si se lo logro cargar, false de lo contrario
	 */
	public boolean cargarSistema(String direccionJson, int pParam);
	
	public void persistirGrafo();
	
	public void leerGrafo(String direccionJsonGraph);
	
	public VerticeConServicios req1();
	
	public Lista req2();
	
	public int getCantidadServicios();

	public void verReq4(boolean pRun);

	public void verReq5();

}