/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
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
 * $Id: CjAssignmentExpression.java,v 1.3 2004-09-09 14:35:56 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * This class implements the assignment operation
 */
public class CjAssignmentExpression extends JBinaryExpression {

    public CjAssignmentExpression(
        TokenReference where,
        JExpression left,
        JExpression right) {
        super(where, left, right);
    }

    public boolean isStatementExpression() {
        return true;
    }

    public JExpression analyse(CExpressionContext context) throws PositionedError {
        TypeFactory factory = context.getTypeFactory();       

        JExpression expr;

        right = right.analyse(context);
        
        expr = new JAssignmentExpression(getTokenReference(), left, right);
	                
        CClass contextClass = context.getClassContext().getCClass();
        
        if(contextClass.getQualifiedName().startsWith("generated/test3/")) {
            System.out.println("stop here");
        }
        
        CType rightType = right.getType(factory);
        
        if(rightType.isClassType()) {
		    CClass rightClass = rightType.getCClass();
		        
		    if(rightClass.isMixinInterface() && contextClass.isMixin()) {
	            String newRightClassQn = 
	                context.getEnvironment().getCaesarTypeSystem().
	                	findInContextOf(
	                	    rightClass.getQualifiedName(),
		                    contextClass.convertToIfcQn()
		                );
	            
	            if(newRightClassQn != null) {
		            CClass newPrefixClass = 
		                context.getClassReader().loadClass(
		                    factory,
		                    newRightClassQn
		                );
		            
		            CType newRightType = newPrefixClass.getAbstractType();          
		            
		            expr = new JAssignmentExpression(
		                getTokenReference(),
		                left,
		                new JCastExpression(
		                    getTokenReference(),
		                    right,
		                    newRightType
		                )
		            );
	            }	        
	        }  
        }

        return expr.analyse(context);
    }

    public void genCode(GenerationContext context, boolean discardValue) {
        throw new InconsistencyException();
    }
}