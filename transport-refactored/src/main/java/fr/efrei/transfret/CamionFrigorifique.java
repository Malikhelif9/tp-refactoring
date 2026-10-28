package fr.efrei.transfret;

import java.util.List;

public class CamionFrigorifique extends Camion {

    public CamionFrigorifique(String immatriculation, double capaciteMax) {
        super(immatriculation, capaciteMax, false);
    }

    @Override
    public boolean peutPrendreEnCharge(ModeTransport mode, List<Colis> colis) {
        return mode == ModeTransport.ROUTE
                && contient(colis, TypeMarchandise.REFRIGERE)
                && (!contient(colis, TypeMarchandise.DANGEREUX) || adr);
    }

    @Override
    public void preparerExpedition(List<Colis> colis, double temperatureRefrigeration) {
        if (contient(colis, TypeMarchandise.REFRIGERE)) {
            System.out.println("Réfrigération activée à " + temperatureRefrigeration + "°C sur " + immatriculation);
        }
    }
}
