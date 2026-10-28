package fr.efrei.transfret;

import java.util.Map;

public final class ConfigurationTarification {

    private ConfigurationTarification() {
    }

    public static CalculateurPrix creerCalculateurPrix() {
        return creerCalculateurPrix(false);
    }

    public static CalculateurPrix creerCalculateurPrixAvecSupplementHorsGabarit() {
        return creerCalculateurPrix(true);
    }

    private static CalculateurPrix creerCalculateurPrix(boolean supplementHorsGabarit) {
        Map<ModeTransport, TarifTransport> tarifs = Map.of(
                ModeTransport.ROUTE, new TarifRoute(),
                ModeTransport.RAIL, new TarifRail(),
                ModeTransport.MER, new TarifMer(),
                ModeTransport.AIR, new TarifAir(),
                ModeTransport.FLUVIAL, new TarifFluvial());
        Map<TypeClient, PolitiqueRemise> remises = Map.of(
                TypeClient.PARTICULIER, new AucuneRemise(),
                TypeClient.ENTREPRISE, new RemiseEntreprise(),
                TypeClient.PREMIUM, new RemisePremium());
        return new CalculateurPrix(new AssembleurTarif(tarifs, remises, supplementHorsGabarit));
    }
}
