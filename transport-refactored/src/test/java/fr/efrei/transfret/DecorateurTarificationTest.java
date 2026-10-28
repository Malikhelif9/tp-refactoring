package fr.efrei.transfret;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecorateurTarificationTest {

    private static final double DELTA = 0.000001;
    private static final TarifTransport TARIF_ROUTE = new TarifRoute();

    @Test
    void supplementFragileAugmenteLePrixDeQuinzePourCent() {
        assertEquals(149.5, new SupplementFragile(TARIF_ROUTE).calculer(parametres(TypeMarchandise.FRAGILE)),
                DELTA);
    }

    @Test
    void supplementDangereuxAugmenteLePrixDeTrentePourCent() {
        assertEquals(169, new SupplementDangereux(TARIF_ROUTE).calculer(parametres(TypeMarchandise.DANGEREUX)),
                DELTA);
    }

    @Test
    void supplementRefrigereAugmenteLePrixDeVingtPourCent() {
        assertEquals(156, new SupplementRefrigere(TARIF_ROUTE).calculer(parametres(TypeMarchandise.REFRIGERE)),
                DELTA);
    }

    @Test
    void supplementHorsGabaritSAppliqueQuandUneDimensionDepasseDeuxMetres() {
        ParametresTarification colisHorsGabarit = parametresDimensions(200.01, 100, 100);

        assertEquals(143, new SupplementHorsGabarit(TARIF_ROUTE).calculer(colisHorsGabarit), DELTA);
    }

    @Test
    void supplementHorsGabaritNeSappliquePasQuandLesDimensionsFontDeuxMetresOuMoins() {
        ParametresTarification colisStandard = parametresDimensions(200, 200, 200);

        assertEquals(130, new SupplementHorsGabarit(TARIF_ROUTE).calculer(colisStandard), DELTA);
    }

    @Test
    void calculateurAppliqueLeSupplementHorsGabaritUneSeuleFoisParExpedition() {
        CalculateurPrix calculateur = ConfigurationTarification.creerCalculateurPrixAvecSupplementHorsGabarit();
        List<Colis> colis = List.of(
                new Colis("hors gabarit", 100, 201, 100, 100, TypeMarchandise.STANDARD, 1000),
                new Colis("autre hors gabarit", 100, 100, 100, 201, TypeMarchandise.STANDARD, 1000));

        assertEquals(154, calculateur.calculerPrixHT(
                colis, 100, ModeTransport.ROUTE, false, false, TypeClient.PARTICULIER), DELTA);
    }

    @Test
    void configurationParDefautNeModifiePasLesPrixDesColisHorsGabarit() {
        CalculateurPrix calculateur = ConfigurationTarification.creerCalculateurPrix();
        List<Colis> colis = List.of(
                new Colis("hors gabarit", 100, 201, 100, 100, TypeMarchandise.STANDARD, 1000));

        assertEquals(130, calculateur.calculerPrixHT(
                colis, 100, ModeTransport.ROUTE, false, false, TypeClient.PARTICULIER), DELTA);
    }

    @Test
    void urgenceMultiplieLePrixParUnEtDemi() {
        assertEquals(195, new SupplementUrgence(TARIF_ROUTE).calculer(parametres(true, false,
                TypeClient.PARTICULIER, List.of())), DELTA);
    }

    @Test
    void remiseEstAppliqueeApresLesSupplements() {
        Map<TypeClient, PolitiqueRemise> remises = Map.of(
                TypeClient.PARTICULIER, new AucuneRemise(),
                TypeClient.ENTREPRISE, new RemiseEntreprise(),
                TypeClient.PREMIUM, new RemisePremium());
        TarifTransport chaine = new RemiseClient(
                new SupplementUrgence(new SupplementFragile(TARIF_ROUTE)), remises);

        assertEquals(201.825, chaine.calculer(parametres(true, false, TypeClient.ENTREPRISE,
                List.of(TypeMarchandise.FRAGILE))), DELTA);
    }

    @Test
    void assuranceEstAjouteeApresLaRemise() {
        Map<TypeClient, PolitiqueRemise> remises = Map.of(
                TypeClient.PARTICULIER, new AucuneRemise(),
                TypeClient.ENTREPRISE, new RemiseEntreprise(),
                TypeClient.PREMIUM, new RemisePremium());
        TarifTransport chaine = new Assurance(new RemiseClient(
                new SupplementUrgence(new SupplementFragile(TARIF_ROUTE)), remises));

        assertEquals(221.825, chaine.calculer(parametres(true, true, TypeClient.ENTREPRISE,
                List.of(TypeMarchandise.FRAGILE))), DELTA);
    }

    @Test
    void remiseEntrepriseEvalueLeSeuilApresSupplementEtUrgence() {
        Map<TypeClient, PolitiqueRemise> remises = Map.of(
                TypeClient.PARTICULIER, new AucuneRemise(),
                TypeClient.ENTREPRISE, new RemiseEntreprise(),
                TypeClient.PREMIUM, new RemisePremium());
        TarifTransport chaine = new Assurance(new RemiseClient(
                new SupplementUrgence(new SupplementFragile(TARIF_ROUTE)), remises));
        ParametresTarification parametres = new ParametresTarification(
                List.of(new Colis("colis fragile", 40000, 10, 10, 10, TypeMarchandise.FRAGILE, 0)),
                0, 40000, 0, 0, true, false, TypeClient.ENTREPRISE);

        assertEquals(5865, chaine.calculer(parametres), DELTA);
    }

    @Test
    void assembleurEmpileLesDecorateursDansLOrdreAttendu() {
        AssembleurTarif assembleur = assembleurTarif();
        TarifTransport chaine = assembleur.assembler(ModeTransport.ROUTE);

        assertEquals(221.825, chaine.calculer(parametres(true, true, TypeClient.ENTREPRISE,
                List.of(TypeMarchandise.FRAGILE))), DELTA);
        assertEquals(143, assembleur.assembler(ModeTransport.ROUTE)
                .calculer(parametresDimensions(200.01, 100, 100)), DELTA);
    }

    private static AssembleurTarif assembleurTarif() {
        return new AssembleurTarif(
                Map.of(
                        ModeTransport.ROUTE, new TarifRoute(),
                        ModeTransport.RAIL, new TarifRail(),
                        ModeTransport.MER, new TarifMer(),
                        ModeTransport.AIR, new TarifAir(),
                        ModeTransport.FLUVIAL, new TarifFluvial()),
                Map.of(
                        TypeClient.PARTICULIER, new AucuneRemise(),
                        TypeClient.ENTREPRISE, new RemiseEntreprise(),
                        TypeClient.PREMIUM, new RemisePremium()),
                true);
    }

    private static ParametresTarification parametres(TypeMarchandise type) {
        return parametres(false, false, TypeClient.PARTICULIER, List.of(type));
    }

    private static ParametresTarification parametres(boolean urgent, boolean assurance, TypeClient typeClient,
                                                      List<TypeMarchandise> types) {
        List<Colis> colis = types.stream()
                .map(type -> new Colis("colis", 100, 10, 10, 10, type, 1000))
                .toList();
        return new ParametresTarification(colis, 100, 100, 0, 1000, urgent, assurance, typeClient);
    }

    private static ParametresTarification parametresDimensions(double longueur, double largeur, double hauteur) {
        List<Colis> colis = List.of(new Colis(
                "colis volumineux", 100, longueur, largeur, hauteur, TypeMarchandise.STANDARD, 1000));
        return new ParametresTarification(colis, 100, 100, 0, 1000, false, false, TypeClient.PARTICULIER);
    }
}
