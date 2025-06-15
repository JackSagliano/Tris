package com.example.progettolamtris.privata;

import android.app.Application;

public class PartitaItem {
    private String nome_host;
    private boolean expanded;
    private String nome_utente;


    public PartitaItem(String nome_host, String nome_utente){
        this.nome_host= nome_host;
        this.expanded = false;
        this.nome_utente = nome_utente;

    }

    public String getNome_host() {
        return nome_host;
    }


    public void setNome_host(String nome_host) {
        this.nome_host = nome_host;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getNome_utente() {
        return nome_utente;
    }

    public void setNome_utente(String nome_utente) {
        this.nome_utente = nome_utente;
    }
}
