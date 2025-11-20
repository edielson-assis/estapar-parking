package br.com.estapar.parking.sector.application;

import java.util.List;

import br.com.estapar.parking.sector.api.dto.SectorDTO;
import br.com.estapar.parking.sector.domain.Sector;

public interface SectorFacade {
    
    Sector createSector(SectorDTO sectorDTO);

    Sector findSectorByName(String name);

    List<String> findAllSector();
}