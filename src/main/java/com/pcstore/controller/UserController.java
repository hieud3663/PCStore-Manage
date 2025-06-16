package com.pcstore.controller;

import com.pcstore.model.Employee;
import com.pcstore.model.User;
import com.pcstore.model.enums.Roles;
import com.pcstore.service.EmployeeService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.service.UserService;
import com.pcstore.utils.ButtonUtils;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.PCrypt;
import com.pcstore.utils.SessionManager;
import com.pcstore.utils.TableUtils;
import com.pcstore.view.UserForm;
import com.pcstore.view.DashboardForm;
import com.pcstore.view.DialogChangePassword;
import com.pcstore.view.DialogChooseEmployee;
import com.pcstore.controller.DialogChooseEmployeeController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Controller cho form quản lý User
 */
public class UserController {
    private UserForm userForm;
    private UserService userService;
    private EmployeeService employeeService;
    private User selectedUser;
    private Employee selectedEmployee;
    private boolean isAddingNew = false;
    private boolean isAdmin = false;
    private boolean isManager = false;
    private static UserController instance;
    private Properties prop;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    /**
     * Khởi tạo controller với form tương ứng
     * @param userForm Form quản lý User
     */
    public UserController(UserForm userForm) {
        try {
            this.userForm = userForm;
            this.prop = LocaleManager.getInstance().getProperties();
            
            this.userService = ServiceFactory.getUserService();
            this.employeeService = ServiceFactory.getEmployeeService();
            this.selectedEmployee = null;
            
            clearForm();
            setupEventListeners();
            setupTableStyle();
            populateRoleComboBox();
            checkAdmin();

            
            if (isAdmin || isManager) {
                loadAllUsers();
            }
            
            checkUserPermissions();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                    String.format(ErrorMessage.USER_CONTROLLER_INIT_ERROR.toString(), e.getMessage()), 
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
       
    
    /**
     * Đăng ký các event listener cho các thành phần trong form
     */
    private void setupEventListeners() {
        if (userForm == null) return;
        
        userForm.getBtnAdd().addActionListener(e -> {
            if (!isAddingNew) {
                userForm.getBtnUpdate().setText(prop.getProperty("btnSave"));
                prepareForAddingNew();
            }
        });
        
        userForm.getBtnUpdate().addActionListener(e -> {

            if(!isAddingNew) {
                updateSelectedUser();
            } else {
                addNewUser();
            }
        });
        
        userForm.getBtnDelete().addActionListener(e -> deleteSelectedUser());
        
        userForm.getBtnRefresh().addActionListener(e -> {
            refreshForm();
        });
        
        userForm.getBtnResetPassword().addActionListener(e ->{
            if(userForm.getBtnResetPassword().getText().equalsIgnoreCase(prop.getProperty("btnResetPassword"))){
                resetPassword();
            }else if(userForm.getBtnResetPassword().getText().equalsIgnoreCase(prop.getProperty("btnChangePassword"))){
                changePassword();
            }
        });
        
        userForm.getTableListUser().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = userForm.getTableListUser().getSelectedRow();
                if (selectedRow >= 0) {
                    String userId = userForm.getTableListUser().getValueAt(
                            userForm.getTableListUser().convertRowIndexToModel(selectedRow), 1).toString();
                    loadUserDetails(userId);
                    isAddingNew = false;
                    userForm.getBtnUpdate().setText(prop.getProperty("btnUpdate"));
                    userForm.getLabelESC().setVisible(false);
                }
            }
        });
        
        userForm.getTextFieldSearch().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = userForm.getTextFieldSearch().getTxtSearchField().getText().trim();
                filterUsers(searchText);
            }
        });
        
        userForm.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && isAddingNew) {
                    cancelAddingNew();
                }
            }
        });
        
        userForm.setFocusTraversalKeysEnabled(false);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE && isAddingNew && e.getID() == KeyEvent.KEY_PRESSED) {
                cancelAddingNew();
                return true;
            }
            return false;
        });
    }
    
    /**
     * Thiết lập style cho bảng users
     */
    private void setupTableStyle() {
        if (userForm == null) return;
        
        TableUtils.applyDefaultStyle(userForm.getTableListUser());
        TableUtils.setBooleanColumns(userForm.getTableListUser(), 0);
    }

    //Check admin
    private void checkAdmin() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user.getRoleName().equals("Admin")) {
            userForm.getTxtPassword().setEnabled(true);
            userForm.getTxtPassword().setBackground(new Color(255, 255, 255));
            isAdmin = true;
        } else {
            userForm.getTxtPassword().setEnabled(false);
            userForm.getTxtPassword().setBackground(new Color(240, 240, 240));
            isAdmin = false;

        }
    }
    
    /**
     * Điền dữ liệu cho combobox vai trò từ enum Roles
     */
    private void populateRoleComboBox() {
        if (userForm == null) return;
        
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (Roles role : Roles.values()) {
            model.addElement(role.getRoleName());
        }
        userForm.getCbbRole().setModel(model);
    }
    
    /**
     * Tải danh sách tất cả người dùng
     */
    public void loadAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            displayUsers(users);
            // Refresh trạng thái đang sort
            if (userForm.getTableListUser().getRowSorter() != null) {
                userForm.getTableListUser().getRowSorter().setSortKeys(null);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(userForm, 
                    String.format(ErrorMessage.USER_LOAD_ERROR.toString(), e.getMessage()), 
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Hiển thị danh sách users lên bảng
     */
    private void displayUsers(List<User> users) {
        DefaultTableModel model = (DefaultTableModel) userForm.getTableListUser().getModel(); 
        model.setRowCount(0);
        
        int count = 1;
        for (User user : users) {
            String status = user.getIsActive() ? "Active" : "Inactive";
            String lastLoginStr = user.getLastLogin() != null ? 
                    user.getLastLogin().format(formatter) : "Chưa đăng nhập";
            
            model.addRow(new Object[]{
                count++, 
                user.getUserId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmployee().getPhoneNumber(),
                user.getEmployee().getEmail(),
                user.getRoleName(),
                status,
                lastLoginStr
            });
        }
    }
    
    /**
     * Lọc danh sách users theo từ khóa tìm kiếm
     */
    private void filterUsers(String searchText) {
        try {
            TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) userForm.getTableListUser().getRowSorter();
            if (sorter == null) {
                sorter = new TableRowSorter<>(userForm.getTableListUser().getModel());
                userForm.getTableListUser().setRowSorter(sorter);
            }
            
            TableUtils.applyFilter(sorter, searchText);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(userForm, 
                    String.format(ErrorMessage.USER_FILTER_ERROR.toString(), e.getMessage()), 
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Tải thông tin chi tiết của một user lên form
     */
    public void loadUserDetails(String userId) {
        try {
            Optional<User> userOpt = userService.getUserById(userId);
            
            if (userOpt.isPresent()) {
                selectedUser = userOpt.get();
                
   
                userForm.getTxtIDUser().setText(selectedUser.getUserId());
                userForm.getTxtNameEmployee().setText(selectedUser.getFullName());
                userForm.getTxtUsername().setText(selectedUser.getUsername());
                userForm.getTxtPhonenumber().setText(selectedUser.getEmployee().getPhoneNumber());
                userForm.getTxtEmail().setText(selectedUser.getEmployee().getEmail());
                getCbbStatus().setSelectedIndex(selectedUser.getIsActive() ? 0 : 1);
                
                userForm.getTxtPassword().setText(selectedUser.getPassword());
                
                userForm.getTxtPassword().setEnabled(isAdmin);
                
                if (selectedUser.getLastLogin() != null) {
                    userForm.getTxtLastlogin().setText(selectedUser.getLastLogin().format(formatter));
                } else {
                    userForm.getTxtLastlogin().setText("Chưa đăng nhập");
                }
                
                for (int i = 0; i < userForm.getCbbRole().getItemCount(); i++) {
                    if (userForm.getCbbRole().getItemAt(i).equals(selectedUser.getRoleName())) {
                        userForm.getCbbRole().setSelectedIndex(i);
                        break;
                    }
                }
                
                ButtonUtils.setKButtonEnabled(userForm.getBtnUpdate(), true);
                ButtonUtils.setKButtonEnabled(userForm.getBtnDelete(), true);
                ButtonUtils.setKButtonEnabled(userForm.getBtnResetPassword(), true);

            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(userForm, "Lỗi khi tải thông tin người dùng: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Chuẩn bị form để thêm mới user
     */
    private void prepareForAddingNew() {
        clearForm();
        userForm.getTableListUser().setEnabled(false);
        userForm.getTextFieldSearch().setEnabled(false);

        selectedEmployee = showDialogChooseEmployee();
        
        if (selectedEmployee != null) {
            userForm.getTxtIDUser().setText(userService.generateUserId());
            userForm.getTxtNameEmployee().setText(selectedEmployee.getFullName());
            userForm.getTxtEmail().setText(selectedEmployee.getEmail());
            userForm.getTxtPhonenumber().setText(selectedEmployee.getPhoneNumber());
            
            this.selectedEmployee = selectedEmployee;
            
            isAddingNew = true;
            

            userForm.getTxtPassword().setEnabled(true);
            userForm.getTxtPassword().setText("");
            userForm.getTxtUsername().requestFocus();
            
            String newUsername = selectedEmployee.getPhoneNumber(); 
            userForm.getTxtUsername().setText(newUsername);
            
            String autoGeneratedPassword = "123456"; 
            userForm.getTxtPassword().setText(autoGeneratedPassword);
            
            ButtonUtils.setKButtonEnabled(userForm.getBtnUpdate(), true);
            userForm.getBtnUpdate().setText(prop.getProperty("btnSave"));
            userForm.getLabelESC().setVisible(true);
        }else{
            ButtonUtils.setKButtonEnabled(userForm.getBtnUpdate(), false);
        }
    }
    
    /**
     * Hiển thị dialog để chọn nhân viên
     * @return Nhân viên được chọn hoặc null nếu không chọn
     */
    private Employee showDialogChooseEmployee() {
        DashboardForm dashboardForm = DashboardForm.getInstance();
        DialogChooseEmployee dialog = new DialogChooseEmployee(dashboardForm, true);
  
        
        DialogChooseEmployeeController dialogController = new DialogChooseEmployeeController(dialog, employeeService);
        
        dialog.setLocationRelativeTo(userForm);
        dialog.setVisible(true);
        
        return dialogController.getSelectedEmployee();
    }
    
    /**
     * Hủy thao tác thêm mới
     */
    private void cancelAddingNew() {
        clearForm();
        isAddingNew = false;
        userForm.getLabelESC().setVisible(false);
        
    }
    
    /**
     * Xóa trắng form
     */
    public void clearForm() {
        isAddingNew = false;
        userForm.getTxtIDUser().setText("");
        userForm.getTxtNameEmployee().setText("");
        userForm.getTxtUsername().setText("");
        userForm.getTxtPhonenumber().setText("");
        userForm.getTxtEmail().setText("");
        userForm.getTxtPassword().setText("");
        
        userForm.getTxtLastlogin().setText("");
        userForm.getCbbRole().setSelectedIndex(0);
        getCbbStatus().setSelectedIndex(0);
        selectedUser = null;
        selectedEmployee = null;
        ButtonUtils.setKButtonEnabled(userForm.getBtnUpdate(), false);
        ButtonUtils.setKButtonEnabled(userForm.getBtnDelete(), false);
        ButtonUtils.setKButtonEnabled(userForm.getBtnResetPassword(), false);
        userForm.getLabelESC().setVisible(false);
        userForm.getBtnUpdate().setText(prop.getProperty("btnUpdate"));
        userForm.getTableListUser().setEnabled(true);
        userForm.getTextFieldSearch().setEnabled(true);
        
    }
    
    /**
     * Thêm mới user
     */
    private void addNewUser() {
        try {
            if (selectedEmployee == null) {
                JOptionPane.showMessageDialog(userForm, "Vui lòng chọn nhân viên", 
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (userForm.getTxtUsername().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(userForm, "Vui lòng điền đầy đủ thông tin tài khoản", 
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            

            //mật khẩu tự động
            // String autoGeneratedPassword = UUID.randomUUID().toString();
            String autoGeneratedPassword = "123456";
            userForm.getTxtPassword().setText(autoGeneratedPassword);

            if (userService.isUsernameExists(userForm.getTxtUsername().getText().trim())) {
                JOptionPane.showMessageDialog(userForm, "Tên đăng nhập đã tồn tại", 
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            User newUser = new User();
            newUser.setUsername(userForm.getTxtUsername().getText().trim());
            
            String newPassword = PCrypt.hashPassword(autoGeneratedPassword);
            newUser.setPassword(newPassword);
            
            newUser.setEmployee(selectedEmployee);
            
            String roleName = userForm.getCbbRole().getSelectedItem().toString();
            Roles selectedRole = Roles.getByName(roleName);
            if (selectedRole != null) {
                newUser.setRoleID(selectedRole.getId());
                newUser.setRoleName(selectedRole.getRoleName());
            } else {
                newUser.setRoleID(userForm.getCbbRole().getSelectedIndex() + 1);
                newUser.setRoleName(roleName);
            }

            newUser.setIsActive(getCbbStatus().getSelectedIndex() == 0);
            newUser.setCreatedAt(LocalDateTime.now());
            
            int checkAdd = JOptionPane.showConfirmDialog(userForm, 
                    "Xác nhận thêm mới người dùng?", 
                    "Xác nhận thêm", JOptionPane.YES_NO_OPTION);
            if (checkAdd != JOptionPane.YES_OPTION) {
                return;
            }

            userService.addUser(newUser);
            
            isAddingNew = false;
            userForm.getLabelESC().setVisible(false);
            userForm.getBtnUpdate().setEnabled(true);
            userForm.getBtnDelete().setEnabled(true);
            userForm.getBtnUpdate().setText(prop.getProperty("btnUpdate"));
            loadAllUsers();
            
            JOptionPane.showMessageDialog(userForm, "Thêm người dùng thành công", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);

            clearForm();    
        } catch (Exception e) {
            JOptionPane.showMessageDialog(userForm, "Lỗi khi thêm người dùng: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();

        }
    }
    
    /**
     * Cập nhật thông tin user đã chọn
     */
    private void updateSelectedUser() {
        try {
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(userForm, "Vui lòng chọn người dùng cần cập nhật", 
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            User currentUser = SessionManager.getInstance().getCurrentUser();
            boolean isAdminOrManager = isAdmin || isManager;
            
            // Nếu không phải admin/manager, kiểm tra xem có đang cập nhật chính mình không
            if (!isAdminOrManager && !selectedUser.getUserId().equals(currentUser.getUserId())) {
                JOptionPane.showMessageDialog(userForm, "Bạn chỉ có thể cập nhật thông tin của mình", 
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Kiểm tra trường thông tin
            if (userForm.getTxtUsername().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(userForm, "Vui lòng điền đầy đủ thông tin", 
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Kiểm tra username đã tồn tại chưa
            if (!userForm.getTxtUsername().getText().trim().equals(selectedUser.getUsername()) &&
                    userService.isUsernameExists(userForm.getTxtUsername().getText().trim())) {
                JOptionPane.showMessageDialog(userForm, "Tên đăng nhập đã tồn tại", 
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Cập nhật thông tin người dùng
            selectedUser.setUsername(userForm.getTxtUsername().getText().trim());
            
            // Chỉ admin/manager mới có thể cập nhật các thông tin khác
            if (isAdminOrManager) {
                String txtPassword = userForm.getTxtPassword().getText().trim();
                if (isAdmin) {
                    if (txtPassword.startsWith("65536:")) {
                        selectedUser.setPassword(txtPassword);
                    } else {
                        selectedUser.setPassword(PCrypt.hashPassword(txtPassword));
                    }
                }
                
                selectedUser.setRoleName(userForm.getCbbRole().getSelectedItem().toString());
                selectedUser.setIsActive(getCbbStatus().getSelectedIndex() == 0);
            }
            
            selectedUser.setUpdatedAt(LocalDateTime.now());
            
            int confirm = JOptionPane.showConfirmDialog(userForm, 
                    "Xác nhận cập nhật thông tin?", 
                    "Xác nhận cập nhật", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            userService.updateUser(selectedUser);
            
            // Chỉ tải lại danh sách nếu là admin/manager
            if (isAdminOrManager) {
                loadAllUsers();
            }
            
            JOptionPane.showMessageDialog(userForm, "Cập nhật người dùng thành công", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(userForm, "Lỗi khi cập nhật người dùng: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Xóa user đã chọn
     */
    private void deleteSelectedUser() {
        try {
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(userForm, "Vui lòng chọn người dùng cần xóa", 
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int option = JOptionPane.showConfirmDialog(userForm, 
                    "Bạn có chắc chắn muốn xóa người dùng này?", 
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            
            if (option == JOptionPane.YES_OPTION) {
                userService.deleteUser(selectedUser.getUserId());
                
                loadAllUsers();
                clearForm();
                
                JOptionPane.showMessageDialog(userForm, "Xóa người dùng thành công", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(userForm, "Lỗi khi xóa người dùng: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();

        }
    }
    
    /**
     * Đặt lại mật khẩu cho user
     */
    private void resetPassword() {
        try {
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(userForm, "Vui lòng chọn người dùng cần đặt lại mật khẩu", 
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            //xác nhận
            
            String confirmMessage = "Bạn có chắc chắn muốn đặt lại mật khẩu cho User: " + selectedUser.getUsername() + "?";
            int option = JOptionPane.showConfirmDialog(userForm, confirmMessage, 
                    "Xác nhận đặt lại mật khẩu", JOptionPane.YES_NO_OPTION);
            if (option != JOptionPane.YES_OPTION) {
                return;
            }

            // String newPassword = UUID.randomUUID().toString().substring(0, 8);

            String newPassword = "123456";
                                   
            String hashedPassword = PCrypt.hashPassword(newPassword);
            
            selectedUser.setPassword(hashedPassword);
            selectedUser.setUpdatedAt(LocalDateTime.now());
            
            userService.updateUser(selectedUser);
            
            JOptionPane.showMessageDialog(userForm, "Đặt lại mật khẩu thành công", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);

            JOptionPane.showMessageDialog(userForm, "Mật khẩu mới: " + newPassword, 
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(userForm, "Lỗi khi đặt lại mật khẩu: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            
        }
    }
    

    /**
     * Kiểm tra quyền của người dùng hiện tại và điều chỉnh 
     */
    private void checkUserPermissions() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        String roleName = currentUser.getRoleName();
        
        boolean isAdminOrManager = roleName.equals("Admin") || roleName.equals("Manager");
        isManager = roleName.equals("Manager");
        
        userForm.getBtnAdd().setVisible(isAdminOrManager);
        userForm.getBtnDelete().setVisible(isAdminOrManager);
        userForm.getPanelPassword().setVisible(isAdminOrManager);
        

        if (!isAdminOrManager) {
            try {
                Optional<User> userOpt = userService.findUserByUsername(currentUser.getUsername());
                if (userOpt.isPresent()) {
                    loadUserDetails(userOpt.get().getUserId());
                    displayUsers(List.of(userOpt.get()));

                    userForm.getTxtNameEmployee().setEditable(false);
                    userForm.getTxtEmail().setEditable(false);
                    userForm.getTxtPhonenumber().setEditable(false);
                    userForm.getCbbRole().setEnabled(false);
                    userForm.getCbbStatus().setEnabled(false); 
                    
                    userForm.getTextFieldSearch().setVisible(false);
                    userForm.getBtnResetPassword().setText(prop.getProperty("btnChangePassword"));                           
                                        
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(userForm, "Lỗi khi tải thông tin người dùng", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Đổi mật khẩu người dùng
     */
    private void changePassword() {
        try {
            User currentUser = SessionManager.getInstance().getCurrentUser();
            

            DashboardForm dashboardForm = DashboardForm.getInstance();
            DialogChangePassword dialog = new DialogChangePassword(dashboardForm, true);
            
            dialog.getTxtUsername().setText(currentUser.getUsername());
            dialog.getTxtUsername().setEditable(false);
            
            dialog.getBtnConfirm().addActionListener(e -> {
                String oldPassword = dialog.getTxtOldPassword().getText().trim();
                String newPassword = dialog.getTxtNewPassword().getText().trim();
                String confirmPassword = dialog.getTxtConfrimPassword().getText().trim();
                
                if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin", 
                            "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(dialog, "Mật khẩu mới không khớp với xác nhận mật khẩu", 
                            "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Kiểm tra độ dài mật khẩu mới
                if (newPassword.length() < 6) {
                    JOptionPane.showMessageDialog(dialog, "Mật khẩu mới phải có ít nhất 6 ký tự", 
                            "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    // Xác thực mật khẩu cũ
                    if (!PCrypt.checkPassword(oldPassword, currentUser.getPassword())) {
                        JOptionPane.showMessageDialog(dialog, "Mật khẩu hiện tại không đúng", 
                                "Thông báo", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    String hashedNewPassword = PCrypt.hashPassword(newPassword);
                    
                    boolean success = userService.updatePassword(currentUser.getUsername(), hashedNewPassword);
                    
                    if (success) {
                        currentUser.setPassword(hashedNewPassword);
                        JOptionPane.showMessageDialog(dialog, "Đổi mật khẩu thành công", 
                                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Đổi mật khẩu thất bại", 
                                "Thông báo", JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Lỗi khi đổi mật khẩu: " + ex.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });
            
            dialog.getBtnClose().addActionListener(e -> dialog.dispose());
            
            dialog.setLocationRelativeTo(userForm);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(userForm, "Lỗi khi mở hộp thoại đổi mật khẩu: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Lấy ComboBox Status từ form
     */
    private JComboBox<String> getCbbStatus() {
        return userForm.getCbbStatus();
    }
    
    private void refreshForm() {
        clearForm();
        if(isAdmin || isManager) {
            loadAllUsers();
        }
        checkUserPermissions();
        
    }
}