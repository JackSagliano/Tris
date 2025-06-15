package com.example.progettolamtris.main;

import android.content.Context;
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
import androidx.cardview.widget.CardView;

import com.example.progettolamtris.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfiloUtente extends AppCompatActivity {
    public DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://progettolamtris-default-rtdb.firebaseio.com/utenti");
private String nome_utente;
private ImageView immagine_profilo;
    private TextView data_creazione;
private TextView n_data_creazione;
private TextView partite_giocate;
private TextView n_partite_giocate;
    private TextView partite_vinte;
    private TextView n_partite_vinte;
    private TextView ultimo_accesso;
    private TextView n_ultimo_accesso;
    private TextView nome_utente_profilo;
    private ImageView connessione_assente;
    private TextView connessione_assente_tw;
    private CardView dettagli_profilo;
    private CardView dettagli_profilo_2;
    private CardView dettagli_profilo_3;
    private CardView dettagli_profilo_4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//Nascondo la barra contenente tutte le informazioni come la batteria, la connessione, ecc..
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.profilo);
        Bundle bundle = getIntent().getExtras();
        nome_utente = bundle.getString("nome_utente");
        n_data_creazione = findViewById(R.id.n_data_creazione);
        data_creazione = findViewById(R.id.data_creazione);
        partite_giocate = findViewById(R.id.partite_giocate);
        nome_utente_profilo = findViewById(R.id.nome_utente_profilo);
        n_partite_giocate = findViewById(R.id.n_partite_giocate);
        partite_vinte = findViewById(R.id.partite_vinte);
        n_partite_vinte = findViewById(R.id.n_partite_vinte);
        ultimo_accesso = findViewById(R.id.ultimo_accesso);
        dettagli_profilo = findViewById(R.id.dettagli_profilo);
        dettagli_profilo_2 = findViewById(R.id.dettagli_profilo2);
        dettagli_profilo_3 = findViewById(R.id.dettagli_profilo3);
        dettagli_profilo_4 = findViewById(R.id.dettagli_profilo_4);
        connessione_assente = findViewById(R.id.connessione_assente);
        connessione_assente_tw = findViewById(R.id.connessione_assente_tw);
        immagine_profilo = findViewById(R.id.immagine_profilo);
        n_ultimo_accesso = findViewById(R.id.n_ultimo_accesso);
        data_creazione.setText("Data creazione");
        partite_vinte.setText("Partite vinte");

        partite_giocate.setText("Partite giocate");
        ultimo_accesso.setText("Ultimo accesso");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

                boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
                if(connected) {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String nome_profilo = String.valueOf(snapshot.child(nome_utente).getKey());
                                    nome_utente_profilo.setText(nome_profilo);
                                    String dataCreazione = String.valueOf(snapshot.child(nome_utente).child("DataCreazione").getValue());
                                    String ultimoAccesso = String.valueOf(snapshot.child(nome_utente).child("UltimoAccesso").getValue());
                                    long dataCreazione2 = Long.parseLong(dataCreazione);
                                    String dataCreazione3 = getTimeDate(dataCreazione2);
                                    n_data_creazione.setText(dataCreazione3);
                                    long ultimoAccesso2 = Long.parseLong(ultimoAccesso);
                                    String ultimoAccesso3 = getTimeDate(ultimoAccesso2);
                                    n_ultimo_accesso.setText(ultimoAccesso3);
                                    String partiteGiocate = String.valueOf(snapshot.child(nome_utente).child("PartiteGiocate").getValue());
                                    n_partite_giocate.setText(partiteGiocate);
                                    String partiteVinte = String.valueOf(snapshot.child(nome_utente).child("PartiteVinte").getValue());
                                    n_partite_vinte.setText(partiteVinte);
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
                            connessione_assente.setVisibility(View.VISIBLE);
                            connessione_assente_tw.setVisibility(View.VISIBLE);
                            immagine_profilo.setVisibility(View.INVISIBLE);
                            dettagli_profilo.setVisibility(View.INVISIBLE);
                             dettagli_profilo_2.setVisibility(View.INVISIBLE);
                             dettagli_profilo_3.setVisibility(View.INVISIBLE);
                             dettagli_profilo_4.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
        thread.start();

    }
    public static String getTimeDate(long timestamp){
        Date date = (new Date(timestamp));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        return sdf.format(date);
    }
}
