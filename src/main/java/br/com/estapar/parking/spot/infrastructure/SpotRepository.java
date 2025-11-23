package br.com.estapar.parking.spot.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.estapar.parking.sector.domain.Sector;
import br.com.estapar.parking.spot.domain.Spot;

public interface SpotRepository extends JpaRepository<Spot, Long> {

    @Query("select s.spotId from Spot s")
    List<Long> findAllIds();

    long countBySectorAndIsOccupiedTrue(Sector sector);

    Optional<Spot> findByLatAndLng(Double lat, Double lng);
}