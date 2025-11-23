package br.com.estapar.parking.parking.api.dto.rvenue;

import java.time.LocalDate;

public record RevenueRequest(
    
    LocalDate date,
    String sector
) {}