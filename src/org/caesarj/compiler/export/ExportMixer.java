package org.caesarj.compiler.export;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.mixer.Linearizator;
import org.caesarj.mixer.MixerException;
import org.caesarj.mixer.MixinList;
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
    public MixinList mix(CClass[] classes) throws MixerException {        
        if(classes.length <= 1)
            throw new InconsistencyException("mixer algorithm needs at least two classes as input");

        // generate mixin lists
        MixinList[] mixinLists = new MixinList[classes.length];
        
        for(int i=0; i<classes.length; i++) {
            mixinLists[i] = generateMixinList(classes[i]);
        }
        
        // mix pairwise from right to left
        // CTODO: 1) & associative?  2) mix from left to right or from right to left
        MixinList mixed = mixinLists[mixinLists.length-1];        
        for(int i=mixinLists.length-2; i>=0; i--) {
             mixed = Linearizator.instance().mix(mixinLists[i], mixed);
        }
        
        return mixed;
    }
            
    /**
     * Generates Mixin List from a export object    
     */
    private MixinList generateMixinList(CClass clazz) {
        MixinList res = new MixinList();
        CClass currentClass = clazz;
        while(currentClass != null && !currentClass.isObjectClass()) {
            res.addTail(new MixinList.Element(currentClass.getQualifiedName()));
            currentClass = currentClass.getSuperClass();
        }
        return res;
    }
    
}

