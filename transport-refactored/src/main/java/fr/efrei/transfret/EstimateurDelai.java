package fr.efrei.transfret;

public class EstimateurDelai {

    private static final double VITESSE_ROUTE_KMH = 70.0;
    private static final double HEURES_ROUTE_PAR_JOUR = 9;
    private static final double HEURES_ROUTE_URGENTE_PAR_JOUR = 18;
    private static final double KM_RAIL_PAR_JOUR = 1440.0;
    private static final int JOURS_MANUTENTION_RAIL = 1;
    private static final double KM_MER_PAR_JOUR = 720.0;
    private static final int JOURS_MANUTENTION_MER = 2;
    private static final int DISTANCE_AIR_LONG_COURRIER_KM = 3000;
    private static final int JOURS_AIR_COURT_COURRIER = 1;
    private static final int JOURS_AIR_LONG_COURRIER = 2;
    private static final double KM_FLUVIAUX_PAR_JOUR = 360.0;
    private static final int JOURS_MANUTENTION_FLUVIALE = 1;
    private static final int JOURS_LIVRAISON_MINIMUM = 1;
    private static final int JOURS_RETRAIT_URGENCE = 1;

    public int calculerDelai(int distance, ModeTransport mode, boolean urgent) {
        int jours = 0;
        switch (mode) {
            case ROUTE:
                double heures = distance / VITESSE_ROUTE_KMH;
                if (urgent) {
                    jours = (int) Math.ceil(heures / HEURES_ROUTE_URGENTE_PAR_JOUR);
                } else {
                    jours = (int) Math.ceil(heures / HEURES_ROUTE_PAR_JOUR);
                }
                break;
            case RAIL:
                jours = (int) Math.ceil(distance / KM_RAIL_PAR_JOUR) + JOURS_MANUTENTION_RAIL;
                if (urgent) jours = jours - JOURS_RETRAIT_URGENCE;
                break;
            case MER:
                jours = (int) Math.ceil(distance / KM_MER_PAR_JOUR) + JOURS_MANUTENTION_MER;
                break;
            case AIR:
                jours = JOURS_AIR_COURT_COURRIER;
                if (distance > DISTANCE_AIR_LONG_COURRIER_KM) jours = JOURS_AIR_LONG_COURRIER;
                if (urgent) jours = JOURS_AIR_COURT_COURRIER;
                break;
            case FLUVIAL:
                jours = (int) Math.ceil(distance / KM_FLUVIAUX_PAR_JOUR) + JOURS_MANUTENTION_FLUVIALE;
                break;
        }
        if (jours < JOURS_LIVRAISON_MINIMUM) jours = JOURS_LIVRAISON_MINIMUM;
        return jours;
    }
}
