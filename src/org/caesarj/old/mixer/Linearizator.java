package org.caesarj.old.mixer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.caesarj.util.InconsistencyException;

/**
 * C3 Linearization Algorithm (&)
 * 
 * @author Ivica Aracic
 */
public class Linearizator {
    private static Linearizator singleton = new Linearizator();
   
    public static Linearizator instance() {
        return singleton;
    }
    
    private Linearizator() {
    }
    
    public List mixFromLeftToRight(List[] mixinLists) throws MixerException {
        if(mixinLists.length < 1)
            throw new MixerException("mixinLists array is empty");
        
        List merged = mixinLists[0];
        for (int i = 1; i < mixinLists.length; i++) {
            merged = mix(merged, mixinLists[i]);
        }
        
        return merged;
    }
    
    public List mixFromRightToLeft(List[] mixinLists) throws MixerException {
        if(mixinLists.length < 1)
            throw new MixerException("mixinLists array is empty");
        
        List merged = mixinLists[mixinLists.length-1];
        for (int i = mixinLists.length-2; i >= 0; i++) {
            merged = mix(mixinLists[i], merged);
        }
        
        return merged;
    }
    
    public List mix(List mixinList1, List mixinList2) throws MixerException {
        List res = new LinkedList();
        
        int i1 = mixinList1.size() - 1;
        int i2 = mixinList2.size() - 1;
    
        while(i1>=0 && i2>=0) {
            
            Object a = mixinList1.get(i1);
            Object b = mixinList2.get(i2);
                        
            if(!mixinList1.contains(b)) {
                res.add(0, b);
                i2--;
            }
            else if(mixinList1.contains(b) && !mixinList2.contains(a)) {
                res.add(0, a);
                i1--;
            }
            else if(a.equals(b)) {
                res.add(0, a);
                i1--;
                i2--;
            }
            else {
                throw new MixerException("bad merge");
            }
        }
        
        while(i1 >= 0)
            res.add(0, mixinList1.get(i1--));

        while(i2 >= 0)
            res.add(0, mixinList2.get(i2--));        
        
        return res;
    }
    
    
    /**
     * @deprecated this one will be removed soon
     */
    public MixinList mix(MixinList mixinList1, MixinList mixinList2) throws MixerException {
        MixinList res = new MixinList();
        
        int i1 = mixinList1.size() - 1;
        int i2 = mixinList2.size() - 1;
    
        while(i1>=0 && i2>=0) {
            
            MixinList.Element a = mixinList1.get(i1);
            MixinList.Element b = mixinList2.get(i2);
                        
            if(!mixinList1.contains(b)) {
                res.addFront(b);
                i2--;
            }
            else if(mixinList1.contains(b) && !mixinList2.contains(a)) {
                res.addFront(a);
                i1--;
            }
            else if(a.equals(b)) {
                res.addFront(a);
                i1--;
                i2--;
            }
            else {
                throw new MixerException("bad merge");
            }
        }
        
        while(i1 >= 0)
            res.addFront(mixinList1.get(i1--));

        while(i2 >= 0)
            res.addFront(mixinList2.get(i2--));        
        
        return res;
    }
            
}

