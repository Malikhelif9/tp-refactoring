package fr.efrei.transfret;

public class SupplementRefrigere extends SupplementMultiplicatif {

    private static final double COEFFICIENT = 1.20;

    public SupplementRefrigere(TarifTransport enveloppe) {
        super(enveloppe, COEFFICIENT);
    }

    @Override
    protected boolean estApplicable(ParametresTarification parametres) {
        return parametres.contient(TypeMarchandise.REFRIGERE);
    }
}
