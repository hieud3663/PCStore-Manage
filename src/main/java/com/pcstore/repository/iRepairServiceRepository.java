package com.pcstore.repository;

import com.pcstore.model.Repair;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RepairService entity operations
 */
public interface iRepairServiceRepository extends iRepository<Repair, Integer> {
    List<Repair> findByCustomerId(String customerId);
    List<Repair> findByStatus(String status);
    List<Repair> findByEmployeeId(String employeeId);
    List<Repair> findDueToday();
    boolean updateStatus(Integer repairServiceId, String status);
    boolean completeService(Integer repairServiceId, LocalDateTime completionDate, String notes, double finalCost);
}