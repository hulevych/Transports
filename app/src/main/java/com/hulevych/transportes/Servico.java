package com.hulevych.transportes;

import android.Manifest;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Servico {
	private List<Transporte> transportes = new ArrayList<Transporte>();

	public Servico(AssetManager am ){
		loadTransportes(am);
	}

	private void loadTransportes(AssetManager am){
		try{
			String name = "Transporte";
			int contagem=1;

			File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getParentFile().getAbsolutePath() + "/Transportes/");
			if(!f.exists())
				f.mkdirs();

			BufferedReader reader=null;

			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getParentFile().getAbsolutePath() + "/Transportes/" + name + contagem + ".txt")));
			}
			catch(FileNotFoundException e){
				reader = new BufferedReader(new InputStreamReader(am.open(name+contagem+".txt")));
			}


			while (reader!=null){
				String line = reader.readLine();
				Transporte t = new Transporte(line);
				line = reader.readLine();
				while (!line.equals("+++")) {
					String[] splited = line.split(";");
					int numero = Integer.parseInt(splited[0]);
					String nome = " "+splited[1];

					String[] splited2;
					int incremento1;
					int incremento2;
					if(splited[2].contains(":")) {
						splited2 = splited[2].split(":");
						incremento1 = Integer.parseInt(splited2[0]);
						incremento2 = Integer.parseInt(splited2[1]);
					}
					else{
						incremento1 = Integer.parseInt(splited[2]);
						incremento2= Integer.parseInt(splited[2]);;
					}

					t.addEstacao(new Estacao(numero,nome,incremento1,incremento2));
					line = reader.readLine();
				}

				line = reader.readLine();
				while (line != null ) {
					String[] splited = line.split(";");
					if (splited.length<4)
						break;

					int origem = Integer.parseInt(splited[0]);
					int destino = Integer.parseInt(splited[1]);

					//if (!t.getDestinos().contains(origem))
						//t.addDestino(origem);
					if (!t.getDestinos().contains(destino))
						t.addDestino(destino);

					String[] diasSplited = splited[2].split(",");
					List<Integer> dias = new ArrayList<Integer>();
					for (int i=0; i< diasSplited.length;i++){
						dias.add(Integer.parseInt(diasSplited[i]));
					}

					String hora = splited[3];
					t.addViagem(new Viagem (origem,destino,dias,hora));
					line = reader.readLine();
				}
				transportes.add(t);

				contagem++;

				try {
					reader = new BufferedReader(new InputStreamReader(new FileInputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getParentFile().getAbsolutePath() + "/Transportes/" + name + contagem + ".txt")));
				}
				catch(FileNotFoundException e){
					reader = new BufferedReader(new InputStreamReader(am.open(name+contagem+".txt")));
				}
			}
			reader.close();
		}
		catch (FileNotFoundException e) {
			return;
		}

		catch (Exception e) {
			System.err.format("Exception occurred trying to read a file\n");
			e.printStackTrace();
		}
	}

	public List<String> getHoras(String transporte,int origem, int destino, int diaSemana, String horaAtual){
		List<String> horas = new ArrayList<String>();
		for (Transporte t: transportes){
			if (t.getNome().equals(transporte)){
				for(Viagem v: t.getViagens()){
					if (origem<destino){
						if (v.getOrigem()<=origem && v.getDestino()>=destino && v.getDias().contains(diaSemana)){
							int i=v.getOrigem();
							int incr = 0;

							//caso estacoes nao terminais
							if(i!=0)
								i++;

							while (i<=origem){
								incr+=t.getEstacoes().get(i).getIncrementoIda();
								i++;
							}
							String[] horaViagem = v.getHora().split(":");
							int min = Integer.parseInt(horaViagem[1])+incr;
							int hora = Integer.parseInt(horaViagem[0]);
							hora = hora + min/60;
							min = min%60;

							if(hora==24 && horaAtual.equals("0:0"))
								hora = -1;
							String[] hAtual = horaAtual.split(":");
							if (hora>Integer.parseInt(hAtual[0]) || (hora==Integer.parseInt(hAtual[0]) && min>Integer.parseInt(hAtual[1])) || hora==24) {
								if(hora==24)
									hora=0;

								String horaFinal= ""+ hora;
								String minFinal= ""+ min;
								if (hora<10)
									horaFinal= "0"+hora;
								if(min<10)
									minFinal= "0"+min;
								if(hora!=-1)
									horas.add(horaFinal + ":" + minFinal);

							}
						}
					}
					else{
						if (v.getOrigem()>=origem && v.getDestino()<=destino && v.getDias().contains(diaSemana)){
							int i=v.getOrigem();
							int incr = 0;

							//caso estacoes nao terminais
							//if(i!=t.getEstacoes().size()-1)
							//	i-=1;
							while (i>origem){
								incr+=t.getEstacoes().get(i).getIncrementoVolta();
								i-=1;
							}
							String[] horaViagem = v.getHora().split(":");
							int min = Integer.parseInt(horaViagem[1])+incr;
							int hora = Integer.parseInt(horaViagem[0]);
							hora = hora + min/60;
							min = min%60;

							if(hora==24 && horaAtual.equals("0:0"))
								hora = -1;

							String[] hAtual = horaAtual.split(":");
							if (hora>Integer.parseInt(hAtual[0]) || (hora==Integer.parseInt(hAtual[0]) && min>Integer.parseInt(hAtual[1])) || hora==24) {
								if(hora==24)
									hora=0;

								String horaFinal= ""+ hora;
								String minFinal= ""+ min;
								if (hora<10)
									horaFinal= "0"+hora;
								if(min<10)
									minFinal= "0"+min;
								if(hora!=-1)
									horas.add(horaFinal + ":" + minFinal);

							}
						}
					}
				}
				Collections.sort(horas);
				return horas;
			}

		}

		return horas;
	}

	public List<Estacao> getParagens (String transporte){
		for (Transporte t: transportes){
			if (t.getNome().equals(transporte)){
				return t.getEstacoes();
			}
		}
		return null;
	}

	public List<String> getTransportes(){
		List<String> trans = new ArrayList<String>();
		for (Transporte t: transportes){
			trans.add(t.getNome());
		}
		return trans;
	}

	public List<Integer> getDestinos(String nome) {
		for(Transporte t :transportes) {
			if (t.getNome().equals(nome)) {
				List<Integer> temp = t.getDestinos();
				Collections.sort(temp);
				return temp;
			}
		}
		return null;
	}

}
