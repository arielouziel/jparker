package com.aouziel.jparker.repository;

import com.aouziel.jparker.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
