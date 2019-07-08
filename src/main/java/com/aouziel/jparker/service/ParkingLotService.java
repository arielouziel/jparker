package com.aouziel.jparker.service;

import com.aouziel.jparker.exception.ConflictException;
import com.aouziel.jparker.exception.PreconditionFailedException;
import com.aouziel.jparker.exception.ResourceNotFoundException;
import com.aouziel.jparker.model.*;
import com.aouziel.jparker.repository.ParkingSlotOccupationRepository;
import com.aouziel.jparker.repository.ParkingSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ParkingLotService {
    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingSlotOccupationRepository parkingSlotOccupationRepository;

    @Autowired
    public ParkingLotService(ParkingSlotRepository parkingSlotRepository, ParkingSlotOccupationRepository parkingSlotOccupationRepository) {
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingSlotOccupationRepository = parkingSlotOccupationRepository;
    }

    public ParkingSlotUse enterParkingLot(Long lotId, @Valid CarPowerType carPowerType) throws ResourceNotFoundException, ConflictException {
        ParkingSlot slot = parkingSlotRepository.findFirstByParkingLotIdAndStatusAndType(lotId, ParkingSlotStatus.free, carPowerType)
                .orElseThrow(() -> new ResourceNotFoundException("Parking slot not found in provided parking lot for car power type"));

        ParkingSlotUse occupation = this.parkingSlotOccupationRepository.save(
                ParkingSlotUse.builder()
                        .slot(slot)
                        .carPowerType(carPowerType)
                        .startTime(new Date())
                        .build()
        );

        slot.setStatus(ParkingSlotStatus.occupied);

        try {
            this.parkingSlotRepository.save(slot);
        } catch (OptimisticLockException ex) {
            throw new ConflictException("Someone else took the spot !");
        }

        return occupation;
    }

    public ParkingSlotUse leaveParkingLot(Long lotId, Long occupationId) throws ResourceNotFoundException, PreconditionFailedException {
        ParkingSlotUse occupation = parkingSlotOccupationRepository.findById(occupationId)
                .orElseThrow(() -> new ResourceNotFoundException("Unkown occupation id"));

        ParkingSlot slot = occupation.getSlot();
        ParkingLot parkingLot = slot.getParkingLot();
        if (parkingLot.getId() != lotId) {
            throw new PreconditionFailedException("This occupation is not in provided parking lot");
        }

        // update slot status
        slot.setStatus(ParkingSlotStatus.free);
        parkingSlotRepository.save(slot);

        occupation.setEndTime(new Date());
        PricingPolicy pricingPolicy = slot.getParkingLot().getPricingPolicy();
        pricingPolicy.computePrice(occupation);
        occupation.setSlot(null);

        return parkingSlotOccupationRepository.save(occupation);
    }

    public List<ParkingSlot> getFreeSlots(Long lotId, Optional<CarPowerType> parkingSlotType) {
        return parkingSlotType
                .map(type -> parkingSlotRepository.findAllByParkingLotIdAndStatusAndType(lotId, ParkingSlotStatus.free, type))
                .orElse(parkingSlotRepository.findAllByParkingLotIdAndStatus(lotId, ParkingSlotStatus.free));
    }
}
