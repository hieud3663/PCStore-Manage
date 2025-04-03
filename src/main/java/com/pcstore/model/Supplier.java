package com.pcstore.model;

import com.pcstore.model.base.BaseTimeEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class biểu diễn nhà cung cấp
 */
public class Supplier extends BaseTimeEntity {
    private String supplierId;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
    
    // Danh sách sản phẩm từ nhà cung cấp - Sử dụng List vì có thể có nhiều sản phẩm
    private List<Product> products = new ArrayList<>();
    
    // Danh sách đơn nhập hàng - Sử dụng List vì có thể có nhiều đơn nhập hàng
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();

    @Override
    public Object getId() {
        return supplierId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        if (supplierId == null || supplierId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhà cung cấp không được để trống");
        }
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhà cung cấp không được để trống");
        }
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !isValidPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ");
        }
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !isValidEmail(email)) {
            throw new IllegalArgumentException("Email không hợp lệ");
        }
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Địa chỉ không được để trống");
        }
        this.address = address;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
    
    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Sản phẩm không được để trống");
        }
        if (!products.contains(product)) {
            products.add(product);
            product.setSupplier(this);
        }
    }
    
    public void removeProduct(Product product) {
        if (products.remove(product)) {
            product.setSupplier(null);
        }
    }
    
    public List<PurchaseOrder> getPurchaseOrders() {
        return purchaseOrders;
    }
    
    public void setPurchaseOrders(List<PurchaseOrder> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }
    
    public void addPurchaseOrder(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null) {
            throw new IllegalArgumentException("Đơn nhập hàng không được để trống");
        }
        if (!purchaseOrders.contains(purchaseOrder)) {
            purchaseOrders.add(purchaseOrder);
            purchaseOrder.setSupplier(this);
        }
    }
    
    public void removePurchaseOrder(PurchaseOrder purchaseOrder) {
        if (purchaseOrders.remove(purchaseOrder)) {
            purchaseOrder.setSupplier(null);
        }
    }

    // Phương thức kiểm tra số điện thoại hợp lệ
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{10,11}");
    }

    // Phương thức kiểm tra email hợp lệ
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    // Phương thức kiểm tra xem nhà cung cấp có thể xóa không
    public boolean canDelete() {
        // Kiểm tra xem có đơn nhập hàng nào đang trong trạng thái xử lý không
        for (PurchaseOrder order : purchaseOrders) {
            if ("Processing".equals(order.getStatus())) {
                return false;
            }
        }
        return true;
    }

    // Factory method để tạo nhà cung cấp mới
    public static Supplier createNew(String supplierId, String name, 
                                   String phoneNumber, String email, String address) {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(supplierId);
        supplier.setName(name);
        supplier.setPhoneNumber(phoneNumber);
        supplier.setEmail(email);
        supplier.setAddress(address);
        return supplier;
    }
}