package com.aouziel.jparker.controller;

import com.aouziel.jparker.JParkerApplication;
import com.aouziel.jparker.model.HourRatePricingPolicy;
import com.aouziel.jparker.model.ParkingLot;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    public void getParkingLotById() {
    }

    @Test
    public void createParkingSlot() {
    }

    @Test
    public void listFreeParkingSlots() {
    }

    @Test
    public void enterParkingLot() {
    }

    @Test
    public void leaveParkingLot() {
    }

    private <T> T toClass(String contentAsString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(contentAsString, new TypeReference<T>() {});
    }

    public static String toJson(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

