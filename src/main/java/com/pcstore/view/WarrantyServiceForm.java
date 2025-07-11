/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import com.pcstore.controller.WarrantyController;
import com.pcstore.utils.DatabaseConnection;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.TableUtils;
import com.pcstore.repository.*;
import com.pcstore.service.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.pcstore.model.Warranty;
import com.pcstore.repository.impl.CustomerRepository;
import com.pcstore.repository.impl.InvoiceDetailRepository;
import com.pcstore.repository.impl.ProductRepository;
import com.pcstore.repository.impl.WarrantyRepository;

/**
 *
 * @author 
 */
public class WarrantyServiceForm extends javax.swing.JPanel {

    private WarrantyController controller;

      // Variables declaration - do not modify//GEN-BEGIN:variables
      private com.k33ptoo.components.KButton btnDetailWarrantyCard;
      private com.k33ptoo.components.KButton btnRemoveRepair;
      private com.k33ptoo.components.KButton btnWarrantyInformationLookup;
      private com.k33ptoo.components.KButton btnWarrantyRegistration;
      private javax.swing.JScrollPane jScrollPaneTable;
      private javax.swing.JPanel panelBody;
      private javax.swing.JPanel panelSearch;
      private javax.swing.JPanel pnFunctions;
      private javax.swing.JPanel pnWarrantyMain;
      private javax.swing.JTable tableListWarranty;
      private javax.swing.JTextField txtSearch;
      // End of variables declaration//GEN-END:variables
    
    /**
     * Creates new form Service
     */
    public WarrantyServiceForm() {
        try {
            initComponents();
            initController();
            TableUtils.applyDefaultStyle(tableListWarranty);

            // Thêm DocumentListener cho tìm kiếm realtime
            txtSearch.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (controller != null)
                        controller.searchWarranties(txtSearch.getText().trim());
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (controller != null)
                        controller.searchWarranties(txtSearch.getText().trim());
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (controller != null)
                        controller.searchWarranties(txtSearch.getText().trim());
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                ErrorMessage.WARRANTY_FORM_INIT_ERROR + ": " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Khởi tạo controller và tải dữ liệu ban đầu
     */
    private void initController() {
        try {
            // Khởi tạo controller với các service cần thiết
            controller = new WarrantyController(
                ServiceFactory.getWarrantyService(),
                ServiceFactory.getInvoiceService(),
                ServiceFactory.getInvoiceDetailService(),
                ServiceFactory.getProductService(),
                ServiceFactory.getCustomerService()
            );
            
            // Thiết lập form và tải dữ liệu
            controller.setServiceForm(this);
            controller.loadWarranties(); // Tải dữ liệu khi khởi tạo
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Lỗi khởi tạo controller: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnWarrantyMain = new javax.swing.JPanel();
        pnFunctions = new javax.swing.JPanel();
        btnWarrantyRegistration = new com.k33ptoo.components.KButton();
        btnRemoveRepair = new com.k33ptoo.components.KButton();
        btnDetailWarrantyCard = new com.k33ptoo.components.KButton();
        panelSearch = new javax.swing.JPanel();
        btnWarrantyInformationLookup = new com.k33ptoo.components.KButton();
        txtSearch = new javax.swing.JTextField();
        panelBody = new javax.swing.JPanel();
        jScrollPaneTable = new javax.swing.JScrollPane();
        tableListWarranty = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1153, 713));
        setMinimumSize(new java.awt.Dimension(1153, 713));
        setPreferredSize(new java.awt.Dimension(1153, 713));
        setLayout(new java.awt.BorderLayout());

        pnWarrantyMain.setBackground(new java.awt.Color(255, 255, 255));
        pnWarrantyMain.setMaximumSize(new java.awt.Dimension(1360, 600));
        pnWarrantyMain.setMinimumSize(new java.awt.Dimension(1360, 600));
        pnWarrantyMain.setPreferredSize(new java.awt.Dimension(1360, 600));
        pnWarrantyMain.setLayout(new javax.swing.BoxLayout(pnWarrantyMain, javax.swing.BoxLayout.Y_AXIS));

        pnFunctions.setBackground(new java.awt.Color(255, 255, 255));
        pnFunctions.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 10));

        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
        btnWarrantyRegistration.setText(bundle.getString("btnReturnProduct")); // NOI18N
        btnWarrantyRegistration.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnWarrantyRegistration.setkAllowGradient(false);
        btnWarrantyRegistration.setkBackGroundColor(new java.awt.Color(0, 190, 94));
        btnWarrantyRegistration.setkBorderRadius(30);
        btnWarrantyRegistration.setkEndColor(new java.awt.Color(0, 255, 51));
        btnWarrantyRegistration.setkFocusColor(new java.awt.Color(255, 255, 255));
        btnWarrantyRegistration.setkHoverColor(new java.awt.Color(0, 190, 94));
        btnWarrantyRegistration.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnWarrantyRegistration.setkHoverForeGround(new java.awt.Color(50, 100, 255));
        btnWarrantyRegistration.setkStartColor(new java.awt.Color(0, 204, 255));
        btnWarrantyRegistration.setPreferredSize(new java.awt.Dimension(150, 35));
        btnWarrantyRegistration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWarrantyRegistrationActionPerformed(evt);
            }
        });
        pnFunctions.add(btnWarrantyRegistration);

        btnRemoveRepair.setText(bundle.getString("btnRemoveRepair")); // NOI18N
        btnRemoveRepair.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnRemoveRepair.setkAllowGradient(false);
        btnRemoveRepair.setkBackGroundColor(new java.awt.Color(255, 0, 51));
        btnRemoveRepair.setkBorderRadius(30);
        btnRemoveRepair.setkEndColor(new java.awt.Color(255, 102, 51));
        btnRemoveRepair.setkHoverColor(new java.awt.Color(255, 39, 51));
        btnRemoveRepair.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnRemoveRepair.setkHoverForeGround(new java.awt.Color(50, 100, 255));
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
        pnFunctions.add(btnRemoveRepair);

        btnDetailWarrantyCard.setText(bundle.getString("btnDetailReturnCard")); // NOI18N
        btnDetailWarrantyCard.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDetailWarrantyCard.setkAllowGradient(false);
        btnDetailWarrantyCard.setkBackGroundColor(new java.awt.Color(102, 153, 255));
        btnDetailWarrantyCard.setkEndColor(new java.awt.Color(102, 153, 255));
        btnDetailWarrantyCard.setkHoverColor(new java.awt.Color(102, 185, 241));
        btnDetailWarrantyCard.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnDetailWarrantyCard.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnDetailWarrantyCard.setkIndicatorThickness(255);
        btnDetailWarrantyCard.setPreferredSize(new java.awt.Dimension(150, 35));
        btnDetailWarrantyCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetailWarrantyCardActionPerformed(evt);
            }
        });
        pnFunctions.add(btnDetailWarrantyCard);

        panelSearch.setBackground(new java.awt.Color(255, 255, 255));
        panelSearch.setBorder(javax.swing.BorderFactory.createTitledBorder("Tìm Kiếm"));
        panelSearch.setPreferredSize(new java.awt.Dimension(425, 65));
        panelSearch.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnWarrantyInformationLookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/search.png"))); // NOI18N
        btnWarrantyInformationLookup.setDisabledSelectedIcon(null);
        btnWarrantyInformationLookup.setEnabled(false);
        btnWarrantyInformationLookup.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnWarrantyInformationLookup.setIconTextGap(25);
        btnWarrantyInformationLookup.setkBackGroundColor(new java.awt.Color(102, 153, 255));
        btnWarrantyInformationLookup.setkBorderRadius(30);
        btnWarrantyInformationLookup.setkEndColor(new java.awt.Color(153, 153, 153));
        btnWarrantyInformationLookup.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnWarrantyInformationLookup.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnWarrantyInformationLookup.setkStartColor(new java.awt.Color(204, 204, 204));
        btnWarrantyInformationLookup.setMargin(new java.awt.Insets(2, 14, 0, 14));
        btnWarrantyInformationLookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWarrantyInformationLookupActionPerformed(evt);
            }
        });
        panelSearch.add(btnWarrantyInformationLookup, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 20, 70, 32));

        txtSearch.setMargin(new java.awt.Insets(2, 6, 2, 0));
        panelSearch.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 310, 30));

        pnFunctions.add(panelSearch);

        pnWarrantyMain.add(pnFunctions);

        panelBody.setLayout(new java.awt.BorderLayout());

        jScrollPaneTable.setPreferredSize(new java.awt.Dimension(452, 500));

        tableListWarranty.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã Bảo Hành ", "Ngày Yêu Cầu", "Mã Khách Hàng", "Tên Khách Hàng", "Số Điện Thoại", "Tên sản Phẩm ", "Mã Sản Phẩm", "Trạng Thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPaneTable.setViewportView(tableListWarranty);
        if (tableListWarranty.getColumnModel().getColumnCount() > 0) {
            tableListWarranty.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("txtWarrantyWarrantyID")); // NOI18N
            tableListWarranty.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("txtWarrantyDateRequested")); // NOI18N
            tableListWarranty.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("txtWarrantyCustomerID")); // NOI18N
            tableListWarranty.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("txtWarrantyNameCustomer")); // NOI18N
            tableListWarranty.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("txtWarrantyPhoneNumber")); // NOI18N
            tableListWarranty.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("txtWarrantyNameProduct")); // NOI18N
            tableListWarranty.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("txtWarrantyPtoductID")); // NOI18N
            tableListWarranty.getColumnModel().getColumn(7).setHeaderValue(bundle.getString("txtWarrantyStatus")); // NOI18N
        }

        panelBody.add(jScrollPaneTable, java.awt.BorderLayout.CENTER);

        pnWarrantyMain.add(panelBody);

        add(pnWarrantyMain, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnWarrantyRegistrationActionPerformed(java.awt.event.ActionEvent evt) {
        if (controller != null) {
            controller.handleWarrantyRegistration();
        }
    }

    private void btnRemoveRepairActionPerformed(java.awt.event.ActionEvent evt) {
        if (controller != null) {
            controller.handleRemoveRepair();
        }
    }

    private void btnRemoveRepairMouseClicked(java.awt.event.MouseEvent evt) {
        btnRemoveRepairActionPerformed(null);
    }

    private void btnDetailWarrantyCardActionPerformed(java.awt.event.ActionEvent evt) {
        if (controller != null) {
            controller.handleDetailWarrantyCard();
        }
    }

    private void btnWarrantyInformationLookupActionPerformed(java.awt.event.ActionEvent evt) {
        if (controller != null) {
            controller.handleWarrantyInformationLookup();
        }
    }
   
    
 
    /**
     * Cập nhật dữ liệu bảng bảo hành từ danh sách
     * @param warranties Danh sách bảo hành để hiển thị
     */
    public void updateWarrantyTable(List<Warranty> warranties) {
        // System.out.println("Updating warranty table with " + (warranties != null ? warranties.size() : 0) + " items");
        
        DefaultTableModel model = (DefaultTableModel) tableListWarranty.getModel();
        model.setRowCount(0); // Xóa tất cả các hàng hiện có
        
        if (warranties == null || warranties.isEmpty()) {
          
            return;
        }
        
        // Định dạng ngày tháng
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        // Thêm dữ liệu từ danh sách bảo hành
        for (Warranty warranty : warranties) {
            try {
                
                String startDateStr = warranty.getStartDate() != null ? 
                    warranty.getStartDate().format(formatter) : "";
                    
                model.addRow(new Object[]{
                    warranty.getWarrantyId(),
                    startDateStr,
                    warranty.getCustomerId(),
                    warranty.getCustomerName(),
                    warranty.getCustomerPhone(),
                    warranty.getProductName(),
                    warranty.getProductId(),
                    warranty.isUsed() ? "Đã sử dụng" : "Chưa sử dụng"
                });
            } catch (Exception e) {
                System.err.println("Error adding warranty to table: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // System.out.println("Table updated with " + model.getRowCount() + " rows");
    }

    /**
     * Xóa tất cả dữ liệu trong bảng
     */
    public void clearWarrantyTable() {
        DefaultTableModel model = (DefaultTableModel) tableListWarranty.getModel();
        model.setRowCount(0);
    }

    /**
     * Trả về bảng hiển thị danh sách bảo hành
     * @return Bảng bảo hành
     */
    public JTable getWarrantyTable() {
        return tableListWarranty;
    }

    /**
     * Trả về trường nhập từ khóa tìm kiếm
     * @return Trường tìm kiếm
     */
    public JTextField getSearchField() {
        return txtSearch;
    }


  
}
