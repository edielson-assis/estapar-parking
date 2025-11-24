package br.com.estapar.parking.parking.application;

import br.com.estapar.parking.parking.domain.Parking;
import br.com.estapar.parking.sector.domain.Sector;

public interface PricingFacade {
    
    double calculatePrice(Parking parking);

    double dynamicFactor(Sector sector);
}