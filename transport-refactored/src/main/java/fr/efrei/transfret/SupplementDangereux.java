package fr.efrei.transfret;

public class SupplementDangereux extends SupplementMultiplicatif {

    private static final double COEFFICIENT = 1.30;

    public SupplementDangereux(TarifTransport enveloppe) {
        super(enveloppe, COEFFICIENT);
    }

    @Override
    protected boolean estApplicable(ParametresTarification parametres) {
        return parametres.contient(TypeMarchandise.DANGEREUX);
    }
}
