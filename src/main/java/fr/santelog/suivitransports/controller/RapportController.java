package fr.santelog.suivitransports.controller;

import fr.santelog.suivitransports.service.RapportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/rapports")
@CrossOrigin(origins = "*")
public class RapportController {

    private static final Logger log = LoggerFactory.getLogger(RapportController.class);

    @Autowired
    private RapportService rapportService;

    @GetMapping("/transports/{id}")
    public ResponseEntity<?> rapportTransport(@PathVariable Long id) {
        try {
            Map<String, Object> rapport = rapportService.rapportTransport(id);
            if (rapport == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(rapport);
        } catch (Exception e) {
            log.error("Erreur lors de la génération du rapport du transport " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }

    @GetMapping("/synthese")
    public ResponseEntity<?> synthese() {
        try {
            return ResponseEntity.ok(rapportService.synthese());
        } catch (Exception e) {
            log.error("Erreur lors de la génération de la synthèse", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }
}
