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
 * $Id: CjMethodCallExpression.java,v 1.13 2005-06-10 12:21:02 klose Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * Inserts casts for method calls 
 * 
 * @author Ivica Aracic 
 */
public class CjMethodCallExpression extends JExpression implements CaesarConstants {
    
    private JMethodCallExpression expr;
    
    public CjMethodCallExpression(
		TokenReference where,
		JExpression prefix,
		String ident,
		JExpression[] args
	) {
		super(where);
		
		expr = new JMethodCallExpression(getTokenReference(), prefix, ident, args);
	}
    
    public CType getType(TypeFactory factory) {
        throw new InconsistencyException();
    }
    
    public JExpression analyse(CExpressionContext context) throws PositionedError {        
        TypeFactory factory = context.getTypeFactory();              

        // analyse expression
        expr = (JMethodCallExpression)expr.analyse(context);
        
        
        // cast the return type
        CType returnType = expr.getType(factory);        
        
        //TODO Fix this code when implementing cclass arrays
        if(!returnType.isArrayType() && returnType.isClassType()) {
            
            CClass returnClass = returnType.getCClass();

            if(returnClass.isMixinInterface()) {
                
                String contextClassName = null;
                
                CClass prefixClass = expr.getPrefixType().getCClass();
                
                if(prefixClass.isMixin()) {
                    // in this case we have this or super;
                    // in both cases the context class is the local class
                    contextClassName = context.getClassContext().getCClass().convertToIfcQn();
                }
                else {
                    contextClassName = prefixClass.getQualifiedName();
                }
                
                String newReturnClassQn = 
	                context.getEnvironment().getCaesarTypeSystem().
	                	findInContextOf(
                	        returnClass.getQualifiedName(),
                	        contextClassName
		                );
                
                if(newReturnClassQn != null) {
		            CClass newReturnClass = 
		                context.getClassReader().loadClass(
		                    factory,
		                    newReturnClassQn
		                );
		            
		            JExpression res = new CjCastExpression(
	                    getTokenReference(),
	                    expr,
	                    newReturnClass.getAbstractType()
		            );
		            
		            return res.analyse(context);
	            }
            }
        }
        
        return expr;
    }
    
    
    public boolean isStatementExpression() {
        return true;
    }
    
    public void genCode(GenerationContext context, boolean discardValue) {
        throw new InconsistencyException();
    }    
    
    /**
     * note: this one is important for join point reflection visitor,
     * which searches for the usage of thisJoinPoint* variables
     */
    public void recurse(IVisitor p) {
        expr.accept(p);
    }
}
