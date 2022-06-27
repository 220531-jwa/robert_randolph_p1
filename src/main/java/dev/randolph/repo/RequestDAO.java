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
    
    public List<RequestDTO> getAllRequests(RequestStatus[] filter) {
        log.debug("Recieved filter: " + filter);
        String sql = "select first_name, last_name, r.*"
                + " from employees, requests r"
                + " where username = employee_username";
        ArrayList<RequestDTO> requests = new ArrayList<>();
        
        // Updating sql query filter
        if (filter != null) {
            StringBuilder builder = new StringBuilder(sql);
            builder.append(" AND status in (");
            for (int i = 0; i < filter.length; i++) {
                if (i == 0) {
                    builder.append("?");
                }
                else {
                    builder.append(", ?");
                }
            }
            builder.append(")");
            sql = builder.toString();
        }
        
        // Attempting to execute query
        try (Connection conn = cu.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            if (filter != null) 
                for (int i = 0; i < filter.length; i++) {
                    ps.setString(i+1, filter[i].toString());
                }
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
    
    public List<RequestDTO> getAllEmployeeRequests(String username, Integer rid) {
        log.debug("Recieved username: " + username + " rid: " + rid);
        String sql = "select first_name, last_name, r.*"
                + " from employees, requests r"
                + " where username = ?";
        ArrayList<RequestDTO> requests = null;
        
        // Checking if getting specific request
        if (rid != null) {
            sql += " and id = ?";
        }
        
        // Attempting to execute query
        try (Connection conn = cu.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            if(rid != null) {
                ps.setInt(2, rid);
            }
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
    
    /*
     * === UTILITY ===
     */
    
    private Request createRequest(ResultSet rs) throws SQLException {
        log.debug("Creating Request Object");
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
    
    private RequestDTO createRequestDTO(ResultSet rs) throws SQLException {
        log.debug("Creating Request DTO Object");
        RequestDTO req = new RequestDTO(
                rs.getString("first_name"),
                rs.getString("last_name"),
                createRequest(rs));
        
        return req;
    }

}
