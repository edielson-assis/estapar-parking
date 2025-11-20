package br.com.estapar.parking.parking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.estapar.parking.parking.model.Parking;

public interface ParkingRepository extends JpaRepository<Parking, Long> {}