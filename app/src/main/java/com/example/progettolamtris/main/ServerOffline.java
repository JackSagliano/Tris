package com.example.progettolamtris.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServerOffline extends AndroidViewModel {
    MutableLiveData<String> turno_giocatore = new MutableLiveData<>();
    private final List<String> caselle_giocate = new ArrayList<>();
    private final String[] caselle_giocatore1 = {"", "", "", "", "", "", "", "", ""}; //caselle giocate dal giocatore 1
    private final String[] caselle_giocatore2 = {"", "", "", "", "", "", "", "", ""};//caselle giocate dal giocatore 2
    private final List<int[]> combinazioni_vittoria = new ArrayList<>();
    private MutableLiveData<Boolean> vittoriag1= new MutableLiveData<>();
    private MutableLiveData<Boolean> vittoriag2= new MutableLiveData<>();
    private MutableLiveData<Boolean> pareggio= new MutableLiveData<>();

    private String giocatore1;
    private String giocatore2;

    public ServerOffline(@NonNull Application application) {
        super(application);
        this.combinazioni_vittoria.add(new int[]{0,1,2});
        this.combinazioni_vittoria.add(new int[]{0,3,6});
        this.combinazioni_vittoria.add(new int[]{0,4,8});
        this.combinazioni_vittoria.add(new int[]{1,4,7});
        this.combinazioni_vittoria.add(new int[]{2,4,6});
        this.combinazioni_vittoria.add(new int[]{2,5,8});
        this.combinazioni_vittoria.add(new int[]{3,4,5});
        this.combinazioni_vittoria.add(new int[]{6,7,8});
        vittoriag1.setValue(false);
        vittoriag2.setValue(false);
        pareggio.setValue(false);
    }

    public void setGiocatore1(String giocatore1) {
        this.giocatore1 = giocatore1;
    }

    public void setGiocatore2(String giocatore2) {
        this.giocatore2 = giocatore2;
    }

    public List<String> getCaselle_giocate() {
        return caselle_giocate;
    }

    public MutableLiveData<String> getTurno_giocatore() {
        return turno_giocatore;
    }

    public void setTurno_giocatore(String turno) {
        this.turno_giocatore.setValue(turno);
    }

    public void inizioPartita(String giocatore1, String giocatore2){
        final int random = new Random().nextInt(2);
        if(random ==0){
            turno_giocatore.setValue(giocatore1);
        } else {
            turno_giocatore.setValue(giocatore2);
        }
    }
    public boolean casella_libera(int casella){
        if(!caselle_giocate.contains(String.valueOf(casella))){
            return true;
        }
        return false;
    }

    public void gioca_casella(int casella, String giocatore) {
            if(giocatore.equals(giocatore1)){
                caselle_giocatore1[casella] = giocatore;
            } else {
                caselle_giocatore2[casella] = giocatore;
            }
            caselle_giocate.add(String.valueOf(casella));
            if(ho_vinto(giocatore1)){
                setVittoriag1(true);
            } else if(ho_vinto(giocatore2)){
                setVittoriag2(true);
            } else if(ho_vinto(giocatore1) == false && ho_vinto(giocatore2)== false && caselle_giocate.size()== 9) {
                setPareggio(true);
            } else {
                if(giocatore.equals(giocatore1)){
                    setTurno_giocatore(giocatore2);
                } else {
                    setTurno_giocatore(giocatore1);
                }
            }




    }
    public void prova_casella(int casella){
        caselle_giocatore2[casella] = giocatore2;
    }
    public void prova_casella_avversaria(int casella){
        caselle_giocatore1[casella] = giocatore1;
    }
    public void annulla_casella(int casella){
        caselle_giocatore2[casella] = "";

    }
    public void annulla_casella_avversaria(int casella){
        caselle_giocatore1[casella] = "";

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
    public MutableLiveData<Boolean> getVittoriag1() {
        return vittoriag1;
    }

    public void setVittoriag1(boolean bool) {
        this.vittoriag1.setValue(bool);
    }

    public MutableLiveData<Boolean> getVittoriag2() {
        return vittoriag2;
    }

    public void setVittoriag2(boolean bool) {
        this.vittoriag2.setValue(bool);
    }

    public MutableLiveData<Boolean> getPareggio() {
        return pareggio;
    }

    public void setPareggio(boolean bool) {
        this.pareggio.setValue(bool);
    }
}
