package br.com.estapar.parking.spot.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.estapar.parking.sector.domain.Sector;
import br.com.estapar.parking.spot.api.dto.SpotDTO;
import br.com.estapar.parking.spot.domain.Spot;
import br.com.estapar.parking.spot.infrastructure.SpotRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpotService Tests")
class SpotServiceTest {

    @Mock
    private SpotRepository spotRepository;

    @InjectMocks
    private SpotService spotService;

    private Spot spot;
    private SpotDTO spotDTO;
    private Sector sector;

    @BeforeEach
    void setUp() {
        sector = new Sector();
        sector.setSectorId(1L);
        sector.setSectorName("A");
        sector.setBasePrice(50.0);
        sector.setMaxCapacity(100);

        spot = new Spot();
        spot.setSpotId(1L);
        spot.setSector(sector);
        spot.setLat(-15.8267);
        spot.setLng(-48.0516);
        spot.setIsOccupied(false);

        spotDTO = new SpotDTO(1L, "A", -15.8267, -48.0516);
    }

    @Test
    @DisplayName("Should create spot successfully")
    void testCreateSpotSuccessfully() {
        when(spotRepository.save(any(Spot.class))).thenReturn(spot);

        Spot result = spotService.createSpot(spotDTO, sector);

        assertNotNull(result);
        assertEquals(1L, result.getSpotId());
        assertEquals(-15.8267, result.getLat());
        assertEquals(-48.0516, result.getLng());
        verify(spotRepository, times(1)).save(any(Spot.class));
    }

    @Test
    @DisplayName("Should find all spot IDs")
    void testFindAllIds() {
        List<Long> spotIds = List.of(1L, 2L, 3L, 4L, 5L);
        when(spotRepository.findAllIds()).thenReturn(spotIds);

        List<Long> result = spotService.findAllIds();

        assertNotNull(result);
        assertEquals(5, result.size());
        assertTrue(result.contains(1L));
        verify(spotRepository, times(1)).findAllIds();
    }

    @Test
    @DisplayName("Should find empty list when no spots exist")
    void testFindAllIdsEmpty() {
        when(spotRepository.findAllIds()).thenReturn(List.of());

        List<Long> result = spotService.findAllIds();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(spotRepository, times(1)).findAllIds();
    }

    @Test
    @DisplayName("Should count occupied spots by sector")
    void testCountOccupiedSpotsBySector() {
        when(spotRepository.countBySectorAndIsOccupiedTrue(sector)).thenReturn(15L);

        long count = spotService.countOccupiedSpotsBySector(sector);

        assertEquals(15, count);
        verify(spotRepository, times(1)).countBySectorAndIsOccupiedTrue(sector);
    }

    @Test
    @DisplayName("Should count zero occupied spots")
    void testCountOccupiedSpotsZero() {
        when(spotRepository.countBySectorAndIsOccupiedTrue(sector)).thenReturn(0L);

        long count = spotService.countOccupiedSpotsBySector(sector);

        assertEquals(0, count);
    }

    @Test
    @DisplayName("Should count available spots")
    void testCountAvailableSpots() {
        when(spotRepository.countByIsOccupiedFalse()).thenReturn(50L);

        long count = spotService.countAvailableSpots();

        assertEquals(50, count);
        verify(spotRepository, times(1)).countByIsOccupiedFalse();
    }

    @Test
    @DisplayName("Should return zero available spots")
    void testCountAvailableSpotsZero() {
        when(spotRepository.countByIsOccupiedFalse()).thenReturn(0L);

        long count = spotService.countAvailableSpots();

        assertEquals(0, count);
    }

    @Test
    @DisplayName("Should find spot by coordinates successfully")
    void testFindByCoordinatesSuccessfully() {
        when(spotRepository.findByLatAndLng(-15.8267, -48.0516)).thenReturn(Optional.of(spot));

        Spot result = spotService.findByCoordinates(-15.8267, -48.0516);

        assertNotNull(result);
        assertEquals(1L, result.getSpotId());
        assertEquals(-15.8267, result.getLat());
        verify(spotRepository, times(1)).findByLatAndLng(-15.8267, -48.0516);
    }

    @Test
    @DisplayName("Should throw exception when spot not found by coordinates")
    void testFindByCoordinatesNotFound() {
        when(spotRepository.findByLatAndLng(0.0, 0.0)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> spotService.findByCoordinates(0.0, 0.0));
        verify(spotRepository, times(1)).findByLatAndLng(0.0, 0.0);
    }

    @Test
    @DisplayName("Should mark spot as occupied")
    void testMarkOccupied() {
        when(spotRepository.save(any(Spot.class))).thenReturn(spot);

        spotService.markOccupied(spot);

        assertTrue(spot.getIsOccupied());
        verify(spotRepository, times(1)).save(spot);
    }

    @Test
    @DisplayName("Should mark spot as available")
    void testMarkAvailable() {
        spot.setIsOccupied(true);
        when(spotRepository.save(any(Spot.class))).thenReturn(spot);

        spotService.markAvailable(spot);

        assertFalse(spot.getIsOccupied());
        verify(spotRepository, times(1)).save(spot);
    }

    @Test
    @DisplayName("Should toggle spot status correctly")
    void testToggleSpotStatus() {
        when(spotRepository.save(any(Spot.class))).thenReturn(spot);

        assertFalse(spot.getIsOccupied());
        
        spotService.markOccupied(spot);
        assertTrue(spot.getIsOccupied());
        
        spotService.markAvailable(spot);
        assertFalse(spot.getIsOccupied());
    }
}
