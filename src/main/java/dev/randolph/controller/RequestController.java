package dev.randolph.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.randolph.model.DTO.RequestDTO;
import dev.randolph.service.RequestService;
import io.javalin.core.validation.Validator;
import io.javalin.http.Context;
import kotlin.Pair;

public class RequestController {
    
    RequestService reqService = new RequestService();
    private static Logger log = LogManager.getLogger(RequestController.class);
    
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
    
    /**
     * Retrieves all reimbursement requests in the database.
     * Takes status filter as query.
     * Takes source from header.
     * @return 200 with request information, 400 series error otherwise.
     */
    public void getAllRequests(Context c) {
        log.debug("Http request recieved at endpoint /request");
        // Getting input
        String statusFilter = c.queryParam("statusFilter");
        String token = c.header("Token");
        
        // Getting reimbursement requests
        Pair<List<RequestDTO>, Integer> result = reqService.getAllRequests(statusFilter, token);
        
        // Checking if request information was gathered
        System.out.println("finished");
        System.out.println("is first: " + result.getFirst());
        if (result.getFirst() != null) {
            log.info("Succesfully got requests.");
            c.json(result.getFirst());
        }
        
        c.status(result.getSecond());
    }
    
    // Get every request of an employee
    public void getAllEmployeeRequests(Context c) {
        log.debug("Http request recieved at endpoint /request/{username}");
        // Getting input
        String username = c.queryParam("username");
        Validator<Integer> vrid = c.queryParamAsClass("rid", Integer.class);
        Integer rid = null;
        String token = c.header("Token");
        
        // Checking if request id was provided
        if (vrid.hasValue()) {
            rid = vrid.get();
        }
        
        // Getting requests
        Pair<List<RequestDTO>, Integer> result = reqService.getAllEmployeeRequests(username, rid, token);
        
        // Checking if requests were gathered
        if (result.getFirst() != null) {
            log.info("Successfully, got requests");
            c.json(result.getFirst());
        }
        
        c.status(result.getSecond());
    }
    
    // Get a specific request from an employee
    public void getEmployeeRequestById(Context c) {
        
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
