package org.caesarj.compiler.typesys;

import java.util.Iterator;
import java.util.Stack;

import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.JFieldDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JVariableDeclarationStatement;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.ast.visitor.VisitorSupport;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.types.CDependentType;
import org.caesarj.compiler.types.CType;

/**
 * calculates paths for variable definitions
 * 
 * @author Ivica Aracic
 */
public class VariablePathGenerationVisitor implements IVisitor, CaesarConstants  {

	private VisitorSupport visitor = new VisitorSupport(this);
	
	private Stack pos = new Stack(); /** position in ast */
	
	private void printPos() {	  
	    System.out.print("  path = ");
	    
	    for (Iterator it = pos.iterator(); it.hasNext();) {
            JPhylum item = (JPhylum) it.next();
            System.out.print(item);
            if(it.hasNext()) System.out.print(" -> ");
        }
	    
	    if(pos.size() > 0)
	        System.out.println();
	    else 
	        System.out.println("no path");
	}
	
	public JPhylum[] makePos() {
	    return (JPhylum[])pos.toArray(new JPhylum[pos.size()]);
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
	
	public boolean visit(JBlock self) {
	    pos.push(self);
	    return true;
	}
	
	public void endVisit(JMethodDeclaration self) {
	    pos.pop();
	}
	
	public boolean visit(JMethodDeclaration self) {
	    pos.push(self);
	    return true;
	}
	
	public void endVisit(JBlock self) {
	    pos.pop();
	}
		
    public boolean visit(JVariableDeclarationStatement self) {
        JVariableDefinition vars[] = self.getVariables();
        for (int i = 0; i < vars.length; i++) {
            CType type = vars[i].getType();            
            if(type instanceof CDependentType) {
                CDependentType dependType = (CDependentType)type;
                dependType.setPos( makePos() );
            }
            System.out.print("~~~ local var: "+vars[i].getIdent());
            printPos();
        }
        return false;
	}

    public boolean visit(JFieldDeclaration self) {
        System.out.print("~~~ field: "+self.getVariable().getIdent());
        printPos();
        return false;
	}

}