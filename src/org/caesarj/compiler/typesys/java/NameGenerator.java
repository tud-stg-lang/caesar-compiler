/*
 * Created on 03.05.2004
 *
 */
package org.caesarj.compiler.typesys.java;

import java.security.MessageDigest;

/**
 * 
 * @author Karl Klose
 */
public class NameGenerator {
    // CTODO generating hex string from byte array? do we realy need to do this manually?
    public static String generateHashCode(String packageNames) {               
        if(packageNames.length() == 0)
             throw new RuntimeException("packageNames String must not be empty!");
            
        char hexVals[] = {
    		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
			'a', 'b', 'c', 'd', 'e', 'f'
		};
        
        try { 
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] hash = messageDigest.digest(packageNames.getBytes());
            StringBuffer res = new StringBuffer();
            
            int m = 0;
            for(int i=0; i<hash.length; i++) {
                
                m ^= hash[i];
                
                if((i % 8) == 0) {
                    int hi = (m >> 4) & 15;
                    int lo = m & 15;

                	res.append(hexVals[hi]);
                	res.append(hexVals[lo]);
                }
            }
            
            return res.toString();
        }
        catch (Exception e) {
            throw new RuntimeException("can not build hashcode", e);
		}
    }
}
