package test.dao;

import test.model.Employee;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private List<Employee> employees = new ArrayList<>();

    public List<Employee> getAllEmployees() {
        return employees;
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public void updateEmployee(Employee employee) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getEmployeeId().equals(employee.getEmployeeId())) {
                employees.set(i, employee);
                return;
            }
        }
    }

    public void deleteEmployee(String employeeId) {
        employees.removeIf(emp -> emp.getEmployeeId().equals(employeeId));
    }
}