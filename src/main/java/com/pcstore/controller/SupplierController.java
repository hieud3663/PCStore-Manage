package com.pcstore.controller;

import com.pcstore.model.Supplier;
import com.pcstore.service.ServiceFactory;
import com.pcstore.service.SupplierService;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.view.SupplierForm;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller cho SupplierForm với các chức năng thêm, xem, lưu, xóa và làm mới
 */
public class SupplierController {
    // Singleton instance
    private static SupplierController instance;
    
    // Services
    private SupplierService supplierService;
    
    // UI related
    private SupplierForm supplierForm;
    private List<Supplier> supplierList;
    private Supplier selectedSupplier;
    private TableRowSorter<TableModel> supplierTableSorter;
    
    /**
     * Lấy instance duy nhất của controller (Singleton pattern)
     * @param supplierForm Form hiển thị nhà cung cấp
     * @return SupplierController instance
     */
    public static synchronized SupplierController getInstance(SupplierForm supplierForm) {
        if (instance == null) {
            instance = new SupplierController(supplierForm);
        } else if (instance.supplierForm != supplierForm) {
            // Cập nhật form nếu khác với instance hiện tại
            instance.supplierForm = supplierForm;
            instance.setupEventListeners();
            instance.setupTableStyle();
            instance.loadAllSuppliers();
        }
        return instance;
    }
    
    /**
     * Khởi tạo controller với form (Giao diện người dùng)
     * @param supplierForm Form hiển thị nhà cung cấp
     */
    public SupplierController(SupplierForm supplierForm) {
        try {
            this.supplierService = ServiceFactory.getInstance().getSupplierService();
            this.supplierForm = supplierForm;
            this.supplierList = new ArrayList<>();

            setupEventListeners();
            setupTableStyle();
            loadAllSuppliers();
            clearForm();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                    "Lỗi khởi tạo SupplierController: " + e.getMessage(), 
                    ErrorMessage.ERROR_TITLE.toString(), 
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Thiết lập các sự kiện cho form
     */
    private void setupEventListeners() {
        // Sự kiện khi chọn nhà cung cấp trong bảng
        supplierForm.getTableSuppliers().getSelectionModel().addListSelectionListener(this::handleSupplierSelection);
        
        // Sự kiện cho nút Làm mới
        supplierForm.getBtnRefresh().addActionListener(e -> {
            loadAllSuppliers();
            clearForm();
        });
        
        // Sự kiện cho nút Lưu
        supplierForm.getBtnSave().addActionListener(e -> {
            if (supplierForm.isAddingNew()) {
                addNewSupplier();
            } else {
                updateSelectedSupplier();
            }
        });
        
        // Sự kiện cho nút Xóa
        supplierForm.getBtnDelete().addActionListener(e -> {
            deleteSelectedSupplier();
        });
        
        // Sự kiện cho nút Thêm
        supplierForm.getBtnAdd().addActionListener(e -> {
            prepareForNewSupplier();
        });
    }

    /**
     * Thiết lập style cho bảng
     */
    private void setupTableStyle() {
        try {
            supplierTableSorter = new TableRowSorter<>(supplierForm.getTableSuppliers().getModel());
            supplierForm.getTableSuppliers().setRowSorter(supplierTableSorter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Chuẩn bị form để thêm mới nhà cung cấp
     */
    private void prepareForNewSupplier() {
        clearForm();
        supplierForm.getTxtSupplierId().setText("[Tự động]");
        
        // Đảm bảo các trường được làm sạch hoàn toàn
        supplierForm.getTxtSupplierName().setText("");
        supplierForm.getTxtPhone().setText("");
        supplierForm.getTxtEmail().setText("");
        supplierForm.getTxtAddress().setText("");
    }
    
    /**
     * Tải danh sách tất cả nhà cung cấp
     */
    public void loadAllSuppliers() {
        try {
            supplierList = supplierService.getAllSuppliers();
            updateSupplierTable(supplierList);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(supplierForm, 
                    "Lỗi khi tải danh sách nhà cung cấp: " + e.getMessage(), 
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Cập nhật bảng nhà cung cấp
     * @param suppliers Danh sách nhà cung cấp
     */
    private void updateSupplierTable(List<Supplier> suppliers) {
        DefaultTableModel tableModel = (DefaultTableModel) supplierForm.getTableSuppliers().getModel();
        tableModel.setRowCount(0);
        
        for (Supplier supplier : suppliers) {
            tableModel.addRow(new Object[] {
                supplier.getSupplierId(),
                supplier.getName(),
                supplier.getPhoneNumber(),
                supplier.getEmail(),
                supplier.getAddress()
            });
        }
    }
    
    /**
     * Xử lý sự kiện khi chọn nhà cung cấp trong bảng
     */
    private void handleSupplierSelection(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = supplierForm.getTableSuppliers().getSelectedRow();
            if (selectedRow >= 0) {
                if (supplierForm.getTableSuppliers().getRowSorter() != null) {
                    selectedRow = supplierForm.getTableSuppliers().getRowSorter().convertRowIndexToModel(selectedRow);
                }
                String supplierId = supplierForm.getTableSuppliers().getModel().getValueAt(selectedRow, 0).toString();
                
                // Tìm nhà cung cấp được chọn
                for (Supplier supplier : supplierList) {
                    if (supplier.getSupplierId().equals(supplierId)) {
                        selectedSupplier = supplier;
                        break;
                    }
                }
                
                // Hiển thị thông tin trên form
                fillFormWithSelectedSupplier();
            } else {
                selectedSupplier = null;
            }
        }
    }
    
    /**
     * Hiển thị thông tin nhà cung cấp được chọn trên form
     */
    private void fillFormWithSelectedSupplier() {
        if (selectedSupplier == null) return;
        
        supplierForm.getTxtSupplierId().setText(selectedSupplier.getSupplierId());
        supplierForm.getTxtSupplierName().setText(selectedSupplier.getName());
        supplierForm.getTxtPhone().setText(selectedSupplier.getPhoneNumber());
        supplierForm.getTxtEmail().setText(selectedSupplier.getEmail() != null ? selectedSupplier.getEmail() : "");
        supplierForm.getTxtAddress().setText(selectedSupplier.getAddress() != null ? selectedSupplier.getAddress() : "");
    }
    
    /**
     * Thêm mới nhà cung cấp
     */
        // ...existing code...
    
    /**
     * Thêm mới nhà cung cấp
     */
    private void addNewSupplier() {
        try {
            // Lấy thông tin từ form
            String name = supplierForm.getTxtSupplierName().getText().trim();
            String phone = supplierForm.getTxtPhone().getText().trim();
            String email = supplierForm.getTxtEmail().getText().trim();
            String address = supplierForm.getTxtAddress().getText().trim();
            
            // Kiểm tra dữ liệu trước khi thêm
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(supplierForm, 
                        "Tên và số điện thoại của nhà cung cấp không được để trống.", 
                        "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Tạo nhà cung cấp mới
            Supplier newSupplier = new Supplier();
            newSupplier.setSupplierId(generateSupplierId());
            newSupplier.setName(name);
            newSupplier.setPhoneNumber(phone);
            newSupplier.setEmail(email);
            newSupplier.setAddress(address);
            
            // Thêm vào database
            supplierService.addSupplier(newSupplier);
            
            JOptionPane.showMessageDialog(supplierForm, 
                    "Thêm nhà cung cấp thành công.", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
            // Tải lại danh sách và làm mới form
            loadAllSuppliers();
            
            // Đảm bảo đặt lại trạng thái isAddingNew thành false trong SupplierForm
            supplierForm.resetAddingState();
            
            // Sau đó mới xóa form
            clearForm();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(supplierForm, 
                    "Lỗi khi thêm nhà cung cấp: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Sinh ID mới cho nhà cung cấp
     * @return ID mới
     */
    private String generateSupplierId() {
        // Tạo ID theo định dạng "NCC" + số thứ tự 2 chữ số (NCC01, NCC02...)
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
                    // Ignore
                }
            }
        }
        
        return String.format("NCC%02d", maxId + 1);
    }
    
    /**
     * Cập nhật thông tin nhà cung cấp đã chọn
     */
       // ...existing code...
    
    /**
     * Cập nhật thông tin nhà cung cấp đã chọn
     */
    private void updateSelectedSupplier() {
        if (selectedSupplier == null) {
            JOptionPane.showMessageDialog(supplierForm, 
                    "Vui lòng chọn nhà cung cấp để cập nhật.", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            // Lấy thông tin từ form
            String name = supplierForm.getTxtSupplierName().getText().trim();
            String phone = supplierForm.getTxtPhone().getText().trim();
            String email = supplierForm.getTxtEmail().getText().trim();
            String address = supplierForm.getTxtAddress().getText().trim();
            
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(supplierForm, 
                        "Tên và số điện thoại của nhà cung cấp không được để trống.", 
                        "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Cập nhật thông tin nhà cung cấp
            selectedSupplier.setName(name);
            selectedSupplier.setPhoneNumber(phone);
            selectedSupplier.setEmail(email);
            selectedSupplier.setAddress(address);
            
            // Cập nhật vào database
            supplierService.updateSupplier(selectedSupplier);
            
            JOptionPane.showMessageDialog(supplierForm, 
                    "Cập nhật nhà cung cấp thành công.", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
            // Đảm bảo đặt lại trạng thái trong form
            supplierForm.resetAddingState();
            
            // Tải lại danh sách và xóa form
            loadAllSuppliers();
            clearForm();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(supplierForm, 
                    "Lỗi khi cập nhật nhà cung cấp: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Xóa nhà cung cấp đã chọn
     */
    private void deleteSelectedSupplier() {
        if (selectedSupplier == null) {
            JOptionPane.showMessageDialog(supplierForm, 
                    "Vui lòng chọn nhà cung cấp để xóa.", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(supplierForm, 
                "Bạn có chắc chắn muốn xóa nhà cung cấp này không?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Xóa nhà cung cấp khỏi database
                supplierService.deleteSupplier(selectedSupplier.getSupplierId());
                
                JOptionPane.showMessageDialog(supplierForm, 
                        "Xóa nhà cung cấp thành công.", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                
                // Tải lại danh sách và xóa form
                loadAllSuppliers();
                clearForm();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(supplierForm, 
                        "Lỗi khi xóa nhà cung cấp: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Phương thức để cập nhật nhà cung cấp từ form
     * @param supplier Nhà cung cấp cần cập nhật
     */
    public void updateSupplier(Supplier supplier) throws Exception {
        if (supplier == null || supplier.getSupplierId() == null) {
            throw new Exception("Nhà cung cấp không hợp lệ để cập nhật");
        }
        
        supplierService.updateSupplier(supplier);
        loadAllSuppliers(); // Tải lại danh sách để cập nhật dữ liệu mới
    }
    
    /**
     * Phương thức để xóa nhà cung cấp theo ID
     * @param supplierId ID của nhà cung cấp cần xóa
     */
    public void deleteSupplier(String supplierId) throws Exception {
        if (supplierId == null || supplierId.isEmpty()) {
            throw new Exception("ID nhà cung cấp không hợp lệ để xóa");
        }
        
        supplierService.deleteSupplier(supplierId);
        loadAllSuppliers(); // Tải lại danh sách sau khi xóa
    }
    
    /**
     * Xóa thông tin nhà cung cấp khỏi form
     */
    public void clearForm() {
        if (supplierForm == null) return;
        
        // Xóa hoàn toàn các trường dữ liệu
        supplierForm.getTxtSupplierId().setText("");
        supplierForm.getTxtSupplierName().setText("");
        supplierForm.getTxtPhone().setText("");
        supplierForm.getTxtEmail().setText("");
        supplierForm.getTxtAddress().setText("");
        
        // Reset trạng thái
        selectedSupplier = null;
    }
    
    /**
     * Phương thức để thêm mới nhà cung cấp
     * @param supplier Nhà cung cấp cần thêm
     * @throws Exception nếu có lỗi
     */
    public void addNewSupplier(Supplier supplier) throws Exception {
        if (supplier == null) {
            throw new IllegalArgumentException("Nhà cung cấp không được phép null");
        }
        
        // Kiểm tra dữ liệu bắt buộc
        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhà cung cấp không được để trống");
        }
        
        if (supplier.getPhoneNumber() == null || supplier.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }
        
        // Sinh ID tự động nếu chưa có
        if (supplier.getSupplierId() == null || supplier.getSupplierId().isEmpty() 
                || supplier.getSupplierId().equals("[Tự động]")) {
            supplier.setSupplierId(generateSupplierId());
        }
        
        try {
            // Thêm vào database
            supplierService.addSupplier(supplier);
            
            // Tải lại danh sách
            loadAllSuppliers();
        } catch (Exception e) {
            throw new Exception("Không thể thêm nhà cung cấp: " + e.getMessage());
        }
    }
}