package br.com.estapar.parking.parking.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.estapar.parking.parking.api.dto.event.ParkingEventDTO;
import br.com.estapar.parking.parking.application.ParkingFacade;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/webhook")
public class ParkingController {
    
    private final ParkingFacade parkingFacade;

    @PostMapping
    public ResponseEntity<Void> receiveEvent(@RequestBody ParkingEventDTO event) {
        parkingFacade.processEvent(event);
        return ResponseEntity.ok().build();
    }
}