package org.caesarj.compiler;

import java.util.Iterator;
import java.util.Vector;

import org.caesarj.compiler.srcgraph.TypeNode;

/**
 * Stores Information about a compiler pass.
 * 
 * @author Ivica Aracic
 */
public class CompilerPass {

    public CompilerPass() {
        this(new Vector(), null);
    }
    
    public CompilerPass(CompilerPass nextPass) {
        this(new Vector(), nextPass);
    }
    
    public CompilerPass(Vector typesToMix, CompilerPass nextPass) {        
        this.typesToMix = typesToMix;
        this.nextPass = nextPass;
    }

    /**
     * Begins a compile pass
     * - enable types to compile in this pass
     */
    public void begin() {
    }

    /**
     * Ends this compile pass
     * - disables all types that has been compiled in this pass
     */
    public void end() {
    }

    public void setNextPass(CompilerPass nextPass) {
        this.nextPass = nextPass;
    }
    
    public CompilerPass getNextPass() {
        return nextPass;
    }

    public Vector getTypesToMix() {
        return typesToMix;
    }    

    // -----------------------------------------------------
    
    private Vector typesToMix; // of CCompositeType    
    private CompilerPass nextPass;

}
