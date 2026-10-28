package fr.efrei.transfret;

import java.util.List;
import java.util.Objects;

public record ParametresTarification(
        List<Colis> colis,
        int distanceKm,
        double poidsKg,
        double volumeM3,
        double valeurDeclaree,
        boolean urgent,
        boolean assurance,
        TypeClient typeClient) {

    public ParametresTarification {
        colis = List.copyOf(colis);
        Objects.requireNonNull(typeClient, "typeClient");
    }

    public boolean contient(TypeMarchandise type) {
        return colis.stream().anyMatch(colis -> colis.getType() == type);
    }
}
