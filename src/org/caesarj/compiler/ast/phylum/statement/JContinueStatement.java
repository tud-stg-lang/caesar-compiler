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
 * $Id: JContinueStatement.java,v 1.3 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.statement;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * JLS 14.15: Continue Statement
 *
 * A continue statement may occur only in a while, do, or for statement;
 * statements of these three kinds are called iteration statements.
 * Control passes to the loop-continuation point of an iteration statement.
 */
public class JContinueStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	label		the label of the enclosing labeled statement
   * @param	comments	comments in the source text
   */
  public JContinueStatement(TokenReference where,
			    String label,
			    JavaStyleComment[] comments)
  {
    super(where, comments);
    this.label = label;
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
    if (label != null) {
      target = context.getLabeledStatement(label);
      check(context, target != null, KjcMessages.LABEL_UNKNOWN, label);
      // JLS 14.15 :
      // The continue target must be a while, do, or for statement.
      check(context,
	    target instanceof JLoopStatement,
	    KjcMessages.CONTINUE_NOTLOOP);
    } else {
      target = context.getNearestContinuableStatement();
      check(context, target != null, KjcMessages.CANNOT_CONTINUE);
    }

    context.addContinue(target);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    code.plantBreak(target, context);
    code.plantJumpInstruction(opc_goto, target.getContinueLabel());

    target = null;
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final String		label;
  private JStatement		target;
}
