package fr.efrei.transfret;

public class AucuneRemise implements PolitiqueRemise {

    @Override
    public double appliquer(double prix) {
        return prix;
    }
}
