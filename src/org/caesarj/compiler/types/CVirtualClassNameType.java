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
 * $Id: CVirtualClassNameType.java,v 1.4 2005-06-10 12:21:02 klose Exp $
 */

package org.caesarj.compiler.types;

import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.util.UnpositionedError;

/**
 * this represents the type in an extends clause of a cclass
 */
public class CVirtualClassNameType extends CClassNameType {

	public CVirtualClassNameType(String qualifiedName) {
		super(qualifiedName);
	}


	public CType checkType(CTypeContext context) throws UnpositionedError {
	    CClassContext classContext = context.getClassContext(); 
	    if(classContext != null && classContext.getCClass().isNested()) {
	        
	        if (qualifiedName.indexOf('/') >= 0)
	            throw new UnpositionedError(CaesarMessages.CCLASS_EXTENDS_ONLY_IDENTIFIER_ALLOWED);
	        
	        // only lookup in the direct enclosing class
	        CClass owner = context.getClassContext().getCClass().getOwner();
	        
	        for (int i = 0; i < owner.getInnerClasses().length; i++) {
                CClass inner = owner.getInnerClasses()[i].getCClass();
                
                if(inner.getIdent().equals(qualifiedName)) {
                    return inner.getAbstractType();
                }
            }
	        	        
	        throw new UnpositionedError(CaesarMessages.CCLASS_SUPER_NOT_FOUND, this);
	    }
	    else {
	        return super.checkType(context);
	    }
	}
}
