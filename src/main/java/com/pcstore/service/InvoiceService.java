package com.pcstore.service;

import com.pcstore.model.Invoice;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.repository.impl.InvoiceRepository;
import com.pcstore.repository.impl.ProductRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý logic nghiệp vụ liên quan đến hóa đơn
 */
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    
    /**
     * Repository cho hóa đơn
     * @param connection Kết nối đến cơ sở dữ liệu
     * @param invoiceRepository Repository hóa đơn
     */
    public InvoiceService(Connection connection, RepositoryFactory repositoryFactory) {
        this.invoiceRepository = new InvoiceRepository(connection, repositoryFactory);
        this.productRepository = new ProductRepository(connection); 
    }

    /**
     * Khởi tạo service với repository
     * @param invoiceRepository Repository hóa đơn
     * @param productRepository Repository sản phẩm để cập nhật tồn kho
     */
    public InvoiceService(InvoiceRepository invoiceRepository, ProductRepository productRepository) {
        this.invoiceRepository = invoiceRepository;
        this.productRepository = productRepository;
    }
    
    /**
     * Tạo hóa đơn mới
     * @param invoice Thông tin hóa đơn
     * @return Hóa đơn đã được tạo
     */
    public Invoice createInvoice(Invoice invoice) {
        // Cập nhật số lượng tồn kho khi tạo hóa đơn
        invoice.getInvoiceDetails().forEach(detail -> {
            // Giảm số lượng tồn kho với số lượng âm
            productRepository.updateStockQuantity(detail.getProduct().getProductId(), -detail.getQuantity());
        });
        
        return invoiceRepository.add(invoice);
    }
    
    /**
     * Cập nhật thông tin hóa đơn
     * @param invoice Thông tin hóa đơn mới
     * @return Hóa đơn đã được cập nhật
     */
    public Invoice updateInvoice(Invoice invoice) {
        // Đây là một hành động phức tạp, cần xử lý cẩn thận về tồn kho
        // Nên lấy hóa đơn cũ để so sánh thay đổi và điều chỉnh tồn kho phù hợp
        Optional<Invoice> oldInvoiceOpt = invoiceRepository.findById(invoice.getInvoiceId());
        if (oldInvoiceOpt.isPresent()) {
            Invoice oldInvoice = oldInvoiceOpt.get();
            
            // Hoàn trả số lượng sản phẩm của hóa đơn cũ vào kho
            oldInvoice.getInvoiceDetails().forEach(detail -> {
                productRepository.updateStockQuantity(detail.getProduct().getProductId(), detail.getQuantity());
            });
            
            // Trừ số lượng sản phẩm của hóa đơn mới từ kho
            invoice.getInvoiceDetails().forEach(detail -> {
                productRepository.updateStockQuantity(detail.getProduct().getProductId(), -detail.getQuantity());
            });
        }
        
        return invoiceRepository.update(invoice);
    }
    
    /**
     * Xóa hóa đơn theo ID
     * @param invoiceId ID của hóa đơn
     * @return true nếu xóa thành công, ngược lại là false
     */
    public boolean deleteInvoice(Integer invoiceId) {
        // Đây có thể là một hành động nhạy cảm, cần cân nhắc xử lý tồn kho và các vấn đề liên quan
        // Có thể cần kiểm tra xem hóa đơn này đã được thanh toán chưa, có liên quan đến bảo hành không, v.v.
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            
            // Hoàn trả số lượng tồn kho
            invoice.getInvoiceDetails().forEach(detail -> {
                productRepository.updateStockQuantity(detail.getProduct().getProductId(), detail.getQuantity());
            });
            
            return invoiceRepository.delete(invoiceId);
        }
        
        return false;
    }
    
    /**
     * Tìm hóa đơn theo ID
     * @param invoiceId ID của hóa đơn
     * @return Optional chứa hóa đơn nếu tìm thấy
     */
    public Optional<Invoice> findInvoiceById(Integer invoiceId) {
        return invoiceRepository.findById(invoiceId);
    }
    
    
    /**
     * Lấy danh sách tất cả hóa đơn
     * @return Danh sách hóa đơn
     */
    public List<Invoice> findAllInvoices() {
        return invoiceRepository.findAll();
    }
    
    /**
     * Tìm hóa đơn theo khách hàng
     * @param customerId ID của khách hàng
     * @return Danh sách hóa đơn của khách hàng
     */
    public List<Invoice> findInvoicesByCustomer(String customerId) {
        return invoiceRepository.findByCustomerId(customerId);
    }
    
    /**
     * Tìm hóa đơn theo nhân viên
     * @param employeeId ID của nhân viên
     * @return Danh sách hóa đơn được tạo bởi nhân viên
     */
    public List<Invoice> findInvoicesByEmployee(String employeeId) {
        return invoiceRepository.findByEmployeeId(employeeId);
    }
    
    /**
     * Tìm hóa đơn trong khoảng thời gian
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Danh sách hóa đơn trong khoảng thời gian
     */
    public List<Invoice> findInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return invoiceRepository.findByDateRange(startDate, endDate);
    }
    
    /**
     * Kiểm tra hóa đơn có tồn tại không
     * @param invoiceId ID của hóa đơn
     * @return true nếu hóa đơn tồn tại, ngược lại là false
     */
    public boolean invoiceExists(Integer invoiceId) {
        return invoiceRepository.exists(invoiceId);
    }
    
    /**
     * Tính tổng doanh thu trong khoảng thời gian
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Tổng doanh thu
     */
    public BigDecimal calculateRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        List<Invoice> invoices = invoiceRepository.findByDateRange(startDate, endDate);
        return invoices.stream()
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}