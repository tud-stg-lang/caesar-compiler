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
 * $Id: CaesarDeclareScope.java,v 1.1 2005-04-05 16:53:12 gasiunas Exp $
 */

package org.caesarj.compiler.aspectj;

import java.util.Iterator;

import org.aspectj.weaver.IHasPosition;
import org.aspectj.weaver.ResolvedTypeX;
import org.aspectj.weaver.TypeX;
import org.aspectj.weaver.patterns.WildTypePattern;
import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.context.FjClassContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.compiler.typesys.graph.CaesarTypeGraph;
import org.caesarj.compiler.typesys.graph.CaesarTypeNode;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;

/**
 * Provides access to the ClassContext.
 * Important for pointcut checking.
 * 
 * @author Jürgen Hallpap
 */
public class CaesarDeclareScope extends CaesarScope {
	
	CaesarTypeGraph typeGraph;
	TypeFactory typeFactory;
	ClassReader classReader;
	
	public CaesarDeclareScope(FjClassContext context, CClass caller) {
		super(context, caller);
		typeGraph = context.getEnvironment().getCaesarTypeSystem().getCaesarTypeGraph();
		typeFactory = context.getTypeFactory();
		classReader = context.getClassReader();
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
			WildTypePattern pattern = (WildTypePattern)location;
			includeSubtypes = pattern.isIncludeSubtypes();
		}
				
		//If the lookup retrieves a crosscutting class, then its
		//aspect registry should be returned instead.
		// Hence do a lookup for the registry.
		if (cclass != null && !includeSubtypes) {
			CClass aspectType = findParentWithRegistry(cclass.getQualifiedName());
			if (aspectType != null) {
				cclass = aspectType;
			}
		}

		if (cclass == null) {
			return ResolvedTypeX.MISSING;
		} 
		else {
			return world.resolve(cclass);
		}
	}
	
	/**
	 * try to find parent class, which has registry
	 */
	private CClass findParentWithRegistry(String qualifiedName) {
		CaesarTypeNode node = typeGraph.getType(new JavaQualifiedName(qualifiedName));
		
		if (node != null) {
	        for (Iterator it = node.getMixinList().iterator(); it.hasNext();) {
	            CaesarTypeNode item = (CaesarTypeNode) it.next();
	            if (item.needsAspectRegistry()) {
	            	String regName = item.getQualifiedName().toString();
	            	return classReader.loadClass(typeFactory, regName);
	            }
	        }
		}
        return null;
	}
}
