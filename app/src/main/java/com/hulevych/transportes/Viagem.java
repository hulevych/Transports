package com.hulevych.transportes;

import java.util.List;

public class Viagem {
	private int origem;
	private int destino;
	private List<Integer> dias;
	private String hora;
	
	public Viagem(int origem, int destino, List<Integer> dias, String hora){
		this.origem=origem;
		this.destino=destino;
		this.dias=dias;
		this.hora=hora;
	}
	public int getOrigem(){
		return origem;
	}
	public int getDestino(){
		return destino;
	}
	public List<Integer> getDias(){
		return dias;
	}
	public String getHora(){
		return hora;
	}
	
}
