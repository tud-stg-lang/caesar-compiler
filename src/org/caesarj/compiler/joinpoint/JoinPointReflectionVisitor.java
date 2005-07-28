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
 * $Id: JoinPointReflectionVisitor.java,v 1.23 2005-07-28 11:47:04 gasiunas Exp $
 */

package org.caesarj.compiler.joinpoint;

import java.util.ArrayList;
import java.util.List;

import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.CjAdviceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMemberDeclaration;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.ast.visitor.VisitorSupport;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.types.CClassNameType;

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
public class JoinPointReflectionVisitor implements IVisitor, CaesarConstants  {

	private boolean needsThisJoinPoint = false;
	private boolean needsThisJoinPointStaticPart = false;
	private boolean needsThisEnclosingJoinPointStaticPart = false;	
	private List adviceParameters;
	
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
    public boolean visit(CjClassDeclaration self) {
       return true;
    }

    // ... and CjAdviceDeclaration ...
    public boolean visit(CjAdviceDeclaration adviceDec) {
		//include the old parameters
        adviceParameters = new ArrayList();
        
		for (int i = 0; i < adviceDec.getParameters().length; i++) {
			adviceParameters.add(adviceDec.getParameters()[i]);
		}
		
        return true;
    }
    
    public void endVisit(CjAdviceDeclaration adviceDec) {                         
        if (needsThisJoinPointStaticPart) {
			adviceDec.setExtraArgumentFlag(
				CaesarConstants.ThisJoinPointStaticPart);
			JFormalParameter extraParameter =
				new JFormalParameter(
					adviceDec.getTokenReference(),
					JFormalParameter.DES_GENERATED,
					new CClassNameType(JOIN_POINT_STATIC_PART_CLASS),
					THIS_JOIN_POINT_STATIC_PART,
					false);
			adviceParameters.add(extraParameter);
		}

		if (needsThisJoinPoint) {

			adviceDec.setExtraArgumentFlag(CaesarConstants.ThisJoinPoint);
			JFormalParameter extraParameter =
				new JFormalParameter(
					adviceDec.getTokenReference(),
					JFormalParameter.DES_GENERATED,
					new CClassNameType(JOIN_POINT_CLASS),
					THIS_JOIN_POINT,
					false);
			adviceParameters.add(extraParameter);
		}

		if (needsThisEnclosingJoinPointStaticPart) {

			adviceDec.setExtraArgumentFlag(
				CaesarConstants.ThisEnclosingJoinPointStaticPart);
			JFormalParameter extraParameter =
				new JFormalParameter(
					adviceDec.getTokenReference(),
					JFormalParameter.DES_GENERATED,
					new CClassNameType(JOIN_POINT_STATIC_PART_CLASS),
					THIS_ENCLOSING_JOIN_POINT_STATIC_PART,
					false);
			adviceParameters.add(extraParameter);
		} //				determineExtraArgumentFlags(adviceDec);
		
		needsThisJoinPoint = false;
		needsThisEnclosingJoinPointStaticPart = false;
		needsThisEnclosingJoinPointStaticPart = false;
		adviceDec.setParameters(
			(JFormalParameter[]) adviceParameters.toArray(
				new JFormalParameter[0])
		);
    }   

    // ... inspect in the body of the adivice only joinpoint reflection
    public boolean visit(JNameExpression expr) {
        
		if (expr.getName().equals(THIS_JOIN_POINT)) {
			needsThisJoinPoint = true;
		} 
		else if (expr.getName().equals(THIS_JOIN_POINT_STATIC_PART)) {
			needsThisJoinPointStaticPart = true;
		}
		else if (expr.getName().equals(THIS_ENCLOSING_JOIN_POINT_STATIC_PART)) {	
			needsThisEnclosingJoinPointStaticPart = true;
		}
		
        return true;
    }
}