package sales.service;

import java.util.List;
import java.util.Set;
import sales.dao.EmployeeDao;
import sales.model.Employee;

public class EmployeeService {

    private static final Set<String> VALID_ROLES = Set.of("ADMIN", "SALES", "CASHIER");
    private final EmployeeDao employeeDao;

    public EmployeeService(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public Employee authenticate(String username, String password) {
        String user = requireText(username, "Username");
        String pass = requireText(password, "Password");
        Employee emp = employeeDao.findByCredentials(user, pass);
        if (emp == null) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        return emp;
    }

    public List<Employee> getAll() {
        return employeeDao.findAll();
    }

    public List<Employee> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return employeeDao.findAll();
        }
        return employeeDao.search(keyword.trim());
    }

    public Employee saveEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee details are required.");
        }
        String name = requireText(employee.getEmployeeName(), "Employee name");
        String username = requireText(employee.getUsername(), "Username");
        String password = requireText(employee.getPassword(), "Password");
        String role = requireText(employee.getRole(), "Role").toUpperCase();
        if (!VALID_ROLES.contains(role)) {
            throw new IllegalArgumentException("Role must be ADMIN, SALES, or CASHIER.");
        }

        int excludeId = employee.getId() > 0 ? employee.getId() : 0;
        if (employeeDao.existsByEmployeeName(name, excludeId)) {
            throw new IllegalArgumentException("Employee name '" + name + "' already exists.");
        }
        if (employeeDao.existsByUsername(username, excludeId)) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken.");
        }

        employee.setEmployeeName(name);
        employee.setUsername(username);
        employee.setPassword(password);
        employee.setRole(role);

        if (employee.getId() <= 0) {
            employeeDao.insert(employee);
        } else {
            employeeDao.update(employee);
        }
        return employee;
    }

    public void deleteEmployee(int employeeId, int loggedInEmployeeId) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Please select an employee to delete.");
        }
        if (employeeId == loggedInEmployeeId) {
            throw new IllegalStateException("You cannot delete the currently logged-in employee.");
        }
        employeeDao.delete(employeeId);
    }

    private String requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return value.trim();
    }
}
