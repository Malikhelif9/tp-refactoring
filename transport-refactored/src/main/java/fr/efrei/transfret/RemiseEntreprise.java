package fr.efrei.transfret;

public class RemiseEntreprise implements PolitiqueRemise {

    private static final double SEUIL_GROS_VOLUME = 5000;
    private static final double COEFFICIENT_STANDARD = 0.90;
    private static final double COEFFICIENT_GROS_VOLUME = 0.85;

    @Override
    public double appliquer(double prix) {
        return prix > SEUIL_GROS_VOLUME
                ? prix * COEFFICIENT_GROS_VOLUME
                : prix * COEFFICIENT_STANDARD;
    }
}
