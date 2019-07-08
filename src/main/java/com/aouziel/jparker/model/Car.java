package com.aouziel.jparker.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "cars")
@Builder
@EqualsAndHashCode(exclude = "owner")
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description="All details about cars. ")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated car ID")
    private long id;

    @Column
    @ApiModelProperty(notes = "The car brand")
    private String brand;

    @Column
    @ApiModelProperty(notes = "The car model")
    private String model;

    @Column
    @ApiModelProperty(notes = "The car color")
    private String color;

    @Column(nullable = false)
    @ApiModelProperty(notes = "The car power type")
    private CarPowerType powerType;

    @ManyToOne
    @JoinColumn
    @ApiModelProperty(notes = "The owner of the car")
    private Customer owner;
}
