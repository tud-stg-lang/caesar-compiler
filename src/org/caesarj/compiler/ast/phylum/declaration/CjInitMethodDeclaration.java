/*
 * Created on 17.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.caesarj.compiler.ast.phylum.declaration;

import java.util.Vector;

import org.caesarj.compiler.ast.phylum.expression.CjMethodCallExpression;
import org.caesarj.compiler.ast.phylum.expression.JConstructorCall;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JSuperExpression;
import org.caesarj.compiler.ast.phylum.expression.JThisExpression;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JExpressionStatement;
import org.caesarj.compiler.ast.phylum.statement.JReturnStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.TokenReference;
import org.caesarj.util.Utils;

/**
 * @author vaidas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CjInitMethodDeclaration extends JMethodDeclaration implements CaesarConstants {
	
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
					 CReferenceType ownerType,
					 String ident,
					 JFormalParameter[] parameters,
					 CReferenceType[] exceptions,
					 JBlock body)
	  {
	  	super(where,
	  			modifiers,
	  			new CClassNameType(CAESAR_OBJECT),
				ident,
				parameters,
				exceptions,
				body,
				null,
				null);
	  	
	  	CjMethodCallExpression superInit = new CjMethodCallExpression(where,
	  				new JSuperExpression(where),
  					CONSTR_METH_NAME, 
  					JExpression.EMPTY);
	  	
	  	Vector block = Utils.toVector(body.getBody());
	  	
	  	if (block.size() > 0 && block.get(0) instanceof JExpressionStatement) {
	  		JExpression expr = ((JExpressionStatement)block.get(0)).getExpression();
	  		if (expr instanceof JConstructorCall) {
	  			JExpression target =  null;
	  			if (((JConstructorCall)expr).isFunctorThis()) {
	  				target = new JThisExpression(where);
	  			}
	  			else {
	  				target = new JSuperExpression(where);
	  			}
	  			superInit = new CjMethodCallExpression(where,
	  					target,
	  					CONSTR_METH_NAME, 
	  					((JConstructorCall)expr).getArguments());
	  			block.remove(0);
	  		}	  		
	  	}
	  	
	  	block.add(0, new JExpressionStatement(where, superInit, null));
	  	block.add(new JReturnStatement(where, new JThisExpression(where), null));
	  	
        this.body = new JBlock(
                where,
                (JStatement[])block.toArray(new JStatement[] {}),
				null);        
	  }
}
