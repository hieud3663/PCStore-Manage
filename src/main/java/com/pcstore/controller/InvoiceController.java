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

import com.pcstore.model.Customer;
import com.pcstore.model.Employee;
import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Product;
import com.pcstore.model.enums.InvoiceStatusEnum;
import com.pcstore.model.enums.PaymentMethodEnum;
import com.pcstore.service.InvoiceService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.ExportInvoice;
import com.pcstore.utils.LocaleManager;
import com.pcstore.view.DashboardForm;
import com.pcstore.view.InvoiceForm;
import com.pcstore.view.PayForm;

import javax.swing.*;
import javax.swing.table.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;
import java.awt.event.*;
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


//==================================Của Hiếu ====================================

/**
 * Controller quản lý các chức năng liên quan đến hóa đơn
 */

    // Singleton instance
    private static InvoiceController instance;
    
    private InvoiceForm invoiceForm;
    private InvoiceDetailController invoiceDetailController;
    
    private List<Invoice> invoiceList;
    private Invoice currentInvoice;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final NumberFormat currencyFormatter = LocaleManager.getInstance().getNumberFormatter();
    private TableRowSorter<TableModel> tableSorter;
    
    /**
     * Lấy instance duy nhất của controller (Singleton pattern)
     * @param invoiceForm Form hiển thị hóa đơn
     * @return InvoiceController instance
     */
    public static synchronized InvoiceController getInstance(InvoiceForm invoiceForm) {
        if (instance == null) {
            instance = new InvoiceController(invoiceForm);
        } else if (instance.invoiceForm != invoiceForm) {
            // Cập nhật form nếu khác với instance hiện tại
            instance.invoiceForm = invoiceForm;
            instance.setupEventListeners();
            instance.setupTableSorter();
            instance.loadAllInvoices();
        }
        return instance;
    }
    
    /**
     * Khởi tạo controller với form
     * @param invoiceForm Form hiển thị hóa đơn
     */
    private InvoiceController(InvoiceForm invoiceForm) {
        try {
            this.invoiceForm = invoiceForm;
            this.invoiceService = ServiceFactory.getInvoiceService();
            this.invoiceDetailController = new InvoiceDetailController();
            
            // Khởi tạo danh sách hóa đơn
            loadAllInvoices();
            
            // Thiết lập các sự kiện cho form
            setupEventListeners();
            
            // Thiết lập bảng có thể sắp xếp
            setupTableSorter();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khởi tạo controller: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Thiết lập các sự kiện cho form
     */
    private void setupEventListeners() {
        // Thiết lập sự kiện khi chọn hóa đơn
        invoiceForm.getTableInvoice().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = invoiceForm.getTableInvoice().getSelectedRow();
                if (selectedRow >= 0) {
                    // Chuyển đổi chỉ số hàng từ view sang model nếu đang sắp xếp
                    int modelRow = invoiceForm.getTableInvoice().convertRowIndexToModel(selectedRow);
                    int invoiceId = Integer.parseInt(invoiceForm.getTableInvoice().getModel().getValueAt(modelRow, 2).toString());
                    loadInvoiceDetails(invoiceId);
                }
            }
        });
        
        // Thiết lập sự kiện xuất Excel
        invoiceForm.getBtnExportExcel().addActionListener(e -> {
            // exportAllInvoicesToExcel();
        });
        
        // Thiết lập sự kiện xuất hóa đơn (PDF)
        invoiceForm.getBtnExportInvoice().addActionListener(e -> {
            printSelectedInvoice();
        });
        
        // Thiết lập sự kiện xóa hóa đơn
        invoiceForm.getBtnDeleteInvoice().addActionListener(e -> {
            deleteSelectedInvoice();
        });
        
        // Thiết lập sự kiện tìm kiếm
        invoiceForm.getTxtSearchField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchInvoices(invoiceForm.getTxtSearchField().getText());
            }
        });

        invoiceForm.getBbtnSearch().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                searchInvoices(invoiceForm.getTxtSearchField().getText());
            }
        });

        invoiceForm.getBtnPaymentInvoice().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                paymentInvoice();
            }
        });
    }
    
    /**
     * Thiết lập bảng có thể sắp xếp
     */
    private void setupTableSorter() {
        // Tạo row sorter cho bảng hóa đơn
        tableSorter = new TableRowSorter<>(invoiceForm.getTableInvoice().getModel());
        invoiceForm.getTableInvoice().setRowSorter(tableSorter);
        
        // Thiết lập một số cột không thể sắp xếp (như cột checkbox)
        tableSorter.setSortable(0, false); // Cột checkbox không sắp xếp được
        
        // comparator (cột 5 - Tổng tiền)
        tableSorter.setComparator(5, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                try {
                    String v1 = s1.replaceAll("\\.", "");
                    String v2 = s2.replaceAll("\\.", "");
                    
                    double d1 = Double.parseDouble(v1);
                    double d2 = Double.parseDouble(v2);
                    
                    return Double.compare(d1, d2);
                } catch (Exception e) {
                    return s1.compareTo(s2);
                }
            }
        });
        
        //Comparator cột ngày (cột 3)
        tableSorter.setComparator(3, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                try {
                    LocalDateTime d1 = LocalDateTime.parse(s1, dateFormatter);
                    LocalDateTime d2 = LocalDateTime.parse(s2, dateFormatter);
                    return d1.compareTo(d2);
                } catch (Exception e) {
                    return s1.compareTo(s2);
                }
            }
        });
    }
    
    
    public void loadAllInvoices() {
        try {
            invoiceList = invoiceService.findAllInvoices();
            updateInvoiceTable(invoiceList);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi tải danh sách hóa đơn: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Cập nhật bảng hiển thị hóa đơn
     * @param invoices Danh sách hóa đơn cần hiển thị
     */
    private void updateInvoiceTable(List<Invoice> invoices) {
        DefaultTableModel model = (DefaultTableModel) invoiceForm.getTableInvoice().getModel();
        model.setRowCount(0);
        
        int count = 0;
        for (Invoice invoice : invoices) {
            count++;
            Object[] row = new Object[11]; 
            row[0] = false; 
            row[1] = count;
            row[2] = invoice.getInvoiceId();
            row[3] = invoice.getInvoiceDate() != null ? invoice.getInvoiceDate().format(dateFormatter) : "";
            row[4] = invoice.getEmployee() != null ? invoice.getEmployee().getFullName() : "";
            row[5] = invoice.getDiscountAmount() != null ? currencyFormatter.format(invoice.getDiscountAmount()) : "";
            row[6] = invoice.getTotalAmount() != null ? currencyFormatter.format(invoice.getTotalAmount()) : "";
            row[7] = getPaymentMethodDisplay(invoice.getPaymentMethod());
            row[8] = invoice.getCustomer() != null ? invoice.getCustomer().getFullName() : "";
            row[9] = getStatusDisplay(invoice.getStatus());
            row[10] = ""; // Ghi chú
            
            model.addRow(row);
        }
    }
    
    /**
     * Tải chi tiết hóa đơn theo ID
     * @param invoiceId ID của hóa đơn cần tải chi tiết
     */
    public void loadInvoiceDetails(int invoiceId) {
        try {
            Optional<Invoice> invoiceOpt = invoiceService.findInvoiceById(invoiceId);
            if (invoiceOpt.isPresent()) {
                currentInvoice = invoiceOpt.get();
                invoiceDetailController.updateInvoiceDetailTable(currentInvoice.getInvoiceDetails(), 
                        invoiceForm.getTableInvoiceDetail());
            } else {
                // Nếu không tìm thấy hóa đơn, xóa bảng chi tiết
                DefaultTableModel model = (DefaultTableModel) invoiceForm.getTableInvoiceDetail().getModel();
                model.setRowCount(0);
                currentInvoice = null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi tải chi tiết hóa đơn: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Tìm kiếm hóa đơn theo từ khóa
     * @param keyword Từ khóa tìm kiếm
     */
    public void searchInvoices2(String keyword) {
        if (tableSorter == null) {
            return;
        }
        
        if (keyword == null || keyword.trim().isEmpty()) {
            tableSorter.setRowFilter(null); // Hiển thị tất cả nếu không có từ khóa
        } else {
            try {
                // Tìm kiếm trên nhiều cột (ID, nhân viên, khách hàng, trạng thái)
                RowFilter<Object, Object> filter = RowFilter.orFilter(Arrays.asList(
                    RowFilter.regexFilter("(?i)" + Pattern.quote(keyword), 2),  // ID hóa đơn
                    RowFilter.regexFilter("(?i)" + Pattern.quote(keyword), 4),  // Nhân viên 
                    RowFilter.regexFilter("(?i)" + Pattern.quote(keyword), 7),  // Khách hàng
                    RowFilter.regexFilter("(?i)" + Pattern.quote(keyword), 8)   // Trạng thái
                ));
                tableSorter.setRowFilter(filter);
            } catch (Exception e) {
                tableSorter.setRowFilter(null);
                JOptionPane.showMessageDialog(null, "Lỗi khi tìm kiếm: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Xuất danh sách hóa đơn ra file Excel
     */
    // public void exportAllInvoicesToExcel() {
    //     try {
    //         boolean success = ExportBill.exportAllInvoicesToExcel(invoiceList);
    //         if (success) {
    //             JOptionPane.showMessageDialog(null, "Xuất Excel thành công!", 
    //                     "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    //         } else {
    //             JOptionPane.showMessageDialog(null, "Xuất Excel thất bại!", 
    //                     "Lỗi", JOptionPane.ERROR_MESSAGE);
    //         }
    //     } catch (Exception e) {
    //         JOptionPane.showMessageDialog(null, "Lỗi khi xuất Excel: " + e.getMessage(), 
    //                 "Lỗi", JOptionPane.ERROR_MESSAGE);
    //     }
    // }
    
    /**
     * In hóa đơn được chọn
     */
    public void printSelectedInvoice() {
        int selectedRow = invoiceForm.getTableInvoice().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn hóa đơn cần in!", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            // Chuyển đổi chỉ số hàng từ view sang model nếu đang sắp xếp
            int modelRow = invoiceForm.getTableInvoice().convertRowIndexToModel(selectedRow);
            int invoiceId = Integer.parseInt(invoiceForm.getTableInvoice().getModel().getValueAt(modelRow, 2).toString());
            
            Optional<Invoice> invoiceOpt = invoiceService.findInvoiceById(invoiceId);
            if (invoiceOpt.isPresent()) {
                Invoice invoice = invoiceOpt.get();
                
                if (invoice.getStatus() != InvoiceStatusEnum.COMPLETED) {
                    JOptionPane.showMessageDialog(null, "Hóa đơn chưa hoàn thành thanh toán!", 
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                
                boolean success = ExportInvoice.exportPDF(invoice, invoice.getPaymentMethod()); // Không cần payment object vì chỉ in lại hóa đơn
                
                if (success) {
                    JOptionPane.showMessageDialog(null, "In hóa đơn thành công!", 
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "In hóa đơn thất bại!", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi in hóa đơn: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    /**
     * Xử lý thanh toán hóa đơn cho những hóa đơn Đang chờ xử lý
     */
    public void paymentInvoice() {
        int selectedRow = invoiceForm.getTableInvoice().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn hóa đơn cần thanh toán!", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Chuyển đổi chỉ số hàng từ view sang model nếu đang sắp xếp
        int modelRow = invoiceForm.getTableInvoice().convertRowIndexToModel(selectedRow);
        int invoiceId = Integer.parseInt(invoiceForm.getTableInvoice().getModel().getValueAt(modelRow, 2).toString());
        
        Optional<Invoice> invoiceOpt = invoiceService.findInvoiceById(invoiceId);
        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            if (invoice.getStatus() == InvoiceStatusEnum.COMPLETED || 
                    invoice.getStatus() == InvoiceStatusEnum.PAID) {
                JOptionPane.showMessageDialog(null, "Hóa đơn đã được thanh toán!", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // form thanh toán hóa đơn
            DashboardForm dashboardForm = DashboardForm.getInstance();
            PayForm paymentForm = new PayForm(dashboardForm, true);
            PaymentController paymentController = new PaymentController(paymentForm, invoice);
            paymentController.showPaymentForm();

            if (paymentController.isPaymentSuccessful()) {
                invoice.setStatus(InvoiceStatusEnum.COMPLETED);
                invoice.setPaymentMethod(paymentController.getCurrentPayment().getPaymentMethod());

                //Cập nhật chi tiết hóa đơn
                invoiceService.updateInvoice(invoice);
                
                loadAllInvoices();
                JOptionPane.showMessageDialog(null, "Thanh toán hóa đơn thành công!", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
                boolean success = false;
                
                try {
                    success = ExportInvoice.exportPDF(invoice, paymentController.getCurrentPayment());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Lỗi khi in hóa đơn: " + e.getMessage(), 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
                        
                if (success) {
                    JOptionPane.showMessageDialog(null, "In hóa đơn thành công!", 
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "In hóa đơn thất bại!", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Không tìm thấy hóa đơn!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xóa các hóa đơn được tích chọn
     */
    public void deleteSelectedInvoice() {
        DefaultTableModel model = (DefaultTableModel) invoiceForm.getTableInvoice().getModel();
        int rowCount = model.getRowCount();
        List<Integer> invoiceIdsToDelete = new ArrayList<>();
        
        // Thu thập tất cả ID hóa đơn được tích chọn
        for (int i = 0; i < rowCount; i++) {
            Boolean isChecked = (Boolean) model.getValueAt(i, 0);
            if (isChecked) {
                int modelRow = invoiceForm.getTableInvoice().convertRowIndexToModel(i);
                int invoiceId = Integer.parseInt(model.getValueAt(modelRow, 2).toString());
                invoiceIdsToDelete.add(invoiceId);
            }
        }
        
        // Nếu không có hóa đơn nào được tích chọn
        if (invoiceIdsToDelete.isEmpty()) {
            JOptionPane.showMessageDialog(null, 
                    "Vui lòng tích chọn ít nhất một hóa đơn để xóa!", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Hiển thị hộp thoại xác nhận
        String message = "Bạn có chắc chắn muốn xóa " + invoiceIdsToDelete.size() + 
                " hóa đơn đã chọn?";
        int option = JOptionPane.showConfirmDialog(null, message, 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            int successCount = 0;
            int failCount = 0;
            List<String> errorMessages = new ArrayList<>();
            
            for (Integer invoiceId : invoiceIdsToDelete) {
                try {
                    // Kiểm tra trạng thái của hóa đơn trước khi xóa
                    Optional<Invoice> invoiceOpt = invoiceService.findInvoiceById(invoiceId);
                    if (invoiceOpt.isPresent()) {
                        Invoice invoice = invoiceOpt.get();
                        if (invoice.getStatus() == InvoiceStatusEnum.PAID || 
                                invoice.getStatus() == InvoiceStatusEnum.DELIVERED) {
                            errorMessages.add("Hóa đơn #" + invoiceId + ": Không thể xóa hóa đơn đã thanh toán hoặc đã giao hàng!");
                            failCount++;
                            continue;
                        }
                    }
                    
                    boolean success = invoiceService.deleteInvoice(invoiceId);
                    if (success) {
                        successCount++;
                    } else {
                        failCount++;
                        errorMessages.add("Hóa đơn #" + invoiceId + ": Xóa thất bại!");
                    }
                } catch (Exception e) {
                    failCount++;
                    errorMessages.add("Hóa đơn #" + invoiceId + ": " + e.getMessage());
                }
            }
            
            // Hiển thị kết quả
            StringBuilder resultMessage = new StringBuilder();
            if (successCount > 0) {
                resultMessage.append("Đã xóa thành công ").append(successCount).append(" hóa đơn.\n");
            }
            
            if (failCount > 0) {
                resultMessage.append("Không thể xóa ").append(failCount).append(" hóa đơn.\n\n");
                resultMessage.append("Chi tiết lỗi:\n");
                
                for (String error : errorMessages) {
                    resultMessage.append("- ").append(error).append("\n");
                }
            }
            
            JOptionPane.showMessageDialog(null, resultMessage.toString(), 
                    (failCount > 0) ? "Kết quả xóa hóa đơn" : "Thành công", 
                    (failCount > 0) ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
            
            // Cập nhật lại danh sách hóa đơn
            loadAllInvoices();
        }
    }
    
    /**
     * Tạo hóa đơn mới
     * @param customer Khách hàng
     * @param employee Nhân viên
     * @return Hóa đơn mới đã được tạo
     */
    public Invoice createInvoice(Customer customer, Employee employee) {
        if (customer == null) {
            throw new IllegalArgumentException(ErrorMessage.INVOICE_CUSTOMER_NULL);
        }
        if (employee == null) {
            throw new IllegalArgumentException(ErrorMessage.INVOICE_EMPLOYEE_NULL);
        }
        
        try {
            Invoice invoice = new Invoice();
            invoice.setCustomer(customer);
            invoice.setEmployee(employee);
            invoice.setInvoiceDate(LocalDateTime.now());
            invoice.setStatus(InvoiceStatusEnum.PENDING);
            invoice.setTotalAmount(BigDecimal.ZERO);
            
            // Lưu hóa đơn vào cơ sở dữ liệu
            Invoice savedInvoice = invoiceService.createInvoice(invoice);
            
            // Cập nhật lại danh sách hóa đơn
            loadAllInvoices();
            
            return savedInvoice;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi tạo hóa đơn mới: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Cập nhật hóa đơn
     * @param invoice Hóa đơn cần cập nhật
     * @return Hóa đơn đã được cập nhật
     */
    public Invoice updateInvoice(Invoice invoice) {
        try {
            Invoice updatedInvoice = invoiceService.updateInvoice(invoice);
            
            // Cập nhật lại danh sách hóa đơn
            loadAllInvoices();
            
            return updatedInvoice;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật hóa đơn: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Xử lý thanh toán hóa đơn
     * @param invoice Hóa đơn cần thanh toán
     * @param paymentMethod Phương thức thanh toán
     * @return true nếu thanh toán thành công, ngược lại là false
     */
    public boolean processPayment(Invoice invoice, PaymentMethodEnum paymentMethod) {
        try {
            // Cập nhật thông tin thanh toán
            invoice.setStatus(InvoiceStatusEnum.PAID);
            invoice.setPaymentMethod(paymentMethod);
            invoice.setUpdatedAt(LocalDateTime.now());
            
            // Cập nhật hóa đơn
            Invoice updatedInvoice = invoiceService.updateInvoice(invoice);
            
            // Cập nhật lại danh sách hóa đơn
            loadAllInvoices();
            
            return updatedInvoice != null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi xử lý thanh toán: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Cập nhật trạng thái hóa đơn
     * @param invoiceId ID của hóa đơn
     * @param status Trạng thái mới
     * @return true nếu cập nhật thành công, ngược lại là false
     */
    public boolean updateInvoiceStatus(int invoiceId, InvoiceStatusEnum status) {
        try {
            Optional<Invoice> invoiceOpt = invoiceService.findInvoiceById(invoiceId);
            if (invoiceOpt.isPresent()) {
                Invoice invoice = invoiceOpt.get();
                invoice.setStatus(status);
                invoice.setUpdatedAt(LocalDateTime.now());
                
                // Cập nhật hóa đơn
                Invoice updatedInvoice = invoiceService.updateInvoice(invoice);
                
                // Cập nhật lại danh sách hóa đơn
                loadAllInvoices();
                
                return updatedInvoice != null;
            }
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật trạng thái hóa đơn: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Lấy chuỗi hiển thị cho phương thức thanh toán
     * @param paymentMethod Phương thức thanh toán
     * @return Chuỗi hiển thị
     */
    private String getPaymentMethodDisplay(PaymentMethodEnum paymentMethod) {
        if (paymentMethod == null) {
            return "Chưa thanh toán";
        }
        
        switch (paymentMethod) {
            case CASH: return "Tiền mặt";
            case CREDIT_CARD: return "Thẻ tín dụng";
            case BANK_TRANSFER: return "Chuyển khoản";
            case E_WALLET: return "Ví điện tử";
            case MOMO: return "MoMo";
            case ZALOPAY: return "ZaloPay";
            default: return "Khác";
        }
    }
    
    /**
     * Lấy chuỗi hiển thị cho trạng thái hóa đơn
     * @param status Trạng thái hóa đơn
     * @return Chuỗi hiển thị
     */
    private String getStatusDisplay(InvoiceStatusEnum status) {
        if (status == null) {
            return "Chưa xác định";
        }
        
        switch (status) {
            case PENDING: return "Đang xử lý";
            case PAID: return "Đã thanh toán";
            case CANCELLED: return "Đã hủy";
            case DELIVERED: return "Đã giao hàng";
            case COMPLETED: return "Hoàn thành";
            case PROCESSING: return "Đang xử lý";
            default: return "Khác";
        }
    }
    
    /**
     * Lấy InvoiceDetailController
     * @return InvoiceDetailController
     */
    public InvoiceDetailController getInvoiceDetailController() {
        return invoiceDetailController;
    }
    
    /**
     * Lấy danh sách hóa đơn hiện tại
     * @return Danh sách hóa đơn
     */
    public List<Invoice> getInvoiceList() {
        return invoiceList;
    }
    
    /**
     * Lấy hóa đơn hiện tại đang được xem chi tiết
     * @return Hóa đơn hiện tại
     */
    public Invoice getCurrentInvoice() {
        return currentInvoice;
    }
}
