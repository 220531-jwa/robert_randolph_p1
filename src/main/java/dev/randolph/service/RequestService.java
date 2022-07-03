package dev.randolph.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.randolph.model.Employee;
import dev.randolph.model.Event;
import dev.randolph.model.MetaData;
import dev.randolph.model.Request;
import dev.randolph.model.DTO.RequestDTO;
import dev.randolph.model.enums.EmployeeType;
import dev.randolph.model.enums.GradeFormatType;
import dev.randolph.model.enums.RequestStatus;
import dev.randolph.repo.EmployeeDAO;
import dev.randolph.repo.RequestDAO;
import dev.randolph.util.ActiveEmployeeSessions;
import kotlin.Pair;

public class RequestService {
    
    private EmployeeDAO empDAO;;
    private RequestDAO reqDAO;
    private static Logger log = LogManager.getLogger(RequestService.class);
    
    /*
     * === POST / CREATE
     */
    
    public RequestService() {
        empDAO  = new EmployeeDAO();
        reqDAO = new RequestDAO();
    }
    
    public RequestService(EmployeeDAO empDAO, RequestDAO reqDAO) {
        super();
        this.empDAO = empDAO;
        this.reqDAO = reqDAO;
    }

    /**
     * Creates a new request for the given username.
     * Inputs must be valid and exist.
     *  - Must have: eventType, cost, gradeFormat, cutoff, eventDescription, eventLocation, startDate, justification.
     *  - Cost must be within range 0-9999.99 inclusive.
     *  - Cutoff must match grade format.
     * Authorization:
     *  - Only current user can create a request for themselves.
     * Automatic:
     *  - Starts status at 'PENDING_REVIEW'
     *  - Calculates initial reimbursement amount
     *      - If over available funds, then sets to what is available.
     *          - Sets exceeds funds to true
     *  - Submission date is when server processes it (IOW now)
     *  - Urgent is set to true if startDate is less than 7 days from submission date.
     *      - Allows for dates that have already passed.
     * @param username The username to create the request for.
     * @param reqData The user data input for the request
     * @param token The active session token
     * @return A new request if successful, and 400 series error otherwise.
     */
    public Pair<Boolean, Integer> createRequest(String username, Request reqData, String token) {
        log.debug("Recieved username: " + username + " reqData: " + reqData + " token: " + token);
        
        // ========================
        // === Validating input ===
        // ========================
        
        if (username == null || reqData == null || token == null || username.isBlank() || token.isBlank()) {
            log.error("username and/or reqData and/ token input(s) is/are invalid");
            return new Pair<>(false, 400);
        }
        
        // Checking if required reqData was provided
        if (reqData.getEventType() == null || reqData.getCost() == null || reqData.getGradeFormat() == null
            || reqData.getCutoff() == null || reqData.getEventDescription() == null || reqData.getEventLocation() == null
            || reqData.getStartDate() == null || reqData.getJustification() == null) {
            log.error("Required request fields weren't provided");
            return new Pair<>(false, 400);
        }
        
        // Validating if request fields are valid
        if (reqData.getCost() < 0 || reqData.getCost() > 9999.99
            || !GradeFormatType.validateGrade(reqData.getGradeFormat(), reqData.getCutoff())
            || reqData.getEventDescription().isBlank()
            || reqData.getEventLocation().isBlank()
            || reqData.getJustification().isBlank()) {
            log.error("Request fields aren't valid");
            return new Pair<>(false, 400);
        }
        
        // Checking if user is in active session
        if (!ActiveEmployeeSessions.isActiveEmployee(token)) {
            log.error("User isn't in active session");
            return new Pair<>(false, 401);
        }
        
        // Checking is user is authorized to create request
        String requesterUsername = ActiveEmployeeSessions.getActiveEmployeeUsername(token);
        if (!requesterUsername.equals(username)) {
            log.error("User isn't authorized to make a new request for the given username");
            return new Pair<>(false, 403);
        }
        
        // Getting employee
        Employee emp = empDAO.getEmployeeByUsername(requesterUsername);
        if (emp == null) {
            log.error("Username doens't exist");    // Shouldn't ever happen (Since only existing employees can be active users)
            return new Pair<>(false, 404);
        }
        
        // ==========================================
        // === Populating calculated request data ===
        // ==========================================
        
        // Getting meta data to calculate values to populate remaining request fields.
        MetaData metaData = MetaData.getMeta();
        
        // Calculating reimbursement amount
        // Determined based on the event type and the default reimbursement percentage amount.
        Double reimAmount = null;
        for (Event event: metaData.getEvents()) {
            if (event.getType() == reqData.getEventType()) {
                // Found matching 
                reimAmount = reqData.getCost() * event.getReimPercent();
                reimAmount = ((double) Math.round(reimAmount * 100)) / 100; // Rounding
                break;
            }
        }
        if (reimAmount == null) {
            log.error("Event Type wasn't found in database");   // Shouldn't happen
            return new Pair<>(false, 503);
        }
        
        // Calculating submission date
        Timestamp submissionDate = Timestamp.from(Instant.now());
        
        // Calculating if request is urgent
        // Determined whether the start date is less than a week from the submission date.
        boolean urgent = false;
        long diff = reqData.getStartDate().getTime() - submissionDate.getTime();
        diff = TimeUnit.MILLISECONDS.toDays(diff);
        if (diff < 7) {
            // Event Starting date is less than 7 from now (submission date)
            urgent = true;
        }
        
        // Calculating if request exceeds funds
        // Determined by whether the request reimbursement amount exceeds available funds.
        //  - If so, sets the reimbursement amount to what it can grant.
        // Ignores other pending requests.
        boolean exceedsFunds = false;
        if (reimAmount > emp.getReimFunds()) {
            // The reimbursement amount exceeds reimbursement funds available to the employee.
            reimAmount = emp.getReimFunds();    // Sending to maximum amount available
            exceedsFunds = true;
        }
        
        // Populating necessary fields for request
        reqData.setEmployeeUsername(username);
        reqData.setStatus(RequestStatus.PENDING_REVIEW);
        reqData.setReimAmount(reimAmount);
        reqData.setSubmissionDate(submissionDate);
        reqData.setIsUrgent(urgent);
        reqData.setExceedsFunds(exceedsFunds);
        
        // Creating request
        boolean result = reqDAO.createRequest(reqData);
        int status = 201;
        if (!result) {
            log.error("Failed to create request. Possible: Couldn't find gradeFormat/event to reference."); // Shouldn't happen
            status = 503;
        }
        
        return new Pair<>(result, status);
    }
    
    /*
     * === GET / READ ===
     */
    
    /**
     * Retrieves all employee requests from the database.
     * Can filter based on a status.
     * Authorization:
     *  - Only the manager can get all requests.
     * @param statusFilter The filter for status.
     * @param token The active session token.
     * @return 200 with requests if successful, 400 series with null otherwise.
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
        
        // Checking if employee exists - Shouldn't ever happen (Since only existing employees can be active users)
        if (emp == null) {
            log.error("Failed to get active user. Possible: Db failed???");
            return new Pair<>(null, 503);
        }
        
        // Checking if user is a manager
        if (emp.getType() != EmployeeType.MANAGER) {
            log.error("User isn't authorized to know about all reimbursement requests");
            return new Pair<>(null, 403);
        }
        
        // Getting status filter - Default is all
        RequestStatus[] filter = RequestStatus.getFilters(statusFilter);
        
        // Getting requests
        List<RequestDTO> requests = reqDAO.getAllRequests(filter);        
        
        return new Pair<>(requests, 200);
    }

    /**
     * Retrieves all the requests of the given username.
     * Authorization:
     *  - Employees can get their own requests.
     *  - Managers can get any requests
     * @param username The username the requests are associated with.
     * @param statusFilter The status filter to filter by.
     * @param token The token for the active session.
     * @return 200 with requests if successful, 400 series with null otherwise.
     */
    public Pair<List<RequestDTO>, Integer> getAllEmployeeRequests(String username, String statusFilter, String token) {
        log.debug("Recieved username: " + username + " statusFilter: " + statusFilter + " token: " + token);
        // Validating input
        if (username == null || token == null || username.isBlank() || token.isBlank()) {
            log.error("username and/or token input(s) is/are invalid.");
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
        
        // Checking if employee exists - Shouldn't ever happen (Since only existing employees can be active users)
        if (emp == null) {
            log.error("Failed to get active user. Possible: Db failed???");
            return new Pair<>(null, 503);
        }
        
        // Checking if user is requesting their own information, or manager
        if (emp.getType() == EmployeeType.EMPLOYEE && emp.getUsername().equals(username)) {}    // Employee is getting their own requests
        else if (emp.getType() == EmployeeType.MANAGER) {}                                      // Manager is getting requests
        else {
            log.error("User isn't authorized to know about given employee requests");
            return new Pair<>(null, 403);
        }
        
        // Getting status filter - Default is all
        RequestStatus[] filter = RequestStatus.getFilters(statusFilter);
        
        // Getting requests - Possible for requests to be empty
        List<RequestDTO> requests = reqDAO.getAllEmployeeRequests(username, filter);
        int status = 200;
        if (requests == null || requests.isEmpty()) {
            log.error("Target employee doesn't exist");
            requests = null;
            status = 404;
        }
        
        return new Pair<>(requests, status);
    }
    
    /**
     * Retrieves the requests of the given username and request id.
     * Authorization:
     *  - Employee can get their own request.
     *  - Manager can get any request.
     * @param username The username the requests are associated with.
     * @param rid The request id.
     * @param token The token for the active session.
     * @return 200 with request if successful, 400 series with null otherwise.
     */
    public Pair<RequestDTO, Integer> getEmployeeRequestById(String username, Integer rid, String token) {
        log.debug("Recieved username: " + username + " rid: " + rid + " Token: " + token);
        // validating input
        if (username == null || token == null || rid == null || username.isBlank() || token.isBlank() || rid < 0) {
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
        
        // Checking if employee exists - Shouldn't ever happen (Since only existing employees can be active users)
        if (emp == null) {
            log.error("Failed to get active user. Possible: Db failed???");
            return new Pair<>(null, 503);
        }
        
        // Checking if user is requesting their own request, or is a manager.
        if (emp.getType() == EmployeeType.EMPLOYEE && emp.getUsername().equals(username)) {/*Does Nothing*/}    // Employee is getting their own request
        else if (emp.getType() == EmployeeType.MANAGER) {/*Does Nothing*/}                                      // Manager is getting request
        else {
            log.error("User isn't authorized to know about given employee request");
            return new Pair<>(null, 403);
        }
        
        // Getting request
        RequestDTO request = reqDAO.getEmployeeRequestById(username, rid);
        int status = 200;
        if (request == null) {
            log.error("Target employee and/or request id doesn't exist");
            status = 404;
        }
        
        return new Pair<>(request, status);
    }
    
    /*
     * === PUT / PATCH / UPDATE
     */
    
    /**
     * Updates the request of the given username and request id.
     * Inputs must be valid and exist.
     *  - Grade must match grade format
     *      - Not required (can be null - for no grade uploaded)
     *  - Reimbursement amount must be within range 0-9999.99 inclusive.
     * Authorization:
     *  - Employee can edit their own request.
     *  - Manager can edit any request.
     *      - Manager is considered an employee for their own request.
     *  - NOBODY can edit a finished request. (maybe an admin role can - which doesn't exist)
     * Allowed Changes - Authorization extended:
     *  - Employee can change grade.
     *      - Grade must follow grade format
     *      - NOTE: If status was PENDING_GRADE -> sets to PENDING_APPROVAL
     *  - Employee can change status to cancelled.
     *  - Manager can change reimbursement amount.
     *      - Manager must provide a reason if changed.
     *      - Reimbursement amount can be over what the employee has.
     *  - Manager can change status.
     * Update is ignored if nothing changed.
     * @param username The username of the request.
     * @param rid The request id.
     * @param reqData The data to change.
     * @param token The token of the users active session.
     * @return The request if successful, and 400 series error otherwise.
     */
    public Pair<Boolean, Integer> updateRequest(String username, Integer rid, Request reqData, String token) {
        log.debug("Recieved username: " + username + " rid: " + rid + " reqData: " + reqData + " token: " + token);
        
        // ========================
        // === Validating input ===
        // ========================
        
        if (username == null || rid == null || reqData == null || token == null || username.isBlank() || rid < 0 || token.isBlank()) {
            log.error("username and/or rid and/or reqData and/ token input(s) is/are invalid");
            return new Pair<>(false, 400);
        }
        
        // Checking if user is in active session
        if (!ActiveEmployeeSessions.isActiveEmployee(token)) {
            log.error("User isn't in active session");
            return new Pair<>(false, 401);
        }
        
        // Checking is user is authorized to update the request
        String requesterUsername = ActiveEmployeeSessions.getActiveEmployeeUsername(token);
        Employee requesterEmp = empDAO.getEmployeeByUsername(requesterUsername);
        
        // Checking if employee exists - Shouldn't ever happen (Since only existing employees can be active users)
        if (requesterEmp == null) {
            log.error("Failed to get active user. Possible: Db failed???");
            return new Pair<>(false, 503);
        }
        
        // Checking if employee is updating their own request, or a manager is.
        if (requesterEmp.getType() == EmployeeType.EMPLOYEE && requesterEmp.getUsername().equals(username)) {}  // Employee is updating their own request
        else if (requesterEmp.getType() == EmployeeType.MANAGER) {}                                             // Manager is updating employee request
        else {
            log.error("User isn't authorized to update the given employee request");
            return new Pair<>(false, 403);
        }
        
        // Getting request to update
        RequestDTO reqDTO = reqDAO.getEmployeeRequestById(username, rid);
        
        // Checking if request exists
        if (reqDTO == null) {
            log.error("request doesn't exist");
            return new Pair<>(false, 404);
        }
        Request request = reqDTO.getRequest();
        
        // Checking if user is authorized to update a finished request. (Nobody is -> Make admin role)
        ArrayList<RequestStatus> statuses = new ArrayList<RequestStatus>(Arrays.asList(RequestStatus.getFinished()));
        if (statuses.contains(request.getStatus())) {
            log.error("User isn't authorized to update a finished request");
            return new Pair<>(false, 403);
        }
        
        // ==========================================
        // === Validating input | Allowed Changes ===
        // ==========================================
        
        // Checking if required reqData was provided (Depends on employee type)
        // Also checks if manager is editing their own request
        //  - If so, they're considered as an Employee
        boolean changed = false;
        if (requesterEmp.getType() == EmployeeType.EMPLOYEE || requesterUsername.equals(username)) {
            // === EMPLOYEE ===
            // === GRADE ===
            // Checking if grade was changed - Interprets null as removing grade
            if ((reqData.getGrade() != null && !reqData.getGrade().equals(request.getGrade())) ||
                (request.getGrade() != null && !request.getGrade().equals(reqData.getGrade()))) {
                // Grade was changed
                changed = true;
                // Validating whether grade follows grade format
                // Grade can be null - for no grade
                if (reqData.getGrade() != null && !GradeFormatType.validateGrade(request.getGradeFormat(), reqData.getGrade())) {
                    log.error("Employee: Grade is invalid or doesn't match grade format");
                    return new Pair<>(false, 400);
                }
                // Grade was valid - Updating grade
                request.setGrade(reqData.getGrade());
                
                // Checking if status was PENDING_GRADE - automation
                if (request.getStatus() == RequestStatus.PENDING_GRADE) {
                    // Automatically setting to PENDING_APPROVAL
                    request.setStatus(RequestStatus.PENDING_APPROVAL);
                }
            }
            
            // === STATUS ===
            // Checking if status was changed - ignores null
            if (reqData.getStatus() != null && reqData.getStatus() != request.getStatus()) {
                // Status was changed
                changed = true;
                // Validating whether status is cancelled or not
                if (reqData.getStatus() == null || reqData.getStatus() != RequestStatus.CANCELLED) {
                    log.error("Employee: user isn't authorized to change status to something other then cancelled");
                    return new Pair<>(false, 403);
                }
                // Status is valid
                request.setStatus(reqData.getStatus());
            }
        }
        else {
            // === MANAGER ===
            // === REIUMBRSEMENT AMOUNT ===
            // Checking if reimbursement amount was changed - Interprets null as (no change)
            if (reqData.getReimAmount() != null && !reqData.getReimAmount().equals(request.getReimAmount())) {
                // Reimbursement amount was changed
                changed = true;
                // Validating amount is within range
                if (reqData.getReimAmount() < 0 || reqData.getReimAmount() > 999.99) {
                    log.error("Manager: reimbursement amount is invalid or isn't within range.");
                    return new Pair<>(false, 400);
                }
                // Reim amount is valid
                request.setReimAmount(reqData.getReimAmount());
            }
            
            // === REASON ===
            // Checking if reim amount was changed and reason was provided
            if (changed && (reqData.getReason() == null || reqData.getReason().isBlank())) {
                log.error("Manager: Reimbursement amount was changed and reason wasn't provided or was invalid");
                return new Pair<>(false, 400);
            }
            // Checking if reason was changed - Interprets null as no change
            else if (reqData.getReason() != null && !reqData.getReason().equals(request.getReason())) {
                // Reason was changed
                changed = true;
                // Reason already valid
                request.setReason(reqData.getReason());
            }
            
            // === STATUS ===
            // Checking if status was changed - Ignores null
            if (reqData.getStatus() != null && reqData.getStatus() != request.getStatus()) {
                // Status changed
                changed = true;
                // Checking if status was set to approved
                if (reqData.getStatus() == RequestStatus.APPROVED) {
                    log.info("Request was approved: Sending funds to employee who owned the request");
                    Employee emp = empDAO.getEmployeeByUsername(username);
                    
                    // Checking if employee exists
                    if (emp == null) {
                        log.error("Failed to send funds: Employee doesn't exist: Cancelling request."); // Shouldn't happen, since only existing employees can create their own requests
                        return new Pair<>(false, 404);
                    }
                    
                    // Employee exists - Updating employee funds
                    Double updatedReimAmount = emp.getReimFunds() - request.getReimAmount();
                    if (updatedReimAmount < 0) {updatedReimAmount = 0.00;}
                    emp.setFunds(requesterEmp.getFunds() + request.getReimAmount());   // Adding reim amount
                    emp.setReimFunds(updatedReimAmount);                               // Removing amount form available funds.
                    boolean r = empDAO.updateEmployeeFunds(emp);
                    // Checking if failed - Service requesterEmp
                    if (!r) {
                        log.error("Failed to send funds: Possible: DB - failed???");
                        return new Pair<>(false, 503);   // Something horribly wrong happened.
                    }
                }
                request.setStatus(reqData.getStatus());  // Updating status
            }
        }
        
        // ========================
        // === Updating Request ===
        // ========================
        
        // Checking if request was changed
        boolean result = true;
        int status = 200;
        if (changed) {
            // Request was changed
            result = reqDAO.updateRequest(request);
        }
        
        // Checking if request failed to update
        if (!result) {
            log.error("Failed to update request: Possible: DB - Failed???");    // Something horribly wrong happened
            status = 503;
        }
        
        return new Pair<>(result, status);
    }
}
