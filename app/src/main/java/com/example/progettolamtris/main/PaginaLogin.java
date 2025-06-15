package com.example.progettolamtris.main;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.progettolamtris.R;
import com.example.progettolamtris.pubblica.MemorizzaPartitaPubblica;
import com.example.progettolamtris.pubblica.PubblicaDao;
import com.example.progettolamtris.pubblica.PubblicaRoomDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class PaginaLogin extends AppCompatActivity {
    public DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://progettolamtris-default-rtdb.firebaseio.com/utenti");
    private MemorizzaPartitaPubblica riprendi_pubblica;
    private EditText nome_utente;
    private EditText password;
    private Button bottone_login;
    private TextView registrati;
    PubblicaDao pubblicaDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PubblicaRoomDatabase db = PubblicaRoomDatabase.getDatabase(getApplication()); //ho reso getDatabase public
        pubblicaDao = db.pubblicaDao();
        super.onCreate(savedInstanceState);
        //Nascondo la barra contenente tutte le informazioni come la batteria, la connessione, ecc..
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.pagina_login);

        nome_utente = findViewById(R.id.nome_utente_login);
        password = findViewById(R.id.password_login);
        bottone_login = findViewById(R.id.login_button);
        registrati = findViewById(R.id.registrati);
        bottone_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome_utente_string = nome_utente.getText().toString();
                String password_string = password.getText().toString();
                if(nome_utente_string.isEmpty() || password_string.isEmpty()){
                    Toast.makeText(PaginaLogin.this, "Devi prima compilare tutti i campi", Toast.LENGTH_SHORT).show();
                }

                else {
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
                                                if (snapshot.hasChild(nome_utente_string)) {
                                                    final String password = snapshot.child(nome_utente_string).child("Password").getValue(String.class);
                                                    if (password.equals(password_string)) {
                                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                databaseReference.child(nome_utente_string).child("UltimoAccesso").setValue(ServerValue.TIMESTAMP);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                        Bundle bundle = new Bundle();
                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                        bundle.putString("nome_utente", nome_utente_string);
                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(PaginaLogin.this, "Password inserita errata", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {

                                                    Toast.makeText(PaginaLogin.this, "Nome utente o password errati", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(PaginaLogin.this, "Connettiti ad internet per poter accedere", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }
                    });
                    thread.start();

                }
            }

        });
        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PaginaRegistrazione.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
