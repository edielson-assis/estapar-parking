package br.com.estapar.parking.spot.api.mapper;

import br.com.estapar.parking.sector.domain.Sector;
import br.com.estapar.parking.spot.api.dto.SpotDTO;
import br.com.estapar.parking.spot.domain.Spot;

public class SpotMapper {
    
    private SpotMapper() {}

    public static Spot toEntity(SpotDTO spotDTO, Sector sector) {
        var spot = new Spot();
        spot.setSpotId(spotDTO.id());
        spot.setSector(sector);
        spot.setLat(spotDTO.lat());
        spot.setLng(spotDTO.lng());
        spot.setIsOccupied(false);
        return spot;
    }
}