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
 * $Id: AsmBuilder.java,v 1.2 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.compiler.asm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.StructureModel;
import org.aspectj.asm.StructureNode;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.SourceLocation;
import org.caesarj.classfile.ClassfileConstants2;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.CjAdviceMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjDeploymentSupportClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjMixinInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjPointcutDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjVirtualClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.ast.visitor.VisitorSupport;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.util.TokenReference;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class AsmBuilder implements IVisitor {
    
    protected static class Context {
        public Context(
            Context parent,
            JCompilationUnit compilationUnit,
            JTypeDeclaration classDecl,
            StructureNode asmElement
        ) {
            if(parent != null) ident = parent.ident + 2;
            this.parent = parent;
            this.compilationUnit = compilationUnit;
            this.typeDecl = classDecl;
            this.asmElement = asmElement;
        }

        public int ident = 0;
        public Context parent;
        public JCompilationUnit compilationUnit;
        public JTypeDeclaration typeDecl;
        public StructureNode asmElement;
    }
    
    private Stack s = new Stack(); //of Context      
    
    protected StructureModel model;
    
    public StructureModel getStructureModel() {
        return model;
    }
    
    protected Context popContext() {
        return (Context)s.pop();
    }
    
    protected Context getContext() {
        return (Context)s.peek();
    }
    
    protected void pushContext(Context context) {
        s.push(context);
    }
    
    protected void pushContext(JTypeDeclaration decl, StructureNode node) {
        Context ctx = getContext();
        pushContext(new Context(ctx, ctx.compilationUnit, decl, node));
    }

    protected void pushContext(JCompilationUnit cu, StructureNode node) {
        Context ctx = getContext();
        pushContext(new Context(ctx, cu, null, node));
    }
    
    public void printIndent() {
        for(int i=0; i<getContext().ident; i++)
            System.out.print("\t");
    }
    
    
    public AsmBuilder(StructureModel model) {
		String rootLabel = "<root>";
		model.setRoot(
			new ProgramElementNode(rootLabel, ProgramElementNode.Kind.FILE_JAVA, new ArrayList()));

		model.setFileMap(new HashMap());

		this.model = model; 
		
        pushContext( new Context(null, null, null, model.getRoot()) );
    }
    
    private VisitorSupport visitor = new VisitorSupport(this);
    
    public boolean start(JPhylum node) {
        return visitor.start(node);
    }
    
    public void end() {
        visitor.end();
    }      
    
    public boolean visit(JCompilationUnit self) {
        
        TokenReference ref = self.getTokenReference();
		File file = new File(new String(ref.getFile()));

		ProgramElementNode cuNode =
			new ProgramElementNode(
				new String(file.getName()),
				ProgramElementNode.Kind.FILE_JAVA,
				makeLocation(ref),
				0,
				"", //$NON-NLS-1$
				new ArrayList()/*,
				self.getImportedPackages(),
				self.getImportedClasses()*/);

		if (self.getPackageName() != null) {

			String pkgName = self.getPackageName().getName();

			pkgName = pkgName.replaceAll("/", "."); //$NON-NLS-1$ //$NON-NLS-2$

			ProgramElementNode pkgNode = null;

			for (Iterator it = getStructureModel().getRoot().getChildren().iterator();
				it.hasNext();
				) {
			    ProgramElementNode currNode = (ProgramElementNode) it.next();
				if (currNode.getName().equals(pkgName))
					pkgNode = currNode;
			}

			if (pkgNode == null) {
				pkgNode =
					new ProgramElementNode(
						pkgName,
						ProgramElementNode.Kind.PACKAGE,
						new ArrayList());
				getStructureModel().getRoot().addChild(pkgNode);
			}

			for (Iterator itt = pkgNode.getChildren().iterator(); itt.hasNext();) {
			    ProgramElementNode child = (ProgramElementNode) itt.next();
				if (child.getSourceLocation().getSourceFile().equals(file)) {
					pkgNode.removeChild(child);
				}
			}
			/*if (duplicate != null) {pkgNode.removeChild(duplicate);}*/
			pkgNode.addChild(cuNode);
		} else {
			// if the node already exists remove before adding

			for (Iterator itt = getStructureModel().getRoot().getChildren().iterator();
				itt.hasNext();
				) {
				ProgramElementNode child = (ProgramElementNode) itt.next();
				if (child.getSourceLocation().getSourceFile().equals(file)) {
					getStructureModel().getRoot().removeChild(child);
				}
			}
			
			getContext().asmElement.addChild(cuNode);
		}

		try {
			//StructureModelManager.INSTANCE.getStructureModel().getFileMap().put(
			getStructureModel().addToFileMap(file.getCanonicalPath(), cuNode);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}       
        
        pushContext(self, cuNode);
        return true;
    }
    
    public void endVisit(JCompilationUnit cu) {
        popContext();
    }
        
    
    // plain Java class
    public boolean visit(JClassDeclaration self) {
        printIndent();
        System.out.println("[plainClass] "+self.getIdent());
        
        ProgramElementNode peNode =
			new ProgramElementNode(
				self.getIdent(),
				ProgramElementNode.Kind.CLASS,
				makeLocation(self.getTokenReference()),
				self.getModifiers(),
				"", // formalComment
				new ArrayList());
        
        getContext().asmElement.addChild(peNode);
        
        pushContext(self, peNode);
        
        return true;
    }
    
    public void endVisit(JClassDeclaration self) {
        popContext();
    }
    
    // plain ifc
    public boolean visit(JInterfaceDeclaration self) {
        printIndent();
        System.out.println("[plainIfc] "+self.getIdent());
        
        
        ProgramElementNode peNode =
			new ProgramElementNode(
				self.getIdent(),
				ProgramElementNode.Kind.CLASS,
				makeLocation(self.getTokenReference()),
				self.getModifiers(),
				"", // formalComment
				new ArrayList());
        
        getContext().asmElement.addChild(peNode);
        
        
        pushContext(self, peNode);
        
        
        return true;
    }
    
    public void endVisit(JInterfaceDeclaration self) {
        popContext();
    }

    
    // virtual class impl
    public boolean visit(CjVirtualClassDeclaration self) {
        printIndent();
        System.out.println("[virtualClass] "+self.getIdent());               
        
        ProgramElementNode peNode =
			new ProgramElementNode(
				self.getIdent(),
				ProgramElementNode.Kind.CLASS,
				makeLocation(self.getTokenReference()),
				self.getModifiers(),
				"", // formalComment
				new ArrayList());
        
        getContext().asmElement.addChild(peNode);
        
        pushContext(self, peNode);
        
        return true;
    }
    
    public void endVisit(CjVirtualClassDeclaration self) {
        popContext();
    }

    // dont't visit mixin interface
    public boolean visit(CjMixinInterfaceDeclaration self) {
        return false;
    }
    
    // dont't visit support ifcs
    public boolean visit(CjInterfaceDeclaration self) {
        return false;
    }
    
    // don't visit registry class    
    public boolean visit(CjDeploymentSupportClassDeclaration self) {        
        return false;
    }   
    
    /**
     * METHOD
     */
    public boolean visit(JMethodDeclaration self) {
        printIndent();
        System.out.println("[method] "+self.getIdent());
        
        ProgramElementNode peNode =
			new ProgramElementNode(
				self.getIdent(),
				ProgramElementNode.Kind.METHOD,
				makeLocation(self.getTokenReference()),
				self.getModifiers(),
				"", //formalComment
				new ArrayList());

		peNode.setBytecodeName(self.getIdent());
		peNode.setBytecodeSignature(self.getMethod().getSignature());
		
		getContext().asmElement.addChild(peNode);
        
        return false;
    }

    /**
     * ADVICE
     */
    public boolean visit(CjAdviceMethodDeclaration self) {
        printIndent();
        System.out.println("[advice] "+self.getIdent());
        
        ProgramElementNode peNode =
			new ProgramElementNode(
				self.getIdent(),
				ProgramElementNode.Kind.ADVICE,
				makeLocation(self.getTokenReference()),
				self.getModifiers(),
				"", //formalComment
				new ArrayList());
        
		peNode.setBytecodeName(self.getIdent());
		peNode.setBytecodeSignature(self.getMethod().getSignature());
        
        JTypeDeclaration ownerType = getContext().typeDecl;
        
        if (CModifier.contains(ownerType.getModifiers(), ClassfileConstants2.ACC_DEPLOYED)) {
            getContext().asmElement.addChild(peNode);
		} 
        else {
            final String REGISTRY_CLASS_NAME = "Registry";
            
			ProgramElementNode registryNode =
				findChildByName(getContext().asmElement.getChildren(), REGISTRY_CLASS_NAME);

			if (registryNode == null) {
				registryNode =
					new ProgramElementNode(
					    REGISTRY_CLASS_NAME,
						ProgramElementNode.Kind.CLASS,
						makeLocation(self.getTokenReference()),
						self.getModifiers(),
						"",
						new ArrayList());

				getContext().asmElement.addChild(registryNode);
			}

			registryNode.addChild(peNode);
		}
        
        
        
        return false;
    }

    /**
     * POINTCUT
     */
    public boolean visit(CjPointcutDeclaration self) {
        printIndent();
        System.out.println("[pointcut] "+self.getIdent());
        
        ProgramElementNode peNode =
			new ProgramElementNode(
				self.getIdent(),
				ProgramElementNode.Kind.POINTCUT,
				makeLocation(self.getTokenReference()),
				self.getModifiers(),
				"", //formalComment
				new ArrayList());	
        
		peNode.setBytecodeName(self.getIdent());
		peNode.setBytecodeSignature("");
        
        getContext().asmElement.addChild(peNode);
        
        return false;
    }
    
    
    /*
     * HELPER 
     */
    
	private ISourceLocation makeLocation(TokenReference ref) {
		String fileName = new String(ref.getFile());

		return new SourceLocation(new File(fileName), ref.getLine());
	}

	private ProgramElementNode findChildByName(Collection childrenList, String name) {
		for (Iterator it = childrenList.iterator(); it.hasNext();) {
		    ProgramElementNode node = (ProgramElementNode) it.next();

			if (node.getName().equals(name))
				return node;
		}

		return null;
	}

}
