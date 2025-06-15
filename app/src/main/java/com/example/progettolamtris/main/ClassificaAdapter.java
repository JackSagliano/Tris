package com.example.progettolamtris.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progettolamtris.R;

import java.util.ArrayList;

public class ClassificaAdapter extends RecyclerView.Adapter<ClassificaAdapter.ClassificaViewHolder> {
    private ArrayList<GiocatoreItemClassifica> dataSet;


    public ClassificaAdapter(ArrayList<GiocatoreItemClassifica> dataSet) {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public ClassificaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.giocatore_classifica, parent, false);
        ClassificaViewHolder viewHolder = new ClassificaAdapter.ClassificaViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClassificaAdapter.ClassificaViewHolder holder, int position) {
        GiocatoreItemClassifica current = dataSet.get(position);
        holder.bind(current.getNome_giocatore(), current.getPunteggio());
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

    public class ClassificaViewHolder extends RecyclerView.ViewHolder {
        private ImageView immagine_profilo;
        private TextView nome_giocatore;
        private TextView punteggio;

        public ClassificaViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_giocatore = itemView.findViewById(R.id.nome_giocatore);
            punteggio = itemView.findViewById(R.id.punteggio);
        }

        public void bind(String nomegiocatore, int punt){
             nome_giocatore.setText(nomegiocatore);
             punteggio.setText(String.valueOf(punt));
        }
    }
}
