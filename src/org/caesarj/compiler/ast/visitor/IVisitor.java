package org.caesarj.compiler.ast.visitor;

import org.caesarj.compiler.ast.phylum.JPhylum;

/**
 * Visitor interface
 * 
 * @author Ivica Aracic
 */
public interface IVisitor {
    boolean start(JPhylum node);
    void end();
}
