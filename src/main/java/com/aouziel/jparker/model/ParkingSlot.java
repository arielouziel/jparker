package com.aouziel.jparker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated slot ID")
    private long id;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn
    private ParkingLot parkingLot;

    @ApiModelProperty(notes = "The type of the parking slot")
    @Column(nullable = false)
    private ParkingSlotType type;

    @Builder.Default
    @ApiModelProperty(notes = "The status of the parking slot")
    @Column(nullable = false, columnDefinition = "integer(10) default 0")
    private ParkingSlotStatus status = ParkingSlotStatus.free;

    @ApiModelProperty(notes = "The slot location in the parking lot")
    @Column(nullable = false)
    private String location;
}
