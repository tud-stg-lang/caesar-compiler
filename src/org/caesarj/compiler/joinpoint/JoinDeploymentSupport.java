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
 * $Id: JoinDeploymentSupport.java,v 1.1 2005-03-29 09:47:01 gasiunas Exp $
 */

package org.caesarj.compiler.joinpoint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.declaration.CjClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjVirtualClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.context.CContext;
import org.caesarj.util.PositionedError;

/**
 * @author Ostermann
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class JoinDeploymentSupport implements CaesarConstants {
	
	/**
	 * Generates for every nested crosscutting class the corresponding deployment support classes.
	 */
	public static void prepareForDynamicDeployment(CompilerBase compiler, JCompilationUnit cu) {
		List newTypeDeclarations = new ArrayList();
		JTypeDeclaration typeDeclarations[] = cu.getInners();
		CContext ownerCtx = cu.createContext(compiler);
		
		for (int i = 0; i < typeDeclarations.length; i++) {
			
			newTypeDeclarations.add(typeDeclarations[i]);

			if (typeDeclarations[i] instanceof CjVirtualClassDeclaration) {

				CjVirtualClassDeclaration caesarClass =
					(CjVirtualClassDeclaration) typeDeclarations[i];
				
				if (caesarClass.getRegistryClass() != null) {
					
					// add the deployment support classes to the enclosing class
					CjInterfaceDeclaration aspectIfc = caesarClass.getAspectInterface();
					newTypeDeclarations.add(aspectIfc);
					
					CjClassDeclaration registryCls = caesarClass.getRegistryClass();
					newTypeDeclarations.add(registryCls);
					
					// join the modified and new classes
					try {
						aspectIfc.join(ownerCtx);
						registryCls.join(ownerCtx);	
						caesarClass.getMixinIfcDeclaration().join(ownerCtx);
					}
					catch (PositionedError err) {
						System.out.println(err.getMessage());
					}

					if (caesarClass.isStaticallyDeployed()) {
						StaticDeploymentPreparation.prepareForStaticDeployment(caesarClass, cu.getEnvironment());					
					}
				}
				
				if (caesarClass.getInners().length > 0) {
					//consider nested types
					new JoinDeploymentSupport().prepareForDynamicDeployment(caesarClass, cu.getEnvironment());
				}
			}
		}
		if (newTypeDeclarations.size() > typeDeclarations.length) {
			cu.setInners((JTypeDeclaration[]) newTypeDeclarations.toArray(new JTypeDeclaration[0]));
			rejoinMixinInterfaces(cu.getInners(), ownerCtx);
		}
	}
	
	private void prepareForDynamicDeployment(CjClassDeclaration cd, KjcEnvironment environment)
	{
	    List newInners = new LinkedList();
	    CContext ownerCtx = (CContext)cd.getTypeContext();
	    
		for (int i = 0; i < cd.getInners().length; i++)
		{
			if (cd.getInners()[i] instanceof CjVirtualClassDeclaration)
			{
				//create support classes for each crosscutting inner class
				CjVirtualClassDeclaration innerCaesarClass =
					(CjVirtualClassDeclaration) cd.getInners()[i];
				if (innerCaesarClass.getRegistryClass() != null)
				{
					DeploymentClassFactory utils =
						new DeploymentClassFactory(
							innerCaesarClass,
							environment);

					//add the deployment support classes to the enclosing class
					CjInterfaceDeclaration aspectIfc = innerCaesarClass.getAspectInterface();
					newInners.add(aspectIfc);
					
					CjClassDeclaration registryCls = innerCaesarClass.getRegistryClass();
					newInners.add(registryCls);
										
					//join the modified and new classes
					try {
						aspectIfc.join(ownerCtx);
						registryCls.join(ownerCtx);
						innerCaesarClass.getMixinIfcDeclaration().join(ownerCtx);
					}
					catch (PositionedError err) {
						System.out.println(err.getMessage());
					}
				}

				//handle the inners of the inners
				JTypeDeclaration[] innersInners = innerCaesarClass.getInners();
				for (int j = 0; j < innersInners.length; j++)
				{
					if (innersInners[j] instanceof CjClassDeclaration)
					{
						CjClassDeclaration currentInnerInner =
							(CjClassDeclaration) innersInners[j];
						new JoinDeploymentSupport().prepareForDynamicDeployment(currentInnerInner, environment);
					}
				}
			}
		}

		if (newInners.size() > 0)
		{
			// add new declarations as inners to cd
			// note that addInners will update the export object in cd
			cd.addInners((JTypeDeclaration[])newInners.toArray(new JTypeDeclaration[0]));
			rejoinMixinInterfaces(cd.getInners(), ownerCtx);
		}
	}
	
	/**
	 * Rejoin the mixin interfaces of the crosscutting Caesar classes
	 * 
	 * @param decl			array of type declarations
	 * @param ownerCtx		owner context
	 */
	private static void rejoinMixinInterfaces(JTypeDeclaration[] decl, CContext ownerCtx) {
		for (int i = 0; i < decl.length; i++) {
			if (decl[i] instanceof CjVirtualClassDeclaration) {
				CjVirtualClassDeclaration caesarClass =	(CjVirtualClassDeclaration)decl[i];
				if (caesarClass.isCrosscutting()) {
					try {
						caesarClass.getMixinIfcDeclaration().join(ownerCtx);
					}
					catch (PositionedError err) {
						System.out.println(err.getMessage());
					}	
				}
			}
		}
	}
}
