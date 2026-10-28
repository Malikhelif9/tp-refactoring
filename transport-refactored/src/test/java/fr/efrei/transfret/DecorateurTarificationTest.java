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
        TarifTransport chaine = new AssembleurTarif(
                Map.of(
                        ModeTransport.ROUTE, new TarifRoute(),
                        ModeTransport.RAIL, new TarifRail(),
                        ModeTransport.MER, new TarifMer(),
                        ModeTransport.AIR, new TarifAir()),
                Map.of(
                        TypeClient.PARTICULIER, new AucuneRemise(),
                        TypeClient.ENTREPRISE, new RemiseEntreprise(),
                        TypeClient.PREMIUM, new RemisePremium()))
                .assembler(ModeTransport.ROUTE);

        assertEquals(221.825, chaine.calculer(parametres(true, true, TypeClient.ENTREPRISE,
                List.of(TypeMarchandise.FRAGILE))), DELTA);
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
}
