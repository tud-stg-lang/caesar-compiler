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
 * $Id: StaticFieldDeploymentVisitor.java,v 1.1 2005-03-30 14:24:35 gasiunas Exp $
 */

package org.caesarj.compiler.joinpoint;

import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.CjClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjVirtualClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JFieldDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMemberDeclaration;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.ast.visitor.VisitorSupport;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.UnpositionedError;

/**
 * Visits the AST down to expression granularity.
 * It extends the interface of advice methods, if they make use of Join Point Reflection.
 * It also determines the corresponding extraArgumentFlags for the advice.
 * 
 * Ivica> changed visitor
 * 
 * @author Jürgen Hallpap
 * @author Ivica Aracic
 */
public class StaticFieldDeploymentVisitor implements IVisitor, CaesarConstants  {

	private StaticDeploymentPreparation statDeplPrep;
	private CompilerBase compiler; 
	private KjcEnvironment environment;
	private VisitorSupport visitor = new VisitorSupport(this);
	
	public StaticFieldDeploymentVisitor(CompilerBase compiler, KjcEnvironment environment) {
		this.environment = environment;
		this.compiler = compiler;
		statDeplPrep = new StaticDeploymentPreparation(compiler, environment);
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
    	/* find all deployed fields */
        for (int i = 0; i < self.getBody().length; i++) {
    		if (self.getBody()[i] instanceof JFieldDeclaration) {
    			JFieldDeclaration field = (JFieldDeclaration) self.getBody()[i];
    			if ((field.getVariable().getModifiers() & ACC_DEPLOYED) != 0) {
    				prepareFieldDeployment(self, field);
    			}
    		}
    	}	        
        return true;
    }
    
    /**
     * Prepare static deployment of a class field
     * 
     * @param classDecl	- class declaration
     * @param field		- field declaration
     */
    private void prepareFieldDeployment(CjVirtualClassDeclaration classDecl, JFieldDeclaration field) {
    	classDecl.addClassBlock(
				statDeplPrep.createStaticFieldDeployBlock(
						field.getTokenReference(),
						classDecl,
						field));
    	CjClassDeclaration regClass = null;
    	try {
    		CTypeContext context = classDecl.getTypeContext();
    		CType type = field.getVariable().getType().checkType(context);
    		CClass cclass = type.getCClass();
    		regClass = statDeplPrep.findRegistryClass(cclass.getQualifiedName());
    		
    	}
    	catch (UnpositionedError e) {    		
    		/* will be handled as regClass == null */
    	}
    			
		if (regClass == null) {
			compiler.reportTrouble(
	            new PositionedError(
	            	field.getTokenReference(),
					CaesarMessages.DEPLOYED_CLASS_NOT_CROSSCUTTING 
                )
            );
		}
		
		statDeplPrep.insertClassTouchBlock(regClass, classDecl);
    }
}