package org.caesarj.mixer;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jdt.internal.core.util.ToStringSorter;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class MixinList {
    
	/* DEBUG */
	private static boolean appendHashcode = true;
	public static void appendHashcode( boolean set ){
		appendHashcode = set;
	}
	
    private LinkedList l = new LinkedList();
    
    public Iterator iterator() {
        return l.iterator();
    }
    
    public void addFront(Element e) {
        l.add(0, e);
    }
    
    public void addTail(Element e) {
        l.add(e);
    }
    
    public int size() {
        return l.size();
    }
    
    public Element get(int i) {
        return (Element)l.get(i);
    }
    
    public boolean contains(Element e) {
        return l.contains(e);
    }
    
    public void setAt( int i, Element e ){
    	l.set(i,e);
    }
    
    /**
     * Generates a name for the class represented by the mixin list.
     */
    public String generateClassName() throws MixerException {
    	return generateClassName(0, size()-1);
    }
    
    /**
     * Generates a name for the class represented by the (partial) list 
     * beginning with item start and icnluding element end. 
     */
    public String generateClassName(int start, int end) throws MixerException {
        try {       
            StringBuffer packageNames = new StringBuffer();
            StringBuffer className    = new StringBuffer();
            for (int element = start; element<=end; element++){
                Element e = get(element);
                className.append('_');
                className.append(e.getInterfaceName());
                    
                if(e.getPackageName().length() > 0)
                    packageNames.append(e.getPackageName());
                else 
                    packageNames.append("(default)");
            }
                
            className.append('_');
           if (appendHashcode)
           		className.append(generateHashCode(packageNames.toString()));
                
            return 
                className.toString();
        }
        catch(Exception e) {
            throw new MixerException(e);
        }

    }
    
    //  CTODO generating hex string from byte array? do we realy need to do this manually?
    private String generateHashCode(String packageNames) {               
        if(packageNames.length() == 0)
             throw new RuntimeException("packageNames String must not be empty!");
            
        char hexVals[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        
        try { 
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] hash = messageDigest.digest(packageNames.getBytes());
            StringBuffer res = new StringBuffer();
            
            for(int i=0; i<hash.length; i++) {
                int hi = (hash[i] >> 4) & 15;
                int lo = hash[i] & 15;
                res.append(hexVals[hi]);
                res.append(hexVals[lo]);  
            }
            
            // shorten the md5 string
            /*
            for(int i=0; i<hash.length; i+=2) {
                int h = hash[i] + hash[i+1];
                int hi = (h >> 4) & 15;
                int lo = h & 15;
                res.append(hexVals[hi]);
                res.append(hexVals[lo]);  
            }
            */
            
            return res.toString();
        }
        catch (Exception e) {
            throw new RuntimeException("can not build hashcode", e);
		}
    }
    
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append('[');
        for(Iterator it = l.iterator(); it.hasNext();) {
            res.append(it.next());            
            if(it.hasNext())
                res.append(", ");
        }
        res.append(']');
        return res.toString();                
    }   
    
    public static MixinList createListFromString(String s) {
        StringTokenizer tok = new StringTokenizer(s, ",");
        MixinList res = new MixinList();
        
        while(tok.hasMoreTokens()) {           
            res.addTail(new Element(tok.nextToken().trim()));
        }
        
        return res;
    }
   
    
    public static class Element {
        private String className;
        private String packageName;        
        private String fullQualifiedName;
        private String interfaceName;
        
        public Element(String fullQualifiedName) {
            String packageName = "";
            String className = fullQualifiedName;
            
            int i = fullQualifiedName.lastIndexOf('/');
            
            if(i >= 0) {
                packageName = fullQualifiedName.substring(0, i);
                className = fullQualifiedName.substring(i+1, fullQualifiedName.length());
            }
            
            init(packageName, className);
        }
                
        public Element(String packageName, String className) {
            init(packageName, className);
		}
        
        private void init(String packageName, String className) {
            this.packageName = packageName;
            this.className   = className;
            fullQualifiedName = packageName+'/'+className;
            interfaceName = className;
            if(interfaceName.endsWith("_Impl")) {
                interfaceName = interfaceName.substring(0, interfaceName.lastIndexOf("_Impl"));
            }
        }

		public String getClassName() {
			return className;
		}
        
        public String getInterfaceName() {
            return interfaceName;
        }

		public String getPackageName() {
			return packageName;
		}
        
        public String getFullQualifiedName() {
            return fullQualifiedName;
        }

		public boolean equals(Object obj) {
            Element e = (Element)obj;
			return e.getFullQualifiedName().equals(this.getFullQualifiedName());
		}

		public int hashCode() {
			return getFullQualifiedName().hashCode();
		}

        public String toString() {
            return getInterfaceName();
        }
    }
}
