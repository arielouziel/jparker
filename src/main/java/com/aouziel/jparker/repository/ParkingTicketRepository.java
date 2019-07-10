package com.aouziel.jparker.repository;

import com.aouziel.jparker.model.ParkingTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingTicketRepository extends JpaRepository<ParkingTicket, Long> {
    boolean existsByNumber(String number);

    Optional<ParkingTicket> findByNumber(String number);
}
