package fr.efrei.transfret;

import java.util.List;

public class Bateau extends Vehicule {

    private static final double KM_PAR_MILLE_NAUTIQUE = 1.852;
    private boolean conteneursRefrigeres;

    public Bateau(String immatriculation, double capaciteMax, boolean conteneursRefrigeres) {
        super(immatriculation, capaciteMax);
        this.conteneursRefrigeres = conteneursRefrigeres;
    }

    @Override
    public boolean peutPrendreEnCharge(ModeTransport mode, List<Colis> colis) {
        return mode == ModeTransport.MER
                && (!contient(colis, TypeMarchandise.REFRIGERE) || conteneursRefrigeres);
    }

    @Override
    public void partir(int distanceKm) {
        int milesNautiques = (int) (distanceKm / KM_PAR_MILLE_NAUTIQUE);
        System.out.println("Le navire " + immatriculation + " navigue sur " + milesNautiques + " milles nautiques");
    }

    @Override
    public void preparerExpedition(List<Colis> colis, double temperatureRefrigeration) {
        if (conteneursRefrigeres && contient(colis, TypeMarchandise.REFRIGERE)) {
            System.out.println("Conteneurs frigorifiques réglés à " + temperatureRefrigeration + "°C");
        }
    }
}
