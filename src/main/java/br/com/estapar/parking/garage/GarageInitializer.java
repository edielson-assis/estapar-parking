package br.com.estapar.parking.garage;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.com.estapar.parking.garage.service.GarageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class GarageInitializer implements CommandLineRunner {

    private final GarageService garageService;
    
    @Override
    public void run(String... args) {
        log.info("Starting garage initialization...");
        garageService.initializeGarage();
    }
}