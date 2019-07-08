package com.aouziel.jparker.repository;

import com.aouziel.jparker.model.ParkingSlot;
import com.aouziel.jparker.model.ParkingSlotStatus;
import com.aouziel.jparker.model.CarPowerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    List<ParkingSlot> findAllByParkingLotIdAndStatus(Long parkingLotId, ParkingSlotStatus status);

    @Lock(LockModeType.OPTIMISTIC)
    List<ParkingSlot> findAllByParkingLotIdAndStatusAndType(Long parkingLotId, ParkingSlotStatus status, CarPowerType type);

    @Lock(LockModeType.OPTIMISTIC)
    Optional<ParkingSlot> findByParkingLotIdAndId(Long lotId, Long id);
}
