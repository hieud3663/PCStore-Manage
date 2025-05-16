/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import java.awt.event.MouseListener;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.pcstore.controller.EmployeeController;
import com.pcstore.model.enums.EmployeePositionEnum;
import com.pcstore.utils.TableStyleUtil;

/**
 *
 * @author MSII
 */
public class EmployeeForm extends javax.swing.JPanel {


    private TableRowSorter<TableModel> tableListEmployeeSorter;
    private EmployeeController employeeController;

    private com.k33ptoo.components.KButton btnAdd;
    private com.k33ptoo.components.KButton btnChangeImage;
    private javax.swing.JButton btnChooseBirthday;
    private com.k33ptoo.components.KButton btnDelete;
    private com.k33ptoo.components.KButton btnExportExcel;
    private javax.swing.ButtonGroup btnGroupGender;
    private com.k33ptoo.components.KButton btnRefresh;
    private javax.swing.JButton btnResetSort;
    private com.k33ptoo.components.KButton btnUpdate;
    private javax.swing.JComboBox<String> cbbPositionEmployee;
    private javax.swing.JComboBox<String> cbbSort;
    private javax.swing.JComboBox<String> cbbSortEmployee;
    private com.raven.datechooser.DateChooser dateChooserBirthday;
    private javax.swing.JRadioButton jRadioFemale;
    private javax.swing.JRadioButton jRadioMale;
    private javax.swing.JScrollPane jScrollPaneListEmployee;
    private javax.swing.JLabel labeCreateUpdate;
    private javax.swing.JLabel labelCreateAt;
    private javax.swing.JLabel labelESC;
    private javax.swing.JLabel labelSort;
    private javax.swing.JLabel lbBirthdayEmployee;
    private javax.swing.JLabel lbEmail;
    private javax.swing.JLabel lbGender;
    private javax.swing.JLabel lbIDEmployee;
    private javax.swing.JLabel lbName;
    private javax.swing.JLabel lbPhonenumber;
    private javax.swing.JLabel lbPosition;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel paneIDEmployee;
    private javax.swing.JPanel panelAction;
    private com.k33ptoo.components.KGradientPanel panelAvatar;
    private javax.swing.JPanel panelBirthdayEmployee;
    private javax.swing.JPanel panelCreateAt;
    private javax.swing.JPanel panelCreateUpdate;
    private javax.swing.JPanel panelDetail;
    private javax.swing.JPanel panelEmail;
    private javax.swing.JPanel panelEmpty;
    private javax.swing.JPanel panelExport;
    private javax.swing.JPanel panelGender;
    private javax.swing.JPanel panelHeader;
    private com.k33ptoo.components.KGradientPanel panelImage;
    private javax.swing.JPanel panelInfoDetail;
    private javax.swing.JPanel panelListEmployee;
    private javax.swing.JPanel panelName;
    private javax.swing.JPanel panelPhonenumber;
    private javax.swing.JPanel panelPosition;
    private javax.swing.JPanel panelSort;
    private javax.swing.JTable tableListEmployee;
    private com.pcstore.utils.TextFieldSearch textFieldSearch;
    private javax.swing.JTextField txtCreateAt;
    private javax.swing.JTextField txtCreateUpdate;
    private javax.swing.JTextField txtDateOfBirthEmployee;
    private javax.swing.JTextField txtEmailEmployee;
    private javax.swing.JTextField txtIDEmployee;
    private javax.swing.JTextField txtNameEmployee;
    private javax.swing.JTextField txtPhonenumberEmployee;

    public EmployeeForm() {
        initComponents();
        setupCusmizeTable();
        labelESC.setVisible(false);  

        // xóa actione mouseListener trong txtDateOfBirth
        for (MouseListener listener : txtDateOfBirthEmployee.getMouseListeners()) {
            txtDateOfBirthEmployee.removeMouseListener(listener);
        }
        txtDateOfBirthEmployee.setText("dd-mm-yyyy");

        employeeController = new EmployeeController(this);

        populatePositionComboBox();
    }

    
    @SuppressWarnings("unchecked")
    private void initComponents() {

        btnGroupGender = new javax.swing.ButtonGroup();
        dateChooserBirthday = new com.raven.datechooser.DateChooser();
        panelHeader = new javax.swing.JPanel();
        lbTitle = new javax.swing.JLabel();
        panelAction = new javax.swing.JPanel();
        btnAdd = new com.k33ptoo.components.KButton();
        btnUpdate = new com.k33ptoo.components.KButton();
        btnDelete = new com.k33ptoo.components.KButton();
        btnRefresh = new com.k33ptoo.components.KButton();
        panelExport = new javax.swing.JPanel();
        btnExportExcel = new com.k33ptoo.components.KButton();
        panelDetail = new javax.swing.JPanel();
        panelImage = new com.k33ptoo.components.KGradientPanel();
        btnChangeImage = new com.k33ptoo.components.KButton();
        panelAvatar = new com.k33ptoo.components.KGradientPanel();
        panelInfoDetail = new javax.swing.JPanel();
        paneIDEmployee = new javax.swing.JPanel();
        lbIDEmployee = new javax.swing.JLabel();
        txtIDEmployee = new javax.swing.JTextField();
        panelName = new javax.swing.JPanel();
        lbName = new javax.swing.JLabel();
        txtNameEmployee = new javax.swing.JTextField();
        panelGender = new javax.swing.JPanel();
        lbGender = new javax.swing.JLabel();
        jRadioMale = new javax.swing.JRadioButton();
        jRadioFemale = new javax.swing.JRadioButton();
        panelBirthdayEmployee = new javax.swing.JPanel();
        lbBirthdayEmployee = new javax.swing.JLabel();
        txtDateOfBirthEmployee = new javax.swing.JTextField();
        btnChooseBirthday = new javax.swing.JButton();
        panelPhonenumber = new javax.swing.JPanel();
        lbPhonenumber = new javax.swing.JLabel();
        txtPhonenumberEmployee = new javax.swing.JTextField();
        panelPosition = new javax.swing.JPanel();
        lbPosition = new javax.swing.JLabel();
        cbbPositionEmployee = new javax.swing.JComboBox<>();
        panelEmail = new javax.swing.JPanel();
        lbEmail = new javax.swing.JLabel();
        txtEmailEmployee = new javax.swing.JTextField();
        panelCreateUpdate = new javax.swing.JPanel();
        labeCreateUpdate = new javax.swing.JLabel();
        txtCreateUpdate = new javax.swing.JTextField();
        panelCreateAt = new javax.swing.JPanel();
        labelCreateAt = new javax.swing.JLabel();
        txtCreateAt = new javax.swing.JTextField();
        panelEmpty = new javax.swing.JPanel();
        labelESC = new javax.swing.JLabel();
        panelSort = new javax.swing.JPanel();
        textFieldSearch = new com.pcstore.utils.TextFieldSearch();
        labelSort = new javax.swing.JLabel();
        cbbSortEmployee = new javax.swing.JComboBox<>();
        cbbSort = new javax.swing.JComboBox<>();
        btnResetSort = new javax.swing.JButton();
        panelListEmployee = new javax.swing.JPanel();
        jScrollPaneListEmployee = new javax.swing.JScrollPane();
        tableListEmployee = new javax.swing.JTable();

        dateChooserBirthday.setTextRefernce(txtDateOfBirthEmployee);

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1197, 713));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        panelHeader.setOpaque(false);
        panelHeader.setPreferredSize(new java.awt.Dimension(100, 40));
        panelHeader.setLayout(new java.awt.BorderLayout());

        lbTitle.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lbTitle.setForeground(new java.awt.Color(30, 113, 195));
        lbTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
        lbTitle.setText(bundle.getString("txtMenuIEmployee")); // NOI18N
        lbTitle.setToolTipText(bundle.getString("txtMenuSell")); // NOI18N
        lbTitle.setPreferredSize(new java.awt.Dimension(172, 30));
        panelHeader.add(lbTitle, java.awt.BorderLayout.PAGE_START);

        add(panelHeader);

        panelAction.setOpaque(false);
        panelAction.setPreferredSize(new java.awt.Dimension(1197, 60));
        panelAction.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/user-add_2.png"))); // NOI18N
        btnAdd.setText(bundle.getString("btnAddEmployee")); // NOI18N
        btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnAdd.setIconTextGap(15);
        btnAdd.setkAllowGradient(false);
        btnAdd.setkBackGroundColor(new java.awt.Color(26, 162, 106));
        btnAdd.setkBorderRadius(20);
        btnAdd.setkEndColor(new java.awt.Color(0, 255, 51));
        btnAdd.setkHoverColor(new java.awt.Color(26, 190, 62));
        btnAdd.setkHoverEndColor(new java.awt.Color(0, 204, 255));
        btnAdd.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnAdd.setkHoverStartColor(new java.awt.Color(0, 204, 255));
        btnAdd.setkStartColor(new java.awt.Color(0, 204, 255));
        btnAdd.setPreferredSize(new java.awt.Dimension(185, 35));
        panelAction.add(btnAdd);

        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/user-pen.png"))); // NOI18N
        btnUpdate.setText(bundle.getString("btnUpdate")); // NOI18N
        btnUpdate.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnUpdate.setIconTextGap(15);
        btnUpdate.setkAllowGradient(false);
        btnUpdate.setkBackGroundColor(new java.awt.Color(86, 167, 233));
        btnUpdate.setkBorderRadius(20);
        btnUpdate.setkEndColor(new java.awt.Color(0, 153, 153));
        btnUpdate.setkHoverColor(new java.awt.Color(122, 196, 235));
        btnUpdate.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnUpdate.setkHoverStartColor(new java.awt.Color(102, 102, 255));
        btnUpdate.setPreferredSize(new java.awt.Dimension(185, 35));
        panelAction.add(btnUpdate);

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/trash.png"))); // NOI18N
        btnDelete.setText(bundle.getString("btnDeleteEmployee")); // NOI18N
        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnDelete.setIconTextGap(10);
        btnDelete.setkAllowGradient(false);
        btnDelete.setkBackGroundColor(new java.awt.Color(226, 21, 29));
        btnDelete.setkBorderRadius(20);
        btnDelete.setkEndColor(new java.awt.Color(255, 102, 51));
        btnDelete.setkHoverColor(new java.awt.Color(252, 83, 0));
        btnDelete.setkHoverEndColor(new java.awt.Color(255, 0, 51));
        btnDelete.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnDelete.setkHoverStartColor(new java.awt.Color(255, 102, 0));
        btnDelete.setkStartColor(new java.awt.Color(255, 0, 51));
        btnDelete.setMinimumSize(new java.awt.Dimension(140, 25));
        btnDelete.setPreferredSize(new java.awt.Dimension(140, 35));
        btnDelete.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/trash.png"))); // NOI18N
        panelAction.add(btnDelete);

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/refresh.png"))); // NOI18N
        btnRefresh.setText(bundle.getString("btnRefresh")); // NOI18N
        btnRefresh.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnRefresh.setIconTextGap(20);
        btnRefresh.setkAllowGradient(false);
        btnRefresh.setkBackGroundColor(new java.awt.Color(144, 194, 244));
        btnRefresh.setkBorderRadius(20);
        btnRefresh.setkEndColor(new java.awt.Color(153, 204, 255));
        btnRefresh.setkHoverColor(new java.awt.Color(144, 206, 245));
        btnRefresh.setkHoverEndColor(new java.awt.Color(102, 204, 255));
        btnRefresh.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnRefresh.setkHoverStartColor(new java.awt.Color(102, 204, 255));
        btnRefresh.setkStartColor(new java.awt.Color(153, 204, 255));
        btnRefresh.setPreferredSize(new java.awt.Dimension(140, 35));
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        panelAction.add(btnRefresh);

        panelExport.setBackground(new java.awt.Color(255, 255, 255));
        panelExport.setOpaque(false);
        panelExport.setPreferredSize(new java.awt.Dimension(350, 35));
        panelExport.setLayout(new java.awt.BorderLayout());

        btnExportExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/xls.png"))); // NOI18N
        btnExportExcel.setText(bundle.getString("btnExportExcel")); // NOI18N
        btnExportExcel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnExportExcel.setkAllowGradient(false);
        btnExportExcel.setkBackGroundColor(new java.awt.Color(26, 176, 114));
        btnExportExcel.setkHoverColor(new java.awt.Color(26, 204, 89));
        btnExportExcel.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnExportExcel.setPreferredSize(new java.awt.Dimension(120, 45));
        panelExport.add(btnExportExcel, java.awt.BorderLayout.LINE_END);

        panelAction.add(panelExport);

        add(panelAction);

        panelDetail.setBackground(new java.awt.Color(255, 255, 255));
        panelDetail.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("txtDetails"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(24, 100, 178))); // NOI18N
        panelDetail.setLayout(new java.awt.BorderLayout());

        panelImage.setBackground(new java.awt.Color(255, 255, 255));
        panelImage.setkBorderColor(new java.awt.Color(0, 204, 204));
        panelImage.setkBorderRadius(20);
        panelImage.setkFillBackground(false);
        panelImage.setMinimumSize(new java.awt.Dimension(60, 150));
        panelImage.setPreferredSize(new java.awt.Dimension(150, 100));
        panelImage.setLayout(new java.awt.BorderLayout(10, 5));

        btnChangeImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/edit.png"))); // NOI18N
        btnChangeImage.setText(bundle.getString("btnChangeImage")); // NOI18N
        btnChangeImage.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnChangeImage.setIconTextGap(15);
        btnChangeImage.setkAllowGradient(false);
        btnChangeImage.setkBackGroundColor(new java.awt.Color(0, 102, 193));
        btnChangeImage.setkHoverColor(new java.awt.Color(0, 0, 215));
        btnChangeImage.setPreferredSize(new java.awt.Dimension(185, 30));
        panelImage.add(btnChangeImage, java.awt.BorderLayout.PAGE_END);

        panelAvatar.setBackground(new java.awt.Color(255, 255, 255));
        panelAvatar.setkBorderColor(new java.awt.Color(51, 255, 51));
        panelAvatar.setkBorderRadius(20);
        panelAvatar.setkFillBackground(false);
        panelAvatar.setLayout(new java.awt.BorderLayout());
        panelImage.add(panelAvatar, java.awt.BorderLayout.CENTER);

        panelDetail.add(panelImage, java.awt.BorderLayout.LINE_START);

        panelInfoDetail.setBackground(new java.awt.Color(255, 255, 255));
        panelInfoDetail.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        panelInfoDetail.setForeground(new java.awt.Color(30, 113, 195));
        panelInfoDetail.setPreferredSize(new java.awt.Dimension(400, 280));
        panelInfoDetail.setLayout(new java.awt.GridLayout(4, 3, 30, 23));

        paneIDEmployee.setOpaque(false);
        paneIDEmployee.setLayout(new java.awt.BorderLayout(15, 0));

        lbIDEmployee.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbIDEmployee.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbIDEmployee.setText(bundle.getString("lbIDEmployee")); // NOI18N
        lbIDEmployee.setPreferredSize(new java.awt.Dimension(120, 16));
        paneIDEmployee.add(lbIDEmployee, java.awt.BorderLayout.LINE_START);

        txtIDEmployee.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        txtIDEmployee.setForeground(new java.awt.Color(30, 113, 195));
        txtIDEmployee.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 153, 153)));
        txtIDEmployee.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        txtIDEmployee.setEnabled(false);
        paneIDEmployee.add(txtIDEmployee, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(paneIDEmployee);

        panelName.setOpaque(false);
        panelName.setLayout(new java.awt.BorderLayout(15, 0));

        lbName.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbName.setText(bundle.getString("lbName")); // NOI18N
        lbName.setPreferredSize(new java.awt.Dimension(120, 16));
        panelName.add(lbName, java.awt.BorderLayout.LINE_START);

        txtNameEmployee.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtNameEmployee.setForeground(new java.awt.Color(30, 113, 195));
        txtNameEmployee.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 153, 153)));
        panelName.add(txtNameEmployee, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelName);

        panelGender.setOpaque(false);
        panelGender.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        lbGender.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbGender.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbGender.setText(bundle.getString("lbGender")); // NOI18N
        lbGender.setToolTipText("");
        panelGender.add(lbGender);

        btnGroupGender.add(jRadioMale);
        jRadioMale.setText(bundle.getString("btnRadioMale")); // NOI18N
        jRadioMale.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jRadioMale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioMaleActionPerformed(evt);
            }
        });
        panelGender.add(jRadioMale);

        btnGroupGender.add(jRadioFemale);
        jRadioFemale.setText(bundle.getString("btnRadioFemale")); // NOI18N
        jRadioFemale.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelGender.add(jRadioFemale);

        panelInfoDetail.add(panelGender);

        panelBirthdayEmployee.setOpaque(false);
        panelBirthdayEmployee.setLayout(new java.awt.BorderLayout(15, 0));

        lbBirthdayEmployee.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbBirthdayEmployee.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbBirthdayEmployee.setText(bundle.getString("lbBirthday")); // NOI18N
        lbBirthdayEmployee.setPreferredSize(new java.awt.Dimension(120, 16));
        panelBirthdayEmployee.add(lbBirthdayEmployee, java.awt.BorderLayout.LINE_START);

        txtDateOfBirthEmployee.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtDateOfBirthEmployee.setForeground(new java.awt.Color(30, 113, 195));
        txtDateOfBirthEmployee.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 153, 153)));
        txtDateOfBirthEmployee.setMinimumSize(new java.awt.Dimension(135, 23));
        txtDateOfBirthEmployee.setName(""); // NOI18N
        txtDateOfBirthEmployee.setPreferredSize(new java.awt.Dimension(135, 23));
        panelBirthdayEmployee.add(txtDateOfBirthEmployee, java.awt.BorderLayout.CENTER);

        btnChooseBirthday.setText("...");
        btnChooseBirthday.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnChooseBirthdayMouseClicked(evt);
            }
        });
        panelBirthdayEmployee.add(btnChooseBirthday, java.awt.BorderLayout.LINE_END);

        panelInfoDetail.add(panelBirthdayEmployee);

        panelPhonenumber.setOpaque(false);
        panelPhonenumber.setPreferredSize(new java.awt.Dimension(64, 35));
        panelPhonenumber.setLayout(new java.awt.BorderLayout(15, 1));

        lbPhonenumber.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbPhonenumber.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbPhonenumber.setText(bundle.getString("lbPhoneNumber")); // NOI18N
        lbPhonenumber.setPreferredSize(new java.awt.Dimension(120, 16));
        panelPhonenumber.add(lbPhonenumber, java.awt.BorderLayout.LINE_START);

        txtPhonenumberEmployee.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtPhonenumberEmployee.setForeground(new java.awt.Color(30, 113, 195));
        txtPhonenumberEmployee.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 153, 153)));
        panelPhonenumber.add(txtPhonenumberEmployee, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelPhonenumber);

        panelPosition.setOpaque(false);
        panelPosition.setLayout(new java.awt.BorderLayout(15, 1));

        lbPosition.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbPosition.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbPosition.setText(bundle.getString("lbPosition")); // NOI18N
        lbPosition.setPreferredSize(new java.awt.Dimension(120, 16));
        panelPosition.add(lbPosition, java.awt.BorderLayout.LINE_START);

        cbbPositionEmployee.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        cbbPositionEmployee.setForeground(new java.awt.Color(30, 113, 195));
        cbbPositionEmployee.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbbPositionEmployee.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 153, 153)));
        cbbPositionEmployee.setPreferredSize(new java.awt.Dimension(64, 23));
        panelPosition.add(cbbPositionEmployee, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelPosition);

        panelEmail.setOpaque(false);
        panelEmail.setLayout(new java.awt.BorderLayout(15, 1));

        lbEmail.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbEmail.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbEmail.setText(bundle.getString("lbEmail")); // NOI18N
        lbEmail.setPreferredSize(new java.awt.Dimension(120, 16));
        panelEmail.add(lbEmail, java.awt.BorderLayout.LINE_START);

        txtEmailEmployee.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtEmailEmployee.setForeground(new java.awt.Color(30, 113, 195));
        txtEmailEmployee.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 153, 153)));
        panelEmail.add(txtEmailEmployee, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelEmail);

        panelCreateUpdate.setOpaque(false);
        panelCreateUpdate.setLayout(new java.awt.BorderLayout(15, 0));

        labeCreateUpdate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        labeCreateUpdate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labeCreateUpdate.setText(bundle.getString("lbCreateUpdate")); // NOI18N
        labeCreateUpdate.setPreferredSize(new java.awt.Dimension(120, 16));
        panelCreateUpdate.add(labeCreateUpdate, java.awt.BorderLayout.LINE_START);

        txtCreateUpdate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtCreateUpdate.setForeground(new java.awt.Color(30, 113, 195));
        txtCreateUpdate.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 153, 153)));
        txtCreateUpdate.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCreateUpdate.setEnabled(false);
        txtCreateUpdate.setOpaque(true);
        txtCreateUpdate.setPreferredSize(new java.awt.Dimension(100, 2));
        panelCreateUpdate.add(txtCreateUpdate, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelCreateUpdate);

        panelCreateAt.setOpaque(false);
        panelCreateAt.setLayout(new java.awt.BorderLayout(15, 0));

        labelCreateAt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        labelCreateAt.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelCreateAt.setText(bundle.getString("lbCreateAt")); // NOI18N
        labelCreateAt.setPreferredSize(new java.awt.Dimension(120, 16));
        panelCreateAt.add(labelCreateAt, java.awt.BorderLayout.LINE_START);

        txtCreateAt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtCreateAt.setForeground(new java.awt.Color(30, 113, 195));
        txtCreateAt.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 153, 153)));
        txtCreateAt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCreateAt.setEnabled(false);
        txtCreateAt.setOpaque(true);
        txtCreateAt.setPreferredSize(new java.awt.Dimension(100, 2));
        panelCreateAt.add(txtCreateAt, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelCreateAt);

        panelEmpty.setOpaque(false);
        panelEmpty.setLayout(new java.awt.BorderLayout(15, 0));

        labelESC.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelESC.setForeground(new java.awt.Color(255, 0, 51));
        labelESC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/exclamation.png"))); // NOI18N
        labelESC.setText(bundle.getString("labelNoteESC")); // NOI18N
        panelEmpty.add(labelESC, java.awt.BorderLayout.PAGE_END);

        panelInfoDetail.add(panelEmpty);

        panelDetail.add(panelInfoDetail, java.awt.BorderLayout.CENTER);

        add(panelDetail);

        panelSort.setBackground(new java.awt.Color(255, 255, 255));
        panelSort.setPreferredSize(new java.awt.Dimension(1197, 70));
        panelSort.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 20));

        textFieldSearch.setPreferredSize(new java.awt.Dimension(450, 31));
        panelSort.add(textFieldSearch);

        labelSort.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        labelSort.setText(bundle.getString("lbSort")); // NOI18N
        panelSort.add(labelSort);

        cbbSortEmployee.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<Không>", "STT", "Tên nhân viên", "Ngày sinh" }));
        cbbSortEmployee.setPreferredSize(new java.awt.Dimension(150, 30));
        panelSort.add(cbbSortEmployee);

        cbbSort.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<Không>", "Tăng dần", "Giảm giần" }));
        cbbSort.setPreferredSize(new java.awt.Dimension(100, 30));
        panelSort.add(cbbSort);

        btnResetSort.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/refresh.png"))); // NOI18N
        btnResetSort.setPreferredSize(new java.awt.Dimension(50, 25));
        btnResetSort.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnResetSortMouseClicked(evt);
            }
        });
        panelSort.add(btnResetSort);

        add(panelSort);

        panelListEmployee.setBackground(new java.awt.Color(255, 255, 255));
        panelListEmployee.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), bundle.getString("txtListEmployee"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(24, 100, 178))); // NOI18N
        panelListEmployee.setMinimumSize(new java.awt.Dimension(1197, 200));
        panelListEmployee.setOpaque(false);
        panelListEmployee.setPreferredSize(new java.awt.Dimension(1197, 400));
        panelListEmployee.setLayout(new java.awt.BorderLayout());

        tableListEmployee.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã nhân viên", "Họ và tên", "Ngày sinh", "Giới tính", "Chức vụ", "Số điện thoại", "Email"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableListEmployee.getTableHeader().setReorderingAllowed(false);
        jScrollPaneListEmployee.setViewportView(tableListEmployee);
        if (tableListEmployee.getColumnModel().getColumnCount() > 0) {
            tableListEmployee.getColumnModel().getColumn(0).setPreferredWidth(50);
            tableListEmployee.getColumnModel().getColumn(0).setMaxWidth(50);
            tableListEmployee.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("txtNo")); // NOI18N
            tableListEmployee.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("txtEmployeeID")); // NOI18N
            tableListEmployee.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("txtEmployeeName")); // NOI18N
            tableListEmployee.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("txtEmployeeDob")); // NOI18N
            tableListEmployee.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("txtEmployeeGender")); // NOI18N
            tableListEmployee.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("txtEmployeePosition")); // NOI18N
            tableListEmployee.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("txtPhonenumber")); // NOI18N
        }

        panelListEmployee.add(jScrollPaneListEmployee, java.awt.BorderLayout.CENTER);

        add(panelListEmployee);
    }


    private void setupCusmizeTable(){
        tableListEmployeeSorter = TableStyleUtil.applyDefaultStyle(tableListEmployee);
    }

    //Hiển thị cbbPositionEmployee là danh sách trong PositionEnum

    private void populatePositionComboBox() {
        cbbPositionEmployee.removeAllItems();
        cbbPositionEmployee.addItem("");
        for (EmployeePositionEnum position : EmployeePositionEnum.values()) {
            cbbPositionEmployee.addItem(position.getDisplayName());
        }
        cbbPositionEmployee.setSelectedIndex(0);
    }

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {
       
    }

    private void btnResetSortMouseClicked(java.awt.event.MouseEvent evt) {
        cbbSortEmployee.setSelectedIndex(0);
        cbbSort.setSelectedIndex(0);
    }

    private void jRadioMaleActionPerformed(java.awt.event.ActionEvent evt) {
      
    }

    private void btnChooseBirthdayMouseClicked(java.awt.event.MouseEvent evt) {
        dateChooserBirthday.showPopup();
    }

    
    public TableRowSorter<TableModel> getTableListEmployeeSorter() {
        return tableListEmployeeSorter;
    }

    public com.k33ptoo.components.KButton getBtnAddEmployee() {
        return btnAdd;
    }

    public com.k33ptoo.components.KButton getBtnDeleteEmployee() {
        return btnDelete;
    }

    public javax.swing.JButton getBtnExportExcel() {
        return btnExportExcel;
    }

    public com.k33ptoo.components.KButton getBtnRefresh() {
        return btnRefresh;
    }

    public javax.swing.JButton getBtnResetSort() {
        return btnResetSort;
    }

    public com.k33ptoo.components.KButton getBtnUpdate() {
        return btnUpdate;
    }

    public javax.swing.JComboBox<String> getCbbSort() {
        return cbbSort;
    }

    public javax.swing.JComboBox<String> getCbbSortEmployee() {
        return cbbSortEmployee;
    }

    
    public com.pcstore.utils.TextFieldSearch getTextFieldSearch() {
        return textFieldSearch;
    }

    public javax.swing.JTextField getTxtCreateAt() {
        return txtCreateAt;
    }

    public javax.swing.JTextField getTxtCreateUpdate() {
        return txtCreateUpdate;
    }
  
    
    public javax.swing.JTextField getTxtBirthdayEmployee() {
        return txtDateOfBirthEmployee;
    }

    public javax.swing.JTextField getTxtEmailEmployee() {
        return txtEmailEmployee;
    }

    public javax.swing.JTextField getTxtIDEmployee() {
        return txtIDEmployee;
    }

    public javax.swing.JTextField getTxtNameEmployee() {
        return txtNameEmployee;
    }

    public javax.swing.JTextField getTxtPhonenumberEmployee() {
        return txtPhonenumberEmployee;
    }

    public javax.swing.JComboBox<String> getCbbPositionEmployee() {
        return cbbPositionEmployee;
    }

    public javax.swing.JLabel getLabelESC() {
        return labelESC;
    }
    
    public javax.swing.JTable getTableListEmployee() {
        return tableListEmployee;
    }
    
    public javax.swing.JRadioButton getjRadioFemale() {
        return jRadioFemale;
    }

    public javax.swing.JRadioButton getjRadioMale() {
        return jRadioMale;
    }

    public com.raven.datechooser.DateChooser getDateChooserBirthday() {
        return dateChooserBirthday;
    }
    
    public com.k33ptoo.components.KGradientPanel getPanelAvatar() {
        return panelAvatar;
    }

    public com.k33ptoo.components.KButton getBtnChangeImage() {
        return btnChangeImage;
    }

    public javax.swing.JPanel getPanelSort() {
        return panelSort;
    }

}
