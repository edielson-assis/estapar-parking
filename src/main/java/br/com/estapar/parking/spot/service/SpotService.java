package br.com.estapar.parking.spot.service;

import java.util.List;

import br.com.estapar.parking.sector.model.Sector;
import br.com.estapar.parking.spot.dto.SpotDTO;
import br.com.estapar.parking.spot.model.Spot;

public interface SpotService {
    
    Spot createSpot(SpotDTO spotDTO, Sector sector);

    List<Long> findAllIds();
}