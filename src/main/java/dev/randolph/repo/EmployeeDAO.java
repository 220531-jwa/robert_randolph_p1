package dev.randolph.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.randolph.model.Employee;
import dev.randolph.model.enums.EmployeeType;
import dev.randolph.util.ConnectionUtil;

public class EmployeeDAO {
    
    private ConnectionUtil cu = ConnectionUtil.getConnectionUtil();
    private static Logger log = LogManager.getLogger(EmployeeDAO.class);
    
    /*
     * === READ ===
     */
    
    /**
     * Gets an employee by their username
     * @param username The username of the employee
     * @return 
     */
    public Employee getEmployeeByUsername(String username) {
        // Init
        log.debug("Received username: " + username);
        String sql = "SELECT * FROM employees"
                + " WHERE username = ?";
        Employee emp = null;
        
        // Attempting to execute query
        try (Connection conn = cu.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Found employee
                emp = createEmployee(rs);
            }
            
        } catch (SQLException e) {
            log.error("Failed to execute query " + sql);
            e.printStackTrace();
        }
        
        return emp;
    }
    
    /*
     * === UTILITY ===
     */
    
    /**
     * Creates an employee from a result set.
     * @param rs The result set with the employee information.
     * @return The employee as an object.
     * @throws SQLException The classic.
     */
    private Employee createEmployee(ResultSet rs) throws SQLException {
        Employee emp = new Employee(
                rs.getString("username"),
                rs.getString("password_cred"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                EmployeeType.valueOf(rs.getString("employee_type")),
                rs.getDouble("reimbursement_funds"),
                rs.getDouble("funds"));
        
        return emp;
    }

}
