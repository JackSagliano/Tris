package com.example.progettolamtris.pubblica;

import android.app.Application;
import android.os.CountDownTimer;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.progettolamtris.privata.MemorizzaPartitaPrivata;
import com.example.progettolamtris.privata.PrivataDao;
import com.example.progettolamtris.privata.PrivataRoomDatabase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerPubblica extends AndroidViewModel {
    public DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://progettolamtris-default-rtdb.firebaseio.com/partite/pubbliche");
    public DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://progettolamtris-default-rtdb.firebaseio.com/utenti");
    private final List<Integer> caselle_giocate = new ArrayList<>();
    private final String[] caselle_giocatore1 = {"", "", "", "", "", "", "", "", ""}; //caselle giocate dal giocatore 1
    private final String[] caselle_giocatore2 = {"", "", "", "", "", "", "", "", ""};//caselle giocate dal giocatore 2
    private final List<int[]> combinazioni_vittoria = new ArrayList<>();
    CountDownTimer abbandono_timer;
    private String giocatore1;
    private String giocatore2;
    private PubblicaDao pubblicaDao;
    private MemorizzaPartitaPubblica partita_in_corso;
    private boolean partita_finita = false;
    public MutableLiveData<Boolean> getSconfitta_tavolino() {
        return sconfitta_tavolino;
    }

    private MutableLiveData<Boolean> sconfitta_tavolino = new MutableLiveData<>();
    public MutableLiveData<String> getNome_partita() {
        return nome_partita;
    }

    MutableLiveData<String> nome_partita= new MutableLiveData<>();
    private MutableLiveData<String> turno_giocatore = new MutableLiveData<>();
    public MutableLiveData<Boolean> inizio_partita = new MutableLiveData<>();
    public MutableLiveData<String> ultima_mossa = new MutableLiveData<>();
    public MutableLiveData<Boolean> vittoriag1 = new MutableLiveData<>();
    public MutableLiveData<Boolean> vittoriag2 = new MutableLiveData<>();
    public MutableLiveData<Boolean> pareggio = new MutableLiveData<>();
    ChildEventListener ultimaMossaEventListener;
    ValueEventListener assegnaPartitaEventListener,esitoPartitaEventListener, turnoEventListener, turnoInizialeEventListener, inizioPartitaEventListener,  sconfittaTavolinoEventListener, vittoriaTavolinoEventListener;
    private MutableLiveData<Boolean> vittoria_tavolino = new MutableLiveData<>();
    private static final int nTHREADS =7;
    static final ExecutorService firebaseExecutor = Executors.newFixedThreadPool(nTHREADS);


    public ServerPubblica(@NonNull Application application) {
        super(application);
        PubblicaRoomDatabase db = PubblicaRoomDatabase.getDatabase(application);
        pubblicaDao = db.pubblicaDao();
        this.combinazioni_vittoria.add(new int[]{0,1,2});
        this.combinazioni_vittoria.add(new int[]{0,3,6});
        this.combinazioni_vittoria.add(new int[]{0,4,8});
        this.combinazioni_vittoria.add(new int[]{1,4,7});
        this.combinazioni_vittoria.add(new int[]{2,4,6});
        this.combinazioni_vittoria.add(new int[]{2,5,8});
        this.combinazioni_vittoria.add(new int[]{3,4,5});
        this.combinazioni_vittoria.add(new int[]{6,7,8});
        inizio_partita.setValue(false);
        vittoriag1.setValue(false);
        vittoriag2.setValue(false);
        pareggio.setValue(false);
        nome_partita.setValue("");
        abbandono_timer = new CountDownTimer(40000, 1000) {
            @Override
            public void onTick(long l) {
                if(getTurno_giocatore().equals(giocatore1)){
                    Log.d("ONTICK", "ONTICK");
                    abbandono_timer.cancel();
                }


            }

            @Override
            public void onFinish() {
                if(partita_finita == false) {
                    abbandono_timer.cancel();
                    databaseReference.child(nome_partita.getValue()).child("vittoria_tavolino").setValue(giocatore1);
                    databaseReference.child(nome_partita.getValue()).removeEventListener(esitoPartitaEventListener);
                    databaseReference.child(nome_partita.getValue()).child("turno_giocatore").removeEventListener(turnoEventListener);
                    databaseReference.child(nome_partita.getValue()).child("Giocatori").child(giocatore2).removeEventListener(ultimaMossaEventListener);
                    assegnaPunteggio(giocatore1);
                    vittoria_tavolino.setValue(true);




                }
            }
        };
        firebaseExecutor.execute(()->{
            ultimaMossaEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("ON CHILD ADDED", snapshot.getKey().toString());
                    if(!snapshot.getKey().equals("nome_giocatore")){
                        ultima_mossa.setValue(snapshot.getValue().toString());
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("ON CHILD CHANGED", snapshot.getValue().toString());
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
        });
        firebaseExecutor.execute(()->{
            vittoriaTavolinoEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild("vittoria_tavolino")){
                        if(snapshot.child("vittoria_tavolino").getValue().toString().equals(giocatore1)){
                            abbandono_timer.cancel();
                            databaseReference.child(nome_partita.getValue()).child("turno_giocatore").removeEventListener(turnoEventListener);
                            databaseReference.child(nome_partita.getValue()).child("Giocatori").child(giocatore2).removeEventListener(ultimaMossaEventListener);
                            databaseReference.child(nome_partita.getValue()).removeEventListener(esitoPartitaEventListener);
                            databaseReference.child(nome_partita.getValue()).removeEventListener(this);
                            assegnaPunteggio(giocatore1);
                            vittoria_tavolino.setValue(true);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
        });
        firebaseExecutor.execute(()->{
            sconfittaTavolinoEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild("vittoria_tavolino")){
                        if(snapshot.child("vittoria_tavolino").getValue().toString().equals(giocatore2)){
                            abbandono_timer.cancel();
                            databaseReference.child(nome_partita.getValue()).child("turno_giocatore").removeEventListener(turnoEventListener);
                            databaseReference.child(nome_partita.getValue()).child("Giocatori").child(giocatore2).removeEventListener(ultimaMossaEventListener);
                            databaseReference.child(nome_partita.getValue()).removeEventListener(esitoPartitaEventListener);
                            databaseReference.child(nome_partita.getValue()).removeEventListener(this);

                            sconfitta_tavolino.setValue(true);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
        });

firebaseExecutor.execute(()->{
    esitoPartitaEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.hasChild("esito_partita")){
                if(snapshot.child("esito_partita").child("esito").hasChild("vittoria")) {
                    if (snapshot.child("esito_partita").child("esito").child("vittoria").getValue().toString().equals(giocatore1)) {
                        abbandono_timer.cancel();
                        assegnaPunteggio(giocatore1);
                        partita_finita = true;
                        databaseReference.child(nome_partita.getValue()).child("turno_giocatore").removeEventListener(turnoEventListener);
                        databaseReference.child(nome_partita.getValue()).child("Giocatori").child(giocatore2).removeEventListener(ultimaMossaEventListener);
                        //databaseReference.child(nome_partita.getValue()).removeValue();
                        databaseReference.child(nome_partita.getValue()).removeEventListener(this);
                        vittoriag1.setValue(true);
                    } else if (snapshot.child("esito_partita").child("esito").child("vittoria").getValue().toString().equals(giocatore2)) {

                        partita_finita = true;
                        abbandono_timer.cancel();
                        databaseReference.child(nome_partita.getValue()).child("turno_giocatore").removeEventListener(turnoEventListener);
                        databaseReference.child(nome_partita.getValue()).child("Giocatori").child(giocatore2).removeEventListener(ultimaMossaEventListener);
                        databaseReference.child(nome_partita.getValue()).removeValue();
                        databaseReference.child(nome_partita.getValue()).removeEventListener(this);
                        vittoriag2.setValue(true);
                    }
                }else if(snapshot.child("esito_partita").child("esito").getValue().toString().equals("pareggio")){

                    partita_finita = true;
                    abbandono_timer.cancel();
                    databaseReference.child(nome_partita.getValue()).child("turno_giocatore").removeEventListener(turnoEventListener);
                    databaseReference.child(nome_partita.getValue()).child("Giocatori").child(giocatore2).removeEventListener(ultimaMossaEventListener);
                    Log.d("NOME PARTITA DA ELIMINARE", nome_partita.getValue());
                    databaseReference.child(nome_partita.getValue()).removeValue();
                    databaseReference.child(nome_partita.getValue()).removeEventListener(this);
                    pareggio.setValue(true);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
});
        firebaseExecutor.execute(()->{
            turnoEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String turnogiocatore = snapshot.getValue().toString();
                        turno_giocatore.setValue(turnogiocatore);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
        });
firebaseExecutor.execute(()->{
    turnoInizialeEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.hasChild("turno_giocatore")) {
                databaseReference.child(nome_partita.getValue()).removeEventListener(this);
                databaseReference.child(nome_partita.getValue()).child("turno_giocatore").addValueEventListener(turnoEventListener);

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
});

firebaseExecutor.execute(()->{
    assegnaPartitaEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            int partite_giocate = Integer.parseInt(snapshot.child("PartiteGiocate").getValue().toString());
            partite_giocate = partite_giocate +1;
            databaseReference2.child(giocatore1).child("PartiteGiocate").setValue(partite_giocate);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
});
        firebaseExecutor.execute(()->{
            inizioPartitaEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("SNAPSHOT", snapshot.toString());
                    PubblicaRoomDatabase.databaseWriteExecutor.execute(()->{
                        partita_in_corso = new MemorizzaPartitaPubblica(nome_partita.getValue(), giocatore1);
                        memorizza_partita(partita_in_corso);
                    });
                    if(snapshot.hasChild("stato") && snapshot.hasChild("Giocatori")) {
                        if (snapshot.child("stato").getValue().toString().equals("in_corso")) {
                            if (snapshot.child("Giocatori").getChildrenCount() == 2) {
                                for (DataSnapshot giocatori : snapshot.child("Giocatori").getChildren()) {
                                    if (!giocatori.getKey().equals(giocatore1)) {
                                        databaseReference.child(nome_partita.getValue()).removeEventListener(this);
                                        giocatore2 = giocatori.getKey();
                                        databaseReference2.child(giocatore1).addListenerForSingleValueEvent(assegnaPartitaEventListener);
                                        databaseReference.child(nome_partita.getValue()).addValueEventListener(turnoInizialeEventListener);
                                        databaseReference.child(nome_partita.getValue()).addValueEventListener(esitoPartitaEventListener);
                                        databaseReference.child(nome_partita.getValue()).child("Giocatori").child(giocatore2).addChildEventListener(ultimaMossaEventListener);
                                        databaseReference.child(nome_partita.getValue()).addValueEventListener(sconfittaTavolinoEventListener);
                                        databaseReference.child(nome_partita.getValue()).addValueEventListener(vittoriaTavolinoEventListener);
                                        setInizio_partita();
                                    }
                                }


                            }

                        }
                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
        });



    }
    public void setNome_partita(String nome_partita) {
        this.nome_partita.setValue(nome_partita);
    }
    public MutableLiveData<Boolean> getVittoria_tavolino() {
        return vittoria_tavolino;
    }

    public void avvia_matchmaking(String giocatore) {
        firebaseExecutor.execute(()->{
            databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    boolean bool = false;
                    for (DataSnapshot partite : dataSnapshot.getChildren()) {
                        if (partite.hasChild("stato")) {
                            if (partite.child("stato").getValue().toString().equals("attesa")) {

                                setNome_partita(partite.getKey());
                                databaseReference.child(partite.getKey()).child("Giocatori").child(giocatore).child("nome_giocatore").setValue(giocatore).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        databaseReference.child(partite.getKey()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                            @Override
                                            public void onSuccess(DataSnapshot dataSnapshot) {
                                                List<String> lista_giocatori = new ArrayList<>();
                                                for (DataSnapshot giocatori : dataSnapshot.child("Giocatori").getChildren()) {
                                                    lista_giocatori.add(giocatori.child("nome_giocatore").getValue().toString());
                                                }

                                                if (!dataSnapshot.hasChild("turno_giocatore")) {
                                                    final int random = new Random().nextInt(2);

                                                    if (random == 0) {

                                                        databaseReference.child(nome_partita.getValue()).child("turno_giocatore").setValue(lista_giocatori.get(0));

                                                    } else {

                                                        databaseReference.child(nome_partita.getValue()).child("turno_giocatore").setValue(lista_giocatori.get(1));
                                                    }


                                                }
                                            }
                                        });

                                    }
                                });
                                bool = true;
                                databaseReference.child(nome_partita.getValue()).child("stato").setValue("in_corso");
                                break;
                            }

                        }
                    }
                    if (!bool) {
                        String nome_partita = String.valueOf(System.currentTimeMillis());
                        setNome_partita(nome_partita);
                        databaseReference.child(nome_partita).child("stato").setValue("attesa");
                        databaseReference.child(nome_partita).child("Giocatori").child(giocatore).child("nome_giocatore").setValue(giocatore);

                    }

                }
            });
        });

    }

    public MutableLiveData<Boolean> isInizio_partita() {
        Log.d("iniziopartita", String.valueOf(inizio_partita.getValue()));

        return inizio_partita;
    }
    public void setGiocatore1(String giocatore1) {
        this.giocatore1 = giocatore1;
    }
    public String getGiocatore1() {

        return giocatore1;
    }

    public String getGiocatore2() {

        return giocatore2;
    }
    public void setInizio_partita() {
        inizio_partita.setValue(true);
        Log.d("valore_di_inizio_partita", String.valueOf(inizio_partita.getValue()));
    }
    public MutableLiveData<String> getTurno_giocatore() {
//Log.d("Get turno giocatore", turno_giocatore.getValue().toString());
        return turno_giocatore;
    }
    public void setTurno_giocatore(String turnogiocatore) {
        //this.turno_giocatore.setValue(turno_giocatore);
        firebaseExecutor.execute(()->{
            databaseReference.child(nome_partita.getValue()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild("turno_giocatore")){
                        databaseReference.child(nome_partita.getValue()).child("turno_giocatore").setValue(turnogiocatore);
                        databaseReference.child(nome_partita.getValue()).removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });


    }
    public void gioca_casella(int casella, String giocatore) {
        if(!caselle_giocate.contains(casella)) {
            if (giocatore == giocatore1) {
                caselle_giocatore1[casella] = giocatore;
            } else {
                caselle_giocatore2[casella] = giocatore;
            }
            caselle_giocate.add(casella);
            firebaseExecutor.execute(()->{
                databaseReference.child(nome_partita.getValue()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int n_casella_giocata = (int) snapshot.child("Giocatori").child(giocatore).getChildrenCount();
                        databaseReference.child(nome_partita.getValue()).child("Giocatori").child(giocatore).child("mossa " + String.valueOf(n_casella_giocata)).setValue(casella).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if (ho_vinto(giocatore1) == false && ho_vinto(giocatore2) == false && caselle_giocate.size() == 9) {
                                    databaseReference.child(nome_partita.getValue()).child("esito_partita").child("esito").setValue("pareggio");
                                }
                                if (ho_vinto(giocatore1)) {
                                    databaseReference.child(nome_partita.getValue()).child("esito_partita").child("esito").child("vittoria").setValue(giocatore1);

                                } else {
                                    setTurno_giocatore(giocatore2);
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            });



        }
    }

    public void gioca_casella_avversaria(int casella, String giocatore){
        if(!caselle_giocate.contains(casella)){
            if(giocatore == giocatore1){
                caselle_giocatore1[casella] = giocatore;
            } else {
                caselle_giocatore2[casella] = giocatore;
            }
            caselle_giocate.add(casella);



        }
    }

    public boolean casella_libera(int casella){
        if(!caselle_giocate.contains(casella)){
            return true;
        }
        return false;
    }
    public MutableLiveData<String> ultima_mossa(){


        return ultima_mossa;

    }

    public MutableLiveData<Boolean> getVittoriag1() {
        return vittoriag1;
    }

    public MutableLiveData<Boolean> getVittoriag2() {
        return vittoriag2;
    }

    public MutableLiveData<Boolean> getPareggio() {
        return pareggio;
    }
    public void memorizza_partita(MemorizzaPartitaPubblica memorizzaPartitaPubblica){
        PubblicaRoomDatabase.databaseWriteExecutor.execute(()->{
            pubblicaDao.insert(memorizzaPartitaPubblica);
        });
    }

    public boolean ho_vinto(String giocatore){
        if(giocatore == giocatore1){
            for (int j = 0; j < combinazioni_vittoria.size(); j++) {
                final int[] combinazione = combinazioni_vittoria.get(j);
                if(caselle_giocatore1[combinazione[0]].equals(giocatore1)&&
                        caselle_giocatore1[combinazione[1]].equals(giocatore1) &&
                        caselle_giocatore1[combinazione[2]].equals(giocatore1)){
                    return true;
                }

            }
            return false;
        } else {
            for (int j = 0; j < combinazioni_vittoria.size(); j++) {
                final int[] combinazione = combinazioni_vittoria.get(j);
                if(caselle_giocatore2[combinazione[0]].equals(giocatore2)&&
                        caselle_giocatore2[combinazione[1]].equals(giocatore2) &&
                        caselle_giocatore2[combinazione[2]].equals(giocatore2)){
                    return true;
                }

            }
            return false;
        }
    }

    public void assegnaPunteggio(String giocatore){
            firebaseExecutor.execute(()->{
                databaseReference2.child(giocatore).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        int punteggio = Integer.parseInt(snapshot.child("PartiteVinte").getValue().toString());
                        punteggio = punteggio +1;
                        databaseReference2.child(giocatore).child("PartiteVinte").setValue(punteggio).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                databaseReference.child(nome_partita.getValue()).removeValue();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            });


    }


    public void vittoria_tavolino_timer(){

        abbandono_timer.start();
    }



    public void abbandona(String giocatore){
        if(getGiocatore2().equals("")){
            databaseReference.child(nome_partita.getValue()).removeValue();
        } else {
            if (giocatore.equals(giocatore1)) {
                databaseReference.child(nome_partita.getValue()).child("vittoria_tavolino").setValue(giocatore2);
            } else {
                databaseReference.child(nome_partita.getValue()).child("vittoria_tavolino").setValue(giocatore1);
            }
        }
    }

public void inizia_partita(){
        databaseReference.child(nome_partita.getValue()).addValueEventListener(inizioPartitaEventListener);
}
    public void elimina_partita(String nome_partita){
        PubblicaRoomDatabase.databaseWriteExecutor.execute(()->{
            pubblicaDao.elimina_partita(nome_partita);
        });
    }
}
