package fr.santelog.suivitransports.repository;

import fr.santelog.suivitransports.entity.Destinataire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DestinataireRepository extends JpaRepository<Destinataire, Long> {

    List<Destinataire> findByVilleIgnoreCase(String ville);
}
