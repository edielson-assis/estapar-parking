package br.com.estapar.parking.parking.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import br.com.estapar.parking.core.exceptions.ValidationException;
import br.com.estapar.parking.parking.api.dto.RevenueRequestDTO;
import br.com.estapar.parking.parking.api.dto.RevenueResponseDTO;
import br.com.estapar.parking.parking.api.dto.event.ParkingEventDTO;
import br.com.estapar.parking.parking.api.mapper.ParkingMapper;
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
    private final PricingFacade pricingFacade;
    private final ParkingRepository parkingRepository;

    @Override
    public void processEvent(ParkingEventDTO event) {
        switch (event.eventType()) {
            case ENTRY -> eventEntry(event);
            case PARKED -> eventParked(event);
            case EXIT -> eventExit(event);
        }
    }

    @Override
    public RevenueResponseDTO calculateRevenueBySector(RevenueRequestDTO request) {
        log.info("Revenue request for sector={} date={}", request.sector(), request.date());
        var sectorName = request.sector();
        var date = request.date();
        var start = date.atStartOfDay();
        var end = start.plusDays(1);
        var result = parkingRepository.sumTotalPriceBySectorAndExitTimeBetween(sectorName, start, end);
        if (result == null) {
            result = 0.0;
        }
        var amount = BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP);
        var timestamp = start.plusHours(12).atOffset(ZoneOffset.UTC).toString();
        return ParkingMapper.toDto(amount, "BRL", timestamp);
    }

    @Transactional
    private void eventEntry(ParkingEventDTO event) {
        verifyVehicleNotAlreadyEntered(event.licensePlate());
        var parking = ParkingMapper.toEntity(event);
        parkingRepository.save(parking);
        log.info("Registering entry event for license plate: {}", event.licensePlate());
    }

    @Transactional
    private void eventParked(ParkingEventDTO event) {
        var parking = findActiveParking(event.licensePlate());
        isActiveVehicle(parking, event.eventType());
        var spot = spotFacade.findByCoordinates(event.lat(), event.lng());
        var sector = spot.getSector();
        isSectorOpen(sector);
        isSectorFull(sector);
        spotFacade.markOccupied(spot);
        var dynamicFactor = pricingFacade.dynamicFactor(sector);
        ParkingMapper.toEntity(parking, event, sector, spot, dynamicFactor);
        parkingRepository.save(parking);
        log.info("Vehicle with license plate {} parked at spot ID {} in sector '{}'.", event.licensePlate(), spot.getSpotId(), sector.getSectorName());
    }

    @Transactional
    private void eventExit(ParkingEventDTO event) {
        var parking = findActiveParking(event.licensePlate());
        verifyParkingDateTime(parking, event);
        ParkingMapper.toEntity(parking, event);
        var totalPrice = pricingFacade.calculatePrice(parking);
        parking.setTotalPrice(totalPrice);
        parkingRepository.save(parking);
        spotFacade.markAvailable(parking.getSpot());
        log.info("Vehicle with license plate {} exited. Total price: ${}.", event.licensePlate(), totalPrice);
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

    private void isActiveVehicle(Parking parking, EventType eventType) {
        if (parking.getEventType() == eventType) {
            log.error("Vehicle with license plate {} is already parked.", parking.getLicensePlate());
            throw new ValidationException("Vehicle is already parked.");
        }
    }

    private void isSectorOpen(Sector sector) {
        if (!sectorFacade.isSectorOpen(sector)) {
            log.error("Sector '{}' is currently closed. Cannot park now.", sector.getSectorName());
            throw new ValidationException("Sector is closed. Cannot park now.");
        }
    }

    private void verifyParkingDateTime(Parking parking, ParkingEventDTO event) {
        if (event.exitTime().isBefore(parking.getEntryTime())) {
            log.error("Exit time {} is before entry time {} for license plate {}.", event.exitTime(), parking.getEntryTime(), event.licensePlate());
            throw new ValidationException("Exit time cannot be before entry time.");
        }
    }
}