package br.com.estapar.parking.sector.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.estapar.parking.sector.domain.Sector;

public interface SectorRepository extends JpaRepository<Sector, Long> {

    Optional<Sector> findBySectorName(String name);

    @Query("select s.sectorName from Sector s")
    List<String> findAllSector();
}