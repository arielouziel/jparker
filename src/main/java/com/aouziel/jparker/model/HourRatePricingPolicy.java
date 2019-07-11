package com.aouziel.jparker.model;

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
    @Column
    @ApiModelProperty(notes = "Hour price in smallest unit of currency")
    private int hourPrice;

    @Column
    @ApiModelProperty(notes = "Code of of price currency")
    private String currencyCode;

    @Override
    public void computePrice(ParkingTicket ticket) {
        if (ticket.getStartTime() == null || ticket.getEndTime() == null) {

        }

        DateTime start = new DateTime(ticket.getStartTime());
        DateTime end = new DateTime(ticket.getEndTime());

        int hourCount = Hours.hoursBetween(end, start).getHours() + 1;
        ticket.setPrice(hourCount * hourPrice);
        ticket.setCurrencyCode(currencyCode);
    }
}
