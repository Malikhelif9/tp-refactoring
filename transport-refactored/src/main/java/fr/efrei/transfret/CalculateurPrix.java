package fr.efrei.transfret;

import java.util.List;

public class CalculateurPrix {

    private final AssembleurTarif assembleurTarif;

    public CalculateurPrix(AssembleurTarif assembleurTarif) {
        this.assembleurTarif = assembleurTarif;
    }

    public double calculerPrixHT(List<Colis> colis, int distance, ModeTransport mode, boolean urgent,
                                 boolean assurance, TypeClient typeClient) {
        double poids = 0;
        double volume = 0;
        double valeur = 0;
        for (Colis c : colis) {
            poids = poids + c.getPoids();
            volume = volume + c.getVolume();
            valeur = valeur + c.getValeurDeclaree();
        }

        ParametresTarification parametres = new ParametresTarification(
                colis, distance, poids, volume, valeur, urgent, assurance, typeClient);
        double prix = assembleurTarif.assembler(mode).calculer(parametres);
        return Utils.arrondir(prix);
    }
}
