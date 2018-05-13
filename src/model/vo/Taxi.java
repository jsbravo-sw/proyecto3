package model.vo;

import model.data_structures.Lista;
import model.data_structures.hashTableSeparateChaining;

/**
 * Representation of a taxi object
 */
public class Taxi implements Comparable<Taxi>
{
	private String taxiId,company;

	protected Lista<Servicio> servicios;

	protected int cantidadServiciosPorHora, tiempoTotalServiciosPorHora, totalServicios, tiempoTotal;

	protected double distanciaRecorridaPorHora, plataGanadaPorHora, rentabilidad, totalFacturacion, totalDinero, totalMillas;


	public Taxi(String pTaxiId, String pCompany)
	{
		taxiId = pTaxiId;
		company = pCompany;
		servicios = new Lista<>();
		cantidadServiciosPorHora = 0;
		tiempoTotalServiciosPorHora = 0;
		distanciaRecorridaPorHora = 0;
		plataGanadaPorHora = 0;
		totalServicios=0;
		totalDinero = 0.0;
		totalMillas = 0.0;
		tiempoTotal = 0;
	}
	/**
	 * @return id - taxi_id
	 */
	public String getTaxiId()
	{
		// TODO Auto-generated method stub
		return taxiId;
	}
	
	public int getTiempoTotal()
	{
		return tiempoTotal;
	}

	/**
	 * @return company
	 */
	public String getCompany()
	{
		// TODO Auto-generated method stub
		return company;
	}

	public Lista<Servicio> getServicios()
	{
		return servicios;
	}

	public void addServicio(Servicio pServicio)
	{
		servicios.addInOrderRepeated(pServicio);	
		totalDinero+=pServicio.getTripTotal();
		totalMillas+=pServicio.getTripMiles();
		totalServicios ++;
		tiempoTotal+=pServicio.getTripSeconds();

	}


	public int cantidadServiciosPorHora()
	{
		return cantidadServiciosPorHora;
	}

	@Override
	public int compareTo(Taxi o) 
	{
		return this.taxiId.compareTo(o.getTaxiId());
	}	

	public double getDistanciaRecorridaHora()
	{
		return distanciaRecorridaPorHora;
	}

	public double getPlataGanadaHora()
	{
		return plataGanadaPorHora;
	}

	public int getTiempoTotalServiciosHora()
	{
		return tiempoTotalServiciosPorHora;
	}

	public double getRentabilidad() {
		return rentabilidad;
	}
	public void setRentabilidad(double max) {
		this.rentabilidad = max;
	}

	public  void setTotalFacturacion(double totalFacturacion) {
		// TODO Auto-generated method stub
		this.totalFacturacion = totalFacturacion;
	}
	public double getTotalFacturacion() {
		// TODO Auto-generated method stub
		return totalFacturacion;
	}

	public double getTotalMillas()
	{
		return totalMillas;
	}
	public double getTotalDinero()
	{
		return totalDinero;
	}
	public int getTotalServicios()
	{
		return totalServicios;
	}

}