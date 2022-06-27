package dev.randolph.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.randolph.model.Employee;
import dev.randolph.service.EmployeeService;
import io.javalin.http.Context;
import kotlin.Pair;

public class EmployeeController {
    
    private EmployeeService empService = new EmployeeService();
    private static Logger log = LogManager.getLogger(EmployeeController.class);
    
    /*
     * === POST ===
     */
    
    /**
     * Handles request to login from the user.
     * Takes source username and password from body.
     * If login was successful password is replaced by a temporary token (handled in service)
     * @return 200 with employee information if login was successful, and 400 series otherwise.
     */
    public void loginWithCredentials(Context c) {
        log.debug("HTTP request recieved at endpoint /login");
        // Getting username and password
        Employee emp = c.bodyAsClass(Employee.class);
        
        // Getting employee from database
        Pair<Employee, Integer> result = empService.loginWithCredentials(emp.getUsername(), emp.getPassword());
        
        // Checking if username and password were correct
        if (result.getFirst() != null) {
            // Login successful
            log.info("Login was successful");
            c.json(result.getFirst());
        }
        
        c.status(result.getSecond());
    }
    
    /*
     * === GET ===
     */
    
    /**
     * Retrieves employee information with the given id
     * Takes target employee username as query
     * Takes source token header
     * @return 200 with employee information is found, 400 series error otherwise
     */
    public void getEmployeeByUsername(Context c) {
        log.debug("HTTP request recieved at endpoint /employee");
        // Getting input
        String username = c.queryParam("username");
        String token = c.header("Token");
        
        // Getting Employee
        Pair<Employee, Integer> result = empService.getEmployeeByUsername(username, token);
        
        // Checking if employee information was gathered
        if (result.getFirst() != null) {
            log.info("Successfully got employee");
            c.json(result.getFirst());
        }
        
        c.status(result.getSecond());
    }
    
    /**
     * === POST / PATCH ===
     */
}
