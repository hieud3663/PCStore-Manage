package com.pcstore.model;

import com.pcstore.model.base.BaseTimeEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Class biểu diễn đơn nhập hàng
 */
public class PurchaseOrder extends BaseTimeEntity {
    private String purchaseOrderId;
    private LocalDateTime orderDate;
    private Supplier supplier;
    private Employee employee;
    private BigDecimal totalAmount;
    private String notes;
    private String status; // Pending, Processing, Completed, Cancelled
    private List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<>();

    @Override
    public Object getId() {
        return purchaseOrderId;
    }


    public String getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(String purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        if (orderDate == null) {
            throw new IllegalArgumentException("Ngày đặt hàng không được để trống");
        }
        this.orderDate = orderDate;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Nhà cung cấp không được để trống");
        }
        this.supplier = supplier;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Nhân viên không được để trống");
        }
        this.employee = employee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        if (totalAmount == null) {
            throw new IllegalArgumentException("Tổng tiền không được để trống");
        }
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tổng tiền không được âm");
        }
        this.totalAmount = totalAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái đơn nhập hàng không được để trống");
        }
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Trạng thái đơn nhập hàng không hợp lệ");
        }
        this.status = status;
    }

    public List<PurchaseOrderDetail> getPurchaseOrderDetails() {
        return purchaseOrderDetails;
    }

    public void setPurchaseOrderDetails(List<PurchaseOrderDetail> purchaseOrderDetails) {
        this.purchaseOrderDetails = purchaseOrderDetails;
    }
    
    public void addPurchaseOrderDetail(PurchaseOrderDetail detail) {
        if (!canUpdate()) {
            throw new IllegalStateException("Không thể thêm chi tiết cho đơn hàng đã hoàn thành hoặc đã hủy");
        }
        if (isValidDetail(detail)) {
            this.purchaseOrderDetails.add(detail);
            detail.setPurchaseOrder(this);
            updateTotalAmount();
        }
    }
    
    public void removePurchaseOrderDetail(PurchaseOrderDetail detail) {
        if (!canUpdate()) {
            throw new IllegalStateException("Không thể xóa chi tiết của đơn hàng đã hoàn thành hoặc đã hủy");
        }
        if (this.purchaseOrderDetails.remove(detail)) {
            detail.setPurchaseOrder(null);
            updateTotalAmount();
        }
    }
    
    // Phương thức tính tổng tiền nhập hàng
    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseOrderDetail detail : purchaseOrderDetails) {
            total = total.add(detail.getSubtotal());
        }
        return total;
    }
    
    // Phương thức cập nhật tổng tiền nhập hàng
    public void updateTotalAmount() {
        this.totalAmount = calculateTotal();
    }

    // Phương thức kiểm tra xem đơn nhập hàng có thể cập nhật không
    public boolean canUpdate() {
        return !("Completed".equals(status) || "Cancelled".equals(status));
    }
    
    // Phương thức kiểm tra xem đơn nhập hàng có thể hoàn thành không
    public boolean canComplete() {
        if (!hasSupplier()) {
            throw new IllegalStateException("Đơn nhập hàng chưa có nhà cung cấp");
        }
        if (!hasEmployee()) {
            throw new IllegalStateException("Đơn nhập hàng chưa có nhân viên phụ trách");
        }
        if (purchaseOrderDetails.isEmpty()) {
            throw new IllegalStateException("Đơn nhập hàng chưa có chi tiết");
        }
        return "Delivering".equals(status);
    }
    
    private boolean hasSupplier() {
        return supplier != null && supplier.getSupplierId() != null;
    }
    
    private boolean hasEmployee() {
        return employee != null && employee.getEmployeeId() != null;
    }
    
    // Phương thức kiểm tra xem đơn nhập hàng có thể hủy không
    public boolean canCancel() {
        return !("Completed".equals(status) || "Cancelled".equals(status));
    }
    
    // Phương thức hoàn thành đơn nhập hàng
    public void complete() {
        if (!canComplete()) {
            throw new IllegalStateException("Không thể hoàn thành đơn nhập hàng trong trạng thái hiện tại");
        }
        this.status = "Completed";
        this.setUpdatedAt(LocalDateTime.now());
        
        // Cập nhật tồn kho cho tất cả các sản phẩm trong đơn
        for (PurchaseOrderDetail detail : purchaseOrderDetails) {
            detail.updateStock();
        }
    }
    
    // Phương thức hủy đơn nhập hàng
    public void cancel() {
        if (!canCancel()) {
            throw new IllegalStateException("Không thể hủy đơn nhập hàng trong trạng thái hiện tại");
        }
        this.status = "Cancelled";
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    // Phương thức tạo mới đơn nhập hàng
    public static PurchaseOrder createNew(Supplier supplier, Employee employee) {
        if (supplier == null) {
            throw new IllegalArgumentException("Nhà cung cấp không được để trống");
        }
        if (employee == null) {
            throw new IllegalArgumentException("Nhân viên không được để trống");
        }
        
        PurchaseOrder order = new PurchaseOrder();
        order.setSupplier(supplier);
        order.setEmployee(employee);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("Pending");
        order.setTotalAmount(BigDecimal.ZERO);
        return order;
    }
    
    // Phương thức kiểm tra chi tiết đơn hàng hợp lệ
    private boolean isValidDetail(PurchaseOrderDetail detail) {
        if (detail == null) {
            throw new IllegalArgumentException("Chi tiết đơn nhập hàng không được để trống");
        }
        if (detail.getProduct() == null) {
            throw new IllegalArgumentException("Sản phẩm trong chi tiết không được để trống");
        }
        if (detail.getQuantity() <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        if (detail.getUnitCost() == null || detail.getUnitCost().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Đơn giá phải lớn hơn 0");
        }
        return true;
    }
    
    private boolean isValidStatus(String status) {
        return status.equals("Pending") || 
                status.equals("Delivering") ||
               status.equals("Completed") ||
               status.equals("Cancelled");
    }
}