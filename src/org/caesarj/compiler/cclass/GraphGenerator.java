/*
 * Created on 08.02.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.caesarj.compiler.cclass;

import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.declaration.CjClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.export.CBinaryClass;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CCompositeType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.InconsistencyException;

/**
 * Generated source graph.
 * 
 * @author Ivica Aracic
 */
public class GraphGenerator implements CaesarConstants {

    private static GraphGenerator singleton = new GraphGenerator();
    
    public static GraphGenerator instance() {
        return singleton; 
    }
    
    private GraphGenerator() {
    }

    
    public void generateGraph(
        TypeGraph g,
        JCompilationUnit cu
    ) {
        JTypeDeclaration inners[] = cu.getInners();
        for(int i=0; i<inners.length; i++)
            if((inners[i].getModifiers() & ACC_CCLASS_INTERFACE) != 0)
                generateGraph(g, (CjInterfaceDeclaration)inners[i]);
    }
    

    private void generateGraph(
        TypeGraph g,
        CjInterfaceDeclaration decl
    ) {
        CClass thisClass   = decl.getCClass();
        CType[] superTypes = decl.getInterfaces();
        CClass ownerClass  = thisClass.getOwner();
        
        CaesarType thisNode  = g.getType(thisClass.getQualifiedName());
                
        if(superTypes.length > 0) {
            for(int i=0; i<superTypes.length; i++) {
                CaesarType superNode = g.getType( superTypes[i].getCClass().getQualifiedName() );
                superNode.addSubType(thisNode);
            }
        }
        else {
            g.getInheritanceRoot().add(thisNode);
        }

        if(ownerClass != null) {
            CaesarType ownerNode = g.getType(ownerClass.getQualifiedName());
            ownerNode.addInner(thisNode);
        }
        else {
            g.getTopClassRoot().add(thisNode);
        }

        // recurse into inners (here we can be sure all inners has ACC_CCLASS_INTERFACE)
		JTypeDeclaration inners[] = decl.getInners();        
        for(int i=0; i<inners.length; i++)
            generateGraph(g, (CjInterfaceDeclaration)inners[i]);
	}

}
