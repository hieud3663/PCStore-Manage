package com.pcstore.repository;

import com.pcstore.model.Supplier;
import java.util.List;
import java.util.Optional;

public interface iSupplierRepository extends iRepository<Supplier, String> {
    List<Supplier> findByName(String name);
    Optional<Supplier> findByEmail(String email);
    Optional<Supplier> findByPhone(String phone);
}