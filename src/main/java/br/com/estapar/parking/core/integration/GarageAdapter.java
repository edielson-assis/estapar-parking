package br.com.estapar.parking.core.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import br.com.estapar.parking.core.exceptions.ObjectNotFoundException;
import br.com.estapar.parking.garage.api.dto.GarageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Component
public class GarageAdapter implements GarageClient {

    @Value("${garage.url}")
    private String garageUrl;

    private final RestTemplate restTemplate;

    @Override
    public final GarageDTO getGarage() {
        try {
            var response = restTemplate.getForObject(garageUrl, GarageDTO.class);
            if (response.garage() == null || response.spots() == null) {
                log.info("Could not retrieve garage data.");
                throw new ObjectNotFoundException("Could not retrieve garage data.");
            }
            log.info("Fetching garage data.");
            return response;
        } catch (Exception e) {
            log.error("Error connecting to the garage service: {}", e.getMessage());
            throw new ResourceAccessException("Error connecting to the garage service.");
        }
    }
}