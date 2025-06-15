package com.example.progettolamtris.privata;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "riprendi_privata_table")
public class MemorizzaPartitaPrivata {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "nome_privata")
    private String nome_partita;


    public void setNome_partita(@NonNull String nome_partita) {
        this.nome_partita = nome_partita;
    }




    public MemorizzaPartitaPrivata(@NonNull String nome_partita){
        this.nome_partita = nome_partita;

    }

    public String getNome_partita(){
        return this.nome_partita;
    }



}
