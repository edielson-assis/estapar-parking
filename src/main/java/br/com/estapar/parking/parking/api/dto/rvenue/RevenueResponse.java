package br.com.estapar.parking.parking.api.dto.rvenue;

import java.time.Instant;

public record RevenueResponse(
    
    double amount,
    String currency,
    Instant timestamp
) {}