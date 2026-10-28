package fr.efrei.transfret;

public class Assurance extends DecorateurTarif {

    private static final double TAUX_ASSURANCE = 0.02;
    private static final double ASSURANCE_MINIMUM = 15;

    public Assurance(TarifTransport enveloppe) {
        super(enveloppe);
    }

    @Override
    protected double appliquer(ParametresTarification parametres, double prix) {
        if (!parametres.assurance()) {
            return prix;
        }
        double montantAssurance = Math.max(
                ASSURANCE_MINIMUM, parametres.valeurDeclaree() * TAUX_ASSURANCE);
        return prix + montantAssurance;
    }
}
