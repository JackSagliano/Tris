package com.example.progettolamtris.pubblica;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {MemorizzaPartitaPubblica.class}, version = 1, exportSchema = false)
public abstract class PubblicaRoomDatabase extends RoomDatabase {
    public abstract PubblicaDao pubblicaDao();
    private static volatile PubblicaRoomDatabase INSTANCE;
    private static final int nTHREADS = 4;


    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(nTHREADS);
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                PubblicaDao dao = INSTANCE.pubblicaDao();
                //jdao.deleteAll();
            });
        }
    };

    // We are using a Singleton design pattern
    public static PubblicaRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PubblicaRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PubblicaRoomDatabase.class, "pubblica_database")
                            .addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

}
