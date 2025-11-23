package br.com.estapar.parking.sector.application;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.estapar.parking.core.exceptions.ObjectNotFoundException;
import br.com.estapar.parking.sector.api.dto.SectorDTO;
import br.com.estapar.parking.sector.api.mapper.SectorMapper;
import br.com.estapar.parking.sector.domain.Sector;
import br.com.estapar.parking.sector.infrastructure.SectorRepository;
import br.com.estapar.parking.spot.application.SpotFacade;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@Log4j2
@RequiredArgsConstructor
@Service
public class SectorService implements SectorFacade {

    private final SectorRepository repository;
    private final SpotFacade spotFacade;

    @Transactional
    @Override
    public Sector createSector(SectorDTO sectorDTO) {
        log.info("Creating new sector '{}'.", sectorDTO.sector());
        var sector = SectorMapper.toEntity(sectorDTO);
        return repository.save(sector);
    }

    @Override
    public Sector findSectorByName(String name) {
        log.info("Verifying the sector's name: {}", name);
        return repository.findBySectorName(name).orElseThrow(() -> { 
            log.error("Sector '{}' not found", name);
            return new ObjectNotFoundException("Sector not found");
        });
    }

    @Override
    public List<String> findAllSector() {
        log.debug("Fetching existing sectors from database...");
        return repository.findAllSector();
    }

    @Override
    public double getOccupancyRate(Sector sector) {
        var occupiedSpots = spotFacade.countOccupiedSpotsBySector(sector);
        var existingSector = findSectorByName(sector.getSectorName());
        var maxCapacity = existingSector.getMaxCapacity();
        if (maxCapacity == 0) {
            return 0.0;
        }
        var occupancyRate = (double) occupiedSpots / maxCapacity;
        log.debug("Occupancy rate for sector '{}': {:.2f}%", sector.getSectorName(), occupancyRate * 100);
        return occupancyRate;
    }

    @Override
    public boolean isSectorFull(Sector sector) {
        log.info("Checking if sector '{}' is full.", sector.getSectorName());
        return getOccupancyRate(sector) >= 1.0;
    }

    @Override
    public boolean isSectorOpen(Sector sector) {
        log.info("Checking operating hours for sector '{}'...", sector.getSectorName());
        var existing = findSectorByName(sector.getSectorName());
        var open = existing.getOpenHour();
        var close = existing.getCloseHour();
        if (open == null || close == null) {
            // if no hours configured, assume always open
            return true;
        }
        try {
            LocalTime openT = LocalTime.parse(open);
            LocalTime closeT = LocalTime.parse(close);
            LocalTime now = LocalTime.now();
            if (openT.equals(closeT)) {
                return true; // open 24h
            }
            if (openT.isBefore(closeT) || openT.equals(closeT)) {
                return !now.isBefore(openT) && !now.isAfter(closeT);
            } else {
                // overnight period (e.g., open 20:00 close 06:00)
                return !now.isBefore(openT) || !now.isAfter(closeT);
            }
        } catch (DateTimeParseException e) {
            log.warn("Invalid open/close hour format for sector '{}': {} - {}", sector.getSectorName(), open, close);
            return true;
        }
    }
}