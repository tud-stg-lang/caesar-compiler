package org.caesarj.compiler.export;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CCjCompositeClassProxy extends CBadClass {

    // ----------------------------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------------------------

    public CCjCompositeClassProxy(String qualifiedName) {
        super(qualifiedName);
    }

    public boolean isAccessible(CClass from) {
        return true;
    }

}
