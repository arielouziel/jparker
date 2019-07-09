package com.aouziel.jparker.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "pricing_policy_type")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "pricingPolicyType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = HourRatePlusFixedPricingPolicy.class, name = "HOURRATEPLUSFIXEDPRICINGPOLICY"),
        @JsonSubTypes.Type(value = HourRatePricingPolicy.class, name = "HOURRATEPRICINGPOLICY")
})
@ApiModel(subTypes = {HourRatePlusFixedPricingPolicy.class, HourRatePricingPolicy.class}, discriminator = "pricingPolicyType")
public abstract class PricingPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "pricing_policy_type", insertable = false, updatable = false)
    @ApiModelProperty(notes = "Type of pricing policy", allowableValues = "HOURRATEPLUSFIXEDPRICINGPOLICY,HOURRATEPRICINGPOLICY")
    private String pricingPolicyType;

    public abstract void computePrice(ParkingSlotUse occupation);
}
