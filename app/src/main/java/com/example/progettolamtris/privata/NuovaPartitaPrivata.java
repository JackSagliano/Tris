package com.example.progettolamtris.privata;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class NuovaPartitaPrivata extends AppCompatActivity {
    private ServerPrivata serverPrivata;
    private CountDownTimer timer_turno;
    private String nome_host;
    private String nome_partita;
    private String nome_giocatore;
    private List<ImageView> caselle = new ArrayList<>();
    private ImageView casella0, casella1, casella2, casella3, casella4, casella5, casella6, casella7, casella8;
    private LinearLayout giocatore1_layout, giocatore2_layout;
    private TextView giocatore_uno, giocatore_due, timer_turno_tw;
    private boolean mio_turno = false;
    Observer<Boolean> inizioPartita;
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
        Log.d("ON CREATE", "ON CREATE");
        Bundle bundle = getIntent().getExtras();
        nome_partita = bundle.getString("nome_partita");
        nome_host = bundle.getString("nome_host");
        nome_giocatore = bundle.getString("nome_utente");
        //Nascondo la barra contenente tutte le informazioni come la batteria, la connessione, ecc..
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


        serverPrivata = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ServerPrivata.class);

        serverPrivata.setNome_partita(nome_partita);
        serverPrivata.setNome_host(nome_host);
        serverPrivata.setGiocatore1(nome_giocatore);


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

                    if (!serverPrivata.casella_libera(i)) {

                        arr.add(i);
                    }
                }
                if (arr.size() < 9) {
                    int casella_random = getRandomWithExclusion(0, 8, arr);
                    Log.d("TURNO SCADUTO", "TURNO SCADTUO SCELGO LA CASELLA NUMERO "+ casella_random);
                    mio_turno = false;
                    caselle.get(casella_random).setImageResource(R.drawable.simbolo_x_removebg_preview);
                    serverPrivata.gioca_casella(casella_random, nome_giocatore);



                }

            }

        };


        inizioPartita = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean.equals(true)){
                    progressDialog.dismiss();

                    giocatore_uno.setText(serverPrivata.getGiocatore1());
                    giocatore_due.setText(serverPrivata.getGiocatore2());
                    serverPrivata.isInizio_partita().removeObservers(NuovaPartitaPrivata.this);
                }
            }
        };
        turnoGiocatore = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s.equals(nome_giocatore)){
                    mio_turno = true;
                    giocatore1_layout.setBackgroundResource(R.drawable.turno_giocatore_illuminato);
                    giocatore2_layout.setBackgroundResource(R.drawable.casella_turno_giocatore);
                    startTimer();
                } else if(s.equals(serverPrivata.getGiocatore2())){
                    mio_turno = false;
                    serverPrivata.vittoria_tavolino_timer();
                    giocatore2_layout.setBackgroundResource(R.drawable.turno_giocatore_illuminato);
                    giocatore1_layout.setBackgroundResource(R.drawable.casella_turno_giocatore);
                }
            }
        };

        ultimaMossa = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(!s.equals("")) {
                    serverPrivata.gioca_casella_avversaria(Integer.parseInt(s), serverPrivata.getGiocatore2());
                    caselle.get(Integer.parseInt(s)).setImageResource(R.drawable.simbolo_o_removebg_preview);

                }
            }
        };

        getSconfittaTavolino = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean.equals(true)){
                    vittoria_tavolino(NuovaPartitaPrivata.this, false);
                }
            }
        };
        getVittoriaTavolino = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean.equals(true)){

                    vittoria_tavolino(NuovaPartitaPrivata.this, true);
                }
            }
        };

        getVittoriaGiocatore1 = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean.equals(true)){
                    dialog_vittoria(NuovaPartitaPrivata.this, true);
                }
            }
        };

        getVittoriaGiocatore2 = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean.equals(true)){
                    dialog_vittoria(NuovaPartitaPrivata.this, false);
                }
            }
        };

        getPareggio = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean.equals(true)){
                    removeObservers();
                    Dialog dialog_pareggio = new Dialog(NuovaPartitaPrivata.this);
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
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("nome_utente", nome_giocatore);
                            intent.putExtras(bundle);
                            startActivity(intent);

                            finish();


                        }
                    });
                }
            }
        };
        serverPrivata.isInizio_partita().observe(NuovaPartitaPrivata.this, inizioPartita);
        serverPrivata.getTurno_giocatore().observe(NuovaPartitaPrivata.this, turnoGiocatore);
        serverPrivata.ultima_mossa().observe(NuovaPartitaPrivata.this, ultimaMossa);
        serverPrivata.getVittoria_tavolino().observe(NuovaPartitaPrivata.this, getVittoriaTavolino);
        serverPrivata.getVittoriag1().observe(NuovaPartitaPrivata.this, getVittoriaGiocatore1);
        serverPrivata.getVittoriag2().observe(NuovaPartitaPrivata.this, getVittoriaGiocatore2);
        serverPrivata.getPareggio().observe(NuovaPartitaPrivata.this, getPareggio);
        serverPrivata.getSconfitta_tavolino().observe(NuovaPartitaPrivata.this, getSconfittaTavolino);
        for (int i = 0; i < caselle.size(); i++) {
            int finalI = i;
            caselle.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // if (serverPrivata.getTurno_giocatore().getValue().equals(nome_giocatore)  && serverPrivata.casella_disponibile(finalI)){
                    if (mio_turno && serverPrivata.casella_libera(finalI)){
                        timer_turno.cancel();
                        timer_turno_tw.setText("10");
                        serverPrivata.gioca_casella(finalI, nome_giocatore);
                        caselle.get(finalI).setImageResource(R.drawable.simbolo_x_removebg_preview);
                        mio_turno = false;
                        //serverPrivata.setTurno_giocatore(serverPrivata.getGiocatore2());

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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("nome_utente", nome_giocatore);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();

            }

        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        final Dialog dialog_abbandono = new Dialog(NuovaPartitaPrivata.this);
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
        serverPrivata.abbandona(nome_giocatore);
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("nome_utente", nome_giocatore);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();

            }

        });
    }
    public void removeObservers(){
        serverPrivata.isInizio_partita().removeObserver(inizioPartita);
        serverPrivata.ultima_mossa().removeObserver(ultimaMossa);
        serverPrivata.getVittoriag1().removeObserver(getVittoriaGiocatore1);
        serverPrivata.getVittoriag2().removeObserver(getVittoriaGiocatore2);
        serverPrivata.getPareggio().removeObserver(getPareggio);
        serverPrivata.getTurno_giocatore().removeObserver(turnoGiocatore);
        serverPrivata.getVittoria_tavolino().removeObserver(getVittoriaTavolino);
        serverPrivata.getSconfitta_tavolino().removeObserver(getSconfittaTavolino);
    }
}
