package com.pcstore.controller;

import com.pcstore.model.Employee;
import com.pcstore.model.User;
import com.pcstore.model.enums.EmployeePositionEnum;
import com.pcstore.service.EmployeeService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.utils.ButtonUtils;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.JExcel;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.SessionManager;
import com.pcstore.utils.TableUtils;
import com.pcstore.view.EmployeeForm;
import com.raven.datechooser.SelectedDate;

import javax.swing.*;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;

import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Controller để quản lý các thao tác liên quan đến nhân viên
 */
public class EmployeeController {
    
    // Services
    private EmployeeService employeeService;
    
    // UI related
    private EmployeeForm employeeForm;
    private List<Employee> employeeList;
    private Employee selectedEmployee;
    private final DateTimeFormatter dateTimeFormatter = LocaleManager.getInstance().getDateTimeFormatter();
    private DateTimeFormatter dateFormatter = LocaleManager.getInstance().getDateFormatter();
    private TableRowSorter<TableModel> employeeTableSorter;
    
    //Lấy file vi_vn.properties
    private Properties prop = LocaleManager.getInstance().getProperties();

    private boolean isAddingNew;

    private String currentAvatar; // Lưu avatar dạng Base64
    private int AVATAR_WIDTH = 400; 
    private int AVATAR_HEIGHT = 600; 
    
    private boolean isAdmin = false;
    private boolean isManager = false;

    /**
     * @param employeeForm Form hiển thị nhân viên
     */
    public EmployeeController(EmployeeForm employeeForm) {
        try {
            this.employeeService = ServiceFactory.getEmployeeService();
            this.employeeForm = employeeForm;
            this.employeeList = new ArrayList<>();
                      
            setupEventListeners();
            setupTableStyle();
            checkUserPermissions();
            refereshForm();
            loadAllEmployees(); 
            refereshForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, ErrorMessage.INIT_CONTROLLER_ERROR.toString() + e.getMessage(), 
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Thiết lập các sự kiện cho form
     */
    private void setupEventListeners() {
        if (employeeForm == null) return;
        
        employeeForm.getTableListEmployee().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = employeeForm.getTableListEmployee().getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = employeeForm.getTableListEmployee().convertRowIndexToModel(selectedRow);
                    String employeeId = employeeForm.getTableListEmployee().getModel().getValueAt(modelRow, 1).toString();
                    loadEmployeeDetails(employeeId);
                }
            }
        });
        
        employeeForm.getBtnAddEmployee().addActionListener(e -> {
            addNewEmployee();
        });
        
        employeeForm.getBtnUpdate().addActionListener(e -> {
            updateSelectedEmployee();
        });
        
        employeeForm.getBtnDeleteEmployee().addActionListener(e -> {
            deleteSelectedEmployee();
        });
        
        employeeForm.getBtnRefresh().addActionListener(e -> {
            refereshForm();
        });
        
        employeeForm.getBtnExportExcel().addActionListener(e -> {
            exportToExcel();
        });
        
        employeeForm.getTextFieldSearch().getTxtSearchField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchEmployees(employeeForm.getTextFieldSearch().getTxtSearchField().getText());
            }
        });
        
        employeeForm.getTextFieldSearch().getBtnSearch().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                searchEmployees(employeeForm.getTextFieldSearch().getTxtSearchField().getText());
            }
        });
        
        employeeForm.getCbbSortEmployee().addActionListener(e -> {
            applySorting();
        });
        
        employeeForm.getCbbSort().addActionListener(e -> {
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
        
        employeeForm.getBtnChangeImage().addActionListener(e -> {
            chooseAvatar();
        });
    }
    
    /**
     * Thiết lập style cho bảng
     */
    private void setupTableStyle() {
        if (employeeForm == null) return;
        
        employeeTableSorter = TableUtils.applyDefaultStyle(employeeForm.getTableListEmployee());
        
        //Comparator cho cột ngày sinh
        employeeTableSorter.setComparator(3, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate date1 = LocalDate.parse(o1.toString(), formatter);
                    LocalDate date2 = LocalDate.parse(o2.toString(), formatter);
                    return date1.compareTo(date2);
                } catch (Exception e) {
                    return o1.toString().compareTo(o2.toString());
                }
            }
        });
    }
    
    /**
     * Tải danh sách tất cả nhân viên
     */
    public void loadAllEmployees() {
        try {
            if (isAdmin || isManager)
                employeeList = employeeService.findAllEmployees();
            else{
                employeeList = List.of(employeeService.findEmployeeById(SessionManager.getInstance().getCurrentUser().getEmployeeId()).orElse(null));   
            }

            updateEmployeeTable(employeeList);
            //refresh sort
            if (employeeForm.getTableListEmployee().getRowSorter() != null){
                employeeForm.getTableListEmployee().getRowSorter().setSortKeys(null);
            }
        } catch (Exception e) {
            if (employeeForm != null) {
                JOptionPane.showMessageDialog(employeeForm, 
                        ErrorMessage.LOAD_EMPLOYEES_ERROR.toString() + e.getMessage(),
                        ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Cập nhật bảng hiển thị nhân viên
     * @param employees Danh sách nhân viên
     */
    private void updateEmployeeTable(List<Employee> employees) {
        if (employeeForm == null) return;
        
        DefaultTableModel model = (DefaultTableModel) employeeForm.getTableListEmployee().getModel();
        model.setRowCount(0);
        
        int index = 1;
        for (Employee employee : employees) {
            Object[] row = new Object[8];
            row[0] = index++; // STT
            row[1] = employee.getEmployeeId();
            row[2] = employee.getFullName();
            row[3] = employee.getDateOfBirth() != null ? 
                 employee.getDateOfBirth().toLocalDate().format(dateFormatter) : "";

            row[4] = employee.getGender() != null ? 
                    employee.getGender().equalsIgnoreCase("Male") ? 
                    prop.getProperty("genderMale") : prop.getProperty("genderFemale") : "";
            row[5] = employee.getPosition() != null ? 
                    employee.getPosition().getDisplayName() : "";
            row[6] = employee.getPhoneNumber();
            row[7] = employee.getEmail();
            
            model.addRow(row);
        }
    }
    
    /**
     * Tải thông tin chi tiết của một nhân viên
     * @param employeeId ID của nhân viên
     */
    public void loadEmployeeDetails(String employeeId) {
        if(isAddingNew){
            if(handleEscapeKey())  return;
        }

        try {
            Optional<Employee> employeeOpt = employeeService.findEmployeeById(employeeId);
            if (employeeOpt.isPresent()) {
                selectedEmployee = employeeOpt.get();
                
                employeeForm.getTxtIDEmployee().setText(selectedEmployee.getEmployeeId());
                employeeForm.getTxtNameEmployee().setText(selectedEmployee.getFullName());
                employeeForm.getTxtPhonenumberEmployee().setText(selectedEmployee.getPhoneNumber());
                employeeForm.getTxtEmailEmployee().setText(selectedEmployee.getEmail() != null ? selectedEmployee.getEmail() : "");

                employeeForm.getCbbPositionEmployee().setSelectedItem(selectedEmployee.getPosition() != null ? 
                        selectedEmployee.getPosition().getDisplayName() : "");
                
                employeeForm.getjRadioMale().setSelected(selectedEmployee.getGender().equalsIgnoreCase("Male"));
                employeeForm.getjRadioFemale().setSelected(selectedEmployee.getGender().equalsIgnoreCase("Female"));
                
                if (selectedEmployee.getDateOfBirth() != null) {
                    employeeForm.getDateChooserBirthday().setSelectedDate(
                        selectedEmployee.getDateOfBirth());
                } else {
                    employeeForm.getTxtBirthdayEmployee().setText("dd-mm-yyyy");
                }
                
                if (selectedEmployee.getCreatedAt() != null) {
                    employeeForm.getTxtCreateAt().setText(selectedEmployee.getCreatedAt().format(dateTimeFormatter));
                }
                
                if (selectedEmployee.getUpdatedAt() != null) {
                    employeeForm.getTxtCreateUpdate().setText(selectedEmployee.getUpdatedAt().format(dateTimeFormatter));
                }
                
                if (selectedEmployee.getAvatar() != null && !selectedEmployee.getAvatar().isEmpty()) {
                    currentAvatar = selectedEmployee.getAvatar();
                    displayAvatar(currentAvatar);
                } else {
                    displayDefaultAvatar();
                }

                ButtonUtils.setKButtonEnabled(employeeForm.getBtnUpdate(), true);
                ButtonUtils.setKButtonEnabled(employeeForm.getBtnDeleteEmployee(), true);
                ButtonUtils.setKButtonEnabled(employeeForm.getBtnChangeImage(), (isAdmin || isManager));

                employeeForm.getBtnUpdate().setText(prop.getProperty("btnUpdate"));
            }
        } catch (Exception e) {
            if (employeeForm != null) {
                JOptionPane.showMessageDialog(employeeForm, 
                        ErrorMessage.LOAD_EMPLOYEE_DETAILS_ERROR.toString() + e.getMessage(),
                        ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Thêm nhân viên mới 
     */
    public void addNewEmployee() {
        // Reset form để nhập thông tin mới
        isAddingNew = true;
        clearForm();
        ButtonUtils.setKButtonEnabled(employeeForm.getBtnUpdate(), true);
        ButtonUtils.setKButtonEnabled(employeeForm.getBtnChangeImage(), true);
        ButtonUtils.setKButtonEnabled(employeeForm.getBtnAddEmployee(), false);
        // Tạo mã nhân viên tự động
        String newEmployeeId = employeeService.generateEmployeeId();
        employeeForm.getTxtIDEmployee().setText(newEmployeeId);
        
        employeeForm.getTxtNameEmployee().setEnabled(true);
        employeeForm.getTxtPhonenumberEmployee().setEnabled(true);
        employeeForm.getTxtEmailEmployee().setEnabled(true);
        employeeForm.getCbbPositionEmployee().setEnabled(true);
        
        ButtonUtils.setKButtonEnabled(employeeForm.getBtnDeleteEmployee(), false);

        employeeForm.getBtnUpdate().setText(prop.getProperty("btnSave"));
        
        employeeForm.getTxtNameEmployee().requestFocus();
        
        // Reset avatar
        currentAvatar = null;
        displayDefaultAvatar();
        
        // Hiển thị ghi chú ESC để thoát
        employeeForm.getLabelESC().setVisible(true);
    }
    
    /**
     * Cập nhật nhân viên đã chọn hoặc lưu nhân viên mới
     */
    public void updateSelectedEmployee() {
        try {
            String id = employeeForm.getTxtIDEmployee().getText().trim();
            String name = employeeForm.getTxtNameEmployee().getText().trim();
            String phone = employeeForm.getTxtPhonenumberEmployee().getText().trim();
            String email = employeeForm.getTxtEmailEmployee().getText().trim();
            String position = employeeForm.getCbbPositionEmployee().getSelectedItem().toString();
       

            String dateStr = employeeForm.getTxtBirthdayEmployee().getText().trim();
            Date dateOfBirth = null;
            
            if (dateStr != null) {
                try {
                    LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    dateOfBirth = Date.valueOf(localDate);
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(employeeForm,
                            ErrorMessage.INVALID_DATE_FORMAT.toString(),
                            ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
                            return;
                }
            }

            if (id.isEmpty() || name.isEmpty() || phone.isEmpty() || position.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(employeeForm,
                        ErrorMessage.EMPTY_FIELDS_ERROR.toString(),
                        ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            EmployeePositionEnum employeePosition = null;
            for (EmployeePositionEnum pos : EmployeePositionEnum.values()) {
                if (pos.getDisplayName().equalsIgnoreCase(position)) {
                    employeePosition = pos;
                    break;
                }
            }
            
            if (employeePosition == null) {
                JOptionPane.showMessageDialog(employeeForm,
                        ErrorMessage.INVALID_POSITION_ERROR.toString(),
                        ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean isMale = employeeForm.getjRadioMale().isSelected();
            String gender = isMale ? "Male" : "Female";

            // Nếu đang thêm mới
            if (isAddingNew) {
                Optional<Employee> existingEmployee = employeeService.findEmployeeById(id);
                if (existingEmployee.isPresent()) {
                    JOptionPane.showMessageDialog(employeeForm,
                            ErrorMessage.DUPLICATE_EMPLOYEE_ID_ERROR.toString(),
                            ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Employee newEmployee = new Employee();
                newEmployee.setEmployeeId(id);
                newEmployee.setFullName(name);
                newEmployee.setPhoneNumber(phone);
                newEmployee.setEmail(email);
                newEmployee.setGender(gender);
                newEmployee.setDateOfBirth(dateOfBirth);
                newEmployee.setPosition(employeePosition);
                newEmployee.setCreatedAt(LocalDateTime.now());
                newEmployee.setUpdatedAt(LocalDateTime.now());
                newEmployee.setAvatar(currentAvatar); 
                
                int checkOption = JOptionPane.showConfirmDialog(employeeForm,
                        ErrorMessage.CONFIRM_ADD_EMPLOYEE.toString(),
                        ErrorMessage.CONFIRM_TITLE.toString(), JOptionPane.YES_NO_OPTION);

                if(checkOption != JOptionPane.YES_OPTION) {
                    return;
                }

                Employee savedEmployee = employeeService.saveEmployee(newEmployee);
                
                isAddingNew = false;

                clearForm();
                loadAllEmployees();
                
                JOptionPane.showMessageDialog(employeeForm,
                        ErrorMessage.ADD_EMPLOYEE_SUCCESS.toString(),
                        ErrorMessage.INFO_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);
                
                selectEmployeeInTable(savedEmployee.getEmployeeId());
                
                employeeForm.getLabelESC().setVisible(false);
                
            } else { // Cập nhật thông tin
                ButtonUtils.setKButtonEnabled(employeeForm.getBtnAddEmployee(), true);
                ButtonUtils.setKButtonEnabled(employeeForm.getBtnUpdate(), true);

                employeeForm.getBtnUpdate().setText(prop.getProperty("btnUpdate"));

                if (selectedEmployee == null) {
                    JOptionPane.showMessageDialog(employeeForm,
                            ErrorMessage.SELECT_EMPLOYEE_TO_UPDATE.toString(),
                            ErrorMessage.INFO_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                selectedEmployee.setFullName(name);
                selectedEmployee.setPhoneNumber(phone);
                selectedEmployee.setEmail(email);
                selectedEmployee.setGender(gender);
                selectedEmployee.setDateOfBirth(dateOfBirth);
                selectedEmployee.setPosition(employeePosition);
                selectedEmployee.setUpdatedAt(LocalDateTime.now());
                selectedEmployee.setAvatar(currentAvatar); 

                int checkOption = JOptionPane.showConfirmDialog(employeeForm,
                        ErrorMessage.CONFIRM_UPDATE_EMPLOYEE.toString(),
                        ErrorMessage.CONFIRM_TITLE.toString(), JOptionPane.YES_NO_OPTION);

                if(checkOption != JOptionPane.YES_OPTION) {
                    return;
                }

                Employee updatedEmployee = employeeService.saveEmployee(selectedEmployee);
                
                loadAllEmployees();
                JOptionPane.showMessageDialog(employeeForm,
                        ErrorMessage.UPDATE_EMPLOYEE_SUCCESS.toString(),
                        ErrorMessage.INFO_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);
                
                selectEmployeeInTable(updatedEmployee.getEmployeeId());
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(employeeForm,
                    ErrorMessage.UPDATE_EMPLOYEE_ERROR.toString() + e.getMessage(),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Xóa nhân viên đã chọn
     */
    public void deleteSelectedEmployee() {
        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(employeeForm,
                    ErrorMessage.SELECT_EMPLOYEE_TO_DELETE.toString(),
                    ErrorMessage.INFO_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(employeeForm,
                ErrorMessage.CONFIRM_DELETE_EMPLOYEE.toString() + selectedEmployee.getFullName() + "?",
                ErrorMessage.CONFIRM_TITLE.toString(), JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                employeeService.deleteEmployee(selectedEmployee.getEmployeeId());
                
                loadAllEmployees();
                clearForm();
                
                JOptionPane.showMessageDialog(employeeForm,
                        ErrorMessage.DELETE_EMPLOYEE_SUCCESS.toString(),
                        ErrorMessage.INFO_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(employeeForm,
                        ErrorMessage.DELETE_EMPLOYEE_ERROR.toString() + e.getMessage(),
                        ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Tìm kiếm nhân viên theo từ khóa sử dụng filter của bảng
     * @param keyword Từ khóa tìm kiếm
     */
    public void searchEmployees(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            TableUtils.applyFilter(employeeTableSorter, "");
            return;
        }
        
        try {
            TableUtils.applyFilter(employeeTableSorter, keyword, 1, 2, 3, 6, 7);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(employeeForm,
                    ErrorMessage.SEARCH_EMPLOYEE_ERROR.toString() + e.getMessage(),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Áp dụng sắp xếp cho bảng nhân viên
     */
    public void applySorting() {
        if (employeeForm == null) return;
        
        String sortField = employeeForm.getCbbSortEmployee().getSelectedItem().toString();
        String sortOrder = employeeForm.getCbbSort().getSelectedItem().toString();
        
        if (sortField.equals("<Không>") || sortOrder.equals("<Không>")) {
            // Bỏ sắp xếp, hiển thị theo thứ tự mặc định
            employeeTableSorter.setSortKeys(null);
            return;
        }
        
        int columnIndex = -1;
        
        if (sortField.equals("STT")) {
            columnIndex = 0;
        } else if (sortField.equalsIgnoreCase("Tên nhân viên")) {
            columnIndex = 2;
        } else if (sortField.equals("Ngày sinh")) {
            columnIndex = 3; // Adjust based on actual column index for birthday
        }
        
        if (columnIndex != -1) {
            SortOrder order = sortOrder.equals("Tăng dần") ? 
                    SortOrder.ASCENDING : SortOrder.DESCENDING;
            
            List<SortKey> sortKeys = new ArrayList<>();
            sortKeys.add(new SortKey(columnIndex, order));
            employeeTableSorter.setSortKeys(sortKeys);
        }
    }
    
    /**
     * Xuất danh sách nhân viên ra file Excel
     */
    public void exportToExcel() {
        if (employeeList == null || employeeList.isEmpty()) {
            JOptionPane.showMessageDialog(employeeForm,
                    ErrorMessage.NO_DATA_TO_EXPORT.toString(),
                    ErrorMessage.INFO_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            // Tạo dữ liệu xuất ra Excel
            String[] headers = {"STT", "Mã nhân viên", "Họ tên", "Ngày sinh", "Giới tính", 
                               "Chức vụ", "Số điện thoại", "Email", "Ngày tạo", "Cập nhật lần cuối"};
            
            Object[][] data = new Object[employeeList.size()][headers.length];
            
            for (int i = 0; i < employeeList.size(); i++) {
                Employee employee = employeeList.get(i);
                data[i][0] = i + 1; // STT
                data[i][1] = employee.getEmployeeId();
                data[i][2] = employee.getFullName();
                data[i][3] = employee.getDateOfBirth() != null ? 
                    employee.getDateOfBirth().toLocalDate().format(dateFormatter) : "";
                data[i][4] = employee.getGender() != null ? employee.getGender() : "";
                    
                data[i][5] = employee.getPosition() != null ? employee.getPosition().getDisplayName() : "";
                data[i][6] = employee.getPhoneNumber();
                data[i][7] = employee.getEmail();
                data[i][8] = employee.getCreatedAt() != null ? 
                        employee.getCreatedAt().format(dateTimeFormatter) : "";
                data[i][9] = employee.getUpdatedAt() != null ? 
                        employee.getUpdatedAt().format(dateTimeFormatter) : "";
            }
            
            // Tạo file Excel
            String fileName = "DANH_SACH_NHAN_VIEN_" + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            
            JExcel jExcel = new JExcel();
            boolean success = jExcel.toExcel(headers, data, "Danh sách nhân viên", fileName);
            
            if (success) {
                JOptionPane.showMessageDialog(employeeForm,
                        ErrorMessage.EXPORT_EXCEL_SUCCESS.toString(),
                        ErrorMessage.INFO_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(employeeForm,
                        ErrorMessage.EXPORT_EXCEL_FAILURE.toString(),
                        ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(employeeForm,
                    ErrorMessage.EXPORT_EXCEL_ERROR.toString() + e.getMessage(),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Xử lý khi nhấn phím Esc để hủy thao tác thêm nhân viên mới
     */
    private boolean handleEscapeKey() {
        if (isAddingNew) {
            int option = JOptionPane.showConfirmDialog(employeeForm,
                    ErrorMessage.CONFIRM_CANCEL_ADD_EMPLOYEE.toString(),
                    ErrorMessage.CONFIRM_TITLE.toString(), JOptionPane.YES_NO_OPTION);
                    
            if (option == JOptionPane.YES_OPTION) {
                isAddingNew = false;
                employeeForm.getLabelESC().setVisible(false);
                employeeForm.getBtnUpdate().setText(prop.getProperty("btnUpdate"));
                clearForm();
                return false;
            }else{
                // ButtonUtils.setKButtonEnabled(employeeForm.getBtnAddEmployee(), false);
                return true;
            }
        }
        return true;
    }
    
    /**
     * Xóa thông tin nhân viên khỏi form
     */
    public void clearForm() {
        if (employeeForm == null) return;
        
        employeeForm.getLabelESC().setVisible(isAddingNew);
        
        employeeForm.getTxtIDEmployee().setText("");
        employeeForm.getTxtNameEmployee().setText("");
        employeeForm.getTxtPhonenumberEmployee().setText("");
        employeeForm.getTxtEmailEmployee().setText("");
        employeeForm.getCbbPositionEmployee().setSelectedIndex(0);
        employeeForm.getTxtBirthdayEmployee().setText("dd-mm-yyyy");
        employeeForm.getTxtCreateAt().setText("");
        employeeForm.getTxtCreateUpdate().setText("");
        
        employeeForm.getjRadioMale().setSelected(false);
        employeeForm.getjRadioFemale().setSelected(false);
        
        selectedEmployee = null;
        
        currentAvatar = null;
        displayDefaultAvatar();

        ButtonUtils.setKButtonEnabled(employeeForm.getBtnAddEmployee(), true);
        ButtonUtils.setKButtonEnabled(employeeForm.getBtnUpdate(), false);
        ButtonUtils.setKButtonEnabled(employeeForm.getBtnDeleteEmployee(), false);
        ButtonUtils.setKButtonEnabled(employeeForm.getBtnChangeImage(), false);
    }
    
    /**
     * Chọn nhân viên trong bảng dựa trên ID
     * @param employeeId ID của nhân viên cần chọn
     */
    private void selectEmployeeInTable(String employeeId) {
        if (employeeForm == null) return;
        
        TableModel model = employeeForm.getTableListEmployee().getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 1).equals(employeeId)) {
                int viewIndex = employeeForm.getTableListEmployee().convertRowIndexToView(i);
                
                employeeForm.getTableListEmployee().getSelectionModel().setSelectionInterval(viewIndex, viewIndex);
                employeeForm.getTableListEmployee().scrollRectToVisible(
                        employeeForm.getTableListEmployee().getCellRect(viewIndex, 0, true));
                break;
            }
        }
    }
    
    /**
     * Chọn và thay đổi avatar nhân viên
     */
    public void chooseAvatar() {
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image", "jpg", "jpeg", "png", "gif"));
        
        int result = fileChooser.showOpenDialog(employeeForm);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                
                BufferedImage originalImage = ImageIO.read(selectedFile);
                if (originalImage == null) {
                    JOptionPane.showMessageDialog(employeeForm, ErrorMessage.INVALID_IMAGE_FILE.toString(), ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Image resizedImage = originalImage.getScaledInstance(AVATAR_WIDTH, AVATAR_HEIGHT, Image.SCALE_SMOOTH);
                BufferedImage bufferedResized = new BufferedImage(AVATAR_WIDTH, AVATAR_HEIGHT, BufferedImage.TYPE_INT_RGB);
                bufferedResized.getGraphics().drawImage(resizedImage, 0, 0, null);
                
                // Chuyển đổi thành Base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedResized, "jpg", baos);
                byte[] imageBytes = baos.toByteArray();
                currentAvatar = Base64.getEncoder().encodeToString(imageBytes);
                
                displayAvatar(currentAvatar);
                
                if (selectedEmployee != null && !isAddingNew) {
                    selectedEmployee.setAvatar(currentAvatar);
                }
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(employeeForm,
                        ErrorMessage.PROCESS_IMAGE_ERROR.toString() + e.getMessage(),
                        ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Hiển thị avatar từ chuỗi Base64
     * @param base64Image Chuỗi Base64 của hình ảnh
     */
    public void displayAvatar(String base64Image) {
        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                // Thiết lập thuộc tính cho panel trước
                employeeForm.getPanelAvatar().setkBorderColor(new Color(51,255,51));
                employeeForm.getPanelAvatar().setkBorderRadius(20);
                employeeForm.getPanelAvatar().setkFillBackground(false);  
                employeeForm.getPanelAvatar().setOpaque(false);
                
                // Thiết lập layout
                employeeForm.getPanelAvatar().setLayout(new BorderLayout());
                
                // Xóa nội dung cũ
                employeeForm.getPanelAvatar().removeAll();
                
                int width = Math.max(1, employeeForm.getPanelAvatar().getWidth());
                int height = Math.max(1, employeeForm.getPanelAvatar().getHeight());
                
                if (width <= 1) width = 150; // Giá trị mặc định nhỏ hơn
                if (height <= 1) height = 150; // Giá trị mặc định nhỏ hơn
                
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                BufferedImage originalImg = ImageIO.read(new ByteArrayInputStream(imageBytes));
                
                if (originalImg != null) {
                    Image resizedImage = originalImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    
                    ImageIcon icon = new ImageIcon(resizedImage);
                    JLabel avatarLabel = new JLabel(icon, JLabel.CENTER);
                    avatarLabel.setOpaque(false);
                    employeeForm.getPanelAvatar().add(avatarLabel, BorderLayout.CENTER);
                    
                    // Cập nhật giao diện
                    employeeForm.getPanelAvatar().revalidate();
                    employeeForm.getPanelAvatar().repaint();
                }
            } catch (IOException | IllegalArgumentException e) {
                JOptionPane.showMessageDialog(employeeForm,
                        ErrorMessage.DISPLAY_AVATAR_ERROR.toString() + e.getMessage(),
                        ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
                displayDefaultAvatar();
            }
        } else {
            displayDefaultAvatar();
        }
    }

    /**
     * Hiển thị avatar mặc định
     */
    public void displayDefaultAvatar() {
        try {
            employeeForm.getPanelAvatar().setkBorderColor(new Color(51,255,51));
            employeeForm.getPanelAvatar().setkBorderRadius(20);
            employeeForm.getPanelAvatar().setkFillBackground(false);  
            employeeForm.getPanelAvatar().setOpaque(false);
            
            employeeForm.getPanelAvatar().setLayout(new BorderLayout());
            
            // Xóa nội dung cũ
            employeeForm.getPanelAvatar().removeAll();
            
            // Thêm avatar mặc định
            ImageIcon defaultIcon = new ImageIcon(getClass().getResource("/com/pcstore/resources/icon/user-default.png"));
            if (defaultIcon.getIconWidth() > 0) {
                Image img = defaultIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                JLabel avatarLabel = new JLabel(new ImageIcon(img), JLabel.CENTER);
                avatarLabel.setOpaque(false);
                employeeForm.getPanelAvatar().add(avatarLabel, BorderLayout.CENTER);
                
                // Cập nhật giao diện
                employeeForm.getPanelAvatar().revalidate();
                employeeForm.getPanelAvatar().repaint();
            }
        } catch (Exception e) {
            System.err.println(ErrorMessage.DISPLAY_DEFAULT_AVATAR_ERROR.toString() + e.getMessage());
        }
    }

    /**
     * Kiểm tra quyền của người dùng hiện tại và điều chỉnh giao diện tương ứng
     */
    private void checkUserPermissions() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        String roleName = currentUser.getRoleName();
        
        isAdmin = roleName.equals("Admin");
        isManager = roleName.equals("Manager");
        boolean isAdminOrManager = isAdmin || isManager;
        
        employeeForm.getBtnAddEmployee().setVisible(isAdminOrManager);
        employeeForm.getBtnDeleteEmployee().setVisible(isAdminOrManager);
        employeeForm.getBtnExportExcel().setVisible(isAdminOrManager);
        employeeForm.getPanelSort().setVisible(isAdminOrManager);
        
        if (!isAdminOrManager) {
            employeeForm.getCbbPositionEmployee().setEnabled(false);
            employeeForm.getTextFieldSearch().setVisible(false);
            employeeForm.getCbbSortEmployee().setVisible(false);
            employeeForm.getCbbSort().setVisible(false);
            employeeForm.getBtnChangeImage().setEnabled(false);
        }
    }

    /**
     * Tải thông tin nhân viên của người dùng hiện tại (cho người dùng không phải admin/manager)
     */
    private void loadCurrentUserEmployeeDetails() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        
        try {
            Optional<Employee> employeeOpt = employeeService.findEmployeeById(currentUser.getEmployeeId());
            
            if (employeeOpt.isPresent()) {
                Employee employee = employeeOpt.get();
                
                loadEmployeeDetails(employee.getEmployeeId());
                
                ButtonUtils.setKButtonEnabled(employeeForm.getBtnUpdate(), true);
                ButtonUtils.setKButtonEnabled(employeeForm.getBtnChangeImage(), false);
                
            } else {
                JOptionPane.showMessageDialog(employeeForm, 
                    ErrorMessage.CURRENT_USER_EMPLOYEE_NOT_FOUND.toString(), 
                    ErrorMessage.INFO_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(employeeForm, 
                ErrorMessage.LOAD_CURRENT_USER_EMPLOYEE_ERROR.toString() + e.getMessage(), 
                ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Làm mới form
     */
    private void refereshForm(){
        clearForm();
        isAddingNew = false;
        if(isAdmin || isManager){
            loadAllEmployees();
        } else {
            loadCurrentUserEmployeeDetails();
        }
    }

}