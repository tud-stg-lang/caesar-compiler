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
 * $Id: KopiMethodPreconditionDeclaration.java,v 1.1 2003-07-05 18:29:41 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
 
public class KopiMethodPreconditionDeclaration extends KopiPreconditionDeclaration { 

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

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
  public KopiMethodPreconditionDeclaration(TokenReference where, 
                                           int modifiers, 
                                           CTypeVariable[] typeVariables,
                                           CType realRetType, 
                                           String ident, 
                                           JFormalParameter[] parameters,
                                           CReferenceType[] exceptions, 
                                           KopiPreconditionStatement body, 
                                           JavadocComment javadoc, 
                                           JavaStyleComment[] comments,
                                           TypeFactory factory
                                           //  FTypeVariable[] typeVariable
                                           ) {
        super(where, 
              modifiers, 
              typeVariables,
              realRetType, 
              ident, 
              parameters, 
              exceptions, 
              new JBlock(where,
                         new JStatement[] { body }, 
                         null),
              javadoc, 
              comments,
              factory);
        this.kopiBody = body;
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
    LanguageExtensions  extensions = context.getEnvironment().getLanguageExtFactory();
    CReferenceType      clazz = local.getAbstractType();

    if (local.isAssertionClass()) {
      try {
        clazz = context.lookupClass(local, local.getIdent().substring(0,local.getIdent().length()-IDENT_CLASS_ASSERT.length())).getAbstractType();
      } catch(UnpositionedError e) {
        throw e.addPosition(getTokenReference());
      }
    }
    parameters = extensions.getPreconditionMethodParameter(getTokenReference(),
                                                           parameters,
                                                           clazz,
                                                           modifiers); 
    if  (local.isAssertionClass()) {
      modifiers = (modifiers | ACC_STATIC| ACC_PUBLIC | ACC_ABSTRACT) ^ ACC_ABSTRACT; // remove abstract, make static and public
    } else {
      modifiers = (modifiers | ACC_ABSTRACT) ^ ACC_ABSTRACT; // remove abstract
    }

    CSourceMethod       method = super.checkInterface(context);

    return method;
  } 

  // ----------------------------------------------------------------------
  // CODE CHECKING
  // ----------------------------------------------------------------------

  public void analyseConditions()  throws PositionedError {
    if (kopiBody == null) {
      return;
    }
    kopiBody.analyseConditions();

    kopiBody = null;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    if (kopiBody == null) {
      super.accept(p);
    }
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA MEMBERS
  // ----------------------------------------------------------------------
  private KopiPreconditionStatement    kopiBody;
}
