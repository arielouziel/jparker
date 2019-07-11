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

    /**
     * @return a list of all parking lots
     */
    public List<ParkingLot> findAll() {
        return this.parkingLotRepository.findAll();
    }

    /**
     * Create a new parking lot
     * @param parkingLot a parking lot with at least a name and a pricing policy
     * @return created parking lot
     */
    public ParkingLot create(ParkingLot parkingLot) {
        this.parkingLotRepository.save(parkingLot);
        return parkingLot;
    }

    /**
     * Delete parking lot by id
     * @param lotId
     */
    public void delete(long lotId) {
        this.parkingLotRepository.deleteById(lotId);
    }

    /**
     * @param lotId
     * @return parking lot with provided id
     */
    public Optional<ParkingLot> findById(Long lotId) {
        return this.parkingLotRepository.findById(lotId);
    }

    /**
     * Add a slot in a parking lot
     * @param lotId the parking lot where to add the slot
     * @param slot the slot to add with at least the car power type
     * @return the created slot
     * @throws ResourceNotFoundException
     */
    public ParkingSlot addSlot(Long lotId, ParkingSlot slot) throws ResourceNotFoundException {
        ParkingLot parkingLot = parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found"));

        slot.setParkingLot(parkingLot);
        return parkingSlotRepository.save(slot);
    }

    /**
     * Remove a slot from a parking lot
     * @param lotId
     * @param slotId
     */
    public void removeSlot(long lotId, long slotId) {
        parkingSlotRepository.deleteByParkingLotIdAndId(lotId, slotId);
    }

    /**
     * Enter a parking lot
     * @param lotId id of the parking lot
     * @param carPowerType power type of the car
     * @return a parking ticket with a number needed to leave the parking
     * @throws ResourceNotFoundException if no free slot is found
     * @throws ConflictException if someone take the selected slot while we were processing (extremely rare case)
     */
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

    /**
     * Leave a parking lot
     * @param lotId parking lot id
     * @param number provided when entered
     * @return the updated ticket with computed price based on parking lot pricing policy
     * @throws ResourceNotFoundException if ticket number is unknown
     * @throws PreconditionFailedException if the ticket does not belong to parking lot
     */
    public ParkingTicket leaveParkingLot(Long lotId, String number) throws ResourceNotFoundException, PreconditionFailedException {
        ParkingTicket ticket = parkingTicketRepository.findByNumber(number)
                .orElseThrow(() -> new ResourceNotFoundException("Unkown ticket number"));

        ParkingSlot slot = ticket.getSlot();
        ParkingLot parkingLot = slot.getParkingLot();
        if (parkingLot.getId() != lotId) {
            throw new PreconditionFailedException("This ticket does not belong to provided parking lot");
        }

        // update slot status
        slot.setStatus(ParkingSlotStatus.free);
        parkingSlotRepository.save(slot);

        ticket.setEndTime(new Date());
        PricingPolicy pricingPolicy = slot.getParkingLot().getPricingPolicy();
        pricingPolicy.computePrice(ticket);
        ticket.setSlot(null);

        return parkingTicketRepository.save(ticket);
    }

    /**
     * Return a list of slots in a parking lot
     * @param lotId parking lot id
     * @param parkingSlotType (optional) slot power type filter
     * @param parkingSlotStatus (optional) slot status filter
     * @return a list of slots
     */
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
