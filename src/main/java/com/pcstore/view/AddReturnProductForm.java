package com.pcstore.view;

import com.pcstore.controller.ReturnController;
import com.pcstore.controller.InvoiceController;
import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Return;
import com.pcstore.service.ServiceFactory;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author DUC ANH
 */
public class AddReturnProductForm extends javax.swing.JPanel {

    private ReturnController returnController;
    private InvoiceController invoiceController;
    private DefaultTableModel invoiceTableModel;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private ReturnServiceForm parentForm;

    /**
     * Creates new form AddReapairProduct
     */
    public AddReturnProductForm() {
        initComponents();
        setupTable();
        initControllers();
        addListeners(); // Thêm dòng này để đảm bảo listeners được đăng ký
    }

    /**
     * Constructor để truyền form cha vào để cập nhật dữ liệu
     */
    public AddReturnProductForm(ReturnServiceForm parent) {
        this();
        this.parentForm = parent;
    }

    private void initControllers() {
        try {
            // Khởi tạo các controller cần thiết
            returnController = new ReturnController(
                ServiceFactory.getInstance().getConnection(),
                ServiceFactory.getInvoiceService(),
                ServiceFactory.getProductService()
            );
            
            invoiceController = new InvoiceController(
                ServiceFactory.getInstance().getConnection()
            );
            
            // Tải tất cả đơn hàng sau khi đã thiết lập bảng
            loadAllInvoices();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Không thể kết nối đến cơ sở dữ liệu: " + ex.getMessage(),
                "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void setupTable() {
        // Thiết lập mô hình bảng
        invoiceTableModel = (DefaultTableModel) jTable2.getModel();
        invoiceTableModel.setRowCount(0);
        
        // Thiết lập các tiêu đề cột
        String[] columnNames = {
            "ID Chi tiết", "Mã SP", "Tên Sản Phẩm", "Đơn Giá", 
            "SL Còn Lại", "Ngày Mua", "Tên Khách Hàng", "SĐT", "Trạng Thái"
        };
        
        invoiceTableModel.setColumnIdentifiers(columnNames);
        
        // Ẩn cột ID chi tiết
        jTable2.getColumnModel().getColumn(0).setMinWidth(0);
        jTable2.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable2.getColumnModel().getColumn(0).setWidth(0);
        
        // Thêm sắp xếp và tìm kiếm
        jTable2.setAutoCreateRowSorter(true);
        
        // Thiết lập độ rộng các cột
        jTable2.getColumnModel().getColumn(1).setPreferredWidth(80); // Mã SP
        jTable2.getColumnModel().getColumn(2).setPreferredWidth(200); // Tên SP
        jTable2.getColumnModel().getColumn(3).setPreferredWidth(100); // Đơn giá
        jTable2.getColumnModel().getColumn(4).setPreferredWidth(80); // SL còn lại
        jTable2.getColumnModel().getColumn(5).setPreferredWidth(150); // Ngày mua
        jTable2.getColumnModel().getColumn(6).setPreferredWidth(150); // Tên KH
        jTable2.getColumnModel().getColumn(7).setPreferredWidth(100); // SĐT
        jTable2.getColumnModel().getColumn(8).setPreferredWidth(100); // Trạng thái
    }

    /**
     * Tìm kiếm hóa đơn theo số điện thoại khách hàng
     */
    private void searchByPhoneNumber() {
        String phoneNumber = txtSearch.getText().trim();
        if (phoneNumber.isEmpty()) {
            // Nếu không nhập gì thì hiển thị lại tất cả đơn hàng
            loadAllInvoices();
            return;
        }
        
        try {
            // Tìm hóa đơn theo SĐT khách hàng
            List<Invoice> invoices = invoiceController.getInvoicesByCustomerPhone(phoneNumber);
            
            if (invoices.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy hóa đơn nào cho khách hàng với số điện thoại: " + phoneNumber, 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                // Không xóa dữ liệu trong bảng
                return;
            }
            
            // Hiển thị thông tin các sản phẩm trong hóa đơn
            displayInvoiceDetails(invoices);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tìm kiếm hóa đơn: " + ex.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Hiển thị chi tiết các hóa đơn
     */
    private void displayInvoiceDetails(List<Invoice> invoices) {
        if (invoices == null) return;
        
        // Đảm bảo bảng đã được thiết lập
        if (invoiceTableModel == null) {
            setupTable();
        }
        
        invoiceTableModel.setRowCount(0);
        
        for (Invoice invoice : invoices) {
            try {
                if (invoice == null) continue;
                
                // Lấy chi tiết hóa đơn
                List<InvoiceDetail> details = null;
                try {
                    details = invoiceController.getInvoiceDetails(invoice.getInvoiceId());
                } catch (Exception e) {
                    System.err.println("Lỗi khi lấy chi tiết hóa đơn: " + e.getMessage());
                    continue;
                }
                
                if (details == null || details.isEmpty()) continue;
                
                // Xử lý từng chi tiết hóa đơn
                for (InvoiceDetail detail : details) {
                    try {
                        if (detail == null || detail.getProduct() == null) continue;
                        
                        // Tính số lượng đã trả (nếu có)
                        int returnedQuantity = 0;
                        try {
                            List<Return> returns = returnController.getReturnsByInvoiceDetail(detail.getInvoiceDetailId());
                            if (returns != null) {
                                for (Return ret : returns) {
                                    if (ret != null && ("Approved".equals(ret.getStatus()) || "Completed".equals(ret.getStatus()))) {
                                        returnedQuantity += ret.getQuantity();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Lỗi khi lấy thông tin trả hàng: " + e.getMessage());
                        }
                        
                        // Tính số lượng còn lại
                        int remainingQuantity = detail.getQuantity() - returnedQuantity;
                        
                        // Thiết lập trạng thái ban đầu
                        String status;
                        if (remainingQuantity <= 0) {
                            status = "Đã trả hết";
                        } else if (returnedQuantity > 0) {
                            status = "Đã trả một phần";
                        } else {
                            status = "Chưa trả hàng";
                        }
                        
                        // Thêm thông tin vào bảng
                        Object[] rowData = {
                            detail.getInvoiceDetailId(),
                            detail.getProduct().getProductId(),
                            detail.getProduct().getProductName(),
                            detail.getUnitPrice(),
                            remainingQuantity,
                            invoice.getInvoiceDate() != null ? invoice.getInvoiceDate().format(dateFormatter) : "",
                            invoice.getCustomer() != null ? invoice.getCustomer().getFullName() : "Khách lẻ",
                            invoice.getCustomer() != null ? invoice.getCustomer().getPhoneNumber() : "",
                            status
                        };
                        
                        // Thêm dữ liệu vào bảng
                        invoiceTableModel.addRow(rowData);
                        
                    } catch (Exception e) {
                        System.err.println("Lỗi khi xử lý chi tiết hóa đơn: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi xử lý hóa đơn: " + e.getMessage());
            }
        }
    }
    
    /**
     * Tải tất cả đơn hàng và hiển thị trong bảng
     */
    private void loadAllInvoices() {
        try {
            // Đảm bảo bảng đã được thiết lập
            if (invoiceTableModel == null) {
                setupTable();
            }
            
            // Xóa hết dữ liệu hiện có trong bảng
            invoiceTableModel.setRowCount(0);
            
            System.out.println("Đang tải danh sách hóa đơn cho trả hàng...");
            
            // Sử dụng phương thức mới đơn giản hơn
            List<Invoice> invoices = invoiceController.getAllInvoicesSimple();
            
            if (invoices.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy hóa đơn nào trong hệ thống.", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            System.out.println("Tìm thấy " + invoices.size() + " hóa đơn.");
            
            // Hiển thị thông tin các sản phẩm trong hóa đơn
            displayInvoiceDetails(invoices);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu hóa đơn: " + ex.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    /**
     * Xử lý chức năng trả hàng
     */
    private void createReturn() {
        try {
            System.out.println("Bắt đầu tạo đơn đổi trả...");
            
            // Lấy dòng được chọn
            int selectedRow = jTable2.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để trả hàng", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Chuyển từ dòng hiển thị sang dòng thực trong model (quan trọng khi có sắp xếp)
            selectedRow = jTable2.convertRowIndexToModel(selectedRow);
            
            // Lấy thông tin từ dòng được chọn
            int invoiceDetailId = Integer.parseInt(invoiceTableModel.getValueAt(selectedRow, 0).toString());
            String productName = invoiceTableModel.getValueAt(selectedRow, 2).toString();
            int availableQuantity = Integer.parseInt(invoiceTableModel.getValueAt(selectedRow, 4).toString());
            String invoiceDateStr = invoiceTableModel.getValueAt(selectedRow, 5).toString();
            
            System.out.println("Thông tin sản phẩm: ID=" + invoiceDetailId + ", Tên=" + productName + ", SL=" + availableQuantity);
            
            // Kiểm tra còn hàng để trả không
            if (availableQuantity <= 0) {
                JOptionPane.showMessageDialog(this, "Sản phẩm này đã trả hết", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Chuyển đổi ngày hóa đơn từ chuỗi
            LocalDateTime invoiceDate = LocalDateTime.parse(invoiceDateStr, dateFormatter);
            LocalDateTime now = LocalDateTime.now();
            
            // Kiểm tra hạn 30 ngày
            long daysBetween = ChronoUnit.DAYS.between(invoiceDate.toLocalDate(), now.toLocalDate());
            if (daysBetween > 30) {
                int option = JOptionPane.showConfirmDialog(this, 
                    "Sản phẩm này đã quá 30 ngày kể từ ngày mua (" + daysBetween + " ngày).\n"
                    + "Việc trả hàng có thể bị từ chối hoặc áp dụng điều kiện đặc biệt.\n"
                    + "Bạn vẫn muốn tiếp tục?",
                    "Cảnh báo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            // Nhập số lượng trả
            String quantityStr = JOptionPane.showInputDialog(this, 
                "Nhập số lượng sản phẩm " + productName + " muốn trả (tối đa " + availableQuantity + "):",
                "Nhập số lượng", JOptionPane.QUESTION_MESSAGE);
            
            if (quantityStr == null || quantityStr.trim().isEmpty()) {
                return; // Người dùng hủy
            }
            
            // Chuyển đổi và kiểm tra số lượng nhập vào
            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr.trim());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (quantity > availableQuantity) {
                    JOptionPane.showMessageDialog(this, "Số lượng không được vượt quá " + availableQuantity, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Nhập lý do trả hàng
            String reason = JOptionPane.showInputDialog(this, 
                "Nhập lý do trả hàng:", "Lý do", JOptionPane.QUESTION_MESSAGE);
            
            if (reason == null || reason.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do trả hàng", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Nhập ghi chú (không bắt buộc)
            String notes = JOptionPane.showInputDialog(this, 
                "Nhập ghi chú (không bắt buộc):", "Ghi chú", JOptionPane.QUESTION_MESSAGE);
            
            System.out.println("Đang tạo đơn trả hàng với thông tin: InvoiceDetailID=" + invoiceDetailId + 
                              ", Quantity=" + quantity + ", Reason=" + reason);
            
            // Tạo đơn trả hàng
            Return returnObj = returnController.createReturn(invoiceDetailId, quantity, reason, notes);
            
            if (returnObj != null) {
                System.out.println("Tạo đơn trả hàng thành công: ID=" + returnObj.getReturnId());
                JOptionPane.showMessageDialog(this, "Đã tạo đơn trả hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                
                // Cập nhật lại bảng
                loadAllInvoices();
                
                // Gọi phương thức cập nhật danh sách đơn trả hàng ở form cha
                if (parentForm != null) {
                    parentForm.loadAllReturns();
                }
            } else {
                System.err.println("Không thể tạo đơn trả hàng");
                JOptionPane.showMessageDialog(this, "Không thể tạo đơn trả hàng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            System.err.println("Lỗi khi tạo đơn trả hàng: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tạo đơn trả hàng: " + ex.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Thêm xử lý sự kiện cho các nút trong form
    private void addListeners() {
        System.out.println("Đăng ký sự kiện cho các nút...");
        
        btnReturnInformationLookup.addActionListener(e -> {
            System.out.println("Nút tìm kiếm được nhấn");
            searchByPhoneNumber();
        });
        
        btnWarranty.addActionListener(e -> {
            System.out.println("Nút đổi trả được nhấn");
            createReturn();
        });
        
        // Đảm bảo nút trả hàng hoạt động
        btnWarranty.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        kGradientPanel3 = new com.k33ptoo.components.KGradientPanel();
        pnSearch = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        btnReturnInformationLookup = new com.k33ptoo.components.KButton();
        pnMain = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        btnWarranty = new com.k33ptoo.components.KButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã Sản Phẩm", "Tên Sản Phẩm", "Hãng Sản Xuất", "Ngày Mua", "Hạn Bảo Hành", "Tên Khách Hàng", "Số Điện Thoại"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/pcstore/resources/vi_VN"); // NOI18N
        kGradientPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("ReTurnService"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N
        kGradientPanel3.setkFillBackground(false);

        pnSearch.setBorder(javax.swing.BorderFactory.createTitledBorder("Tìm Kiếm SĐT Khách Hàng\n"));

        txtSearch.setToolTipText("");
        txtSearch.setMargin(new java.awt.Insets(2, 6, 2, 0));
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });

        btnReturnInformationLookup.setText(bundle.getString("btnReturnInformationLookup")); // NOI18N
        btnReturnInformationLookup.setkBackGroundColor(new java.awt.Color(102, 153, 255));
        btnReturnInformationLookup.setkEndColor(new java.awt.Color(102, 153, 255));
        btnReturnInformationLookup.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnReturnInformationLookup.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnReturnInformationLookup.setkHoverStartColor(new java.awt.Color(153, 255, 153));
        btnReturnInformationLookup.setkStartColor(new java.awt.Color(102, 153, 255));
        btnReturnInformationLookup.setMargin(new java.awt.Insets(2, 14, 0, 14));

        javax.swing.GroupLayout pnSearchLayout = new javax.swing.GroupLayout(pnSearch);
        pnSearch.setLayout(pnSearchLayout);
        pnSearchLayout.setHorizontalGroup(
            pnSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReturnInformationLookup, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        pnSearchLayout.setVerticalGroup(
            pnSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnSearchLayout.createSequentialGroup()
                .addGroup(pnSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReturnInformationLookup, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã Sản Phẩm", "Tên Sản Phẩm", "Hãng Sản Xuất", "Ngày Mua", "Tên Khách Hàng", "Số Điện Thoại", "Trạng Thái"
            }
        ));
        jScrollPane2.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("cl1ProductCode")); // NOI18N
            jTable2.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("clNameProduct")); // NOI18N
            jTable2.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("clManufacturer")); // NOI18N
            jTable2.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("clDateOfPurchase")); // NOI18N
            jTable2.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("clNameCustomer")); // NOI18N
            jTable2.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("clStatus")); // NOI18N
        }

        btnWarranty.setText(bundle.getString("btnReturn")); // NOI18N
        btnWarranty.setkBackGroundColor(new java.awt.Color(102, 153, 255));
        btnWarranty.setkEndColor(new java.awt.Color(102, 153, 255));
        btnWarranty.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnWarranty.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnWarranty.setkHoverStartColor(new java.awt.Color(153, 255, 153));
        btnWarranty.setkStartColor(new java.awt.Color(102, 153, 255));
        btnWarranty.setMargin(new java.awt.Insets(2, 14, 0, 14));

        javax.swing.GroupLayout pnMainLayout = new javax.swing.GroupLayout(pnMain);
        pnMain.setLayout(pnMainLayout);
        pnMainLayout.setHorizontalGroup(
            pnMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnMainLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(pnMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnWarranty, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 886, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        pnMainLayout.setVerticalGroup(
            pnMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnMainLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnWarranty, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(56, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout kGradientPanel3Layout = new javax.swing.GroupLayout(kGradientPanel3);
        kGradientPanel3.setLayout(kGradientPanel3Layout);
        kGradientPanel3Layout.setHorizontalGroup(
            kGradientPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(kGradientPanel3Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(pnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 496, Short.MAX_VALUE))
        );
        kGradientPanel3Layout.setVerticalGroup(
            kGradientPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(pnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(kGradientPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        searchByPhoneNumber();
    }//GEN-LAST:event_txtSearchActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.k33ptoo.components.KButton btnReturnInformationLookup;
    private com.k33ptoo.components.KButton btnWarranty;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private com.k33ptoo.components.KGradientPanel kGradientPanel3;
    private javax.swing.JPanel pnMain;
    private javax.swing.JPanel pnSearch;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
