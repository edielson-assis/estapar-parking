package br.com.estapar.parking.parking.api.dto;

import java.time.LocalDate;

public record RevenueRequestDTO(LocalDate date, String sector) {}