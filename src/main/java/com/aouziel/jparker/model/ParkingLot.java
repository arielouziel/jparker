package com.aouziel.jparker.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "slots")
@Entity
@ApiModel(description="All details about parking lots. ")
public class ParkingLot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated parking lot ID")
    private long id;

    @Column(nullable = false)
    @ApiModelProperty(notes = "The name of the parking lot")
    private String name;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "parking_lot_id")
    @ApiModelProperty(notes = "All the slots of the parking lot")
    @Singular
    private Set<ParkingSlot> slots;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "pricing_policy_id", referencedColumnName = "id", nullable = false)
    private PricingPolicy pricingPolicy;
}
