package com.aouziel.jparker.model;

import io.swagger.annotations.ApiModel;
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
@DiscriminatorValue("HOURRATEPLUSFIXEDPRICINGPOLICY")
@ApiModel(description = "Details about hour rate plus fixed amount pricing policy", parent = PricingPolicy.class)
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
    public void computePrice(ParkingTicket ticket) {
        super.computePrice(ticket);
        ticket.setPrice(ticket.getPrice() + fixedPrice);
    }
}
