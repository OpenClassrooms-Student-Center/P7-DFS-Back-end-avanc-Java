package fr.santelog.suivitransports.repository;

import fr.santelog.suivitransports.entity.StatutTransport;
import fr.santelog.suivitransports.entity.Transport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransportRepository extends JpaRepository<Transport, Long> {

    Optional<Transport> findByReference(String reference);

    List<Transport> findByStatut(StatutTransport statut);

    List<Transport> findByDestinataireId(Long destinataireId);
}
