package org.caesarj.compiler.family;

import java.util.Stack;

import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.ast.phylum.expression.JAssignmentExpression;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JFieldAccessExpression;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.ast.visitor.VisitorSupport;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.export.CClass;
import org.caesarj.util.InconsistencyException;

/**
 * calculates paths for variable definitions
 * 
 * @author Ivica Aracic
 */
public class FamilyCheckVisitor implements IVisitor, CaesarConstants  {

	private VisitorSupport visitor = new VisitorSupport(this);
	
	private Stack pos = new Stack(); /** position in ast */	
	
	JTypeDeclaration getCurrentClassDeclaration() {
	    return (JTypeDeclaration)pos.peek();
	}

	CClass getCurrentCClass() {
	    return getCurrentClassDeclaration().getCClass();
	}
	
	public boolean start(JPhylum node) {
        return visitor.start(node);
    }

	public void end() {
	    visitor.end();
    }	
	
	public boolean visit(JPhylum self) {
	    return true;
    }   

	public boolean visit(JTypeDeclaration self) {
	    pos.push(self);
	    return true;
	}
	
	public void endVisit(JTypeDeclaration self) {
	    pos.pop();
	}
		
    public boolean visit(JAssignmentExpression self) {
        JExpression left = self.getLeft();
        JExpression right = self.getRight();
        
        // both are dependent types or both are not dependent types
        if(
            // CRITICAL typefactory may not be null
            left.getType(null).isDependentType()!=right.getType(null).isDependentType()
        ) {
            System.err.println("~~~ can not mix dependent and non-dependent types, line "+self.getTokenReference().getLine());
            throw new InconsistencyException();
        }
        
        // if one of them is dependent type then check family
        if(left.getType(null).isDependentType()) {            
            System.out.println("check family at line "+self.getTokenReference().getLine());
            
            CClass contextClass = getCurrentCClass();
            
            Path leftExpr = 
                Path.createFrom(contextClass, (JFieldAccessExpression)left);

            Path rightExpr = 
                Path.createFrom(contextClass, (JFieldAccessExpression)right);
            
            StaticObject leftSO = leftExpr.type(contextClass);
            
            StaticObject rightSO = rightExpr.type(contextClass);
            
            System.out.println("~~~ leftSO: "+leftSO+"     rightSO: "+rightSO);
            
            if(!leftSO.hasSameFamiliy(rightSO)) {
                System.out.println("!!!incompatible families at line "+self.getTokenReference().getLine());
                System.exit(1);
            }
        }
            
        return false;
	}

}