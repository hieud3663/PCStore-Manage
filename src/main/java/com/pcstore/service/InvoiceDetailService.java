package com.pcstore.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Product;
import com.pcstore.repository.impl.InvoiceDetailRepository;
import com.pcstore.repository.impl.InvoiceRepository;
import com.pcstore.repository.impl.ProductRepository;

/**
 * Service xử lý logic nghiệp vụ liên quan đến chi tiết hóa đơn
 */
public class InvoiceDetailService {
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;
    private final Logger logger = Logger.getLogger(InvoiceDetailService.class.getName());
    
    /**
     * Khởi tạo service với repository
     * @param invoiceDetailRepository Repository chi tiết hóa đơn
     * @param productRepository Repository sản phẩm để cập nhật tồn kho
     * @param invoiceRepository Repository hóa đơn
     */
    public InvoiceDetailService(InvoiceDetailRepository invoiceDetailRepository, ProductRepository productRepository, InvoiceRepository invoiceRepository) {
        this.invoiceDetailRepository = invoiceDetailRepository;
        this.productRepository = productRepository;
        this.invoiceRepository = invoiceRepository;
    }
    
    /**
     * Thêm chi tiết hóa đơn mới
     * @param invoiceDetail Thông tin chi tiết hóa đơn
     * @return Chi tiết hóa đơn đã được thêm
     */
    public InvoiceDetail addInvoiceDetail(InvoiceDetail invoiceDetail) {
        // Kiểm tra số lượng tồn kho
        String productId = invoiceDetail.getProduct().getProductId();
        int quantity = invoiceDetail.getQuantity();
        
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            if (product.getStockQuantity() < quantity) {
                throw new IllegalArgumentException("Số lượng sản phẩm vượt quá tồn kho");
            }
            
        } else {
            throw new IllegalArgumentException("Sản phẩm không tồn tại");
        }
        
        return invoiceDetailRepository.add(invoiceDetail);
    }
    
    /**
     * Cập nhật thông tin chi tiết hóa đơn
     * @param invoiceDetail Thông tin chi tiết hóa đơn mới
     * @return Chi tiết hóa đơn đã được cập nhật
     */
    public InvoiceDetail updateInvoiceDetail(InvoiceDetail invoiceDetail) {
        // Tìm chi tiết hóa đơn cũ để so sánh số lượng
        Optional<InvoiceDetail> oldDetailOpt = invoiceDetailRepository.findById(invoiceDetail.getInvoiceDetailId());
        if (!oldDetailOpt.isPresent()) {
            throw new IllegalArgumentException("Chi tiết hóa đơn không tồn tại");
        }
        
        InvoiceDetail oldDetail = oldDetailOpt.get();
        String productId = invoiceDetail.getProduct().getProductId();
        int oldQuantity = oldDetail.getQuantity();
        int newQuantity = invoiceDetail.getQuantity();
        int quantityDiff = newQuantity - oldQuantity;
        
        // Nếu số lượng tăng, kiểm tra tồn kho
        if (quantityDiff > 0) {
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                if (product.getStockQuantity() < quantityDiff) {
                    throw new IllegalArgumentException("Số lượng sản phẩm vượt quá tồn kho");
                }
            } else {
                throw new IllegalArgumentException("Sản phẩm không tồn tại");
            }
        }
        
        // Repository sẽ tự xử lý việc cập nhật tồn kho
        return invoiceDetailRepository.update(invoiceDetail);
    }
    
    /**
     * Xóa chi tiết hóa đơn theo ID
     * @param invoiceDetailId ID của chi tiết hóa đơn
     * @return true nếu xóa thành công, ngược lại là false
     */
    public boolean deleteInvoiceDetail(Integer invoiceDetailId) {
        // Repository sẽ tự xử lý việc hoàn trả số lượng tồn kho
        return invoiceDetailRepository.delete(invoiceDetailId);
    }
    
    /**
     * Tìm chi tiết hóa đơn theo ID
     * @param invoiceDetailId ID của chi tiết hóa đơn
     * @return Optional chứa chi tiết hóa đơn nếu tìm thấy
     */
    public Optional<InvoiceDetail> findInvoiceDetailById(Integer invoiceDetailId) {
        return invoiceDetailRepository.findById(invoiceDetailId);
    }
    
    /**
     * Lấy danh sách tất cả chi tiết hóa đơn
     * @return Danh sách chi tiết hóa đơn
     */
    public List<InvoiceDetail> findAllInvoiceDetails() {
        return invoiceDetailRepository.findAll();
    }
    
    /**
     * Tìm chi tiết hóa đơn theo mã hóa đơn
     * @param invoiceId Mã hóa đơn
     * @return Danh sách chi tiết hóa đơn
     */
    public List<InvoiceDetail> findInvoiceDetailsByInvoiceId(Integer invoiceId) {
        return invoiceDetailRepository.findByInvoiceId(invoiceId);
    }
    
    /**
     * Tìm chi tiết hóa đơn theo mã sản phẩm
     * @param productId Mã sản phẩm
     * @return Danh sách chi tiết hóa đơn
     */
    public List<InvoiceDetail> findInvoiceDetailsByProductId(String productId) {
        return invoiceDetailRepository.findByProductId(productId);
    }
    
    /**
     * Kiểm tra chi tiết hóa đơn có tồn tại không
     * @param invoiceDetailId ID của chi tiết hóa đơn
     * @return true nếu chi tiết hóa đơn tồn tại, ngược lại là false
     */
    public boolean invoiceDetailExists(Integer invoiceDetailId) {
        return invoiceDetailRepository.exists(invoiceDetailId);
    }
    
    /**
     * Lưu chi tiết hóa đơn (thêm mới nếu chưa tồn tại, cập nhật nếu đã tồn tại)
     * @param invoiceDetail Chi tiết hóa đơn cần lưu
     * @return Chi tiết hóa đơn đã được lưu
     */
    public InvoiceDetail saveInvoiceDetail(InvoiceDetail invoiceDetail) {
        // Kiểm tra số lượng tồn kho nếu là thêm mới
        if (invoiceDetail.getInvoiceDetailId() == 0) {
            String productId = invoiceDetail.getProduct().getProductId();
            int quantity = invoiceDetail.getQuantity();
            
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                if (product.getStockQuantity() < quantity) {
                    throw new IllegalArgumentException("Số lượng sản phẩm vượt quá tồn kho");
                }
            } else {
                throw new IllegalArgumentException("Sản phẩm không tồn tại");
            }
        } else {
            // Nếu là cập nhật, kiểm tra số lượng tồn kho
            return updateInvoiceDetail(invoiceDetail);
        }
        
        return invoiceDetailRepository.save(invoiceDetail);
    }
    
    /**
     * Tìm tất cả chi tiết hóa đơn của khách hàng
     * @param customerId Mã khách hàng
     * @return Danh sách chi tiết hóa đơn
     */
    public List<InvoiceDetail> findByCustomerId(String customerId) {
        try {
            // Lấy các hóa đơn của khách hàng
            List<Invoice> invoices = invoiceRepository.findByCustomerId(customerId);
            
            if (invoices.isEmpty()) {
                logger.info("No invoices found for customer ID: " + customerId);
                return new ArrayList<>();
            }
            
            // Log số lượng hóa đơn tìm thấy để debug
            logger.info("Found " + invoices.size() + " invoices for customer ID: " + customerId);
            
            List<InvoiceDetail> allDetails = new ArrayList<>();
            
            // Với mỗi hóa đơn, lấy chi tiết và thêm vào danh sách kết quả
            for (Invoice invoice : invoices) {
                List<InvoiceDetail> details = invoiceDetailRepository.findByInvoiceId(invoice.getInvoiceId());
                
                // Log số lượng chi tiết hóa đơn
                logger.info("Found " + details.size() + " details for invoice ID: " + invoice.getInvoiceId());
                
                // Thêm thông tin bổ sung vào mỗi chi tiết
                for (InvoiceDetail detail : details) {
                    // Thêm thông tin khách hàng
                    detail.getInvoice().getCustomer().setCustomerId(customerId);
                    
                    // Fix: Correctly set the customer's full name from the invoice
                    detail.getInvoice().getCustomer().setFullName(invoice.getCustomer().getFullName()); 
                    
                    // Set purchase date from invoice
                    detail.setCreatedAt(invoice.getInvoiceDate());
                    
                    // Thêm thông tin sản phẩm
                    Optional<Product> productOpt = productRepository.findById(detail.getProduct().getProductId());
                    if (productOpt.isPresent()) {
                        Product product = productOpt.get();
                        // Since setProductName and setSupplier don't exist in InvoiceDetail, we'll ensure the product info is updated
                        detail.setProduct(product);
                        
                        // Set warranty end date if you have a method for it in InvoiceDetail or related objects
                        if (invoice.getInvoiceDate() != null && detail.getWarranty() != null) {
                            detail.getWarranty().setEndDate(invoice.getInvoiceDate().plusMonths(12));
                        }
                    }
                    
                    allDetails.add(detail);
                }
            }
            
            return allDetails;
        } catch (Exception e) {
            logger.warning("Error finding invoice details by customer ID: " + e.getMessage());
            e.printStackTrace(); // Thêm stack trace để debug
            return new ArrayList<>();
        }
    }
    
    /**
     * Tìm các chi tiết hóa đơn trong khoảng thời gian chỉ định
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Danh sách chi tiết hóa đơn
     */
    public List<InvoiceDetail> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<InvoiceDetail> result = new ArrayList<>();
        
        try {
            // Lấy danh sách hóa đơn trong khoảng thời gian
            List<Invoice> invoices = invoiceRepository.findByDateRange(startDate, endDate);
            
            if (invoices.isEmpty()) {
                return result;
            }
            
            // Tạo danh sách các ID hóa đơn để tìm kiếm chi tiết
            List<Integer> invoiceIds = new ArrayList<>();
            for (Invoice invoice : invoices) {
                invoiceIds.add(invoice.getInvoiceId());
            }
            
            // Lấy chi tiết hóa đơn theo danh sách ID
            List<InvoiceDetail> allDetails = invoiceDetailRepository.findByInvoiceIds(invoiceIds);
            
            // Thêm thông tin sản phẩm cho mỗi chi tiết
            for (InvoiceDetail detail : allDetails) {
                // Tìm hóa đơn tương ứng
                for (Invoice invoice : invoices) {
                    if (invoice.getInvoiceId().equals(detail.getInvoice().getInvoiceId())) {
                        detail.setInvoice(invoice);
                        break;
                    }
                }
                
                // Thêm thông tin sản phẩm
                Optional<Product> productOpt = productRepository.findById(detail.getProduct().getProductId());
                if (productOpt.isPresent()) {
                    detail.setProduct(productOpt.get());
                }
                
                result.add(detail);
            }
            
        } catch (Exception e) {
            logger.warning("Error finding invoice details by date range: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
}