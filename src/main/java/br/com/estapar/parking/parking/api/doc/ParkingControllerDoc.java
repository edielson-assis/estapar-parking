package br.com.estapar.parking.parking.api.doc;

import org.springframework.http.ResponseEntity;

import br.com.estapar.parking.parking.api.dto.event.ParkingEventDTO;
import br.com.estapar.parking.parking.api.dto.rvenue.RevenueRequestDTO;
import br.com.estapar.parking.parking.api.dto.rvenue.RevenueResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Parking", description = "Endpoints handling parking events and revenue calculation")
public interface ParkingControllerDoc {

    @Operation(
            summary = "Receives parking events sent by the simulator",
            description = "Handles ENTRY, PARKED and EXIT events received from the garage simulator.",
            tags = {"Parking"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Event processed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid payload - Missing or incorrect fields", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Sector not found", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Conflict - Event already processed or invalid state transition", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    ResponseEntity<Void> receiveEvent(ParkingEventDTO event);

    @Operation(
            summary = "Returns total revenue by sector",
            description = "Calculates the revenue for a specific parking sector on a given date.",
            tags = {"Parking"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Revenue successfully calculated",
                            content = @Content(schema = @Schema(implementation = RevenueResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Sector not found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    ResponseEntity<RevenueResponseDTO> revenueBySector(RevenueRequestDTO request);
}