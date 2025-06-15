package com.example.progettolamtris.main;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.progettolamtris.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class PaginaRegistrazione extends AppCompatActivity {
    public DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://progettolamtris-default-rtdb.firebaseio.com/utenti");
    private EditText nome_utente;
    private EditText password;
    private Button registration_button;
    private TextView password_label;
    private TextView accedi;
    private EditText conferma_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Nascondo la barra contenente tutte le informazioni come la batteria, la connessione, ecc..
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.pagina_registrazione);
        nome_utente = findViewById(R.id.inserisci_nome_utente);
        password = findViewById(R.id.inserisci_password);
        registration_button = findViewById(R.id.bottone_registrati);
        accedi = findViewById(R.id.accedi);
        conferma_password = findViewById(R.id.conferma_password);

        registration_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome_utente_string = nome_utente.getText().toString();
                String password_string = password.getText().toString();
                String conferma_password_string = conferma_password.getText().toString();
                if(nome_utente_string.isEmpty() || password_string.isEmpty()){
                    Toast.makeText(PaginaRegistrazione.this, "Devi prima completare tutti i campi", Toast.LENGTH_SHORT).show();
                } else if(!conferma_password_string.equals(password_string)){
                    Toast.makeText(PaginaRegistrazione.this, "Le due password non coincidono", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(PaginaRegistrazione.this, "Nome utente già in uso", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    databaseReference.child(nome_utente_string).child("NomeUtente").setValue(nome_utente_string);
                                                    databaseReference.child(nome_utente_string).child("Password").setValue(password_string);
                                                    databaseReference.child(nome_utente_string).child("PartiteGiocate").setValue(0);
                                                    databaseReference.child(nome_utente_string).child("PartiteVinte").setValue(0);
                                                    databaseReference.child(nome_utente_string).child("DataCreazione").setValue(ServerValue.TIMESTAMP);
                                                    Toast.makeText(PaginaRegistrazione.this, "Account registrato con successo", Toast.LENGTH_SHORT).show();

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
                                        Toast.makeText(PaginaRegistrazione.this, "Connettiti ad internet per poterti registrare", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }
                    });
                    thread.start();



                }
            }
        });

        accedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PaginaLogin.class);
                startActivity(intent);
                finish();
            }
        });

    }


}
