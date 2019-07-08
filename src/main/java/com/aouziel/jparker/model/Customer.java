package com.aouziel.jparker.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customers")
@EqualsAndHashCode(exclude = "cars")@ApiModel(description="All details about customers. ")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated car ID")
    private long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "owner_id")
    @ApiModelProperty(notes = "All the cars owned by the customer")
    @Singular
    private Set<Car> cars;
}
