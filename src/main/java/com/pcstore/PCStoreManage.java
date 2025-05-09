/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.pcstore;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.pcstore.controller.LoginController;
import com.pcstore.utils.LocaleManager;
import com.pcstore.view.LoginForm;


// regex: \/\/\s*(GEN-FIRST|GEN-LAST).*$
/**
 *
 * @author MSII
 */
public class PCStoreManage {

    public static void main(String[] args) {

        // LocaleManager.getInstance().setLocale(LocaleManager.LOCALE_VIETNAM);
        
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {

        LoginForm loginForm = new LoginForm();
        loginForm.setVisible(true);       
        // DashboardForm sẽ được tạo sau khi đăng nhập thành công
        // trong LoginController
    });
    }
}
