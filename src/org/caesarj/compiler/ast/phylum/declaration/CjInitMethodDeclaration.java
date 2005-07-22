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
import org.caesarj.compiler.ast.phylum.statement.JConstructorBlock;
import org.caesarj.compiler.ast.phylum.statement.JExpressionStatement;
import org.caesarj.compiler.ast.phylum.statement.JReturnStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CConstructorContext;
import org.caesarj.compiler.context.CMethodContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.PositionedError;
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
					 JConstructorBlock body)
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
	  	
  		JConstructorCall constrCall = body.getConstructorCall();
  		if (constrCall != null) {
  			JExpression target =  null;
  			if (constrCall.isFunctorThis()) {
  				target = new JThisExpression(where);
  			}
  			else {
  				target = new JSuperExpression(where);
  			}
  			superInit = new CjMethodCallExpression(where,
  					target,
  					CONSTR_METH_NAME, 
  					constrCall.getArguments());
  		}
	  	
	  	block.add(0, new JExpressionStatement(where, superInit, null));
	  	block.add(new JReturnStatement(where, new JThisExpression(where), null));
	  	
        this.body = new JBlock(
                where,
                (JStatement[])block.toArray(new JStatement[] {}),
				null);        
	  }
	  
	  /**
	   *	Construct default constructor 
	   */
	  public CjInitMethodDeclaration(TokenReference where,
			 int modifiers,
			 CReferenceType ownerType,
			 String ident,
			 JFormalParameter[] parameters,
			 CReferenceType[] exceptions)
		{
			super(where,
					modifiers,
					new CClassNameType(CAESAR_OBJECT),
				ident,
				parameters,
				exceptions,
				new JBlock(
						where,
						new JStatement[] {
							new JExpressionStatement(where, 
									new CjMethodCallExpression(where,
											new JSuperExpression(where),
											CONSTR_METH_NAME, 
											JExpression.EMPTY),
									null),									
							new JReturnStatement(where, new JThisExpression(where), null)	
						},					    
						null),
				null,
				null);			        
		}
	  
	  /**
	   * Check expression and evaluate and alter context
	   * @param	context			the actual context of analyse
	   * @return	a pure java expression including promote node
	   * @exception	PositionedError Error catched as soon as possible
	   */
	  public void checkBody1(CClassContext context) throws PositionedError {
	    check(context, body != null, KjcMessages.CONSTRUCTOR_NOBODY, ident);

	    CMethodContext	self = new CConstructorContext(context, 
	                                                       context.getEnvironment(), 
	                                                       this);
	    CBlockContext	block = new CBlockContext(self, 
	                                                  context.getEnvironment(), 
	                                                  parameters.length);
	    CClass              owner = context.getClassContext().getCClass();

	    block.addThisVariable();
	    if (owner.isNested() && owner.hasOuterThis()) {
	      block.addThisVariable(); // add enclosing this$0
	    }
	    for (int i = 0; i < parameters.length; i++) {
	      parameters[i].analyse(block);
	    }
	    body.analyse(block);

	    block.close(getTokenReference());
	    self.close(getTokenReference());
	  }
}
