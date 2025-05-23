package com.pcstore.controller;

import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.pcstore.model.Customer;
import com.pcstore.model.Discount;
import com.pcstore.model.Employee;
import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Product;
import com.pcstore.model.base.BasePayment;
import com.pcstore.model.enums.InvoiceStatusEnum;
import com.pcstore.model.enums.PaymentMethodEnum;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.repository.impl.CustomerRepository;
import com.pcstore.repository.impl.DiscountRepository;
import com.pcstore.repository.impl.EmployeeRepository;
import com.pcstore.repository.impl.InvoiceDetailRepository;
import com.pcstore.repository.impl.InvoiceRepository;
import com.pcstore.repository.impl.ProductRepository;
import com.pcstore.service.InvoiceService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.utils.DatabaseConnection;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.ExportInvoice;
import com.pcstore.utils.JDialogInputUtils;
import com.pcstore.utils.LocaleManager;
import com.pcstore.view.DashboardForm;
import com.pcstore.view.PayForm;
import com.pcstore.view.SellForm;

/**
 * Controller xử lý các hoạt động bán hàng
 */
/**
 * Controller quản lý quy trình bán hàng.
 * Lớp này xử lý tất cả các chức năng liên quan đến việc tạo hóa đơn bán hàng,
 * từ khởi tạo đơn hàng, quản lý giỏ hàng, tìm kiếm khách hàng và sản phẩm,
 * áp dụng giảm giá, đến hoàn thành giao dịch.
 * 
 * Quản lý trạng thái giỏ hàng hiện tại, hóa đơn đang xử lý, 
 * và các tương tác với cơ sở dữ liệu thông qua các repository.
 */
public class SellController {
    private Connection connection;
    private RepositoryFactory repositoryFactory;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final EmployeeRepository employeeRepository;
    private final DiscountRepository discountRepository;
    private final InvoiceService invoiceService;
    
    private SellForm sellForm;
    
    private Invoice currentInvoice;
    private List<InvoiceDetail> cartItems;
    private TableRowSorter<TableModel> tabelListProductSorter;

    /**
     * Constructor khởi tạo các repositories và services
     * @param connection Kết nối cơ sở dữ liệu
     * @param repositoryFactory Factory để tạo repositories
     */
    public SellController(SellForm sellForm) {
        this.sellForm = sellForm;

        this.connection = DatabaseConnection.getInstance().getConnection();
        this.repositoryFactory = RepositoryFactory.getInstance(connection);
        this.invoiceRepository = new InvoiceRepository(connection, repositoryFactory);
        this.invoiceDetailRepository = new InvoiceDetailRepository(connection, repositoryFactory);
        this.customerRepository = new CustomerRepository(connection);
        this.productRepository = new ProductRepository(connection);
        this.employeeRepository = new EmployeeRepository(connection);
        this.discountRepository = new DiscountRepository(connection);
        this.invoiceService = new InvoiceService(invoiceRepository, productRepository);
        
        this.cartItems = new ArrayList<>();

        loadAllProducts();

        setuptabelListProductSorter();
    }
    
    /**
     * Khởi tạo một giao dịch bán hàng mới
     * @param employeeId ID của nhân viên tạo giao dịch
     * @return True nếu khởi tạo thành công, false nếu ngược lại
     */
    public boolean initializeSale(Employee employee) {
        try {
            // Đặt lại bất kỳ trạng thái bán hàng trước đó
            this.cartItems.clear();
            this.currentInvoice = null;
            
            // Tạo một giao dịch tạm thời không có khách hàng (sẽ được thiết lập sau)
            Customer defaultCustomer = customerRepository.findById("GUEST").orElse(null);
            if (defaultCustomer == null) {
                JOptionPane.showMessageDialog(null, 
                    ErrorMessage.DEFAULT_CUSTOMER_NOT_FOUND,
                    ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                return false;
            }

            createInvoice(defaultCustomer, employee);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    

    private void setuptabelListProductSorter(){
        if (sellForm == null) return;

        tabelListProductSorter = sellForm.getTableListProductSorter();
        
        // comparator (cột 5 - Giá bán)
        tabelListProductSorter.setComparator(5, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return compareAmount(s1, s2);
            }
        });
      
    }

    private int compareAmount(String s1, String s2) {
        try {
            String v1 = s1.replaceAll("\\.", "").replaceAll("\\,", "");
            String v2 = s2.replaceAll("\\.", "").replaceAll("\\,", "");
            
            double d1 = Double.parseDouble(v1);
            double d2 = Double.parseDouble(v2);
            
            return Double.compare(d1, d2);
        } catch (Exception e) {
            return s1.compareTo(s2);
        }
    }
    
    
    //----------------------------------
    //======================[ BEGIN PHẦN LOAD SẢN PHẨM ]=========================
    //----------------------------------
    /**
     * Tìm kiếm sản phẩm theo tên hoặc mã
     * @param query Chuỗi tìm kiếm
     * @return Danh sách các sản phẩm phù hợp
     */
    public List<Product> searchProducts(String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return productRepository.findAll();
            }
            
            return productRepository.findByIdOrName(query);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public void loadAllProducts() {
        List<Product> allProducts = searchProducts("");  // Truyền chuỗi rỗng để lấy tất cả
        updateProductTable(sellForm.getTableListProduct(), allProducts);    // tblProducts là JTable hiển thị sản phẩm
    }

    /**
     * Cập nhật bảng danh sách sản phẩm
     * @param table Bảng cần cập nhật
     * @param products Danh sách sản phẩm cần hiển thị
     */
    public void updateProductTable(JTable table, List<Product> products) {
        if (table == null || products == null) {
            return;
        }
        
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        NumberFormat formatter = LocaleManager.getInstance().getNumberFormatter();
 
        for (Product product : products) {
            Object[] row = {
                product.getProductId(),
                product.getProductName(),
                product.getCategory() != null ? product.getCategory().getCategoryName() : "",
                product.getManufacturer() != null ? product.getManufacturer() : "N/A",
                product.getQuantityInStock(),
                formatter.format(product.getPrice())
         };
            model.addRow(row);
        }
    }

    //======================[ END PHẦN LOAD SẢN PHẨM ]=================================
    
    //----------------------------------
    //====================[ BEGIN PHẦN GIỎ HÀNG ]=====================================
    //----------------------------------
    
    /**
     * Cập nhật bảng giỏ hàng với các mục giỏ hàng hiện tại
     * @param table Bảng cần cập nhật
     */
    public void updateCartTable(JTable table) {
        if (table == null) {
            return;
        }
        
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);   
        
        try {
            NumberFormat formatter = LocaleManager.getInstance().getNumberFormatter();
 
            int index = 1;
            for (InvoiceDetail detail : cartItems) {
                Product product = detail.getProduct();
                Object[] row = {
                    Boolean.FALSE,
                    index++,
                    product.getProductId(),
                    product.getProductName(),
                    detail.getQuantity(),
                    formatter.format(detail.getUnitPrice()),
                    formatter.format(detail.getTotalAmount())   
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                ErrorMessage.CART_UPDATE_ERROR.formatted(e.getMessage()),
                ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }

    // xử lý sự kiện 
   // Phương thức để thêm sản phẩm đã chọn vào giỏ hàng
    public void addToCart() {
        int selectedRow = sellForm.getTableListProduct().getSelectedRow();
        if (selectedRow >= 0) {
            String productId = sellForm.getTableListProduct().getValueAt(selectedRow, 0).toString();

            // Hỏi số lượng
            Integer quantityStr = JDialogInputUtils.showInputDialogInt(sellForm, 
                ErrorMessage.ENTER_PRODUCT_QUANTITY, 
                "1");
                
            if (quantityStr != null) {
                try {
                    int quantity = quantityStr;
                    if (quantity > 0) {
                        boolean added = addProductToCart(productId, quantity);
                        if (added) {
                            sellForm.updateCartDisplay();
                        }
                    } else {
                        JOptionPane.showMessageDialog(sellForm,
                            ErrorMessage.QUANTITY_MUST_BE_POSITIVE,
                            ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(sellForm,
                        ErrorMessage.INVALID_QUANTITY,
                        ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                }
            } 
        } else {
            JOptionPane.showMessageDialog(sellForm,
                ErrorMessage.SELECT_PRODUCT_TO_ADD,
                ErrorMessage.INFO_TITLE, JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Thêm một sản phẩm vào giỏ hàng
     * @param productId ID sản phẩm cần thêm
     * @param quantity Số lượng cần thêm
     * @return True nếu thêm thành công, false nếu ngược lại
     */
    public boolean addProductToCart(String productId, int quantity) {

        if (productId == null || quantity <= 0) {
            return false;
        }
        
        try {
            Optional<Product> productOpt = productRepository.findById(productId);
            if (!productOpt.isPresent()) {
                return false;
            }
            
            Product product = productOpt.get();
            
            // Check if product is already in cart
            for (InvoiceDetail detail : cartItems) {
                if (detail.getProduct().getProductId().equals(productId)) {
                    // Update quantity instead of adding new
                    int newQuantity = detail.getQuantity() + quantity;
                    
                    // Check if we have enough stock
                    if (!product.hasEnoughStock(newQuantity)) {
                        JOptionPane.showMessageDialog(null, 
                            ErrorMessage.INSUFFICIENT_STOCK.formatted(product.getQuantityInStock()),
                            ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    
                    detail.setQuantity(newQuantity);
                    return true;
                }
            }
            
            // Product not in cart, add new
            if (!product.hasEnoughStock(quantity)) {
                JOptionPane.showMessageDialog(null, 
                    ErrorMessage.INSUFFICIENT_STOCK.formatted(product.getQuantityInStock()),
                    ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            InvoiceDetail newDetail = new InvoiceDetail();
            newDetail.setProduct(product);
            newDetail.setQuantity(quantity);
            newDetail.setUnitPrice(product.getPrice());
            // newDetail.setInvoice(currentInvoice);
            
            cartItems.add(newDetail);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa một sản phẩm khỏi giỏ hàng
     * @param productId ID sản phẩm cần xóa
     */
    public void removeProductFromCart(String productId) {

        if (productId == null || productId.trim().isEmpty()) {
            return;
        }
        
        for (int i = 0; i < cartItems.size(); i++) {
            InvoiceDetail detail = cartItems.get(i);
            if (detail.getProduct().getProductId().equals(productId)) {
                cartItems.remove(i);
                break;
            }
        }
    }

    // Xử lý sự kiện khi nhấn chuột vào bảng giỏ hàng
    public void processProductQuantityInCart(MouseEvent evt) {
        int row = sellForm.getTableCart().rowAtPoint(evt.getPoint());
        if (row >= 0) {
            String productId = sellForm.getTableCart().getValueAt(row, 2).toString();
            Boolean isSelected = (Boolean) sellForm.getTableCart().getValueAt(row, 0);
            if (isSelected != null && isSelected) {
                sellForm.getTableCart().setValueAt(Boolean.FALSE, row, 0);
                sellForm.getListSelectProductIDs().remove(productId);
            } else {
                sellForm.getTableCart().setValueAt(Boolean.TRUE, row, 0);
                sellForm.getListSelectProductIDs().add(productId);
            }
        }

        //Nếu click duoble thì sửa số lượng sản phẩm
        if (evt.getClickCount() == 2) {
            int selectedRow = sellForm.getTableCart().getSelectedRow();
            if (selectedRow >= 0) {
                String productId = sellForm.getTableCart().getValueAt(selectedRow, 2).toString();
                Integer quantity = JDialogInputUtils.showInputDialogInt(sellForm,
                    ErrorMessage.ENTER_PRODUCT_QUANTITY,
                    String.valueOf(sellForm.getTableCart().getValueAt(selectedRow, 4)));

                // Cập nhật số lượng trong controller
                boolean updated = updateProductQuantityInCart(productId, quantity);

                if (updated) {
                    sellForm.updateCartDisplay();
                } else {
                    JOptionPane.showMessageDialog(sellForm,
                        ErrorMessage.QUANTITY_UPDATE_ERROR,
                        ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);

                    sellForm.updateCartDisplay();
                }
            }
        }
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    public boolean updateProductQuantityInCart(String productId, int newQuantity) {
        // Kiểm tra số lượng tồn kho
        Optional<Product> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent() || newQuantity > productOpt.get().getQuantityInStock()) {
            return false;
        }
        
        // Cập nhật số lượng trong giỏ hàng
        for (InvoiceDetail detail : cartItems) {
            if (detail.getProduct().getProductId().equals(productId)) {
                detail.setQuantity(newQuantity);
                return true;
            }
        }
        
        return false;
    }
    
    //Xử lý sự kiện // Chức năng khi nhấn xóa sản phẩm trong giỏ hàng
    public void deleteItemCart (){ 
        if (sellForm.getListSelectProductIDs().isEmpty()) {
            JOptionPane.showMessageDialog(sellForm, 
                ErrorMessage.SELECT_PRODUCT_TO_DELETE, 
                ErrorMessage.INFO_TITLE, JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(sellForm, 
            ErrorMessage.CONFIRM_DELETE_PRODUCT, 
            ErrorMessage.CONFIRM_TITLE, JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            for (String productId : sellForm.getListSelectProductIDs()) {

                removeProductFromCart(productId);

                // listSelectProductIDs.remove(productId);
            }
            sellForm.getListSelectProductIDs().clear();
        }

        sellForm.updateCartDisplay();
    }

    /**
     * Xóa một sản phẩm khỏi giỏ hàng
     * @param cartIndex Chỉ số của sản phẩm trong giỏ hàng
     * @return True nếu xóa thành công, false nếu ngược lại
     */
    public boolean removeCartItem(int cartIndex) {
        if (currentInvoice == null || cartIndex < 0 || cartIndex >= cartItems.size()) {
            return false;
        }
        
        try {
            cartItems.remove(cartIndex);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa tất cả các mục trong giỏ hàng
     */
    public void clearCart() {
        cartItems.clear();
    }
    //=====================[ END PHẦN GIỎ HÀNG ]======================================
    
    /**
     * Tính tổng số tiền của giỏ hàng
     * @return Tổng số tiền
     */
    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceDetail detail : cartItems) {
            total = total.add(detail.getTotalAmount());
        }
        return total;
    }
    

    //======================[ BEGIN PHẦN XỬ LÝ ĐIỂM ĐỂ GIẢM GIÁ ]=======================
    // Xử lý sự kiện khi nhấn nút giảm giá bằng điểm
    public void processPointDiscount(){
        Customer customer = getCurrentInvoice().getCustomer();

        if (customer == null || "Khách vãng lai".equalsIgnoreCase(customer.getFullName())) {
            JOptionPane.showMessageDialog(sellForm, 
                ErrorMessage.CUSTOMER_SELECT_REQUIRED, 
                ErrorMessage.INFO_TITLE, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int points = customer.getPoints();
        if (points < 10000) {
            JOptionPane.showMessageDialog(sellForm, 
                ErrorMessage.CUSTOMER_POINTS_INSUFFICIENT, 
                ErrorMessage.INFO_TITLE, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(sellForm, 
            "Khách hàng có " + points + " điểm tích lũy.\nBạn có muốn sử dụng điểm để giảm giá không?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            BigDecimal discountAmount = applyPointsDiscount(true);
            
            if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
                sellForm.updateDiscountAmount(discountAmount);
            }
        }else{
            BigDecimal discountAmount = applyPointsDiscount(false);
            sellForm.updateDiscountAmount(discountAmount);

        }
    }

    /**
     * Áp dụng giảm giá cho giao dịch
     * @param discountCode Mã giảm giá
     * @return True nếu áp dụng thành công, false nếu ngược lại
     */
    public boolean applyDiscount(String discountCode) {
        if (currentInvoice == null || discountCode == null || discountCode.trim().isEmpty()) {
            return false;
        }
        
        try {
            Optional<Discount> discountOpt = discountRepository.findByCode(discountCode);
            if (!discountOpt.isPresent()) {
                return false;
            }
            
            Discount discount = discountOpt.get();
            if (!discount.isValid()) {
                return false;
            }
            
            // Apply discount to all applicable items
            for (InvoiceDetail detail : cartItems) {
                detail.applyDiscount(discount);
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //======================[ END PHẦN XỬ LÝ ĐIỂM ĐỂ GIẢM GIÁ ]=========================
    

    //-----------------------------
    //======================[ BEGIN PHẦN XỬ LÝ HÓA ĐƠN ]================================
    //-----------------------------

    // Xử lý khách hàng, nếu chưa có KH thì thêm vào
    private Customer insertCustomer(String FullNameCustomer, String PhoneNumberCustomer) {
        // Kiểm tra xem khách hàng đã tồn tại trong cơ sở dữ liệu chưa
        Customer existingCustomer = searchCustomerByPhone(PhoneNumberCustomer);
        if (existingCustomer != null) {
            return existingCustomer;
        }

        // Nếu chưa có khách hàng, thì tạo khách hàng mới nhé
        Customer customer = new Customer();
        if (FullNameCustomer.isEmpty() || FullNameCustomer.equalsIgnoreCase("Khách vãng lai")) {
            JOptionPane.showMessageDialog(sellForm, 
                ErrorMessage.CUSTOMER_NAME_REQUIRED,
                ErrorMessage.INFO_TITLE, JOptionPane.WARNING_MESSAGE);
            return null;
        }

        customer.setFullName(FullNameCustomer);
        customer.setPhoneNumber(PhoneNumberCustomer);
        customer.setPoints(0); // Hoặc lấy từ cơ sở dữ liệu nếu cần
        try {
            if (ServiceFactory.getCustomerService().customerExists(customer.getPhoneNumber())) {
                //Xác nhận thêm khách hàng
                int confirm = JOptionPane.showConfirmDialog(sellForm,
                    ErrorMessage.CUSTOMER_ADD_CONFIRM,
                    ErrorMessage.INFO_TITLE, JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Thêm khách hàng vào cơ sở dữ liệu TRƯỚC KHI thêm vào hóa đơn
                    Customer savedCustomer = ServiceFactory.getCustomerService().addCustomer(customer);
                    
                    JOptionPane.showMessageDialog(sellForm,
                        ErrorMessage.CUSTOMER_ADD_SUCCESS.formatted(savedCustomer.getFullName()),
                        ErrorMessage.INFO_TITLE, JOptionPane.INFORMATION_MESSAGE);
                }

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(sellForm,
                ErrorMessage.CUSTOMER_ADD_ERROR.formatted(e.getMessage()),
                ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
        return customer;
    }


    public void prepareInvoiceToPay() {
        if (getCartItems().isEmpty()) {
            JOptionPane.showMessageDialog(sellForm, 
                ErrorMessage.EMPTY_CART, 
                ErrorMessage.INFO_TITLE, JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            boolean checkUpdateInvoive = updateCurrentInvoice();
            if (!checkUpdateInvoive) {
                JOptionPane.showMessageDialog(sellForm, 
                    ErrorMessage.INVOICE_COMPLETE_ERROR, 
                    ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            //===================Lưu thông tin khách hàng=========================
            String FullNameCustomer = sellForm.getTxtNameKH().getText().trim();
            String PhoneNumberCustomer = sellForm.getTxtPhoneNumberKH().getText().trim();

            if(!PhoneNumberCustomer.isEmpty()) {
                Customer customer = insertCustomer(FullNameCustomer, PhoneNumberCustomer);
                if (customer == null) {
                    return; 
                }

                addCustomerToSale(customer);
            }
            //====================================================================
    
            // Xác nhận lưu hóa đơn
            int confirm = JOptionPane.showConfirmDialog(sellForm, 
                ErrorMessage.CONFIRM_SAVE_INVOICE, 
                ErrorMessage.CONFIRM_TITLE, JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return; 
            }
    
            // Lưu hóa đơn trước khi thanh toán 
            Invoice saveInvoice = saveInvoice(PaymentMethodEnum.CASH); //Mặc định là tiền mặt
            if (saveInvoice == null) {
                JOptionPane.showMessageDialog(sellForm, 
                    ErrorMessage.SAVE_INVOICE_ERROR, 
                    ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JOptionPane.showMessageDialog(sellForm, 
                ErrorMessage.SAVE_INVOICE_SUCCESS.formatted(saveInvoice.getInvoiceId()), 
                ErrorMessage.INFO_TITLE, JOptionPane.INFORMATION_MESSAGE);
            
            DashboardForm dashboard = DashboardForm.getInstance();
            PayForm payForm = new PayForm(dashboard, true);
            
            PaymentController paymentController = new PaymentController(payForm, saveInvoice);
            paymentController.showPaymentForm();
            
            if (paymentController.isPaymentSuccessful()) {
                
                saveInvoice.setPaymentMethod(paymentController.getCurrentPayment().getPaymentMethod());

                completeSale(saveInvoice);
                
                exportInvoiceToPDF(paymentController.getCurrentPayment());
    
                JOptionPane.showMessageDialog(sellForm, 
                    ErrorMessage.PAYMENT_SUCCESS, 
                    ErrorMessage.INFO_TITLE, JOptionPane.INFORMATION_MESSAGE);
                    
                // Reset form bán hàng
                sellForm.resetSaleForm();
            } else {
                JOptionPane.showMessageDialog(sellForm, 
                    ErrorMessage.PAYMENT_FAILED, 
                    ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(sellForm,
                ErrorMessage.UNKNOWN_ERROR.formatted(e.getMessage()),
                ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lưu hóa đơn khi nhấn nút thanh toán 
     * @param paymentMethod Phương thức thanh toán được sử dụng
     * @return Hóa đơn đã được lưu hoặc null nếu thất bại
     */
    public Invoice saveInvoice(PaymentMethodEnum paymentMethod) {
        if (currentInvoice == null || cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(null, 
                ErrorMessage.INVALID_INVOICE_SAVE,
                ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        try {
            // Cập nhật thông tin hóa đơn trước khi lưu
            currentInvoice.setInvoiceDate(LocalDateTime.now());
            // currentInvoice.setStatus(InvoiceStatusEnum.PROCESSING); // Trạng thái chờ xác nhận
            currentInvoice.setPaymentMethod(paymentMethod);
            currentInvoice.setTotalAmount(calculateTotalAfterDiscount());
            
            // Lưu hóa đơn vào CSDL
            Invoice savedInvoice = invoiceRepository.save(currentInvoice);
            if (savedInvoice == null) {
                JOptionPane.showMessageDialog(null, 
                    ErrorMessage.INVOICE_SAVE_ERROR,
                    ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                return null;
            }
        
            // Lưu tất cả các mục trong giỏ hàng 
            boolean detailsSaved = true;
            for (InvoiceDetail detail : cartItems) {
                detail.setInvoice(savedInvoice);
                if(detail.getId() != null) {
                    continue;
                }
                
                InvoiceDetail invoiceDetail = invoiceDetailRepository.add(detail);

                if (invoiceDetail == null) {
                    detailsSaved = false;
                    break;
                }    
                detail.setInvoiceDetailId(invoiceDetail.getInvoiceDetailId());
            }
            
            if (!detailsSaved) {
                JOptionPane.showMessageDialog(null, 
                    ErrorMessage.INVOICE_DETAIL_SAVE_ERROR,
                    ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                return null;
            }

            
            // Đặt lại trạng thái hiện tại
            this.currentInvoice = savedInvoice;
            this.currentInvoice.setInvoiceDetails(cartItems);
            
            return savedInvoice;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                ErrorMessage.UNKNOWN_ERROR.formatted(e.getMessage()),
                ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Hoàn thành giao dịch và lưu hóa đơn
     * @param paymentMethod Phương thức thanh toán được sử dụng
     * @return Hóa đơn đã hoàn thành hoặc null nếu thất bại
     */
    public Invoice completeSale(Invoice invoice) {
        if (invoice == null) {
            JOptionPane.showMessageDialog(null, 
                ErrorMessage.INVALID_INVOICE_COMPLETE,
                ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        try {
            // Cập nhật trạng thái hóa đơn
            invoice.setStatus(InvoiceStatusEnum.COMPLETED);
            invoice.setTotalAmount(calculateTotalAfterDiscount());
            
            // Lưu hóa đơn vào CSDL
            Invoice savedInvoice = invoiceRepository.save(invoice);
            if (savedInvoice == null) {
                JOptionPane.showMessageDialog(null, 
                    ErrorMessage.INVOICE_COMPLETE_ERROR,
                    ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            // Cập nhật điểm thưởng cho khách hàng
            updateCustomerPoints(savedInvoice);

            // Đặt lại trạng thái hiện tại
            this.currentInvoice = savedInvoice;
            this.currentInvoice.setInvoiceDetails(cartItems);
            
            return savedInvoice;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                ErrorMessage.UNKNOWN_ERROR.formatted(e.getMessage()),
                ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Hủy giao dịch hiện tại
     */
    public void cancelSale() {
        currentInvoice = null;
        cartItems.clear();
    }
    
    /**
     * Lấy danh sách các mục trong giỏ hàng
     * @return Danh sách các mục trong giỏ hàng
     */
    public List<InvoiceDetail> getCartItems() {
        return new ArrayList<>(cartItems);
    }
    
    /**
     * Lấy hóa đơn đang được xử lý
     * @return Hóa đơn hiện tại
     */
    public Invoice getCurrentInvoice() {
        return currentInvoice;
    }
    

     /**
     * Tìm kiếm khách hàng bằng số điện thoại
     * @param phoneNumber Số điện thoại của khách hàng
     * @return Khách hàng tìm thấy hoặc null
     */
    public Customer searchCustomerByPhone(String phoneNumber) {
        try {
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return null;
            }
            
            Optional<Customer> customerOpt = customerRepository.findByPhoneNumber(phoneNumber);
            return customerOpt.orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    /**
     * Thêm khách hàng vào giao dịch hiện tại
     * @param customer Khách hàng cần thêm
     * @return True nếu thêm thành công, false nếu ngược lại
     */
    public boolean addCustomerToSale(Customer customer) {
        if (currentInvoice == null || customer == null) {
            return false;
        }
        try {

            Customer existingCustomer = customerRepository.findById(customer.getCustomerId()).orElse(null);

            if (!customer.getFullName().equalsIgnoreCase("Khách vãng lai") && existingCustomer == null) {
                customer.setCustomerId(customerRepository.generateCustomerId());
            }
            currentInvoice.setCustomer(customer);
            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }


        /**
     * Áp dụng giảm giá dựa trên điểm thưởng của khách hàng
     * @param usePoints Có sử dụng điểm thưởng để giảm giá hay không
     * @return BigDecimal Số tiền được giảm
     */
    public BigDecimal applyPointsDiscount(boolean usePoints) {
        //nếu usepoints = false thì reset lại dùng giảm giá trong hóa đơn hiện tại
        if (!usePoints) {
            currentInvoice.setDiscountAmount(BigDecimal.ZERO);
            currentInvoice.setPointUsed(false);
            return BigDecimal.ZERO;
        }

        if (currentInvoice == null || !usePoints) {
            return BigDecimal.ZERO;
        }

        if(currentInvoice.isPointUsed()){
            JOptionPane.showMessageDialog(null, 
                ErrorMessage.POINTS_ALREADY_USED,
                ErrorMessage.INFO_TITLE, JOptionPane.INFORMATION_MESSAGE);
            return BigDecimal.ZERO;
        }
        
        //Trên 10k điểm mới được giảm giá
        Customer customer = currentInvoice.getCustomer();
        if (customer == null || customer.getPoints() < 10000) {
            return BigDecimal.ZERO;
        }
        
        try {
            // Tính toán số tiền giảm dựa trên điểm thưởng
            // Giả sử: 10,000 điểm = 100,000 VND giảm giá (10 điểm = 10VND)
            BigDecimal discountAmount = new BigDecimal(customer.getPoints()); // 1 VND cho mỗi điểm
            
            // Giới hạn số tiền giảm không vượt quá 50% tổng giá trị hóa đơn
            BigDecimal maxDiscount = calculateTotal().multiply(new BigDecimal("0.5"));
            if (discountAmount.compareTo(maxDiscount) > 0) {
                discountAmount = maxDiscount;
            }
            
            // Thiết lập giảm giá cho hóa đơn
            currentInvoice.setDiscountAmount(discountAmount);
            currentInvoice.setPointUsed(true);
            
            return discountAmount;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                ErrorMessage.UNKNOWN_ERROR.formatted(e.getMessage()),
                ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Tính tổng tiền sau khi áp dụng giảm giá
     * @return Tổng tiền sau khi giảm giá
     */
    public BigDecimal calculateTotalAfterDiscount() {
        BigDecimal total = calculateTotal();
        BigDecimal discountAmount = currentInvoice.getDiscountAmount() != null ? 
                                currentInvoice.getDiscountAmount() : BigDecimal.ZERO;
        
        return total.subtract(discountAmount);
    }

    /**
     * Cập nhật điểm thưởng của khách hàng sau khi thanh toán
     * @param invoice Hóa đơn đã thanh toán
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    private boolean updateCustomerPoints(Invoice invoice) {
        if (invoice == null || invoice.getCustomer() == null) {
            return false;
        }
        
        try {
            Customer customer = invoice.getCustomer();
            int currentPoints = customer.getPoints();
            
            // Trừ điểm đã sử dụng
            int pointsUsed = invoice.getDiscountAmount() != null ? invoice.getDiscountAmount().intValue() : 0;
            
            // Cộng điểm mới (1% giá trị hóa đơn)
            BigDecimal totalAmount = invoice.getTotalAmount();
            int newPoints = (int)(totalAmount.intValue() * LocaleManager.getPointRate());

            // Cập nhật tổng điểm
            customer.setPoints(currentPoints - pointsUsed + newPoints);
            
            // Lưu vào database
            customerRepository.save(customer);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCurrentInvoice() {
        if (currentInvoice == null) {
            return false;
        }
        
        try {
            currentInvoice.setCreatedAt(LocalDateTime.now());
            currentInvoice.setUpdatedAt(LocalDateTime.now());
            currentInvoice.setTotalAmount(calculateTotalAfterDiscount());
            currentInvoice.setInvoiceDetails(cartItems);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    public boolean setCurrentInvoice(Invoice invoice) {
        if (invoice == null) {
            return false;
        }
        
        try {
            currentInvoice = invoice;
            
            // Đảm bảo invoice có danh sách InvoiceDetails
            if (currentInvoice.getInvoiceDetails() == null) {
                currentInvoice.setInvoiceDetails(new ArrayList<>());
            }
            
            // Gán cartItems tham chiếu đến danh sách trong invoice
            this.cartItems = currentInvoice.getInvoiceDetails();
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Invoice createInvoice(Customer customer, Employee employee) {
        this.currentInvoice = new Invoice();
        currentInvoice.setCustomer(customer);
        currentInvoice.setEmployee(employee);
        currentInvoice.setInvoiceDate(LocalDateTime.now());
        currentInvoice.setStatus(InvoiceStatusEnum.PENDING);
        currentInvoice.setPaymentMethod(PaymentMethodEnum.CASH); // Mặc định là tiền mặt
        currentInvoice.setTotalAmount(calculateTotalAfterDiscount());

        // Thay đổi ở đây: thiết lập một danh sách mới trong Invoice
        if (currentInvoice.getInvoiceDetails() == null) {
            currentInvoice.setInvoiceDetails(new ArrayList<>());
        }
        
        // Gán địa chỉ của danh sách trong Invoice cho cartItems
        this.cartItems = currentInvoice.getInvoiceDetails();
        

        //Lấy id hóa đơn mới
        int invoiceId = invoiceRepository.generateInvoiceId();
        currentInvoice.setInvoiceId(invoiceId);
        this.cartItems.clear();
        return currentInvoice;
    }


    // Phương thức exportInvoiceToPDF
    public void exportInvoiceToPDF(BasePayment payment) {
        if (currentInvoice == null) {
            JOptionPane.showMessageDialog(null, 
                ErrorMessage.INVALID_INVOICE_COMPLETE,
                ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            boolean success = ExportInvoice.exportPDF(currentInvoice, payment);
            if (!success) {
                JOptionPane.showMessageDialog(null, 
                    ErrorMessage.EXPORT_INVOICE_FAILED,
                    ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                ErrorMessage.EXPORT_INVOICE_ERROR.formatted(e.getMessage()),
                ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }

}
