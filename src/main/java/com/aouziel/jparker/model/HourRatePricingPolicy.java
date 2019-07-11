package com.aouziel.jparker.model;

import com.aouziel.jparker.exception.PreconditionFailedException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.Hours;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("HOURRATEPRICINGPOLICY")
@ApiModel(description = "Details about hour rate pricing policy", parent = PricingPolicy.class)
public class HourRatePricingPolicy extends PricingPolicy {
    /**
     * Hour price in smallest currency unit (eg. cents)
     */
    @Column
    @ApiModelProperty(notes = "Hour price in smallest unit of currency")
    private int hourPrice;

    /**
     * Currency code (EUR, USD, etc.)
     */
    @Column
    @ApiModelProperty(notes = "Code of of price currency")
    private String currencyCode;

    /**
     * Compute price for provided ticket. Every started hour count as a complete hour. <br>
     * Mutate the ticket by setting the price and currency code
     * @param ticket the ticket with a start and end time
     * @throws PreconditionFailedException
     */
    @Override
    public void computePrice(ParkingTicket ticket) throws PreconditionFailedException {
        if (ticket.getStartTime() == null || ticket.getEndTime() == null) {
            throw new PreconditionFailedException("Need start time and end time");
        }

        DateTime start = new DateTime(ticket.getStartTime());
        DateTime end = new DateTime(ticket.getEndTime());

        int hourCount = Hours.hoursBetween(end, start).getHours() + 1;
        ticket.setPrice(hourCount * hourPrice);
        ticket.setCurrencyCode(currencyCode);
    }
}
