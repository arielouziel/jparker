package com.aouziel.jparker.database;

import com.aouziel.jparker.model.Car;
import com.aouziel.jparker.model.CarPowerType;
import com.aouziel.jparker.model.Customer;
import com.aouziel.jparker.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomerSeeder implements ApplicationRunner {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerSeeder(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        this.customerRepository.save(Customer.builder()
                .firstName("Ariel")
                .lastName("Ouziel")
                .car(Car.builder()
                        .brand("Renault")
                        .model("Clio")
                        .color("blue")
                        .powerType(CarPowerType.sedan)
                        .build()
                )
                .car(Car.builder()
                        .brand("Volkswagen")
                        .model("Golf SW")
                        .color("grey")
                        .powerType(CarPowerType.twentyKw)
                        .build()
                )
                .build()
        );
    }
}
