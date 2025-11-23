package br.com.estapar.parking.parking.application;

import br.com.estapar.parking.parking.api.dto.event.ParkingEventDTO;

public interface ParkingFacade {
    
    void processEvent(ParkingEventDTO event);
}