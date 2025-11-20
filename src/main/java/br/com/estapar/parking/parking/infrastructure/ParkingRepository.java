package br.com.estapar.parking.parking.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.estapar.parking.parking.domain.Parking;

public interface ParkingRepository extends JpaRepository<Parking, Long> {}