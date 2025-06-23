package com.pcstore.controller;

import com.pcstore.model.Supplier;
import com.pcstore.service.ServiceFactory;
import com.pcstore.service.SupplierService;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.view.SupplierForm;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller cho SupplierForm với các chức năng thêm, xem, lưu, xóa và làm mới
 * Xử lý tất cả logic và tương tác với model, cập nhật view
 */
public class SupplierController implements ActionListener, ListSelectionListener {
    
    // Singleton instance
    private static SupplierController instance;
    
    // Services (Model)
    private SupplierService supplierService;
    
    // View
    private SupplierForm view;
    
    // Data
    private List<Supplier> supplierList;
    private Supplier selectedSupplier;
    private TableRowSorter<TableModel> tableSorter;
    
    // State
    private boolean isAddingNew = false;
    private boolean isEditing = false;
    
    /**
     * Lấy instance duy nhất của controller (Singleton pattern)
     * @param view Form hiển thị nhà cung cấp
     * @return SupplierController instance
     */
    public static synchronized SupplierController getInstance(SupplierForm view) {
        if (instance == null) {
            instance = new SupplierController(view);
        } else if (instance.view != view) {
            instance.view = view;
            instance.setupEventHandlers();
        }
        return instance;
    }
    
    /**
     * Khởi tạo controller với view
     */
    private SupplierController(SupplierForm view) {
        try {
            // Lấy service từ ServiceFactory
            this.supplierService = ServiceFactory.getInstance().getSupplierService();
            
            // Gán view
            this.view = view;
            view.setController(this);
            
            // Khởi tạo danh sách
            this.supplierList = new ArrayList<>();
            
            // Thiết lập xử lý sự kiện
            setupEventHandlers();
            
            // Thiết lập bộ lọc bảng
            setupTableSorter();
            
            // Tải dữ liệu ban đầu
            loadAllSuppliers();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                view,
                ErrorMessage.SUPPLIER_CONTROLLER_INIT_ERROR.format(e.getMessage()),
                ErrorMessage.ERROR_TITLE.toString(),
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Thiết lập xử lý sự kiện cho các thành phần của view
     */
    private void setupEventHandlers() {
        // Xử lý sự kiện cho các nút
        view.getBtnAdd().addActionListener(this);
        view.getBtnSave().addActionListener(this);
        view.getBtnDelete().addActionListener(this);
        view.getBtnRefresh().addActionListener(this);
        
        // Xử lý sự kiện chọn dòng trong bảng
        view.getTableSuppliers().getSelectionModel().addListSelectionListener(this);
    }
    
    /**
     * Thiết lập bộ lọc bảng
     */
    private void setupTableSorter() {
        try {
            tableSorter = new TableRowSorter<>(view.getTableModel());
            view.getTableSuppliers().setRowSorter(tableSorter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Xử lý sự kiện khi các nút được nhấn
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == view.getBtnAdd()) {
            prepareForNewSupplier();
        } 
        else if (source == view.getBtnSave()) {
            if (isAddingNew) {
                saveNewSupplier();
            } else if (isEditing) {
                updateSelectedSupplier();
            }
        } 
        else if (source == view.getBtnDelete()) {
            deleteSelectedSupplier();
        } 
        else if (source == view.getBtnRefresh()) {
            refreshData();
        }
    }
    
    /**
     * Xử lý sự kiện khi chọn dòng trong bảng
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = view.getTableSuppliers().getSelectedRow();
            if (selectedRow >= 0 && !isAddingNew) {
                loadSelectedSupplier(selectedRow);
                updateButtonStates(true);
            } else if (selectedRow < 0 && !isAddingNew) {
                selectedSupplier = null;
                updateButtonStates(false);
            }
        }
    }
    
    /**
     * Xử lý khi người dùng thay đổi nội dung trên form
     */
    public void handleFormFieldChange() {
        if (!isAddingNew && selectedSupplier != null && !isEditing) {
            isEditing = true;
            updateButtonStates(true);
        }
    }
    
    /**
     * Chuẩn bị form để thêm mới nhà cung cấp
     */
    private void prepareForNewSupplier() {
        clearForm();
        isAddingNew = true;
        isEditing = false;
        
        view.getTxtSupplierId().setText("[Tự động]");
        view.getTableSuppliers().clearSelection();
        
        updateButtonStates(false);
        view.getBtnSave().setEnabled(true);
        view.getBtnAdd().setEnabled(false);
        
        view.getTxtSupplierName().requestFocus();
    }
    
    /**
     * Lưu nhà cung cấp mới
     */
    private void saveNewSupplier() {
        try {
            // Lấy dữ liệu từ form
            String name = view.getTxtSupplierName().getText().trim();
            String phone = view.getTxtPhone().getText().trim();
            String email = view.getTxtEmail().getText().trim();
            String address = view.getTxtAddress().getText().trim();
            
            // Kiểm tra dữ liệu
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(
                    view,
                    ErrorMessage.SUPPLIER_NAME_PHONE_REQUIRED.toString(),
                    ErrorMessage.WARNING_TITLE.toString(),
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            
            // Tạo đối tượng nhà cung cấp mới
            Supplier newSupplier = new Supplier();
            newSupplier.setName(name);
            newSupplier.setPhoneNumber(phone);
            newSupplier.setEmail(email);
            newSupplier.setAddress(address);
            newSupplier.setSupplierId(generateSupplierId());
            
            // Thêm vào CSDL
            supplierService.addSupplier(newSupplier);
            
            // Thông báo thành công
            JOptionPane.showMessageDialog(
                view,
                ErrorMessage.SUPPLIER_ADD_SUCCESS.toString(),
                ErrorMessage.INFO_TITLE.toString(),
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Làm mới dữ liệu và reset trạng thái
            loadAllSuppliers();
            clearForm();
            resetState();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                view,
                ErrorMessage.SUPPLIER_ADD_ERROR.format(ex.getMessage()),
                ErrorMessage.ERROR_TITLE.toString(),
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }
    
    /**
     * Cập nhật nhà cung cấp đã chọn
     */
    private void updateSelectedSupplier() {
        if (selectedSupplier == null) {
            JOptionPane.showMessageDialog(
                view,
                ErrorMessage.SUPPLIER_SELECT_TO_UPDATE.toString(),
                ErrorMessage.WARNING_TITLE.toString(),
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        try {
            // Lấy dữ liệu từ form
            String name = view.getTxtSupplierName().getText().trim();
            String phone = view.getTxtPhone().getText().trim();
            String email = view.getTxtEmail().getText().trim();
            String address = view.getTxtAddress().getText().trim();
            
            // Kiểm tra dữ liệu
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(
                    view,
                    ErrorMessage.SUPPLIER_NAME_PHONE_REQUIRED.toString(),
                    ErrorMessage.WARNING_TITLE.toString(),
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            
            // Cập nhật thông tin
            selectedSupplier.setName(name);
            selectedSupplier.setPhoneNumber(phone);
            selectedSupplier.setEmail(email);
            selectedSupplier.setAddress(address);
            
            // Lưu vào CSDL
            supplierService.updateSupplier(selectedSupplier);
            
            // Thông báo thành công
            JOptionPane.showMessageDialog(
                view,
                ErrorMessage.SUPPLIER_UPDATE_SUCCESS.toString(),
                ErrorMessage.INFO_TITLE.toString(),
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Làm mới dữ liệu và reset trạng thái
            loadAllSuppliers();
            clearForm();
            resetState();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                view,
                ErrorMessage.SUPPLIER_UPDATE_ERROR.format(ex.getMessage()),
                ErrorMessage.ERROR_TITLE.toString(),
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }
    
    /**
     * Xóa nhà cung cấp đã chọn
     */
    private void deleteSelectedSupplier() {
        if (selectedSupplier == null) {
            JOptionPane.showMessageDialog(
                view,
                ErrorMessage.SUPPLIER_SELECT_TO_DELETE.toString(),
                ErrorMessage.WARNING_TITLE.toString(),
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
                view, 
                ErrorMessage.SUPPLIER_DELETE_CONFIRM.toString(), 
                ErrorMessage.SUPPLIER_DELETE_CONFIRM_TITLE.toString(), 
                JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Xóa khỏi CSDL
                supplierService.deleteSupplier(selectedSupplier.getSupplierId());
                
                // Thông báo thành công
                JOptionPane.showMessageDialog(
                    view,
                    ErrorMessage.SUPPLIER_DELETE_SUCCESS.toString(),
                    ErrorMessage.INFO_TITLE.toString(),
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                // Làm mới dữ liệu và form
                loadAllSuppliers();
                clearForm();
                resetState();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    view,
                    ErrorMessage.SUPPLIER_DELETE_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(),
                    JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Làm mới dữ liệu
     */
    private void refreshData() {
        loadAllSuppliers();
        clearForm();
        resetState();
    }
    
    /**
     * Tải danh sách tất cả nhà cung cấp
     */
    private void loadAllSuppliers() {
        try {
            supplierList = supplierService.getAllSuppliers();
            updateSupplierTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                view,
                ErrorMessage.SUPPLIER_LOAD_ALL_ERROR.format(e.getMessage()),
                ErrorMessage.ERROR_TITLE.toString(),
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Tải thông tin nhà cung cấp đã chọn
     * @param selectedRow Dòng đang chọn
     */
    private void loadSelectedSupplier(int selectedRow) {
        try {
            if (selectedRow >= 0) {
                // Chuyển từ view index sang model index nếu có sorter
                if (view.getTableSuppliers().getRowSorter() != null) {
                    selectedRow = view.getTableSuppliers().getRowSorter().convertRowIndexToModel(selectedRow);
                }
                
                // Lấy ID từ bảng
                String supplierId = view.getTableModel().getValueAt(selectedRow, 0).toString();
                
                // Tìm nhà cung cấp trong danh sách
                for (Supplier supplier : supplierList) {
                    if (supplier.getSupplierId().equals(supplierId)) {
                        selectedSupplier = supplier;
                        displaySupplierDetails(supplier);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Cập nhật bảng nhà cung cấp
     */
    private void updateSupplierTable() {
        // Xóa tất cả dữ liệu cũ
        view.getTableModel().setRowCount(0);
        
        // Thêm dữ liệu mới
        for (Supplier supplier : supplierList) {
            view.getTableModel().addRow(new Object[] {
                supplier.getSupplierId(),
                supplier.getName(),
                supplier.getPhoneNumber(),
                supplier.getEmail(),
                supplier.getAddress()
            });
        }
    }
    
    /**
     * Hiển thị chi tiết nhà cung cấp lên form
     * @param supplier Nhà cung cấp cần hiển thị
     */
    private void displaySupplierDetails(Supplier supplier) {
        view.getTxtSupplierId().setText(supplier.getSupplierId());
        view.getTxtSupplierName().setText(supplier.getName());
        view.getTxtPhone().setText(supplier.getPhoneNumber());
        view.getTxtEmail().setText(supplier.getEmail() != null ? supplier.getEmail() : "");
        view.getTxtAddress().setText(supplier.getAddress() != null ? supplier.getAddress() : "");
    }
    
    /**
     * Sinh ID mới cho nhà cung cấp
     */
    private String generateSupplierId() {
        int maxId = 0;
        
        for (Supplier s : supplierList) {
            String id = s.getSupplierId();
            if (id != null && id.startsWith("NCC")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > maxId) {
                        maxId = num;
                    }
                } catch (NumberFormatException e) {
                    // Bỏ qua
                }
            }
        }
        
        return String.format("NCC%02d", maxId + 1);
    }
    
    /**
     * Xóa form
     */
    private void clearForm() {
        view.getTxtSupplierId().setText("");
        view.getTxtSupplierName().setText("");
        view.getTxtPhone().setText("");
        view.getTxtEmail().setText("");
        view.getTxtAddress().setText("");
    }
    
    /**
     * Reset trạng thái
     */
    private void resetState() {
        isAddingNew = false;
        isEditing = false;
        selectedSupplier = null;
        view.getTableSuppliers().clearSelection();
        updateButtonStates(false);
    }
    
    /**
     * Cập nhật trạng thái các nút
     * @param isRowSelected Có dòng nào được chọn không
     */
    private void updateButtonStates(boolean isRowSelected) {
        if (isAddingNew) {
            view.getBtnAdd().setEnabled(false);
            view.getBtnSave().setEnabled(true);
            view.getBtnDelete().setEnabled(false);
        } else if (isEditing && isRowSelected) {
            view.getBtnAdd().setEnabled(true);
            view.getBtnSave().setEnabled(true);
            view.getBtnDelete().setEnabled(true);
        } else if (isRowSelected) {
            view.getBtnAdd().setEnabled(true);
            view.getBtnSave().setEnabled(false);
            view.getBtnDelete().setEnabled(true);
        } else {
            view.getBtnAdd().setEnabled(true);
            view.getBtnSave().setEnabled(false);
            view.getBtnDelete().setEnabled(false);
        }
    }
}