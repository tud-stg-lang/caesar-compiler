package org.caesarj.compiler.joinpoint;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.declaration.CjAdviceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjPointcutDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjVirtualClassDeclaration;
import org.caesarj.compiler.typesys.CaesarTypeSystem;
import org.caesarj.compiler.typesys.graph.CaesarTypeNode;
import org.caesarj.compiler.typesys.java.JavaTypeNode;

/**
 * @author vaidas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GenerateDeploymentSupport {
	CompilerBase compiler;
	KjcEnvironment environment;
	CaesarTypeSystem caesarTypeSystem;
		
	public GenerateDeploymentSupport(CompilerBase compiler, KjcEnvironment environment) {
		this.compiler = compiler;
		this.environment = environment;
		this.caesarTypeSystem = environment.getCaesarTypeSystem(); 
	}
	
	public void generateSupportClasses() {
		determineUniqueCrosscutting();
		determineToBeGenerated();
		generateAdviceMethods();
		generateAspectRegistries();
		cleanCrosscuttingInfo();		
	}
	
	private void determineUniqueCrosscutting() {
        Collection allTypes = caesarTypeSystem.getJavaTypeGraph().getAllTypes();        
		 
		for (Iterator it = allTypes.iterator(); it.hasNext();) {
		 	JavaTypeNode item = (JavaTypeNode)it.next();
		 	CaesarTypeNode node = item.getType();
		 	if (node == null) {
		 		continue;
		 	}		 	
		 	List ccLst = getCrosscuttingMixinList(node);
		 	/* skip non-crosscutting classes */
		 	if (!ccLst.isEmpty()) {
		 		List ccSuperLst = getSuperCrosscuttingMixinList(node);
		 	
			 	/* check if the crosscutting info is different from super */
			 	if (!ccSuperLst.containsAll(ccLst)) {
			 		node.setUniqueCrosscutting();
			 	}
		 	}
		}
	}
	
	private void determineToBeGenerated() {
		Collection allTypes = caesarTypeSystem.getJavaTypeGraph().getAllTypes();        
		 
		for (Iterator it = allTypes.iterator(); it.hasNext();) {
		 	JavaTypeNode item = (JavaTypeNode)it.next();
		 	CaesarTypeNode node = item.getType();
		 	if (node == null) {
		 		continue;
		 	}
		 	if (node.canBeInstantiated()) {
		 		List ccLst = getCrosscuttingMixinList(node);
		 		for (Iterator it2 = ccLst.iterator(); it2.hasNext();) {
		 			CaesarTypeNode mixin = (CaesarTypeNode)it2.next();
		 			if (mixin.isUniqueCrosscutting()) {
		 				mixin.setNeedsAspectRegistry();
		 				break;
		 			}		 			
		 		}
		 	}
		}
	}
	
	private void generateAdviceMethods() {
		Collection allTypes = caesarTypeSystem.getJavaTypeGraph().getAllTypes();        
		 
		for (Iterator it = allTypes.iterator(); it.hasNext();) {
		 	JavaTypeNode item = (JavaTypeNode)it.next();
		 	CaesarTypeNode node = item.getType();
		 	if (node == null) {
		 		continue;
		 	}		 	
		 	if (node.declaredCrosscutting()) {
		 		CjVirtualClassDeclaration caesarClass = node.getTypeDecl().getCorrespondingClassDeclaration();
				
		 		DeploymentClassFactory utils =
					new DeploymentClassFactory(
						caesarClass,
						environment);
				
				utils.generateAdviceMethods();
		 	}
		}
	}
	
	private void generateAspectRegistries() {
		Collection allTypes = caesarTypeSystem.getJavaTypeGraph().getAllTypes();        
		 
		for (Iterator it = allTypes.iterator(); it.hasNext();) {
		 	JavaTypeNode item = (JavaTypeNode)it.next();
		 	CaesarTypeNode node = item.getType();
		 	if (node == null) {
		 		continue;
		 	}		 	
		 	if (node.needsAspectRegistry()) {
		 		CjVirtualClassDeclaration caesarClass = node.getTypeDecl().getCorrespondingClassDeclaration();
				
		 		DeploymentClassFactory utils =
					new DeploymentClassFactory(
						caesarClass,
						environment);
		 		
		 		List pointcuts = new LinkedList();
		 		List advices = new LinkedList();
		 		collectAllPointcutsAndAdvice(node, pointcuts, advices);
		 		
		 		caesarClass.setAspectInterface(utils.createAspectInterface(
		 				(CjAdviceDeclaration[])advices.toArray(new CjAdviceDeclaration[0])));
		 		caesarClass.setRegistryClass(utils.createSingletonAspect(
		 				(CjPointcutDeclaration[])pointcuts.toArray(new CjPointcutDeclaration[0]), 
		 				(CjAdviceDeclaration[])advices.toArray(new CjAdviceDeclaration[0])));
		 		utils.modifyAspectClass();				
		 	}
		}
	}
	
	private void cleanCrosscuttingInfo() {
		Collection allTypes = caesarTypeSystem.getJavaTypeGraph().getAllTypes();        
		 
		for (Iterator it = allTypes.iterator(); it.hasNext();) {
		 	JavaTypeNode item = (JavaTypeNode)it.next();
		 	CaesarTypeNode node = item.getType();
		 	if (node == null) {
		 		continue;
		 	}		 	
		 	if (node.declaredCrosscutting()) {
		 		CjVirtualClassDeclaration caesarClass = node.getTypeDecl().getCorrespondingClassDeclaration();
				
		 		DeploymentClassFactory utils =
					new DeploymentClassFactory(
						caesarClass,
						environment);
				
				utils.cleanCrosscuttingInfo();
		 	}
		}
	}
		
	private List getSuperCrosscuttingMixinList(CaesarTypeNode node) {
		Iterator it = node.getMixinList().iterator();
		it.next(); /* skip the node itself */
		if (it.hasNext()) {
			return getCrosscuttingMixinList((CaesarTypeNode)it.next());
		}
		else {
			return new LinkedList(); /* return empty list */
		}
	}
	
	private List getCrosscuttingMixinList(CaesarTypeNode node) {
		List lst = new LinkedList();
		for (Iterator it = node.getMixinList().iterator(); it.hasNext();) {
            CaesarTypeNode item = (CaesarTypeNode) it.next();
            if (item.declaredCrosscutting()) {
            	lst.add(item);
            }
        }
		return lst;
	}
	
	private void collectAllPointcutsAndAdvice(CaesarTypeNode node, List pointcuts, List advices) {
		List ccLst = getCrosscuttingMixinList(node);
		HashSet pctSet = new HashSet();
			
 		for (Iterator it = ccLst.iterator(); it.hasNext();) {
 			CaesarTypeNode mixin = (CaesarTypeNode)it.next();
 			CjVirtualClassDeclaration classDecl = mixin.getTypeDecl().getCorrespondingClassDeclaration();
 			
 			/* add all advices to the list */
 			CjAdviceDeclaration declAdv[] = classDecl.getAdvices();
 			for (int i1 = 0; i1 < declAdv.length; i1++) {
 				advices.add(declAdv[i1]);
 			}
 			
 			/* add unique pointcuts to the list */
 			CjPointcutDeclaration declPct[] = classDecl.getPointcuts();
 			for (int i1 = 0; i1 < declPct.length; i1++) {
 				String ident = declPct[i1].getIdent();
 				if (!pctSet.contains(ident)) {
 					pctSet.add(ident);
 					pointcuts.add(declPct[i1]);
 				} 				
 			} 				 			
 		}	
	}
}