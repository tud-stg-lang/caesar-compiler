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
 * $Id: KopiMethodDeclaration.java,v 1.1 2003-07-05 18:29:38 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.util.InconsistencyException;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;

/**
 * This class represents a Java method declaration in the syntax tree.
 */
public class KopiMethodDeclaration extends JMethodDeclaration {

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
  public KopiMethodDeclaration(TokenReference where, 
                               int modifiers, 
                               CTypeVariable[] typeVariables,
                               CType returnType, 
                               String ident,
                               JFormalParameter[] parameters, 
                               CReferenceType[] exceptions, 
                               JBlock body, 
                               JavadocComment javadoc, 
                               JavaStyleComment[] comments,
                               KopiMethodPreconditionDeclaration precondition,
                               KopiMethodPostconditionDeclaration postcondition 
                               // FStoreClassDeclaration storeClass, 
                               //StoreGenerator storeGenerator
                               ) {
        super(where, 
              modifiers, 
              typeVariables, 
              returnType, 
              ident, 
              parameters, 
              exceptions, 
              body, 
              javadoc, 
              comments); 
        this.precondition = precondition;
        this.postcondition = postcondition;
        //        this.storeGenerator = storeGenerator;
        //        this.storeClass = storeClass;
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
    CSourceMethod       method = super.checkInterface(context);

    if (precondition != null) {
      method.setPreconditionMethod(precondition.getMethod());
    }
    if (postcondition != null) {
      method.setPostconditionMethod(postcondition.getMethod());
    }
    return method;
  }


  // ----------------------------------------------------------------------
  // CODE CHECKING
  // ----------------------------------------------------------------------
  /**
   * Check expression and evaluate and alter context
   * @param context the actual context of analyse
   * @return  a pure java expression including promote node
   * @exception PositionedError Error catched as soon as possible
   */
  public void checkBody1(CClassContext context) throws PositionedError {
    super.checkBody1(context);
    pretty = false; // no beautifing
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  public void accept(KjcVisitor p) {
    if (pretty) {
      // FIX use: visitKopiMethodDeclaration
      p.visitMethodDeclaration(this,
                               modifiers,
                               typeVariables,
                               returnType,
                               ident,
                               parameters,
                               exceptions,
                               body);
    } else {
      super.accept(p);
    }
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA MEMBERS
  // ----------------------------------------------------------------------
  private boolean                               pretty = true;

  private KopiMethodPreconditionDeclaration     precondition;
  private KopiMethodPostconditionDeclaration    postcondition;
 }
