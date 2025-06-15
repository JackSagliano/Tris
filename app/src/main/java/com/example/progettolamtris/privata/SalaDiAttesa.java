package com.example.progettolamtris.privata;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progettolamtris.main.VerticalSpaceItemDecoration;
import com.example.progettolamtris.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SalaDiAttesa extends AppCompatActivity {
    private String nome_utente;
    private ArrayList<PartitaItem> myDataSet = new ArrayList<>();
    private Button crea_partita_button;
    private String nome_partita="";
    private ValueEventListener aggiornaRecyclerEventListener;
    public DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://progettolamtris-default-rtdb.firebaseio.com/partite/private");
    public DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://progettolamtris-default-rtdb.firebaseio.com/PasswordPrivate");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //Nascondo la barra contenente tutte le informazioni come la batteria, la connessione, ecc..
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sala_attesa);
        Bundle bundle = getIntent().getExtras();
        nome_utente = bundle.getString("nome_utente");
        crea_partita_button = findViewById(R.id.crea_partita);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        ListaPartiteAdapter listaPartiteAdapter = new ListaPartiteAdapter(myDataSet, getApplication());
        recyclerView.setAdapter(listaPartiteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(48));
        //serverPrivata = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ServerPrivata.class);


    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listaPartiteAdapter.clear();
                            for (DataSnapshot partite : snapshot.getChildren()) {
                                if (partite.hasChild("host")) {
                                    String nome_host = partite.child("host").getValue().toString();
                                    PartitaItem partitaItem = new PartitaItem(nome_host, nome_utente);
                                    myDataSet.add(partitaItem);
                                    listaPartiteAdapter.notifyItemInserted(myDataSet.size() + 1);
                                } else {
                                    databaseReference.child(partite.getKey()).removeValue();
                                }
                            }
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    });
    thread.start();



        crea_partita_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(SalaDiAttesa.this);
                int width = (int)(getResources().getDisplayMetrics().widthPixels*1);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.popup_crea_privata);
                TextView textView = dialog.findViewById(R.id.textview_privata);
                LinearLayout linearLayout = dialog.findViewById(R.id.linearLayout_privata);
                EditText editText = dialog.findViewById(R.id.password_partita);
                Button button = dialog.findViewById(R.id.crea_partita_privata);
                dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nome_partita = String.valueOf(System.currentTimeMillis());
                        String password_partita = editText.getText().toString();
                        if (password_partita.isEmpty()) {
                            Toast.makeText(SalaDiAttesa.this, "Devi inserire una password per poter creare una partita",Toast.LENGTH_SHORT).show();
                        } else {
                            if (!nome_partita.equals("")) {
                                databaseReference2.child(nome_partita).child("Password").setValue(password_partita).addOnSuccessListener(new OnSuccessListener<Void>() {

                                    @Override
                                    public void onSuccess(Void unused) {
                                        databaseReference2.child(nome_partita).child("host").setValue(nome_utente);
                                        databaseReference.child(nome_partita).child("host").setValue(nome_utente);
                                        databaseReference.child(nome_partita).child("Giocatori").child(nome_utente).child("nome_giocatore").setValue(nome_utente);
                                        Intent intent = new Intent(getApplicationContext(), NuovaPartitaPrivata.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("nome_partita", nome_partita);
                                        bundle.putString("nome_host", nome_utente);
                                        bundle.putString("host", "si");
                                        bundle.putString("nome_utente", nome_utente);
                                        dialog.dismiss();
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        finish();
                                    }
                                });


                            }
                        }



                        }
                    });



            }
        });
    }


}
