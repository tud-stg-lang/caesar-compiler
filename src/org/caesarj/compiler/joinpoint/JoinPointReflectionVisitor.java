package org.caesarj.compiler.joinpoint;

import java.util.ArrayList;
import java.util.List;

import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.CjAdviceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JFieldDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMemberDeclaration;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JMethodCallExpression;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.expression.JTypeNameExpression;
import org.caesarj.compiler.ast.phylum.statement.JClassBlock;
import org.caesarj.compiler.ast.phylum.statement.JExpressionStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.ast.visitor.VisitorSupport;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.util.TokenReference;

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
        for (int i = 0; i < self.getBody().length; i++) {
    		if (self.getBody()[i] instanceof JFieldDeclaration) {
    			JFieldDeclaration field = (JFieldDeclaration) self.getBody()[i];
    			if ((field.getVariable().getModifiers() & ACC_DEPLOYED) != 0) {
    				((CjClassDeclaration) self).addClassBlock(
    					createStaticDeployBlock(
    						field.getTokenReference(),
    						(CjClassDeclaration) self,
    						field));
    			}
    		}
    	}	        
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
					TokenReference.NO_REF,
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
					TokenReference.NO_REF,
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
					TokenReference.NO_REF,
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


	/*
	 * Creates static initalization block:
	 * 
	 * {
	 *    <field>.$deploySelf(java.lang.Thread.currentThread());
	 * }
	 * 
	 */
	private JClassBlock createStaticDeployBlock(
		TokenReference where,
		CjClassDeclaration classDeclaration,
		JFieldDeclaration fieldDeclaration) {

		JExpression fieldExpr =
			new JNameExpression(
				where,
				null,
				fieldDeclaration.getVariable().getIdent());

		JExpression threadPrefix =
			new JTypeNameExpression(
				where,
				new CClassNameType(QUALIFIED_THREAD_CLASS));

		JExpression[] args =
			{
				new JMethodCallExpression(
					where,
					threadPrefix,
					"currentThread",
					JExpression.EMPTY)};

		JExpression expr =
			new JMethodCallExpression(where, fieldExpr, DEPLOY_SELF_METHOD, args);

		JStatement[] body = { new JExpressionStatement(where, expr, null)};

		return new JClassBlock(where, true, body);
	}

}