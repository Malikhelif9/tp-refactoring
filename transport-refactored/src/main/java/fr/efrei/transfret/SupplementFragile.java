package fr.efrei.transfret;

public class SupplementFragile extends SupplementMultiplicatif {

    private static final double COEFFICIENT = 1.15;

    public SupplementFragile(TarifTransport enveloppe) {
        super(enveloppe, COEFFICIENT);
    }

    @Override
    protected boolean estApplicable(ParametresTarification parametres) {
        return parametres.contient(TypeMarchandise.FRAGILE);
    }
}
