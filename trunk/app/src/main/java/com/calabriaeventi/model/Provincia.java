package com.calabriaeventi.model;

import java.io.Serializable;

public class Provincia implements Serializable {
    private String nome;
    private String cacheKey;
    private int feed;

    public Provincia() {
    }

    public Provincia(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public int getFeed() {
        return feed;
    }

    public void setFeed(int feed) {
        this.feed = feed;
    }

    public String getFirebaseName() {
        if (nome.equals("Home")) {
            return "regione";
        }
        return nome.toLowerCase().replaceAll(" ", "-");
    }
}
