package dev.randolph.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.randolph.model.Request;
import dev.randolph.model.DTO.RequestDTO;
import dev.randolph.model.enums.EventType;
import dev.randolph.model.enums.GradeFormatType;
import dev.randolph.model.enums.RequestStatus;
import dev.randolph.util.ConnectionUtil;

public class RequestDAO {
    
    private ConnectionUtil cu = ConnectionUtil.getConnectionUtil();
    private static Logger log = LogManager.getLogger(RequestDAO.class);
    
    /*
     * === CREATE ===
     */
    
    /**
     * Adds a request to the database.
     * @param request The request to add
     * @return Returns true if the 
     */
    public boolean createRequest(Request reqData) {
        log.debug("Recieved reqData: " + reqData);
        String sql = "insert into requests"
                + " (id, employee_username, event_type,"
                + "status, request_cost, reimbursement_amount, grade_format,"
                + "cutoff, justification, submission_date, start_date,"
                + "event_location, event_description, urgent, exceeds_funds)"
                + " values (default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Attempting to execute query
        try (Connection conn = cu.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, reqData.getEmployeeUsername());
            ps.setString(2, reqData.getEventType().name());
            ps.setString(3, reqData.getStatus().name());
            ps.setDouble(4, reqData.getCost());
            ps.setDouble(5, reqData.getReimAmount());
            ps.setString(6, reqData.getGradeFormat().name());
            ps.setString(7, reqData.getCutoff());
            ps.setString(8, reqData.getJustification());
            ps.setTimestamp(9, reqData.getSubmissionDate());
            ps.setTimestamp(10, reqData.getStartDate());
            ps.setString(11, reqData.getEventLocation());
            ps.setString(12, reqData.getEventDescription());
            ps.setBoolean(13, reqData.getIsUrgent());
            ps.setBoolean(14, reqData.getExceedsFunds());
            int changes = ps.executeUpdate();
            
            if (changes != 0) {
                // Created
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to execute query " + sql);
            e.printStackTrace();
        }
        
        return false;
    }
    
    /*
     * === READ ===
     */
    
    /**
     * Retrieves all the employee requests from the database.
     * Can add filters to filter by status.
     * @param filter The filters to filter by (Optional)
     * @return The list of requests. Can be empty.
     */
    public List<RequestDTO> getAllRequests(RequestStatus[] filter) {
        log.debug("Recieved filter: " + filter);
        String sql = "select username, first_name, last_name, r.*"
                + " from employees, requests r"
                + " where username = employee_username";
        ArrayList<RequestDTO> requests = null;
        
        sql = addStatusFilters(sql, filter);
        
        // Attempting to execute query
        try (Connection conn = cu.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            setStatusFilters(ps, 1, filter);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Found requests
                requests = new ArrayList<RequestDTO>();
                do {
                    requests.add(createRequestDTO(rs, false));
                } while (rs.next());
            }
        } catch (SQLException e) {
            log.error("Failed to execute query " + sql);
            e.printStackTrace();
        }
        
        return requests;
    }
    
    /**
     * Retrieves all the requests of the given username.
     * Can add filters for status
     * @param username The username to find the requests of.
     * @param filter The list of status filters. (Optional)
     * @return A list of requests if successful, and null otherwise.
     */
    public List<RequestDTO> getAllEmployeeRequests(String username, RequestStatus[] filter) {
        log.debug("Recieved username: " + username + " filter: " + Arrays.toString(filter));
        String sql = "select username, first_name, last_name, r.*"
                + " from employees, requests r"
                + " where username = employee_username"
                + " and username = ?";
        ArrayList<RequestDTO> requests = null;
        
        // Adding filters if any
        sql = addStatusFilters(sql, filter);
        
        // Attempting to execute query
        try (Connection conn = cu.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            setStatusFilters(ps, 2, filter);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next() ) {
                // Found requests
                requests = new ArrayList<>();
                do {
                    requests.add(createRequestDTO(rs, false));
                } while (rs.next());
            }
        } catch (SQLException e) {
            log.error("Failed to execute query " + sql);
            e.printStackTrace();
        }
        
        return requests;
    }
    
    /**
     * Retrieves a specific employee request
     * @param username The username of the request.
     * @param rid The id of the request.
     * @return A RequestDTO if successful, and null otherwise.
     */
    public RequestDTO getEmployeeRequestById(String username, Integer rid) {
        log.debug("Recieved username: " + username + " rid: " + rid);
        String sql = "select username, first_name, last_name, reimbursement_funds, r.*"
                + " from employees, requests r"
                + " where username = employee_username"
                + " and username = ? and id = ?";
        RequestDTO request = null;
        
        // Attempting to execute query
        try (Connection conn = cu.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setInt(2, rid);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next() ) {
                // Found requests
                request = createRequestDTO(rs, true);
            }
        } catch (SQLException e) {
            log.error("Failed to execute query " + sql);
            e.printStackTrace();
        }
        
        return request;
    }
    
    /*
     * === UPDATE ===
     */
    
    /**
     * Updates the request with the given data values.
     * Only updates status, reim amount, grade, reason.
     * @param request The request to change with the updated data.
     * @return True if successful, and false otherwise.
     */
    public boolean updateRequest(Request reqData) {
        log.debug("Recieved reqData: " + reqData);
        String sql = "update requests"
                + " set (status, reimbursement_amount, grade, reason) ="
                + " (?, ?, ?, ?)"
                + " where employee_username = ? and id = ?";
        
        // Attempting to execute query
        try (Connection conn = cu.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, reqData.getStatus().name());
            ps.setDouble(2, reqData.getReimAmount());
            ps.setString(3, reqData.getGrade());
            ps.setString(4, reqData.getReason());
            ps.setString(5, reqData.getEmployeeUsername());
            ps.setInt(6, reqData.getId());
            int changes = ps.executeUpdate();
            
            if (changes != 0) {
                // Updated
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to execute query " + sql);
            e.printStackTrace();
        }
        
        return false;
    }
    
    /*
     * === UTILITY ===
     */
    
    /**
     * Adds a prepared filter to the sql query.
     * @param sql The sql statement to add the filter to.
     * @param filter The list of filters to apply. (Only adds ? for setFilters)
     * @return The sql statement with the prepared fitlers.
     * @see setFilters
     */
    private String addStatusFilters(String sql, RequestStatus[] filter) {
        // Checking if there are filters to add.
        if (filter == null) {
            // No filters to add
            return sql;
        }
        log.debug("Adding filters to sql");
        
        StringBuilder builder = new StringBuilder(sql);
        builder.append(" and status in (");
        for (int i = 0; i < filter.length; i++) {
            if (i == 0) {
                builder.append("?");
            }
            else {
                builder.append(", ?");
            }
        }
        builder.append(")");
        
        return builder.toString();
    }
    
    /**
     * Adds the actual filters to the prepared sql statement.
     * @param ps The prepared statement to set the filters
     * @param index The index to start setting the filters.
     * @param filter The filters to set.
     * @see addFilters
     * @throws SQLException
     */
    private void setStatusFilters(PreparedStatement ps, int index, RequestStatus[] filter) throws SQLException {
        // Checking if there are filters to set.
        if (filter == null) {
            // No filters to set
            return;
        }
        log.debug("Adding filters to sql");
        
        for (int i = 0; i < filter.length; i++) {
            ps.setString(i+index, filter[i].toString());
        }
    }
    
    /**
     * Creates a Request with the given result set.
     * Result set must have the actual data elements.
     * @param rs The result set that currently holds the data.
     * @return A Request
     * @throws SQLException
     */
    private Request createRequest(ResultSet rs) throws SQLException {
        Request req = new Request(
                rs.getInt("id"),
                rs.getString("employee_username"),
                EventType.valueOf(rs.getString("event_type")),
                RequestStatus.valueOf(rs.getString("status")),
                rs.getDouble("request_cost"),
                rs.getDouble("reimbursement_amount"),
                GradeFormatType.valueOf(rs.getString("grade_format")),
                rs.getString("grade"),
                rs.getString("cutoff"),
                rs.getString("justification"),
                rs.getTimestamp("submission_date"),
                rs.getTimestamp("start_date"),
                rs.getString("event_location"),
                rs.getString("event_description"),
                rs.getBoolean("urgent"),
                rs.getBoolean("exceeds_funds"),
                rs.getString("reason"));
        
        return req;
    }
    
    /**
     * Creates a RequestDTO with the given result set.
     * Result set must have the actual data elements.
     * @param rs The result set that currently holds the data.
     * @param reimFunds Whether or not to check for reimbursement funds
     * @return A RequestDTO
     * @throws SQLException
     */
    private RequestDTO createRequestDTO(ResultSet rs, boolean reimFunds) throws SQLException {
        RequestDTO req = new RequestDTO(
                rs.getString("first_name"),
                rs.getString("last_name"),
                reimFunds ? rs.getDouble("reimbursement_funds") : null,
                createRequest(rs));
        
        return req;
    }

}
