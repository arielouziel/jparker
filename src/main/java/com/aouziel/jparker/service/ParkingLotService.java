package com.aouziel.jparker.service;

import com.aouziel.jparker.exception.ConflictException;
import com.aouziel.jparker.exception.PreconditionFailedException;
import com.aouziel.jparker.exception.ResourceNotFoundException;
import com.aouziel.jparker.model.Car;
import com.aouziel.jparker.model.ParkingSlot;
import com.aouziel.jparker.repository.CarRepository;
import com.aouziel.jparker.repository.ParkingSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;

@Service
@Transactional
public class ParkingLotService {
    private final ParkingSlotRepository parkingSlotRepository;
    private final CarRepository carRepository;

    @Autowired
    public ParkingLotService(ParkingSlotRepository parkingSlotRepository, CarRepository carRepository) {
        this.parkingSlotRepository = parkingSlotRepository;
        this.carRepository = carRepository;
    }

    public ParkingSlot pickSlot(Long lotId, Long slotId, Long carId) throws ResourceNotFoundException, PreconditionFailedException, ConflictException {
        // pick up a free slot
        ParkingSlot slot = parkingSlotRepository.findByParkingLotIdAndId(lotId, slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking slot not found in provided parking lot"));

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        if (slot.getType() != car.getPowerType()) {
            throw new PreconditionFailedException("Car power type mismatch");
        }

        if (slot.getCar() != null) {
            throw new PreconditionFailedException("Parking slot occupied");
        }

        slot.setCar(car);

        try {
            return this.parkingSlotRepository.save(slot);
        } catch (OptimisticLockException ex) {
            throw new ConflictException("Someone else took the spot !");
        }
    }
}
