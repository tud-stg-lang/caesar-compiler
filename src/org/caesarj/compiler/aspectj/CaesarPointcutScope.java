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
 */

package org.caesarj.compiler.aspectj;

import org.aspectj.weaver.IHasPosition;
import org.aspectj.weaver.ResolvedTypeX;
import org.aspectj.weaver.TypeX;
import org.aspectj.weaver.patterns.WildTypePattern;
import org.caesarj.compiler.context.FjClassContext;
import org.caesarj.compiler.export.CCjIfcSourceClass;
import org.caesarj.compiler.export.CClass;

/**
 * 
 * The scope used when resolving Pointcuts.
 *
 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
 *
 */
public class CaesarPointcutScope extends CaesarScope {
	
    /**
     * 
     * @param context
     * @param caller
     */
	public CaesarPointcutScope(FjClassContext context, CClass caller) {
		super(context, caller);
	}
	
	/**
	 * Performs a lookup for the given typeName.
	 * 
	 * @param typeName
	 * @param location
	 * 
	 * @return TypeX
	 */
	public TypeX lookupType(String typeName, IHasPosition location) {
		
		if (context.getTypeFactory().isPrimitive(typeName)) return TypeX.forName(typeName); 

		CClass cclass = lookupClass(typeName);
		
		// hack - extract information if subtypes included
		boolean includeSubtypes = false;
		if (location instanceof WildTypePattern) {
			WildTypePattern pattern = (WildTypePattern) location;
			includeSubtypes = pattern.isIncludeSubtypes();
		}
				
		// If the lookup retrieves a crosscutting class, then its
		// aspect registry should be returned instead.
		// Hence do a lookup for the registry.
		if (cclass != null && !includeSubtypes && cclass instanceof CCjIfcSourceClass) {
		    CClass aspectType = ((CCjIfcSourceClass) cclass).getRegistryClass();
			if (aspectType != null) {
				cclass = aspectType;
			}
		}

		if (cclass == null) {
			return ResolvedTypeX.MISSING;
		} 
		else 
		{
			return world.resolve(cclass);
		}
	}
}
