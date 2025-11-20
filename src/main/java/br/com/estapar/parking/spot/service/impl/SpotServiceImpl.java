package br.com.estapar.parking.spot.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.estapar.parking.sector.model.Sector;
import br.com.estapar.parking.spot.dto.SpotDTO;
import br.com.estapar.parking.spot.mapper.SpotMapper;
import br.com.estapar.parking.spot.model.Spot;
import br.com.estapar.parking.spot.repository.SpotRepository;
import br.com.estapar.parking.spot.service.SpotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class SpotServiceImpl implements SpotService {

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
}