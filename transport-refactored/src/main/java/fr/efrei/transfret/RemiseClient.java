package fr.efrei.transfret;

import java.util.Map;
import java.util.Objects;

public class RemiseClient extends DecorateurTarif {

    private final Map<TypeClient, PolitiqueRemise> politiques;

    public RemiseClient(TarifTransport enveloppe, Map<TypeClient, PolitiqueRemise> politiques) {
        super(enveloppe);
        this.politiques = Map.copyOf(politiques);
        for (TypeClient typeClient : TypeClient.values()) {
            Objects.requireNonNull(this.politiques.get(typeClient), "Remise manquante pour " + typeClient);
        }
    }

    @Override
    protected double appliquer(ParametresTarification parametres, double prix) {
        return politiques.get(parametres.typeClient()).appliquer(prix);
    }
}
