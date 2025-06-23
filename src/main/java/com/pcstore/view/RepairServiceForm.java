/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.pcstore.controller.RepairController;
import com.pcstore.model.enums.RepairEnum;
import com.pcstore.utils.TableUtils;
import com.pcstore.view.AddReapairProductForm;
import com.pcstore.utils.ErrorMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 *
 * @author DUC ANH
 */
public class RepairServiceForm extends javax.swing.JPanel {

    private RepairController repairController;
    private final java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle();

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

    private static final Map<String, String> statusRepairTranslation = new HashMap<>();
    static{
            java.util.ResourceBundle bundle2 = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle();

            statusRepairTranslation.put("Received", bundle2.getString("repairService.status.received"));
            statusRepairTranslation.put("Diagnosing", bundle2.getString("repairService.status.diagnosing"));
            statusRepairTranslation.put("Waiting for Parts", bundle2.getString("repairService.status.waitingForParts"));
            statusRepairTranslation.put("Repairing", bundle2.getString("repairService.status.repairing"));
            statusRepairTranslation.put("Completed", bundle2.getString("repairService.status.completed"));
            statusRepairTranslation.put("Delivered", bundle2.getString("repairService.status.delivered"));
            statusRepairTranslation.put("Cancelled", bundle2.getString("repairService.status.cancelled"));
        
    }

    /**
     * Creates new form RepairService
     */
    public RepairServiceForm() {
        initComponents();
        repairController = new RepairController();
        try {
            loadRepairServices();

            TableUtils.applyDefaultStyle(tableRepair);
        
            setupSearchFunctionality();            

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, 
                ErrorMessage.REPAIR_FORM_INIT_ERROR.get() + ": " + e.getMessage(), 
                bundle.getString("repairService.initError"), 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }

        TableUtils.applyDefaultStyle(tableRepair);
      

        // statusTranslation = new HashMap<>();
        // statusTranslation.put("Received", "Đã tiếp nhận");
        // statusTranslation.put("Diagnosing", "Đang chẩn đoán");
        // statusTranslation.put("Waiting for Parts", "Chờ linh kiện");
        // statusTranslation.put("Repairing", "Đang sửa chữa");
        // statusTranslation.put("Completed", "Đã hoàn thành");
        // statusTranslation.put("Delivered", "Đã giao khách");
        // statusTranslation.put("Cancelled", "Đã hủy");
    
//        setupSearchFunctionality();
    }

    /**
     * Tải danh sách dịch vụ sửa chữa
     */
    public void loadRepairServices() {
        try {
            // System.out.println("Đang tải danh sách dịch vụ sửa chữa...");
            System.out.println(bundle.getString("repairService.loadingData"));
            
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
                System.out.println(bundle.getString("repairService.warningControllerNotSet"));
                return;
            }
            
            // Lấy danh sách dịch vụ từ controller - đảm bảo lấy dữ liệu mới nhất
            List<com.pcstore.model.Repair> repairs = repairController.getAllRepairServices();
            System.out.println(bundle.getString("repairService.foundServices").replace("%d", String.valueOf(repairs.size())));
            
            // Thêm dữ liệu vào bảng
            if (repairs.isEmpty()) {
                model.addRow(new Object[]{"", bundle.getString("repairService.noData"), "", "", "", "", "", ""});
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
                String statusVi = statusRepairTranslation.getOrDefault(statusEn, statusEn);
                
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
            System.out.println(bundle.getString("repairService.updateSuccess"));
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(bundle.getString("repairService.loadError").replace("%s", e.getMessage()));
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

        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
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

        textFieldSearch.setPreferredSize(new java.awt.Dimension(450, 31));
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
                bundle.getString("repairService.table.repairCode"),
                bundle.getString("repairService.table.customerName"), 
                bundle.getString("repairService.table.phoneNumber"),
                bundle.getString("repairService.table.deviceName"), 
                bundle.getString("repairService.table.repairProblem"),
                bundle.getString("repairService.table.cost"), 
                bundle.getString("repairService.table.status"),
                bundle.getString("repairService.table.notes")
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
        if (tableRepair.getColumnModel().getColumnCount() > 0) {
            tableRepair.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("txtRepairID"));
            tableRepair.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("txtRepairNameCustomer"));
            tableRepair.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("txtRepairPhoneNumber"));
            tableRepair.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("txtRepairNameDevice"));
            tableRepair.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("txtRepairProblermRepair"));
            tableRepair.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("txtRepairCost"));
            tableRepair.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("txtRepairStatus"));
            tableRepair.getColumnModel().getColumn(7).setHeaderValue(bundle.getString("txtRepairNote"));
        }

        panelBody.add(jScrollPaneTable, java.awt.BorderLayout.CENTER);

        pnMainRepair.add(panelBody);

        add(pnMainRepair, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    private void btnAddRepairMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddRepairMouseClicked
        // Call the action performed method to avoid duplicate code
        btnAddRepairActionPerformed(null);
    }//GEN-LAST:event_btnAddRepairMouseClicked

    private void btnAddRepairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRepairActionPerformed
        if (repairController != null) {
            repairController.handleAddRepair(this);
        }
    }//GEN-LAST:event_btnAddRepairActionPerformed

    private void btnDetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDetailMouseClicked
        // Gọi phương thức xử lý sự kiện action để tránh lặp code
        btnDetailActionPerformed(null);
    }//GEN-LAST:event_btnDetailMouseClicked

    private void btnDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetailActionPerformed
        if (repairController != null) {
            repairController.handleDetailRepair(this);
        }
    }//GEN-LAST:event_btnDetailActionPerformed

    private void btnRemoveRepairMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRemoveRepairMouseClicked
        // Gọi phương thức xử lý sự kiện action để tránh lặp code
        btnRemoveRepairActionPerformed(null);
    }//GEN-LAST:event_btnRemoveRepairMouseClicked

    private void btnRemoveRepairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveRepairActionPerformed
        if (repairController != null) {
            repairController.handleRemoveRepair(this);
        }
    }//GEN-LAST:event_btnRemoveRepairActionPerformed

    private void btnUpdateStatusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUpdateStatusMouseClicked
        if (repairController != null) {
            repairController.handleUpdateRepairStatus(this);
        }
    }//GEN-LAST:event_btnUpdateStatusMouseClicked

    /**
     * Thiết lập chức năng tìm kiếm cho bảng
     */
    private void setupSearchFunctionality() {
             
        // Tạo sorter cho bảng sử dụng TableStyleUtil
        TableRowSorter<TableModel> sorter = TableUtils.setupSorting(tableRepair);
        
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
        TableUtils.applyFilter(sorter, searchText, 1, 2, 3, 4, 6, 7);
        
    }

    /**
     * Cập nhật trạng thái dịch vụ sửa chữa
     */
    private void updateRepairStatus() {
        // Lấy dòng đang được chọn
        int selectedRow = tableRepair.getSelectedRow();
        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                ErrorMessage.REPAIR_SELECT_ONE_UPDATE_STATUS.get(),
                bundle.getString("repairService.notification"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Lấy thông tin từ dòng được chọn
        Object repairIdObj = tableRepair.getValueAt(selectedRow, 0);
        String deviceName = tableRepair.getValueAt(selectedRow, 2).toString();
        String currentStatus = tableRepair.getValueAt(selectedRow, 5).toString();
        
        if (repairIdObj == null || repairIdObj.toString().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                ErrorMessage.REPAIR_ID_INVALID.get(),
                bundle.getString("repairService.error"),
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Integer repairId;
        try {
            repairId = Integer.parseInt(repairIdObj.toString());
        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this,
                bundle.getString("repairService.invalidID").replace("%s", repairIdObj.toString()),
                bundle.getString("repairService.error"),
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Lấy thông tin dịch vụ sửa chữa hiện tại
            java.util.Optional<com.pcstore.model.Repair> repairOpt = repairController.getRepairServiceById(repairId);
            if (repairOpt.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    String.format(ErrorMessage.REPAIR_NOT_FOUND_WITH_ID.get(), repairId), 
                    bundle.getString("repairService.notification"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            com.pcstore.model.Repair repair = repairOpt.get();
            RepairEnum currentStatusEnum = repair.getStatus();
            
            // Tạo danh sách các trạng thái có thể chuyển đổi dựa trên trạng thái hiện tại
            List<String> availableStatusesEn = getValidNextStatuses(currentStatusEnum);
            String[] availableStatusesVi = new String[availableStatusesEn.size()];
            
            for (int i = 0; i < availableStatusesEn.size(); i++) {
                availableStatusesVi[i] = statusRepairTranslation.getOrDefault(availableStatusesEn.get(i), availableStatusesEn.get(i));
            }
            
            // Hiển thị dialog để chọn trạng thái mới
            String newStatusVi = (String) JOptionPane.showInputDialog(
                this,
                bundle.getString("repairService.selectNewStatus")
                    .replace("%d", repairId.toString())
                    .replace("%s", deviceName),
                bundle.getString("repairService.updateStatusTitle"),
                JOptionPane.QUESTION_MESSAGE,
                null,
                availableStatusesVi,
                getCurrentStatusVi(currentStatusEnum.getStatus())
            );
            
            // Nếu người dùng không chọn hoặc hủy
            if (newStatusVi == null) {
                return;
            }
            
            // Chuyển đổi từ tiếng Việt sang tiếng Anh để lưu vào DB
            String newStatusEn = null;
            for (Map.Entry<String, String> entry : statusRepairTranslation.entrySet()) {
                if (entry.getValue().equals(newStatusVi)) {
                    newStatusEn = entry.getKey();
                    break;
                }
            }
            
            if (newStatusEn == null) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    ErrorMessage.REPAIR_STATUS_CONVERT_ERROR.get(), 
                    bundle.getString("repairService.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Xử lý logic đặc biệt khi chuyển sang trạng thái "Completed"
            if ("Completed".equals(newStatusEn)) {
                // Dialog nhập phí dịch vụ cuối cùng
                String feeInput = JOptionPane.showInputDialog(this,
                    bundle.getString("repairService.enterFinalFee"),
                    repair.getServiceFee() != null ? repair.getServiceFee().toString() : "0"
                );
                
                if (feeInput == null) {
                    return; // Người dùng đã hủy
                }
                
                try {
                    // Cập nhật phí dịch vụ
                    java.math.BigDecimal fee = new java.math.BigDecimal(feeInput);
                    
                    // Yêu cầu nhập ghi chú hoàn thành
                    String notes = JOptionPane.showInputDialog(this,
                        bundle.getString("repairService.enterCompletionNotes"),
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
                            bundle.getString("repairService.completedSuccess"),
                            bundle.getString("repairService.success"), JOptionPane.INFORMATION_MESSAGE);
                        
                        // Tải lại dữ liệu
                        loadRepairServices();
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(this,
                            bundle.getString("repairService.updateFailed"),
                            bundle.getString("repairService.error"), JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (NumberFormatException e) {
                    javax.swing.JOptionPane.showMessageDialog(this,
                        bundle.getString("repairService.invalidFee"),
                        bundle.getString("repairService.error"), JOptionPane.ERROR_MESSAGE);
                }
            } 
            // Xử lý khi chuyển sang trạng thái "Cancelled"
            else if ("Cancelled".equals(newStatusEn)) {
                // Xác nhận hủy dịch vụ
                int confirm = JOptionPane.showConfirmDialog(this,
                    bundle.getString("repairService.confirmCancel"),
                    bundle.getString("repairService.confirmCancelTitle"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
                
                // Yêu cầu nhập lý do hủy
                String cancelReason = JOptionPane.showInputDialog(this,
                    bundle.getString("repairService.enterCancelReason"), 
                    "");
                    
                if (cancelReason == null) {
                    cancelReason = bundle.getString("repairService.defaultCancelReason");
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
                        bundle.getString("repairService.cancelSuccess"),
                        bundle.getString("repairService.success"), JOptionPane.INFORMATION_MESSAGE);
                    
                    // Tải lại dữ liệu
                    loadRepairServices();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this,
                        bundle.getString("repairService.cancelFailed"),
                        bundle.getString("repairService.error"), JOptionPane.ERROR_MESSAGE);
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
                ErrorMessage.REPAIR_STATUS_UPDATE_ERROR.get() + ": " + e.getMessage(), 
                bundle.getString("repairService.error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lấy trạng thái tiếng Việt từ trạng thái tiếng Anh
     */
    private String getCurrentStatusVi(String statusEn) {
        // Nếu trạng thái đã là tiếng Việt, trả về nguyên trạng
        for (String viStatus : statusRepairTranslation.values()) {
            if (viStatus.equals(statusEn)) {
                return statusEn;
            }
        }
        
        // Nếu là trạng thái tiếng Anh, chuyển đổi sang tiếng Việt
        return statusRepairTranslation.getOrDefault(statusEn, bundle.getString("repairService.statusUnknown"));
    }
    
    /**
     * Lấy danh sách các trạng thái hợp lệ có thể chuyển từ trạng thái hiện tại
     */
    private List<String> getValidNextStatuses(RepairEnum currentStatus) {
        List<String> validStatuses = new ArrayList<>();
        
        switch (currentStatus) {
            case RECEIVED:
                validStatuses.add("Diagnosing");
                validStatuses.add("Cancelled");
                break;
            case DIAGNOSING:
                validStatuses.add("Waiting for Parts");
                validStatuses.add("Repairing");
                validStatuses.add("Completed");
                validStatuses.add("Cancelled");
                break;
            case WAITING_FOR_PARTS:
                validStatuses.add("Repairing");
                validStatuses.add("Cancelled");
                break;
            case REPAIRING:
                validStatuses.add("Completed");
                validStatuses.add("Cancelled");
                break;
            case COMPLETED:
                validStatuses.add("Delivered");
                validStatuses.add("Cancelled");
                break;
            case DELIVERED:
                // Trạng thái cuối, không thể chuyển đổi
                break;
            case CANCELLED:
                // Trạng thái cuối, không thể chuyển đổi
                break;
            default:
                // Thêm tất cả các trạng thái có thể
                validStatuses.add("Received");
                validStatuses.add("Diagnosing");
                validStatuses.add("Waiting for Parts");
                validStatuses.add("Repairing"); 
                validStatuses.add("Completed");
                validStatuses.add("Delivered");
                validStatuses.add("Cancelled");
        }
        
        return validStatuses;
    }

    public JTable getTableRepair() {
        return tableRepair;
    }
}
