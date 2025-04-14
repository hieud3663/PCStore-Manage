package com.pcstore.controller;

import com.pcstore.model.Customer;
import com.pcstore.model.Employee;
import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Product;
import com.pcstore.model.Discount;
import com.pcstore.model.enums.InvoiceStatusEnum;
import com.pcstore.model.enums.PaymentMethodEnum;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.repository.impl.CustomerRepository;
import com.pcstore.repository.impl.EmployeeRepository;
import com.pcstore.repository.impl.InvoiceDetailRepository;
import com.pcstore.repository.impl.InvoiceRepository;
import com.pcstore.repository.impl.ProductRepository;
import com.pcstore.repository.impl.DiscountRepository;
import com.pcstore.service.InvoiceService;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.SessionManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.text.NumberFormat;

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
    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final EmployeeRepository employeeRepository;
    private final DiscountRepository discountRepository;
    private final InvoiceService invoiceService;
    
    private Invoice currentInvoice;
    private List<InvoiceDetail> cartItems;
    
    /**
     * Constructor khởi tạo các repositories và services
     * @param connection Kết nối cơ sở dữ liệu
     * @param repositoryFactory Factory để tạo repositories
     */
    public SellController(Connection connection, RepositoryFactory repositoryFactory) {
        this.invoiceRepository = new InvoiceRepository(connection, repositoryFactory);
        this.invoiceDetailRepository = new InvoiceDetailRepository(connection, repositoryFactory);
        this.customerRepository = new CustomerRepository(connection);
        this.productRepository = new ProductRepository(connection);
        this.employeeRepository = new EmployeeRepository(connection);
        this.discountRepository = new DiscountRepository(connection);
        this.invoiceService = new InvoiceService(invoiceRepository, productRepository);
        
        this.cartItems = new ArrayList<>();
        
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
                    "Không tìm thấy khách hàng mặc định. Vui lòng kiểm tra lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            createInvoice(defaultCustomer, employee);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
   
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
                "Lỗi khi cập nhật bảng giỏ hàng: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
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
                product.getSupplier() != null ? product.getSupplier().getName() : "",
                product.getQuantityInStock(),
                formatter.format(product.getPrice())
         };
            model.addRow(row);
        }
    }


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
                            "Không đủ số lượng tồn kho. Chỉ còn " + product.getQuantityInStock() + " sản phẩm.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    
                    detail.setQuantity(newQuantity);
                    return true;
                }
            }
            
            // Product not in cart, add new
            if (!product.hasEnoughStock(quantity)) {
                JOptionPane.showMessageDialog(null, 
                    "Không đủ số lượng tồn kho. Chỉ còn " + product.getQuantityInStock() + " sản phẩm.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
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
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     * @param cartIndex Chỉ số của sản phẩm trong giỏ hàng
     * @param newQuantity Số lượng mới
     * @return True nếu cập nhật thành công, false nếu ngược lại
     */
    public boolean updateCartItemQuantity(int cartIndex, int newQuantity) {
        if (currentInvoice == null || cartIndex < 0 || cartIndex >= cartItems.size() || newQuantity <= 0) {
            return false;
        }
        
        try {
            InvoiceDetail detail = cartItems.get(cartIndex);
            Product product = detail.getProduct();
            
            // Check if we have enough stock
            if (!product.hasEnoughStock(newQuantity)) {
                JOptionPane.showMessageDialog(null, 
                    "Không đủ số lượng tồn kho. Chỉ còn " + product.getQuantityInStock() + " sản phẩm.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            detail.setQuantity(newQuantity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
    

    /**
     * Lưu hóa đơn khi nhấn nút thanh toán 
     * @param paymentMethod Phương thức thanh toán được sử dụng
     * @return Hóa đơn đã được lưu hoặc null nếu thất bại
     */
    public Invoice saveInvoice(PaymentMethodEnum paymentMethod) {
        if (currentInvoice == null || cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(null, 
                "Không thể lưu hóa đơn. Giỏ hàng trống hoặc thông tin hóa đơn không hợp lệ.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        try {
            // Cập nhật thông tin hóa đơn trước khi lưu
            currentInvoice.setInvoiceDate(LocalDateTime.now());
            // currentInvoice.setStatus(InvoiceStatusEnum.PROCESSING); // Trạng thái chờ xác nhận
            currentInvoice.setPaymentMethod(paymentMethod);
            currentInvoice.setTotalAmount(calculateTotal());
            
            // Lưu hóa đơn vào CSDL
            Invoice savedInvoice = invoiceRepository.save(currentInvoice);
            if (savedInvoice == null) {
                JOptionPane.showMessageDialog(null, 
                    "Lỗi khi lưu hóa đơn. Vui lòng thử lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
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
                    "Lỗi khi lưu chi tiết hóa đơn. Vui lòng thử lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            // Đặt lại trạng thái hiện tại
            this.currentInvoice = savedInvoice;
            return savedInvoice;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Lỗi không xác định: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
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
                "Không thể hoàn thành giao dịch. Hóa đơn không hợp lệ.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        try {
            // Cập nhật trạng thái hóa đơn
            invoice.setStatus(InvoiceStatusEnum.COMPLETED);
            invoice.setTotalAmount(calculateTotal());
            
            // Lưu hóa đơn vào CSDL
            Invoice savedInvoice = invoiceRepository.save(invoice);
            if (savedInvoice == null) {
                JOptionPane.showMessageDialog(null, 
                    "Lỗi khi hoàn thành giao dịch. Vui lòng thử lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            // Đặt lại trạng thái hiện tại
            this.currentInvoice = savedInvoice;
            this.cartItems.clear();
            return savedInvoice;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Lỗi không xác định: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
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


    public boolean updateCurrentInvoice() {
        if (currentInvoice == null) {
            return false;
        }
        
        try {
            currentInvoice.setCreatedAt(LocalDateTime.now());
            currentInvoice.setUpdatedAt(LocalDateTime.now());
            currentInvoice.setTotalAmount(calculateTotal());
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
        currentInvoice.setInvoiceDetails(cartItems);
        currentInvoice.setTotalAmount(calculateTotal());

        //Lấy id hóa đơn mới
        int invoiceId = invoiceRepository.generateInvoiceId();
        currentInvoice.setInvoiceId(invoiceId);

        return currentInvoice;
    }
}
