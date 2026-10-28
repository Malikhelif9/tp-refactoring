package fr.efrei.transfret;

import java.util.ArrayList;
import java.util.List;

public class CanalSmsConsole implements CanalNotification {

    private static final int LONGUEUR_MAXIMALE_SMS = 160;
    private static final String SUFFIXE_TRONCATURE = "...";
    private static final int LONGUEUR_MESSAGE_TRONQUE = LONGUEUR_MAXIMALE_SMS - SUFFIXE_TRONCATURE.length();

    private final List<NotificationEnvoyee> historique = new ArrayList<>();

    @Override
    public boolean concerne(Client client) {
        return client.getType() == TypeClient.PREMIUM;
    }

    @Override
    public void envoyer(Client client, MessageNotification message) {
        String contenu = message.contenuSms();
        if (contenu.length() > LONGUEUR_MAXIMALE_SMS) {
            contenu = contenu.substring(0, LONGUEUR_MESSAGE_TRONQUE) + SUFFIXE_TRONCATURE;
        }
        System.out.println("[SMS] -> " + client.getTelephone() + " : " + contenu);
        historique.add(new NotificationEnvoyee(TypeCanalNotification.SMS, client.getTelephone(), contenu));
    }

    @Override
    public List<NotificationEnvoyee> historique() {
        return List.copyOf(historique);
    }
}
