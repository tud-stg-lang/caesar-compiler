/*
 * Created on 03.05.2004
 *
 */
package org.caesarj.old.mixer;

import java.security.MessageDigest;

/**
 * 
 * @author Karl Klose
 */
public class NameGenerator {
	
	public static String forMixin( MixinList list ) throws MixerException{
		return forMixin(list, 0, list.size() );
	}

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

    
    /**
     * Finds the maximum postfix of all provided strings.
     *  findPostFix( ["A$X", "B$X"] ).equals("X") == true;
     * Side-effect: removes the prefix from the items of the array.  
     */
    public static String findPostfix( String classNames[] ){
    	int 	names = classNames.length;
    	String	postfix = "";
    	int 	postfixCount =0;
    	String temp[][] = new String[names][];
    	
    	int minRemaining=Integer.MAX_VALUE; 
    	// Split all class names 
    	for (int name=0; name<names; name++){
    		temp[name] = classNames[name].split("\\$");
   			minRemaining = Math.min(temp[name].length, minRemaining);
    	}
    	
    	int position=1;
    	while(minRemaining>=position){
    		boolean failed = false;
    		
    		String candidate = temp[0][ temp[0].length-position ];
    		
    		for (int check=1; check<temp.length; check++){
    			if ( ! temp[check][ temp[check].length-position ].equals(candidate) ){
    				failed = true;
    				break;
    			}
    		}
    		if (!failed){
	    		// This candidate is a postfix
	    		postfix = "$" + candidate;
	    		postfixCount++;
	    		position++;
	    	}
    		else	break;
    	}
    	
    	// at this point we modify the provided classnames 
    	for (int name=0; name<names; name++){
    		String t="";
    		for (int part=0; part<temp[name].length-postfixCount; part++){
    			t = t + (part==0?"":"$") + temp[name][part]; 
    		}
    		classNames[name] = t;
    	}
    	
    	return postfix;
    }
    
    
    public static String forMixin( MixinList list, int start, int end ) throws MixerException{
 
    	return list.generateClassName(start,end);
 /*  	
    	String [] names = new String[end-start+1];
   	   	StringBuffer packageNames = new StringBuffer();
   	   	StringBuffer className    = new StringBuffer();
    	
        for (int element = start; element<=end; element++){
            MixinList.Element e = list.get(element);
            
            String name = e.getFullName();
            
            names[element-start] = name;
            if(e.getPackageName().length() > 0)
                packageNames.append(e.getPackageName());
            else 
                packageNames.append("(default)");
        }
         
        String postfix = findPostfix(names);
        
        for (int name=0; name<names.length; name++){
        	className.append('_');
        	className.append(names[name]);
        }
        className.append('_');
        className.append(postfix);
        
        if (false) className.append(generateHashCode(packageNames.toString()));
        
        return className.toString();
	*/
	}

    
    
}
