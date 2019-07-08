package com.aouziel.jparker.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "parking_slots")
@ApiModel(description="All details about parking slots. ")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ParkingSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "The database generated slot ID")
    private long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    private ParkingLot parkingLot;

    @ApiModelProperty(notes = "The type of the parking slot")
    @Column(nullable = false)
    private ParkingSlotType type;

    @ApiModelProperty(notes = "The slot location in the parking lot")
    @Column(nullable = false, unique = true)
    private String location;
}
