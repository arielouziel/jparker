package com.aouziel.jparker.controller;

import com.aouziel.jparker.JParkerApplication;
import com.aouziel.jparker.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = JParkerApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties")
public class ParkingLotControllerTest {
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getAllParkingLots() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/parking-lots"))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        List<ParkingLot> lots = mapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<ParkingLot>>() {});

        assertThat(lots).isNotNull().isNotEmpty();
    }

    @Test
    public void createParkingLot() throws Exception {
        ParkingLot parkingLot = ParkingLot.builder()
                .name("My new parking")
                .pricingPolicy(HourRatePricingPolicy.builder()
                        .currencyCode("EUR")
                        .hourPrice(150)
                        .build()
                )
                .build();

        MvcResult mvcResult = this.mockMvc.perform(
                post("/api/v1/parking-lots")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(toJson(parkingLot))
        ).andExpect(status().isOk()).andReturn();

        ParkingLot result = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(),
                ParkingLot.class);
        assertThat(result.getId()).isNotNull();
    }

    @Test
    public void getParkingLotById() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/parking-lots/1"))
                .andExpect(status().isOk())
                .andReturn();

        ParkingLot lot = new ObjectMapper().readValue(
                mvcResult.getResponse().getContentAsString(), ParkingLot.class);
        assertThat(lot).isNotNull();
        assertThat(lot.getName()).isEqualTo("My First Parking Lot");
    }

    @Test
    public void createParkingSlot() throws Exception {
        ParkingSlot slot = ParkingSlot.builder()
                .type(CarPowerType.sedan)
                .location("XXX")
                .build();

        MvcResult mvcResult = this.mockMvc.perform(
                post("/api/v1/parking-lots/1/slots")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(toJson(slot))
        ).andExpect(status().isOk()).andReturn();

        ParkingSlot result = new ObjectMapper().readValue(
                mvcResult.getResponse().getContentAsString(), ParkingSlot.class);
        assertThat(result.getId()).isNotNull();
    }

    @Test
    public void getParkingSlots() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/parking-lots/1/slots"))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        List<ParkingSlot> slots = mapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<ParkingSlot>>() {});

        assertThat(slots).isNotNull().isNotEmpty();
    }

    @Test
    public void enterParkingLot() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(
                post("/api/v1/parking-lots/1/tickets")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(toJson("sedan")))
                .andExpect(status().isOk())
                .andReturn();

        ParkingTicket result = new ObjectMapper().readValue(
                mvcResult.getResponse().getContentAsString(), ParkingTicket.class);
        assertThat(result.getNumber()).isNotNull();
    }

    @Test
    public void leaveParkingLot() throws Exception {
        // first enter to get a ticket
        MvcResult mvcResult = this.mockMvc.perform(
                post("/api/v1/parking-lots/1/tickets")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(toJson("sedan")))
                .andExpect(status().isOk())
                .andReturn();

        ParkingTicket ticket = new ObjectMapper().readValue(
                mvcResult.getResponse().getContentAsString(), ParkingTicket.class);
        assertThat(ticket.getNumber()).isNotNull();

        mvcResult = this.mockMvc.perform(
                put("/api/v1/parking-lots/1/tickets/" + ticket.getNumber() + "/leave"))
                    .andExpect(status().isOk())
                    .andReturn();

        ticket = new ObjectMapper().readValue(
                mvcResult.getResponse().getContentAsString(), ParkingTicket.class);
        assertThat(ticket.getPrice()).isGreaterThan(0);
    }

    public static String toJson(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

