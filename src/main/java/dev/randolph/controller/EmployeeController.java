package dev.randolph.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.randolph.model.Employee;
import dev.randolph.service.EmployeeService;
import io.javalin.http.Context;

public class EmployeeController {
    
    private EmployeeService es = new EmployeeService();
    private static Logger log = LogManager.getLogger(EmployeeController.class);
    
    /*
     * === POST ===
     */
    
    /**
     * Handles request to login from the user.
     * Takes username and password from body.
     * If login was successful password is replaced by a temporary token (handled in service)
     * @return 200 with employee information if login was successful, and 401 otherwise.
     */
    public void loginWithCredentials(Context c) {
        log.debug("HTTP request recieved at endpoint /login");
        // Getting username and password
        Employee emp = c.bodyAsClass(Employee.class);
        
        // Getting employee from database
        emp = es.loginWithCredentials(emp.getUsername(), emp.getPassword());
        
        // Checking if username and password were correct
        if (emp != null) {
            // Login successful
            c.status(200);
            c.json(emp);      // Sending back employee information
        }
        else {
            // Login failed
            c.status(401);
        }
    }
    
    /*
     * === GET ===
     */
    
    /**
     * Retrieves employee information with the given id
     * Takes Employee id as query
     * TODO: This may not be necessary if login works as expected
     * @return 200 with employee information is found, and 404 otherwise.
     */
    public void getEmployeeByUsername(Context c) {
        // Getting input
        String username = c.queryParam("username");
        
        // Getting Employee
        Employee e = es.getEmployeeByUsername(username);
        
        // Validating output
        if (e != null) {
            // Successfully got employee
            c.status(200);
            c.json(e);
        }
        else {
            // Failed to get employee
            c.status(404);
        }
    }
    
    /**
     * === POST / PATCH ===
     */
    
    /**
     * Updates the employees available reimbursement funds, and current funds
     * 
     */
    public void updateEmployeeFundsById(Context c) {
        
    }

}
