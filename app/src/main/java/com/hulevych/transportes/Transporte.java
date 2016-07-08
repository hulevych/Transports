package com.hulevych.transportes;

import java.util.ArrayList;
import java.util.List;

public class Transporte {
	private String nome;
	private List<Estacao> estacoes;
	private List <Viagem> viagens;
	private List<Integer> destinos;
	
	public Transporte(String nome){
		this.nome=nome;
		estacoes = new ArrayList<Estacao>();
		viagens = new ArrayList<Viagem>();
		destinos =  new ArrayList<Integer>();
	}
	
	public String getNome(){
		return nome;
	}
	public List<Estacao> getEstacoes(){
		return estacoes;
	}
	public List<Viagem> getViagens(){return viagens;}
	public List<Integer> getDestinos(){return destinos;}
	public void addEstacao(Estacao estacao){
		estacoes.add(estacao);
	}
	public void addViagem(Viagem viagem){viagens.add(viagem);}
	public void addDestino(Integer d){destinos.add(d);}
}
