package com.example.progettolamtris.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.progettolamtris.R;
import com.example.progettolamtris.pubblica.MemorizzaPartitaPubblica;
import com.example.progettolamtris.pubblica.PubblicaDao;
import com.example.progettolamtris.pubblica.PubblicaRoomDatabase;

public class SchermataCaricamento extends AppCompatActivity {
    private Handler caricamentoHandler = new Handler();
    private Handler caricamentoHandler1 = new Handler();
    private Handler caricamentoHandler2 = new Handler();
    private Handler caricamentoHandler3 = new Handler();
    private Handler caricamentoHandler4 = new Handler();
    private ImageView cella1;
    private ImageView cella2;
    private ImageView cella3;
    private TextView tris;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Nascondo la barra contenente tutte le informazioni come la batteria, la connessione, ecc..
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.schermata_caricamento);
        cella1 = findViewById(R.id.cella1);
        cella2 = findViewById(R.id.cella5);
        cella3 = findViewById(R.id.cella9);
        tris = findViewById(R.id.textView_tris);
        caricamentoHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    //Schermata di caricamento che dura 5 secondi prima di passare alla pagina di login
                    Intent intent = new Intent(SchermataCaricamento.this, PaginaLogin.class);
                    startActivity(intent);
                    finish();




            }

        }, 5000);
        caricamentoHandler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                cella1.setImageResource(R.drawable.simbolo_x_removebg_preview);
            }
        }, 1000);
        caricamentoHandler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                cella2.setImageResource(R.drawable.simbolo_x_removebg_preview);
            }
        }, 2000);
        caricamentoHandler3.postDelayed(new Runnable() {
            @Override
            public void run() {
                cella3.setImageResource(R.drawable.simbolo_x_removebg_preview);
            }
        }, 3000);
        caricamentoHandler4.postDelayed(new Runnable() {
            @Override
            public void run() {
                tris.setVisibility(View.VISIBLE);
            }
        }, 4000);
    }

    public void onBackPressed() {
        //non scrivendo nulla, faccio in modo che l'utente non possa tornare indietro mentre avviene il caricamento
    }
    }

