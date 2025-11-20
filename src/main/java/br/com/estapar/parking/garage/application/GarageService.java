package br.com.estapar.parking.garage.application;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import br.com.estapar.parking.core.integration.GarageClient;
import br.com.estapar.parking.garage.api.dto.GarageDTO;
import br.com.estapar.parking.sector.application.SectorFacade;
import br.com.estapar.parking.spot.application.SpotFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class GarageService implements GarageFacade {
    
    private final GarageClient client;
    private final SectorFacade sectorService;
    private final SpotFacade spotService;

    @Override
    public void initializeGarage() {
        log.info("Fetching garage data from simulator...");
        var garage = client.getGarage();
        callPersistSectorIfNotExists(garage);
        callPersistSpotIfNotExists(garage);
    }

    private void callPersistSectorIfNotExists(GarageDTO garage) {
        Set<String> existingSectors = new HashSet<>(sectorService.findAllSector());
        log.info("Checking existing sectors before persistence...");
        garage.garage().forEach(sectorData -> {
            if (!existingSectors.contains(sectorData.sector())) {
                sectorService.createSector(sectorData);
            }
        });
    }

    private void callPersistSpotIfNotExists(GarageDTO garage) {
        Set<Long> existingSpotIds = new HashSet<>(spotService.findAllIds());
        log.info("Checking existing spots before persistence...");
        garage.spots().forEach(spotData -> {
            if (!existingSpotIds.contains(spotData.id())) {
                var sector = sectorService.findSectorByName(spotData.sector());
                spotService.createSpot(spotData, sector);
            }
        });
    }
}