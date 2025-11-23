package br.com.estapar.parking.parking.application;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import br.com.estapar.parking.core.exceptions.ValidationException;
import br.com.estapar.parking.parking.domain.Parking;
import br.com.estapar.parking.sector.application.SectorService;
import br.com.estapar.parking.sector.domain.Sector;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final SectorService sectorService;

    public double calculatePrice(Parking parking) {
        var entry = parking.getEntryTime();
        var exit = parking.getExitTime();
        var sector = sectorService.findSectorByName(parking.getSector());
        var minutes = Duration.between(entry, exit).toMinutes();
        if (minutes <= 30) {
            return 0.0;
        }
        var price = applyDynamicPricing(sector, sector.getBasePrice());
        return switch (sector.getSectorName()) {
            case "A"  -> price;
            case "B" -> calculateHourlyPrice(price, entry, exit);
            default -> throw new ValidationException("Unknown sector: " + sector.getSectorName());
        };
    }

    public double dynamicFactor(Sector sector) {
        var occupancy = sectorService.getOccupancyRate(sector);
        if (occupancy < 0.25) return 0.90;
        if (occupancy < 0.50) return 1.0;
        if (occupancy < 0.75) return 1.10;
        return 1.25;
    }

    private double applyDynamicPricing(Sector sector, double basePrice) {
        return basePrice * dynamicFactor(sector);
    }

    private double calculateHourlyPrice(double price, LocalDateTime entry, LocalDateTime exit) {
        var totalMinutes = Duration.between(entry, exit).toMinutes();
        var hours = Duration.ofMinutes(totalMinutes).toHoursPart() +
                (Duration.ofMinutes(totalMinutes).toMinutesPart() > 0 ? 1 : 0);
        return hours * price;
    }
}