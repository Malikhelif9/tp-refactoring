package fr.efrei.transfret;

public abstract class SupplementMultiplicatif extends DecorateurTarif {

    private final double coefficient;

    protected SupplementMultiplicatif(TarifTransport enveloppe, double coefficient) {
        super(enveloppe);
        this.coefficient = coefficient;
    }

    protected abstract boolean estApplicable(ParametresTarification parametres);

    @Override
    protected final double appliquer(ParametresTarification parametres, double prix) {
        return estApplicable(parametres) ? prix * coefficient : prix;
    }
}
