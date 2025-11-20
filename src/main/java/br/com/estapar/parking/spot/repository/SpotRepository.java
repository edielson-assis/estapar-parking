package br.com.estapar.parking.spot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.estapar.parking.spot.model.Spot;

public interface SpotRepository extends JpaRepository<Spot, Long> {}