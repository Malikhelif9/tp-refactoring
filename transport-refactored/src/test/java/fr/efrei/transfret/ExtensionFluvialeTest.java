package fr.efrei.transfret;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionFluvialeTest {

    @Test
    void estimeLeDelaiFluvialAvecUnJourDeManutention() {
        EstimateurDelai estimateur = new EstimateurDelai();

        assertEquals(2, estimateur.calculerDelai(360, ModeTransport.FLUVIAL, false));
        assertEquals(3, estimateur.calculerDelai(365, ModeTransport.FLUVIAL, false));
    }

    @Test
    void penicheAUneCapaciteDeDeuxMilleCinqCentTonnesEtAccepteLeFluvial() {
        Peniche peniche = new Peniche("PEN-0001", 2_500_000);

        assertEquals("PEN-0001", peniche.getImmatriculation());
        assertEquals(2_500_000, peniche.getCapaciteMax());
        assertTrue(peniche.peutPrendreEnCharge(ModeTransport.FLUVIAL, List.of()));
        assertFalse(peniche.peutPrendreEnCharge(ModeTransport.ROUTE, List.of()));
    }

    @Test
    void expedieParPenicheSurParisLeHavreAvecLeTarifEtLeDelaiFluvial() {
        Client client = new Client("Client test", "test@example.com", "0600000000",
                "Paris", TypeClient.PARTICULIER, null);
        Colis colis = new Colis("Palette", 8000, 100, 100, 100, TypeMarchandise.STANDARD, 1000);
        CanalEmailConsole email = new CanalEmailConsole();
        ServiceExpedition service = new ServiceExpedition(
                new DepotExpeditionsMemoire(),
                ConfigurationTarification.creerCalculateurPrix(),
                new AffectationVehicule(new Flotte()),
                new ValidateurExpedition(),
                new EstimateurDelai(),
                new Notificateur(List.of(email, new CanalSmsConsole())));

        Expedition expedition = service.creerExpedition(client, List.of(colis), "Paris", "Le Havre",
                ModeTransport.FLUVIAL, false, false);

        assertEquals("PEN-0001", expedition.getVehicule().getImmatriculation());
        assertEquals(306, expedition.getPrixHT());
        assertEquals(3, new EstimateurDelai().calculerDelai(
                expedition.getDistance(), ModeTransport.FLUVIAL, false));
    }

    @Test
    void fluvialRefuseLurgenceEtLesColisRefrigeres() {
        ValidateurExpedition validateur = new ValidateurExpedition();
        Client client = new Client("Client test", "test@example.com", "0600000000",
                "Paris", TypeClient.PARTICULIER, null);
        List<Colis> colisStandard = List.of(
                new Colis("Palette", 100, 100, 100, 100, TypeMarchandise.STANDARD, 1000));
        List<Colis> colisRefrigeres = List.of(
                new Colis("Frais", 100, 100, 100, 100, TypeMarchandise.REFRIGERE, 1000));

        OptionalInt distanceUrgente = validateur.valider(
                client, colisStandard, "Paris", "Le Havre", ModeTransport.FLUVIAL, true);
        OptionalInt distanceRefrigeree = validateur.valider(
                client, colisRefrigeres, "Paris", "Le Havre", ModeTransport.FLUVIAL, false);

        assertTrue(distanceUrgente.isEmpty());
        assertTrue(distanceRefrigeree.isEmpty());
    }

    @Test
    void distanceParisLeHavreEstConnueDansLesDeuxSens() {
        assertEquals(365, Utils.getDistance("Paris", "Le Havre"));
        assertEquals(365, Utils.getDistance("Le Havre", "Paris"));
    }
}
