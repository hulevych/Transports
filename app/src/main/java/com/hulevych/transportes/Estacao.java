package com.hulevych.transportes;

public class Estacao {
	private int numero;
	private String nome;
	private int incrementoIda;
	private int incrementoVolta;

	public Estacao(int numero, String nome, int incrementoIda, int incrementoVolta){
		this.numero=numero;
		this.nome=nome;
		this.incrementoIda=incrementoIda;
		this.incrementoVolta=incrementoVolta;
	}
	public int getNumero(){
		return numero;
	}
	public String getName(){
		return nome;
	}
	public int getIncrementoIda(){
		return incrementoIda;
	}
	public int getIncrementoVolta(){
		return incrementoVolta;
	}

}
