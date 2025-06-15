package com.example.progettolamtris.privata;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {MemorizzaPartitaPrivata.class}, version = 1, exportSchema = false)
public abstract class PrivataRoomDatabase extends RoomDatabase {

    public abstract PrivataDao privataDao();
    private static volatile PrivataRoomDatabase INSTANCE;
    private static final int nTHREADS = 4;


    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(nTHREADS);
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
               PrivataDao dao = INSTANCE.privataDao();
                //jdao.deleteAll();
            });
        }
    };

    // We are using a Singleton design pattern
    static PrivataRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PrivataRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PrivataRoomDatabase.class, "privata_database")
                            .addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }
}

