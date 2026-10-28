package fr.efrei.transfret;

public class TarifMer extends TarifLineaire {

    private static final double PRIX_PAR_KM = 0.5;
    private static final double PRIX_PAR_KG = 0.03;
    private static final double MINIMUM_FACTURATION = 500;

    public TarifMer() {
        super(PRIX_PAR_KM, PRIX_PAR_KG, MINIMUM_FACTURATION);
    }
}
