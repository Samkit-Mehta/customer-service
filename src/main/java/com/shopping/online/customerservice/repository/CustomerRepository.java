package com.shopping.online.customerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopping.online.customerservice.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

}
