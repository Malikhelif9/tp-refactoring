package fr.efrei.transfret;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrategieTarificationTest {

    private static final double DELTA = 0.000001;

    @Test
    void tarifRouteCalculeLePrixParDistanceEtPoids() {
        assertEquals(130, new TarifRoute().calculer(parametresDeBase()), DELTA);
    }

    @Test
    void tarifRailAppliqueLeMinimumDeFacturation() {
        assertEquals(150, new TarifRail().calculer(parametres(10, 10, 0)), DELTA);
    }

    @Test
    void tarifMerAppliqueLeMinimumDeFacturation() {
        assertEquals(500, new TarifMer().calculer(parametres(10, 10, 0)), DELTA);
    }

    @Test
    void tarifAirUtiliseLePoidsVolumetriqueQuandIlEstSuperieur() {
        assertEquals(135.15, new TarifAir().calculer(parametres(100, 10, 0.1)), DELTA);
    }

    @Test
    void particulierNeBeneficiePasDeRemise() {
        assertEquals(6000, new AucuneRemise().appliquer(6000), DELTA);
    }

    @Test
    void entrepriseBeneficieDesDeuxNiveauxDeRemiseSelonLePrix() {
        RemiseEntreprise remise = new RemiseEntreprise();

        assertEquals(4500, remise.appliquer(5000), DELTA);
        assertEquals(4250.85, remise.appliquer(5001), DELTA);
    }

    @Test
    void premiumBeneficieDeVingtPourCentDeRemise() {
        assertEquals(800, new RemisePremium().appliquer(1000), DELTA);
    }

    @Test
    void calculateurSelectionneLesStrategiesInjecteesSansConditionSurModeOuClient() {
        CalculateurPrix calculateur = ConfigurationTarification.creerCalculateurPrix();
        List<Colis> colis = List.of(new Colis("colis", 100, 10, 10, 10, TypeMarchandise.STANDARD, 1000));

        assertEquals(130, calculateur.calculerPrixHT(
                colis, 100, ModeTransport.ROUTE, false, false, TypeClient.PARTICULIER), DELTA);
        assertEquals(117, calculateur.calculerPrixHT(
                colis, 100, ModeTransport.ROUTE, false, false, TypeClient.ENTREPRISE), DELTA);
        assertEquals(104, calculateur.calculerPrixHT(
                colis, 100, ModeTransport.ROUTE, false, false, TypeClient.PREMIUM), DELTA);
    }

    private static ParametresTarification parametresDeBase() {
        return parametres(100, 100, 0);
    }

    private static ParametresTarification parametres(double distance, double poids, double volume) {
        return new ParametresTarification(List.of(), (int) distance, poids, volume, 0,
                false, false, TypeClient.PARTICULIER);
    }
}
