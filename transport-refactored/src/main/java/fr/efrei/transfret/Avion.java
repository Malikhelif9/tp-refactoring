package fr.efrei.transfret;

import java.util.List;

public class Avion extends Vehicule {

    private static final int DISTANCE_ROULAGE_PISTE_KM = 2;
    private static final int ALTITUDE_CROISIERE_PIEDS = 35000;

    public Avion(String immatriculation, double capaciteMax) {
        super(immatriculation, capaciteMax);
    }

    @Override
    public boolean peutPrendreEnCharge(ModeTransport mode, List<Colis> colis) {
        return mode == ModeTransport.AIR
                && !contient(colis, TypeMarchandise.DANGEREUX)
                && !contient(colis, TypeMarchandise.REFRIGERE);
    }

    @Override
    public void partir(int distanceKm) {
        System.out.println("L'avion " + immatriculation + " roule sur " + DISTANCE_ROULAGE_PISTE_KM + " km de piste");
        System.out.println("L'avion " + immatriculation + " vole à " + ALTITUDE_CROISIERE_PIEDS + " pieds");
    }
}
