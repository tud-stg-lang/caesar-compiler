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
 * $Id: KopiAssertionClassDeclaration.java,v 1.1 2003-07-05 18:29:38 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;

public class KopiAssertionClassDeclaration extends JClassDeclaration {
  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	context		the context in which this class is defined
   * @param	modifiers	the list of modifiers of this class
   * @param	ident		the short name of this class
   * @param	superName	the name of super class of this class
   * @param	interfaces	the name of this class's interfaces
   * @param	javadoc	is this class deprecated
   */
  public KopiAssertionClassDeclaration(TokenReference where, 
                                       int modifiers, 
                                       String ident,          
                                       CTypeVariable[] typeVariables,
                                       JMethodDeclaration[] methods, 
                                       JavadocComment javadoc, 
                                       JavaStyleComment[] comment,
                                       TypeFactory factory) {
        super(where, 
              modifiers, 
              ident+IDENT_CLASS_ASSERT, 
              typeVariables,
              null,
              CReferenceType.EMPTY, 
              new JFieldDeclaration[0], 
              methods, 
              new JTypeDeclaration[0],  
              new JPhylum[0], 
              javadoc, 
              comment);
    //ctor without assertions!!
    setDefaultConstructor(new JConstructorDeclaration(getTokenReference(),
                                                      ACC_PROTECTED,
                                                      ident+IDENT_CLASS_ASSERT,
                                                      JFormalParameter.EMPTY,
                                                      CReferenceType.EMPTY,
                                                      new JConstructorBlock(getTokenReference(), null, new JStatement[0]),
                                                      null,
                                                      null,
                                                      factory));
  }

  public void generateInterface(ClassReader classReader, CClass owner, String prefix) {
    super.generateInterface(classReader, owner, prefix);
    ((CSourceClass)getCClass()).setAssertionClass(true);
  }

  public void checkInterface(final CContext context) throws PositionedError {
    if (getCClass().isNested()) {
      setModifiers(getModifiers() | ACC_STATIC);
    }
    super.checkInterface(context);
  }
}
