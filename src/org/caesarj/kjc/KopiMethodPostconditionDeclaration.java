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
 * $Id: KopiMethodPostconditionDeclaration.java,v 1.1 2003-07-05 18:29:37 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
 
public class KopiMethodPostconditionDeclaration extends KopiPostconditionDeclaration { 
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
  public KopiMethodPostconditionDeclaration(TokenReference where, 
                                            int modifiers, 
                                            CTypeVariable[] typeVariables,
                                            CType realRetType, 
                                            String ident,
                                            JFormalParameter[] parameters, 
                                            CReferenceType[] exceptions, 
                                            KopiPostconditionStatement body, 
                                            JavadocComment javadoc, 
                                            JavaStyleComment[] comments,
                                            TypeFactory factory
                                            //   FTypeVariable[] typeVariable
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
      //      this.typeVariable= typeVariable;
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

    parameters = extensions.getPostconditionMethodParameter(getTokenReference(),
                                                            parameters,
                                                            getRealReturnType(),
                                                            clazz,
                                                            modifiers); 
   //this.context = context;
    if  (local.isAssertionClass()) {
      modifiers = (modifiers | ACC_STATIC | ACC_PUBLIC | ACC_ABSTRACT) ^ ACC_ABSTRACT; // remove abstract, make static and public
    } else {
      modifiers = (modifiers | ACC_ABSTRACT) ^ ACC_ABSTRACT; // remove abstract
    }
   //this.context = context;
    CSourceMethod       method = super.checkInterface(context);

    return method;
  }


  /**
   * First the pstcondition gets always a formal parameter for the storage of 
   * the old values. If this parameter is not necessary it is removed. Otherwise
   * the type of this paramter is changed to the correct type. (First its type is
   * always java.lang.Object.
   */
  private void correctTheInterface() throws PositionedError {
    CReferenceType      oldValueStore;

    oldValueStore = getMethod().getOldValueStore(); ;

    if (oldValueStore == null) {
      // remove parameter IDENT_STORAGE
      JFormalParameter[]      temp = new JFormalParameter[parameters.length-1];
      CType[]                 types = new CType[temp.length];

      for (int i = 0; i < temp.length; i++) {
        temp[i] = parameters[i+1];
        temp[i].setPosition( temp[i].getPosition()-1);
        types[i] = temp[i].getType();
      }
      // correct formal parameter
      parameters = temp;
      // correct CMethod representation
      getMethod().setParameters(types);
    } else {
      // correct the type of the parameter!!
      JFormalParameter          tmp = parameters[0];
      CType[]                   types = getMethod().getParameters();

      parameters[0] = new JFormalParameter(getTokenReference(), JLocalVariable.DES_PARAMETER , oldValueStore, IDENT_STORAGE, true);
      parameters[0].setPosition(tmp.getPosition());
      types[0] = oldValueStore;
      getMethod().setParameters(types);     
    }
  }

  // ----------------------------------------------------------------------
  // CODE CHECKING
  // ----------------------------------------------------------------------

  public void analyseConditions()  throws PositionedError {
    if (kopiBody == null) {
      return;
    }
    kopiBody.analyseConditions();

    correctTheInterface();
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

  private KopiPostconditionStatement    kopiBody;
}
