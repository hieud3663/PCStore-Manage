package com.pcstore.controller;

import com.pcstore.model.Employee;
import com.pcstore.service.EmployeeService;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.TableUtils;
import com.pcstore.view.DialogChooseEmployee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller cho Dialog chọn nhân viên
 */
public class DialogChooseEmployeeController {
    private DialogChooseEmployee dialog;
    private EmployeeService employeeService;
    private Employee selectedEmployee;
    private static final Logger logger = Logger.getLogger(DialogChooseEmployeeController.class.getName());
    
    /**
     * Khởi tạo controller
     * @param dialog Dialog chọn nhân viên
     * @param employeeService Service để lấy dữ liệu nhân viên
     */
    public DialogChooseEmployeeController(DialogChooseEmployee dialog, EmployeeService employeeService) {
        this.dialog = dialog;
        this.employeeService = employeeService;
        this.selectedEmployee = null;
        setupEventListeners();
        setupTableStyle();
        loadEmployees();
    }
    
    /**
     * Thiết lập các sự kiện
     */
    private void setupEventListeners() {
        dialog.getBtnConfirm().addActionListener(e -> confirmSelection());
        
        dialog.getBtnClose().addActionListener(e -> dialog.dispose());
        
        dialog.getTableListEmployee().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    confirmSelection();
                }
                
                handleRowSelection(dialog.getTableListEmployee().getSelectedRow());
            }
        });
        
        dialog.getTextFieldSearch().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterEmployees(dialog.getTextFieldSearch().getTxtSearchField().getText().trim());
            }
        });
    }
    
    /**
     * Thiết lập style cho bảng
     */
    private void setupTableStyle() {
        TableUtils.applyDefaultStyle(dialog.getTableListEmployee());
        TableUtils.setBooleanColumns(dialog.getTableListEmployee(), 0);
    }
    
    /**
     * Tải danh sách nhân viên
     */
    private void loadEmployees() {
        try {
            List<Employee> employees = employeeService.findAllEmployees();
            displayEmployees(employees);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading employees", e);
            JOptionPane.showMessageDialog(dialog, 
                    String.format(ErrorMessage.EMPLOYEE_LOAD_ERROR, e.getMessage()), 
                    ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Hiển thị danh sách nhân viên lên bảng
     */
    private void displayEmployees(List<Employee> employees) {
        DefaultTableModel model = (DefaultTableModel) dialog.getTableListEmployee().getModel();
        model.setRowCount(0);
        
        int count = 1;
        for (Employee employee : employees) {
            boolean hasAccount = false;
            try {
                hasAccount = employeeService.hasUserAccount(employee.getEmployeeId());
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error checking if employee has account: " + e.getMessage());
            }
            
            if (!hasAccount) {
                model.addRow(new Object[]{
                    false, // Cột checkbox
                    count++,
                    employee.getEmployeeId(),
                    employee.getFullName()
                });
            }
        }
    }
    
    /**
     * Lọc danh sách nhân viên theo từ khóa
     */
    private void filterEmployees(String searchText) {
        try {
            TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) dialog.getTableListEmployee().getRowSorter();
            if (sorter == null) {
                sorter = new TableRowSorter<>(dialog.getTableListEmployee().getModel());
                dialog.getTableListEmployee().setRowSorter(sorter);
            }
            
            TableUtils.applyFilter(sorter, searchText);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error filtering employees", e);
            // No need to show message to user for non-critical error
        }
    }
    
    /**
     * Xử lý khi chọn một hàng trong bảng
     */
    private void handleRowSelection(int selectedRow) {
        if (selectedRow < 0) return;
        
      
        int modelRow = dialog.getTableListEmployee().convertRowIndexToModel(selectedRow);
        DefaultTableModel model = (DefaultTableModel) dialog.getTableListEmployee().getModel();
        
        for (int i = 0; i < model.getRowCount(); i++) {
            if (i != modelRow) {
                model.setValueAt(false, i, 0);
            }
        }
        
        model.setValueAt(true, modelRow, 0);
    }
    
    /**
     * Xác nhận lựa chọn và đóng dialog
     */
    private void confirmSelection() {
        DefaultTableModel model = (DefaultTableModel) dialog.getTableListEmployee().getModel();
        
        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean isSelected = (Boolean) model.getValueAt(i, 0);
            if (isSelected) {
                String employeeId = model.getValueAt(i, 2).toString();
                
                try {
                    selectedEmployee = employeeService.findEmployeeById(employeeId).orElse(null);
                    
                    dialog.dispose();
                    return;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(dialog, 
                            String.format(ErrorMessage.EMPLOYEE_GET_INFO_ERROR, e.getMessage()),
                            ErrorMessage.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        JOptionPane.showMessageDialog(dialog, ErrorMessage.EMPLOYEE_SELECTION_REQUIRED, 
                ErrorMessage.INFO_TITLE, JOptionPane.WARNING_MESSAGE);
    }
    

    public Employee getSelectedEmployee() {
        return selectedEmployee;
    }
}
