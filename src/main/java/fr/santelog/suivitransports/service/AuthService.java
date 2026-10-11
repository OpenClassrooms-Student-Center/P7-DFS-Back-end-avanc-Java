package fr.santelog.suivitransports.service;

import fr.santelog.suivitransports.entity.Utilisateur;
import fr.santelog.suivitransports.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public Utilisateur authentifier(String login, String motDePasse) {
        Utilisateur utilisateur = utilisateurRepository.findByLogin(login).orElse(null);
        if (utilisateur == null) {
            log.info("Échec d'authentification : login=" + login + " motDePasse=" + motDePasse + " (utilisateur inconnu)");
            return null;
        }
        if (!utilisateur.isActif()) {
            log.info("Échec d'authentification : login=" + login + " motDePasse=" + motDePasse + " (compte inactif)");
            return null;
        }
        if (utilisateur.getMotDePasse() == null || !utilisateur.getMotDePasse().equals(motDePasse)) {
            log.info("Échec d'authentification : login=" + login + " motDePasse=" + motDePasse + " (mot de passe incorrect)");
            return null;
        }
        log.info("Authentification réussie pour " + login + " avec le rôle " + utilisateur.getRole());
        return utilisateur;
    }
}
