package com.pcstore.controller;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.pcstore.model.Customer;
import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Product;
import com.pcstore.model.Warranty;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.repository.impl.InvoiceDetailRepository;
import com.pcstore.repository.impl.ProductRepository;
import com.pcstore.repository.impl.WarrantyRepository;
import com.pcstore.service.CustomerService;
import com.pcstore.service.InvoiceDetailService;
import com.pcstore.service.InvoiceService;
import com.pcstore.service.ProductService;
import com.pcstore.service.WarrantyService;
import com.pcstore.view.AddWarrantyForm;
import com.pcstore.view.WarrantyCardForm;
import com.pcstore.view.WarrantyServiceForm;

/**
 * Controller xử lý các thao tác bảo hành
 */
public class WarrantyController {
    private final WarrantyService warrantyService;
    private final InvoiceDetailService invoiceDetailService;
    private final ProductService productService;
    private final CustomerService customerService;
    private final InvoiceService invoiceService;
    private WarrantyServiceForm serviceForm;
    private WarrantyCardForm cardForm;
    
    /**
     * Khởi tạo controller với kết nối CSDL được cung cấp
     * @param connection Kết nối CSDL
     */
    public WarrantyController(Connection connection) {

        RepositoryFactory repositoryFactory = RepositoryFactory.getInstance(connection);
        
        WarrantyRepository warrantyRepository = repositoryFactory.getWarrantyRepository();
        InvoiceDetailRepository invoiceDetailRepository = repositoryFactory.getInvoiceDetailRepository();
        ProductRepository productRepository = repositoryFactory.getProductRepository();
        
       
        this.warrantyService = new WarrantyService(warrantyRepository);
        this.invoiceService = new InvoiceService(repositoryFactory.getInvoiceRepository(), productRepository);
        this.invoiceDetailService = new InvoiceDetailService(invoiceDetailRepository, productRepository);
        this.productService = new ProductService(productRepository);
        this.customerService = new CustomerService(repositoryFactory.getCustomerRepository());

    }
    
    /**
     * Constructor chính sử dụng đối tượng service
     * 
     * @param warrantyService Service xử lý bảo hành
     * @param invoiceDetailService Service xử lý chi tiết hóa đơn
     * @param productService Service xử lý sản phẩm
     * @param customerService Service xử lý khách hàng
     */
    public WarrantyController(WarrantyService warrantyService, InvoiceService invoiceService,
                         InvoiceDetailService invoiceDetailService,
                         ProductService productService,
                         CustomerService customerService) {
        this.warrantyService = warrantyService;
        this.invoiceDetailService = invoiceDetailService;
        this.productService = productService;
        this.customerService = customerService;
        this.invoiceService = invoiceService;
    }
    
    /**
     * Khởi tạo controller với service được cung cấp (chủ yếu dùng cho testing)
     * @param warrantyService Service xử lý bảo hành
     */
    public WarrantyController(WarrantyService warrantyService) {
        this.warrantyService = warrantyService;
        this.invoiceDetailService = null;
        this.productService = null;
        this.customerService = null;
        this.invoiceService = null;
    }
    
    /**
     * Thiết lập form dịch vụ bảo hành
     * @param serviceForm Form quản lý bảo hành
     */
    public void setServiceForm(WarrantyServiceForm serviceForm) {
        this.serviceForm = serviceForm;
    }
    
    /**
     * Thiết lập form thẻ bảo hành
     * @param cardForm Form thẻ bảo hành
     */
    public void setCardForm(WarrantyCardForm cardForm) {
        this.cardForm = cardForm;
    }
    
    /**
     * Tải danh sách bảo hành từ cơ sở dữ liệu
     */
    public void loadWarranties() {
        try {
            // Lấy tất cả bảo hành từ service
            List<Warranty> warranties = warrantyService.getAllWarranties();
            
            // Cập nhật giao diện
            if (serviceForm != null) {
                serviceForm.updateWarrantyTable(warranties);
            }
        } catch (Exception e) {
            if (serviceForm != null) {
                JOptionPane.showMessageDialog(
                    serviceForm,
                    "Lỗi khi tải danh sách bảo hành: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
            e.printStackTrace();
        }
    }
    
    /**
     * Tìm kiếm bảo hành theo từ khóa
     * @param keyword Từ khóa tìm kiếm
     */
    public void searchWarranties(String keyword) {
        try {
            List<Warranty> warranties;
            
            if (keyword == null || keyword.trim().isEmpty()) {
                // Nếu từ khóa trống, hiển thị tất cả
                warranties = warrantyService.getAllWarranties();
            } else {
                // Tìm kiếm theo từ khóa
                warranties = warrantyService.searchWarranties(keyword);
            }
            
            // Log số lượng kết quả tìm thấy
            System.out.println("Found " + warranties.size() + " warranties matching keyword: " + keyword);
            
            // Cập nhật giao diện
            if (serviceForm != null) {
                serviceForm.updateWarrantyTable(warranties);
            }
        } catch (Exception e) {
            System.err.println("Error in searchWarranties: " + e.getMessage());
            e.printStackTrace();
            
            if (serviceForm != null) {
                JOptionPane.showMessageDialog(
                    serviceForm,
                    "Lỗi khi tìm kiếm bảo hành: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    /**
     * Hiển thị danh sách bảo hành trên bảng
     * @param warranties Danh sách bảo hành
     */
    private void displayWarranties(List<Warranty> warranties) {
        if (serviceForm == null) return;
        
        DefaultTableModel model = (DefaultTableModel) serviceForm.getWarrantyTable().getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ trong bảng
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Warranty warranty : warranties) {
            // Xác định trạng thái bảo hành
            String status;
            if (warranty.isValid()) {
                status = warranty.isUsed() ? "Đã sử dụng" : "Còn hiệu lực";
            } else {
                status = "Hết hạn";
            }
            
            // Xử lý các trường an toàn để tránh NullPointerException
            String customerId = "";
            if (warranty.getInvoiceDetail() != null && 
                warranty.getInvoiceDetail().getInvoice() != null && 
                warranty.getInvoiceDetail().getInvoice().getCustomerId() != null) {
                customerId = warranty.getInvoiceDetail().getInvoice().getCustomerId();
            }
            
            String productId = "";
            if (warranty.getInvoiceDetail() != null && 
                warranty.getInvoiceDetail().getProduct() != null && 
                warranty.getInvoiceDetail().getProduct().getProductId() != null) {
                productId = warranty.getInvoiceDetail().getProduct().getProductId();
            }
            
            model.addRow(new Object[] {
                warranty.getWarrantyId(),
                warranty.getStartDate() != null ? warranty.getStartDate().format(dateFormatter) : "",
                customerId,
                warranty.getCustomerName() != null ? warranty.getCustomerName() : "",
                warranty.getCustomerPhone() != null ? warranty.getCustomerPhone() : "",
                warranty.getProductName() != null ? warranty.getProductName() : "",
                productId,
                status
            });
        }
    }
    
    /**
     * Xem chi tiết thẻ bảo hành
     * @param warrantyId Mã bảo hành
     */
    public void viewWarrantyDetail(String warrantyId) {
        try {
            // Sử dụng trực tiếp phương thức findById nhận tham số String
            Optional<Warranty> warrantyOpt = warrantyService.findWarrantyById(warrantyId);
            
            if (warrantyOpt.isPresent()) {
                Warranty warranty = warrantyOpt.get();
                showWarrantyCard(warranty);
            } else {
                JOptionPane.showMessageDialog(
                    serviceForm,
                    "Không tìm thấy bảo hành có mã " + warrantyId,
                    "Không tìm thấy",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        } catch (Exception e) {
            showError("Lỗi khi hiển thị thẻ bảo hành", e.getMessage());
        }
    }
    
    /**
     * Hiển thị form thẻ bảo hành
     * @param warranty Thông tin bảo hành
     */
    private void showWarrantyCard(Warranty warranty) {
        if (cardForm == null) {
            cardForm = new WarrantyCardForm();
            cardForm.setController(this);
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        // Thiết lập thông tin khách hàng
        cardForm.getNameCustomerLabel().setText(warranty.getCustomerName() != null ? 
            warranty.getCustomerName() : "");
        cardForm.getAdressCustomerLabel().setText(""); // Nếu có địa chỉ trong CSDL thì hiển thị
        cardForm.getSdtLabel().setText(warranty.getCustomerPhone() != null ? 
            warranty.getCustomerPhone() : "");
        
        // Thiết lập ngày mua
        String purchaseDate = warranty.getStartDate() != null ? 
            warranty.getStartDate().format(formatter) : "";
        cardForm.getDateOfPurchaseLabel().setText(purchaseDate);
        
        // Thiết lập thông tin sản phẩm và bảo hành
        DefaultTableModel model = (DefaultTableModel) cardForm.getProductTable().getModel();
        model.setRowCount(0);
        
        // Tính thời hạn bảo hành
        if (warranty.getStartDate() != null && warranty.getEndDate() != null) {
            long months = ChronoUnit.MONTHS.between(warranty.getStartDate(), warranty.getEndDate());
            String endDate = warranty.getEndDate().format(formatter);
            
            model.addRow(new Object[] {
                1, // STT
                warranty.getProductName() != null ? warranty.getProductName() : "",
                warranty.getInvoiceDetail() != null ? warranty.getInvoiceDetail().getQuantity() : 1,
                months + " tháng (đến " + endDate + ")"
            });
        }
        
        // Hiển thị form
        javax.swing.JFrame frame = new javax.swing.JFrame("Thẻ Bảo Hành");
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        frame.add(cardForm);
        frame.pack();
        frame.setLocationRelativeTo(serviceForm);
        frame.setVisible(true);
    }
    
    /**
     * Hiển thị form đăng ký bảo hành mới
     */
    public void registerWarranty() {
        try {
            // Tạo instance của AddWarrantyForm
            AddWarrantyForm addWarrantyForm = new AddWarrantyForm(this);
            
            // Hiển thị dialog chứa form
            JDialog dialog = new JDialog();
            dialog.setTitle("Đăng ký bảo hành");
            dialog.setModal(true);
            dialog.setSize(980, 650);
            dialog.setLocationRelativeTo(serviceForm);
            dialog.setContentPane(addWarrantyForm);
            dialog.setVisible(true);
        } catch (Exception e) {
            showError("Lỗi hiển thị form đăng ký bảo hành", e.getMessage());
        }
    }
    
    /**
     * Tìm kiếm sản phẩm đã mua theo số điện thoại khách hàng
     * @param phoneNumber Số điện thoại khách hàng
     * @return Danh sách sản phẩm đã mua
     */
    public List<InvoiceDetail> findPurchasedProductsByPhone(String phoneNumber) {
        try {
            // Tìm khách hàng theo số điện thoại
            Optional<Customer> customerOpt = customerService.findCustomerByPhone(phoneNumber);
            
            if (!customerOpt.isPresent()) {
                return new ArrayList<>(); 
            }
            
            Customer customer = customerOpt.get();
            
            //Lấy hóa đơn của khách hàng
            List<Invoice> listInvoices = invoiceService.findInvoicesByCustomer(customer.getCustomerId());
            
            if (listInvoices.isEmpty()) {
                return new ArrayList<>(); // Không có hóa đơn nào
            }

            // Tìm các chi tiết hóa đơn của khách hàng
            List<InvoiceDetail> allInvoiceDetails = new ArrayList<>();
            for (Invoice invoice : listInvoices) {
                if (invoice.getInvoiceDetails() != null) {
                    allInvoiceDetails.addAll(invoice.getInvoiceDetails());
                }
            }
            
            return allInvoiceDetails;
        } catch (Exception e) {
            showError("Lỗi khi tìm kiếm sản phẩm đã mua", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * In thẻ bảo hành
     */
    public void printWarrantyCard() {
        JOptionPane.showMessageDialog(
            cardForm,
            "Chức năng in thẻ bảo hành sẽ được phát triển sau.",
            "Thông Báo",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Hiển thị thông báo lỗi
     * @param title Tiêu đề
     * @param message Nội dung
     */
    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(
            serviceForm != null ? serviceForm : cardForm,
            message,
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Tạo bảo hành từ chi tiết hóa đơn
     * @param invoiceDetail Chi tiết hóa đơn
     * @return Bảo hành đã tạo
     */
    public Warranty createWarrantyFromInvoiceDetail(InvoiceDetail invoiceDetail) {
        try {
            // Kiểm tra xem đã có bảo hành cho chi tiết hóa đơn này chưa
            Optional<Warranty> existingWarranty = warrantyService.findWarrantyByInvoiceDetailId(invoiceDetail.getInvoiceDetailId());
            
            if (existingWarranty.isPresent()) {
                throw new IllegalArgumentException("Sản phẩm này đã được đăng ký bảo hành");
            }
            
            // Lấy thông tin về sản phẩm
            Optional<Product> productOpt = productService.findProductById(invoiceDetail.getProduct().getProductId());
            
            if (!productOpt.isPresent()) {
                throw new IllegalArgumentException("Không tìm thấy thông tin sản phẩm");
            }
            
            Product product = productOpt.get();
            
            // Tạo mới thẻ bảo hành
            Warranty warranty = new Warranty();
            warranty.setInvoiceDetail(invoiceDetail);
            
            // Thiết lập thời gian bảo hành
            LocalDateTime now = LocalDateTime.now();
            warranty.setStartDate(now);
            
            // Thời hạn bảo hành là 12 tháng kể từ ngày tạo thẻ
            warranty.setEndDate(now.plusMonths(12));
            
            // Thiết lập điều kiện bảo hành
            warranty.setWarrantyTerms("Bảo hành 12 tháng cho lỗi phần cứng");
            
            // Thêm bảo hành vào cơ sở dữ liệu
            Warranty savedWarranty = warrantyService.addWarranty(warranty);
            
            // Cập nhật lại danh sách bảo hành
            loadWarranties();
            
            return savedWarranty;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo bảo hành: " + e.getMessage(), e);
        }
    }
    
    /**
     * Xóa thông tin bảo hành theo ID
     * 
     * @param warrantyId ID của bảo hành cần xóa
     * @return true nếu xóa thành công, ngược lại là false
     */
    public boolean deleteWarranty(Integer warrantyId) {
        try {
            if (warrantyId == null) {
                System.err.println("Không thể xóa bảo hành với ID null");
                return false;
            }
            
            System.out.println("Đang xóa bảo hành với ID: " + warrantyId);
            
            // Gọi phương thức xóa từ service
            boolean result = warrantyService.deleteWarranty(warrantyId);
            
            // Log kết quả xóa để debug
            System.out.println("Kết quả xóa bảo hành ID " + warrantyId + ": " + (result ? "Thành công" : "Thất bại"));
            
            return result;
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa bảo hành: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}