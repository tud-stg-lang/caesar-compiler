/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
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
 * $Id: JForStatement.java,v 1.1 2003-07-05 18:29:38 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;

/**
 * JLS 14.11: While Statement
 *
 * The while statement executes an expression and a statement repeatedly
 * until the value of the expression is false.
 */
public class JForStatement extends JLoopStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	init		the init part
   * @param	cond		the cond part
   * @param	incr		the increment part
   * @param	body		the loop body.
   */
  public JForStatement(TokenReference where,
		       JStatement init,
		       JExpression cond,
		       JStatement incr,
		       JStatement body,
		       JavaStyleComment[] comments)
  {
    super(where, comments);

    this.init = init;
    this.cond = cond;
    this.incr = incr;
    this.body = body;
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
    try {
      CBlockContext bodyContext = new CBlockContext(context, 
                                                    context.getEnvironment(),
                                                    init instanceof JVariableDeclarationStatement ?
                                                    ((JVariableDeclarationStatement)init).getVars().length :
                                                    0);
      TypeFactory       factory = context.getTypeFactory();

      if (init != null) {
	init.analyse(bodyContext);
      }

      if (cond != null) {
	cond = cond.analyse(new CExpressionContext(bodyContext, context.getEnvironment()));
	check(bodyContext,
	      cond.getType(factory) == factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN),
	      KjcMessages.FOR_COND_NOTBOOLEAN, cond.getType(factory));
	if (cond.isConstant()) {
	  // JLS 14.20 Unreachable Statements :
	  // The contained statement [of a for statement] is reachable iff the
	  // for statement is reachable and the condition expression is not a constant
	  // expression whose value is false.
	  check(context, cond.booleanValue(), KjcMessages.STATEMENT_UNREACHABLE);

	  // for (A; true; B) equivalent to for (A; ; B)
	  cond = null;
	}
      }

      CBodyContext	neverContext = cond == null ? null : bodyContext.cloneContext();

      CLoopContext	loopContext = new CLoopContext(bodyContext, 
                                                       context.getEnvironment(), 
                                                       this);

      if (init instanceof JVariableDeclarationStatement) {
	((JVariableDeclarationStatement)init).setIsInFor();
      }
      body.analyse(loopContext);
      if (init instanceof JVariableDeclarationStatement) {
	((JVariableDeclarationStatement)init).unsetIsInFor();
      }

      if (neverContext == null && loopContext.isBreakTarget()) {
	loopContext.adopt(loopContext.getBreakContextSummary());
      }

      loopContext.close(getTokenReference());

      if (incr != null) {
	incr.analyse(bodyContext);
      }

      bodyContext.close(getTokenReference());

      if (neverContext != null) {
	context.merge(neverContext);
      } else if (!loopContext.isBreakTarget()) {
	context.setReachable(false);
      }
    } catch (CBlockError e) {
      context.reportTrouble(e);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitForStatement(this, init, cond, incr, body);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    CodeLabel		startLabel = new CodeLabel();
    CodeLabel		condLabel = new CodeLabel();

    code.pushContext(this);
    if (init != null) {
      init.genCode(context);			//		INIT
    }
    code.plantJumpInstruction(opc_goto, condLabel);	//		GOTO cond
    code.plantLabel(startLabel);		//	start:
    if (body != null) {
      body.genCode(context);			//		BODY
    }
    code.plantLabel(getContinueLabel());		//	incr:
    if (incr != null) {
      incr.genCode(context);			//		INCR
    }
    code.plantLabel(condLabel);			//	cond:
    if (cond != null) {
      cond.genBranch(true, context, startLabel);	//		COND IFNE start
    } else {
      code.plantJumpInstruction(opc_goto, startLabel);	//		GOTO start
    }
    code.plantLabel(getBreakLabel());			//	end:

    code.popContext(this);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JStatement		init;
  private JExpression		cond;
  private JStatement		incr;
  private JStatement		body;
}
