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
 * $Id: CjOuterExpression.java,v 1.3 2005-02-17 17:40:54 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.family.ContextExpression;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;


/**
 * handles the outer().outer().outer()... calls
 * A.B.this.a() -> outer().a() in A.B.C
 * 
 * @author Ivica Aracic
 */
public class CjOuterExpression extends JExpression {
    
    private CReferenceType prefixType;
    private JExpression transformation = null;
    private int steps = 0; /** number of outer calls */
    
    CjOuterExpression(TokenReference where, CReferenceType prefixType) {
        super(where);
        this.prefixType = prefixType;
    }
    
    public JExpression analyse(CExpressionContext context) throws PositionedError {
        
        CClass caller =
            context.getClassReader().loadClass(
                context.getTypeFactory(),
                context.getClassContext().getCClass().convertToIfcQn()
            );
        
        transformation = 
            generateOuterAccessExpression(caller, prefixType.getCClass());
        
        check(
            context,
            transformation != null,
            KjcMessages.THIS_BADACCESS);
        
        transformation.analyse(context);
        
        // set family
        int k = getOuterSteps();
        
        // getOuterSteps is relative to the method context - 1
        // however, we could have a call from the method body
        CContext ctx = context.getBlockContext();
        while(ctx.getMethodContext() != null) {
            ctx = ctx.getParentContext();
            k++;
        }
        
        thisAsFamily = new ContextExpression(null, k, (CReferenceType)getType(context.getTypeFactory()));
        family = new ContextExpression(null, k+1, (CReferenceType)getType(context.getTypeFactory()));
        
        
        return this;
    }
    
    public void genCode(GenerationContext context, boolean discardValue) {
        transformation.genCode(context, discardValue);
    }
    
    public CType getType(TypeFactory factory) {
        if(transformation == null) throw new InconsistencyException();
        return transformation.getType(factory);
    }
    
    
    protected JExpression generateOuterAccessExpression(final CClass caller, final CClass target) throws PositionedError {
	    JExpression expr = new JThisExpression(getTokenReference());
	    	
	    CClass clazz = caller; 
	    steps = 0;
	    
	    while(!clazz.descendsFrom(target)) {
	        clazz = clazz.getOwner();
	        
		    if(clazz == null)
		        throw new PositionedError(getTokenReference(), KjcMessages.THIS_BADACCESS); 
	        
		    expr = 
		        new JCastExpression(
		            getTokenReference(),		            
			        new JMethodCallExpression(
			            getTokenReference(),
			            expr,
			            CaesarConstants.OUTER_ACCESS,
			            JExpression.EMPTY),
			        clazz.getAbstractType()
	            );	
		    
		    steps++;
	    }
	    
	    return expr;
	}
    
    public int getOuterSteps() {
        return steps;
    }
}
