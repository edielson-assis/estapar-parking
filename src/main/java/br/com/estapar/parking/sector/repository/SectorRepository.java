package br.com.estapar.parking.sector.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.estapar.parking.sector.model.Sector;

public interface SectorRepository extends JpaRepository<Sector, Long> {}