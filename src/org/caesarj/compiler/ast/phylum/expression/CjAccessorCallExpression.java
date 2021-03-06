/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
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
 * $Id: CjAccessorCallExpression.java,v 1.10 2005-07-28 11:44:07 gasiunas Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.cclass.CastUtils;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.export.CField;
import org.caesarj.compiler.family.FieldAccess;
import org.caesarj.compiler.family.Path;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
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
    	 
    	 // cast the return type
         CType returnType = getType(context.getTypeFactory());
    	 
    	 CType castType = 
	          CastUtils.instance().castFrom(
	              context, returnType, getPrefixType().getCClass());
    	  
	     if(castType != null) {
	         return new CjCastExpression(
	            getTokenReference(),
	            res,
	            castType
	         );
	     }
         return res;
    } 
    
    /**
     * handle this method call as a regular field access
     */
    protected void calcExpressionFamily() throws PositionedError {
        try {
	        // store family here 
	        Path prefixFam = prefix.getThisAsFamily();
	        thisAsFamily = new FieldAccess(field.isFinal(), prefixFam.clonePath(), field.getIdent(), (CReferenceType)type);
	        family = thisAsFamily.normalize();	        
        }
        catch (UnpositionedError e) {
            throw e.addPosition(getTokenReference());
        }
    }
    
    public boolean isSetter() {
        return setter;
    }
    
    public CField getField() {
        return field;
    }
    
    public String getFieldIdent() {
        return field.getIdent();
    }

    public void setArgument(JExpression arg) {
        args = new JExpression[]{arg};
    }
    
    
}
