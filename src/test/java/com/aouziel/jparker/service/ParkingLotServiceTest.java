package com.aouziel.jparker.service;

import com.aouziel.jparker.exception.ConflictException;
import com.aouziel.jparker.exception.PreconditionFailedException;
import com.aouziel.jparker.exception.ResourceNotFoundException;
import com.aouziel.jparker.model.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-test.properties")
public class ParkingLotServiceTest {
    @Autowired
    private ParkingLotService parkingLotService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void findAll() {
        List<ParkingLot> parkingLots = parkingLotService.findAll();
        assertThat(parkingLots).isNotNull().isNotEmpty();
    }

    @Test
    public void create() {
        List<ParkingLot> all = parkingLotService.findAll();
        assertThat(all).isNotNull().isNotEmpty();
        int initSize = all.size();

        ParkingLot parkingLot = parkingLotService.create(ParkingLot.builder()
                .name("My Test Parking")
                .pricingPolicy(HourRatePricingPolicy.builder()
                        .hourPrice(100)
                        .currencyCode("EUR")
                        .build()
                )
                .build()
        );

        assertThat(parkingLot).isNotNull();
        assertThat(parkingLot.getId()).isGreaterThan(0L);
        assertThat(parkingLot.getPricingPolicy()).isNotNull();

        all = parkingLotService.findAll();
        assertThat(all.size()).isEqualTo(initSize + 1);
    }

    @Test
    public void findById() {
        Optional<ParkingLot> parkingLot = parkingLotService.findById(1L);
        assertThat(parkingLot).isNotNull();
    }

    @Test
    public void addSlot() throws ResourceNotFoundException {
        List<ParkingSlot> slots = parkingLotService.getSlots(1L, CarPowerType.sedan, null);
        assertThat(slots).isNotNull().isNotEmpty();
        int initSize = slots.size();

        parkingLotService.addSlot(1L, ParkingSlot.builder()
                .type(CarPowerType.sedan)
                .location("XXX")
                .build()
        );

        slots = parkingLotService.getSlots(1L, CarPowerType.sedan, null);
        assertThat(slots).isNotNull().isNotEmpty();
        assertThat(slots.size()).isEqualTo(initSize + 1);
    }

    @Test
    public void enterParkingLot() throws ResourceNotFoundException, ConflictException {
        ParkingTicket parkingTicket = parkingLotService.enterParkingLot(1L, CarPowerType.sedan);
        assertThat(parkingTicket).isNotNull();
        assertThat(parkingTicket.getNumber()).hasSize(6);
        assertThat(parkingTicket.getSlot()).isNotNull();
        assertThat(parkingTicket.getStartTime()).isNotNull();
        assertThat(parkingTicket.getEndTime()).isNull();
        assertThat(parkingTicket.getPrice()).isEqualTo(0);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void enterParkingLotFail() throws ResourceNotFoundException, ConflictException {
        // create a new parking lot
        ParkingLot parkingLot = createTestParkingLot("My Other Test Parking Lot");

        for (int i = 0; i < 2; i++) { // add 2 slots
            parkingLotService.addSlot(parkingLot.getId(), ParkingSlot.builder()
                    .type(CarPowerType.twentyKw)
                    .build()
            );
        }

        // fill the whole parking lot and more
        for (int i = 0; i < 3; i++) {
            parkingLotService.enterParkingLot(parkingLot.getId(), CarPowerType.twentyKw);
        }
    }

    @Test
    public void leaveParkingFail() throws ResourceNotFoundException, PreconditionFailedException {
        exceptionRule.expect(ResourceNotFoundException.class);
        exceptionRule.expectMessage("Unkown ticket number");
        parkingLotService.leaveParkingLot(1L, "XXXXXX");
    }

    @Test
    public void delete() throws ResourceNotFoundException {
        int size = parkingLotService.findAll().size();

        // create new parking lot
        ParkingLot parkingLot = createTestParkingLot("Parking to delete");
        assertThat(parkingLotService.findAll().size()).isEqualTo(size + 1);

        // add a slot to test cascading
        parkingLotService.addSlot(parkingLot.getId(), ParkingSlot.builder()
                .type(CarPowerType.twentyKw)
                .build()
        );

        parkingLotService.delete(parkingLot.getId());
        assertThat(parkingLotService.findAll().size()).isEqualTo(size);
    }

    @Test
    public void leaveParkingLot() throws ResourceNotFoundException, ConflictException, PreconditionFailedException {
        // first we have to enter to get a ticket
        ParkingTicket ticket = parkingLotService.enterParkingLot(1L, CarPowerType.sedan);
        ticket = parkingLotService.leaveParkingLot(1L, ticket.getNumber());
        assertThat(ticket.getPrice()).isGreaterThan(0);
        assertThat(ticket.getEndTime()).isNotNull();
    }

    @Test
    public void getSlots() {
        List<ParkingSlot> slots = parkingLotService.getSlots(1L, CarPowerType.sedan, null);
        assertThat(slots).isNotNull().isNotEmpty();
    }

    @Test
    public void getFreeSlots() throws ResourceNotFoundException, ConflictException {
        List<ParkingSlot> freeSlots = parkingLotService.getSlots(1L, CarPowerType.sedan, ParkingSlotStatus.free);
        int initSize = freeSlots.size();

        // take one slot
        parkingLotService.enterParkingLot(1L, CarPowerType.sedan);

        freeSlots = parkingLotService.getSlots(1L, CarPowerType.sedan, ParkingSlotStatus.free);
        assertThat(freeSlots.size()).isEqualTo(initSize - 1);
    }

    private ParkingLot createTestParkingLot(String name) {
        ParkingLot parkingLot = parkingLotService.create(ParkingLot.builder()
                .name(name)
                .pricingPolicy(HourRatePricingPolicy.builder()
                        .hourPrice(100)
                        .currencyCode("EUR")
                        .build()
                )
                .build()
        );

        assertThat(parkingLot).isNotNull();
        assertThat(parkingLot.getId()).isGreaterThan(0L);
        assertThat(parkingLot.getPricingPolicy()).isNotNull();

        return parkingLot;
    }

    @Test
    public void removeSlot() throws ResourceNotFoundException {
        ParkingLot parkingLot = createTestParkingLot("Remove slot test");
        int size = parkingLotService.getSlots(parkingLot.getId(), null, null).size();
        ParkingSlot slot = parkingLotService.addSlot(
                parkingLot.getId(),
                ParkingSlot.builder().type(CarPowerType.sedan).build()
        );

        assertThat(
                parkingLotService.getSlots(parkingLot.getId(), null, null).size()
        ).isEqualTo(size + 1);

        parkingLotService.removeSlot(parkingLot.getId(), slot.getId());

        assertThat(
                parkingLotService.getSlots(parkingLot.getId(), null, null).size()
        ).isEqualTo(size);
    }
}
