/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: VisitorSupport.java,v 1.3 2005-01-24 16:53:02 aracic Exp $
 */

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
            ex.printStackTrace();
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