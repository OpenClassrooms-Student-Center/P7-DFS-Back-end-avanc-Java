package fr.santelog.suivitransports.service;

import fr.santelog.suivitransports.entity.Destinataire;
import fr.santelog.suivitransports.repository.DestinataireRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DestinataireService {

    private static final Logger log = LoggerFactory.getLogger(DestinataireService.class);

    @Autowired
    private DestinataireRepository destinataireRepository;

    public List<Destinataire> listerTous() {
        log.info("Chargement de la liste des destinataires");
        return destinataireRepository.findAll();
    }

    public Destinataire trouverParId(Long id) {
        Destinataire destinataire = destinataireRepository.findById(id).orElse(null);
        if (destinataire == null) {
            log.error("Destinataire " + id + " introuvable");
        }
        return destinataire;
    }

    public int creer(Destinataire destinataire) {
        if (destinataire.getNom() == null || destinataire.getNom().trim().isEmpty()) {
            log.info("Création de destinataire refusée : nom manquant");
            return -1;
        }
        destinataireRepository.save(destinataire);
        log.info("Destinataire créé : " + destinataire.getNom() + " - email=" + destinataire.getEmail()
                + " - telephone=" + destinataire.getTelephone() + " - contact=" + destinataire.getContactReferent());
        return 1;
    }

    public List<Destinataire> parVille(String ville) {
        log.info("Recherche des destinataires de la ville " + ville);
        return destinataireRepository.findByVilleIgnoreCase(ville);
    }
}
