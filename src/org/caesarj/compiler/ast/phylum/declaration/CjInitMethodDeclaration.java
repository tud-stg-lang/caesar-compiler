/*
 * Created on 17.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.ast.phylum.expression.JThisExpression;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JReturnStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.TokenReference;

/**
 * @author vaidas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CjInitMethodDeclaration extends JMethodDeclaration {
	
	/**
	   * Construct a node in the parsing tree
	   * This method is directly called by the parser
	   * @param	where		the line of this node in the source code
	   * @param	parent		parent in which this methodclass is built
	   * @param	modifiers	list of modifiers
	   * @param	ident		the name of this method
	   * @param	parameters	the parameters of this method
	   * @param	exceptions	the exceptions throw by this method
	   * @param	body		the body of the method
	   */
	  public CjInitMethodDeclaration(TokenReference where,
					 int modifiers,
					 CReferenceType returnType,
					 String ident,
					 JFormalParameter[] parameters,
					 CReferenceType[] exceptions,
					 JBlock body)
	  {
	  	super(where,
	  			modifiers,
	  			returnType,
				ident,
				parameters,
				exceptions,
				body,
				null,
				null);
	  	
        this.body = new JBlock(
                where,
	            new JStatement[] {
	                    body,
						new JReturnStatement(where, new JThisExpression(where), null)
	            }, 
				null);        
	  }
}
