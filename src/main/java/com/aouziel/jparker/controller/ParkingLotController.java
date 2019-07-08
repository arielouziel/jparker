package com.aouziel.jparker.controller;

import com.aouziel.jparker.exception.ConflictException;
import com.aouziel.jparker.exception.PreconditionFailedException;
import com.aouziel.jparker.exception.ResourceNotFoundException;
import com.aouziel.jparker.model.CarPowerType;
import com.aouziel.jparker.model.ParkingLot;
import com.aouziel.jparker.model.ParkingSlot;
import com.aouziel.jparker.model.ParkingSlotStatus;
import com.aouziel.jparker.repository.ParkingLotRepository;
import com.aouziel.jparker.repository.ParkingSlotRepository;
import com.aouziel.jparker.service.ParkingLotService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@Api(value="Parking Lot Management System", description="Operations pertaining to parking lots in Parking Lot Management System")
public class ParkingLotController {
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingLotService parkingLotService;

    @Autowired
    public ParkingLotController(
            ParkingLotRepository parkingLotRepository,
            ParkingSlotRepository parkingSlotRepository, ParkingLotService parkingLotService) {
        this.parkingLotRepository = parkingLotRepository;
        this.parkingSlotRepository = parkingSlotRepository;
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
    @GetMapping("/parking-lots/{id}")
    public ResponseEntity<ParkingLot> getParkingLotById(
            @ApiParam(value = "ParkingLot id from which parking object will retrieve", required = true)
            @PathVariable(value = "id") Long parkingId)
            throws ResourceNotFoundException {
        ParkingLot parking = parkingLotRepository.findById(parkingId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found for this id :: " + parkingId));
        return ResponseEntity.ok().body(parking);
    }

    @ApiOperation(value = "Get a list of free slots in a parking lot")
    @GetMapping("/parking-lots/{parkingId}/free-slots")
    public ResponseEntity<List<ParkingSlot>> getFreeParkingSlot(
            @ApiParam(value = "ParkingLot id from which parking slots will be retrieved", required = true)
            @PathVariable(value = "parkingId") Long parkingId,

            @ApiParam(value = "Specify parking slot type to be used (twentyKw, fiftyKw or sedan)")
            @RequestParam(name = "parkingSlotType") Optional<CarPowerType> parkingSlotType
    ) {
        List<ParkingSlot> slots = parkingSlotType
                .map(type -> parkingSlotRepository.findAllByParkingLotIdAndStatusAndType(parkingId, ParkingSlotStatus.free, type))
                .orElse(parkingSlotRepository.findAllByParkingLotIdAndStatus(parkingId, ParkingSlotStatus.free));

        return ResponseEntity.ok().body(slots);
    }

    @ApiOperation(value = "Put a car in a parking slot")
    @PutMapping("/parking-lots/{lotId}/slots/{slotId}/car/{carId}")
    public ParkingSlot putCarInParkingSlot(
            @ApiParam(value = "ParkingLot id from which parking slot will be retrieved", required = true)
            @PathVariable(value = "lotId") Long lotId,

            @ApiParam(value = "Parking slot id where the car should go", required = true)
            @PathVariable(value = "slotId") Long slotId,

            @ApiParam(value = "Car id to put in the slot", required = true)
            @PathVariable(value = "carId") Long carId
    ) throws ResourceNotFoundException, PreconditionFailedException, ConflictException {
        return parkingLotService.pickSlot(lotId, slotId, carId);
    }
}
