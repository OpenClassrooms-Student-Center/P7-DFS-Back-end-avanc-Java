package fr.santelog.suivitransports.controller;

import fr.santelog.suivitransports.entity.Destinataire;
import fr.santelog.suivitransports.service.DestinataireService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/destinataires")
@CrossOrigin(origins = "*")
public class DestinataireController {

    private static final Logger log = LoggerFactory.getLogger(DestinataireController.class);

    @Autowired
    private DestinataireService destinataireService;

    @GetMapping
    public ResponseEntity<?> lister() {
        return ResponseEntity.ok(destinataireService.listerTous());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        Destinataire destinataire = destinataireService.trouverParId(id);
        if (destinataire == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(destinataire);
    }

    @PostMapping
    public ResponseEntity<?> creer(@RequestBody Destinataire destinataire) {
        try {
            int code = destinataireService.creer(destinataire);
            if (code == -1) {
                return ResponseEntity.badRequest().body("Le nom du destinataire est obligatoire");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(destinataire);
        } catch (Exception e) {
            log.error("Erreur lors de la création du destinataire", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }

    @GetMapping("/ville/{ville}")
    public ResponseEntity<?> parVille(@PathVariable String ville) {
        return ResponseEntity.ok(destinataireService.parVille(ville));
    }
}
