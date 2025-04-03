package test.controller;

import test.Repository.EmployeeRepository;
import test.model.Employee;
import test.model.enums.EmployeePositionEnum;
import test.view.EmployeeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

public class EmployeeController {
    private EmployeeRepository employeeRepository;
    private EmployeeView employeeView;

    public EmployeeController(EmployeeRepository employeeRepository, EmployeeView employeeView) {
        this.employeeRepository = employeeRepository;
        this.employeeView = employeeView;

        this.employeeView.addAddButtonListener(new AddButtonListener());
        this.employeeView.addUpdateButtonListener(new UpdateButtonListener());
        this.employeeView.addDeleteButtonListener(new DeleteButtonListener());
        this.employeeView.addClearButtonListener(new ClearButtonListener());

        loadEmployeeData();
    }

    private void loadEmployeeData() {
        for (Employee employee : employeeRepository.getAllEmployees()) {
            employeeView.addEmployeeToTable(new Object[]{
                    employee.getEmployeeId(),
                    employee.getFullName(),
                    employee.getPhoneNumber(),
                    employee.getEmail(),
                    employee.getAddress(),
                    employee.getPosition().getDisplayName()
            });
        }
    }

    private class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Employee employee = new Employee();
            employee.setEmployeeId(employeeView.getEmployeeId());
            employee.setFullName(employeeView.getFullName());
            employee.setPhoneNumber(employeeView.getPhoneNumber());
            employee.setEmail(employeeView.getEmail());
            employee.setAddress(employeeView.getAddress());
            employee.setPosition(EmployeePositionEnum.valueOf(employeeView.getPosition().toUpperCase().replace(" ", "_")));

            employeeRepository.addEmployee(employee);
            employeeView.addEmployeeToTable(new Object[]{
                    employee.getEmployeeId(),
                    employee.getFullName(),
                    employee.getPhoneNumber(),
                    employee.getEmail(),
                    employee.getAddress(),
                    employee.getPosition().getDisplayName()
            });
            employeeView.clearForm();
        }
    }

    private class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = employeeView.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(employeeView, "Please select an employee to update.");
                return;
            }

            Employee employee = new Employee();
            employee.setEmployeeId(employeeView.getEmployeeId());
            employee.setFullName(employeeView.getFullName());
            employee.setPhoneNumber(employeeView.getPhoneNumber());
            employee.setEmail(employeeView.getEmail());
            employee.setAddress(employeeView.getAddress());
            employee.setPosition(EmployeePositionEnum.valueOf(employeeView.getPosition().toUpperCase().replace(" ", "_")));

            employeeRepository.updateEmployee(employee);
            employeeView.updateEmployeeInTable(selectedRow, new Object[]{
                    employee.getEmployeeId(),
                    employee.getFullName(),
                    employee.getPhoneNumber(),
                    employee.getEmail(),
                    employee.getAddress(),
                    employee.getPosition().getDisplayName()
            });
            employeeView.clearForm();
        }
    }

    private class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = employeeView.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(employeeView, "Please select an employee to delete.");
                return;
            }

            String employeeId = (String) employeeView.getSelectedRowData()[0];
            employeeRepository.deleteEmployee(employeeId);
            employeeView.removeEmployeeFromTable(selectedRow);
            employeeView.clearForm();
        }
    }

    private class ClearButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            employeeView.clearForm();
        }
    }
}