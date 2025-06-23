package com.pcstore.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import com.k33ptoo.components.KButton;
import com.raven.datechooser.DateChooser;
import com.pcstore.controller.ReportRevenueSellingController;

/**
 * Revenue Selling Report Form
 * 
 * @author DUC ANH
 */
public class ReportRevenueSellingForm extends javax.swing.JPanel {

    private final ReportRevenueSellingController controller;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Revenue;
    private com.k33ptoo.components.KButton btnApply;
    private com.k33ptoo.components.KButton btnExportReport;
    private javax.swing.JButton btnFromDate;
    private javax.swing.JButton btnToDate;
    private javax.swing.JComboBox<String> cmbTimePeriod;
    private com.raven.datechooser.DateChooser dateChooserFromDate;
    private com.raven.datechooser.DateChooser dateChooserToDate;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JLabel lbFromDate;
    private javax.swing.JLabel lbTimePeriod;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JLabel lbToDate;
    private javax.swing.JLabel lbTotalOrders;
    private javax.swing.JLabel lbTotalRevenue;
    private javax.swing.JLabel lbAverageOrderValue;
    private javax.swing.JLabel lbProfit;
    private javax.swing.JPanel panelAction;
    private javax.swing.JPanel panelBody;
    private javax.swing.JPanel panelBodyTop;
    private javax.swing.JPanel panelChart;
    private com.k33ptoo.components.KGradientPanel panelContent;
    private javax.swing.JPanel panelDate;
    private javax.swing.JPanel panelEmpty;
    private javax.swing.JPanel panelFooter;
    private javax.swing.JPanel panelFromDate;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelTable;
    private javax.swing.JPanel panelTimePeriod;
    private javax.swing.JPanel panelToDate;
    private javax.swing.JTable tableRevenue;
    private javax.swing.JTextField txtFromDate;
    private javax.swing.JLabel txtTotalOrders;
    private javax.swing.JLabel txtTotalRevenue;
    private javax.swing.JLabel txtAverageOrderValue;
    private javax.swing.JLabel txtProfit;
    private javax.swing.JTextField txtToDate;
    // End of variables declaration//GEN-END:variables

    /**
     * Creates new form ReportRevenueSellingForm
     */
    public ReportRevenueSellingForm() {
        initComponents();
        initializeTimePeriods();
        this.setChooserFromDate(LocalDate.now().withDayOfMonth(1));
        this.setChooserToDate(LocalDate.now());
        this.controller = ReportRevenueSellingController.getInstance(this);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dateChooserFromDate = new com.raven.datechooser.DateChooser();
        dateChooserToDate = new com.raven.datechooser.DateChooser();
        panelHeader = new javax.swing.JPanel();
        lbTitle = new javax.swing.JLabel();
        panelAction = new javax.swing.JPanel();
        panelDate = new javax.swing.JPanel();
        panelTimePeriod = new javax.swing.JPanel();
        lbTimePeriod = new javax.swing.JLabel();
        cmbTimePeriod = new javax.swing.JComboBox<>();
        panelFromDate = new javax.swing.JPanel();
        lbFromDate = new javax.swing.JLabel();
        txtFromDate = new javax.swing.JTextField();
        btnFromDate = new javax.swing.JButton();
        panelToDate = new javax.swing.JPanel();
        lbToDate = new javax.swing.JLabel();
        txtToDate = new javax.swing.JTextField();
        btnToDate = new javax.swing.JButton();
        btnApply = new com.k33ptoo.components.KButton();
        btnExportReport = new com.k33ptoo.components.KButton();
        panelEmpty = new javax.swing.JPanel();
        panelBody = new javax.swing.JPanel();
        panelBodyTop = new javax.swing.JPanel();
        panelChart = new javax.swing.JPanel();
        panelContent = new com.k33ptoo.components.KGradientPanel();
        lbTotalOrders = new javax.swing.JLabel();
        txtTotalOrders = new javax.swing.JLabel();
        lbTotalRevenue = new javax.swing.JLabel();
        txtTotalRevenue = new javax.swing.JLabel();
        lbAverageOrderValue = new javax.swing.JLabel();
        txtAverageOrderValue = new javax.swing.JLabel();
        lbProfit = new javax.swing.JLabel();
        txtProfit = new javax.swing.JLabel();
        panelTable = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        tableRevenue = new javax.swing.JTable();
        panelFooter = new javax.swing.JPanel();
        lbTotalRevenue = new javax.swing.JLabel();
        Revenue = new javax.swing.JLabel();

        dateChooserFromDate.setTextRefernce(txtFromDate);
        dateChooserToDate.setTextRefernce(txtToDate);

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        panelHeader.setBackground(new java.awt.Color(255, 255, 255));
        panelHeader.setPreferredSize(new java.awt.Dimension(990, 50));
        panelHeader.setLayout(new java.awt.BorderLayout());

        lbTitle.setFont(new java.awt.Font("Segoe UI", 1, 20));
        lbTitle.setForeground(new java.awt.Color(30, 113, 195));
        lbTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle();
        lbTitle.setText(bundle.getString("lbReportRevenueTitle"));

        lbTitle.setFocusable(false);
        panelHeader.add(lbTitle, java.awt.BorderLayout.CENTER);

        add(panelHeader);

        panelAction.setBackground(new java.awt.Color(255, 255, 255));
        panelAction.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 30, 1, 30));
        panelAction.setMinimumSize(new java.awt.Dimension(354, 45));
        panelAction.setPreferredSize(new java.awt.Dimension(980, 60));
        panelAction.setLayout(new java.awt.BorderLayout());

        panelDate.setBackground(new java.awt.Color(255, 255, 255));
        panelDate.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));

        panelTimePeriod.setBackground(new java.awt.Color(255, 255, 255));

        lbTimePeriod.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lbTimePeriod.setForeground(new java.awt.Color(30, 113, 195));
        lbTimePeriod.setText(bundle.getString("lbTimePeriod"));
        panelTimePeriod.add(lbTimePeriod);

        cmbTimePeriod.setFont(new java.awt.Font("Segoe UI", 0, 13));
        cmbTimePeriod.setForeground(new java.awt.Color(26, 162, 106));
        cmbTimePeriod.setPreferredSize(new java.awt.Dimension(150, 25));
        cmbTimePeriod.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelTimePeriod.add(cmbTimePeriod);

        panelDate.add(panelTimePeriod);

        panelFromDate.setBackground(new java.awt.Color(255, 255, 255));

        lbFromDate.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lbFromDate.setForeground(new java.awt.Color(30, 113, 195));
        lbFromDate.setText(bundle.getString("lbFromDate"));
        panelFromDate.add(lbFromDate);

        txtFromDate.setFont(new java.awt.Font("Segoe UI", 0, 13));
        txtFromDate.setForeground(new java.awt.Color(26, 162, 106));
        txtFromDate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtFromDate.setPreferredSize(new java.awt.Dimension(150, 25));
        panelFromDate.add(txtFromDate);

        btnFromDate.setFont(new java.awt.Font("Segoe UI", 0, 14));
        btnFromDate
                .setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/schedule.png")));

        btnFromDate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFromDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnFromDateMouseClicked(evt);
            }
        });
        panelFromDate.add(btnFromDate);

        panelDate.add(panelFromDate);

        panelToDate.setBackground(new java.awt.Color(255, 255, 255));

        lbToDate.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lbToDate.setForeground(new java.awt.Color(30, 113, 195));
        lbToDate.setText(bundle.getString("lbToDate"));
        panelToDate.add(lbToDate);

        txtToDate.setFont(new java.awt.Font("Segoe UI", 0, 13));
        txtToDate.setForeground(new java.awt.Color(26, 162, 106));
        txtToDate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtToDate.setPreferredSize(new java.awt.Dimension(150, 25));
        panelToDate.add(txtToDate);

        btnToDate.setFont(new java.awt.Font("Segoe UI", 0, 14));
        btnToDate
                .setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/schedule.png")));
        btnToDate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnToDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnToDateMouseClicked(evt);
            }
        });
        panelToDate.add(btnToDate);

        panelDate.add(panelToDate);

        btnApply.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/filter.png")));
        btnApply.setText(bundle.getString("btnApply"));
        btnApply.setFont(new java.awt.Font("Segoe UI", 1, 14));
        btnApply.setkAllowGradient(false);
        btnApply.setkBackGroundColor(new java.awt.Color(0, 189, 225));
        btnApply.setkBorderRadius(20);
        btnApply.setkFocusColor(new java.awt.Color(255, 255, 255));
        btnApply.setkHoverColor(new java.awt.Color(0, 102, 255));
        btnApply.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnApply.setkShowFocusBorder(true);
        btnApply.setPreferredSize(new java.awt.Dimension(130, 32));
        panelDate.add(btnApply);

        panelAction.add(panelDate, java.awt.BorderLayout.LINE_START);

        btnExportReport
                .setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/xls.png")));
        btnExportReport.setText(bundle.getString("btnExportExcel"));
        btnExportReport.setFont(new java.awt.Font("Segoe UI", 1, 14));
        btnExportReport.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        btnExportReport.setkAllowGradient(false);
        btnExportReport.setkBackGroundColor(new java.awt.Color(26, 162, 106));
        btnExportReport.setkBorderRadius(30);
        btnExportReport.setkEndColor(new java.awt.Color(102, 153, 255));
        btnExportReport.setkFocusColor(new java.awt.Color(255, 255, 255));
        btnExportReport.setkHoverColor(new java.awt.Color(30, 189, 125));
        btnExportReport.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnExportReport.setkHoverForeGround(new java.awt.Color(30, 113, 195));
        btnExportReport.setPreferredSize(new java.awt.Dimension(150, 40));
        panelAction.add(btnExportReport, java.awt.BorderLayout.LINE_END);

        panelEmpty.setBackground(new java.awt.Color(255, 255, 255));
        panelEmpty.setPreferredSize(new java.awt.Dimension(644, 50));

        javax.swing.GroupLayout panelEmptyLayout = new javax.swing.GroupLayout(panelEmpty);
        panelEmpty.setLayout(panelEmptyLayout);
        panelEmptyLayout.setHorizontalGroup(
                panelEmptyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 253, Short.MAX_VALUE));
        panelEmptyLayout.setVerticalGroup(
                panelEmptyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 68, Short.MAX_VALUE));

        panelAction.add(panelEmpty, java.awt.BorderLayout.CENTER);

        add(panelAction);

        panelBody.setBackground(new java.awt.Color(255, 255, 255));
        panelBody.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("lbReportInfoTitle"),
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(30, 113, 195)));
        panelBody.setPreferredSize(new java.awt.Dimension(990, 580));
        panelBody.setLayout(new java.awt.BorderLayout());

        panelBodyTop.setOpaque(false);
        panelBodyTop.setPreferredSize(new java.awt.Dimension(980, 300));
        panelBodyTop.setLayout(new javax.swing.BoxLayout(panelBodyTop, javax.swing.BoxLayout.LINE_AXIS));

        panelChart.setMinimumSize(new java.awt.Dimension(500, 250));
        panelChart.setOpaque(false);
        panelChart.setPreferredSize(new java.awt.Dimension(400, 250));
        panelChart.setLayout(new java.awt.BorderLayout());
        panelBodyTop.add(panelChart);

        panelContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 5));
        panelContent.setkBorderColor(new java.awt.Color(153, 153, 153));
        panelContent.setkBorderSize(2.0F);
        panelContent.setkFillBackground(false);
        panelContent.setOpaque(false);
        panelContent.setPreferredSize(new java.awt.Dimension(600, 233));
        panelContent.setLayout(new java.awt.GridLayout(4, 2));

        lbTotalOrders.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lbTotalOrders.setForeground(new java.awt.Color(0, 69, 196));
        lbTotalOrders.setText(bundle.getString("lbTotalOrders"));
        panelContent.add(lbTotalOrders);

        txtTotalOrders.setFont(new java.awt.Font("Segoe UI", 1, 14));
        txtTotalOrders.setForeground(new java.awt.Color(0, 69, 196));
        txtTotalOrders.setText("...");
        panelContent.add(txtTotalOrders);

        // lbTotalRevenue.setFont(new java.awt.Font("Segoe UI", 1, 14));
        // lbTotalRevenue.setForeground(new java.awt.Color(0, 69, 196));
        // lbTotalRevenue.setText("Tổng doanh thu:");
        // panelContent.add(lbTotalRevenue);

        // txtTotalRevenue.setFont(new java.awt.Font("Segoe UI", 1, 14));
        // txtTotalRevenue.setForeground(new java.awt.Color(0, 69, 196));
        // txtTotalRevenue.setText("...");
        // panelContent.add(txtTotalRevenue);

        lbAverageOrderValue.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lbAverageOrderValue.setForeground(new java.awt.Color(0, 69, 196));
        lbAverageOrderValue.setText(bundle.getString("lbAverageOrderValue"));
        panelContent.add(lbAverageOrderValue);

        txtAverageOrderValue.setFont(new java.awt.Font("Segoe UI", 1, 14));
        txtAverageOrderValue.setForeground(new java.awt.Color(0, 69, 196));
        txtAverageOrderValue.setText("...");
        panelContent.add(txtAverageOrderValue);

        lbProfit.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lbProfit.setForeground(new java.awt.Color(0, 69, 196));
        lbProfit.setText(bundle.getString("lbNetProfit"));
        panelContent.add(lbProfit);

        txtProfit.setFont(new java.awt.Font("Segoe UI", 1, 14));
        txtProfit.setForeground(new java.awt.Color(0, 69, 196));
        txtProfit.setText("...");
        panelContent.add(txtProfit);

        panelBodyTop.add(panelContent);

        panelBody.add(panelBodyTop, java.awt.BorderLayout.CENTER);

        panelTable.setPreferredSize(new java.awt.Dimension(980, 300));
        panelTable.setLayout(new java.awt.BorderLayout());

        tableRevenue.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null, null, null, null }
                },
                new String[] {
                        "Mã hóa đơn", "Khách hàng", "Nhân viên", "Số lượng", "Tổng tiền hàng", "Giảm giá", "Doanh thu",
                        "Thu khác", "Thực thu", "Ghi chú"
                }) {
            boolean[] canEdit = new boolean[] {
                    false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        tableRevenue.getTableHeader().setReorderingAllowed(false);
        jScrollPane.setViewportView(tableRevenue);
        if (tableRevenue.getColumnModel().getColumnCount() > 0) {
            tableRevenue.getColumnModel().getColumn(0).setPreferredWidth(120);
            tableRevenue.getColumnModel().getColumn(0).setMaxWidth(120);
            tableRevenue.getColumnModel().getColumn(1).setPreferredWidth(150);
            tableRevenue.getColumnModel().getColumn(2).setPreferredWidth(120);
            tableRevenue.getColumnModel().getColumn(3).setPreferredWidth(80);
            tableRevenue.getColumnModel().getColumn(3).setMaxWidth(80);
            tableRevenue.getColumnModel().getColumn(4).setPreferredWidth(120);
            tableRevenue.getColumnModel().getColumn(5).setPreferredWidth(100);
            tableRevenue.getColumnModel().getColumn(6).setPreferredWidth(120);
            tableRevenue.getColumnModel().getColumn(7).setPreferredWidth(100);
            tableRevenue.getColumnModel().getColumn(8).setPreferredWidth(120);
            tableRevenue.getColumnModel().getColumn(9).setPreferredWidth(150);
        }

        panelTable.add(jScrollPane, java.awt.BorderLayout.CENTER);

        panelBody.add(panelTable, java.awt.BorderLayout.PAGE_END);

        add(panelBody);

        panelFooter.setBackground(new java.awt.Color(255, 255, 255));
        panelFooter.setPreferredSize(new java.awt.Dimension(217, 50));
        panelFooter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));

        lbTotalRevenue.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lbTotalRevenue.setForeground(new java.awt.Color(30, 113, 195));
        lbTotalRevenue.setText("Tổng Doanh Thu:");
        lbTotalRevenue.setFocusable(false);
        panelFooter.add(lbTotalRevenue);

        Revenue.setFont(new java.awt.Font("Segoe UI", 1, 18));
        Revenue.setForeground(new java.awt.Color(26, 162, 106));
        Revenue.setText("                     ");
        Revenue.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(204, 204, 204)));
        Revenue.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        Revenue.setFocusable(false);
        Revenue.setPreferredSize(new java.awt.Dimension(200, 26));
        panelFooter.add(Revenue);

        add(panelFooter);
    }// </editor-fold>//GEN-END:initComponents

    private void btnFromDateMouseClicked(java.awt.event.MouseEvent evt) {
        dateChooserFromDate.showPopup();
    }

    private void btnToDateMouseClicked(java.awt.event.MouseEvent evt) {
        dateChooserToDate.showPopup();
    }

    private void initializeTimePeriods() {
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle();
        cmbTimePeriod.addItem(bundle.getString("cmbTimePeriod.custom"));
        cmbTimePeriod.addItem(bundle.getString("cmbTimePeriod.today"));
        cmbTimePeriod.addItem(bundle.getString("cmbTimePeriod.last7Days"));
        cmbTimePeriod.addItem(bundle.getString("cmbTimePeriod.last30Days"));
        cmbTimePeriod.addItem(bundle.getString("cmbTimePeriod.thisMonth"));
        cmbTimePeriod.addItem(bundle.getString("cmbTimePeriod.lastMonth"));

        cmbTimePeriod.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleTimePeriodSelection();
            }
        });
    }

    private void handleTimePeriodSelection() {
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle();
        String selected = (String) cmbTimePeriod.getSelectedItem();
        LocalDate fromDate = null;
        LocalDate toDate = LocalDate.now();

        if (selected.equals(bundle.getString("cmbTimePeriod.today"))) {
            fromDate = LocalDate.now();
        } else if (selected.equals(bundle.getString("cmbTimePeriod.last7Days"))) {
            fromDate = LocalDate.now().minusDays(7);
        } else if (selected.equals(bundle.getString("cmbTimePeriod.last30Days"))) {
            fromDate = LocalDate.now().minusDays(30);
        } else if (selected.equals(bundle.getString("cmbTimePeriod.thisMonth"))) {
            fromDate = LocalDate.now().withDayOfMonth(1);
        } else if (selected.equals(bundle.getString("cmbTimePeriod.lastMonth"))) {
            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            fromDate = lastMonth.withDayOfMonth(1);
            toDate = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth());
        } else {
            return;
        }

        if (fromDate != null) {
            setChooserFromDate(fromDate);
            setChooserToDate(toDate);
        }
    }

    private boolean isUpdatingDates = false;

    public void setChooserFromDate(LocalDate date) {
        if (isUpdatingDates)
            return;

        isUpdatingDates = true;
        try {
            final Date selectedDate = java.sql.Date.valueOf(date);
            SwingUtilities.invokeLater(() -> {
                dateChooserFromDate.setSelectedDate(selectedDate);
            });
        } finally {
            isUpdatingDates = false;
        }
    }

    public void setChooserToDate(LocalDate date) {
        if (isUpdatingDates)
            return;

        isUpdatingDates = true;
        try {
            final Date selectedDate = java.sql.Date.valueOf(date);
            SwingUtilities.invokeLater(() -> {
                dateChooserToDate.setSelectedDate(selectedDate);
            });
        } finally {
            isUpdatingDates = false;
        }
    }

    // Getter methods
    public JComboBox<String> getCmbTimePeriod() {
        return cmbTimePeriod;
    }

    public KButton getBtnApply() {
        return btnApply;
    }

    public KButton getBtnExportReport() {
        return btnExportReport;
    }

    public javax.swing.JButton getBtnFromDate() {
        return btnFromDate;
    }

    public javax.swing.JButton getBtnToDate() {
        return btnToDate;
    }

    public javax.swing.JLabel getRevenue() {
        return Revenue;
    }

    public javax.swing.JTable getTableRevenue() {
        return tableRevenue;
    }

    public javax.swing.JTextField getTxtFromDate() {
        return txtFromDate;
    }

    public javax.swing.JTextField getTxtToDate() {
        return txtToDate;
    }

    public javax.swing.JPanel getPanelChart() {
        return panelChart;
    }

    public javax.swing.JLabel getTxtTotalOrders() {
        return txtTotalOrders;
    }

    public javax.swing.JLabel getTxtTotalRevenue() {
        return txtTotalRevenue;
    }

    public javax.swing.JLabel getTxtAverageOrderValue() {
        return txtAverageOrderValue;
    }

    public javax.swing.JLabel getTxtProfit() {
        return txtProfit;
    }
}
