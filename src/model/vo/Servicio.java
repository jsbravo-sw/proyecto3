package model.vo;

/**
 * Representation of a Service object
 */
public class Servicio implements Comparable<Servicio>
{	
	private String tripId,taxiId,fechaInicio,fechaFinal,horaInicial,horaFinal, pickUpLatitud,pickUpLongitud, vertexInicialId, vertexFinalId;
	private int tripSeconds, pickUpZone, dropOffZone;
	private double tripMiles,tripTotal, distanciaHarvesiana;
	private boolean peaje;

	public Servicio (String pTripId, String pTaxiId, int pTripSeconds, double pTripMiles, double pTripTotal, String pTripStartTimeStamp, String pTripEndTimeStamp, int pPickupZone, int pDropOffZone, String pPickupLatitud, String pPickupLongitud, boolean pPeaje)
	{
		tripId = pTripId;
		taxiId = pTaxiId;
		tripSeconds = pTripSeconds;
		tripTotal = pTripTotal;
		tripMiles = pTripMiles;
		
		setPickupLatitud(pPickupLatitud);
		setPickupLongitud(pPickupLongitud);
		
		pickUpZone = pPickupZone;
		dropOffZone = pDropOffZone;
		String[] x = pTripStartTimeStamp.split("T");
		fechaInicio = x[0];
		horaInicial = x[1];
		
		String[] y = pTripEndTimeStamp.split("T");
		fechaFinal = y[0];
		horaFinal = y[1];
		
		setPeaje(pPeaje);

	}
	/**
	 * @return id - Trip_id
	 */
	public String getTripId()
	{
		// TODO Auto-generated method stub
		return tripId;
	}	
	
	
	public double getTripTotal()
	{
		// TODO Auto-generated method stub
		return tripTotal;
	}	

	/**
	 * @return id - Taxi_id
	 */
	public String getTaxiId() {
		// TODO Auto-generated method stub
		return taxiId;
	}	

	/**
	 * @return time - Time of the trip in seconds.
	 */
	public int getTripSeconds() {
		// TODO Auto-generated method stub
		return tripSeconds;
	}

	/**
	 * @return miles - Distance of the trip in miles.
	 */
	public double getTripMiles() {
		// TODO Auto-generated method stub
		return tripMiles;
	}

	/**
	 * @return total - Total cost of the trip
	 */
	public String getFechaInicial() {
		// TODO Auto-generated method stub
		return fechaInicio;
	}
	
	public String getHoraInicial() {
		// TODO Auto-generated method stub
		return horaInicial;
	}
	
	public String getFechaFinal() {
		// TODO Auto-generated method stub
		return fechaFinal;
	}
	
	public String getHoraFinal() {
		// TODO Auto-generated method stub
		return horaFinal;
	}

	@Override
	public int compareTo(Servicio arg0) {
		return arg0.getFechaInicial().compareTo(fechaInicio);
	}
	
	public int getPickupZone() {
		return pickUpZone;
	}
	public int getDropOffZone() {
		return dropOffZone;
	}
	public String getStartTime() {
		// TODO Auto-generated method stub
		return horaInicial;
	}
	public String getPickupLatitud() {
		return pickUpLatitud;
	}
	public void setPickupLatitud(String pickUpLatitud) {
		this.pickUpLatitud = pickUpLatitud;
	}
	public String getPickupLongitud() {
		return pickUpLongitud;
	}
	public void setPickupLongitud(String pickUpLongitud) {
		this.pickUpLongitud = pickUpLongitud;
	}
	public double getDistanciaHarvesiana() {
		return distanciaHarvesiana;
	}
	public void setDistanciaHarvesiana(double distanciaHarvesiana) {
		this.distanciaHarvesiana = distanciaHarvesiana;
	}
	public String getVertexInicialId() {
		return vertexInicialId;
	}
	public void setVertexInicialId(String vertexInicialId) {
		this.vertexInicialId = vertexInicialId;
	}
	public String getVertexFinalId() {
		return vertexFinalId;
	}
	public void setVertexFinalId(String vertexFinalId) {
		this.vertexFinalId = vertexFinalId;
	}
	public boolean isPeaje() {
		return peaje;
	}
	public void setPeaje(boolean peaje) {
		this.peaje = peaje;
	}
}