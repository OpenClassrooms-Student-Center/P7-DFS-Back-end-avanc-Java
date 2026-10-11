package fr.santelog.suivitransports.service;

import fr.santelog.suivitransports.entity.Destinataire;
import fr.santelog.suivitransports.entity.StatutTransport;
import fr.santelog.suivitransports.entity.Transport;
import fr.santelog.suivitransports.repository.DestinataireRepository;
import fr.santelog.suivitransports.repository.TransportRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportService {

    private static final Logger log = LoggerFactory.getLogger(TransportService.class);

    @Autowired
    private TransportRepository transportRepository;

    @Autowired
    private DestinataireRepository destinataireRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Transport> listerTous() {
        log.info("Chargement de tous les transports");
        return transportRepository.findAll();
    }

    public Transport trouverParId(Long id) {
        Transport transport = transportRepository.findById(id).orElse(null);
        if (transport == null) {
            log.error("Transport " + id + " introuvable");
        }
        return transport;
    }

    public int creer(Transport transport) {
        if (transport.getReference() == null || transport.getReference().trim().isEmpty()) {
            log.info("Création de transport refusée : référence manquante");
            return -1;
        }
        if (transportRepository.findByReference(transport.getReference()).isPresent()) {
            log.info("Création de transport refusée : référence déjà utilisée " + transport.getReference());
            return -2;
        }
        if (transport.getDestinataire() != null && transport.getDestinataire().getId() != null) {
            Long destinataireId = transport.getDestinataire().getId();
            Destinataire destinataire = destinataireRepository.findById(destinataireId).orElse(null);
            if (destinataire == null) {
                log.error("Création de transport refusée : destinataire " + destinataireId + " introuvable");
                return -3;
            }
            transport.setDestinataire(destinataire);
        }
        if (transport.getStatut() == null) {
            transport.setStatut(StatutTransport.PLANIFIE);
        }
        transportRepository.save(transport);
        log.info("Transport créé : " + transport);
        return 1;
    }

    public int changerStatut(Long id, String nouveauStatut) {
        Transport transport = transportRepository.findById(id).orElse(null);
        if (transport == null) {
            log.error("Changement de statut impossible : transport " + id + " introuvable");
            return -1;
        }
        StatutTransport statut;
        try {
            statut = StatutTransport.valueOf(nouveauStatut);
        } catch (Exception e) {
            log.info("Statut inconnu : " + nouveauStatut);
            return -2;
        }
        transport.setStatut(statut);
        transportRepository.save(transport);
        log.info("Transport " + transport.getReference() + " passé au statut " + statut);
        return 1;
    }

    public int supprimer(Long id) {
        Transport transport = transportRepository.findById(id).orElse(null);
        if (transport == null) {
            log.error("Suppression impossible : transport " + id + " introuvable");
            return -1;
        }
        transportRepository.delete(transport);
        log.info("Transport " + transport.getReference() + " supprimé");
        return 1;
    }

    @SuppressWarnings("unchecked")
    public List<Transport> rechercherParReference(String fragment) {
        String sql = "SELECT * FROM transport WHERE reference LIKE '%" + fragment + "%'";
        log.info("Recherche de transports : " + sql);
        return entityManager.createNativeQuery(sql, Transport.class).getResultList();
    }
}
