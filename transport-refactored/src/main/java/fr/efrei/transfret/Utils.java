package fr.efrei.transfret;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Utils {

    public static final int DISTANCE_INCONNUE = -1;

    private static final double CENTIMES_PAR_UNITE = 100.0;
    private static final int DISTANCE_PARIS_LYON_KM = 465;
    private static final int DISTANCE_PARIS_MARSEILLE_KM = 775;
    private static final int DISTANCE_PARIS_LILLE_KM = 225;
    private static final int DISTANCE_PARIS_BORDEAUX_KM = 585;
    private static final int DISTANCE_LYON_MARSEILLE_KM = 315;
    private static final int DISTANCE_LE_HAVRE_NEW_YORK_KM = 5850;
    private static final int DISTANCE_MARSEILLE_ALGER_KM = 760;
    private static final int DISTANCE_PARIS_NEW_YORK_KM = 5840;

    private static final DateTimeFormatter FORMAT_DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault());

    public static double arrondir(double valeur) {
        return Math.round(valeur * CENTIMES_PAR_UNITE) / CENTIMES_PAR_UNITE;
    }

    public static String formaterDate(Date d) {
        if (d == null) {
            return "";
        }
        return FORMAT_DATE.format(d.toInstant());
    }

    public static String formaterPrix(double prix) {
        return String.format("%.2f €", prix);
    }

    public static boolean emailValide(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    // retourne la distance en km entre deux villes, -1 si inconnue
    public static int getDistance(String villeA, String villeB) {
        String a = villeA.toUpperCase();
        String b = villeB.toUpperCase();
        if ((a.equals("PARIS") && b.equals("LYON")) || (a.equals("LYON") && b.equals("PARIS"))) {
            return DISTANCE_PARIS_LYON_KM;
        } else if ((a.equals("PARIS") && b.equals("MARSEILLE")) || (a.equals("MARSEILLE") && b.equals("PARIS"))) {
            return DISTANCE_PARIS_MARSEILLE_KM;
        } else if ((a.equals("PARIS") && b.equals("LILLE")) || (a.equals("LILLE") && b.equals("PARIS"))) {
            return DISTANCE_PARIS_LILLE_KM;
        } else if ((a.equals("PARIS") && b.equals("BORDEAUX")) || (a.equals("BORDEAUX") && b.equals("PARIS"))) {
            return DISTANCE_PARIS_BORDEAUX_KM;
        } else if ((a.equals("LYON") && b.equals("MARSEILLE")) || (a.equals("MARSEILLE") && b.equals("LYON"))) {
            return DISTANCE_LYON_MARSEILLE_KM;
        } else if ((a.equals("LE HAVRE") && b.equals("NEW YORK")) || (a.equals("NEW YORK") && b.equals("LE HAVRE"))) {
            return DISTANCE_LE_HAVRE_NEW_YORK_KM;
        } else if ((a.equals("MARSEILLE") && b.equals("ALGER")) || (a.equals("ALGER") && b.equals("MARSEILLE"))) {
            return DISTANCE_MARSEILLE_ALGER_KM;
        } else if ((a.equals("PARIS") && b.equals("NEW YORK")) || (a.equals("NEW YORK") && b.equals("PARIS"))) {
            return DISTANCE_PARIS_NEW_YORK_KM;
        }
        return DISTANCE_INCONNUE;
    }
}
