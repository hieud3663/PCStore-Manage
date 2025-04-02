package com.pcstore.repository;

import com.pcstore.model.Customer;
import java.util.List;
import java.util.Optional;

public interface iCustomerRepository extends iRepository<Customer, String> {
    List<Customer> findByName(String name);
    Optional<Customer> findByPhone(String phone);
    Optional<Customer> findByEmail(String email);
}