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
 * $Id: KopiReturnValueExpression.java,v 1.1 2003-07-05 18:29:39 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;

/**
 * Used in postconditions to access the return value of the method. 
 * It is the node for the operator "@@()".
 */
public class KopiReturnValueExpression extends JNameExpression{
  public KopiReturnValueExpression(TokenReference ref) {
    super(ref, IDENT_RETURN);
  }

  /**
   * Analyses the expression (semantically).
   * @param	context		the analysis context
   * @return	an equivalent, analysed expression
   * @exception	PositionedError	the analysis detected an error
   */
  public JExpression analyse(CExpressionContext context) throws PositionedError {
    check(context, context.getMethodContext().getCMethod().isPostcondition(), KjcMessages.WRONG_RETURN_VALUE);

    // is $return defined ? or is teh return type of the orginal method void?
    JFormalParameter[]  parameter = context.getMethodContext().getFormalParameter();
    boolean             hasReturnType = false;

    for (int i = 0; i < parameter.length && i < 3; i++) {
      if (parameter[i].getIdent() == IDENT_RETURN) {
        hasReturnType = true;
        break;
      }
    }
    check(context, hasReturnType, KjcMessages.RETURN_VALUE_WITHOUT);

    return super.analyse(context);
  }
}
