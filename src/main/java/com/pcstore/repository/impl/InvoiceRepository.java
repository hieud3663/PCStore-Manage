package com.pcstore.repository.impl;

import com.pcstore.repository.Repository;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.model.Customer;
import com.pcstore.model.Employee;
import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.enums.InvoiceStatusEnum;
import com.pcstore.model.enums.PaymentMethodEnum;

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

/**
 * Repository implementation cho Invoice entity
 */
public class InvoiceRepository implements Repository<Invoice, Integer> {
    private Connection connection;
    private RepositoryFactory RepositoryFactory;
    
    public InvoiceRepository(Connection connection, RepositoryFactory RepositoryFactory) {
        this.connection = connection;
        this.RepositoryFactory = RepositoryFactory;
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
            int statusId = 1; // Default: PENDING
            if (invoice.getStatus() != null) {
                switch (invoice.getStatus()) {
                    case PAID: statusId = 2; break;
                    case CANCELLED: statusId = 3; break;
                    case DELIVERED: statusId = 4; break;
                    case PROCESSING: statusId = 5; break;
                }
            }
            statement.setInt(5, statusId);
            
            // Chuyển đổi enum PaymentMethodID thành int
            int paymentMethodId = 1; // Default: CASH
            if (invoice.getPaymentMethod() != null) {
                switch (invoice.getPaymentMethod()) {
                    case CREDIT_CARD: paymentMethodId = 2; break;
                    case BANK_TRANSFER: paymentMethodId = 3; break;
                    case E_WALLET: paymentMethodId = 4; break;
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
            
            // Lưu các chi tiết hóa đơn nếu có
            if (invoice.getInvoiceDetails() != null && !invoice.getInvoiceDetails().isEmpty()) {
                for (InvoiceDetail detail : invoice.getInvoiceDetails()) {
                    detail.setInvoice(invoice);
                    RepositoryFactory.getInvoiceDetailRepository().add(detail);
                }
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
            int statusId = 1; // Default: PENDING
            if (invoice.getStatus() != null) {
                switch (invoice.getStatus()) {
                    case PAID: statusId = 2; break;
                    case CANCELLED: statusId = 3; break;
                    case DELIVERED: statusId = 4; break;
                    case PROCESSING: statusId = 5; break;
                }
            }
            statement.setInt(5, statusId);
            
            // Chuyển đổi enum PaymentMethodID thành int
            int paymentMethodId = 1; // Default: CASH
            if (invoice.getPaymentMethod() != null) {
                switch (invoice.getPaymentMethod()) {
                    case CREDIT_CARD: paymentMethodId = 2; break;
                    case BANK_TRANSFER: paymentMethodId = 3; break;
                    case E_WALLET: paymentMethodId = 4; break;
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
                
                // Load các chi tiết hóa đơn
                invoice.setInvoiceDetails(RepositoryFactory.getInvoiceDetailRepository().findByInvoiceId(id));
                
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
    
    // Tìm hóa đơn theo khách hàng
    public List<Invoice> findByCustomerId(String customerId) {
        String sql = "SELECT i.*, c.FullName as CustomerName, e.FullName as EmployeeName " +
                     "FROM Invoices i " +
                     "LEFT JOIN Customers c ON i.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON i.EmployeeID = e.EmployeeID " +
                     "WHERE i.CustomerID = ? " +
                     "ORDER BY i.InvoiceDate DESC";
        List<Invoice> invoices = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Invoice invoice = mapResultSetToInvoice(resultSet);
                invoices.add(invoice);
            }
            
            return invoices;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding invoices by customer", e);
        }
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
    public Invoice loadInvoiceDetails(Invoice invoice) {
        invoice.setInvoiceDetails(RepositoryFactory.getInvoiceDetailRepository().findByInvoiceId(invoice.getInvoiceId()));
        return invoice;
    }
    
    private Invoice mapResultSetToInvoice(ResultSet resultSet) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(resultSet.getInt("InvoiceID"));
        invoice.setTotalAmount(resultSet.getBigDecimal("TotalAmount"));
        invoice.setInvoiceDate(resultSet.getObject("InvoiceDate", LocalDateTime.class));
        
        // Map status from ID to enum
        int statusId = resultSet.getInt("StatusID");
        switch (statusId) {
            case 1: invoice.setStatus(InvoiceStatusEnum.PENDING); break;
            case 2: invoice.setStatus(InvoiceStatusEnum.PAID); break;
            case 3: invoice.setStatus(InvoiceStatusEnum.CANCELLED); break;
            case 4: invoice.setStatus(InvoiceStatusEnum.DELIVERED); break;
            case 5: invoice.setStatus(InvoiceStatusEnum.PROCESSING); break;
            default: invoice.setStatus(InvoiceStatusEnum.PENDING);
        }
        
        // Map payment method from ID to enum
        int paymentMethodId = resultSet.getInt("PaymentMethodID");
        switch (paymentMethodId) {
            case 1: invoice.setPaymentMethod(PaymentMethodEnum.CASH); break;
            case 2: invoice.setPaymentMethod(PaymentMethodEnum.CREDIT_CARD); break;
            case 3: invoice.setPaymentMethod(PaymentMethodEnum.BANK_TRANSFER); break;
            case 4: invoice.setPaymentMethod(PaymentMethodEnum.E_WALLET); break;
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