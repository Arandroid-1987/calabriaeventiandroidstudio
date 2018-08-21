package com.calabriaeventi.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.calabriaeventi.io.AsyncCallback;
import com.calabriaeventi.net.meteo.MeteoLoader;

public class Evento implements Serializable {
    private static final long serialVersionUID = 6891277652233297417L;
    private String nome;
    private String dataInizio;
    private String dataFine;
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

    public String getDataFine() {
        return dataFine;
    }

    public String getDataInizio() {
        return dataInizio;
    }

    public void setDataFine(String dataFine) {
        this.dataFine = dataFine;
    }

    public void setDataInizio(String dataInizio) {
        this.dataInizio = dataInizio;
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

    public String getMeteo(AsyncCallback<String> asyncCallback) {
        if (meteo == null && asyncCallback != null) {
            new MeteoLoader(this, asyncCallback).execute();
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
        result = prime * result + ((dataInizio == null) ? 0 : dataInizio.hashCode());
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
        if (dataInizio == null) {
            if (other.dataInizio != null)
                return false;
        } else if (!dataInizio.equals(other.dataInizio))
            return false;
        if (nome == null) {
            return other.nome == null;
        } else return nome.equals(other.nome);
    }

    @Override
    public String toString() {
        return nome + "\n" +
                dataInizio + "\n" +
                luogo;
    }

}
