package fr.santelog.suivitransports.service;

import fr.santelog.suivitransports.entity.ReleveTemperature;
import fr.santelog.suivitransports.entity.Transport;
import fr.santelog.suivitransports.repository.TransportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class RapportService {

    private static final Logger log = LoggerFactory.getLogger(RapportService.class);

    @Autowired
    private TransportRepository transportRepository;

    public Map<String, Object> rapportTransport(Long transportId) {
        Transport transport = transportRepository.findById(transportId).orElse(null);
        if (transport == null) {
            log.error("Rapport demandé pour un transport introuvable : " + transportId);
            return null;
        }
        log.info("Génération du rapport pour " + transport);

        Map<String, Object> rapport = new LinkedHashMap<>();
        rapport.put("reference", transport.getReference());
        rapport.put("statut", transport.getStatut() != null ? transport.getStatut().name() : null);
        rapport.put("destinataire", transport.getDestinataire() != null ? transport.getDestinataire().getNom() : null);

        List<ReleveTemperature> releves = transport.getReleves();
        rapport.put("nombreReleves", releves.size());

        double somme = 0.0;
        Double min = null;
        Double max = null;
        int depassements = 0;
        for (ReleveTemperature releve : releves) {
            if (releve.getValeur() == null) {
                continue;
            }
            double v = releve.getValeur();
            somme = somme + v;
            if (min == null || v < min) {
                min = v;
            }
            if (max == null || v > max) {
                max = v;
            }
            if (transport.getTemperatureMin() != null && v < transport.getTemperatureMin()) {
                depassements++;
            } else if (transport.getTemperatureMax() != null && v > transport.getTemperatureMax()) {
                depassements++;
            }
        }
        Double moyenne = null;
        if (!releves.isEmpty()) {
            moyenne = Math.round((somme / releves.size()) * 10.0) / 10.0;
        }
        rapport.put("temperatureMoyenne", moyenne);
        rapport.put("temperatureMinRelevee", min);
        rapport.put("temperatureMaxRelevee", max);
        rapport.put("nombreDepassements", depassements);

        String resume = "";
        resume = resume + "Transport " + transport.getReference();
        resume = resume + " (" + transport.getTypeProduit() + ")";
        if (transport.getDestinataire() != null) {
            resume = resume + " à destination de " + transport.getDestinataire().getNom();
            resume = resume + " - " + transport.getDestinataire().getVille();
        }
        resume = resume + ", statut " + transport.getStatut() + ". ";
        resume = resume + "Plage attendue : " + transport.getTemperatureMin() + " °C à " + transport.getTemperatureMax() + " °C. ";
        int nb = 0;
        double total = 0.0;
        double plusBas = Double.MAX_VALUE;
        double plusHaut = -Double.MAX_VALUE;
        int horsPlage = 0;
        for (ReleveTemperature releve : releves) {
            if (releve.getValeur() == null) {
                continue;
            }
            nb++;
            total = total + releve.getValeur();
            if (releve.getValeur() < plusBas) {
                plusBas = releve.getValeur();
            }
            if (releve.getValeur() > plusHaut) {
                plusHaut = releve.getValeur();
            }
            if (transport.getTemperatureMin() != null && releve.getValeur() < transport.getTemperatureMin()) {
                horsPlage++;
            }
            if (transport.getTemperatureMax() != null && releve.getValeur() > transport.getTemperatureMax()) {
                horsPlage++;
            }
        }
        if (nb == 0) {
            resume = resume + "Aucun relevé de température enregistré.";
        } else {
            resume = resume + nb + " relevé(s), moyenne " + (Math.round((total / nb) * 10.0) / 10.0) + " °C";
            resume = resume + " (min " + plusBas + " °C, max " + plusHaut + " °C). ";
            if (horsPlage == 0) {
                resume = resume + "Aucun dépassement de plage constaté.";
            } else if (horsPlage == 1) {
                resume = resume + "1 dépassement de plage constaté.";
            } else {
                resume = resume + horsPlage + " dépassements de plage constatés.";
            }
        }
        rapport.put("resume", resume);
        return rapport;
    }

    public Map<String, Object> synthese() {
        log.info("Génération de la synthèse globale");
        List<Transport> transports = transportRepository.findAll();

        Map<String, Long> parStatut = new TreeMap<>();
        Map<String, Long> parType = new TreeMap<>();
        List<String> enIncident = new ArrayList<>();

        for (Transport transport : transports) {
            String statut = transport.getStatut() != null ? transport.getStatut().name() : "INCONNU";
            parStatut.put(statut, parStatut.getOrDefault(statut, 0L) + 1);
            String type = transport.getTypeProduit() != null ? transport.getTypeProduit().name() : "INCONNU";
            parType.put(type, parType.getOrDefault(type, 0L) + 1);
            if ("INCIDENT".equals(statut)) {
                enIncident.add(transport.getReference());
            }
        }

        Map<String, Object> synthese = new LinkedHashMap<>();
        synthese.put("total", transports.size());
        synthese.put("parStatut", parStatut);
        synthese.put("parType", parType);
        synthese.put("enIncident", enIncident);
        return synthese;
    }
}
