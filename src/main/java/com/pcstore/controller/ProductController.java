package com.pcstore.controller;

import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.ss.usermodel.TableStyle;
import org.apache.xmlbeans.StringEnumAbstractBase.Table;

import com.pcstore.model.Category;
import com.pcstore.model.Product;
import com.pcstore.model.Supplier;
import com.pcstore.repository.impl.CategoryRepository;
import com.pcstore.repository.impl.ProductRepository;
import com.pcstore.repository.impl.SupplierRepository;
import com.pcstore.utils.ButtonUtils;
import com.pcstore.utils.DatabaseConnection;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.TableStyleUtil;
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
    private boolean addingProduct = false;

    /**
     * Khởi tạo controller với form sản phẩm
     * @param productForm Form sản phẩm
     */
    public ProductController(ProductForm productForm) {
        this.productForm = productForm;
        
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi kết nối database: " + e.getMessage());
        }
        
        // Khởi tạo các repository
        this.productRepository = new ProductRepository(connection);
        this.categoryRepository = new CategoryRepository(connection);
        this.supplierRepository = new SupplierRepository(connection);
        
        // Đăng ký các sự kiện cho form
        registerEvents();
        
        // Khởi tạo dữ liệu cho form
        initializeFormData();
        
        // Load dữ liệu sản phẩm ngay sau khi khởi tạo
        loadProducts();
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
            displaySelectedProduct(); // Gọi phương thức hiển thị thông tin chi tiết
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

        // Đăng ký xử lý phím ESC toàn cục bằng KeyboardFocusManager
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && addingProduct) {
                    System.out.println("Phát hiện phím ESC khi đang ở chế độ thêm");
                    cancelAddProduct();
                    return true;
                }
            }
            return false; // Tiếp tục xử lý sự kiện cho các thành phần khác
        });
    }
    
    /**
     * Tải dữ liệu sản phẩm vào bảng
     */
    public void loadProducts() {
        try {
            
            // Kiểm tra kết nối trước khi truy vấn
            if (connection == null || connection.isClosed()) {
                connection = DatabaseConnection.getInstance().getConnection();
                this.productRepository = new ProductRepository(connection);
            }
            
            List<Product> products = productRepository.findAll();
            
            if (products != null && !products.isEmpty()) {
                updateProductTable(products);
            } else {
                DefaultTableModel model = (DefaultTableModel) productForm.getTable().getModel();
                model.setRowCount(0);
            }

            TableStyleUtil.refreshSorter(productForm.getTable());
        } catch (Exception e) {
            System.err.println("Lỗi khi tải dữ liệu sản phẩm: " + e.getMessage());
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
     * Thêm sản phẩm mới vào cơ sở dữ liệu
     */
    public void addProduct() {
        try {
            // Kiểm tra xem có đang ở chế độ thêm không
            if (!addingProduct) {
                handleAddButtonClick();
                return; 
            }
            
            // Chỉ thực hiện phần code dưới đây khi đang ở chế độ thêm và người dùng nhấn "Xác nhận thêm"
            
            String productName = productForm.getNameField().getText().trim();
            Category category = (Category) productForm.getCategoryComboBox().getSelectedItem();
            
            if (productName.isEmpty()) {
                JOptionPane.showMessageDialog(productForm, 
                        "Vui lòng nhập tên sản phẩm",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (category == null) {
                JOptionPane.showMessageDialog(productForm, 
                        "Vui lòng chọn danh mục",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            
            int stockQuantity = 0;
            BigDecimal price = BigDecimal.ZERO;
            
            try {
                String quantityStr = productForm.getQuantityField().getText().trim();
                String priceStr = productForm.getPriceField().getText().trim();
                
                if (!quantityStr.isEmpty()) {
                    stockQuantity = Integer.parseInt(quantityStr);
                }
                
                if (!priceStr.isEmpty()) {
                    price = new BigDecimal(priceStr);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(productForm, 
                        "Số lượng và giá phải là số hợp lệ",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Tạo mã sản phẩm mới dựa trên danh mục
            String productId = generateProductID(category);
            
            Product product = new Product();
            product.setProductId(productId);
            product.setProductName(productName);
            product.setCategory(category);
            product.setStockQuantity(stockQuantity);
            product.setPrice(price);
            product.setSpecifications(productForm.getSpecificationsArea().getText());
            product.setDescription(productForm.getDescriptionArea().getText());

            int checkOpt = JOptionPane.showConfirmDialog(productForm, 
                    "Bạn có chắc chắn muốn thêm sản phẩm này?",
                    "Xác nhận thêm", JOptionPane.YES_NO_OPTION);
            if(checkOpt != JOptionPane.YES_OPTION) {
                return;
            }

            Product savedProduct = productRepository.add(product);
            
            if (savedProduct != null) {
                JOptionPane.showMessageDialog(productForm, 
                        "Thêm sản phẩm thành công!",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                
                addingProduct = false;
                updateUIForAddMode(false);
                
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(productForm, 
                        "Không thể thêm sản phẩm. Vui lòng thử lại!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(productForm, 
                    "Lỗi: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    /**
     * Tạo mã sản phẩm mới dựa trên danh mục
     */
    public String generateProductID(Category category) {
       return productRepository.generateProductID(category.getCategoryId());
    }

    /**
     * Hủy chế độ thêm sản phẩm
     */
    public void cancelAddProduct() {
        try {
            if (addingProduct) {

                int checkOpt = JOptionPane.showConfirmDialog(productForm, 
                        "Bạn có chắc chắn muốn hủy chế độ thêm sản phẩm?",
                        "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
                if (checkOpt != JOptionPane.YES_OPTION) {
                    return;
                }

                addingProduct = false;
                resetForm();
                updateUIForAddMode(false);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi hủy chế độ thêm: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Kiểm tra xem đang ở chế độ thêm hay không
     * @return true nếu đang ở chế độ thêm, ngược lại false
     */
    public boolean isAddingProduct() {
        return addingProduct;
    }
    
   /**
 * Hiển thị thông tin của sản phẩm đã chọn vào các trường trong form
 */
public void displaySelectedProduct() {
    // Nếu đang ở chế độ thêm, không làm gì cả
    if (addingProduct) {
        return;
    }
    
    try {
        int selectedRow = productForm.getTable().getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        
        String productId = (String) productForm.getTable().getValueAt(selectedRow, 0);
        Optional<Product> productOpt = productRepository.findById(productId);
        
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            
            
            productForm.getIdField().setText(product.getProductId());
            productForm.getNameField().setText(product.getProductName());
            
            if (product.getCategory() != null) {
                selectCategoryInComboBox(product.getCategory());
            }
            
            productForm.getQuantityField().setText(String.valueOf(product.getStockQuantity()));
            productForm.getPriceField().setText(product.getPrice().toString());
            
            // Xử lý thông số kỹ thuật - đảm bảo không null
            String specs = product.getSpecifications();
            productForm.getSpecificationsArea().setText(specs != null ? specs : "");
            
            // Xử lý mô tả - đảm bảo không null
            String desc = product.getDescription();
            productForm.getDescriptionArea().setText(desc != null ? desc : "");
        }
    } catch (Exception e) {
        System.err.println("Error displaying product: " + e.getMessage());
        e.printStackTrace();
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
    // Nếu supplier hoặc ComboBox là null thì return
    if (supplier == null || productForm.getSupplierComboBox() == null) return;
    
    // Phần còn lại của code
    JComboBox<Supplier> supplierComboBox = productForm.getSupplierComboBox();
    if (supplierComboBox == null) return;
    
    for (int i = 0; i < supplierComboBox.getItemCount(); i++) {
        Supplier item = supplierComboBox.getItemAt(i);
        if (item.getSupplierId().equals(supplier.getSupplierId())) {
            supplierComboBox.setSelectedIndex(i);
            return;
        }
    }
}
    
    /**
     * Xử lý khi người dùng nhấn nút Thêm để bắt đầu nhập thông tin mới
     */
    public void handleAddButtonClick() {
        try {
            // Chuyển sang chế độ thêm mới
            addingProduct = true;
            
            if (productForm.getTable() != null) {
                productForm.getTable().clearSelection();
            }
            
            resetForm();

            updateUIForAddMode(true);
            
            // Đặt focus vào trường tên sản phẩm
            if (productForm.getNameField() != null) {
                productForm.getNameField().requestFocus();
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi khi chuyển sang chế độ thêm: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật các thành phần giao diện dựa trên chế độ thêm mới
     * @param isAddMode true nếu đang ở chế độ thêm mới, ngược lại false
     */
    private void updateUIForAddMode(boolean isAddMode) {
        try {
            // Thay đổi text của nút Add (không ẩn hiện nút)
            if (productForm.getBtnAdd() != null) {
                if (isAddMode) {
                    // Đổi text của nút thêm thành "Xác nhận thêm" khi ở chế độ thêm
                    productForm.getBtnAdd().setText("Xác nhận thêm");
                    productForm.getBtnAdd().setkBackGroundColor(new java.awt.Color(0, 204, 102));
                    productForm.getBtnAdd().repaint();
                } else {
                    java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle();
                    productForm.getBtnAdd().setText(bundle.getString("btnAddProduct"));
                    productForm.getBtnAdd().setkBackGroundColor(new java.awt.Color(0, 102, 255));
                    productForm.getBtnAdd().repaint();
                }
            }

            productForm.getLabelESC().setVisible(isAddMode);
            

            // Vô hiệu hóa/kích hoạt các nút khác
            if (productForm.getBtnUpdate() != null) {
                ButtonUtils.setKButtonEnabled(productForm.getBtnUpdate(), !isAddMode);
            }
            
            if (productForm.getBtnDelete() != null) {
                ButtonUtils.setKButtonEnabled(productForm.getBtnDelete(), !isAddMode);
            }
         
            if (productForm.getTable() != null) {
                productForm.getTable().setEnabled(!isAddMode);
            }
            
            if (productForm.getTextFieldSearch() != null) {
                productForm.getTextFieldSearch().setEnabled(!isAddMode);
            }
            
            if (productForm.getCbbSort() != null) {
                productForm.getCbbSort().setEnabled(!isAddMode);
            }
            
            if (productForm.getCbbSortCustomer() != null) {
                productForm.getCbbSortCustomer().setEnabled(!isAddMode);
            }
            
            if (productForm.getBtnResetSort() != null) {
                productForm.getBtnResetSort().setEnabled(!isAddMode);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật giao diện: " + e.getMessage());
            e.printStackTrace();
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
            
            // Kiểm tra thông tin bắt buộc
            if (productName.isEmpty()) {
                JOptionPane.showMessageDialog(productForm, "Vui lòng nhập tên sản phẩm", 
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (category == null) {
                JOptionPane.showMessageDialog(productForm, "Vui lòng chọn danh mục sản phẩm", 
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
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
                    if (stockQuantity < 0) throw new NumberFormatException(ErrorMessage.PRODUCT_QUANTITY_NEGATIVE);
                }
                
                if (!priceStr.isEmpty()) {
                    price = new BigDecimal(priceStr);
                    if (price.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException(ErrorMessage.VALUE_MUST_BE_POSITIVE);
                } else {
                    throw new NumberFormatException(ErrorMessage.FIELD_EMPTY.formatted("Giá"));
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
            product.setPrice(price);
            product.setStockQuantity(stockQuantity);
            product.setSpecifications(specifications);
            product.setDescription(description);
            
            productRepository.update(product);
            
            // Hiển thị thông báo thành công
            JOptionPane.showMessageDialog(productForm,
                    "Cập nhật sản phẩm thành công!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
            resetForm();
            
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
        productForm.getLabelESC().setVisible(false);

        productForm.getTable().clearSelection();

        // Chỉ tạo mã sản phẩm tạm thời khi đang ở chế độ thêm
        if (addingProduct) {
            generateTemporaryProductId();
        } else {
            productForm.getIdField().setText(""); 
        }

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
        
    }
    
    /**
     * Cập nhật bảng sản phẩm với danh sách đã cho
     */
    private void updateProductTable(List<Product> products) {
        try {
            DefaultTableModel model = (DefaultTableModel) productForm.getTable().getModel();
            model.setRowCount(0); // Xóa tất cả các dòng hiện tại
            
            
            for (Product product : products) {
                try {
                    // Xử lý các giá trị null
                    String specs = (product.getSpecifications() != null) ? product.getSpecifications() : "";
                    String desc = (product.getDescription() != null) ? product.getDescription() : "";
                    
                    model.addRow(new Object[]{
                        product.getProductId(),
                        product.getProductName(),
                        product.getCategory() != null ? product.getCategory().getCategoryName() : "",
                        product.getStockQuantity(),
                        product.getPrice(),
                        specs,
                        desc
                    });
                    
                } catch (Exception e) {
                    System.err.println("Lỗi khi thêm sản phẩm " + product.getProductId() + ": " + e.getMessage());
                }
            }
            
            // System.out.println("Số dòng trong bảng sau khi cập nhật: " + model.getRowCount());
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật bảng: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    /**
     * Xuất danh sách sản phẩm ra file Excel
     */
    public void exportToExcel() {
        try {
            if (productRepository == null) {
                JOptionPane.showMessageDialog(productForm, "Chưa kết nối tới CSDL", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            List<Product> products = productRepository.findAll();
            if (products == null || products.isEmpty()) {
                JOptionPane.showMessageDialog(productForm, 
                        "Không có dữ liệu sản phẩm để xuất",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Tạo dữ liệu xuất ra Excel
            String[] headers = {"STT", "Mã sản phẩm", "Tên sản phẩm", "Danh mục", "Số lượng", 
                               "Giá", "Thông số kỹ thuật", "Mô tả"};
            
            Object[][] data = new Object[products.size()][headers.length];
            
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                data[i][0] = i + 1; // STT bắt đầu từ 1
                data[i][1] = product.getProductId();
                data[i][2] = product.getProductName();
                data[i][3] = product.getCategory() != null ? product.getCategory().getCategoryName() : "";
                data[i][4] = product.getStockQuantity();
                data[i][5] = product.getPrice();
                data[i][6] = product.getSpecifications() != null ? product.getSpecifications() : "";
                data[i][7] = product.getDescription() != null ? product.getDescription() : "";
            }
            
            // Tạo file Excel
            String fileName = "DANH_SACH_SAN_PHAM_" + 
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            
            com.pcstore.utils.JExcel jExcel = new com.pcstore.utils.JExcel();
            boolean success = jExcel.toExcel(headers, data, "Danh sách sản phẩm", fileName);
            
            if (success) {
                JOptionPane.showMessageDialog(productForm,
                        "Xuất Excel thành công!",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(productForm,
                        "Xuất Excel không thành công!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(productForm, 
                    "Lỗi khi xuất Excel: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Khởi tạo dữ liệu cho form
     */
    private void initializeFormData() {
        try {
            TableStyleUtil.applyDefaultStyle(productForm.getTable());
            loadCategories();
            
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
        // Chỉ tạo mã tạm thời khi đang ở chế độ thêm sản phẩm
        if (addingProduct) {
            // Lấy danh mục được chọn
            Category selectedCategory = (Category) productForm.getCategoryComboBox().getSelectedItem();

            if (selectedCategory != null) {
                String categoryId = selectedCategory.getCategoryId();
                String productID = productRepository.generateProductID(categoryId);
                productForm.getIdField().setText(productID); // Hiển thị mã tạm thời
            } else {
                productForm.getIdField().setText("xxxxx"); // Mẫu mặc định
            }
        }
    }
}