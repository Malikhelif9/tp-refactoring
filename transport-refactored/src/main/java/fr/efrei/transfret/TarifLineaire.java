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
    public double calculer(double distanceKm, double poidsKg, double volumeM3) {
        return Math.max(minimum, distanceKm * prixParKm + poidsKg * prixParKg);
    }
}
