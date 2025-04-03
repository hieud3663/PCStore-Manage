package test;

import test.controller.EmployeeController;
import test.Repository.EmployeeRepository;
import test.view.EmployeeView;

public class Main {
    public static void main(String[] args) {
        EmployeeRepository employeeRepository = new EmployeeRepository();
        EmployeeView employeeView = new EmployeeView();
        EmployeeController employeeController = new EmployeeController(employeeRepository, employeeView);

        // employeeController.initView();
        employeeView.setVisible(true);
    }
}