package com.calabriaeventi.core;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.calabriaeventi.net.meteo.MeteoLoader;

public class Evento implements Serializable{
	private static final long serialVersionUID = 6891277652233297417L;
	private String nome;
	private String data;
	private String luogo;
	private String descrizione;
	private String imageUrl;
	private List<Date> date;
	private String meteo;
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Evento() {
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public String getLuogo() {
		return luogo;
	}
	
	public void setLuogo(String luogo) {
		this.luogo = luogo;
	}
	
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
	public void setMeteo(String meteo) {
		this.meteo = meteo;
	}
	
	public String getMeteo() {
		if(meteo == null){
			new MeteoLoader(this).execute();
		}
		return meteo;
	}
	
	public String getDescrizione() {
		return descrizione;
	}
	
	public List<Date> getDate() {
		return date;
	}
	
	public void setDate(List<Date> date) {
		this.date = date;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Evento other = (Evento) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(nome).append("\n");
		sb.append(data).append("\n");
		sb.append(luogo);
		return sb.toString();
	}
	
}
