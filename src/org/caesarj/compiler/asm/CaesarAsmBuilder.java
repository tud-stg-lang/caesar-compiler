/*
 * Created on 25.11.2004
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

import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.StructureModel;
import org.aspectj.asm.StructureNode;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.SourceLocation;
import org.caesarj.classfile.ClassfileConstants2;
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
 * @author meffert
 *
 * TODO [documentation]
 */
public class CaesarAsmBuilder {

	private static final String REGISTRY_CLASS_NAME = "Registry"; //$NON-NLS-1$

	protected StructureModel structureModel = null;

	protected Stack asmStack = new Stack();

	protected Stack classStack = new Stack();

	//static Logger logger = Logger.getLogger(AsmBuilder.class);

	/**
	 * initialize the model
	 */
	public static void preBuild(StructureModel structureModel) {
		String rootLabel = "<root>"; //$NON-NLS-1$
		structureModel.setRoot(new ProgramElementNode(rootLabel,
				ProgramElementNode.Kind.FILE_JAVA, new ArrayList()));

		structureModel.setFileMap(new HashMap());
	}

	/**
	 * resolve advice and method signatures before weaving
	 */
	public static void preWeave(StructureModel structureModel) {

	}

	/**
	 * remove support methods and inner classes
	 */
	public static void postBuild(StructureModel structureModel) {
	}

	/**
	 * insert a single compilation unit to model
	 */
	public static void build(JCompilationUnit unit,
			StructureModel structureModel) {
		new CaesarAsmBuilder().internalBuild(unit, structureModel);
	}

	private void internalBuild(JCompilationUnit unit,
			StructureModel structureModelArg) {
		if (unit == null)
			return;

		setStructureModel(structureModelArg);
		this.asmStack.push(structureModelArg.getRoot());

		VisitorSupport visitor = new VisitorSupport(this);
		unit.accept(visitor);

		this.asmStack.pop();
		setStructureModel(null);
	}

	/**
	 * CompilationUnit visit method
	 */
	public boolean visit(JCompilationUnit self) {
		TokenReference ref = self.getTokenReference();
		File file = new File(new String(ref.getFile()));
		// create node for file
		CaesarProgramElementNode node = new CaesarProgramElementNode(
				file.getName(), 
		    	CaesarProgramElementNode.Kind.FILE_JAVA, 
		    	0, 
		    	makeLocation(ref),
		    	new ArrayList(),
		    	new ArrayList(),
				"",
				"");
		
		if (self.getPackageName() != null) {
			String packageName = self.getPackageName().getName();
			
			CaesarProgramElementNode fileListNode = null;
			fileListNode = findChildByName(getStructureModel().getRoot().getChildren(), packageName);
			if(fileListNode == null){
				// create new filelist
				fileListNode = new CaesarProgramElementNode(
						packageName,
						// Have to use PACKAGE as parent type because of ProgramElementNode.getPackagename()
						// instead of ---> CaesarProgramElementNode.Kind.FILE_LST,
						CaesarProgramElementNode.Kind.PACKAGE,
						0,
						null,
						new ArrayList(),
						new ArrayList(),
						"",
						"");
				// add file-list to root node
				getStructureModel().getRoot().addChild(fileListNode);
			}
			// remove all children of filelist node, which have the same source-file
			Iterator it = fileListNode.getChildren().iterator();
			while(it.hasNext()) {
				CaesarProgramElementNode child = (CaesarProgramElementNode) it.next();
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
			Iterator it = getStructureModel().getRoot().getChildren().iterator();
			while(it.hasNext()) {
				CaesarProgramElementNode child = (CaesarProgramElementNode) it.next();
				if (child.getSourceLocation().getSourceFile().equals(file)) {
					getStructureModel().getRoot().removeChild(child);
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
		CaesarProgramElementNode pkgNode = new CaesarProgramElementNode(
				pkgName, 
		    	CaesarProgramElementNode.Kind.PACKAGE, 
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
				CaesarProgramElementNode currentPiNode = new CaesarProgramElementNode(
						currentPi.getName(),
						CaesarProgramElementNode.Kind.PACKAGE_IMPORT,
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
				CaesarProgramElementNode currentCiNode = new CaesarProgramElementNode(
						currentCi.getQualifiedName(),
						CaesarProgramElementNode.Kind.CLASS_IMPORT,
						0,
						makeLocation(currentCi.getTokenReference()),
						new ArrayList(),
						new ArrayList(),
						"",
						"");
				importElements.add(currentCiNode);
			}
			
			// add imports root node
			CaesarProgramElementNode imports = new CaesarProgramElementNode(
					"import declarations",
					CaesarProgramElementNode.Kind.IMPORTS,
					0,
					makeLocation(firstRef),
					importElements,
					new ArrayList(),
					"",
					"");
			
			node.addChild(imports);
		}

		try {
			getStructureModel().addToFileMap(file.getCanonicalPath(), node);
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
		CaesarProgramElementNode node = new CaesarProgramElementNode(
				self.getIdent(), 
		    	CaesarProgramElementNode.Kind.INTERFACE, 
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
		CaesarProgramElementNode.Kind kind = CaesarProgramElementNode.Kind.CLASS;
		if (CModifier.contains(self.getModifiers(),	ClassfileConstants2.ACC_CROSSCUTTING)){
			kind = CaesarProgramElementNode.Kind.ASPECT;
		}
		CaesarProgramElementNode node = new CaesarProgramElementNode(
				self.getIdent(), 
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
		CaesarProgramElementNode.Kind kind = CaesarProgramElementNode.Kind.VIRTUAL_CLASS;
		if (CModifier.contains(self.getModifiers(),	ClassfileConstants2.ACC_CROSSCUTTING)){
			kind = CaesarProgramElementNode.Kind.ASPECT;
		}
		CaesarProgramElementNode node = new CaesarProgramElementNode(
				self.getIdent(), 
				kind, 
		    	self.getModifiers(), 
		    	makeLocation(self.getTokenReference()),
		    	new ArrayList(),
		    	new ArrayList(),
				"",
				"");

		// remove static modifier
		node.getModifiers().remove(CaesarProgramElementNode.Modifiers.STATIC);
		
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
			CaesarProgramElementNode parameter = new CaesarProgramElementNode(
					parameterArray[i].getIdent(),
					CaesarProgramElementNode.Kind.PARAMETER,
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
		CaesarProgramElementNode node = new CaesarProgramElementNode(
				self.getIdent(), 
		    	CaesarProgramElementNode.Kind.CONSTRUCTOR, 
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
			CaesarProgramElementNode parameter = new CaesarProgramElementNode(
					parameterArray[i].getIdent(),
					CaesarProgramElementNode.Kind.PARAMETER,
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
		CaesarProgramElementNode node = new CaesarProgramElementNode(
				self.getIdent(), 
		    	CaesarProgramElementNode.Kind.METHOD, 
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
		List parameters = new ArrayList();
		JFormalParameter[] parameterArray = self.getArgs();
		for(int i=0; i < parameterArray.length; i++){
			CaesarProgramElementNode parameter = new CaesarProgramElementNode(
					parameterArray[i].getIdent(),
					CaesarProgramElementNode.Kind.PARAMETER,
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
		CaesarProgramElementNode node = new CaesarProgramElementNode(
				self.getIdent(), 
		    	CaesarProgramElementNode.Kind.POINTCUT, 
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
		CaesarProgramElementNode node = new CaesarProgramElementNode(
				self.getAdvice().getKind().wrappee().getName(), 
		    	CaesarProgramElementNode.Kind.ADVICE, 
		    	self.getModifiers(), 
		    	makeLocation(self.getTokenReference()),
		    	new ArrayList(),
		    	new ArrayList(),
				"",
				"");
		node.setBytecodeName(self.getIdent());
		node.setBytecodeSignature(self.getMethod().getSignature());

		// TODO [understand]: why use a registry?
		// When adding directly, LinkNodes won't be created. 
		// (Based on REGISTRY_CLASS_NAME?)
		CaesarProgramElementNode registry = findChildByName(getCurrentStructureNode().getChildren(), REGISTRY_CLASS_NAME);
		if(registry == null){
			registry = new CaesarProgramElementNode(
					REGISTRY_CLASS_NAME,
					CaesarProgramElementNode.Kind.ADVICE_REGISTRY,
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
		CaesarProgramElementNode node = new CaesarProgramElementNode(
				self.getKind().wrappee().getName(), //self.getIdent(), 
		    	CaesarProgramElementNode.Kind.ADVICE, 
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
		
		CaesarProgramElementNode node = new CaesarProgramElementNode(
				var.getIdent(), 
		    	CaesarProgramElementNode.Kind.FIELD, 
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

	private StructureNode getCurrentStructureNode() {
		return (StructureNode) this.asmStack.peek();
	}

	private void setStructureModel(StructureModel structureModelArg) {
		this.structureModel = structureModelArg;
	}

	private StructureModel getStructureModel() {
		return this.structureModel;
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
	private CaesarProgramElementNode findChildByName(Collection childrenList, String name) {
		CaesarProgramElementNode child = null;
		Iterator it = childrenList.iterator();
		while(it.hasNext() && child == null) {
			CaesarProgramElementNode currentNode = (CaesarProgramElementNode) it.next();

			if (currentNode.getName().equals(name))
				child = currentNode;
		}
		return child;
	}
}
