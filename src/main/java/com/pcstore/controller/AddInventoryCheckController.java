package com.pcstore.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.pcstore.utils.DatabaseConnection;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.model.Category;
import com.pcstore.model.Employee;
import com.pcstore.model.InventoryCheck;
import com.pcstore.model.InventoryCheckDetail;
import com.pcstore.model.Product;
import com.pcstore.service.CategoryService;
import com.pcstore.service.EmployeeService;
import com.pcstore.service.InventoryCheckService;
import com.pcstore.service.ProductService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.utils.TableUtils;
import com.pcstore.view.AddInventoryCheckForm;

import raven.toast.Notifications;

/**
 * Controller điều khiển form thêm phiếu kiểm kê
 */
public class AddInventoryCheckController {
    private AddInventoryCheckForm view;
    private InventoryCheckService inventoryCheckService;
    private ProductService productService;
    private CategoryService categoryService;
    private EmployeeService employeeService;
    private Connection connection;

    // Danh sách sản phẩm đã chọn để kiểm kê
    private List<Product> selectedProducts;

    private TableRowSorter<TableModel> tableRowSorter;

    private final NumberFormat currencyFormat = LocaleManager.getInstance().getCurrencyFormatter();
    private final DecimalFormat numberFormat = new DecimalFormat("#,###");

    public AddInventoryCheckController(AddInventoryCheckForm view) {
        this.view = view;
        this.selectedProducts = new ArrayList<>();
        initializeServices();
        initializeView();
        setupEventListeners();
        loadInitialData();
    }

    private void initializeServices() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            this.inventoryCheckService = ServiceFactory.getInstance().getInventoryCheckService();
            this.productService = ServiceFactory.getInstance().getProductService();
            this.categoryService = ServiceFactory.getInstance().getCategoryService();
            this.employeeService = ServiceFactory.getInstance().getEmployeeService();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.ADD_INVENTORY_CHECK_INIT_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.ADD_INVENTORY_CHECK_DB_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void initializeView() {
        setupProductTable();

        view.getTxtCreateDate().setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

        String defaultName = ErrorMessage.ADD_INVENTORY_CHECK_DEFAULT_DATE_NAME.get() + " " + new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        view.getTxtInventoryName().setText(defaultName);
    }

    private void setupProductTable() {
        tableRowSorter = TableUtils.applyDefaultStyle(view.getTableProducts());

        TableUtils.addDeleteButton(view.getTableProducts(), 7,
                (table, modelRow, column, value) -> {
                    handleRemoveProduct(modelRow);
                });

        TableUtils.setupColumnWidths(view.getTableProducts(),
                40, 200, 120, 120, 80, 100, 120, 20);
    }

    private void setupEventListeners() {
        // nút tạo phiếu kiểm kê
        view.getBtnCreate().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCreateInventoryCheck();
            }
        });

        // nút hủy
        view.getBtnCreate1().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCancel();
            }
        });

        // checkbox "Kiểm toàn bộ hàng trong kho"
        view.getChkSelectAll().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSelectAllProducts();
            }
        });

        // Sự kiện thay đổi danh mục
        view.getCbbCategory().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    loadProductsByCategory();
                }
            }
        });

        // Sự kiện thay đổi sản phẩm được chọn
        view.getCbbProduct().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    handleProductSelection();
                }
            }
        });

        // Sự kiện xuất Excel
        view.getBtnExportExcel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleExportExcel();
            }
        });

        // Sự kiện tìm kiếm sản phẩm
        view.getTextFieldSearch().getTxtSearchField().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                performProductSearch();
            }
        });

        // Sự kiện chọn ngày
        view.getBtnChooseDate().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getDateChooser().showPopup();
            }
        });
    }

    private void loadInitialData() {
        loadCategories();
        loadEmployees();
        loadProducts();
    }

    /**
     * Tải danh sách danh mục
     */
    private void loadCategories() {
        try {
            view.getCbbCategory().removeAllItems();
            view.getCbbCategory().addItem(ErrorMessage.ADD_INVENTORY_CHECK_ALL_CATEGORIES.get());

            List<Category> categories = categoryService.getAllCategories();
            for (Category category : categories) {
                view.getCbbCategory().addItem(category.getCategoryName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.ADD_INVENTORY_CHECK_LOAD_CATEGORIES_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Tải danh sách nhân viên
     */
    private void loadEmployees() {
        try {
            view.getCbbChecker().removeAllItems();

            List<Employee> employees = employeeService.findAllEmployees();
            for (Employee employee : employees) {
                view.getCbbChecker().addItem(employee.getFullName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.ADD_INVENTORY_CHECK_LOAD_EMPLOYEES_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Tải danh sách sản phẩm
     */
    private void loadProducts() {
        try {
            view.getCbbProduct().removeAllItems();
            view.getCbbProduct().addItem(ErrorMessage.ADD_INVENTORY_CHECK_SELECT_PRODUCT.get());

            List<Product> products = productService.findAllProducts();
            for (Product product : products) {
                view.getCbbProduct().addItem(product.getId() + " - " + product.getProductName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.ADD_INVENTORY_CHECK_LOAD_PRODUCTS_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Tải sản phẩm theo danh mục
     */
    private void loadProductsByCategory() {
        try {
            String selectedCategory = (String) view.getCbbCategory().getSelectedItem();

            view.getCbbProduct().removeAllItems();
            view.getCbbProduct().addItem(ErrorMessage.ADD_INVENTORY_CHECK_SELECT_PRODUCT.get());

            List<Product> products;
            if (ErrorMessage.ADD_INVENTORY_CHECK_ALL_CATEGORIES.get().equals(selectedCategory)) {
                products = productService.findAllProducts();
            } else {
                // Tìm category ID theo tên
                List<Category> categories = categoryService.getAllCategories();
                String categoryId = null;
                for (Category category : categories) {
                    if (category.getCategoryName().equals(selectedCategory)) {
                        categoryId = category.getCategoryId();
                        break;
                    }
                }

                if (categoryId != null) {
                    products = productService.findProductsByCategory(categoryId);
                } else {
                    products = new ArrayList<>();
                }
            }

            for (Product product : products) {
                view.getCbbProduct().addItem(product.getId() + " - " + product.getProductName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.ADD_INVENTORY_CHECK_LOAD_PRODUCTS_BY_CATEGORY_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Xử lý chọn sản phẩm
     */
    private void handleProductSelection() {
        try {
            String selectedProductName = (String) view.getCbbProduct().getSelectedItem().toString().split(" - ")[0];

            if (selectedProductName == null || ErrorMessage.ADD_INVENTORY_CHECK_SELECT_PRODUCT.get().equals(selectedProductName)) {
                return;
            }

            Product selectedProduct = productService.findProductById(selectedProductName)
                    .orElse(null);

            if (selectedProduct != null) {
                addProductToTable(selectedProduct);
                view.getCbbProduct().setSelectedIndex(0);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.ADD_INVENTORY_CHECK_ADD_PRODUCT_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Thêm sản phẩm vào bảng
     */
    private void addProductToTable(Product product) {
        for (Product p : selectedProducts) {
            if (p.getProductId().equals(product.getProductId())) {
                Notifications.getInstance().show(Notifications.Type.ERROR,
                        ErrorMessage.ADD_INVENTORY_CHECK_PRODUCT_ALREADY_ADDED.format(product.getProductName()));
                return;
            }
        }

        selectedProducts.add(product);
        updateProductTable();
        updateSummary();
    }

    /**
     * Cập nhật bảng sản phẩm
     */
    private void updateProductTable() {
        DefaultTableModel model = (DefaultTableModel) view.getTableProducts().getModel();
        model.setRowCount(0);

        int index = 1;
        for (Product product : selectedProducts) {
            BigDecimal totalValue = product.getPrice().multiply(new BigDecimal(product.getQuantityInStock()));

            Object[] row = new Object[] {
                    index++,
                    product.getProductName(),
                    product.getProductId(),
                    // product.getBarcode() != null ? product.getBarcode() : "",
                    ErrorMessage.ADD_INVENTORY_CHECK_BARCODE_PLACEHOLDER.get(),
                    numberFormat.format(product.getQuantityInStock()),
                    currencyFormat.format(product.getPrice()),
                    currencyFormat.format(totalValue),
                    null // Nút xóa
            };
            model.addRow(row);
        }
        view.toggleEmptyTableMessage(false);
    }

    /**
     * Xử lý xóa sản phẩm khỏi danh sách
     */
    private void handleRemoveProduct(int modelRow) {
        if (modelRow >= 0 && modelRow < selectedProducts.size()) {
            Product removedProduct = selectedProducts.get(modelRow);

            int result = JOptionPane.showConfirmDialog(view,
                    ErrorMessage.ADD_INVENTORY_CHECK_REMOVE_PRODUCT_CONFIRM.format(removedProduct.getProductName()),
                    ErrorMessage.ADD_INVENTORY_CHECK_REMOVE_PRODUCT_TITLE.toString(),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                selectedProducts.remove(modelRow);
                updateProductTable();
                updateSummary();
            }
        }
    }

    /**
     * Xử lý chọn tất cả sản phẩm
     */
    private void handleSelectAllProducts() {
        if (view.getChkSelectAll().isSelected()) {
            try {
                List<Product> allProducts = productService.findAllProducts();
                selectedProducts.clear();
                selectedProducts.addAll(allProducts);

                updateProductTable();
                updateSummary();

                view.getCbbCategory().setEnabled(false);
                view.getCbbProduct().setEnabled(false);

                Notifications.getInstance().show(Notifications.Type.INFO,
                        ErrorMessage.ADD_INVENTORY_CHECK_SELECT_ALL_SUCCESS.format(allProducts.size()));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view,
                        ErrorMessage.ADD_INVENTORY_CHECK_SELECT_ALL_ERROR.format(e.getMessage()),
                        ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
                view.getChkSelectAll().setSelected(false);
                e.printStackTrace();
            }
        } else {
            // Kích hoạt lại các control chọn sản phẩm
            view.getCbbCategory().setEnabled(true);
            view.getCbbProduct().setEnabled(true);

            selectedProducts.clear();
            updateProductTable();
            updateSummary();
        }
    }

    /**
     * Cập nhật thông tin tổng kết
     */
    private void updateSummary() {
        view.getLbTotalValue().setText(String.valueOf(selectedProducts.size()));
    }

    /**
     * Thực hiện tìm kiếm sản phẩm
     */
    private void performProductSearch() {
        // TODO: Implement search functionality in product table
        String searchText = view.getTextFieldSearch().getText().trim();
        TableUtils.applyFilter(tableRowSorter, searchText, 1, 2, 3, 4, 5, 6);
    }

    /**
     * Xử lý tạo phiếu kiểm kê
     */
    private void handleCreateInventoryCheck() {
        try {
            if (!validateInput()) {
                return;
            }

            String checkName = view.getTxtInventoryName().getText().trim();
            String employeeName = (String) view.getCbbChecker().getSelectedItem();
            String notes = view.getTxtNotes().getText().trim();

            List<Employee> employees = employeeService.findAllEmployees();
            Employee selectedEmployee = null;
            for (Employee emp : employees) {
                if (emp.getFullName().equals(employeeName)) {
                    selectedEmployee = emp;
                    break;
                }
            }

            if (selectedEmployee == null) {
                JOptionPane.showMessageDialog(view,
                        ErrorMessage.ADD_INVENTORY_CHECK_EMPLOYEE_NOT_FOUND.toString(),
                        ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
                return;
            }

            int result = JOptionPane.showConfirmDialog(view,
                    ErrorMessage.ADD_INVENTORY_CHECK_CREATE_CONFIRM.format(selectedProducts.size()),
                    ErrorMessage.ADD_INVENTORY_CHECK_CREATE_CONFIRM_TITLE.toString(),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (result != JOptionPane.YES_OPTION) {
                return;
            }

            InventoryCheck inventoryCheck = inventoryCheckService.createInventoryCheck(
                    checkName,
                    selectedEmployee.getEmployeeId(),
                    view.getChkSelectAll().isSelected() ? "FULL" : "PARTIAL",
                    notes);

            for (Product product : selectedProducts) {
                inventoryCheckService.addCheckDetail(
                        inventoryCheck.getId(),
                        product.getProductId(),
                        product.getQuantityInStock(),
                        "");
            }

            Notifications.getInstance().show(Notifications.Type.SUCCESS, 4000,
                    ErrorMessage.ADD_INVENTORY_CHECK_CREATE_SUCCESS.format(
                            inventoryCheck.getCheckCode(), selectedProducts.size()));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.ADD_INVENTORY_CHECK_CREATE_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Validate dữ liệu đầu vào
     */
    private boolean validateInput() {
        if (view.getTxtInventoryName().getText().trim().isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.ERROR,
                    ErrorMessage.ADD_INVENTORY_CHECK_NAME_EMPTY.toString());
            view.getTxtInventoryName().requestFocus();
            return false;
        }

        if (view.getCbbChecker().getSelectedItem() == null) {
            Notifications.getInstance().show(Notifications.Type.ERROR,
                    ErrorMessage.ADD_INVENTORY_CHECK_EMPLOYEE_EMPTY.toString());
            view.getCbbChecker().requestFocus();
            return false;
        }

        if (selectedProducts.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.ERROR,
                    ErrorMessage.ADD_INVENTORY_CHECK_PRODUCTS_EMPTY.toString());
            return false;
        }

        return true;
    }

    /**
     * Xử lý hủy tạo phiếu kiểm kê
     */
    private void handleCancel() {
        if (!selectedProducts.isEmpty()) {
            int result = JOptionPane.showConfirmDialog(view,
                    ErrorMessage.ADD_INVENTORY_CHECK_CANCEL_CONFIRM.toString(),
                    ErrorMessage.ADD_INVENTORY_CHECK_CANCEL_CONFIRM_TITLE.toString(), 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        view.dispose();
    }

    /**
     * Xử lý xuất Excel
     */
    private void handleExportExcel() {
        if (selectedProducts.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                ErrorMessage.ADD_INVENTORY_CHECK_EXPORT_NO_DATA.toString(),
                ErrorMessage.INFO_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JOptionPane.showMessageDialog(view,
            ErrorMessage.ADD_INVENTORY_CHECK_EXPORT_DEVELOPING.toString(),
            ErrorMessage.INFO_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Làm mới form
     */
    public void refreshForm() {
        selectedProducts.clear();
        updateProductTable();
        updateSummary();

        view.getChkSelectAll().setSelected(false);
        view.getCbbCategory().setSelectedIndex(0);
        view.getCbbProduct().setSelectedIndex(0);
        view.getTxtInventoryName().setText(ErrorMessage.ADD_INVENTORY_CHECK_DEFAULT_DATE_NAME.get() + " " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        view.getTxtNotes().setText("");

        loadInitialData();
    }

    /**
     * Lấy danh sách sản phẩm đã chọn
     */
    public List<Product> getSelectedProducts() {
        return new ArrayList<>(selectedProducts);
    }

    /**
     * Thiết lập danh sách sản phẩm đã chọn
     */
    public void setSelectedProducts(List<Product> products) {
        this.selectedProducts.clear();
        this.selectedProducts.addAll(products);
        updateProductTable();
        updateSummary();
    }

    /**
     * Cleanup resources
     */
    public void dispose() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InventoryCheckService getInventoryCheckService() {
        return inventoryCheckService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public CategoryService getCategoryService() {
        return categoryService;
    }

    public EmployeeService getEmployeeService() {
        return employeeService;
    }
}