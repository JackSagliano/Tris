package com.example.progettolamtris.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.progettolamtris.privata.PrivataRoomDatabase;
import com.example.progettolamtris.pubblica.MemorizzaPartitaPubblica;
import com.example.progettolamtris.pubblica.PartitaPubblica;
import com.example.progettolamtris.R;
import com.example.progettolamtris.privata.SalaDiAttesa;
import com.example.progettolamtris.pubblica.PubblicaDao;
import com.example.progettolamtris.pubblica.PubblicaRoomDatabase;
import com.example.progettolamtris.pubblica.RiprendiPartitaPubblica;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private AppCompatButton gioca_partita;
    public DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://progettolamtris-default-rtdb.firebaseio.com/partite/pubbliche");
    public DatabaseReference databaseReferenceUtenti = FirebaseDatabase.getInstance().getReferenceFromUrl("https://progettolamtris-default-rtdb.firebaseio.com/utenti");
    private String nome_utente;
private String avversario_pubblica="";
private AppCompatButton partita_offline;
private ImageView profilo;
private AppCompatButton partita_pubblica;
private ImageView classifica_image;
private ValueEventListener abbandonaPartitaEventListener;
private MemorizzaPartitaPubblica vittoria_tavolino;
    //MemorizzaPartitaPubblica riprendi_pubblica;
    //String riprendi_pubblica;
    private MemorizzaPartitaPubblica riprendi_pubblica;
PubblicaDao pubblicaDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        //riprendi_pubblica = bundle.getString("riprendi_pubblica");
        nome_utente = bundle.getString("nome_utente");
        PubblicaRoomDatabase db = PubblicaRoomDatabase.getDatabase(getApplication()); //ho reso getDatabase public
        pubblicaDao = db.pubblicaDao();
        /*PubblicaRoomDatabase.databaseWriteExecutor.execute(() -> {
            Log.d("SONO QUI DENTRO", "SONO QUI DENTRO");
            riprendi_pubblica = pubblicaDao.estrai_partita();
        });*/
        super.onCreate(savedInstanceState);
        //Nascondo la barra contenente tutte le informazioni come la batteria, la connessione, ecc..
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        gioca_partita = findViewById(R.id.partita_privata);
        profilo = findViewById(R.id.profilo_image);
        classifica_image = findViewById(R.id.classifica_image);
        partita_pubblica = findViewById(R.id.partita_pubblica);


        PubblicaRoomDatabase.databaseWriteExecutor.execute(() -> {
            vittoria_tavolino = pubblicaDao.estrai_partita(nome_utente);
            if(vittoria_tavolino != null) {

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(vittoria_tavolino.getNome_partita())){
                                if(snapshot.child(vittoria_tavolino.getNome_partita()).hasChild("vittoria_tavolino")){
                                    if(snapshot.child(vittoria_tavolino.getNome_partita()).child("vittoria_tavolino").getValue().equals(nome_utente)){
                                        databaseReference.child(vittoria_tavolino.getNome_partita()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                databaseReferenceUtenti.child(nome_utente).addListenerForSingleValueEvent(new ValueEventListener() {

                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        int partite_vinte = Integer.parseInt(snapshot.child("PartiteVinte").getValue().toString());
                                                        partite_vinte = partite_vinte +1;
                                                        databaseReferenceUtenti.child(nome_utente).child("PartiteVinte").setValue(partite_vinte);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        });


                                    }
                                }
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        profilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfiloUtente.class);
                Bundle bundle = new Bundle();
                bundle.putString("nome_utente", nome_utente);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        classifica_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Classifica.class);
                startActivity(intent);
            }
        });
        gioca_partita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SalaDiAttesa.class);
                Bundle bundle = new Bundle();
                bundle.putString("nome_utente", nome_utente);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        partita_offline = findViewById(R.id.partita_offline);
        partita_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), DifficoltaComputer.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("nome_utente", nome_utente);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

        });
        abbandonaPartitaEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!avversario_pubblica.equals("")) {

                    databaseReference.child(riprendi_pubblica.getNome_partita()).child("vittoria_tavolino").setValue(avversario_pubblica);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        partita_pubblica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PubblicaRoomDatabase.databaseWriteExecutor.execute(() -> {
                    riprendi_pubblica = pubblicaDao.estrai_partita(nome_utente);
                if(riprendi_pubblica != null) {
                    Log.d("C'è UNA PARTITA DA GIOCARE", "C'è UNA PARTITA DA GIOCARE");
                    Log.d("NOME DELLA PARTITA DA GIOCARE", riprendi_pubblica.getNome_partita());
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(riprendi_pubblica.getNome_partita())) {
                                if (snapshot.child(riprendi_pubblica.getNome_partita()).hasChild("vittoria_tavolino")) {
                                        if (snapshot.child(riprendi_pubblica.getNome_partita()).child("vittoria_tavolino").getValue().equals(nome_utente)) {
                                            Intent intent = new Intent(getApplicationContext(), RiprendiPartitaPubblica.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("nome_utente", nome_utente);
                                            bundle.putString("nome_partita", riprendi_pubblica.getNome_partita());
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }

                                } else {
                                if (!snapshot.child(riprendi_pubblica.getNome_partita()).hasChild("Giocatori")) {

                                    PubblicaRoomDatabase.databaseWriteExecutor.execute(() -> {
                                        pubblicaDao.elimina_partita(riprendi_pubblica.getNome_partita());
                                    });
                                    databaseReference.child(riprendi_pubblica.getNome_partita()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Intent intent = new Intent(getApplicationContext(), PartitaPubblica.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("nome_utente", nome_utente);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    });
                                } else {
                                    if (snapshot.child(riprendi_pubblica.getNome_partita()).child("Giocatori").getChildrenCount() == 2) {
                                        final Dialog dialog = new Dialog(MainActivity.this);
                                        int width = (int) (getResources().getDisplayMetrics().widthPixels * 1);

                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setCancelable(true);
                                        dialog.setContentView(R.layout.dialog_riprendi_pubblica);
                                        Button riprendi = dialog.findViewById(R.id.riprendi_pubblica_button);
                                        Button abbandona = dialog.findViewById(R.id.abbandona_pubblica_button);
                                        TextView textView1 = dialog.findViewById(R.id.textview1);
                                        TextView textView2 = dialog.findViewById(R.id.textview2);
                                        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        dialog.show();
                                        riprendi.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (riprendi.getText().toString().equals("Riprendi partita")) {
                                                    dialog.dismiss();
                                                    Intent intent = new Intent(getApplicationContext(), RiprendiPartitaPubblica.class);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("nome_utente", nome_utente);
                                                    bundle.putString("nome_partita", riprendi_pubblica.getNome_partita());
                                                    intent.putExtras(bundle);
                                                    startActivity(intent);
                                                } else {
                                                    dialog.dismiss();
                                                    databaseReference.child(riprendi_pubblica.getNome_partita()).child("Giocatori").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot giocatori : dataSnapshot.getChildren()) {
                                                                if (!giocatori.getKey().equals(nome_utente)) {
                                                                    avversario_pubblica = giocatori.getKey();
                                                                    PubblicaRoomDatabase.databaseWriteExecutor.execute(() -> {
                                                                        pubblicaDao.elimina_partita(riprendi_pubblica.getNome_partita());
                                                                    });
                                                                    databaseReferenceUtenti.child(nome_utente).addListenerForSingleValueEvent(abbandonaPartitaEventListener);
                                                                }
                                                            }
                                                        }
                                                    });

                                                }
                                            }
                                        });

                                        abbandona.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (abbandona.getText().toString().equals("Abbandona partita")) {
                                                    riprendi.setText("Abbandona");
                                                    abbandona.setText("Non abbandonare");
                                                    textView1.setText("Vuoi davvero abbandonare?");
                                                    textView2.setText("Il tuo avversario vincerà la partita");
                                                } else {
                                                    dialog.dismiss();
                                                }
                                            }
                                        });

                                    } else {
                                        PubblicaRoomDatabase.databaseWriteExecutor.execute(() -> {
                                            pubblicaDao.elimina_partita(riprendi_pubblica.getNome_partita());
                                        });
                                        databaseReference.child(riprendi_pubblica.getNome_partita()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Intent intent = new Intent(getApplicationContext(), PartitaPubblica.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("nome_utente", nome_utente);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                            }
                                        });

                                    }
                                }
                            }
                            } else {

                                PubblicaRoomDatabase.databaseWriteExecutor.execute(() -> {
                                    pubblicaDao.elimina_partita(riprendi_pubblica.getNome_partita());
                                });
                                Intent intent = new Intent(getApplicationContext(), PartitaPubblica.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("nome_utente", nome_utente);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    Intent intent = new Intent(getApplicationContext(), PartitaPubblica.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("nome_utente", nome_utente);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                });


            }
        });

    }
}