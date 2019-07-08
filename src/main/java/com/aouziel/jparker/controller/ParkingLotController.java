package com.aouziel.jparker.controller;

import com.aouziel.jparker.exception.ConflictException;
import com.aouziel.jparker.exception.PreconditionFailedException;
import com.aouziel.jparker.exception.ResourceNotFoundException;
import com.aouziel.jparker.model.*;
import com.aouziel.jparker.repository.ParkingLotRepository;
import com.aouziel.jparker.service.ParkingLotService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@Api(value="Parking Lot Management System", description="Operations pertaining to parking lots in Parking Lot Management System")
public class ParkingLotController {
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingLotService parkingLotService;

    @Autowired
    public ParkingLotController(
            ParkingLotRepository parkingLotRepository,
            ParkingLotService parkingLotService) {
        this.parkingLotRepository = parkingLotRepository;
        this.parkingLotService = parkingLotService;
    }

    @ApiOperation(value = "View a list of all parking lots", response = List.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
    @GetMapping("/parking-lots")
    public List<ParkingLot> getAllParkingLots() {
        return parkingLotRepository.findAll();
    }

    @ApiOperation(value = "Get a parking lot by Id")
    @GetMapping("/parking-lots/{lotId}")
    public ResponseEntity<ParkingLot> getParkingLotById(
            @ApiParam(value = "ParkingLot id from which parking object will retrieve", required = true)
            @PathVariable(value = "lotId") Long lotId)
            throws ResourceNotFoundException {
        ParkingLot parking = parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found for this id :: " + lotId));
        return ResponseEntity.ok().body(parking);
    }

    @ApiOperation(value = "Get a list of free slots in a parking lot")
    @GetMapping("/parking-lots/{lotId}/free-slots")
    public ResponseEntity<List<ParkingSlot>> listFreeParkingSlots(
            @ApiParam(value = "ParkingLot id from which parking slots will be retrieved", required = true)
            @PathVariable(value = "lotId") Long lotId,

            @ApiParam(value = "Specify parking slot type to be used (twentyKw, fiftyKw or sedan)")
            @RequestParam(name = "parkingSlotType") Optional<CarPowerType> parkingSlotType
    ) {
        List<ParkingSlot> slots = this.parkingLotService.getFreeSlots(lotId, parkingSlotType);

        return ResponseEntity.ok().body(slots);
    }

    @ApiOperation(value = "Put a car in a any free parking slot")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully picked the slot"),
            @ApiResponse(code = 409, message = "Somebody took the slot before you"),
            @ApiResponse(code = 412, message = "Preconditions failed"),
    })
    @PostMapping("/parking-lots/{lotId}/slot-uses")
    public ParkingSlotUse enterParkingLot(
            @ApiParam(value = "ParkingLot id from which parking slot will be retrieved", required = true)
            @PathVariable(value = "lotId") Long lotId,

            @Valid @RequestBody CarPowerType carPowerType
    ) throws ResourceNotFoundException, ConflictException {
        return parkingLotService.enterParkingLot(lotId, carPowerType);
    }

    @ApiOperation(value = "Remove car from parking lot and bill the customer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully leaved the parking lot"),
            @ApiResponse(code = 409, message = "Somebody took the slot before you"),
            @ApiResponse(code = 412, message = "Preconditions failed"),
    })
    @PutMapping("/parking-lots/{lotId}/slot-uses/{useId}/leave")
    public ParkingSlotUse leaveParkingLot(
            @ApiParam(value = "ParkingLot id from which parking slot will be retrieved", required = true)
            @PathVariable(value = "lotId") Long lotId,

            @ApiParam(value = "Occupation id provided when entered the parking lot", required = true)
            @PathVariable(value = "useId") Long useId
    ) throws ResourceNotFoundException, PreconditionFailedException {
        return parkingLotService.leaveParkingLot(lotId, useId);
    }
}
