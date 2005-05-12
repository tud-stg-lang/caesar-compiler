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
 * $Id: JBlock.java,v 1.5 2005-05-12 10:38:34 meffert Exp $
 */

package org.caesarj.compiler.ast.phylum.statement;

import org.caesarj.compiler.ast.CLineError;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * JLS 14.2: Block
 *
 * TA block is a sequence of statements and local variable declaration
 * statements within braces.
 */
public class JBlock extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	body		the statements contained in the block
   * @param	comment		other comments in the source code
   */
  public JBlock(TokenReference where,
		JStatement[] body,
		JavaStyleComment[] comments)
  {
    super(where, comments);

    this.body = body;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Tests whether the block is empty.
   *
   * @return	true iff the block is empty.
   */
  public boolean isEmpty() {
    return body.length == 0;
  }

  public JStatement[] getBody() {
    return body;
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
    // if change: don't forget KopiImplicitReturnBlock
    CBlockContext	self = new CBlockContext(context, context.getEnvironment());

    for (int i = 0; i < body.length; i++) {
      if (! self.isReachable()) {
	throw new CLineError(body[i].getTokenReference(), KjcMessages.STATEMENT_UNREACHABLE);
      }
      try {
	body[i].analyse(self);
      } catch (CLineError e) {
	self.reportTrouble(e);
      }
    }

    self.close(getTokenReference());
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void recurse(IVisitor s) {
    for (int i = 0; i < body.length; i++) {
        body[i].accept(s);
    }
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);
    
    code.openNewScope();
    for (int i = 0; i < body.length; i++) {
      body[i].genCode(context);
    }
    code.closeScope();
  }
  
  public String toString() {
    return "block";
  }

 // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected final JStatement[]		body;
}
