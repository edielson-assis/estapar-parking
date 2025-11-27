package br.com.estapar.parking.parking.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.estapar.parking.core.exceptions.ValidationException;
import br.com.estapar.parking.parking.api.dto.event.ParkingEventDTO;
import br.com.estapar.parking.parking.api.dto.rvenue.RevenueRequestDTO;
import br.com.estapar.parking.parking.api.dto.rvenue.RevenueResponseDTO;
import br.com.estapar.parking.parking.domain.Parking;
import br.com.estapar.parking.parking.domain.enums.EventType;
import br.com.estapar.parking.parking.infrastructure.ParkingRepository;
import br.com.estapar.parking.sector.application.SectorFacade;
import br.com.estapar.parking.sector.domain.Sector;
import br.com.estapar.parking.spot.application.SpotFacade;
import br.com.estapar.parking.spot.domain.Spot;

@ExtendWith(MockitoExtension.class)
@DisplayName("ParkingService Tests")
class ParkingServiceTest {

    @Mock
    private SpotFacade spotFacade;

    @Mock
    private SectorFacade sectorFacade;

    @Mock
    private PricingFacade pricingFacade;

    @Mock
    private ParkingRepository parkingRepository;

    @InjectMocks
    private ParkingService parkingService;

    private ParkingEventDTO entryEvent;
    private ParkingEventDTO parkedEvent;
    private ParkingEventDTO exitEvent;
    private Parking parking;
    private Sector sector;
    private Spot spot;
    private RevenueRequestDTO revenueRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        entryEvent = new ParkingEventDTO(
            "ABC1234",
            null,
            null,
            now,
            null,
            "ENTRY"
        );

        parkedEvent = new ParkingEventDTO(
            "ABC1234",
            -15.8267,
            -48.0516,
            null,
            null,
            "PARKED"
        );

        exitEvent = new ParkingEventDTO(
            "ABC1234",
            null,
            null,
            null,
            now.plusHours(2),
            "EXIT"
        );

        sector = new Sector();
        sector.setSectorId(1L);
        sector.setSectorName("A");
        sector.setBasePrice(50.0);
        sector.setMaxCapacity(100);
        sector.setOpenHour("08:00");
        sector.setCloseHour("22:00");

        spot = new Spot();
        spot.setSpotId(1L);
        spot.setSector(sector);
        spot.setLat(-15.8267);
        spot.setLng(-48.0516);
        spot.setIsOccupied(false);

        parking = new Parking();
        parking.setParkingId(1L);
        parking.setLicensePlate("ABC1234");
        parking.setEntryTime(now);
        parking.setSpot(spot);
        parking.setSector("A");
        parking.setBasePriceAtEntry(50.0);
        parking.setDynamicFactor(1.0);
        parking.setEventType(EventType.ENTRY);

        revenueRequest = new RevenueRequestDTO(LocalDate.now(), "A");
    }

    @Test
    @DisplayName("Should process ENTRY event successfully")
    void testProcessEntryEventSuccessfully() {
        when(sectorFacade.isSectorFull()).thenReturn(false);
        when(parkingRepository.findByLicensePlate("ABC1234")).thenReturn(Optional.empty());
        when(parkingRepository.save(any(Parking.class))).thenReturn(parking);

        parkingService.processEvent(entryEvent);

        verify(parkingRepository, times(1)).save(any(Parking.class));
        verify(sectorFacade, times(1)).isSectorFull();
    }

    @Test
    @DisplayName("Should throw exception when vehicle already in parking")
    void testProcessEntryEventVehicleAlreadyParked() {
        when(parkingRepository.findByLicensePlate("ABC1234")).thenReturn(Optional.of(parking));

        assertThrows(ValidationException.class, () -> parkingService.processEvent(entryEvent));
        verify(parkingRepository, times(1)).findByLicensePlate("ABC1234");
    }

    @Test
    @DisplayName("Should throw exception when parking is full")
    void testProcessEntryEventParkingFull() {
        when(parkingRepository.findByLicensePlate("ABC1234")).thenReturn(Optional.empty());
        when(sectorFacade.isSectorFull()).thenReturn(true);

        assertThrows(ValidationException.class, () -> parkingService.processEvent(entryEvent));
    }

    @Test
    @DisplayName("Should throw exception when license plate is blank in ENTRY event")
    void testProcessEntryEventBlankLicensePlate() {
        ParkingEventDTO invalidEvent = new ParkingEventDTO(
            "",
            null,
            null,
            LocalDateTime.now(),
            null,
            "ENTRY"
        );

        assertThrows(IllegalArgumentException.class, () -> parkingService.processEvent(invalidEvent));
    }

    @Test
    @DisplayName("Should throw exception when entry time is null")
    void testProcessEntryEventNullEntryTime() {
        ParkingEventDTO invalidEvent = new ParkingEventDTO(
            "ABC1234",
            null,
            null,
            null,
            null,
            "ENTRY"
        );

        assertThrows(IllegalArgumentException.class, () -> parkingService.processEvent(invalidEvent));
    }

    @Test
    @DisplayName("Should process PARKED event successfully")
    void testProcessParkedEventSuccessfully() {
        when(parkingRepository.findByLicensePlate("ABC1234")).thenReturn(Optional.of(parking));
        when(spotFacade.findByCoordinates(-15.8267, -48.0516)).thenReturn(spot);
        when(sectorFacade.isSectorOpen(sector)).thenReturn(true);
        when(pricingFacade.dynamicFactor(sector)).thenReturn(1.2);
        when(parkingRepository.save(any(Parking.class))).thenReturn(parking);

        parkingService.processEvent(parkedEvent);

        verify(spotFacade, times(1)).findByCoordinates(-15.8267, -48.0516);
        verify(spotFacade, times(1)).markOccupied(spot);
        verify(parkingRepository, times(1)).save(any(Parking.class));
    }

    @Test
    @DisplayName("Should throw exception when sector is closed in PARKED event")
    void testProcessParkedEventSectorClosed() {
        when(parkingRepository.findByLicensePlate("ABC1234")).thenReturn(Optional.of(parking));
        when(spotFacade.findByCoordinates(-15.8267, -48.0516)).thenReturn(spot);
        when(sectorFacade.isSectorOpen(sector)).thenReturn(false);

        assertThrows(ValidationException.class, () -> parkingService.processEvent(parkedEvent));
    }

    @Test
    @DisplayName("Should throw exception when coordinates are null in PARKED event")
    void testProcessParkedEventNullCoordinates() {
        ParkingEventDTO invalidEvent = new ParkingEventDTO(
            "ABC1234",
            null,
            -48.0516,
            null,
            null,
            "PARKED"
        );

        assertThrows(IllegalArgumentException.class, () -> parkingService.processEvent(invalidEvent));
    }

    @Test
    @DisplayName("Should process EXIT event successfully")
    void testProcessExitEventSuccessfully() {
        when(parkingRepository.findByLicensePlate("ABC1234")).thenReturn(Optional.of(parking));
        when(pricingFacade.calculatePrice(parking)).thenReturn(150.0);
        when(parkingRepository.save(any(Parking.class))).thenReturn(parking);

        parkingService.processEvent(exitEvent);

        verify(parkingRepository, times(1)).save(any(Parking.class));
        verify(spotFacade, times(1)).markAvailable(spot);
    }

    @Test
    @DisplayName("Should throw exception when exit time is before entry time")
    void testProcessExitEventInvalidTime() {
        LocalDateTime now = LocalDateTime.now();
        ParkingEventDTO invalidExitEvent = new ParkingEventDTO(
            "ABC1234",
            null,
            null,
            null,
            now.minusHours(1),
            "EXIT"
        );

        parking.setEntryTime(now);
        when(parkingRepository.findByLicensePlate("ABC1234")).thenReturn(Optional.of(parking));

        assertThrows(ValidationException.class, () -> parkingService.processEvent(invalidExitEvent));
    }

    @Test
    @DisplayName("Should throw exception when exit time is null")
    void testProcessExitEventNullExitTime() {
        ParkingEventDTO invalidEvent = new ParkingEventDTO(
            "ABC1234",
            null,
            null,
            null,
            null,
            "EXIT"
        );

        assertThrows(IllegalArgumentException.class, () -> parkingService.processEvent(invalidEvent));
    }

    @Test
    @DisplayName("Should throw exception for unknown event type")
    void testProcessUnknownEventType() {
        ParkingEventDTO invalidEvent = new ParkingEventDTO(
            "ABC1234",
            null,
            null,
            LocalDateTime.now(),
            null,
            "UNKNOWN"
        );

        assertThrows(IllegalArgumentException.class, () -> parkingService.processEvent(invalidEvent));
    }

    @Test
    @DisplayName("Should calculate revenue by sector successfully")
    void testCalculateRevenueBySectorSuccessfully() {
        when(parkingRepository.sumTotalPriceBySectorAndExitTimeBetween(
            "A",
            revenueRequest.date().atStartOfDay(),
            revenueRequest.date().atStartOfDay().plusDays(1)
        )).thenReturn(500.0);

        RevenueResponseDTO result = parkingService.calculateRevenueBySector(revenueRequest);

        assertNotNull(result);
        assertEquals("BRL", result.currency());
        assertEquals(new BigDecimal("500.00"), result.amount());
    }

    @Test
    @DisplayName("Should return zero revenue when no parking records found")
    void testCalculateRevenueBySectorZeroRevenue() {
        when(parkingRepository.sumTotalPriceBySectorAndExitTimeBetween(
            "A",
            revenueRequest.date().atStartOfDay(),
            revenueRequest.date().atStartOfDay().plusDays(1)
        )).thenReturn(null);

        RevenueResponseDTO result = parkingService.calculateRevenueBySector(revenueRequest);

        assertNotNull(result);
        assertEquals("BRL", result.currency());
        assertEquals(new BigDecimal("0.00"), result.amount());
    }

    @Test
    @DisplayName("Should convert event type to uppercase")
    void testEventTypeConvertToUppercase() {
        ParkingEventDTO lowercaseEvent = new ParkingEventDTO(
            "ABC1234",
            null,
            null,
            LocalDateTime.now(),
            null,
            "entry"
        );

        when(sectorFacade.isSectorFull()).thenReturn(false);
        when(parkingRepository.findByLicensePlate("ABC1234")).thenReturn(Optional.empty());
        when(parkingRepository.save(any(Parking.class))).thenReturn(parking);

        parkingService.processEvent(lowercaseEvent);

        verify(parkingRepository, times(1)).save(any(Parking.class));
    }
}
