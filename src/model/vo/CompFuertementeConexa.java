package model.vo;

import java.awt.Color;
import java.util.Random;

import model.data_structures.Graph.Vertex;
import model.data_structures.Lista;

public class CompFuertementeConexa implements Comparable<CompFuertementeConexa>
{
	private Lista listaVertices;
	private Color colorComponente;
	private int tamañoLista;

	public CompFuertementeConexa(Lista pLista) 
	{
		listaVertices = pLista;
		colorComponente = randomColor();
		tamañoLista = pLista.size();
	}
	
	public Color randomColor()
	{
		Random rand = new Random();
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();
		
		return new Color(r,g,b);
	}

	public Lista getListaVertices()
	{
		return listaVertices;
	}
	
	public Color getColorComponente()
	{
		return colorComponente;
	}
	
	public int getTamañoComp()
	{
		return tamañoLista;
	}

	@Override
	public int compareTo(CompFuertementeConexa o) {
		
		if(this.equals(o))
			return 0;
		else
			return -1;
	}

}
