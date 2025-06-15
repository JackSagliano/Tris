package com.example.progettolamtris.pubblica;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.example.progettolamtris.main.MainActivity;
import com.example.progettolamtris.privata.RiprendiPartitaPrivata;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RiprendiPartitaPubblica extends AppCompatActivity {

    private ServerRiprendiPubblica serverPubblica;
    private String nome_giocatore;
    private String nome_partita;
    private List<ImageView> caselle = new ArrayList<>();
    private ImageView casella0, casella1, casella2, casella3, casella4, casella5, casella6, casella7, casella8;
    private LinearLayout giocatore1_layout, giocatore2_layout;
    private TextView giocatore_uno, giocatore_due, timer_turno_tw;
    private boolean mio_turno = false;
    private int ultima_mossa = -1;
    private CountDownTimer timer_turno;
    private List<Integer> caselle_giocate = new ArrayList<>();
    private List<Integer> caselle_giocate_avversario = new ArrayList<>();
    private SingletonRiprendiPubblica singletonRiprendiPubblica = SingletonRiprendiPubblica.getSingletonRiprendiPubblica();
    Observer<Boolean> inizioPartita;
    Observer<String> nomePartita;
    Observer<String> turnoGiocatore;
    Observer<String> ultimaMossa;
    Observer<Boolean> getVittoriaTavolino;
    Observer<Boolean> getVittoriaGiocatore1;
    Observer<Boolean> getVittoriaGiocatore2;
    Observer<Boolean> getSconfittaTavolino;
    Observer<Boolean> getPareggio;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        nome_giocatore = bundle.getString("nome_utente");
        nome_partita = bundle.getString("nome_partita");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.partita);
        timer_turno_tw = findViewById(R.id.timer_turno);
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


        serverPubblica = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ServerRiprendiPubblica.class);
        serverPubblica.setGiocatore1(nome_giocatore);
        serverPubblica.setNome_partita(nome_partita);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("In attesa dell'avversario");
        progressDialog.show();
        timer_turno = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {

                timer_turno_tw.setText(String.valueOf(l / 1000));
                if(mio_turno == false){
                    timer_turno.cancel();
                    timer_turno_tw.setText("10");

                }
            }

            @Override
            public void onFinish() {

                ArrayList<Integer> arr = new ArrayList<>();

                for (int i = 0; i < caselle.size(); i++) {

                    if (!serverPubblica.casella_libera(i)) {

                        arr.add(i);
                    }
                }
                if (arr.size() < 9) {
                    int casella_random = getRandomWithExclusion(0, 8, arr);
                    Log.d("TURNO SCADUTO", "TURNO SCADTUO SCELGO LA CASELLA NUMERO "+ casella_random);
                    caselle.get(casella_random).setImageResource(R.drawable.simbolo_x_removebg_preview);
                    serverPubblica.gioca_casella(casella_random, nome_giocatore);
                    //serverPubblica.setTurno_giocatore(serverPubblica.getGiocatore2());


                }

            }

        };
        inizioPartita = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean.equals(true)){
                    caselle_giocate = singletonRiprendiPubblica.getCaselle_g1();
                    caselle_giocate_avversario = singletonRiprendiPubblica.getCaselle_g2();
                    for (int i = 0; i < caselle_giocate.size(); i++) {
                        int j = caselle_giocate.get(i);
                        caselle.get(j).setImageResource(R.drawable.simbolo_x_removebg_preview);
                    }
                    for (int i = 0; i < caselle_giocate_avversario.size(); i++) {
                        int j = caselle_giocate_avversario.get(i);
                        caselle.get(j).setImageResource(R.drawable.simbolo_o_removebg_preview);
                    }
                    progressDialog.dismiss();
                    giocatore_uno.setText(serverPubblica.getGiocatore1());
                    giocatore_due.setText(serverPubblica.getGiocatore2());
                    serverPubblica.isInizio_partita().removeObservers(RiprendiPartitaPubblica.this);
                }
            }
        };
        nomePartita = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(!s.equals("")){
                    serverPubblica.riprendi_partita();
                }
            }
        };

        turnoGiocatore = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s.equals(nome_giocatore)){
                    Log.d("è il mio turno", "è il mio turno");
                    mio_turno = true;
                    giocatore1_layout.setBackgroundResource(R.drawable.turno_giocatore_illuminato);
                    giocatore2_layout.setBackgroundResource(R.drawable.casella_turno_giocatore);
                    startTimer();
                } else if(s.equals(serverPubblica.getGiocatore2())){
                    mio_turno = false;
                    serverPubblica.vittoria_tavolino_timer();
                    giocatore2_layout.setBackgroundResource(R.drawable.turno_giocatore_illuminato);
                    giocatore1_layout.setBackgroundResource(R.drawable.casella_turno_giocatore);
                }
            }
        };

        ultimaMossa = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(!s.equals("")) {
                    serverPubblica.gioca_casella_avversaria(Integer.parseInt(s), serverPubblica.getGiocatore2());
                    caselle.get(Integer.parseInt(s)).setImageResource(R.drawable.simbolo_o_removebg_preview);

                }
            }
        };

        getVittoriaTavolino = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean.equals(true)){
                    cancelTimer();
                    serverPubblica.elimina_partita(serverPubblica.getNome_partita().getValue().toString());
                    vittoria_tavolino(RiprendiPartitaPubblica.this, true);
                }
            }
        };

        getVittoriaGiocatore1 = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean.equals(true)){
                    cancelTimer();
                    serverPubblica.elimina_partita(serverPubblica.getNome_partita().getValue().toString());
                    dialog_vittoria(RiprendiPartitaPubblica.this, true);
                }
            }
        };

        getVittoriaGiocatore2 = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean.equals(true)){
                    cancelTimer();
                    serverPubblica.elimina_partita(serverPubblica.getNome_partita().getValue().toString());
                    dialog_vittoria(RiprendiPartitaPubblica.this, false);
                }
            }
        };

        getPareggio = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean.equals(true)){
                    cancelTimer();
                    serverPubblica.elimina_partita(serverPubblica.getNome_partita().getValue().toString());
                    removeObservers();
                    Dialog dialog_pareggio = new Dialog(RiprendiPartitaPubblica.this);
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
                            if(dialog_pareggio.isShowing()) {
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
                                            bundle.putString("nome_utente", nome_giocatore);
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
            }
        };

        getSconfittaTavolino = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean.equals(true)){
                    cancelTimer();
                    vittoria_tavolino(RiprendiPartitaPubblica.this, false);
                }
            }
        };
        serverPubblica.isInizio_partita().observe(RiprendiPartitaPubblica.this, inizioPartita);
        serverPubblica.getNome_partita().observe(RiprendiPartitaPubblica.this, nomePartita);
        serverPubblica.getTurno_giocatore().observe(RiprendiPartitaPubblica.this, turnoGiocatore);
        serverPubblica.ultima_mossa().observe(RiprendiPartitaPubblica.this, ultimaMossa);
        serverPubblica.getVittoria_tavolino().observe(RiprendiPartitaPubblica.this, getVittoriaTavolino);
        serverPubblica.getVittoriag1().observe(RiprendiPartitaPubblica.this, getVittoriaGiocatore1);
        serverPubblica.getVittoriag2().observe(RiprendiPartitaPubblica.this, getVittoriaGiocatore2);
        serverPubblica.getPareggio().observe(RiprendiPartitaPubblica.this, getPareggio);
        serverPubblica.getSconfitta_tavolino().observe(RiprendiPartitaPubblica.this,getSconfittaTavolino);
        for (int i = 0; i < caselle.size(); i++) {
            int finalI = i;
            caselle.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // if (serverPrivata.getTurno_giocatore().getValue().equals(nome_giocatore)  && serverPrivata.casella_disponibile(finalI)){
                    if (mio_turno && serverPubblica.casella_libera(finalI)){
                        serverPubblica.gioca_casella(finalI, nome_giocatore);
                        caselle.get(finalI).setImageResource(R.drawable.simbolo_x_removebg_preview);
                        mio_turno = false;
                        timer_turno.cancel();
                        timer_turno_tw.setText("10");
                        serverPubblica.setTurno_giocatore(serverPubblica.getGiocatore2());
                    }


                }
            });

        }







    }
    public void dialog_vittoria(Context context, boolean win){
        removeObservers();
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
                                bundle.putString("nome_utente", nome_giocatore);
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        final Dialog dialog_abbandono = new Dialog(RiprendiPartitaPubblica.this);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 1);
        dialog_abbandono.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_abbandono.setCancelable(false);
        dialog_abbandono.setContentView(R.layout.dialog_menu_principale);
        Button abbandona = dialog_abbandono.findViewById(R.id.abbandona);
        Button non_abbandonare = dialog_abbandono.findViewById(R.id.non_abbandonare);
        dialog_abbandono.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_abbandono.show();
        abbandona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog_abbandono.isShowing()){
                    dialog_abbandono.dismiss();
                }
                //serverPrivata.abbandona(nome_giocatore);

                abbandono();
            }
        });
        non_abbandonare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_abbandono.dismiss();
            }
        });

    }

    public void abbandono(){
        removeObservers();
        serverPubblica.abbandona(nome_giocatore);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("nome_utente", nome_giocatore);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }


    public void startTimer(){
        timer_turno.start();

    }
    public void cancelTimer(){
        timer_turno.cancel();
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

    public void vittoria_tavolino(Context context, boolean vittoria){
        removeObservers();
        final Dialog dialog_vittoria = new Dialog(context);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 1);
        dialog_vittoria.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_vittoria.setCancelable(false);
        dialog_vittoria.setContentView(R.layout.dialog_vittoria_tavolino);
        Button bottone_vittoria = dialog_vittoria.findViewById(R.id.bottone_tavolino);
        TextView testo1 = dialog_vittoria.findViewById(R.id.tavolinoTw1);
        TextView testo2 = dialog_vittoria.findViewById(R.id.tavolinoTw2);
        if(vittoria == true){
            testo1.setText("Il tuo avversario ha abbandonato la partita");
            testo2.setText("Hai vinto la partita a tavolino");
        } else {
            testo1.setText("Hai esaurito il tempo a disposizione");
            testo2.setText("Hai perso la partita a tavolino");
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
                                bundle.putString("nome_utente", nome_giocatore);
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
    public void removeObservers(){
        serverPubblica.isInizio_partita().removeObserver(inizioPartita);
        serverPubblica.getNome_partita().removeObserver(nomePartita);
        serverPubblica.ultima_mossa().removeObserver(ultimaMossa);
        serverPubblica.getVittoriag1().removeObserver(getVittoriaGiocatore1);
        serverPubblica.getVittoriag2().removeObserver(getVittoriaGiocatore2);
        serverPubblica.getPareggio().removeObserver(getPareggio);
        serverPubblica.getTurno_giocatore().removeObserver(turnoGiocatore);
        serverPubblica.getVittoria_tavolino().removeObserver(getVittoriaTavolino);
        serverPubblica.getSconfitta_tavolino().removeObserver(getSconfittaTavolino);
    }
}
