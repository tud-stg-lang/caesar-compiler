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
 * $Id: KopiImplicitReturnBlock.java,v 1.1 2003-07-05 18:29:38 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;

public class KopiImplicitReturnBlock extends JBlock {
  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   */
  public KopiImplicitReturnBlock(TokenReference where, JStatement[] body, JavaStyleComment[] comments) {
    super(where, body, comments);
  }

//   public JStatement[] getBody() {
//     return body;
//   }
  // ----------------------------------------------------------------------
  // CODE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Check statement.
   * @param	context		the actual context of analyse
   * @exception	PositionedError		if the check fails
   */
  public void analyse(CBodyContext context) throws PositionedError {
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

    TokenReference      ref = getTokenReference();

    if (self.isReachable()) {
      implicitReturn = new KopiReturnStatement(ref,null,null);
      try {
        implicitReturn.analyse(self);
      } catch (CLineError le) {
        context.reportTrouble(le);
      }
    }

    self.close(ref);
  }
  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitBlockStatement(this, body, getComments());
    if (implicitReturn != null) {
      implicitReturn.accept(p);
    }
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    code.setLineNumber(getTokenReference().getLine());
    for (int i = 0; i < body.length; i++) {
      body[i].genCode(context);
    }
    if (implicitReturn != null) {
      implicitReturn.genCode(context);
    }
  }


  // ----------------------------------------------------------------------
  // PRIVATE DATA MEMBERS
  // ----------------------------------------------------------------------
  protected JStatement          implicitReturn;
}
