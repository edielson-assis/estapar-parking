package br.com.estapar.parking.garage.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.estapar.parking.core.integration.GarageClient;
import br.com.estapar.parking.garage.api.dto.GarageDTO;
import br.com.estapar.parking.sector.api.dto.SectorDTO;
import br.com.estapar.parking.sector.application.SectorFacade;
import br.com.estapar.parking.spot.api.dto.SpotDTO;
import br.com.estapar.parking.spot.application.SpotFacade;

@ExtendWith(MockitoExtension.class)
@DisplayName("GarageService Tests")
class GarageServiceTest {

    @Mock
    private GarageClient garageClient;

    @Mock
    private SectorFacade sectorFacade;

    @Mock
    private SpotFacade spotFacade;

    @InjectMocks
    private GarageService garageService;

    private GarageDTO garageDTO;
    private SectorDTO sectorDTO;
    private SpotDTO spotDTO;

    @BeforeEach
    void setUp() {
        sectorDTO = new SectorDTO("A", 50.0, 100, "00:00", "23:59", 1440);
        spotDTO = new SpotDTO(1L, "A", -15.8267, -48.0516);
        List<SectorDTO> sectors = new ArrayList<>();
        sectors.add(sectorDTO);
        List<SpotDTO> spots = new ArrayList<>();
        spots.add(spotDTO);
        garageDTO = new GarageDTO(sectors, spots);
    }

    @Test
    @DisplayName("Should initialize garage successfully when sectors and spots don't exist")
    void testInitializeGarageSuccessfully() {
        when(garageClient.getGarage()).thenReturn(garageDTO);
        when(sectorFacade.findAllSector()).thenReturn(new ArrayList<>());
        when(spotFacade.findAllIds()).thenReturn(new ArrayList<>());

        garageService.initializeGarage();

        verify(garageClient, times(1)).getGarage();
        verify(sectorFacade, times(1)).findAllSector();
        verify(sectorFacade, times(1)).createSector(sectorDTO);
        verify(spotFacade, times(1)).findAllIds();
        verify(spotFacade, times(1)).createSpot(eq(spotDTO), any());
    }

    @Test
    @DisplayName("Should skip sector creation if sector already exists")
    void testSkipSectorCreationIfExists() {
        when(garageClient.getGarage()).thenReturn(garageDTO);
        when(sectorFacade.findAllSector()).thenReturn(List.of("A"));
        when(spotFacade.findAllIds()).thenReturn(new ArrayList<>());

        garageService.initializeGarage();

        verify(garageClient, times(1)).getGarage();
        verify(sectorFacade, times(0)).createSector(any());
        verify(spotFacade, times(1)).createSpot(eq(spotDTO), any());
    }

    @Test
    @DisplayName("Should skip spot creation if spot already exists")
    void testSkipSpotCreationIfExists() {
        when(garageClient.getGarage()).thenReturn(garageDTO);
        when(sectorFacade.findAllSector()).thenReturn(new ArrayList<>());
        when(spotFacade.findAllIds()).thenReturn(List.of(1L));

        garageService.initializeGarage();

        verify(garageClient, times(1)).getGarage();
        verify(sectorFacade, times(1)).createSector(any());
        verify(spotFacade, times(0)).createSpot(any(), any());
    }

    @Test
    @DisplayName("Should create multiple sectors and spots")
    void testCreateMultipleSectorsAndSpots() {
        SectorDTO sector2 = new SectorDTO("B", 30.0, 50, "08:00", "23:59", 60);
        SpotDTO spot2 = new SpotDTO(2L, "B", -15.8270, -48.0520);
        List<SectorDTO> sectors = List.of(sectorDTO, sector2);
        List<SpotDTO> spots = List.of(spotDTO, spot2);
        GarageDTO multiGarageDTO = new GarageDTO(sectors, spots);

        when(garageClient.getGarage()).thenReturn(multiGarageDTO);
        when(sectorFacade.findAllSector()).thenReturn(new ArrayList<>());
        when(spotFacade.findAllIds()).thenReturn(new ArrayList<>());

        garageService.initializeGarage();

        verify(sectorFacade, times(2)).createSector(any());
        verify(spotFacade, times(2)).createSpot(any(), any());
    }

    @Test
    @DisplayName("Should handle empty garage data")
    void testHandleEmptyGarageData() {
        GarageDTO emptyGarage = new GarageDTO(new ArrayList<>(), new ArrayList<>());
        when(garageClient.getGarage()).thenReturn(emptyGarage);
        when(sectorFacade.findAllSector()).thenReturn(new ArrayList<>());
        when(spotFacade.findAllIds()).thenReturn(new ArrayList<>());

        garageService.initializeGarage();

        verify(sectorFacade, times(0)).createSector(any());
        verify(spotFacade, times(0)).createSpot(any(), any());
    }
}
