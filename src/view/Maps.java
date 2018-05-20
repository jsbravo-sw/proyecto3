package view;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import model.data_structures.Lista;
import model.vo.VerticeConServicios;

public class Maps {

	public final static String direccionReq1 = "./data/templates/templateReq1.html";

	public final static String mapaReq1 = "./data/templates/mapaReq1.html";

	public final static String direccionReq2 = "./data/templates/templateReq2.html";

	public final static String direccionReq3 = "./data/templates/templateReq3.html";

	public final static String direccionReq4 = "./data/templates/templateReq4.html";
	
	public final static String mapaReq4 = "./data/templates/mapaReq4.html";

	public final static String direccionReq5 = "./data/templates/templateReq5.html";

	public final static String direccionReq6 = "./data/templates/templateReq6.html";

	public static void mapaReq1(double lat, double lon){
		System.out.println("Se ha impreso el mapa");
		try {
			File htmlTemplateFile = new File(direccionReq1);
			String htmlString;
			htmlString = FileUtils.readFileToString(htmlTemplateFile);
			String scriptTag = "var myLatLng = {lat: "+lat+", lng: "+lon+"};" + 
					"var marker = new google.maps.Marker({" + 
					"    position: myLatLng," + 
					"    map: map," + 
					"    title: 'Vertice mas congestionado'" + 
					"  });";
			htmlString = htmlString.replace("//$script", scriptTag);
			File newHtmlFile = new File(mapaReq1);
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
			for (int i = 0; i<lista.size()-1;i++)
			{
				scriptTag +=  lista.get(i).toString() + ": { center: { lat:" +  lista.get(i).getLatRef() + ", lng:" + lista.get(i).getLongRef() + "},";
			}
			scriptTag+= lista.get(lista.size()-1).toString() + ": { center: { lat:" +  lista.get(lista.size()-1).getLatRef() + ", lng:" + lista.get(lista.size()-1).getLongRef() + "}";
			
			//Finalizo
			
			scriptTag += "};";
			htmlString = htmlString.replace("//$defineVertices", scriptTag);
			
			//TODO: Definir arcos
			
		scriptTag = "";
		for (int i=0;i<lista.size()/2;i++)
		{
			scriptTag  += " var line = new google.maps.Polyline({ path: [{ + lat:" + lista.get(i).getLatRef() + ", lng:" + lista.get(i).getLongRef() + "}, {lat:" + lista.get(i+1).getLatRef() + ", lng:" +  lista.get(i+1).getLongRef()+ "}],icons: [{icon: lineSymbol,offset: '100%'}],map: map});";
		}
		htmlString = htmlString.replace("//$defineArcos", scriptTag);
		File newHtmlFile = new File(mapaReq4);
		FileUtils.writeStringToFile(newHtmlFile, htmlString);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
