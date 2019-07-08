package com.aouziel.jparker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;

@Entity
@ApiModel(description="All details about parking slots. ")
@Builder @NoArgsConstructor @AllArgsConstructor
@Data
public class ParkingSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated slot ID")
    private long id;

    @Version
    private Integer version; // used for optimistic locking

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn
    private ParkingLot parkingLot;

    @ApiModelProperty(notes = "The type of the parking slot")
    @Column(nullable = false)
    private CarPowerType type;

    @Builder.Default
    @ApiModelProperty(notes = "The status of the parking slot")
    @Column(nullable = false, columnDefinition = "integer default 0")
    private ParkingSlotStatus status = ParkingSlotStatus.free;

    @ApiModelProperty(notes = "The slot location in the parking lot")
    @Column(nullable = false)
    private String location;
}
