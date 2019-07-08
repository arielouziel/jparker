package com.aouziel.jparker.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "parking_lots")
@ApiModel(description="All details about parking lots. ")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ParkingLot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "The database generated parking lot ID")
    private long id;

    @Column(nullable = false)
    @ApiModelProperty(notes = "The name of the parking lot")
    private String name;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "parkingLot")
    @ApiModelProperty(notes = "All the slots of the parking lot")
    @Singular
    private Collection<ParkingSlot> slots;
}
