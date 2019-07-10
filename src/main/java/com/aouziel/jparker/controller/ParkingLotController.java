package com.aouziel.jparker.controller;

import com.aouziel.jparker.exception.ConflictException;
import com.aouziel.jparker.exception.PreconditionFailedException;
import com.aouziel.jparker.exception.ResourceNotFoundException;
import com.aouziel.jparker.model.*;
import com.aouziel.jparker.service.ParkingLotService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    @ApiOperation(value = "View a list of all parking lots")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
    @GetMapping("/parking-lots")
    public List<ParkingLot> getAllParkingLots() {
        return parkingLotService.findAll();
    }

    @ApiOperation(value = "Create a new parking lot", response = ParkingLot.class)
    @PostMapping("/parking-lots")
    public ParkingLot createParkingLot(
            @Valid @RequestBody ParkingLot parkingLot
    ) {
        return parkingLotService.create(parkingLot);
    }

    @ApiOperation(value = "Get a parking lot by Id")
    @GetMapping("/parking-lots/{lotId}")
    public ResponseEntity<ParkingLot> getParkingLotById(
            @ApiParam(value = "ParkingLot id from which parking object will retrieve", required = true)
            @PathVariable(value = "lotId") Long lotId)
            throws ResourceNotFoundException {
        ParkingLot parking = parkingLotService.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found for this id :: " + lotId));
        return ResponseEntity.ok().body(parking);
    }

    @ApiOperation(value = "Create a new slot in a parking lot")
    @PostMapping("/parking-lots/{lotId}/slots")
    public ParkingSlot createParkingSlot(
            @ApiParam(value = "ParkingLot id where to add the slot", required = true)
            @PathVariable(value = "lotId") Long lotId,

            @Valid @RequestBody ParkingSlot slot
    ) throws ResourceNotFoundException {
        return parkingLotService.addSlot(lotId, slot);
    }

    @ApiOperation(value = "Get a list of free slots in a parking lot")
    @GetMapping("/parking-lots/{lotId}/slots")
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

    @ApiOperation(value = "Put a car in a any free parking slot")
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

    @ApiOperation(value = "Remove car from parking lot and bill the customer")
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
