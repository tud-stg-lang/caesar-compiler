package org.caesarj.compiler.export;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.util.InconsistencyException;

/**
 * This Class mixes a list of CClass Objects to a mixin List CClass[]
 * 
 * @author Ivica Aracic
 */
public class ExportMixer {
    private static ExportMixer singleton = new ExportMixer();
   
    public static ExportMixer instance() {
        return singleton;
    }
    
    private ExportMixer() {
    }
    
    /**
     * mixes a List of Classes (e.g. A & B & C)
     */
    public CClass[] mix(CClass[] classes) throws ExportMixerException {        
        if(classes.length <= 1)
            throw new InconsistencyException("mixer algorithm needs at least two classes as input");

        // generate mixin lists
        List[] mixinLists = new List[classes.length];
        
        for(int i=0; i<classes.length; i++) {
            mixinLists[i] = generateMixinList(classes[i]);
        }
        
        // mix pairwise from right to left
        // CTODO: 1) & associative?  2) mix from left to right or from right to left
        List mixed = mixinLists[mixinLists.length-1];        
        for(int i=mixinLists.length-2; i>=0; i--) {
             mixed = mix(mixinLists[i], mixed);
        }
        
        return (CClass[])mixed.toArray(new CClass[]{});
    }
    
    /**
     * Mixes two mixinLists to a single mixin list using the 
     * linearization algorithm introduced by Erik Ernst
     */
    public List mix(List mixinList1, List mixinList2) throws ExportMixerException {
        LinkedList res = new LinkedList();
        
        CClass[] l1 = (CClass[])mixinList1.toArray(new CClass[]{});
        CClass[] l2 = (CClass[])mixinList2.toArray(new CClass[]{});
    
        int i1 = l1.length - 1;
        int i2 = l2.length - 1;
    
        while(i1>=0 && i2>=0) {
            
            CClass a = l1[i1];
            CClass b = l2[i2];
                        
            if(!containsMixin(b, mixinList1)) {
                res.add(0, b);
                i2--;
            }
            else if(containsMixin(b, mixinList1) && !containsMixin(a, mixinList2)) {
                res.add(0, a);
                i1--;
            }
            else if(a.getQualifiedName().equals(b.getQualifiedName())) {
                res.add(0, a);
                i1--;
                i2--;
            }
            else {
                throw new ExportMixerException("bad merge in export mixer");
            }
        }
        
        while(i1 >= 0)
            res.add(0, l1[i1--]);

        while(i2 >= 0)
            res.add(0, l2[i2--]);        
        
        return res;
    }
    
    // CTODO this should be checked over List.contains; need equals to be overridden correctly!
    private boolean containsMixin(CClass mixin, List l) {
        for(Iterator it=l.iterator(); it.hasNext(); ) {
            CClass other = (CClass)it.next();
            if(other.getQualifiedName().equals(mixin.getQualifiedName()))
                return true;
        }
        return false;
    }
        
    /**
     * Generates Mixin List from a export object
     */
    private List generateMixinList(CClass clazz) {
        List res = new ArrayList();
        CClass currentClass = clazz;
        while(currentClass != null) {
            res.add(currentClass);
            currentClass = currentClass.getSuperClass();
        }
        return res;
    }
    
    public String generateClassName(CClass[] mixins) throws ExportMixerException {
        try {       
            StringBuffer packageNames = new StringBuffer();
            StringBuffer className    = new StringBuffer();
            for(int i=0; i<mixins.length; i++) {
                
                className.append('_');
                className.append(mixins[i].getIdent());
                
                if(mixins[i].getPackage().length() > 0)
                    packageNames.append(mixins[i].getPackage());
                else 
                    packageNames.append("(default)");
            }
            
            className.append('_');
            className.append(generateHashCode(packageNames.toString()));
            
            return 
                className.toString();
        }
        catch(Exception e) {
			throw new ExportMixerException(e);
		}
    }
    
    // CTODO generating hex string from byte array? do we realy need to do this manually?
    private String generateHashCode(String packageNames) throws NoSuchAlgorithmException {        
        if(packageNames.length() == 0)
            throw new InconsistencyException("packageNames String must not be empty!");
            
        char hexVals[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] hash = messageDigest.digest(packageNames.getBytes());
        StringBuffer res = new StringBuffer();
        
        for(int i=0; i<hash.length; i++) {
            int hi = (hash[i] >> 4) & 15;
            int lo = hash[i] & 15;
            res.append(hexVals[hi]);
            res.append(hexVals[lo]);  
        }
        
        return res.toString();
    }
}

