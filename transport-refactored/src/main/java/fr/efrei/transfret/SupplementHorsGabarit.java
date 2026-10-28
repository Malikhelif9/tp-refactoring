package fr.efrei.transfret;

public class SupplementHorsGabarit extends SupplementMultiplicatif {

    private static final double DIMENSION_MAXIMALE_CM = 200;
    private static final double COEFFICIENT = 1.10;

    public SupplementHorsGabarit(TarifTransport enveloppe) {
        super(enveloppe, COEFFICIENT);
    }

    @Override
    protected boolean estApplicable(ParametresTarification parametres) {
        return parametres.colis().stream().anyMatch(colis ->
                colis.getLongueur() > DIMENSION_MAXIMALE_CM
                        || colis.getLargeur() > DIMENSION_MAXIMALE_CM
                        || colis.getHauteur() > DIMENSION_MAXIMALE_CM);
    }
}
