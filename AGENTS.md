# Guide pour l'assistant IA de l'éditeur

Ce fichier s'adresse aux assistants de programmation utilisés dans l'éditeur sur ce dépôt. Il décrit la posture attendue et le contexte du projet.

## Posture

- **Aider à comprendre avant de produire.** Quand une question porte sur un comportement du code, explique ce qui se passe et pourquoi avant de proposer une modification.
- **Procéder par petites étapes.** Une modification à la fois, vérifiable par une commande (`mvn test`, un appel d'API), plutôt qu'une réécriture massive.
- **Poser des questions avant de coder.** Si l'intention n'est pas claire (périmètre, comportement attendu en cas d'erreur, impact sur les autres couches), demander plutôt que supposer.
- **Expliquer les annotations.** Pour chaque annotation Spring ou JPA introduite, dire ce qu'elle déclenche à l'exécution, pas seulement où la poser.
- **Ne pas trancher à la place du développeur.** Présenter les options et leurs conséquences ; la décision d'architecture lui appartient et il doit pouvoir la justifier.

## Bonnes pratiques de la stack

- Architecture en couches : `Controller` → `Service` → `Repository` → `Entity`. Chaque couche a une responsabilité ; une règle de gestion vit dans un service, un mapping HTTP dans un contrôleur, une requête dans un repository.
- Injection de dépendances par constructeur : c'est ce qui rend une classe testable sans conteneur Spring.
- Validation des entrées par Bean Validation (`@NotNull`, `@Size`, `@Valid`) plutôt que par des vérifications manuelles dispersées.
- Exceptions métier en classes dédiées, traduites en réponses HTTP par un `@ControllerAdvice`.
- Objets de transfert (DTO) en entrée et en sortie des contrôleurs plutôt que des entités JPA.
- Tests unitaires rapides avec JUnit et Mockito (`@Mock` n'a pas besoin du contexte Spring ; `@MockitoBean` remplace un bean dans un contexte démarré. `@MockBean` n'existe plus depuis Spring Boot 4). Tests d'intégration sur une base réelle éphémère avec Testcontainers.
- Transactions explicites (`@Transactional`) là où des relations JPA paresseuses sont parcourues.
- Requêtes paramétrées, toujours : jamais de concaténation de valeurs dans une requête SQL ou JPQL.

## Mises en garde

- **Secrets.** Ne jamais écrire de mot de passe, de clé ou de jeton dans le code, dans un fichier de configuration versionné ou dans un exemple. Les secrets passent par des variables d'environnement ; `.env` est ignoré par Git, `.env.example` ne contient que des valeurs factices.
- **Données des utilisateurs.** L'application manipule des données de santé et des données personnelles (destinataires, chauffeurs, utilisateurs). Ne jamais les journaliser, les exposer dans une réponse d'API au-delà du nécessaire, ni les copier dans un prompt ou un exemple.
- **Code généré.** Tout code proposé doit être relu et compris avant d'être accepté. Une configuration de sécurité ou une requête copiée sans être comprise est un risque, pas un gain de temps.
- **Dépendances.** Ne pas ajouter de dépendance sans expliquer ce qu'elle apporte et vérifier sa compatibilité avec Spring Boot 4.

## Le projet

Application Spring Boot exposant une API REST de suivi des transports de produits de santé pour Santélog. Paquet racine : `fr.santelog.suivitransports`.

```
src/main/java/fr/santelog/suivitransports/
├── SuiviTransportsApplication.java   # point d'entrée
├── config/                           # insertion des données de démarrage (CommandLineRunner)
├── controller/                       # TransportController, DestinataireController,
│                                     # ReleveTemperatureController, AuthController, RapportController
├── entity/                           # Transport, Destinataire, ReleveTemperature, Utilisateur,
│                                     # StatutTransport, TypeProduit
├── repository/                       # interfaces Spring Data JpaRepository
└── service/                          # TransportService, DestinataireService,
                                      # ReleveTemperatureService, AuthService, RapportService
src/test/java/fr/santelog/suivitransports/service/   # tests unitaires
```

Règle métier centrale : un transport porte une plage de température `[temperatureMin, temperatureMax]` ; un relevé hors plage fait passer le transport au statut `INCIDENT`.

Documentation complémentaire : `README.md` (lancement, endpoints) et `starter-kit/note-contexte-problemes-connus-java.md` (contexte rédigé par l'équipe).

## Stack et versions

- Java 25
- Spring Boot 4.1.1 (Spring Web MVC, Spring Data JPA, Hibernate)
- PostgreSQL 16 (via `compose.yaml`)
- springdoc-openapi 3.1.1
- JUnit 6, Mockito (via `spring-boot-starter-test`)
- Maven

Commandes : `docker compose up -d`, `mvn spring-boot:run`, `mvn test`, `mvn clean package`. Swagger UI : `http://localhost:8080/swagger-ui.html`.
