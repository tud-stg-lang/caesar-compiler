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
 * $Id: JSynchronizedStatement.java,v 1.3 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.statement;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.phylum.expression.*;
import org.caesarj.compiler.ast.phylum.variable.JGeneratedLocalVariable;
import org.caesarj.compiler.ast.phylum.variable.JLocalVariable;
import org.caesarj.compiler.ast.visitor.*;
import org.caesarj.compiler.codegen.CodeLabel;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * JLS 14.18: Synchronized Statement
 *
 * A synchronized statement acquires a mutual-exclusion lock on behalf
 * of the executing thread, executes a block, then releases the lock.
 */
public class JSynchronizedStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	cond		the expression to evaluate.
   * @param	body		the loop body.
   */
  public JSynchronizedStatement(TokenReference where,
				JExpression cond,
				JStatement body,
				JavaStyleComment[] comments)
  {
    super(where, comments);
    this.cond = cond;
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
    TypeFactory         factory = context.getTypeFactory();

    localVar = new JGeneratedLocalVariable(null,
					   0,
					   factory.getPrimitiveType(TypeFactory.PRM_INT),
					   "sync$" + toString() /* unique ID */,
					   null);
    catchVar = new JGeneratedLocalVariable(null,
					   0,
					   context.getTypeFactory().createReferenceType(TypeFactory.RFT_THROWABLE),
					   "sync$catch" + toString() /* unique ID */,
					   null);
    try {
      context.addMonitorVariable(localVar);
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }
    cond = cond.analyse(new CExpressionContext(context, context.getEnvironment()));
    check(context, cond.getType(factory).isReference(), KjcMessages.SYNCHRONIZED_NOTREFERENCE);
    check(context, cond.getType(factory) != factory.getNullType(), KjcMessages.SYNCHRONIZED_NOTREFERENCE);
    body.analyse(context);
    
    CBlockContext       blockcontext = new CBlockContext(context, context.getEnvironment());

    try {
      blockcontext.addVariable(catchVar);
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

	public void recurse(IVisitor s) {
	    cond.accept(s);
	    body.accept(s);
	}

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genMonitorExit(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    code.plantLocalVar(opc_aload, localVar);
    code.plantNoArgInstruction(opc_monitorexit);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    CodeLabel		nextLabel = new CodeLabel();

    cond.genCode(context, false);
    code.plantNoArgInstruction(opc_dup);
    code.plantLocalVar(opc_astore, localVar);
    code.plantNoArgInstruction(opc_monitorenter);

    code.pushContext(this);

    int		startPC = code.getPC();
    body.genCode(context);
    code.popContext(this);
    genMonitorExit(context);			//!!! CHECK : inside ?
    code.plantJumpInstruction(opc_goto, nextLabel);
    int		endPC = code.getPC();

    int         errorPC = code.getPC();
    catchVar.genStore(context);
    genMonitorExit(context);
    catchVar.genLoad(context);
    int         errorPCend = code.getPC();

    code.plantNoArgInstruction(opc_athrow);
    code.plantLabel(nextLabel);			//	next:	...

    // protect
    code.addExceptionHandler(startPC, endPC, errorPC, null);
    code.addExceptionHandler(errorPC, errorPCend, errorPC, null);
  }

  public JExpression getCondition() {return cond;}
  public JStatement getBody() {return body;}
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression		cond;
  private JStatement		body;
  private JLocalVariable	localVar;
  private JLocalVariable	catchVar;
}
