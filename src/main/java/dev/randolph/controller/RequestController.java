package dev.randolph.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.randolph.model.Request;
import dev.randolph.model.DTO.RequestDTO;
import dev.randolph.service.RequestService;
import io.javalin.core.validation.BodyValidator;
import io.javalin.core.validation.Validator;
import io.javalin.http.Context;
import kotlin.Pair;

public class RequestController {
    
    RequestService reqService = new RequestService();
    private static Logger log = LogManager.getLogger(RequestController.class);
    
    /*
     * === POST ===
     */
    
    /**
     * Creates a request that is associated with the given username.
     * Takes username from path.
     * Takes Token from header.
     * Takes request data from body.
     * @return 200 With request data, 400 series error otherwise.
     */
    public void createNewRequest(Context c) {
        log.debug("Http post request recieved at endpoint /request/{username}");
        // Getting input
        String username = c.pathParam("username");
        String token = c.header("Token");
        BodyValidator<Request> vrequest = c.bodyValidator(Request.class);
        Request request = vrequest.errors().isEmpty() ? vrequest.get() : null;
        
        // Creating request
        Pair<Boolean, Integer> result = reqService.createRequest(username, request, token);
        
        // Checking if request was created
        if (result.getFirst()) {
            log.info("Successfully created request");
            c.json(result.getFirst());
        }
        
        c.status(result.getSecond());
    }
    
    /*
     * === GET ===
     */
    
    /**
     * Retrieves all reimbursement requests in the database.
     * Takes status filter from query.
     * Takes source token from header.
     * @return 200 with request information, 400 series error otherwise.
     */
    public void getAllRequests(Context c) {
        log.debug("Http get request recieved at endpoint /request");
        // Getting input
        String statusFilter = c.queryParam("statusFilter");
        String token = c.header("Token");
        
        // Getting reimbursement requests
        Pair<List<RequestDTO>, Integer> result = reqService.getAllRequests(statusFilter, token);
        
        // Checking if request information was gathered
        if (result.getFirst() != null) {
            log.info("Succesfully got requests.");
            c.json(result.getFirst());
        }
        
        c.status(result.getSecond());
    }
    
    /**
     * Retrieves all reimbursement requests of the given employee username.
     * Takes username from path.
     * Takes status filter from query.
     * Takes source token from header.
     * @return 200 with request information, 400 series error otherwise.
     */
    public void getAllEmployeeRequests(Context c) {
        log.debug("Http get request recieved at endpoint /request/{username}");
        // Getting input
        String username = c.pathParam("username");
        String statusFilter = c.queryParam("statusFilter");
        String token = c.header("Token");
        
        // Getting requests
        Pair<List<RequestDTO>, Integer> result = reqService.getAllEmployeeRequests(username, statusFilter, token);
        
        // Checking if requests were gathered
        if (result.getFirst() != null) {
            log.info("Successfully, got requests");
            c.json(result.getFirst());
        }
        
        c.status(result.getSecond());
    }
    
    /**
     * Retrieves a specific reimbursement request of the given employee username.
     * Takes username from path.
     * Takes request id from path.
     * Takes source token from header.
     * @return 200 with request information, 400 series error otherwise.
     */
    public void getEmployeeRequestById(Context c) {
        log.debug("Http get request recieved at endpoint /request/{username}/{rid}");
        // Getting input
        String username = c.pathParam("username");
        Validator<Integer> vrid = c.pathParamAsClass("rid", Integer.class);
        Integer rid = vrid.hasValue() ? vrid.get() : null;
        String token = c.header("Token");
        
        // Getting request
        Pair<RequestDTO, Integer> result = reqService.getEmployeeRequestById(username, rid, token);
        
        // Checking if request was fathered
        if (result.getFirst() != null) {
            log.info("Successfully, got request");
            c.json(result.getFirst());
        }
        
        c.status(result.getSecond());
    }
    
    /*
     * === PATCH ===
     */
    
    /**
     * Updates a specific reimbursement request of the given employee username
     * Takes username from path.
     * Takes request id from path.
     * Takes source token from header.
     * @return 200 with update request, 400 series error otherwise.
     */
    public void updateEmployeeRequestById(Context c) {
        log.debug("Http put request recieved at endpoint /request/{username}/{rid}");
        // Getting input
        String username = c.pathParam("username");
        Validator<Integer> vrid = c.pathParamAsClass("rid", Integer.class);
        Integer rid = vrid.hasValue() ? vrid.get() : null;
        String token = c.header("Token");
        BodyValidator<Request> vrequest = c.bodyValidator(Request.class);
        Request request = vrequest.errors().isEmpty() ? vrequest.get() : null;
        
        // Creating request
        Pair<Boolean, Integer> result = reqService.updateRequest(username, rid, request, token);
        
        // Checking if request was created
        if (result.getFirst()) {
            log.info("Successfully created request");
            c.json(result.getFirst());
        }
    
        c.status(result.getSecond());
    }
}
