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
 * $Id: KopiMethodCallExpression.java,v 1.1 2003-07-05 18:29:39 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.compiler.CWarning;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;

public class KopiMethodCallExpression extends JMethodCallExpression {
  /**
   * Construct a node in the parsing tree This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	prefix		an expression that is a field of a class representing a method
   * @param	ident		the method ident
   * @param	args		the argument of the call       
   */
  public KopiMethodCallExpression(TokenReference where, CMethod method, JExpression[] args) { 
    this(where, null, method, args);
  }
  /**
   * Construct a node in the parsing tree This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	prefix		an expression that is a field of a class representing a method
   * @param	ident		the method ident
   * @param	args		the argument of the call       
   */
  public KopiMethodCallExpression(TokenReference where, JExpression prefix, CMethod method, JExpression[] args) { 
    super(where, prefix, method.getIdent(), args);
    this.method = method;
    verify(method.getReturnType().getTypeID() == TID_VOID);
    verify(method != null);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------
  protected void findMethod(CExpressionContext context, CClass local, CType[] argTypes) throws PositionedError  {
    // method already known    
    if (prefix != null) {
      // evaluate the prefix in rhs mode, result will be used
      prefix = prefix.analyse(new CExpressionContext(context, context.getEnvironment()));
    }

  }

}
