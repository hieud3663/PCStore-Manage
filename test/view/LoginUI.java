import javax.swing.*;
import java.awt.*;

public class LoginUI extends JFrame {
    public LoginUI() {
        setTitle("Đăng nhập");
        setSize(350, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding

        // ==== Phần trên: Icon ====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel iconLabel = new JLabel(new ImageIcon("login_icon.png")); // Thay bằng icon của bạn
        topPanel.add(iconLabel);

        // ==== Phần giữa: Nhập liệu ====
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel usernameLabel = new JLabel("Tên đăng nhập");
        JTextField usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Mật khẩu");
        JPasswordField passwordField = new JPasswordField(20);
        
        JLabel forgotPasswordLabel = new JLabel("<html><a href='#'>Quên mật khẩu?</a></html>");
        forgotPasswordLabel.setForeground(Color.BLUE);

        // Căn lề trái
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        forgotPasswordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        centerPanel.add(usernameLabel);
        centerPanel.add(usernameField);
        centerPanel.add(Box.createVerticalStrut(10)); // Khoảng cách
        centerPanel.add(passwordLabel);
        centerPanel.add(passwordField);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(forgotPasswordLabel);

        // ==== Phần dưới: Nút đăng nhập ====
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Đăng nhập");
        loginButton.setPreferredSize(new Dimension(150, 40)); // Kích thước nút

        bottomPanel.add(loginButton);

        // Thêm vào panel chính
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginUI::new);
    }
}
