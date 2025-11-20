package br.com.estapar.parking.sector.mapper;

import br.com.estapar.parking.sector.dto.SectorDTO;
import br.com.estapar.parking.sector.model.Sector;

public class SectorMapper {
    
    private SectorMapper() {}

    public static Sector toEntity(SectorDTO sectorDTO) {
        var sector = new Sector();
        sector.setSectorName(sectorDTO.sector());
        sector.setBasePrice(sectorDTO.basePrice());
        sector.setMaxCapacity(sectorDTO.maxCapacity());
        return sector;
    }
}