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
 * $Id: CTypeVariableAlias.java,v 1.1 2004-02-08 16:47:47 ostermann Exp $
 */

package org.caesarj.compiler.types;

import org.caesarj.compiler.KjcMessages;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.util.UnpositionedError;

public class CTypeVariableAlias extends CTypeVariable {

  public CTypeVariableAlias(String name) {
    super(name, null);
  }

  // ----------------------------------------------------------------------
  // BODY CHECKING
  // ----------------------------------------------------------------------

  /**
   * @param	context		the context (may be be null)
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CType checkType(CTypeContext context) throws UnpositionedError {
    CTypeVariable       typeVariable = context.lookupTypeVariable(getIdent());

    if (typeVariable != null) {
      return typeVariable.checkType(context);
    } else {
      throw new UnpositionedError(KjcMessages.TYPEVAR_NOT_FOUND, getIdent());
    }
  }
}
