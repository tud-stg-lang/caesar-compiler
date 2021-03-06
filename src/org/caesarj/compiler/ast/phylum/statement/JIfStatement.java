/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
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
 * $Id: JIfStatement.java,v 1.6 2005-05-31 08:59:35 meffert Exp $
 */

package org.caesarj.compiler.ast.phylum.statement;

import org.caesarj.classfile.LocalVariableScope;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.phylum.expression.JAssignmentExpression;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.codegen.CodeLabel;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.CSimpleBodyContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.CWarning;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * JLS 14.9: If Statement
 *
 * The if statement executes an expression and a statement repeatedly
 * until the value of the expression is false.
 */
public class JIfStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	cond		the expression to evaluate
   * @param	thenClause	the statement to execute if the condition is true
   * @param	elseClause	the statement to execute if the condition is false
   */
  public JIfStatement(TokenReference where,
		      JExpression cond,
		      JStatement thenClause,
		      JStatement elseClause,
		      JavaStyleComment[] comments)
  {
    super(where, comments);

    this.cond = cond;
    this.thenClause = thenClause;
    this.elseClause = elseClause;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the statement (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CBodyContext context) throws PositionedError {
    TypeFactory         factory = context.getTypeFactory();

    cond = cond.analyse(new CExpressionContext(context, context.getEnvironment()));
    check(context,
	  cond.getType(factory) == factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN),
	  KjcMessages.IF_COND_NOTBOOLEAN, cond.getType(factory));
    if (cond instanceof JAssignmentExpression) {
      context.reportTrouble(new CWarning(getTokenReference(),
					 KjcMessages.ASSIGNMENT_IN_CONDITION));
    }

    CBodyContext	thenContext = new CSimpleBodyContext(context, 
                                                             context.getEnvironment(), 
                                                             context);

    thenClause.analyse(thenContext);

    if (elseClause == null) {
      context.merge(thenContext);
    } else {
      elseClause.analyse(context);
      if (thenContext.isReachable() && context.isReachable()) {
	context.merge(thenContext);
      } else if (thenContext.isReachable()) {
	context.adopt(thenContext);
      }
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void recurse(IVisitor s) {
    cond.accept(s);
    thenClause.accept(s);
    if(elseClause != null)
        elseClause.accept(s);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    if (cond.isConstant()) {
      if (cond.booleanValue()) {
        LocalVariableScope scope = new LocalVariableScope();
        code.pushLocalVariableScope(scope);
		thenClause.genCode(context);
		code.popLocalVariableScope(scope);
      } else if (elseClause != null) {
        LocalVariableScope scope = new LocalVariableScope();
        code.pushLocalVariableScope(scope);
		elseClause.genCode(context);
		code.popLocalVariableScope(scope);
      }
    } else {
      CodeLabel		elseLabel = new CodeLabel();
      CodeLabel		nextLabel = new CodeLabel();

      cond.genBranch(false, context, elseLabel);   //		COND IFEQ else
      LocalVariableScope scope = new LocalVariableScope();
      code.pushLocalVariableScope(scope);
      thenClause.genCode(context);			//		THEN CODE
      code.popLocalVariableScope(scope);
      code.plantJumpInstruction(opc_goto, nextLabel);	//		GOTO next
      code.plantLabel(elseLabel);		//	else:
      if (elseClause != null) {
        LocalVariableScope scope2 = new LocalVariableScope();
        code.pushLocalVariableScope(scope2);
		elseClause.genCode(context);		//		ELSE CODE
		code.popLocalVariableScope(scope2);
      }
      code.plantLabel(nextLabel);		//	next	...
    }
  }

  public JExpression getCondition() {return cond;}
  public void setThenClause(JStatement thenClause) {this.thenClause = thenClause;}
  public void setElseClause(JStatement elseClause) {this.elseClause = elseClause;}
  public JStatement getThenClause() {return thenClause;}
  public JStatement getElseClause() {return elseClause;}
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression		cond;
  private JStatement		thenClause;
  private JStatement		elseClause;
}
