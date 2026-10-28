package fr.efrei.transfret;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Objects;

public class ServiceExpedition {

    private static final int ID_PREMIER_SUFFIXE = 1000;
    private static final int NOMBRE_SUFFIXES_ID = 9000;
    private static final double COEFFICIENT_TVA = 1.2;
    private static final double KM_PAR_MILLE_NAUTIQUE = 1.852;
    private static final int DISTANCE_ROULAGE_AVION_KM = 2;
    private static final int ALTITUDE_AVION_PIEDS = 35000;
    private static final double TAUX_FRAIS_ANNULATION = 0.5;
    private static final int TEMPERATURE_REFRIGERATION_CELSIUS = 4;

    private final ExpeditionRepository expeditions;
    private final CalculateurPrix calculateur;
    private final AffectationVehicule affectation;
    private final ValidateurExpedition validateur;
    private final EstimateurDelai estimateurDelai;
    private final Notificateur notificateur;
    private int compteur;

    public ServiceExpedition(ExpeditionRepository expeditions, CalculateurPrix calculateur,
                             AffectationVehicule affectation, ValidateurExpedition validateur,
                             EstimateurDelai estimateurDelai, Notificateur notificateur) {
        this.expeditions = Objects.requireNonNull(expeditions);
        this.calculateur = Objects.requireNonNull(calculateur);
        this.affectation = Objects.requireNonNull(affectation);
        this.validateur = Objects.requireNonNull(validateur);
        this.estimateurDelai = Objects.requireNonNull(estimateurDelai);
        this.notificateur = Objects.requireNonNull(notificateur);
    }

    public Expedition creerExpedition(Client client, List<Colis> colis, String villeDepart, String villeArrivee,
                                      ModeTransport mode, boolean urgent, boolean assurance) {
        OptionalInt distanceValidee = validateur.valider(client, colis, villeDepart, villeArrivee, mode, urgent);
        if (distanceValidee.isEmpty()) {
            return null;
        }
        int distance = distanceValidee.getAsInt();

        Vehicule v = affectation.trouverVehicule(colis, mode);
        if (v == null) {
            System.out.println("ERREUR : aucun véhicule disponible pour cette expédition");
            return null;
        }
        for (Colis c : colis) {
            v.charger(c);
        }

        Expedition e = new Expedition();
        compteur++;
        e.setId("EXP-" + compteur + "-" + (ID_PREMIER_SUFFIXE + new Random().nextInt(NOMBRE_SUFFIXES_ID)));
        e.setClient(client);
        e.setColis(colis);
        e.setVilleDepart(villeDepart);
        e.setVilleArrivee(villeArrivee);
        e.setDistance(distance);
        e.setMode(mode);
        e.setUrgent(urgent);
        e.setAssurance(assurance);
        e.setStatut(StatutExpedition.CREEE);
        e.setVehicule(v);
        e.setPrixHT(calculateur.calculerPrixHT(colis, distance, mode, urgent, assurance, client.getType()));
        e.setPrixTTC(Utils.arrondir(e.getPrixHT() * COEFFICIENT_TVA));
        e.setDateCreation(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(e.getDateCreation());
        cal.add(Calendar.DAY_OF_MONTH, estimateurDelai.calculerDelai(distance, mode, urgent));
        e.setDateLivraisonPrevue(cal.getTime());

        expeditions.sauvegarder(e);
        notificateur.notifier(client, new MessageNotification(
                "Confirmation de votre expédition " + e.getId(),
                "Votre expédition " + villeDepart + " -> " + villeArrivee + " (" + mode
                        + ") a été enregistrée. Montant : " + e.getPrixTTC() + " € TTC.",
                "TransFret : expédition " + e.getId() + " créée, livraison prévue le "
                        + Utils.formaterDate(e.getDateLivraisonPrevue())));
        return e;
    }

    // retourne le prix TTC, -1 si le trajet est inconnu
    public double calculerDevis(List<Colis> colis, String villeDepart, String villeArrivee, ModeTransport mode,
                                boolean urgent, boolean assurance, TypeClient typeClient) {
        int distance = Utils.getDistance(villeDepart, villeArrivee);
        if (distance == Utils.DISTANCE_INCONNUE) {
            return -1;
        }
        double ht = calculateur.calculerPrixHT(colis, distance, mode, urgent, assurance, typeClient);
        return Utils.arrondir(ht * COEFFICIENT_TVA);
    }

    public boolean demarrerExpedition(String id) {
        Expedition e = expeditions.trouver(id).orElse(null);
        if (e == null || e.getStatut() != StatutExpedition.CREEE) {
            System.out.println("ERREUR : impossible de démarrer l'expédition " + id);
            return false;
        }
        e.setStatut(StatutExpedition.EN_COURS);
        Vehicule v = e.getVehicule();
        v.setDisponible(false);
        try {
            v.faireLePlein();
        } catch (Exception ex) {
        }
        if (e.getMode() == ModeTransport.ROUTE || e.getMode() == ModeTransport.RAIL) {
            v.rouler(e.getDistance());
        } else if (e.getMode() == ModeTransport.MER) {
            v.naviguer((int) (e.getDistance() / KM_PAR_MILLE_NAUTIQUE));
        } else if (e.getMode() == ModeTransport.AIR) {
            v.rouler(DISTANCE_ROULAGE_AVION_KM);
            v.voler(ALTITUDE_AVION_PIEDS);
        }
        for (Colis c : e.getColis()) {
            if (c.getType() == TypeMarchandise.REFRIGERE) {
                v.activerRefrigeration(TEMPERATURE_REFRIGERATION_CELSIUS);
                break;
            }
        }
        expeditions.mettreAJour(e);
        notificateur.notifier(e.getClient(), new MessageNotification(
                "Votre expédition " + e.getId() + " est en route",
                "Votre expédition a quitté " + e.getVilleDepart() + ".",
                "TransFret : expédition " + e.getId() + " partie de " + e.getVilleDepart()));
        return true;
    }

    public boolean livrerExpedition(String id) {
        Expedition e = expeditions.trouver(id).orElse(null);
        if (e == null || e.getStatut() != StatutExpedition.EN_COURS) {
            System.out.println("ERREUR : impossible de livrer l'expédition " + id);
            return false;
        }
        e.setStatut(StatutExpedition.LIVREE);
        libererVehicule(e);
        expeditions.mettreAJour(e);
        notificateur.notifier(e.getClient(), new MessageNotification(
                "Votre expédition " + e.getId() + " est livrée",
                "Votre expédition est arrivée à " + e.getVilleArrivee() + ". Merci de votre confiance.",
                "TransFret : expédition " + e.getId() + " livrée à " + e.getVilleArrivee()));
        return true;
    }

    public boolean annulerExpedition(String id) {
        Expedition e = expeditions.trouver(id).orElse(null);
        if (e == null || e.getStatut() == StatutExpedition.LIVREE
                || e.getStatut() == StatutExpedition.ANNULEE) {
            System.out.println("ERREUR : impossible d'annuler l'expédition " + id);
            return false;
        }
        if (e.getStatut() == StatutExpedition.EN_COURS) {
            // Une moitié des frais si l'expédition est déjà partie.
            e.setFraisAnnulation(Utils.arrondir(e.getPrixTTC() * TAUX_FRAIS_ANNULATION));
        }
        e.setStatut(StatutExpedition.ANNULEE);
        libererVehicule(e);
        expeditions.mettreAJour(e);
        notificateur.notifier(e.getClient(), new MessageNotification(
                "Annulation de votre expédition " + e.getId(),
                "Votre expédition a été annulée. Frais d'annulation : " + e.getFraisAnnulation() + " €",
                "TransFret : expédition " + e.getId() + " annulée"));
        return true;
    }

    private void libererVehicule(Expedition e) {
        for (Colis c : e.getColis()) {
            e.getVehicule().decharger(c);
        }
        e.getVehicule().setDisponible(true);
    }

}
