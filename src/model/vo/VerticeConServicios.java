package model.vo;

import model.data_structures.IList;
import model.data_structures.Lista;

public class VerticeConServicios implements Comparable<VerticeConServicios>{

    private double latref;
    private double longref;
//    Lista de IDs de servicios que llegan o salen de este vértice de referencia
    private IList<String> serviciosQueLlegan;
    private IList<String> serviciosQueSalen;

    public VerticeConServicios(double latitud,double longitud){
        this.latref = latitud;
        this.longref = longitud;
  this.serviciosQueLlegan = new Lista<String>(); // inicializar la lista de servicios
  this.serviciosQueSalen = new Lista<String>(); // inicializar la lista de servicios        
    }

    public double getLatRef() {
        return latref;
    }

    public double getLongRef() {
        return longref;
    }

    public IList<String> getServiciosQueLlegan()
    {
    	return serviciosQueLlegan;
    }
    
    public IList<String> getServiciosQueSalen()
    {
    	return serviciosQueSalen;
    }
    
    public int numeroServiciosQueLlegan(){
        return serviciosQueLlegan.size();
    }
    
    public int numeroServiciosQueSalen(){
        return serviciosQueSalen.size();
    }
    
    public int numeroServiciosTotal(){
        return numeroServiciosQueLlegan() + numeroServiciosQueSalen();
    }
    
    /**
     * Agrega servicio al listado de los que llegan
     * @param idServicio
     */
    public void agregarServicioQueLlega(String idServicio){
        ((Lista<String>) serviciosQueLlegan).addAtEnd(idServicio);
    }
    
    /**
     * Agrega servicio al listado de los que salen
     * @param idServicio
     */
    public void agregarServicioQueSale(String idServicio){
        ((Lista<String>) serviciosQueSalen).addAtEnd(idServicio);
    }

    @Override
    public int compareTo(VerticeConServicios o) {
        return Integer.compare(this.numeroServiciosTotal(), o.numeroServiciosTotal());
    }

    public void print(){
        System.out.println("Latitud: " + this.latref + ". Longitud: "+this.longref);
        System.out.println("Total servicios que salieron: " + this.numeroServiciosQueSalen() + ". Total servicios que llegaron: "+this.numeroServiciosQueLlegan());
        
        System.out.println("___________________________________");;
    }
}
