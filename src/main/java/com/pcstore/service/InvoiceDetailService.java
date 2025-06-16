package com.pcstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Product;
import com.pcstore.repository.impl.InvoiceDetailRepository;
import com.pcstore.repository.impl.InvoiceRepository;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Product;
import com.pcstore.repository.iInvoiceDetailRepository;
import com.pcstore.repository.impl.InvoiceDetailRepository;
import com.pcstore.repository.Repository;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.utils.DatabaseConnection;
import com.pcstore.utils.ErrorMessage;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý logic nghiệp vụ liên quan đến chi tiết hóa đơn
 */
public class InvoiceDetailService {
    
    Connection connection;
  
    private final Repository<InvoiceDetail, Integer> invoiceDetailRepository;
    private final Repository<Product, String> productRepository;
  
    /**
     * Khởi tạo service với repository
     * @param invoiceDetailRepository Repository chi tiết hóa đơn
     * @param productRepository Repository sản phẩm để cập nhật tồn kho
     */
    public InvoiceDetailService(Repository<InvoiceDetail, Integer> invoiceDetailRepository, 
                              Repository<Product, String> productRepository) {
        this.invoiceDetailRepository = invoiceDetailRepository;
        this.productRepository = productRepository;
        this.connection = DatabaseConnection.getInstance().getConnection();

    }


    //Khởi tạo với connection
    public InvoiceDetailService(Connection connection){
        this.connection = connection;
        this.invoiceDetailRepository = RepositoryFactory.getInstance(connection).getInvoiceDetailRepository();
        this.productRepository = RepositoryFactory.getInstance(connection).getProductRepository();
       
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
            if (product.getQuantityInStock() < quantity) {
                throw new IllegalArgumentException(ErrorMessage.PRODUCT_INSUFFICIENT_STOCK.toString());
            }
            
            // Cập nhật chi tiết hóa đơn với thông tin đầy đủ của sản phẩm
            invoiceDetail.setProduct(product);
            
            // Thêm chi tiết hóa đơn - không xử lý tồn kho ở đây vì đã được xử lý trong repository
            return invoiceDetailRepository.add(invoiceDetail);
        } else {
            throw new IllegalArgumentException(ErrorMessage.PRODUCT_NULL.toString());
        }
    }
    
    /**
     * Cập nhật thông tin chi tiết hóa đơn
     * @param invoiceDetail Thông tin chi tiết hóa đơn mới
     * @return Chi tiết hóa đơn đã được cập nhật
     */
    public InvoiceDetail updateInvoiceDetail(InvoiceDetail invoiceDetail) {
        return invoiceDetailRepository.update(invoiceDetail);
    }
    
    /**
     * Xóa chi tiết hóa đơn theo ID
     * @param invoiceDetailId ID của chi tiết hóa đơn
     * @return true nếu xóa thành công, ngược lại là false
     */
    public boolean deleteInvoiceDetail(Integer invoiceDetailId) {
        // Việc hoàn trả số lượng tồn kho được xử lý trong repository
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
        return ((InvoiceDetailRepository) invoiceDetailRepository).findByInvoiceId(invoiceId);
    }
    
    /**
     * Tìm chi tiết hóa đơn theo mã sản phẩm
     * @param productId Mã sản phẩm
     * @return Danh sách chi tiết hóa đơn
     */
    public List<InvoiceDetail> findInvoiceDetailsByProductId(String productId) {
        if (invoiceDetailRepository instanceof iInvoiceDetailRepository) {
            return ((iInvoiceDetailRepository) invoiceDetailRepository).findByProductId(productId);
        }
        throw new UnsupportedOperationException("Repository không hỗ trợ phương thức findByProductId");
    }

  
    /**
     * Xóa tất cả chi tiết hóa đơn theo mã hóa đơn
     * @param invoiceId Mã hóa đơn
     */
    public void deleteInvoiceDetailsByInvoiceId(String invoiceId) {
        // Việc hoàn trả số lượng tồn kho được xử lý trong repository
        if (invoiceDetailRepository instanceof iInvoiceDetailRepository) {
            ((iInvoiceDetailRepository) invoiceDetailRepository).deleteByInvoiceId(invoiceId);
        } else {
            throw new UnsupportedOperationException("Repository không hỗ trợ phương thức deleteByInvoiceId");
        }
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
     * Lưu (thêm mới hoặc cập nhật) chi tiết hóa đơn
     * @param invoiceDetail Chi tiết hóa đơn cần lưu
     * @return Chi tiết hóa đơn đã được lưu
     */
    public InvoiceDetail saveInvoiceDetail(InvoiceDetail invoiceDetail) {
        if (invoiceDetail.getInvoiceDetailId() == null) {
            return addInvoiceDetail(invoiceDetail);
        } else {
            return updateInvoiceDetail(invoiceDetail);
        }
    }

        /**
     * Tìm chi tiết hóa đơn trong khoảng thời gian
     * @param startDate Thời gian bắt đầu
     * @param endDate Thời gian kết thúc
     * @return Danh sách chi tiết hóa đơn trong khoảng thời gian
     */
    public List<InvoiceDetail> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<InvoiceDetail> result = new ArrayList<>();
        
        InvoiceRepository invoiceRepository = RepositoryFactory.getInstance(connection).getInvoiceRepository();
        // Lấy danh sách hóa đơn trong khoảng thời gian
        List<Invoice> invoices = invoiceRepository.findByDateRange(startDate, endDate);
        
        // Với mỗi hóa đơn, lấy chi tiết hóa đơn và thêm vào kết quả
        for (Invoice invoice : invoices) {
            List<InvoiceDetail> details = findInvoiceDetailsByInvoiceId(invoice.getInvoiceId());
            if (details != null) {
                result.addAll(details);
            }
        }
        
        return result;
    }
}