package com.aouziel.jparker.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "pricing_policy_type")
public abstract class PricingPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "pricing_policy_type", insertable = false, updatable = false)
    @ApiModelProperty(notes = "Type of pricing policy")
    private String pricingPolicyType;

    public abstract void computePrice(ParkingSlotUse occupation);
}
