package br.com.estapar.parking.sector.sevice;

import java.util.List;

import br.com.estapar.parking.sector.dto.SectorDTO;
import br.com.estapar.parking.sector.model.Sector;

public interface SectorService {
    
    Sector createSector(SectorDTO sectorDTO);

    Sector findSectorByName(String name);

    List<String> findAllSector();
}