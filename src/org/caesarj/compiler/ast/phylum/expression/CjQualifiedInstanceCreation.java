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
 * $Id: CjQualifiedInstanceCreation.java,v 1.7 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CjQualifiedInstanceCreation extends JExpression {
    private JExpression prefix;
    private String ident;
    private JExpression[] params;

    public CjQualifiedInstanceCreation(
        TokenReference where,
        JExpression prefix,
        String ident,
        JExpression[] params
    ) {
        super(where);

        this.prefix = prefix;
        this.ident = ident;
        this.params = params;
    }

    public CType getType(TypeFactory factory) {
        throw new InconsistencyException();
    }
    
    public JExpression analyse(CExpressionContext context) throws PositionedError {        
        JExpression expr;
        
        TypeFactory factory = context.getTypeFactory();       

        prefix = prefix.analyse(context);
        
        check(context,
    	  prefix.getType(factory).isClassType(),
    	  KjcMessages.FIELD_BADACCESS, prefix.getType(factory));

        CClass	newClass;
        CType prefixType = prefix.getType(factory);
        CClass prefixClass = prefixType.getCClass();
        
        if((prefixClass.isMixinInterface() || prefixClass.isMixin()) && params.length == 0) {
            
            if(prefixClass.isMixin()) {
                prefixType = prefixClass.getInterfaces()[0];
                prefixClass = prefixType.getCClass();
            }
            
            CClass returnClass = context.getClassReader().loadClass(
                factory,
                prefixClass.getQualifiedName()+'$'+ident
            );
            
            // convert to factory method
            // a.new C() -> a.$newC()
            expr = new CjMethodCallExpression(getTokenReference(), prefix, "$new"+ident, params);
            //expr = new JCastExpression(getTokenReference(), expr, returnClass.getAbstractType());
        }
        else {
            // create normal qualified instance creation
            expr = new JQualifiedInstanceCreation(getTokenReference(), prefix, ident, params);
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
        prefix.accept(s);
        for (int i = 0; i < params.length; i++) {
            params[i].accept(s);
        }
    }
}