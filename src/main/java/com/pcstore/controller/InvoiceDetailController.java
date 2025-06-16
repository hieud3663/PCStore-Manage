package com.pcstore.controller;

import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Product;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.repository.impl.ProductRepository;
import com.pcstore.service.InvoiceDetailService;
import com.pcstore.service.InvoiceService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.utils.DatabaseConnection;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.LocaleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Controller quản lý các chức năng liên quan đến chi tiết hóa đơn
 */
public class InvoiceDetailController {
    private InvoiceDetailService invoiceDetailService;
    private InvoiceService invoiceService;
    private ProductRepository productRepository;
    private Connection connection;

    private final NumberFormat currencyFormatter = LocaleManager.getInstance().getNumberFormatter();
    
    /**
     * Khởi tạo controller
     */
    public InvoiceDetailController() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            this.invoiceService = ServiceFactory.getInvoiceService();
            this.productRepository = RepositoryFactory.getInstance(connection).getProductRepository();
            this.invoiceDetailService = ServiceFactory.getInstance().getInvoiceDetailService();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                    String.format(ErrorMessage.INVOICE_DETAIL_CONTROLLER_INIT_ERROR.toString(), e.getMessage()), 
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Thêm chi tiết hóa đơn mới
     * @param invoice Hóa đơn
     * @param product Sản phẩm
     * @param quantity Số lượng
     * @param unitPrice Đơn giá
     * @return Chi tiết hóa đơn mới đã được tạo
     */
    public InvoiceDetail addInvoiceDetail(Invoice invoice, Product product, int quantity, BigDecimal unitPrice) {
        try {
            // Kiểm tra số lượng
            if (quantity <= 0) {
                throw new IllegalArgumentException(ErrorMessage.PRODUCT_QUANTITY_NOT_POSITIVE.toString());
            }
            
            // Kiểm tra tồn kho
            if (product.getQuantityInStock() < quantity) {
                throw new IllegalArgumentException(
                        String.format(ErrorMessage.PRODUCT_INSUFFICIENT_STOCK.toString(), 
                                product.getQuantityInStock(), quantity));
            }
            
            // Tạo chi tiết hóa đơn mới
            InvoiceDetail invoiceDetail = new InvoiceDetail();
            invoiceDetail.setInvoice(invoice);
            invoiceDetail.setProduct(product);
            invoiceDetail.setQuantity(quantity);
            invoiceDetail.setUnitPrice(unitPrice);
            
            // Lưu chi tiết hóa đơn
            InvoiceDetail savedDetail = invoiceDetailService.addInvoiceDetail(invoiceDetail);
            
            // Cập nhật tổng tiền hóa đơn
            invoice.updateTotalAmount();
            invoiceService.updateInvoice(invoice);
            
            return savedDetail;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                    String.format(ErrorMessage.INVOICE_DETAIL_ADD_ERROR.toString(), e.getMessage()), 
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Cập nhật chi tiết hóa đơn
     * @param invoiceDetail Chi tiết hóa đơn cần cập nhật
     * @param quantity Số lượng mới
     * @param unitPrice Đơn giá mới
     * @return Chi tiết hóa đơn đã được cập nhật
     */
    public InvoiceDetail updateInvoiceDetail(InvoiceDetail invoiceDetail, int quantity, BigDecimal unitPrice) {
        try {
            // Kiểm tra số lượng
            if (quantity <= 0) {
                throw new IllegalArgumentException(ErrorMessage.PRODUCT_QUANTITY_NOT_POSITIVE.toString());
            }
            
            // Tính toán sự chênh lệch số lượng
            int quantityDiff = quantity - invoiceDetail.getQuantity();
            
            // Kiểm tra tồn kho nếu số lượng tăng
            if (quantityDiff > 0) {
                Product product = productRepository.findById(invoiceDetail.getProduct().getProductId())
                        .orElseThrow(() -> new RuntimeException(ErrorMessage.PRODUCT_NOT_FOUND.toString()));
                        
                if (product.getQuantityInStock() < quantityDiff) {
                    throw new IllegalArgumentException(
                            String.format(ErrorMessage.PRODUCT_INSUFFICIENT_STOCK.toString(), 
                                    product.getQuantityInStock(), quantityDiff));
                }
            }
            
            // Cập nhật thông tin
            invoiceDetail.setQuantity(quantity);
            invoiceDetail.setUnitPrice(unitPrice);
            invoiceDetail.setUpdatedAt(LocalDateTime.now());
            
            // Lưu chi tiết hóa đơn
            InvoiceDetail updatedDetail = invoiceDetailService.updateInvoiceDetail(invoiceDetail);
            
            // Cập nhật tổng tiền hóa đơn
            Invoice invoice = invoiceDetail.getInvoice();
            invoice.updateTotalAmount();
            invoiceService.updateInvoice(invoice);
            
            return updatedDetail;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                    String.format(ErrorMessage.INVOICE_DETAIL_UPDATE_ERROR.toString(), e.getMessage()), 
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Xóa chi tiết hóa đơn
     * @param invoiceDetail Chi tiết hóa đơn cần xóa
     * @return true nếu xóa thành công, ngược lại là false
     */
    public boolean deleteInvoiceDetail(InvoiceDetail invoiceDetail) {
        try {
            boolean success = invoiceDetailService.deleteInvoiceDetail(invoiceDetail.getInvoiceDetailId());
            if (success) {
                // Cập nhật tổng tiền hóa đơn
                Invoice invoice = invoiceDetail.getInvoice();
                invoice.getInvoiceDetails().remove(invoiceDetail);
                invoice.updateTotalAmount();
                invoiceService.updateInvoice(invoice);
            }
            return success;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                    String.format(ErrorMessage.INVOICE_DETAIL_DELETE_ERROR.toString(), e.getMessage()), 
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Tìm chi tiết hóa đơn theo ID
     * @param invoiceDetailId ID chi tiết hóa đơn
     * @return Optional chứa chi tiết hóa đơn nếu tìm thấy
     */
    public Optional<InvoiceDetail> findInvoiceDetailById(int invoiceDetailId) {
        try {
            return invoiceDetailService.findInvoiceDetailById(invoiceDetailId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                    String.format(ErrorMessage.INVOICE_DETAIL_FIND_ERROR.toString(), e.getMessage()), 
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            return Optional.empty();
        }
    }
    
    /**
     * Tìm chi tiết hóa đơn theo mã hóa đơn
     * @param invoiceId mã hóa đơn
     * @return Danh sách chi tiết hóa đơn
     */
    public List<InvoiceDetail> findInvoiceDetailsByInvoiceId(int invoiceId) {
        try {
            return invoiceDetailService.findInvoiceDetailsByInvoiceId(invoiceId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                    String.format(ErrorMessage.INVOICE_DETAIL_FIND_ERROR.toString(), e.getMessage()), 
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            return List.of();
        }
    }
    
    /**
     * Cập nhật bảng hiển thị chi tiết hóa đơn
     * @param invoiceDetails Danh sách chi tiết hóa đơn
     * @param table Bảng hiển thị
     */
    public void updateInvoiceDetailTable(List<InvoiceDetail> invoiceDetails, JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        int stt = 0;
        for (InvoiceDetail detail : invoiceDetails) {
            stt++;
            Object[] row = new Object[6];
            row[0] = stt;
            row[1] = detail.getProduct().getProductId();
            row[2] = detail.getProduct().getProductName();
            row[3] = currencyFormatter.format(detail.getUnitPrice());
            row[4] = detail.getQuantity();
            
            // Tính tổng tiền theo sản phẩm
            BigDecimal lineTotal = detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
            row[5] = currencyFormatter.format(lineTotal);
            
            model.addRow(row);
        }
    }
    
    /**
     * Tạo chi tiết hóa đơn từ sản phẩm được chọn
     * @param invoice Hóa đơn
     * @param product Sản phẩm được chọn
     * @param quantity Số lượng
     * @return Chi tiết hóa đơn mới
     */
    public InvoiceDetail createInvoiceDetailFromProduct(Invoice invoice, Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException(ErrorMessage.PRODUCT_NULL.toString());
        }
        
        if (quantity <= 0) {
            throw new IllegalArgumentException(ErrorMessage.PRODUCT_QUANTITY_NOT_POSITIVE.toString());
        }
        
        // Kiểm tra tồn kho
        if (product.getQuantityInStock() < quantity) {
            throw new IllegalArgumentException(
                    String.format(ErrorMessage.PRODUCT_INSUFFICIENT_STOCK.toString(), 
                            product.getQuantityInStock(), quantity));
        }
        
        // Tạo chi tiết hóa đơn
        InvoiceDetail detail = new InvoiceDetail();
        detail.setInvoice(invoice);
        detail.setProduct(product);
        detail.setQuantity(quantity);
        detail.setUnitPrice(product.getPrice());
        
        return detail;
    }
}