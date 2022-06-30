package dev.randolph.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.randolph.model.Request;
import dev.randolph.model.DTO.RequestDTO;
import dev.randolph.model.enums.GradeFormatType;
import dev.randolph.model.enums.RequestStatus;
import dev.randolph.util.ConnectionUtil;

public class RequestDAO {
    
    private ConnectionUtil cu = ConnectionUtil.getConnectionUtil();
    private static Logger log = LogManager.getLogger(RequestDAO.class);
    
    /**
     * Retrieves all the employee requests from the database.
     * Can add filters to filter by status.
     * @param filter The filters to filter by (Optional)
     * @return The list of requests. Can be empty.
     */
    public List<RequestDTO> getAllRequests(RequestStatus[] filter) {
        log.debug("Recieved filter: " + filter);
        String sql = "select first_name, last_name, r.*"
                + " from employees, requests r"
                + " where username = employee_username";
        ArrayList<RequestDTO> requests = new ArrayList<>();
        
        sql = addStatusFilters(sql, filter);
        
        // Attempting to execute query
        try (Connection conn = cu.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            setStatusFilters(ps, 1, filter);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                // Found requests
                requests.add(createRequestDTO(rs));
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
        log.debug("Recieved username: " + username + " filter: " + filter);
        String sql = "select first_name, last_name, reimbursement_funds, r.*"
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
                    requests.add(createRequestDTO(rs));
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
    public RequestDTO getAllEmployeeRequestById(String username, Integer rid) {
        log.debug("Recieved username: " + username + " rid: " + rid);
        String sql = "select first_name, last_name, reimbursement_funds, r.*"
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
                request = createRequestDTO(rs);
            }
        } catch (SQLException e) {
            log.error("Failed to execute query " + sql);
            e.printStackTrace();
        }
        
        return request;
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
                rs.getString("event_type"),
                RequestStatus.valueOf(rs.getString("status")),
                rs.getDouble("request_cost"),
                rs.getDouble("reimbursement_amount"),
                rs.getString("grade"),
                GradeFormatType.valueOf(rs.getString("grade_format")),
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
     * @return A RequestDTO
     * @throws SQLException
     */
    private RequestDTO createRequestDTO(ResultSet rs) throws SQLException {
        RequestDTO req = new RequestDTO(
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getDouble("reimbursement_funds"),
                createRequest(rs));
        
        return req;
    }

}
