package com.aouziel.jparker.database;

import com.aouziel.jparker.model.ParkingLot;
import com.aouziel.jparker.model.ParkingSlot;
import com.aouziel.jparker.model.ParkingSlotType;
import com.aouziel.jparker.repository.ParkingLotRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ParkingLotSeeder implements ApplicationRunner {

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    public ParkingLotSeeder() {
    }

    public void run(ApplicationArguments args) {
        log.info("Seeding Parking Lot table...");

        parkingLotRepository.save(ParkingLot.builder()
                .name("My First Parking Lot")
                .slot(ParkingSlot.builder()
                        .type(ParkingSlotType.twentyKw)
                        .location("001")
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(ParkingSlotType.fiftyKw)
                        .location("002")
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(ParkingSlotType.sedan)
                        .location("003")
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(ParkingSlotType.sedan)
                        .location("004")
                        .build()
                )
                .build()
        );

        parkingLotRepository.save(ParkingLot.builder()
                .name("My Second Parking Lot")
                .slot(ParkingSlot.builder()
                        .type(ParkingSlotType.twentyKw)
                        .location("alpha")
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(ParkingSlotType.fiftyKw)
                        .location("beta")
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(ParkingSlotType.sedan)
                        .location("gamma")
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(ParkingSlotType.sedan)
                        .location("pouet")
                        .build()
                )
                .build()
        );

        parkingLotRepository.save(ParkingLot.builder()
                .name("Yet Another Parking Lot")
                .slot(ParkingSlot.builder()
                        .type(ParkingSlotType.twentyKw)
                        .location("A")
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(ParkingSlotType.fiftyKw)
                        .location("B")
                        .build()
                )
                .slot(ParkingSlot.builder()
                        .type(ParkingSlotType.sedan)
                        .location("C")
                        .build()
                )
                .build()
        );
    }
}