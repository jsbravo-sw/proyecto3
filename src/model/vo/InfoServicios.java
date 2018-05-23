package model.vo;

import java.util.Comparator;

public class InfoServicios implements Comparable<InfoServicios>
{
	private String vertexInicialId, vertexFinalId;
	private double trip_total, trip_miles;
	private double  trip_seconds;
	private int cantidadServicios=0;
	private boolean peaje;
	
	public InfoServicios(double pTripTotal, double pTripMiles, double pTripSeconds)
	{
		trip_miles += pTripMiles;
		trip_total += pTripTotal;
		trip_seconds += pTripSeconds;
		cantidadServicios++;
	}
	public InfoServicios(Servicio pServicio, String pInicial, String pFinal)
	{
		trip_miles += pServicio.getTripMiles();
		trip_total += pServicio.getTripTotal();
		trip_seconds += pServicio.getTripSeconds();
		vertexInicialId = pInicial;
		vertexFinalId = pFinal;
		peaje = pServicio.hayPeaje(); 
		cantidadServicios++;
	}
	public double getTrip_total() {
		return ((double)Math.round(trip_total*100/cantidadServicios))/100;
	}
	public void setTrip_total(double trip_total) {
		this.trip_total = trip_total;
	}
	public double getTrip_miles() {
		return ((double) Math.round(trip_miles*100/cantidadServicios))/100;
	}
	public void setTrip_miles(double trip_miles) {
		this.trip_miles = trip_miles;
	}
	public double getTrip_seconds() {
		return ((double) Math.round(trip_seconds*100/cantidadServicios))/100;
	}
	public void setTrip_seconds(double trip_seconds) {
		this.trip_seconds = trip_seconds;
	}
	public String getVertexInicialId()
	{
		return vertexInicialId;
	}
	public String getVertexFinalId()
	{
		return vertexFinalId;
	}
	@Override
	public int compareTo(InfoServicios o) {
		return vertexInicialId.compareTo(o.getVertexInicialId()) - vertexFinalId.compareTo(o.getVertexFinalId());
	}
	
	public void addServicio (Servicio pServicio)
	{
		cantidadServicios++;
		trip_miles+= pServicio.getTripMiles();
		trip_total+=pServicio.getTripTotal();
		trip_seconds+=pServicio.getTripSeconds();
		peaje = peaje != true ? pServicio.hayPeaje() : true; 
		
	}

	public static Comparator<InfoServicios> InfoServiciosComparadorPorDuracion = new Comparator<InfoServicios>()
	{
		public int compare(InfoServicios iS1, InfoServicios iS2)
		{
			return (int) (iS1.getTrip_seconds() - iS2.getTrip_seconds());
		}
	};

	public static Comparator<InfoServicios> InfoServiciosComparadorPorPrecio = new Comparator<InfoServicios>()
	{
		public int compare(InfoServicios iS1, InfoServicios iS2)
		{
			return (int) (iS1.getTrip_total() - iS2.getTrip_total());
		}
	};

	public static Comparator<InfoServicios> InfoServiciosComparadorPorDistancia = new Comparator<InfoServicios>()
	{
		public int compare(InfoServicios iS1, InfoServicios iS2)
		{
			return (int) (iS1.getTrip_miles() - iS2.getTrip_miles());
		}
	};

	public double hayPeaje() 
	{
		//Si hay peaje, retorna el peso máximo para que no lo tenga en cuenta
		return  peaje? Double.MAX_VALUE : 0;
	}

}
