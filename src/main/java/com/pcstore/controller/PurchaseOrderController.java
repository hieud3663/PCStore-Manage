package com.pcstore.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.pcstore.model.Employee;
import com.pcstore.model.Product;
import com.pcstore.model.PurchaseOrder;
import com.pcstore.model.PurchaseOrderDetail;
import com.pcstore.model.Supplier;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.repository.impl.ProductRepository;
import com.pcstore.repository.impl.PurchaseOrderDetailRepository;
import com.pcstore.repository.impl.PurchaseOrderRepository;
import com.pcstore.repository.impl.SupplierRepository;
import com.pcstore.utils.BillPrintUtils;
import com.pcstore.utils.DatabaseConnection;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.JDialogInputUtils;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.SessionManager;
import com.pcstore.utils.TableUtils;
import com.pcstore.view.PurchaseOrderForm;

import raven.toast.Notifications;
import raven.toast.Notifications.Type;

/**
 * Controller để quản lý việc nhập hàng
 */
public class PurchaseOrderController {
    private PurchaseOrderForm purchaseOrderForm;
    private PurchaseOrderRepository purchaseOrderRepository;
    private PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    private ProductRepository productRepository;
    private SupplierRepository supplierRepository;
    private Connection connection;
    private RepositoryFactory repositoryFactory;

    // Danh sách các sản phẩm đã chọn (giỏ hàng)
    private List<PurchaseOrderDetail> selectedProducts = new ArrayList<>();
    private PurchaseOrder currentPurchaseOrder;
    private Employee currentEmployee;
    private TableRowSorter<TableModel> productTableSorter;

    private final NumberFormat currencyFormat = LocaleManager.getInstance().getCurrencyFormatter();

    /**
     * Khởi tạo controller với form nhập hàng và kết nối được chia sẻ
     * 
     * @param purchaseOrderForm Form nhập hàng
     * @param sharedConnection  Kết nối database dùng chung
     */
    public PurchaseOrderController(PurchaseOrderForm purchaseOrderForm, Connection sharedConnection) {
        this.purchaseOrderForm = purchaseOrderForm;

        try {
            // Sử dụng kết nối được truyền vào
            this.connection = sharedConnection;
            // System.out.println("StockInController: Đã nhận kết nối được chia sẻ");

            // Khởi tạo các repository
            initRepositories();

            // Khởi tạo phiếu nhập hàng mới
            initializeNewPurchaseOrder();

            // Đăng ký các sự kiện
            registerEvents();

            // Tải dữ liệu ban đầu
            loadInitialData();

            initTableStyle();

            // System.out.println("StockInController: Khởi tạo hoàn tất");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(purchaseOrderForm,
                    ErrorMessage.PURCHASE_ORDER_CONTROLLER_INIT_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Khởi tạo các repository
     */
    private void initRepositories() {
        try {
            // Kiểm tra kết nối
            ensureConnection();

            // Tạo RepositoryFactory
            this.repositoryFactory = new RepositoryFactory(connection);
            // System.out.println("StockInController: Đã tạo RepositoryFactory");

            // Khởi tạo các repository
            this.productRepository = new ProductRepository(connection);
            this.supplierRepository = new SupplierRepository(connection);
            this.purchaseOrderRepository = new PurchaseOrderRepository(connection, repositoryFactory);
            this.purchaseOrderDetailRepository = new PurchaseOrderDetailRepository(connection, repositoryFactory);

            // System.out.println("StockInController: Đã khởi tạo repository thành công");
        } catch (Exception e) {
            System.err.println(ErrorMessage.PURCHASE_ORDER_REPOSITORY_INIT_ERROR.format(e.getMessage()));
            e.printStackTrace();
        }
    }

    private void initializeNewPurchaseOrder() {
        try {
            // Khởi tạo phiếu nhập hàng mới
            currentPurchaseOrder = new PurchaseOrder();

            // Tạo mã phiếu nhập mới
            String newId = purchaseOrderRepository.generatePurchaseOrderId();
            currentPurchaseOrder.setPurchaseOrderId(newId);

            // Hiển thị mã phiếu nhập trên form
            purchaseOrderForm.getTextField2().setText(newId);

            // Thiết lập ngày tạo và trạng thái
            currentPurchaseOrder.setOrderDate(LocalDateTime.now());
            currentPurchaseOrder.setStatus("Pending");

            // Xóa danh sách sản phẩm đã chọn
            selectedProducts.clear();
            updateSelectedProductsTable(); // Cập nhật bảng giỏ hàng (trống)

            System.out.println("StockInController: Đã khởi tạo phiếu nhập hàng mới với ID: " + newId);
        } catch (Exception e) {
            System.err.println(ErrorMessage.PURCHASE_ORDER_NEW_ORDER_INIT_ERROR.format(e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * Tải dữ liệu ban đầu
     */
    private void loadInitialData() {
        try {
            // Kiểm tra kết nối trước khi tải dữ liệu
            ensureConnection();

            // Tải danh sách sản phẩm
            loadProducts();

            // Tải danh sách nhà cung cấp
            loadSuppliers();

            // Tạo bảng rỗng cho sản phẩm đã chọn (giỏ hàng)
            updateSelectedProductsTable();

            // Thiết lập thông tin nhân viên
            getCurrentEmployee();

            System.out.println("StockInController: Đã tải dữ liệu ban đầu thành công");
        } catch (Exception e) {
            System.err.println(ErrorMessage.PURCHASE_ORDER_LOAD_INITIAL_DATA_ERROR.format(e.getMessage()));
            e.printStackTrace();
        }
    }

    private void initTableStyle() {
        // Khởi tạo sorter cho bảng sản phẩm
        productTableSorter = TableUtils.applyDefaultStyle(purchaseOrderForm.getTableProducts());
        TableUtils.applyDefaultStyle(purchaseOrderForm.getTableSelectedProducts());
        TableUtils.setBooleanColumns(purchaseOrderForm.getTableSelectedProducts(), 0);
        TableUtils.setNumberColumns(productTableSorter, 2, 3);
        TableUtils.applyProductTableStyle(purchaseOrderForm.getTableProducts(), 3);
    }

    /**
     * Đăng ký các sự kiện
     */
    private void registerEvents() {
        try {
            // Đăng ký sự kiện search (giữ nguyên)
            purchaseOrderForm.getTextFieldSearch().getTxtSearchField().addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    searchProducts();
                }
            });

            // Thêm sự kiện cho nút tìm kiếm (giữ nguyên)
            purchaseOrderForm.getTextFieldSearch().getBtnSearch().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    searchProducts();
                }
            });

            // Đăng ký sự kiện phím F5 toàn cục để refresh form
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
                @Override
                public boolean dispatchKeyEvent(KeyEvent e) {
                    // Chỉ xử lý khi form đang hiển thị
                    if (!purchaseOrderForm.isVisible()) {
                        return false;
                    }

                    // Kiểm tra phím F5 và chỉ xử lý khi phím được nhấn (KEY_PRESSED)
                    if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_F5) {
                        refreshForm();
                        return true; // Đánh dấu sự kiện đã được xử lý
                    }
                    return false; // Cho phép các bộ xử lý khác xử lý sự kiện
                }
            });

            // Đăng ký sự kiện cho nút Nhập hàng
            purchaseOrderForm.getBtnStockIn().addActionListener(e -> savePurchaseOrder());

            // Đăng ký sự kiện cho bảng sản phẩm (bảng bên trái)
            purchaseOrderForm.getTableProducts().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                        // Khi double-click vào sản phẩm, thêm vào giỏ hàng
                        addProductToPurchaseOrder();
                    }
                }
            });

            // Đăng ký sự kiện cho bảng sản phẩm đã chọn (bảng bên phải - giỏ hàng)
            purchaseOrderForm.getTableSelectedProducts().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    handleProductSelection(evt);
                }
            });

            // Đăng ký sự kiện cho nút Xóa sản phẩm trong giỏ hàng
            purchaseOrderForm.getBtnDeleteProduct().addActionListener(e -> {
                deleteSelectedProducts();
            });

        } catch (Exception e) {
            System.err.println(ErrorMessage.PURCHASE_ORDER_EVENTS_REGISTER_ERROR.format(e.getMessage()));
            e.printStackTrace();
        }
    }

    private void handleProductSelection(java.awt.event.MouseEvent evt) {
        int row = purchaseOrderForm.getTableSelectedProducts().rowAtPoint(evt.getPoint());
        if (row >= 0) {
            String productId = purchaseOrderForm.getTableSelectedProducts().getValueAt(row, 1).toString();
            Boolean isSelected = (Boolean) purchaseOrderForm.getTableSelectedProducts().getValueAt(row, 0);

            if (isSelected != null && isSelected) {
                purchaseOrderForm.getTableSelectedProducts().setValueAt(Boolean.FALSE, row, 0);
                purchaseOrderForm.getSelectedProductIDs().remove(productId);
            } else {
                purchaseOrderForm.getTableSelectedProducts().setValueAt(Boolean.TRUE, row, 0);
                purchaseOrderForm.getSelectedProductIDs().add(productId);
            }
        }

        // Nếu double click thì sửa số lượng
        if (evt.getClickCount() == 2) {
            editProductQuantity(row);
        }
    }

    /**
     * Xóa sản phẩm tại vị trí chỉ định khỏi giỏ hàng
     * 
     * @param row Dòng cần xóa
     */
    public void removeProductFromCart(int row) {
        try {
            // Xóa sản phẩm khỏi danh sách
            if (row >= 0 && row < selectedProducts.size()) {
                PurchaseOrderDetail removedProduct = selectedProducts.remove(row);
                Notifications.getInstance().show(Type.INFO,
                        ErrorMessage.PURCHASE_ORDER_PRODUCT_REMOVED_SUCCESS
                                .format(removedProduct.getProduct().getProductName()));

                // Cập nhật bảng
                updateSelectedProductsTable();

                // Cập nhật tổng tiền
                updateTotalAmount();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(purchaseOrderForm,
                    ErrorMessage.PURCHASE_ORDER_REMOVE_PRODUCT_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Xóa các sản phẩm đã chọn khỏi giỏ hàng
     */
    public void deleteSelectedProducts() {
        List<String> selectedIDs = purchaseOrderForm.getSelectedProductIDs();

        if (selectedIDs.isEmpty()) {
            JOptionPane.showMessageDialog(purchaseOrderForm,
                    ErrorMessage.PURCHASE_ORDER_SELECT_PRODUCT_TO_DELETE.get(),
                    ErrorMessage.INFO_TITLE.get(), JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(purchaseOrderForm,
                ErrorMessage.PURCHASE_ORDER_DELETE_PRODUCTS_CONFIRM.get(),
                ErrorMessage.CONFIRM_TITLE.get(), JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Cần xóa từ cuối lên đầu để tránh lỗi khi xóa nhiều phần tử
            List<Integer> indexesToRemove = new ArrayList<>();

            // Tìm các vị trí cần xóa
            for (int i = 0; i < selectedProducts.size(); i++) {
                PurchaseOrderDetail detail = selectedProducts.get(i);
                if (selectedIDs.contains(detail.getProduct().getProductId())) {
                    indexesToRemove.add(i);
                }
            }

            // Xóa từ cuối lên đầu
            for (int i = indexesToRemove.size() - 1; i >= 0; i--) {
                int indexToRemove = indexesToRemove.get(i);
                PurchaseOrderDetail removedProduct = selectedProducts.remove(indexToRemove);
                Notifications.getInstance().show(Type.INFO,
                        ErrorMessage.PURCHASE_ORDER_PRODUCT_REMOVED_SUCCESS
                                .format(removedProduct.getProduct().getProductName()));
            }

            // Xóa danh sách ID đã chọn
            purchaseOrderForm.getSelectedProductIDs().clear();

            // Cập nhật giao diện
            updateSelectedProductsTable();
            updateTotalAmount();
        }
    }

    /**
     * Refresh toàn bộ form khi nhấn F5
     */
    public void refreshForm() {
        try {
            System.out.println(ErrorMessage.PURCHASE_ORDER_REFRESH_PROCESSING.get());

            // 1. Làm mới danh sách sản phẩm
            loadProducts();

            // 2. Làm mới danh sách nhà cung cấp
            loadSuppliers();

            // 3. Xóa dữ liệu giỏ hàng hiện tại
            selectedProducts.clear();
            updateSelectedProductsTable();

            // 4. Cập nhật tổng tiền về 0
            purchaseOrderForm.getTxtTotalPrice().setText("0");

            // 5. Khởi tạo lại phiếu nhập mới
            try {
                String newId = purchaseOrderRepository.generatePurchaseOrderId();
                currentPurchaseOrder = new PurchaseOrder();
                currentPurchaseOrder.setPurchaseOrderId(newId);
                currentPurchaseOrder.setOrderDate(LocalDateTime.now());
                currentPurchaseOrder.setStatus("Pending");

                // Hiển thị mã phiếu mới
                purchaseOrderForm.getTextField2().setText(newId);
            } catch (Exception e) {
                System.err.println(ErrorMessage.PURCHASE_ORDER_REFRESH_CREATE_NEW_ID_ERROR.format(e.getMessage()));
            }

            // 6. Cập nhật thông tin nhân viên (đề phòng thay đổi)
            getCurrentEmployee();

            // 7. Hiển thị thông báo thành công (tùy chọn)
            JOptionPane.showMessageDialog(purchaseOrderForm,
                    ErrorMessage.PURCHASE_ORDER_REFRESH_SUCCESS.get(),
                    ErrorMessage.INFO_TITLE.get(), JOptionPane.INFORMATION_MESSAGE);

            System.out.println(ErrorMessage.PURCHASE_ORDER_REFRESH_COMPLETE_SUCCESS.get());
        } catch (Exception e) {
            System.err.println(ErrorMessage.PURCHASE_ORDER_REFRESH_ERROR_MESSAGE.format(e.getMessage()));
            e.printStackTrace();
            JOptionPane.showMessageDialog(purchaseOrderForm,
                    ErrorMessage.PURCHASE_ORDER_REFRESH_FORM_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Đảm bảo kết nối hoạt động
     */
    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DatabaseConnection.getInstance().getConnection();
            // System.out.println("StockInController: Đã tạo lại kết nối");
        }
    }

    /**
     * Tải danh sách sản phẩm vào bảng
     */
    private void loadProducts() {
        try {
            // Đảm bảo kết nối
            ensureConnection();

            // System.out.println("PurchaseOrderController: Đang tải sản phẩm...");
            List<Product> products = productRepository.findAll();

            if (products == null || products.isEmpty()) {
                // System.out.println("PurchaseOrderController: Không có sản phẩm nào để hiển
                // thị");
                products = new ArrayList<>();
            } else {
                // System.out.println("PurchaseOrderController: Đã tìm thấy " + products.size()
                // + " sản phẩm");
            }

            // Lấy model của bảng
            DefaultTableModel model = (DefaultTableModel) purchaseOrderForm.getTableProducts().getModel();

            // Xóa dữ liệu cũ
            model.setRowCount(0);

            // Thêm dữ liệu mới
            for (Product product : products) {
                try {
                    // Thêm vào bảng - CHỈ SỬ DỤNG 4 CỘT
                    model.addRow(new Object[] {
                            product.getProductId(), // Mã máy
                            product.getProductName(), // Tên máy
                            product.getCostPrice(), // Đơn giá
                            product.getStockQuantity() // SL tồn kho
                    });
                } catch (Exception e) {
                    System.err.println("Lỗi khi thêm sản phẩm vào bảng: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Làm mới sorter nếu đã khởi tạo
            if (productTableSorter != null) {
                TableUtils.refreshSorter(purchaseOrderForm.getTableProducts());
            }

            System.out.println("PurchaseOrderController: Đã cập nhật bảng sản phẩm thành công với " + products.size()
                    + " sản phẩm");
        } catch (Exception e) {
            System.err.println(ErrorMessage.PURCHASE_ORDER_LOAD_PRODUCTS_ERROR.format(e.getMessage()));
            e.printStackTrace();
        }
    }

    // * Thêm sản phẩm từ dòng được chọn vào giỏ hàng trực tiếp
    // * @param row Dòng được chọn
    // */
    public void addProductToCartByRowDirect(int row) {
        if (row < 0 || row >= purchaseOrderForm.getTableProducts().getRowCount()) {
            return;
        }

        try {
            // Lấy thông tin sản phẩm được chọn
            String productId = purchaseOrderForm.getTableProducts().getValueAt(row, 0).toString();

            // Kiểm tra sản phẩm đã tồn tại trong phiếu chưa
            for (PurchaseOrderDetail detail : selectedProducts) {
                if (detail.getProduct().getProductId().equals(productId)) {
                    int choice = JOptionPane.showConfirmDialog(purchaseOrderForm,
                            ErrorMessage.PURCHASE_ORDER_PRODUCT_ALREADY_ADDED.get(),
                            ErrorMessage.INFO_TITLE.get(), JOptionPane.YES_NO_OPTION);

                    if (choice == JOptionPane.YES_OPTION) {
                        // Cập nhật số lượng
                        String quantityStr = JOptionPane.showInputDialog(purchaseOrderForm,
                                ErrorMessage.PURCHASE_ORDER_UPDATE_QUANTITY_CONFIRM.get(),
                                detail.getQuantity());

                        if (quantityStr != null && !quantityStr.isEmpty()) {
                            try {
                                int newQuantity = Integer.parseInt(quantityStr);
                                if (newQuantity > 0) {
                                    detail.setQuantity(newQuantity);
                                    updateSelectedProductsTable();
                                    updateTotalAmount();
                                } else {
                                    JOptionPane.showMessageDialog(purchaseOrderForm,
                                            ErrorMessage.PURCHASE_ORDER_QUANTITY_POSITIVE.get(),
                                            ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(purchaseOrderForm,
                                        ErrorMessage.PURCHASE_ORDER_QUANTITY_INVALID.get(),
                                        ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                    return;
                }
            }

            // Lấy thông tin chi tiết sản phẩm từ repository
            Product product = productRepository.findById(productId).orElse(null);

            if (product != null) {
                // Hiển thị dialog để nhập số lượng và đơn giá
                String quantityStr = JOptionPane.showInputDialog(purchaseOrderForm,
                        ErrorMessage.PURCHASE_ORDER_ENTER_QUANTITY.get(), "1");

                if (quantityStr == null || quantityStr.isEmpty())
                    return; // Người dùng đã hủy

                int quantity;
                try {
                    quantity = Integer.parseInt(quantityStr);
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(purchaseOrderForm,
                                ErrorMessage.PURCHASE_ORDER_QUANTITY_POSITIVE.get(),
                                ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(purchaseOrderForm,
                            ErrorMessage.PURCHASE_ORDER_QUANTITY_INVALID.get(),
                            ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String priceStr = JOptionPane.showInputDialog(purchaseOrderForm,
                        ErrorMessage.PURCHASE_ORDER_ENTER_UNIT_PRICE.get(), product.getCostPrice().toString());

                if (priceStr == null || priceStr.isEmpty())
                    return; // Người dùng đã hủy

                BigDecimal price;
                try {
                    price = new BigDecimal(priceStr);
                    if (price.compareTo(BigDecimal.ZERO) <= 0) {
                        JOptionPane.showMessageDialog(purchaseOrderForm,
                                ErrorMessage.PURCHASE_ORDER_UNIT_PRICE_POSITIVE.get(),
                                ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(purchaseOrderForm,
                            ErrorMessage.PURCHASE_ORDER_UNIT_PRICE_INVALID.get(),
                            ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Lấy nhà cung cấp từ ComboBox chính
                Supplier supplier = purchaseOrderForm.getSelectedSupplier();
                if (supplier == null) {
                    JOptionPane.showMessageDialog(purchaseOrderForm,
                            ErrorMessage.PURCHASE_ORDER_SELECT_SUPPLIER_FIRST.get(),
                            ErrorMessage.INFO_TITLE.get(), JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Tạo chi tiết phiếu nhập mới
                PurchaseOrderDetail detail = new PurchaseOrderDetail();
                detail.setProduct(product);
                detail.setQuantity(quantity);
                detail.setUnitCost(price);
                detail.setSupplier(supplier);

                // Thêm vào danh sách
                selectedProducts.add(detail);

                // Cập nhật bảng sản phẩm đã chọn
                updateSelectedProductsTable();

                // Cập nhật tổng tiền
                updateTotalAmount();
            } else {
                JOptionPane.showMessageDialog(purchaseOrderForm,
                        ErrorMessage.PURCHASE_ORDER_PRODUCT_NOT_FOUND.get(),
                        ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(purchaseOrderForm,
                    ErrorMessage.PURCHASE_ORDER_ADD_PRODUCT_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Tải danh sách nhà cung cấp
     */
    private void loadSuppliers() {
        try {
            // Đảm bảo kết nối
            ensureConnection();

            List<Supplier> suppliers = supplierRepository.findAll();

            // Cập nhật ComboBox trong PurchaseOrderForm
            purchaseOrderForm.updateSupplierComboBoxes(suppliers);

            System.out.println("StockInController: Đã tải " + suppliers.size() + " nhà cung cấp");
        } catch (Exception e) {
            System.err.println(ErrorMessage.PURCHASE_ORDER_LOAD_SUPPLIERS_ERROR.format(e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * Tìm kiếm sản phẩm sử dụng TableStyleUtil
     */
    public void searchProducts() {
        try {
            // Kiểm tra null và lấy từ khóa tìm kiếm
            if (purchaseOrderForm.getTextFieldSearch() == null)
                return;

            String keyword = "";
            try {
                keyword = purchaseOrderForm.getTextFieldSearch().getText().trim();
            } catch (Exception e) {
                System.err.println("Không thể lấy text từ TextFieldSearch: " + e.getMessage());
                return;
            }

            // Lấy sorter từ bảng sản phẩm
            TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) purchaseOrderForm.getTableProducts()
                    .getRowSorter();
            if (sorter == null) {
                sorter = TableUtils.setupSorting(purchaseOrderForm.getTableProducts());
            }

            // Áp dụng filter trên các cột mã sản phẩm, tên sản phẩm và giá
            TableUtils.applyFilter(sorter, keyword, 0, 1, 2);

        } catch (Exception e) {
            System.err.println(ErrorMessage.PURCHASE_ORDER_SEARCH_ERROR.format(e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * Thêm sản phẩm vào phiếu nhaập (giỏ hàng)
     */
    private void addProductToPurchaseOrder() {
        // Kiểm tra đã chọn sản phẩm chưa
        int selectedRow = purchaseOrderForm.getTableProducts().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(purchaseOrderForm,
                    ErrorMessage.PURCHASE_ORDER_SELECT_PRODUCT_FIRST.get(),
                    ErrorMessage.INFO_TITLE.get(), JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Lấy thông tin sản phẩm được chọn
            String productId = purchaseOrderForm.getTableProducts().getValueAt(selectedRow, 0).toString();

            // Kiểm tra sản phẩm đã tồn tại trong phiếu chưa
            for (PurchaseOrderDetail detail : selectedProducts) {
                if (detail.getProduct().getProductId().equals(productId)) {
                    int choice = JOptionPane.showConfirmDialog(purchaseOrderForm,
                            ErrorMessage.PURCHASE_ORDER_PRODUCT_ALREADY_ADDED.get(),
                            ErrorMessage.INFO_TITLE.get(), JOptionPane.YES_NO_OPTION);

                    if (choice == JOptionPane.YES_OPTION) {
                        // Cập nhật số lượng
                        String quantityStr = JOptionPane.showInputDialog(purchaseOrderForm,
                                ErrorMessage.PURCHASE_ORDER_UPDATE_QUANTITY_CONFIRM.get(),
                                detail.getQuantity());

                        if (quantityStr != null && !quantityStr.isEmpty()) {
                            try {
                                int newQuantity = Integer.parseInt(quantityStr);
                                if (newQuantity > 0) {
                                    detail.setQuantity(newQuantity);
                                    updateSelectedProductsTable();
                                    updateTotalAmount();
                                } else {
                                    JOptionPane.showMessageDialog(purchaseOrderForm,
                                            ErrorMessage.PURCHASE_ORDER_QUANTITY_POSITIVE.get(),
                                            ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(purchaseOrderForm,
                                        ErrorMessage.PURCHASE_ORDER_QUANTITY_INVALID.get(),
                                        ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                    return;
                }
            }

            Product product = productRepository.findById(productId).orElse(null);

            if (product != null) {
                // Hiển thị dialog để nhập số lượng và đơn giá
                String quantityStr = JOptionPane.showInputDialog(purchaseOrderForm,
                        ErrorMessage.PURCHASE_ORDER_ENTER_QUANTITY.get(), "1");

                if (quantityStr == null || quantityStr.isEmpty())
                    return; // Người dùng đã hủy

                int quantity;
                try {
                    quantity = Integer.parseInt(quantityStr);
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(purchaseOrderForm,
                                ErrorMessage.PURCHASE_ORDER_QUANTITY_POSITIVE.get(),
                                ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(purchaseOrderForm,
                            ErrorMessage.PURCHASE_ORDER_QUANTITY_INVALID.get(),
                            ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BigDecimal price = JDialogInputUtils.showInputDialogBigDecimal(purchaseOrderForm,
                        ErrorMessage.PURCHASE_ORDER_ENTER_UNIT_PRICE.get(), product.getCostPrice().toString());

                if (price == null)
                    return; // Người dùng đã hủy

                // Tạo chi tiết phiếu nhập mới
                PurchaseOrderDetail detail = new PurchaseOrderDetail();
                detail.setProduct(product);
                detail.setQuantity(quantity);
                detail.setUnitCost(price);

                // Thêm vào danh sách
                selectedProducts.add(detail);

                // Cập nhật bảng sản phẩm đã chọn
                updateSelectedProductsTable();

                // Cập nhật tổng tiền
                updateTotalAmount();

                System.out.println("Đã thêm sản phẩm " + product.getProductName() + " vào giỏ hàng");
            } else {
                JOptionPane.showMessageDialog(purchaseOrderForm,
                        ErrorMessage.PURCHASE_ORDER_PRODUCT_NOT_FOUND.get(),
                        ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(purchaseOrderForm,
                    ErrorMessage.PURCHASE_ORDER_ADD_PRODUCT_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Xóa sản phẩm khỏi phiếu nhập (giỏ hàng)
     */
    public void removeProductFromPurchaseOrder() {
        // Kiểm tra đã chọn sản phẩm trong bảng đã chọn chưa
        int selectedRow = purchaseOrderForm.getTableSelectedProducts().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(purchaseOrderForm,
                    "Vui lòng chọn sản phẩm cần xóa",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Xác nhận xóa
        int choice = JOptionPane.showConfirmDialog(purchaseOrderForm,
                ErrorMessage.PURCHASE_ORDER_DELETE_CONFIRM.get(),
                ErrorMessage.CONFIRM_TITLE.get(), JOptionPane.YES_NO_OPTION);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // Xóa sản phẩm khỏi danh sách
            if (selectedRow >= 0 && selectedRow < selectedProducts.size()) {
                PurchaseOrderDetail removedProduct = selectedProducts.remove(selectedRow);
                System.out.println("Đã xóa sản phẩm " + removedProduct.getProduct().getProductName() + " khỏi giỏ hàng");

                // Cập nhật bảng
                updateSelectedProductsTable();

                // Cập nhật tổng tiền
                updateTotalAmount();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(purchaseOrderForm,
                    ErrorMessage.PURCHASE_ORDER_REMOVE_PRODUCT_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật bảng hiển thị các sản phẩm đã chọn (giỏ hàng)
     */
    private void updateSelectedProductsTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) purchaseOrderForm.getTableSelectedProducts().getModel();

            // Xóa tất cả các dòng hiện tại
            model.setRowCount(0);

            // Thêm các sản phẩm đã chọn vào bảng
            for (PurchaseOrderDetail detail : selectedProducts) {
                try {
                    model.addRow(new Object[] {
                            Boolean.FALSE,
                            detail.getProduct().getProductId(),
                            detail.getProduct().getProductName(),
                            // detail.getSupplier().getName(), // Hiển thị tên nhà cung cấp
                            detail.getQuantity(),
                            currencyFormat.format(detail.getUnitCost())
                    });
                } catch (Exception e) {
                    System.err.println("Lỗi khi thêm sản phẩm vào bảng giỏ hàng: " + e.getMessage());
                }
            }
            TableUtils.setBooleanColumns(purchaseOrderForm.getTableSelectedProducts(), 0);

            // System.out.println("Đã cập nhật bảng giỏ hàng với " + selectedProducts.size()
            // + " sản phẩm");

        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật bảng giỏ hàng: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật tổng tiền phiếu nhập
     */
    private void updateTotalAmount() {
        try {
            BigDecimal total = BigDecimal.ZERO;

            for (PurchaseOrderDetail detail : selectedProducts) {
                BigDecimal amount = detail.getUnitCost().multiply(BigDecimal.valueOf(detail.getQuantity()));
                total = total.add(amount);
            }

            // Hiển thị tổng tiền
            purchaseOrderForm.getTxtTotalPrice().setText(currencyFormat.format(total));

        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật tổng tiền: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lưu phiếu nhập hàng
     */
    private boolean isProcessingSave = false;

    public void savePurchaseOrder() {
        // Sử dụng synchronized để đảm bảo thread safety
        synchronized (this) {
            if (isProcessingSave) {
                // System.out.println("Đang xử lý lưu phiếu nhập, bỏ qua lệnh gọi mới");
                return;
            }
            isProcessingSave = true;
        }

        try {
            // System.out.println("Bắt đầu lưu phiếu nhập");

            // 1. Kiểm tra điều kiện đầu vào
            Supplier supplier = (Supplier) purchaseOrderForm.getComboBoxSupplier().getSelectedItem();
            if (supplier == null) {
                JOptionPane.showMessageDialog(purchaseOrderForm,
                        ErrorMessage.PURCHASE_ORDER_SELECT_SUPPLIER.get(),
                        ErrorMessage.INFO_TITLE.get(), JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (selectedProducts.isEmpty()) {
                JOptionPane.showMessageDialog(purchaseOrderForm,
                        ErrorMessage.PURCHASE_ORDER_ADD_PRODUCTS.get(),
                        ErrorMessage.INFO_TITLE.get(), JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Set trạng thái cho phiếu nhập
            String selectedStatus = "Pending"; // Trạng thái mặc định

            // 3. Bắt đầu giao dịch database
            try {
                connection.setAutoCommit(false);

                // 4. Tạo và thiết lập thông tin phiếu nhập
                currentPurchaseOrder = new PurchaseOrder();
                String newId = purchaseOrderRepository.generatePurchaseOrderId();
                currentPurchaseOrder.setPurchaseOrderId(newId);

                // Cập nhật thông tin
                currentPurchaseOrder.setSupplier(supplier);
                currentPurchaseOrder.setEmployee(getCurrentEmployee());
                currentPurchaseOrder.setOrderDate(LocalDateTime.now());
                currentPurchaseOrder.setStatus(selectedStatus);

                // Tính tổng tiền
                BigDecimal totalAmount = BigDecimal.ZERO;
                for (PurchaseOrderDetail detail : selectedProducts) {
                    BigDecimal detailAmount = detail.getUnitCost().multiply(BigDecimal.valueOf(detail.getQuantity()));
                    totalAmount = totalAmount.add(detailAmount);
                }
                currentPurchaseOrder.setTotalAmount(totalAmount);

                System.out.println("Purchase Order prepared: " + currentPurchaseOrder.getPurchaseOrderId());
                System.out.println("Total amount: " + totalAmount);

                int option = JOptionPane.showConfirmDialog(purchaseOrderForm,
                        ErrorMessage.PURCHASE_ORDER_SAVE_CONFIRM.get(),
                        ErrorMessage.CONFIRM_TITLE.get(), JOptionPane.YES_NO_OPTION);

                if (option != JOptionPane.YES_OPTION)
                    return;

                // 5. Lưu phiếu nhập vào database
                PurchaseOrder savedOrder = purchaseOrderRepository.add(currentPurchaseOrder);
                System.out.println("Đã tạo phiếu nhập hàng: " + savedOrder.getPurchaseOrderId());

                // 6. Lưu chi tiết phiếu nhập
                for (PurchaseOrderDetail detail : selectedProducts) {
                    detail.setPurchaseOrder(savedOrder);
                    purchaseOrderDetailRepository.add(detail);
                }

                // 7. Commit giao dịch
                connection.commit();
                System.out.println("Transaction committed successfully!");

                // 8. Hiển thị thông báo thành công
                JOptionPane.showMessageDialog(purchaseOrderForm,
                        ErrorMessage.PURCHASE_ORDER_SAVE_SUCCESS.get(),
                        ErrorMessage.INFO_TITLE.get(), JOptionPane.INFORMATION_MESSAGE);

                // 9. Tạo phiếu nhập mới và làm mới form
                clearForm();
                loadProducts();

                // System.out.println("Form đã được làm mới hoàn toàn");

                exportPDF(savedOrder);

            } catch (Exception e) {
                // 10. Rollback nếu có lỗi
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.rollback();
                        System.err.println("Transaction rolled back due to error: " + e.getMessage());
                    }
                } catch (SQLException ex) {
                    System.err.println("Error during rollback: " + ex.getMessage());
                }
                throw e; // Ném lại ngoại lệ để xử lý ở khối catch bên ngoài
            } finally {
                // 11. Đảm bảo autoCommit luôn được reset
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.setAutoCommit(true);
                    }
                } catch (SQLException ex) {
                    System.err.println("Error resetting autoCommit: " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            // 12. Xử lý ngoại lệ tổng quát
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(purchaseOrderForm,
                    ErrorMessage.PURCHASE_ORDER_SAVE_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
        } finally {
            // 13. Đảm bảo cờ hiệu luôn được reset
            synchronized (this) {
                isProcessingSave = false;
            }
        }
    }

    /**
     * Xóa dữ liệu trên form và khởi tạo mới
     */
    private void clearForm() {
        try {
            selectedProducts.clear();

            updateSelectedProductsTable();

            purchaseOrderForm.getTxtTotalPrice().setText("0");

            if (purchaseOrderForm.getComboBoxSupplier().getItemCount() > 0) {
                purchaseOrderForm.getComboBoxSupplier().setSelectedIndex(0);
            }

            try {
                currentPurchaseOrder = new PurchaseOrder();

                String newId = purchaseOrderRepository.generatePurchaseOrderId();
                currentPurchaseOrder.setPurchaseOrderId(newId);

                purchaseOrderForm.getTextField2().setText(newId);

                currentPurchaseOrder.setOrderDate(LocalDateTime.now());
                currentPurchaseOrder.setStatus("Pending");

            } catch (Exception e) {
                System.err.println(ErrorMessage.PURCHASE_ORDER_REFRESH_CREATE_NEW_ID_ERROR.format(e.getMessage()));
                e.printStackTrace();
            }

            System.out.println("Đã làm mới form nhập hàng thành công");
        } catch (Exception e) {
            System.err.println("Lỗi khi làm mới form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lấy thông tin nhân viên hiện tại từ session hoặc khởi tạo giả lập
     */
    private Employee getCurrentEmployee() {
        if (currentEmployee == null) {
            // Tạo nhân viên mặc định cho demo
            currentEmployee = SessionManager.getInstance().getCurrentUser().getEmployee();
        }

        // Hiển thị thông tin nhân viên trên form
        purchaseOrderForm.getTextField4().setText(currentEmployee.getFullName());

        return currentEmployee;
    }

    /**
     * Cập nhật thông tin nhân viên hiện tại
     * 
     * @param employee Nhân viên hiện tại
     */
    public void setCurrentEmployee(Employee employee) {
        this.currentEmployee = employee;

        // Hiển thị thông tin nhân viên trên form
        if (employee != null) {
            purchaseOrderForm.getTextField4().setText(employee.getFullName());
        }
    }

    /**
     * Sắp xếp bảng sản phẩm theo cột và hướng chỉ định
     * 
     * @param column    Cột cần sắp xếp
     * @param ascending True nếu sắp xếp tăng dần, False nếu giảm dần
     */
    public void sortProductsTable(int column, boolean ascending) {
        if (productTableSorter == null)
            return;

        List<SortKey> sortKeys = new ArrayList<>();
        SortOrder sortOrder = ascending ? SortOrder.ASCENDING : SortOrder.DESCENDING;
        sortKeys.add(new SortKey(column, sortOrder));

        productTableSorter.setSortKeys(sortKeys);
    }

    /**
     * Chỉnh sửa số lượng sản phẩm trong giỏ hàng
     * 
     * @param row Dòng của sản phẩm cần chỉnh sửa
     */
    public void editProductQuantity(int row) {
        try {
            if (row < 0 || row >= selectedProducts.size()) {
                return;
            }

            PurchaseOrderDetail detail = selectedProducts.get(row);

            // Hỏi số lượng
            Integer quantity = JDialogInputUtils.showInputDialogInt(purchaseOrderForm,
                    ErrorMessage.ENTER_PRODUCT_QUANTITY.toString(),
                    "1");

            if (quantity == null)
                return;

            if (quantity > 0) {
                detail.setQuantity(quantity);
                updateSelectedProductsTable();
                updateTotalAmount();
                Notifications.getInstance().show(Type.INFO,
                        ErrorMessage.PURCHASE_ORDER_PRODUCT_QUANTITY_UPDATED
                                .format(detail.getProduct().getProductName()));
            } else {
                JOptionPane.showMessageDialog(purchaseOrderForm,
                        ErrorMessage.PURCHASE_ORDER_QUANTITY_POSITIVE.get(),
                        ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(purchaseOrderForm,
                    ErrorMessage.PURCHASE_ORDER_QUANTITY_INVALID.get(),
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(purchaseOrderForm,
                    ErrorMessage.PURCHASE_ORDER_UPDATE_QUANTITY_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void exportPDF(PurchaseOrder purchaseOrder) {
        try {
            // Validate dữ liệu
            if (purchaseOrder == null) {
                JOptionPane.showMessageDialog(purchaseOrderForm,
                        ErrorMessage.EXPORT_NO_DATA.get(),
                        ErrorMessage.INFO_TITLE.get(), JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Map<String, Object> printData = createPurchaseOrderPrintData(purchaseOrder);

            String defaultFileName = "PhieuNhapHang_" + purchaseOrder.getPurchaseOrderId();

            BillPrintUtils.printBill(purchaseOrderForm, "bill_purchase_order_template", printData, defaultFileName);

        } catch (Exception e) {
            System.err.println("Lỗi khi xuất PDF: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(purchaseOrderForm,
                    ErrorMessage.PDF_EXPORT_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tạo dữ liệu cho template phiếu nhập hàng
     */
    private Map<String, Object> createPurchaseOrderPrintData(PurchaseOrder purchaseOrder) throws Exception {
        Map<String, Object> data = new HashMap<>();

        // 2. Thông tin phiếu nhập
        data.put("purchaseOrderId", purchaseOrder.getPurchaseOrderId());
        String formattedOrderDate = purchaseOrder.getOrderDate() != null
                ? purchaseOrder.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "";
        data.put("orderDate", formattedOrderDate);

        String formattedCreatedAt = purchaseOrder.getCreatedAt() != null
                ? purchaseOrder.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        data.put("createdAt", formattedCreatedAt);
        data.put("status", purchaseOrder.getStatus());

        // 3. Thông tin nhà cung cấp
        Supplier supplier = purchaseOrder.getSupplier();
        if (supplier != null) {
            data.put("supplierName", supplier.getName());
            data.put("supplierAddress", supplier.getAddress());
            data.put("supplierPhone", supplier.getPhoneNumber());
        } else {
            data.put("supplierName", "Chưa xác định");
            data.put("supplierAddress", "");
            data.put("supplierPhone", "");
        }

        // 4. Thông tin nhân viên
        Employee employee = purchaseOrder.getEmployee();
        if (employee != null) {
            data.put("employeeName", employee.getFullName());
            data.put("employeePosition", employee.getPosition() != null ? employee.getPosition() : "Nhân viên");
        } else {
            data.put("employeeName", "");
            data.put("employeePosition", "");
        }

        // 5. Chi tiết sản phẩm
        data.put("purchaseOrderDetails", createPurchaseOrderDetailsList(purchaseOrder));

        // 6. Thống kê
        Map<String, Object> summary = calculatePurchaseOrderSummary(purchaseOrder);
        data.put("totalItems", summary.get("totalItems"));
        data.put("totalQuantity", summary.get("totalQuantity"));
        data.put("totalAmountPurchase", summary.get("totalAmountPurchase"));
        data.put("totalAmount", summary.get("totalAmount"));

        // 7. Ghi chú
        data.put("notes", purchaseOrder.getNotes());

        return data;
    }

    /**
     * Tạo danh sách chi tiết phiếu nhập cho template
     */
    private List<Map<String, Object>> createPurchaseOrderDetailsList(PurchaseOrder purchaseOrder) throws Exception {
        List<Map<String, Object>> details = new ArrayList<>();

        List<PurchaseOrderDetail> orderDetails;

        if (selectedProducts != null && !selectedProducts.isEmpty()) {
            orderDetails = selectedProducts;
        } else {
            orderDetails = purchaseOrderDetailRepository.findByPurchaseOrderId(purchaseOrder.getPurchaseOrderId());
        }

        if (orderDetails != null) {
            for (PurchaseOrderDetail detail : orderDetails) {
                Map<String, Object> item = new HashMap<>();

                Product product = detail.getProduct();
                if (product != null) {
                    item.put("productName", product.getProductName());
                    item.put("productId", product.getProductId());
                } else {
                    item.put("productName", "Không xác định");
                    item.put("productId", "");
                }

                item.put("quantity", detail.getQuantity());
                item.put("unitCostFormatted", currencyFormat.format(detail.getUnitCost()));

                // Tính thành tiền
                BigDecimal subtotal = detail.getUnitCost().multiply(BigDecimal.valueOf(detail.getQuantity()));
                item.put("subtotalFormatted", currencyFormat.format(subtotal));

                // Ghi chú
                // item.put("notes", detail.getNotes() != null ? detail.getNotes() : "");
                item.put("notes", " ");

                details.add(item);
            }
        }

        return details;
    }

    /**
     * Tính tổng kết cho phiếu nhập hàng
     */
    private Map<String, Object> calculatePurchaseOrderSummary(PurchaseOrder purchaseOrder) throws Exception {
        Map<String, Object> summary = new HashMap<>();

        List<PurchaseOrderDetail> orderDetails;

        if (selectedProducts != null && !selectedProducts.isEmpty()) {
            orderDetails = selectedProducts;
        } else {
            orderDetails = purchaseOrderDetailRepository.findByPurchaseOrderId(purchaseOrder.getPurchaseOrderId());
        }

        int totalItems = orderDetails != null ? orderDetails.size() : 0;
        int totalQuantity = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (orderDetails != null) {
            for (PurchaseOrderDetail detail : orderDetails) {
                totalQuantity += detail.getQuantity();

                BigDecimal subtotal = detail.getUnitCost().multiply(
                        BigDecimal.valueOf(detail.getQuantity()));
                totalAmount = totalAmount.add(subtotal);
            }
        }

        summary.put("totalItems", totalItems);
        summary.put("totalQuantity", totalQuantity);
        summary.put("totalAmount", totalAmount);
        summary.put("totalAmountPurchase", totalAmount);

        return summary;
    }
}