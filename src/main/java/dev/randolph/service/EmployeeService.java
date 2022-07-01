package dev.randolph.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.randolph.model.Employee;
import dev.randolph.repo.EmployeeDAO;
import dev.randolph.util.ActiveEmployeeSessions;
import kotlin.Pair;

public class EmployeeService {
    
    private EmployeeDAO empDAO = new EmployeeDAO();
    private static Logger log = LogManager.getLogger(EmployeeService.class);
    
    /*
     * === POST / CREATE ===
     */
    
    /**
     * Attempts to login an employee with the given username and password.
     * If the login was successful will mark the employee as having an active session.
     * If the passed credentials were invalid or the employee doesn't exist will return null.
     * @param username The username of the employee.
     * @param password The password of the employee.
     * @return 200 with employee if successful, and 400 null series error otherwise.
     */
    public Pair<Employee, Integer> loginWithCredentials(String username, String password) {
        log.debug("Recieved credentials: " + username + " | " + password);
        // Validating input
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            log.error("username and/or password input(s) is/are invalid.");
            return new Pair<>(null, 400);
        }
        
        // Getting employee
        Employee emp = empDAO.getEmployeeByUsername(username);
        
        // Checking if employee exists
        if (emp == null) {
            log.error("Employee does not exist.");
            return new Pair<>(null, 404);
        }
        
        // Checking if employee credentials were valid
        if (!emp.getPassword().equals(password)) {
            log.error("Credentials don't match.");
            return new Pair<>(null, 401);
        }
        
        // Credentials were valid
        // Adding session as active.
        String token = ActiveEmployeeSessions.addActiveEmployee(emp.getUsername());
        
        // Checking if token is null
        if (token == null || token.isBlank()) {
            log.error("Token failed to generate."); // Something horribly wrong happened
            return new Pair<>(null, 503);
        }
        else {
            // Token successfully generated
            // Modifying data to return back to client
            emp.setPassword(token); // Placing token where password is
        }
        
        return new Pair<>(emp, 200);
    }
    
    /**
     * Attempts to log the user out.
     * @param token The token of the active user session.
     * @return 200 if successful, and 400 series otherwise.
     */
    public int logout(String token) {
        log.debug("Recieved token: " + token);
        // Validating input
        if (token == null || token.isBlank()) {
            log.error("token input is invalid.");
            return 400;
        }
        
        // Removing active session of user
        boolean result = ActiveEmployeeSessions.removeActiveEmployee(token);
        
        // Checking if token was associated with an active session.
        if (!result) {
            // Didn't find active session
            return 404;
        }
        
        // Success
        return 200;
    }
    
    /*
     * === GET / READ ===
     */
    
    /**
     * Retrieves the employee with the given username from the database.
     * Authorization:
     *  - User can only get their own information
     * @param username The target username of the employee to get.
     * @param token The source token of the active user.
     * @return The employee if they exist, and null otherwise. Status depends on success/error
     */
    public Pair<Employee, Integer> getEmployeeByUsername(String username, String token) {
        log.debug("Recieved username: " + username + " token: " + token);
        // Validating input
        if (username == null || token == null || username.isBlank() || token.isBlank()) {
            log.error("username and/or token input is/are invalid");
            return new Pair<>(null, 400);
        }
        
        // Checking if user is in active session
        if (!ActiveEmployeeSessions.isActiveEmployee(token)) {
            log.error("User isn't in active session");
            return new Pair<>(null, 401);
        }
        
        // Checking if user is authorized to request employee information
        String requesterUsername = ActiveEmployeeSessions.getActiveEmployeeUsername(token);
        if (!requesterUsername.equals(username)) {
            log.error("User isn't authorized to know about given employee.");
            return new Pair<>(null, 403);
        }
        
        // Checking if target employee exists
        Employee emp = empDAO.getEmployeeByUsername(username);
        int status = 200;
        if (emp == null) {
            log.error("Target Employee doesn't exist");
            status = 404;
        }
        
        // Getting employee -> 404 used when employee is null
        return new Pair<>(emp, status);
    }
}
