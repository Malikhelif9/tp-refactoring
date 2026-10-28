package fr.efrei.transfret;

import java.util.List;

public class Notificateur {

    private final List<CanalNotification> canaux;

    public Notificateur(List<CanalNotification> canaux) {
        this.canaux = List.copyOf(canaux);
    }

    public void notifier(Client client, MessageNotification message) {
        for (CanalNotification canal : canaux) {
            if (canal.concerne(client)) {
                canal.envoyer(client, message);
            }
        }
    }
}
