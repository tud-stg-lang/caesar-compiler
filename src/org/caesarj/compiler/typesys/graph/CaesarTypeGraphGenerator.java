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
 * $Id: CaesarTypeGraphGenerator.java,v 1.9 2005-03-01 15:38:42 gasiunas Exp $
 */

package org.caesarj.compiler.typesys.graph;

import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.declaration.CjMixinInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.export.CClass;
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
        for(int i=0; i<inners.length; i++)
            if(inners[i].getCClass().isMixinInterface())
                generateGraph(g, (CjMixinInterfaceDeclaration)inners[i]);
    }
    

    private void generateGraph(
        CaesarTypeGraph g,
        CjMixinInterfaceDeclaration decl
    ) {
        CClass thisClass   = decl.getCClass();
        CType[] superTypes = decl.getInterfaces();
        CClass ownerClass  = thisClass.getOwner();
        
        CaesarTypeNode thisNode  = g.getTypeCreateIfNotExsistent(
            new JavaQualifiedName(thisClass.getQualifiedName()), 
			CaesarTypeNode.DECLARED
        );
        
        thisNode.setTypeDecl(decl);
         
        // check inner outer relations
        if(ownerClass != null) {
            CaesarTypeNode ownerNode = g.getTypeCreateIfNotExsistent(
                new JavaQualifiedName(ownerClass.getQualifiedName()),
				CaesarTypeNode.DECLARED
            );
            new OuterInnerRelation(ownerNode, thisNode);
        }
        else {
            g.getTopClassRoot().add(thisNode);
        }
        
        // check super type relations
        // shift super class references to the context of the thisNode
        // if thisNode is not a top level class
        if(superTypes.length > 0) {
            for(int i=0; i<superTypes.length; i++) {
                if(superTypes[i].getCClass().isMixinInterface()) {
                    
                    /*
                    // super class can only be from the direct enclosing parent
                    JavaQualifiedName superQn = null;

                    if(!thisNode.isTopLevelClass()) {
	                    superQn = new JavaQualifiedName(
	                        thisNode.getQualifiedName().getPrefix()
	                        +superTypes[i].getCClass().getIdent()
	                    );
                	}
                    else {
                        superQn = new JavaQualifiedName(	                        
                            superTypes[i].getCClass().getQualifiedName()
	                    ); 
                    }
                    
                    CaesarTypeNode superNode = g.getTypeCreateIfNotExsistent(
                        superQn, 
						superQn.toString().equals(superTypes[i].getCClass().getQualifiedName()) ?                        
						    CaesarTypeNode.DECLARED : CaesarTypeNode.IMPLICIT
                    );
                    */
                    
                    CaesarTypeNode superNode = g.getTypeCreateIfNotExsistent(
                        new JavaQualifiedName(superTypes[i].getCClass().getQualifiedName()),                         
					    CaesarTypeNode.DECLARED
                    );
                    
                    new SuperSubRelation(superNode, thisNode);
                }
            }
        }
        else {
            g.getInheritanceRoot().add(thisNode);
        }

        // recurse into inners (here we can be sure all inners has ACC_CCLASS_INTERFACE)
		JTypeDeclaration inners[] = decl.getInners();        
        for(int i=0; i<inners.length; i++)
            generateGraph(g, (CjMixinInterfaceDeclaration)inners[i]);
	}
}
