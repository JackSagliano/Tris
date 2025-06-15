package com.example.progettolamtris.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.progettolamtris.R;

public class DifficoltaComputer extends AppCompatActivity {
    private String nome_utente;
    private Button facile;
    private Button media;
    private Button difficile;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.difficolta_computer);
        Bundle bundle = getIntent().getExtras();
        nome_utente = bundle.getString("nome_utente");
        facile = findViewById(R.id.button_facile);
        media = findViewById(R.id.button_media);
        difficile = findViewById(R.id.button_difficile);

        facile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ComputerFacile.class);
                Bundle bundle = new Bundle();
                bundle.putString("nome_utente", nome_utente);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ComputerMedia.class);
                Bundle bundle = new Bundle();
                bundle.putString("nome_utente", nome_utente);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        difficile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ComputerDifficile.class);
                Bundle bundle = new Bundle();
                bundle.putString("nome_utente", nome_utente);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
