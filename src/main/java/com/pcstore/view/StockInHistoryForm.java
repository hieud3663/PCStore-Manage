package com.pcstore.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
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
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.k33ptoo.components.KButton;
import com.k33ptoo.components.KGradientPanel;
import com.pcstore.controller.StockInHistoryController;

/**
 * Form hiển thị lịch sử nhập kho
 */
public class StockInHistoryForm extends JDialog {
    private StockInHistoryController controller;
    
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
        kGradientPanel1 = new KGradientPanel();
        lblTitle = new JLabel("LỊCH SỬ NHẬP HÀNG");
        jScrollPane1 = new JScrollPane();
        tablePurchaseOrders = new JTable();
        jScrollPane2 = new JScrollPane();
        tablePurchaseOrderDetails = new JTable();
        btnClose = new KButton();
        btnRefresh = new KButton(); // Nút làm mới
        btnUpdateStatus = new KButton(); // Nút cập nhật trạng thái
        lblOrderDetail = new JLabel("Chi tiết phiếu nhập");

        setMinimumSize(new java.awt.Dimension(1000, 700));
        setResizable(false);

        kGradientPanel1.setkFillBackground(false);
        kGradientPanel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Thiết lập tiêu đề
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(new Color(0, 102, 204));
        
        // Thiết lập bảng phiếu nhập
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
        jScrollPane1.setViewportView(tablePurchaseOrders);
        
        // Thiết lập nhãn chi tiết
        lblOrderDetail.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblOrderDetail.setForeground(new Color(0, 102, 204));
        
        // Thiết lập bảng chi tiết phiếu nhập
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
        jScrollPane2.setViewportView(tablePurchaseOrderDetails);

        // Thiết lập nút làm mới
        btnRefresh.setText("Làm Mới");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setkBorderRadius(30);
        btnRefresh.setkEndColor(new Color(0, 153, 255)); // Màu xanh nhạt
        btnRefresh.setkHoverEndColor(new Color(51, 153, 255));
        btnRefresh.setkHoverForeGround(new Color(255, 255, 255));
        btnRefresh.setkHoverStartColor(new Color(102, 204, 255));
        btnRefresh.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                btnRefreshMouseClicked(evt);
            }
        });
        
        // Thiết lập nút cập nhật trạng thái
        btnUpdateStatus.setText("Cập Nhật Trạng Thái");
        btnUpdateStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnUpdateStatus.setkBorderRadius(30);
        btnUpdateStatus.setkEndColor(new Color(51, 153, 102)); // Màu xanh lá cây
        btnUpdateStatus.setkHoverEndColor(new Color(51, 204, 102));
        btnUpdateStatus.setkHoverForeGround(new Color(255, 255, 255));
        btnUpdateStatus.setkHoverStartColor(new Color(102, 204, 102));
        btnUpdateStatus.setEnabled(false); // Ban đầu vô hiệu hóa
        btnUpdateStatus.addActionListener(e -> {
            if (btnUpdateStatus.isEnabled()) {
                int selectedRow = tablePurchaseOrders.getSelectedRow();
                if (selectedRow >= 0) {
                    String purchaseOrderId = tablePurchaseOrders.getValueAt(selectedRow, 1).toString();
                    String currentStatus = tablePurchaseOrders.getValueAt(selectedRow, 4).toString();
                    
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
        });

        // Thiết lập nút đóng
        btnClose.setText("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setkBorderRadius(30);
        btnClose.setkEndColor(new Color(102, 153, 255));
        btnClose.setkHoverEndColor(new Color(102, 153, 255));
        btnClose.setkHoverForeGround(new Color(255, 255, 255));
        btnClose.setkHoverStartColor(new Color(153, 255, 153));
        btnClose.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                btnCloseMouseClicked(evt);
            }
        });

        // Thiết lập layout
        GroupLayout kGradientPanel1Layout = new GroupLayout(kGradientPanel1);
        kGradientPanel1.setLayout(kGradientPanel1Layout);
        kGradientPanel1Layout.setHorizontalGroup(
            kGradientPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(kGradientPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle, GroupLayout.DEFAULT_SIZE, 954, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.TRAILING, kGradientPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnUpdateStatus, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRefresh, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2)
                    .addGroup(GroupLayout.Alignment.TRAILING, kGradientPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnClose, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblOrderDetail, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        kGradientPanel1Layout.setVerticalGroup(
            kGradientPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(kGradientPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRefresh, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdateStatus, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblOrderDetail)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnClose, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        // Thiết lập layout của form
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(kGradientPanel1, BorderLayout.CENTER);

        pack();
    }

    private void btnCloseMouseClicked(MouseEvent evt) {
        dispose();
    }
    
    private void btnRefreshMouseClicked(MouseEvent evt) {
        // Gọi phương thức làm mới dữ liệu từ controller
        if (controller != null) {
            System.out.println("Refreshing purchase order history...");
            controller.loadPurchaseOrderHistory();
            
            // Vô hiệu hóa nút cập nhật trạng thái khi làm mới
            enableUpdateStatusButton(false);
        }
    }
    
    private void btnUpdateStatusMouseClicked(MouseEvent evt) {
        // Kiểm tra xem nút có được kích hoạt không
        if (!btnUpdateStatus.isEnabled()) {
            return;
        }
        
        int selectedRow = tablePurchaseOrders.getSelectedRow();
        if (selectedRow >= 0) {
            String purchaseOrderId = tablePurchaseOrders.getValueAt(selectedRow, 1).toString(); // Cột Mã Phiếu
            String currentStatus = tablePurchaseOrders.getValueAt(selectedRow, 4).toString();   // Cột Trạng Thái
            
            // Gọi phương thức hiển thị dialog từ controller
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

    /**
     * Getter cho bảng phiếu nhập
     */
    public JTable getTablePurchaseOrders() {
        return tablePurchaseOrders;
    }

    /**
     * Getter cho bảng chi tiết phiếu nhập
     */
    public JTable getTablePurchaseOrderDetails() {
        return tablePurchaseOrderDetails;
    }

    /**
     * Getter cho nút đóng
     */
    public KButton getBtnClose() {
        return btnClose;
    }
    
    /**
     * Getter cho nút làm mới
     */
    public KButton getBtnRefresh() {
        return btnRefresh;
    }
    
    /**
     * Getter cho nút cập nhật trạng thái
     */
    public KButton getBtnUpdateStatus() {
        return btnUpdateStatus;
    }
    
    /**
     * Kích hoạt hoặc vô hiệu hóa nút cập nhật trạng thái
     * @param enable true để kích hoạt, false để vô hiệu hóa
     */
    public void enableUpdateStatusButton(boolean enable) {
        btnUpdateStatus.setEnabled(enable);
    }

    // Variables declaration - do not modify
    private JTable tablePurchaseOrders;
    private JTable tablePurchaseOrderDetails;
    private KButton btnClose;
    private KButton btnRefresh; 
    private KButton btnUpdateStatus; // Nút cập nhật trạng thái
    
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private KGradientPanel kGradientPanel1;
    private JLabel lblTitle;
    private JLabel lblOrderDetail;
    // End of variables declaration
}