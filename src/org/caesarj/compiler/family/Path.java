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
 * $Id: Path.java,v 1.13 2005-02-04 19:09:07 aracic Exp $
 */

package org.caesarj.compiler.family;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.ast.phylum.expression.CjAccessorCallExpression;
import org.caesarj.compiler.ast.phylum.expression.CjOuterExpression;
import org.caesarj.compiler.ast.phylum.expression.JCastExpression;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JFieldAccessExpression;
import org.caesarj.compiler.ast.phylum.expression.JLocalVariableExpression;
import org.caesarj.compiler.ast.phylum.expression.JMethodCallExpression;
import org.caesarj.compiler.ast.phylum.expression.JQualifiedInstanceCreation;
import org.caesarj.compiler.ast.phylum.expression.JThisExpression;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.phylum.variable.JLocalVariable;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.Constants;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.CMethodContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.UnpositionedError;

/**
 * ...
 * 
 * @author Karl Klose
 */
public abstract class Path {       

    private static final String ANONYMOUS_FIELD_ACCESS = "$".intern();
    Path prefix;
    CReferenceType type;
    
    private Path getPrefix() {
        return prefix;
    }      
    
    public Path(Path prefix, CReferenceType type) {
        this.prefix = prefix;
        this.type = type;
    }
    
    public CReferenceType getType() {
        return type;
    }

    /**
     * Calculate <code>k</code> for an expression in a given context.
     * @param contextClass The context in which the expression should be analysed
     * @param expr	The expression containing the path
     */
    public static int calcK(CContext context, JExpression expr) throws UnpositionedError {
        Path p = createFrom(context, expr);
        return ((ContextExpression)p.getHead()).getK(); 
    }

    public Path substituteFirstAccess(Path path){
        Path p = clonePath();
        Path pred = p.getPredOf( p.getHeadPred() );        

        if(pred != null) {            
            pred.prefix = path;
            return p;
        }
        else {
            return path;
        }
    }
    
    /**
     * Create a path from an expression.
     * @param contextClass The context in which the expression should be analysed
     * @param expr	The expression containing the path
     */
    public static Path createFrom(CContext context, JExpression expr) throws UnpositionedError {    	
        List 	nameList = new LinkedList();	// stores the field access identifiers (Strings)
        List 	typeList = new LinkedList();	// stores the corresponding types (CTypes)
        int 	k =0;
        JExpression tmp = expr;
        
        boolean done = false;
        
        while (!done){
            if(tmp instanceof JQualifiedInstanceCreation){
                JQualifiedInstanceCreation qc = (JQualifiedInstanceCreation) tmp;
                nameList.add(0, ANONYMOUS_FIELD_ACCESS );
                
                CReferenceType type = (CReferenceType)qc.getType(context.getTypeFactory());
                type.setDeclContext(context);
                
                typeList.add(type);
                done = true;
                
            }
            else if(tmp instanceof CjOuterExpression) {
                CjOuterExpression oe = (CjOuterExpression)tmp;
                k += oe.getOuterSteps();
                done = true;
            }
            else if(tmp instanceof CjAccessorCallExpression) {
                CjAccessorCallExpression ac = (CjAccessorCallExpression)tmp;
                nameList.add(0, ac.getFieldIdent());
                CType type = ac.getType(context.getTypeFactory()); 
                
                typeList.add(0, type);
                tmp = ac.getPrefix();
            }
            else if (tmp instanceof JFieldAccessExpression){
                // if tmp is a field-access...
                JFieldAccessExpression fa = (JFieldAccessExpression)tmp;
                if ( fa.getIdent().equals(Constants.JAV_OUTER_THIS) ){
                    // ... if it is this$0.xxx then increase k ...
                    context = context.getParentContext();
                    k++;
                } else {
                    // ... else add identifier to path.
                    nameList.add(0, fa.getIdent());    
                    typeList.add(0, fa.getType(context.getTypeFactory()));
                }
                tmp = fa.getPrefix();
            
            }
            else if (tmp instanceof JThisExpression) {
                while (! (context instanceof CClassContext)){
                    context = context.getParentContext();
                    k++;
                }
                CClass owner = ((JThisExpression)tmp).getSelf();
                CClass current = context.getClassContext().getCClass();
                while(current != owner) {
                    if(current == null) throw new InconsistencyException();
                    current = current.getOwner();
                    k++;
                }
                done = true;
            }
            else if (tmp instanceof JLocalVariableExpression) {
                JLocalVariableExpression local = (JLocalVariableExpression) tmp;
                JLocalVariable var = local.getVariable();
                
                int pos = local.getVariable().getPosition();
                
                if(var instanceof JFormalParameter) {
                    nameList.add(0, "#"+(pos-1));
                }
                else {
                    nameList.add(0, var.getIdent());
                }
                
                typeList.add(0, var.getType());

                
                if (var instanceof JFormalParameter) {
                    // find next MethodContext
                    while (! (context instanceof CMethodContext)) {
                        context = context.getParentContext();
                        k++;
                    }
                } 
                else if (var instanceof JVariableDefinition) {
                    // find next block-context that declares this variable
                    boolean found = false;
                    do {
                        if (! (context instanceof CBlockContext)){                            
                            throw new RuntimeException("Cannot find "+var.getIdent());
                        }
                        CBlockContext block = (CBlockContext)context;
                        if (block.containsVariable(var.getIdent())){
                            found = true;
                        } else {
                            context = context.getParentContext();
                            k++;
                        }
                    } while (!found);                    
                }
                done = true;
            } else if (tmp instanceof JMethodCallExpression){
                JMethodCallExpression me = (JMethodCallExpression) tmp;
                if (me.getIdent().startsWith(Constants.JAV_ACCESSOR) ){
                    CType type = me.getType(null);
                    CClass clazz = type.getCClass();
                    // ... find first class context ... 
                    while (!(context instanceof CClassContext)){
                        context = context.getParentContext();
                        k++;
                    }
                    // ... and search for the correct outer class.
                    while ( ((CClassContext)context).getCClass() != clazz ){
                        context = context.getParentContext();
                        k++;
                        if ( context == null || !(context instanceof CClassContext) ){
                            throw new UnpositionedError(
                                CaesarMessages.ILLEGAL_PATH_ELEMENT, 
                                "accessor method not returning a outer reference");
                        }
                    }
                    done = true;
                }
                else /*if(tmp == expr)*/ {
                    // method call is last in the chain
                    // handle for now as a field
                    // consider that we could have more than one method call
                    // f1.f2.f3.m1().m2().m3()
                    nameList.add(0, me.getIdent());    
                    typeList.add(0, me.getType(context.getTypeFactory()));
                    tmp = me.getPrefix();
                }
//                else {
//                    throw new InconsistencyException("Path can not include method calls");
//                }                
            }
            else if(tmp instanceof JCastExpression) {
                // CTODO: ignore for now
                tmp = ((JCastExpression)tmp).getExpression();
            }
            else {
                throw new UnpositionedError(CaesarMessages.ILLEGAL_PATH_ELEMENT, tmp.getIdent());
            }
        }
        
                
        // Construct path        
        Path result = new ContextExpression(null, k, null);
        
        
        Iterator it1, it2;
        for (it1 = nameList.iterator(), it2=typeList.iterator(); it1.hasNext();) {
            String name = (String) it1.next();
            CReferenceType refType = (CReferenceType)it2.next();

            /*
             * In the case we have a nested Java class which is not defined in the context
             * of the owner of that type, we throw an exception.
             * Reason: 
             * 1) Java inner classes may not be annotated with a path to a family.
             * 2) Even if so, subtype relations are different than those defined for Caesar types
             *    e.g.: Y y1 = x.new Y(); Y y2 = x2.new Y(); y1 = y2; // ok 
             */            
            if(refType.getCClass().isNested() && !refType.getCClass().isMixinInterface()) {                
                throw new UnpositionedError(CaesarMessages.INNER_PLAIN_JAVA_OBJECTS_IN_PATH);
            }            
            
            try {
	            if(name.startsWith("#")) {
	                int pos = Integer.parseInt( name.substring(1) );                    
	                result = new FieldAccess(result, name, refType, pos);
	            }
	            else { 
	                result = new FieldAccess(result, name, refType);
	        	}
            }
            catch (Exception e) {
                throw new InconsistencyException("invalid identifier for a parameter: "+name);
            }
        }        
        return result;        
    }

    public boolean equals(Path other) {
        return other.toString().equals(this.toString());
    }
    
    public Path getHead() {
        if(prefix != null)
            return prefix.getHead();
        else 
            return this;
    }
    
    public Path getHeadPred() {
        if(prefix == null) return null;
        if(prefix.prefix != null)
            return prefix.getHeadPred();
        else 
            return this;
    }

    public Path getPredOf(Path p){
        if (prefix == null) return null;
        if (prefix == p) return this;
        return prefix.getPredOf(p);
    }

    
    
    public abstract Path normalize() throws UnpositionedError;
    
    protected abstract Path _normalize(Path pred, Path tail) throws UnpositionedError;
    
    protected abstract Path clonePath();

}
