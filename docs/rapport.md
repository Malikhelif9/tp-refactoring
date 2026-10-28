# Étape 1 – Diagnostic



## Responsabilité et dépendances de chaque classe

| Classe | Responsabilité actuelle | Dépendances / observations |
|---|---|---|
| `AffectationVehicule` | Trouver un véhicule disponible compatible avec un voyage | Parcourt `Flotte.vehicules`, connaît les classes concrètes (`Camion`, `Train`, `Bateau`, `Avion`) et inspecte colis et capacités |
| `Avion` | Implémenter le comportement d'un avion de la flotte | Étend `Vehicule`; lève des exceptions pour les opérations non aériennes |
| `Bateau` | Implémenter le comportement d'un bateau et gérer ses conteneurs réfrigérés | Étend `Vehicule`; lève des exceptions pour rouler, voler ou réfrigérer sans équipement |
| `CalculateurPrix` | Calculer le tarif de base, les suppléments, la remise, l'assurance et l'arrondi HT | Dépend des colis, mais utilise des chaînes pour le mode et le type client et des entiers pour les marchandises |
| `Camion` | Implémenter les capacités et aptitudes d'un camion | Étend `Vehicule`; distingue les camions ADR |
| `CamionFrigorifique` | Spécialiser le camion pour les colis réfrigérés | Étend `Camion` et ajoute une précondition à `charger`, incompatible avec l'usage général d'un `Camion` |
| `Client` | Porter les données d'un client | Modèle mutable; le type client est une chaîne |
| `Colis` | Porter les données et dimensions d'un colis | Le type de marchandise est un entier; le libellé est calculé par un `switch` |
| `Database` | Simuler la persistance et fournir l'accès aux clients/expéditions | Singleton, état statique partagé, stockage mémoire et affichage de pseudo-SQL dans la même classe |
| `EmailService` | Simuler l'envoi d'emails et en conserver le journal | Méthodes et journal statiques; affiche directement sur la console |
| `Expedition` | Porter l'état d'une expédition et produire sa facture texte | Mélange modèle métier et présentation; utilise `Utils`, les dates et le client |
| `Flotte` | Initialiser la flotte de véhicules | Liste et initialisation statiques; instancie directement toutes les classes concrètes |
| `IVehicule` | Définir les opérations possibles sur un véhicule | Interface très large : impose conduite, navigation, vol, réfrigération et plein à tous les véhicules |
| `Main` | Exécuter un scénario de démonstration | Construit directement les services et les objets métier |
| `ServiceClient` | Valider un client, le créer, le persister et envoyer un email de bienvenue | Dépend directement de `Database` et `EmailService` |
| `ServiceExpedition` | Valider, créer, tarifer, planifier, persister, démarrer, livrer, annuler et notifier une expédition | Dépend directement du singleton, du calculateur, de l'affectation, des services statiques et des classes utilitaires |
| `SmsService` | Simuler l'envoi de SMS et en conserver le journal | Méthodes et journal statiques; affiche directement sur la console |
| `Train` | Implémenter le comportement d'un train de la flotte | Étend `Vehicule`; lève des exceptions pour navigation, vol, réfrigération et plein |
| `Utils` | Fournir distances, validation d'email, formatage de dates/prix et arrondi | Regroupe des fonctions sans lien direct; son `SimpleDateFormat` statique est mutable et non thread-safe |
| `Vehicule` | Porter l'état commun des véhicules et gérer chargement/déchargement | Implémente l'interface large `IVehicule`; ses opérations concrètes varient selon le mode |

## Défauts observés et principes concernés

| Défaut | Emplacement | Principe / notion |
|---|---|---|
| `ServiceExpedition` cumule validation, tarification, délai, affectation, persistance, cycle de vie et notifications | `ServiceExpedition` | Responsabilité unique (S) |
| Une entité métier produit directement une facture formatée | `Expedition.genererFacture` | Responsabilité unique (S) |
| Le service client valide, crée, persiste et notifie | `ServiceClient.creerClient` | Responsabilité unique (S) |
| Les règles tarifaires, délais et choix de véhicules reposent sur des cascades de conditions | `CalculateurPrix`, `ServiceExpedition`, `AffectationVehicule` | Ouvert/fermé (O) |
| Ajouter une variante métier oblige à modifier plusieurs classes centrales | `CalculateurPrix`, `ServiceExpedition`, `AffectationVehicule`, `Flotte`, `Utils` | Ouvert/fermé (O) |
| `CamionFrigorifique.charger` refuse les colis acceptés par la classe parent | `CamionFrigorifique` | Substitution de Liskov (L) |
| Les véhicules doivent implémenter des opérations qui ne les concernent pas et lancent des exceptions | `IVehicule`, `Train`, `Bateau`, `Avion` | Ségrégation des interfaces (I) |
| Les services construisent eux-mêmes leurs collaborateurs concrets et utilisent des services globaux | `ServiceExpedition`, `ServiceClient`, `Database`, `Flotte` | Inversion des dépendances (D) |
| Mode, statut et type client sont des chaînes; type de marchandise est un entier | `Expedition`, `Client`, `Colis` | Typage faible |
| Des nombres métier sont intégrés aux calculs | `CalculateurPrix`, `ServiceExpedition`, `Colis`, `Expedition` | Nombres magiques |
| L'arrondi au centime est recodé à plusieurs endroits | `Utils`, `CalculateurPrix`, `ServiceExpedition`, `Expedition` | Duplication |
| Le stockage simulé mélange état global, pseudo-SQL et persistance mémoire | `Database` | Responsabilités mêlées, dépendance concrète |
| L'identifiant de base de données et la clé d'API sont codés en dur | `Database`, `SmsService` | Configuration et sécurité |
| Des erreurs sont ignorées par un `catch` vide; plusieurs services affichent eux-mêmes les erreurs | `ServiceExpedition`, services de notification | Gestion d'erreurs et séparation des responsabilités |

## Où intervenir pour ajouter le mode `FLUVIAL` au legacy

Le calcul et le cycle de vie du nouveau mode sont dispersés. Les emplacements à prévoir sont :

1. `CalculateurPrix.calculerPrixHT` : ajouter le tarif fluvial (0,40 €/km + 0,02 €/kg, minimum 300 €).
2. `ServiceExpedition.creerExpedition` : définir l'acceptation du réfrigéré et de l'urgence pour ce mode.
3. `ServiceExpedition.calculerDelai` : ajouter distance / 360, arrondi supérieur, puis un jour.
4. `ServiceExpedition.demarrerExpedition` : démarrer un trajet fluvial au lieu de choisir parmi route/rail, mer et air.
5. `AffectationVehicule.trouverVehicule` : reconnaître la péniche et vérifier sa disponibilité, sa capacité et son aptitude.
6. `Flotte.initialiser` : ajouter la péniche `PEN-0001` de 2 500 tonnes.
7. `Utils.getDistance` : ajouter le trajet Paris ↔ Le Havre de 365 km.
8. Créer une classe `Peniche`, qui devra aussi fournir les méthodes imposées par `IVehicule`, y compris celles qui ne sont pas pertinentes pour un bateau fluvial.

Il faudra également ajouter le mode aux types utilisés par la nouvelle implémentation et prévoir ses tests, sans modifier `transport-contrat` ni le legacy de référence.

## Où intervenir pour ajouter le supplément « hors gabarit »

Dans le legacy, le point de calcul des suppléments est `CalculateurPrix.calculerPrixHT` :

1. Détecter si au moins un colis a une dimension supérieure à 200 cm. Les trois dimensions sont déjà disponibles dans `Colis`.
2. Ajouter le multiplicateur 1,10 au bon endroit dans l'ordre des suppléments, avant urgence et remise.
3. Ajouter des tests couvrant l'absence de supplément à 200 cm ou moins et son application au-delà de 200 cm.

La détection pourrait être extraite dans `Colis`, mais le calcul de prix restera à modifier dans cette conception : les suppléments sont tous regroupés dans le calculateur.
