package com.smurfhouse.scratch.util; /**
 * Created by paco on 2/7/16.
 */
import java.util.HashMap;
import java.util.regex.Pattern;

public class CachedPattern {
    private static HashMap<String, Pattern> cached = new HashMap<>();

    /**
     * This value must be unique, to make sure user won't use this inside "regex" variable,
     * so that objects without flags would be returned
     * For example if UNIQUE_HASH would be empty:
     *     compile(pattern = "abc1")
     *          VS.
     *     compile(pattern = "abc", flag = 1)
     * This would give same keys "abc1" and "abc1"
     */
    private static final String UNIQUE_HASH = "(())[]+@#$%^@!@#$%*";

    public static Pattern compile(String regex){
        if(cached.containsKey(regex)){
            return cached.get(regex);
        }
        Pattern p = Pattern.compile(regex);
        cached.put(regex, p);
        return p;
    }
    public static Pattern compile(String regex, int flags){
        String uniqueKey = regex + UNIQUE_HASH + flags;
        if(cached.containsKey(uniqueKey)){
            return cached.get(uniqueKey);
        }
        Pattern p = Pattern.compile(regex);
        cached.put(uniqueKey, p);
        return p;
    }

}
