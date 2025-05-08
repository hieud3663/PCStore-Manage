package com.pcstore.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.pcstore.model.Product;
import com.pcstore.repository.impl.ProductRepository;
import com.pcstore.utils.DatabaseConnection;
import com.pcstore.utils.TableStyleUtil;
import com.pcstore.view.DashboardForm;
import com.pcstore.view.PurchaseOrderForm;
import com.pcstore.view.StockInHistoryForm;
import com.pcstore.view.WareHouseForm;
import com.pcstore.view.WareHouseForm.EditableTableModel;

/**
 * Controller cho màn hình quản lý kho hàng
 */
public class WareHouseController {
    private WareHouseForm wareHouseForm;
    private ProductRepository productRepository;
    private Connection connection;

    /**
     * Khởi tạo controller với form quản lý kho hàng
     * @param wareHouseForm Form quản lý kho hàng
     */
    public WareHouseController(WareHouseForm wareHouseForm) {
        this.wareHouseForm = wareHouseForm;
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.productRepository = new ProductRepository(connection);
        
        // Đăng ký các sự kiện cho form
        registerEvents();
        initTable();
        // Tải dữ liệu sản phẩm
        loadProducts();

    }

    /**
     * Đăng ký các sự kiện cho form
     */
    private void registerEvents() {
        // Nút tạo đơn đặt hàng
        wareHouseForm.getBtnCreatePurchaseOrder().addActionListener(e -> createPurchaseOrder());
        
        // Nút xem lịch sử nhập kho
        wareHouseForm.getBtnHistoryStockIn().addActionListener(e -> viewStockInHistory());
        
        // Nút tìm kiếm (nếu có)
        if (wareHouseForm.getTextFieldSearch() != null) {
            wareHouseForm.getTextFieldSearch().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    searchProducts();
                }

                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    searchProducts();
                }

                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    searchProducts();
                }
            });
        }
        
    }
    
    
    /**
     * Tải dữ liệu sản phẩm vào bảng
     */
    public void loadProducts() {
        try {
            List<Product> products = productRepository.findAll();
            
            // Sử dụng DefaultTableModel thông thường thay vì EditableTableModel
            DefaultTableModel model = (DefaultTableModel) wareHouseForm.getTable().getModel();
            model.setRowCount(0); // Xóa tất cả các dòng hiện tại
            
            int stt = 1;
            for (Product product : products) {
                // Kiểm tra số lượng dòng và cột trong model
                System.out.println("Columns in model: " + model.getColumnCount());
                
                Object[] rowData;
                // Kiểm tra số lượng cột để xác định đúng cấu trúc dữ liệu cần thêm vào
                if (model.getColumnCount() == 4) {
                    // Model có 4 cột: STT, Mã Máy, Tên Máy, Số Lượng
                    rowData = new Object[]{
                        stt++,
                        product.getProductId(),
                        product.getProductName(),
                        product.getStockQuantity()  // Số lượng ở vị trí thứ 3 (index 3)
                    };
                } else {
                    // Model có 5 cột: STT, Mã Máy, Tên Máy, Nhà Cung Cấp, Số Lượng
                    rowData = new Object[]{
                        stt++,
                        product.getProductId(),
                        product.getProductName(),
                        product.getSupplier() != null ? product.getSupplier().getName() : "",
                        product.getStockQuantity()  // Số lượng ở vị trí thứ 4 (index 4)
                    };
                }
                
                // Thêm dữ liệu vào bảng
                model.addRow(rowData);
            }
            
            System.out.println("Loaded " + products.size() + " products");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(wareHouseForm, "Lỗi khi tải dữ liệu: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Khởi tạo bảng với EditableTableModel
     */
    private void initTable() {
        TableStyleUtil.applyDefaultStyle(wareHouseForm.getTable());
    }

    /**
     * Cập nhật số lượng sản phẩm
     * @param productId Mã sản phẩm
     * @param quantity Số lượng mới
     */
    
    public void updateProductQuantity(String productId, int quantity) {
        try {
            // Lấy sản phẩm từ database
            java.util.Optional<Product> productOpt = productRepository.findById(productId);
            
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                
                // Cập nhật số lượng
                product.setStockQuantity(quantity);
                
                // Lưu vào database
                productRepository.update(product);
                
                // Tải lại dữ liệu
                loadProducts();
            } else {
                throw new Exception("Không tìm thấy sản phẩm với mã: " + productId);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(wareHouseForm, "Lỗi khi cập nhật số lượng: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Xóa sản phẩm
     * @param productId Mã sản phẩm
     */
    public void deleteProduct(String productId) {
        try {
            // Xóa sản phẩm từ database
            productRepository.delete(productId);
            
            // Tải lại dữ liệu
            loadProducts();
            
            JOptionPane.showMessageDialog(wareHouseForm, "Đã xóa sản phẩm thành công!", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(wareHouseForm, "Lỗi khi xóa sản phẩm: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Tìm kiếm sản phẩm theo từ khóa
     */
    private void searchProducts() {
        if (wareHouseForm.getTextFieldSearch() == null) return;
        
        String keyword = wareHouseForm.getTextFieldSearch().getText().trim();
        
        try {
            List<Product> products;
            if (keyword.isEmpty()) {
                products = productRepository.findAll();
            } else {
                products = productRepository.findByName(keyword);
            }
            
            DefaultTableModel model = (DefaultTableModel) wareHouseForm.getTable().getModel();
            model.setRowCount(0); // Xóa tất cả các dòng hiện tại
            
            int stt = 1;
            for (Product product : products) {
                model.addRow(new Object[]{
                    stt++,
                    product.getProductId(),
                    product.getProductName(),
                    product.getSupplier() != null ? product.getSupplier().getName() : "",
                    product.getStockQuantity()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(wareHouseForm, "Lỗi khi tìm kiếm: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Tạo đơn đặt hàng mới
     */
    private void createPurchaseOrder() {
        try {
            DashboardForm dashboardForm = DashboardForm.getInstance();
            PurchaseOrderForm purchaseOrderForm = new PurchaseOrderForm(dashboardForm, true , connection);
            purchaseOrderForm.setLocationRelativeTo(dashboardForm);
            purchaseOrderForm.setVisible(true);
            
            // Sau khi đóng form đặt hàng, cập nhật lại danh sách sản phẩm
            loadProducts();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(wareHouseForm, "Lỗi khi mở form đặt hàng: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Xem lịch sử nhập kho
     */
    private void viewStockInHistory() {
        try {
            DashboardForm dashboardForm = DashboardForm.getInstance();
            StockInHistoryForm historyForm = new StockInHistoryForm(dashboardForm, true);
            historyForm.setLocationRelativeTo(dashboardForm);
            historyForm.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(wareHouseForm, "Lỗi khi mở form lịch sử nhập kho: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Đóng kết nối khi không cần thiết nữa
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
