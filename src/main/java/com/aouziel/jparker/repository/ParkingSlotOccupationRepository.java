package com.aouziel.jparker.repository;

import com.aouziel.jparker.model.ParkingSlotUse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingSlotOccupationRepository extends JpaRepository<ParkingSlotUse, Long> {
}
