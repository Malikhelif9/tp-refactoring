package fr.efrei.transfret;

import java.util.List;

public class Camion extends Vehicule {

    protected boolean adr; // certification matières dangereuses

    public Camion(String immatriculation, double capaciteMax, boolean adr) {
        super(immatriculation, capaciteMax);
        this.adr = adr;
    }

    @Override
    public boolean peutPrendreEnCharge(ModeTransport mode, List<Colis> colis) {
        return mode == ModeTransport.ROUTE
                && !contient(colis, TypeMarchandise.REFRIGERE)
                && (!contient(colis, TypeMarchandise.DANGEREUX) || adr);
    }

    @Override
    public void partir(int distanceKm) {
        System.out.println("Le camion " + immatriculation + " roule sur " + distanceKm + " km");
    }
}
