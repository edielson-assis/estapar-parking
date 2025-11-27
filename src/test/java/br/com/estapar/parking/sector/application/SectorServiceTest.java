package br.com.estapar.parking.sector.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.estapar.parking.core.exceptions.ObjectNotFoundException;
import br.com.estapar.parking.sector.api.dto.SectorDTO;
import br.com.estapar.parking.sector.domain.Sector;
import br.com.estapar.parking.sector.infrastructure.SectorRepository;
import br.com.estapar.parking.spot.application.SpotFacade;

@ExtendWith(MockitoExtension.class)
@DisplayName("SectorService Tests")
class SectorServiceTest {

    @Mock
    private SectorRepository sectorRepository;

    @Mock
    private SpotFacade spotFacade;

    @InjectMocks
    private SectorService sectorService;

    private Sector sector;
    private SectorDTO sectorDTO;

    @BeforeEach
    void setUp() {
        sector = new Sector();
        sector.setSectorId(1L);
        sector.setSectorName("A");
        sector.setBasePrice(50.0);
        sector.setMaxCapacity(100);
        sector.setOpenHour("00:00");
        sector.setCloseHour("23:59");
        sector.setDurationLimitMinutes(1440);

        sectorDTO = new SectorDTO("A", 50.0, 100, "00:00", "23:59", 1440);
    }

    @Test
    @DisplayName("Should create sector successfully")
    void testCreateSectorSuccessfully() {
        when(sectorRepository.save(any(Sector.class))).thenReturn(sector);

        Sector result = sectorService.createSector(sectorDTO);

        assertNotNull(result);
        assertEquals("A", result.getSectorName());
        assertEquals(50.0, result.getBasePrice());
        assertEquals(100, result.getMaxCapacity());
        verify(sectorRepository, times(1)).save(any(Sector.class));
    }

    @Test
    @DisplayName("Should find sector by name successfully")
    void testFindSectorByNameSuccessfully() {
        when(sectorRepository.findBySectorName("A")).thenReturn(Optional.of(sector));

        Sector result = sectorService.findSectorByName("A");

        assertNotNull(result);
        assertEquals("A", result.getSectorName());
        verify(sectorRepository, times(1)).findBySectorName("A");
    }

    @Test
    @DisplayName("Should throw ObjectNotFoundException when sector not found")
    void testFindSectorByNameNotFound() {
        when(sectorRepository.findBySectorName("Z")).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> sectorService.findSectorByName("Z"));
        verify(sectorRepository, times(1)).findBySectorName("Z");
    }

    @Test
    @DisplayName("Should find all sectors")
    void testFindAllSectors() {
        List<String> sectors = List.of("A", "B", "C");
        when(sectorRepository.findAllSector()).thenReturn(sectors);

        List<String> result = sectorService.findAllSector();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("A"));
        verify(sectorRepository, times(1)).findAllSector();
    }

    @Test
    @DisplayName("Should calculate occupancy rate correctly")
    void testGetOccupancyRateSuccessfully() {
        when(spotFacade.countOccupiedSpotsBySector(sector)).thenReturn(50L);
        when(sectorRepository.findBySectorName("A")).thenReturn(Optional.of(sector));

        double occupancyRate = sectorService.getOccupancyRate(sector);

        assertEquals(0.5, occupancyRate);
        verify(spotFacade, times(1)).countOccupiedSpotsBySector(sector);
    }

    @Test
    @DisplayName("Should return zero occupancy rate when max capacity is zero")
    void testGetOccupancyRateWhenCapacityZero() {
        Sector zeroCapacitySector = new Sector();
        zeroCapacitySector.setSectorName("Z");
        zeroCapacitySector.setMaxCapacity(0);
        
        when(spotFacade.countOccupiedSpotsBySector(zeroCapacitySector)).thenReturn(0L);
        when(sectorRepository.findBySectorName("Z")).thenReturn(Optional.of(zeroCapacitySector));

        double occupancyRate = sectorService.getOccupancyRate(zeroCapacitySector);

        assertEquals(0.0, occupancyRate);
    }

    @Test
    @DisplayName("Should return true when parking is full")
    void testIsSectorFullTrue() {
        when(spotFacade.countAvailableSpots()).thenReturn(0L);

        boolean isFull = sectorService.isSectorFull();

        assertTrue(isFull);
        verify(spotFacade, times(1)).countAvailableSpots();
    }

    @Test
    @DisplayName("Should return false when parking is not full")
    void testIsSectorFullFalse() {
        when(spotFacade.countAvailableSpots()).thenReturn(5L);

        boolean isFull = sectorService.isSectorFull();

        assertFalse(isFull);
        verify(spotFacade, times(1)).countAvailableSpots();
    }

    @Test
    @DisplayName("Should return true when sector is open during operating hours")
    void testIsSectorOpenTrue() {
        when(sectorRepository.findBySectorName("A")).thenReturn(Optional.of(sector));

        boolean isOpen = sectorService.isSectorOpen(sector);

        assertTrue(isOpen);
        verify(sectorRepository, times(1)).findBySectorName("A");
    }

    @Test
    @DisplayName("Should return false when sector has no operating hours")
    void testIsSectorOpenNoHours() {
        Sector noHoursSector = new Sector();
        noHoursSector.setSectorName("B");
        noHoursSector.setOpenHour(null);
        noHoursSector.setCloseHour(null);
        
        when(sectorRepository.findBySectorName("B")).thenReturn(Optional.of(noHoursSector));

        boolean isOpen = sectorService.isSectorOpen(noHoursSector);

        assertFalse(isOpen);
    }

    @Test
    @DisplayName("Should return false when opening hour equals closing hour")
    void testIsSectorOpenInvalidHours() {
        Sector invalidHoursSector = new Sector();
        invalidHoursSector.setSectorName("C");
        invalidHoursSector.setOpenHour("12:00");
        invalidHoursSector.setCloseHour("12:00");
        
        when(sectorRepository.findBySectorName("C")).thenReturn(Optional.of(invalidHoursSector));

        boolean isOpen = sectorService.isSectorOpen(invalidHoursSector);

        assertFalse(isOpen);
    }

    @Test
    @DisplayName("Should return false when sector has invalid hour format")
    void testIsSectorOpenInvalidFormat() {
        Sector invalidFormatSector = new Sector();
        invalidFormatSector.setSectorName("D");
        invalidFormatSector.setOpenHour("invalid");
        invalidFormatSector.setCloseHour("22:00");
        
        when(sectorRepository.findBySectorName("D")).thenReturn(Optional.of(invalidFormatSector));

        boolean isOpen = sectorService.isSectorOpen(invalidFormatSector);

        assertFalse(isOpen);
    }
}
