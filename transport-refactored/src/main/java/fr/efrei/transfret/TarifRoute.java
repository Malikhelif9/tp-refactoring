package fr.efrei.transfret;

public class TarifRoute extends TarifLineaire {

    private static final double PRIX_PAR_KM = 1.2;
    private static final double PRIX_PAR_KG = 0.10;

    public TarifRoute() {
        super(PRIX_PAR_KM, PRIX_PAR_KG, 0);
    }
}
