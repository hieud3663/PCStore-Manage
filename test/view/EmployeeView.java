package test.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionListener;

public class EmployeeView extends JFrame {
    private JTextField txtEmployeeId = new JTextField(10);
    private JTextField txtFullName = new JTextField(20);
    private JTextField txtPhoneNumber = new JTextField(15);
    private JTextField txtEmail = new JTextField(20);
    private JTextField txtAddress = new JTextField(30);
    private JComboBox<String> cbPosition = new JComboBox<>(new String[]{"Manager", "Sales", "Stock Keeper"});
    private JButton btnAdd = new JButton("Add");
    private JButton btnUpdate = new JButton("Update");
    private JButton btnDelete = new JButton("Delete");
    private JButton btnClear = new JButton("Clear");
    private JTable table = new JTable();
    private DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Phone", "Email", "Address", "Position"}, 0);

    public EmployeeView() {
        setTitle("Employee Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.add(new JLabel("Employee ID:"));
        panel.add(txtEmployeeId);
        panel.add(new JLabel("Full Name:"));
        panel.add(txtFullName);
        panel.add(new JLabel("Phone Number:"));
        panel.add(txtPhoneNumber);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Address:"));
        panel.add(txtAddress);
        panel.add(new JLabel("Position:"));
        panel.add(cbPosition);
        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnClear);

        add(panel, BorderLayout.NORTH);

        table.setModel(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public String getEmployeeId() {
        return txtEmployeeId.getText();
    }

    public String getFullName() {
        return txtFullName.getText();
    }

    public String getPhoneNumber() {
        return txtPhoneNumber.getText();
    }

    public String getEmail() {
        return txtEmail.getText();
    }

    public String getAddress() {
        return txtAddress.getText();
    }

    public String getPosition() {
        return (String) cbPosition.getSelectedItem();
    }

    public void setEmployeeId(String employeeId) {
        txtEmployeeId.setText(employeeId);
    }

    public void setFullName(String fullName) {
        txtFullName.setText(fullName);
    }

    public void setPhoneNumber(String phoneNumber) {
        txtPhoneNumber.setText(phoneNumber);
    }

    public void setEmail(String email) {
        txtEmail.setText(email);
    }

    public void setAddress(String address) {
        txtAddress.setText(address);
    }

    public void setPosition(String position) {
        cbPosition.setSelectedItem(position);
    }

    public void addEmployeeToTable(Object[] rowData) {
        tableModel.addRow(rowData);
    }

    public void updateEmployeeInTable(int rowIndex, Object[] rowData) {
        for (int i = 0; i < rowData.length; i++) {
            tableModel.setValueAt(rowData[i], rowIndex, i);
        }
    }

    public void removeEmployeeFromTable(int rowIndex) {
        tableModel.removeRow(rowIndex);
    }

    public void clearForm() {
        txtEmployeeId.setText("");
        txtFullName.setText("");
        txtPhoneNumber.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        cbPosition.setSelectedIndex(0);
    }

    public void addAddButtonListener(ActionListener listener) {
        btnAdd.addActionListener(listener);
    }

    public void addUpdateButtonListener(ActionListener listener) {
        btnUpdate.addActionListener(listener);
    }

    public void addDeleteButtonListener(ActionListener listener) {
        btnDelete.addActionListener(listener);
    }

    public void addClearButtonListener(ActionListener listener) {
        btnClear.addActionListener(listener);
    }

    public int getSelectedRow() {
        return table.getSelectedRow();
    }

    public Object[] getSelectedRowData() {
        int rowIndex = getSelectedRow();
        if (rowIndex == -1) {
            return null;
        }
        Object[] rowData = new Object[tableModel.getColumnCount()];
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            rowData[i] = tableModel.getValueAt(rowIndex, i);
        }
        return rowData;
    }
}