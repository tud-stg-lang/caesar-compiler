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
 * $Id: CaesarAsmBuilder.java,v 1.6 2005-04-15 10:23:13 thiago Exp $
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

import org.aspectj.asm.IHierarchy;
import org.aspectj.asm.IProgramElement;
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
import org.caesarj.compiler.ast.phylum.declaration.CjAdviceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjAdviceMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjDeploymentSupportClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjMixinInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjPointcutDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjVirtualClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JConstructorDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JFieldDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.ast.visitor.VisitorSupport;
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

	protected Stack classStack = new Stack();
	
	protected CaesarJAsmManager asmManager = null;

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
		
		hierarchy.setRoot(
		        new ProgramElement(
		                rootLabel.replaceAll("/", "."), 
		                IProgramElement.Kind.FILE_JAVA,
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

		VisitorSupport visitor = new VisitorSupport(this);
		unit.accept(visitor);

		this.asmStack.pop();
	}

	/**
	 * CompilationUnit visit method
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
		
		if (self.getPackageName() != null) {
			String packageName = self.getPackageName().getName();
			
			CaesarProgramElement fileListNode = null;
			fileListNode = findChildByName(asmManager.getHierarchy().getRoot().getChildren(), packageName);
			if(fileListNode == null){
				// create new filelist
				fileListNode = new CaesarProgramElement(
						packageName.replaceAll("/", "."),
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
			
			
			/*String pkgName = self.getPackageName().getName();
			pkgName = pkgName.replaceAll("/", ".");

			// find package-node or create new if it does not exist yet.
			CaesarProgramElementNode pkgNode = null;
			pkgNode = findChildByName(getStructureModel().getRoot().getChildren(), pkgName);
			if(pkgNode == null){
				// create new package node
				pkgNode = new CaesarProgramElementNode(
						pkgName, 
				    	CaesarProgramElementNode.Kind.PACKAGE, 
				    	0, 
				    	null,
				    	new ArrayList(),
				    	new ArrayList(),
						"",
						"");
				// add package-node to structuremodel
				getStructureModel().getRoot().addChild(pkgNode);
			}
			
			// remove all children of package node, which have the same source-file (why?)
			Iterator it = pkgNode.getChildren().iterator();
			while(it.hasNext()) {
				CaesarProgramElementNode child = (CaesarProgramElementNode) it.next();
				if (child.getSourceLocation().getSourceFile().equals(file)) {
					pkgNode.removeChild(child);
				}
			}
			// add file-node as child for package-node
			pkgNode.addChild(node);*/
		}else{
			// self.getPackageName() == null
			
			// if the node already exists remove before adding
			Iterator it = asmManager.getHierarchy().getRoot().getChildren().iterator();
			while(it.hasNext()) {
				CaesarProgramElement child = (CaesarProgramElement) it.next();
				if (child.getSourceLocation().getSourceFile().equals(file)) {
				    ((ProgramElement) asmManager.getHierarchy().getRoot()).removeChild(child);
				}
			}
			getCurrentStructureNode().addChild(node);
		}
		
		// add package
		String pkgName = "";
		if(self.getPackageName() == null){
			pkgName = "(default package)";
		}else if(self.getPackageName().getName().equals("")){
			pkgName = "(default package)";
		}else{
			pkgName = self.getPackageName().getName().replaceAll("/", ".");
		}
		CaesarProgramElement pkgNode = new CaesarProgramElement(
				pkgName.replaceAll("/", "."), 
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

		try {
		    asmManager.getHierarchy().addToFileMap(file.getCanonicalPath(), node);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		this.asmStack.push(node);
			
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
		/*InterfaceNode peNode = new InterfaceNode(self.getIdent(),
				CaesarProgramElementNode.Kind.INTERFACE, makeLocation(self
						.getTokenReference()), self.getModifiers(), "", //$NON-NLS-1$
				new ArrayList());

		getCurrentStructureNode().addChild(peNode);
		this.asmStack.push(peNode);
		this.classStack.push(self);
		*/
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
		this.classStack.push(self);		
		return true;
	}

	public void endVisit(JInterfaceDeclaration self) {
		this.asmStack.pop();
		this.classStack.pop();
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
	 * crosscutting und normal classes
	 */
	public boolean visit(JClassDeclaration self) {
		/*this.classStack.push(self);

		CaesarProgramElementNode peNode;

		if (CModifier.contains(self.getModifiers(),
				ClassfileConstants2.ACC_CROSSCUTTING)) {
			peNode = new AspectNode(self.getIdent(),
					CaesarProgramElementNode.Kind.ASPECT, makeLocation(self
							.getTokenReference()), self.getModifiers(), "", //$NON-NLS-1$
					new ArrayList(), self.getCClass());
		} else if (self instanceof CjVirtualClassDeclaration) {

			if (self.getCClass().isImplicit()) {
				this.asmStack.push(null);
				return false;
			}

			peNode = new CClassNode(self.getIdent(),
					CaesarProgramElementNode.Kind.CLASS, makeLocation(self
							.getTokenReference()), self.getModifiers(), "", //$NON-NLS-1$
					new ArrayList(), self.getCClass());
		} else {
			peNode = new ClassNode(self.getIdent(),
					CaesarProgramElementNode.Kind.CLASS, makeLocation(self
							.getTokenReference()), self.getModifiers(), "", //$NON-NLS-1$
					new ArrayList());
		}

		getCurrentStructureNode().addChild(peNode);
		this.asmStack.push(peNode);
		*/
		CaesarProgramElement.Kind kind = CaesarProgramElement.Kind.CLASS;
		if (CModifier.contains(self.getModifiers(),	ClassfileConstants2.ACC_CROSSCUTTING)){
			kind = CaesarProgramElement.Kind.ASPECT;
		}
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
		this.classStack.push(self);		
		return true;
	}

	public void endVisit(JClassDeclaration self) {
		this.asmStack.pop();
		this.classStack.pop();
	}
	
	public boolean visit(CjVirtualClassDeclaration self) {
		this.classStack.push(self);		
		if (self.getCClass().isImplicit()) {
			// why this statement? without it an InconsistencyException is thorwn during compilation. 
			this.asmStack.push(null);
			return false;
		}
		// If class is an Aspect, kind = aspect instead of virtual_class
		CaesarProgramElement.Kind kind = CaesarProgramElement.Kind.VIRTUAL_CLASS;
		if (CModifier.contains(self.getModifiers(),	ClassfileConstants2.ACC_CROSSCUTTING)){
			kind = CaesarProgramElement.Kind.ASPECT;
		}
		CaesarProgramElement node = new CaesarProgramElement(
				self.getIdent().replaceAll("/", "."), 
				kind, 
		    	self.getModifiers(), 
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
		this.asmStack.pop();
		this.classStack.pop();
	}

	/**
	 * ConstructorDeclaration
	 *  
	 */
	public boolean visit(JConstructorDeclaration self) {
		/*ConstructorDeclarationNode peNode = new ConstructorDeclarationNode(
				self, ((JClassDeclaration) this.classStack.peek()), self
						.getIdent(), CaesarProgramElementNode.Kind.CONSTRUCTOR,
				makeLocation(self.getTokenReference()), self.getModifiers(),
				"", //$NON-NLS-1$
				new ArrayList());

		peNode.setBytecodeName(self.getIdent());
		peNode.setBytecodeSignature(self.getMethod().getSignature());

		getCurrentStructureNode().addChild(peNode);
		*/
		// resolve formal parameters
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
		// create node
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
		/*
		if (self.getIdent().startsWith("$"))
			return false;

		CaesarProgramElementNode peNode = new MethodDeclarationNode(self,
				((JTypeDeclaration) this.classStack.peek()), self.getIdent(),
				CaesarProgramElementNode.Kind.METHOD, makeLocation(self
						.getTokenReference()), self.getModifiers(), "", //$NON-NLS-1$
				new ArrayList());

		peNode.setBytecodeName(self.getIdent());
		peNode.setBytecodeSignature(self.getMethod().getSignature());

		if (self.getIdent().equals("main")) { //$NON-NLS-1$
			peNode.setRunnable(true);
		}

		getCurrentStructureNode().addChild(peNode);
		*/
		
		if (self.getIdent().startsWith("$"))
			return false;
		
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
		// create node
		CaesarProgramElement node = new CaesarProgramElement(
				self.getIdent().replaceAll("/", "."), 
		    	CaesarProgramElement.Kind.METHOD, 
		    	self.getModifiers(), 
		    	makeLocation(self.getTokenReference()),
		    	new ArrayList(),
		    	parameters,
				//self.getReturnType().getSignature(),
				self.getReturnType().toString(),
				"");
		node.setBytecodeName(self.getIdent());
		node.setBytecodeSignature(self.getMethod().getSignature());
		if (self.getIdent().equals("main")) { 
			node.setRunnable(true);
		}
		getCurrentStructureNode().addChild(node);
		return false;
	}

	public boolean visit(CjPointcutDeclaration self) {
		/*CaesarProgramElementNode peNode = new PointcutNode(pointcut,
				((JClassDeclaration) this.classStack.peek()), pointcut
						.getIdent(), CaesarProgramElementNode.Kind.POINTCUT,
				makeLocation(pointcut.getTokenReference()), pointcut
						.getModifiers(), "", //$NON-NLS-1$
				new ArrayList());
		getCurrentStructureNode().addChild(peNode);
		*/
	    System.out.println("POINTCUT DECL " + self.toString());
	    System.out.println("POINTCUT - " + self.getPointcut().wrappee().toString());
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
					// TODO [test]: returns getSignature() the correct value or toString()
					parameterArray[i].getType().getSignature());
			parameters.add(parameter);
		}
		// create node
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

	public boolean visit(CjAdviceMethodDeclaration self) {
		/*CaesarProgramElementNode peNode = new AdviceDeclarationNode(
				((JTypeDeclaration) this.classStack.peek()).getCClass()
						.getQualifiedName(), self.getAdvice().getKind()
						.wrappee().getName(),
				CaesarProgramElementNode.Kind.ADVICE, makeLocation(self
						.getTokenReference()), self.getModifiers(), "", //$NON-NLS-1$
				new ArrayList());

		peNode.setBytecodeName(self.getIdent());
		peNode.setBytecodeSignature(self.getMethod().getSignature());

		{
			CaesarProgramElementNode registryNode = findChildByName(
					getCurrentStructureNode().getChildren(),
					REGISTRY_CLASS_NAME);
			if (registryNode == null) {
				registryNode = new AspectRegistryNode(REGISTRY_CLASS_NAME, //$NON-NLS-1$
						CaesarProgramElementNode.Kind.CLASS, makeLocation(self
								.getTokenReference()), self.getModifiers(), "", //$NON-NLS-1$
						new ArrayList());
				getCurrentStructureNode().addChild(registryNode);
			}
			registryNode.addChild(peNode);
		}
		*/
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
	    System.out.println("ADVICE METHOD " + self.toString());
	    List l = new ArrayList();
	    addAllNamed(pointcut.wrappee(), l);
	    Iterator i = l.iterator();
	    while(i.hasNext()) {
	        ReferencePointcut rp = (ReferencePointcut) i.next();
	        System.out.println("GOT REFERENCE " + rp.name + " - " + rp.onType);
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

	/*
    private List genNamedPointcuts(MethodDeclaration methodDeclaration) {
        List pointcuts = new ArrayList();
        if (methodDeclaration instanceof AdviceDeclaration) {
            if (((AdviceDeclaration)methodDeclaration).pointcutDesignator != null) 
                addAllNamed(((AdviceDeclaration)methodDeclaration).pointcutDesignator.getPointcut(), pointcuts);
		} else if (methodDeclaration instanceof PointcutDeclaration) { 
		    if (((PointcutDeclaration)methodDeclaration).pointcutDesignator != null)
		        addAllNamed(((PointcutDeclaration)methodDeclaration).pointcutDesignator.getPointcut(), pointcuts);	
		} 
		return pointcuts;
    }
*/
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
    /*
    private ResolvedMember getPointcutDeclaration(ReferencePointcut rp, CjAdviceMethodDeclaration declaration) {
        
		World world = ((AjLookupEnvironment)declaration.scope.environment()).factory.getWorld();
		TypeX onType = rp.onType;
		if (onType == null) {
		    Member member = EclipseFactory.makeResolvedMember(declaration.binding);
			onType = member.getDeclaringType();
		}
		ResolvedMember[] members = onType.getDeclaredPointcuts(world);
		if (members != null) {
			for (int i = 0; i < members.length; i++) {
			    if (members[i].getName().equals(rp.name)) {
			        return members[i];
			    }
			}
		}
		return null;
    }
	*/
	
	public boolean visit(CjAdviceDeclaration self) {
		/*CaesarProgramElementNode peNode = new AdviceDeclarationNode(
				((JTypeDeclaration) this.classStack.peek()).getCClass()
						.getQualifiedName(),
				self.getKind().wrappee().getName(),
				CaesarProgramElementNode.Kind.ADVICE, makeLocation(self
						.getTokenReference()), self.getModifiers(), "", //$NON-NLS-1$
				new ArrayList());

		peNode.setBytecodeName(self.getIdent());
		peNode.setBytecodeSignature(self.getMethod().getSignature());

		getCurrentStructureNode().addChild(peNode);
		*/
	    System.out.println("ADVICE  " + self.toString());
	    System.out.println("POINTCUT  " + self.getKind().wrappee().getName());
		CaesarProgramElement node = new CaesarProgramElement(
				self.getKind().wrappee().getName().replaceAll("/", "."), //self.getIdent(), 
		    	CaesarProgramElement.Kind.ADVICE, 
		    	self.getModifiers(), 
		    	makeLocation(self.getTokenReference()),
		    	new ArrayList(),
		    	new ArrayList(),
				"",
				"");
		node.setBytecodeName(self.getIdent());
		node.setBytecodeSignature(self.getMethod().getSignature());
		getCurrentStructureNode().addChild(node);
		return false;
	}

	/**
	 * FIELD
	 */
	public boolean visit(JFieldDeclaration self) {
		/*
		if (self.getVariable().getIdent().startsWith("$"))
			return false;

		JVariableDefinition var = self.getVariable();
		FieldNode peNode = new FieldNode(var.getIdent(),
				CaesarProgramElementNode.Kind.FIELD, makeLocation(self
						.getTokenReference()), var.getType(), var
						.getModifiers(), "", //$NON-NLS-1$
				new ArrayList());

		getCurrentStructureNode().addChild(peNode);
		*/
		
		JVariableDefinition var = self.getVariable();
		if (var.getIdent().startsWith("$"))
			return false;
		
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

	private ISourceLocation makeLocation(TokenReference ref) {
		String fileName = new String(ref.getFile());

		return new SourceLocation(new File(fileName), ref.getLine());
	}

	private IProgramElement getCurrentStructureNode() {
		return (IProgramElement) this.asmStack.peek();
	}

	/*
	 * HELPER METHODS
	 */

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
}
