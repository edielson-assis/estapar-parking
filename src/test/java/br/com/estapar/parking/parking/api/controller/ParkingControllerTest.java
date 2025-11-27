package br.com.estapar.parking.parking.api.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.estapar.parking.parking.api.dto.event.ParkingEventDTO;
import br.com.estapar.parking.parking.api.dto.rvenue.RevenueRequestDTO;
import br.com.estapar.parking.parking.api.dto.rvenue.RevenueResponseDTO;
import br.com.estapar.parking.parking.application.ParkingFacade;

@WebMvcTest(ParkingController.class)
@DisplayName("ParkingController Tests")
class ParkingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ParkingFacade parkingFacade;

    private static final String WEBHOOK_PATH = "/webhook";
    private static final String REVENUE_PATH = "/revenue";
    private static final String LICENSE_PLATE = "ABC1234";
    private static final String SECTOR = "A";

    private ParkingEventDTO eventEntry;
    private RevenueResponseDTO revenueResponse;

    @BeforeEach
    void setup() {
        eventEntry = new ParkingEventDTO(
            LICENSE_PLATE,
            null,
            null,
            LocalDateTime.now(),
            null,
            "ENTRY"
        );

        revenueResponse = new RevenueResponseDTO(
            new BigDecimal("500.00"),
            "BRL",
            LocalDateTime.now().toString()
        );
    }

    @Test
    @DisplayName("When POST /webhook with ENTRY event then return 200 OK")
    void testWhenPostWebhookWithEventThenReturnOk() throws Exception {
        willDoNothing().given(parkingFacade).processEvent(any(ParkingEventDTO.class));

        ResultActions response = mockMvc.perform(
            post(WEBHOOK_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventEntry))
        );

        response.andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("When POST /webhook with EXIT event then return 200 OK")
    void testWhenPostWebhookWithExitEventThenReturnOk() throws Exception {
        var eventExit = new ParkingEventDTO(
            LICENSE_PLATE,
            null,
            null,
            null,
            LocalDateTime.now().plusHours(2),
            "EXIT"
        );

        willDoNothing().given(parkingFacade).processEvent(any(ParkingEventDTO.class));

        ResultActions response = mockMvc.perform(
            post(WEBHOOK_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventExit))
        );

        response.andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("When POST /webhook with PARKED event then return 200 OK")
    void testWhenPostWebhookWithParkedEventThenReturnOk() throws Exception {
        var eventParked = new ParkingEventDTO(
            LICENSE_PLATE,
            -15.8267,
            -48.0516,
            null,
            null,
            "PARKED"
        );

        willDoNothing().given(parkingFacade).processEvent(any(ParkingEventDTO.class));

        ResultActions response = mockMvc.perform(
            post(WEBHOOK_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventParked))
        );

        response.andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("When POST /revenue with valid request then return RevenueResponseDTO")
    void testWhenPostRevenueWithValidRequestThenReturnResponse() throws Exception {
        var request = new RevenueRequestDTO(LocalDate.now(), SECTOR);

        given(parkingFacade.calculateRevenueBySector(any(RevenueRequestDTO.class)))
            .willReturn(revenueResponse);

        ResultActions response = mockMvc.perform(
            post(REVENUE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        response.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.amount", is(revenueResponse.amount().doubleValue())))
            .andExpect(jsonPath("$.currency", is(revenueResponse.currency())))
            .andExpect(jsonPath("$.timestamp", is(revenueResponse.timestamp())));
    }

    @Test
    @DisplayName("When POST /revenue with sector A then return 500.00 BRL")
    void testWhenPostRevenueForSectorAThenReturnCorrectAmount() throws Exception {
        var expectedResponse = new RevenueResponseDTO(
            new BigDecimal("500.00"),
            "BRL",
            LocalDateTime.now().toString()
        );
        var request = new RevenueRequestDTO(LocalDate.now(), "A");

        given(parkingFacade.calculateRevenueBySector(any(RevenueRequestDTO.class)))
            .willReturn(expectedResponse);

        ResultActions response = mockMvc.perform(
            post(REVENUE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        response.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.amount", is(500.00)))
            .andExpect(jsonPath("$.currency", is("BRL")));
    }

    @Test
    @DisplayName("When POST /revenue returns zero revenue then return 0.00 BRL")
    void testWhenPostRevenueReturnsZeroThenReturn() throws Exception {
        var zeroResponse = new RevenueResponseDTO(
            new BigDecimal("0.00"),
            "BRL",
            LocalDateTime.now().toString()
        );
        var request = new RevenueRequestDTO(LocalDate.now(), SECTOR);

        given(parkingFacade.calculateRevenueBySector(any(RevenueRequestDTO.class)))
            .willReturn(zeroResponse);

        ResultActions response = mockMvc.perform(
            post(REVENUE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        response.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.amount", is(0.00)))
            .andExpect(jsonPath("$.currency", is("BRL")));
    }
}
