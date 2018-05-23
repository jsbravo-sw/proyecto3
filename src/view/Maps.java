package view;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import model.data_structures.Lista;
import model.vo.VerticeConServicios;

public class Maps {

	private static boolean escribio = false;
	private static boolean escribio2 = false;
	private static boolean escribio3 = false;
	private static boolean escribio4 = false;
	
	public final static String direccionReq1 = "./data/templates/templateReq1.html";

	public final static String mapaReq1 = "./data/templates/mapaReq1.html";

	public final static String mapaReq2 = "./data/templates/mapaReq2.html";

	public final static String direccionReq2 = "./data/templates/templateReq2.html";

	public final static String mapaReq3 = "./data/templates/mapaReq3.html";
	
	public final static String direccionReq3 = "./data/templates/templateReq3.html";

	public final static String direccionReq4 = "./data/templates/templateReq4.html";
	
	public final static String mapaReq4 = "data/templates/mapaReq4.html";

	public final static String direccionReq5 = "./data/templates/templateReq5.html";
	
	public final static String mapaReq5 = "data/templates/mapaReq5.html";

	public final static String direccionReq6 = "./data/templates/templateReq6.html";
	
	public final static String mapaReq6 = "data/templates/mapaReq6.html";

	public static void mapaReq1(double lat, double lon, int pPopulation, int pTotalPopulation){
		System.out.println("Se ha impreso el mapa");
		try {
			double densidad = (((double)pPopulation/pTotalPopulation)*100);
			File htmlTemplateFile = new File(direccionReq1);
			String htmlString;
			htmlString = FileUtils.readFileToString(htmlTemplateFile);
			String scriptTag = "var myLatLng = {lat: "+lat+", lng: "+lon+"};" + 
					"var marker = new google.maps.Marker({" + 
					"    position: myLatLng," + 
					"    map: map," + 
					"    title: 'Vertice mas congestionado'" + 
					"  });"+
					"      var citymap = {"
					+ "chicago: {"
					+"center: {lat: 41.880994471, lng: -87.632746489},"
					+"population: " + densidad
					+"}};";

			htmlString = htmlString.replace("//$script", scriptTag);
			File newHtmlFile = new File(mapaReq1);
			FileUtils.writeStringToFile(newHtmlFile, htmlString);	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void mapaReq2(double lat, double lon, int pPopulation, int pTotalPopulation, int pIdentificador, String pColor){
		//System.out.println("Se ha impreso el mapa");
		double densidad = (((double)pPopulation/pTotalPopulation)*100);
		try {
			File htmlTemplateFile;
			if(!escribio)
			{
				htmlTemplateFile = new File(direccionReq2);
				escribio = true;
			}
			else
				htmlTemplateFile = new File(mapaReq2);		
			String htmlString;
			htmlString = FileUtils.readFileToString(htmlTemplateFile);
			//System.out.println(htmlString);
			String scriptTag = "var myLatLng"+pIdentificador+" = {lat: "+lat+", lng: "+lon+"};" + 
					"var marker"+pIdentificador+" = new google.maps.Marker({" + 
					"    position: myLatLng"+pIdentificador+"," + 
					"    map: map," + 
					"    title: 'Vertice"+pIdentificador+"'" + 
					"  });"+
					"\n\n //$scriptVertices";

			String scriptTag2 ="chicago"+pIdentificador+" : {"+
					"center: myLatLng"+pIdentificador+","+
					"population: " + densidad
					+"}, /*$scriptCirculos*/";
			String scriptTag3 = "'"+pColor+"'";

			htmlString = htmlString.replace("//$scriptVertices", scriptTag);
			htmlString = htmlString.replace("/*$scriptCirculos*/", scriptTag2);
			htmlString = htmlString.replace("/*color*/", scriptTag3);
			htmlString = htmlString.replace("/*color2*/", scriptTag3);
			File newHtmlFile = new File(mapaReq2);
			FileUtils.writeStringToFile(newHtmlFile, htmlString);	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void mapaReq2Lineas(String pFuenteLat, String pFuenteLong, String pDestinoLat, String pDestinoLong, String pColor)
	{
		File htmlTemplateFile;
		if(!escribio2)
		{
			htmlTemplateFile = new File(direccionReq2);
			escribio2 = true;
		}
		else
			htmlTemplateFile = new File(mapaReq2);		
		String htmlString;
		try {
			htmlString = FileUtils.readFileToString(htmlTemplateFile);
			String scriptTag4 = "var line = new google.maps.Polyline("
					+ "{path: [{lat: "+pFuenteLat+", lng: "+pFuenteLong+"},{lat: "+pDestinoLat+", lng: "+pDestinoLong+"}],"
					+ "strokeColor: '"+pColor+"',"
					+ "strokeWeight: 1.5,"
					+"icons: [{"
					+"offset: '200%'"
					+"}],"
					+"map: map"
					+"});"
					+ "\n//$scriptLineas";
			htmlString = htmlString.replace("//$scriptLineas", scriptTag4);
			File newHtmlFile = new File(mapaReq2);
			FileUtils.writeStringToFile(newHtmlFile, htmlString);	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void mapaReq3(double lat, double lon, int pPopulation, int pTotalPopulation, int pIdentificador, String pColor){
		//System.out.println("Se ha impreso el mapa");
		double densidad = (((double)pPopulation/pTotalPopulation)*100);
		try {
			File htmlTemplateFile;
			if(!escribio3)
			{
				htmlTemplateFile = new File(direccionReq3);
				escribio3 = true;
			}
			else
				htmlTemplateFile = new File(mapaReq3);		
			String htmlString;
			htmlString = FileUtils.readFileToString(htmlTemplateFile);
			//System.out.println(htmlString);
			String scriptTag =	"var myLatLng"+pIdentificador+" = {lat: "+lat+", lng: "+lon+"};" 
					+"\n\n //$scriptVertices";

			String scriptTag2 ="chicago"+pIdentificador+" : {"+
					"center: myLatLng"+pIdentificador+","
					+"color: '"+pColor+"',"
					+"population: " + densidad
					+"}, /*$scriptCirculos*/";

			htmlString = htmlString.replace("//$scriptVertices", scriptTag);
			htmlString = htmlString.replace("/*$scriptCirculos*/", scriptTag2);
			File newHtmlFile = new File(mapaReq3);
			FileUtils.writeStringToFile(newHtmlFile, htmlString);	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void mapaReq3Lineas(String pFuenteLat, String pFuenteLong, String pDestinoLat, String pDestinoLong, String pColor)
	{
		File htmlTemplateFile;
		if(!escribio4)
		{
			htmlTemplateFile = new File(direccionReq3);
			escribio4 = true;
		}
		else
			htmlTemplateFile = new File(mapaReq3);		
		String htmlString;
		try {
			htmlString = FileUtils.readFileToString(htmlTemplateFile);
			String scriptTag4 = "var line = new google.maps.Polyline("
					+ "{path: [{lat: "+pFuenteLat+", lng: "+pFuenteLong+"},{lat: "+pDestinoLat+", lng: "+pDestinoLong+"}],"
					+ "strokeColor: '"+pColor+"',"
					+ "strokeWeight: 1,"
					+"icons: [{"
					+"offset: '200%'"
					+"}],"
					+"map: map"
					+"});"
					+ "\n//$scriptLineas";
			htmlString = htmlString.replace("//$scriptLineas", scriptTag4);
			File newHtmlFile = new File(mapaReq3);
			FileUtils.writeStringToFile(newHtmlFile, htmlString);	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void mapaReq4(Lista<VerticeConServicios> lista)
	{
		System.out.println("Se ha impreso el mapa");
		try {
			File htmlTemplateFile = new File(direccionReq4);
			String htmlString;
			htmlString = FileUtils.readFileToString(htmlTemplateFile);
			//https://developers.google.com/maps/documentation/javascript/examples/circle-simple?hl=es-419
			String scriptTag = "var citymap = {";
			scriptTag +=  "vertice"+(lista.size()-1) + ": {center: {lat:" +  lista.get(lista.size()-1).getLatRef() + ", lng:" + lista.get(lista.size()-1).getLongRef() + "}, color: {fillColor: '#008000', strokeColor: '#008000'} },";
			for (int i = lista.size()-2; i>0;i--)
			{
				scriptTag +=  "vertice"+(i) + ": {center: {lat:" +  lista.get(i).getLatRef() + ", lng:" + lista.get(i).getLongRef() + "}, color: {fillColor:'#696969', strokeColor: '#696969'}},";
			}
			scriptTag+= "vertice"+(0) + ": {center: {lat:" +  lista.get(0).getLatRef() + ", lng:" + lista.get(0).getLongRef() + "}, color: {fillColor:'#FF0000', strokeColor: '#FF0000'}}";
			
			//Finalizo
			
			scriptTag += "};";
			htmlString = htmlString.replace("//$defineVertices", scriptTag);
			
			//TODO: Definir arcos
			
		scriptTag = "";
		for (int i=(lista.size()-2);i>=0;i--)
		{
			scriptTag  += "var line" + (i) + " = new google.maps.Polyline({ path: [{lat:" + lista.get(i).getLatRef() + ", lng:" + lista.get(i).getLongRef() + "}, {lat:" + lista.get(i+1).getLatRef() + ", lng:" +  lista.get(i+1).getLongRef()+ "}],icons: [{icon: lineSymbol,offset: '100%'}],map: map});";
		}
		//scriptTag += "var line" + (lista.size()) + " = new google.maps.Polyline({ path: [{lat:" + lista.get(lista.size()-2).getLatRef() + ", lng:" + lista.get(lista.size()-2).getLongRef() + "}, {lat:" + lista.get(lista.size()-1).getLatRef() + ", lng:" +  lista.get(lista.size()-1).getLongRef()+ "}],icons: [{icon: lineSymbol,offset: '100%'}],map: map});";
		htmlString = htmlString.replace("//$defineArcos", scriptTag);
		File newHtmlFile = new File(mapaReq4);
		FileUtils.writeStringToFile(newHtmlFile, htmlString);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public static void mapaReq5(Lista<VerticeConServicios> lista)
	{
		System.out.println("Se ha impreso el mapa");
		try {
			File htmlTemplateFile = new File(direccionReq5);
			String htmlString;
			htmlString = FileUtils.readFileToString(htmlTemplateFile);
			//https://developers.google.com/maps/documentation/javascript/examples/circle-simple?hl=es-419
			String scriptTag = "var citymap = {";
			scriptTag +=  "vertice"+(lista.size()-1) + ": {center: {lat:" +  lista.get(lista.size()-1).getLatRef() + ", lng:" + lista.get(lista.size()-1).getLongRef() + "}, color: {fillColor: '#008000', strokeColor: '#008000'} },";
			for (int i = lista.size()-2; i>0;i--)
			{
				scriptTag +=  "vertice"+(i) + ": {center: {lat:" +  lista.get(i).getLatRef() + ", lng:" + lista.get(i).getLongRef() + "}, color: {fillColor:'#696969', strokeColor: '#696969'}},";
			}
			scriptTag+= "vertice"+(0) + ": {center: {lat:" +  lista.get(0).getLatRef() + ", lng:" + lista.get(0).getLongRef() + "}, color: {fillColor:'#FF0000', strokeColor: '#FF0000'}}";
			
			//Finalizo
			
			scriptTag += "};";
			htmlString = htmlString.replace("//$defineVertices", scriptTag);
			
			//TODO: Definir arcos
			
		scriptTag = "";
		for (int i=(lista.size()-2);i>=0;i--)
		{
			scriptTag  += "var line" + (i) + " = new google.maps.Polyline({ path: [{lat:" + lista.get(i).getLatRef() + ", lng:" + lista.get(i).getLongRef() + "}, {lat:" + lista.get(i+1).getLatRef() + ", lng:" +  lista.get(i+1).getLongRef()+ "}],icons: [{icon: lineSymbol,offset: '100%'}],map: map});";
		}
		//scriptTag += "var line" + (lista.size()) + " = new google.maps.Polyline({ path: [{lat:" + lista.get(lista.size()-2).getLatRef() + ", lng:" + lista.get(lista.size()-2).getLongRef() + "}, {lat:" + lista.get(lista.size()-1).getLatRef() + ", lng:" +  lista.get(lista.size()-1).getLongRef()+ "}],icons: [{icon: lineSymbol,offset: '100%'}],map: map});";
		htmlString = htmlString.replace("//$defineArcos", scriptTag);
		File newHtmlFile = new File(mapaReq5);
		FileUtils.writeStringToFile(newHtmlFile, htmlString);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void mapaReq6(Lista<VerticeConServicios> lista)
	{
	
		System.out.println("Se ha impreso el mapa");
		try {
			File htmlTemplateFile = new File(direccionReq6);
			String htmlString;
			htmlString = FileUtils.readFileToString(htmlTemplateFile);
			//https://developers.google.com/maps/documentation/javascript/examples/circle-simple?hl=es-419
			String scriptTag = "var citymap = {";
			scriptTag +=  "vertice"+(lista.size()-1) + ": {center: {lat:" +  lista.get(lista.size()-1).getLatRef() + ", lng:" + lista.get(lista.size()-1).getLongRef() + "}, color: {fillColor: '#008000', strokeColor: '#008000'} },";
			for (int i = lista.size()-2; i>0;i--)
			{
				scriptTag +=  "vertice"+(i) + ": {center: {lat:" +  lista.get(i).getLatRef() + ", lng:" + lista.get(i).getLongRef() + "}, color: {fillColor:'#696969', strokeColor: '#696969'}},";
			}
			scriptTag+= "vertice"+(0) + ": {center: {lat:" +  lista.get(0).getLatRef() + ", lng:" + lista.get(0).getLongRef() + "}, color: {fillColor:'#FF0000', strokeColor: '#FF0000'}}";
			
			//Finalizo
			
			scriptTag += "};";
			htmlString = htmlString.replace("//$defineVertices", scriptTag);
			
			//TODO: Definir arcos
			
		scriptTag = "";
		for (int i=(lista.size()-2);i>=0;i--)
		{
			scriptTag  += "var line" + (i) + " = new google.maps.Polyline({ path: [{lat:" + lista.get(i).getLatRef() + ", lng:" + lista.get(i).getLongRef() + "}, {lat:" + lista.get(i+1).getLatRef() + ", lng:" +  lista.get(i+1).getLongRef()+ "}],icons: [{icon: lineSymbol,offset: '100%'}],map: map});";
		}
		//scriptTag += "var line" + (lista.size()) + " = new google.maps.Polyline({ path: [{lat:" + lista.get(lista.size()-2).getLatRef() + ", lng:" + lista.get(lista.size()-2).getLongRef() + "}, {lat:" + lista.get(lista.size()-1).getLatRef() + ", lng:" +  lista.get(lista.size()-1).getLongRef()+ "}],icons: [{icon: lineSymbol,offset: '100%'}],map: map});";
		htmlString = htmlString.replace("//$defineArcos", scriptTag);
		File newHtmlFile = new File(mapaReq6);
		FileUtils.writeStringToFile(newHtmlFile, htmlString);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
