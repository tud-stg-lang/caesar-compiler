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
 * $Id: CDependentType.java,v 1.24 2005-07-25 12:46:33 gasiunas Exp $
 */

package org.caesarj.compiler.types;

import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.CContextUtil;
import org.caesarj.compiler.context.CMethodContext;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.family.ContextExpression;
import org.caesarj.compiler.family.Path;
import org.caesarj.util.UnpositionedError;


/**
 * Dependent Types, e.g.: 
 * final ctx(1).G g; ctx(0).g.N n; 
 * 
 * @author Ivica Aracic
 */
public class CDependentType extends CReferenceType {   
    
    private CType plainType;    /** static type of the */
    
    private JExpression family; /** family expression */
    
    private Path path = null;
    
    private boolean checked = false;
    
    private CContext exprContext; /** the context in which the expression has been resolved */
    
    public CDependentType(CContext ctx, CContext exprContext, JExpression family, CType staticType) {
        setDeclContext(ctx);      
        this.family = family;
        this.plainType = staticType;
        this.exprContext = exprContext;
    }
    
    public CDependentType(CContext ctx, JExpression family, CType staticType, boolean adaptK) {
    }
    
    /**
     * @return the family path of the family expression
     * NOTE: returned path should be cloned if manipulated
     */
    public Path getPath() throws UnpositionedError {
        
        if(family == null)
            return ((CReferenceType)plainType).getPath();
        
        // check if we have calculated path already
        if(path != null) 
            return path;                 
        
        // get path from the family expression
        path = family.getThisAsFamily().clonePath();
        
        // if this type has been resolve in the context of a caesar accessor method
        // subtract 1 from k 
        // (we want ot start our path relative to the field decl rather than the context of the accessor method)
        CMethodContext methodCtx = declContext.getMethodContext();
        if(methodCtx != null) {            
            if(methodCtx.getMethodDeclaration().getMethod().isCaesarAccessorMethod())
                ((ContextExpression)path.getHead()).adaptK(-1);
        }
        
        // if the family expression has not been evaluated in the declaring context
        // -> adapt k (e.g., field declarations which are evaluate in the Block$ ctx)
        int adaptK = CContextUtil.getRelativeDepth(exprContext.getBodyContext(), declContext);
        
        if(adaptK > 0)
            ((ContextExpression)path.getHead()).adaptK(-adaptK);

        return path;
    }

    public CClass getCClass() {
        return plainType.getCClass();
    }
    
    public CType checkType(CTypeContext context) throws UnpositionedError {
                
        if(checked) return this;
        
        if(!plainType.getCClass().isMixinInterface()) {
            throw new UnpositionedError(CaesarMessages.PLAINTYPE_WITH_PATH);
        }                               
        
        if(family != null) {
            if(family.getThisAsFamily() == null || !family.getThisAsFamily().isFinal())
            throw new UnpositionedError(CaesarMessages.ILLEGAL_PATH);
        }
        
        // it is forbidden for type paths to contain java elements
        /* CRITICAL: check removed, did not think about it but I guess this check doesn't make sense
        if(dt.getPath().containsJavaElements()) {
            throw new UnpositionedError(CaesarMessages.INNER_PLAIN_JAVA_OBJECTS_IN_PATH);
        }
        */
        
        checked = true;
        
        return this;
    }
    
    /**
     * simply check the plain type here
     * family checks are done in a separate step after analyse has been executed
     */
    public boolean isAssignableTo(CTypeContext context, CType dest) {
        // only a dependent type is assignable to another dependet type
        if(dest.isDependentType()) {
            CDependentType other = (CDependentType)dest;
            
            // check if plain types are subtypes
            return plainType.isAssignableTo(context, other.plainType);
        }
        else if(dest.isReference()) {
            // this case handles dependent type without an explict path
            return plainType.isAssignableTo(context, dest);
        }        
        
        return false;
    }
    

    public boolean isDependentType() {
        return true;
    }
    
    public boolean isChecked() {
        return plainType.isChecked();
    }

    // always castable for now
    public boolean isCastableTo(CType dest) {
        return true;
    }
    
    public JExpression getFamily() {
        return family;
    }
    
    public CType getPlainType() {
        return plainType;
    }
    
    public int getK() throws UnpositionedError {
        return ((ContextExpression)getPath().getHead()).getK();
    }
    
    public String toString() {
        StringBuffer res = new StringBuffer();
                
        try {
            res.append(getPath());
        }
        catch (Throwable e) {
            res.append("<PATH?>");
        }
        res.append(".");
        res.append(plainType);
        
        return res.toString();        
    }
}
