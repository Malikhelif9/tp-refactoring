package fr.efrei.transfret;

import java.util.List;

public interface CanalNotification {

    boolean concerne(Client client);

    void envoyer(Client client, MessageNotification message);

    List<NotificationEnvoyee> historique();
}
