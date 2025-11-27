package br.com.estapar.parking.parking.api.dto.rvenue;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Response containing the calculated revenue for a given sector and date.")
public record RevenueResponseDTO(

    @Schema(
            description = "Total revenue amount calculated for the requested sector and date.",
            example = "152.75"
    )
    BigDecimal amount,

    @Schema(
            description = "Currency of the calculated revenue. Typically 'BRL'.",
            example = "BRL"
    )
    String currency,

    @Schema(
            description = "Timestamp of the calculation in ISO-8601 format.",
            example = "2025-01-01T23:59:59"
    )
    String timestamp
) {}