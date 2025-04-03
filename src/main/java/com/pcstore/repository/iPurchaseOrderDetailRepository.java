package com.pcstore.repository;

import com.pcstore.model.PurchaseOrderDetail;
import java.util.List;

/**
 * Repository interface for PurchaseOrderDetail entity operations
 */
public interface iPurchaseOrderDetailRepository extends iRepository<PurchaseOrderDetail, Long> {
    List<PurchaseOrderDetail> findByPurchaseOrderId(String purchaseOrderId);
    List<PurchaseOrderDetail> findByProductId(String productId);
    void deleteByPurchaseOrderId(String purchaseOrderId);
}