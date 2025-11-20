package br.com.estapar.parking.spot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.estapar.parking.spot.model.Spot;

public interface SpotRepository extends JpaRepository<Spot, Long> {

    @Query("select s.spotId from Spot s")
    List<Long> findAllIds();
}