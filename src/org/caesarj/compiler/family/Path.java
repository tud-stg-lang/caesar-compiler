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
 * $Id: Path.java,v 1.22 2005-02-25 13:45:15 aracic Exp $
 */

package org.caesarj.compiler.family;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.ast.phylum.expression.CjAccessorCallExpression;
import org.caesarj.compiler.ast.phylum.expression.CjCastExpression;
import org.caesarj.compiler.ast.phylum.expression.CjOuterExpression;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JFieldAccessExpression;
import org.caesarj.compiler.ast.phylum.expression.JLocalVariableExpression;
import org.caesarj.compiler.ast.phylum.expression.JMethodCallExpression;
import org.caesarj.compiler.ast.phylum.expression.JQualifiedInstanceCreation;
import org.caesarj.compiler.ast.phylum.expression.JThisExpression;
import org.caesarj.compiler.ast.phylum.expression.JTypeNameExpression;
import org.caesarj.compiler.ast.phylum.expression.JUnaryPromote;
import org.caesarj.compiler.ast.phylum.expression.literal.JNullLiteral;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.phylum.variable.JLocalVariable;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.Constants;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CCompilationUnitContext;
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

    public static final Path NULL = new Null();
    
    private static final String ANONYMOUS_FIELD_ACCESS = "$".intern();
    
    protected Path prefix;
    protected CReferenceType type;
    
    public Path getPrefix() {
        return prefix;
    }
    
    public void setPrefix(Path prefix) {
        this.prefix = prefix;
    }
    
    public Path(Path prefix, CReferenceType type) {
        this.prefix = prefix;
        this.type = type;
    }       
    
    public CReferenceType getType() {
        return type;
    }

    public Path getTypePath() throws UnpositionedError {
        Path res = type.getPath();        
        
        return res;
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
     * Create a path from an expression. valid context are block-, method-, or class-ctx
     * 
     * CTODO this has to be refactored, some of the ast elements implements
     * the generation of the path by themself
     * 
     * @param contextClass The context in which the expression should be analysed
     * @param expr	The expression containing the path
     */
    public static Path createFrom(CContext context, JExpression expr) throws UnpositionedError {
        // assert we have a valid context
        if(
            !(  
                context instanceof CClassContext
                || context instanceof CMethodContext
                || context instanceof CBlockContext
            )
        ) {
            throw new InconsistencyException("context may not be null");
        }
        
        List	path = new LinkedList();
        int 	k =0;
        JExpression tmp = expr;
        
        boolean done = false;
        
        while (!done){
            if(tmp instanceof JNullLiteral) {
                return NULL;
            }
            else if(tmp instanceof JQualifiedInstanceCreation){
                JQualifiedInstanceCreation qc = (JQualifiedInstanceCreation) tmp;
                
                CReferenceType type = (CReferenceType)qc.getType(context.getTypeFactory());
                type.setDeclContext(context);
                
                path.add(0, new FieldAccess(null, ANONYMOUS_FIELD_ACCESS, type) );
                
                done = true;
                
            }
            else if(tmp instanceof CjOuterExpression) {
                CjOuterExpression oe = (CjOuterExpression)tmp;
                k += oe.getOuterSteps();
                
                // getOuterSteps is relative to the method context - 1
                // however, we could have a call from the method body
                while(context.getMethodContext() != null) {
                    context = context.getParentContext();
                    k++;
                }
                
                done = true;
            }
            else if(tmp instanceof CjAccessorCallExpression) {
                CjAccessorCallExpression ac = (CjAccessorCallExpression)tmp;
                CType type = ac.getType(context.getTypeFactory()); 
                
                // CTODO: type of the method call expression or type of the field?
                path.add(0, new FieldAccess(null, ac.getFieldIdent(), (CReferenceType)type) );
                
                tmp = ac.getPrefix();
            }
            else if (tmp instanceof JFieldAccessExpression){
                // if tmp is a field-access...
                JFieldAccessExpression fa = (JFieldAccessExpression)tmp;
                if ( fa.getIdent().equals(Constants.JAV_OUTER_THIS) ) {
                    // ... if it is this$0.xxx then increase k ...                   
                    k++;
                } else {
                    // ... else add identifier to path.
                    path.add(0, new FieldAccess(null, fa.getIdent(), (CReferenceType)fa.getType(context.getTypeFactory())) );
                }
                tmp = fa.getPrefix();
            
            }
            else if (tmp instanceof JThisExpression) {
                // navigate out to the first class context
                while (! (context instanceof CClassContext)){
                    context = context.getParentContext();
                    k++;
                }
                
                // then navigate to the owner
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
                                
                
                if(var instanceof JFormalParameter) {
                    path.add(0, new ArgumentAccess(null, (CReferenceType)var.getType(), var.getIndex()));
                }
                else {
                    path.add(0, new FieldAccess(null, var.getIdent(), (CReferenceType)var.getType()));
                }

                
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
                            throw new InconsistencyException("Cannot find "+var.getIdent());
                        }
                        CBlockContext block = (CBlockContext)context;
                        if (block.containsVariable(var.getIdent())){
                            found = true;
                        } 
                        else {
                            // we only want block context
                            context = context.getParentContext().getBlockContext();
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
                else if(me.getIdent().startsWith(CaesarConstants.FACTORY_METHOD_PREFIX)) {
                    done = true;
                }
                else /*if(tmp == expr)*/ {
                    // method call is last in the chain
                    // handle for now as a field
                    // consider that we could have more than one method call
                    // f1.f2.f3.m1().m2().m3()
                    path.add(0, new MethodAccess(null, me.getIdent(), (CReferenceType)me.getType(context.getTypeFactory())));
                    tmp = me.getPrefix();
                }
//                else {
//                    throw new InconsistencyException("Path can not include method calls");
//                }                
            }
            else if(tmp instanceof CjCastExpression) {
                // CTODO: ignore for now
                tmp = ((CjCastExpression)tmp).getExpression();
            }
            else if(tmp instanceof JUnaryPromote) {
                // CTODO: ignore for now
                tmp = ((JUnaryPromote)tmp).getExpression();
            }
            else if(tmp instanceof JTypeNameExpression) {
                JTypeNameExpression tne = (JTypeNameExpression)tmp;
                CReferenceType type = (CReferenceType)tmp.getType(context.getTypeFactory());
                
                path.add(0, new FieldAccess(null, type.getCClass().getQualifiedName(), type));
                // navigate out to the CU context
                CContext ctx = context;
                while(!(ctx instanceof CCompilationUnitContext)) {
                    ctx = ctx.getParentContext();
                    k++;
                }
                
                done = true;
            }
            else {
                throw new UnpositionedError(CaesarMessages.ILLEGAL_PATH_ELEMENT, tmp.getIdent());
            }
        }
        
                
        // Construct path        
        Path result = new ContextExpression(null, k, null);
                
        for (Iterator it = path.iterator(); it.hasNext();) {
            Path p = (Path) it.next();
            
            p.prefix = result;
            result = p;
        }        
        
        return result;        
    }
    
    /**
     * @return true if this path contains java elements
     */
    public boolean containsJavaElements() {
        if(getType().getCClass().isNested() && !getType().getCClass().isMixinInterface()) {
            return true;
        }   

        if(prefix != null)
            return prefix.containsJavaElements();
        else
            return false;
    }    
    
    public boolean isAssignableTo(Path other) {        
        return this==NULL || other.toString().equals(this.toString());
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
    
    public abstract Path normalize2() throws UnpositionedError;
    
    protected abstract Path _normalize(Path pred, Path tail) throws UnpositionedError;
    
    public abstract Path clonePath();

    public Path append(Path other) {
        other.getHead().prefix = this;
        return other;
    }

}
