package fr.efrei.transfret;

import java.util.List;

public class CalculateurPrix {

    private static final double ROUTE_PRIX_PAR_KM = 1.2;
    private static final double ROUTE_PRIX_PAR_KG = 0.10;
    private static final double RAIL_PRIX_PAR_KM = 0.8;
    private static final double RAIL_PRIX_PAR_KG = 0.07;
    private static final double RAIL_PRIX_MINIMUM = 150;
    private static final double MER_PRIX_PAR_KM = 0.5;
    private static final double MER_PRIX_PAR_KG = 0.03;
    private static final double MER_PRIX_MINIMUM = 500;
    private static final double AIR_PRIX_PAR_KM = 0.6;
    private static final double AIR_PRIX_PAR_KG_TAXABLE = 4.5;
    private static final double KILOGRAMMES_TAXABLES_PAR_METRE_CUBE = 167;
    private static final double SUPPLEMENT_FRAGILE = 1.15;
    private static final double SUPPLEMENT_DANGEREUX = 1.30;
    private static final double SUPPLEMENT_REFRIGERE = 1.20;
    private static final double SUPPLEMENT_URGENCE = 1.5;
    private static final double SEUIL_REMISE_ENTREPRISE = 5000;
    private static final double REMISE_ENTREPRISE_STANDARD = 0.90;
    private static final double REMISE_ENTREPRISE_GROS_VOLUME = 0.85;
    private static final double REMISE_PREMIUM = 0.80;
    private static final double TAUX_ASSURANCE = 0.02;
    private static final double ASSURANCE_MINIMUM = 15;

    public double calculerPrixHT(List<Colis> colis, int distance, ModeTransport mode, boolean urgent,
                                 boolean assurance, TypeClient typeClient) {
        double poids = 0;
        double volume = 0;
        double valeur = 0;
        boolean fragile = false;
        boolean dangereux = false;
        boolean refrigere = false;
        for (Colis c : colis) {
            poids = poids + c.getPoids();
            volume = volume + c.getVolume();
            valeur = valeur + c.getValeurDeclaree();
            if (c.getType() == TypeMarchandise.FRAGILE) fragile = true;
            if (c.getType() == TypeMarchandise.DANGEREUX) dangereux = true;
            if (c.getType() == TypeMarchandise.REFRIGERE) refrigere = true;
        }

        // tarif de base selon le mode
        double prix;
        if (mode == ModeTransport.ROUTE) {
            prix = distance * ROUTE_PRIX_PAR_KM + poids * ROUTE_PRIX_PAR_KG;
        } else if (mode == ModeTransport.RAIL) {
            prix = distance * RAIL_PRIX_PAR_KM + poids * RAIL_PRIX_PAR_KG;
            if (prix < RAIL_PRIX_MINIMUM) prix = RAIL_PRIX_MINIMUM;
        } else if (mode == ModeTransport.MER) {
            prix = distance * MER_PRIX_PAR_KM + poids * MER_PRIX_PAR_KG;
            if (prix < MER_PRIX_MINIMUM) prix = MER_PRIX_MINIMUM;
        } else if (mode == ModeTransport.AIR) {
            double poidsTaxable = poids;
            if (volume * KILOGRAMMES_TAXABLES_PAR_METRE_CUBE > poids) {
                poidsTaxable = volume * KILOGRAMMES_TAXABLES_PAR_METRE_CUBE;
            }
            prix = distance * AIR_PRIX_PAR_KM + poidsTaxable * AIR_PRIX_PAR_KG_TAXABLE;
        } else {
            throw new IllegalArgumentException("Mode inconnu : " + mode);
        }

        // suppléments
        if (fragile) prix = prix * SUPPLEMENT_FRAGILE;
        if (dangereux) prix = prix * SUPPLEMENT_DANGEREUX;
        if (refrigere) prix = prix * SUPPLEMENT_REFRIGERE;
        if (urgent) prix = prix * SUPPLEMENT_URGENCE;

        // remise client
        if (typeClient == TypeClient.ENTREPRISE) {
            if (prix > SEUIL_REMISE_ENTREPRISE) {
                prix = prix * REMISE_ENTREPRISE_GROS_VOLUME;
            } else {
                prix = prix * REMISE_ENTREPRISE_STANDARD;
            }
        } else if (typeClient == TypeClient.PREMIUM) {
            prix = prix * REMISE_PREMIUM;
        }

        // assurance
        if (assurance) {
            double montantAssurance = valeur * TAUX_ASSURANCE;
            if (montantAssurance < ASSURANCE_MINIMUM) montantAssurance = ASSURANCE_MINIMUM;
            prix = prix + montantAssurance;
        }
        return Utils.arrondir(prix);
    }
}
