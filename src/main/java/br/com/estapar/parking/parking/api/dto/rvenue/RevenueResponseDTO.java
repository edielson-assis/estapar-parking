package br.com.estapar.parking.parking.api.dto.rvenue;

import java.math.BigDecimal;

public record RevenueResponseDTO(BigDecimal amount, String currency, String timestamp) {}