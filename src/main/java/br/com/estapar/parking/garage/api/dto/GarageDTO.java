package br.com.estapar.parking.garage.api.dto;

import java.util.List;

import br.com.estapar.parking.sector.api.dto.SectorDTO;
import br.com.estapar.parking.spot.api.dto.SpotDTO;

public record GarageDTO(List<SectorDTO> garage, List<SpotDTO> spots) {}