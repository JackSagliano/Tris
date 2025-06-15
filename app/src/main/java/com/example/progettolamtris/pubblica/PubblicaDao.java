package com.example.progettolamtris.pubblica;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.progettolamtris.privata.MemorizzaPartitaPrivata;
@Dao
public interface PubblicaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MemorizzaPartitaPubblica partitaPubblica);

    @Query("DELETE FROM riprendi_pubblica_table")
    void deleteAll();


    @Query("SELECT EXISTS(SELECT * FROM riprendi_pubblica_table WHERE nome_pubblica = :nome_partita and nome_giocatore = :nome_giocatore)")
    boolean esiste_partita(String nome_partita, String nome_giocatore);

    @Query("DELETE FROM riprendi_pubblica_table WHERE nome_pubblica = :nome_partita")
    void elimina_partita(String nome_partita);

    @Query(("SELECT * FROM riprendi_pubblica_table WHERE nome_giocatore = :nome_giocatore"))
    MemorizzaPartitaPubblica estrai_partita(String nome_giocatore);
}
