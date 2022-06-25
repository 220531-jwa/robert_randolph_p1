package dev.randolph.util;

import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ActiveEmployeeSessions {
    
    private static Hashtable<String, String> activeEmployees = new Hashtable<>();   // Token => username
    private static TokenGenerator tg = new TokenGenerator();
    private static Logger log = LogManager.getLogger(ActiveEmployeeSessions.class);
    
    /**
     * Adds an active employee (signed in) allowing the employee to use server services.
     * Employees can have multiple active sessions.
     * Validation on whether the employee exists is determined in employee service.
     * Assumes that the employee exists.
     * Generated tokens are unique.
     * @param username The employee username that is active.
     * @return The token associated with the now active employee, and null otherwise.
     */
    public static String addActiveEmployee(String username) {
        log.debug("Creating active login session for username: " + username);
        // Generating token and activating user.
        String token = tg.generateToken();
        
        // Checking if token was successfully created
        if (token == null || token.isBlank()) {
            log.error("Failed to create active session: Token wasn't generated");
            return null;
        }
        
        activeEmployees.put(token, username);
        
        return token;
    }
    
    /**
     * Removes an active employee, meaning the employee can no longer use server services.
     * Used token is returned back to TokenGenerator.
     * Will fail if the token isn't active.
     * @param token The token of the employee to remove.
     * @return True if successful, and false otherwise.
     */
    public static boolean removeActiveEmployee(String token) {
        log.debug("Removing active login session for token: " + token);
        // Checking if employee is already active
        if (!isActiveEmployee(token) ) {
            // Isn't active - can't remove
            log.error("Token isn't associated with an active session.");
            return false;
        }
        
        // Removing active session
        activeEmployees.remove(token);
        tg.removeUsedToken(token);
        
        return true;
    }
    
    /**
     * Retrieves the username of the given token.
     * If the token isn't associated with an active session returns null.
     * @param token The token associated with the username for an active session.
     * @return The token username if found, and null otherwise.
     */
    public static String getActiveEmployeeUsername(String token) {
        log.debug("Getting employee username associated with token: " + token);
        // Checking if employee is active
        if (!isActiveEmployee(token)) {
            // isn't active - get get username
            log.error("Token isn't associated with an active session.");
            return null;
        }
        
        return activeEmployees.get(token);
    }
    
    /**
     * Determines if the employee of the given token is still active (signed in).
     * @param token The token associated with the employee.
     * @return True if the employee is active, and false otherwise.
     */
    public static boolean isActiveEmployee(String token) {
        log.debug("Checking if session is active with token: " + token);
        return activeEmployees.containsKey(token);
    }
}
