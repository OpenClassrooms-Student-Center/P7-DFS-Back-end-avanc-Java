# Suivi Transports - Santélog

Application de suivi des transports de produits de santé (médicaments, dispositifs médicaux, échantillons biologiques) : transports, plages de température, relevés des capteurs, destinataires, rapports d'exploitation.

Version en production : 1.4.2.

## Prérequis

- Java 25 (JDK)
- Maven 3.9 ou supérieur
- Docker et Docker Compose (pour la base de données de développement)

## Installation

```bash
git clone <url-du-depot> suivi-transports
cd suivi-transports
cp .env.example .env
docker compose up -d
mvn clean install
```

Le fichier `.env` alimente le service PostgreSQL décrit dans `compose.yaml`. Les paramètres de connexion de l'application se trouvent dans `src/main/resources/application.properties`.

## Lancement

```bash
mvn spring-boot:run
```

L'application écoute sur `http://localhost:8080`. Au premier démarrage sur une base vide, un jeu de données de démonstration est inséré automatiquement (utilisateurs, destinataires, transports et relevés).

Comptes de démonstration :

| Login     | Mot de passe | Rôle      |
|-----------|--------------|-----------|
| `admin`   | `Admin2021!` | ADMIN     |
| `mdurand` | `marie123`   | OPERATEUR |
| `lecture` | `lecture`    | LECTEUR   |

Documentation interactive de l'API : `http://localhost:8080/swagger-ui.html`

## Stack technique

- Java 25
- Spring Boot 4.1.1 (Spring Web MVC, Spring Data JPA)
- Hibernate (schéma géré par `ddl-auto=update`)
- PostgreSQL 16
- springdoc-openapi 3.1.1 (Swagger UI)
- JUnit 6 et Mockito (tests)
- Maven

## Structure du projet

```
.
├── compose.yaml                         # PostgreSQL de développement
├── pom.xml
├── starter-kit/
│   └── note-contexte-problemes-connus-java.md  # note de contexte rédigée par l'équipe
├── src/main/java/fr/santelog/suivitransports/
│   ├── SuiviTransportsApplication.java  # point d'entrée
│   ├── config/                          # données de démarrage
│   ├── controller/                      # API REST
│   ├── entity/                          # entités JPA et énumérations
│   ├── repository/                      # accès aux données (Spring Data)
│   └── service/                         # règles de gestion
├── src/main/resources/
│   └── application.properties
└── src/test/java/fr/santelog/suivitransports/
    └── service/                         # tests unitaires des services
```

L'application est organisée en couches : les contrôleurs reçoivent les requêtes HTTP, délèguent aux services qui portent les règles de gestion, lesquels s'appuient sur les repositories Spring Data pour la persistance des entités.

## Points d'entrée de l'API

### Authentification

| Méthode | Route             | Description                                   |
|---------|-------------------|-----------------------------------------------|
| POST    | `/api/auth/login` | Connexion (`{"login": "...", "motDePasse": "..."}`) |

### Transports

| Méthode | Route                              | Description                                    |
|---------|------------------------------------|------------------------------------------------|
| GET     | `/api/transports`                  | Liste des transports                           |
| GET     | `/api/transports/{id}`             | Détail d'un transport                          |
| POST    | `/api/transports`                  | Création d'un transport                        |
| PUT     | `/api/transports/{id}/statut`      | Changement de statut (`{"statut": "EN_COURS"}`) |
| DELETE  | `/api/transports/{id}`             | Suppression d'un transport                     |
| GET     | `/api/transports/recherche?q=...`  | Recherche par fragment de référence            |

Statuts possibles : `PLANIFIE`, `EN_COURS`, `LIVRE`, `INCIDENT`.
Types de produit : `MEDICAMENT`, `DISPOSITIF_MEDICAL`, `ECHANTILLON_BIOLOGIQUE`.

Exemple de corps pour la création :

```json
{
  "reference": "TRP-2024-0007",
  "typeProduit": "MEDICAMENT",
  "dateDepart": "2024-03-11T07:00:00",
  "dateArriveePrevue": "2024-03-11T09:30:00",
  "temperatureMin": 2.0,
  "temperatureMax": 8.0,
  "chauffeur": "Thomas Wallaert",
  "commentaire": "Vaccins",
  "destinataire": { "id": 1 }
}
```

### Relevés de température

| Méthode | Route                                     | Description                                  |
|---------|-------------------------------------------|----------------------------------------------|
| GET     | `/api/transports/{transportId}/releves`   | Relevés d'un transport, par ordre chronologique |
| POST    | `/api/transports/{transportId}/releves`   | Ajout d'un relevé (`horodatage`, `valeur`, `capteurId`) |

Un relevé dont la valeur sort de la plage `[temperatureMin, temperatureMax]` du transport fait passer celui-ci au statut `INCIDENT`.

### Destinataires

| Méthode | Route                              | Description                      |
|---------|------------------------------------|----------------------------------|
| GET     | `/api/destinataires`               | Liste des destinataires          |
| GET     | `/api/destinataires/{id}`          | Détail d'un destinataire         |
| POST    | `/api/destinataires`               | Création d'un destinataire       |
| GET     | `/api/destinataires/ville/{ville}` | Destinataires d'une ville        |

### Rapports

| Méthode | Route                          | Description                                           |
|---------|--------------------------------|-------------------------------------------------------|
| GET     | `/api/rapports/transports/{id}`| Rapport d'un transport (moyenne, min, max, dépassements) |
| GET     | `/api/rapports/synthese`       | Synthèse globale par statut et par type               |

## Scripts

| Commande                 | Effet                                        |
|--------------------------|----------------------------------------------|
| `docker compose up -d`   | Démarre PostgreSQL                           |
| `docker compose down`    | Arrête PostgreSQL (les données sont conservées dans le volume `santelog-pgdata`) |
| `mvn spring-boot:run`    | Lance l'application                          |
| `mvn test`               | Exécute la suite de tests                    |
| `mvn clean package`      | Produit le `.jar` dans `target/`             |

## Pipeline

Le workflow GitHub Actions `.github/workflows/build.yml` compile le projet à chaque push sur `main`.

## Licence

Logiciel interne, © Santélog. Tous droits réservés. Reproduction et diffusion interdites hors de l'entreprise.
