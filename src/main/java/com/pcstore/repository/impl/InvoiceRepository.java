package com.pcstore.repository.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pcstore.model.Customer;
import com.pcstore.model.Employee;
import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Product;
import com.pcstore.model.enums.InvoiceStatusEnum;
import com.pcstore.model.enums.PaymentMethodEnum;
import com.pcstore.repository.Repository;
import com.pcstore.repository.RepositoryFactory;

/**
 * Repository implementation cho Invoice entity
 */
public class InvoiceRepository implements Repository<Invoice, Integer> {
    private Connection connection;
    private RepositoryFactory RepositoryFactory;
    private static final Logger logger = Logger.getLogger(InvoiceRepository.class.getName());
    
    public InvoiceRepository(Connection connection, RepositoryFactory RepositoryFactory) {
        this.connection = connection;
        this.RepositoryFactory = RepositoryFactory;
    }
    
    //Tạo id tự động cho hóa đơn
    public int generateInvoiceId() {
        String sql = "SELECT MAX(InvoiceID) AS MaxID FROM Invoices";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
             
            if (resultSet.next()) {
                return resultSet.getInt("MaxID") + 1;
            }
            return 1; // Nếu không có hóa đơn nào, bắt đầu từ 1
        } catch (SQLException e) {
            throw new RuntimeException("Error generating invoice ID", e);
        }
    }


    public Invoice save(Invoice invoice){
        Invoice existInvoice = findById(invoice.getInvoiceId()).orElse(null);
        if (existInvoice == null) {
            return add(invoice);
        } else {
            return update(invoice);
        }
    }


    @Override
    public Invoice add(Invoice invoice) {
        String sql = "INSERT INTO Invoices (CustomerID, EmployeeID, TotalAmount, InvoiceDate, StatusID, PaymentMethodID) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, invoice.getCustomer() != null ? (String) invoice.getCustomer().getId() : null);
            statement.setString(2, invoice.getEmployee() != null ? (String) invoice.getEmployee().getId() : null);
            statement.setBigDecimal(3, invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO);
            
            LocalDateTime now = LocalDateTime.now();
            invoice.setCreatedAt(now);
            invoice.setUpdatedAt(now);
            
            // Nếu ngày hóa đơn chưa được thiết lập, sử dụng thời gian hiện tại
            if (invoice.getInvoiceDate() == null) {
                invoice.setInvoiceDate(now);
            }
            statement.setObject(4, invoice.getInvoiceDate());
            
            // Chuyển đổi enum StatusID thành int
            int statusId = 0; // Default: Processing
            if (invoice.getStatus() != null) {
                switch (invoice.getStatus()) {
                    case PAID: statusId = 1; break;
                    case CANCELLED: statusId = 4; break;
                    case DELIVERED: statusId = 5; break;
                    case PROCESSING: statusId = 0; break;
                    case COMPLETED: statusId = 3; break;
                }
            }
            statement.setInt(5, statusId);
            
            // Chuyển đổi enum PaymentMethodID thành int
            int paymentMethodId = 1; // Default: CASH
            if (invoice.getPaymentMethod() != null) {
                switch (invoice.getPaymentMethod()) {
                    case CASH: paymentMethodId = 1; break;
                    case ZALOPAY: paymentMethodId = 3; break;
                    case MOMO: paymentMethodId = 2; break;
                    case BANK_TRANSFER: paymentMethodId = 4; break;
                    case CREDIT_CARD: paymentMethodId = 5; break;
                }
            }
            statement.setInt(6, paymentMethodId);
            
            statement.executeUpdate();
            
            // Lấy ID được tự động tạo
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                invoice.setInvoiceId(generatedId);
            }
            
            return invoice;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding invoice", e);
        }
    }
    
    @Override
    public Invoice update(Invoice invoice) {
        String sql = "UPDATE Invoices SET CustomerID = ?, EmployeeID = ?, TotalAmount = ?, " +
                     "InvoiceDate = ?, StatusID = ?, PaymentMethodID = ? WHERE InvoiceID = ?";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, invoice.getCustomer() != null ? (String) invoice.getCustomer().getId() : null);
            statement.setString(2, invoice.getEmployee() != null ? (String) invoice.getEmployee().getId() : null);
            statement.setBigDecimal(3, invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO);
            statement.setObject(4, invoice.getInvoiceDate());
            
            // Chuyển đổi enum StatusID thành int
            int statusId = 0; // Default: Processing
            if (invoice.getStatus() != null) {
                switch (invoice.getStatus()) {
                    case PAID: statusId = 1; break;
                    case CANCELLED: statusId = 4; break;
                    case DELIVERED: statusId = 5; break;
                    case PROCESSING: statusId = 0; break;
                    case COMPLETED: statusId = 3; break;
                }
            }
            statement.setInt(5, statusId);
            
            // Chuyển đổi enum PaymentMethodID thành int
            int paymentMethodId = 1; // Default: CASH
            if (invoice.getPaymentMethod() != null) {
                switch (invoice.getPaymentMethod()) {
                    case CASH: paymentMethodId = 1; break;
                    case ZALOPAY: paymentMethodId = 3; break;
                    case MOMO: paymentMethodId = 2; break;
                    case BANK_TRANSFER: paymentMethodId = 4; break;
                    case CREDIT_CARD: paymentMethodId = 5; break;
                }
            }
            statement.setInt(6, paymentMethodId);
            
            statement.setInt(7, invoice.getInvoiceId());
            
            statement.executeUpdate();
            
            invoice.setUpdatedAt(LocalDateTime.now());
            return invoice;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating invoice", e);
        }
    }
    
    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Invoices WHERE InvoiceID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting invoice", e);
        }
    }
    
    @Override
    public Optional<Invoice> findById(Integer id) {
        String sql = "SELECT i.*, c.FullName as CustomerName, e.FullName as EmployeeName " +
                     "FROM Invoices i " +
                     "LEFT JOIN Customers c ON i.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON i.EmployeeID = e.EmployeeID " +
                     "WHERE i.InvoiceID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                Invoice invoice = mapResultSetToInvoice(resultSet);
                
                // // Load các chi tiết hóa đơn
                // invoice.setInvoiceDetails(RepositoryFactory.getInvoiceDetailRepository().findByInvoiceId(id));
                
                return Optional.of(invoice);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding invoice by ID", e);
        }
    }
    
    @Override
    public List<Invoice> findAll() {
        String sql = "SELECT i.*, c.FullName as CustomerName, e.FullName as EmployeeName " +
                     "FROM Invoices i " +
                     "LEFT JOIN Customers c ON i.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON i.EmployeeID = e.EmployeeID " +
                     "ORDER BY i.InvoiceDate DESC";
        List<Invoice> invoices = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
             
            while (resultSet.next()) {
                Invoice invoice = mapResultSetToInvoice(resultSet);
                invoices.add(invoice);
            }
            
            return invoices;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all invoices", e);
        }
    }
    
    @Override
    public boolean exists(Integer id) {
        String sql = "SELECT COUNT(*) FROM Invoices WHERE InvoiceID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if invoice exists", e);
        }
    }
    
    /**
     * Tìm hóa đơn theo mã khách hàng
     * @param customerId Mã khách hàng
     * @return Danh sách hóa đơn
     */
    public List<Invoice> findByCustomerId(String customerId) {
        String sql = "SELECT i.*, c.FullName as CustomerName, c.PhoneNumber as CustomerPhone, " +
                     "s.StatusName as StatusName, p.MethodName as PaymentMethodName " +
                     "FROM Invoices i " +
                     "LEFT JOIN Customers c ON i.CustomerID = c.CustomerID " +
                     "LEFT JOIN InvoiceStatus s ON i.StatusID = s.StatusID " +
                     "LEFT JOIN PaymentMethods p ON i.PaymentMethodID = p.PaymentMethodID " +
                     "WHERE i.CustomerID = ? " +
                     "ORDER BY i.InvoiceDate DESC";
        
        List<Invoice> invoices = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Invoice invoice = mapResultSetToInvoice(rs);
                    invoices.add(invoice);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding invoices by customer ID: " + customerId, e);
            throw new RuntimeException("Database error when finding invoices by customer", e);
        }
        
        return invoices;
    }
    
    /**
     * Tìm hóa đơn theo ID khách hàng - dùng riêng cho chức năng bảo hành
     * @param customerId ID của khách hàng
     * @return Danh sách các hóa đơn của khách hàng đó
     */
    public List<Invoice> findByCustomerIdWarranty(String customerId) {
        List<Invoice> invoices = new ArrayList<>();
        
        String sql = "SELECT I.InvoiceID, I.CustomerID, I.InvoiceDate, I.TotalAmount, " +
                     "C.FullName, C.PhoneNumber, C.Email, C.Address " +
                     "FROM Invoices I " +
                     "JOIN Customers C ON I.CustomerID = C.CustomerID " +
                     "WHERE I.CustomerID = ? " +
                     "ORDER BY I.InvoiceDate DESC";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerId);
            
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Invoice invoice = new Invoice();
                    invoice.setInvoiceId(rs.getInt("InvoiceID"));
                    
                    // Tạo đối tượng Customer với thông tin đầy đủ
                    Customer customer = new Customer();
                    customer.setCustomerId(customerId);
                    customer.setFullName(rs.getString("FullName"));
                    customer.setPhoneNumber(rs.getString("PhoneNumber"));
                    customer.setEmail(rs.getString("Email"));
                    customer.setAddress(rs.getString("Address"));
                    invoice.setCustomer(customer);
                    
                    if (rs.getTimestamp("InvoiceDate") != null) {
                        invoice.setInvoiceDate(rs.getTimestamp("InvoiceDate").toLocalDateTime());
                    }
                    
                    invoice.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                    
                    // Tải chi tiết hóa đơn
                    try {
                        List<InvoiceDetail> details = loadInvoiceDetails(invoice.getInvoiceId());
                        invoice.setInvoiceDetails(details);
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Error loading invoice details for invoice " + invoice.getInvoiceId(), e);
                    }
                    
                    invoices.add(invoice);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding invoices for customer " + customerId, e);
            throw new RuntimeException("Error finding invoices for customer " + customerId, e);
        }
        
        return invoices;
    }
    
    // Tìm hóa đơn theo nhân viên
    public List<Invoice> findByEmployeeId(String employeeId) {
        String sql = "SELECT i.*, c.FullName as CustomerName, e.FullName as EmployeeName " +
                     "FROM Invoices i " +
                     "LEFT JOIN Customers c ON i.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON i.EmployeeID = e.EmployeeID " +
                     "WHERE i.EmployeeID = ? " +
                     "ORDER BY i.InvoiceDate DESC";
        List<Invoice> invoices = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employeeId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Invoice invoice = mapResultSetToInvoice(resultSet);
                invoices.add(invoice);
            }
            
            return invoices;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding invoices by employee", e);
        }
    }
    
    // Tìm hóa đơn theo trạng thái
    public List<Invoice> findByStatus(InvoiceStatusEnum status) {
        int statusId = 1; // Default: PENDING
        if (status != null) {
            switch (status) {
                case PAID: statusId = 2; break;
                case CANCELLED: statusId = 3; break;
                case DELIVERED: statusId = 4; break;
                case PROCESSING: statusId = 5; break;
            }
        }
        
        String sql = "SELECT i.*, c.FullName as CustomerName, e.FullName as EmployeeName " +
                     "FROM Invoices i " +
                     "LEFT JOIN Customers c ON i.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON i.EmployeeID = e.EmployeeID " +
                     "WHERE i.StatusID = ? " +
                     "ORDER BY i.InvoiceDate DESC";
        List<Invoice> invoices = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, statusId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Invoice invoice = mapResultSetToInvoice(resultSet);
                invoices.add(invoice);
            }
            
            return invoices;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding invoices by status", e);
        }
    }
    
    // Tìm hóa đơn trong khoảng thời gian
    public List<Invoice> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT i.*, c.FullName as CustomerName, e.FullName as EmployeeName " +
                     "FROM Invoices i " +
                     "LEFT JOIN Customers c ON i.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON i.EmployeeID = e.EmployeeID " +
                     "WHERE i.InvoiceDate BETWEEN ? AND ? " +
                     "ORDER BY i.InvoiceDate DESC";
        List<Invoice> invoices = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, startDate);
            statement.setObject(2, endDate);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Invoice invoice = mapResultSetToInvoice(resultSet);
                invoices.add(invoice);
            }
            
            return invoices;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding invoices by date range", e);
        }
    }
    
    // Tính tổng doanh thu trong khoảng thời gian
    public BigDecimal calculateRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT SUM(TotalAmount) AS TotalRevenue FROM Invoices " +
                     "WHERE StatusID = 2 AND InvoiceDate BETWEEN ? AND ?"; // Chỉ tính hóa đơn đã thanh toán
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, startDate);
            statement.setObject(2, endDate);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                BigDecimal revenue = resultSet.getBigDecimal("TotalRevenue");
                return revenue != null ? revenue : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating revenue", e);
        }
    }
    
    // Load tất cả chi tiết của hóa đơn
    public List<InvoiceDetail> loadInvoiceDetails(int invoiceId) {
        List<InvoiceDetail> details = new ArrayList<>();
        
        String sql = "SELECT ID.InvoiceDetailID, ID.InvoiceID, ID.ProductID, ID.Quantity, ID.UnitPrice, "
                   + "P.ProductName, P.Description AS ProductDescription, "
                   + "I.InvoiceDate, I.CustomerID, "
                   + "C.FullName AS CustomerName, C.PhoneNumber AS CustomerPhone "
                   + "FROM InvoiceDetails ID "
                   + "LEFT JOIN Products P ON ID.ProductID = P.ProductID "
                   + "JOIN Invoices I ON ID.InvoiceID = I.InvoiceID "
                   + "LEFT JOIN Customers C ON I.CustomerID = C.CustomerID "
                   + "WHERE ID.InvoiceID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, invoiceId);
            
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    InvoiceDetail detail = new InvoiceDetail();
                    detail.setInvoiceDetailId(rs.getInt("InvoiceDetailID"));
                    detail.setQuantity(rs.getInt("Quantity"));
                    detail.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                    
                    // Tạo đối tượng Invoice với thông tin đầy đủ
                    Invoice invoice = new Invoice();
                    invoice.setInvoiceId(invoiceId);
                    
                    if (rs.getTimestamp("InvoiceDate") != null) {
                        invoice.setInvoiceDate(rs.getTimestamp("InvoiceDate").toLocalDateTime());
                    }
                    
                    // Tạo đối tượng Customer với thông tin đầy đủ
                    String customerId = rs.getString("CustomerID");
                    if (customerId != null) {
                        Customer customer = new Customer();
                        customer.setCustomerId(customerId);
                        customer.setFullName(rs.getString("CustomerName"));
                        customer.setPhoneNumber(rs.getString("CustomerPhone"));
                        invoice.setCustomer(customer);
                    }
                    
                    detail.setInvoice(invoice);
                    
                    // Tạo đối tượng Product
                    Product product = new Product();
                    product.setProductId(rs.getString("ProductID"));
                    product.setProductName(rs.getString("ProductName"));
                    product.setDescription(rs.getString("ProductDescription"));
                    
                    detail.setProduct(product);
                    
                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading invoice details for invoice " + invoiceId, e);
        }
        
        return details;
    }
    
    /**
     * Tìm tất cả hóa đơn có thể trả hàng
     * @return Danh sách các hóa đơn có thể trả hàng
     */
    public List<Invoice> findAllInvoicesForReturn() {
        // Lấy hóa đơn đã thanh toán và còn trong thời gian cho phép trả hàng (ví dụ: 30 ngày)
        String sql = "SELECT i.*, c.FullName as CustomerName, c.PhoneNumber as CustomerPhone, " +
                     "e.FullName as EmployeeName, " +
                     "s.StatusName as StatusName, p.MethodName as PaymentMethodName " +
                     "FROM Invoices i " +
                     "LEFT JOIN Customers c ON i.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON i.EmployeeID = e.EmployeeID " +
                     "LEFT JOIN InvoiceStatus s ON i.StatusID = s.StatusID " +
                     "LEFT JOIN PaymentMethods p ON i.PaymentMethodID = p.PaymentMethodID " +
                     "WHERE i.StatusID = 2 " + // Giả sử 2 là mã trạng thái "Đã thanh toán"
                    //  "AND i.InvoiceDate >= DATEADD(day, -30, GETDATE()) " + // Chỉ lấy hóa đơn trong vòng 30 ngày
                     "ORDER BY i.InvoiceDate DESC";
        
        List<Invoice> invoices = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
             
            while (resultSet.next()) {
                try {
                    Invoice invoice = mapResultSetToInvoice(resultSet);
                    
                    // Thêm thông tin khách hàng vào hóa đơn
                    String customerID = resultSet.getString("CustomerID");
                    if (customerID != null) {
                        Customer customer = new Customer();
                        customer.setCustomerId(customerID); // Sửa từ setId thành setCustomerId
                        customer.setFullName(resultSet.getString("CustomerName"));
                        customer.setPhoneNumber(resultSet.getString("CustomerPhone"));
                        invoice.setCustomer(customer);
                    }

                    // Thêm thông tin nhân viên vào hóa đơn
                    String employeeID = resultSet.getString("EmployeeID");
                    if (employeeID != null) {
                        Employee employee = new Employee();
                        employee.setEmployeeId(employeeID); // Sửa từ setId thành setEmployeeId
                        employee.setFullName(resultSet.getString("EmployeeName"));
                        invoice.setEmployee(employee);
                    }
                    
                    invoices.add(invoice);
                } catch (Exception e) {
                    System.err.println("Lỗi khi xử lý hóa đơn từ ResultSet: " + e.getMessage());
                    // Tiếp tục với hóa đơn tiếp theo
                }
            }
            
            return invoices;
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm hóa đơn cho trả hàng: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm hóa đơn cho trả hàng", e);
        }
    }
    
    private Invoice mapResultSetToInvoice(ResultSet resultSet) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(resultSet.getInt("InvoiceID"));
        invoice.setTotalAmount(resultSet.getBigDecimal("TotalAmount"));
        invoice.setInvoiceDate(resultSet.getObject("InvoiceDate", LocalDateTime.class));
        invoice.setDiscountAmount(resultSet.getBigDecimal("DiscountAmount"));
        invoice.setCreatedAt(resultSet.getObject("InvoiceDate", LocalDateTime.class));
        
        // Map status from ID to enum
        int statusId = resultSet.getInt("StatusID");
        switch (statusId) {
            case 0: invoice.setStatus(InvoiceStatusEnum.PENDING); break;
            case 1: invoice.setStatus(InvoiceStatusEnum.CONFIRMED); break;
            case 2: invoice.setStatus(InvoiceStatusEnum.DELIVERED); break;
            case 3: invoice.setStatus(InvoiceStatusEnum.COMPLETED); break;
            case 4: invoice.setStatus(InvoiceStatusEnum.CANCELLED); break;
            case 5: invoice.setStatus(InvoiceStatusEnum.PROCESSING); break;
            default: invoice.setStatus(InvoiceStatusEnum.PENDING);
        }
        
        // Map payment method from ID to enum
        int paymentMethodId = resultSet.getInt("PaymentMethodID");
        switch (paymentMethodId) {
            case 1: invoice.setPaymentMethod(PaymentMethodEnum.CASH); break;
            case 2: invoice.setPaymentMethod(PaymentMethodEnum.MOMO); break;
            case 3: invoice.setPaymentMethod(PaymentMethodEnum.ZALOPAY); break;
            case 4: invoice.setPaymentMethod(PaymentMethodEnum.BANK_TRANSFER); break;
            case 5: invoice.setPaymentMethod(PaymentMethodEnum.CREDIT_CARD); break;
            default: invoice.setPaymentMethod(PaymentMethodEnum.CASH);
        }
        
        // Tạo và thiết lập thông tin khách hàng
        String customerId = resultSet.getString("CustomerID");
        if (customerId != null) {
            Customer customer = new Customer();
            customer.setCustomerId(customerId);
            customer.setFullName(resultSet.getString("CustomerName"));
            invoice.setCustomer(customer);
        }
        
        // Tạo và thiết lập thông tin nhân viên
        String employeeId = resultSet.getString("EmployeeID");
        if (employeeId != null) {
            Employee employee = new Employee();
            employee.setEmployeeId(employeeId);
            employee.setFullName(resultSet.getString("EmployeeName"));
            invoice.setEmployee(employee);
        }
        
        return invoice;
    
}
    
}

