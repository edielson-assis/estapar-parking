package br.com.estapar.parking.parking.api.dto.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ParkingEventDTO(
        
    @JsonProperty("license_plate")
    String licensePlate,

    Double lat,
    Double lng,

    @JsonProperty("entry_time")
    LocalDateTime entryTime,
    
    @JsonProperty("exit_time")
    LocalDateTime exitTime,
    
    @JsonProperty("event_type")
    String eventType
) {}