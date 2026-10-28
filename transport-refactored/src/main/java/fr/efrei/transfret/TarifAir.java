package fr.efrei.transfret;

public class TarifAir implements TarifTransport {

    private static final double PRIX_PAR_KM = 0.6;
    private static final double PRIX_PAR_KG_TAXABLE = 4.5;
    private static final double KILOGRAMMES_TAXABLES_PAR_METRE_CUBE = 167;

    @Override
    public double calculer(double distanceKm, double poidsKg, double volumeM3) {
        double poidsTaxable = Math.max(poidsKg, volumeM3 * KILOGRAMMES_TAXABLES_PAR_METRE_CUBE);
        return distanceKm * PRIX_PAR_KM + poidsTaxable * PRIX_PAR_KG_TAXABLE;
    }
}
