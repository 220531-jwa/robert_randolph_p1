package dev.randolph.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.randolph.model.Employee;
import dev.randolph.repo.EmployeeDAO;
import dev.randolph.service.EmployeeService;
import io.javalin.http.Context;
import kotlin.Pair;

public class EmployeeController {
    
    private EmployeeService empService = new EmployeeService(new EmployeeDAO());
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
        log.debug("HTTP post request recieved at endpoint /login");
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
    
    /**
     * Handles request to logout from user.
     * Takes token from header.
     * Always returns a 200 status.
     *  - Session was either not active to begin with, or was and now isn't.
     *  - Also avoids sending a 404 for a "session not found"
     *      - Prevents attackers from 'guessing' session tokens.
     * @return 200
     */
    public void logout(Context c) {
        log.debug("Http post request recieved at endpoint /logout");
        // Getting input
        String token = c.header("Token");
        
        // Attempting to logout - Don't care about return
        int result = empService.logout(token);
        
        // Result used for logging purposes
        if (result == 200) {
            log.info("Logout was successful.");
        }
        else {
            log.error("Token wasn't associated with an active account.");
        }
        
        // Saying logout was successful (Wasn't active to being with, or was but now isn't)
        c.status(200);
    }
    
    /*
     * === GET ===
     */
    
    /**
     * Retrieves employee information with the given id.
     * Takes target employee username from path.
     * Takes source token from header.
     * @return 200 with employee information is found, 400 series error otherwise.
     */
    public void getEmployeeByUsername(Context c) {
        log.debug("HTTP get request recieved at endpoint /employee/{username}");
        // Getting input
        String username = c.pathParam("username");
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
}
