package dev.randolph.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.randolph.model.Employee;
import dev.randolph.repo.EmployeeDAO;
import dev.randolph.util.ActiveEmployeeSessions;

public class EmployeeService {
    
    private EmployeeDAO empDAO = new EmployeeDAO();
    private static Logger log = LogManager.getLogger(EmployeeService.class);
    
    /*
     * === GET / READ ===
     */
    
    /**
     * Attempts to login an employee with the given username and password.
     * If the login was successful will mark the employee as having an active session.
     * If the passed credentials were invalid or the employee doesn't exist will return null.
     * @param username The username of the employee.
     * @param password The password of the employee.
     * @return The employee if successful, and null otherwise.
     */
    public Employee loginWithCredentials(String username, String password) {
        log.debug("Recieved credentials: " + username + " | " + password);
        // Validating input
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            log.error("username and/or password input(s) is/are invalid.");
            return null;
        }
        
        // Getting employee
        Employee emp = getEmployeeByUsername(username);
        
        // Checking if employee exists
        if (emp == null) {
            // Employee doesn't exist
            log.error("Employee does not exist.");
            return null;
        }
        
        // Checking if employee credentials were valid
        if (!emp.getPassword().equals(password)) {
            // Employee credentials didn't match
            log.error("Credentials don't match.");
            return null;
        }
        
        // Credentials were valid
        // Adding session as active.
        String token = ActiveEmployeeSessions.addActiveEmployee(emp.getUsername());
        
        // Checking if token is null
        if (token == null || token.isBlank()) {
            // Error generating token
            log.error("Token failed to generate.");
            emp = null;
        }
        else {
            // Token successfully generated
            // Modifying data to return back to client
            emp.setPassword(token); // Placing token where password is
            log.info("Login session successful.");
        }
        
        return emp;
    }
    
    /**
     * Retrieves the employee with the given username from the database.
     * @param username The username of the employee.
     * @return The employee if they exist, and null otherwise.
     */
    public Employee getEmployeeByUsername(String username) {
        log.debug("Recieved username: " + username);
        // Validating input
        if (username == null || username.isBlank()) {
            log.error("username input is invalid");
            return null;
        }
        
        // Getting employee
        return empDAO.getEmployeeByUsername(username);
    }
    
    /*
     * === PUT / PATCH / UPDATE ===
     */
}
