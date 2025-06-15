package com.example.progettolamtris.privata;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface PrivataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MemorizzaPartitaPrivata partitaPrivata);

    @Query("DELETE FROM riprendi_privata_table")
    void deleteAll();


    @Query("SELECT EXISTS(SELECT * FROM riprendi_privata_table WHERE nome_privata = :nome_partita)")
    boolean esiste_partita(String nome_partita);

    @Query("DELETE FROM riprendi_privata_table WHERE nome_privata = :nome_partita")
    void elimina_partita(String nome_partita);
}
