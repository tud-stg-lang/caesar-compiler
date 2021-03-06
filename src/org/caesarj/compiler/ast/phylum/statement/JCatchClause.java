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
 * $Id: JCatchClause.java,v 1.3 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.statement;

import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.CWarning;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * This class represents a parameter declaration in the syntax tree
 */
public class JCatchClause extends JPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	exception	the exception caught
   * @param	body		the body of the exception handler
   */
  public JCatchClause(TokenReference where,
		      JFormalParameter exception,
		      JBlock body)
  {
    super(where);

    this.exception = exception;
    this.body = body;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * getType
   * @return	the type of exception catched by this clause
   */
  public CReferenceType getType() {
    return (CReferenceType)exception.getType();
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the node (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CBodyContext context) throws PositionedError {
    CBlockContext	block = new CBlockContext(context, context.getEnvironment(), 1);

    exception.analyse(block);
    block.setReachable(true);
    //    exception.analyse(block);
    //!!! Throwable !!!
    body.analyse(block);

    block.close(getTokenReference());

    if (body.isEmpty()) {
      context.reportTrouble(new CWarning(getTokenReference(),
					 KjcMessages.EMPTY_CATCH_BLOCK));
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
    body.accept(s);
  }

  /**
   * Generates bytecode for the exception handler.
   *
   * @param	code		the code sequence
   * @param	start		the beginning of the checked area (inclusive)
   * @param	end		the end of the checked area (exclusive !)
   */
  public void genCode(GenerationContext context, int start, int end) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    int		catchPC;

    catchPC = code.getPC();
    exception.genStore(context);
    body.genCode(context);

    code.addExceptionHandler(start,
			     end,
			     catchPC,
			     exception.getType().getCClass().getQualifiedName());
  }

  public JFormalParameter getExceptionParameter() {return exception;}
  public JBlock getBody() {return body;}
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JFormalParameter	exception;
  private JBlock		body;
}
