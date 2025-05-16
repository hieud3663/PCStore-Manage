package com.pcstore.controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pcstore.model.Customer;
import com.pcstore.model.Employee;
import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Product;
import com.pcstore.model.Return;
import com.pcstore.model.enums.InvoiceStatusEnum;
import com.pcstore.model.enums.PaymentMethodEnum;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.repository.impl.InvoiceRepository;
import com.pcstore.repository.impl.ProductRepository;
import com.pcstore.service.CustomerService;
import com.pcstore.service.InvoiceDetailService;
import com.pcstore.service.InvoiceService;
import com.pcstore.service.ProductService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.ExportInvoice;
import com.pcstore.utils.JExcel;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.TableStyleUtil;
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
    // Singleton instance
    private static InvoiceController instance;
    
    private  InvoiceService invoiceService;
    private  InvoiceDetailService invoiceDetailService;
    private  ProductService productService;
    private  CustomerService customerService;
    
    // UI related fields
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
     * Khởi tạo controller với form (Giao diện người dùng)
     * @param invoiceForm Form hiển thị hóa đơn
     */
    private InvoiceController(InvoiceForm invoiceForm) {
        try {
            this.invoiceForm = invoiceForm;
            this.invoiceService = ServiceFactory.getInvoiceService();
            this.invoiceDetailService = ServiceFactory.getInvoiceDetailService();
            this.productService = ServiceFactory.getProductService();
            this.customerService = ServiceFactory.getCustomerService();
            this.invoiceDetailController = new InvoiceDetailController();
            
            // Khởi tạo danh sách hóa đơn
            loadAllInvoices();
            
            // Thiết lập các sự kiện cho form
            setupEventListeners();
            
            // Thiết lập bảng có thể sắp xếp
            setupTableSorter();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                    String.format(ErrorMessage.INVOICE_CONTROLLER_INIT_ERROR, e.getMessage()), 
                    ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Khởi tạo controller với connection (không liên quan đến UI, sử dụng cho các service khác)
     * 
     * @param connection Kết nối database
     */
    public InvoiceController(Connection connection) {
        try {
 
            // Khởi tạo các services
            this.invoiceService = ServiceFactory.getInvoiceService();
            this.invoiceDetailService = ServiceFactory.getInvoiceDetailService();
            this.productService = ServiceFactory.getProductService();
            this.customerService = ServiceFactory.getCustomerService();
            this.invoiceDetailController = new InvoiceDetailController();
            
            // UI-related fields remain null as this constructor is used for non-UI contexts
            this.invoiceForm = null;
            this.invoiceDetailController = null;
            this.invoiceList = null;
            this.currentInvoice = null;
            this.tableSorter = null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                    String.format(ErrorMessage.INVOICE_CONTROLLER_INIT_ERROR, e.getMessage()), 
                    ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Thiết lập các sự kiện cho form
     */
    private void setupEventListeners() {
        if (invoiceForm == null) return;
        
        // Thiết lập sự kiện khi chọn hóa đơn
        invoiceForm.getTableInvoice().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                try {
                    int selectedRow = invoiceForm.getTableInvoice().getSelectedRow();
                    if (selectedRow >= 0) {
                        // Chuyển đổi chỉ số hàng từ view sang model nếu đang sắp xếp
                        int modelRow = invoiceForm.getTableInvoice().convertRowIndexToModel(selectedRow);
                        Object idValue = invoiceForm.getTableInvoice().getModel().getValueAt(modelRow, 2);
                        
                        if (idValue != null) {
                            int invoiceId = Integer.parseInt(idValue.toString());
                            loadInvoiceDetails(invoiceId);
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Lỗi khi chọn hóa đơn: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        
        // Thiết lập sự kiện xuất Excel
        invoiceForm.getBtnExportExcel().addActionListener(e -> {
            exportAllInvoicesToExcel();
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
        if (invoiceForm == null) return;
        
        tableSorter = invoiceForm.getInvoiceTableSorter();
        
        // comparator (cột 5 - Tổng tiền)
        tableSorter.setComparator(5, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return compareAmount(s1, s2);
            }
        });

        tableSorter.setComparator(6, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return compareAmount(s1, s2);
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

    private int compareAmount(String s1, String s2) {
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
            
            Invoice result = invoiceService.createInvoice(invoice);
            
            // Nếu đang chạy trong context của UI, cập nhật danh sách hóa đơn
            if (invoiceForm != null) {
                loadAllInvoices();
            }
            
            return result;
        } catch (Exception e) {
            if (invoiceForm != null) {
                JOptionPane.showMessageDialog(null, 
                        String.format(ErrorMessage.INVOICE_CREATE_ERROR, e.getMessage()), 
                        ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            }
            throw new RuntimeException(String.format(ErrorMessage.INVOICE_CREATE_ERROR, e.getMessage()), e);
        }
    }

    /**
     * Tạo hóa đơn mới (overloaded method without payment method)
     * 
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
            
            // Nếu đang chạy trong context của UI, cập nhật danh sách hóa đơn
            if (invoiceForm != null) {
                loadAllInvoices();
            }
            
            return savedInvoice;
        } catch (Exception e) {
            if (invoiceForm != null) {
                JOptionPane.showMessageDialog(null, "Lỗi khi tạo hóa đơn mới: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
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
                throw new IllegalArgumentException(ErrorMessage.INVOICE_CUSTOMER_NULL);
            }
            
            if (product == null) {
                throw new IllegalArgumentException(ErrorMessage.PRODUCT_NULL);
            }
            
            if (quantity <= 0) {
                throw new IllegalArgumentException(ErrorMessage.PRODUCT_QUANTITY_NOT_POSITIVE);
            }
            
            // Kiểm tra tồn kho
            if (product.getStockQuantity() < quantity) {
                throw new IllegalArgumentException(String.format("Số lượng sản phẩm không đủ. Hiện chỉ còn %d", product.getStockQuantity()));
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
            
            // Refresh UI nếu đang trong context UI
            if (invoiceForm != null && currentInvoice != null && 
                    currentInvoice.getInvoiceId().equals(invoice.getInvoiceId())) {
                loadInvoiceDetails(invoice.getInvoiceId());
            }
            
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
                throw new IllegalArgumentException(ErrorMessage.PRODUCT_QUANTITY_NOT_POSITIVE);
            }
            
            Optional<InvoiceDetail> detailOpt = invoiceDetailService.findInvoiceDetailById(invoiceDetailId);
            if (!detailOpt.isPresent()) {
                throw new IllegalArgumentException(ErrorMessage.INVOICE_DETAIL_PRODUCT_NULL);
            }
            
            InvoiceDetail detail = detailOpt.get();
            
            // Kiểm tra tồn kho nếu số lượng tăng
            int oldQuantity = detail.getQuantity();
            if (newQuantity > oldQuantity) {
                Product product = detail.getProduct();
                int additionalQuantity = newQuantity - oldQuantity;
                
                if (product.getStockQuantity() < additionalQuantity) {
                    throw new IllegalArgumentException(String.format("Số lượng sản phẩm không đủ. Hiện chỉ còn %d", product.getStockQuantity()));
                }
            }
            
            // Cập nhật số lượng
            detail.setQuantity(newQuantity);
            
            // Lưu chi tiết hóa đơn
            InvoiceDetail updatedDetail = invoiceDetailService.updateInvoiceDetail(detail);
            
            // Refresh UI nếu đang trong context UI
            if (invoiceForm != null && currentInvoice != null && 
                    currentInvoice.getInvoiceId().equals(detail.getInvoice().getInvoiceId())) {
                loadInvoiceDetails(detail.getInvoice().getInvoiceId());
            }
            
            return updatedDetail;
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
            Optional<InvoiceDetail> detailOpt = invoiceDetailService.findInvoiceDetailById(invoiceDetailId);
            Integer invoiceId = null;
            
            if (detailOpt.isPresent()) {
                invoiceId = detailOpt.get().getInvoice().getInvoiceId();
            }
            
            boolean result = invoiceDetailService.deleteInvoiceDetail(invoiceDetailId);
            
            // Refresh UI nếu đang trong context UI
            if (result && invoiceForm != null && currentInvoice != null && 
                    invoiceId != null && currentInvoice.getInvoiceId().equals(invoiceId)) {
                loadInvoiceDetails(invoiceId);
            }
            
            return result;
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
            Invoice updatedInvoice = invoiceService.updateInvoice(invoice);
            
            // Refresh UI nếu đang trong context UI
            if (invoiceForm != null) {
                loadAllInvoices();
                
                if (currentInvoice != null && currentInvoice.getInvoiceId().equals(invoiceId)) {
                    loadInvoiceDetails(invoiceId);
                }
            }
            
            return updatedInvoice;
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
            
            // Refresh UI nếu đang trong context UI
            if (invoiceForm != null) {
                loadAllInvoices();
                
                if (currentInvoice != null && currentInvoice.getInvoiceId().equals(invoiceId)) {
                    loadInvoiceDetails(invoiceId);
                }
            }
            
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi hủy hóa đơn: " + e.getMessage(), e);
        }
    }
    
    /**
     * Lấy danh sách tất cả hóa đơn
     * @return Danh sách hóa đơn
     */
    public List<Invoice> getAllInvoices() {
        try {
            System.out.println("Đang lấy danh sách tất cả hóa đơn...");
            List<Invoice> invoices = invoiceService.findAllInvoices();
            
            if (invoices == null) {
                System.err.println("InvoiceService.findAllInvoices() trả về null");
                return new ArrayList<>();
            }
            
            System.out.println("Lấy được " + invoices.size() + " hóa đơn");
            return invoices;
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy danh sách hóa đơn: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Lấy danh sách tất cả hóa đơn có thể trả hàng
     * @return Danh sách hóa đơn có thể trả hàng
     */
    public List<Invoice> getAllInvoicesForReturn() {
        try {
            System.out.println("Đang lấy danh sách hóa đơn có thể trả hàng...");
            List<Invoice> invoices = invoiceService.findAllInvoicesForReturn();
            
            if (invoices == null) {
                System.out.println("Không có hóa đơn nào có thể trả hàng.");
                return new ArrayList<>();
            }
            
            System.out.println("Tìm thấy " + invoices.size() + " hóa đơn có thể trả hàng.");
            return invoices;
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy hóa đơn cho trả hàng: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy danh sách tất cả hóa đơn cho màn hình bảo hành
     * @return Danh sách hóa đơn với chi tiết sản phẩm
     */
    public List<Invoice> getAllInvoicesForWarranty() {
        try {
            return invoiceService.findAllInvoicesForWarranty();
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy danh sách hóa đơn cho bảo hành: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy danh sách chi tiết hóa đơn cho màn hình bảo hành
     * @param invoiceId ID hóa đơn
     * @return Danh sách chi tiết hóa đơn với thông tin sản phẩm
     */
    private List<InvoiceDetail> getInvoiceDetailsForWarranty(Integer invoiceId) {
        try {
            return invoiceService.findInvoiceDetailsForWarranty(invoiceId);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy chi tiết hóa đơn cho bảo hành: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    

    /**
     * Lấy danh sách chi tiết hóa đơn có thể trả hàng
     * @param invoiceId ID hóa đơn cần lấy chi tiết
     * @return Danh sách chi tiết hóa đơn có thể trả hàng
     */
    public List<InvoiceDetail> getReturnableInvoiceDetails(Integer invoiceId) {
        try {
            if (invoiceId == null) {
                return new ArrayList<>();
            }
            
            List<InvoiceDetail> allDetails = getInvoiceDetails(invoiceId);
            List<InvoiceDetail> returnableDetails = new ArrayList<>();
            
            // Lấy thông tin về các đơn trả hàng đã có
            ReturnController returnController = new ReturnController(
                ServiceFactory.getInstance().getConnection(),
                ServiceFactory.getInvoiceService(), 
                ServiceFactory.getProductService()
            );
            
            for (InvoiceDetail detail : allDetails) {
                try {
                    // Lấy số lượng đã trả
                    List<Return> returns = returnController.getReturnsByInvoiceDetail(detail.getInvoiceDetailId());
                    int returnedQuantity = 0;
                    
                    if (returns != null) {
                        for (Return ret : returns) {
                            if (ret != null && ("Approved".equals(ret.getStatus()) || 
                                               "Completed".equals(ret.getStatus()))) {
                                returnedQuantity += ret.getQuantity();
                            }
                        }
                    }
                    
                    // Tính số lượng còn lại có thể trả
                    int remainingQuantity = detail.getQuantity() - returnedQuantity;
                    
                    // Nếu còn có thể trả, thêm vào danh sách
                    if (remainingQuantity > 0) {
                        // Thay vì sử dụng setAvailableQuantity, lưu tạm thời trong một map
                        detail.setExtraData("availableQuantity", remainingQuantity);
                        // Hoặc nếu không có phương thức setExtraData, chỉ thêm vào danh sách
                        returnableDetails.add(detail);
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi khi xử lý chi tiết hóa đơn " + detail.getInvoiceDetailId() + ": " + e.getMessage());
                }
            }
            
            return returnableDetails;
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy chi tiết hóa đơn có thể trả: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy danh sách tất cả hóa đơn cho màn hình trả hàng
     * @return Danh sách hóa đơn đơn giản
     */
    public List<Invoice> getAllInvoicesSimple() {
        try {
            return invoiceService.findAllInvoicesSimple();
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy danh sách hóa đơn đơn giản: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
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
            return invoiceService.findInvoicesByCustomerID(customerId);
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
                return invoiceService.findInvoicesByCustomerID(customerOpt.get().getCustomerId());
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            // Nếu không có form thì dùng phương thức tìm kiếm cũ
            if (invoiceForm == null || tableSorter == null) {
                return new ArrayList<>();
            }
            
            // Xóa bộ lọc nếu từ khóa trống
            if (keyword == null || keyword.trim().isEmpty()) {
                tableSorter.setRowFilter(null);
                return invoiceList;
            }
            
            // Sử dụng TableStyleUtil để áp dụng bộ lọc
            // Các cột cần tìm kiếm:
            // 2: ID hóa đơn
            // 3: Ngày tạo
            // 4: Nhân viên
            // 6: Tổng tiền
            // 7: Phương thức thanh toán
            // 8: Tên khách hàng
            // 9: SĐT khách hàng
            // 10: Trạng thái
            TableStyleUtil.applyFilter(tableSorter, keyword, 2, 3, 4, 6, 7, 8, 9, 10);
            
            // Tạo danh sách kết quả từ các hàng được lọc
            List<Invoice> filteredInvoices = new ArrayList<>();
            for (int i = 0; i < tableSorter.getViewRowCount(); i++) {
                int modelRow = tableSorter.convertRowIndexToModel(i);
                int invoiceId = Integer.parseInt(invoiceForm.getTableInvoice().getModel().getValueAt(modelRow, 2).toString());
                
                // Tìm invoice tương ứng từ danh sách
                for (Invoice invoice : invoiceList) {
                    if (invoice.getInvoiceId() == invoiceId) {
                        filteredInvoices.add(invoice);
                        break;
                    }
                }
            }
            
            return filteredInvoices;
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm kiếm hóa đơn: " + e.getMessage());
            e.printStackTrace();
            
           return new ArrayList<>();
        }
    }

    /**
     * Xóa bộ lọc tìm kiếm và hiển thị tất cả hóa đơn
     */
    public void clearSearch() {
        if (invoiceForm != null && tableSorter != null) {
            tableSorter.setRowFilter(null);
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
            boolean result = invoiceService.deleteInvoice(invoiceId);
            
            // Nếu đang trong context UI, cập nhật danh sách hóa đơn
            if (result && invoiceForm != null) {
                loadAllInvoices();
                
                // Nếu hóa đơn đang hiển thị chi tiết bị xóa, xóa dữ liệu chi tiết
                if (currentInvoice != null && currentInvoice.getInvoiceId().equals(invoiceId)) {
                    currentInvoice = null;
                    DefaultTableModel model = (DefaultTableModel) invoiceForm.getTableInvoiceDetail().getModel();
                    model.setRowCount(0);
                }
            }
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa hóa đơn: " + e.getMessage(), e);
        }
    }

    //==================== UI Related Methods ====================
    
    /**
     * Tải tất cả hóa đơn và cập nhật UI
     */
    public void loadAllInvoices() {
        if (invoiceForm == null) {
            throw new IllegalStateException("Không thể tải hóa đơn khi không có form");
        }
        
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
        if (invoiceForm == null) return;
        
        DefaultTableModel model = (DefaultTableModel) invoiceForm.getTableInvoice().getModel();
        model.setRowCount(0);
        
        int count = 0;
        for (Invoice invoice : invoices) {
            count++;
            Object[] row = new Object[12]; // Increased array size to accommodate the new column
            row[0] = false; 
            row[1] = count;
            row[2] = invoice.getInvoiceId();
            row[3] = invoice.getInvoiceDate() != null ? invoice.getInvoiceDate().format(dateFormatter) : "";
            row[4] = invoice.getEmployee() != null ? invoice.getEmployee().getFullName() : "";
            row[5] = invoice.getDiscountAmount() != null ? currencyFormatter.format(invoice.getDiscountAmount()) : "";
            row[6] = invoice.getTotalAmount() != null ? currencyFormatter.format(invoice.getTotalAmount()) : "";
            row[7] = getPaymentMethodDisplay(invoice.getPaymentMethod());
            row[8] = invoice.getCustomer() != null ? invoice.getCustomer().getFullName() : "";
            row[9] = invoice.getCustomer() != null ? invoice.getCustomer().getPhoneNumber() : ""; 
            row[10] = getStatusDisplay(invoice.getStatus());
            row[11] = ""; 
            
            model.addRow(row);
        }
    }
    
    /**
     * Tải chi tiết hóa đơn theo ID
     * @param invoiceId ID của hóa đơn cần tải chi tiết
     */
    public void loadInvoiceDetails(int invoiceId) {
        if (invoiceForm == null) {
            throw new IllegalStateException("Không thể tải chi tiết hóa đơn khi không có form");
        }
        
        try {
            Optional<Invoice> invoiceOpt = invoiceService.findInvoiceById(invoiceId);
            
            if (invoiceOpt.isPresent()) {
                Invoice invoice = invoiceOpt.get();
                currentInvoice = invoice;
                
                List<InvoiceDetail> details = invoiceDetailService.findInvoiceDetailsByInvoiceId(invoiceId);
                
                invoice.setInvoiceDetails(details);
                
                // Cập nhật bảng chi tiết
                if (invoiceDetailController != null) {
                    invoiceDetailController.updateInvoiceDetailTable(details, 
                            invoiceForm.getTableInvoiceDetail());
                } else {
                    // Cập nhật bảng theo cách thủ công nếu controller là null
                    DefaultTableModel detailModel = (DefaultTableModel) invoiceForm.getTableInvoiceDetail().getModel();
                    detailModel.setRowCount(0);
                    
                    int no = 1;
                    for (InvoiceDetail detail : details) {
                        Object[] row = new Object[6]; // Điều chỉnh số cột nếu cần
                        row[0] = no++;
                        row[1] = detail.getProduct().getProductId();
                        row[2] = detail.getProduct().getProductName();
                        row[3] = detail.getQuantity();
                        row[4] = currencyFormatter.format(detail.getUnitPrice());
                        row[5] = currencyFormatter.format(detail.getSubtotal());
                        detailModel.addRow(row);
                    }
                }
                
            } else {
                System.err.println("Không tìm thấy hóa đơn với ID: " + invoiceId);
                // Nếu không tìm thấy hóa đơn, xóa bảng chi tiết
                DefaultTableModel model = (DefaultTableModel) invoiceForm.getTableInvoiceDetail().getModel();
                model.setRowCount(0);
                currentInvoice = null;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải chi tiết hóa đơn: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                    
            // Xóa bảng chi tiết khi có lỗi
            DefaultTableModel model = (DefaultTableModel) invoiceForm.getTableInvoiceDetail().getModel();
            model.setRowCount(0);
        }
    }
    
    /**
     * In hóa đơn được chọn
     */
    public void printSelectedInvoice() {
        if (invoiceForm == null) {
            throw new IllegalStateException("Không thể in hóa đơn khi không có form");
        }
        
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
                
                if (invoice.getStatus() != InvoiceStatusEnum.COMPLETED && 
                    invoice.getStatus() != InvoiceStatusEnum.PAID) {
                    JOptionPane.showMessageDialog(null, "Hóa đơn chưa hoàn thành thanh toán!", 
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int option = JOptionPane.showConfirmDialog(null, 
                        "Bạn có muốn in hóa đơn này không?", "Xác nhận", 
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (option != JOptionPane.YES_OPTION) {
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
        if (invoiceForm == null) {
            throw new IllegalStateException("Không thể thanh toán hóa đơn khi không có form");
        }
        
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
        if (invoiceForm == null) {
            throw new IllegalStateException("Không thể xóa hóa đơn khi không có form");
        }
        
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
            
            // Nếu đang trong context UI, cập nhật danh sách hóa đơn
            if (invoiceForm != null) {
                loadAllInvoices();
            }
            
            return updatedInvoice != null;
        } catch (Exception e) {
            if (invoiceForm != null) {
                JOptionPane.showMessageDialog(null, "Lỗi khi xử lý thanh toán: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
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
                
                // Nếu đang trong context UI, cập nhật danh sách hóa đơn
                if (invoiceForm != null) {
                    loadAllInvoices();
                }
                
                return updatedInvoice != null;
            }
            return false;
        } catch (Exception e) {
            if (invoiceForm != null) {
                JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật trạng thái hóa đơn: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
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
     * Xuất danh sách hóa đơn ra file Excel theo dữ liệu hiển thị trên bảng hiện tại
     */
    public void exportAllInvoicesToExcel() {
        if (invoiceForm == null) {
            throw new IllegalStateException("Không thể xuất Excel khi không có form");
        }
        
        JTable table = invoiceForm.getTableInvoice();
        int rowCount = table.getRowCount();
        
        // Kiểm tra xem có dữ liệu để xuất không
        if (rowCount == 0) {
            JOptionPane.showMessageDialog(invoiceForm, 
                    "Không có dữ liệu hóa đơn để xuất",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            // Tạo dữ liệu xuất ra Excel
            String[] headers = {"STT", "Mã hóa đơn", "Ngày tạo", "Nhân viên", "Khách hàng", 
                               "Số điện thoại", "Tổng tiền", "Giảm giá", "Thanh toán", "Trạng thái", "Ghi chú"};
            
            Object[][] data = new Object[rowCount][headers.length];
            
            for (int i = 0; i < rowCount; i++) {
                // STT theo thứ tự trong bảng
                data[i][0] = i + 1;
                
                // Lấy dữ liệu từ bảng
                int invoiceId = Integer.parseInt(table.getValueAt(i, 2).toString());
                data[i][1] = invoiceId;
                data[i][2] = table.getValueAt(i, 3); // Ngày tạo
                data[i][3] = table.getValueAt(i, 4); // Nhân viên
                data[i][4] = table.getValueAt(i, 8); // Khách hàng
                data[i][5] = table.getValueAt(i, 9); // Số điện thoại
                
                // Xử lý tổng tiền và giảm giá (loại bỏ định dạng tiền tệ)
                String totalAmount = table.getValueAt(i, 6).toString();
                String discountAmount = table.getValueAt(i, 5).toString();
                
                totalAmount = totalAmount.replace(".", "").replace(",", "");
                discountAmount = discountAmount.replace(".", "").replace(",", "");
                
                data[i][6] = totalAmount.isEmpty() ? "0" : totalAmount;
                data[i][7] = discountAmount.isEmpty() ? "0" : discountAmount;
                
                data[i][8] = table.getValueAt(i, 7); // Phương thức thanh toán
                data[i][9] = table.getValueAt(i, 10); // Trạng thái
                data[i][10] = ""; // Ghi chú (không có trong bảng)
            }
            
            String fileName = "DANH_SACH_HOA_DON_" + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            
            // Nếu đang áp dụng bộ lọc, thêm thông tin vào tên file
            if (tableSorter != null && tableSorter.getRowFilter() != null) {
                fileName += "_FILTERED";
            }
            
            JExcel jExcel = new JExcel();
            boolean success = jExcel.toExcel(headers, data, "Danh sách hóa đơn", fileName);
            
            if (success) {
                JOptionPane.showMessageDialog(invoiceForm,
                        "Xuất Excel thành công!",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(invoiceForm,
                        "Xuất Excel không thành công!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(invoiceForm,
                    "Lỗi khi xuất Excel: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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
