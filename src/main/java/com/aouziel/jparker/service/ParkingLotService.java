package com.aouziel.jparker.service;

import com.aouziel.jparker.exception.ConflictException;
import com.aouziel.jparker.exception.PreconditionFailedException;
import com.aouziel.jparker.exception.ResourceNotFoundException;
import com.aouziel.jparker.model.*;
import com.aouziel.jparker.repository.ParkingLotRepository;
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
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingSlotOccupationRepository parkingSlotOccupationRepository;

    @Autowired
    public ParkingLotService(
            ParkingLotRepository parkingLotRepository,
            ParkingSlotRepository parkingSlotRepository,
            ParkingSlotOccupationRepository parkingSlotOccupationRepository
    ) {
        this.parkingLotRepository = parkingLotRepository;
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingSlotOccupationRepository = parkingSlotOccupationRepository;
    }

    public List<ParkingLot> findAll() {
        return this.parkingLotRepository.findAll();
    }

    public ParkingLot create(ParkingLot parkingLot) {
        this.parkingLotRepository.save(parkingLot);
        return parkingLot;
    }

    public Optional<ParkingLot> findById(Long lotId) {
        return this.parkingLotRepository.findById(lotId);
    }

    public ParkingSlot addSlot(Long lotId, ParkingSlot slot) throws ResourceNotFoundException {
        ParkingLot parkingLot = parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found"));

        slot.setParkingLot(parkingLot);
        return parkingSlotRepository.save(slot);
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

    public List<ParkingSlot> getSlots(Long lotId, CarPowerType parkingSlotType, ParkingSlotStatus parkingSlotStatus) {
        if (parkingSlotType != null && parkingSlotStatus != null) {
            return parkingSlotRepository.findAllByParkingLotIdAndStatusAndType(lotId, parkingSlotStatus, parkingSlotType);
        } else if (parkingSlotType != null) {
            return parkingSlotRepository.findAllByParkingLotIdAndType(lotId, parkingSlotType);
        } else if (parkingSlotStatus != null) {
            return parkingSlotRepository.findAllByParkingLotIdAndStatus(lotId, parkingSlotStatus);
        } else {
            return parkingSlotRepository.findAllByParkingLotId(lotId);
        }
    }
}
