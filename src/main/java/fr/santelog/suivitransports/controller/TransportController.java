package fr.santelog.suivitransports.controller;

import fr.santelog.suivitransports.entity.Transport;
import fr.santelog.suivitransports.service.TransportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/transports")
@CrossOrigin(origins = "*")
public class TransportController {

    private static final Logger log = LoggerFactory.getLogger(TransportController.class);

    @Autowired
    private TransportService transportService;

    @GetMapping
    public ResponseEntity<?> lister() {
        return ResponseEntity.ok(transportService.listerTous());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        Transport transport = transportService.trouverParId(id);
        if (transport == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transport);
    }

    @PostMapping
    public ResponseEntity<?> creer(@RequestBody Transport transport) {
        try {
            int code = transportService.creer(transport);
            if (code == -1) {
                return ResponseEntity.badRequest().body("La référence du transport est obligatoire");
            }
            if (code == -2) {
                return ResponseEntity.badRequest().body("Un transport porte déjà la référence " + transport.getReference());
            }
            if (code == -3) {
                return ResponseEntity.badRequest().body("Destinataire introuvable");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(transport);
        } catch (Exception e) {
            log.error("Erreur lors de la création du transport", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<?> changerStatut(@PathVariable Long id, @RequestBody Map<String, String> corps) {
        try {
            int code = transportService.changerStatut(id, corps.get("statut"));
            if (code == -1) {
                return ResponseEntity.notFound().build();
            }
            if (code == -2) {
                return ResponseEntity.badRequest().body("Statut inconnu : " + corps.get("statut"));
            }
            return ResponseEntity.ok(transportService.trouverParId(id));
        } catch (Exception e) {
            log.error("Erreur lors du changement de statut du transport " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimer(@PathVariable Long id) {
        try {
            int code = transportService.supprimer(id);
            if (code == -1) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du transport " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }

    @GetMapping("/recherche")
    public ResponseEntity<?> rechercher(@RequestParam("q") String q) {
        return ResponseEntity.ok(transportService.rechercherParReference(q));
    }
}
