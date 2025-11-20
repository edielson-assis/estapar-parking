package br.com.estapar.parking.garage.dto;

import java.util.List;

import br.com.estapar.parking.sector.dto.SectorDTO;
import br.com.estapar.parking.spot.dto.SpotDTO;

public record GarageDTO(List<SectorDTO> garage, List<SpotDTO> spots) {}