package br.com.estapar.parking.spot.application;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.estapar.parking.sector.domain.Sector;
import br.com.estapar.parking.spot.api.dto.SpotDTO;
import br.com.estapar.parking.spot.api.mapper.SpotMapper;
import br.com.estapar.parking.spot.domain.Spot;
import br.com.estapar.parking.spot.infrastructure.SpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class SpotService implements SpotFacade {

    private final SpotRepository repository;
    
    @Override
    public Spot createSpot(SpotDTO spotDTO, Sector sector) {
        log.info("Creating new spot with ID {} in sector '{}'.", spotDTO.id(), spotDTO.sector());
        var spot = SpotMapper.toEntity(spotDTO, sector);
        return repository.save(spot);
    }

    @Override
    public List<Long> findAllIds() {
        log.debug("Fetching existing spot IDs from database...");
        return repository.findAllIds();
    }

    @Override
    public long countOccupiedSpotsBySector(Sector sector) {
        log.debug("Counting occupied spots in sector '{}'.", sector.getSectorName());
        return repository.countBySectorAndIsOccupiedTrue(sector);
    }

    @Override
    public Spot findByCoordinates(Double lat, Double lng) {
        log.debug("Searching for spot at coordinates (lat: {}, lng: {}).", lat, lng);
        return repository.findByLatAndLng(lat, lng).orElseThrow(() -> { 
            log.error("No spot found at coordinates (lat: {}, lng: {}).", lat, lng);
            throw new IllegalArgumentException("No spot found at coordinates (lat: " + lat + ", lng: " + lng + ").");
        });
    }

    @Override
    public void markOccupied(Spot spot) {
        log.info("Marking spot ID {} as occupied.", spot.getSpotId());
        spot.setIsOccupied(true);
        repository.save(spot);
    }

    @Override
    public void markAvailable(Spot spot) {
        log.info("Marking spot ID {} as available.", spot.getSpotId());
        spot.setIsOccupied(false);
        repository.save(spot);
    }
}