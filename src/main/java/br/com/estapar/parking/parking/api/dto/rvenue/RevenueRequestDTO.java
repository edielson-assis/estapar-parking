package br.com.estapar.parking.parking.api.dto.rvenue;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

@Schema(description = "Request payload used to retrieve the revenue for a specific sector on a given date.")
public record RevenueRequestDTO(

    @Schema(
            description = "Date used to filter the parking revenue. Must be today or a past date.",
            example = "2025-01-01",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    LocalDate date,

    @Schema(
            description = "Sector identifier used to filter revenue. Must correspond to an existing parking sector.",
            example = "A",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Sector is required")
    String sector
) {}