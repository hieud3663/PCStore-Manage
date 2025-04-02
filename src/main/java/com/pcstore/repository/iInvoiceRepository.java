package com.pcstore.repository;

import com.pcstore.model.Invoice;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface iInvoiceRepository extends iRepository<Invoice, String> {
    List<Invoice> findByCustomerId(String customerId);
    List<Invoice> findByEmployeeId(String employeeId);
    List<Invoice> findByDateRange(LocalDate startDate, LocalDate endDate);
    Optional<Invoice> findWithDetails(String invoiceId);
}