package br.com.estapar.parking.sector.sevice.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.estapar.parking.exceptions.ObjectNotFoundException;
import br.com.estapar.parking.sector.dto.SectorDTO;
import br.com.estapar.parking.sector.mapper.SectorMapper;
import br.com.estapar.parking.sector.model.Sector;
import br.com.estapar.parking.sector.repository.SectorRepository;
import br.com.estapar.parking.sector.sevice.SectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class SectorServiceImpl implements SectorService {

    private final SectorRepository repository;

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
}