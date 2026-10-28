package fr.efrei.transfret;

import java.util.List;

public class Peniche extends Vehicule {

    public Peniche(String immatriculation, double capaciteMax) {
        super(immatriculation, capaciteMax);
    }

    @Override
    public boolean peutPrendreEnCharge(ModeTransport mode, List<Colis> colis) {
        return mode == ModeTransport.FLUVIAL
                && !contient(colis, TypeMarchandise.REFRIGERE);
    }

    @Override
    public void partir(int distanceKm) {
        System.out.println("La péniche " + immatriculation + " navigue sur " + distanceKm + " km de voies fluviales");
    }
}
