/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.pcstore.view;

import com.pcstore.controller.LoginController;
import com.pcstore.model.User;
import com.pcstore.utils.DatabaseConnection;
import raven.toast.Notifications;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author MSII
 */
public class LoginForm extends JFrame {

    private LoginController loginController;

    private static LoginForm instance;

    private JPanel PanelContentLogin;
    private com.k33ptoo.components.KGradientPanel PanelLogin;
    private com.k33ptoo.components.KGradientPanel PanelMainLogin;
    private com.k33ptoo.components.KButton btnLogin;
    private JLabel lbUsername;
    private JLabel lbPassword;
    private JLabel lbTitleLogin;
    private JTextField lbForgetPassword;
    private JLabel lbPicture;
    private JLabel titleName;
    private JLabel titleWelcome;
    private JPasswordField txtPassword;
    private JTextField txtUsername;

    public static LoginForm getInstance() {
        if (instance == null) {
            instance = new LoginForm();
        }
        return instance;
    }

    public static void restartInstance() {
        if (instance != null) {
            instance.dispose();
        }
        instance = new LoginForm();
    }

    public static void resetInstance() {
        instance = null;
    }

    public LoginForm() {


        initComponents();

        loginController = new LoginController();

        // Thêm sự kiện window listener để xử lý đóng cửa sổ
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                // Đóng kết nối khi đóng cửa sổ
                DatabaseConnection.getInstance().closeConnection();
            }
        });


        SwingUtilities.invokeLater(() -> {
            txtUsername.requestFocusInWindow();
        });

       checkLogin(); //auto login

    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        PanelMainLogin = new com.k33ptoo.components.KGradientPanel();
        PanelContentLogin = new JPanel();
        titleName = new JLabel();
        titleWelcome = new JLabel();
        lbPicture = new JLabel();
        PanelLogin = new com.k33ptoo.components.KGradientPanel();
        btnLogin = new com.k33ptoo.components.KButton();
        lbUsername = new JLabel();
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        lbPassword = new JLabel();
        lbTitleLogin = new JLabel();
        lbForgetPassword = new JTextField();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setAutoRequestFocus(true);
        setResizable(false);

        PanelMainLogin.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));
        PanelMainLogin.setkEndColor(new Color(102, 153, 255));
        PanelMainLogin.setkGradientFocus(100);
        PanelMainLogin.setkStartColor(new Color(153, 255, 153));
        PanelMainLogin.setMinimumSize(new Dimension(1060, 521));
        PanelMainLogin.setOpaque(false);
        PanelMainLogin.setPreferredSize(new Dimension(1100, 522));
        PanelMainLogin.setLayout(new GridLayout(1, 2, 60, 0));

        PanelContentLogin.setOpaque(false);
        PanelContentLogin.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 20));

        titleWelcome.setFont(new Font("Segoe UI", 1, 20)); // NOI18N
        titleWelcome.setForeground(new Color(255, 255, 255));
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
        titleWelcome.setText(bundle.getString("titleWelcome")); // NOI18N
        PanelContentLogin.add(titleWelcome);

        titleName.setFont(new Font("Segoe UI", 1, 23)); // NOI18N
        titleName.setForeground(new Color(255, 255, 255));
        titleName.setText(bundle.getString("titleName")); // NOI18N
        PanelContentLogin.add(titleName);

        lbPicture.setHorizontalAlignment(SwingConstants.CENTER);
        lbPicture.setIcon(new ImageIcon(getClass().getResource("/com/pcstore/resources/icon/grap.png"))); // NOI18N
        PanelContentLogin.add(lbPicture);

        PanelMainLogin.add(PanelContentLogin);

        PanelLogin.setkBorderRadius(30);
        PanelLogin.setkEndColor(new Color(255, 255, 255));
        PanelLogin.setkStartColor(new Color(255, 255, 255));
        PanelLogin.setkTransparentControls(false);
        PanelLogin.setOpaque(false);
        PanelLogin.setPreferredSize(new Dimension(335, 459));
        PanelLogin.setLayout(new GridBagLayout());

        btnLogin.setText(bundle.getString("btnLogin")); // NOI18N
        btnLogin.setFont(new Font("Segoe UI", 0, 14)); // NOI18N
        btnLogin.setkBorderRadius(40);
        btnLogin.setkHoverForeGround(new Color(255, 255, 255));
        btnLogin.setMaximumSize(new Dimension(200, 45));
        btnLogin.setMinimumSize(new Dimension(185, 45));
        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                btnLoginMouseClicked(evt);
            }
        });
        btnLogin.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                btnLoginKeyPressed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        PanelLogin.add(btnLogin, gridBagConstraints);

        lbUsername.setFont(new Font("Segoe UI", 0, 13)); // NOI18N
        lbUsername.setForeground(new Color(102, 102, 102));
        lbUsername.setText(bundle.getString("lbUsername")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(28, 48, 0, 0);
        PanelLogin.add(lbUsername, gridBagConstraints);

        txtUsername.setFont(new Font("Segoe UI", 0, 13)); // NOI18N
        txtUsername.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(204, 204, 204)));
        txtUsername.setCursor(new Cursor(Cursor.TEXT_CURSOR));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 182;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(12, 48, 0, 51);
        PanelLogin.add(txtUsername, gridBagConstraints);

        txtPassword.setFont(new Font("Segoe UI", 0, 13)); // NOI18N
        txtPassword.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(204, 204, 204)));
        txtPassword.setCursor(new Cursor(Cursor.TEXT_CURSOR));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 182;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(12, 48, 0, 51);
        PanelLogin.add(txtPassword, gridBagConstraints);

        lbPassword.setFont(new Font("Segoe UI", 0, 13)); // NOI18N
        lbPassword.setForeground(new Color(102, 102, 102));
        lbPassword.setText(bundle.getString("lbPassword")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 31;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(24, 48, 0, 0);
        PanelLogin.add(lbPassword, gridBagConstraints);

        lbTitleLogin.setFont(new Font("Segoe UI", 1, 20)); // NOI18N
        lbTitleLogin.setHorizontalAlignment(SwingConstants.CENTER);
        lbTitleLogin.setIcon(new ImageIcon(getClass().getResource("/com/pcstore/resources/icon/login.png"))); // NOI18N
        lbTitleLogin.setText(bundle.getString("titleLogin")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = -93;
        gridBagConstraints.ipady = -109;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(6, 100, 0, 0);
        PanelLogin.add(lbTitleLogin, gridBagConstraints);

        lbForgetPassword.setFont(new Font("Segoe UI", 0, 14)); // NOI18N
        lbForgetPassword.setForeground(new Color(0, 153, 255));
        lbForgetPassword.setText(bundle.getString("lbForgetPassword")); // NOI18N
        lbForgetPassword.setBorder(null);
        lbForgetPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbForgetPassword.setRequestFocusEnabled(false);
        lbForgetPassword.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                lbForgetPasswordMouseClicked(evt);
            }
        });
        lbForgetPassword.setFocusable(false);
        // lbForgetPassword.setEditable(false);    

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 55;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(6, 44, 0, 51);
        PanelLogin.add(lbForgetPassword, gridBagConstraints);

        PanelMainLogin.add(PanelLogin);

        getContentPane().add(PanelMainLogin, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private void btnLoginKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            checkLogin();
        }
    }

    private void btnLoginMouseClicked(MouseEvent evt) {
        checkLogin();
    }

    private void lbForgetPasswordMouseClicked(MouseEvent evt) {

        new DialogForgotPasswordForm(this, true).setVisible(true);

    }


    public void checkLogin() {
        // String username = txtUsername.getText();
        // String password = txtPassword.getText();

       String username = "admin";
       String password = "admin";

        if (username.isEmpty() && password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập và mật khẩu không được để trống", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }

        try {
            User user = loginController.authenticate(username, password);

            if (user != null) {
                this.dispose();

                // JOptionPane.showMessageDialog(this, "Đăng nhập thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Đăng nhập thành công");

                this.resetInstance();
            } else {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
