package com.pcstore.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.k33ptoo.components.KButton;
import com.k33ptoo.components.KGradientPanel;
import com.pcstore.controller.StockInHistoryController;

/**
 * Form hiển thị lịch sử nhập kho
 */
public class StockInHistoryForm extends JDialog {
    private StockInHistoryController controller;

    // Variables declaration - do not modify
    private JTable tablePurchaseOrders;
    private JTable tablePurchaseOrderDetails;
    private KButton btnClose;
    private KButton btnRefresh; 
    private KButton btnUpdateStatus; // Nút cập nhật trạng thái
    
    private JScrollPane jScrollPaneOrders;
    private JScrollPane jScrollPaneDetails;
    private KGradientPanel panelMain;
    private JLabel lblTitle;
    private JLabel lblOrderDetail;
    // End of variables declaration
    
    /**
     * Creates new form StockInHistory
     */
    public StockInHistoryForm(JFrame parent, boolean modal) {
        super(parent, modal);
        setTitle("Lịch Sử Nhập Hàng");
        initComponents();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Khởi tạo controller
        controller = new StockInHistoryController(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {
        panelMain = new KGradientPanel();
        lblTitle = new JLabel("LỊCH SỬ NHẬP HÀNG");
        jScrollPaneOrders = new JScrollPane();
        tablePurchaseOrders = new JTable();
        jScrollPaneDetails = new JScrollPane();
        tablePurchaseOrderDetails = new JTable();
        btnClose = new KButton();
        btnRefresh = new KButton();
        btnUpdateStatus = new KButton();
        lblOrderDetail = new JLabel("Chi tiết phiếu nhập");
        setMinimumSize(new java.awt.Dimension(1000, 700));
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());
        panelMain.setkFillBackground(false);
        panelMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelMain.setLayout(new BorderLayout(0, 10)); // Khoảng cách 10px giữa các thành phần
        JPanel panelTop = new JPanel(new BorderLayout(0, 10));
        panelTop.setOpaque(false);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(new Color(0, 102, 204));
        panelTop.add(lblTitle, BorderLayout.NORTH);
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelButtons.setOpaque(false);
        btnRefresh.setText("Làm Mới");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setkBorderRadius(30);
        btnRefresh.setkEndColor(new Color(0, 153, 255));
        btnRefresh.setkHoverEndColor(new Color(51, 153, 255));
        btnRefresh.setkHoverForeGround(new Color(255, 255, 255));
        btnRefresh.setkHoverStartColor(new Color(102, 204, 255));
        btnRefresh.setPreferredSize(new Dimension(120, 40));
        btnRefresh.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                btnRefreshMouseClicked(evt);
            }
        });
        btnUpdateStatus.setText("Cập Nhật Trạng Thái");
        btnUpdateStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnUpdateStatus.setkBorderRadius(30);
        btnUpdateStatus.setkEndColor(new Color(51, 153, 102));
        btnUpdateStatus.setkHoverEndColor(new Color(51, 204, 102));
        btnUpdateStatus.setkHoverForeGround(new Color(255, 255, 255));
        btnUpdateStatus.setkHoverStartColor(new Color(102, 204, 102));
        btnUpdateStatus.setPreferredSize(new Dimension(180, 40));
        btnUpdateStatus.setEnabled(false);
        btnUpdateStatus.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                btnUpdateStatusMouseClicked(evt);
            }
        });
        panelButtons.add(btnUpdateStatus);
        panelButtons.add(btnRefresh);
        panelTop.add(panelButtons, BorderLayout.CENTER);
        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(new BorderLayout(0, 10));
        panelCenter.setOpaque(false);
        
        tablePurchaseOrders.setModel(new DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null}
            },
            new String [] {
                "STT", "Mã Phiếu", "Ngày Tạo", "Người Tạo", "Trạng Thái", "Tổng Tiền"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        tablePurchaseOrders.setRowHeight(25);
        tablePurchaseOrders.getTableHeader().setReorderingAllowed(false);
        jScrollPaneOrders.setViewportView(tablePurchaseOrders);
        jScrollPaneOrders.setPreferredSize(new Dimension(900, 220));    
        panelCenter.add(jScrollPaneOrders, BorderLayout.NORTH);
        JPanel panelDetails = new JPanel();
        panelDetails.setLayout(new BorderLayout(0, 5));
        panelDetails.setOpaque(false);
        lblOrderDetail.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblOrderDetail.setForeground(new Color(0, 102, 204));
        panelDetails.add(lblOrderDetail, BorderLayout.NORTH);

        tablePurchaseOrderDetails.setModel(new DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null}
            },
            new String [] {
                "STT", "Mã Sản Phẩm", "Tên Sản Phẩm", "Số Lượng", "Đơn Giá", "Thành Tiền"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        tablePurchaseOrderDetails.setRowHeight(25);
        tablePurchaseOrderDetails.getTableHeader().setReorderingAllowed(false);
        jScrollPaneDetails.setViewportView(tablePurchaseOrderDetails);
        jScrollPaneDetails.setPreferredSize(new Dimension(900, 220));
        
        panelDetails.add(jScrollPaneDetails, BorderLayout.CENTER);
        
        panelCenter.add(panelDetails, BorderLayout.CENTER);

        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBottom.setOpaque(false);

        btnClose.setText("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setkBorderRadius(30);
        btnClose.setkEndColor(new Color(102, 153, 255));
        btnClose.setkHoverEndColor(new Color(102, 153, 255));
        btnClose.setkHoverForeGround(new Color(255, 255, 255));
        btnClose.setkHoverStartColor(new Color(153, 255, 153));
        btnClose.setPreferredSize(new Dimension(120, 40));
        btnClose.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                btnCloseMouseClicked(evt);
            }
        });
        
        panelBottom.add(btnClose);
        

        panelMain.add(panelTop, BorderLayout.NORTH);
        panelMain.add(panelCenter, BorderLayout.CENTER);
        panelMain.add(panelBottom, BorderLayout.SOUTH);
        

        getContentPane().add(panelMain, BorderLayout.CENTER);

        pack();
    }

    private void btnCloseMouseClicked(MouseEvent evt) {
        dispose();
    }
    
    private void btnRefreshMouseClicked(MouseEvent evt) {

        if (controller != null) {
            System.out.println("Refreshing purchase order history...");
            controller.loadPurchaseOrderHistory();
            enableUpdateStatusButton(false);
        }
    }
    
    private void btnUpdateStatusMouseClicked(MouseEvent evt) {

        if (!btnUpdateStatus.isEnabled()) {
            return;
        }
        
        int selectedRow = tablePurchaseOrders.getSelectedRow();
        if (selectedRow >= 0) {
            String purchaseOrderId = tablePurchaseOrders.getValueAt(selectedRow, 1).toString(); // Cột Mã Phiếu
            String currentStatus = tablePurchaseOrders.getValueAt(selectedRow, 4).toString();   // Cột Trạng Thái
            
            if (controller != null) {
                controller.showUpdateStatusDialog(purchaseOrderId, currentStatus);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một phiếu nhập để cập nhật trạng thái.", 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

 
    public JTable getTablePurchaseOrders() {
        return tablePurchaseOrders;
    }


    public JTable getTablePurchaseOrderDetails() {
        return tablePurchaseOrderDetails;
    }


    public KButton getBtnClose() {
        return btnClose;
    }
    

    public KButton getBtnRefresh() {
        return btnRefresh;
    }
    

    public KButton getBtnUpdateStatus() {
        return btnUpdateStatus;
    }
    
 
    public void enableUpdateStatusButton(boolean enable) {
        btnUpdateStatus.setEnabled(enable);
    }

    
}