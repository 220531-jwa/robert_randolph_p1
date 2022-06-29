package dev.randolph.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.randolph.model.Employee;
import dev.randolph.model.DTO.RequestDTO;
import dev.randolph.model.enums.EmployeeType;
import dev.randolph.model.enums.RequestStatus;
import dev.randolph.repo.EmployeeDAO;
import dev.randolph.repo.RequestDAO;
import dev.randolph.util.ActiveEmployeeSessions;
import kotlin.Pair;

public class RequestService {
    
    private EmployeeDAO empDAO  = new EmployeeDAO();
    private RequestDAO reqDAO = new RequestDAO();
    private static Logger log = LogManager.getLogger(RequestService.class);
    
    /*
     * === GET / READ ===
     */
    
    public Pair<List<RequestDTO>, Integer> getAllRequests(String statusFilter, String token) {
        log.debug("Recieved statusFilter: " + statusFilter + " token: " + token);
        // Validating input
        if (token == null || token.isBlank()) {
            log.error("token input is invalid");
            return new Pair<>(null, 400);
        }
        
        // Checking if user is in active session
        if (!ActiveEmployeeSessions.isActiveEmployee(token)) {
            log.error("User isn't in active session");
            return new Pair<>(null, 401);
        }
        
        // Checking if user is authorized to request all reimbursement requests
        String requesterUsername = ActiveEmployeeSessions.getActiveEmployeeUsername(token);
        Employee emp = empDAO.getEmployeeByUsername(requesterUsername);
        if (emp.getType() != EmployeeType.MANAGER) {
            log.error("User isn't authorized to know about all reimbursement requests");
            return new Pair<>(null, 403);
        }
        
        // Getting status filter - Default is all
        RequestStatus[] filter = getFilters(statusFilter);
        
        // Getting requests
        List<RequestDTO> requests = reqDAO.getAllRequests(filter);        
        
        return new Pair<>(requests, 200);
    }
    
    
    
    
    
    
    
    
    public Pair<List<RequestDTO>, Integer> getAllEmployeeRequests(String username, Integer rid, String statusFilter, String token) {
        log.debug("Recieved username: " + username + " rid: " + rid + " statusFilter: " + statusFilter + " token: " + token);
        // Validating input
        if (username == null || token == null || (rid != null && rid < 0) || username.isBlank() || token.isBlank()) {
            log.error("username and/or rid and/or token input(s) is/are invalid.");
            return new Pair<>(null, 400);
        }
        
        // Checking if user is in active session
        if (!ActiveEmployeeSessions.isActiveEmployee(token)) {
            log.error("User isn't in active session");
            return new Pair<>(null, 401);
        }
        
        // Checking if user is authorized to request employee reimbursement requests
        String requesterUsername = ActiveEmployeeSessions.getActiveEmployeeUsername(token);
        Employee emp = empDAO.getEmployeeByUsername(requesterUsername);
        if (emp.getType() == EmployeeType.EMPLOYEE && emp.getUsername().equals(username)) {
            // Employee is getting their own requests
        }
        else if (emp.getType() == EmployeeType.MANAGER) {
            // Manager is getting requests
        }
        else {
            log.error("User isn't authorized to know about given employee requests");
            return new Pair<>(null, 403);
        }
        
        // Getting status filter - Default is all
        RequestStatus[] filter = getFilters(statusFilter);
        
        // Getting requests
        // Possible for username or request id to not exist
        List<RequestDTO> requests;
        if (rid == null) {
            // Getting all employee requests | uses filters
            requests = reqDAO.getAllEmployeeRequests(username, filter);
        }
        else {
            // Getting specific employee request | ignores filters
            requests = reqDAO.getAllEmployeeRequestById(username, rid);
        }
        int status = 200;
        if (requests == null) {
            log.error("Target employee or request doesn't exist");
            status = 404;
        }
        
        return new Pair<>(requests, status);
    }
    
    /*
     * === UTILITY ===
     */
    
    /**
     * Retrieves the filters based on the given string.
     * @param statusFilter The statuses to filter by.
     * @return An array of desired statuses to filter by.
     */
    private RequestStatus[] getFilters(String statusFilter) {
        RequestStatus[] filter = null;
        if (statusFilter != null) {
            if (statusFilter.equalsIgnoreCase("PENDING")) {
                filter = RequestStatus.getPending();
            }
            else if (statusFilter.equalsIgnoreCase("FINISHED")) {
                filter = RequestStatus.getFinished();
            }
        }
        return filter;
    }
}
