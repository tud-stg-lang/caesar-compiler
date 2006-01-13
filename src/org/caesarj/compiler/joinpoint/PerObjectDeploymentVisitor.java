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
 * $Id: PerObjectDeploymentVisitor.java,v 1.1 2006-01-13 12:06:06 gasiunas Exp $
 */

package org.caesarj.compiler.joinpoint;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.weaver.patterns.AndPointcut;
import org.aspectj.weaver.patterns.NamePattern;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.ThisOrTargetPointcut;
import org.caesarj.compiler.aspectj.CaesarWildTypePattern;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.CjAdviceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjVirtualClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMemberDeclaration;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.ast.visitor.VisitorSupport;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.Constants;
import org.caesarj.compiler.types.CClassNameType;

/**
 * Finds all classes that require per this deployment and modifies their
 * advices so that they intercept the "this" of the joinpoints
 * 
 * @author Vaidas Gasiunas
 */
public class PerObjectDeploymentVisitor implements IVisitor, CaesarConstants  {

	private VisitorSupport visitor = new VisitorSupport(this);
	
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
    	return self.isPerThisDeployable();
    }
    
    // ... and CjAdviceDeclaration ...
    public boolean visit(CjAdviceDeclaration adviceDec) {
		//include the old parameters
        List adviceParameters = new ArrayList();
        
		for (int i = 0; i < adviceDec.getParameters().length; i++) {
			adviceParameters.add(adviceDec.getParameters()[i]);
		}
		
		/* add additiona parameter JOINPOINT_THIS_PARAM to the advice */
		adviceDec.setExtraArgumentFlag(
			CaesarConstants.JoinPointThis);
		
		JFormalParameter extraParameter =
			new JFormalParameter(
				adviceDec.getTokenReference(),
				JFormalParameter.DES_PARAMETER,
				new CClassNameType(Constants.JAV_OBJECT),
				CaesarConstants.JOINPOINT_THIS_PARAM,
				false);
		adviceParameters.add(extraParameter);
		
		adviceDec.setParameters(
				(JFormalParameter[]) adviceParameters.toArray(
					new JFormalParameter[0])
			);
		
		/* transform the advice poincut from a() to (a() && this(JOINPOINT_THIS_PARAM)) */
		ArrayList<NamePattern> names = new ArrayList<NamePattern>();
		names.add(new NamePattern(CaesarConstants.JOINPOINT_THIS_PARAM));
		
		CaesarWildTypePattern pattern =  new CaesarWildTypePattern(names, false, 0);
		
		Pointcut origPointcut = adviceDec.getPointcut().wrappee();
		Pointcut newPointcut = new AndPointcut(origPointcut, new ThisOrTargetPointcut(true, pattern));
		adviceDec.getPointcut().replacePointcut(newPointcut);
		
        return false;
    }    
}