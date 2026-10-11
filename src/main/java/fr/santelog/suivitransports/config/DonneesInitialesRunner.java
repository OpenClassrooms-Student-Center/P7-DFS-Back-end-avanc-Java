package fr.santelog.suivitransports.config;

import fr.santelog.suivitransports.entity.Destinataire;
import fr.santelog.suivitransports.entity.ReleveTemperature;
import fr.santelog.suivitransports.entity.StatutTransport;
import fr.santelog.suivitransports.entity.Transport;
import fr.santelog.suivitransports.entity.TypeProduit;
import fr.santelog.suivitransports.entity.Utilisateur;
import fr.santelog.suivitransports.repository.DestinataireRepository;
import fr.santelog.suivitransports.repository.ReleveTemperatureRepository;
import fr.santelog.suivitransports.repository.TransportRepository;
import fr.santelog.suivitransports.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DonneesInitialesRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DonneesInitialesRunner.class);

    @Autowired
    private TransportRepository transportRepository;

    @Autowired
    private DestinataireRepository destinataireRepository;

    @Autowired
    private ReleveTemperatureRepository releveTemperatureRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public void run(String... args) {
        if (transportRepository.count() > 0) {
            log.info("Base déjà alimentée, aucune donnée de démarrage insérée");
            return;
        }

        log.info("Base vide : insertion des données de démarrage");

        creerUtilisateur("admin", "Admin2021!", "Administrateur Santélog", "ADMIN");
        creerUtilisateur("mdurand", "marie123", "Marie Durand", "OPERATEUR");
        creerUtilisateur("lecture", "lecture", "Compte consultation", "LECTEUR");

        Destinataire chuLille = creerDestinataire("CHU de Lille", "2 avenue Oscar Lambret", "59000", "Lille",
                "pharmacie.centrale@chu-lille.example", "03 20 44 59 62", "Dr Nathalie Lefebvre");
        Destinataire pharmacieArras = creerDestinataire("Pharmacie du Beffroi", "14 place des Héros", "62000", "Arras",
                "contact@pharmacie-beffroi.example", "03 21 71 08 15", "Julien Caron");
        Destinataire laboBiopath = creerDestinataire("Laboratoire Biopath", "7 rue de l'Hôpital", "59300", "Valenciennes",
                "reception@biopath.example", "03 27 46 12 90", "Sophie Delannoy");
        Destinataire cliniqueFlandres = creerDestinataire("Clinique des Flandres", "56 boulevard de la République", "59140", "Dunkerque",
                "logistique@clinique-flandres.example", "03 28 59 33 41", "Karim Benali");

        Transport t1 = creerTransport("TRP-2024-0001", TypeProduit.MEDICAMENT, StatutTransport.LIVRE,
                LocalDateTime.of(2024, 3, 4, 6, 30), LocalDateTime.of(2024, 3, 4, 9, 0),
                2.0, 8.0, "Thomas Wallaert", "Vaccins - livraison pharmacie centrale", chuLille);
        ajouterReleve(t1, LocalDateTime.of(2024, 3, 4, 6, 45), 4.2, "CAP-017");
        ajouterReleve(t1, LocalDateTime.of(2024, 3, 4, 7, 45), 4.6, "CAP-017");
        ajouterReleve(t1, LocalDateTime.of(2024, 3, 4, 8, 50), 5.1, "CAP-017");
        transportRepository.save(t1);

        Transport t2 = creerTransport("TRP-2024-0002", TypeProduit.ECHANTILLON_BIOLOGIQUE, StatutTransport.EN_COURS,
                LocalDateTime.of(2024, 3, 5, 14, 0), LocalDateTime.of(2024, 3, 5, 16, 30),
                2.0, 8.0, "Amina Ouali", "Prélèvements sanguins - urgence relative", laboBiopath);
        ajouterReleve(t2, LocalDateTime.of(2024, 3, 5, 14, 10), 3.8, "CAP-042");
        ajouterReleve(t2, LocalDateTime.of(2024, 3, 5, 15, 10), 4.4, "CAP-042");
        ajouterReleve(t2, LocalDateTime.of(2024, 3, 5, 16, 5), 4.9, "CAP-042");
        transportRepository.save(t2);

        Transport t3 = creerTransport("TRP-2024-0003", TypeProduit.DISPOSITIF_MEDICAL, StatutTransport.PLANIFIE,
                LocalDateTime.of(2024, 3, 7, 8, 0), LocalDateTime.of(2024, 3, 7, 10, 0),
                15.0, 25.0, "Thomas Wallaert", "Pompes à perfusion - 12 unités", pharmacieArras);
        transportRepository.save(t3);

        Transport t4 = creerTransport("TRP-2024-0004", TypeProduit.MEDICAMENT, StatutTransport.INCIDENT,
                LocalDateTime.of(2024, 3, 5, 9, 15), LocalDateTime.of(2024, 3, 5, 12, 0),
                2.0, 8.0, "Pierre Vandamme", "Insuline - panne groupe froid signalée par le chauffeur", cliniqueFlandres);
        ajouterReleve(t4, LocalDateTime.of(2024, 3, 5, 9, 30), 5.0, "CAP-023");
        ajouterReleve(t4, LocalDateTime.of(2024, 3, 5, 10, 30), 7.6, "CAP-023");
        ajouterReleve(t4, LocalDateTime.of(2024, 3, 5, 11, 30), 11.4, "CAP-023");
        transportRepository.save(t4);

        Transport t5 = creerTransport("TRP-2024-0005", TypeProduit.ECHANTILLON_BIOLOGIQUE, StatutTransport.LIVRE,
                LocalDateTime.of(2024, 3, 6, 5, 45), LocalDateTime.of(2024, 3, 6, 7, 30),
                -25.0, -15.0, "Amina Ouali", "Échantillons congelés pour anatomopathologie", chuLille);
        ajouterReleve(t5, LocalDateTime.of(2024, 3, 6, 6, 0), -21.3, "CAP-008");
        ajouterReleve(t5, LocalDateTime.of(2024, 3, 6, 7, 15), -19.8, "CAP-008");
        transportRepository.save(t5);

        Transport t6 = creerTransport("TRP-2024-0006", TypeProduit.DISPOSITIF_MEDICAL, StatutTransport.PLANIFIE,
                LocalDateTime.of(2024, 3, 8, 13, 30), LocalDateTime.of(2024, 3, 8, 15, 0),
                15.0, 25.0, "Pierre Vandamme", "Consommables de prélèvement", laboBiopath);
        transportRepository.save(t6);

        log.info("Données de démarrage insérées : {} utilisateurs, {} destinataires, {} transports, {} relevés",
                utilisateurRepository.count(), destinataireRepository.count(),
                transportRepository.count(), releveTemperatureRepository.count());
    }

    private void creerUtilisateur(String login, String motDePasse, String nomComplet, String role) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin(login);
        utilisateur.setMotDePasse(motDePasse);
        utilisateur.setNomComplet(nomComplet);
        utilisateur.setRole(role);
        utilisateur.setActif(true);
        utilisateurRepository.save(utilisateur);
    }

    private Destinataire creerDestinataire(String nom, String adresse, String codePostal, String ville,
                                           String email, String telephone, String contactReferent) {
        Destinataire destinataire = new Destinataire();
        destinataire.setNom(nom);
        destinataire.setAdresse(adresse);
        destinataire.setCodePostal(codePostal);
        destinataire.setVille(ville);
        destinataire.setEmail(email);
        destinataire.setTelephone(telephone);
        destinataire.setContactReferent(contactReferent);
        return destinataireRepository.save(destinataire);
    }

    private Transport creerTransport(String reference, TypeProduit typeProduit, StatutTransport statut,
                                     LocalDateTime dateDepart, LocalDateTime dateArriveePrevue,
                                     Double temperatureMin, Double temperatureMax, String chauffeur,
                                     String commentaire, Destinataire destinataire) {
        Transport transport = new Transport();
        transport.setReference(reference);
        transport.setTypeProduit(typeProduit);
        transport.setStatut(statut);
        transport.setDateDepart(dateDepart);
        transport.setDateArriveePrevue(dateArriveePrevue);
        transport.setTemperatureMin(temperatureMin);
        transport.setTemperatureMax(temperatureMax);
        transport.setChauffeur(chauffeur);
        transport.setCommentaire(commentaire);
        transport.setDestinataire(destinataire);
        return transport;
    }

    private void ajouterReleve(Transport transport, LocalDateTime horodatage, Double valeur, String capteurId) {
        ReleveTemperature releve = new ReleveTemperature();
        releve.setTransport(transport);
        releve.setHorodatage(horodatage);
        releve.setValeur(valeur);
        releve.setCapteurId(capteurId);
        transport.getReleves().add(releve);
    }
}
