package com.aouziel.jparker.controller;

import com.aouziel.jparker.exception.ResourceNotFoundException;
import com.aouziel.jparker.model.ParkingLot;
import com.aouziel.jparker.repository.ParkingLotRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Api(value="Parking Lot Management System", description="Operations pertaining to parking lots in Parking Lot Management System")
public class ParkingLotController {
    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @ApiOperation(value = "View a list of available parking lots", response = List.class)
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

}
