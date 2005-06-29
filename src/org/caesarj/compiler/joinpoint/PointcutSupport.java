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

package org.caesarj.compiler.joinpoint;

import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.declaration.CjVirtualClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.context.CContext;

/**
 * Provides support methods for pointcut declarations.
 *
 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
 *
 */
public class PointcutSupport {

    /**
     * Iterate the type declarations from the compilation unit making them declare its
     * pointcuts to its cclass.
     * 
     * @param compiler
     * @param cu
     */
    public static void preparePointcutDeclarations(CompilerBase compiler, JCompilationUnit cu) {
        JTypeDeclaration typeDeclarations[] = cu.getInners();
		CContext ownerCtx = cu.createContext(compiler);
		
		for (int i = 0; i < typeDeclarations.length; i++) {
			if (typeDeclarations[i] instanceof CjVirtualClassDeclaration) {

				CjVirtualClassDeclaration caesarClass =
					(CjVirtualClassDeclaration) typeDeclarations[i];
				
				if (caesarClass.getInners().length > 0) {
					//consider nested types
				    PointcutSupport.preparePointcutDeclarations(caesarClass);
				}
				
				// declare the pointcuts to cclass
				caesarClass.declarePointcuts();
			}
		}
    }
    
    /**
     * Iterate the type declarations on the virtual class, making it declare its
     * pointcuts to its cclass.
     * 
     * @param cd
     */
    public static void preparePointcutDeclarations(CjVirtualClassDeclaration cd) {
        JTypeDeclaration typeDeclarations[] = cd.getInners();
		
		for (int i = 0; i < typeDeclarations.length; i++) {
		    if (typeDeclarations[i] instanceof CjVirtualClassDeclaration) {
			    CjVirtualClassDeclaration caesarClass =
					(CjVirtualClassDeclaration) typeDeclarations[i];
				
				// declare the pointcuts to cclass
				caesarClass.declarePointcuts();
		    }
		}
    }
}
