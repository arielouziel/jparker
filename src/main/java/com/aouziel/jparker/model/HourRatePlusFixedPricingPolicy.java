package com.aouziel.jparker.model;

import com.aouziel.jparker.exception.PreconditionFailedException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *  Implements pricing policy with an hour rate + fixed amount
 */
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

    /**
     * Fixed price in smallest currency unit (eg. cents)
     */
    @Column
    @ApiModelProperty(notes = "Fixed amount")
    private int fixedPrice;

    /**
     * Compute price for provided ticket. Every started hour count as a complete hour.
     * The fixed amount is added to the total
     * @param ticket
     */
    @Override
    public void computePrice(ParkingTicket ticket) throws PreconditionFailedException {
        super.computePrice(ticket);
        ticket.setPrice(ticket.getPrice() + fixedPrice);
    }
}
