package com.pcstore.controller;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.pcstore.model.Customer;
import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
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
import com.pcstore.utils.ErrorMessage;
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
    private static final Logger logger = Logger.getLogger(WarrantyController.class.getName());
    
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
            List<Warranty> warranties = warrantyService.getAllWarranties();
            if (serviceForm != null) {
                serviceForm.updateWarrantyTable(warranties);
            }
        } catch (Exception e) {
            if (serviceForm != null) {
                JOptionPane.showMessageDialog(
                    serviceForm,
                    ErrorMessage.WARRANTY_LOAD_ERROR + ": " + e.getMessage(),
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
                warranties = warrantyService.getAllWarranties();
            } else {
                warranties = warrantyService.searchWarranties(keyword);
            }
            if (serviceForm != null) {
                serviceForm.updateWarrantyTable(warranties);
            }
        } catch (Exception e) {
            if (serviceForm != null) {
                JOptionPane.showMessageDialog(
                    serviceForm,
                    ErrorMessage.WARRANTY_SEARCH_ERROR + ": " + e.getMessage(),
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
            Optional<Warranty> warrantyOpt = warrantyService.findWarrantyById(warrantyId);
            if (warrantyOpt.isPresent()) {
                Warranty warranty = warrantyOpt.get();
                showWarrantyCard(warranty);
            } else {
                JOptionPane.showMessageDialog(
                    serviceForm,
                    String.format(ErrorMessage.WARRANTY_NOT_FOUND_WITH_ID.toString(), warrantyId),
                    "Không tìm thấy",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        } catch (Exception e) {
            showError(ErrorMessage.WARRANTY_DETAIL_ERROR.toString(), e.getMessage());
        }
    }
    
    /**
     * Hiển thị form thẻ bảo hành
     * @param warranty Thông tin bảo hành
     */
    private void showWarrantyCard(Warranty warranty) {
        try {
            // Create new form if it doesn't exist
            if (cardForm == null) {
                cardForm = new WarrantyCardForm();
            }
            
            // Display all warranty information using the new method
            cardForm.displayWarrantyInfo(warranty);
            
            // Display the form in a new window
            javax.swing.JFrame frame = new javax.swing.JFrame("Thẻ Bảo Hành");
            frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
            frame.add(cardForm);
            frame.pack();
            frame.setLocationRelativeTo(serviceForm);
            frame.setVisible(true);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi hiển thị thẻ bảo hành", e);
            showError("Lỗi hiển thị thẻ bảo hành", e.getMessage());
        }
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
     * Tìm sản phẩm đã mua theo số điện thoại khách hàng
     * @param phoneNumber Số điện thoại khách hàng
     * @return Danh sách chi tiết hóa đơn
     */
    public List<InvoiceDetail> findPurchasedProductsByPhone(String phoneNumber) {
        List<InvoiceDetail> result = new ArrayList<>();
        try {
            // Tìm khách hàng theo số điện thoại
            Optional<Customer> customer = customerService.findCustomerByPhone(phoneNumber);
            
            if (customer.isPresent()) {
                logger.info("Tìm thấy khách hàng: " + customer.get().getCustomerId());
                
                // Tìm tất cả hóa đơn của khách hàng
                List<Invoice> invoices = invoiceService.findInvoicesByCustomerForWarranty(customer.get().getCustomerId());
                logger.info("Số hóa đơn tìm thấy: " + invoices.size());
                
                for (Invoice invoice : invoices) {
                    logger.info("Xử lý hóa đơn ID: " + invoice.getInvoiceId() + 
                               ", Ngày: " + (invoice.getInvoiceDate() != null ? invoice.getInvoiceDate() : "N/A"));
                    
                    if (invoice.getInvoiceDetails() != null) {
                        for (InvoiceDetail detail : invoice.getInvoiceDetails()) {
                            // Kiểm tra xem detail có đầy đủ thông tin không
                            if (detail != null) {
                                // Đảm bảo InvoiceDetail có reference đến Invoice
                                detail.setInvoice(invoice);
                                
                                // Log thông tin chi tiết
                                String productName = (detail.getProduct() != null && detail.getProduct().getProductName() != null) 
                                    ? detail.getProduct().getProductName() : "Không có tên";
                                logger.info("Chi tiết: " + detail.getInvoiceDetailId() + 
                                          ", Sản phẩm: " + productName);
                                
                                result.add(detail);
                            }
                        }
                    } else {
                        logger.warning("Hóa đơn " + invoice.getInvoiceId() + " không có chi tiết");
                    }
                }
            } else {
                logger.warning("Không tìm thấy khách hàng với số điện thoại: " + phoneNumber);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi tìm sản phẩm đã mua theo số điện thoại", e);
            throw new RuntimeException("Lỗi khi tìm sản phẩm đã mua: " + e.getMessage(), e);
        }
        
        logger.info("Đã tìm thấy " + result.size() + " chi tiết hóa đơn");
        return result;
    }
    
    /**
     * Tìm kiếm sản phẩm đã mua theo SĐT và đã loại bỏ những sản phẩm đã có bảo hành
     * @param phoneNumber Số điện thoại khách hàng
     * @return Danh sách sản phẩm chưa đăng ký bảo hành
     */
    public List<InvoiceDetail> findPurchasedProductsWithoutWarranty(String phoneNumber) {
        List<InvoiceDetail> allProducts = findPurchasedProductsByPhone(phoneNumber);
        List<InvoiceDetail> productsWithoutWarranty = new ArrayList<>();
        
        for (InvoiceDetail detail : allProducts) {
            // Kiểm tra xem sản phẩm đã có bảo hành chưa
            Optional<Warranty> warranty = warrantyService.findWarrantyByInvoiceDetailId(detail.getInvoiceDetailId());
            if (!warranty.isPresent()) {
                productsWithoutWarranty.add(detail);
            }
        }
        
        return productsWithoutWarranty;
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
            if (invoiceDetail == null) {
                throw new IllegalArgumentException(ErrorMessage.INVOICE_DETAIL_NULL.toString());
            }
            if (invoiceDetail.getProduct() == null) {
                throw new IllegalArgumentException(ErrorMessage.PRODUCT_NULL.toString());
            }
            if (invoiceDetail.getInvoice() == null) {
                throw new IllegalArgumentException(ErrorMessage.INVOICE_NULL.toString());
            }
            // Tạo đối tượng bảo hành mới
            Warranty warranty = new Warranty();
            
            // Tạo mã bảo hành tự động nếu chưa có
            if (warranty.getWarrantyId() == null || warranty.getWarrantyId().isEmpty()) {
                String nextId = warrantyService.generateNextWarrantyId();
                warranty.setWarrantyId(nextId);
                logger.info("Tự động tạo mã bảo hành: " + nextId);
            }
            
            // Thiết lập chi tiết hóa đơn
            warranty.setInvoiceDetail(invoiceDetail);
            
            // Thiết lập thời hạn bảo hành
            LocalDateTime now = LocalDateTime.now();
            if (invoiceDetail.getInvoice() != null && invoiceDetail.getInvoice().getInvoiceDate() != null) {
                warranty.setStartDate(invoiceDetail.getInvoice().getInvoiceDate());
            } else {
                warranty.setStartDate(now);
            }
            
            // Thời hạn bảo hành là 12 tháng kể từ ngày mua
            warranty.setEndDate(warranty.getStartDate().plusMonths(12));
            
            // Thiết lập điều kiện bảo hành
            warranty.setWarrantyTerms("Bảo hành 12 tháng cho lỗi phần cứng");
            
            // Thêm bảo hành vào cơ sở dữ liệu
            Warranty savedWarranty = warrantyService.addWarranty(warranty);
            
            // Cập nhật lại danh sách bảo hành
            loadWarranties();
            
            return savedWarranty;
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.WARRANTY_CREATE_ERROR + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Xóa thông tin bảo hành theo ID
     * 
     * @param warrantyId ID của bảo hành cần xóa
     * @return true nếu xóa thành công, ngược lại là false
     */
    public boolean deleteWarranty(String warrantyId) {
        try {
            if (warrantyId == null) {
                System.err.println(ErrorMessage.WARRANTY_ID_NULL);
                return false;
            }
            
            System.out.println("Đang xóa bảo hành với ID: " + warrantyId);
            
            // Gọi phương thức xóa từ service
            boolean result = warrantyService.deleteWarranty(warrantyId);
            
            // Log kết quả xóa để debug
            System.out.println("Kết quả xóa bảo hành ID " + warrantyId + ": " + (result ? "Thành công" : "Thất bại"));
            
            return result;
        } catch (Exception e) {
            System.err.println(ErrorMessage.WARRANTY_DELETE_ERROR + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy đối tượng service bảo hành
     * @return Service bảo hành
     */
    public WarrantyService getWarrantyService() {
        return this.warrantyService;
    }
    
    /**
     * Hiển thị thẻ bảo hành
     * @param warranty Thông tin bảo hành
     */

     public void handleWarrantyRegistration() {
        try {
            // Tạo dialog mới
            javax.swing.JDialog dialog = new javax.swing.JDialog();
            dialog.setTitle("Đăng ký bảo hành mới");
            dialog.setModal(true);
            dialog.setSize(980, 650);
            dialog.setLocationRelativeTo(serviceForm);

            // Tạo form đăng ký bảo hành và thêm vào dialog
            AddWarrantyForm addWarrantyForm = new AddWarrantyForm(this);
            dialog.add(addWarrantyForm);

            // Hiển thị dialog
            dialog.setVisible(true);

            // Sau khi dialog đóng, cập nhật lại danh sách bảo hành
            loadWarranties();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(serviceForm,
                ErrorMessage.WARRANTY_FORM_ADD_ERROR + ": " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleRemoveRepair() {
        try {
            JTable table = serviceForm.getWarrantyTable();
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(serviceForm,
                    ErrorMessage.WARRANTY_SELECT_ONE_DELETE,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Object warrantyIdObj = table.getValueAt(selectedRow, 0);
            if (warrantyIdObj == null || warrantyIdObj.toString().isEmpty()) {
                JOptionPane.showMessageDialog(serviceForm,
                    ErrorMessage.WARRANTY_ID_INVALID,
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            String warrantyId = warrantyIdObj.toString();
            int choice = JOptionPane.showConfirmDialog(serviceForm,
                ErrorMessage.WARRANTY_DELETE_CONFIRM,
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
            boolean success = deleteWarranty(warrantyId);
            if (success) {
                JOptionPane.showMessageDialog(serviceForm,
                    ErrorMessage.WARRANTY_DELETE_SUCCESS,
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
                loadWarranties();
            } else {
                JOptionPane.showMessageDialog(serviceForm,
                    ErrorMessage.WARRANTY_DELETE_FAIL,
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(serviceForm,
                ErrorMessage.WARRANTY_DELETE_ERROR + ": " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleDetailWarrantyCard() {
        JTable table = serviceForm.getWarrantyTable();
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                serviceForm,
                ErrorMessage.WARRANTY_SELECT_ONE_DETAIL,
                "Chưa Chọn",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        String warrantyId = table.getValueAt(selectedRow, 0).toString();
        viewWarrantyDetail(warrantyId);
    }

    public void handleWarrantyInformationLookup() {
        String keyword = serviceForm.getSearchField().getText().trim();
        searchWarranties(keyword);
    }
}