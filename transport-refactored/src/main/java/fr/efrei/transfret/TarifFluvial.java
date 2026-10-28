package fr.efrei.transfret;

public class TarifFluvial extends TarifLineaire {

    private static final double PRIX_PAR_KM = 0.40;
    private static final double PRIX_PAR_KG = 0.02;
    private static final double MINIMUM_FACTURATION = 300;

    public TarifFluvial() {
        super(PRIX_PAR_KM, PRIX_PAR_KG, MINIMUM_FACTURATION);
    }
}
