package br.com.estapar.parking.parking.api.dto.rvenue;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record RevenueRequestDTO(

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    LocalDate date, 
    
    @NotBlank(message = "Sector is required")
    String sector
) {}