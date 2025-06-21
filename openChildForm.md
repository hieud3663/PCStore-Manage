## Hướng dẫn tạo form thanh toán Modal với hiệu ứng làm mờ form chính
1. Tổng quan về JDialog và Modal Window
Trong Java Swing, có hai loại cửa sổ chính:

JFrame: Cửa sổ độc lập, có thể tự do di chuyển và tương tác.
JDialog: Cửa sổ phụ thuộc vào một cửa sổ chính (owner), có thể là modal hoặc non-modal.
Modal Dialog là một cửa sổ đặc biệt:

Khi mở, tự động làm mờ và khóa tương tác với cửa sổ chính
Người dùng phải xử lý xong Dialog trước khi quay lại cửa sổ chính
Thích hợp cho các tác vụ yêu cầu hoàn thành trước khi tiếp tục


2. Chuyển đổi từ JFrame sang JDialog

### Bước 1: Sửa khai báo lớp
// Thay đổi từ
public class PayForm extends javax.swing.JFrame {
    // ...
}

// Thành
public class PayForm extends javax.swing.JDialog {
    // ...
}

### Bước 2: Thêm constructor với tham số owner

public PayForm(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
    setLocationRelativeTo(parent);
}

// Giữ constructor cũ cho backward compatibility
public PayForm() {
    super();
    initComponents();
}

### Bước 3: Sửa DefaultCloseOperation
// Thay đổi từ (nếu có)
setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

// Thành
setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

3. Gọi Dialog Modal từ form chính

private void btnPayMouseClicked(java.awt.event.MouseEvent evt) {
    try {
        UIManager.setLookAndFeel(new FlatLightLaf());
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Lấy instance của Dashboard (singleton)
    Dashboard dashboard = Dashboard.getInstance();
    
    // Tạo dialog thanh toán với owner là dashboard và modal=true
    PayForm payForm = new PayForm(dashboard, true);
    
    // Hiển thị dialog ở giữa cửa sổ chính
    payForm.setVisible(true);
    
    // Xử lý kết quả sau khi dialog đóng (nếu cần)
    // Ví dụ: if (payForm.isPaymentSuccessful()) { ... }
}

4. Xử lý việc đóng Dialog

private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
    this.dispose(); // Đóng dialog và giải phóng tài nguyên
}