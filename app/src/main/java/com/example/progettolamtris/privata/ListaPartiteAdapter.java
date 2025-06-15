package com.example.progettolamtris.privata;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progettolamtris.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class ListaPartiteAdapter extends RecyclerView.Adapter<ListaPartiteAdapter.ListaPartiteViewHolder> {
    private ArrayList<PartitaItem> dataSet;
    public DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://progettolamtris-default-rtdb.firebaseio.com/PasswordPrivate");
    public DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://progettolamtris-default-rtdb.firebaseio.com/partite/private");
    private PrivataDao privataDao;
    Application application;
    String nome_partita="";
    PrivataRoomDatabase db;
    private ValueEventListener uniscitiPartitaEventListener;
    public ListaPartiteAdapter(ArrayList<PartitaItem> dataSet, Application application) {
        this.dataSet = dataSet;
        this.application = application;
        db = PrivataRoomDatabase.getDatabase(application);
        privataDao = db.privataDao();
    }

    @NonNull
    @Override
    public ListaPartiteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.partita_item, parent, false);
        ListaPartiteViewHolder viewHolder = new ListaPartiteViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListaPartiteViewHolder holder, int position) {
        PartitaItem current = dataSet.get(position);
        boolean isExpanded = dataSet.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.bind(current.getNome_host(), current.getNome_utente());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void clear() {
        int size = dataSet.size();
        dataSet.clear();
        notifyItemRangeRemoved(0, size);
    }

    public class ListaPartiteViewHolder extends RecyclerView.ViewHolder {
        protected ConstraintLayout expandableLayout;
        protected TextView nome_host_item;
        protected Button bottone_unisciti;
        protected ImageView expandable_button;
        private EditText password_privata;

        public ListaPartiteViewHolder(@NonNull View itemView) {
            super(itemView);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            password_privata = itemView.findViewById(R.id.password_privata);
            nome_host_item = itemView.findViewById(R.id.nome_host);
            bottone_unisciti = itemView.findViewById(R.id.bottone_unisciti);
            expandable_button = itemView.findViewById(R.id.expandable_button);

            nome_host_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PartitaItem partitaItem = dataSet.get(getAdapterPosition());
                    partitaItem.setExpanded(!partitaItem.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });
            expandable_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PartitaItem partitaItem = dataSet.get(getAdapterPosition());
                    partitaItem.setExpanded(!partitaItem.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }

        public void bind(String nome_host, String nome_utente) {

            nome_host_item.setText(nome_host);
            bottone_unisciti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uniscitiPartitaEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.d("NOME PARTITA", nome_partita);
                            if (snapshot.child("Giocatori").getChildrenCount() == 1) {
                                databaseReference2.child(nome_partita).child("Giocatori").child(nome_utente).child("nome_giocatore").setValue(nome_utente);
                                if (!snapshot.hasChild("turno_giocatore")) {
                                    final int random = new Random().nextInt(2);
                                    if (random == 0) {
                                        databaseReference2.child(nome_partita).child("turno_giocatore").setValue(nome_host);
                                    } else {
                                        databaseReference2.child(nome_partita).child("turno_giocatore").setValue(nome_utente);
                                    }
                                    databaseReference2.child(nome_partita).removeEventListener(this);
                                    Intent intent = new Intent(view.getContext(), NuovaPartitaPrivata.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("nome_host", nome_host);
                                    bundle.putString("nome_partita", nome_partita);
                                    bundle.putString("host", "no");
                                    bundle.putString("nome_utente", nome_utente);
                                    intent.putExtras(bundle);
                                    view.getContext().startActivity(intent);
                                    ((Activity) view.getContext()).finish();
                                }
                            } else {
                                Toast.makeText(view.getContext(), "Non puoi unirti alla partita. Limite di giocatori raggiunto", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    String password_partita = password_privata.getText().toString();
                    if (password_partita.isEmpty()) {
                        Toast.makeText(view.getContext(), "Devi inserire una password per poter unirti alla partita", Toast.LENGTH_SHORT).show();
                    } else {
                            databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot password : dataSnapshot.getChildren()) {
                                        if (password_partita.equals(password.child("Password").getValue().toString())){
                                            Log.d("CIAOOOOOO","ciao");
                                        }
                                        if(password.child("host").getValue().toString().equals(nome_host)){
                                            Log.d("CIAOCIAO", "CIAOCIAO");
                                        }
                                        if (password_partita.equals(password.child("Password").getValue().toString()) && password.child("host").getValue().toString().equals(nome_host)) {
                                                nome_partita = password.getKey();
                                            PrivataRoomDatabase.databaseWriteExecutor.execute(() -> {
                                                if (privataDao.esiste_partita(nome_partita)) {
                                                    Log.d("LISTA PARTITE ADAPTER", "LA PARTITA è GIA' IN CORSO");
                                                    Intent intent = new Intent(view.getContext(), RiprendiPartitaPrivata.class);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("nome_host", nome_host);
                                                    bundle.putString("nome_partita", nome_partita);
                                                    bundle.putString("nome_utente", nome_utente);
                                                    intent.putExtras(bundle);
                                                    view.getContext().startActivity(intent);
                                                    ((Activity) view.getContext()).finish();
                                                } else {
                                                    databaseReference2.child(nome_partita).addListenerForSingleValueEvent(uniscitiPartitaEventListener);
                                                }
                                            });


                                        } else {
                                            Toast.makeText(view.getContext(), "Password inserita errata", Toast.LENGTH_SHORT);
                                        }
                                    }
                                }
                            });

                    }
                }
            });
        }
    }

}





