package br.com.estapar.parking.sector.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SectorDTO(String sector, @JsonProperty("base_price") double basePrice, @JsonProperty("max_capacity") int maxCapacity) {}