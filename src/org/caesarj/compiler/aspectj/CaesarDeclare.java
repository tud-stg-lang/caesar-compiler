/*
 * Created on 12.12.2003
 */
package org.caesarj.compiler.aspectj;

import org.aspectj.weaver.patterns.Declare;

/**
 * Wrapper for AspectJ-Declares
 * 
 * @author Karl Klose 
 */
public class CaesarDeclare {
    // attributes
    private Declare declare;

    // construction
    public CaesarDeclare(Declare declare) {
        this.declare = declare;
    }

    // interface
    public void resolve(CaesarScope scope) {
        declare.resolve(scope);
    }

    public Declare wrappee() {
        return declare;
    }

    /**
     * Returns an array containing the wrapped objects
     * 
     * @param declares
     *            An array of CaesarDeclares
     * @return An array of the wrappees
     */
    public static Declare[] wrappees(CaesarDeclare[] declares) {
        Declare[] ret = new Declare[declares.length];
        for (int i = 0; i < declares.length; i++)
            ret[i] = declares[i].wrappee();
        return ret;
    }
}