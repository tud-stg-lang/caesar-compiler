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
 * $Id: CastUtils.java,v 1.5 2005-06-10 12:21:02 klose Exp $
 */

package org.caesarj.compiler.cclass;

import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CArrayType;
import org.caesarj.compiler.types.CType;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CastUtils {

    private static CastUtils singleton = new CastUtils();

    public static CastUtils instance() {
        return singleton;
    }

    private CastUtils() {        
    }
    
    public CType castFrom(CContext context, CType t, CClass contextClass) {
        
        CType res = null;
        //CArrayType arrayType = t.isArrayType() ? (CArrayType)t : null;        
        //CType type = arrayType != null ? arrayType.getBaseType() : t;
        CType type = t.isArrayType()? ((CArrayType)t).getBaseType() : t;
        
        if(type.isClassType()) {
		    CClass tClass = type.getCClass();
		        
		    if(tClass.isMixinInterface() && contextClass.isMixin()) {
	            String tNewClassQn = 
	                context.getEnvironment().getCaesarTypeSystem().
	                	findInContextOf(
	                	    tClass.getQualifiedName(),
		                    contextClass.convertToIfcQn()
		                );
	            
	            if(tNewClassQn != null) {
		            CClass newPrefixClass = 
		                context.getClassReader().loadClass(
		                    context.getTypeFactory(),
		                    tNewClassQn
		                );
		            
		            CType newT = newPrefixClass.getAbstractType();          
		            
		            res = newT;
	            }	        
	        }  
        }
        
        /*
        if(arrayType != null) {
            res = new CArrayType(res, arrayType.getArrayBound());
        }
        */
        
        // cast not necessary
        if(res != null && res.equals(t))
            res = null;
        
        return res;
    }
}
