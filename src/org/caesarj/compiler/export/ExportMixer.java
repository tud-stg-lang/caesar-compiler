package org.caesarj.compiler.export;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.InconsistencyException;

/**
 * This Class mixes a list of CClass Objects to CMixedClass
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
    
    // CTODO export mixer not implemented yet
    /**
     * mixes a List of Classes (e.g. A & B & C)
     */
    public CClass mix(CClass[] classes) throws ExportMixerException {
        
        if(classes.length == 0)
            throw new InconsistencyException("we need at least one class in the classList");
        
        if(classes.length == 1)
            return classes[0];
        
        // generate mixin lists
        List[] mixinLists = new List[classes.length];
        
        for(int i=0; i<classes.length; i++) {
            // CTODO support for CSourceClass
            if(!(classes[i] instanceof CBinaryClass))
                throw new InconsistencyException("ExportMixer supports only CBinaryClass");
            mixinLists[i] = generateMixinList(classes[i]);
        }
        
        // mix pairwise from right to left
        // CTODO: 1) & associative?  2) mix from left to right or from right to left
        List mixed = mixinLists[mixinLists.length-1];
        for(int i=mixinLists.length-2; i>=0; i--) {
             mixed = mix(mixinLists[i], mixed);
        }
        
        return generateExportFromMixinList(mixed);
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
     * This method takes a list as input and transforms it to CClass
     * Algorithm in short: Create delegate and and replace supertype
     */
    private CClass generateExportFromMixinList(List mixinList) {        
        CClass[] l = (CClass[])mixinList.toArray(new CClass[]{});
        int i = l.length-2;     
/*
        CClass res = l[i].cloneAndReplaceSuperType(new CReferenceType(l[i+1]));
        
        while(i >= 0) {
            res = l[i--].cloneAndReplaceSuperType(new CReferenceType(res));
        }
        
        return res;
*/
        return (CClass)mixinList.get(0);
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

}
