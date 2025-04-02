package com.pcstore.repository;

import com.pcstore.model.RepairService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RepairService entity operations
 */
public interface iRepairServiceRepository extends iRepository<RepairService, Integer> {
    List<RepairService> findByCustomerId(String customerId);
    List<RepairService> findByStatus(String status);
    List<RepairService> findByEmployeeId(String employeeId);
    List<RepairService> findDueToday();
    boolean updateStatus(Integer repairServiceId, String status);
    boolean completeService(Integer repairServiceId, LocalDateTime completionDate, String notes, double finalCost);
}