/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.w3c.dom.events.MouseEvent;

import com.k33ptoo.components.KButton;
import com.pcstore.controller.WareHouseController;
import com.pcstore.utils.DatabaseConnection;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.TableUtils;
import com.pcstore.utils.TextFieldSearch;

/**
 *
 * @author nloc2
 */
public class WareHouseForm extends javax.swing.JPanel {

    private WareHouseController controller;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable WareHouseTable;
    public com.k33ptoo.components.KButton btnCreatePurchaseOrder;
    public com.k33ptoo.components.KButton btnHistoryStockIn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelAction;
    private com.k33ptoo.components.KGradientPanel panelBody;
    private javax.swing.JPanel panelTable;
    private com.k33ptoo.components.KGradientPanel panelTitle;
    // End of variables declaration//GEN-END:variables
    private KButton btnRefresh;
    private JLabel redLabel;
    private JLabel orangeLabel;
    private JLabel normalLabel;
    /**
     * Creates new form PurchaseOder
     */
    public WareHouseForm() {
        initComponents();
        initQuantityLegendPanel();
        initComponentsV2();
        // Khởi tạo controller
        controller = new WareHouseController(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelTitle = new com.k33ptoo.components.KGradientPanel();
        jLabel1 = new javax.swing.JLabel();
        panelAction = new javax.swing.JPanel();
        btnCreatePurchaseOrder = new com.k33ptoo.components.KButton();
        btnHistoryStockIn = new com.k33ptoo.components.KButton();
        panelBody = new com.k33ptoo.components.KGradientPanel();
        panelTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        WareHouseTable = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        panelTitle.setBackground(new java.awt.Color(255, 255, 255));
        panelTitle.setkFillBackground(false);
        panelTitle.setOpaque(false);
        panelTitle.setPreferredSize(new java.awt.Dimension(167, 35));
        panelTitle.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 29, 204));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
        jLabel1.setText(bundle.getString("txtMenuWareHouse")); // NOI18N
        panelTitle.add(jLabel1, java.awt.BorderLayout.CENTER);

        add(panelTitle);

        panelAction.setMinimumSize(new java.awt.Dimension(459, 77));
        panelAction.setOpaque(false);
        panelAction.setPreferredSize(new java.awt.Dimension(459, 77));
        panelAction.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnCreatePurchaseOrder.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        btnCreatePurchaseOrder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/plus.png"))); // NOI18N
        btnCreatePurchaseOrder.setText(bundle.getString("btnCreatePurchaseOrder")); // NOI18N
        btnCreatePurchaseOrder.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCreatePurchaseOrder.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCreatePurchaseOrder.setIconTextGap(25);
        btnCreatePurchaseOrder.setkBorderRadius(40);
        btnCreatePurchaseOrder.setkEndColor(new java.awt.Color(0, 204, 51));
        btnCreatePurchaseOrder.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnCreatePurchaseOrder.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnCreatePurchaseOrder.setkStartColor(new java.awt.Color(0, 204, 51));
        btnCreatePurchaseOrder.setOpaque(true);
        btnCreatePurchaseOrder.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCreatePurchaseOrderMouseClicked(evt);
            }
        });
        panelAction.add(btnCreatePurchaseOrder);

        btnHistoryStockIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/history.png"))); // NOI18N
        btnHistoryStockIn.setText(bundle.getString("btnHistoryofStockIn")); // NOI18N
        btnHistoryStockIn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnHistoryStockIn.setIconTextGap(30);
        btnHistoryStockIn.setkBorderRadius(40);
        btnHistoryStockIn.setkEndColor(new java.awt.Color(0, 153, 255));
        btnHistoryStockIn.setkHoverEndColor(new java.awt.Color(255, 102, 102));
        btnHistoryStockIn.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnHistoryStockIn.setkHoverStartColor(new java.awt.Color(255, 102, 102));
        btnHistoryStockIn.setOpaque(true);
        btnHistoryStockIn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnHistoryStockInMouseClicked(evt);
            }
        });
        panelAction.add(btnHistoryStockIn);

        add(panelAction);

        panelBody.setkFillBackground(false);
        panelBody.setPreferredSize(new java.awt.Dimension(1188, 592));
        panelBody.setLayout(new java.awt.BorderLayout());

        panelTable.setBackground(new java.awt.Color(255, 255, 255));
        panelTable.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("pnWareHouse"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 15), new java.awt.Color(51, 29, 204))); // NOI18N
        panelTable.setLayout(new java.awt.BorderLayout());

        WareHouseTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "STT", "Mã Máy", "Tên Máy", "Số Lượng"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        WareHouseTable.setRowHeight(32);
        jScrollPane1.setViewportView(WareHouseTable);
        if (WareHouseTable.getColumnModel().getColumnCount() > 0) {
            WareHouseTable.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("clProductID")); // NOI18N
            WareHouseTable.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("clProductName")); // NOI18N
            WareHouseTable.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("clQuantity")); // NOI18N
        }

        panelTable.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panelBody.add(panelTable, java.awt.BorderLayout.CENTER);

        add(panelBody);
    }// </editor-fold>//GEN-END:initComponents

    private void initComponentsV2(){
        btnRefresh = new KButton();
        ResourceBundle bundle = LocaleManager.getInstance().getResourceBundle();
        // Thiết lập nút làm mới
        btnRefresh.setText(bundle.getString("btnRefresh"));
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setIcon(new ImageIcon(getClass().getResource("/com/pcstore/resources/icon/refresh.png")));
        btnRefresh.setkBorderRadius(30);
        btnRefresh.setkEndColor(new Color(0, 153, 255));
        btnRefresh.setkHoverEndColor(new Color(51, 153, 255));
        btnRefresh.setkHoverForeGround(new Color(255, 255, 255));
        btnRefresh.setkHoverStartColor(new Color(102, 204, 255));
        btnRefresh.setPreferredSize(new Dimension(120, 40));
        panelAction.add(btnRefresh);
    }

    /**
     * Tạo panel chú thích cho các màu hiển thị số lượng
     * 
     * @return Panel chứa các chú thích màu
     */
    private void initQuantityLegendPanel() {
        ResourceBundle bundle = LocaleManager.getInstance().getResourceBundle();
        
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        legendPanel.setOpaque(false);

        // Thêm chú thích cho màu đỏ (Hết hàng)
        JPanel redPanel = new JPanel();
        redPanel.setPreferredSize(new Dimension(15, 15));
        redPanel.setBackground(TableUtils.ZERO_QUANTITY_COLOR);
        redLabel = new JLabel(bundle.getString("txtOutOfStock"));
        redLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JPanel redContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        redContainer.setOpaque(false);
        redContainer.add(redPanel);
        redContainer.add(redLabel);

        // Thêm chú thích cho màu cam (Sắp hết hàng)
        JPanel orangePanel = new JPanel();
        orangePanel.setPreferredSize(new Dimension(15, 15));
        orangePanel.setBackground(TableUtils.LOW_QUANTITY_COLOR);
        orangeLabel = new JLabel(bundle.getString("txtLowStock"));
        orangeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JPanel orangeContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        orangeContainer.setOpaque(false);
        orangeContainer.add(orangePanel);
        orangeContainer.add(orangeLabel);

        // Thêm chú thích cho màu mặc định (Còn hàng)
        JPanel normalPanel = new JPanel();
        normalPanel.setPreferredSize(new Dimension(15, 15));
        normalPanel.setBackground(Color.WHITE);
        normalPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        normalLabel = new JLabel(bundle.getString("txtInStock"));
        normalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JPanel normalContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        normalContainer.setOpaque(false);
        normalContainer.add(normalPanel);
        normalContainer.add(normalLabel);

        // Thêm tất cả vào panel chính
        legendPanel.add(redContainer);
        legendPanel.add(orangeContainer);
        legendPanel.add(normalContainer);

        panelAction.add(legendPanel);
    }

    private void btnCreatePurchaseOrderMouseClicked(java.awt.event.MouseEvent evt) {
        try {
            // Lấy instance của DashboardForm
            DashboardForm dashboardForm = DashboardForm.getInstance();
            if (dashboardForm == null) {
                JOptionPane.showMessageDialog(this, "DashboardForm chưa được khởi tạo!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Lấy kết nối cơ sở dữ liệu
            Connection connection = DatabaseConnection.getInstance().getConnection();
            if (connection == null || connection.isClosed()) {
                JOptionPane.showMessageDialog(this, "Không thể kết nối cơ sở dữ liệu!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Khởi tạo PurchaseOrderForm
            PurchaseOrderForm purchaseOrderForm = new PurchaseOrderForm(dashboardForm, true, connection);
            purchaseOrderForm.setLocationRelativeTo(this);
            purchaseOrderForm.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi mở form: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnHistoryStockInMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btnHistoryStockInMouseClicked
        DashboardForm dashboardForm = DashboardForm.getInstance();
        StockInHistoryForm historyStockInForm = new StockInHistoryForm(dashboardForm, true);
        historyStockInForm.setLocationRelativeTo(historyStockInForm);
        historyStockInForm.setVisible(true);
    }// GEN-LAST:event_btnHistoryStockInMouseClicked

    /**
     * Getter cho bảng sản phẩm
     */
    public JTable getTable() {
        return WareHouseTable;
    }

    /**
     * Getter cho nút tạo đơn đặt hàng
     */
    public JButton getBtnCreatePurchaseOrder() {
        return btnCreatePurchaseOrder;
    }

    public TextFieldSearch getTextFieldSearch() {
        return null;
    }

    /**
     * Getter cho nút xem lịch sử nhập kho
     */
    public JButton getBtnHistoryStockIn() {
        return btnHistoryStockIn;
    }

    public JLabel getRedLabel() {
        return redLabel;
    }

    public JLabel getOrangeLabel() {
        return orangeLabel;
    }
    

    public JLabel getNormalLabel() {
        return normalLabel;
    }

    public KButton getBtnRefresh() {
        return btnRefresh;
    }

    
}
