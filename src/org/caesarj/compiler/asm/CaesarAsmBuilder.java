/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
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
 * $Id: CaesarAsmBuilder.java,v 1.14 2005-10-12 07:58:18 gasiunas Exp $
 */

package org.caesarj.compiler.asm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.aspectj.asm.HierarchyWalker;
import org.aspectj.asm.IHierarchy;
import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.IRelationship;
import org.aspectj.asm.IRelationshipMap;
import org.aspectj.asm.internal.ProgramElement;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.SourceLocation;
import org.aspectj.weaver.patterns.AndPointcut;
import org.aspectj.weaver.patterns.OrPointcut;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.ReferencePointcut;
import org.caesarj.classfile.ClassfileConstants2;
import org.caesarj.compiler.aspectj.CaesarPointcut;
import org.caesarj.compiler.ast.phylum.JClassImport;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.JPackageImport;
import org.caesarj.compiler.ast.phylum.JPackageName;
import org.caesarj.compiler.ast.phylum.declaration.CjAdviceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjAdviceMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjDeploymentSupportClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjInitMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjMixinInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjPointcutDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjVirtualClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JConstructorDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JFieldDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.ast.visitor.VisitorSupport;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.util.TokenReference;

/**
 * TODO [documentation]
 * 
 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
 * @author meffert
 */
public class CaesarAsmBuilder {

	private static final String REGISTRY_CLASS_NAME = "Registry"; //$NON-NLS-1$
	
	protected Stack asmStack = new Stack();
	
	protected CaesarJAsmManager asmManager = null;

	protected VisitorSupport visitor = null;
	
	/**
	 * Prepares the asmManager to build.
	 * 
	 * This method will initialize the IHierarchy with a root node, reset
	 * the maps we can and clear the relationship map.
	 * 
	 * @param asmManager The asmManager to be initialized
	 */
	public static void preBuild(CaesarJAsmManager asmManager) {
	    
		String rootLabel = "<root>"; //$NON-NLS-1$
		
		IHierarchy hierarchy = asmManager.getHierarchy();
		
		// Creates a Java File node as the root
		hierarchy.setRoot(
		        new ProgramElement(
		                rootLabel, 
		                IProgramElement.Kind.PROJECT,
		                new ArrayList()));
		hierarchy.setFileMap(new HashMap());
		
    	asmManager.getRelationshipMap().clear();
	}

	/**
	 * resolve advice and method signatures before weaving
	 */
	public static void preWeave(CaesarJAsmManager asmManager) {

	}

	/**
	 * Finalization method for the asmManager.
	 * 
	 * After building the structure model, the asmManager has its instances
	 * attached to AsmManager from AspectJ (because the weaver uses it as singleton).
	 * Since we want to keep the IHierarchy and IRelationshipMap separated for each
	 * asmManager, we have to clone them.
	 * 
	 * remove support methods and inner classes --> this is an old comment
	 * 
	 * @param asmManager the asmManager that will have its instances deattached
	 */
	public static void postBuild(CaesarJAsmManager asmManager) {
	    asmManager.deattach();
	    
	    // Create the link nodes
	    new NodeLinker(asmManager).process(asmManager.hierarchy.getRoot());
	}

	/**
	 * insert a single compilation unit to model
	 */
	public static void build(JCompilationUnit unit, CaesarJAsmManager asmManager) {
		new CaesarAsmBuilder().internalBuild(unit, asmManager);
	}

	/**
	 * 
	 * @param unit
	 * @param hierarchy
	 */
	private void internalBuild(JCompilationUnit unit, CaesarJAsmManager asmManager) {
		if (unit == null)
			return;

		this.asmManager = asmManager;
		this.asmStack.push(asmManager.getHierarchy().getRoot());

		visitor = new VisitorSupport(this);
		unit.accept(visitor);

		this.asmStack.pop();
	}

	/**
	 * CompilationUnit visit method
	 * 
	 * A compilation unit is a file which contains a package, import references and
	 * classes.
	 */
	public boolean visit(JCompilationUnit self) {
	    
		TokenReference ref = self.getTokenReference();
		File file = new File(new String(ref.getFile()));
		
		// create node for file
		CaesarProgramElement node = new CaesarProgramElement(
				file.getName().replaceAll("/", "."), 
		    	CaesarProgramElement.Kind.FILE_JAVA, 
		    	0, 
		    	makeLocation(ref),
		    	new ArrayList(),
		    	new ArrayList(),
				"",
				"");
		
		// Get the package name object
		JPackageName packageName = self.getPackageName();
		String pkgName = "";

		if (packageName == null) {
			
		    // if the node already exists remove before adding
			Iterator it = asmManager.getHierarchy().getRoot().getChildren().iterator();
			while(it.hasNext()) {
				CaesarProgramElement child = (CaesarProgramElement) it.next();
				if (child.getSourceLocation().getSourceFile().equals(file)) {
				    ((ProgramElement) asmManager.getHierarchy().getRoot()).removeChild(child);
				}
			}
			getCurrentStructureNode().addChild(node);
			
		} else {
		    // The package name is "" if null or the real package name for non-collaborations.
		    // For collaboration, the package name is the collaboration package (must remove the name)
		    String name = packageName.getName();
		    if (name == null || name.trim().length() == 0) {
	            pkgName = "";
		    } else if (packageName.isCollaboration()) {
		           if (name.lastIndexOf('/') == -1) {
		               pkgName = name;
		           } else {
		               pkgName = name.substring(0, name.lastIndexOf('/'));   
		           }
		    } else {
		        pkgName = name.replaceAll("/", ".");
		    }

			CaesarProgramElement fileListNode = null;
			// See if there is already a package node
			fileListNode = findChildByName(asmManager.getHierarchy().getRoot().getChildren(), pkgName);
			if(fileListNode == null) {
				// create new filelist
				fileListNode = new CaesarProgramElement(
						pkgName,
						// Have to use PACKAGE as parent type because of ProgramElementNode.getPackagename()
						// instead of ---> CaesarProgramElementNode.Kind.FILE_LST,
						CaesarProgramElement.Kind.PACKAGE,
						0,
						null,
						new ArrayList(),
						new ArrayList(),
						"",
						"");
				// add file-list to root node
				asmManager.getHierarchy().getRoot().addChild(fileListNode);
			}
			// remove all children of filelist node, which have the same source-file
			Iterator it = fileListNode.getChildren().iterator();
			while(it.hasNext()) {
				CaesarProgramElement child = (CaesarProgramElement) it.next();
				if (child.getSourceLocation().getSourceFile().equals(file)) {
					fileListNode.removeChild(child);
				}
			}
			// add file-node as child for filelist-node
			fileListNode.addChild(node);
		}
		
		// Add package
		CaesarProgramElement pkgNode = new CaesarProgramElement(
				pkgName, 
		    	CaesarProgramElement.Kind.PACKAGE, 
		    	0,
		    	makeLocation(self.getPackageName().getTokenReference()),
		    	new ArrayList(),
		    	new ArrayList(),
				"",
				"");
		node.addChild(pkgNode);
		
		// add imports (package and file imports)
		JPackageImport[] pi = self.getImportedPackages();
		JClassImport[] ci = self.getImportedClasses();
		if(pi.length + ci.length > 0){
			
			ArrayList importElements = new ArrayList();
			
			// add package imports to imports root node
			TokenReference firstRef = null;
			for(int i=0; i < pi.length; i++){
				JPackageImport currentPi = pi[i];
				if(i == 0){
					firstRef = currentPi.getTokenReference();
				}
				CaesarProgramElement currentPiNode = new CaesarProgramElement(
						currentPi.getName().replaceAll("/", "."),
						CaesarProgramElement.Kind.PACKAGE_IMPORT,
						0,
						makeLocation(currentPi.getTokenReference()),
						new ArrayList(),
						new ArrayList(),
						"",
						"");
				importElements.add(currentPiNode);
			}
			
			// add class imports to import node
			for(int j=0; j < ci.length; j++){
				JClassImport currentCi = ci[j];
				if(firstRef == null && j == 0){
					firstRef = currentCi.getTokenReference();
				}
				CaesarProgramElement currentCiNode = new CaesarProgramElement(
						currentCi.getQualifiedName().replaceAll("/", "."),
						CaesarProgramElement.Kind.CLASS_IMPORT,
						0,
						makeLocation(currentCi.getTokenReference()),
						new ArrayList(),
						new ArrayList(),
						"",
						"");
				importElements.add(currentCiNode);
			}
			
			// add imports root node
			CaesarProgramElement imports = new CaesarProgramElement(
					"import declarations",
					CaesarProgramElement.Kind.IMPORTS,
					0,
					makeLocation(firstRef),
					importElements,
					new ArrayList(),
					"",
					"");
			
			node.addChild(imports);
		}
		
		// Check if the package name is actually a cclass (externalized classes)
		if (packageName.isCollaboration() ) {
		    // In this case, get the collaboration and create a node for it
		    CjVirtualClassDeclaration collab = self.getCollaboration();
		    
			// Create the node
		    CaesarProgramElement collabNode = new CaesarProgramElement(
					collab.getIdent().replaceAll("/", "."), 
					CaesarProgramElement.Kind.EXTERNAL_COLLABORATION, 
			    	collab.getModifiers(), 
			    	makeLocation(collab.getTokenReference()),
			    	new ArrayList(),
			    	new ArrayList(),
					"",
					"");
			
		    // Add the collaboration node to the tree and push in the stack
			node.addChild(collabNode);
			this.asmStack.push(collabNode);
			
			// Here we visit all the original inners, which are not normally
			// visited, because they belong to the collaboration cclass.
			JTypeDeclaration[] inners = self.getOriginalInners();
		    for (int i = 0; i < inners.length; i++) {
		        inners[i].accept(visitor);
	        }

		} else {
		    this.asmStack.push(node);
		}
		
		try {
		    asmManager.getHierarchy().addToFileMap(file.getCanonicalPath(), node);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return true;
	}

	public void endVisit(JCompilationUnit self) {
		this.asmStack.pop();
	}

	/*
	 * INTERFACES
	 */
 
	/** handle all interface types equivalent */
	public boolean visit(JInterfaceDeclaration self) {

	    // Create a node for the interface
		CaesarProgramElement node = new CaesarProgramElement(
				self.getIdent().replaceAll("/", "."), 
		    	CaesarProgramElement.Kind.INTERFACE, 
		    	self.getModifiers(), 
		    	makeLocation(self.getTokenReference()),
		    	new ArrayList(),
		    	new ArrayList(),
				"",
				"");
		getCurrentStructureNode().addChild(node);
		this.asmStack.push(node);
		return true;
	}

	public void endVisit(JInterfaceDeclaration self) {
		this.asmStack.pop();
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

	/*
	 * CLASS DECLARATIONS
	 */

	/**
	 * crosscutting and normal classes
	 */
	public boolean visit(JClassDeclaration self) {
	    
	    // Check if the class is an aspect
		CaesarProgramElement.Kind kind = CaesarProgramElement.Kind.CLASS;
		if (CModifier.contains(self.getModifiers(),	ClassfileConstants2.ACC_CROSSCUTTING)){
			kind = CaesarProgramElement.Kind.ASPECT;
		}
		// Create the node
		CaesarProgramElement node = new CaesarProgramElement(
				self.getIdent().replaceAll("/", "."), 
		    	kind, 
		    	self.getModifiers(), 
		    	makeLocation(self.getTokenReference()),
		    	new ArrayList(),
		    	new ArrayList(),
				"",
				"");
		getCurrentStructureNode().addChild(node);
		this.asmStack.push(node);		
		return true;
	}

	public void endVisit(JClassDeclaration self) {
		this.asmStack.pop();
	}
	
	/**
	 * 
	 * Visitor for the VirtualClassDeclaration
	 * 
	 * @param self
	 * @return
	 */
	public boolean visit(CjVirtualClassDeclaration self) {
		
		// do not show implicit classes
		if (self.getSourceClass().isImplicit())
			return false;
	    
		// If class is an Aspect, kind = aspect instead of virtual_class
		CaesarProgramElement.Kind kind = CaesarProgramElement.Kind.VIRTUAL_CLASS;
		if (CModifier.contains(self.getModifiers(),	ClassfileConstants2.ACC_CROSSCUTTING)){
			kind = CaesarProgramElement.Kind.ASPECT;
		}
		//System.out.println(self.toString() + " has " + self.getCjSourceClass().getPackage());
		CaesarProgramElement node = new CaesarProgramElement(
				self.getIdent().replaceAll("/", "."), 
				kind,
		    	self.getModifiers(),
		    	//self.getCjSourceClass().getPackage(),
		    	makeLocation(self.getTokenReference()),
		    	new ArrayList(),
		    	new ArrayList(),
				"",
				"");

		// remove static modifier
		node.getModifiers().remove(CaesarProgramElement.Modifiers.STATIC);
		
		getCurrentStructureNode().addChild(node);
		this.asmStack.push(node);
		return true;
	}

	public void endVisit(CjVirtualClassDeclaration self) {
		if (!self.getSourceClass().isImplicit()) {
			this.asmStack.pop();
		}
	}

	/**
	 * ConstructorDeclaration
	 *  
	 */
	public boolean visit(JConstructorDeclaration self) {
		
		// Do not create nodes for generated constructors
		if (self.isGenerated())
			return false;

	    // Create nodes for the parameters
		List parameters = new ArrayList();
		JFormalParameter[] parameterArray = self.getArgs();
		for(int i=0; i < parameterArray.length; i++){
			CaesarProgramElement parameter = new CaesarProgramElement(
					parameterArray[i].getIdent().replaceAll("/", "."),
					CaesarProgramElement.Kind.PARAMETER,
					0,
					null,
					new ArrayList(),
					new ArrayList(),
					"",
					//parameterArray[i].getType().getSignature()
					parameterArray[i].getType().toString());
			parameters.add(parameter);
		}
		// Create a node for the contructor, including the parameters
		CaesarProgramElement node = new CaesarProgramElement(
				self.getIdent().replaceAll("/", "."), 
		    	CaesarProgramElement.Kind.CONSTRUCTOR, 
		    	self.getModifiers(), 
		    	makeLocation(self.getTokenReference()),
		    	new ArrayList(),
		    	parameters,
				"",
				"");
		node.setBytecodeName(self.getIdent());
		node.setBytecodeSignature(self.getMethod().getSignature());
		getCurrentStructureNode().addChild(node);
		return false;
	}

	/**
	 * MethodDeclaration
	 */
	public boolean visit(JMethodDeclaration self) {

	    // Do not create nodes for generated methods
		if (self.isGenerated())
			return false;
		
		// Create nodes for the parameters
		List parameters = new ArrayList();
		JFormalParameter[] parameterArray = self.getArgs();
		for(int i=0; i < parameterArray.length; i++){
			CaesarProgramElement parameter = new CaesarProgramElement(
					parameterArray[i].getIdent().replaceAll("/", "."),
					CaesarProgramElement.Kind.PARAMETER,
					0,
					null,
					new ArrayList(),
					new ArrayList(),
					"",
					parameterArray[i].getType().toString());
			parameters.add(parameter);
		}
		
		String displayIdent = self.getIdent().replaceAll("/", ".");
		String displayRettype = self.getReturnType().toString(); 
		
		/* show class name for generated contructors */
		if (self instanceof CjInitMethodDeclaration) {
			displayIdent = self.getMethod().getOwner().getIdent();
			displayIdent = displayIdent.replaceAll(CaesarConstants.IMPLEMENTATION_EXTENSION, "");
			displayRettype = displayIdent;
		}
		
		// Create a node for the method, including the parameters
		CaesarProgramElement node = new CaesarProgramElement(
				displayIdent, 
		    	CaesarProgramElement.Kind.METHOD, 
		    	self.getModifiers(), 
		    	makeLocation(self.getTokenReference()),
		    	new ArrayList(),
		    	parameters,
		    	displayRettype,
				"");
		node.setBytecodeName(self.getIdent());
		node.setBytecodeSignature(self.getMethod().getSignature());
		if (self.getIdent().equals("main")) { 
			node.setRunnable(true);
		}
		getCurrentStructureNode().addChild(node);
		return false;
	}

	/**
	 * Pointcut declaration
	 * 
	 * @param self
	 * @return
	 */
	public boolean visit(CjPointcutDeclaration self) {

	    // Create a list of parameter nodes
		List parameters = new ArrayList();
		JFormalParameter[] parameterArray = self.getArgs();
		for(int i=0; i < parameterArray.length; i++){
			CaesarProgramElement parameter = new CaesarProgramElement(
					parameterArray[i].getIdent().replaceAll("/", "."),
					CaesarProgramElement.Kind.PARAMETER,
					0,
					null,
					new ArrayList(),
					new ArrayList(),
					"",
					parameterArray[i].getType().getSignature());
			parameters.add(parameter);
		}
		
		// Create the pointcut node including the parameters
		CaesarProgramElement node = new CaesarProgramElement(
				self.getIdent().replaceAll("/", "."), 
		    	CaesarProgramElement.Kind.POINTCUT, 
		    	self.getModifiers(), 
		    	makeLocation(self.getTokenReference()),
		    	new ArrayList(),
		    	parameters,
				"",
				"");
		getCurrentStructureNode().addChild(node);
		return false;
	}

	/**
	 * Visitor for the advices.
	 * 
	 * Creates only one node with the first copy.
	 * 
	 * @param self
	 * @return
	 */
	public boolean visit(CjAdviceDeclaration self) {
	    
	    // Get the first copy to create the node
	    Iterator i = self.getAdviceCopies(0);
	    
	    // TODO - CHANGE IT	
	    // At least one of the copies contain the export field and so we can iterate over
	    // the copies until we find the copy with the export. This is needed because we
	    // want to get the name of the advice, if it is before, after, etc. It is normally
	    // in the wrappee from the CaesarAdvice.
	    // Below is the code as it should be, when we correct it and make the first copy
	    // always with the export.
	    CjAdviceDeclaration copy = null;
	    while(i.hasNext()) {
	        CjAdviceDeclaration curr = (CjAdviceDeclaration) i.next();
	        try {
	            curr.getMethod();
	            copy = curr;
	            break;
	        } catch (Exception e) {
	        }
	    }
	    if (copy != null) {
	        CaesarProgramElement node = new CaesarProgramElement(
			        copy.getCaesarAdvice().getKind().wrappee().getName(),
			    	CaesarProgramElement.Kind.ADVICE, 
			    	copy.getModifiers(), 
			    	makeLocation(copy.getTokenReference()),
			    	new ArrayList(),
			    	new ArrayList(),
					"",
					"");

			node.setBytecodeName(copy.getIdent());
			node.setBytecodeSignature(copy.getMethod().getSignature());			 
		    getCurrentStructureNode().addChild(node);	        
	    }
	    /*
	     IMPLEMENTATION HOW IT SHOULD BE
	    if(i.hasNext()) {
	        CjAdviceDeclaration copy = (CjAdviceDeclaration) i.next();
	        
	        CaesarProgramElement node = new CaesarProgramElement(
			        copy.getCaesarAdvice().getKind().wrappee().getName(),
			    	CaesarProgramElement.Kind.ADVICE, 
			    	copy.getModifiers(), 
			    	makeLocation(copy.getTokenReference()),
			    	new ArrayList(),
			    	new ArrayList(),
					"",
					"");

			node.setBytecodeName(copy.getIdent());
			node.setBytecodeSignature(copy.getMethod().getSignature());			 
		    getCurrentStructureNode().addChild(node);  
	    }
	    */
		return false;
	}

	/**
	 * FIELD
	 */
	public boolean visit(JFieldDeclaration self) {
		
	    // Don't create nodes for generated fields
		if (self.isGenerated())
			return false;
		
		JVariableDefinition var = self.getVariable();
		
		// Create hte field node
		CaesarProgramElement node = new CaesarProgramElement(
				var.getIdent().replaceAll("/", "."), 
		    	CaesarProgramElement.Kind.FIELD, 
		    	var.getModifiers(), 
		    	makeLocation(self.getTokenReference()),
		    	new ArrayList(),
		    	new ArrayList(),
				"",
				var.getType().toString());
		getCurrentStructureNode().addChild(node);
		return false;
	}
	
	/*
	 * HELPER METHODS
	 */

	/**
	 * 
	 */
	private ISourceLocation makeLocation(TokenReference ref) {
		String fileName = new String(ref.getFile());

		return new SourceLocation(new File(fileName), ref.getLine());
	}

	/**
	 * Return the node that is in the top of the stack,
	 * without removing it.
	 * 
	 * @return
	 */
	private IProgramElement getCurrentStructureNode() {
		return (IProgramElement) this.asmStack.peek();
	}

    /**
     * @param left
     * @param pointcuts	accumulator for named pointcuts
     */
    private void addAllNamed(Pointcut pointcut, List pointcuts) {
        if (pointcut == null) return;
        if (pointcut instanceof ReferencePointcut) {
			ReferencePointcut rp = (ReferencePointcut)pointcut;
			pointcuts.add(rp);
		} else if (pointcut instanceof AndPointcut) {
		    AndPointcut ap = (AndPointcut)pointcut;
		    addAllNamed(ap.getLeft(), pointcuts);
		    addAllNamed(ap.getRight(), pointcuts);
		} else if (pointcut instanceof OrPointcut) {
			OrPointcut op = (OrPointcut)pointcut;
			addAllNamed(op.getLeft(), pointcuts);
		    addAllNamed(op.getRight(), pointcuts);
		} 
    }
    
	/**
	 * Returns the child with the given name or null.
	 * @param childrenList
	 * @param name
	 * @return
	 */
	private CaesarProgramElement findChildByName(Collection childrenList, String name) {
		CaesarProgramElement child = null;
		Iterator it = childrenList.iterator();
		while(it.hasNext() && child == null) {
			CaesarProgramElement currentNode = (CaesarProgramElement) it.next();

			if (currentNode.getName().equals(name))
				child = currentNode;
		}
		return child;
	}

	
	/**
	 * 
	 * TODO - Comments
	 *
	 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
	 *
	 */
	public static class NodeLinker extends HierarchyWalker {
	    
	    private IRelationshipMap map = null;
	    private IHierarchy hierarchy = null;
	    
	    public NodeLinker(CaesarJAsmManager asmManager) {
	        this.map = asmManager.getRelationshipMap();
	        this.hierarchy = asmManager.getHierarchy();
	    }
	    
	    /**
	     * 
	     */
	    public void preProcess(IProgramElement node) {
	    
	        // Get this node's relationship list
	        List relationships = map.get(node.getHandleIdentifier());
	        if (relationships != null) {
	            
	            // Iterate the relationships
	            Iterator j = relationships.iterator();
				while(j.hasNext()) {
				    
				    // Create the relationship node and append it
					IRelationship relationship = (IRelationship) j.next();
					LinkNode relationNode = new LinkNode(relationship);
					node.addChild(relationNode);
					
					// Check the relation type
					int type = LinkNode.LINK_NODE_ADVISED_BY;
					if (relationship.getName().equals("advises")) {
					    type =  LinkNode.LINK_NODE_ADVISES;
					}
					
					// For each target, create a node
					Iterator k = relationship.getTargets().iterator();
					while(k.hasNext()) {
					    
					    IProgramElement targetElement = hierarchy.findElementForHandle((String) k.next());
					    LinkNode link = new LinkNode(relationship, targetElement, type);
					    
					    // Append to the relation node
					    relationNode.addChild(link);
					} 
				}	  
			}
	    }
	    
	    public void postProcess(IProgramElement node) {
	        
	    }
	}
	
	/********************************************************************************************************************
	 * JUST HERE FOR DEBUG AND BACKUP
	 * TODO REMOVE IT!!!
	 * 
	 * @param self
	 * @return
	 */
	public boolean visit(CjAdviceMethodDeclaration self) {

	    //System.out.println("Got an CjAdviceMethodDeclaration.. abandoning " + self.toString());
	    if (true)
	        return false;
		CaesarProgramElement node = new CaesarProgramElement(
				self.getAdvice().getKind().wrappee().getName().replaceAll("/", "."), 
		    	CaesarProgramElement.Kind.ADVICE, 
		    	self.getModifiers(), 
		    	makeLocation(self.getTokenReference()),
		    	new ArrayList(),
		    	new ArrayList(),
				"",
				"");
		node.setBytecodeName(self.getIdent());
		node.setBytecodeSignature(self.getMethod().getSignature());

		
	    CaesarPointcut pointcut = self.getAdvice().getPointcut();
	    //System.out.println("ADVICE METHOD " + self.toString());
	    List l = new ArrayList();
	    addAllNamed(pointcut.wrappee(), l);
	    Iterator i = l.iterator();
	    while(i.hasNext()) {
	        ReferencePointcut rp = (ReferencePointcut) i.next();
	      //  System.out.println("GOT REFERENCE " + rp.name + " - " + rp.onType);
	        //ResolvedMember member = getPointcutDeclaration(rp, self);
	        
	    }
	    /*
	    IRelationship forward = asmManager.getMap().get(node, );        
	    IRelationship foreward = AsmManager.getDefault().getRelationshipMap().
	    	get(peNode.getHandleIdentifier(), IRelationship.Kind.USES_POINTCUT, "uses pointcut", false, true);
        foreward.addTarget(ProgramElement.genHandleIdentifier(member.getSourceLocation())); 
        
        IRelationship back = AsmManager.getDefault().getRelationshipMap().
        	get(ProgramElement.genHandleIdentifier(member.getSourceLocation()), IRelationship.Kind.USES_POINTCUT, "pointcut used by", false, true);
        back.addTarget(peNode.getHandleIdentifier()); 
        
		public IRelationship get(String source, IRelationship.Kind kind,
                String relationshipName, boolean runtimeTest,
                boolean createIfMissing);
		*/
	    
		// TODO [understand]: why use a registry?
		// When adding directly, LinkNodes won't be created. 
		// (Based on REGISTRY_CLASS_NAME?)
		CaesarProgramElement registry = findChildByName(getCurrentStructureNode().getChildren(), REGISTRY_CLASS_NAME);
		if(registry == null){
			registry = new CaesarProgramElement(
					REGISTRY_CLASS_NAME.replaceAll("/", "."),
					CaesarProgramElement.Kind.ADVICE_REGISTRY,
					0,
					null,
					new ArrayList(),
					new ArrayList(),
					"",
					"");
			getCurrentStructureNode().addChild(registry);
		}
		registry.addChild(node);
		
		return false;
	}
}
