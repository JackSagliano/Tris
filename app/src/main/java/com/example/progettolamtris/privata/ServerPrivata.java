package com.example.progettolamtris.privata;

import android.app.Application;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerPrivata extends AndroidViewModel {
    public DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://progettolamtris-default-rtdb.firebaseio.com/partite/private");
    public DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://progettolamtris-default-rtdb.firebaseio.com/PasswordPrivate");
    private final List<Integer> caselle_giocate = new ArrayList<>();
    private final String[] caselle_giocatore1 = {"", "", "", "", "", "", "", "", ""}; //caselle giocate dal giocatore 1
    private final String[] caselle_giocatore2 = {"", "", "", "", "", "", "", "", ""};//caselle giocate dal giocatore 2
    private final List<int[]> combinazioni_vittoria = new ArrayList<>();
    private String nome_partita="";
    private String nome_host = "";
    private String giocatore1;
    private String giocatore2;
    private MutableLiveData<String> turno_giocatore = new MutableLiveData<>();
    private MutableLiveData<Boolean> inizio_partita = new MutableLiveData<>();
    private MutableLiveData<String> ultima_mossa = new MutableLiveData<>();
    private MutableLiveData<Boolean> vittoriag1 = new MutableLiveData<>();
    private MutableLiveData<Boolean> vittoriag2 = new MutableLiveData<>();
    private ValueEventListener esitoPartitaEventListener, turnoEventListener, sconfittaTavolinoEventListener, vittoriaTavolinoEventListener;
    private ChildEventListener ultimaMossaEventListener;
    private MutableLiveData<Boolean> pareggio = new MutableLiveData<>();
    private int n_casella_giocata = 1;
    private MemorizzaPartitaPrivata partita_in_corso;
    private PrivataDao privataDao;
    private boolean partita_finita = false;
    public MutableLiveData<Boolean> getVittoria_tavolino() {
        return vittoria_tavolino;
    }
    public MutableLiveData<Boolean> getSconfitta_tavolino() {
        return sconfitta_tavolino;
    }

    private MutableLiveData<Boolean> sconfitta_tavolino = new MutableLiveData<>();
    private MutableLiveData<Boolean> vittoria_tavolino = new MutableLiveData<>();
    CountDownTimer abbandono_timer;
    private static final int nTHREADS = 6;
    static final ExecutorService firebaseExecutor = Executors.newFixedThreadPool(nTHREADS);

    public ServerPrivata(Application application){
        super(application);
        PrivataRoomDatabase db = PrivataRoomDatabase.getDatabase(application);
        privataDao = db.privataDao();
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
        giocatore2 = "";
        vittoria_tavolino.setValue(false);
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
                    databaseReference.child(nome_partita).child("vittoria_tavolino").setValue(giocatore1);
                    databaseReference.child(nome_partita).removeEventListener(turnoEventListener);
                    databaseReference.child(nome_partita).child("Giocatori").child(giocatore2).removeEventListener(ultimaMossaEventListener);
                    databaseReference.child(nome_partita).removeEventListener(esitoPartitaEventListener);
                       databaseReference.child(nome_partita).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               databaseReference2.child(nome_partita).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void unused) {
                                       vittoria_tavolino.setValue(true);
                                   }
                               });
                           }
                       });



                }
            }
        };
        firebaseExecutor.execute(()->{
            vittoriaTavolinoEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild("vittoria_tavolino")){
                        if(snapshot.child("vittoria_tavolino").getValue().toString().equals(giocatore1)){
                            abbandono_timer.cancel();
                            databaseReference.child(nome_partita).removeEventListener(turnoEventListener);
                            databaseReference.child(nome_partita).child("Giocatori").child(giocatore2).removeEventListener(ultimaMossaEventListener);
                            databaseReference.child(nome_partita).removeEventListener(esitoPartitaEventListener);
                            databaseReference.child(nome_partita).removeEventListener(this);
                            databaseReference.child(nome_partita).removeValue();
                            vittoria_tavolino.setValue(true);

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
        });
        sconfittaTavolinoEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("vittoria_tavolino")){
                    if(snapshot.child("vittoria_tavolino").getValue().toString().equals(giocatore2)){
                        databaseReference.child(nome_partita).removeEventListener(turnoEventListener);
                        databaseReference.child(nome_partita).child("Giocatori").child(giocatore2).removeEventListener(ultimaMossaEventListener);
                        databaseReference.child(nome_partita).removeEventListener(esitoPartitaEventListener);
                        databaseReference.child(nome_partita).removeEventListener(this);
                        sconfitta_tavolino.setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        firebaseExecutor.execute(() -> {
            ultimaMossaEventListener = new ChildEventListener(){


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
            esitoPartitaEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild("esito_partita")){
                        if(snapshot.child("esito_partita").child("esito").hasChild("vittoria")) {
                            if (snapshot.child("esito_partita").child("esito").child("vittoria").getValue().toString().equals(giocatore1)) {
                                partita_finita = true;
                                abbandono_timer.cancel();
                                databaseReference.child(nome_partita).removeEventListener(turnoEventListener);
                                databaseReference.child(nome_partita).child("Giocatori").child(giocatore2).removeEventListener(ultimaMossaEventListener);
                                databaseReference.child(nome_partita).removeValue();
                                databaseReference2.child(nome_partita).removeValue();
                                databaseReference.child(nome_partita).removeEventListener(this);
                                vittoriag1.setValue(true);
                            } else if (snapshot.child("esito_partita").child("esito").child("vittoria").getValue().toString().equals(giocatore2)) {
                                partita_finita = true;
                                abbandono_timer.cancel();
                                databaseReference.child(nome_partita).removeEventListener(turnoEventListener);
                                databaseReference.child(nome_partita).child("Giocatori").child(giocatore2).removeEventListener(ultimaMossaEventListener);
                                databaseReference.child(nome_partita).removeValue();
                                databaseReference2.child(nome_partita).removeValue();
                                databaseReference.child(nome_partita).removeEventListener(this);
                                vittoriag2.setValue(true);
                            }
                        }else if(snapshot.child("esito_partita").child("esito").getValue().toString().equals("pareggio")){
                            partita_finita = true;
                            abbandono_timer.cancel();
                            databaseReference.child(nome_partita).removeEventListener(turnoEventListener);
                            databaseReference.child(nome_partita).child("Giocatori").child(giocatore2).removeEventListener(ultimaMossaEventListener);
                            databaseReference.child(nome_partita).removeValue();
                            databaseReference2.child(nome_partita).removeValue();
                            databaseReference.child(nome_partita).removeEventListener(this);
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
            if (snapshot.hasChild("turno_giocatore")) {
                String turnogiocatore = snapshot.child("turno_giocatore").getValue().toString();
                turno_giocatore.setValue(turnogiocatore);


            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
});




   firebaseExecutor.execute(()->{

       databaseReference.addValueEventListener(new ValueEventListener() {

           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (!nome_partita.equals("")) {
                   PrivataRoomDatabase.databaseWriteExecutor.execute(()->{
                       partita_in_corso = new MemorizzaPartitaPrivata(nome_partita);
                       memorizza_partita(partita_in_corso);
                   });
                   if(snapshot.child(nome_partita).hasChild("Giocatori")) {
                       Log.d("A", "A");
                       Log.d("CHILDREN COUNT", String.valueOf(snapshot.child(nome_partita).child("Giocatori").getChildrenCount()));
                       if (snapshot.child(nome_partita).child("Giocatori").getChildrenCount() == 2) {
                           Log.d("AA", "AA");
                           for (DataSnapshot giocatori : snapshot.child(nome_partita).child("Giocatori").getChildren()) {
                               if (!giocatori.getKey().equals(giocatore1)) {
                                   Log.d("AAA", "AAA");
                                   databaseReference.removeEventListener(this);
                                   giocatore2 = giocatori.getKey();
                                   databaseReference.child(nome_partita).child("Giocatori").child(giocatore2).addChildEventListener(ultimaMossaEventListener);
                                   setInizio_partita();
                                   databaseReference.child(nome_partita).addValueEventListener(esitoPartitaEventListener);
                                   databaseReference.child(nome_partita).addValueEventListener(turnoEventListener);
                                   databaseReference.child(nome_partita).addValueEventListener(sconfittaTavolinoEventListener);
                                   databaseReference.child(nome_partita).addValueEventListener(vittoriaTavolinoEventListener);
                                   break;
                               }
                           }
                       }
                   }


               }
           }
           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
   });




    }
    public void setNome_host(String nome_host) {
        this.nome_host = nome_host;
    }
    public MutableLiveData<String> getTurno_giocatore() {
        return turno_giocatore;
    }
    public void setNome_partita(String nome_partita) {
        this.nome_partita = nome_partita;
    }

    public String getNome_partita() {
        return nome_partita;
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

    public void setGiocatore1(String giocatore1) {
        this.giocatore1 = giocatore1;
    }


    public String getGiocatore1() {

        return giocatore1;
    }

    public String getGiocatore2() {

        return giocatore2;
    }
    public void setTurno_giocatore(String turnogiocatore) {
        if(!ho_vinto(giocatore1) || !ho_vinto(giocatore2) || caselle_giocate.size() != 9) {
            firebaseExecutor.execute(()->{
                databaseReference.child(nome_partita).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("turno_giocatore")) {
                            databaseReference.child(nome_partita).child("turno_giocatore").setValue(turnogiocatore);
                            databaseReference.child(nome_partita).removeEventListener(this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            });

        }
    }
    public void setInizio_partita() {
        inizio_partita.setValue(true);
    }
    public MutableLiveData<Boolean> isInizio_partita() {

        return inizio_partita;
    }



    public void gioca_casella(int casella, String giocatore) {
        if(!caselle_giocate.contains(casella)){
            if(giocatore == giocatore1){
                caselle_giocatore1[casella] = giocatore;
            } else {
                caselle_giocatore2[casella] = giocatore;
            }
            caselle_giocate.add(casella);
            firebaseExecutor.execute(()->{
                databaseReference.child(nome_partita).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        n_casella_giocata = (int) snapshot.child("Giocatori").child(giocatore).getChildrenCount();
                        databaseReference.child(nome_partita).child("Giocatori").child(giocatore).child("mossa "+String.valueOf(n_casella_giocata)).setValue(casella).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if(ho_vinto(giocatore1) == false && ho_vinto(giocatore2)== false && caselle_giocate.size()== 9) {
                                    databaseReference.child(nome_partita).child("esito_partita").child("esito").setValue("pareggio");
                                }
                                if(ho_vinto(giocatore1)){
                                    databaseReference.child(nome_partita).child("esito_partita").child("esito").child("vittoria").setValue(giocatore1);

                                } else {
                                    setTurno_giocatore(giocatore2);
                                }
                            }
                        });
                        databaseReference.child(nome_partita).removeEventListener(this);
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


    public MutableLiveData <String> ultima_mossa(){

        return ultima_mossa;

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
    public void abbandona(String giocatore){

        if(getGiocatore2().equals("")){
            databaseReference.child(nome_partita).removeValue();
        } else {
            if (giocatore.equals(giocatore1)) {
                databaseReference.child(nome_partita).child("vittoria_tavolino").setValue(giocatore2);
            } else {
                databaseReference.child(nome_partita).child("vittoria_tavolino").setValue(giocatore1);
            }
        }
    }

    public void memorizza_partita(MemorizzaPartitaPrivata memorizzaPartitaPrivata){
        PrivataRoomDatabase.databaseWriteExecutor.execute(()->{
            Log.d("MEMORIZZA PARTITA", partita_in_corso.getNome_partita());
            privataDao.insert(memorizzaPartitaPrivata);
        });
    }


    public void elimina_partita(String nome_partita){
        PrivataRoomDatabase.databaseWriteExecutor.execute(()->{
            privataDao.elimina_partita(nome_partita);
        });
    }

    public boolean casella_libera(int casella){
        if(!caselle_giocate.contains(casella)){
            return true;
        }
        return false;
    }


    public void vittoria_tavolino_timer(){
        abbandono_timer.start();
    }


}