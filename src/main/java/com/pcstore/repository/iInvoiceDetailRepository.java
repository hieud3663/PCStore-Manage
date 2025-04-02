package com.pcstore.repository;

import com.pcstore.model.InvoiceDetail;
import java.util.List;

public interface iInvoiceDetailRepository extends iRepository<InvoiceDetail, Long> {
    List<InvoiceDetail> findByInvoiceId(String invoiceId);
    List<InvoiceDetail> findByProductId(String productId);
    void deleteByInvoiceId(String invoiceId);
}