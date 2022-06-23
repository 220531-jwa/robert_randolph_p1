package dev.randolph.controller;

import dev.randolph.service.RequestService;
import io.javalin.http.Context;

public class RequestController {
    
    RequestService rs = new RequestService();
    
    /*
     * === POST ===
     */
    
    public void createNewRequest(Context c) {
        // Get employee id
        // Check authorization
    }
    
    /*
     * === GET ===
     */
    
    public void getAllEmployeeRequests(Context c) {
        // Get employee id (who is requesting)
        // Get status filter
        // Check authorization
    }
    
    /*
     * === PATCH ===
     */
    
    public void updateEmployeeRequest(Context c) {
        // Get employee id (who is updating)
        // Get request id (request to update)
        // Get optional body params (request fields to update)
        //  - status, urgent, reason, grade, reim-amount, exceeds funds
        // Check authorization
    }
}
