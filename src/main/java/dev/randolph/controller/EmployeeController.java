package dev.randolph.controller;

import dev.randolph.model.Employee;
import dev.randolph.service.EmployeeService;
import io.javalin.core.validation.Validator;
import io.javalin.http.Context;

public class EmployeeController {
    
    EmployeeService es = new EmployeeService();
    
    public void loginWithUsername(Context c) {
        // Get username
        // Get password
    }
    
    /**
     * Retrieves employee information with the given id
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
