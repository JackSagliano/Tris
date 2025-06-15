package com.example.progettolamtris.main;

public class GiocatoreItemClassifica {
    private String nome_giocatore;
    private int punteggio;


    public GiocatoreItemClassifica(String nome_giocatore, int punteggio){
        this.nome_giocatore = nome_giocatore;
        this.punteggio = punteggio;
    }


    public String getNome_giocatore() {
        return nome_giocatore;
    }

    public void setNome_giocatore(String nome_giocatore) {
        this.nome_giocatore = nome_giocatore;
    }

    public int getPunteggio() {
        return punteggio;
    }

    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }

}
