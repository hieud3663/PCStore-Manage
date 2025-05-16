package com.pcstore.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.pcstore.controller.InvoiceController;
import com.pcstore.controller.InvoiceDetailController;
import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.utils.ExportInvoice;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.TableStyleUtil;

public class InvoiceDetailDialog extends JDialog {
    
    private JTable tableProducts;
    private DefaultTableModel tableModel;
    private TableRowSorter tableSorter;
    private final DateTimeFormatter dateFormatter = LocaleManager.getInstance().getDateTimeFormatter();
    private final NumberFormat numberFormat = LocaleManager.getInstance().getNumberFormatter();
    private final ResourceBundle bundle = LocaleManager.getInstance().getResourceBundle();
    private Invoice invoice;
    private List<InvoiceDetail> invoiceDetails;
    private InvoiceDetailController invoiceDetailController;
    
    public InvoiceDetailDialog(JDialog owner, String invoiceId) {
        super(owner, true);
        try {
            // Sử dụng InvoiceController để lấy thông tin hóa đơn
            InvoiceController invoiceController = new InvoiceController(null);
            Optional<Invoice> invoiceOpt = invoiceController.getInvoiceById(Integer.parseInt(invoiceId));
            this.invoice = invoiceOpt.orElseThrow(() -> new Exception("Không tìm thấy hóa đơn"));
            
            // Sử dụng InvoiceDetailController để lấy chi tiết hóa đơn
            this.invoiceDetailController = new InvoiceDetailController();
            this.invoiceDetails = invoiceDetailController.findInvoiceDetailsByInvoiceId(Integer.parseInt(invoiceId));
            
            initComponents();
            setSize(950, 650);
            setLocationRelativeTo(owner);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(owner, 
                    "Lỗi khi tải thông tin hóa đơn: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }
    
    private void initComponents() {
        setTitle(bundle.getString("InvoiceDetails") + ": " + invoice.getInvoiceId());
        setBackground(Color.WHITE);
        
        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // ===== HEADER PANEL =====
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setBackground(Color.WHITE);
        
        // Left side - Invoice info
        JPanel invoiceInfoPanel = createInvoiceInfoPanel();
        
        // Right side - Customer & Employee info
        JPanel peopleInfoPanel = createPeopleInfoPanel();
        
        headerPanel.add(invoiceInfoPanel, BorderLayout.WEST);
        headerPanel.add(peopleInfoPanel, BorderLayout.EAST);
        
        // ===== CONTENT PANEL =====
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        
        // Products table
        JPanel productsPanel = new JPanel(new BorderLayout());
        productsPanel.setBackground(Color.WHITE);
        
        JLabel productsLabel = new JLabel(bundle.getString("titileBorderListProduct"));
        productsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        productsLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        createProductsTable();
        JScrollPane scrollPane = new JScrollPane(tableProducts);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        JPanel roundedTablePanel = new JPanel(new BorderLayout());
        roundedTablePanel.setBackground(Color.WHITE);
        roundedTablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(0, 0, 0, 0)));
        roundedTablePanel.add(scrollPane);
        
        productsPanel.add(productsLabel, BorderLayout.NORTH);
        productsPanel.add(roundedTablePanel, BorderLayout.CENTER);
        
        // Summary panel
        JPanel summaryPanel = createSummaryPanel();
        
        contentPanel.add(productsPanel, BorderLayout.CENTER);
        contentPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        // ===== FOOTER PANEL =====
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        
        JButton printButton = new JButton(bundle.getString("btnPrintInvoice"));
        printButton.setIcon(new ImageIcon(getClass().getResource("/com/pcstore/resources/icon/printer.png")));
        printButton.setPreferredSize(new Dimension(200, 40));
        printButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        printButton.setBackground(new Color(240, 240, 240));
        printButton.setForeground(new Color(50, 50, 50));
        printButton.setFocusPainted(false);
        printButton.setBorderPainted(false);
        printButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        printButton.addActionListener(e -> printInvoice());
        
        JButton closeButton = new JButton(bundle.getString("btnClose"));
        closeButton.setPreferredSize(new Dimension(120, 40));
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setBackground(new Color(51, 153, 255));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());
        
        footerPanel.add(printButton);
        footerPanel.add(Box.createHorizontalStrut(10));
        footerPanel.add(closeButton);
        
        // Add all sections to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createInvoiceInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(0, 0, 15, 0)));
        
        JLabel idLabel = new JLabel(bundle.getString("txtInvoiceID") + ": " + invoice.getInvoiceId());
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Status badge
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel statusLabel = createStatusBadge(invoice.getStatus().getDisplayName());
        statusPanel.add(statusLabel);
        
        JLabel dateLabel = new JLabel(bundle.getString("txtInvoiceCreateAt") + ": " + 
                invoice.getCreatedAt().format(dateFormatter));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel paymentLabel = new JLabel(bundle.getString("txtInvoicePaymentMethod") + ": " + 
                invoice.getPaymentMethod().getDisplayName());
        paymentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        paymentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(idLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(statusPanel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(dateLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(paymentLabel);
        
        return panel;
    }
    
    private JLabel createStatusBadge(String status) {
        JLabel badge = new JLabel(status);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        String statusLower = status.toLowerCase();
        if (statusLower.contains("hoàn thành") || statusLower.contains("completed")) {
            badge.setBackground(new Color(232, 245, 233));
            badge.setForeground(new Color(76, 175, 80));
        } else if (statusLower.contains("xử lý") || statusLower.contains("processing")) {
            badge.setBackground(new Color(255, 248, 225));
            badge.setForeground(new Color(255, 193, 7));
        } else if (statusLower.contains("hủy") || statusLower.contains("cancel")) {
            badge.setBackground(new Color(255, 235, 238));
            badge.setForeground(new Color(244, 67, 54));
        } else {
            badge.setBackground(new Color(245, 245, 245));
            badge.setForeground(new Color(158, 158, 158));
        }
        
        return badge;
    }
    
    private JPanel createPeopleInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 30, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(0, 0, 15, 0)));
        
        // Customer info
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new BoxLayout(customerPanel, BoxLayout.Y_AXIS));
        customerPanel.setBackground(Color.WHITE);
        
        JLabel customerTitle = new JLabel(bundle.getString("txtInvoiceCustomer"));
        customerTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        customerTitle.setForeground(new Color(100, 100, 100));
        customerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel customerName = new JLabel(invoice.getCustomer() != null ? 
                invoice.getCustomer().getFullName() : "Khách lẻ");
        customerName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        customerName.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel customerPhone = new JLabel(invoice.getCustomer() != null ? 
                invoice.getCustomer().getPhoneNumber() : "");
        customerPhone.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        customerPhone.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        customerPanel.add(customerTitle);
        customerPanel.add(Box.createVerticalStrut(5));
        customerPanel.add(customerName);
        customerPanel.add(Box.createVerticalStrut(3));
        customerPanel.add(customerPhone);
        
        // Employee info
        JPanel employeePanel = new JPanel();
        employeePanel.setLayout(new BoxLayout(employeePanel, BoxLayout.Y_AXIS));
        employeePanel.setBackground(Color.WHITE);
        
        JLabel employeeTitle = new JLabel(bundle.getString("txtInvoiceEmployee"));
        employeeTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        employeeTitle.setForeground(new Color(100, 100, 100));
        employeeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel employeeName = new JLabel(invoice.getEmployee().getFullName());
        employeeName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        employeeName.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel employeeEmail = new JLabel(invoice.getEmployee().getEmail());
        employeeEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        employeeEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        employeePanel.add(employeeTitle);
        employeePanel.add(Box.createVerticalStrut(5));
        employeePanel.add(employeeName);
        employeePanel.add(Box.createVerticalStrut(3));
        employeePanel.add(employeeEmail);
        
        panel.add(customerPanel);
        panel.add(employeePanel);
        
        return panel;
    }
    
    private void createProductsTable() {
        String[] columnNames = {
            bundle.getString("txtNo"),
            bundle.getString("txtProductID"),
            bundle.getString("txtProductName"),
            bundle.getString("txtPrice"),
            bundle.getString("txtQuantity"),
            bundle.getString("txtInvoiceTotalAmountProduct")
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableProducts = new JTable(tableModel);
        tableProducts.setRowHeight(40);
        tableProducts.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableProducts.setShowGrid(false);
        tableProducts.setIntercellSpacing(new Dimension(0, 0));
        tableProducts.setShowHorizontalLines(true);
        tableProducts.setGridColor(new Color(240, 240, 240));
        
        // Header styling
        tableProducts.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableProducts.getTableHeader().setBackground(new Color(250, 250, 250));
        tableProducts.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        
        // Áp dụng TableStyleUtil
        tableSorter = TableStyleUtil.applyDefaultStyle(tableProducts);
        
        // Sử dụng InvoiceDetailController để cập nhật bảng
        populateProductsTable();
    }
    
    private void populateProductsTable() {
        tableModel.setRowCount(0);
        
        int index = 1;
        for (InvoiceDetail detail : invoiceDetails) {
            Object[] row = {
                index++,
                detail.getProduct().getProductId(),
                detail.getProduct().getProductName(),
                numberFormat.format(detail.getUnitPrice()) + " ₫",
                detail.getQuantity(),
                numberFormat.format(detail.getUnitPrice().multiply(java.math.BigDecimal.valueOf(detail.getQuantity()))) + " ₫"
            };
            tableModel.addRow(row);
        }
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        JPanel summaryContent = new JPanel();
        summaryContent.setLayout(new BoxLayout(summaryContent, BoxLayout.Y_AXIS));
        summaryContent.setBackground(new Color(250, 250, 250));
        summaryContent.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // Tổng tiền hàng
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (InvoiceDetail detail : invoiceDetails) {
            totalAmount = totalAmount.add(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
        }
        JPanel totalPanel = createSummaryRow(bundle.getString("txtInvoiceTotalAmount"),
                numberFormat.format(totalAmount) + " ₫", false);

        // Giảm giá
        JPanel discountPanel = createSummaryRow(bundle.getString("txtInvoiceTotalDiscount"), 
                "- " + numberFormat.format(invoice.getDiscountAmount()) + " ₫", false);
        
        // Thành tiền
        JPanel finalPanel = createSummaryRow(bundle.getString("txtTotalAmount"), 
                numberFormat.format(invoice.getTotalAmount()) + " ₫", true);
        
        
        summaryContent.add(totalPanel);
        summaryContent.add(Box.createVerticalStrut(8));
        summaryContent.add(discountPanel);
        summaryContent.add(Box.createVerticalStrut(8));
        summaryContent.add(new JSeparator());
        summaryContent.add(Box.createVerticalStrut(8));
        summaryContent.add(finalPanel);

        panel.add(summaryContent, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createSummaryRow(String label, String value, boolean isBold) {
        JPanel panel = new JPanel(new BorderLayout(40, 0));
        panel.setBackground(new Color(250, 250, 250));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", isBold ? Font.BOLD : Font.PLAIN, 14));
        
        JLabel valueComponent = new JLabel(value);
        Font valueFont = new Font("Segoe UI", isBold ? Font.BOLD : Font.PLAIN, isBold ? 16 : 14);
        valueComponent.setFont(valueFont);
        if (isBold) {
            valueComponent.setForeground(new Color(51, 153, 255));
        }
        
        panel.add(labelComponent, BorderLayout.WEST);
        panel.add(valueComponent, BorderLayout.EAST);
        
        return panel;
    }
    
    private void printInvoice() {
        try {
            boolean success = ExportInvoice.exportPDF(invoice, invoice.getPaymentMethod());
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        bundle.getString("txtNotification"), 
                        bundle.getString("txtNotification"), 
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "In hóa đơn thất bại!", 
                        bundle.getString("txtNotification"), 
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "Lỗi khi in hóa đơn: " + e.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}