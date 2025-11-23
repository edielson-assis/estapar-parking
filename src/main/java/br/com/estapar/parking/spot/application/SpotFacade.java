package br.com.estapar.parking.spot.application;

import java.util.List;

import br.com.estapar.parking.sector.domain.Sector;
import br.com.estapar.parking.spot.api.dto.SpotDTO;
import br.com.estapar.parking.spot.domain.Spot;

public interface SpotFacade {
    
    Spot createSpot(SpotDTO spotDTO, Sector sector);

    List<Long> findAllIds();

    long countOccupiedSpotsBySector(Sector sector);

    Spot findByCoordinates(Double lat, Double lng);

    void markOccupied(Spot spot);

    void markAvailable(Spot spot);
}