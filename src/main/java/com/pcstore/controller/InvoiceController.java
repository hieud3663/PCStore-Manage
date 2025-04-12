package com.pcstore.controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pcstore.model.Customer;
import com.pcstore.model.Employee;
import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Product;
import com.pcstore.model.enums.InvoiceStatusEnum;
import com.pcstore.model.enums.PaymentMethodEnum;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.repository.impl.InvoiceRepository;
import com.pcstore.repository.impl.ProductRepository;
import com.pcstore.service.CustomerService;
import com.pcstore.service.InvoiceDetailService;
import com.pcstore.service.InvoiceService;
import com.pcstore.service.ProductService;

/**
 * Controller để quản lý các thao tác liên quan đến hóa đơn
 */
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final InvoiceDetailService invoiceDetailService;
    private final ProductService productService;
    private final CustomerService customerService;

    /**
     * Khởi tạo controller với connection
     * 
     * @param connection Kết nối database
     */
    public InvoiceController(Connection connection) {
        // Khởi tạo RepositoryFactory
        RepositoryFactory repositoryFactory = new RepositoryFactory(connection);
        
        // Khởi tạo các repositories
        InvoiceRepository invoiceRepository = repositoryFactory.getInvoiceRepository();
        ProductRepository productRepository = repositoryFactory.getProductRepository();
        
        // Khởi tạo các services
        this.invoiceService = new InvoiceService(invoiceRepository, productRepository);
        this.productService = new ProductService(productRepository);
        this.invoiceDetailService = new InvoiceDetailService(
            repositoryFactory.getInvoiceDetailRepository(),
            productRepository,
            invoiceRepository
        );
        this.customerService = new CustomerService(repositoryFactory.getCustomerRepository());
    }

    /**
     * Tạo hóa đơn mới
     * 
     * @param customer Khách hàng
     * @param employee Nhân viên
     * @param paymentMethod Phương thức thanh toán
     * @return Hóa đơn mới đã được tạo
     */
    public Invoice createInvoice(Customer customer, Employee employee, PaymentMethodEnum paymentMethod) {
        try {
            // Tạo hóa đơn mới với giá trị mặc định
            Invoice invoice = Invoice.createNew(customer, employee);
            invoice.setPaymentMethod(paymentMethod);
            
            return invoiceService.createInvoice(invoice);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo hóa đơn: " + e.getMessage(), e);
        }
    }
    
    /**
     * Thêm sản phẩm vào hóa đơn
     * 
     * @param invoice Hóa đơn
     * @param product Sản phẩm
     * @param quantity Số lượng
     * @param unitPrice Đơn giá (nếu null sẽ sử dụng giá mặc định của sản phẩm)
     * @return Chi tiết hóa đơn đã được thêm
     */
    public InvoiceDetail addProductToInvoice(Invoice invoice, Product product, int quantity, BigDecimal unitPrice) {
        try {
            if (invoice == null) {
                throw new IllegalArgumentException("Hóa đơn không được để trống");
            }
            
            if (product == null) {
                throw new IllegalArgumentException("Sản phẩm không được để trống");
            }
            
            if (quantity <= 0) {
                throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
            }
            
            // Kiểm tra tồn kho
            if (product.getStockQuantity() < quantity) {
                throw new IllegalArgumentException("Số lượng sản phẩm không đủ. Hiện chỉ còn " + product.getStockQuantity());
            }
            
            // Tạo chi tiết hóa đơn
            InvoiceDetail detail = new InvoiceDetail();
            detail.setInvoice(invoice);
            detail.setProduct(product);
            detail.setQuantity(quantity);
            detail.setUnitPrice(unitPrice != null ? unitPrice : product.getPrice());
            
            // Lưu chi tiết hóa đơn
            InvoiceDetail savedDetail = invoiceDetailService.addInvoiceDetail(detail);
            
            // Cập nhật tổng tiền hóa đơn
            invoice = getInvoiceById(invoice.getInvoiceId()).orElse(invoice);
            
            return savedDetail;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm sản phẩm vào hóa đơn: " + e.getMessage(), e);
        }
    }
    
    /**
     * Cập nhật số lượng sản phẩm trong hóa đơn
     * 
     * @param invoiceDetailId ID chi tiết hóa đơn
     * @param newQuantity Số lượng mới
     * @return Chi tiết hóa đơn đã được cập nhật
     */
    public InvoiceDetail updateProductQuantity(Integer invoiceDetailId, int newQuantity) {
        try {
            if (newQuantity <= 0) {
                throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
            }
            
            Optional<InvoiceDetail> detailOpt = invoiceDetailService.findInvoiceDetailById(invoiceDetailId);
            if (!detailOpt.isPresent()) {
                throw new IllegalArgumentException("Chi tiết hóa đơn không tồn tại");
            }
            
            InvoiceDetail detail = detailOpt.get();
            
            // Kiểm tra tồn kho nếu số lượng tăng
            int oldQuantity = detail.getQuantity();
            if (newQuantity > oldQuantity) {
                Product product = detail.getProduct();
                int additionalQuantity = newQuantity - oldQuantity;
                
                if (product.getStockQuantity() < additionalQuantity) {
                    throw new IllegalArgumentException("Số lượng sản phẩm không đủ. Hiện chỉ còn " + product.getStockQuantity());
                }
            }
            
            // Cập nhật số lượng
            detail.setQuantity(newQuantity);
            
            // Lưu chi tiết hóa đơn
            return invoiceDetailService.updateInvoiceDetail(detail);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật số lượng sản phẩm: " + e.getMessage(), e);
        }
    }
    
    /**
     * Xóa sản phẩm khỏi hóa đơn
     * 
     * @param invoiceDetailId ID chi tiết hóa đơn
     * @return true nếu xóa thành công
     */
    public boolean removeProductFromInvoice(Integer invoiceDetailId) {
        try {
            return invoiceDetailService.deleteInvoiceDetail(invoiceDetailId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa sản phẩm khỏi hóa đơn: " + e.getMessage(), e);
        }
    }
    
    /**
     * Hoàn thành hóa đơn (thanh toán)
     * 
     * @param invoiceId ID hóa đơn
     * @param paymentMethod Phương thức thanh toán
     * @return Hóa đơn đã được thanh toán
     */
    public Invoice completeInvoice(Integer invoiceId, PaymentMethodEnum paymentMethod) {
        try {
            Optional<Invoice> invoiceOpt = invoiceService.findInvoiceById(invoiceId);
            if (!invoiceOpt.isPresent()) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại");
            }
            
            Invoice invoice = invoiceOpt.get();
            
            // Kiểm tra hóa đơn có chi tiết không
            List<InvoiceDetail> details = invoiceDetailService.findInvoiceDetailsByInvoiceId(invoiceId);
            if (details.isEmpty()) {
                throw new IllegalStateException("Hóa đơn không có sản phẩm nào");
            }
            
            // Cập nhật trạng thái và phương thức thanh toán
            invoice.setStatus(InvoiceStatusEnum.PAID);
            invoice.setPaymentMethod(paymentMethod);
            
            // Lưu hóa đơn
            return invoiceService.updateInvoice(invoice);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi hoàn thành hóa đơn: " + e.getMessage(), e);
        }
    }
    
    /**
     * Hủy hóa đơn
     * 
     * @param invoiceId ID hóa đơn
     * @return true nếu hủy thành công
     */
    public boolean cancelInvoice(Integer invoiceId) {
        try {
            Optional<Invoice> invoiceOpt = invoiceService.findInvoiceById(invoiceId);
            if (!invoiceOpt.isPresent()) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại");
            }
            
            Invoice invoice = invoiceOpt.get();
            
            // Chỉ hủy được hóa đơn ở trạng thái chờ xử lý hoặc đang xử lý
            if (invoice.getStatus() != InvoiceStatusEnum.PENDING && 
                invoice.getStatus() != InvoiceStatusEnum.PROCESSING) {
                throw new IllegalStateException("Không thể hủy hóa đơn ở trạng thái " + invoice.getStatus());
            }
            
            // Cập nhật trạng thái
            invoice.setStatus(InvoiceStatusEnum.CANCELLED);
            
            // Lưu hóa đơn
            invoiceService.updateInvoice(invoice);
            
            // Hoàn trả số lượng sản phẩm vào tồn kho
            List<InvoiceDetail> details = invoiceDetailService.findInvoiceDetailsByInvoiceId(invoiceId);
            for (InvoiceDetail detail : details) {
                Product product = detail.getProduct();
                product.increaseStock(detail.getQuantity());
                productService.updateProduct(product);
            }
            
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi hủy hóa đơn: " + e.getMessage(), e);
        }
    }
    
    /**
     * Lấy danh sách tất cả hóa đơn
     * 
     * @return Danh sách hóa đơn
     */
    public List<Invoice> getAllInvoices() {
        try {
            return invoiceService.findAllInvoices();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách hóa đơn: " + e.getMessage(), e);
        }
    }
    
    /**
     * Lấy hóa đơn theo ID
     * 
     * @param invoiceId ID hóa đơn
     * @return Hóa đơn nếu tìm thấy
     */
    public Optional<Invoice> getInvoiceById(Integer invoiceId) {
        try {
            return invoiceService.findInvoiceById(invoiceId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy hóa đơn: " + e.getMessage(), e);
        }
    }
    
    /**
     * Lấy chi tiết hóa đơn
     * 
     * @param invoiceId ID hóa đơn
     * @return Danh sách chi tiết hóa đơn
     */
    public List<InvoiceDetail> getInvoiceDetails(Integer invoiceId) {
        try {
            return invoiceDetailService.findInvoiceDetailsByInvoiceId(invoiceId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy chi tiết hóa đơn: " + e.getMessage(), e);
        }
    }
    
    /**
     * Lấy chi tiết hóa đơn theo ID chi tiết hóa đơn
     * 
     * @param invoiceDetailId ID chi tiết hóa đơn
     * @return Chi tiết hóa đơn nếu tìm thấy
     */
    public Optional<InvoiceDetail> getInvoiceDetailById(Integer invoiceDetailId) {
        try {
            return invoiceDetailService.findInvoiceDetailById(invoiceDetailId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy chi tiết hóa đơn: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tìm hóa đơn theo khách hàng
     * 
     * @param customerId ID khách hàng
     * @return Danh sách hóa đơn của khách hàng
     */
    public List<Invoice> getInvoicesByCustomer(String customerId) {
        try {
            return invoiceService.findInvoicesByCustomer(customerId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm hóa đơn theo khách hàng: " + e.getMessage(), e);
        }
    }
    
    /**
     * Lấy danh sách hóa đơn theo số điện thoại khách hàng
     * 
     * @param phoneNumber Số điện thoại khách hàng
     * @return Danh sách hóa đơn thuộc khách hàng
     */
    public List<Invoice> getInvoicesByCustomerPhone(String phoneNumber) {
        try {
            // Tìm khách hàng theo số điện thoại
            Optional<Customer> customerOpt = customerService.findCustomerByPhone(phoneNumber);
            
            if (customerOpt.isPresent()) {
                // Nếu tìm thấy khách hàng, lấy danh sách hóa đơn của họ
                return invoiceService.findInvoicesByCustomer(customerOpt.get().getCustomerId());
            } else {
                // Không tìm thấy khách hàng
                return new ArrayList<>();
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy hóa đơn theo số điện thoại: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tìm hóa đơn theo nhân viên
     * 
     * @param employeeId ID nhân viên
     * @return Danh sách hóa đơn được tạo bởi nhân viên
     */
    public List<Invoice> getInvoicesByEmployee(String employeeId) {
        try {
            return invoiceService.findInvoicesByEmployee(employeeId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm hóa đơn theo nhân viên: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tìm hóa đơn theo trạng thái
     * 
     * @param status Trạng thái hóa đơn
     * @return Danh sách hóa đơn có trạng thái tương ứng
     */
    // public List<Invoice> getInvoicesByStatus(InvoiceStatusEnum status) {
    //     try {
    //         return invoiceService.findInvoicesByStatus(status);
    //     } catch (Exception e) {
    //         throw new RuntimeException("Lỗi khi tìm hóa đơn theo trạng thái: " + e.getMessage(), e);
    //     }
    // }
    
    /**
     * Tìm hóa đơn trong khoảng thời gian
     * 
     * @param startDate Thời gian bắt đầu
     * @param endDate Thời gian kết thúc
     * @return Danh sách hóa đơn trong khoảng thời gian
     */
    public List<Invoice> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return invoiceService.findInvoicesByDateRange(startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm hóa đơn theo khoảng thời gian: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tính tổng doanh thu trong khoảng thời gian
     * 
     * @param startDate Thời gian bắt đầu
     * @param endDate Thời gian kết thúc
     * @return Tổng doanh thu
     */
    public BigDecimal calculateRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return invoiceService.calculateRevenue(startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tính doanh thu: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tìm kiếm hóa đơn theo từ khóa
     * 
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách hóa đơn phù hợp
     */
    public List<Invoice> searchInvoices(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return getAllInvoices();
            }
            
            List<Invoice> result = new ArrayList<>();
            List<Invoice> allInvoices = getAllInvoices();
            
            keyword = keyword.toLowerCase().trim();
            
            for (Invoice invoice : allInvoices) {
                // Tìm theo ID hóa đơn
                if (String.valueOf(invoice.getInvoiceId()).contains(keyword)) {
                    result.add(invoice);
                    continue;
                }
                
                // Tìm theo khách hàng
                if (invoice.getCustomer() != null) {
                    Customer customer = invoice.getCustomer();
                    
                    if (customer.getFullName() != null && 
                        customer.getFullName().toLowerCase().contains(keyword)) {
                        result.add(invoice);
                        continue;
                    }
                    
                    if (customer.getPhoneNumber() != null && 
                        customer.getPhoneNumber().contains(keyword)) {
                        result.add(invoice);
                        continue;
                    }
                }
                
                // Tìm theo nhân viên
                if (invoice.getEmployee() != null) {
                    Employee employee = invoice.getEmployee();
                    
                    if (employee.getFullName() != null && 
                        employee.getFullName().toLowerCase().contains(keyword)) {
                        result.add(invoice);
                        continue;
                    }
                }
                
                // Tìm theo tổng tiền
                if (String.valueOf(invoice.getTotalAmount()).contains(keyword)) {
                    result.add(invoice);
                    continue;
                }
                
                // Tìm theo trạng thái
                if (invoice.getStatus() != null && 
                    invoice.getStatus().toString().toLowerCase().contains(keyword)) {
                    result.add(invoice);
                    continue;
                }
                
                // Tìm theo phương thức thanh toán
                if (invoice.getPaymentMethod() != null && 
                    invoice.getPaymentMethod().toString().toLowerCase().contains(keyword)) {
                    result.add(invoice);
                    continue;
                }
                
                // Tìm theo sản phẩm trong chi tiết hóa đơn
                List<InvoiceDetail> details = invoiceDetailService.findInvoiceDetailsByInvoiceId(invoice.getInvoiceId());
                for (InvoiceDetail detail : details) {
                    Product product = detail.getProduct();
                    
                    if (product != null) {
                        if (product.getProductId() != null && 
                            product.getProductId().toLowerCase().contains(keyword)) {
                            result.add(invoice);
                            break;
                        }
                        
                        if (product.getProductName() != null && 
                            product.getProductName().toLowerCase().contains(keyword)) {
                            result.add(invoice);
                            break;
                        }
                    }
                }
            }
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm kiếm hóa đơn: " + e.getMessage(), e);
        }
    }
    
    /**
     * Xóa hóa đơn
     * 
     * @param invoiceId ID hóa đơn
     * @return true nếu xóa thành công
     */
    public boolean deleteInvoice(Integer invoiceId) {
        try {
            // Kiểm tra hóa đơn có tồn tại không
            Optional<Invoice> invoiceOpt = invoiceService.findInvoiceById(invoiceId);
            if (!invoiceOpt.isPresent()) {
                throw new IllegalArgumentException("Hóa đơn không tồn tại");
            }
            
            Invoice invoice = invoiceOpt.get();
            
            // Chỉ cho phép xóa hóa đơn ở trạng thái PENDING, PROCESSING hoặc CANCELLED
            if (invoice.getStatus() != InvoiceStatusEnum.PENDING && 
                invoice.getStatus() != InvoiceStatusEnum.PROCESSING && 
                invoice.getStatus() != InvoiceStatusEnum.CANCELLED) {
                throw new IllegalStateException("Không thể xóa hóa đơn đã thanh toán hoặc đã giao");
            }
            
            // Hoàn trả số lượng sản phẩm vào tồn kho
            List<InvoiceDetail> details = invoiceDetailService.findInvoiceDetailsByInvoiceId(invoiceId);
            for (InvoiceDetail detail : details) {
                Product product = detail.getProduct();
                product.increaseStock(detail.getQuantity());
                productService.updateProduct(product);
            }
            
            // Xóa hóa đơn
            return invoiceService.deleteInvoice(invoiceId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa hóa đơn: " + e.getMessage(), e);
        }
    }
}