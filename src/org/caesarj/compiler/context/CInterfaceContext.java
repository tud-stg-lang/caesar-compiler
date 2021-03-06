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
 * $Id: CInterfaceContext.java,v 1.3 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.context;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.export.CSourceClass;
import org.caesarj.util.UnpositionedError;

/**
 * This class represents an interface context during check
 */
public class CInterfaceContext extends CClassContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * @param	parent		the parent context or null at top level
   * @param	clazz		the corresponding clazz
   */
  public CInterfaceContext(CContext parent, KjcEnvironment environment, CSourceClass clazz, JTypeDeclaration decl) {
    super(parent, environment, clazz, decl);
  }

  /**
   * Verify all final fields are initialized
   * @exception	UnpositionedError	this error will be positioned soon
   */
/* Andreas start
  public void close(JTypeDeclaration decl, CBodyContext virtual) throws UnpositionedError {
*/
  public void close(JTypeDeclaration decl, CBodyContext _virtual) throws UnpositionedError {
// Andreas end
  }
  /**
   * Verify all final fields are initialized
   * @exception UnpositionedError	this error will be positioned soon
   */
  public void close(JTypeDeclaration decl,
		    CVariableInfo staticC,
		    CVariableInfo instanceC,
		    CVariableInfo[] constructorsC)
    throws UnpositionedError
  {
    // getAbstractMethods test the consistence of abstract method
    self.testInheritMethods(this);
  }
}
