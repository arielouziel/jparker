package com.aouziel.jparker.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Data
@Entity
@NoArgsConstructor
@DiscriminatorValue("hourRatePlusFixed")
public class HourRatePlusFixedPricingPolicy extends HourRatePricingPolicy {

    @Builder(builderMethodName = "fixedBuilder")
    public HourRatePlusFixedPricingPolicy(int hourPrice, String currencyCode, int fixedPrice) {
        super(hourPrice, currencyCode);
        this.fixedPrice = fixedPrice;
    }

    @Column
    @ApiModelProperty(notes = "Fixed amount")
    private int fixedPrice;

    @Override
    public void computePrice(ParkingSlotUse occupation) {
        super.computePrice(occupation);
        occupation.setPrice(occupation.getPrice() + fixedPrice);
    }
}
