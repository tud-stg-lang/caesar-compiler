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
 * $Id: CjAccessorCallExpression.java,v 1.5 2005-02-09 16:56:28 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.family.FieldAccess;
import org.caesarj.compiler.family.Path;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * handles access to public caesar fields
 * 
 * @author Ivica Aracic
 */
public class CjAccessorCallExpression extends JMethodCallExpression {
   
    private String fieldIdent;
    private boolean setter = false;
    
    public CjAccessorCallExpression(
        TokenReference where,
        JExpression prefix,
        String ident
    ) {
        super(where, prefix, "get_"+ident, JExpression.EMPTY);
        fieldIdent = ident;
    }
    
    public CjAccessorCallExpression(
        TokenReference where,
        JExpression prefix,
        JExpression argument,
        String ident
    ) {
        super(where, prefix, "set_"+ident, new JExpression[]{argument});
        fieldIdent = ident;
        setter = true;
    }
    
    public JExpression analyse(CExpressionContext context) throws PositionedError {
        JExpression res = super.analyse(context);
        
        try {
	        // store family here 
	        Path prefixFam = prefix.getThisAsFamily();
	        if(prefixFam != null && type.isCaesarReference()) {
	            thisAsFamily = new FieldAccess(prefixFam.clonePath(), fieldIdent, (CReferenceType)type);
	            family = thisAsFamily.normalize();
	        }
        }
        catch (UnpositionedError e) {
            throw e.addPosition(getTokenReference());
        }
	        
        return res;
    }       
    
    public boolean isSetter() {
        return setter;
    }
    
    public String getFieldIdent() {
        return fieldIdent;
    }

    public void setArgument(JExpression arg) {
        args = new JExpression[]{arg};
    }
    
    
}
