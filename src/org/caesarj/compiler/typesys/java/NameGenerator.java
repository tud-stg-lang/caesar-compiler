/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: NameGenerator.java,v 1.3 2005-01-24 16:52:58 aracic Exp $
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
