package com.pcstore.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.pcstore.model.Customer;
import com.pcstore.model.Invoice;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.TableUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerInvoicesDialog extends JDialog {
    
    private JTable tableInvoices;
    private DefaultTableModel tableModel;
    private TableRowSorter tableSorter;
    private final DateTimeFormatter dateFormatter = LocaleManager.getInstance().getDateTimeFormatter();
    private final NumberFormat numberFormat = LocaleManager.getInstance().getNumberFormatter();
    private final Customer customer;
    private final ResourceBundle bundle = LocaleManager.getInstance().getResourceBundle();
    
    public CustomerInvoicesDialog(Frame owner, Customer customer, List<Invoice> invoices) {
        super(owner, true);
        this.customer = customer;
        initComponents(invoices);
        setSize(900, 550);
        setLocationRelativeTo(owner);
    }
    
    private void initComponents(List<Invoice> invoices) {
        setTitle(bundle.getString("txtCustomerInvoices"));
        setBackground(Color.WHITE);
        
        // Tạo panel chính với border layout
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // ===== HEADER PANEL =====
        // Card hiển thị thông tin khách hàng
        JPanel customerCard = createCustomerInfoCard();
        
        // ===== CONTENT PANEL =====
        // Panel chứa bảng hóa đơn
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        
        // Label tiêu đề
        JLabel titleLabel = new JLabel(bundle.getString("txtCustomerInvoices"));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        tablePanel.add(titleLabel, BorderLayout.NORTH);
        
        // Tạo bảng với border bo tròn và bóng đổ
        createInvoiceTable(invoices);
        JScrollPane scrollPane = new JScrollPane(tableInvoices);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Panel chứa bảng có viền và góc bo tròn
        JPanel roundedTablePanel = new JPanel(new BorderLayout());
        roundedTablePanel.setBackground(Color.WHITE);
        roundedTablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(0, 0, 0, 0)));
        roundedTablePanel.add(scrollPane);
        
        tablePanel.add(roundedTablePanel, BorderLayout.CENTER);
        
        // ===== FOOTER PANEL =====
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        
        // Nút đóng
        JButton btnClose = new JButton(bundle.getString("btnClose"));
        btnClose.setPreferredSize(new Dimension(120, 40));
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setBackground(new Color(51, 153, 255));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        
        // Nút xem chi tiết
        JButton btnViewDetail = new JButton(bundle.getString("btnDetail"));
        btnViewDetail.setPreferredSize(new Dimension(120, 40));
        btnViewDetail.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnViewDetail.setBackground(new Color(240, 240, 240));
        btnViewDetail.setForeground(new Color(50, 50, 50));
        btnViewDetail.setFocusPainted(false);
        btnViewDetail.setBorderPainted(false);
        btnViewDetail.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewDetail.addActionListener(e -> viewInvoiceDetail());
        
        footerPanel.add(btnViewDetail);
        footerPanel.add(Box.createHorizontalStrut(10));
        footerPanel.add(btnClose);
        
        // Thêm các panel vào panel chính
        mainPanel.add(customerCard, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Thêm panel chính vào dialog
        setContentPane(mainPanel);
    }
    
    private JPanel createCustomerInfoCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(245, 247, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 20, 15, 20)));
        
        // Tên khách hàng
        JLabel nameLabel = new JLabel(customer.getFullName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Thông tin liên hệ
        JPanel contactPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        contactPanel.setBackground(new Color(245, 247, 250));
        contactPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel phoneLabel = new JLabel(customer.getPhoneNumber());
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel emailLabel = new JLabel(customer.getEmail() != null ? " | " + customer.getEmail() : "");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        contactPanel.add(phoneLabel);
        contactPanel.add(emailLabel);
        
        // Panel chứa các thông tin khác
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(1, 3, 20, 0));
        detailsPanel.setBackground(new Color(245, 247, 250));
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Mã khách hàng
        JPanel idPanel = createInfoPanel(bundle.getString("txtCustomerID"), customer.getCustomerId());
        
        // Điểm tích lũy
        JPanel pointsPanel = createInfoPanel(bundle.getString("lbPoint"), 
                numberFormat.format(customer.getPoints()));
        
        // Ngày tạo
        JPanel datePanel = createInfoPanel(bundle.getString("lbCreateAt"), 
                customer.getCreatedAt() != null ? 
                        customer.getCreatedAt().format(dateFormatter) : "");
        
        detailsPanel.add(idPanel);
        detailsPanel.add(pointsPanel);
        detailsPanel.add(datePanel);
        
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(contactPanel);
        card.add(Box.createVerticalStrut(15));
        card.add(detailsPanel);
        
        return card;
    }
    
    private JPanel createInfoPanel(String title, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 247, 250));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(120, 120, 120));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(3));
        panel.add(valueLabel);
        
        return panel;
    }
    
    private void createInvoiceTable(List<Invoice> invoices) {
        String[] columnNames = {
            bundle.getString("txtNo"),
            bundle.getString("txtInvoiceId"),
            bundle.getString("txtCreatedAt"),
            bundle.getString("txtTotalAmount"),
            bundle.getString("txtPaymentMethod"),
            bundle.getString("txtStatus")
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableInvoices = new JTable(tableModel);
        tableInvoices.setRowHeight(40);
        tableInvoices.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableInvoices.setShowGrid(false);
        tableInvoices.setIntercellSpacing(new Dimension(0, 0));
        tableInvoices.setShowHorizontalLines(true);
        tableInvoices.setGridColor(new Color(240, 240, 240));
        
        // Header styling
        tableInvoices.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableInvoices.getTableHeader().setBackground(new Color(250, 250, 250));
        tableInvoices.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        
        // Áp dụng TableStyleUtil
        tableSorter = TableUtils.applyDefaultStyle(tableInvoices);
        
        // Double-click to view details
        tableInvoices.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewInvoiceDetail();
                }
            }
        });
        
        // Thêm dữ liệu vào bảng
        updateTable(invoices);
    }
    
    private void updateTable(List<Invoice> invoices) {
        tableModel.setRowCount(0);
        
        if (invoices.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                bundle.getString("txtNoInvoices"), 
                bundle.getString("txtNotification"), 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Integer index = 1;
        for (Invoice invoice : invoices) {
            Object[] row = {
                index++,
                invoice.getInvoiceId(),
                invoice.getCreatedAt() != null ? invoice.getCreatedAt().format(dateFormatter) : "",
                numberFormat.format(invoice.getTotalAmount()) + " ₫",
                invoice.getPaymentMethod() != null ? invoice.getPaymentMethod().getDisplayName() : "",
                invoice.getStatus() != null ? invoice.getStatus().getDisplayName() : ""
            };
            tableModel.addRow(row);
        }

        // Áp dụng màu sắc cho cột trạng thái
        TableUtils.applyInvoiceTableStyle(tableInvoices, 5);
    }
    
    private void viewInvoiceDetail() {
        int selectedRow = tableInvoices.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = tableInvoices.convertRowIndexToModel(selectedRow);
            String invoiceId = tableInvoices.getModel().getValueAt(modelRow, 1).toString();
            
            // Mở dialog chi tiết hóa đơn
            InvoiceDetailDialog detailDialog = new InvoiceDetailDialog(
                    this,
                    invoiceId);
            detailDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn hóa đơn để xem chi tiết.",
                    // ErrorMessage.SELECT_INVOICE,
                    ErrorMessage.ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}