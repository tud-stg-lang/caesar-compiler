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
 * $Id: GenerationContext.java,v 1.3 2005-11-02 15:46:07 gasiunas Exp $
 */

package org.caesarj.compiler.context;

import java.lang.ref.WeakReference;

import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.types.TypeFactory;

public class GenerationContext {
  
  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a GenerationContext that will be passed
   * as a parameter of the genCode methods
   * @param	factory         the TypeFactory
   * @param	sequence        the CodeSequence
   */
  public GenerationContext(TypeFactory factory, CodeSequence sequence) {
    this.factory        = new WeakReference<TypeFactory>(factory);
    this.sequence       = sequence;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Return the TypeFactory member of this
   */
  public TypeFactory getTypeFactory() {
    return factory.get();
  }

  /**
   * Return the CodeSequence member of this
   */
  public CodeSequence getCodeSequence() {
    return sequence;
  }
  

  // ----------------------------------------------------------------------
  // MEMBERS
  // ----------------------------------------------------------------------
  
  private WeakReference<TypeFactory>   factory;
  private CodeSequence  sequence;
  
}
