package com.pcstore.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

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
import com.pcstore.utils.DatabaseConnection;
import com.pcstore.utils.SessionManager;
import com.pcstore.view.PurchaseOrderForm;

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
    
    /**
     * Khởi tạo controller với form nhập hàng và kết nối được chia sẻ
     * @param purchaseOrderForm Form nhập hàng
     * @param sharedConnection Kết nối database dùng chung
     */
    public PurchaseOrderController(PurchaseOrderForm purchaseOrderForm, Connection sharedConnection) {
        this.purchaseOrderForm = purchaseOrderForm;
        
        try {
            // Sử dụng kết nối được truyền vào
            this.connection = sharedConnection;
            System.out.println("StockInController: Đã nhận kết nối được chia sẻ");
            
            // Khởi tạo các repository
            initRepositories();
            
            // Khởi tạo phiếu nhập hàng mới
            initializeNewPurchaseOrder();
            
            // Đăng ký các sự kiện
            registerEvents();
            
            // Tải dữ liệu ban đầu
            loadInitialData();
            
            System.out.println("StockInController: Khởi tạo hoàn tất");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(purchaseOrderForm, 
                    "Lỗi khi khởi tạo StockInController: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
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
            System.out.println("StockInController: Đã tạo RepositoryFactory");
            
            // Khởi tạo các repository
            this.productRepository = new ProductRepository(connection);
            this.supplierRepository = new SupplierRepository(connection);
            this.purchaseOrderRepository = new PurchaseOrderRepository(connection, repositoryFactory);
            this.purchaseOrderDetailRepository = new PurchaseOrderDetailRepository(connection, repositoryFactory);
            
            System.out.println("StockInController: Đã khởi tạo repository thành công");
        } catch (Exception e) {
            System.err.println("Lỗi khi khởi tạo repository: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Khởi tạo phiếu nhập hàng mới
     */
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
            System.err.println("Lỗi khi khởi tạo phiếu nhập hàng: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Đăng ký các sự kiện
     */
    private void registerEvents() {
        try {
            // Đăng ký sự kiện search
            purchaseOrderForm.getTextFieldSearch().addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    searchProducts();
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
                    if (evt.getClickCount() == 2) {
                        // Khi double-click vào sản phẩm trong giỏ hàng, xóa khỏi giỏ
                        removeProductFromPurchaseOrder();
                    }
                }
            });
            
            System.out.println("StockInController: Đã đăng ký các sự kiện thành công");
        } catch (Exception e) {
            System.err.println("Lỗi khi đăng ký sự kiện: " + e.getMessage());
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
            System.err.println("Lỗi khi tải dữ liệu ban đầu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Đảm bảo kết nối hoạt động
     */
    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DatabaseConnection.getInstance().getConnection();
            System.out.println("StockInController: Đã tạo lại kết nối");
        }
    }
    
    /**
     * Tải danh sách sản phẩm vào bảng
     */
    private void loadProducts() {
        try {
            // Đảm bảo kết nối
            ensureConnection();
    
            System.out.println("StockInController: Đang tải sản phẩm...");
            List<Product> products = productRepository.findAll();
    
            if (products == null || products.isEmpty()) {
                System.out.println("StockInController: Không có sản phẩm nào để hiển thị");
                products = new ArrayList<>();
            } else {
                System.out.println("StockInController: Đã tìm thấy " + products.size() + " sản phẩm");
            }
    
            // Lấy model của bảng
            DefaultTableModel model = (DefaultTableModel) purchaseOrderForm.getTableProducts().getModel();
    
            // Xóa dữ liệu cũ
            model.setRowCount(0);
    
            // Thêm dữ liệu mới
            for (Product product : products) {
                try {
                    // Lấy thông tin cần thiết từ Product
                    String productId = product.getProductId();
                    String productName = product.getProductName();
                    int stockQuantity = product.getStockQuantity();
                    BigDecimal price = product.getPrice();
    
                    // Thêm vào bảng, nhưng để trống cột nhà cung cấp và cột xác nhận
                    model.addRow(new Object[] { 
                        productId, 
                        productName, 
                        null,  // Cột nhà cung cấp sẽ là ComboBox
                        stockQuantity, 
                        price,
                        null   // Cột xác nhận sẽ được renderer đặc biệt xử lý
                    });
                } catch (Exception e) {
                    System.err.println("Lỗi khi thêm sản phẩm vào bảng: " + e.getMessage());
                    e.printStackTrace();
                }
            }
    
            System.out.println("StockInController: Đã cập nhật bảng sản phẩm thành công với " + products.size() + " sản phẩm");
        } catch (Exception e) {
            System.err.println("Lỗi khi tải danh sách sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void addProductToCartByRow(int row) {
        if (row < 0 || row >= purchaseOrderForm.getTableProducts().getRowCount()) {
            return;
        }
        
        try {
            // Lấy thông tin sản phẩm được chọn
            String productId = purchaseOrderForm.getTableProducts().getValueAt(row, 0).toString();
            
            // Lấy nhà cung cấp đã chọn trong ComboBox
            Supplier supplier = purchaseOrderForm.getSelectedSupplierForRow(row);
            
            if (supplier == null) {
                JOptionPane.showMessageDialog(purchaseOrderForm, 
                    "Vui lòng chọn nhà cung cấp trước khi thêm sản phẩm", 
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Kiểm tra sản phẩm đã tồn tại trong phiếu chưa
            for (PurchaseOrderDetail detail : selectedProducts) {
                if (detail.getProduct().getProductId().equals(productId)) {
                    int choice = JOptionPane.showConfirmDialog(purchaseOrderForm, 
                            "Sản phẩm này đã được thêm vào phiếu nhập. Bạn có muốn cập nhật số lượng không?",
                            "Thông báo", JOptionPane.YES_NO_OPTION);
                    
                    if (choice == JOptionPane.YES_OPTION) {
                        // Cập nhật số lượng
                        String quantityStr = JOptionPane.showInputDialog(purchaseOrderForm, 
                                "Nhập số lượng mới:", 
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
                                            "Số lượng phải lớn hơn 0",
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(purchaseOrderForm, 
                                        "Số lượng không hợp lệ",
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                    return;
                }
            }
            
            // Lấy thông tin chi tiết sản phẩm từ repository
            ensureConnection();
            Product product = productRepository.findById(productId).orElse(null);
            
            if (product != null) {
                // Hiển thị dialog để nhập số lượng và đơn giá
                String quantityStr = JOptionPane.showInputDialog(purchaseOrderForm, 
                        "Nhập số lượng:", "1");
                
                if (quantityStr == null || quantityStr.isEmpty()) return; // Người dùng đã hủy
                
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityStr);
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(purchaseOrderForm, 
                                "Số lượng phải lớn hơn 0",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(purchaseOrderForm, 
                            "Số lượng không hợp lệ",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String priceStr = JOptionPane.showInputDialog(purchaseOrderForm, 
                        "Nhập đơn giá:", product.getPrice().toString());
                
                if (priceStr == null || priceStr.isEmpty()) return; // Người dùng đã hủy
                
                BigDecimal price;
                try {
                    price = new BigDecimal(priceStr);
                    if (price.compareTo(BigDecimal.ZERO) <= 0) {
                        JOptionPane.showMessageDialog(purchaseOrderForm, 
                                "Đơn giá phải lớn hơn 0",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(purchaseOrderForm, 
                            "Đơn giá không hợp lệ",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Tạo chi tiết phiếu nhập mới
                PurchaseOrderDetail detail = new PurchaseOrderDetail();
                detail.setProduct(product);
                detail.setQuantity(quantity);
                detail.setUnitPrice(price);
                detail.setSupplier(supplier);
                
                // Thêm vào danh sách
                selectedProducts.add(detail);
                
                // Cập nhật bảng sản phẩm đã chọn
                updateSelectedProductsTable();
                
                // Cập nhật tổng tiền
                updateTotalAmount();
                
                System.out.println("Đã thêm sản phẩm " + product.getProductName() + " vào giỏ hàng");
            } else {
                JOptionPane.showMessageDialog(purchaseOrderForm, 
                        "Không tìm thấy thông tin sản phẩm",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(purchaseOrderForm, 
                    "Lỗi khi thêm sản phẩm vào phiếu nhập: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
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
            System.err.println("Lỗi khi tải danh sách nhà cung cấp: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Tìm kiếm sản phẩm
     */
    private void searchProducts() {
        try {
            // Đảm bảo kết nối
            ensureConnection();
            
            String keyword = purchaseOrderForm.getTextFieldSearch().getText().trim();
            System.out.println("StockInController: Đang tìm kiếm sản phẩm với từ khóa: " + keyword);
            
            List<Product> products;
            if (keyword.isEmpty()) {
                products = productRepository.findAll();
            } else {
                products = productRepository.findByNameOrIdContaining(keyword);
            }
            
            // Lấy model của bảng
            DefaultTableModel model = (DefaultTableModel) purchaseOrderForm.getTableProducts().getModel();
            
            // Xóa dữ liệu cũ
            model.setRowCount(0);
            
            // Thêm dữ liệu mới
            for (Product product : products) {
                try {
                    String categoryName = product.getCategory() != null ? product.getCategory().getCategoryName() : "Chưa phân loại";
                    model.addRow(new Object[] {
                        product.getProductId(),
                        product.getProductName(),
                        categoryName,
                        product.getStockQuantity(),
                        product.getPrice()
                    });
                } catch (Exception e) {
                    System.err.println("Lỗi khi thêm sản phẩm vào kết quả tìm kiếm: " + e.getMessage());
                }
            }
            
            System.out.println("StockInController: Tìm thấy " + products.size() + " sản phẩm");
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm kiếm sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Thêm sản phẩm vào phiếu nhập (giỏ hàng)
     */
    private void addProductToPurchaseOrder() {
        // Kiểm tra đã chọn sản phẩm chưa
        int selectedRow = purchaseOrderForm.getTableProducts().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(purchaseOrderForm, 
                    "Vui lòng chọn sản phẩm trước khi thêm vào phiếu nhập",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Lấy thông tin sản phẩm được chọn
            String productId = purchaseOrderForm.getTableProducts().getValueAt(selectedRow, 0).toString();
            
            // Kiểm tra sản phẩm đã tồn tại trong phiếu chưa
            for (PurchaseOrderDetail detail : selectedProducts) {
                if (detail.getProduct().getProductId().equals(productId)) {
                    int choice = JOptionPane.showConfirmDialog(purchaseOrderForm, 
                            "Sản phẩm này đã được thêm vào phiếu nhập. Bạn có muốn cập nhật số lượng không?",
                            "Thông báo", JOptionPane.YES_NO_OPTION);
                    
                    if (choice == JOptionPane.YES_OPTION) {
                        // Cập nhật số lượng
                        String quantityStr = JOptionPane.showInputDialog(purchaseOrderForm, 
                                "Nhập số lượng mới:", 
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
                                            "Số lượng phải lớn hơn 0",
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(purchaseOrderForm, 
                                        "Số lượng không hợp lệ",
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                    return;
                }
            }
            
            // Lấy thông tin chi tiết sản phẩm từ repository
            ensureConnection();
            Product product = productRepository.findById(productId).orElse(null);
            
            if (product != null) {
                // Hiển thị dialog để nhập số lượng và đơn giá
                String quantityStr = JOptionPane.showInputDialog(purchaseOrderForm, 
                        "Nhập số lượng:", "1");
                
                if (quantityStr == null || quantityStr.isEmpty()) return; // Người dùng đã hủy
                
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityStr);
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(purchaseOrderForm, 
                                "Số lượng phải lớn hơn 0",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(purchaseOrderForm, 
                            "Số lượng không hợp lệ",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String priceStr = JOptionPane.showInputDialog(purchaseOrderForm, 
                        "Nhập đơn giá:", product.getPrice().toString());
                
                if (priceStr == null || priceStr.isEmpty()) return; // Người dùng đã hủy
                
                BigDecimal price;
                try {
                    price = new BigDecimal(priceStr);
                    if (price.compareTo(BigDecimal.ZERO) <= 0) {
                        JOptionPane.showMessageDialog(purchaseOrderForm, 
                                "Đơn giá phải lớn hơn 0",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(purchaseOrderForm, 
                            "Đơn giá không hợp lệ",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Tạo chi tiết phiếu nhập mới
                PurchaseOrderDetail detail = new PurchaseOrderDetail();
                detail.setProduct(product);
                detail.setQuantity(quantity);
                detail.setUnitPrice(price);
                
                // Thêm vào danh sách
                selectedProducts.add(detail);
                
                // Cập nhật bảng sản phẩm đã chọn
                updateSelectedProductsTable();
                
                // Cập nhật tổng tiền
                updateTotalAmount();
                
                System.out.println("Đã thêm sản phẩm " + product.getProductName() + " vào giỏ hàng");
            } else {
                JOptionPane.showMessageDialog(purchaseOrderForm, 
                        "Không tìm thấy thông tin sản phẩm",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(purchaseOrderForm, 
                    "Lỗi khi thêm sản phẩm vào phiếu nhập: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Xóa sản phẩm khỏi phiếu nhập (giỏ hàng)
     */
    private void removeProductFromPurchaseOrder() {
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
                "Bạn có chắc muốn xóa sản phẩm này khỏi phiếu nhập?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
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
                    "Lỗi khi xóa sản phẩm khỏi phiếu nhập: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
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
                    model.addRow(new Object[]{
                        detail.getProduct().getProductId(),
                        detail.getProduct().getProductName(),
                        // detail.getSupplier().getName(), // Hiển thị tên nhà cung cấp
                        detail.getQuantity(),
                        detail.getUnitPrice()
                    });
                } catch (Exception e) {
                    System.err.println("Lỗi khi thêm sản phẩm vào bảng giỏ hàng: " + e.getMessage());
                }
            }
            
            System.out.println("Đã cập nhật bảng giỏ hàng với " + selectedProducts.size() + " sản phẩm");
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
                BigDecimal amount = detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
                total = total.add(amount);
            }
            
            // Hiển thị tổng tiền
            purchaseOrderForm.getTxtTotalPrice().setText(total.toString() + " đ");
            
            System.out.println("Đã cập nhật tổng tiền: " + total);
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật tổng tiền: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Lưu phiếu nhập hàng
     */
    private void savePurchaseOrder() {
        try {
            // Kiểm tra xem đã chọn nhà cung cấp chưa
            Supplier supplier = (Supplier) purchaseOrderForm.getComboBoxSupplier().getSelectedItem();
            if (supplier == null) {
                JOptionPane.showMessageDialog(purchaseOrderForm, 
                        "Vui lòng chọn nhà cung cấp",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Kiểm tra xem đã có sản phẩm nào được chọn chưa
            if (selectedProducts.isEmpty()) {
                JOptionPane.showMessageDialog(purchaseOrderForm, 
                        "Vui lòng thêm ít nhất một sản phẩm vào phiếu nhập",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Cập nhật thông tin phiếu nhập
            currentPurchaseOrder.setSupplier(supplier);
            currentPurchaseOrder.setEmployee(getCurrentEmployee());
            currentPurchaseOrder.setOrderDate(LocalDateTime.now());
            currentPurchaseOrder.setStatus("Completed");
            
            // Tính tổng tiền
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (PurchaseOrderDetail detail : selectedProducts) {
                BigDecimal detailAmount = detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
                totalAmount = totalAmount.add(detailAmount);
            }
            currentPurchaseOrder.setTotalAmount(totalAmount);
            
            // Lưu phiếu nhập
            ensureConnection();
            PurchaseOrder savedOrder = purchaseOrderRepository.add(currentPurchaseOrder);
            System.out.println("Đã lưu phiếu nhập hàng: " + savedOrder.getPurchaseOrderId());
            
            // Lưu chi tiết phiếu nhập
            for (PurchaseOrderDetail detail : selectedProducts) {
                detail.setPurchaseOrder(savedOrder);
                purchaseOrderDetailRepository.add(detail);
                
                // Cập nhật số lượng sản phẩm trong kho
                Product product = detail.getProduct();
                int newQuantity = product.getStockQuantity() + detail.getQuantity();
                product.setStockQuantity(newQuantity);
                productRepository.update(product);
                System.out.println("Đã cập nhật số lượng sản phẩm " + product.getProductName() + 
                                   " thành " + product.getStockQuantity());
            }
            
            JOptionPane.showMessageDialog(purchaseOrderForm,
                    "Đã lưu phiếu nhập hàng thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            // Làm mới form
            clearForm();
            
            // Tải lại danh sách sản phẩm để cập nhật số lượng
            loadProducts();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(purchaseOrderForm, 
                    "Lỗi khi lưu phiếu nhập: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Xóa dữ liệu trên form
     */
    private void clearForm() {
        try {
            // Xóa danh sách sản phẩm đã chọn
            selectedProducts.clear();
            
            // Cập nhật bảng
            updateSelectedProductsTable();
            
            // Reset tổng tiền
            purchaseOrderForm.getTxtTotalPrice().setText("0");
            
            // Reset nhà cung cấp
            if (purchaseOrderForm.getComboBoxSupplier().getItemCount() > 0) {
                purchaseOrderForm.getComboBoxSupplier().setSelectedIndex(0);
            }
            
            // Khởi tạo phiếu nhập mới
            initializeNewPurchaseOrder();
            
            System.out.println("Đã làm mới form nhập hàng");
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
     * @param employee Nhân viên hiện tại
     */
    public void setCurrentEmployee(Employee employee) {
        this.currentEmployee = employee;
        
        // Hiển thị thông tin nhân viên trên form
        if (employee != null) {
            purchaseOrderForm.getTextField4().setText(employee.getFullName());
        }
    }
}