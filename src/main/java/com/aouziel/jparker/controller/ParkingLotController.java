package com.aouziel.jparker.controller;

import com.aouziel.jparker.exception.ConflictException;
import com.aouziel.jparker.exception.PreconditionFailedException;
import com.aouziel.jparker.exception.ResourceNotFoundException;
import com.aouziel.jparker.model.*;
import com.aouziel.jparker.service.ParkingLotService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Api(value="Parking Lot Management System", description="Operations pertaining to parking lots in Parking Lot Management System")
public class ParkingLotController {
    private final ParkingLotService parkingLotService;

    @Autowired
    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @ApiOperation(value = "View a list of all parking lots", nickname = "getAllParkingLots")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @GetMapping("/parking-lots")
    public List<ParkingLot> getAllParkingLots() {
        return parkingLotService.findAll();
    }

    @ApiOperation(value = "Create a new parking lot", response = ParkingLot.class, nickname = "createParkingLot")
    @PostMapping("/parking-lots")
    public ParkingLot createParkingLot(
            @Valid @RequestBody ParkingLot parkingLot
    ) {
        return parkingLotService.create(parkingLot);
    }

    @ApiOperation(value = "Get a parking lot by id", nickname = "getParkingLotById")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved parking lot"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @GetMapping("/parking-lots/{lotId}")
    public ResponseEntity<ParkingLot> getParkingLotById(
            @ApiParam(value = "ParkingLot id from which parking object will retrieve", required = true)
            @PathVariable(value = "lotId") Long lotId)
            throws ResourceNotFoundException {
        ParkingLot parking = parkingLotService.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found for this id :: " + lotId));
        return ResponseEntity.ok().body(parking);
    }

    @ApiOperation(value = "Delete a parking", nickname = "deleteParkingLot")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted parking lot"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @DeleteMapping("/parking-lots/{lotId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteParkingLot(
            @ApiParam(value = "Parking lot id to delete", required = true)
            @PathVariable(value = "lotId") Long lotId) {
        parkingLotService.delete(lotId);
    }

    @ApiOperation(value = "Create a new slot in a parking lot", nickname = "createParkingSlot")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully added slot"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @PostMapping("/parking-lots/{lotId}/slots")
    public ParkingSlot createParkingSlot(
            @ApiParam(value = "ParkingLot id where to add the slot", required = true)
            @PathVariable(value = "lotId") Long lotId,

            @Valid @RequestBody ParkingSlot slot
    ) throws ResourceNotFoundException {
        return parkingLotService.addSlot(lotId, slot);
    }

    @ApiOperation(value = "Remove a slot from a parking lot", nickname = "removeParkingSlot")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully removed slot"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @DeleteMapping("/parking-lots/{lotId}/slots/{slotId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void removeParkingSlot(
            @ApiParam(value = "ParkingLot id where to add the slot", required = true)
            @PathVariable(value = "lotId") Long lotId,

            @ApiParam(value = "Slot id to remove", required = true)
            @PathVariable(value = "slotId") Long slotId
    ) {
        parkingLotService.removeSlot(lotId, slotId);
    }

    @ApiOperation(value = "Get a list of free slots in a parking lot", nickname = "listFreeParkingSlots")
    @GetMapping("/parking-lots/{lotId}/slots")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public ResponseEntity<List<ParkingSlot>> listFreeParkingSlots(
            @ApiParam(value = "ParkingLot id from which parking slots will be retrieved", required = true)
            @PathVariable(value = "lotId") Long lotId,

            @ApiParam(value = "Specify parking slot status to be used (free, occupied)")
            @RequestParam(name = "parkingSlotStatus") ParkingSlotStatus parkingSlotStatus,

            @ApiParam(value = "Specify parking slot type to be used (twentyKw, fiftyKw or sedan)")
            @RequestParam(name = "parkingSlotType") CarPowerType parkingSlotType
    ) {
        List<ParkingSlot> slots = this.parkingLotService.getSlots(lotId, parkingSlotType, parkingSlotStatus);

        return ResponseEntity.ok().body(slots);
    }

    @ApiOperation(value = "Put a car in a any free parking slot", nickname = "enterParkingLot")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully picked the slot"),
            @ApiResponse(code = 409, message = "Somebody took the slot before you"),
            @ApiResponse(code = 412, message = "Preconditions failed"),
    })
    @PostMapping("/parking-lots/{lotId}/slot-uses")
    public ParkingTicket enterParkingLot(
            @ApiParam(value = "ParkingLot id from which parking slot will be retrieved", required = true)
            @PathVariable(value = "lotId") Long lotId,

            @Valid @RequestBody CarPowerType carPowerType
    ) throws ResourceNotFoundException, ConflictException {
        return parkingLotService.enterParkingLot(lotId, carPowerType);
    }

    @ApiOperation(value = "Remove car from parking lot and bill the customer", nickname = "leaveParkingLot")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully leaved the parking lot"),
            @ApiResponse(code = 409, message = "Somebody took the slot before you"),
            @ApiResponse(code = 412, message = "Preconditions failed"),
    })
    @PutMapping("/parking-lots/{lotId}/tickets/{ticketNumber}/leave")
    public ParkingTicket leaveParkingLot(
            @ApiParam(value = "ParkingLot id from which parking slot will be retrieved", required = true)
            @PathVariable(value = "lotId") Long lotId,

            @ApiParam(value = "Ticket number provided when entered the parking lot", required = true)
            @PathVariable(value = "ticketNumber") String ticketNumber
    ) throws ResourceNotFoundException, PreconditionFailedException {
        return parkingLotService.leaveParkingLot(lotId, ticketNumber);
    }
}
