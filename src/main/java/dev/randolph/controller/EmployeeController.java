package dev.randolph.controller;

import dev.randolph.model.Employee;
import dev.randolph.service.EmployeeService;
import io.javalin.core.validation.Validator;
import io.javalin.http.Context;

public class EmployeeController {
    
    private EmployeeService es = new EmployeeService();
    
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
        // Getting username and password
        Employee e = c.bodyAsClass(Employee.class);
        
        // Getting employee from database
        e = es.loginWithCredentials(e.getUsername(), e.getPassword());
        
        // Checking if username and password were correct
        if (e != null) {
            // Login successful
            c.json(e);      // Sending back employee information
            c.status(200);
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
     */
    public void getEmployeeById(Context c) {
        // Getting input
        Validator<Integer> eid = c.queryParamAsClass("eid", Integer.class);
        
        // Getting Employee
        Employee e = es.getEmployeeById(eid.get());
        
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

}
