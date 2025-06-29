/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import com.pcstore.utils.ErrorMessage;
import javax.swing.JDialog;
import java.util.List;

/**
 *
 * @author DUC ANH
 */
public class AddReapairProductForm extends JDialog {

    private com.pcstore.controller.RepairController repairController;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField TxtNameCustomer;
    private com.k33ptoo.components.KButton btnAddRepairDevice;
    private com.k33ptoo.components.KButton btnReturnInformationLookup;
    private javax.swing.JComboBox<String> cbEmployee;
    private javax.swing.JComboBox<String> cbStatus;
    private javax.swing.JLabel lbCost;
    private javax.swing.JLabel lbDiagnose;
    private javax.swing.JLabel lbEmployee;
    private javax.swing.JLabel lbNameCustomer;
    private javax.swing.JLabel lbNameProduct;
    private javax.swing.JLabel lbNote;
    private javax.swing.JLabel lbProblem;
    private javax.swing.JLabel lbSDT;
    private javax.swing.JLabel lbStasus;
    private javax.swing.JLabel lbTitle;
    private com.k33ptoo.components.KGradientPanel panelBody;
    private com.k33ptoo.components.KGradientPanel panelBottom;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel panelRepairID;
    private javax.swing.JPanel panelRepairID1;
    private javax.swing.JPanel panelRepairID2;
    private javax.swing.JPanel panelRepairID3;
    private javax.swing.JPanel panelRepairID4;
    private javax.swing.JPanel panelRepairID5;
    private javax.swing.JPanel panelRepairID6;
    private javax.swing.JPanel panelRepairID7;
    private javax.swing.JPanel panelRepairID8;
    private com.k33ptoo.components.KGradientPanel panelTop;
    private javax.swing.JTextField txtCost;
    private javax.swing.JTextField txtDiagnose;
    private javax.swing.JTextField txtNameProduct;
    private javax.swing.JTextField txtNote;
    private javax.swing.JTextField txtProblem;
    private javax.swing.JTextField txtSDT;
    // End of variables declaration//GEN-END:variables
    
    public AddReapairProductForm() {
        initComponents();
        loadStatusComboBox(); 
        setupButtonListeners();
        setModal(true);
    }
    
     // Thiết lập controller
    public void setRepairController(com.pcstore.controller.RepairController controller) {
        this.repairController = controller;
        loadEmployeeData();
    }
    
    
     // Tải dữ liệu nhân viên cho combobox từ database
     
    private void loadEmployeeData() {
        try {
            if (repairController == null) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    ErrorMessage.REPAIR_CONTROLLER_NOT_SET, 
                    "Cảnh báo", 
                    javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Xóa các mục hiện có
            cbEmployee.removeAllItems();
            cbEmployee.addItem("Chọn nhân viên"); // Mục mặc định đầu tiên
            
            try {
                // Lấy danh sách nhân viên từ database thông qua controller
                List<com.pcstore.model.Employee> employees = repairController.getAllEmployees();
                
                if (employees != null && !employees.isEmpty()) {
                    System.out.println("Đã tìm thấy " + employees.size() + " nhân viên");
                    
                    // Thêm nhân viên vào combobox
                    for (com.pcstore.model.Employee employee : employees) {
                        String employeeId = employee.getEmployeeId();
                        String fullName = employee.getFullName();
                        
                        if (employeeId != null && fullName != null) {
                            cbEmployee.addItem(employeeId + " - " + fullName);
                        }
                    }
                } else {
                    System.out.println("Không tìm thấy nhân viên nào trong database");
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        ErrorMessage.EMPLOYEE_NOT_FOUND, 
                        "Thông báo", 
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println(ErrorMessage.REPAIR_LIST_ERROR + ": " + ex.getMessage());
                javax.swing.JOptionPane.showMessageDialog(this, 
                    ErrorMessage.REPAIR_LIST_ERROR + ": " + ex.getMessage() + 
                    "\nHệ thống sẽ sử dụng dữ liệu mẫu.", 
                    "Lỗi", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                
              
            }
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, 
                ErrorMessage.REPAIR_FORM_INIT_ERROR + ": " + e.getMessage(), 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelMain = new javax.swing.JPanel();
        panelTop = new com.k33ptoo.components.KGradientPanel();
        lbTitle = new javax.swing.JLabel();
        panelBody = new com.k33ptoo.components.KGradientPanel();
        panelRepairID = new javax.swing.JPanel();
        lbNameCustomer = new javax.swing.JLabel();
        TxtNameCustomer = new javax.swing.JTextField();
        panelRepairID1 = new javax.swing.JPanel();
        lbSDT = new javax.swing.JLabel();
        txtSDT = new javax.swing.JTextField();
        panelRepairID2 = new javax.swing.JPanel();
        lbNameProduct = new javax.swing.JLabel();
        txtNameProduct = new javax.swing.JTextField();
        panelRepairID3 = new javax.swing.JPanel();
        lbProblem = new javax.swing.JLabel();
        txtProblem = new javax.swing.JTextField();
        panelRepairID5 = new javax.swing.JPanel();
        lbCost = new javax.swing.JLabel();
        txtCost = new javax.swing.JTextField();
        panelRepairID4 = new javax.swing.JPanel();
        lbDiagnose = new javax.swing.JLabel();
        txtDiagnose = new javax.swing.JTextField();
        panelRepairID6 = new javax.swing.JPanel();
        lbStasus = new javax.swing.JLabel();
        cbStatus = new javax.swing.JComboBox<>();
        panelRepairID8 = new javax.swing.JPanel();
        lbNote = new javax.swing.JLabel();
        txtNote = new javax.swing.JTextField();
        panelRepairID7 = new javax.swing.JPanel();
        lbEmployee = new javax.swing.JLabel();
        cbEmployee = new javax.swing.JComboBox<>();
        panelBottom = new com.k33ptoo.components.KGradientPanel();
        btnReturnInformationLookup = new com.k33ptoo.components.KButton();
        btnAddRepairDevice = new com.k33ptoo.components.KButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(500, 32));

        panelMain.setMaximumSize(new java.awt.Dimension(700, 65534));
        panelMain.setPreferredSize(new java.awt.Dimension(700, 660));
        panelMain.setLayout(new javax.swing.BoxLayout(panelMain, javax.swing.BoxLayout.Y_AXIS));

        panelTop.setkEndColor(new java.awt.Color(0, 153, 255));
        panelTop.setkStartColor(new java.awt.Color(0, 153, 255));
        panelTop.setPreferredSize(new java.awt.Dimension(100, 80));

        lbTitle.setBackground(new java.awt.Color(0, 153, 255));
        lbTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbTitle.setForeground(new java.awt.Color(255, 255, 255));
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
        lbTitle.setText(bundle.getString("lbAddRepairProduct")); // NOI18N
        lbTitle.setIconTextGap(1);
        lbTitle.setMaximumSize(new java.awt.Dimension(346, 100));
        lbTitle.setMinimumSize(new java.awt.Dimension(346, 100));
        lbTitle.setPreferredSize(new java.awt.Dimension(346, 100));
        panelTop.add(lbTitle);

        panelMain.add(panelTop);

        panelBody.setBackground(new java.awt.Color(255, 255, 255));
        panelBody.setkEndColor(new java.awt.Color(255, 255, 255));
        panelBody.setkFillBackground(false);
        panelBody.setkStartColor(new java.awt.Color(255, 255, 255));
        panelBody.setMinimumSize(new java.awt.Dimension(660, 450));
        panelBody.setLayout(new java.awt.GridLayout(4, 2, 30, 50));

        panelRepairID.setBackground(new java.awt.Color(255, 255, 255));
        panelRepairID.setMinimumSize(new java.awt.Dimension(200, 100));
        panelRepairID.setPreferredSize(new java.awt.Dimension(200, 55));
        panelRepairID.setLayout(new java.awt.GridLayout(2, 0));

        lbNameCustomer.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbNameCustomer.setText(bundle.getString("lbRepairCode")); // NOI18N
        panelRepairID.add(lbNameCustomer);

        TxtNameCustomer.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        TxtNameCustomer.setPreferredSize(new java.awt.Dimension(64, 18));
        panelRepairID.add(TxtNameCustomer);

        panelBody.add(panelRepairID);

        panelRepairID1.setBackground(new java.awt.Color(255, 255, 255));
        panelRepairID1.setMinimumSize(new java.awt.Dimension(200, 100));
        panelRepairID1.setPreferredSize(new java.awt.Dimension(200, 55));
        panelRepairID1.setLayout(new java.awt.GridLayout(2, 0));

        lbSDT.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbSDT.setText(bundle.getString("lbSDT")); // NOI18N
        panelRepairID1.add(lbSDT);

        txtSDT.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtSDT.setPreferredSize(new java.awt.Dimension(64, 18));
        panelRepairID1.add(txtSDT);

        panelBody.add(panelRepairID1);

        panelRepairID2.setBackground(new java.awt.Color(255, 255, 255));
        panelRepairID2.setMinimumSize(new java.awt.Dimension(200, 100));
        panelRepairID2.setPreferredSize(new java.awt.Dimension(200, 55));
        panelRepairID2.setLayout(new java.awt.GridLayout(2, 0));

        lbNameProduct.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbNameProduct.setText(bundle.getString("lbEnterNameProduct")); // NOI18N
        panelRepairID2.add(lbNameProduct);

        txtNameProduct.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtNameProduct.setPreferredSize(new java.awt.Dimension(64, 18));
        panelRepairID2.add(txtNameProduct);

        panelBody.add(panelRepairID2);

        panelRepairID3.setBackground(new java.awt.Color(255, 255, 255));
        panelRepairID3.setMinimumSize(new java.awt.Dimension(200, 100));
        panelRepairID3.setPreferredSize(new java.awt.Dimension(200, 55));
        panelRepairID3.setLayout(new java.awt.GridLayout(2, 0));

        lbProblem.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbProblem.setText(bundle.getString("lbRepairProblem")); // NOI18N
        panelRepairID3.add(lbProblem);

        txtProblem.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtProblem.setPreferredSize(new java.awt.Dimension(64, 18));
        panelRepairID3.add(txtProblem);

        panelBody.add(panelRepairID3);

        panelRepairID5.setBackground(new java.awt.Color(255, 255, 255));
        panelRepairID5.setMinimumSize(new java.awt.Dimension(200, 100));
        panelRepairID5.setPreferredSize(new java.awt.Dimension(200, 55));
        panelRepairID5.setLayout(new java.awt.GridLayout(2, 0));

        lbCost.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbCost.setText(bundle.getString("lbCost")); // NOI18N
        panelRepairID5.add(lbCost);

        txtCost.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtCost.setPreferredSize(new java.awt.Dimension(64, 18));
        panelRepairID5.add(txtCost);

        panelBody.add(panelRepairID5);

        panelRepairID4.setBackground(new java.awt.Color(255, 255, 255));
        panelRepairID4.setMinimumSize(new java.awt.Dimension(200, 100));
        panelRepairID4.setPreferredSize(new java.awt.Dimension(200, 55));
        panelRepairID4.setLayout(new java.awt.GridLayout(2, 0));

        lbDiagnose.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbDiagnose.setText(bundle.getString("lbDiagnose")); // NOI18N
        panelRepairID4.add(lbDiagnose);

        txtDiagnose.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtDiagnose.setPreferredSize(new java.awt.Dimension(64, 18));
        panelRepairID4.add(txtDiagnose);

        panelBody.add(panelRepairID4);

        panelRepairID6.setBackground(new java.awt.Color(255, 255, 255));
        panelRepairID6.setMinimumSize(new java.awt.Dimension(200, 100));
        panelRepairID6.setPreferredSize(new java.awt.Dimension(200, 55));
        panelRepairID6.setLayout(new java.awt.GridLayout(2, 0));

        lbStasus.setBackground(new java.awt.Color(255, 255, 255));
        lbStasus.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbStasus.setText(bundle.getString("lbStatus")); // NOI18N
        lbStasus.setOpaque(true);
        panelRepairID6.add(lbStasus);

        cbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Received", "Diagnosing", "Delivered", "Repairing", "Cancelld" }));
        cbStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbStatusActionPerformed(evt);
            }
        });
        panelRepairID6.add(cbStatus);

        panelBody.add(panelRepairID6);

        panelRepairID8.setBackground(new java.awt.Color(255, 255, 255));
        panelRepairID8.setMinimumSize(new java.awt.Dimension(200, 100));
        panelRepairID8.setPreferredSize(new java.awt.Dimension(200, 55));
        panelRepairID8.setLayout(new java.awt.GridLayout(2, 0));

        lbNote.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbNote.setText(bundle.getString("lbNote")); // NOI18N
        panelRepairID8.add(lbNote);

        txtNote.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtNote.setPreferredSize(new java.awt.Dimension(64, 18));
        panelRepairID8.add(txtNote);

        panelBody.add(panelRepairID8);

        panelRepairID7.setBackground(new java.awt.Color(255, 255, 255));
        panelRepairID7.setMinimumSize(new java.awt.Dimension(200, 100));
        panelRepairID7.setPreferredSize(new java.awt.Dimension(200, 55));
        panelRepairID7.setLayout(new java.awt.GridLayout(2, 0));

        lbEmployee.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbEmployee.setText(bundle.getString("lbEmployee")); // NOI18N
        panelRepairID7.add(lbEmployee);

        cbEmployee.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbEmployeeActionPerformed(evt);
            }
        });
        panelRepairID7.add(cbEmployee);

        panelBody.add(panelRepairID7);

        panelMain.add(panelBody);

        panelBottom.setBackground(new java.awt.Color(255, 255, 255));
        panelBottom.setkBorderRadius(0);
        panelBottom.setkEndColor(new java.awt.Color(255, 255, 255));
        panelBottom.setkFillBackground(false);
        panelBottom.setkStartColor(new java.awt.Color(255, 255, 255));

        btnReturnInformationLookup.setText(bundle.getString("btnCancelAddRepairDevice")); // NOI18N
        btnReturnInformationLookup.setkBackGroundColor(new java.awt.Color(102, 153, 255));
        btnReturnInformationLookup.setkEndColor(new java.awt.Color(255, 51, 102));
        btnReturnInformationLookup.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnReturnInformationLookup.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnReturnInformationLookup.setkStartColor(new java.awt.Color(255, 51, 102));
        btnReturnInformationLookup.setMargin(new java.awt.Insets(2, 14, 0, 14));
        panelBottom.add(btnReturnInformationLookup);

        btnAddRepairDevice.setText(bundle.getString("btnAddRepairDevice")); // NOI18N
        btnAddRepairDevice.setkBackGroundColor(new java.awt.Color(102, 153, 255));
        btnAddRepairDevice.setkEndColor(new java.awt.Color(102, 153, 255));
        btnAddRepairDevice.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnAddRepairDevice.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnAddRepairDevice.setMargin(new java.awt.Insets(2, 14, 0, 14));
        panelBottom.add(btnAddRepairDevice);

        panelMain.add(panelBottom);

        getContentPane().add(panelMain, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void cbEmployeeActionPerformed(java.awt.event.ActionEvent evt) {
        
    }

    private void btnAddRepairDeviceActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (repairController == null) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    ErrorMessage.REPAIR_CONTROLLER_NOT_SET, 
                    "Cảnh báo", 
                    javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Lấy giá trị từ form
            String customerName = TxtNameCustomer.getText().trim(); // Sử dụng tên khách hàng
            String customerPhone = txtSDT.getText().trim(); // Sử dụng số điện thoại
            String deviceName = txtNameProduct.getText().trim();
            String problem = txtProblem.getText().trim();
            String diagnosis = txtDiagnose.getText().trim();
            String notes = txtNote.getText().trim();
            
            // Lấy trạng thái từ combobox
            String statusStr = cbStatus.getSelectedItem().toString();
            com.pcstore.model.enums.RepairEnum status = null;
            
            try {
                // Chuyển đổi chuỗi trạng thái thành enum
                status = com.pcstore.model.enums.RepairEnum.fromString(statusStr);
            } catch (Exception ex) {
                // Nếu không chuyển đổi được, sử dụng trạng thái mặc định RECEIVED
                status = com.pcstore.model.enums.RepairEnum.RECEIVED;
                System.out.println("Sử dụng trạng thái mặc định RECEIVED vì không thể chuyển đổi: " + statusStr);
            }
            
            // Kiểm tra thông tin bắt buộc
            if (customerName.isEmpty() || customerPhone.isEmpty() || deviceName.isEmpty() || problem.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    ErrorMessage.FIELD_EMPTY.toString().formatted("Tên khách hàng, SĐT, tên thiết bị, vấn đề"), 
                    "Thiếu thông tin", 
                    javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Parse phí dịch vụ
            java.math.BigDecimal fee = null;
            try {
                if (!txtCost.getText().trim().isEmpty()) {
                    fee = new java.math.BigDecimal(txtCost.getText().trim());
                } else {
                    fee = java.math.BigDecimal.ZERO;
                }
            } catch (NumberFormatException nfe) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    ErrorMessage.REPAIR_COST_NEGATIVE, 
                    "Lỗi", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Lấy ID nhân viên từ combobox (định dạng "ID - Tên")
            String employeeSelection = cbEmployee.getSelectedItem().toString();
            String employeeId = null; // Mặc định là null
            
            if (employeeSelection.equals("Chọn nhân viên")) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    ErrorMessage.EMPLOYEE_NOT_FOUND, 
                    "Thiếu thông tin", 
                    javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            } else if (employeeSelection.contains("-")) {
                employeeId = employeeSelection.split("-")[0].trim();
            }
            
            // Xác minh khách hàng tồn tại hoặc tạo mới
            boolean customerExists = false;
            try {
                // Kiểm tra khách hàng theo số điện thoại
                java.util.Optional<com.pcstore.model.Customer> customer = 
                    repairController.findCustomerByPhone(customerPhone);
                customerExists = customer.isPresent();
                
                if (!customerExists) {
                    // Tạo khách hàng mới nếu không tìm thấy
                    int choice = javax.swing.JOptionPane.showConfirmDialog(this,
                        ErrorMessage.CUSTOMER_NOT_FOUND + " Bạn có muốn tạo mới khách hàng?",
                        "Tạo khách hàng mới",
                        javax.swing.JOptionPane.YES_NO_OPTION);
                    
                    if (choice == javax.swing.JOptionPane.YES_OPTION) {
                        // Lấy tên khách hàng trực tiếp từ trường TxtNameCustomer của form
                        // Kiểm tra lại tên khách hàng đã nhập
                        if (customerName == null || customerName.trim().isEmpty()) {
                            javax.swing.JOptionPane.showMessageDialog(this,
                                ErrorMessage.CUSTOMER_NAME_EMPTY,
                                "Lỗi",
                                javax.swing.JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        // Tạo khách hàng với controller
                        com.pcstore.model.Customer newCustomer = repairController.createCustomer(customerName, customerPhone);
                        
                        if (newCustomer != null) {
                            customerExists = true;
                            // Hiển thị thông báo thành công ngắn gọn
                            javax.swing.JOptionPane.showMessageDialog(this,
                                "Đã tạo khách hàng mới: " + customerName,
                                "Thành công",
                                javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        // Người dùng không muốn tạo khách hàng mới
                        return;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(this,
                    ErrorMessage.CUSTOMER_NOT_FOUND + ": " + ex.getMessage(),
                    "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!customerExists) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    ErrorMessage.CUSTOMER_NOT_FOUND,
                    "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Tạo dịch vụ sửa chữa mới
            com.pcstore.model.Repair repair = repairController.createRepairService(
                customerPhone, employeeId, deviceName, problem, diagnosis, null);
            
            // Cập nhật trạng thái theo quy trình
            if (status != null && status != com.pcstore.model.enums.RepairEnum.RECEIVED) {
                try {
                    // Lấy giá trị từ model Repair
                    com.pcstore.model.enums.RepairEnum currentStatus = repair.getStatus();
                    
                    // Nếu muốn chuyển sang REPAIRING, phải qua DIAGNOSING trước
                    if (status == com.pcstore.model.enums.RepairEnum.REPAIRING && 
                        currentStatus == com.pcstore.model.enums.RepairEnum.RECEIVED) {
                        
                        // Đầu tiên chuyển sang DIAGNOSING
                        repairController.updateRepairService(
                            repair.getRepairServiceId(), null, null, null,
                            null, com.pcstore.model.enums.RepairEnum.DIAGNOSING, null);
                        
                        // Sau đó mới chuyển sang REPAIRING
                        repairController.updateRepairService(
                            repair.getRepairServiceId(), null, null, null,
                            null, com.pcstore.model.enums.RepairEnum.REPAIRING, null);
                    }
                    // Kiểm tra các trường hợp khác cần cập nhật trạng thái gián tiếp
                    else if (status == com.pcstore.model.enums.RepairEnum.WAITING_FOR_PARTS && 
                            currentStatus == com.pcstore.model.enums.RepairEnum.RECEIVED) {
                        
                        // Đầu tiên chuyển sang DIAGNOSING
                        repairController.updateRepairService(
                            repair.getRepairServiceId(), null, null, null,
                            null, com.pcstore.model.enums.RepairEnum.DIAGNOSING, null);
                        
                        // Sau đó mới chuyển sang WAITING_FOR_PARTS
                        repairController.updateRepairService(
                            repair.getRepairServiceId(), null, null, null,
                            null, com.pcstore.model.enums.RepairEnum.WAITING_FOR_PARTS, null);
                    }
                    else if (status == com.pcstore.model.enums.RepairEnum.COMPLETED && 
                            (currentStatus == com.pcstore.model.enums.RepairEnum.RECEIVED || 
                             currentStatus == com.pcstore.model.enums.RepairEnum.DIAGNOSING)) {
                        
                        // Chuyển sang REPAIRING trước
                        repairController.updateRepairService(
                            repair.getRepairServiceId(), null, null, null,
                            null, com.pcstore.model.enums.RepairEnum.REPAIRING, null);
                        
                        // Sau đó mới chuyển sang COMPLETED
                        repairController.updateRepairService(
                            repair.getRepairServiceId(), null, null, null,
                            null, com.pcstore.model.enums.RepairEnum.COMPLETED, null);
                    }
                    else {
                        // Trường hợp khác cố gắng cập nhật trực tiếp
                        repairController.updateRepairService(
                            repair.getRepairServiceId(), null, null, null,
                            null, status, null);
                    }
                } catch (Exception ex) {
                    System.err.println("Lỗi khi cập nhật trạng thái: " + ex.getMessage());
                    // Không hiển thị lỗi cho người dùng, vẫn tạo dịch vụ với trạng thái mặc định
                }
            }
            
            // Cập nhật các trường bổ sung
            if (fee != null && fee.compareTo(java.math.BigDecimal.ZERO) > 0) {
                repairController.updateServiceFee(repair.getRepairServiceId(), fee);
            }
            
            if (!notes.isEmpty()) {
                repairController.updateRepairService(
                    repair.getRepairServiceId(), null, null, null,
                    null, null, notes);
            }
            
            // Đánh dấu đã thêm thành công
            repairAdded = true;
            
            // Đóng dialog
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, 
                ErrorMessage.REPAIR_CREATE_ERROR + ": " + ex.getMessage(), 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cbStatusActionPerformed(java.awt.event.ActionEvent evt) {
       
    }
    private boolean repairAdded = false;

    // Thêm phương thức này vào AddReapairProductForm
    public boolean isRepairAdded() {
        return repairAdded;
    }
    private void setupButtonListeners() {
        // Thêm action listener cho nút hủy
        btnReturnInformationLookup.addActionListener(e -> {
            // Kiểm tra xem đã nhập dữ liệu hay chưa
            if (!TxtNameCustomer.getText().trim().isEmpty() || 
                !txtNameProduct.getText().trim().isEmpty() || 
                !txtProblem.getText().trim().isEmpty()) {
                
                int choice = javax.swing.JOptionPane.showConfirmDialog(this,
                    "Bạn có dữ liệu chưa lưu. Bạn có chắc muốn hủy không?",
                    "Xác nhận hủy",
                    javax.swing.JOptionPane.YES_NO_OPTION);
                    
                if (choice != javax.swing.JOptionPane.YES_OPTION) {
                    return; // Không đóng form nếu người dùng chọn "Không"
                }
            }
            dispose(); // Đóng dialog
        });
        
        // Thêm action listener cho nút Thêm
        btnAddRepairDevice.addActionListener(e -> {
            btnAddRepairDeviceActionPerformed(new java.awt.event.ActionEvent(btnAddRepairDevice, 0, ""));
        });
    }

    private void loadStatusComboBox() {
        // Khi tạo mới, chỉ cho phép các trạng thái hợp lệ ban đầu
        cbStatus.removeAllItems();
        cbStatus.addItem("Received"); // Trạng thái mặc định khi tạo mới
        cbStatus.addItem("Diagnosing"); // Trạng thái tiếp theo hợp lệ
        cbStatus.addItem("Cancelled"); // Luôn cho phép hủy
        
        // Đặt giá trị mặc định là "Received"
        cbStatus.setSelectedItem("Received");
    }

}
