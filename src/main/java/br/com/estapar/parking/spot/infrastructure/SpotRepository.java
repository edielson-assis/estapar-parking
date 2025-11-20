package br.com.estapar.parking.spot.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.estapar.parking.spot.domain.Spot;

public interface SpotRepository extends JpaRepository<Spot, Long> {

    @Query("select s.spotId from Spot s")
    List<Long> findAllIds();
}