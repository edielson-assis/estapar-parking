package br.com.estapar.parking.parking.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.estapar.parking.parking.api.doc.ParkingControllerDoc;
import br.com.estapar.parking.parking.api.dto.event.ParkingEventDTO;
import br.com.estapar.parking.parking.api.dto.rvenue.RevenueRequestDTO;
import br.com.estapar.parking.parking.api.dto.rvenue.RevenueResponseDTO;
import br.com.estapar.parking.parking.application.ParkingFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ParkingController implements ParkingControllerDoc {
    
    private final ParkingFacade parkingFacade;

    @PostMapping("/webhook")
    public ResponseEntity<Void> receiveEvent(@RequestBody ParkingEventDTO event) {
        parkingFacade.processEvent(event);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/revenue")
    public ResponseEntity<RevenueResponseDTO> revenueBySector(@RequestBody @Valid RevenueRequestDTO request) {
        var response = parkingFacade.calculateRevenueBySector(request);
        return ResponseEntity.ok(response);
    }
}