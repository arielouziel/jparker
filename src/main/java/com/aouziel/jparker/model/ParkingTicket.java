package com.aouziel.jparker.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Date;

@Builder @AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
public class ParkingTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated slot occupation item ID")
    private long id;

    @ApiModelProperty(notes = "The ticket number")
    @Column(nullable = false, unique = true)
    private String number;

    @ApiModelProperty(notes = "The time when occupation has started")
    @Column(nullable = false)
    private Date startTime;

    @ApiModelProperty(notes = "The time when occupation has ended")
    @Column
    private Date endTime;

    @ApiModelProperty(notes = "The power type of the car using the slot")
    @Column
    private CarPowerType carPowerType;

    @OneToOne
    @JoinColumn(name = "slot_id", referencedColumnName = "id")
    private ParkingSlot slot;

    @ApiModelProperty(notes = "The billed price for this ticket")
    @Column
    private int price;

    @ApiModelProperty(notes = "The currency code for billed price")
    @Column
    private String currencyCode;

    @ApiModelProperty(notes = "The billed price in human readable format")
    public String getFormattedPrice() {
        if (price == 0 || currencyCode == null) {
            return "";
        }

        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        Currency currency = Currency.getInstance(currencyCode);
        formatter.setCurrency(currency);

        return formatter.format(price / 100.0);
    }
}
