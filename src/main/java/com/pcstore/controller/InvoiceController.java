package com.pcstore.controller;

import com.pcstore.model.Customer;
import com.pcstore.model.Employee;
import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Product;
import com.pcstore.model.enums.InvoiceStatusEnum;
import com.pcstore.model.enums.PaymentMethodEnum;
import com.pcstore.service.InvoiceService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.ExportInvoice;
import com.pcstore.utils.LocaleManager;
import com.pcstore.view.DashboardForm;
import com.pcstore.view.InvoiceForm;
import com.pcstore.view.PayForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.awt.event.*;

/**
 * Controller quản lý các chức năng liên quan đến hóa đơn
 */
public class InvoiceController {
    // Singleton instance
    private static InvoiceController instance;
    
    private InvoiceService invoiceService;
    private InvoiceForm invoiceForm;
    private InvoiceDetailController invoiceDetailController;
    
    private List<Invoice> invoiceList;
    private Invoice currentInvoice;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final NumberFormat currencyFormatter = LocaleManager.getInstance().getNumberFormatter();
    private TableRowSorter<TableModel> tableSorter;
    
    /**
     * Lấy instance duy nhất của controller (Singleton pattern)
     * @param invoiceForm Form hiển thị hóa đơn
     * @return InvoiceController instance
     */
    public static synchronized InvoiceController getInstance(InvoiceForm invoiceForm) {
        if (instance == null) {
            instance = new InvoiceController(invoiceForm);
        } else if (instance.invoiceForm != invoiceForm) {
            // Cập nhật form nếu khác với instance hiện tại
            instance.invoiceForm = invoiceForm;
            instance.setupEventListeners();
            instance.setupTableSorter();
            instance.loadAllInvoices();
        }
        return instance;
    }
    
    /**
     * Khởi tạo controller với form
     * @param invoiceForm Form hiển thị hóa đơn
     */
    private InvoiceController(InvoiceForm invoiceForm) {
        try {
            this.invoiceForm = invoiceForm;
            this.invoiceService = ServiceFactory.getInvoiceService();
            this.invoiceDetailController = new InvoiceDetailController();
            
            // Khởi tạo danh sách hóa đơn
            loadAllInvoices();
            
            // Thiết lập các sự kiện cho form
            setupEventListeners();
            
            // Thiết lập bảng có thể sắp xếp
            setupTableSorter();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khởi tạo controller: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Thiết lập các sự kiện cho form
     */
    private void setupEventListeners() {
        // Thiết lập sự kiện khi chọn hóa đơn
        invoiceForm.getTableInvoice().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = invoiceForm.getTableInvoice().getSelectedRow();
                if (selectedRow >= 0) {
                    // Chuyển đổi chỉ số hàng từ view sang model nếu đang sắp xếp
                    int modelRow = invoiceForm.getTableInvoice().convertRowIndexToModel(selectedRow);
                    int invoiceId = Integer.parseInt(invoiceForm.getTableInvoice().getModel().getValueAt(modelRow, 2).toString());
                    loadInvoiceDetails(invoiceId);
                }
            }
        });
        
        // Thiết lập sự kiện xuất Excel
        invoiceForm.getBtnExportExcel().addActionListener(e -> {
            // exportAllInvoicesToExcel();
        });
        
        // Thiết lập sự kiện xuất hóa đơn (PDF)
        invoiceForm.getBtnExportInvoice().addActionListener(e -> {
            printSelectedInvoice();
        });
        
        // Thiết lập sự kiện xóa hóa đơn
        invoiceForm.getBtnDeleteInvoice().addActionListener(e -> {
            deleteSelectedInvoice();
        });
        
        // Thiết lập sự kiện tìm kiếm
        invoiceForm.getTxtSearchField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchInvoices(invoiceForm.getTxtSearchField().getText());
            }
        });

        invoiceForm.getBbtnSearch().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                searchInvoices(invoiceForm.getTxtSearchField().getText());
            }
        });

        invoiceForm.getBtnPaymentInvoice().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                paymentInvoice();
            }
        });
    }
    
    /**
     * Thiết lập bảng có thể sắp xếp
     */
    private void setupTableSorter() {
        // Tạo row sorter cho bảng hóa đơn
        tableSorter = new TableRowSorter<>(invoiceForm.getTableInvoice().getModel());
        invoiceForm.getTableInvoice().setRowSorter(tableSorter);
        
        // Thiết lập một số cột không thể sắp xếp (như cột checkbox)
        tableSorter.setSortable(0, false); // Cột checkbox không sắp xếp được
        
        // comparator (cột 5 - Tổng tiền)
        tableSorter.setComparator(5, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                try {
                    String v1 = s1.replaceAll("\\.", "");
                    String v2 = s2.replaceAll("\\.", "");
                    
                    double d1 = Double.parseDouble(v1);
                    double d2 = Double.parseDouble(v2);
                    
                    return Double.compare(d1, d2);
                } catch (Exception e) {
                    return s1.compareTo(s2);
                }
            }
        });
        
        //Comparator cột ngày (cột 3)
        tableSorter.setComparator(3, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                try {
                    LocalDateTime d1 = LocalDateTime.parse(s1, dateFormatter);
                    LocalDateTime d2 = LocalDateTime.parse(s2, dateFormatter);
                    return d1.compareTo(d2);
                } catch (Exception e) {
                    return s1.compareTo(s2);
                }
            }
        });
    }
    
    
    public void loadAllInvoices() {
        try {
            invoiceList = invoiceService.findAllInvoices();
            updateInvoiceTable(invoiceList);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi tải danh sách hóa đơn: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Cập nhật bảng hiển thị hóa đơn
     * @param invoices Danh sách hóa đơn cần hiển thị
     */
    private void updateInvoiceTable(List<Invoice> invoices) {
        DefaultTableModel model = (DefaultTableModel) invoiceForm.getTableInvoice().getModel();
        model.setRowCount(0);
        
        int count = 0;
        for (Invoice invoice : invoices) {
            count++;
            Object[] row = new Object[11]; 
            row[0] = false; 
            row[1] = count;
            row[2] = invoice.getInvoiceId();
            row[3] = invoice.getInvoiceDate() != null ? invoice.getInvoiceDate().format(dateFormatter) : "";
            row[4] = invoice.getEmployee() != null ? invoice.getEmployee().getFullName() : "";
            row[5] = invoice.getDiscountAmount() != null ? currencyFormatter.format(invoice.getDiscountAmount()) : "";
            row[6] = invoice.getTotalAmount() != null ? currencyFormatter.format(invoice.getTotalAmount()) : "";
            row[7] = getPaymentMethodDisplay(invoice.getPaymentMethod());
            row[8] = invoice.getCustomer() != null ? invoice.getCustomer().getFullName() : "";
            row[9] = getStatusDisplay(invoice.getStatus());
            row[10] = ""; // Ghi chú
            
            model.addRow(row);
        }
    }
    
    /**
     * Tải chi tiết hóa đơn theo ID
     * @param invoiceId ID của hóa đơn cần tải chi tiết
     */
    public void loadInvoiceDetails(int invoiceId) {
        try {
            Optional<Invoice> invoiceOpt = invoiceService.findInvoiceById(invoiceId);
            if (invoiceOpt.isPresent()) {
                currentInvoice = invoiceOpt.get();
                invoiceDetailController.updateInvoiceDetailTable(currentInvoice.getInvoiceDetails(), 
                        invoiceForm.getTableInvoiceDetail());
            } else {
                // Nếu không tìm thấy hóa đơn, xóa bảng chi tiết
                DefaultTableModel model = (DefaultTableModel) invoiceForm.getTableInvoiceDetail().getModel();
                model.setRowCount(0);
                currentInvoice = null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi tải chi tiết hóa đơn: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Tìm kiếm hóa đơn theo từ khóa
     * @param keyword Từ khóa tìm kiếm
     */
    public void searchInvoices(String keyword) {
        if (tableSorter == null) {
            return;
        }
        
        if (keyword == null || keyword.trim().isEmpty()) {
            tableSorter.setRowFilter(null); // Hiển thị tất cả nếu không có từ khóa
        } else {
            try {
                // Tìm kiếm trên nhiều cột (ID, nhân viên, khách hàng, trạng thái)
                RowFilter<Object, Object> filter = RowFilter.orFilter(Arrays.asList(
                    RowFilter.regexFilter("(?i)" + Pattern.quote(keyword), 2),  // ID hóa đơn
                    RowFilter.regexFilter("(?i)" + Pattern.quote(keyword), 4),  // Nhân viên 
                    RowFilter.regexFilter("(?i)" + Pattern.quote(keyword), 7),  // Khách hàng
                    RowFilter.regexFilter("(?i)" + Pattern.quote(keyword), 8)   // Trạng thái
                ));
                tableSorter.setRowFilter(filter);
            } catch (Exception e) {
                tableSorter.setRowFilter(null);
                JOptionPane.showMessageDialog(null, "Lỗi khi tìm kiếm: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Xuất danh sách hóa đơn ra file Excel
     */
    // public void exportAllInvoicesToExcel() {
    //     try {
    //         boolean success = ExportBill.exportAllInvoicesToExcel(invoiceList);
    //         if (success) {
    //             JOptionPane.showMessageDialog(null, "Xuất Excel thành công!", 
    //                     "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    //         } else {
    //             JOptionPane.showMessageDialog(null, "Xuất Excel thất bại!", 
    //                     "Lỗi", JOptionPane.ERROR_MESSAGE);
    //         }
    //     } catch (Exception e) {
    //         JOptionPane.showMessageDialog(null, "Lỗi khi xuất Excel: " + e.getMessage(), 
    //                 "Lỗi", JOptionPane.ERROR_MESSAGE);
    //     }
    // }
    
    /**
     * In hóa đơn được chọn
     */
    public void printSelectedInvoice() {
        int selectedRow = invoiceForm.getTableInvoice().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn hóa đơn cần in!", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            // Chuyển đổi chỉ số hàng từ view sang model nếu đang sắp xếp
            int modelRow = invoiceForm.getTableInvoice().convertRowIndexToModel(selectedRow);
            int invoiceId = Integer.parseInt(invoiceForm.getTableInvoice().getModel().getValueAt(modelRow, 2).toString());
            
            Optional<Invoice> invoiceOpt = invoiceService.findInvoiceById(invoiceId);
            if (invoiceOpt.isPresent()) {
                Invoice invoice = invoiceOpt.get();
                
                if (invoice.getStatus() != InvoiceStatusEnum.COMPLETED) {
                    JOptionPane.showMessageDialog(null, "Hóa đơn chưa hoàn thành thanh toán!", 
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                
                boolean success = ExportInvoice.exportPDF(invoice, invoice.getPaymentMethod()); // Không cần payment object vì chỉ in lại hóa đơn
                
                if (success) {
                    JOptionPane.showMessageDialog(null, "In hóa đơn thành công!", 
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "In hóa đơn thất bại!", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi in hóa đơn: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    /**
     * Xử lý thanh toán hóa đơn cho những hóa đơn Đang chờ xử lý
     */
    public void paymentInvoice() {
        int selectedRow = invoiceForm.getTableInvoice().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn hóa đơn cần thanh toán!", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Chuyển đổi chỉ số hàng từ view sang model nếu đang sắp xếp
        int modelRow = invoiceForm.getTableInvoice().convertRowIndexToModel(selectedRow);
        int invoiceId = Integer.parseInt(invoiceForm.getTableInvoice().getModel().getValueAt(modelRow, 2).toString());
        
        Optional<Invoice> invoiceOpt = invoiceService.findInvoiceById(invoiceId);
        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            if (invoice.getStatus() == InvoiceStatusEnum.COMPLETED || 
                    invoice.getStatus() == InvoiceStatusEnum.PAID) {
                JOptionPane.showMessageDialog(null, "Hóa đơn đã được thanh toán!", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // form thanh toán hóa đơn
            DashboardForm dashboardForm = DashboardForm.getInstance();
            PayForm paymentForm = new PayForm(dashboardForm, true);
            PaymentController paymentController = new PaymentController(paymentForm, invoice);
            paymentController.showPaymentForm();

            if (paymentController.isPaymentSuccessful()) {
                invoice.setStatus(InvoiceStatusEnum.COMPLETED);
                invoice.setPaymentMethod(paymentController.getCurrentPayment().getPaymentMethod());

                //Cập nhật chi tiết hóa đơn
                invoiceService.updateInvoice(invoice);
                
                loadAllInvoices();
                JOptionPane.showMessageDialog(null, "Thanh toán hóa đơn thành công!", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
                boolean success = false;
                
                try {
                    success = ExportInvoice.exportPDF(invoice, paymentController.getCurrentPayment());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Lỗi khi in hóa đơn: " + e.getMessage(), 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
                        
                if (success) {
                    JOptionPane.showMessageDialog(null, "In hóa đơn thành công!", 
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "In hóa đơn thất bại!", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Không tìm thấy hóa đơn!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xóa các hóa đơn được tích chọn
     */
    public void deleteSelectedInvoice() {
        DefaultTableModel model = (DefaultTableModel) invoiceForm.getTableInvoice().getModel();
        int rowCount = model.getRowCount();
        List<Integer> invoiceIdsToDelete = new ArrayList<>();
        
        // Thu thập tất cả ID hóa đơn được tích chọn
        for (int i = 0; i < rowCount; i++) {
            Boolean isChecked = (Boolean) model.getValueAt(i, 0);
            if (isChecked) {
                int modelRow = invoiceForm.getTableInvoice().convertRowIndexToModel(i);
                int invoiceId = Integer.parseInt(model.getValueAt(modelRow, 2).toString());
                invoiceIdsToDelete.add(invoiceId);
            }
        }
        
        // Nếu không có hóa đơn nào được tích chọn
        if (invoiceIdsToDelete.isEmpty()) {
            JOptionPane.showMessageDialog(null, 
                    "Vui lòng tích chọn ít nhất một hóa đơn để xóa!", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Hiển thị hộp thoại xác nhận
        String message = "Bạn có chắc chắn muốn xóa " + invoiceIdsToDelete.size() + 
                " hóa đơn đã chọn?";
        int option = JOptionPane.showConfirmDialog(null, message, 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            int successCount = 0;
            int failCount = 0;
            List<String> errorMessages = new ArrayList<>();
            
            for (Integer invoiceId : invoiceIdsToDelete) {
                try {
                    // Kiểm tra trạng thái của hóa đơn trước khi xóa
                    Optional<Invoice> invoiceOpt = invoiceService.findInvoiceById(invoiceId);
                    if (invoiceOpt.isPresent()) {
                        Invoice invoice = invoiceOpt.get();
                        if (invoice.getStatus() == InvoiceStatusEnum.PAID || 
                                invoice.getStatus() == InvoiceStatusEnum.DELIVERED) {
                            errorMessages.add("Hóa đơn #" + invoiceId + ": Không thể xóa hóa đơn đã thanh toán hoặc đã giao hàng!");
                            failCount++;
                            continue;
                        }
                    }
                    
                    boolean success = invoiceService.deleteInvoice(invoiceId);
                    if (success) {
                        successCount++;
                    } else {
                        failCount++;
                        errorMessages.add("Hóa đơn #" + invoiceId + ": Xóa thất bại!");
                    }
                } catch (Exception e) {
                    failCount++;
                    errorMessages.add("Hóa đơn #" + invoiceId + ": " + e.getMessage());
                }
            }
            
            // Hiển thị kết quả
            StringBuilder resultMessage = new StringBuilder();
            if (successCount > 0) {
                resultMessage.append("Đã xóa thành công ").append(successCount).append(" hóa đơn.\n");
            }
            
            if (failCount > 0) {
                resultMessage.append("Không thể xóa ").append(failCount).append(" hóa đơn.\n\n");
                resultMessage.append("Chi tiết lỗi:\n");
                
                for (String error : errorMessages) {
                    resultMessage.append("- ").append(error).append("\n");
                }
            }
            
            JOptionPane.showMessageDialog(null, resultMessage.toString(), 
                    (failCount > 0) ? "Kết quả xóa hóa đơn" : "Thành công", 
                    (failCount > 0) ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
            
            // Cập nhật lại danh sách hóa đơn
            loadAllInvoices();
        }
    }
    
    /**
     * Tạo hóa đơn mới
     * @param customer Khách hàng
     * @param employee Nhân viên
     * @return Hóa đơn mới đã được tạo
     */
    public Invoice createInvoice(Customer customer, Employee employee) {
        if (customer == null) {
            throw new IllegalArgumentException(ErrorMessage.INVOICE_CUSTOMER_NULL);
        }
        if (employee == null) {
            throw new IllegalArgumentException(ErrorMessage.INVOICE_EMPLOYEE_NULL);
        }
        
        try {
            Invoice invoice = new Invoice();
            invoice.setCustomer(customer);
            invoice.setEmployee(employee);
            invoice.setInvoiceDate(LocalDateTime.now());
            invoice.setStatus(InvoiceStatusEnum.PENDING);
            invoice.setTotalAmount(BigDecimal.ZERO);
            
            // Lưu hóa đơn vào cơ sở dữ liệu
            Invoice savedInvoice = invoiceService.createInvoice(invoice);
            
            // Cập nhật lại danh sách hóa đơn
            loadAllInvoices();
            
            return savedInvoice;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi tạo hóa đơn mới: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Cập nhật hóa đơn
     * @param invoice Hóa đơn cần cập nhật
     * @return Hóa đơn đã được cập nhật
     */
    public Invoice updateInvoice(Invoice invoice) {
        try {
            Invoice updatedInvoice = invoiceService.updateInvoice(invoice);
            
            // Cập nhật lại danh sách hóa đơn
            loadAllInvoices();
            
            return updatedInvoice;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật hóa đơn: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Xử lý thanh toán hóa đơn
     * @param invoice Hóa đơn cần thanh toán
     * @param paymentMethod Phương thức thanh toán
     * @return true nếu thanh toán thành công, ngược lại là false
     */
    public boolean processPayment(Invoice invoice, PaymentMethodEnum paymentMethod) {
        try {
            // Cập nhật thông tin thanh toán
            invoice.setStatus(InvoiceStatusEnum.PAID);
            invoice.setPaymentMethod(paymentMethod);
            invoice.setUpdatedAt(LocalDateTime.now());
            
            // Cập nhật hóa đơn
            Invoice updatedInvoice = invoiceService.updateInvoice(invoice);
            
            // Cập nhật lại danh sách hóa đơn
            loadAllInvoices();
            
            return updatedInvoice != null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi xử lý thanh toán: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Cập nhật trạng thái hóa đơn
     * @param invoiceId ID của hóa đơn
     * @param status Trạng thái mới
     * @return true nếu cập nhật thành công, ngược lại là false
     */
    public boolean updateInvoiceStatus(int invoiceId, InvoiceStatusEnum status) {
        try {
            Optional<Invoice> invoiceOpt = invoiceService.findInvoiceById(invoiceId);
            if (invoiceOpt.isPresent()) {
                Invoice invoice = invoiceOpt.get();
                invoice.setStatus(status);
                invoice.setUpdatedAt(LocalDateTime.now());
                
                // Cập nhật hóa đơn
                Invoice updatedInvoice = invoiceService.updateInvoice(invoice);
                
                // Cập nhật lại danh sách hóa đơn
                loadAllInvoices();
                
                return updatedInvoice != null;
            }
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật trạng thái hóa đơn: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Lấy chuỗi hiển thị cho phương thức thanh toán
     * @param paymentMethod Phương thức thanh toán
     * @return Chuỗi hiển thị
     */
    private String getPaymentMethodDisplay(PaymentMethodEnum paymentMethod) {
        if (paymentMethod == null) {
            return "Chưa thanh toán";
        }
        
        switch (paymentMethod) {
            case CASH: return "Tiền mặt";
            case CREDIT_CARD: return "Thẻ tín dụng";
            case BANK_TRANSFER: return "Chuyển khoản";
            case E_WALLET: return "Ví điện tử";
            case MOMO: return "MoMo";
            case ZALOPAY: return "ZaloPay";
            default: return "Khác";
        }
    }
    
    /**
     * Lấy chuỗi hiển thị cho trạng thái hóa đơn
     * @param status Trạng thái hóa đơn
     * @return Chuỗi hiển thị
     */
    private String getStatusDisplay(InvoiceStatusEnum status) {
        if (status == null) {
            return "Chưa xác định";
        }
        
        switch (status) {
            case PENDING: return "Đang xử lý";
            case PAID: return "Đã thanh toán";
            case CANCELLED: return "Đã hủy";
            case DELIVERED: return "Đã giao hàng";
            case COMPLETED: return "Hoàn thành";
            case PROCESSING: return "Đang xử lý";
            default: return "Khác";
        }
    }
    
    /**
     * Lấy InvoiceDetailController
     * @return InvoiceDetailController
     */
    public InvoiceDetailController getInvoiceDetailController() {
        return invoiceDetailController;
    }
    
    /**
     * Lấy danh sách hóa đơn hiện tại
     * @return Danh sách hóa đơn
     */
    public List<Invoice> getInvoiceList() {
        return invoiceList;
    }
    
    /**
     * Lấy hóa đơn hiện tại đang được xem chi tiết
     * @return Hóa đơn hiện tại
     */
    public Invoice getCurrentInvoice() {
        return currentInvoice;
    }
}
