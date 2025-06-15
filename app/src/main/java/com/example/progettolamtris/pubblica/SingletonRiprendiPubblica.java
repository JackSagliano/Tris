package com.example.progettolamtris.pubblica;

import com.example.progettolamtris.privata.SingletonRiprendiPrivata;

import java.util.ArrayList;
import java.util.List;

public class SingletonRiprendiPubblica {
    private static SingletonRiprendiPubblica singletonRiprendiPubblica;
    private List<Integer> caselle_g1 = new ArrayList<>();
    private List<Integer> caselle_g2 = new ArrayList<>();

    public static SingletonRiprendiPubblica getSingletonRiprendiPubblica(){
        if(singletonRiprendiPubblica == null){
            singletonRiprendiPubblica = new SingletonRiprendiPubblica();
        }

        return singletonRiprendiPubblica;
    }

    public void setCaselle_g1(List<Integer> caselle_g1) {
        this.caselle_g1 = caselle_g1;
    }

    public void setCaselle_g2(List<Integer> caselle_g2) {
        this.caselle_g2 = caselle_g2;
    }

    public List<Integer> getCaselle_g1() {
        return caselle_g1;
    }

    public List<Integer> getCaselle_g2() {
        return caselle_g2;
    }
}
