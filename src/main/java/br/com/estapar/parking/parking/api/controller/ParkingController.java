package br.com.estapar.parking.parking.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.estapar.parking.parking.api.dto.RevenueRequestDTO;
import br.com.estapar.parking.parking.api.dto.RevenueResponseDTO;
import br.com.estapar.parking.parking.api.dto.event.ParkingEventDTO;
import br.com.estapar.parking.parking.application.ParkingFacade;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ParkingController {
    
    private final ParkingFacade parkingFacade;

    @PostMapping("/webhook")
    public ResponseEntity<Void> receiveEvent(@RequestBody ParkingEventDTO event) {
        parkingFacade.processEvent(event);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/revenue")
    public ResponseEntity<RevenueResponseDTO> revenueBySector(@RequestBody RevenueRequestDTO request) {
        var response = parkingFacade.calculateRevenueBySector(request);
        return ResponseEntity.ok(response);
    }
}