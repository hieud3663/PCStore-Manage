package com.pcstore.controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.pcstore.model.Category;
import com.pcstore.model.Product;
import com.pcstore.model.Supplier;
import com.pcstore.repository.impl.CategoryRepository;
import com.pcstore.repository.impl.ProductRepository;
import com.pcstore.repository.impl.SupplierRepository;
import com.pcstore.utils.DatabaseConnection;
import com.pcstore.view.ProductForm;

/**
 * Controller cho màn hình quản lý sản phẩm
 */
public class ProductController {
    private ProductForm productForm;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private SupplierRepository supplierRepository;
    private Connection connection;

    /**
     * Khởi tạo controller với form sản phẩm
     * @param productForm Form sản phẩm
     */
    public ProductController(ProductForm productForm) {
        this.productForm = productForm;
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.productRepository = new ProductRepository(connection);
        this.categoryRepository = new CategoryRepository(connection);
        this.supplierRepository = new SupplierRepository(connection);
        
        // Đăng ký các sự kiện cho form
        registerEvents();
        
        // Khởi tạo dữ liệu cho form
        initializeFormData();
    }

    /**
     * Đăng ký các sự kiện cho form
     */
    private void registerEvents() {
        // Đăng ký sự kiện khi thay đổi danh mục để cập nhật ID tạm thời
        productForm.getCategoryComboBox().addActionListener(e -> generateTemporaryProductId());
        
        // Đăng ký sự kiện cho table khi người dùng chọn một dòng
        productForm.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displaySelectedProduct();
            }
        });
        
        // Đăng ký sự kiện tìm kiếm
        if (productForm.getTextFieldSearch() != null) {
            try {
                // Thử lấy document trực tiếp
                javax.swing.text.Document doc = productForm.getTextFieldSearch().getDocument();
                if (doc != null) {
                    doc.addDocumentListener(new javax.swing.event.DocumentListener() {
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
            } catch (Exception e) {
                // Nếu không có getDocument, thử phương pháp khác
                productForm.getTextFieldSearch().addKeyListener(new java.awt.event.KeyAdapter() {
                    @Override
                    public void keyReleased(java.awt.event.KeyEvent evt) {
                        searchProducts();
                    }
                });
            }
        }
        
        // Đăng ký sự kiện sắp xếp
        productForm.getCbbSortCustomer().addActionListener(e -> sortProducts());
        productForm.getCbbSort().addActionListener(e -> sortProducts());
        
        // Đăng ký sự kiện cho nút reset sort
        productForm.getBtnResetSort().addActionListener(e -> resetSort());

        try {
            if (productForm.getBtnConfirmAddProduct() != null) {
                productForm.getBtnConfirmAddProduct().addActionListener(e -> addProduct());
            }
        } catch (Exception e) {
            System.err.println("Không thể đăng ký sự kiện cho nút Xác nhận thêm: " + e.getMessage());
        }
    }
    
    /**
     * Tải dữ liệu sản phẩm vào bảng
     */
    public void loadProducts() {
        try {
            List<Product> products = productRepository.findAll();
            updateProductTable(products);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(productForm, "Lỗi khi tải dữ liệu: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Tìm kiếm sản phẩm dựa theo từ khóa
     */
    public void searchProducts(String keyword) {
        try {
            List<Product> products;
            if (keyword == null || keyword.trim().isEmpty()) {
                products = productRepository.findAll();
            } else {
                // Đảm bảo repository có phương thức findByName
                products = productRepository.findByName(keyword);
            }
            
            updateProductTable(products);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(productForm, "Lỗi khi tìm kiếm: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Tìm kiếm sản phẩm từ text field search
     */
    private void searchProducts() {
        // Kiểm tra null
        if (productForm.getTextFieldSearch() == null) return;
        
        String keyword = "";
        try {
            // Cách 1: Nếu TextFieldSearch đã kế thừa từ JTextField
            keyword = productForm.getTextFieldSearch().getText().trim();
        } catch (Exception e) {
            try {
                // Cách 2: Nếu TextFieldSearch có phương thức getSearchText() riêng
                java.lang.reflect.Method method = productForm.getTextFieldSearch().getClass().getMethod("getSearchText");
                Object result = method.invoke(productForm.getTextFieldSearch());
                if (result != null) {
                    keyword = result.toString().trim();
                }
            } catch (Exception ex) {
                System.err.println("Không thể lấy text từ TextFieldSearch: " + ex.getMessage());
                return;
            }
        }
        
        searchProducts(keyword);
    }
    
    /**
     * Hiển thị thông tin của sản phẩm đã chọn vào các trường trong form
     */
    public void displaySelectedProduct() {
        JTable table = productForm.getTable();
        int selectedRow = table.getSelectedRow();
        
        if (selectedRow != -1) {
            String productId = table.getValueAt(selectedRow, 0).toString();
            
            try {
                Optional<Product> productOpt = productRepository.findById(productId);
                
                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    
                    // Điền thông tin vào các trường
                    productForm.getIdField().setText(product.getProductId());
                    productForm.getNameField().setText(product.getProductName());
                    
                    // Chọn Category trong combobox
                    selectCategoryInComboBox(product.getCategory());
                    
                    // Chọn Supplier trong combobox
                    selectSupplierInComboBox(product.getSupplier());
                    
                    productForm.getQuantityField().setText(String.valueOf(product.getStockQuantity()));
                    productForm.getPriceField().setText(product.getPrice().toString());
                    productForm.getSpecificationsArea().setText(product.getSpecifications());
                    productForm.getDescriptionArea().setText(product.getDescription());
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(productForm, "Lỗi khi hiển thị thông tin sản phẩm: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Chọn danh mục trong combobox
     */
    private void selectCategoryInComboBox(Category category) {
        if (category == null) return;
        
        for (int i = 0; i < productForm.getCategoryComboBox().getItemCount(); i++) {
            Category item = productForm.getCategoryComboBox().getItemAt(i);
            if (item.getCategoryId().equals(category.getCategoryId())) {
                productForm.getCategoryComboBox().setSelectedIndex(i);
                return;
            }
        }
    }
    
    /**
     * Chọn nhà cung cấp trong combobox
     */
    private void selectSupplierInComboBox(Supplier supplier) {
        if (supplier == null) return;
        
        for (int i = 0; i < productForm.getSupplierComboBox().getItemCount(); i++) {
            Supplier item = productForm.getSupplierComboBox().getItemAt(i);
            if (item.getSupplierId().equals(supplier.getSupplierId())) {
                productForm.getSupplierComboBox().setSelectedIndex(i);
                return;
            }
        }
    }
    
    /**
     * Xử lý khi người dùng nhấn nút Thêm để bắt đầu nhập thông tin mới
     */
    /**
 * Xử lý khi người dùng nhấn nút Thêm để bắt đầu nhập thông tin mới
 */
public void handleAddButtonClick() {
    // Xóa lựa chọn hiện tại trong bảng
    productForm.getTable().clearSelection();
    
    // Reset form trước khi thêm mới để tránh sử dụng dữ liệu của sản phẩm đang chọn
    resetForm();
    
    // Đặt focus vào trường tên sản phẩm
    productForm.getNameField().requestFocus();
    
}

    /**
     * Thêm sản phẩm mới
     */
    public void addProduct() {
        try {
            // Lấy thông tin từ form
            String productName = productForm.getNameField().getText().trim();
            Category category = (Category) productForm.getCategoryComboBox().getSelectedItem();
            Supplier supplier = (Supplier) productForm.getSupplierComboBox().getSelectedItem();
            
            // Kiểm tra thông tin bắt buộc
            if (productName.isEmpty() || category == null || supplier == null) {
                JOptionPane.showMessageDialog(productForm,
                        "Vui lòng nhập đầy đủ thông tin sản phẩm!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Các thông tin khác
            String quantityStr = productForm.getQuantityField().getText().trim();
            String priceStr = productForm.getPriceField().getText().trim();
            String specifications = productForm.getSpecificationsArea().getText().trim();
            String description = productForm.getDescriptionArea().getText().trim();
            
            // Kiểm tra và chuyển đổi kiểu dữ liệu
            int stockQuantity = 0;
            BigDecimal price = BigDecimal.ZERO;
            
            try {
                if (!quantityStr.isEmpty()) {
                    stockQuantity = Integer.parseInt(quantityStr);
                    if (stockQuantity < 0) throw new NumberFormatException("Số lượng không được âm");
                }
                
                if (!priceStr.isEmpty()) {
                    price = new BigDecimal(priceStr);
                    if (price.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException("Giá phải lớn hơn 0");
                } else {
                    throw new NumberFormatException("Giá không được để trống");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(productForm,
                        "Lỗi định dạng số: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Tạo đối tượng sản phẩm mới
            Product product = new Product();
            
            // Tạo mã sản phẩm mẫu cho repository
            String tempProductId = category.getCategoryId() + "TMP"; // ID tạm thời
            product.setProductId(tempProductId);
            
            product.setProductName(productName);
            product.setCategory(category);
            product.setSupplier(supplier);
            product.setPrice(price);
            product.setStockQuantity(stockQuantity);
            product.setSpecifications(specifications);
            product.setDescription(description);
            
            // Thực hiện thêm sản phẩm
            Product savedProduct = null;
            try {
                savedProduct = productRepository.add(product);
                
                // Lấy sản phẩm vừa thêm dựa trên các thông tin
                String findSql = "SELECT TOP 1 * FROM Products WHERE ProductName = ? AND CategoryID = ? AND SupplierID = ? ORDER BY CreatedAt DESC";
                try (PreparedStatement stmt = connection.prepareStatement(findSql)) {
                    stmt.setString(1, productName);
                    stmt.setString(2, category.getCategoryId());
                    stmt.setString(3, supplier.getSupplierId());
                    
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        product.setProductId(rs.getString("ProductID"));
                        savedProduct = product;
                    }
                }
                
                // Nếu vẫn không tìm thấy sản phẩm, thử tìm sản phẩm mới nhất theo danh mục
                if (savedProduct == null || savedProduct.getProductId() == null || savedProduct.getProductId().equals(tempProductId)) {
                    String altFindSql = "SELECT TOP 1 * FROM Products WHERE CategoryID = ? ORDER BY CreatedAt DESC";
                    try (PreparedStatement stmt = connection.prepareStatement(altFindSql)) {
                        stmt.setString(1, category.getCategoryId());
                        
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            product.setProductId(rs.getString("ProductID"));
                            savedProduct = product;
                        }
                    }
                }
                
                // Kiểm tra kết quả
                if (savedProduct == null || savedProduct.getProductId() == null || savedProduct.getProductId().equals(tempProductId)) {
                    throw new SQLException("Không thể lấy ID sản phẩm sau khi thêm");
                }
                
                // Hiển thị thông báo thành công
                JOptionPane.showMessageDialog(productForm,
                        "Thêm sản phẩm thành công! Mã sản phẩm: " + savedProduct.getProductId(),
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                
                // Reset form
                resetForm();
                
                // Cập nhật lại bảng sản phẩm
                loadProducts();
                
            } catch (SQLException e) {
                System.err.println("Lỗi SQL: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(productForm,
                        "Lỗi khi thêm sản phẩm: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(productForm,
                    "Lỗi khi thêm sản phẩm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Cập nhật sản phẩm
     */
    public void updateProduct() {
        try {
            // Lấy ID sản phẩm
            String productId = productForm.getIdField().getText().trim();
            if (productId.isEmpty()) {
                JOptionPane.showMessageDialog(productForm, "Vui lòng chọn sản phẩm cần cập nhật", 
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Lấy thông tin từ form
            String productName = productForm.getNameField().getText().trim();
            Category category = (Category) productForm.getCategoryComboBox().getSelectedItem();
            Supplier supplier = (Supplier) productForm.getSupplierComboBox().getSelectedItem();
            
            // Kiểm tra thông tin bắt buộc
            if (productName.isEmpty() || category == null || supplier == null) {
                JOptionPane.showMessageDialog(productForm,
                        "Vui lòng nhập đầy đủ thông tin sản phẩm!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Các thông tin khác
            String quantityStr = productForm.getQuantityField().getText().trim();
            String priceStr = productForm.getPriceField().getText().trim();
            String specifications = productForm.getSpecificationsArea().getText().trim();
            String description = productForm.getDescriptionArea().getText().trim();
            
            // Kiểm tra và chuyển đổi kiểu dữ liệu
            int stockQuantity = 0;
            BigDecimal price = BigDecimal.ZERO;
            
            try {
                if (!quantityStr.isEmpty()) {
                    stockQuantity = Integer.parseInt(quantityStr);
                    if (stockQuantity < 0) throw new NumberFormatException("Số lượng không được âm");
                }
                
                if (!priceStr.isEmpty()) {
                    price = new BigDecimal(priceStr);
                    if (price.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException("Giá phải lớn hơn 0");
                } else {
                    throw new NumberFormatException("Giá không được để trống");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(productForm,
                        "Lỗi định dạng số: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Tạo đối tượng sản phẩm để cập nhật
            Product product = new Product();
            product.setProductId(productId);
            product.setProductName(productName);
            product.setCategory(category);
            product.setSupplier(supplier);
            product.setPrice(price);
            product.setStockQuantity(stockQuantity);
            product.setSpecifications(specifications);
            product.setDescription(description);
            
            // Cập nhật sản phẩm vào database
            productRepository.update(product);
            
            // Hiển thị thông báo thành công
            JOptionPane.showMessageDialog(productForm,
                    "Cập nhật sản phẩm thành công!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
            // Reset form
            resetForm();
            
            // Cập nhật lại bảng sản phẩm
            loadProducts();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(productForm,
                    "Lỗi khi cập nhật sản phẩm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Xóa sản phẩm
     */
    public void deleteProduct() {
        JTable table = productForm.getTable();
        int selectedRow = table.getSelectedRow();
        
        if (selectedRow != -1) {
            String productId = table.getValueAt(selectedRow, 0).toString();
            
            int confirm = JOptionPane.showConfirmDialog(productForm, 
                    "Bạn có chắc muốn xóa sản phẩm này?", 
                    "Xác nhận xóa", 
                    JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    productRepository.delete(productId);
                    loadProducts();
                    resetForm();
                    
                    JOptionPane.showMessageDialog(productForm, "Xóa sản phẩm thành công!", 
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(productForm, "Lỗi khi xóa sản phẩm: " + e.getMessage(), 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(productForm, "Vui lòng chọn sản phẩm cần xóa", 
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Reset form nhập liệu
     */
    private void resetForm() {
        // Xóa dữ liệu trong form
        productForm.getNameField().setText("");
        productForm.getQuantityField().setText("0");
        productForm.getPriceField().setText("0");
        productForm.getSpecificationsArea().setText("");
        productForm.getDescriptionArea().setText("");
        
        // Bỏ chọn dòng đang chọn trong bảng
        productForm.getTable().clearSelection();
        
        // Tạo lại mã sản phẩm tạm thời
        generateTemporaryProductId();
        
        // Focus vào trường tên sản phẩm
        productForm.getNameField().requestFocus();
    }
    
    /**
     * Sắp xếp sản phẩm theo tiêu chí đã chọn
     */
    public void sortProducts(String sortField, String sortOrder) {
        try {
            // Nếu không chọn tiêu chí sắp xếp hoặc thứ tự sắp xếp thì không làm gì
            if ("<Không>".equals(sortField) || "<Không>".equals(sortOrder)) {
                return;
            }
            
            // Lấy danh sách sản phẩm
            List<Product> products = productRepository.findAll();
            
            // Sắp xếp sản phẩm theo tiêu chí
            if ("Giá Bán".equals(sortField)) {
                if ("Tăng dần".equals(sortOrder)) {
                    products.sort((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()));
                } else {
                    products.sort((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()));
                }
            } else if ("Phân Loại".equals(sortField)) {
                if ("Tăng dần".equals(sortOrder)) {
                    products.sort((p1, p2) -> {
                        String cat1 = p1.getCategory() != null ? p1.getCategory().getCategoryName() : "";
                        String cat2 = p2.getCategory() != null ? p2.getCategory().getCategoryName() : "";
                        return cat1.compareToIgnoreCase(cat2);
                    });
                } else {
                    products.sort((p1, p2) -> {
                        String cat1 = p1.getCategory() != null ? p1.getCategory().getCategoryName() : "";
                        String cat2 = p2.getCategory() != null ? p2.getCategory().getCategoryName() : "";
                        return cat2.compareToIgnoreCase(cat1);
                    });
                }
            }
            
            // Cập nhật bảng sản phẩm
            updateProductTable(products);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(productForm, "Lỗi khi sắp xếp sản phẩm: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Sắp xếp sản phẩm theo tiêu chí trong combobox
     */
    private void sortProducts() {
        // Lấy tiêu chí sắp xếp từ combobox
        String sortField = (String) productForm.getCbbSortCustomer().getSelectedItem();
        String sortOrder = (String) productForm.getCbbSort().getSelectedItem();
        
        sortProducts(sortField, sortOrder);
    }
    
    /**
     * Reset các tùy chọn sắp xếp về mặc định và tải lại dữ liệu
     */
    private void resetSort() {
        productForm.getCbbSortCustomer().setSelectedIndex(0);
        productForm.getCbbSort().setSelectedIndex(0);
        loadProducts();
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
    
    /**
     * Cập nhật bảng sản phẩm với danh sách đã cho
     */
    private void updateProductTable(List<Product> products) {
        DefaultTableModel model = (DefaultTableModel) productForm.getTable().getModel();
        model.setRowCount(0); // Xóa tất cả các dòng hiện tại
        
        for (Product product : products) {
            model.addRow(new Object[]{
                product.getProductId(),
                product.getProductName(),
                product.getCategory() != null ? product.getCategory().getCategoryName() : "",
                product.getSupplier() != null ? product.getSupplier().getName() : "",
                product.getStockQuantity(),
                product.getPrice(),
                product.getSpecifications(),
                product.getDescription()
            });
        }
    }
    
    /**
     * Xuất danh sách sản phẩm ra file Excel
     */
    public void exportToExcel() {
        try {
            // Implement later
            JOptionPane.showMessageDialog(productForm, "Chức năng xuất Excel đang được phát triển",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(productForm, "Lỗi khi xuất file Excel: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Khởi tạo dữ liệu cho form
     */
    private void initializeFormData() {
        try {
            // Tải danh sách danh mục và nhà cung cấp
            loadCategories();
            loadSuppliers();
            
            // Tạo mã sản phẩm tạm thời
            generateTemporaryProductId();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(productForm,
                    "Lỗi khi khởi tạo dữ liệu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Tải danh sách danh mục sản phẩm từ database
     */
    private void loadCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            
            // Xóa tất cả items hiện có
            productForm.getCategoryComboBox().removeAllItems();
            
            // Thêm danh mục vào combobox
            for (Category category : categories) {
                productForm.getCategoryComboBox().addItem(category);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(productForm,
                    "Lỗi khi tải danh sách danh mục: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Tải danh sách nhà cung cấp từ database
     */
    private void loadSuppliers() {
        try {
            List<Supplier> suppliers = supplierRepository.findAll();
            
            // Xóa tất cả items hiện có
            productForm.getSupplierComboBox().removeAllItems();
            
            // Thêm nhà cung cấp vào combobox
            for (Supplier supplier : suppliers) {
                productForm.getSupplierComboBox().addItem(supplier);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(productForm,
                    "Lỗi khi tải danh sách nhà cung cấp: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Tạo mã sản phẩm tạm thời để hiển thị (sẽ được thay thế bởi trigger khi thêm vào database)
     */
    public void generateTemporaryProductId() {
        // Lấy danh mục được chọn
        Category selectedCategory = (Category) productForm.getCategoryComboBox().getSelectedItem();
        
        if (selectedCategory != null) {
            String categoryId = selectedCategory.getCategoryId();
            productForm.getIdField().setText(categoryId + "xxx"); // Chỉ hiển thị mẫu
        } else {
            productForm.getIdField().setText("xxxxx"); // Mẫu mặc định
        }
    }
}