package com.aouziel.jparker.repository;

import com.aouziel.jparker.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
