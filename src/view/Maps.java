package view;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class Maps {
	
	public final static String direccionReq1 = "./data/templates/templateReq1.html";
	
	public final static String mapaReq1 = "./data/templates/mapaReq1.html";
	
	public final static String direccionReq2 = "./data/templates/templateReq2.html";
	
	public final static String direccionReq3 = "./data/templates/templateReq3.html";
	
	public final static String direccionReq4 = "./data/templates/templateReq4.html";
	
	public final static String direccionReq5 = "./data/templates/templateReq5.html";
	
	public final static String direccionReq6 = "./data/templates/templateReq6.html";
	
	public static void dibujoRequerimiento1(double lat, double lon){
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

}
