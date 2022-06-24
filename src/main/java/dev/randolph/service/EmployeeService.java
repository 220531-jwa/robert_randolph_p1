package dev.randolph.service;

import dev.randolph.model.Employee;
import dev.randolph.repo.EmployeeDAO;

public class EmployeeService {
    
    private EmployeeDAO ed = new EmployeeDAO();
    
    /*
     * === GET / READ ===
     */
    
    /**
     * Attempts to login an employee with the given username and password.
     * @param username The username of the employee.
     * @param password The password of the employee.
     * @return The employee if successful, and null otherwise.
     */
    public Employee loginWithCredentials(String username, String password) {
        // Validating input
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            return null;
        }
        
        // Getting employee
        Employee e = ed.getEmployeeByCredentials(username, password);
        
        // Checking if credentials were valid
        if (e != null) {
            // Credentials were valid
        }
        
        return e;
    }
    
    public Employee getEmployeeById(int eid) {
        // Validating input
        if (eid < 0) {
            return null;
        }
        
        // Getting Employee from database
        Employee e = ed.getEmployeeById(eid);
        
        // Checking if employee exists
        if (e != null) {
            return e;
        }
        
        return null;
    }
}
