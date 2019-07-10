package com.aouziel.jparker.service;

import com.aouziel.jparker.exception.ConflictException;
import com.aouziel.jparker.exception.PreconditionFailedException;
import com.aouziel.jparker.exception.ResourceNotFoundException;
import com.aouziel.jparker.model.*;
import com.aouziel.jparker.repository.ParkingLotRepository;
import com.aouziel.jparker.repository.ParkingSlotRepository;
import com.aouziel.jparker.repository.ParkingTicketRepository;
import com.aouziel.jparker.util.StringUtils;
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
    private final ParkingTicketRepository parkingTicketRepository;

    @Autowired
    public ParkingLotService(
            ParkingLotRepository parkingLotRepository,
            ParkingSlotRepository parkingSlotRepository,
            ParkingTicketRepository parkingTicketRepository
    ) {
        this.parkingLotRepository = parkingLotRepository;
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingTicketRepository = parkingTicketRepository;
    }

    public List<ParkingLot> findAll() {
        return this.parkingLotRepository.findAll();
    }

    public ParkingLot create(ParkingLot parkingLot) {
        this.parkingLotRepository.save(parkingLot);
        return parkingLot;
    }

    public void delete(long lotId) {
        this.parkingLotRepository.deleteById(lotId);
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

    public void removeSlot(long lotId, long slotId) {
        parkingSlotRepository.deleteByParkingLotIdAndId(lotId, slotId);
    }

    public ParkingTicket enterParkingLot(Long lotId, @Valid CarPowerType carPowerType) throws ResourceNotFoundException, ConflictException {
        ParkingSlot slot = parkingSlotRepository.findFirstByParkingLotIdAndStatusAndType(lotId, ParkingSlotStatus.free, carPowerType)
                .orElseThrow(() -> new ResourceNotFoundException("Parking slot not found in provided parking lot for car power type"));

        String number = StringUtils.randomNumeric(6);
        while (this.parkingTicketRepository.existsByNumber(number)) { // find free ticket number
            number = StringUtils.randomNumeric(6);
        }

        ParkingTicket ticket = this.parkingTicketRepository.save(
                ParkingTicket.builder()
                        .number(number)
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

        return ticket;
    }

    public ParkingTicket leaveParkingLot(Long lotId, String number) throws ResourceNotFoundException, PreconditionFailedException {
        ParkingTicket occupation = parkingTicketRepository.findByNumber(number)
                .orElseThrow(() -> new ResourceNotFoundException("Unkown ticket number"));

        ParkingSlot slot = occupation.getSlot();
        ParkingLot parkingLot = slot.getParkingLot();
        if (parkingLot.getId() != lotId) {
            throw new PreconditionFailedException("This ticket does not belong to provided parking lot");
        }

        // update slot status
        slot.setStatus(ParkingSlotStatus.free);
        parkingSlotRepository.save(slot);

        occupation.setEndTime(new Date());
        PricingPolicy pricingPolicy = slot.getParkingLot().getPricingPolicy();
        pricingPolicy.computePrice(occupation);
        occupation.setSlot(null);

        return parkingTicketRepository.save(occupation);
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
