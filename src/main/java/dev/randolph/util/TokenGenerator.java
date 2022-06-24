package dev.randolph.util;

import java.util.Random;
import java.util.TreeSet;

public class TokenGenerator {
    
    private static Random rand = new Random();
    private static TreeSet<String> usedTokens = new TreeSet<String>();
    
    public static String generateToken() {
        String token = "";
        
        do {
            token = "" + Math.abs(rand.nextInt()) + Math.abs(rand.nextInt());
        } while (!usedTokens.contains(token));
            
        return token;
    }
    
    public static void main(String[] args) {
        System.out.println(generateToken());
    }

}
