package fr.santelog.suivitransports.controller;

import fr.santelog.suivitransports.entity.Utilisateur;
import fr.santelog.suivitransports.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> corps) {
        try {
            String login = corps.get("login");
            String motDePasse = corps.get("motDePasse");
            Utilisateur utilisateur = authService.authentifier(login, motDePasse);
            if (utilisateur == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiants invalides");
            }
            String token = utilisateur.getLogin() + "-" + System.currentTimeMillis();
            Map<String, Object> reponse = new LinkedHashMap<>();
            reponse.put("token", token);
            reponse.put("utilisateur", utilisateur);
            log.info("Jeton émis pour " + login + " : " + token);
            return ResponseEntity.ok(reponse);
        } catch (Exception e) {
            log.error("Erreur lors de l'authentification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }
}
