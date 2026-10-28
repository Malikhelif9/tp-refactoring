package fr.efrei.transfret;

import java.util.List;
import java.util.OptionalInt;

public class ValidateurExpedition {

    public OptionalInt valider(Client client, List<Colis> colis, String villeDepart, String villeArrivee,
                               ModeTransport mode, boolean urgent) {
        if (client == null) {
            System.out.println("ERREUR : client obligatoire");
            return OptionalInt.empty();
        }
        if (colis == null || colis.isEmpty()) {
            System.out.println("ERREUR : au moins un colis est nécessaire");
            return OptionalInt.empty();
        }
        boolean dangereux = false;
        boolean refrigere = false;
        for (Colis c : colis) {
            if (c.getPoids() <= 0) {
                System.out.println("ERREUR : poids invalide pour " + c.getDescription());
                return OptionalInt.empty();
            }
            if (c.getType() == TypeMarchandise.DANGEREUX) dangereux = true;
            if (c.getType() == TypeMarchandise.REFRIGERE) refrigere = true;
        }
        if (dangereux && mode == ModeTransport.AIR) {
            System.out.println("ERREUR : les matières dangereuses sont interdites en transport aérien");
            return OptionalInt.empty();
        }
        if (refrigere && !(mode == ModeTransport.ROUTE || mode == ModeTransport.MER)) {
            System.out.println("ERREUR : le transport réfrigéré n'est possible que par route ou par mer");
            return OptionalInt.empty();
        }
        if (urgent && mode == ModeTransport.MER) {
            System.out.println("ERREUR : pas d'option urgente pour le transport maritime");
            return OptionalInt.empty();
        }
        if (urgent && mode == ModeTransport.FLUVIAL) {
            System.out.println("ERREUR : pas d'option urgente pour le transport fluvial");
            return OptionalInt.empty();
        }
        int distance = Utils.getDistance(villeDepart, villeArrivee);
        if (distance == Utils.DISTANCE_INCONNUE) {
            System.out.println("ERREUR : trajet inconnu " + villeDepart + " -> " + villeArrivee);
            return OptionalInt.empty();
        }
        return OptionalInt.of(distance);
    }
}
