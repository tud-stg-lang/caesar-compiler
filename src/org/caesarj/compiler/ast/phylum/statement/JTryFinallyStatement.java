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
 * $Id: JTryFinallyStatement.java,v 1.3 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.statement;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.phylum.variable.JGeneratedLocalVariable;
import org.caesarj.compiler.ast.phylum.variable.JLocalVariable;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.codegen.CodeLabel;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.Constants;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.CSimpleBodyContext;
import org.caesarj.compiler.context.CTryFinallyContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * JLS 14.19: Try Statement
 *
 * A try statement executes a block.
 * If a value is thrown and the try statement has one or more catch
 * clauses that can catch it, then control will be transferred to the
 * first such catch clause.
 * If the try statement has a finally clause, then another block of code
 * is executed, no matter whether the try block completes normally or abruptly,
 * and no matter whether a catch clause is first given control.
 *
 * In this implementation, the Try Statement is split into a Try-Catch Statement
 * and a Try-Finally Statement. A Try Statement where both catch and finally
 * clauses are present is rewritten as a Try-Catch Statement enclosed in a
 * Try-Finally Statement.
 */
public class JTryFinallyStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where			the line of this node in the source code
   * @param	tryClause		the body
   * @param	finallyClause		the finally clause
   * @param	comments		comments in the source text
   */
  public JTryFinallyStatement(TokenReference where,
			      JBlock tryClause,
			      JBlock finallyClause,
			      JavaStyleComment[] comments)
  {
    super(where, comments);

    this.tryClause = tryClause;
    this.finallyClause = finallyClause;
    this.finallyLabel = new CodeLabel();
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
    CBlockContext	self = new CBlockContext(context, context.getEnvironment()){
        public void addMonitorVariable(JLocalVariable var)  throws UnpositionedError {
          addVariable(var);
        }
      };
    CType               integerType = context.getTypeFactory().getPrimitiveType(TypeFactory.PRM_INT);

    /*
     * Create synthetic variables for try-finally administration.
     */
    exceptionVar = addSyntheticVariable(self,
					"try_finally_excp$",
					integerType);
    addressVar = addSyntheticVariable(self,
				      "try_finally_addr$",
				      integerType);
    returnVar = addSyntheticVariable(self,
				     "try_finally_rtvl$",
				     context.getMethodContext().getCMethod().getReturnType());
     
    CTryFinallyContext		tryContext;
    CBodyContext	finallyContext;

    tryContext = new CTryFinallyContext(self, context.getEnvironment());
    tryClause.analyse(tryContext);

    finallyContext = new CSimpleBodyContext(self, context.getEnvironment(), self);
    finallyContext.mergeVariableInfos(tryContext);
    finallyContext.mergeFieldInfos(tryContext);
    finallyContext.setReachable(true);
    finallyClause.analyse(finallyContext);

    if (!finallyContext.isReachable()) {
      self.setReachable(false);
      self.adoptThrowables(finallyContext);
    } else {
      tryContext.forwardBreaksAndContinues();
      self.setReachable(tryContext.isReachable());
      self.mergeThrowables(tryContext);
      self.mergeThrowables(finallyContext);
      self.adoptVariableInfos(finallyContext);
      self.adoptFieldInfos(finallyContext);
    }

    context.adoptVariableInfos(tryContext);
    context.adoptFieldInfos(tryContext);
    context.completeVariableInfos(self);
    context.completeFieldInfos(self);
    context.setReachable(self.isReachable());
  }

  /**
   * Adds a synthetic local variable.
   */
  private JLocalVariable addSyntheticVariable(CBodyContext context,
					      String prefix,
					      CType type)
    throws PositionedError
  {
    if (type.getTypeID() == TID_VOID) {
      // No need to create a variable, i.e. to hold a return value during
      // execution of the finally clause if the return type of the
      // enclosing method is void.
      return null;
    } else {
      JLocalVariable	var;

      var = new JGeneratedLocalVariable(null,
					0,
					type,
					prefix + toString() /* unique ID */,
					null);
      try {
	context.getBlockContext().addVariable(var);
      } catch (UnpositionedError e) {
	throw e.addPosition(getTokenReference());
      }
      return var;
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
    tryClause.accept(s);
    finallyClause.accept(s);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    CodeLabel		nextLabel = new CodeLabel();

    int		startPC = code.getPC();
    code.pushContext(this);
    tryClause.genCode(context);			//		TRY CODE
    code.popContext(this);
    int		endPC = code.getPC();
    code.plantJumpInstruction(opc_jsr, finallyLabel);	//		JSR finally
    code.plantJumpInstruction(opc_goto, nextLabel);	//		GOTO next

    // HANDLER FOR OTHER EXCEPTIONS
    int	finallyPC = code.getPC();
    code.plantLocalVar(opc_astore, exceptionVar);
    code.plantJumpInstruction(opc_jsr, finallyLabel);	//		JSR finally
    code.plantLocalVar(opc_aload, exceptionVar);
    code.plantNoArgInstruction(opc_athrow);

    // FINALLY CODE
    code.plantLabel(finallyLabel);		//	finally:...
    code.plantLocalVar(opc_astore, addressVar);
    finallyClause.genCode(context);			//		FINALLY CLAUSE

    code.plantLocalVar(opc_ret, addressVar);
    code.addExceptionHandler(startPC, endPC, finallyPC, null);
    code.plantLabel(nextLabel);			//	next:	...

    finallyLabel = null;
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genFinallyCall(GenerationContext context, JReturnStatement ret) {
    CodeSequence        code = context.getCodeSequence();
    TypeFactory         factory = context.getTypeFactory();

    if (ret != null && ret.getType(factory).getSize() > 0) {
      ret.store(context, returnVar);
      code.plantJumpInstruction(Constants.opc_jsr, finallyLabel);
      ret.load(context, returnVar);
    } else {
      code.plantJumpInstruction(Constants.opc_jsr, finallyLabel);
    }
  }
  
  public JBlock getTryCaluse() {return tryClause;}
  public JBlock getFinallyCaluse() {return finallyClause;}

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CodeLabel		finallyLabel;
  private JBlock		tryClause;
  private JBlock		finallyClause;

  /**
   * A synthetic local variable to store an exception that might be raised
   * by the execution try clause; after execution of the finally clause
   * the exception must be rethrown.
   */
  private JLocalVariable	exceptionVar;

  /**
   * A synthetic local variable to hold the return address for
   * the jsr to the finally clause.
   */
  private JLocalVariable	addressVar;

  /**
   * A synthetic local variable to hold the return value during the execution
   * of the finally clause if a return statement is enclosed in the try clause.
   */
  private JLocalVariable	returnVar;
}
