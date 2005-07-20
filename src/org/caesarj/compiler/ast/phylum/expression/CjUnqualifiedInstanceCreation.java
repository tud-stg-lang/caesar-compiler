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
 * $Id: CjUnqualifiedInstanceCreation.java,v 1.11 2005-07-20 10:07:15 gasiunas Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.phylum.expression.literal.JNullLiteral;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CjUnqualifiedInstanceCreation extends JExpression implements CaesarConstants {
    private CReferenceType type;
    private JExpression[] params;

    public CjUnqualifiedInstanceCreation(
        TokenReference where,
        CReferenceType type,
        JExpression[] params
    ) {
        super(where);

        this.type = type;
        this.params = params;
    }

    public CType getType(TypeFactory factory) {
        throw new InconsistencyException();
    }
    
    public JExpression analyse(CExpressionContext context) throws PositionedError {        
        JExpression expr; 
        
        TypeFactory factory = context.getTypeFactory();

        try {
            type = (CReferenceType)type.checkType(context);            
        }
        catch (UnpositionedError e) {
            throw e.addPosition(getTokenReference());
        }
        
        CClass typeClass = type.getCClass();
        
        // IVICA
        // if we have a caesar class, we have to replace the interface with the impl class
        // and add null parameter to the constructor
        // new C() -> (C)(new C_Impl(null)) if not a nested class (owner == null)
        // new C() -> this.$newC() if a nested class (owner != null)
        if (typeClass.isMixinInterface() || typeClass.isMixin()) {
        	
        	JExpression createExpr;
                        
            if( type.getCClass().isNested() ) {
            	createExpr = new CjMethodCallExpression(
                    getTokenReference(),
                    null,
                    CaesarConstants.FACTORY_METHOD_PREFIX+type.getCClass().getIdent(),
					JExpression.EMPTY
                ).analyse(context);
            }
            else {      
                String typeName = type.getCClass().convertToImplQn();
                CReferenceType newType = new CClassNameType(typeName);
                
	            expr = new JUnqualifiedInstanceCreation(getTokenReference(), newType, new JExpression[]{new JNullLiteral(getTokenReference())});
	            createExpr = new CjCastExpression(getTokenReference(), expr, type).analyse(context);	            
            }
            expr = new CjMethodCallExpression(getTokenReference(), createExpr, CONSTR_METH_NAME, this.params);
            expr = new JCastExpression(getTokenReference(), expr, type);
            expr = new CjFamilyCastExpression(getTokenReference(), expr, createExpr.getFamily().clonePath(), createExpr.getThisAsFamily().clonePath());
        }
        else {
            expr = new JUnqualifiedInstanceCreation(getTokenReference(), type, params);
        }
        
        return expr.analyse(context);
    }
    
    public boolean isStatementExpression() {
        return true;
    }
    
    public void genCode(GenerationContext context, boolean discardValue) {
        throw new InconsistencyException();
    }
    
    public void recurse(IVisitor s) {
        for (int i = 0; i < params.length; i++) {
            params[i].accept(s);
        }
    }
}