package com.aouziel.jparker.database;

import com.aouziel.jparker.model.*;
import com.aouziel.jparker.repository.ParkingLotRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ParkingLotSeeder implements ApplicationRunner {

    private final ParkingLotRepository parkingLotRepository;

    @Autowired
    public ParkingLotSeeder(ParkingLotRepository parkingLotRepository) {
        this.parkingLotRepository = parkingLotRepository;
    }

    public void run(ApplicationArguments args) {
        log.info("Seeding Parking Lot Database");

        parkingLotRepository.save(ParkingLot.builder()
                .name("My First Parking Lot")
                .pricingPolicy(HourRatePricingPolicy.builder()
                        .currencyCode("EUR")
                        .hourPrice(150)
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(CarPowerType.twentyKw)
                        .location("001")
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(CarPowerType.fiftyKw)
                        .location("002")
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(CarPowerType.sedan)
                        .location("003")
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(CarPowerType.sedan)
                        .location("004")
                        .build()
                )
                .build()
        );

        parkingLotRepository.save(ParkingLot.builder()
                .name("My Second Parking Lot")
                .pricingPolicy(HourRatePlusFixedPricingPolicy.fixedBuilder()
                        .fixedPrice(100)
                        .hourPrice(150)
                        .currencyCode("USD")
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(CarPowerType.twentyKw)
                        .location("alpha")
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(CarPowerType.fiftyKw)
                        .location("beta")
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(CarPowerType.sedan)
                        .location("gamma")
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(CarPowerType.sedan)
                        .location("pouet")
                        .build()
                )
                .build()
        );

        parkingLotRepository.save(ParkingLot.builder()
                .name("Yet Another Expensive Parking Lot")
                .pricingPolicy(HourRatePlusFixedPricingPolicy.fixedBuilder()
                        .fixedPrice(200)
                        .hourPrice(300)
                        .currencyCode("AUD")
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(CarPowerType.twentyKw)
                        .location("A")
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(CarPowerType.fiftyKw)
                        .location("B")
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(CarPowerType.sedan)
                        .location("C")
                        .build()
                )
                .build()
        );
    }
}