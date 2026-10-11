package fr.santelog.suivitransports.repository;

import fr.santelog.suivitransports.entity.ReleveTemperature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReleveTemperatureRepository extends JpaRepository<ReleveTemperature, Long> {

    List<ReleveTemperature> findByTransportIdOrderByHorodatageAsc(Long transportId);
}
