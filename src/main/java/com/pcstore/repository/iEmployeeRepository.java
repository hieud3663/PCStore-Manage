package com.pcstore.repository;

import com.pcstore.model.Employee;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Employee entity operations
 */
public interface iEmployeeRepository {
    Employee save(Employee employee);
    Optional<Employee> findById(String employeeId);
    List<Employee> findAll();
    boolean existsById(String employeeId);
    void deleteById(String employeeId);
    List<Employee> findByPosition(String position);
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByPhoneNumber(String phoneNumber);
    List<Employee> findBySearchTerm(String searchTerm);
    String generateEmployeeId();
}