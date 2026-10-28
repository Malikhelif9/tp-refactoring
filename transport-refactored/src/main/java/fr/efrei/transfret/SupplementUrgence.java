package fr.efrei.transfret;

public class SupplementUrgence extends SupplementMultiplicatif {

    private static final double COEFFICIENT = 1.5;

    public SupplementUrgence(TarifTransport enveloppe) {
        super(enveloppe, COEFFICIENT);
    }

    @Override
    protected boolean estApplicable(ParametresTarification parametres) {
        return parametres.urgent();
    }
}
