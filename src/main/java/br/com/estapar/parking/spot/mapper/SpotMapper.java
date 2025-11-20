package br.com.estapar.parking.spot.mapper;

import br.com.estapar.parking.sector.model.Sector;
import br.com.estapar.parking.spot.dto.SpotDTO;
import br.com.estapar.parking.spot.model.Spot;

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