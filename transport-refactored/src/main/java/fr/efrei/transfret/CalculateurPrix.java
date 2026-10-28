package fr.efrei.transfret;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CalculateurPrix {

    private static final double SUPPLEMENT_FRAGILE = 1.15;
    private static final double SUPPLEMENT_DANGEREUX = 1.30;
    private static final double SUPPLEMENT_REFRIGERE = 1.20;
    private static final double SUPPLEMENT_URGENCE = 1.5;
    private static final double TAUX_ASSURANCE = 0.02;
    private static final double ASSURANCE_MINIMUM = 15;

    private final Map<ModeTransport, TarifTransport> tarifsParMode;
    private final Map<TypeClient, PolitiqueRemise> remisesParTypeClient;

    public CalculateurPrix(Map<ModeTransport, TarifTransport> tarifsParMode,
                           Map<TypeClient, PolitiqueRemise> remisesParTypeClient) {
        this.tarifsParMode = Map.copyOf(tarifsParMode);
        this.remisesParTypeClient = Map.copyOf(remisesParTypeClient);
        for (ModeTransport mode : ModeTransport.values()) {
            Objects.requireNonNull(this.tarifsParMode.get(mode), "Tarif manquant pour " + mode);
        }
        for (TypeClient typeClient : TypeClient.values()) {
            Objects.requireNonNull(this.remisesParTypeClient.get(typeClient), "Remise manquante pour " + typeClient);
        }
    }

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

        TarifTransport tarif = tarifsParMode.get(Objects.requireNonNull(mode, "mode"));
        double prix = tarif.calculer(distance, poids, volume);

        // suppléments
        if (fragile) prix = prix * SUPPLEMENT_FRAGILE;
        if (dangereux) prix = prix * SUPPLEMENT_DANGEREUX;
        if (refrigere) prix = prix * SUPPLEMENT_REFRIGERE;
        if (urgent) prix = prix * SUPPLEMENT_URGENCE;

        prix = remisesParTypeClient.get(Objects.requireNonNull(typeClient, "typeClient")).appliquer(prix);

        // assurance
        if (assurance) {
            double montantAssurance = valeur * TAUX_ASSURANCE;
            if (montantAssurance < ASSURANCE_MINIMUM) montantAssurance = ASSURANCE_MINIMUM;
            prix = prix + montantAssurance;
        }
        return Utils.arrondir(prix);
    }
}
