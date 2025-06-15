package com.example.progettolamtris.pubblica;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "riprendi_pubblica_table")
public class MemorizzaPartitaPubblica {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "nome_pubblica")
    private String nome_partita;

    @ColumnInfo(name = "nome_giocatore")
    private String nome_giocatore;

    public String getNome_giocatore() {
        return nome_giocatore;
    }

    public void setNome_giocatore(String nome_giocatore) {
        this.nome_giocatore = nome_giocatore;
    }


    public void setNome_partita(@NonNull String nome_partita) {
        this.nome_partita = nome_partita;
    }




    public MemorizzaPartitaPubblica(@NonNull String nome_partita, String nome_giocatore){
        this.nome_partita = nome_partita;
        this.nome_giocatore = nome_giocatore;

    }

    public String getNome_partita(){
        return this.nome_partita;
    }

}
