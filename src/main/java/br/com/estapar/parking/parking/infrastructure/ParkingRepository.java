package br.com.estapar.parking.parking.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.estapar.parking.parking.domain.Parking;

public interface ParkingRepository extends JpaRepository<Parking, Long> {

  @Query("""
       SELECT p FROM Parking p
       WHERE p.licensePlate = :plate
         AND p.exitTime IS NULL
      """)
  Optional<Parking> findByLicensePlate(String plate);
}