package fr.efrei.transfret;

abstract class TarifLineaire implements TarifTransport {

    private final double prixParKm;
    private final double prixParKg;
    private final double minimum;

    TarifLineaire(double prixParKm, double prixParKg, double minimum) {
        this.prixParKm = prixParKm;
        this.prixParKg = prixParKg;
        this.minimum = minimum;
    }

    @Override
    public double calculer(ParametresTarification parametres) {
        return Math.max(minimum,
                parametres.distanceKm() * prixParKm + parametres.poidsKg() * prixParKg);
    }
}
