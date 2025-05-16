package com.pcstore.controller;

import com.pcstore.model.Customer;
import com.pcstore.model.Invoice;
import com.pcstore.service.CustomerService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.utils.ButtonUtils;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.JExcel;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.TableStyleUtil;
import com.pcstore.view.CustomerForm;
import com.pcstore.view.CustomerInvoicesDialog;
import com.pcstore.view.DashboardForm;

import javax.swing.*;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Controller để quản lý các thao tác liên quan đến khách hàng
 */
public class CustomerController {
    // Singleton instance
    private static CustomerController instance;
    
    // Services
    private CustomerService customerService;
    
    // UI related
    private CustomerForm customerForm;
    private List<Customer> customerList;
    private Customer selectedCustomer;
    private final DateTimeFormatter dateFormatter = LocaleManager.getInstance().getDateTimeFormatter();
    private final NumberFormat numberFormat = LocaleManager.getInstance().getNumberFormatter();
    private TableRowSorter<TableModel> customerTableSorter;
    
    //Lấy file vi_vn.properties
    private Properties prop = LocaleManager.getInstance().getProperties();

    private boolean isAddingNew;
    
    /**
     * Lấy instance duy nhất của controller (Singleton pattern)
     * @param customerForm Form hiển thị khách hàng
     * @return CustomerController instance
     */
    public static synchronized CustomerController getInstance(CustomerForm customerForm) {
        if (instance == null) {
            instance = new CustomerController(customerForm);
        } else if (instance.customerForm != customerForm) {
            // Cập nhật form nếu khác với instance hiện tại
            instance.customerForm = customerForm;
            instance.setupEventListeners();
            instance.setupTableStyle();
            instance.loadAllCustomers();
        }
        return instance;
    }
    
    /**
     * Khởi tạo controller với form (Giao diện người dùng)
     * @param customerForm Form hiển thị khách hàng
     */
    public CustomerController(CustomerForm customerForm) {
        try {
            this.customerService = ServiceFactory.getCustomerService();
            this.customerForm = customerForm;
            this.customerList = new ArrayList<>();

            setupComboBox();
            setupEventListeners();
            setupTableStyle();
            loadAllCustomers();
            clearForm();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                    ErrorMessage.CUSTOMER_CONTROLLER_INIT_ERROR.formatted(e.getMessage()), 
                    ErrorMessage.ERROR_TITLE, 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Thiết lập các sự kiện cho form
     */
    private void setupEventListeners() {
        if (customerForm == null) return;
        
        customerForm.getTableCustomers().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = customerForm.getTableCustomers().getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = customerForm.getTableCustomers().convertRowIndexToModel(selectedRow);
                    String customerId = customerForm.getTableCustomers().getModel().getValueAt(modelRow, 0).toString();
                    loadCustomerDetails(customerId);
                }
            }
        });
        
        //thêm khách hàng
        customerForm.getBtnAddCustomer().addActionListener(e -> {
            addNewCustomer();
        });
        
        // cập nhật khách hàng
        customerForm.getBtnUpdate().addActionListener(e -> {
            updateSelectedCustomer();
        });
        
        // xóa khách hàng
        customerForm.getBtnDeleteCustomer().addActionListener(e -> {
            deleteSelectedCustomer();
        });
        
        //làm mới
        customerForm.getBtnRefresh().addActionListener(e -> {
            loadAllCustomers();
            isAddingNew = false;
            clearForm();
        });
        
        //xuất Excel
        customerForm.getBtnExportExcel().addActionListener(e -> {
            exportToExcel();
        });
        
        //tìm kiếm
        customerForm.getTextFieldSearch().getTxtSearchField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchCustomers(customerForm.getTextFieldSearch().getTxtSearchField().getText());
            }
        });
        
        customerForm.getTextFieldSearch().getBtnSearch().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                searchCustomers(customerForm.getTextFieldSearch().getTxtSearchField().getText());
            }
        });
        
        //sắp xếp
        customerForm.getCbbSortCustomer().addActionListener(e -> {
            applySorting();
        });
        
        customerForm.getCbbSort().addActionListener(e -> {
            applySorting();
        });

        //Nếu đang ở trạng thái đang thêm mà nhấn esc thì sẽ thoát thêm
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && isAddingNew) {
                    handleEscapeKey();
                    return true;
                }
            }
            return false; 
        });       

        customerForm.getTableCustomers().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {  // Double-click
                    int selectedRow = customerForm.getTableCustomers().getSelectedRow();
                    if (selectedRow >= 0) {
                        int modelRow = customerForm.getTableCustomers().convertRowIndexToModel(selectedRow);
                        String customerId = customerForm.getTableCustomers().getModel()
                                .getValueAt(modelRow, 0).toString();
                        
                        Optional<Customer> customerOpt = findCustomerById(customerId);
                        if (customerOpt.isPresent()) {
                            Customer customer = customerOpt.get();
                            showCustomerInvoices(customer);
                        }
                    }
                }
            }
        });
    }
    
    /**
     * Setup combobox cho các trường sắp xếp
     */
    private void setupComboBox() {
        if (customerForm == null) return;
        customerForm.getCbbSortCustomer().removeAllItems();
        customerForm.getCbbSort().removeAllItems();

        customerForm.getCbbSortCustomer().addItem(prop.getProperty("txtNone"));
        customerForm.getCbbSortCustomer().addItem(prop.getProperty("txtCustomerName"));
        customerForm.getCbbSortCustomer().addItem(prop.getProperty("txtCustomerPoint"));
        
        customerForm.getCbbSort().addItem(prop.getProperty("txtAscending"));
        customerForm.getCbbSort().addItem(prop.getProperty("txtDescending"));
    }

    /**
     * Thiết lập style cho bảng
     */
    private void setupTableStyle() {
        if (customerForm == null) return;
        
        customerTableSorter = TableStyleUtil.applyDefaultStyle(customerForm.getTableCustomers());
        
        // cột điểm tích lũy (index 4)
        customerTableSorter.setComparator(4, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                try {
                    String s1 = o1.toString().replaceAll("[^\\d]", "");
                    String s2 = o2.toString().replaceAll("[^\\d]", "");
                    
                    int n1 = s1.isEmpty() ? 0 : Integer.parseInt(s1);
                    int n2 = s2.isEmpty() ? 0 : Integer.parseInt(s2);
                    
                    return Integer.compare(n1, n2);
                } catch (Exception e) {
                    return o1.toString().compareTo(o2.toString());
                }
            }
        });
    }
    
    /**
     * Tải danh sách tất cả khách hàng
     */
    public void loadAllCustomers() {
        try {
            customerList = customerService.findAllCustomers();
            updateCustomerTable(customerList);
            
            TableStyleUtil.refreshSorter(customerForm.getTableCustomers());
        } catch (Exception e) {
            if (customerForm != null) {
                JOptionPane.showMessageDialog(customerForm, 
                        ErrorMessage.CUSTOMER_LOAD_ERROR.formatted(e.getMessage()),
                        ErrorMessage.ERROR_TITLE, 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Cập nhật bảng hiển thị khách hàng
     * @param customers Danh sách khách hàng
     */
    private void updateCustomerTable(List<Customer> customers) {
        if (customerForm == null) return;
        
        DefaultTableModel model = (DefaultTableModel) customerForm.getTableCustomers().getModel();
        model.setRowCount(0);
        
        for (Customer customer : customers) {
            Object[] row = new Object[6];
            row[0] = customer.getCustomerId();
            row[1] = customer.getFullName();
            row[2] = customer.getPhoneNumber();
            row[3] = customer.getEmail();
            row[4] = customer.getPoints() != null ? 
                    numberFormat.format(customer.getPoints()) : "0";
            row[5] = customer.getCreatedAt() != null ? 
                    customer.getCreatedAt().format(dateFormatter) : "";
            
            model.addRow(row);
        }
    }
    
    /**
     * Tải thông tin chi tiết của một khách hàng
     * @param customerId ID của khách hàng
     */
    public void loadCustomerDetails(String customerId) {
        if(isAddingNew){
            if(handleEscapeKey())  return;
        }

        try {
            Optional<Customer> customerOpt = customerService.findCustomerById(customerId);
            if (customerOpt.isPresent()) {
                selectedCustomer = customerOpt.get();
                
                // Hiển thị thông tin khách hàng lên form
                customerForm.getTxtCustomerID().setText(selectedCustomer.getCustomerId());
                customerForm.getTxtCustomerName().setText(selectedCustomer.getFullName());
                customerForm.getTxtCustomerPhone().setText(selectedCustomer.getPhoneNumber());
                customerForm.getTxtCustomerEmail().setText(selectedCustomer.getEmail() != null ? selectedCustomer.getEmail() : "");
                customerForm.getTxtCustomerPoint().setText(numberFormat.format(selectedCustomer.getPoints()) != null ? 
                        selectedCustomer.getPoints().toString() : "0");
                
                if (selectedCustomer.getCreatedAt() != null) {
                    customerForm.getTxtCreateAt().setText(selectedCustomer.getCreatedAt().format(dateFormatter));
                }
                
                if (selectedCustomer.getUpdatedAt() != null) {
                    customerForm.getTxtCreateUpdate().setText(selectedCustomer.getUpdatedAt().format(dateFormatter));
                }
                
                ButtonUtils.setKButtonEnabled(customerForm.getBtnUpdate(), true);
                ButtonUtils.setKButtonEnabled(customerForm.getBtnDeleteCustomer(), true);

                customerForm.getBtnUpdate().setText(prop.getProperty("btnUpdate"));
            }
        } catch (Exception e) {
            if (customerForm != null) {
                JOptionPane.showMessageDialog(customerForm, 
                        ErrorMessage.CUSTOMER_DETAILS_LOAD_ERROR.formatted(e.getMessage()),
                        ErrorMessage.ERROR_TITLE, 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
        /**
     * Thêm khách hàng mới - Chuẩn bị form để nhập thông tin khách hàng mới
     */
    public void addNewCustomer() {
        // Reset form để nhập thông tin mới
        isAddingNew = true;
        clearForm();
        ButtonUtils.setKButtonEnabled(customerForm.getBtnUpdate(), isAddingNew);

        
        // Tạo mã khách hàng tự động (có thể tùy chỉnh theo yêu cầu)
        String newCustomerId = customerService.generateCustomerId();
        customerForm.getTxtCustomerID().setText(newCustomerId);
        
        // Enable các trường nhập liệu và nút lưu
        customerForm.getTxtCustomerName().setEnabled(true);
        customerForm.getTxtCustomerPhone().setEnabled(true);
        customerForm.getTxtCustomerEmail().setEnabled(true);
        customerForm.getTxtCustomerPoint().setEnabled(true);
        customerForm.getTxtCustomerPoint().setText("0"); // Thiết lập điểm mặc định
        

        ButtonUtils.setKButtonEnabled(customerForm.getBtnDeleteCustomer(), false);
        ButtonUtils.setKButtonEnabled(customerForm.getBtnAddCustomer(), false);

        customerForm.getBtnUpdate().setText(prop.getProperty("btnSave"));
        
        customerForm.getTxtCustomerName().requestFocus();
        
    }
    
    /**
     * Cập nhật khách hàng đã chọn hoặc lưu khách hàng mới (tùy thuộc vào trạng thái isAddingNew)
     */
    public void updateSelectedCustomer() {

        try {
            String id = customerForm.getTxtCustomerID().getText().trim();
            String name = customerForm.getTxtCustomerName().getText().trim();
            String phone = customerForm.getTxtCustomerPhone().getText().trim();
            String email = customerForm.getTxtCustomerEmail().getText().trim();
            String pointText = customerForm.getTxtCustomerPoint().getText().trim();
            
            if (id.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(customerForm,
                        ErrorMessage.CUSTOMER_REQUIRED_FIELDS,
                        ErrorMessage.ERROR_TITLE, 
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int points = 0;
            if (!pointText.isEmpty()) {
                try {
                    points = Integer.parseInt(pointText);
                    if (points < 0) {
                        JOptionPane.showMessageDialog(customerForm,
                                ErrorMessage.CUSTOMER_POINTS_NEGATIVE,
                                ErrorMessage.ERROR_TITLE, 
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(customerForm,
                            ErrorMessage.CUSTOMER_POINTS_INTEGER,
                            ErrorMessage.ERROR_TITLE, 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Nếu đang thêm mới
            if (isAddingNew) {
                Optional<Customer> existingCustomer = customerService.findCustomerById(id);
                if (existingCustomer.isPresent()) {
                    JOptionPane.showMessageDialog(customerForm,
                            ErrorMessage.CUSTOMER_ID_EXISTS,
                            ErrorMessage.ERROR_TITLE, 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Customer newCustomer = new Customer();
                newCustomer.setCustomerId(id);
                newCustomer.setFullName(name);
                newCustomer.setPhoneNumber(phone);
                
                if (!email.isEmpty()) {
                    newCustomer.setEmail(email);
                }
                
                newCustomer.setPoints(points);
                newCustomer.setCreatedAt(LocalDateTime.now());
                newCustomer.setUpdatedAt(LocalDateTime.now());
                
                Customer savedCustomer = customerService.saveCustomer(newCustomer);
                
                loadAllCustomers();
                
                JOptionPane.showMessageDialog(customerForm,
                        ErrorMessage.CUSTOMER_ADD_SUCCESS,
                        ErrorMessage.INFO_TITLE, 
                        JOptionPane.INFORMATION_MESSAGE);
                
                selectCustomerInTable(savedCustomer.getCustomerId());
                
                isAddingNew = false;
                // ButtonUtils.setKButtonEnabled(customerForm.getBtnAddCustomer(), false);
                
            } else { //Cập nhật bảng thông tin
                customerForm.getBtnUpdate().setText(prop.getProperty("btnUpdate"));

                if (selectedCustomer == null) {
                    JOptionPane.showMessageDialog(customerForm,
                            ErrorMessage.CUSTOMER_SELECT_UPDATE,
                            ErrorMessage.INFO_TITLE, 
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                selectedCustomer.setFullName(name);
                selectedCustomer.setPhoneNumber(phone);
                
                if (!email.isEmpty()) {
                    selectedCustomer.setEmail(email);
                } else {
                    selectedCustomer.setEmail(null);
                }
                
                selectedCustomer.setPoints(points);
                
                selectedCustomer.setUpdatedAt(LocalDateTime.now());
                
                Customer updatedCustomer = customerService.updateCustomer(selectedCustomer);
                
                loadAllCustomers();
                JOptionPane.showMessageDialog(customerForm,
                        ErrorMessage.CUSTOMER_UPDATE_SUCCESS,
                        ErrorMessage.INFO_TITLE, 
                        JOptionPane.INFORMATION_MESSAGE);
                
                selectCustomerInTable(updatedCustomer.getCustomerId());
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(customerForm,
                    (isAddingNew ? ErrorMessage.CUSTOMER_ADD_ERROR : ErrorMessage.CUSTOMER_UPDATE_ERROR).formatted(e.getMessage()),
                    ErrorMessage.ERROR_TITLE, 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Xóa khách hàng đã chọn
     */
    public void deleteSelectedCustomer() {
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(customerForm,
                    ErrorMessage.CUSTOMER_SELECT_DELETE,
                    ErrorMessage.INFO_TITLE, 
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Hiển thị hộp thoại xác nhận
        int option = JOptionPane.showConfirmDialog(customerForm,
                ErrorMessage.CUSTOMER_DELETE_CONFIRM.formatted(selectedCustomer.getFullName()),
                ErrorMessage.CUSTOMER_DELETE_TITLE,
                JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                boolean success = customerService.deleteCustomer(selectedCustomer.getCustomerId());
                
                if (success) {
                    loadAllCustomers();
                    
                    clearForm();
                    
                    JOptionPane.showMessageDialog(customerForm,
                            ErrorMessage.CUSTOMER_DELETE_SUCCESS,
                            ErrorMessage.INFO_TITLE, 
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(customerForm,
                            ErrorMessage.CUSTOMER_DELETE_CONSTRAINT,
                            ErrorMessage.ERROR_TITLE, 
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(customerForm,
                        ErrorMessage.CUSTOMER_DELETE_ERROR.formatted(e.getMessage()),
                        ErrorMessage.ERROR_TITLE, 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
        /**
     * Tìm kiếm khách hàng theo từ khóa sử dụng filter của bảng
     * @param keyword Từ khóa tìm kiếm
     */
    public void searchCustomers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // Nếu từ khóa rỗng, hiển thị tất cả khách hàng (bỏ filter)
            TableStyleUtil.applyFilter(customerTableSorter, "");
            return;
        }
        
        try {
            TableStyleUtil.applyFilter(customerTableSorter, keyword);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(customerForm,
                    ErrorMessage.CUSTOMER_SEARCH_ERROR.formatted(e.getMessage()),
                    ErrorMessage.ERROR_TITLE, 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Áp dụng sắp xếp cho bảng khách hàng
     */
    public void applySorting() {
        if (customerForm == null) return;
        
        String sortField = customerForm.getCbbSortCustomer().getSelectedItem().toString();
        String sortOrder = customerForm.getCbbSort().getSelectedItem().toString();
        
        // Lấy các giá trị từ file ngôn ngữ
        String noSort = prop.getProperty("txtNone");
        String nameSort = prop.getProperty("txtCustomerName");
        String pointSort = prop.getProperty("txtCustomerPoint");
        String ascSort = prop.getProperty("txtAscending");
        String descSort = prop.getProperty("txtDescending");

        if (sortField.equals(noSort) || sortOrder.equals(noSort)) {
            // Bỏ sắp xếp, hiển thị theo thứ tự mặc định
            customerTableSorter.setSortKeys(null);
            return;
        }
        
        int columnIndex = -1;
        
        if (sortField.equals(nameSort)) {
            columnIndex = 1; // Index của cột tên khách hàng
        } else if (sortField.equals(pointSort)) {
            columnIndex = 4; // Index của cột điểm
        }
        
        if (columnIndex != -1) {
            SortOrder order = sortOrder.equals(ascSort) ? 
                    SortOrder.ASCENDING : SortOrder.DESCENDING;
            
            List<SortKey> sortKeys = new ArrayList<>();
            sortKeys.add(new SortKey(columnIndex, order));
            customerTableSorter.setSortKeys(sortKeys);
        }
    }
    
    /**
     * Xuất danh sách khách hàng ra file Excel
     */
    public void exportToExcel() {
        if (customerList == null || customerList.isEmpty()) {
            JOptionPane.showMessageDialog(customerForm,
                    ErrorMessage.CUSTOMER_EXPORT_NO_DATA,
                    ErrorMessage.INFO_TITLE, 
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            // Tạo dữ liệu xuất ra Excel
            String[] headers = {"Mã khách hàng", "Họ tên", "Số điện thoại", 
                                "Email", "Điểm tích lũy", "Ngày tạo", "Cập nhật lần cuối"};

            if (LocaleManager.getInstance().getCurrentLocale().getLanguage().equals("en")) {
                headers = new String[]{"Customer ID", "Full Name", "Phone Number",
                                "Email", "Points", "Created At", "Updated At"};
            }

            Object[][] data = new Object[customerList.size()][headers.length];
            
            for (int i = 0; i < customerList.size(); i++) {
                Customer customer = customerList.get(i);
                data[i][0] = customer.getCustomerId();
                data[i][1] = customer.getFullName();
                data[i][2] = customer.getPhoneNumber();
                data[i][3] = customer.getEmail();
                data[i][4] = customer.getPoints();
                data[i][5] = customer.getCreatedAt() != null ? 
                        customer.getCreatedAt().format(dateFormatter) : "";
                data[i][6] = customer.getUpdatedAt() != null ? 
                        customer.getUpdatedAt().format(dateFormatter) : "";
            }
            
            // Tạo file Excel
            String fileName = "DANH_SACH_KHACH_HANG_" + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            JExcel jExcel = new JExcel();
            boolean success = jExcel.toExcel(headers, data, "Danh sách khách hàng", fileName);
            
            if (success) {
                JOptionPane.showMessageDialog(customerForm,
                        ErrorMessage.CUSTOMER_EXPORT_SUCCESS,
                        ErrorMessage.INFO_TITLE, 
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(customerForm,
                        ErrorMessage.CUSTOMER_EXPORT_FAILED,
                        ErrorMessage.ERROR_TITLE, 
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(customerForm,
                    ErrorMessage.CUSTOMER_EXPORT_ERROR.formatted(e.getMessage()),
                    ErrorMessage.ERROR_TITLE, 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean handleEscapeKey() {
        if (isAddingNew) {
            int option = JOptionPane.showConfirmDialog(customerForm,
                    ErrorMessage.CUSTOMER_ADD_CANCEL,
                    ErrorMessage.CONFIRM_TITLE, JOptionPane.YES_NO_OPTION);
                    
            if (option == JOptionPane.YES_OPTION) {
                isAddingNew = false;
                customerForm.getLabelESC().setVisible(false);
                customerForm.getBtnUpdate().setText(prop.getProperty("btnUpdate"));
                clearForm();
                return false;
            }else{
                // ButtonUtils.setKButtonEnabled(customerForm.getBtnAddCustomer(), false);
            }
        }
        return true;

    }
    
    /**
     * Xóa thông tin khách hàng khỏi form
     */
    public void clearForm() {
        if (customerForm == null) return;
        customerForm.getLabelESC().setVisible(isAddingNew);

        customerForm.getTxtCustomerID().setText("");
        customerForm.getTxtCustomerName().setText("");
        customerForm.getTxtCustomerPhone().setText("");
        customerForm.getTxtCustomerEmail().setText("");
        customerForm.getTxtCustomerPoint().setText("");
        customerForm.getTxtCreateAt().setText("");
        customerForm.getTxtCreateUpdate().setText("");
        
        selectedCustomer = null;

        ButtonUtils.setKButtonEnabled(customerForm.getBtnAddCustomer(), true);
        // Disable các nút cập nhật và xóa
        ButtonUtils.setKButtonEnabled(customerForm.getBtnUpdate(), false);
        ButtonUtils.setKButtonEnabled(customerForm.getBtnDeleteCustomer(), false);
    }
    
    /**
     * Chọn khách hàng trong bảng dựa trên ID
     * @param customerId ID của khách hàng cần chọn
     */
    private void selectCustomerInTable(String customerId) {
        if (customerForm == null) return;
        
        TableModel model = customerForm.getTableCustomers().getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(customerId)) {
                // Tìm vị trí tương ứng trong view (nếu đang sắp xếp)
                int viewIndex = customerForm.getTableCustomers().convertRowIndexToView(i);
                
                // Chọn và cuộn đến hàng đó
                customerForm.getTableCustomers().getSelectionModel().setSelectionInterval(viewIndex, viewIndex);
                customerForm.getTableCustomers().scrollRectToVisible(
                        customerForm.getTableCustomers().getCellRect(viewIndex, 0, true));
                break;
            }
        }
    }
    
    /**
     * Lấy danh sách tất cả khách hàng
     * @return Danh sách khách hàng
     */
    public List<Customer> getAllCustomers() {
        try {
            return customerService.findAllCustomers();
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.CUSTOMER_LOAD_ERROR.formatted(e.getMessage()), e);
        }
    }
    

    /**
     * Lấy danh sách hóa đơn của một khách hàng
     * @param customerId ID của khách hàng
     * @return Danh sách hóa đơn
     */
    public List<Invoice> getCustomerInvoices(String customerId) {
        try {
            // Gọi InvoiceService để lấy danh sách hóa đơn theo customerId
            return ServiceFactory.getInvoiceService().findInvoicesByCustomerID(customerId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(customerForm,
                    ErrorMessage.INVOICE_LOAD_ERROR.formatted(e.getMessage()),
                    ErrorMessage.ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
    }
    
    /**
     * Hiển thị dialog với danh sách hóa đơn của khách hàng
     * @param customer Khách hàng cần xem hóa đơn
     */
    public void showCustomerInvoices(Customer customer) {
        if (customer == null) {
            JOptionPane.showMessageDialog(customerForm,
                    "Vui lòng chọn khách hàng để xem hóa đơn",
                    ErrorMessage.INFO_TITLE,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        List<Invoice> invoices = getCustomerInvoices(customer.getCustomerId());
        CustomerInvoicesDialog dialog = new CustomerInvoicesDialog(
                DashboardForm.getInstance(), 
                customer, 
                invoices);
        dialog.setVisible(true);
    }

    
    /**
     * Tìm khách hàng theo ID
     * @param customerId ID khách hàng
     * @return Optional chứa khách hàng nếu tìm thấy
     */
    public Optional<Customer> findCustomerById(String customerId) {
        try {
            return customerService.findCustomerById(customerId);
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.CUSTOMER_FIND_BY_ID_ERROR.formatted(e.getMessage()), e);
        }
    }
    
    /**
     * Tìm khách hàng theo số điện thoại
     * @param phoneNumber Số điện thoại khách hàng
     * @return Optional chứa khách hàng nếu tìm thấy
     */
    public Optional<Customer> findCustomerByPhone(String phoneNumber) {
        try {
            return customerService.findCustomerByPhone(phoneNumber);
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.CUSTOMER_FIND_BY_PHONE_ERROR.formatted(e.getMessage()), e);
        }
    }
    
    /**
     * Cập nhật điểm tích lũy của khách hàng
     * @param customerId ID khách hàng
     * @param points Số điểm mới
     * @return true nếu cập nhật thành công
     */
    public boolean updateCustomerPoints(String customerId, int points) {
        try {
            Optional<Customer> customerOpt = customerService.findCustomerById(customerId);
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                customer.setPoints(points);
                customer.setUpdatedAt(LocalDateTime.now());
                
                customerService.updateCustomer(customer);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.CUSTOMER_POINTS_UPDATE_ERROR.formatted(e.getMessage()), e);
        }
    }
    
    /**
     * Cộng điểm tích lũy cho khách hàng
     * @param customerId ID khách hàng
     * @param pointsToAdd Số điểm cần cộng thêm
     * @return true nếu cập nhật thành công
     */
    public boolean addCustomerPoints(String customerId, int pointsToAdd) {
        try {
            Optional<Customer> customerOpt = customerService.findCustomerById(customerId);
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                int currentPoints = customer.getPoints() != null ? customer.getPoints() : 0;
                customer.setPoints(currentPoints + pointsToAdd);
                customer.setUpdatedAt(LocalDateTime.now());
                
                customerService.updateCustomer(customer);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.CUSTOMER_POINTS_ADD_ERROR.formatted(e.getMessage()), e);
        }
    }
    
    /**
     * Trừ điểm tích lũy của khách hàng
     * @param customerId ID khách hàng
     * @param pointsToDeduct Số điểm cần trừ
     * @return true nếu cập nhật thành công
     */
    public boolean deductCustomerPoints(String customerId, int pointsToDeduct) {
        try {
            Optional<Customer> customerOpt = customerService.findCustomerById(customerId);
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                int currentPoints = customer.getPoints() != null ? customer.getPoints() : 0;
                
                if (currentPoints < pointsToDeduct) {
                    throw new IllegalArgumentException(ErrorMessage.INSUFFICIENT_POINTS);
                }
                
                customer.setPoints(currentPoints - pointsToDeduct);
                customer.setUpdatedAt(LocalDateTime.now());
                
                customerService.updateCustomer(customer);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.CUSTOMER_POINTS_DEDUCT_ERROR.formatted(e.getMessage()), e);
        }
    }
    
    // Getters cho form và các components
    
    public CustomerForm getCustomerForm() {
        return customerForm;
    }
    
    public JTable getTableCustomers() {
        return customerForm != null ? customerForm.getTableCustomers() : null;
    }
    
    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }
    
    public List<Customer> getCustomerList() {
        return customerList;
    }
}
