package test;

import test.controller.EmployeeController;
import test.dao.EmployeeDAO;
import test.view.EmployeeView;

public class Main {
    public static void main(String[] args) {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        EmployeeView employeeView = new EmployeeView();
        EmployeeController employeeController = new EmployeeController(employeeDAO, employeeView);

        // employeeController.initView();
        employeeView.setVisible(true);
    }
}