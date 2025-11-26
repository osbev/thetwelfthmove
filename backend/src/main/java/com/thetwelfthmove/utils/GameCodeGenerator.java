// /backend/src/main/java/com/thetwelfthmove/utils/GameCodeGenerator.java
package com.thetwelfthmove.utils;

import java.util.Random;

public class GameCodeGenerator {
    
    private static final String[] BRAINROT_WORDS = {
        "skibidi", "gyatt", "rizz", "sigma", "alpha", "beta", "omega",
        "bussin", "slay", "cap", "nocap", "sus", "vibe", "mid", "fire",
        "goat", "bruh", "yeet", "simp", "stan", "karen", "chad", "based",
        "cringe", "ratio", "mog", "goon", "edging", "hawk", "tuah", "fanum",
        "tax", "ohio", "griddy", "giga", "mewing", "mogging", "aura",
        "brain", "rot", "zesty", "salty", "toxic", "unhinged", "unalive", "zev", "virna", "drixyl",
        "vera", "leira", "ynaki"
    };
    
    private static final Random random = new Random();
    
    public static String generateCode() {
        String word1 = BRAINROT_WORDS[random.nextInt(BRAINROT_WORDS.length)];
        String word2 = BRAINROT_WORDS[random.nextInt(BRAINROT_WORDS.length)];
        
        // Ensure words are different
        while (word2.equals(word1)) {
            word2 = BRAINROT_WORDS[random.nextInt(BRAINROT_WORDS.length)];
        }
        
        int number = random.nextInt(100); // 0-99
        
        return String.format("u-%s-%s-%d", word1, word2, number);
    }
    
    public static boolean isValidCodeFormat(String code) {
        if (code == null) return false;
        
        // Should match pattern: u-word-word-number
        String pattern = "^u-[a-z]+-[a-z]+-\\d{1,2}$";
        return code.matches(pattern);
    }
}