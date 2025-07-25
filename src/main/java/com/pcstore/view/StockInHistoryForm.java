package com.pcstore.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
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
import com.pcstore.utils.ButtonUtils;
import com.pcstore.utils.LocaleManager;

/**
 * Form hiển thị lịch sử nhập kho
 */
public class StockInHistoryForm extends JDialog {
    private StockInHistoryController controller;
    private ResourceBundle bundle;

    // Variables declaration - do not modify
    private JTable tablePurchaseOrders;
    private JTable tablePurchaseOrderDetails;
    private KButton btnClose;
    private KButton btnRefresh;
    private KButton btnUpdateStatus; // Nút cập nhật trạng thái
    private KButton btnPrintOrder; 
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
        // Lấy bundle từ LocaleManager
        bundle = LocaleManager.getInstance().getResourceBundle();
        setTitle(bundle.getString("txtHistoryStockIn"));
        initComponents();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Khởi tạo controller
        controller = new StockInHistoryController(this);
        enableOrderButtons(false); 
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {
        // Khởi tạo các component
        panelMain = new KGradientPanel();
        lblTitle = new JLabel(bundle.getString("txtHistoryStockIn"));
        jScrollPaneOrders = new JScrollPane();
        tablePurchaseOrders = new JTable();
        jScrollPaneDetails = new JScrollPane();
        tablePurchaseOrderDetails = new JTable();
        btnClose = new KButton();
        btnRefresh = new KButton();
        btnUpdateStatus = new KButton();
        btnPrintOrder = new KButton();
        lblOrderDetail = new JLabel(bundle.getString("txtOrderDetail"));

        // Thiết lập cơ bản cho form
        setMinimumSize(new java.awt.Dimension(1000, 700));
        setResizable(false);

        // Thiết lập layout cho form chính (ContentPane)
        getContentPane().setLayout(new BorderLayout());

        // Thiết lập panelMain
        panelMain.setkFillBackground(false);
        panelMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelMain.setLayout(new BorderLayout(0, 10)); // Khoảng cách 10px giữa các thành phần

        // === PANEL TIÊU ĐỀ - NORTH ===
        JPanel panelTop = new JPanel(new BorderLayout(0, 10));
        panelTop.setOpaque(false);

        // Thiết lập tiêu đề
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(new Color(0, 102, 204));
        panelTop.add(lblTitle, BorderLayout.NORTH);

        // Panel chứa các nút trên cùng
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelButtons.setOpaque(false);

        // Thiết lập nút làm mới
        btnUpdateStatus.setkAllowGradient(false);
        btnRefresh.setText(bundle.getString("btnRefresh"));
        btnRefresh.setIcon(new ImageIcon(getClass().getResource("/com/pcstore/resources/icon/refresh.png")));
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setkBorderRadius(30);
        btnRefresh.setkEndColor(new Color(0, 153, 255));
        btnRefresh.setkHoverColor(new Color(51, 153, 255));
        btnRefresh.setkHoverForeGround(new Color(255, 255, 255));
        btnRefresh.setPreferredSize(new Dimension(120, 40));
        btnRefresh.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                btnRefreshMouseClicked(evt);
            }
        });

        // Thiết lập nút cập nhật trạng thái
        btnUpdateStatus.setkAllowGradient(false);
        btnUpdateStatus.setText(bundle.getString("btnUpdateStatus"));
        btnUpdateStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnUpdateStatus.setkBorderRadius(30);
        btnUpdateStatus.setkBackGroundColor(new Color(51, 153, 102));
        btnUpdateStatus.setkHoverColor(new Color(51, 204, 102));
        btnUpdateStatus.setkHoverForeGround(new Color(255, 255, 255));
        btnUpdateStatus.setPreferredSize(new Dimension(180, 40));
        btnUpdateStatus.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                btnUpdateStatusMouseClicked(evt);
            }
        });

        // Thiết lập nút In phiếu
        btnPrintOrder.setText(bundle.getString("btnPrintOrder"));
        btnPrintOrder.setIcon(new ImageIcon(getClass().getResource("/com/pcstore/resources/icon/printer.png")));
        btnPrintOrder.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPrintOrder.setkBorderRadius(30);
        btnPrintOrder.setkBackGroundColor(new Color(0, 102, 204));
        btnPrintOrder.setkHoverForeGround(new Color(255, 255, 255));
        btnPrintOrder.setkHoverColor(new Color(102, 204, 255));
        btnPrintOrder.setPreferredSize(new Dimension(150, 40));
        btnPrintOrder.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                btnPrintOrderMouseClicked(evt);
            }
        });

        // Thêm nút vào panel
        panelButtons.add(btnUpdateStatus);
        panelButtons.add(btnPrintOrder); 
        panelButtons.add(btnRefresh);
        panelTop.add(panelButtons, BorderLayout.CENTER);

        // === PANEL NỘI DUNG CHÍNH - CENTER ===
        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(new BorderLayout(0, 10));
        panelCenter.setOpaque(false);

        // === BẢNG PHIẾU NHẬP ===
        // Thiết lập bảng phiếu nhập
        tablePurchaseOrders.setModel(new DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null, null }
                },
                new String[] {
                        bundle.getString("txtSTT"), 
                        bundle.getString("lbPurchaseOrderId"), 
                        bundle.getString("txtCreatedAt"), 
                        bundle.getString("lbEmloyeesCreate"), 
                        bundle.getString("txtStatus"), 
                        bundle.getString("lbTotalMoney")
                }) {
            boolean[] canEdit = new boolean[] {
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

        // === PANEL CHI TIẾT PHIẾU NHẬP - SOUTH ===
        JPanel panelDetails = new JPanel();
        panelDetails.setLayout(new BorderLayout(0, 5));
        panelDetails.setOpaque(false);

        // Thiết lập nhãn chi tiết
        lblOrderDetail.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblOrderDetail.setForeground(new Color(0, 102, 204));
        panelDetails.add(lblOrderDetail, BorderLayout.NORTH);

        // Thiết lập bảng chi tiết phiếu nhập
        tablePurchaseOrderDetails.setModel(new DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null, null }
                },
                new String[] {
                        bundle.getString("txtSTT"), 
                        bundle.getString("clProductID"), 
                        bundle.getString("clProductName"), 
                        bundle.getString("clQuantity"), 
                        bundle.getString("clPrice"), 
                        bundle.getString("clMoney")
                }) {
            boolean[] canEdit = new boolean[] {
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

        // === PANEL BUTTON DƯỚI - SOUTH ===
        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBottom.setOpaque(false);

        // Thiết lập nút đóng
        btnClose.setText(bundle.getString("btnClose"));
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

        // Thêm các panel vào panelMain
        panelMain.add(panelTop, BorderLayout.NORTH);
        panelMain.add(panelCenter, BorderLayout.CENTER);
        panelMain.add(panelBottom, BorderLayout.SOUTH);

        // Thêm panelMain vào ContentPane
        getContentPane().add(panelMain, BorderLayout.CENTER);

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
            String currentStatus = tablePurchaseOrders.getValueAt(selectedRow, 4).toString(); // Cột Trạng Thái

            // Gọi phương thức hiển thị dialog từ controller
            if (controller != null) {
                controller.showUpdateStatusDialog(purchaseOrderId, currentStatus);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    bundle.getString("txtSelectPurchaseOrderToUpdate"),
                    bundle.getString("txtNotification"),
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void btnPrintOrderMouseClicked(MouseEvent evt) {
        if (!btnPrintOrder.isEnabled()) {
            return;
        }

        int selectedRow = tablePurchaseOrders.getSelectedRow();
        if (selectedRow >= 0) {
            String purchaseOrderId = tablePurchaseOrders.getValueAt(selectedRow, 1).toString(); 

            if (controller != null) {
                controller.printPurchaseOrder(purchaseOrderId);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    bundle.getString("txtSelectPurchaseOrderToPrint"),
                    bundle.getString("txtNotification"),
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
     * Getter cho nút in phiếu
     */
    public KButton getBtnPrintOrder() {
        return btnPrintOrder;
    }

    /**
     * Kích hoạt hoặc vô hiệu hóa nút cập nhật trạng thái
     * 
     * @param enable true để kích hoạt, false để vô hiệu hóa
     */
    public void enableUpdateStatusButton(boolean enable) {
        ButtonUtils.setKButtonEnabled(btnUpdateStatus, enable);
    }

    /**
     * Kích hoạt hoặc vô hiệu hóa các nút khi chọn phiếu
     * 
     * @param enable true để kích hoạt, false để vô hiệu hóa
     */
    public void enableOrderButtons(boolean enable) {
        ButtonUtils.setKButtonEnabled(btnUpdateStatus, enable);
        ButtonUtils.setKButtonEnabled(btnPrintOrder, enable);
    }
}