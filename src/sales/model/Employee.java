package sales.model;

import java.util.Objects;

public class Employee {

    private int id;
    private String employeeName;
    private String username;
    private String password;
    private String role;

    public Employee() {
    }

    public Employee(int id, String employeeName, String username, String password, String role) {
        this.id = id;
        this.employeeName = employeeName;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        if (employeeName == null) {
            return "";
        }
        return employeeName + " (" + (role != null ? role : "") + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Employee other)) {
            return false;
        }
        if (this.id > 0 && other.id > 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.username, other.username);
    }

    @Override
    public int hashCode() {
        return id > 0 ? Integer.hashCode(id) : Objects.hashCode(username);
    }
}
