package com.pcstore.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Return;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.repository.impl.InvoiceRepository;
import com.pcstore.repository.impl.ProductRepository;
import com.pcstore.repository.impl.ReturnRepository;
import com.pcstore.utils.DatabaseConnection;

/**
 * Service xử lý logic nghiệp vụ liên quan đến hóa đơn
 */
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    private InvoiceDetailService invoiceDetailService;

    public InvoiceService() {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        this.invoiceRepository = RepositoryFactory.getInstance(connection).getInvoiceRepository();
        this.productRepository = RepositoryFactory.getInstance(connection).getProductRepository();
    }

    /**
     * Repository cho hóa đơn
     * @param connection Kết nối đến cơ sở dữ liệu
     * @param invoiceRepository Repository hóa đơn
     */
    public InvoiceService(Connection connection) {

        this.invoiceRepository = RepositoryFactory.getInstance(connection).getInvoiceRepository();
        this.productRepository = RepositoryFactory.getInstance(connection).getProductRepository(); 
        this.invoiceDetailService = new InvoiceDetailService(connection);
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
        Optional<Invoice> oldInvoiceOpt = this.findInvoiceById(invoice.getInvoiceId());
        if (oldInvoiceOpt.isPresent()) {
            Invoice oldInvoice = oldInvoiceOpt.get();
            
            //Lấy thông tin chi tiết hóa đơn cũ
            List<InvoiceDetail> oldDetails = oldInvoice.getInvoiceDetails();
            oldDetails.forEach(detail -> {
                invoiceDetailService.updateInvoiceDetail(detail);
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
        Optional<Invoice> invoiceOpt =  invoiceRepository.findById(invoiceId);

        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            invoice.setInvoiceDetails(invoiceDetailService.findInvoiceDetailsByInvoiceId(invoiceId));
            return Optional.of(invoice);
        }
        
        return invoiceOpt;
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
    public List<Invoice> findInvoicesByCustomerID(String customerId) {
        try {
            List<Invoice> invoices = invoiceRepository.findByCustomerId(customerId);
            if (invoices != null) {
                for (Invoice invoice : invoices) {
                    invoice.setInvoiceDetails(invoiceDetailService.findInvoiceDetailsByInvoiceId(invoice.getInvoiceId()));
                }
            }
            return invoices;
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm hóa đơn theo khách hàng: " + e.getMessage());
            return new ArrayList<>();
        }

    }

    /**
     * Tìm hóa đơn theo khách hàng - dành riêng cho chức năng bảo hành
     * @param customerId ID khách hàng
     * @return Danh sách hóa đơn
     */
    public List<Invoice> findInvoicesByCustomerForWarranty(String customerId) {
        return invoiceRepository.findByCustomerIdWarranty(customerId);
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

    /**
     * Đếm số lượng hóa đơn trong khoảng thời gian
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Số lượng hóa đơn
     */
    public int countInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // Gọi phương thức mới trong repository
            return invoiceRepository.countInvoicesByDateRange(startDate, endDate);
        } catch (Exception e) {
            System.err.println("Lỗi khi đếm số lượng hóa đơn trong khoảng thời gian: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Tìm tất cả hóa đơn có thể trả hàng
     * @return Danh sách hóa đơn có thể trả hàng
     */
    public List<Invoice> findAllInvoicesForReturn() {
        try {
            List<Invoice> invoices = invoiceRepository.findAllInvoicesForReturn();
            
            if (invoices == null) {
                return new ArrayList<>();
            }
            
            // Tải chi tiết hóa đơn và lọc sản phẩm có thể trả
            for (Invoice invoice : invoices) {
                try {
                    List<InvoiceDetail> allDetails = invoiceDetailService.findInvoiceDetailsByInvoiceId(invoice.getInvoiceId());
                    List<InvoiceDetail> returnableDetails = new ArrayList<>();
                    
                    // Chỉ giữ lại những chi tiết hóa đơn có sản phẩm có thể trả hàng
                    if (allDetails != null) {
                        for (InvoiceDetail detail : allDetails) {
                            // Kiểm tra sản phẩm đã trả hết chưa
                            boolean canReturn = isDetailReturnable(detail);
                            if (canReturn) {
                                returnableDetails.add(detail);
                            }
                        }
                    }
                    
                    invoice.setInvoiceDetails(returnableDetails);
                } catch (Exception e) {
                    System.err.println("Lỗi khi tải chi tiết cho hóa đơn " + invoice.getInvoiceId() + ": " + e.getMessage());
                    invoice.setInvoiceDetails(new ArrayList<>());
                }
            }
            
            // Chỉ giữ lại hóa đơn còn sản phẩm có thể trả
            return invoices.stream()
                    .filter(invoice -> invoice.getInvoiceDetails() != null && !invoice.getInvoiceDetails().isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm hóa đơn có thể trả hàng: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Kiểm tra xem một chi tiết hóa đơn có thể trả hàng không
     * @param detail Chi tiết hóa đơn cần kiểm tra
     * @return true nếu có thể trả hàng, false nếu không
     */
    private boolean isDetailReturnable(InvoiceDetail detail) {
        if (detail == null || detail.getProduct() == null) {
            return false;
        }
        
        try {
            // Khởi tạo ReturnRepository để lấy số lượng đã trả
            ReturnRepository returnRepo = new ReturnRepository(
                ServiceFactory.getInstance().getConnection()
            );
            
            // Lấy số lượng đã trả từ repository
            int returnedQuantity = returnRepo.getReturnedQuantityForDetail(detail.getInvoiceDetailId());
            
            // Nếu số lượng còn lại lớn hơn 0, có thể trả hàng
            return (detail.getQuantity() - returnedQuantity) > 0;
        } catch (Exception e) {
            System.err.println("Lỗi khi kiểm tra khả năng trả hàng: " + e.getMessage());
            // Mặc định là có thể trả nếu có lỗi
            return true;
        }
    }

    /**
     * Tìm tất cả hóa đơn có thể áp dụng cho bảo hành
     * @return Danh sách hóa đơn với chi tiết sản phẩm có bảo hành
     */
    public List<Invoice> findAllInvoicesForWarranty() {
        try {
            return invoiceRepository.findAllInvoicesForWarranty();
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm hóa đơn cho bảo hành: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tìm chi tiết hóa đơn dành cho bảo hành
     * @param invoiceId ID hóa đơn
     * @return Danh sách chi tiết hóa đơn có sản phẩm bảo hành
     */
    public List<InvoiceDetail> findInvoiceDetailsForWarranty(Integer invoiceId) {
        try {
            return invoiceRepository.findInvoiceDetailsForWarranty(invoiceId);
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm chi tiết hóa đơn cho bảo hành: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tìm hóa đơn đơn giản (chỉ thông tin cơ bản) theo ID
     * @param invoiceId ID hóa đơn
     * @return Optional chứa hóa đơn với thông tin cơ bản nếu tìm thấy
     */
    public Optional<Invoice> findSimpleInvoiceById(Integer invoiceId) {
        try {
            return invoiceRepository.findSimpleInvoiceById(invoiceId);
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm hóa đơn đơn giản: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Tìm tất cả hóa đơn với thông tin đơn giản
     * @return Danh sách hóa đơn với thông tin đơn giản
     */
    public List<Invoice> findAllInvoicesSimple() {
        try {
            return invoiceRepository.findAllInvoicesSimple();
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm hóa đơn đơn giản: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /*
     * Báo cáo hóa đơn doanh thu
     */
    public List<Invoice> getInvoiceStatistics(LocalDateTime startDate, LocalDateTime endDate){
        try {
            return invoiceRepository.getInvoiceStatistics(startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}