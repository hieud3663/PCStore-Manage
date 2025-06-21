/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import com.pcstore.controller.WarrantyController;
import com.pcstore.model.*;
import com.pcstore.utils.ButtonUtils;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.TableUtils;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.time.LocalDateTime;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 *
 * @author DUC ANH
 */
public class AddWarrantyForm extends javax.swing.JPanel {
    private WarrantyController controller;
    private List<InvoiceDetail> currentInvoiceDetails; 
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


        
     private com.k33ptoo.components.KButton btnReturnInformationLookup;
     private com.k33ptoo.components.KButton btnWarranty;
     private javax.swing.JScrollPane jScrollPane1;
     private javax.swing.JPanel panelBody;
     private javax.swing.JPanel panelFooter;
     private javax.swing.JPanel panelHeader;
     private com.k33ptoo.components.KGradientPanel panelMain;
     private javax.swing.JTable table;
     private javax.swing.JTextField txtSearch;
   

    /**
     * Constructor với reference đến controller và service form cha
     */
    public AddWarrantyForm(WarrantyController controller) {
        this.controller = controller;
        initComponents();
        
        setupListeners();
        setupTable();
        setupStatusFilter();
        
        // Focus vào ô tìm kiếm khi hiển thị form
        txtSearch.requestFocus();
        
        // Vô hiệu hóa nút bảo hành ban đầu khi chưa có dữ liệu
        btnWarranty.setEnabled(false);
    }

    /**
     * Thiết lập các thuộc tính cho bảng
     */
    private void setupTable() {
        TableUtils.applyDefaultStyle(table);

        // Đặt tên cột
        String[] columnNames = {
            "Mã Chi Tiết Hóa Đơn", 
            "Mã Sản Phẩm", 
            "Tên Sản Phẩm", 
            "Ngày Mua", 
            "Hạn Bảo Hành", 
            "Tên Khách Hàng", 
            "Số Điện Thoại",
            "Trạng Thái"  // Thêm cột mới
        };
        
        // Tạo model
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Integer.class; // ID là kiểu Integer
                return String.class; // Các cột khác là String
            }
        };
        
        // Thiết lập model cho bảng
        table.setModel(model);
        
        // Tùy chỉnh giao diện
        table.setRowHeight(25);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Căn giữa nội dung các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Cập nhật renderer với trạng thái mới
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                // Nếu là cột trạng thái
                if (column == 7 && value != null) {
                    String status = value.toString();
                    if (status.equals("Hết hạn")) {
                        c.setForeground(Color.RED);
                    } else if (status.equals("Đã bảo hành")) {
                        c.setForeground(new Color(128, 0, 128)); // Purple
                    } else if (status.equals("Hợp lệ")) {
                        c.setForeground(new Color(0, 128, 0)); // Green
                    } else if (status.equals("Sắp hết hạn")) {
                        c.setForeground(new Color(255, 140, 0)); // Orange
                    }
                } else {
                    c.setForeground(Color.BLACK); // Màu mặc định cho các cột khác
                }
                
                // Highlight cả dòng nếu sắp hết hạn
                if (!isSelected) {
                    String status = table.getValueAt(row, 7).toString();
                    if (status.equals("Sắp hết hạn")) {
                        c.setBackground(new Color(255, 255, 204)); // Light yellow
                    } else {
                        c.setBackground(table.getBackground());
                    }
                }
                
                return c;
            }
        });
        
        // Thêm tooltip
        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                
                if (row >= 0 && col >= 0) {
                    String status = table.getValueAt(row, 7).toString();
                    String productName = table.getValueAt(row, 2).toString();
                    String warrantyDate = table.getValueAt(row, 4).toString();
                    
                    StringBuilder tooltip = new StringBuilder();
                    tooltip.append("<html><b>").append(productName).append("</b><br>");
                    
                    if ("Hợp lệ".equals(status)) {
                        tooltip.append("Sản phẩm này đủ điều kiện bảo hành<br>");
                        tooltip.append("Hạn bảo hành: ").append(warrantyDate);
                    } else if ("Sắp hết hạn".equals(status)) {
                        tooltip.append("<font color='orange'>Sản phẩm này sắp hết hạn bảo hành</font><br>");
                        tooltip.append("Hạn bảo hành: ").append(warrantyDate);
                    } else if ("Hết hạn".equals(status)) {
                        tooltip.append("<font color='red'>Sản phẩm này đã hết hạn bảo hành</font><br>");
                        tooltip.append("Đã hết hạn vào: ").append(warrantyDate);
                    } else if ("Đã bảo hành".equals(status)) {
                        tooltip.append("<font color='purple'>Sản phẩm này đã được đăng ký bảo hành</font>");
                    }
                    
                    tooltip.append("</html>");
                    table.setToolTipText(tooltip.toString());
                    return;
                }
                
                table.setToolTipText(null);
            }
        });
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelMain = new com.k33ptoo.components.KGradientPanel();
        panelHeader = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        btnReturnInformationLookup = new com.k33ptoo.components.KButton();
        panelBody = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        panelFooter = new javax.swing.JPanel();
        btnWarranty = new com.k33ptoo.components.KButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.BorderLayout());

        panelMain.setBackground(new java.awt.Color(255, 255, 255));
        panelMain.setkFillBackground(false);
        panelMain.setLayout(new javax.swing.BoxLayout(panelMain, javax.swing.BoxLayout.Y_AXIS));

        panelHeader.setBorder(javax.swing.BorderFactory.createTitledBorder("Tìm Kiếm"));
        panelHeader.setOpaque(false);
        panelHeader.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 5));

        txtSearch.setToolTipText("");
        txtSearch.setMargin(new java.awt.Insets(2, 6, 2, 0));
        txtSearch.setPreferredSize(new java.awt.Dimension(300, 35));
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        panelHeader.add(txtSearch);

        btnReturnInformationLookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/search.png"))); // NOI18N
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
        btnReturnInformationLookup.setText(bundle.getString("btnReturnInformationLookup")); // NOI18N
        btnReturnInformationLookup.setkBackGroundColor(new java.awt.Color(102, 255, 255));
        btnReturnInformationLookup.setkEndColor(new java.awt.Color(51, 153, 255));
        btnReturnInformationLookup.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnReturnInformationLookup.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnReturnInformationLookup.setkShowFocusBorder(true);
        btnReturnInformationLookup.setkStartColor(new java.awt.Color(255, 153, 153));
        btnReturnInformationLookup.setMargin(new java.awt.Insets(2, 14, 0, 14));
        btnReturnInformationLookup.setPreferredSize(new java.awt.Dimension(120, 35));
        panelHeader.add(btnReturnInformationLookup);

        panelMain.add(panelHeader);

        panelBody.setBackground(new java.awt.Color(255, 255, 255));
        panelBody.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("pnListOfPurchasedProducts"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 14))); // NOI18N
        panelBody.setLayout(new java.awt.BorderLayout());

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã Chi Tiết Hóa Đơn", "Mã Sản Phẩm", "Tên Sản Phẩm", "Ngày Mua", "Hạn Bảo Hành", "Tên Khách Hàng", "Số Điện Thoại", "Trạng Thái"
            }
        ));
        jScrollPane1.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("txtAddWarrantyInvoiceDetailID")); // NOI18N
            table.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("txtAddWarrantyProductID")); // NOI18N
            table.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("txtAddWarrantyNameProduct")); // NOI18N
            table.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("txtAddWarrantyDateOfPurchase")); // NOI18N
            table.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("txtAddWarrantyWarrantyPeriod")); // NOI18N
            table.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("txtAddWarrantyNameCustomer")); // NOI18N
            table.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("txtAddWarrantyPhoneNumber")); // NOI18N
            table.getColumnModel().getColumn(7).setHeaderValue(bundle.getString("txtAddWarrantyStatus")); // NOI18N
        }

        panelBody.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panelFooter.setBackground(new java.awt.Color(255, 255, 255));
        panelFooter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnWarranty.setText(bundle.getString("btnWarranty")); // NOI18N
        btnWarranty.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnWarranty.setkBackGroundColor(new java.awt.Color(102, 153, 255));
        btnWarranty.setkEndColor(new java.awt.Color(51, 204, 255));
        btnWarranty.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnWarranty.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnWarranty.setkShowFocusBorder(true);
        btnWarranty.setkStartColor(new java.awt.Color(51, 204, 255));
        btnWarranty.setMargin(new java.awt.Insets(2, 14, 0, 14));
        panelFooter.add(btnWarranty);

        panelBody.add(panelFooter, java.awt.BorderLayout.PAGE_END);

        panelMain.add(panelBody);

        add(panelMain, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void setupListeners() {
        // Thiết lập listener cho nút tìm kiếm
        btnReturnInformationLookup.addActionListener(evt -> searchProductsByPhone());
        
        // Thiết lập listener cho nút bảo hành (disable ban đầu)
        btnWarranty.setEnabled(false);
        btnWarranty.addActionListener(evt -> createWarranty());
        
        // Thiết lập listener cho phím Enter trong ô tìm kiếm
        txtSearch.addActionListener(evt -> searchProductsByPhone());
        
        // Thiết lập listener cho double click trên bảng
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    createWarranty();
                }
            }
        });
    }

    private void searchProductsByPhone() {
        String phoneNumber = txtSearch.getText().trim();
        try {
            List<InvoiceDetail> invoiceDetails = controller.findPurchasedProductsByPhone(phoneNumber);
            if (invoiceDetails.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    ErrorMessage.WARRANTY_NOT_FOUND_BY_PHONE,
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Lưu lại danh sách để sử dụng khi người dùng chọn một dòng
            currentInvoiceDetails = invoiceDetails;
            
            // Cập nhật bảng với kết quả tìm kiếm
            updateProductTable(invoiceDetails);
            
            // Cho phép đăng ký bảo hành nếu có dữ liệu
            ButtonUtils.setKButtonEnabled(btnWarranty, true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                ErrorMessage.WARRANTY_SEARCH_ERROR + ": " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProductTable(List<InvoiceDetail> invoiceDetails) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        currentInvoiceDetails = new ArrayList<>(invoiceDetails);
        
        if (invoiceDetails.isEmpty()) {
            System.out.println("Không có dữ liệu để hiển thị");
            btnWarranty.setEnabled(false);
            return;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // Thống kê để hiển thị thông tin tổng quan
        int validCount = 0;
        int expiredCount = 0;
        int alreadyWarrantyCount = 0;
        int nearExpiryCount = 0;
        
        for (InvoiceDetail detail : invoiceDetails) {
            // Kiểm tra null
            if (detail == null) continue;
            
            // Lấy dữ liệu từ đối tượng
            Product product = detail.getProduct();
            Invoice invoice = detail.getInvoice();
            Customer customer = invoice != null ? invoice.getCustomer() : null;
            
            if (product == null) continue;
            
            // Kiểm tra xem sản phẩm này đã có bảo hành chưa và có hết hạn không
            boolean hasWarranty = false;
            boolean isExpired = false;
            String warrantyStatus = ""; // Trạng thái bảo hành để hiển thị
            
            // Kiểm tra nếu đã có bảo hành
            try {
                Optional<Warranty> warranty = controller.getWarrantyService().findWarrantyByInvoiceDetailId(detail.getInvoiceDetailId());
                if (warranty.isPresent()) {
                    hasWarranty = true;
                    warrantyStatus = "Đã bảo hành";
                    System.out.println("Sản phẩm có ID chi tiết " + detail.getInvoiceDetailId() + " đã có bảo hành");
                }
            } catch (Exception e) {
                System.err.println("Lỗi kiểm tra bảo hành: " + e.getMessage());
            }
            
            // Kiểm tra hạn bảo hành
            LocalDateTime warrantyEndDate = null;
            if (invoice != null && invoice.getInvoiceDate() != null) {
                warrantyEndDate = invoice.getInvoiceDate().plusMonths(12);
                isExpired = warrantyEndDate.isBefore(now);
                
                if (isExpired && warrantyStatus.isEmpty()) {
                    warrantyStatus = "Hết hạn";
                    System.out.println("Sản phẩm có ID chi tiết " + detail.getInvoiceDetailId() + " đã hết hạn bảo hành");
                }
            }
            
            // Hạn bảo hành (12 tháng) - Cập nhật hiển thị
            String warrantyPeriod = "";
            long daysLeft = 0;
            if (warrantyEndDate != null) {
                // Tính số ngày còn lại
                daysLeft = java.time.temporal.ChronoUnit.DAYS.between(now, warrantyEndDate);
                
                if (daysLeft > 0) {
                    warrantyPeriod = dateFormatter.format(warrantyEndDate) + " (" + daysLeft + " ngày)";
                } else {
                    warrantyPeriod = dateFormatter.format(warrantyEndDate);
                }
            }
            
            // Trạng thái đủ điều kiện bảo hành - Thêm chi tiết
            if (warrantyStatus.isEmpty()) {
                if (daysLeft < 30 && daysLeft > 0) {
                    warrantyStatus = "Sắp hết hạn";
                } else {
                    warrantyStatus = "Hợp lệ";
                }
            }
            
            // Chuẩn bị dữ liệu hiển thị
            String productId = product.getProductId() != null ? product.getProductId() : "";
            String productName = product.getProductName() != null ? product.getProductName() : "";
            
            // Ngày mua
            String purchaseDate = "";
            if (invoice != null && invoice.getInvoiceDate() != null) {
                purchaseDate = dateFormatter.format(invoice.getInvoiceDate());
            }
            
            // Thông tin khách hàng
            String customerName = "";
            String customerPhone = "";
            if (customer != null) {
                customerName = customer.getFullName() != null ? customer.getFullName() : "";
                customerPhone = customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "";
            }
            
            // Thêm vào bảng
            model.addRow(new Object[]{
                detail.getInvoiceDetailId(), // Mã Chi Tiết Hóa Đơn
                productId,                   // Mã Sản Phẩm
                productName,                 // Tên Sản Phẩm
                purchaseDate,                // Ngày Mua
                warrantyPeriod,              // Hạn Bảo Hành
                customerName,                // Tên Khách Hàng
                customerPhone,               // Số Điện Thoại
                warrantyStatus               // Trạng thái bảo hành
            });
            
            // Cập nhật thống kê
            if ("Hợp lệ".equals(warrantyStatus)) {
                validCount++;
            } else if ("Hết hạn".equals(warrantyStatus)) {
                expiredCount++;
            } else if ("Đã bảo hành".equals(warrantyStatus)) {
                alreadyWarrantyCount++;
            } else if ("Sắp hết hạn".equals(warrantyStatus)) {
                nearExpiryCount++;
            }
        }
        
        // Hiển thị thông tin thống kê
        String summary = String.format("Tìm thấy %d sản phẩm: %d hợp lệ, %d sắp hết hạn, %d đã bảo hành, %d hết hạn",
                model.getRowCount(), validCount, nearExpiryCount, alreadyWarrantyCount, expiredCount);
        
        // Hiển thị thông tin ở dưới bảng (cần thêm một JLabel vào giao diện)
        // lblSummary.setText(summary);
        System.out.println(summary);
        
        // Cho phép đăng ký bảo hành nếu có sản phẩm hợp lệ
        btnWarranty.setEnabled(validCount > 0 || nearExpiryCount > 0);
    }

    private void createWarranty() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                ErrorMessage.WARRANTY_SELECT_ONE,
                "Thông báo",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            String warrantyStatus = (String) table.getValueAt(selectedRow, 7);

            if ("Hết hạn".equals(warrantyStatus)) {
                String expiryDate = (String) table.getValueAt(selectedRow, 4);
                JOptionPane.showMessageDialog(
                    this,
                    String.format(ErrorMessage.WARRANTY_EXPIRED.toString(), expiryDate),
                    "Không thể bảo hành",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if ("Đã bảo hành".equals(warrantyStatus)) {
                Integer selectedDetailId = (Integer) table.getValueAt(selectedRow, 0);
                Optional<Warranty> warranty = controller.getWarrantyService()
                    .findWarrantyByInvoiceDetailId(selectedDetailId);

                String message = ErrorMessage.WARRANTY_ALREADY_REGISTERED.toString();
                if (warranty.isPresent()) {
                    message += "\nMã bảo hành: " + warranty.get().getWarrantyId() +
                              "\nNgày đăng ký: " + dateFormatter.format(warranty.get().getCreatedAt());
                }

                JOptionPane.showMessageDialog(
                    this,
                    message,
                    "Không thể bảo hành",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            Integer selectedDetailId = (Integer) table.getValueAt(selectedRow, 0);

            InvoiceDetail selectedDetail = null;
            for (InvoiceDetail detail : currentInvoiceDetails) {
                if (detail != null && detail.getInvoiceDetailId() != null && 
                    detail.getInvoiceDetailId().equals(selectedDetailId)) {
                    selectedDetail = detail;
                    break;
                }
            }

            if (selectedDetail == null) {
                JOptionPane.showMessageDialog(
                    this,
                    ErrorMessage.WARRANTY_DETAIL_NOT_FOUND,
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            Warranty warranty = controller.createWarrantyFromInvoiceDetail(selectedDetail);

            JOptionPane.showMessageDialog(
                this,
                String.format(ErrorMessage.WARRANTY_REGISTER_SUCCESS.toString(), warranty.getWarrantyId()),
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE
            );

            table.setValueAt("Đã bảo hành", selectedRow, 7);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                ErrorMessage.WARRANTY_REGISTER_ERROR + ": " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void setupStatusFilter() {
        JComboBox<String> statusFilter = new JComboBox<>(
                new String[]{"Tất cả", "Hợp lệ", "Sắp hết hạn", "Hết hạn", "Đã bảo hành"}
        );
        
        statusFilter.addActionListener(e -> {
            String selected = (String) statusFilter.getSelectedItem();
            filterTableByStatus(selected);
        });
        
        // Thêm vào panel trên cùng của bảng
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.add(new JLabel("Lọc theo trạng thái:"));
        filterPanel.add(statusFilter);
        
        // Thêm vào layout
        panelBody.add(filterPanel, BorderLayout.NORTH);
    }

    private void filterTableByStatus(String status) {
        if (currentInvoiceDetails == null || currentInvoiceDetails.isEmpty()) {
            return;
        }
        
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        if ("Tất cả".equals(status)) {
            // Xóa bộ lọc (không áp dụng bộ lọc nào)
            sorter.setRowFilter(null);
        } else {
            // Lọc theo cột trạng thái (cột 7)
            sorter.setRowFilter(RowFilter.regexFilter("^" + status + "$", 7));
        }
    }
}
