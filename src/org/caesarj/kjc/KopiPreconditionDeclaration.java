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
 * $Id: KopiPreconditionDeclaration.java,v 1.1 2003-07-05 18:29:40 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;

public class KopiPreconditionDeclaration extends JMethodDeclaration {

 /**
   * Construct a node in the parsing tree This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	parent		parent in which this methodclass is built
   * @param	modifiers	list of modifiers
   * @param	returnType	the return type of this method
   * @param	ident		the name of this method
   * @param	parameters	the parameters of this method
   * @param	exceptions	the exceptions throw by this method
   * @param	body		the body of this method
   * @param	javadoc		is this method deprecated
   */
  public KopiPreconditionDeclaration(TokenReference where, 
                                     int modifiers, 
                                     CTypeVariable[] typeVariables,
                                     CType realRetType, 
                                     String ident, 
                                     JFormalParameter[] parameters,
                                     CReferenceType[] exceptions, 
                                     JBlock body, 
                                     JavadocComment javadoc, 
                                     JavaStyleComment[] comments,
                                     TypeFactory factory) {
        super(where, 
              modifiers,
              typeVariables, 
              factory.getVoidType(), 
              (ident+IDENT_PRE).intern(), 
              parameters, 
              exceptions, 
              body, 
              javadoc, 
              comments);
  }

  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------
  /**
   * Second pass (quick), check interface looks good
   * Exceptions are not allowed here, this pass is just a tuning
   * pass in order to create informations about exported elements
   * such as Classes, Interfaces, Methods, Constructors and Fields
   * @return true iff sub tree is correct enough to check code
   * @exception	PositionedError	an error with reference to the source file
   */
  public CSourceMethod checkInterface(CClassContext context) throws PositionedError {
    CClass              local = context.getClassContext().getCClass();

    if (local.isAssertionClass()) {
      // in Class Xxxx$$Assertions
      String    className = local.getQualifiedName().substring(0,local.getQualifiedName().length()-IDENT_CLASS_ASSERT.length()).intern();
      CClass    clazz = context.getClassReader().loadClass(context.getTypeFactory(), className);

      if (clazz.isGenericClass()) {
        // The type variable of the interface are the type variables of this static method
        CTypeVariable[]         classTypeVariables = CTypeVariable.cloneArray(clazz.getTypeVariables());

        if (typeVariables.length == 0) {
          typeVariables = classTypeVariables;
        } else {
          CTypeVariable[]       tmp = new CTypeVariable[typeVariables.length+classTypeVariables.length];

          System.arraycopy(classTypeVariables, 0, tmp, 0, classTypeVariables.length);
          System.arraycopy(typeVariables, 0, tmp, classTypeVariables.length, typeVariables.length);

          typeVariables = tmp;
        }
      }
    }
    if  ((modifiers & (ACC_STATIC | ACC_PRIVATE)) == 0) {
      modifiers = (modifiers | (ACC_PROTECTED | ACC_PUBLIC)) ^ ACC_PUBLIC; // remove public, make protected
    }
    CSourceMethod       method = super.checkInterface(context);

    method.setSynthetic(true);
    method.setPrecondition(true);
    method.setUsed();
    return method;
  }

  // ----------------------------------------------------------------------
  // CODE CHECKING
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // PRIVATE DATA MEMBERS
  // ----------------------------------------------------------------------
}
 
