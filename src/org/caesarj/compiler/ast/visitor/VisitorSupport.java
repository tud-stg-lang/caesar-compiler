package org.caesarj.compiler.ast.visitor;

import java.lang.reflect.Method;
import java.util.Stack;

import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.util.InconsistencyException;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class VisitorSupport implements IVisitor {

    private static class StackEntry {
        JPhylum astNode = null;
        Method visitMethod = null;
        Method endVisitMethod = null;
    }  

    Stack s = new Stack();
    Object visitorObject;    
    
    public VisitorSupport(Object visitorObj) {
        this.visitorObject = visitorObj;
    }
    
    private StackEntry selectNode(JPhylum node) {
        StackEntry e = new StackEntry();
        
        e.astNode = node;       
        Class currentClass = node.getClass();

        while (e.visitMethod == null) {
            try {                
                e.visitMethod = 
                    visitorObject.getClass().getMethod("visit", new Class[]{currentClass});

                if (e.visitMethod != null) {
                    try {
                        e.endVisitMethod = 
                            visitorObject.getClass().getMethod("endVisit", new Class[]{currentClass});
                    }
                    catch (Exception ex) {
                        // do nothing here, postVisitMethod is not necessery
                    }
                    break;
                }
            }
            catch (Exception ex) {
                if (!currentClass.equals(JPhylum.class)) currentClass = currentClass
                    .getSuperclass();
                else break;
            }
        }
        return e;
    }

    public boolean start(JPhylum node) {        
        if(node == null)
            throw new InconsistencyException("can not visit null node");
        
        StackEntry e = selectNode(node);
        s.push(e);

        try {
            if (e.visitMethod != null) {
                Object res = e.visitMethod.invoke(visitorObject, new Object[]{e.astNode});

                return ((Boolean) res).booleanValue();
            }
        }
        catch (ClassCastException cce) {
            throw new InconsistencyException("visit method has to return boolean");
        }
        catch (Exception ex) {
            throw new InconsistencyException(ex.getMessage());
        }
        
        return false;
    }

    public void end() {
        StackEntry e = (StackEntry)s.pop();
        
        try {
            if (e.endVisitMethod != null) 
                e.endVisitMethod.invoke(visitorObject, new Object[]{e.astNode});
        }        
        catch (Exception ex) {
            throw new InconsistencyException(ex.getMessage());
        }
    }
}