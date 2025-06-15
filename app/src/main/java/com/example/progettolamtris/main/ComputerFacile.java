package com.example.progettolamtris.main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.progettolamtris.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ComputerFacile extends AppCompatActivity {
    public DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://progettolamtris-default-rtdb.firebaseio.com/partite/offline");
    private ServerOffline serverOffline;
    private String nome_utente;
    private boolean mio_turno = false;
    private LinearLayout giocatore1_layout, giocatore2_layout;
    private List<ImageView> caselle = new ArrayList<>();
    private ImageView casella0, casella1, casella2, casella3,casella4,casella5,casella6,casella7,casella8;
    private TextView giocatore_uno, giocatore_due;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        nome_utente = bundle.getString("nome_utente");
        //Nascondo la barra contenente tutte le informazioni come la batteria, la connessione, ecc..
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.partita);
        giocatore1_layout = findViewById(R.id.giocatore1_layout);
        giocatore2_layout = findViewById(R.id.giocatore2_layout);
        casella0 = findViewById(R.id.casella_0);
        casella1 = findViewById(R.id.casella_1);
        casella2 = findViewById(R.id.casella_2);
        casella3 = findViewById(R.id.casella_3);
        casella4 = findViewById(R.id.casella_4);
        casella5 = findViewById(R.id.casella_5);
        casella6 = findViewById(R.id.casella_6);
        casella7 = findViewById(R.id.casella_7);
        casella8 = findViewById(R.id.casella_8);
        giocatore_uno = findViewById(R.id.giocatore_uno);
        giocatore_due = findViewById(R.id.giocatore_due);
        caselle.add(casella0);
        caselle.add(casella1);
        caselle.add(casella2);
        caselle.add(casella3);
        caselle.add(casella4);
        caselle.add(casella5);
        caselle.add(casella6);
        caselle.add(casella7);
        caselle.add(casella8);

        serverOffline= new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ServerOffline.class);
        giocatore_uno.setText(nome_utente);
        giocatore_due.setText("Computer");
        String giocatore1 = nome_utente;
        String giocatore2 = "Computer";
        serverOffline.setGiocatore1(giocatore1);
        serverOffline.setGiocatore2(giocatore2);
        serverOffline.inizioPartita(giocatore1, giocatore2);

        serverOffline.getTurno_giocatore().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d("stringaS", s);
                if(s.equals(giocatore1)){
                    mio_turno = true;
                    giocatore1_layout.setBackgroundResource(R.drawable.turno_giocatore_illuminato);
                    giocatore2_layout.setBackgroundResource(R.drawable.casella_turno_giocatore);
                } else {
                    mio_turno = false;

                    giocatore2_layout.setBackgroundResource(R.drawable.turno_giocatore_illuminato);
                    giocatore1_layout.setBackgroundResource(R.drawable.casella_turno_giocatore);
                    ArrayList<Integer> arr = new ArrayList<>();

                    for (int i = 0; i < caselle.size(); i++) {

                        if (!serverOffline.casella_libera(i)) {

                            arr.add(i);
                        }
                    }
                    if (arr.size() < 9) {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        int casella_random = getRandomWithExclusion(0, 8, arr);
                                        caselle.get(casella_random).setImageResource(R.drawable.simbolo_o_removebg_preview);
                                        serverOffline.gioca_casella(casella_random, giocatore2);
                                        // serverOffline.setTurno_giocatore(giocatore1);
                                        timer.cancel();

                                }
                            });

                        }
                    }, 1000);
                }

                }
            }});


        for (int i = 0; i < caselle.size(); i++) {
            int finalI = i;
            caselle.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("quiqui2", "quiqui");
                    if (mio_turno && serverOffline.casella_libera(finalI)) {
                        Log.d("casella_giocata", "sono il giocatore" + nome_utente + " e ho scelto la casella " + finalI);
                        caselle.get(finalI).setImageResource(R.drawable.simbolo_x_removebg_preview);
                        serverOffline.gioca_casella(finalI, giocatore1);
                        //serverOffline.setTurno_giocatore(giocatore2);

                    }

                }


            });


        }
        serverOffline.getPareggio().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean.equals(true)) {
                    serverOffline.getPareggio().removeObserver(this);
                    Dialog dialog_pareggio = new Dialog(ComputerFacile.this);
                    int width = (int) (getResources().getDisplayMetrics().widthPixels * 1);
                    dialog_pareggio.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog_pareggio.setCancelable(true);
                    dialog_pareggio.setContentView(R.layout.dialog_pareggio);
                    Button bottone_pareggio = dialog_pareggio.findViewById(R.id.bottone_pareggio);
                    dialog_pareggio.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog_pareggio.show();
                    bottone_pareggio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (dialog_pareggio.isShowing()) {
                                dialog_pareggio.dismiss();
                            }
                            final Handler threadHandler = new Handler();
                            new Thread() {
                                @Override
                                public void run() {
                                    threadHandler.postDelayed(new Runnable() {
                                        public void run() {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("nome_utente", nome_utente);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                            threadHandler.removeCallbacks(this);
                                            finish();
                                        }
                                    }, 3000);
                                }
                            }.start();

                        }
                    });
                }
        }
                });

        serverOffline.getVittoriag1().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean.equals(true)){
                    serverOffline.getVittoriag1().removeObserver(this);
                    dialog_vittoria(ComputerFacile.this, true);
                }
            }
        });

        serverOffline.getVittoriag2().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean.equals(true)){
                    serverOffline.getVittoriag2().removeObserver(this);
                    dialog_vittoria(ComputerFacile.this, false);
                }
            }
        });

    }






    public void dialog_vittoria(Context context, boolean win){
        final Dialog dialog_vittoria = new Dialog(context);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 1);
        dialog_vittoria.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_vittoria.setCancelable(false);
        dialog_vittoria.setContentView(R.layout.dialog_vittoria);
        Button bottone_vittoria = dialog_vittoria.findViewById(R.id.bottone_vittoria);
        TextView esito_partita = dialog_vittoria.findViewById(R.id.esito_partita_tw);
        if(win){
            esito_partita.setText("Congratulazioni! Hai vinto la partita");
        } else {
            esito_partita.setText("Sconfitta! Hai perso la partita");
        }
        dialog_vittoria.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_vittoria.show();
        bottone_vittoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog_vittoria.isShowing()){
                    dialog_vittoria.dismiss();
                }
                final Handler threadHandler = new Handler();
                new Thread() {
                    @Override
                    public void run() {
                        threadHandler.postDelayed(new Runnable() {
                            public void run() {

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("nome_utente", nome_utente);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                threadHandler.removeCallbacks(this);
                                finish();
                            }
                        }, 2000);
                    }
                }.start();
            }

        });
    }
    public int getRandomWithExclusion(int start, int end, ArrayList<Integer> exclude) {
        int random = start + new Random().nextInt(end - start + 1 - exclude.size());
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }
}
