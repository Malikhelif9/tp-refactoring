package fr.efrei.transfret;

public class ServiceClient {

    private static final int LONGUEUR_SIRET = 14;

    private final ClientRepository clients;
    private final CanalNotification canalBienvenue;

    public ServiceClient(ClientRepository clients, CanalNotification canalBienvenue) {
        this.clients = clients;
        this.canalBienvenue = canalBienvenue;
    }

    public Client creerClient(String nom, String email, String telephone, String adresse, TypeClient type, String siret) {
        if (nom == null || nom.trim().length() == 0) {
            System.out.println("ERREUR : nom obligatoire");
            return null;
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            System.out.println("ERREUR : email invalide (" + email + ")");
            return null;
        }
        if (type == null) {
            System.out.println("ERREUR : type de client inconnu");
            return null;
        }
        if (type == TypeClient.ENTREPRISE && (siret == null || siret.length() != LONGUEUR_SIRET)) {
            System.out.println("ERREUR : SIRET obligatoire (" + LONGUEUR_SIRET
                    + " chiffres) pour une entreprise");
            return null;
        }
        Client c = new Client(nom, email, telephone, adresse, type, siret);
        clients.sauvegarder(c);
        canalBienvenue.envoyer(c, new MessageNotification("Bienvenue chez TransFret",
                "Bonjour " + c.getNom() + ", votre compte " + c.getType().name().toLowerCase()
                        + " a été créé.", null));
        return c;
    }
}
