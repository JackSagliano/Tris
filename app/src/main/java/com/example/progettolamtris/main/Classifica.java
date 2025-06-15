package com.example.progettolamtris.main;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progettolamtris.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Classifica extends AppCompatActivity {
    public DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://progettolamtris-default-rtdb.firebaseio.com/utenti");
    private ArrayList<GiocatoreItemClassifica> myDataSet = new ArrayList<>();
    TextView textViewClassifica;
    ImageView simbolo_wifi;
    TextView connessione_scarsa;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.classifica);
        textViewClassifica = findViewById(R.id.textViewClassifica);
        simbolo_wifi = findViewById(R.id.simbolo_wifi);
        connessione_scarsa = findViewById(R.id.connessione_scarsa_tw);
        RecyclerView recyclerView = findViewById(R.id.classifica_recycler);
        ClassificaAdapter classificaAdapter = new ClassificaAdapter(myDataSet);
        recyclerView.setAdapter(classificaAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(35));
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

                boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

                if (connected) {

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    classificaAdapter.clear();
                                    for(DataSnapshot utenti : snapshot.getChildren()){
                                        int punteggio = Integer.parseInt(utenti.child("PartiteVinte").getValue().toString());
                                        GiocatoreItemClassifica giocatoreItemClassifica = new GiocatoreItemClassifica(utenti.getKey(), punteggio);
                                        aggiungi(giocatoreItemClassifica);
                                        classificaAdapter.notifyItemInserted(myDataSet.size() + 1);
                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            simbolo_wifi.setVisibility(View.VISIBLE);
                            connessione_scarsa.setVisibility(View.VISIBLE);
                        }
                    });


                }
            }
        });
        thread.start();


    }

    public void aggiungi(GiocatoreItemClassifica gic){
        if(myDataSet.size() == 0){
            myDataSet.add(gic);
        } else {
            for (int i = 0; i <= myDataSet.size(); i++) {
                if(i == myDataSet.size()){
                    myDataSet.add(gic);
                    break;
                } else if(gic.getPunteggio() > myDataSet.get(i).getPunteggio()){
                    myDataSet.add(i, gic);
                    break;
                    }
            }
        }
    }
}
