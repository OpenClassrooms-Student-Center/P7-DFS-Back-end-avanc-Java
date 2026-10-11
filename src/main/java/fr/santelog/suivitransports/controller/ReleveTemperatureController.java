package fr.santelog.suivitransports.controller;

import fr.santelog.suivitransports.entity.ReleveTemperature;
import fr.santelog.suivitransports.service.ReleveTemperatureService;
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
@RequestMapping("/api/transports/{transportId}/releves")
@CrossOrigin(origins = "*")
public class ReleveTemperatureController {

    private static final Logger log = LoggerFactory.getLogger(ReleveTemperatureController.class);

    @Autowired
    private ReleveTemperatureService releveTemperatureService;

    @GetMapping
    public ResponseEntity<?> lister(@PathVariable Long transportId) {
        return ResponseEntity.ok(releveTemperatureService.releves(transportId));
    }

    @PostMapping
    public ResponseEntity<?> ajouter(@PathVariable Long transportId, @RequestBody ReleveTemperature releve) {
        try {
            int code = releveTemperatureService.ajouter(transportId, releve);
            if (code == -1) {
                return ResponseEntity.notFound().build();
            }
            if (code == -2) {
                return ResponseEntity.badRequest().body("La valeur du relevé est obligatoire");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(releve);
        } catch (Exception e) {
            log.error("Erreur lors de l'ajout d'un relevé sur le transport " + transportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }
}
