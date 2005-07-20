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
 * $Id: ConstructorTransformVisitor.java,v 1.3 2005-07-20 10:09:29 gasiunas Exp $
 */

package org.caesarj.compiler.contructors;

import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.CjInitMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjMixinInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjVirtualClassCtorDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjVirtualClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JConstructorDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMemberDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JConstructorBlock;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.ast.visitor.VisitorSupport;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;

/**
 * Visits all classthe AST down to expression granularity.
 * It extends the interface of advice methods, if they make use of Join Point Reflection.
 * It also determines the corresponding extraArgumentFlags for the advice.
 * 
 * Ivica> changed visitor
 * 
 * @author Jürgen Hallpap
 * @author Ivica Aracic
 */
public class ConstructorTransformVisitor implements IVisitor, CaesarConstants  {

	private CompilerBase compiler; 
	private TypeFactory factory;
	private VisitorSupport visitor = new VisitorSupport(this);
	
	public ConstructorTransformVisitor(CompilerBase compiler, KjcEnvironment environment) {
		this.factory = environment.getTypeFactory();
		this.compiler = compiler;	
	}
	
	public boolean start(JPhylum node) {
        return visitor.start(node);
    }
	
	public void end() {
	    visitor.end();
    }
	
	// recurse by default into all nodes...
	public boolean visit(JPhylum self) {
	    return true;
    }
    
	// ...but not into member declaration...
	public boolean visit(JMemberDeclaration self) {
	    return false;
    }    

	// except CjClassDeclaration
    public boolean visit(CjVirtualClassDeclaration self) {
    	transformConstructors(self);
        return true;
    }
    
    public void transformConstructors(CjVirtualClassDeclaration cd) {
    	// add outer variable
    	CType outerType = null;
    	
    	CjMixinInterfaceDeclaration mixinIfc = cd.getMixinIfcDeclaration();
    	
    	// create a type reference representing the class declaration
        CReferenceType selfType = new CClassNameType(mixinIfc.getIdent());
        
        JMethodDeclaration methods[] = cd.getMethods();
        
        // transform contructors to  methods
        boolean defConstrFound = false;
        for (int i = 0; i < methods.length; i++) {
			if (methods[i] instanceof JConstructorDeclaration) {
				// IVICA: check ctor name
		        // here we tune the error message in order to avoid displaying _Impl
		        // suffix
				if (methods[i].getIdent() != cd.getIdent()) {
					compiler.reportTrouble(
							new PositionedError(
									methods[i].getTokenReference(),
									KjcMessages.CONSTRUCTOR_BAD_NAME,
									methods[i].getIdent().substring(0, methods[i].getIdent().length() - 5),
									cd.getIdent().substring(0, cd.getIdent().length() - 5)));
				}
				
				if (!CModifier.contains(methods[i].getModifiers(), ACC_PUBLIC)) {
					compiler.reportTrouble(
							new PositionedError(
									methods[i].getTokenReference(),
									CaesarMessages.CCLASS_CTOR_PUBLIC));
				}				
				
				methods[i] = new CjInitMethodDeclaration(
					methods[i].getTokenReference(),
					methods[i].getModifiers(),
					selfType,
					CONSTR_METH_NAME,
					methods[i].getParameters(),
					methods[i].getExceptions(),
					new JBlock(
						methods[i].getTokenReference(), 
						((JConstructorBlock)methods[i].getBlockBody()).getBody(), 
						null
					)
				);
				
				if (methods[i].getArgs().length == 0) {
					defConstrFound = true;
				}
			}
		}
        
        cd.setMethods(methods);
        
        // create method for default contructor
        if (!defConstrFound) {
        	String methIdent = CONSTR_METH_NAME;
	        cd.addMethod(
				new CjInitMethodDeclaration(
						cd.getTokenReference(),
						ACC_PUBLIC,
						selfType,
						CONSTR_METH_NAME,
						new JFormalParameter[] {},
						CReferenceType.EMPTY,
						new JBlock(
								cd.getTokenReference(), 
								JBlock.EMPTY, 
								null
					)
				));
        }
        
	    // add real constructor
	    cd.addMethod(
	    	new CjVirtualClassCtorDeclaration(
				cd.getTokenReference(),
				ACC_PUBLIC,
				cd.getIdent(),
				outerType,
				factory));    
    }
}