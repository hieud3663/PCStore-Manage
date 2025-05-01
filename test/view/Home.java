import javax.swing.*;
import java.awt.*;

public class Home extends JFrame {
    public Home() {
        setTitle("Computer Store Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // North Panel (Profile & Statistics)
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        JPanel profilePanel = new JPanel();
        profilePanel.setBorder(BorderFactory.createTitledBorder("Profile"));
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.add(new JLabel("Họ tên: Nguyễn Văn A"));
        profilePanel.add(new JLabel("Vai trò: Quản lý"));
        profilePanel.add(new JLabel("Mã NV: NV001"));
        profilePanel.add(new JLabel("Đăng nhập lúc: 09:25 AM"));
        profilePanel.add(Box.createVerticalStrut(10));
        profilePanel.add(new JButton("Chỉnh sửa thông tin"));
        profilePanel.add(new JButton("Đăng xuất"));

        JPanel statsPanel = new JPanel();
        statsPanel.setBorder(BorderFactory.createTitledBorder("Thống kê hôm nay"));
        statsPanel.setLayout(new GridLayout(4, 1));
        statsPanel.add(new JLabel("Đơn hàng: 12"));
        statsPanel.add(new JLabel("Doanh thu: 45.200.000 VNĐ"));
        statsPanel.add(new JLabel("Hàng tồn kho thấp: 5"));
        statsPanel.add(new JLabel("Khách hàng mới: 3"));

        topPanel.add(profilePanel);
        topPanel.add(statsPanel);

        // Center Panel (Main content)
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createTitledBorder("Hoạt động hôm nay"));
        mainPanel.setLayout(new BorderLayout());

        JTextArea contentArea = new JTextArea("Biểu đồ, danh sách đơn hàng hoặc lịch hẹn hiển thị ở đây");
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        mainPanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);

        // Add to Frame
        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Home().setVisible(true);
        });
    }
}