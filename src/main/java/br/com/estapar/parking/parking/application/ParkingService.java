package br.com.estapar.parking.parking.application;

import org.springframework.stereotype.Service;

import br.com.estapar.parking.core.exceptions.ValidationException;
import br.com.estapar.parking.parking.api.dto.event.ParkingEventDTO;
import br.com.estapar.parking.parking.domain.Parking;
import br.com.estapar.parking.parking.domain.enums.EventType;
import br.com.estapar.parking.parking.infrastructure.ParkingRepository;
import br.com.estapar.parking.sector.application.SectorFacade;
import br.com.estapar.parking.sector.domain.Sector;
import br.com.estapar.parking.spot.application.SpotFacade;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class ParkingService implements ParkingFacade {

    private final SpotFacade spotFacade;
    private final SectorFacade sectorFacade;
    private final PricingService pricingService;
    private final ParkingRepository parkingRepository;

    @Override
    public void processEvent(ParkingEventDTO event) {
        switch (event.eventType()) {
            case ENTRY -> eventEntry(event);
            case PARKED -> eventParked(event);
            case EXIT -> eventExit(event);
        }
    }

    @Transactional
    private void eventEntry(ParkingEventDTO event) {
        verifyVehicleNotAlreadyEntered(event.licensePlate());
        var parking = new Parking();
        parking.setLicensePlate(event.licensePlate());
        parking.setEntryTime(event.entryTime());
        parking.setEventType(event.eventType());
        parkingRepository.save(parking);
        log.info("Registering entry event for license plate: {}", event.licensePlate());
    }

    @Transactional
    private void eventParked(ParkingEventDTO event) {
        var parking = findActiveParking(event.licensePlate());
        if (isActiveVehicle(parking, event.eventType())) {
            throw new ValidationException("Vehicle is already parked.");
        }
        var spot = spotFacade.findByCoordinates(event.lat(), event.lng());
        var sector = spot.getSector();
        if (!sectorFacade.isSectorOpen(sector)) {
            log.error("Sector '{}' is currently closed. Cannot park now.", sector.getSectorName());
            throw new ValidationException("Sector is closed. Cannot park now.");
        }
        isSectorFull(sector);
        var dynamicPrice = pricingService.dynamicFactor(sector);
        spotFacade.markOccupied(spot);
        parking.setSpot(spot);
        parking.setSector(sector.getSectorName());
        parking.setEventType(event.eventType());
        parking.setBasePriceAtEntry(sector.getBasePrice());
        parking.setDynamicFactor(dynamicPrice);
        parkingRepository.save(parking);
        log.info("Vehicle with license plate {} parked at spot ID {} in sector '{}'.", event.licensePlate(),
                spot.getSpotId(), sector.getSectorName());
    }

    @Transactional
    private void eventExit(ParkingEventDTO event) {
        var parking = findActiveParking(event.licensePlate());
        parking.setExitTime(event.exitTime());
        parking.setEventType(event.eventType());
        var total = pricingService.calculatePrice(parking);
        parking.setTotalPrice(total);
        parkingRepository.save(parking);
        spotFacade.markAvailable(parking.getSpot());
        log.info("Vehicle with license plate {} exited. Total price: ${}.", event.licensePlate(), total);
    }

    private Parking findActiveParking(String licensePlate) {
        log.info("Searching for active parking with license plate: {}", licensePlate);
        return parkingRepository.findByLicensePlate(licensePlate).orElseThrow(() -> {
            log.error("No active parking found for license plate: {}", licensePlate);
            throw new ValidationException("No active parking found for license plate.");
        });
    }

    private void verifyVehicleNotAlreadyEntered(String licensePlate) {
        parkingRepository.findByLicensePlate(licensePlate).ifPresent(p -> {
            log.error("Vehicle with license plate {} is already in the parking lot.", licensePlate);
            throw new ValidationException("Vehicle with this license plate is already in the parking lot.");
        });
    }

    private void isSectorFull(Sector sector) {
        if (sectorFacade.isSectorFull(sector)) {
            log.error("Sector '{}' is full. Cannot park now.", sector.getSectorName());
            throw new ValidationException("Sector is full. Cannot park now.");
        }
    }

    private boolean isActiveVehicle(Parking parking, EventType eventType) {
        if (parking.getEventType() == eventType) {
            return true;
        }
        return false;
    }
}