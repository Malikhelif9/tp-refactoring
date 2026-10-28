package fr.efrei.transfret;

import java.util.ArrayList;
import java.util.List;

public class CanalEmailConsole implements CanalNotification {

    private static final String SMTP_HOST = "smtp.transfret.fr";
    private static final String EXPEDITEUR = "noreply@transfret.fr";

    private final List<NotificationEnvoyee> historique = new ArrayList<>();

    @Override
    public boolean concerne(Client client) {
        return true;
    }

    @Override
    public void envoyer(Client client, MessageNotification message) {
        if (client.getEmail() == null || !client.getEmail().contains("@")) {
            throw new IllegalArgumentException("Adresse email invalide : " + client.getEmail());
        }
        System.out.println("[EMAIL via " + SMTP_HOST + "] " + EXPEDITEUR + " -> " + client.getEmail()
                + " | " + message.sujet());
        System.out.println("        " + message.contenuEmail());
        historique.add(new NotificationEnvoyee(TypeCanalNotification.EMAIL, client.getEmail(), message.sujet()));
    }

    @Override
    public List<NotificationEnvoyee> historique() {
        return List.copyOf(historique);
    }
}
