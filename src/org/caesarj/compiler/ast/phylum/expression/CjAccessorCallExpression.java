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
 * $Id: CjAccessorCallExpression.java,v 1.7 2005-02-12 17:56:59 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.export.CField;
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
   
    protected CField field;
    protected boolean setter = false;
    
    public CjAccessorCallExpression(
        TokenReference where,
        JExpression prefix,
        CField field
    ) {
        super(where, prefix, CaesarConstants.GETTER_PREFIX+field.getIdent(), JExpression.EMPTY);
        this.field = field;
    }
    
    public CjAccessorCallExpression(
        TokenReference where,
        JExpression prefix,
        JExpression argument,
        CField field
    ) {
        super(where, prefix, CaesarConstants.SETTER_PREFIX+field.getIdent(), new JExpression[]{argument});
        this.field = field;
        setter = true;
    }
    
    public JExpression analyse(CExpressionContext context) throws PositionedError {
        JExpression res = super.analyse(context);
        
        try {
	        // store family here 
	        Path prefixFam = prefix.getThisAsFamily();
	        if(prefixFam != null && type.isCaesarReference() && field.isFinal()) {
	            thisAsFamily = new FieldAccess(prefixFam.clonePath(), field.getIdent(), (CReferenceType)type);
	            family = thisAsFamily.normalize();
	        }
        }
        catch (UnpositionedError e) {
            throw e.addPosition(getTokenReference());
        }
	        
        return res;
    } 
    
    /**
     * handle this method call as a regular field access
     */
    protected void calcExpressionFamily() throws PositionedError {
        try {
            Path prefixFam = prefix.getThisAsFamily();
            if(prefixFam != null && type.isReference()) {
                Path p = new FieldAccess(prefixFam.clonePath(), getFieldIdent(), (CReferenceType)type);
                family = p.normalize();
                
                if(field.isFinal()) {
                    thisAsFamily = p;
                }
            }
        }
        catch (UnpositionedError e) {
            throw e.addPosition(getTokenReference());
        }
    }
    
    public boolean isSetter() {
        return setter;
    }
    
    public String getFieldIdent() {
        return field.getIdent();
    }

    public void setArgument(JExpression arg) {
        args = new JExpression[]{arg};
    }
    
    
}
