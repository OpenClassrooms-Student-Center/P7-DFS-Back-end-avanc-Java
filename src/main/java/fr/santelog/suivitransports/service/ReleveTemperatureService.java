package fr.santelog.suivitransports.service;

import fr.santelog.suivitransports.entity.ReleveTemperature;
import fr.santelog.suivitransports.entity.StatutTransport;
import fr.santelog.suivitransports.entity.Transport;
import fr.santelog.suivitransports.repository.ReleveTemperatureRepository;
import fr.santelog.suivitransports.repository.TransportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReleveTemperatureService {

    private static final Logger log = LoggerFactory.getLogger(ReleveTemperatureService.class);

    @Autowired
    private ReleveTemperatureRepository releveTemperatureRepository;

    @Autowired
    private TransportRepository transportRepository;

    public List<ReleveTemperature> releves(Long transportId) {
        log.info("Chargement des relevés du transport " + transportId);
        return releveTemperatureRepository.findByTransportIdOrderByHorodatageAsc(transportId);
    }

    public int ajouter(Long transportId, ReleveTemperature releve) {
        Transport transport = transportRepository.findById(transportId).orElse(null);
        if (transport == null) {
            log.error("Ajout de relevé impossible : transport " + transportId + " introuvable");
            return -1;
        }
        if (releve.getValeur() == null) {
            log.info("Ajout de relevé refusé : valeur manquante");
            return -2;
        }
        if (releve.getHorodatage() == null) {
            releve.setHorodatage(LocalDateTime.now());
        }
        releve.setTransport(transport);
        releveTemperatureRepository.save(releve);
        log.info("Relevé enregistré sur " + transport.getReference() + " : " + releve.getValeur() + " °C (" + releve.getCapteurId() + ")");

        boolean horsPlage = false;
        if (transport.getTemperatureMin() != null && releve.getValeur() < transport.getTemperatureMin()) {
            horsPlage = true;
        }
        if (transport.getTemperatureMax() != null && releve.getValeur() > transport.getTemperatureMax()) {
            horsPlage = true;
        }
        if (horsPlage) {
            transport.setStatut(StatutTransport.INCIDENT);
            transportRepository.save(transport);
            log.info("Transport " + transport.getReference() + " passé en INCIDENT suite au relevé " + releve.getValeur()
                    + " °C hors plage [" + transport.getTemperatureMin() + " ; " + transport.getTemperatureMax() + "]");
        }
        return 1;
    }
}
