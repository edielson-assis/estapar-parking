package br.com.estapar.parking.parking.application;

import br.com.estapar.parking.parking.api.dto.event.ParkingEventDTO;
import br.com.estapar.parking.parking.api.dto.rvenue.RevenueRequestDTO;
import br.com.estapar.parking.parking.api.dto.rvenue.RevenueResponseDTO;

public interface ParkingFacade {
    
    void processEvent(ParkingEventDTO event);

    RevenueResponseDTO calculateRevenueBySector(RevenueRequestDTO request);
}