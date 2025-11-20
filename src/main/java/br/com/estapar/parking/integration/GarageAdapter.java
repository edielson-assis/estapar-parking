package br.com.estapar.parking.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import br.com.estapar.parking.exceptions.ObjectNotFoundException;
import br.com.estapar.parking.garage.dto.GarageDTO;
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
        var response = restTemplate.getForObject(garageUrl, GarageDTO.class);
        if (response.garage() == null || response.spots() == null) {
            log.info("Não foi possível obter os dados da garagem.");
            throw new ObjectNotFoundException("Não foi possível obter os dados da garagem.");
        }
        log.info("Obtendo os dados da garagem.");
        return response;
    }
}