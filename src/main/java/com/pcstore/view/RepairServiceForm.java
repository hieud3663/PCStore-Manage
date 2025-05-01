/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JDialog;

import com.pcstore.controller.RepairController;
import com.pcstore.model.enums.RepairEnum;
import com.pcstore.utils.TableStyleUtil;
import com.pcstore.view.AddReapairProductForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author DUC ANH
 */
public class RepairServiceForm extends javax.swing.JPanel {

    private RepairController repairController;

    private final Map<String, String> statusTranslation;
   

    /**
     * Creates new form RepairService
     */
    public RepairServiceForm() {
        initComponents();
        try {
            loadRepairServices();
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Lỗi khi khởi tạo form: " + e.getMessage(), 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }

        TableStyleUtil.applyDefaultStyle(tableRepair);
      

        statusTranslation = new HashMap<>();
        statusTranslation.put("Received", "Đã tiếp nhận");
        statusTranslation.put("Diagnosing", "Đang chẩn đoán");
        statusTranslation.put("Waiting for Parts", "Chờ linh kiện");
        statusTranslation.put("Repairing", "Đang sửa chữa");
        statusTranslation.put("Completed", "Đã hoàn thành");
        statusTranslation.put("Delivered", "Đã giao khách");
        statusTranslation.put("Cancelled", "Đã hủy");
    
        setupSearchFunctionality();
    }

    /**
     * Tải danh sách dịch vụ sửa chữa
     */
    public void loadRepairServices() {
        try {
            System.out.println("Đang tải danh sách dịch vụ sửa chữa...");
            
            // Lưu trạng thái tìm kiếm và sắp xếp hiện tại
            TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) tableRepair.getRowSorter();
            RowFilter<? super TableModel, ? super Integer> currentFilter = null;
            List<? extends RowSorter.SortKey> currentSortKeys = null;
            
            if (sorter != null) {
                currentFilter = sorter.getRowFilter();
                currentSortKeys = sorter.getSortKeys();
            }
            
            // Xóa dữ liệu hiện tại trong bảng
            javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) tableRepair.getModel();
            model.setRowCount(0);
            
            if (repairController == null) {
                System.out.println("Cảnh báo: RepairController chưa được thiết lập");
                return;
            }
            
            // Lấy danh sách dịch vụ từ controller - đảm bảo lấy dữ liệu mới nhất
            List<com.pcstore.model.Repair> repairs = repairController.getAllRepairServices();
            System.out.println("Đã tìm thấy " + repairs.size() + " dịch vụ sửa chữa");
            
            // Thêm dữ liệu vào bảng
            if (repairs.isEmpty()) {
                model.addRow(new Object[]{"", "Không có dữ liệu", "", "", "", "", "", ""});
                return;
            }
            
            for (com.pcstore.model.Repair repair : repairs) {
                String customerName = repair.getCustomer() != null ? repair.getCustomer().getFullName() : "N/A";
                
                // Lấy số điện thoại của khách hàng
                String customerPhone = "N/A";
                if (repair.getCustomer() != null && repair.getCustomer().getPhoneNumber() != null) {
                    customerPhone = repair.getCustomer().getPhoneNumber();
                }
                
                String deviceName = repair.getDeviceName() != null ? repair.getDeviceName() : "N/A";
                String problem = repair.getProblem() != null ? repair.getProblem() : "N/A";
                String fee = repair.getServiceFee() != null ? repair.getServiceFee().toString() : "0";
                
                // Lấy trạng thái tiếng Anh
                String statusEn = repair.getStatus() != null ? repair.getStatus().getStatus() : "N/A";
                
                // Chuyển đổi sang tiếng Việt
                String statusVi = statusTranslation.getOrDefault(statusEn, statusEn);
                
                String notes = repair.getNotes() != null ? repair.getNotes() : "";
                
                model.addRow(new Object[]{
                    repair.getRepairServiceId(),
                    customerName,
                    customerPhone,  // Thêm số điện thoại vào cột thứ 3
                    deviceName,
                    problem,
                    fee,
                    statusVi,
                    notes
                });
            }
            
            // Khôi phục trạng thái tìm kiếm và sắp xếp
            if (sorter != null) {
                if (currentFilter != null) {
                    sorter.setRowFilter(currentFilter);
                }
                
                if (currentSortKeys != null && !currentSortKeys.isEmpty()) {
                    sorter.setSortKeys(currentSortKeys);
                }
            }
            
            // Tùy chọn: hiển thị thông báo cập nhật thành công trong console
            System.out.println("Cập nhật bảng dịch vụ sửa chữa thành công!");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi tải danh sách dịch vụ sửa chữa: " + e.getMessage());
            // Hiển thị thông báo lỗi cho người dùng nếu cần
        }
    }

    /**
     * Thiết lập controller
     */
    public void setRepairController(RepairController controller) {
        this.repairController = controller;
        loadRepairServices();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnMainRepair = new com.k33ptoo.components.KGradientPanel();
        pnRepairFunctions = new javax.swing.JPanel();
        btnAddRepair = new com.k33ptoo.components.KButton();
        btnRemoveRepair = new com.k33ptoo.components.KButton();
        btnDetail = new com.k33ptoo.components.KButton();
        btnUpdateStatus = new com.k33ptoo.components.KButton();
        textFieldSearch = new com.pcstore.utils.TextFieldSearch();
        panelBody = new javax.swing.JPanel();
        jScrollPaneTable = new javax.swing.JScrollPane();
        tableRepair = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1153, 713));
        setLayout(new java.awt.BorderLayout());

        pnMainRepair.setkFillBackground(false);
        pnMainRepair.setPreferredSize(new java.awt.Dimension(1120, 713));
        pnMainRepair.setLayout(new javax.swing.BoxLayout(pnMainRepair, javax.swing.BoxLayout.Y_AXIS));

        pnRepairFunctions.setBackground(new java.awt.Color(255, 255, 255));
        pnRepairFunctions.setPreferredSize(new java.awt.Dimension(490, 70));
        pnRepairFunctions.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 15));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/pcstore/resources/vi_VN"); // NOI18N
        btnAddRepair.setText(bundle.getString("btnReturnProduct")); // NOI18N
        btnAddRepair.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAddRepair.setkAllowGradient(false);
        btnAddRepair.setkBackGroundColor(new java.awt.Color(0, 190, 94));
        btnAddRepair.setkBorderRadius(30);
        btnAddRepair.setkEndColor(new java.awt.Color(0, 255, 51));
        btnAddRepair.setkFocusColor(new java.awt.Color(255, 255, 255));
        btnAddRepair.setkHoverColor(new java.awt.Color(0, 204, 124));
        btnAddRepair.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnAddRepair.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnAddRepair.setkStartColor(new java.awt.Color(0, 204, 255));
        btnAddRepair.setPreferredSize(new java.awt.Dimension(150, 35));
        btnAddRepair.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddRepairMouseClicked(evt);
            }
        });
        pnRepairFunctions.add(btnAddRepair);

        btnRemoveRepair.setText(bundle.getString("btnRemoveRepair")); // NOI18N
        btnRemoveRepair.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnRemoveRepair.setkAllowGradient(false);
        btnRemoveRepair.setkBackGroundColor(new java.awt.Color(255, 0, 51));
        btnRemoveRepair.setkBorderRadius(30);
        btnRemoveRepair.setkEndColor(new java.awt.Color(255, 102, 51));
        btnRemoveRepair.setkHoverColor(new java.awt.Color(255, 71, 91));
        btnRemoveRepair.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnRemoveRepair.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnRemoveRepair.setkStartColor(new java.awt.Color(255, 0, 51));
        btnRemoveRepair.setPreferredSize(new java.awt.Dimension(150, 35));
        btnRemoveRepair.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRemoveRepairMouseClicked(evt);
            }
        });
        btnRemoveRepair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveRepairActionPerformed(evt);
            }
        });
        pnRepairFunctions.add(btnRemoveRepair);

        btnDetail.setText(bundle.getString("btnDetailReturnCard")); // NOI18N
        btnDetail.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDetail.setkAllowGradient(false);
        btnDetail.setkBackGroundColor(new java.awt.Color(102, 153, 255));
        btnDetail.setkEndColor(new java.awt.Color(102, 153, 255));
        btnDetail.setkHoverColor(new java.awt.Color(102, 185, 241));
        btnDetail.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnDetail.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnDetail.setkIndicatorThickness(255);
        btnDetail.setPreferredSize(new java.awt.Dimension(150, 35));
        btnDetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDetailMouseClicked(evt);
            }
        });
        pnRepairFunctions.add(btnDetail);

        btnUpdateStatus.setText(bundle.getString("btnUpdateStatus")); // NOI18N
        btnUpdateStatus.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUpdateStatus.setkAllowGradient(false);
        btnUpdateStatus.setkBackGroundColor(new java.awt.Color(255, 102, 0));
        btnUpdateStatus.setkHoverColor(new java.awt.Color(255, 153, 0));
        btnUpdateStatus.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnUpdateStatus.setkShowFocusBorder(true);
        btnUpdateStatus.setPreferredSize(new java.awt.Dimension(150, 35));
        btnUpdateStatus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnUpdateStatusMouseClicked(evt);
            }
        });
        pnRepairFunctions.add(btnUpdateStatus);

        textFieldSearch.setPreferredSize(new java.awt.Dimension(400, 31));
        pnRepairFunctions.add(textFieldSearch);

        pnMainRepair.add(pnRepairFunctions);

        panelBody.setLayout(new java.awt.BorderLayout());

        tableRepair.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã Sửa Chữa", "Tên Khách Hàng", "Số điện thoại", "Tên Thiết Bị", "Vấn Đề Sữa Chữa", "Chi Phí ", "Trạng Thái", "Ghi Chú"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPaneTable.setViewportView(tableRepair);

        panelBody.add(jScrollPaneTable, java.awt.BorderLayout.CENTER);

        pnMainRepair.add(panelBody);

        add(pnMainRepair, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    private void btnAddRepairMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddRepairMouseClicked
        // Call the action performed method to avoid duplicate code
        btnAddRepairActionPerformed(null);
    }//GEN-LAST:event_btnAddRepairMouseClicked

    private void btnAddRepairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRepairActionPerformed
        try {
            // Tạo và hiển thị dialog AddReapairProductForm
            AddReapairProductForm addDialog = new AddReapairProductForm();
            addDialog.setTitle("Thêm sản phẩm sửa chữa");
            addDialog.setSize(850, 700);
            addDialog.setLocationRelativeTo(this);
            addDialog.setModal(true); // Làm dialog modal để chặn tương tác với form cha
            addDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            // Truyền controller vào dialog
            if (repairController != null) {
                addDialog.setRepairController(repairController);
            } else {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Cảnh báo: Controller chưa được thiết lập. Thao tác thêm mới có thể không hoạt động.",
                    "Cảnh báo",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            }

            // Hiển thị dialog
            addDialog.setVisible(true);

            // Kiểm tra xem đã thêm mới thành công hay không
            if (addDialog.isRepairAdded()) {
                // Cập nhật bảng với dữ liệu mới
                loadRepairServices();

                // Có thể thêm thông báo sau khi đã cập nhật bảng thành công
                System.out.println("Đã cập nhật bảng sau khi thêm mới dịch vụ sửa chữa");
            }
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this,
                "Lỗi khi mở form thêm mới: " + e.getMessage(),
                "Lỗi",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAddRepairActionPerformed

    private void btnDetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDetailMouseClicked
        // Gọi phương thức xử lý sự kiện action để tránh lặp code
        btnDetailActionPerformed(null);
    }//GEN-LAST:event_btnDetailMouseClicked

    private void btnDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetailActionPerformed
        try {
            // Kiểm tra xem có hàng nào được chọn không
            int selectedRow = tableRepair.getSelectedRow();
            if (selectedRow == -1) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một dịch vụ sửa chữa để xem chi tiết.",
                    "Thông báo",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Lấy ID của dịch vụ sửa chữa được chọn
            Object repairIdObj = tableRepair.getValueAt(selectedRow, 0);
            if (repairIdObj == null || repairIdObj.toString().isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Dịch vụ sửa chữa không có ID hợp lệ.",
                    "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            Integer repairId = null;
            try {
                repairId = Integer.parseInt(repairIdObj.toString());
            } catch (NumberFormatException e) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "ID dịch vụ sửa chữa không hợp lệ: " + repairIdObj,
                    "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra controller
            if (repairController == null) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Controller chưa được khởi tạo.",
                    "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Lấy thông tin chi tiết dịch vụ sửa chữa
            java.util.Optional<com.pcstore.model.Repair> repairOpt = repairController.getRepairServiceById(repairId);
            if (!repairOpt.isPresent()) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Không tìm thấy thông tin dịch vụ sửa chữa với ID: " + repairId,
                    "Thông báo",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Hiển thị dialog chi tiết
            javax.swing.JDialog detailDialog = new javax.swing.JDialog();
            detailDialog.setTitle("Chi tiết dịch vụ sửa chữa");
            detailDialog.setSize(900,800 );
            detailDialog.setLocationRelativeTo(this);
            detailDialog.setModal(true);

            // Tạo form chi tiết
            RepairDetailsForm detailsForm = new RepairDetailsForm();
            detailsForm.setRepairDetails(repairOpt.get());

            // Thêm form vào dialog
            detailDialog.getContentPane().add(detailsForm, java.awt.BorderLayout.CENTER);
            detailsForm.addCloseButton(detailDialog); // Thêm nút đóng

            detailDialog.pack();
            detailDialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this,
                "Lỗi khi hiển thị chi tiết: " + e.getMessage(),
                "Lỗi",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnDetailActionPerformed

    private void btnRemoveRepairMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRemoveRepairMouseClicked
        // Gọi phương thức xử lý sự kiện action để tránh lặp code
        btnRemoveRepairActionPerformed(null);
    }//GEN-LAST:event_btnRemoveRepairMouseClicked

    private void btnRemoveRepairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveRepairActionPerformed
        try {
            // Kiểm tra xem có hàng nào được chọn không
            int selectedRow = tableRepair.getSelectedRow();
            if (selectedRow == -1) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một dịch vụ sửa chữa để xóa.",
                    "Thông báo",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Lấy ID của dịch vụ sửa chữa được chọn
            Object repairIdObj = tableRepair.getValueAt(selectedRow, 0);
            if (repairIdObj == null || repairIdObj.toString().isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Dịch vụ sửa chữa không có ID hợp lệ.",
                    "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            Integer repairId = null;
            try {
                repairId = Integer.parseInt(repairIdObj.toString());
            } catch (NumberFormatException e) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "ID dịch vụ sửa chữa không hợp lệ: " + repairIdObj,
                    "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra controller
            if (repairController == null) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Controller chưa được khởi tạo.",
                    "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Xác nhận trước khi xóa
            int choice = javax.swing.JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa dịch vụ sửa chữa này không?",
                "Xác nhận xóa",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE);

            if (choice != javax.swing.JOptionPane.YES_OPTION) {
                return; // Người dùng đã hủy việc xóa
            }

            // Tiến hành xóa dịch vụ
            boolean success = repairController.deleteRepair(repairId);

            if (success) {
                // Hiển thị thông báo thành công
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Đã xóa dịch vụ sửa chữa thành công!",
                    "Thành công",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);

                // Cập nhật lại bảng
                loadRepairServices();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Không thể xóa dịch vụ sửa chữa. Vui lòng thử lại sau.",
                    "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this,
                "Lỗi khi xóa dịch vụ sửa chữa: " + e.getMessage(),
                "Lỗi",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnRemoveRepairActionPerformed

    private void btnUpdateStatusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUpdateStatusMouseClicked
        updateRepairStatus();
    }//GEN-LAST:event_btnUpdateStatusMouseClicked

    /**
     * Thiết lập chức năng tìm kiếm cho bảng
     */
    private void setupSearchFunctionality() {
             
        // Tạo sorter cho bảng sử dụng TableStyleUtil
        TableRowSorter<TableModel> sorter = TableStyleUtil.setupSorting(tableRepair);
        
        // Thêm sự kiện tìm kiếm khi thay đổi text
        textFieldSearch.getTxtSearchField().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String searchText = textFieldSearch.getTxtSearchField().getText();
                applySearchFilter(searchText);
            }
        });
        
        // Thêm sự kiện khi người dùng xóa text trong ô tìm kiếm
        textFieldSearch.getTxtSearchField().addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (textFieldSearch.getTxtSearchField().getText().isEmpty()) {
                    applySearchFilter("");
                }
            }
        });
    }

    /**
     * Áp dụng bộ lọc tìm kiếm
     * @param searchText Từ khóa tìm kiếm
     */
    private void applySearchFilter(String searchText) {
        // Sử dụng tiện ích TableStyleUtil để áp dụng bộ lọc tìm kiếm
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) tableRepair.getRowSorter();
        
        // Chỉ định các cột cần tìm kiếm:
        // 1: Tên khách hàng
        // 2: Số điện thoại
        // 3: Tên thiết bị
        // 4: Vấn đề sửa chữa
        // 6: Trạng thái
        // 7: Ghi chú
        TableStyleUtil.applyFilter(sorter, searchText, 1, 2, 3, 4, 6, 7);
        
    }

    /**
     * Cập nhật trạng thái dịch vụ sửa chữa
     */
    private void updateRepairStatus() {
        // Lấy dòng đang được chọn
        int selectedRow = tableRepair.getSelectedRow();
        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một dịch vụ sửa chữa để cập nhật trạng thái",
                "Thông báo", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Lấy thông tin từ dòng được chọn
        Object repairIdObj = tableRepair.getValueAt(selectedRow, 0);
        String deviceName = tableRepair.getValueAt(selectedRow, 2).toString();
        String currentStatus = tableRepair.getValueAt(selectedRow, 5).toString();
        
        if (repairIdObj == null || repairIdObj.toString().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Dịch vụ sửa chữa không có ID hợp lệ.",
                "Lỗi",
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Integer repairId;
        try {
            repairId = Integer.parseInt(repairIdObj.toString());
        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "ID dịch vụ sửa chữa không hợp lệ: " + repairIdObj,
                "Lỗi",
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Lấy thông tin dịch vụ sửa chữa hiện tại
            java.util.Optional<com.pcstore.model.Repair> repairOpt = repairController.getRepairServiceById(repairId);
            if (repairOpt.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy thông tin dịch vụ sửa chữa với ID: " + repairId, 
                    "Thông báo", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            com.pcstore.model.Repair repair = repairOpt.get();
            
            // Tạo danh sách các trạng thái có thể chuyển đổi bằng tiếng Việt
            String[] availableStatusesVi = {
                "Đã tiếp nhận", "Đang chẩn đoán", "Chờ linh kiện", 
                "Đang sửa chữa", "Đã hoàn thành", "Đã giao khách", "Đã hủy"
            };
            
            // Hiển thị dialog để chọn trạng thái mới
            String newStatusVi = (String) javax.swing.JOptionPane.showInputDialog(
                this,
                "Chọn trạng thái mới cho dịch vụ sửa chữa #" + repairId + " - " + deviceName,
                "Cập nhật trạng thái",
                javax.swing.JOptionPane.QUESTION_MESSAGE,
                null,
                availableStatusesVi,
                getCurrentStatusVi(currentStatus)
            );
            
            // Nếu người dùng không chọn hoặc hủy
            if (newStatusVi == null) {
                return;
            }
            
            // Chuyển đổi từ tiếng Việt sang tiếng Anh để lưu vào DB
            String newStatusEn = null;
            for (Map.Entry<String, String> entry : statusTranslation.entrySet()) {
                if (entry.getValue().equals(newStatusVi)) {
                    newStatusEn = entry.getKey();
                    break;
                }
            }
            
            if (newStatusEn == null) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Lỗi chuyển đổi trạng thái", 
                    "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Xử lý logic đặc biệt khi chuyển sang trạng thái "Completed"
            if ("Completed".equals(newStatusEn)) {
                // Dialog nhập phí dịch vụ cuối cùng
                String feeInput = javax.swing.JOptionPane.showInputDialog(this,
                    "Nhập phí dịch vụ cuối cùng (VNĐ):",
                    repair.getServiceFee() != null ? repair.getServiceFee().toString() : "0"
                );
                
                if (feeInput == null) {
                    return; // Người dùng đã hủy
                }
                
                try {
                    // Cập nhật phí dịch vụ
                    java.math.BigDecimal fee = new java.math.BigDecimal(feeInput);
                    
                    // Yêu cầu nhập ghi chú hoàn thành
                    String notes = javax.swing.JOptionPane.showInputDialog(this,
                        "Nhập ghi chú hoàn thành:",
                        repair.getNotes() != null ? repair.getNotes() : ""
                    );
                    
                    // Cập nhật trạng thái và thông tin
                    com.pcstore.model.Repair updatedRepair = new com.pcstore.model.Repair(
                        repair.getRepairServiceId(),
                        repair.getCustomer(),
                        repair.getDeviceName(),
                        repair.getProblem(),
                        repair.getDiagnosis(),
                        fee,
                        RepairEnum.fromString(newStatusEn),
                        notes,
                        repair.getCreatedAt(),
                        java.time.LocalDateTime.now()
                    );
                    
                    // Gọi controller để cập nhật
                    com.pcstore.model.Repair result = repairController.updateRepairService(
                        updatedRepair.getRepairServiceId(),
                        updatedRepair.getDeviceName(),
                        updatedRepair.getProblem(),
                        updatedRepair.getDiagnosis(),
                        updatedRepair.getServiceFee(),
                        updatedRepair.getStatus(),
                        updatedRepair.getNotes()
                    );
                    
                    if (result != null) {
                        javax.swing.JOptionPane.showMessageDialog(this,
                            "Đã hoàn thành dịch vụ sửa chữa thành công!",
                            "Thành công", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        
                        // Tải lại dữ liệu
                        loadRepairServices();
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(this,
                            "Không thể cập nhật trạng thái. Vui lòng thử lại sau!",
                            "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (NumberFormatException e) {
                    javax.swing.JOptionPane.showMessageDialog(this,
                        "Phí dịch vụ không hợp lệ! Vui lòng nhập số.",
                        "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            } 
            // Xử lý khi chuyển sang trạng thái "Cancelled"
            else if ("Cancelled".equals(newStatusEn)) {
                // Xác nhận hủy dịch vụ
                int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn hủy dịch vụ sửa chữa này?",
                    "Xác nhận hủy",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.WARNING_MESSAGE);
                
                if (confirm != javax.swing.JOptionPane.YES_OPTION) {
                    return;
                }
                
                // Yêu cầu nhập lý do hủy
                String cancelReason = javax.swing.JOptionPane.showInputDialog(this,
                    "Nhập lý do hủy dịch vụ:", 
                    "");
                    
                if (cancelReason == null) {
                    cancelReason = "Đã hủy không có lý do";
                }
                
                // Cập nhật trạng thái và thông tin
                com.pcstore.model.Repair updatedRepair = new com.pcstore.model.Repair(
                    repair.getRepairServiceId(),
                    repair.getCustomer(),
                    repair.getDeviceName(),
                    repair.getProblem(),
                    repair.getDiagnosis(),
                    repair.getServiceFee(),
                    RepairEnum.fromString(newStatusEn),
                    cancelReason,
                    repair.getCreatedAt(),
                    java.time.LocalDateTime.now()
                );
                
                // Gọi controller để cập nhật
                com.pcstore.model.Repair result = repairController.updateRepairService(
                    updatedRepair.getRepairServiceId(),
                    updatedRepair.getDeviceName(),
                    updatedRepair.getProblem(),
                    updatedRepair.getDiagnosis(),
                    updatedRepair.getServiceFee(),
                    updatedRepair.getStatus(),
                    updatedRepair.getNotes()
                );
                
                if (result != null) {
                    javax.swing.JOptionPane.showMessageDialog(this,
                        "Đã hủy dịch vụ sửa chữa thành công!",
                        "Thành công", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    
                    // Tải lại dữ liệu
                    loadRepairServices();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this,
                        "Không thể hủy dịch vụ. Vui lòng thử lại sau!",
                        "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
            // Xử lý các trạng thái còn lại
            else {
                // Cập nhật trạng thái thông thường
                com.pcstore.model.Repair updatedRepair = new com.pcstore.model.Repair(
                    repair.getRepairServiceId(),
                    repair.getCustomer(),
                    repair.getDeviceName(),
                    repair.getProblem(),
                    repair.getDiagnosis(),
                    repair.getServiceFee(),
                    RepairEnum.fromString(newStatusEn),
                    repair.getNotes(),
                    repair.getCreatedAt(),
                    java.time.LocalDateTime.now()
                );
                
                // Gọi controller để cập nhật trạng thái
                com.pcstore.model.Repair result = repairController.updateRepairService(
                    updatedRepair.getRepairServiceId(),
                    updatedRepair.getDeviceName(),
                    updatedRepair.getProblem(),
                    updatedRepair.getDiagnosis(),
                    updatedRepair.getServiceFee(),
                    updatedRepair.getStatus(),
                    updatedRepair.getNotes()
                );
                
                if (result != null) {
                    // Cập nhật lại bảng
                    tableRepair.setValueAt(newStatusVi, selectedRow, 5);
                    
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "Cập nhật trạng thái thành công!", 
                        "Thành công", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        
                    // Tải lại dữ liệu để đảm bảo hiển thị đúng
                    loadRepairServices();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "Không thể cập nhật trạng thái. Vui lòng thử lại sau!", 
                        "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Lỗi khi cập nhật trạng thái: " + e.getMessage(), 
                "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lấy trạng thái tiếng Việt từ trạng thái tiếng Anh
     */
    private String getCurrentStatusVi(String statusEn) {
        // Nếu trạng thái đã là tiếng Việt, trả về nguyên trạng
        for (String viStatus : statusTranslation.values()) {
            if (viStatus.equals(statusEn)) {
                return statusEn;
            }
        }
        
        // Nếu là trạng thái tiếng Anh, chuyển đổi sang tiếng Việt
        return statusTranslation.getOrDefault(statusEn, "Không xác định");
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.k33ptoo.components.KButton btnAddRepair;
    private com.k33ptoo.components.KButton btnDetail;
    private com.k33ptoo.components.KButton btnRemoveRepair;
    private com.k33ptoo.components.KButton btnUpdateStatus;
    private javax.swing.JScrollPane jScrollPaneTable;
    private javax.swing.JPanel panelBody;
    private com.k33ptoo.components.KGradientPanel pnMainRepair;
    private javax.swing.JPanel pnRepairFunctions;
    private javax.swing.JTable tableRepair;
    private com.pcstore.utils.TextFieldSearch textFieldSearch;
    // End of variables declaration//GEN-END:variables
}
