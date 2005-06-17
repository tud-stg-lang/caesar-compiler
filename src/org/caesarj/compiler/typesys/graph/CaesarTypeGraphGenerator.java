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
 * $Id: CaesarTypeGraphGenerator.java,v 1.10 2005-06-17 11:12:22 gasiunas Exp $
 */

package org.caesarj.compiler.typesys.graph;

import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.declaration.CjMixinInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;

/**
 * Traverses AST and generates type graph for all types declared in the source code.
 * 
 * @author Ivica Aracic
 */
public class CaesarTypeGraphGenerator {

    private static CaesarTypeGraphGenerator singleton = new CaesarTypeGraphGenerator();
    
    public static CaesarTypeGraphGenerator instance() {
        return singleton; 
    }
    
    private CaesarTypeGraphGenerator() {
    }

    
    public void generateGraph(
        CaesarTypeGraph g,
        JCompilationUnit cu
    ) {
        JTypeDeclaration inners[] = cu.getInners();
        for (int i = 0; i < inners.length; i++)
            if (inners[i] instanceof CjMixinInterfaceDeclaration)
                generateGraph(g, (CjMixinInterfaceDeclaration)inners[i], null, cu.getPackageName().getName() + "/");
    }    

    private void generateGraph(
        CaesarTypeGraph g,
        CjMixinInterfaceDeclaration decl,
        CaesarTypeNode ownerNode,
		String prefix
    ) {
        CType[] superTypes = decl.getExtendedTypes();
        String qualName = prefix + decl.getIdent();
        
        CaesarTypeNode thisNode  = g.getTypeCreateIfNotExsistent(
            new JavaQualifiedName(qualName)
        );
        
        thisNode.setTypeDecl(decl);
         
        // check inner outer relations
        if (ownerNode != null) {
            new OuterInnerRelation(ownerNode, thisNode);
        }
        else {
            g.getTopClassRoot().add(thisNode);
        }
        
        // check super type relations
        // shift super class references to the context of the thisNode
        // if thisNode is not a top level class
        if (superTypes.length > 0) {
            for (int i = 0; i < superTypes.length; i++) {
            	/* only the types of outer classes are resolved */
            	CaesarTypeNode superNode = g.getTypeCreateIfNotExsistent(
            			(ownerNode == null) ?
            					new JavaQualifiedName(superTypes[i].getCClass().getQualifiedName()) :
            					new JavaQualifiedName(prefix + superTypes[i].toString())
                );
                
                new SuperSubRelation(superNode, thisNode);
            }
        }
        else {
            g.getInheritanceRoot().add(thisNode);
        }

        // recurse into inners (here we can be sure all inners has ACC_CCLASS_INTERFACE)
		JTypeDeclaration inners[] = decl.getInners();        
        for (int i = 0; i < inners.length; i++)
            generateGraph(g, (CjMixinInterfaceDeclaration)inners[i], thisNode, qualName + "$");
	}
}
