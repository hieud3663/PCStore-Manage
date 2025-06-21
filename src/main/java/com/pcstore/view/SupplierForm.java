package com.pcstore.view;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.pcstore.model.Supplier;
import com.pcstore.service.ServiceFactory;
import com.pcstore.service.SupplierService;
import com.pcstore.utils.TableUtils;
import com.pcstore.utils.TextFieldSearch;
import com.k33ptoo.components.KButton;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;


/**
 * Form quản lý nhà cung cấp với thiết kế hiện đại
 * 
 * @author MSII
 */
public class SupplierForm extends JPanel {

    // Variables declaration
    private JPanel panelMain;
    private JPanel panelTop;
    private JPanel panelContent;

    private JLabel lbTitle;

    private SupplierService supplierService;

    /**
     * Creates new form SupplierForm
     */
    public SupplierForm() {
        createTopPanel();
        initComponents();
    }

    private void initService() {

        try {
            this.supplierService = ServiceFactory.getInstance().getSupplierService();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Main panel
        panelMain = new JPanel(new BorderLayout());
        panelMain.setBackground(new Color(249, 250, 251));

        panelMain.add(panelTop, BorderLayout.PAGE_START);

        add(panelMain, BorderLayout.CENTER);
    }

    private void createTopPanel() {
        panelTop = new JPanel(new BorderLayout(10, 0));
        panelTop.setBackground(Color.WHITE);
        panelTop.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(204, 204, 204)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panelTop.setPreferredSize(new Dimension(1200, 70));

        // Title
        lbTitle = new JLabel("QUẢN LÝ NHÀ CUNG CẤP");
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbTitle.setForeground(new Color(17, 24, 39));
        lbTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    }

}