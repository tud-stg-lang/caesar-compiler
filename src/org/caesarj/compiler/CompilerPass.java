package org.caesarj.compiler;

import java.util.Iterator;
import java.util.Vector;

import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;

/**
 * Stores Information about a compiler pass.
 * 
 * @author Ivica Aracic
 */
public class CompilerPass {

    public CompilerPass() {
        this(new Vector(), new Vector(), null);
    }
    
    public CompilerPass(CompilerPass nextPass) {
        this(new Vector(), new Vector(), nextPass);
    }
    
    public CompilerPass(Vector typesToMix, Vector typesToCompile, CompilerPass nextPass) {        
        this.typesToMix = typesToMix;
        this.typesToCompile = typesToCompile;
        this.nextPass = nextPass;
    }

    /**
     * Begins a compile pass
     * - enable types to compile in this pass
     */
    public void begin() {
        for(Iterator it=typesToCompile.iterator(); it.hasNext();) {
            ((JTypeDeclaration)it.next()).setEnabled(true);
        }
    }

    /**
     * Ends this compile pass
     * - disables all types that has been compiled in this pass
     */
    public void end() {
        for(Iterator it=typesToCompile.iterator(); it.hasNext();) {
            ((JTypeDeclaration)it.next()).setEnabled(false);
        }
    }

    public void setNextPass(CompilerPass nextPass) {
        this.nextPass = nextPass;
    }
    
    public CompilerPass getNextPass() {
        return nextPass;
    }

    public Vector getTypesToCompile() {
        return typesToCompile;
    }

    public Vector getTypesToMix() {
        return typesToMix;
    }    

    // -----------------------------------------------------
    
    private Vector typesToMix; // of CCompositeType
    private Vector typesToCompile; // of JTypeDeclaration
    private CompilerPass nextPass;

}
