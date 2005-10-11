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
 * $Id: CClassFactory.java,v 1.35 2005-10-11 14:59:55 gasiunas Exp $
 */

package org.caesarj.compiler.cclass;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.CjInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjMixinInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjVirtualClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JFieldDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JReturnStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.export.CCjSourceClass;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.TokenReference;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CClassFactory implements CaesarConstants {

	private CjVirtualClassDeclaration caesarClass;
	
	private String interfaceName;
	private String prefix;
    CClass interfaceOwner;

	/**
	 * Constructor for CaesarDeploymentUtils.
	 */
	public CClassFactory(
		CjVirtualClassDeclaration caesarClass
	) {
		this.caesarClass = caesarClass;
	
		initState();
	}

	private void initState() {              
        CCjSourceClass caesarClassOwner = (CCjSourceClass)caesarClass.getOwner();

        if(caesarClassOwner != null) {
            CjVirtualClassDeclaration ownerClassDeclaration = 
                (CjVirtualClassDeclaration)caesarClassOwner.getTypeDeclaration();
            interfaceOwner = 
                ownerClassDeclaration.getMixinIfcDeclaration().getCClass();
        }

        if(interfaceOwner != null) {
            prefix = interfaceOwner.getQualifiedName()+'$';
        }
        else {
            prefix = caesarClass.getCClass().getPackage();
            if(prefix.length() > 0) {
                prefix += '/';
            }
        }

		//Intialize some class and interface identifiers
		interfaceName = caesarClass.getOriginalIdent();
	}

	/**
	 * Creates the cclass Interface.
	 */
	public CjInterfaceDeclaration createCaesarClassInterface() {

		JMethodDeclaration[] cclassMethods = caesarClass.getMethods();

        ArrayList interfaceMethods = new ArrayList(cclassMethods.length);
       
        CReferenceType[] extendedTypes = new CReferenceType[caesarClass.getSuperClasses().length];
        CReferenceType[] implementedTypes = new CReferenceType[caesarClass.getInterfaces().length];
        
        for (int i = 0; i < extendedTypes.length; i++) {
            extendedTypes[i] = caesarClass.getSuperClasses()[i];
        }

        for (int i = 0; i < implementedTypes.length; i++) {
            implementedTypes[i] = caesarClass.getInterfaces()[i];
        }

		CjMixinInterfaceDeclaration cclassInterface =
			new CjMixinInterfaceDeclaration(
				caesarClass.getTokenReference(),
				ACC_PUBLIC,
				interfaceName,
				extendedTypes,
				implementedTypes,
				JFieldDeclaration.EMPTY,
				(JMethodDeclaration[])interfaceMethods.toArray(new JMethodDeclaration[]{}),
				new JTypeDeclaration[0],
				new JPhylum[0]);                  

        // link this two AST elements
        caesarClass.setMixinIfcDeclaration(cclassInterface);
        cclassInterface.setCorrespondingClassDeclaration(caesarClass);
        cclassInterface.setOriginalCompUnit(caesarClass.getOriginalCompUnit());
        
		return cclassInterface;
	}
   
	public void modifyCaesarClass(TypeFactory factory) {    	
		caesarClass.setInterfaces(CReferenceType.EMPTY);
		caesarClass.setSuperClass(null);				
		caesarClass.getCClass().setInterfaces(CReferenceType.EMPTY);
		caesarClass.getCClass().setSuperClass(null);
		
		List accessors = new LinkedList();
		JFieldDeclaration fields[] = caesarClass.getFields();
		for (int i = 0; i < fields.length; i++) {
		    JFieldDeclaration f = fields[i];
            if( CModifier.contains(CModifier.ACC_PUBLIC, f.getVariable().getModifiers())) {
                
                //TokenReference where = TokenReference.NO_REF;
                TokenReference where = f.getTokenReference();
                
                JMethodDeclaration decl = 
                	new JMethodDeclaration(
                	    where,
                	    CModifier.ACC_PUBLIC,
                	    f.getType(factory),
                	    CaesarConstants.GETTER_PREFIX+f.getVariable().getIdent(),
                	    JFormalParameter.EMPTY,
                	    CReferenceType.EMPTY,
                	    new JBlock(
                	        where, 
                	        new JStatement[]{
                	            new JReturnStatement(
                	                where, 
                	                new JNameExpression(
                	                    f.getTokenReference(), 
                	                    f.getVariable().getIdent()),
                	                null
            	                )                	            
            	            }, 
                	        null
            	        ),
                	    null, null
                	);
                decl.setGenerated();                
                accessors.add(decl);
            }
        }
		
		caesarClass.addMethods(accessors);
	}
}
