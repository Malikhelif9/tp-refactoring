package fr.efrei.transfret;

public class TarifRail extends TarifLineaire {

    private static final double PRIX_PAR_KM = 0.8;
    private static final double PRIX_PAR_KG = 0.07;
    private static final double MINIMUM_FACTURATION = 150;

    public TarifRail() {
        super(PRIX_PAR_KM, PRIX_PAR_KG, MINIMUM_FACTURATION);
    }
}
