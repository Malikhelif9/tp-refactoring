package fr.efrei.transfret;

import java.util.List;

public class Train extends Vehicule {

    public Train(String immatriculation, double capaciteMax) {
        super(immatriculation, capaciteMax);
    }

    @Override
    public boolean peutPrendreEnCharge(ModeTransport mode, List<Colis> colis) {
        return mode == ModeTransport.RAIL && !contient(colis, TypeMarchandise.REFRIGERE);
    }

    @Override
    public void partir(int distanceKm) {
        System.out.println("Le train " + immatriculation + " circule sur " + distanceKm + " km de voies ferrées");
    }
}
