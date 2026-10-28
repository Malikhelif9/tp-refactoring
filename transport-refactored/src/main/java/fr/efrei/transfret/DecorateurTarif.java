package fr.efrei.transfret;

import java.util.Objects;

public abstract class DecorateurTarif implements TarifTransport {

    private final TarifTransport enveloppe;

    protected DecorateurTarif(TarifTransport enveloppe) {
        this.enveloppe = Objects.requireNonNull(enveloppe);
    }

    @Override
    public final double calculer(ParametresTarification parametres) {
        return appliquer(parametres, enveloppe.calculer(parametres));
    }

    protected abstract double appliquer(ParametresTarification parametres, double prix);
}
