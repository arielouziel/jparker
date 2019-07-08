package com.aouziel.jparker.loader;

import com.aouziel.jparker.model.ParkingLot;
import com.aouziel.jparker.model.ParkingSlot;
import com.aouziel.jparker.model.ParkingSlotType;
import com.aouziel.jparker.repository.ParkingLotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    public DataLoader() {
    }

    public void run(ApplicationArguments args) {
        parkingLotRepository.save(ParkingLot.builder()
                .name("My Great Parking")
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
                .build()
        );
    }
}