package fr.efrei.transfret;

/**
 * Interface commune à tous les véhicules de la flotte.
 */
public interface IVehicule {

    String getImmatriculation();

    double getCapaciteMax();

    double getChargeActuelle();

    boolean estDisponible();

    void setDisponible(boolean disponible);

    void charger(Colis colis);

    void decharger(Colis colis);

    boolean peutPrendreEnCharge(ModeTransport mode, java.util.List<Colis> colis);

    void partir(int distanceKm);

    void preparerExpedition(java.util.List<Colis> colis, double temperatureRefrigeration);
}
