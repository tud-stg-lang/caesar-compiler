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
 * $Id: CDependentType.java,v 1.17 2005-02-16 13:24:47 aracic Exp $
 */

package org.caesarj.compiler.types;

import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.context.CContext;
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
    
    public CDependentType(CContext ctx, JExpression family, CType staticType) {
        setDeclContext(ctx);      
        this.family = family;
        this.plainType = staticType;
    }
    
    public Path getPath() throws UnpositionedError {
        // CRITICAL: no caching, this has caused errors, reason is unkown
        //if(path != null) return path.clonePath();  
        
        path = Path.createFrom(declContext, family);
        
        // if this type has been resolve in the context of a caesar accessor method
        // subtract 1 from k 
        // (we want ot start our path relative to the field decl rather than the context of the accessor method)
        if(declContext.getMethodContext() != null) {            
            if(declContext.getMethodContext().getMethodDeclaration().getMethod().isCaesarAccessorMethod())
                ((ContextExpression)path.getHead()).adaptK(-1);
        }

        return path;
    }

    public CClass getCClass() {
        return plainType.getCClass();
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
        else if(dest.isCaesarReference()) {
            // this case handles dependent type without an explict path
            return plainType.isAssignableTo(context, dest);
        }
        else if(
            dest.isClassType() 
            && (dest.getCClass().isObjectClass() || dest.getCClass().isCaesarObjectClass())
        ) {        
        	// assignable to Object
        	return true;        
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
                
        res.append(path);        
        res.append(".");
        res.append(plainType);
        
        return res.toString();        
    }
}
