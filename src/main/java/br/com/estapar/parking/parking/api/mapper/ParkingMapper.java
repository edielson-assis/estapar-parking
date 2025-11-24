package br.com.estapar.parking.parking.api.mapper;

import java.math.BigDecimal;

import br.com.estapar.parking.parking.api.dto.RevenueResponseDTO;
import br.com.estapar.parking.parking.api.dto.event.ParkingEventDTO;
import br.com.estapar.parking.parking.domain.Parking;
import br.com.estapar.parking.sector.domain.Sector;
import br.com.estapar.parking.spot.domain.Spot;

public class ParkingMapper {
    
    private ParkingMapper() {}

    public static Parking toEntity(ParkingEventDTO parkingDto) {
        var parking = new Parking();
        parking.setLicensePlate(parkingDto.licensePlate());
        parking.setEntryTime(parkingDto.entryTime());
        parking.setEventType(parkingDto.eventType());
        return parking;
    }

    public static void toEntity(Parking parking, ParkingEventDTO parkingDto, Sector sector, Spot spot, double dynamicFactor) {
        parking.setSpot(spot);
        parking.setSector(sector.getSectorName());
        parking.setBasePriceAtEntry(sector.getBasePrice());
        parking.setDynamicFactor(dynamicFactor);
        parking.setEventType(parkingDto.eventType());
    }

    public static void toEntity(Parking parking, ParkingEventDTO parkingDto) {
        parking.setExitTime(parkingDto.exitTime());
        parking.setEventType(parkingDto.eventType());
    }

    public static RevenueResponseDTO toDto(BigDecimal amount, String currency, String timestamp) {
        return new RevenueResponseDTO(amount, currency, timestamp);
    }
}