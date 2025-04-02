package com.pcstore.repository;

import com.pcstore.model.PurchaseOrder;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PurchaseOrder entity operations
 */
public interface iPurchaseOrderRepository extends iRepository<PurchaseOrder, String> {
    List<PurchaseOrder> findBySupplier(String supplierId);
    List<PurchaseOrder> findByStatus(String status);
    List<PurchaseOrder> findByDateRange(LocalDate startDate, LocalDate endDate);
    boolean updateStatus(String purchaseOrderId, String status);
}