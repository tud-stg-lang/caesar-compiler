package org.caesarj.mixer;

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
    
    // TEST 
    public static void main(String[] args) throws Exception {
        MixinList l1 = MixinList.createListFromString("A, C");
        MixinList l2 = MixinList.createListFromString("B, C");
        MixinList res = Linearizator.instance().mix(l1, l2);
        
        System.out.println(res.generateClassName());               
        System.out.println(res.toString());
	}
        
}

