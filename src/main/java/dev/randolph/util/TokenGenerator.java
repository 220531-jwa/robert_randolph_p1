package dev.randolph.util;

import java.util.Random;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TokenGenerator {
    
    private Random rand = new Random();
    private TreeSet<String> usedTokens = new TreeSet<String>();
    private static Logger log = LogManager.getLogger(TokenGenerator.class);
    
    /**
     * Generates a token as a string composed of two random integers.
     * The token is unique in that this will never return a token that is in use.
     * Used tokens can be released using removeUsedToken.
     * @return A unique token that isn't currently being used.
     * @see removeUsedToken
     */
    public String generateToken() {
        log.debug("Genearting new token");
        String token = "";
        
        do {
            token = "" + Math.abs(rand.nextInt()) + Math.abs(rand.nextInt());
        } while (usedTokens.contains(token));
            
        return token;
    }
    
    /**
     * Marks the given token as no longer being used.
     * The token must be in use for this to be successful.
     * @param token The token to mark as not used.
     * @return True if removed successfully, and false otherwise.
     */
    public boolean removeUsedToken(String token) {
        log.debug("Removing token: " + token);
        // Checking if token is being used
        if (!usedTokens.contains(token)) {
            // Token isn't being used.
            log.error("Token isn't in use.");
            return false;
        }
        
        usedTokens.remove(token);
        
        return true;
    }

}
