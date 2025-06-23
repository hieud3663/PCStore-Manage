package com.pcstore.controller;

import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.List;
import java.util.Optional;

import javax.management.Notification;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.pcstore.model.Category;
import com.pcstore.model.Product;
import com.pcstore.model.Supplier;
import com.pcstore.model.PriceHistory;
import com.pcstore.repository.impl.CategoryRepository;
import com.pcstore.repository.impl.ProductRepository;
import com.pcstore.repository.impl.SupplierRepository;
import com.pcstore.repository.impl.PriceHistoryRepository;
import com.pcstore.utils.ButtonUtils;
import com.pcstore.utils.DatabaseConnection;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.NumberUtils;
import com.pcstore.utils.TableUtils;
import com.pcstore.utils.SessionManager;
import com.pcstore.view.ProductForm;

import raven.toast.Notifications;
import raven.toast.Notifications.Type;

/**
 * Controller cho màn hình quản lý sản phẩm
 */
public class ProductController {
    private ProductForm productForm;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private SupplierRepository supplierRepository;
    private PriceHistoryRepository priceHistoryRepository = new PriceHistoryRepository();
    private Connection connection;
    private boolean addingProduct = false;
    private NumberFormat currencyFormatter = LocaleManager.getInstance().getNumberFormatter();

    private TableRowSorter<TableModel> productTableSorter;

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
            System.err.println(ErrorMessage.PRODUCT_DATABASE_CONNECTION_ERROR_DETAIL.format(e.getMessage()));
        }
        
        // Khởi tạo các repository
        this.productRepository = new ProductRepository(connection);
        this.categoryRepository = new CategoryRepository(connection);
        this.supplierRepository = new SupplierRepository(connection);
    
        // Đăng ký các sự kiện cho form
        registerEvents();

        productForm.getPanelSort().setVisible(false);
        
        // Khởi tạo dữ liệu cho form
        initializeFormData();
        
        // Load dữ liệu sản phẩm ngay sau khi khởi tạo
        loadListProducts();
    }

    /**
     * Đăng ký các sự kiện cho form
     */
    private void registerEvents() {

        NumberUtils.applyCurrencyFilter(productForm.getTxtCostPrice());
        NumberUtils.applyCurrencyFilter(productForm.getPriceField());

        productForm.getBtnAdd().addActionListener(e -> {
            if (isAddingProduct()) {
                // Nếu đang ở chế độ thêm, thực hiện thêm sản phẩm
                addProduct();
            } else {
                // Nếu đang ở chế độ bình thường, chuyển sang chế độ thêm
                handleAddButtonClick();
            }
        });
        
        productForm.getBtnUpdate().addActionListener(e -> handleUpdateButtonClick());
        
        productForm.getBtnDelete().addActionListener(e -> deleteProduct());
        
        productForm.getBtnExport().addActionListener(e -> exportToExcel());

        productForm.getBtnRefresh().addActionListener(e -> refereshForm());
        // Đăng ký sự kiện cho nút reset sort
        productForm.getBtnResetSort().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                handleResetSortClick();
            }
        });
        
        // Đăng ký sự kiện khi thay đổi danh mục
        productForm.getCategoryComboBox().addActionListener(e -> generateTemporaryProductId());
        
        // Đăng ký sự kiện tìm kiếm
        if (productForm.getTextFieldSearch() != null) {
            productForm.getTextFieldSearch().getTxtSearchField().addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    searchProducts();
                }
            });
        }
        
        // Đăng ký sự kiện click vào bảng
        productForm.getTable().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                displaySelectedProduct();
            }
        });
        
        // Đăng ký sự kiện cho combobox sắp xếp
        productForm.getCbbSortCustomer().addActionListener(e -> handleSortFieldChange());
        productForm.getCbbSort().addActionListener(e -> handleSortFieldChange());
        
        
        // Đăng ký sự kiện nhập liệu cho trường giá (số và dấu chấm)
        productForm.getPriceField().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    evt.consume();
                }
            }

            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateProfitMargin();

            }
        });
        
        productForm.getTxtCostPrice().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    evt.consume();
                }
            }

            public void keyReleased(java.awt.event.KeyEvent evt) {
                // Tự động tính giá bán khi giá vốn thay đổi
                calculateSellingPrice();
            }
        });
        
        productForm.getTxtProfit().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    evt.consume();
                }
            }

            public void keyReleased(java.awt.event.KeyEvent evt) {
                // Tự động tính giá bán khi tỷ suất lợi nhuận thay đổi
                calculateSellingPrice();
            }
        });
        
        // Đăng ký sự kiện click vào form để hủy thêm sản phẩm
        productForm.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (isAddingProduct()) {
                    // Kiểm tra xem click có phải trên nút thêm không
                    java.awt.Point p = evt.getPoint();
                    if (productForm.getBtnAdd() != null && !productForm.getBtnAdd().getBounds().contains(p)) {
                        cancelAddProduct();
                    }
                }
            }
        });

        // Đăng ký xử lý phím ESC toàn cục
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && isAddingProduct()) {
                    cancelAddProduct();
                    return true;
                }
            }
            return false;
        });


    }

    /**
     * Tải dữ liệu sản phẩm vào bảng
     */
    public void loadListProducts() {
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

            TableUtils.refreshSorter(productForm.getTable());
        } catch (Exception e) {
            // System.err.println("Lỗi khi tải dữ liệu sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Tìm kiếm sản phẩm từ text field search
     */
    public void searchProducts() {
        // Lấy từ khóa tìm kiếm
        String keyword = productForm.getTextFieldSearch().getText().trim();
        
        // Tìm kiếm sản phẩm
        try {
            // Nếu từ khóa rỗng, hiển thị tất cả sản phẩm
            if (keyword.isEmpty()) {
                loadListProducts();
                return;
            }
            
            // Không cần lọc theo mã sản phẩm theo kiểu danh mục nữa
            // Áp dụng bộ lọc cho bảng
            TableUtils.applyFilter(productTableSorter, keyword);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(productForm, 
                    ErrorMessage.PRODUCT_SEARCH_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
        }
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
                        ErrorMessage.PRODUCT_SELECT_REQUIRED.get(),
                        ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (category == null) {
                JOptionPane.showMessageDialog(productForm, 
                        ErrorMessage.PRODUCT_CATEGORY_SELECT_REQUIRED.get(),
                        ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            
            // int stockQuantity = 0;
            BigDecimal price = BigDecimal.ZERO;
            
            String priceStr = productForm.getPriceField().getText().trim().replaceAll("\\.", "").replaceAll("\\,", "");

            if (!priceStr.isEmpty()) {
                try {
                    price = new BigDecimal(priceStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(productForm,
                            ErrorMessage.PRODUCT_PRICE_VALIDATION_ERROR.format(e.getMessage()),
                            ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Tạo mã sản phẩm mới dựa trên danh mục
            String productId = generateProductID();
            
            Product product = new Product();
            product.setProductId(productId);
            product.setProductName(productName);
            product.setCategory(category);
            product.setManufacturer(productForm.getTxtManufacturer().getText().trim());
            product.setStockQuantity(0);
            product.setPrice(price);
            product.setSpecifications(productForm.getSpecificationsArea().getText());
            product.setDescription(productForm.getDescriptionArea().getText());

            String costPriceStr = productForm.getTxtCostPrice().getText().trim().replaceAll("\\.", "").replaceAll("\\,", "");

            String profitMarginStr = productForm.getTxtProfit().getText().trim();
            
            BigDecimal costPrice = null;
            BigDecimal profitMargin = null;
            
            // Kiểm tra và chuyển đổi costPrice
            if (!costPriceStr.isEmpty()) {
                costPrice = new BigDecimal(costPriceStr);
                if (costPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new NumberFormatException(ErrorMessage.VALUE_MUST_BE_POSITIVE.toString());
                }
            }
            
            // Kiểm tra và chuyển đổi profitMargin
            if (!profitMarginStr.isEmpty()) {
                profitMargin = new BigDecimal(profitMarginStr);
                if (profitMargin.compareTo(BigDecimal.ZERO) < 0) {
                    throw new NumberFormatException("Rate of profit margin must be non-negative");
                }
            }
            
            // Thiết lập giá vốn và tỷ suất lợi nhuận
            product.setCostPrice(costPrice);
            product.setProfitMargin(profitMargin);
            
            // Tính giá vốn trung bình (ban đầu = giá vốn)
            if (costPrice != null) {
                product.setAverageCostPrice(costPrice);
                
                // Tính giá bán tự động nếu có giá vốn và tỷ suất lợi nhuận
                if (profitMargin != null) {
                    int option = JOptionPane.showConfirmDialog(
                        productForm,
                        ErrorMessage.PRODUCT_AUTOMATIC_PRICE_UPDATE_CONFIRM.get(),
                        ErrorMessage.CONFIRM_TITLE.get(),
                        JOptionPane.YES_NO_OPTION
                    );
                    
                    if (option == JOptionPane.YES_OPTION) {
                        price = product.calculateSellingPrice();
                        product.setPrice(price);
                    }
                }
            }

            int checkOpt = JOptionPane.showConfirmDialog(productForm, 
                    ErrorMessage.PRODUCT_ADD_CONFIRM.get(),
                    ErrorMessage.CONFIRM_BUTTON_ADD_TEXT.get(), JOptionPane.YES_NO_OPTION);
            if(checkOpt != JOptionPane.YES_OPTION) {
                return;
            }

            Product savedProduct = productRepository.add(product);
            
            if (savedProduct != null) {
                JOptionPane.showMessageDialog(productForm, 
                        ErrorMessage.PRODUCT_ADD_SUCCESS.get(),
                        ErrorMessage.INFO_TITLE.get(), JOptionPane.INFORMATION_MESSAGE);
                

                addingProduct = false;
                updateUIForAddMode(false);
                
                loadListProducts();

                // Lưu lịch sử giá nếu thêm mới thành công
                if (productId != null) { // Nếu lưu sản phẩm thành công
                    PriceHistory priceHistory = new PriceHistory(
                        productId,
                        BigDecimal.ZERO, 
                        price,
                        BigDecimal.ZERO,
                        costPrice,
                        SessionManager.getInstance().getCurrentUser().getEmployeeId(),
                        "Thêm mới sản phẩm"
                    );
                    priceHistoryRepository.save(priceHistory);
                }
            } else {
                JOptionPane.showMessageDialog(productForm, 
                        ErrorMessage.PRODUCT_ADD_ERROR.format(""),
                        ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(productForm, 
                    ErrorMessage.PRODUCT_ADD_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Tạo mã sản phẩm mới dựa trên danh mục
     */
    public String generateProductID() {
       return productRepository.generateProductID();
    }

    /**
     * Hủy chế độ thêm sản phẩm
     */
    public void cancelAddProduct() {
        try {
            if (addingProduct) {

                int checkOpt = JOptionPane.showConfirmDialog(productForm, 
                        ErrorMessage.PRODUCT_CANCEL_ADD_CONFIRM.get(),
                        ErrorMessage.PRODUCT_CANCEL_ADD_TITLE.get(), JOptionPane.YES_NO_OPTION);
                if (checkOpt != JOptionPane.YES_OPTION) {
                    return;
                }

                addingProduct = false;
                resetForm();
                updateUIForAddMode(false);
            }
        } catch (Exception e) {
            System.err.println(ErrorMessage.PRODUCT_CANCEL_ADD_ERROR.format(e.getMessage()));
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

                productForm.getTxtManufacturer().setText(product.getManufacturer() != null ? product.getManufacturer() : ErrorMessage.PRODUCT_N_A.get());

                NumberUtils.setFormattedValue(productForm.getTxtCostPrice(), product.getCostPrice(), currencyFormatter);
                NumberUtils.setFormattedValue(productForm.getPriceField(), product.getPrice(), currencyFormatter);

                // productForm.getTxtCostPrice().setText(product.getCostPrice() != null ? product.getCostPrice().toString() : "0");

                // productForm.getPriceField().setText(product.getPrice() != null ? product.getPrice().toString() : "0");

                productForm.getTxtProfit().setText(product.getProfitMargin() != null ? product.getProfitMargin().toString() : "0");

                // Xử lý thông số kỹ thuật - đảm bảo không null
                String specs = product.getSpecifications();
                productForm.getSpecificationsArea().setText(specs != null ? specs : "");
                
                // Xử lý mô tả - đảm bảo không null
                String desc = product.getDescription();
                productForm.getDescriptionArea().setText(desc != null ? desc : "");
            }
        } catch (Exception e) {
            System.err.println(ErrorMessage.ERROR_DISPLAYING_PRODUCT.format(e.getMessage()));
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
            System.err.println(ErrorMessage.PRODUCT_ADD_MODE_ERROR.format(e.getMessage()));
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
                    productForm.getBtnAdd().setText(ErrorMessage.CONFIRM_BUTTON_ADD_TEXT.get());
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
            System.err.println(ErrorMessage.PRODUCT_UI_UPDATE_ERROR.format(e.getMessage()));
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
                
                Notifications.getInstance().show(Notifications.Type.WARNING, ErrorMessage.PRODUCT_SELECT_REQUIRED.get());

                return;
            }

            Optional<Product> productOpt = productRepository.findById(productId);

            Integer quantity = 0;
            if (productOpt.isPresent()) {
                quantity = productOpt.get().getQuantityInStock();
            }

            
            // Lấy thông tin từ form
            String productName = productForm.getNameField().getText().trim();
            Category category = (Category) productForm.getCategoryComboBox().getSelectedItem();
            
            // Kiểm tra thông tin bắt buộc
            if (productName.isEmpty()) {
                Notifications.getInstance().show(Notifications.Type.WARNING, ErrorMessage.PRODUCT_SELECT_REQUIRED.get());
                return;
            }
            
            if (category == null) {
                Notifications.getInstance().show(Notifications.Type.WARNING, ErrorMessage.PRODUCT_CATEGORY_SELECT_REQUIRED.get());
                return;
            }
            
            // Các thông tin khác
            String manufacturerText = productForm.getTxtManufacturer().getText().trim();
            String priceStr = productForm.getPriceField().getText().trim().replaceAll("\\.", "").replaceAll("\\,", "");
            String specifications = productForm.getSpecificationsArea().getText().trim();
            String description = productForm.getDescriptionArea().getText().trim();
            
            // Kiểm tra và chuyển đổi kiểu dữ liệu
            // int stockQuantity = 0;
            BigDecimal price = BigDecimal.ZERO;
            
            try {
                if (!priceStr.isEmpty()) {
                    price = new BigDecimal(priceStr);
                    if (price.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException(ErrorMessage.VALUE_MUST_BE_POSITIVE.toString());
                } else {
                    throw new NumberFormatException(ErrorMessage.FIELD_EMPTY.toString().formatted("Giá"));
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(productForm,
                        ErrorMessage.PRODUCT_NUMBER_FORMAT_ERROR.format(e.getMessage()),
                        ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String costPriceStr = productForm.getTxtCostPrice().getText().trim().replaceAll("\\.", "").replaceAll("\\,", "");

            String profitMarginStr = productForm.getTxtProfit().getText().trim();
            
            BigDecimal costPrice = null;
            BigDecimal profitMargin = null;
            
            // Kiểm tra và chuyển đổi costPrice
            if (!costPriceStr.isEmpty()) {
                costPrice = new BigDecimal(costPriceStr);
                if (costPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new NumberFormatException(ErrorMessage.VALUE_MUST_BE_POSITIVE.toString());
                }
            }
            
            // Kiểm tra và chuyển đổi profitMargin
            if (!profitMarginStr.isEmpty()) {
                profitMargin = new BigDecimal(profitMarginStr);
                if (profitMargin.compareTo(BigDecimal.ZERO) < 0) {
                    throw new NumberFormatException("Rate of profit margin must be non-negative");
                }
            }
            
            // Lấy thông tin sản phẩm cũ để so sánh
            Product oldProduct = productRepository.findById(productId).orElse(null);
            if (oldProduct == null) {
                throw new Exception("No product found with ID: " + productId);
            }
            
            // Tạo đối tượng sản phẩm để cập nhật
            Product product = new Product();
            product.setProductId(productId);
            product.setProductName(productName);
            product.setCategory(category);
            product.setPrice(price);
            product.setStockQuantity(quantity);
            product.setManufacturer(manufacturerText);
            product.setCategory(category);
            product.setSpecifications(specifications);
            product.setDescription(description);
            
            // Thiết lập giá vốn và tỷ suất lợi nhuận
            product.setCostPrice(costPrice);
            product.setProfitMargin(profitMargin);
            
            // Giữ nguyên giá vốn trung bình nếu chỉ cập nhật thông tin khác
            product.setAverageCostPrice(oldProduct.getAverageCostPrice());
            
            // Tính giá bán tự động nếu có sự thay đổi giá vốn hoặc tỷ suất lợi nhuận
            boolean costPriceChanged = costPrice != null && 
                (oldProduct.getCostPrice() == null || costPrice.compareTo(oldProduct.getCostPrice()) != 0);
                
            boolean profitMarginChanged = profitMargin != null && 
                (oldProduct.getProfitMargin() == null || profitMargin.compareTo(oldProduct.getProfitMargin()) != 0);
            
            if (costPriceChanged || profitMarginChanged) {
                // Hỏi người dùng có muốn tính giá bán tự động không
                int option = JOptionPane.showConfirmDialog(
                    productForm,
                    ErrorMessage.PRODUCT_AUTOMATIC_PRICE_CONFIRM.get(),
                    ErrorMessage.CONFIRM_TITLE.get(),
                    JOptionPane.YES_NO_OPTION
                );
                
                if (option == JOptionPane.YES_OPTION) {
                    price = product.calculateSellingPrice();
                    product.setPrice(price);
                }
            }
            
            // Cập nhật sản phẩm
            Product result = productRepository.update(product);
            
            // Lưu lịch sử giá nếu có sự thay đổi giá hoặc giá vốn
            boolean priceChanged = price.compareTo(oldProduct.getPrice()) != 0;
            
            if (result != null && (priceChanged || costPriceChanged)) {
                PriceHistory priceHistory = new PriceHistory(
                    productId,
                    oldProduct.getPrice(),
                    price,
                    oldProduct.getCostPrice(),
                    costPrice,
                    SessionManager.getInstance().getCurrentUser().getEmployeeId(),
                    "Cập nhật thông tin sản phẩm"
                );
                priceHistoryRepository.save(priceHistory);
            }
            
            Notifications.getInstance().show(Notifications.Type.SUCCESS, ErrorMessage.PRODUCT_UPDATE_SUCCESS.get());

            resetForm();
            
            loadListProducts();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(productForm,
                    ErrorMessage.PRODUCT_UPDATE_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
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
                    ErrorMessage.PRODUCT_DELETE_CONFIRM.get(), 
                    ErrorMessage.CONFIRM_TITLE.get(), 
                    JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    productRepository.delete(productId);
                    loadListProducts();
                    resetForm();

                    Notifications.getInstance().show(Notifications.Type.SUCCESS, ErrorMessage.PRODUCT_DELETE_SUCCESS.get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(productForm, ErrorMessage.PRODUCT_DELETE_ERROR.format(e.getMessage()), 
                            ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(productForm, ErrorMessage.PRODUCT_SELECT_TO_DELETE.get(), 
                    ErrorMessage.INFO_TITLE.get(), JOptionPane.WARNING_MESSAGE);
        }
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
            JOptionPane.showMessageDialog(productForm, ErrorMessage.PRODUCT_SEARCH_ERROR.format(e.getMessage()), 
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
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
                        product.getManufacturer() != null ? product.getManufacturer() : "",
                        product.getStockQuantity(),
                        product.getCostPrice(),
                        product.getPrice(),
                        product.getProfitMargin()
                    });
                    
                } catch (Exception e) {
                    // System.err.println("Lỗi khi thêm sản phẩm " + product.getProductId() + ": " + e.getMessage());
                    Notifications.getInstance().show(Type.ERROR,
                            ErrorMessage.PRODUCT_ADD_ERROR.format(product.getProductId()));
                }
            }
            
            // System.out.println("Số dòng trong bảng sau khi cập nhật: " + model.getRowCount());
        } catch (Exception e) {
            // System.err.println("Lỗi khi cập nhật bảng: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Khởi tạo dữ liệu cho form
     */
    private void initializeFormData() {
        try {
            productTableSorter = TableUtils.applyDefaultStyle(productForm.getTable());
            TableUtils.setNumberColumns(productTableSorter, 4, 5, 6, 7);

            loadCategories();
            
            generateTemporaryProductId();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(productForm,
                    ErrorMessage.PRODUCT_INIT_DATA_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
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
                            ErrorMessage.PRODUCT_CATEGORY_LOAD_ERROR.format(e.getMessage()),
                            ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
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
                String productID = productRepository.generateProductID();
                productForm.getIdField().setText(productID); // Hiển thị mã tạm thời
            } else {
                productForm.getIdField().setText("xxxxx"); // Mẫu mặc định
            }
        }
    }

        /**
     * Tính giá bán dựa trên giá vốn và tỷ suất lợi nhuận
     */
    public void calculateSellingPrice() {
        try {
            // Lấy giá trị từ các trường nhập liệu và loại bỏ dấu phân cách hàng nghìn
            String costPriceStr = productForm.getTxtCostPrice().getText().trim().replaceAll("\\.", "").replaceAll("\\,", "");
            String profitMarginStr = productForm.getTxtProfit().getText().trim();
            
            if (costPriceStr.isEmpty()) {
                Notifications.getInstance().show(Type.ERROR, ErrorMessage.PRODUCT_COST_PRICE_EMPTY_ERROR.get());
                return;
            }
    
            if (profitMarginStr.isEmpty()) {
                Notifications.getInstance().show(Type.ERROR, ErrorMessage.PRODUCT_PROFIT_MARGIN_EMPTY_ERROR.get());
                profitMarginStr = "0";
            }
            
            // Chuyển đổi các chuỗi thành số
            BigDecimal costPrice = new BigDecimal(costPriceStr);
            BigDecimal profitMargin = new BigDecimal(profitMarginStr);
            
            if (profitMargin.compareTo(BigDecimal.ZERO) < 0) {
                Notifications.getInstance().show(Type.ERROR, ErrorMessage.PRODUCT_PROFIT_MARGIN_NEGATIVE_ERROR.get());
                return;
            }
            
            // Tính giá bán
            BigDecimal factor = BigDecimal.ONE.add(profitMargin.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            BigDecimal sellingPrice = costPrice.multiply(factor).setScale(0, RoundingMode.CEILING);
            // Cập nhật trường giá bán
            productForm.getPriceField().setText(sellingPrice.toString());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(productForm,
        ErrorMessage.PRODUCT_SELLING_PRICE_VALIDATION_ERROR.get(),
        ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /*
     * Tính tỉ suất lợi nhuận dựa trên giá vốn và giá bán
     */
    public void calculateProfitMargin() {
        try {
            // Lấy giá trị từ các trường nhập liệu và loại bỏ dấu phân cách hàng nghìn
            String costPriceStr = productForm.getTxtCostPrice().getText().trim().replaceAll("\\.", "").replaceAll("\\,", "");
            String sellingPriceStr = productForm.getPriceField().getText().trim().replaceAll("\\.", "").replaceAll("\\,", "");
            
            if (costPriceStr.isEmpty()) {
                Notifications.getInstance().show(Type.ERROR, ErrorMessage.PRODUCT_COST_PRICE_EMPTY_ERROR.get());
                return;
            }
    
            if (sellingPriceStr.isEmpty()) {
                Notifications.getInstance().show(Type.ERROR, ErrorMessage.PRODUCT_SELLING_PRICE_EMPTY_ERROR.get());
                return;
            }
            
            // Chuyển đổi các chuỗi thành số
            BigDecimal costPrice = new BigDecimal(costPriceStr);
            BigDecimal sellingPrice = new BigDecimal(sellingPriceStr);
            
            if (costPrice.compareTo(BigDecimal.ZERO) <= 0) {
                Notifications.getInstance().show(Type.ERROR, ErrorMessage.PRODUCT_COST_PRICE_VALIDATION_ERROR.get());
                return;
            }
            
            // Tính tỷ suất lợi nhuận
            BigDecimal profitMargin = sellingPrice.subtract(costPrice).divide(costPrice, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
            
            // Cập nhật trường tỷ suất lợi nhuận
            productForm.getTxtProfit().setText(profitMargin.toString());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(productForm, 
                ErrorMessage.PRODUCT_SELLING_PRICE_VALIDATION_ERROR.get(), 
                ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút cập nhật
     */
    public void handleUpdateButtonClick() {
        int confirm = JOptionPane.showConfirmDialog(productForm,
                ErrorMessage.PRODUCT_UPDATE_CONFIRM.get(),
                ErrorMessage.PRODUCT_CONFIRM_UPDATE_TITLE.get(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            updateProduct();
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút reset sắp xếp
     */
    public void handleResetSortClick() {
        productForm.getCbbSortCustomer().setSelectedIndex(0);
        productForm.getCbbSort().setSelectedIndex(0);
        loadListProducts();
    }

    /**
     * Xử lý sự kiện khi thay đổi các trường sắp xếp
     */
    public void handleSortFieldChange() {
        String sortField = productForm.getCbbSortCustomer().getSelectedItem().toString();
        String sortOrder = productForm.getCbbSort().getSelectedItem().toString();
        
        if (!"<Không>".equals(sortField) && !"<Không>".equals(sortOrder)) {
            sortProducts(sortField, sortOrder);
        }
    }

    /**
     * Reset form nhập liệu
     */
    private void resetForm() {
        // Xóa dữ liệu trong form
        productForm.getNameField().setText("");
        productForm.getTxtManufacturer().setText(ErrorMessage.PRODUCT_N_A.get());
        productForm.getPriceField().setText("0");
        productForm.getSpecificationsArea().setText("");
        productForm.getDescriptionArea().setText("");
        productForm.getLabelESC().setVisible(false);

        productForm.getTable().clearSelection();
        productForm.getCbbSortCustomer().setSelectedIndex(0);
        productForm.getCbbSort().setSelectedIndex(0);
        productForm.getTextFieldSearch().setText("");
        productForm.getTxtCostPrice().setText("");
        // Chỉ tạo mã sản phẩm tạm thời khi đang ở chế độ thêm
        if (addingProduct) {
            generateTemporaryProductId();
        } else {
            productForm.getIdField().setText(""); 
        }

        productForm.getNameField().requestFocus();
        loadListProducts();
        loadCategories();
    }

    /**
     * Xuất danh sách sản phẩm ra file Excel
     */
    public void exportToExcel() {
        try {
            if (productRepository == null) {
                JOptionPane.showMessageDialog(productForm, ErrorMessage.PRODUCT_NO_CONNECTION.get(), 
                        ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            List<Product> products = productRepository.findAll();
            if (products == null || products.isEmpty()) {
                JOptionPane.showMessageDialog(productForm, 
                        ErrorMessage.PRODUCT_EXPORT_NO_DATA.get(),
                        ErrorMessage.INFO_TITLE.get(), JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Tạo dữ liệu xuất ra Excel
            String[] headers = {"STT", "Mã sản phẩm", "Tên sản phẩm", "Danh mục", "Số lượng", 
                               "Giá vốn", "Giá bán", "Lợi nhuận", "Thông số kỹ thuật", "Mô tả"};

            if (LocaleManager.getInstance().getCurrentLocale().equals("vi")) {
                headers = new String[]{"STT", "Mã sản phẩm", "Tên sản phẩm", "Danh mục", "Số lượng",
                                       "Giá vốn", "Giá bán", "Lợi nhuận", "Thông số kỹ thuật", "Mô tả"};
            } else {
                headers = new String[]{"No.", "Product ID", "Product Name", "Category", "Stock Quantity", 
                                       "Cost Price", "Selling Price", "Profit", "Specifications", "Description"};
            }

            Object[][] data = new Object[products.size()][headers.length];
            
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                data[i][0] = i + 1; // STT bắt đầu từ 1
                data[i][1] = product.getProductId();
                data[i][2] = product.getProductName();
                data[i][3] = product.getCategory() != null ? product.getCategory().getCategoryName() : "";
                data[i][4] = product.getStockQuantity();
                data[i][5] = product.getCostPrice();
                data[i][6] = product.getPrice() != null ? product.getPrice() : "";
                data[i][7] = product.getProfitMargin() != null ? product.getProfitMargin() : "";
                data[i][8] = product.getSpecifications() != null ? product.getSpecifications() : "";
                data[i][9] = product.getDescription() != null ? product.getDescription() : "";
            }
            
            // Tạo file Excel
            String fileName = "DANH_SACH_SAN_PHAM_" + 
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            
            com.pcstore.utils.JExcel jExcel = new com.pcstore.utils.JExcel();
            boolean success = jExcel.toExcel(headers, data, "Danh sách sản phẩm", fileName);
            
            if (success) {
                JOptionPane.showMessageDialog(productForm,
                        ErrorMessage.PRODUCT_EXPORT_SUCCESS.get(),
                        ErrorMessage.INFO_TITLE.get(), JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(productForm,
                        ErrorMessage.PRODUCT_EXPORT_FAILED.get(),
                        ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(productForm, 
                    ErrorMessage.PRODUCT_EXPORT_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void refereshForm(){
        loadCategories();
        loadListProducts();
        try {
            productForm.resetForm();
            if (addingProduct) {
                generateTemporaryProductId();
            } else {
                productForm.getIdField().setText(""); 
            }

            productForm.getNameField().requestFocus();
        } catch (Exception e) {
            System.err.println(ErrorMessage.PRODUCT_REFRESH_ERROR.format(e.getMessage()));
        }
    }
}